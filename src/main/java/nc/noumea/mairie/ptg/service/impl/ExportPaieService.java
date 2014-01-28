package nc.noumea.mairie.ptg.service.impl;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spmatr;
import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.domain.Spphre;
import nc.noumea.mairie.domain.Sppprm;
import nc.noumea.mairie.domain.Spprim;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.ExportPaieTask;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IExportPaieAbsenceService;
import nc.noumea.mairie.ptg.service.IExportPaieHSupService;
import nc.noumea.mairie.ptg.service.IExportPaiePrimeService;
import nc.noumea.mairie.ptg.service.IExportPaieService;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;
import nc.noumea.mairie.ptg.workflow.WorkflowInvalidStateException;

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
	private IPointageRepository pointageRepository;
	
	@Autowired
	private ISirhRepository sirhRepository;
	
	@Autowired
	private IMairieRepository mairieRepository;
	
	@Autowired
	private HelperService helperService;
	
	@Autowired
	private IExportPaieAbsenceService exportPaieAbsenceService;
	
	@Autowired
	private IExportPaieHSupService exportPaieHSupService;
	
	@Autowired
	private IExportPaiePrimeService exportPaiePrimeService;
	
	@Autowired
	private IPointageService pointageService;
	
	@Autowired
	private IPaieWorkflowService paieWorkflowService;
	
	@Override
	public ReturnMessageDto startExportToPaie(Integer agentIdValidating, AgentStatutEnum statut) {
		
		logger.info("Starting exportToPaie of Pointages for Agents statut [{}], asked by [{}]", 
    			agentIdValidating, statut);
		
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
		
		// 2. Call workflow to make sure we can start the export process
		try {
			paieWorkflowService.changeStateToExportPaieStarted(helperService.getTypeChainePaieFromStatut(statut));
		} catch (WorkflowInvalidStateException e) {
			logger.error("Could not start exportPaie process: {}", e.getMessage());
			result.getErrors().add(e.getMessage());
			return result;
		}
				
		// 3. retrieve list of Agent from pointages
		List<Integer> idAgents = ventilationRepository.getListIdAgentsForExportPaie(ventilDate.getIdVentilDate());
		
		logger.info("Found {} agents to export pointages for (based on all available pointages for export and before agent filtering).", idAgents.size());
		
		for (Integer agent : idAgents) {
			
			// 4. Verify whether this agent is eligible, through its Status (Spcarr)
			if (!isAgentEligibleToExport(agent, statut, ventilDate.getDateVentilation())) {
				logger.info("Agent {} not eligible for export (status not matching), skipping to next.", agent);
				continue;
			}
			
			// 5. Create ExportPaieTask to notify job of a new expot to do
			ExportPaieTask task = new ExportPaieTask();
			task.setIdAgent(agent);
			task.setIdAgentCreation(agentIdValidating);
			task.setTypeChainePaie(chainePaie);
			task.setDateCreation(helperService.getCurrentDate());
			task.setVentilDate(ventilDate);
			pointageRepository.persisEntity(task);
			
			result.getInfos().add(String.format("Agent %s", agent));
        }
        
        logger.info("Added exportPaie tasks for {} agents after filtering.", result.getInfos().size());
        
        return result;
	}
	
	@Override
	public void processExportPaieForAgent(Integer idExportPaieTask) {

		logger.info("Starting exportation to Paie of ExportPaieTask [{}]", idExportPaieTask);
		ExportPaieTask task = pointageRepository.getEntity(ExportPaieTask.class, idExportPaieTask);
    	logger.info("Exportation of Agent [{}] created by agent [{}] at [{}].", task.getIdAgent(), task.getIdAgentCreation(), task.getDateCreation());
		
    	Integer idAgent = task.getIdAgent();
    	VentilDate ventilDate = task.getVentilDate();
    	Integer agentIdValidating = task.getIdAgentCreation();
    	TypeChainePaieEnum chainePaie = task.getTypeChainePaie();
    	
    	// 1. Retrieve all pointages that have been ventilated
    	logger.debug("Retrieving ventilated pointages...");
		List<Pointage> ventilatedPointages = pointageService.getPointagesVentilesForAgent(idAgent, ventilDate);
		List<PointageCalcule> ventilatedPointagesCalcules = pointageService.getPointagesCalculesVentilesForAgent(idAgent, ventilDate);
		
		if (ventilatedPointages.size() == 0) {
			logger.debug("No pointages to export. Exiting...");
			return;
		}
		
		// 2. Export absences
    	logger.debug("Exporting Absences...");
		persistSppac(exportPaieAbsenceService.exportAbsencesToPaie(ventilatedPointages));
		
		// 3. Export hSups
    	logger.debug("Exporting HSups...");
		List<VentilHsup> vHsups = ventilationRepository.getListVentilHSupForAgentAndVentilDateOrderByDateAsc(idAgent, ventilDate.getIdVentilDate());
		persistSpphre(exportPaieHSupService.exportHsupToPaie(vHsups));
		
		// 4. Export Primes
    	logger.debug("Exporting Primes...");
		List<VentilPrime> vPrimes = ventilationRepository.getListVentilPrimesMoisForAgentAndVentilDateOrderByDateAsc(idAgent, ventilDate.getIdVentilDate());
		persistSpprim(exportPaiePrimeService.exportPrimesMoisToPaie(vPrimes));
		persistSppprm(exportPaiePrimeService.exportPrimesJourToPaie(ventilatedPointages));
		persistSppprm(exportPaiePrimeService.exportPrimesCalculeesJourToPaie(ventilatedPointagesCalcules));
		
		// 5. Mark pointages as validated
    	logger.debug("Marking pointages as Etat : valide ...");
		markPointagesAsValidated(ventilatedPointages, agentIdValidating);
		markPointagesCalculesAsValidated(ventilatedPointagesCalcules);
		
		// 6. Update SPMATR with oldest pointage month
    	logger.debug("Updating SPMATR for Paie...");
		updateSpmatrForAgentAndPointages(idAgent, chainePaie, ventilatedPointages);
    	
		logger.info("Exportation of idExportPaieTask [{}] done.", idExportPaieTask);
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

			if (ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.JOURNALISE)
				continue;
			
			EtatPointage ep = new EtatPointage();
			ep.setDateEtat(currentDate);
			ep.setDateMaj(currentDate);
			ep.setPointage(ptg);
			ep.setEtat(EtatPointageEnum.VALIDE);
			ep.setIdAgent(idAgent);
			ptg.getEtats().add(ep);
		}
	}
	
	/**
	 * Updates each pointage calcule to set them as VALIDE state
	 * @param pointages
	 */
	protected void markPointagesCalculesAsValidated(List<PointageCalcule> pointagesCalcules) {
		for (PointageCalcule ptgC : pointagesCalcules) {
			if (ptgC.getEtat() == EtatPointageEnum.JOURNALISE)
				continue;
			
			ptgC.setEtat(EtatPointageEnum.VALIDE);
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

		// If no pointages were updated, return without updating SPMATR
		if (pointages.size() == 0)
			return;
		
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
			sirhRepository.mergeEntity(sppact);
		}
	}
	
	protected void persistSpphre(List<Spphre> hSups) {
		for (Spphre spphre : hSups) {
			sirhRepository.mergeEntity(spphre);
		}
	}
	
	protected void persistSppprm(List<Sppprm> prms) {
		for (Sppprm prm : prms) {
			sirhRepository.mergeEntity(prm);
		}
	}
	
	protected void persistSpprim(List<Spprim> pris) {
		for (Spprim pri : pris) {
			sirhRepository.mergeEntity(pri);
		}
	}

	@Override
	public CanStartWorkflowPaieActionDto canStartExportPaieAction(TypeChainePaieEnum chainePaie) {
		CanStartWorkflowPaieActionDto result = new CanStartWorkflowPaieActionDto();
		result.setCanStartAction(paieWorkflowService.canChangeStateToExportPaieStarted(chainePaie));
		return result;
	}

	@Override
	public void stopExportToPaie(TypeChainePaieEnum typeChainePaie) throws WorkflowInvalidStateException {
		paieWorkflowService.changeStateToExportPaieDone(typeChainePaie);
	}
	
	@Override
	public ExportPaieTask findExportPaieTask(Integer idExportPaieTask) {
		return pointageRepository.getEntity(ExportPaieTask.class, idExportPaieTask);
	}
}
