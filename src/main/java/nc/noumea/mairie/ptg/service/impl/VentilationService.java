package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IPointageCalculeService;
import nc.noumea.mairie.ptg.service.IVentilationAbsenceService;
import nc.noumea.mairie.ptg.service.IVentilationHSupService;
import nc.noumea.mairie.ptg.service.IVentilationPrimeService;
import nc.noumea.mairie.ptg.service.IVentilationService;
import nc.noumea.mairie.sirh.domain.Agent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
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
	private IPointageCalculeService pointageCalculeService;
	
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
		
		for (Integer idAgent : agentIds) {
			// 1. remove existing ventilations
			removePreviousVentilations(toVentilDate, idAgent, pointageType); 	
			
			// 2. remove previously calculated pointages
			removePreviousCalculatedPointages(idAgent, fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation());
			
			// 3. generate pointage calcules
			calculatePointages(idAgent,  fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation());
			
			// 4. call processVentilationForAgent method
			processVentilationForAgent(toVentilDate, idAgent,  fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation(), pointageType);
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
	 * This method distributes all pointages into 4 maps
	 * hSups: with a list of pointage per week
	 * primes & abs: with a list of pointage per month
	 * allByWeek: all kinds per week
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
	 * @param idAgent
	 * @param pointageType
	 */
	protected void removePreviousVentilations(VentilDate date, Integer idAgent, RefTypePointageEnum pointageType) {
	
		if (pointageType == null) {
			pointageRepository.removeVentilationsForDateAgentAndType(date, idAgent, RefTypePointageEnum.ABSENCE);
			pointageRepository.removeVentilationsForDateAgentAndType(date, idAgent, RefTypePointageEnum.H_SUP);
			pointageRepository.removeVentilationsForDateAgentAndType(date, idAgent, RefTypePointageEnum.PRIME);
		}
		else {
			pointageRepository.removeVentilationsForDateAgentAndType(date, idAgent, pointageType);
		}
	}

	/**
	 * Removes existing pointages calcules for agent and between two dates
	 * @param idAgent
	 * @param from
	 * @param to
	 */
	protected void removePreviousCalculatedPointages(Integer idAgent, Date from, Date to) {
		pointageRepository.removePointageCalculesForDateAgent(idAgent, from, to);
	}
	
	/**
	 * Generates calculated pointages for all weeks in between two dates
	 * @param idAgent
	 * @param from
	 * @param to
	 */
	protected void calculatePointages(Integer idAgent, Date from, Date to) {

		Date monday = from;
		List<PointageCalcule> result = new ArrayList<PointageCalcule>();
		
		do {
			monday = new DateTime(monday).withDayOfWeek(DateTimeConstants.MONDAY).plusWeeks(1).toDate();
			Spcarr carr = mairieRepository.getAgentCurrentCarriere(Agent.getNoMatrFromIdAgent(idAgent), monday);
			result.addAll(pointageCalculeService.calculatePointagesForAgentAndWeek(idAgent, carr.getStatutCarriere(), monday));
		} while (monday.before(to)) ;

		for (PointageCalcule ptgC : result) {
			ptgC.persist();
		}
	}
}
