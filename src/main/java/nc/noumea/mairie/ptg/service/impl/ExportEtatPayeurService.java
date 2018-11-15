package nc.noumea.mairie.ptg.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HeaderElement;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.h2.util.StringUtils;
import org.hibernate.id.IdentifierGeneratorAggregator;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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
import nc.noumea.mairie.ptg.dto.evp.EVPDto;
import nc.noumea.mairie.ptg.dto.evp.EVPElementDto;
import nc.noumea.mairie.ptg.reporting.EtatPayeurReporting;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.ExportEtatsPayeurServiceException;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;
import nc.noumea.mairie.ptg.workflow.WorkflowInvalidStateException;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.titreRepas.repository.ITitreRepasRepository;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

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
	private ITitreRepasRepository trRepository;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	private EtatPayeurReporting etatPayeurReport;

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

		List<AbsencesEtatPayeurDto> listAbsencesEtatPayeurDto = new ArrayList<AbsencesEtatPayeurDto>();
		
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
			AbsencesEtatPayeurDto dtoAbs = new AbsencesEtatPayeurDto(va, vaOld);
			listAbsencesEtatPayeurDto.add(dtoAbs);
		}
		
		// #17035 on additionne les ventils absences pour avoir qu une
		// seule ligne pour l etat payeur
		// #15314 si vide alors on ajoute pas la ligne
		if (!listAbsencesEtatPayeurDto.isEmpty()) {
			result.setAbsences(new AbsencesEtatPayeurDto(listAbsencesEtatPayeurDto));
		}


		return result;
	}

	public AbstractItemEtatPayeurDto getHeuresSupEtatPayeurDataForStatut(Integer idAgent,
			AbstractItemEtatPayeurDto result, VentilDate toVentilDate, VentilDate fromVentilDate, Boolean isForEVP) {
		
		List<HeuresSupEtatPayeurVo> listHSupEtatPayeur = new ArrayList<HeuresSupEtatPayeurVo>();

		// For all VentilAbsences of this ventilation ordered by dateLundi asc
		for (VentilHsup vh : ventilationRepository.getListOfVentilHeuresSupWithDateForEtatPayeur(
				toVentilDate.getIdVentilDate(), idAgent)) {

			// 2. If the period concerns a date prior to ventilation, fetch the second last ventilated item to output the difference
			VentilHsup vhOld = null;
			if (vh.getDateLundi().before(fromVentilDate.getDateVentilation())) {
				vhOld = ventilationRepository.getPriorVentilHSupAgentAndDate(vh.getIdAgent(), vh.getDateLundi(), vh);
			}

			// 3. Then create the DTOs for HSups : #14640 on n affiche pas les heures sup recuperees
			if (vh.getMHorsContrat() - vh.getMRecuperees() != 0 || (null != vhOld && vhOld.getMHorsContrat() - vhOld.getMRecuperees() != 0)) {
				HeuresSupEtatPayeurVo dtoHsup = new HeuresSupEtatPayeurVo(vh, vhOld, isForEVP);
				if (isNotVoid(dtoHsup))
					listHSupEtatPayeur.add(dtoHsup);
			}
		}

		// #14640 on additionne les 3 ventils d heures sup pour avoir qu une seule ligne pour l etat payeur
		// #15314 si vide alors on ajoute pas la ligne
		if (listHSupEtatPayeur == null || listHSupEtatPayeur.size() == 0) {
			return null;
		} else {
//			result.setHeuresSup(new HeuresSupEtatPayeurDto(listHSupEtatPayeur, helperService, isForEVP));
			result.setMapHeuresSup(getMapHSFromList(listHSupEtatPayeur));
		}

		return result;
	}
	
	private boolean isNotVoid(HeuresSupEtatPayeurVo vo) {
		if ((vo.getSup25() == null || vo.getSup25() == 0)
				&& (vo.getSup50() == null || vo.getSup50() == 0)
				&& (vo.getDjf() == null || vo.getDjf() == 0)
				&& (vo.getH1Mai() == null || vo.getH1Mai() == 0)
				&& (vo.getNuit() == null || vo.getNuit() == 0)
				&& (vo.getNormales() == null || vo.getNormales() == 0)
				&& (vo.getSimples() == null || vo.getSimples() == 0)
				&& (vo.getComposees() == null || vo.getComposees() == 0))
			return false;
		else return true;
	}
	
	private Map<Date, HeuresSupEtatPayeurDto> getMapHSFromList(List<HeuresSupEtatPayeurVo> listVO) {
		Map<Date, HeuresSupEtatPayeurVo> halfMap = Maps.newHashMap();
		HeuresSupEtatPayeurVo hsDto;
		
		for (HeuresSupEtatPayeurVo hs : listVO) {
			if (halfMap.containsKey(hs.getDateVentilation())) {
				hsDto = halfMap.get(hs.getDateVentilation());
				hsDto.setSup25(hsDto.getSup25() + hs.getSup25());
				hsDto.setSup50(hsDto.getSup50() + hs.getSup50());
				hsDto.setDjf(hsDto.getDjf() + hs.getDjf());
				hsDto.setH1Mai(hsDto.getH1Mai() + hs.getH1Mai());
				hsDto.setNuit(hsDto.getNuit() + hs.getNuit());
				hsDto.setNormales(hsDto.getNormales() + hs.getNormales());
				hsDto.setSimples(hsDto.getSimples() + hs.getSimples());
				hsDto.setComposees(hsDto.getComposees() + hs.getComposees());
			} else {
				hsDto = new HeuresSupEtatPayeurVo();
				hsDto.setSup25(hs.getSup25());
				hsDto.setSup50(hs.getSup50());
				hsDto.setDjf(hs.getDjf());
				hsDto.setH1Mai(hs.getH1Mai());
				hsDto.setNuit(hs.getNuit());
				hsDto.setNormales(hs.getNormales());
				hsDto.setSimples(hs.getSimples());
				hsDto.setComposees(hs.getComposees());
				
				halfMap.put(hs.getDateVentilation(), hsDto);
			}
		}

		Map<Date, HeuresSupEtatPayeurDto> returnMap = Maps.newHashMap();
		for (Map.Entry<Date, HeuresSupEtatPayeurVo> entry : halfMap.entrySet())
		{
			returnMap.put(entry.getKey(), new HeuresSupEtatPayeurDto(entry.getValue(), helperService));
		}
		
		return returnMap;
	}

	public AbstractItemEtatPayeurDto getPrimesEtatPayeurDataForStatut(Integer idAgent,
			AbstractItemEtatPayeurDto result, VentilDate toVentilDate, VentilDate fromVentilDate) {

		// For all VentilAbsences of this ventilation ordered by dateLundi asc
		for (VentilPrime vp : ventilationRepository.getListOfVentilPrimeWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), idAgent)) {

			// 2. If the period concerns a date prior to ventilation, fetch the second last ventilated item to output the difference
			VentilPrime vpOld = null;
			if (vp.getDateDebutMois().before(fromVentilDate.getDateVentilation())) {
				vpOld = ventilationRepository.getPriorVentilPrimeForAgentAndDate(vp.getIdAgent(), vp.getDateDebutMois(), vp);
			}

			// 3. Then create the DTOs for Primes #18592 supprimer les lignes a 0
			if(null != vp && null != vp.getQuantite()) {
				double qte = vp.getQuantite() - (vpOld != null ? vpOld.getQuantite() : 0);
			
				if(0 != qte) {
					PrimesEtatPayeurDto dtoPrime = new PrimesEtatPayeurDto(vp, vpOld, helperService);
					result.getPrimes().add(dtoPrime);
				}
			}
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
		item.getAgent().setIdAgent(idAgent);

		AgentWithServiceDto agDto = sirhWsConsumer.getAgentService(idAgent, helperService.getCurrentDate());
		// #17897 dans le cas ou les agents n ont plus d affectation
		item.getAgent().setSigleService(null != agDto ? agDto.getSigleService() : "");
	}
	
	protected void fillAgentsDataWithoutService(AbstractItemEtatPayeurDto item, Integer idAgent, Date fromVentilDate) {

		AgentGeneriqueDto ag = sirhWsConsumer.getAgent(idAgent);
		
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent), fromVentilDate);
		if (carr == null) {
			logger.warn("Pas de carrière active au début de la ventilation pour l'agent ID {}. Récupération de la carrière suivante.", idAgent);
			carr = mairieRepository.getAgentNextCarriere(helperService.getMairieMatrFromIdAgent(idAgent), fromVentilDate);
		}
		
		Integer cdCate = carr.getCdcate();
		
		item.getAgent().setStatut(cdCate.toString());
		item.getAgent().setIdTiarhe(ag.getIdTiarhe());
		item.getAgent().setIdAgent(idAgent);
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
				if(nbMinutesRecupereesTotal > 0)
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
		// #19036 
		ep.setFichier(String.format("%s-%s.pdf", sfd.format(helperService.getCurrentDate()), statut));
		ep.setLabel(String.format("%s-%s", sfd.format(helperService.getCurrentDate()), statut));
		ep.setDateEtatPayeur(new LocalDate(date).withDayOfMonth(1).toDate());
		ep.setStatut(statut);
		ep.setIdAgent(idAgent);
		ep.setDateEdition(helperService.getCurrentDate());

		logger.info("Downloading report named [{}]...", ep.getFichier());
		// #15138 timeout lors de la generation du BIRT, on genere maintenant avec IText
		try {
			etatPayeurReport.downloadEtatPayeurByStatut(statut, ep);
		} catch (Exception e) {
			logger.error("Une erreur est survenue dans la generation du rapport des etats du payeur pour le statut "
					+ statut.toString());
			throw e;
		}
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
		// et avec la nouvelle evol #17538, on alimente le compteur lors de l approbation
		// et non plus ici
		if (carr.getStatutCarriere().equals(AgentStatutEnum.C) || carr.getStatutCarriere().equals(AgentStatutEnum.F)) {
			return 0;
		}
		// pour les conventions collectives
		// on prend l heure supplementaire + la majoration
		// #17538 : nouvelle gestion des compteurs de RECUP
		result += vh.getMSup25Recup() * 0.25;
		result += vh.getMSup50Recup() * 0.50;
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

		// evol #29821 sur la date du titre dans le PDF
		EtatPayeurDto result = new EtatPayeurDto(chainePaie, statut, helperService.getCurrentDate(),
				toVentilDate.getDateVentilation());

		VentilDate fromVentilDate = ventilationRepository.getLatestVentilDate(chainePaie, true);

		// on recupere tous les agents des différentes tables
		for (Integer idAgent : ventilationRepository
				.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())) {

			// 1. Verify whether this agent is eligible, through its AgentStatutEnum (Spcarr)
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
			getHeuresSupEtatPayeurDataForStatut(idAgent, dto, toVentilDate, fromVentilDate, false);
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
	public EVPDto getDataForEVP(String chainePaieString) {

		TypeChainePaieEnum chainePaie = chainePaieString.equals(TypeChainePaieEnum.SHC.toString()) ? TypeChainePaieEnum.SHC : TypeChainePaieEnum.SCV;

		VentilDate toVentilDate = ventilationRepository.getLatestVentilDate(chainePaie, true);

		if (toVentilDate == null) {
			logger.error("Impossible to retrieve data for Etats Payeur, there is no unpaid ventilation for TypeChainePaie [{}]", chainePaie);
			return new EVPDto();
		}

		EtatPayeurDto result = new EtatPayeurDto(toVentilDate.getDateVentilation());

		VentilDate fromVentilDate = ventilationRepository.get2ndLatestVentilDate(chainePaie, true);
		
		List<Integer> listIdsAgents = ventilationRepository.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate());
		
		logger.info("Processing list of {} agents.", listIdsAgents.size());
		// on recupere tous les agents des différentes tables
		for (Integer idAgent : listIdsAgents) {
			
			// on cree l'agent
			AbstractItemEtatPayeurDto dto = new AbstractItemEtatPayeurDto();
			fillAgentsDataWithoutService(dto, idAgent, fromVentilDate.getDateVentilation());
			result.getAgents().add(dto);
			
			// on rempli les informations
			getHeuresSupEtatPayeurDataForStatut(idAgent, dto, toVentilDate, fromVentilDate, true);
			getPrimesEtatPayeurDataForStatut(idAgent, dto, toVentilDate, fromVentilDate);

			if ((dto.getPrimes() == null || dto.getPrimes().size() == 0) && (dto.getMapHeuresSup() == null || dto.getMapHeuresSup().isEmpty())) {
				result.getAgents().remove(dto);
			}
		}

		result.setChainePaie(chainePaieString);
		logger.info("Mapping the result");
		return mapEVP(result, toVentilDate.getDateVentilation());
	}
	
	private EVPDto mapEVP(EtatPayeurDto etatPayeur, Date dateVentilation) {
		EVPDto evp = new EVPDto();
		
		evp.setChainePaie(etatPayeur.getChainePaie());
		evp.setDatePeriodePaie(helperService.getMonthOfVentilation(dateVentilation));
		
		logger.info("Mapping primes");
		mapPrimes(evp, etatPayeur);
		logger.info("Mapping h. supp.");
		mapHSupp(evp, etatPayeur);
		// Récupération des TR pour les ajouter aux EVP
		logger.info("Mapping TR");
		mapTR(evp, etatPayeur);
		
		return evp;
	}
	
	private void mapPrimes(EVPDto evp, EtatPayeurDto etatPayeur) {
		
		List<EVPElementDto> listEVP;
		
		AgentWithServiceDto newAgent;
		EVPElementDto elementEVP;
		
		for (AbstractItemEtatPayeurDto item : etatPayeur.getAgents()) {
			listEVP = Lists.newArrayList();
			newAgent = item.getAgent();
			
			for (PrimesEtatPayeurDto prime : item.getPrimes()) {

				String type = "";
				
				switch (prime.getNorubr()) {
					case 7121 : type = "IN9";
						break;
					case 7650 : type = "INE";
						break;
					case 7651 : type = "INF";
						break;
					case 7652 : type = "ING";
						break;
					case 7656 : type = "INU";
						break;
					case 7657 : type = "INV";
						break;
					case 7705 : type = "INI";
						break;
					case 7708 : type = "Oui";
						break;
					case 7709 : type = "INJ";
						break;
					case 7710 : type = "INK";
						break;
					case 7711 : type = "INL";
						break;
					case 7712 : type = "INM";
						break;
					case 7713 : type = "INN";
						break;
					case 7718 : type = "INO";
						break;
					case 7719 : type = "INP";
						break;
					case 7720 : type = "INQ";
						break;
					case 7721 : type = "INR";
						break;
					case 7722 : type = "INS";
						break;
				}
				
				elementEVP = new EVPElementDto();
				elementEVP.setQuantite(prime.getQuantite());
				elementEVP.setRubrique(type);
				elementEVP.setPeriodeEV(prime.getDate());
				listEVP.add(elementEVP);
			}
			
			evp.getElements().put(newAgent, listEVP);
		}
	}

	private void mapTR(EVPDto evp, EtatPayeurDto etatPayeur) {
		List<EVPElementDto> listEVP;
		AgentWithServiceDto newAgent;
		EVPElementDto elementEVP;
		
		List<Integer> listIDAgentWithTR = trRepository.getListIdAgentWithTitreRepasByMonth(evp.getDatePeriodePaie());
		
		logger.info("Processing {} TR ...", listIDAgentWithTR.size());
		Integer i = 0;
		
		for (Integer ida : listIDAgentWithTR) {
			Spcarr carr = mairieRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(ida), evp.getDatePeriodePaie());
			
			// Les TR n'ont pas de distinction sur le statut des agents. Il faut donc faire le filtre.
			if (etatPayeur.getChainePaie().equals(TypeChainePaieEnum.SHC.toString()) && carr.getCdcate() == 7)
				continue;
			if (etatPayeur.getChainePaie().equals(TypeChainePaieEnum.SCV.toString()) && carr.getCdcate() != 7)
				continue;
			
			elementEVP = new EVPElementDto();
			elementEVP.setQuantite("15,00");
			elementEVP.setRubrique("OTR");
			elementEVP.setPeriodeEV(evp.getDatePeriodePaie());

			newAgent = new AgentWithServiceDto();
			newAgent.setIdAgent(ida);
			
			// Si l'agent existe, on ajoute le TR, sinon on le créé.
			if (evp.getElements().containsKey(newAgent)) {
				evp.getElements().get(newAgent).add(elementEVP);
			} else {
				AgentGeneriqueDto age = sirhWsConsumer.getAgent(ida);
				newAgent.setIdTiarhe(age.getIdTiarhe());
				
				listEVP = Lists.newArrayList();
				listEVP.add(elementEVP);
				evp.getElements().put(newAgent, listEVP);
			}
			++i;
		}

		logger.info("{} TR finally processed...", i);
	}

	private void mapHSupp(EVPDto evp, EtatPayeurDto etatPayeur) {
		List<EVPElementDto> listEVP;
		AgentWithServiceDto newAgent;
		EVPElementDto elementEVP;
		
		for (AbstractItemEtatPayeurDto a : etatPayeur.getAgents()) {
			for (Map.Entry<Date, HeuresSupEtatPayeurDto> entry : a.getMapHeuresSup().entrySet()) {
				listEVP = Lists.newArrayList();
				newAgent = new AgentWithServiceDto();
				String statusAgent = a.getAgent().getStatut();
				newAgent.setIdAgent(a.getAgent().getIdAgent());
				newAgent.setIdTiarhe(a.getAgent().getIdTiarhe());
				
				if (!StringUtils.isNullOrEmpty(entry.getValue().getDjf())) {
					elementEVP = new EVPElementDto();
					elementEVP.setPeriodeEV(entry.getKey());
					elementEVP.setQuantite(entry.getValue().getDjf());
					elementEVP.setRubrique(statusAgent.equals("4") ? "HCM" : statusAgent.equals("7") ? "HDR" : "HBA");
					listEVP.add(elementEVP);
				}
				if (!StringUtils.isNullOrEmpty(entry.getValue().getH1Mai())) {
					elementEVP = new EVPElementDto();
					elementEVP.setPeriodeEV(entry.getKey());
					elementEVP.setQuantite(entry.getValue().getH1Mai());
					elementEVP.setRubrique(statusAgent.equals("4") ? "HCM" : statusAgent.equals("7") ? "HDR" : "HBF");
					listEVP.add(elementEVP);
				}
				if (!StringUtils.isNullOrEmpty(entry.getValue().getNormales())) {
					elementEVP = new EVPElementDto();
					elementEVP.setPeriodeEV(entry.getKey());
					elementEVP.setQuantite(entry.getValue().getNormales());
					elementEVP.setRubrique(statusAgent.equals("4") ? "HCJ" : statusAgent.equals("7") ? "" : "HBE");
					listEVP.add(elementEVP);
				}
				if (!StringUtils.isNullOrEmpty(entry.getValue().getNuit())) {
					elementEVP = new EVPElementDto();
					elementEVP.setPeriodeEV(entry.getKey());
					elementEVP.setQuantite(entry.getValue().getNuit());
					elementEVP.setRubrique(statusAgent.equals("4") ? "HCN" : statusAgent.equals("7") ? "HDS" : "HBN");
					listEVP.add(elementEVP);
				}
				if (!StringUtils.isNullOrEmpty(entry.getValue().getSup25())) {
					elementEVP = new EVPElementDto();
					elementEVP.setPeriodeEV(entry.getKey());
					elementEVP.setQuantite(entry.getValue().getSup25());
					elementEVP.setRubrique(statusAgent.equals("4") ? "HCK" : statusAgent.equals("7") ? "HDP" : "");
					listEVP.add(elementEVP);
				}
				if (!StringUtils.isNullOrEmpty(entry.getValue().getSup50())) {
					elementEVP = new EVPElementDto();
					elementEVP.setPeriodeEV(entry.getKey());
					elementEVP.setQuantite(entry.getValue().getSup50());
					elementEVP.setRubrique(statusAgent.equals("4") ? "HCL" : statusAgent.equals("7") ? "HDQ" : "");
					listEVP.add(elementEVP);
				}
				if (!StringUtils.isNullOrEmpty(entry.getValue().getSimples())) {
					elementEVP = new EVPElementDto();
					elementEVP.setPeriodeEV(entry.getKey());
					elementEVP.setQuantite(entry.getValue().getSimples());
					elementEVP.setRubrique(statusAgent.equals("4") ? "" : statusAgent.equals("7") ? "" : "HBK");
					listEVP.add(elementEVP);
				}
				if (!StringUtils.isNullOrEmpty(entry.getValue().getComposees())) {
					elementEVP = new EVPElementDto();
					elementEVP.setPeriodeEV(entry.getKey());
					elementEVP.setQuantite(entry.getValue().getComposees());
					elementEVP.setRubrique(statusAgent.equals("4") ? "" : statusAgent.equals("7") ? "" : "HBL");
					listEVP.add(elementEVP);
				}
				
				// Si l'agent existe, on ajoute les h. supp., sinon on créé l'agent avec ses heures.
				if (evp.getElements().containsKey(newAgent)) {
					evp.getElements().get(newAgent).addAll(listEVP);
				} else {
					ArrayList<EVPElementDto> listNewEVP = Lists.newArrayList();
					listNewEVP.addAll(listEVP);
					evp.getElements().put(newAgent, listNewEVP);
				}
			}
		}
	}

	@Override
	public byte[] exportEVP(EVPDto evpDto, String sheetName) throws FileNotFoundException, IOException {
		
		XSSFWorkbook wb = new XSSFWorkbook();
	    SimpleDateFormat sdf = new SimpleDateFormat("MMyy");
	    Sheet sheet1 = wb.createSheet(sheetName);
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    
	    // En-têtes
	    Row row = sheet1.createRow(0);
	    row.createCell(0).setCellValue("Soccle");
	    row.createCell(1).setCellValue("Matcle");
		row.createCell(2).setCellValue("Rubrique");
		row.createCell(3).setCellValue("Format");
		row.createCell(4).setCellValue("Nombre/base");
		row.createCell(5).setCellValue("Montant/Taux sal.");
		row.createCell(6).setCellValue("Période d'origine");
		row.createCell(7).setCellValue("Période de paie");
	    // Fin des en-têtes
	    
	    Integer i = 1;
	    
	    for (Map.Entry<AgentWithServiceDto,List<EVPElementDto>> entry : evpDto.getElements().entrySet()) {
			AgentWithServiceDto key = entry.getKey();
			
			for (EVPElementDto elt : entry.getValue()) {
			    row = sheet1.createRow(i);
			    row.createCell(0).setCellValue("18");
			    row.createCell(1).setCellValue(key.getIdTiarhe());
				row.createCell(2).setCellValue(elt.getRubrique());
				row.createCell(3).setCellValue("Q");
				row.createCell(4).setCellValue(elt.getQuantite());
				row.createCell(5).setCellValue("");
				row.createCell(6).setCellValue(sdf.format(elt.getPeriodeEV()));
				row.createCell(7).setCellValue(sdf.format(evpDto.getDatePeriodePaie()));
			    ++i;
			}
		}
	    
	    try {
	        wb.write(bos);
	    } finally {
	        bos.close();
	    }

	    // Write the output to a file
	    return bos.toByteArray();
	}
}
