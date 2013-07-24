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
					agent, fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation());
			
			List<Pointage> pointagesVentiles = new ArrayList<Pointage>();

			// 4. Ventilation of H_SUP and ABS
			if (pointageType == null || pointageType == RefTypePointageEnum.ABSENCE || pointageType == RefTypePointageEnum.H_SUP) {
				for (Date dateLundi : getDistinctDateLundiFromListOfDates(pointagesToVentilateDates)) {
					pointagesVentiles.addAll(processHSupAndAbsVentilationForWeekAndAgent(
							toVentilDate, agent, carr, dateLundi, fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation()));
				}
			}
			
			// 5. Pointages generated (Primes)
			for (Date dateLundi : getDistinctDateLundiFromListOfDates(pointagesToVentilateDates)) {
				// 5.1 Remove previously calculated Pointages (Primes)
				removePreviousCalculatedPointages(agent, dateLundi);
				
				// 5.2 Calculate pointages for week
				calculatePointages(agent, dateLundi, fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation());
			}
			
			// 6. Ventilation of PRIMES
			if (pointageType == null || pointageType == RefTypePointageEnum.PRIME) {
				for (Date dateDebutMois : getDistinctDateDebutMoisFromListOfDates(pointagesToVentilateDates)) {
					
					processPrimesVentilationForMonthAndAgent(
							toVentilDate, agent, dateDebutMois, fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation());
				}
			}
			
			// 7. Mark pointages as etat VENTILE
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
		VentilHsup hSupsVentilees = ventilationHSupService.processHSup(idAgent, carr, dateLundi, agentsPointageForPeriod, carr.getStatutCarriere(), has1150Prime);
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
		
		List<Pointage> agentsPointageForPeriod = ventilationRepository.getListPointagesPrimeForVentilation(
				idAgent, fromVentilDate, toVentilDate, dateDebutMois);
		List<PointageCalcule> agentsPointagesCalculesForPeriod = ventilationRepository.getListPointagesCalculesPrimeForVentilation(
				idAgent, dateDebutMois);

		// Ventilate pointages per type
		List<VentilPrime> primesVentilees = new ArrayList<VentilPrime>();
		
		primesVentilees.addAll(ventilationPrimeService.processPrimesAgent(idAgent, agentsPointageForPeriod, dateDebutMois));
		primesVentilees.addAll(ventilationPrimeService.processPrimesCalculeesAgent(idAgent, agentsPointagesCalculesForPeriod, dateDebutMois));
		
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
	protected void removePreviousCalculatedPointages(Integer idAgent, Date dateLundi) {
		pointageRepository.removePointageCalculesForDateAgent(idAgent, dateLundi);
	}
	
	/**
	 * Generates calculated pointages for all weeks in between two dates
	 * @param idAgent
	 * @param fromEtatDate
	 * @param toEtatDate
	 * @param dateLundi
	 */
	protected void calculatePointages(Integer idAgent, Date dateLundi, Date fromEtatDate, Date toEtatDate) {

		List<PointageCalcule> result = new ArrayList<PointageCalcule>();
		
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(Agent.getNoMatrFromIdAgent(idAgent), dateLundi);
		List<Pointage> ptgs = ventilationRepository.getListPointagesForPrimesCalculees(idAgent, fromEtatDate, toEtatDate, dateLundi);
		result.addAll(pointageCalculeService.calculatePointagesForAgentAndWeek(idAgent, carr.getStatutCarriere(), dateLundi, ptgs));

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
}
