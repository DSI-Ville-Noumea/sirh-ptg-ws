package nc.noumea.mairie.ptg.service.impl;

import java.util.Date;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
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
import nc.noumea.mairie.ptg.dto.etatsPayeur.PrimesEtatPayeurDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;
import nc.noumea.mairie.ptg.workflow.WorkflowInvalidStateException;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.ws.ISirhWSConsumer;

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
	private ISirhRepository sirhRepository;
	
	@Autowired
	private IAccessRightsRepository accessRightRepository;
	
	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Override
	public CanStartWorkflowPaieActionDto canStartExportEtatPayeurAction(TypeChainePaieEnum chainePaie) {
		CanStartWorkflowPaieActionDto result = new CanStartWorkflowPaieActionDto();
		result.setCanStartAction(paieWorkflowService.canChangeStateToExportEtatsPayeurStarted(chainePaie));
		return result;
	}

	@Override
	public EtatPayeurDto getAbsencesEtatPayeurDataForStatut(AgentStatutEnum statut) {

		TypeChainePaieEnum chainePaie = helperService.getTypeChainePaieFromStatut(statut);

		VentilDate toVentilDate = ventilationRepository.getLatestVentilDate(chainePaie, false);

		if (toVentilDate == null) {
			logger.error(
					"Impossible to retrieve data for Etats Payeur, there is no unpaid ventilation for TypeChainePaie [{}]",
					chainePaie);
			return new EtatPayeurDto();
		}

		EtatPayeurDto result = new EtatPayeurDto(chainePaie, statut, toVentilDate.getDateVentilation());

		VentilDate fromVentilDate = ventilationRepository.getLatestVentilDate(chainePaie, true);

		// For all VentilAbsences of this ventilation ordered by dateLundi asc
		for (VentilAbsence va : toVentilDate.getVentilAbsences()) {

			// 1. Verify whether this agent is eligible, through its
			// AgentStatutEnum (Spcarr)
			if (!isAgentEligibleToVentilation(va.getIdAgent(), statut, toVentilDate.getDateVentilation())) {
				logger.info("Agent {} not eligible for Etats payeurs (status not matching), skipping to next.",
						va.getIdAgent());
				continue;
			}

			// 2. If the period concerns a date prior to ventilation, fetch the
			// second last ventilated item to
			// output the difference
			VentilAbsence vaOld = null;
			if (va.getDateLundi().before(fromVentilDate.getDateVentilation())) {
				vaOld = ventilationRepository.getPriorVentilAbsenceForAgentAndDate(
						va.getIdAgent(), va.getDateLundi(),	va);
			}

			// 3. Then create the DTOs for Absence if the value is other than 0
			// or different from previous one
			if (va.getMinutesConcertee() != (vaOld != null ? vaOld.getMinutesConcertee() : 0)) {
				AbsencesEtatPayeurDto dto = new AbsencesEtatPayeurDto(va, vaOld, true, helperService);
				fillAgentsData(dto);
				result.getAbsences().add(dto);
			}
			if (va.getMinutesNonConcertee() != (vaOld != null ? vaOld.getMinutesNonConcertee() : 0)) {
				AbsencesEtatPayeurDto dto = new AbsencesEtatPayeurDto(va, vaOld, false, helperService);
				fillAgentsData(dto);
				result.getAbsences().add(dto);
			}
		}

		return result;
	}

	@Override
	public EtatPayeurDto getHeuresSupEtatPayeurDataForStatut(AgentStatutEnum statut) {

		TypeChainePaieEnum chainePaie = helperService.getTypeChainePaieFromStatut(statut);

		VentilDate toVentilDate = ventilationRepository.getLatestVentilDate(chainePaie, false);

		if (toVentilDate == null) {
			logger.error(
					"Impossible to retrieve data for Etats Payeur, there is no unpaid ventilation for TypeChainePaie [{}]",
					chainePaie);
			return new EtatPayeurDto();
		}

		EtatPayeurDto result = new EtatPayeurDto(chainePaie, statut, toVentilDate.getDateVentilation());

		VentilDate fromVentilDate = ventilationRepository.getLatestVentilDate(chainePaie, true);

		// For all VentilAbsences of this ventilation ordered by dateLundi asc
		for (VentilHsup vh : toVentilDate.getVentilHsups()) {

			// 1. Verify whether this agent is eligible, through its
			// AgentStatutEnum (Spcarr)
			if (!isAgentEligibleToVentilation(vh.getIdAgent(), statut, toVentilDate.getDateVentilation())) {
				logger.info("Agent {} not eligible for Etats payeurs (status not matching), skipping to next.",
						vh.getIdAgent());
				continue;
			}

			// 2. If the period concerns a date prior to ventilation, fetch the
			// second last ventilated item to
			// output the difference
			VentilHsup vhOld = null;
			if (vh.getDateLundi().before(fromVentilDate.getDateVentilation())) {
				vhOld = ventilationRepository.getPriorVentilHSupAgentAndDate(
						vh.getIdAgent(), vh.getDateLundi(),	vh);
			}

			// 3. Then create the DTOs for HSups
			HeuresSupEtatPayeurDto dto = new HeuresSupEtatPayeurDto(vh, vhOld, helperService);
			fillAgentsData(dto);
			result.getHeuresSup().add(dto);
		}

		return result;
	}

	@Override
	public EtatPayeurDto getPrimesEtatPayeurDataForStatut(AgentStatutEnum statut) {

		TypeChainePaieEnum chainePaie = helperService.getTypeChainePaieFromStatut(statut);

		VentilDate toVentilDate = ventilationRepository.getLatestVentilDate(chainePaie, false);

		if (toVentilDate == null) {
			logger.error(
					"Impossible to retrieve data for Etats Payeur, there is no unpaid ventilation for TypeChainePaie [{}]",
					chainePaie);
			return new EtatPayeurDto();
		}

		EtatPayeurDto result = new EtatPayeurDto(chainePaie, statut, toVentilDate.getDateVentilation());

		VentilDate fromVentilDate = ventilationRepository.getLatestVentilDate(chainePaie, true);

		// For all VentilAbsences of this ventilation ordered by dateLundi asc
		for (VentilPrime vp : toVentilDate.getVentilPrimes()) {

			// 1. Verify whether this agent is eligible, through its
			// AgentStatutEnum (Spcarr)
			if (!isAgentEligibleToVentilation(vp.getIdAgent(), statut, toVentilDate.getDateVentilation())) {
				logger.info("Agent {} not eligible for Etats payeurs (status not matching), skipping to next.",
						vp.getIdAgent());
				continue;
			}

			// 2. If the period concerns a date prior to ventilation, fetch the
			// second last ventilated item to
			// output the difference
			VentilPrime vpOld = null;
			if (vp.getDateDebutMois().before(fromVentilDate.getDateVentilation())) {
				vpOld = ventilationRepository.getPriorVentilPrimeForAgentAndDate(
						vp.getIdAgent(), vp.getDateDebutMois(),	vp);
			}

			// 3. Then create the DTOs for Primes 
			PrimesEtatPayeurDto dto = new PrimesEtatPayeurDto(vp, vpOld, helperService);
			fillAgentsData(dto);
			result.getPrimes().add(dto);
		}

		return result;
	}

	/**
	 * This methods takes an abstract base object for Etat payeur and
	 * completes the agent's name, lastname and approbateur's info
	 * @param item
	 */
	protected void fillAgentsData(AbstractItemEtatPayeurDto item) {
		
		Agent ag = sirhRepository.getAgent(item.getIdAgent());
		item.setNom(ag.getDisplayNom());
		item.setPrenom(ag.getDisplayPrenom());
		
		Integer idAgentApprobateur = accessRightRepository.getAgentsApprobateur(item.getIdAgent());
		
		if (idAgentApprobateur == null)
			return;
		
		AgentWithServiceDto agApproDto = sirhWsConsumer.getAgentService(idAgentApprobateur, helperService.getCurrentDate());
		item.setApprobateurIdAgent(idAgentApprobateur);
		item.setApprobateurNom(agApproDto.getNom());
		item.setApprobateurPrenom(agApproDto.getPrenom());
		item.setApprobateurServiceLabel(String.format("%s - %s", agApproDto.getCodeService(), agApproDto.getService()));
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
		Spcarr carr = sirhRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent), date);
		AgentStatutEnum agentStatus = carr != null ? carr.getStatutCarriere() : null;
		return agentStatus == statut;
	}

	@Override
	public ReturnMessageDto startExportEtatsPayeur(Integer agentIdExporting, AgentStatutEnum statut) {
		
		logger.info("Starting exportEtatsPayeurs of Pointages for Agents statut [{}], asked by [{}]", 
				agentIdExporting, statut);
		
		ReturnMessageDto result = new ReturnMessageDto();
		
		// 1. Call workflow to make sure we can start the export process
		try {
			paieWorkflowService.changeStateToExportPaieStarted(helperService.getTypeChainePaieFromStatut(statut));
		} catch (WorkflowInvalidStateException e) {
			logger.error("Could not start exportPaie process: {}", e.getMessage());
			result.getErrors().add(e.getMessage());
			return result;
		}
		
		exportEtatsPayeur(agentIdExporting, statut);
				
        logger.info("Added exportEtatPayeur task.");
        
        return result;
	}
	
	public void exportEtatsPayeur(Integer agentIdExporting, AgentStatutEnum statut) {
		
	}
}
