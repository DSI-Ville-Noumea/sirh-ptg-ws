package nc.noumea.mairie.ptg.service.impl;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spmatr;
import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPointagePK;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IExportAbsencePaieService;
import nc.noumea.mairie.ptg.service.IExportPaieService;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportPaieService implements IExportPaieService {

	private Logger logger = LoggerFactory.getLogger(ExportPaieService.class);
	
	@Autowired
	private IVentilationRepository ventilationRepository;

	@Autowired
	private ISirhRepository sirhRepository;
	
	@Autowired
	private IMairieRepository mairieRepository;
	
	@Autowired
	private HelperService helperService;
	
	@Autowired
	private IExportAbsencePaieService exportAbsencePaieService;
	
	@Autowired
	private IPointageService pointageService;
	
	@Autowired
	private IPaieWorkflowService paieWorkflowService;
	
	public ReturnMessageDto exportToPaie(Integer agentIdValidating, AgentStatutEnum statut) {
		
		logger.info("Starting exportation to Paie with status [{}]", statut);
		
		ReturnMessageDto result = new ReturnMessageDto();
		
		// 0. Verify and set PAIE status to EXPORTING
//		try {
//			paieStatusService.setExportStatus();
//		} catch (PaieStatusServiceException ex) {
//			logger.error("Unable to start export process : ", ex);
//			result.getErrors().add(ex.getMessage());
//			return result;
//		}
		
		// 1. Retrieve eligible ventilation in order to get dates
		TypeChainePaieEnum chainePaie = helperService.getTypeChainePaieFromStatut(statut);
		VentilDate ventilDate = ventilationRepository.getLatestVentilDate(chainePaie, false);
		
		// If no ventilation has ever been ran, return now
		if (ventilDate == null) {
			logger.info("No unpaid ventilation date found. Nothing to export. Stopping process here.");
			result.getInfos().add(String.format("Aucune ventilation n'existe pour le statut [%s].", statut));
			return result;
		}
		
		// 2. retrieve list of Agent from pointages
		List<Integer> idAgents = ventilationRepository.getListIdAgentsForExportPaie(ventilDate.getIdVentilDate());
		
		logger.info("Found {} agents to export pointages for (based on all available pointages for export and before agent filtering).", idAgents.size());
		int nbProcessedAgents = 0;
		
		for (Integer idAgent : idAgents) {
			
			// 3. Verify whether this agent is eligible, through its Status (Spcarr)
			if (!isAgentEligibleToExport(idAgent, statut, ventilDate.getDateVentilation())) {
				logger.info("Agent {} not eligible for export (status not matching), skipping to next.", idAgent);
				continue;
			}
			
			nbProcessedAgents++;
			logger.debug("Exporting pointages of agent {} [#{}]...", idAgent, nbProcessedAgents);
			
			// 4. Retrieve all pointages that have been ventilated
			List<Pointage> ventilatedPointages = pointageService.getPointagesVentilesForAgent(idAgent, ventilDate);
			
			// 5. Export absences
			persistSppac(exportAbsencePaieService.exportAbsencesToPaie(ventilatedPointages));
			
			// 6. Mark pointages as validated
			markPointagesAsValidated(ventilatedPointages, agentIdValidating);
			
			// 7. Update SPMATR with oldest pointage month
			updateSpmatrForAgentAndPointages(idAgent, chainePaie, ventilatedPointages);
		}
		
		logger.info("Exported pointages of {} agents with status {}", nbProcessedAgents, statut);
		
		return result;
	}
	
	/**
	 * Returns whether or not an agent is eligible to ventilation considering its
	 * status (F, C or CC) we are currently ventilating
	 * @param idAgent
	 * @param statut
	 * @return 
	 */
	protected Boolean isAgentEligibleToExport(Integer idAgent, AgentStatutEnum statut, Date date) {
		Spcarr carr = sirhRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent), date);
		AgentStatutEnum agentStatus = carr != null ? carr.getStatutCarriere() : null;
		return agentStatus == statut;
	}
	
	/**
	 * Updates each pointage to set them as VALIDE state after being exported to Paie
	 * @param pointages
	 * @param idAgent 
	 */
	protected void markPointagesAsValidated(List<Pointage> pointages, int idAgent) {
		
		Date currentDate = helperService.getCurrentDate();
		
		for (Pointage ptg : pointages) {
			EtatPointagePK pk = new EtatPointagePK();
			pk.setDateEtat(currentDate);
			pk.setPointage(ptg);
			EtatPointage ep = new EtatPointage();
			ep.setEtat(EtatPointageEnum.VALIDE);
			ep.setIdAgent(idAgent);
			ep.setEtatPointagePk(pk);
			ptg.getEtats().add(ep);
		}
	}
	
	/**
	 * Updates the SPMATR paie table in order to set the earliest date
	 * at which the Paie should start re processing the pointages (if there
	 * were modifications on old pointages for example)
	 * @param idAgent
	 * @param pointages
	 */
	protected void updateSpmatrForAgentAndPointages(Integer idAgent, TypeChainePaieEnum chainePaie, List<Pointage> pointages) {

		Pointage oldestPointage = pointages.get(0);
		
		for(Pointage ptg : pointages) {
			if (ptg.getDateDebut().before(oldestPointage.getDateDebut()))
				oldestPointage = ptg;
		}
		
		Integer nomatr = helperService.getMairieMatrFromIdAgent(idAgent);
		
		Spmatr matr = mairieRepository.findSpmatrForAgent(nomatr);
		
		Integer oldestPointageIntegerDate = helperService.getIntegerMonthDateMairieFromDate(oldestPointage.getDateDebut());
		
		if (matr == null) {
			matr = new Spmatr();
			matr.setNomatr(nomatr);
			matr.setPerrap(oldestPointageIntegerDate);
			matr.setTypeChainePaie(chainePaie);
			mairieRepository.persistEntity(matr);
			return;
		}
		
		if (matr.getPerrap() > oldestPointageIntegerDate) {
			matr.setPerrap(oldestPointageIntegerDate);
		}
	}
	
	protected void persistSppac(List<Sppact> absences) {
		for (Sppact sppact : absences) {
			sppact.merge();
		}
	}

	@Override
	public CanStartWorkflowPaieActionDto canStartExportPaieActionDto(TypeChainePaieEnum chainePaie) {
		CanStartWorkflowPaieActionDto result = new CanStartWorkflowPaieActionDto();
		result.setCanStartExportPaieAction(paieWorkflowService.canChangeStateToExportPaieStarted(chainePaie));
		return result;
	}
}
