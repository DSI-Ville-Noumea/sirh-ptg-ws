package nc.noumea.mairie.ptg.service.impl;

import java.util.Date;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.AbsencesEtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

	@Override
	public CanStartWorkflowPaieActionDto canStartExportEtatPayeurAction(TypeChainePaieEnum chainePaie) {
		CanStartWorkflowPaieActionDto result = new CanStartWorkflowPaieActionDto();
		result.setCanStartExportPaieAction(paieWorkflowService.canChangeStateToExportEtatPayeurStarted(chainePaie));
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
				result.getAbsences().add(dto);
			}
			if (va.getMinutesNonConcertee() != (vaOld != null ? vaOld.getMinutesNonConcertee() : 0)) {
				AbsencesEtatPayeurDto dto = new AbsencesEtatPayeurDto(va, vaOld, false, helperService);
				result.getAbsences().add(dto);
			}
		}

		return result;
	}

	@Override
	public EtatPayeurDto getHeuresSupEtatPayeurDataForStatut(AgentStatutEnum statut) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EtatPayeurDto getPrimesEtatPayeurDataForStatut(AgentStatutEnum statut) {
		// TODO Auto-generated method stub
		return null;
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
}
