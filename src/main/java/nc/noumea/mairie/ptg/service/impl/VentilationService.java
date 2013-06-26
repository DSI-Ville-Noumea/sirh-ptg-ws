package nc.noumea.mairie.ptg.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.service.IVentilationAbsenceService;
import nc.noumea.mairie.ptg.service.IVentilationHSupService;
import nc.noumea.mairie.ptg.service.IVentilationPrimeService;
import nc.noumea.mairie.ptg.service.IVentilationService;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentilationService implements IVentilationService {

	@Autowired
	private IVentilationPrimeService ventilationPrimeService;

	@Autowired
	private IVentilationHSupService ventilationHSupService;

	@Autowired
	private IVentilationAbsenceService ventilationAbsenceService;
	
	@Autowired
	private IMairieRepository mairieRepository;
	
	@Autowired
	private IPointageService pointageService;
	
	public void processVentilation(Date ventilationDate, AgentStatutEnum statut, RefTypePointageEnum pointageType) {
		
		DateTime ventilDate = new DateTime(ventilationDate);
		
		Date fromDate = new DateTime(ventilDate.getYear(), ventilDate.getMonthOfYear(), 1, 0, 0, 0).toDate();
		Date toDate =  new DateTime(ventilDate.getYear(), ventilDate.getMonthOfYear(), ventilDate.dayOfMonth().getMaximumValue(), 0, 0, 0).toDate();
		
		// Get eligible agents
		List<Integer> idAgents = mairieRepository.getAllAgentIdsByStatus(statut);
		
		// Get pointages eligible to ventilation depending on agents and date
		for (Integer idAgent : idAgents) {
			
			List<Pointage> monthPointages = pointageService.getLatestPointagesForAgentAndDates(
					idAgent, fromDate, toDate, 
					Arrays.asList(EtatPointageEnum.EN_ATTENTE, EtatPointageEnum.APPROUVE));
			
			switch(pointageType) {
				case ABSENCE:
					VentilAbsence abs = ventilationAbsenceService.processAbsenceAgent(idAgent, monthPointages, fromDate);
					break;
			}
		}
		
		// call right ventilation service depending on pointageType
		
		// save result in db
		
		// return status ok
	}
	
//	protected void 
}
