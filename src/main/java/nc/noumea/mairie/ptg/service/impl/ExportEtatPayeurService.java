package nc.noumea.mairie.ptg.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.ExportEtatsPayeurTask;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.ReposCompTask;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.AbsencesEtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.AbstractItemEtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.HeuresSupEtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.HeuresSupEtatPayeurVo;
import nc.noumea.mairie.ptg.dto.etatsPayeur.PrimesEtatPayeurDto;
import nc.noumea.mairie.ptg.reporting.EtatPayeurReport;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.ExportEtatsPayeurServiceException;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;
import nc.noumea.mairie.ptg.workflow.WorkflowInvalidStateException;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.IBirtEtatsPayeurWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportEtatPayeurService implements IExportEtatPayeurService {

	private Logger logger = LoggerFactory.getLogger(ExportEtatPayeurService.class);

	@Autowired
	private IPaieWorkflowService paieWorkflowService;

	@Autowired
	private HelperService helperService;

	@Autowired
	private IVentilationRepository ventilationRepository;

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private IMairieRepository mairieRepository;

	@Autowired
	private IAccessRightsRepository accessRightRepository;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	private IBirtEtatsPayeurWsConsumer birtEtatsPayeurWsConsumer;

	@Autowired
	private EtatPayeurReport etatPayeurReport;

	@Autowired
	private IAbsWsConsumer absWsConsumer;

	private static SimpleDateFormat sfd = new SimpleDateFormat("YYYY-MM");

	@Override
	public CanStartWorkflowPaieActionDto canStartExportEtatPayeurAction(TypeChainePaieEnum chainePaie) {
		CanStartWorkflowPaieActionDto result = new CanStartWorkflowPaieActionDto();
		result.setCanStartAction(paieWorkflowService.canChangeStateToExportEtatsPayeurStarted(chainePaie));
		return result;
	}

	public AbstractItemEtatPayeurDto getAbsencesEtatPayeurDataForStatut(Integer idAgent,
			AbstractItemEtatPayeurDto result, VentilDate toVentilDate, VentilDate fromVentilDate) {

		// For all VentilAbsences of this ventilation ordered by dateLundi asc
		for (VentilAbsence va : ventilationRepository.getListOfVentilAbsenceWithDateForEtatPayeur(
				toVentilDate.getIdVentilDate(), idAgent)) {

			// 2. If the period concerns a date prior to ventilation, fetch the
			// second last ventilated item to
			// output the difference
			VentilAbsence vaOld = null;
			if (va.getDateLundi().before(fromVentilDate.getDateVentilation())) {
				vaOld = ventilationRepository.getPriorVentilAbsenceForAgentAndDate(va.getIdAgent(), va.getDateLundi(),
						va);
			}

			// 3. Then create the DTOs for Absence if the value is other than 0
			// or different from previous one
			AbsencesEtatPayeurDto dtoAbs = new AbsencesEtatPayeurDto(va, vaOld, helperService);
			result.setAbsences(dtoAbs);

		}

		return result;
	}

	public AbstractItemEtatPayeurDto getHeuresSupEtatPayeurDataForStatut(Integer idAgent,
			AbstractItemEtatPayeurDto result, VentilDate toVentilDate, VentilDate fromVentilDate) {

		List<HeuresSupEtatPayeurVo> listHSupEtatPayeur = new ArrayList<HeuresSupEtatPayeurVo>();

		// For all VentilAbsences of this ventilation ordered by dateLundi asc
		for (VentilHsup vh : ventilationRepository.getListOfVentilHeuresSupWithDateForEtatPayeur(
				toVentilDate.getIdVentilDate(), idAgent)) {

			// 2. If the period concerns a date prior to ventilation, fetch the
			// second last ventilated item to
			// output the difference
			VentilHsup vhOld = null;
			if (vh.getDateLundi().before(fromVentilDate.getDateVentilation())) {
				vhOld = ventilationRepository.getPriorVentilHSupAgentAndDate(vh.getIdAgent(), vh.getDateLundi(), vh);
			}

			// 3. Then create the DTOs for HSups
			// #14640 on n affiche pas les heures sup recuperees
			if (vh.getMHorsContrat() - vh.getMRecuperees() != 0
					|| (null != vhOld && vhOld.getMHorsContrat() - vhOld.getMRecuperees() != 0)) {
				HeuresSupEtatPayeurVo dtoHsup = new HeuresSupEtatPayeurVo(vh, vhOld);
				listHSupEtatPayeur.add(dtoHsup);
			}
		}

		// #14640 on additionne les 3 ventils d heures sup pour avoir qu une
		// seule ligne pour l etat payeur
		// #15314 si vide alors on ajoute pas la ligne
		if (listHSupEtatPayeur == null || listHSupEtatPayeur.size() == 0) {
			return null;
		} else {
			result.setHeuresSup(new HeuresSupEtatPayeurDto(listHSupEtatPayeur, helperService));
		}

		return result;
	}

	public AbstractItemEtatPayeurDto getPrimesEtatPayeurDataForStatut(Integer idAgent,
			AbstractItemEtatPayeurDto result, VentilDate toVentilDate, VentilDate fromVentilDate) {

		// For all VentilAbsences of this ventilation ordered by dateLundi asc
		for (VentilPrime vp : ventilationRepository.getListOfVentilPrimeWithDateForEtatPayeur(
				toVentilDate.getIdVentilDate(), idAgent)) {

			// 2. If the period concerns a date prior to ventilation, fetch the
			// second last ventilated item to
			// output the difference
			VentilPrime vpOld = null;
			if (vp.getDateDebutMois().before(fromVentilDate.getDateVentilation())) {
				vpOld = ventilationRepository.getPriorVentilPrimeForAgentAndDate(vp.getIdAgent(),
						vp.getDateDebutMois(), vp);
			}

			// 3. Then create the DTOs for Primes
			PrimesEtatPayeurDto dtoPrime = new PrimesEtatPayeurDto(vp, vpOld, helperService);
			result.getPrimes().add(dtoPrime);
		}

		return result;
	}

	/**
	 * This methods takes an abstract base object for Etat payeur and completes
	 * the agent's name, lastname and approbateur's info
	 * 
	 * @param item
	 */
	protected void fillAgentsData(AbstractItemEtatPayeurDto item, Integer idAgent) {

		AgentGeneriqueDto ag = sirhWsConsumer.getAgent(idAgent);
		item.getAgent().setNom(ag.getDisplayNom());
		item.getAgent().setPrenom(ag.getDisplayPrenom());
		item.getAgent().setNomatr(ag.getNomatr());

		AgentWithServiceDto agDto = sirhWsConsumer.getAgentService(idAgent, helperService.getCurrentDate());
		item.getAgent().setSigleService(agDto.getSigleService());
	}

	/**
	 * Returns whether or not an agent is eligible to exporting Etats Payeur
	 * considering its status (F, C or CC) we are currently exporting
	 * 
	 * @param idAgent
	 * @param statut
	 * @return true if the agent is eligible
	 */
	protected boolean isAgentEligibleToVentilation(Integer idAgent, AgentStatutEnum statut, Date date) {
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent), date);
		AgentStatutEnum agentStatus = carr != null ? carr.getStatutCarriere() : null;
		return agentStatus == statut;
	}

	@Override
	public ReturnMessageDto startExportEtatsPayeur(Integer agentIdExporting, AgentStatutEnum statut) {

		logger.info("Starting exportEtatsPayeurs of Pointages for Agents statut [{}], asked by [{}]", agentIdExporting,
				statut);

		ReturnMessageDto result = new ReturnMessageDto();

		// 1. Retrieve eligible ventilation in order to get dates
		TypeChainePaieEnum chainePaie = helperService.getTypeChainePaieFromStatut(statut);
		VentilDate ventilDate = ventilationRepository.getLatestVentilDate(chainePaie, false);

		// If no ventilation has ever been ran, return now
		if (ventilDate == null) {
			logger.info("No unpaid ventilation date found. Nothing to export. Stopping process here.");
			result.getErrors().add(String.format("Aucune ventilation n'existe pour le statut [%s].", statut));
			return result;
		}

		// 2. Call workflow to make sure we can start the export Etats Payeurs
		// process
		try {
			paieWorkflowService
					.changeStateToExportEtatsPayeurStarted(helperService.getTypeChainePaieFromStatut(statut));
		} catch (WorkflowInvalidStateException e) {
			logger.error("Could not start exportEtatsPayeur process: {}", e.getMessage());
			result.getErrors().add(e.getMessage());
			return result;
		}

		// Create ExportEtatsPayeurTask for SIRH-JOBS to process it
		// asynchronously
		ExportEtatsPayeurTask task = new ExportEtatsPayeurTask();
		task.setVentilDate(ventilDate);
		task.setIdAgent(agentIdExporting);
		task.setTypeChainePaie(chainePaie);
		task.setDateCreation(helperService.getCurrentDate());
		pointageRepository.persisEntity(task);

		result.getInfos().add(
				String.format("L'export des Etats du Payeur pour la chaine paie [%s] a bien été lancé.", chainePaie));

		return result;
	}

	@Override
	public void exportEtatsPayeur(Integer idExportEtatsPayeurTask) {

		ExportEtatsPayeurTask task = pointageRepository.getEntity(ExportEtatsPayeurTask.class, idExportEtatsPayeurTask);

		if (task == null) {
			logger.info("The given idExportEtatsPayeurTask [{}] does not match any task in database. Exiting.",
					idExportEtatsPayeurTask);
			return;
		}

		// 1. Retrieve latest ventilDate in order to date the reports
		logger.info("Retrieving ventilation date for chaine paie [{}]", task.getTypeChainePaie());
		VentilDate vd = task.getVentilDate();

		// 2. Call Birt and store files
		logger.info("Calling Birt reports...");
		List<EtatPayeur> etats = callBirtEtatsPayeurForChainePaie(task.getIdAgent(), task.getTypeChainePaie(),
				vd.getDateVentilation());

		// 3. Update Recups through SIRH-ABS-WS
		logger.info("Updating recuperations by calling SIRH-ABS-WS...");
		List<Integer> idAgentsToProcessRCTasks = new ArrayList<Integer>();

		for (VentilHsup vh : ventilationRepository.getListOfVentilHeuresSupWithDateForEtatPayeur(vd.getIdVentilDate(),
				null)) {

			if (vh.getMSup() != 0 && !idAgentsToProcessRCTasks.contains(vh.getIdAgent())) {
				idAgentsToProcessRCTasks.add(vh.getIdAgent());
			}

			if (vh.getMRecuperees() != 0) {
				int nbMinutesRecupereesTotal = calculMinutesRecuperation(vh);
				absWsConsumer.addRecuperationsToAgent(vh.getIdAgent(), vh.getDateLundi(), nbMinutesRecupereesTotal);
			}
		}

		logger.info("Creating ReposComptTask for processing RCs via SIRH-JOBS...");
		for (Integer idAgent : idAgentsToProcessRCTasks) {

			if (isAgentEligibleToReposComp(idAgent, vd.getDateVentilation())) {
				ReposCompTask t = new ReposCompTask();
				t.setIdAgent(idAgent);
				t.setIdAgentCreation(task.getIdAgent());
				t.setDateCreation(helperService.getCurrentDate());
				t.setVentilDate(vd);
				pointageRepository.persisEntity(t);
			}
		}

		// 4. Save records for exported files
		logger.info("Saving generated reports...");
		for (EtatPayeur etat : etats) {
			pointageRepository.persisEntity(etat);
		}

		logger.info("Export Etats Payeurs done.");
	}

	@Override
	public void journalizeEtatsPayeur(Integer idExportEtatsPayeurTask) {

		ExportEtatsPayeurTask task = pointageRepository.getEntity(ExportEtatsPayeurTask.class, idExportEtatsPayeurTask);

		if (task == null) {
			logger.info("The given idExportEtatsPayeurTask [{}] does not match any task in database. Exiting.",
					idExportEtatsPayeurTask);
			return;
		}

		if (task.getVentilDate().isPaye()) {
			logger.info(
					"The given ExportEtatsPayeurTask id [{}] has already been stopped. Etats Payeurs cannot be exported again. Exiting.",
					idExportEtatsPayeurTask);
			return;
		}

		// 5. Set all related pointages as Journalise
		logger.info("Marking pointages as JOURNALISES...");
		markPointagesAsJournalises(task.getVentilDate().getPointages(), task.getIdAgent());
		markPointagesCalculesAsJournalises(task.getVentilDate().getPointagesCalcules());

		// 6. Set VentilDate as Payee
		logger.info("Setting Ventilation as PAYE...");
		task.getVentilDate().setPaye(true);

		logger.info("Export Etats Payeurs stopped.");
	}

	protected List<EtatPayeur> callBirtEtatsPayeurForChainePaie(Integer agentIdExporting,
			TypeChainePaieEnum chainePaie, Date ventilationDate) {

		List<EtatPayeur> etats = new ArrayList<EtatPayeur>();

		try {
			switch (chainePaie) {
				case SCV:
					etats.add(exportEtatPayeur(agentIdExporting, AgentStatutEnum.CC, ventilationDate));
					break;

				case SHC:
					etats.add(exportEtatPayeur(agentIdExporting, AgentStatutEnum.F, ventilationDate));
					etats.add(exportEtatPayeur(agentIdExporting, AgentStatutEnum.C, ventilationDate));
					break;
			}
		} catch (Exception ex) {
			throw new ExportEtatsPayeurServiceException(
					"An error occured while retrieving reports from SIRH-REPORTS for Etats Payeur.", ex);
		}

		return etats;
	}

	protected EtatPayeur exportEtatPayeur(Integer idAgent, AgentStatutEnum statut, Date date) throws Exception {

		EtatPayeur ep = new EtatPayeur();
		ep.setFichier(String.format("%s-%s.pdf", sfd.format(date), statut));
		ep.setLabel(String.format("%s-%s", sfd.format(date), statut));
		ep.setDateEtatPayeur(new LocalDate(date).withDayOfMonth(1).toDate());
		ep.setStatut(statut);
		ep.setIdAgent(idAgent);
		ep.setDateEdition(helperService.getCurrentDate());

		logger.info("Downloading report named [{}]...", ep.getFichier());
		// #15138 on commente l'appel à BIRT pour le moment car soucis de
		// timeout lors de la génération du BIT
		// birtEtatsPayeurWsConsumer.downloadEtatPayeurByStatut(statut.toString(),
		// ep.getFichier());
		// try {
		// etatPayeurReport.downloadEtatPayeurByStatut(statut, ep);
		// } catch (Exception e) {
		// logger.error("Un erreur est survenue dans la generation du rapport des etats du payeur pour le statut "
		// + statut.toString());
		// }
		logger.info("Downloading report [{}] done.", ep.getFichier());

		return ep;
	}

	/**
	 * Updates each pointage to set them as VALIDE state after being exported to
	 * Paie
	 * 
	 * @param pointages
	 * @param idAgent
	 */
	protected void markPointagesAsJournalises(Set<Pointage> pointages, int idAgent) {

		Date currentDate = helperService.getCurrentDate();

		for (Pointage ptg : pointages) {

			if (ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.JOURNALISE)
				continue;

			EtatPointage ep = new EtatPointage();
			ep.setDateEtat(currentDate);
			ep.setDateMaj(currentDate);
			ep.setPointage(ptg);
			ep.setEtat(EtatPointageEnum.JOURNALISE);
			ep.setIdAgent(idAgent);
			ptg.getEtats().add(ep);
		}
	}

	/**
	 * Updates each pointage calcule to set them as VALIDE state
	 * 
	 * @param pointagesCalcules
	 */
	protected void markPointagesCalculesAsJournalises(Set<PointageCalcule> pointagesCalcules) {
		for (PointageCalcule ptgC : pointagesCalcules) {
			if (ptgC.getEtat() == EtatPointageEnum.JOURNALISE)
				continue;

			ptgC.setEtat(EtatPointageEnum.JOURNALISE);
		}
	}

	@Override
	public void stopExportEtatsPayeur(TypeChainePaieEnum typeChainePaie) throws WorkflowInvalidStateException {

		logger.info("Updating workflow by setting state to [ETATS_PAYEURS_TERMINES] for typeChainePaie {}...",
				typeChainePaie);
		paieWorkflowService.changeStateToExportEtatsPayeurDone(typeChainePaie);
	}

	/**
	 * Returns whether or not an agent is eligible to exporting Etats Payeur
	 * considering its status (F, C or CC) we are currently exporting
	 * 
	 * @param idAgent
	 * @param statut
	 * @return true if the agent is eligible
	 */
	protected boolean isAgentEligibleToReposComp(Integer idAgent, Date date) {

		Spcarr carr = mairieRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent), date);

		if (carr.getStatutCarriere().equals(AgentStatutEnum.C) || carr.getStatutCarriere().equals(AgentStatutEnum.CC)) {
			return true;
		}
		return false;
	}

	protected int calculMinutesRecuperation(VentilHsup vh) {

		int result = 0;

		Spcarr carr = mairieRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(vh.getIdAgent()),
				vh.getDateLundi());
		// dans le cas des fonctionnaires et contractuels : pas de majoration
		if (carr.getStatutCarriere().equals(AgentStatutEnum.C) || carr.getStatutCarriere().equals(AgentStatutEnum.F)) {
			return vh.getMRecuperees() + vh.getMRappelService();
		}
		// pour les conventions collectives
		// on prend l heure supplementaire + la majoration
		result += vh.getMNormalesRecup();
		result += vh.getMSup25Recup() * 1.25;
		result += vh.getMSup50Recup() * 1.50;
		// on ne prend que la majoration car l heure sup est deja prise en
		// compte juste au dessus
		result += vh.getMsdjfRecup() * 0.75;
		result += vh.getMMaiRecup() * 0.75;
		result += vh.getMsNuitRecup();

		return result;
	}

	@Override
	public EtatPayeurDto getEtatPayeurDataForStatut(AgentStatutEnum statut) {

		TypeChainePaieEnum chainePaie = helperService.getTypeChainePaieFromStatut(statut);

		VentilDate toVentilDate = ventilationRepository.getLatestVentilDate(chainePaie, false);

		if (toVentilDate == null) {
			logger.error(
					"Impossible to retrieve data for Etats Payeur, there is no unpaid ventilation for TypeChainePaie [{}]",
					chainePaie);
			return new EtatPayeurDto();
		}

		EtatPayeurDto result = new EtatPayeurDto(chainePaie, statut, toVentilDate.getDateVentilation(),
				toVentilDate.getDateVentilation());

		VentilDate fromVentilDate = ventilationRepository.getLatestVentilDate(chainePaie, true);

		// on recupere tous les agents des différentes tables
		for (Integer idAgent : ventilationRepository
				.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())) {

			// 1. Verify whether this agent is eligible, through its
			// AgentStatutEnum (Spcarr)
			if (!isAgentEligibleToVentilation(idAgent, statut, toVentilDate.getDateVentilation())) {
				logger.info("Agent {} not eligible for Etats payeurs (status not matching), skipping to next.", idAgent);
				continue;
			}
			// on cree l'agent
			AbstractItemEtatPayeurDto dto = new AbstractItemEtatPayeurDto();
			fillAgentsData(dto, idAgent);
			result.getAgents().add(dto);
			// on rempli les informations
			getAbsencesEtatPayeurDataForStatut(idAgent, dto, toVentilDate, fromVentilDate);
			getHeuresSupEtatPayeurDataForStatut(idAgent, dto, toVentilDate, fromVentilDate);
			getPrimesEtatPayeurDataForStatut(idAgent, dto, toVentilDate, fromVentilDate);

			// #15314 si tout est vide, on supprime la ligne
			if (dto.getAbsences().getQuantiteEntre1HeureEt4Heure() == null
					&& dto.getAbsences().getQuantiteInf1Heure() == null
					&& dto.getAbsences().getQuantiteSup4Heure() == null) {
				if (dto.getHeuresSup().getDjf() == null && dto.getHeuresSup().getH1Mai() == null
						&& dto.getHeuresSup().getNormales() == null && dto.getHeuresSup().getNuit() == null
						&& dto.getHeuresSup().getSup25() == null && dto.getHeuresSup().getSup50() == null) {
					if (dto.getPrimes() == null || dto.getPrimes().size() == 0) {
						result.getAgents().remove(dto);
					}
				}
			}
		}

		return result;
	}

	@Override
	public void exportEtatsPayeurTest(Integer idExportEtatsPayeurTask) {

		ExportEtatsPayeurTask task = pointageRepository.getEntity(ExportEtatsPayeurTask.class, idExportEtatsPayeurTask);

		if (task == null) {
			logger.info("The given idExportEtatsPayeurTask [{}] does not match any task in database. Exiting.",
					idExportEtatsPayeurTask);
			return;
		}

		// 1. Retrieve latest ventilDate in order to date the reports
		logger.info("Retrieving ventilation date for chaine paie [{}]", task.getTypeChainePaie());
		VentilDate vd = task.getVentilDate();

		// 2. Call Birt and store files
		logger.info("Calling Birt reports...");
		callBirtEtatsPayeurForChainePaieTest(task.getIdAgent(), task.getTypeChainePaie(), vd.getDateVentilation());

		// 4. Save records for exported files
		logger.info("Saving generated reports...");

		logger.info("Export Etats Payeurs done.");
	}

	protected List<EtatPayeur> callBirtEtatsPayeurForChainePaieTest(Integer agentIdExporting,
			TypeChainePaieEnum chainePaie, Date ventilationDate) {

		List<EtatPayeur> etats = new ArrayList<EtatPayeur>();

		try {
			switch (chainePaie) {
				case SCV:
					etats.add(exportEtatPayeurTest(agentIdExporting, AgentStatutEnum.CC, ventilationDate));
					break;

				case SHC:
					etats.add(exportEtatPayeurTest(agentIdExporting, AgentStatutEnum.F, ventilationDate));
					etats.add(exportEtatPayeurTest(agentIdExporting, AgentStatutEnum.C, ventilationDate));
					break;
			}
		} catch (Exception ex) {
			throw new ExportEtatsPayeurServiceException(
					"An error occured while retrieving reports from SIRH-REPORTS for Etats Payeur.", ex);
		}

		return etats;
	}

	protected EtatPayeur exportEtatPayeurTest(Integer idAgent, AgentStatutEnum statut, Date date) throws Exception {

		EtatPayeur ep = new EtatPayeur();
		ep.setFichier(String.format("%s-%s.pdf", sfd.format(date), statut));
		ep.setLabel(String.format("%s-%s", sfd.format(date), statut));
		ep.setDateEtatPayeur(new LocalDate(date).withDayOfMonth(1).toDate());
		ep.setStatut(statut);
		ep.setIdAgent(idAgent);
		ep.setDateEdition(helperService.getCurrentDate());

		logger.info("Downloading report named [{}]...", ep.getFichier());
		// #15138 on commente l'appel à BIRT pour le moment car soucis de
		// timeout lors de la génération du BIT
		// birtEtatsPayeurWsConsumer.downloadEtatPayeurByStatut(statut.toString(),
		// ep.getFichier());
		etatPayeurReport.downloadEtatPayeurByStatut(statut, ep);
		logger.info("Downloading report [{}] done.", ep.getFichier());

		return ep;
	}
}
