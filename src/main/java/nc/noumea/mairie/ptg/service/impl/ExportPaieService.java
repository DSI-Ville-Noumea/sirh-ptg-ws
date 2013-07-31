package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
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
	private HelperService helperService;
	
	public void exportToPaie(Integer agentIdValidating, AgentStatutEnum statut) {
		
		// 1. Retrieve eligible ventilation in order to get dates
		VentilDate ventilDate = ventilationRepository.getLatestVentilDate(helperService.getTypeChainePaieFromStatut(statut), false);
		
		// If no ventilation has ever been ran, return now
		if (ventilDate == null)
			return;
		
		// 2. retrieve list of Agent from pointages
		List<Integer> idAgents = new ArrayList<Integer>();
		
		// 3. for each agent
		for (Integer idAgent : idAgents) {
			// 1. Verify whether this agent is eligible, through its Status (Spcarr)
			if (!isAgentEligibleToExport(idAgent, statut, ventilDate.getDateVentilation()))
				continue;
		
			// 5. export absences
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
}
