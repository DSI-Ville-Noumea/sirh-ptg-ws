package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spmatr;
import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPointagePK;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IExportAbsencePaieService;
import nc.noumea.mairie.ptg.service.IExportPaieService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportPaieService implements IExportPaieService {

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
	
	public void exportToPaie(Integer agentIdValidating, AgentStatutEnum statut) {
		
		// 1. Retrieve eligible ventilation in order to get dates
		VentilDate ventilDate = ventilationRepository.getLatestVentilDate(helperService.getTypeChainePaieFromStatut(statut), false);
		
		// If no ventilation has ever been ran, return now
		if (ventilDate == null)
			return;
		
		// 2. retrieve list of Agent from pointages
		List<Integer> idAgents = new ArrayList<Integer>();
		
		for (Integer idAgent : idAgents) {
			// 3. Verify whether this agent is eligible, through its Status (Spcarr)
			if (!isAgentEligibleToExport(idAgent, statut, ventilDate.getDateVentilation()))
				continue;
			
			// 4. Retrieve all pointages that have been ventilated
			List<Pointage> ventilatedPointages = ventilationRepository.getPointagesVentilesForAgent(idAgent, ventilDate.getIdVentilDate());
			
			// 5. Export absences
			persistSppac(exportAbsencePaieService.exportAbsencesToPaie(ventilatedPointages));
			
			// 6. Mark pointages as validated
			markPointagesAsValidated(ventilatedPointages, agentIdValidating);
			
			// 7. Update SPMATR with oldest pointage month
			updateSpmatrForAgentAndPointages(idAgent, ventilatedPointages);
		}
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
	protected void updateSpmatrForAgentAndPointages(Integer idAgent, List<Pointage> pointages) {

		Pointage oldestPointage = pointages.get(0);
		
		for(Pointage ptg : pointages) {
			if (ptg.getDateDebut().before(oldestPointage.getDateDebut()))
				oldestPointage = ptg;
		}
		
		Spmatr matr = mairieRepository.findSpmatrForAgent(idAgent);
		
		Integer oldestPointageIntegerDate = helperService.getIntegerMonthDateMairieFromDate(oldestPointage.getDateDebut());
		
		if (matr == null) {
			matr = new Spmatr();
			matr.setNomatr(helperService.getMairieMatrFromIdAgent(idAgent));
			matr.setPerrap(oldestPointageIntegerDate);
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
}
