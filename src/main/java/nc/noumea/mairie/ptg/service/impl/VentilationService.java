package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IVentilationAbsenceService;
import nc.noumea.mairie.ptg.service.IVentilationHSupService;
import nc.noumea.mairie.ptg.service.IVentilationPrimeService;
import nc.noumea.mairie.ptg.service.IVentilationService;
import nc.noumea.mairie.sirh.domain.Agent;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentilationService implements IVentilationService {

	@Autowired
	private IPointageRepository pointageRepository;
	
	@Autowired
	private IVentilationPrimeService ventilationPrimeService;

	@Autowired
	private IVentilationHSupService ventilationHSupService;

	@Autowired
	private IVentilationAbsenceService ventilationAbsenceService;
	
	@Autowired
	private IMairieRepository mairieRepository;
	
	public void processVentilation(Integer fromAgentId, Integer toAgentId, Date ventilationDate, TypeChainePaieEnum typeChainePaie, RefTypePointageEnum pointageType) {
		
		// Retrieving the current ventilation dates (from / to)
		VentilDate fromVentilDate = pointageRepository.getLatestVentilDate(typeChainePaie, true);
		VentilDate toVentilDate = pointageRepository.getLatestVentilDate(typeChainePaie, false);
		
		if (toVentilDate == null) {
			toVentilDate = new VentilDate();
			toVentilDate.setDateVentilation(ventilationDate);
			toVentilDate.setPaye(false);
			toVentilDate.setTypeChainePaie(typeChainePaie);
			toVentilDate.persist();
		}
		
		List<Integer> agentIds = new ArrayList<Integer>(); // retrieve agent ids
		
		for (Integer agentId : agentIds) {
			// 1. remove existing ventilations
			removePreviousVentilations(toVentilDate, agentId, pointageType); 	
			
			// 2. call processVentilationForAgent method
			processVentilationForAgent(toVentilDate, agentId,  fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation(), pointageType);
		}
		
	}
	
	public void processVentilationForAgent(VentilDate ventilDate, Integer idAgent, Date from, Date to, RefTypePointageEnum pointageType) {
		
		List<Pointage> agentsPointageForPeriod = pointageRepository.getListPointagesForVentilationByDateEtat(idAgent, from, to, pointageType);

		Map<Date, List<Pointage>> hSups = new HashMap<Date, List<Pointage>>();
		Map<Date, List<Pointage>> primes = new HashMap<Date, List<Pointage>>();
		Map<Date, List<Pointage>> abs = new HashMap<Date, List<Pointage>>();

		// distribute pointage into 3 maps treating them as groups
		distributePointages(agentsPointageForPeriod, hSups, primes, abs);

		// Ventilate pointages per type
		
		List<VentilHsup> hSupsVentilees = new ArrayList<VentilHsup>();
		for (Entry<Date, List<Pointage>> set : hSups.entrySet()) {
			Spcarr carr = mairieRepository.getAgentCurrentCarriere(Agent.getNoMatrFromIdAgent(idAgent), set.getKey());
			hSupsVentilees.add(ventilationHSupService.processHSup(idAgent, carr, set.getValue(), carr.getStatutCarriere()));
		}
		
		List<VentilPrime> primesVentilees = new ArrayList<VentilPrime>();
		for (Entry<Date, List<Pointage>> set : primes.entrySet()) {
			primesVentilees.addAll(ventilationPrimeService.processPrimesAgent(idAgent, set.getValue(), set.getKey()));
		}
		
		List<VentilAbsence> absVentilees = new ArrayList<VentilAbsence>();
		for (Entry<Date, List<Pointage>> set : abs.entrySet()) {
			absVentilees.add(ventilationAbsenceService.processAbsenceAgent(idAgent, set.getValue(), set.getKey()));
		}

		// persisting all the generated entities linking them to the current ventil date
		for (VentilHsup v : hSupsVentilees) {
			v.setVentilDate(ventilDate);
			v.persist();
		}
		
		for (VentilPrime v : primesVentilees) {
			v.setVentilDate(ventilDate);
			v.persist();
		}
		
		for (VentilAbsence v : absVentilees) {
			v.setVentilDate(ventilDate);
			v.persist();
		}
	}
	
	/**
	 * This method distributes all pointages into 3 maps
	 * hSups: with a list of pointage per week
	 * primes & abs: with a list of pointage per month
	 */
	protected void distributePointages(List<Pointage> pointages, Map<Date, List<Pointage>> hSups, Map<Date, List<Pointage>> primes, Map<Date, List<Pointage>> abs) {
		
		for (Pointage ptg : pointages) {
			
			Date mapDate = ptg.getDateLundi();
			Map<Date, List<Pointage>> eligibleMap = null;
			
			switch(ptg.getTypePointageEnum()) {
				case H_SUP:
					mapDate = ptg.getDateLundi();
					eligibleMap = hSups;
					break;
				case PRIME:
					mapDate = new LocalDate(ptg.getDateDebut()).withDayOfMonth(1).toDate();
					eligibleMap = primes;
					break;
				case ABSENCE:
					mapDate = ptg.getDateLundi();
					eligibleMap = abs;
					break;
			}
			
			if (!eligibleMap.containsKey(mapDate))
				eligibleMap.put(mapDate, new ArrayList<Pointage>());
			
			eligibleMap.get(mapDate).add(ptg);
		}
	}

	/**
	 * This method removes all existing ventilations for a given
	 * agent, date, and typePointage. 
	 * Type is optional: if it is not given, all types will be deleted.
	 * @param date
	 * @param agentId
	 * @param pointageType
	 */
	protected void removePreviousVentilations(VentilDate date, Integer agentId, RefTypePointageEnum pointageType) {
	
		if (pointageType == null) {
			pointageRepository.removeVentilationsForDateAgentAndType(date, agentId, RefTypePointageEnum.ABSENCE);
			pointageRepository.removeVentilationsForDateAgentAndType(date, agentId, RefTypePointageEnum.H_SUP);
			pointageRepository.removeVentilationsForDateAgentAndType(date, agentId, RefTypePointageEnum.PRIME);
		}
		else {
			pointageRepository.removeVentilationsForDateAgentAndType(date, agentId, pointageType);
		}
	}
}
