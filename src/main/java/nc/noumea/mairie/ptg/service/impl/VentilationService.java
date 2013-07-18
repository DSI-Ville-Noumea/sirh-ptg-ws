package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPointagePK;
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
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
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
	private IMairieRepository mairieRepository;
	
	@Autowired
	private IVentilationRepository ventilationRepository;
	
	@Autowired
	private IVentilationPrimeService ventilationPrimeService;

	@Autowired
	private IVentilationHSupService ventilationHSupService;

	@Autowired
	private IVentilationAbsenceService ventilationAbsenceService;
	
	@Autowired
	private IPointageCalculeService pointageCalculeService;
	
	@Autowired
	private HelperService helperService;
	
	public void processVentilation(Integer idAgent, List<Integer> agents, Date ventilationDate, AgentStatutEnum statut, RefTypePointageEnum pointageType) {
		
		// Retrieving the current ventilation dates (from / to)
		TypeChainePaieEnum typeChainePaie = getTypeChainePaieFromStatut(statut);
		VentilDate fromVentilDate = ventilationRepository.getLatestVentilDate(typeChainePaie, true);
		VentilDate toVentilDate = ventilationRepository.getLatestVentilDate(typeChainePaie, false);
		
		if (toVentilDate == null) {
			toVentilDate = new VentilDate();
			toVentilDate.setDateVentilation(new DateTime(ventilationDate).withHourOfDay(23).withMinuteOfHour(59).toDate());
			toVentilDate.setPaye(false);
			toVentilDate.setTypeChainePaie(typeChainePaie);
			toVentilDate.persist();
		}

		// If no agents were set as parameters, we need to take everyone concerned
		if (agents.size() == 0) {
			agents = ventilationRepository
					.getListIdAgentsForVentilationByDateAndEtat(
							fromVentilDate.getDateVentilation(),
							toVentilDate.getDateVentilation());
		}

		// For all seleted agents, proceed to ventilation
		for (Integer agent : agents) {
			// 1. Verify whether this agent is eligible, through its Status (Spcarr)
			Spcarr carr = isAgentEligibleToVentilation(agent, statut, toVentilDate.getDateVentilation());
			if (carr == null)
				continue;
			
			// 2. remove existing ventilations
			removePreviousVentilations(toVentilDate, agent, pointageType); 	
			
			// 3. select all distinct dates of pointages needing ventilation
			List<Date> pointagesToVentilateDates = ventilationRepository.getDistinctDatesOfPointages(
					idAgent, fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation());
			
			List<Pointage> pointagesVentiles = new ArrayList<Pointage>();

			// 4. Ventilation of H_SUP and ABS
			if (pointageType == null || pointageType == RefTypePointageEnum.ABSENCE || pointageType == RefTypePointageEnum.H_SUP) {
				for (Date dateLundi : getDistinctDateLundiFromListOfDates(pointagesToVentilateDates)) {
					pointagesVentiles.addAll(processHSupAndAbsVentilationForWeekAndAgent(
							toVentilDate, idAgent, carr, fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation(), dateLundi));
				}
			}
			
			// 5. Ventilation of PRIMES
			if (pointageType == null || pointageType == RefTypePointageEnum.PRIME) {
				for (Date dateDebutMois : getDistinctDateDebutMoisFromListOfDates(pointagesToVentilateDates)) {
					
					// 5.1 removePreviousCalculatedPointages()
					// 5.2 calculatePointages()
					// 5.3 Ventilation of PRIMES
					processPrimesVentilationForMonthAndAgent(
							toVentilDate, idAgent, fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation(), dateDebutMois);
				}
			}
			
			// 6. Mark pointages as etat VENTILE
			markPointagesAsVentile(pointagesVentiles, agent);
		}
		
	}

	protected TypeChainePaieEnum getTypeChainePaieFromStatut(AgentStatutEnum statut) {
		if (statut == AgentStatutEnum.CC)
			return TypeChainePaieEnum.CC;
		else
			return TypeChainePaieEnum.HCC;
	}

	protected List<Date> getDistinctDateLundiFromListOfDates(List<Date> dates) {
		
		List<Date> result = new ArrayList<Date>();
		
		for (Date d : dates) {
			Date monday = new LocalDate(d).withDayOfWeek(1).toDate();
			if (!result.contains(monday))
				result.add(monday);
		}
		
		return result;
	}
	
	protected List<Date> getDistinctDateDebutMoisFromListOfDates(List<Date> dates) {
		
		List<Date> result = new ArrayList<Date>();
		
		for (Date d : dates) {
			Date firstOfMonth = new LocalDate(d).withDayOfMonth(1).toDate();
			if (!result.contains(firstOfMonth))
				result.add(firstOfMonth);
		}
		
		return result;
	}
	
	protected List<Pointage> processHSupAndAbsVentilationForWeekAndAgent(VentilDate ventilDate, Integer idAgent, Spcarr carr, Date dateLundi, Date fromVentilDate, Date toVentilDate) {
		
		List<Pointage> agentsPointageForPeriod = ventilationRepository.getListPointagesAbsenceAndHSupForVentilation(idAgent, fromVentilDate, toVentilDate, dateLundi);

		boolean has1150Prime = mairieRepository.getPrimePointagesByAgent(idAgent, dateLundi).contains(1150);
		VentilHsup hSupsVentilees = ventilationHSupService.processHSup(idAgent, carr, agentsPointageForPeriod, carr.getStatutCarriere(), has1150Prime);
		VentilAbsence vAbs = ventilationAbsenceService.processAbsenceAgent(idAgent, agentsPointageForPeriod, dateLundi);
		
		// persisting all the generated entities linking them to the current ventil date
		if (hSupsVentilees != null) {
			hSupsVentilees.setVentilDate(ventilDate);
			hSupsVentilees.persist();
		}
		
		if (vAbs != null) {
			vAbs.setVentilDate(ventilDate);
			vAbs.persist();
		}
		
		return agentsPointageForPeriod;
	}
	
	protected List<Pointage> processPrimesVentilationForMonthAndAgent(VentilDate ventilDate, Integer idAgent, Date dateDebutMois, Date fromVentilDate, Date toVentilDate) {
		
		List<Pointage> agentsPointageForPeriod = ventilationRepository.getListPointagesPrimeForVentilation(idAgent, fromVentilDate, toVentilDate, dateDebutMois);

		// Ventilate pointages per type
		List<VentilPrime> primesVentilees = new ArrayList<VentilPrime>();
		
		primesVentilees.addAll(ventilationPrimeService.processPrimesAgent(idAgent, agentsPointageForPeriod, dateDebutMois));
		
		// persisting all the generated entities linking them to the current ventil date
		for (VentilPrime v : primesVentilees) {
			v.setVentilDate(ventilDate);
			v.persist();
		}
		
		return agentsPointageForPeriod;
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
	
		if (pointageType == null || pointageType == RefTypePointageEnum.H_SUP || pointageType == RefTypePointageEnum.ABSENCE) {
			ventilationRepository.removeVentilationsForDateAgentAndType(date, idAgent, RefTypePointageEnum.ABSENCE);
			ventilationRepository.removeVentilationsForDateAgentAndType(date, idAgent, RefTypePointageEnum.H_SUP);
		}
		if (pointageType == null || pointageType == RefTypePointageEnum.PRIME) {
			ventilationRepository.removeVentilationsForDateAgentAndType(date, idAgent, RefTypePointageEnum.PRIME);
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

	/**
	 * Updates each pointages to set them as VENTILE state after being used for ventilation
	 * @param pointages
	 * @param idAgent 
	 */
	protected void markPointagesAsVentile(List<Pointage> pointages, int idAgent) {
		
		Date currentDate = helperService.getCurrentDate();
		
		for (Pointage ptg : pointages) {
			if (ptg.getLatestEtatPointage().getEtat() == EtatPointageEnum.VENTILE)
				continue;

			EtatPointagePK pk = new EtatPointagePK();
			pk.setDateEtat(currentDate);
			pk.setPointage(ptg);
			EtatPointage ep = new EtatPointage();
			ep.setEtat(EtatPointageEnum.VENTILE);
			ep.setIdAgent(idAgent);
			ep.setEtatPointagePk(pk);
			ptg.getEtats().add(ep);
		}
	}

	/**
	 * Returns whether or not an agent is eligible to ventilation considering its
	 * status (F, C or CC) we are currently ventilating
	 * @param idAgent
	 * @param statut
	 * @return the Agent's Spcarr if the agent is eligible, null otherwise
	 */
	protected Spcarr isAgentEligibleToVentilation(Integer idAgent, AgentStatutEnum statut, Date date) {
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(Agent.getNoMatrFromIdAgent(idAgent), date);
		AgentStatutEnum agentStatus = carr != null ? carr.getStatutCarriere() : null;
		return agentStatus == statut ? carr : null;
	}
	

	
//	protected List<Pointage> processVentilationForAgent(VentilDate ventilDate, Integer idAgent, Spcarr carr, Date from, Date to, RefTypePointageEnum pointageType) {
//		
//		List<Pointage> agentsPointageForPeriod = pointageRepository.getListPointagesForVentilationByDateAndEtat(idAgent, from, to);
//
//		Map<Date, List<Pointage>> hSups = new HashMap<Date, List<Pointage>>();
//		Map<Date, List<Pointage>> primes = new HashMap<Date, List<Pointage>>();
//		Map<Date, List<Pointage>> abs = new HashMap<Date, List<Pointage>>();
//
//		// distribute pointage into 3 maps treating them as groups
//		distributePointages(agentsPointageForPeriod, hSups, primes, abs);
//
//		// Ventilate pointages per type
//		List<VentilHsup> hSupsVentilees = new ArrayList<VentilHsup>();
//		List<VentilAbsence> absVentilees = new ArrayList<VentilAbsence>();
//		List<VentilPrime> primesVentilees = new ArrayList<VentilPrime>();
//		
//		// If the choice was to ventilate ABSENCE or H_SUP (or all), ventilate both because they're tied together
//		if (pointageType == null || pointageType == RefTypePointageEnum.H_SUP || pointageType == RefTypePointageEnum.ABSENCE) {
//			
//			for (Entry<Date, List<Pointage>> set : hSups.entrySet()) {
//				boolean has1150Prime = mairieRepository.getPrimePointagesByAgent(idAgent, set.getKey()).contains(1150);
//				List<Pointage> pointagesToUse = getPointagesForWeek(idAgent, set.getKey(), from, to, set.getValue(), abs.get(set.getKey()));
//				hSupsVentilees.add(ventilationHSupService.processHSup(idAgent, carr, pointagesToUse, carr.getStatutCarriere(), has1150Prime));
//			}
//			
//			for (Entry<Date, List<Pointage>> set : abs.entrySet()) {
//				List<Pointage> pointagesToUse = getPointagesForWeek(idAgent, set.getKey(), from, to, set.getValue(), null);
//				absVentilees.add(ventilationAbsenceService.processAbsenceAgent(idAgent, pointagesToUse, set.getKey()));
//			}
//		}
//		
//		// If the choice was to ventilate only PRIME or all
//		if (pointageType == null || pointageType == RefTypePointageEnum.PRIME) {
//			for (Entry<Date, List<Pointage>> set : primes.entrySet()) {
//				List<Pointage> pointagesToUse = getPointagesForMonth(idAgent, set.getKey(), from, to, set.getValue());
//				primesVentilees.addAll(ventilationPrimeService.processPrimesAgent(idAgent, pointagesToUse, set.getKey()));
//			}
//		}
//		
//		// persisting all the generated entities linking them to the current ventil date
//		for (VentilHsup v : hSupsVentilees) {
//			v.setVentilDate(ventilDate);
//			v.persist();
//		}
//		
//		for (VentilPrime v : primesVentilees) {
//			v.setVentilDate(ventilDate);
//			v.persist();
//		}
//		
//		for (VentilAbsence v : absVentilees) {
//			v.setVentilDate(ventilDate);
//			v.persist();
//		}
//		
//		return agentsPointageForPeriod;
//	}
	

//	/**
//	 * This method distributes all pointages into 3 maps
//	 * hSups & abs : with a list of pointage per week
//	 * primes : with a list of pointage per month
//	 */
//	protected void distributePointages(List<Pointage> pointages, Map<Date, List<Pointage>> hSups, Map<Date, List<Pointage>> primes, Map<Date, List<Pointage>> abs) {
//		
//		for (Pointage ptg : pointages) {
//			
//			Date mapDate = ptg.getDateLundi();
//			Map<Date, List<Pointage>> eligibleMap = null;
//			
//			switch(ptg.getTypePointageEnum()) {
//				case H_SUP:
//					mapDate = ptg.getDateLundi();
//					eligibleMap = hSups;
//					break;
//				case PRIME:
//					mapDate = new LocalDate(ptg.getDateDebut()).withDayOfMonth(1).toDate();
//					eligibleMap = primes;
//					break;
//				case ABSENCE:
//					mapDate = ptg.getDateLundi();
//					eligibleMap = abs;
//					break;
//			}
//			
//			if (!eligibleMap.containsKey(mapDate))
//				eligibleMap.put(mapDate, new ArrayList<Pointage>());
//			eligibleMap.get(mapDate).add(ptg);
//			
//		}
//	}

}
