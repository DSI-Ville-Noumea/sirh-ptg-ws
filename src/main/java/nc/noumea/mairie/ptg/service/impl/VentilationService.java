package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.domain.VentilTask;
import nc.noumea.mairie.ptg.dto.CanStartVentilationDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.VentilAbsenceDto;
import nc.noumea.mairie.ptg.dto.VentilDateDto;
import nc.noumea.mairie.ptg.dto.VentilDto;
import nc.noumea.mairie.ptg.dto.VentilHSupDto;
import nc.noumea.mairie.ptg.dto.VentilPrimeDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IPointageCalculeService;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.service.IVentilationAbsenceService;
import nc.noumea.mairie.ptg.service.IVentilationHSupService;
import nc.noumea.mairie.ptg.service.IVentilationPrimeService;
import nc.noumea.mairie.ptg.service.IVentilationService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentilationService implements IVentilationService {

	private Logger logger = LoggerFactory.getLogger(VentilationService.class);

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private ISirhRepository sirhRepository;

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
	private IPointageService pointageService;

	@Autowired
	private HelperService helperService;

	@Override
	public ReturnMessageDto startVentilation(Integer idAgent, List<Integer> agents, Date ventilationDate,
			AgentStatutEnum statut, RefTypePointageEnum pointageType) {

		logger.info("Starting ventilation of Pointages for Agents [{}], date [{}], status [{}] and pointage type [{}]",
				agents, ventilationDate, statut, pointageType);

		ReturnMessageDto result = new ReturnMessageDto();
		TypeChainePaieEnum typeChainePaie = helperService.getTypeChainePaieFromStatut(statut);

		// Check whether a ventilation can be started (is there one currently
		// running for this chainePaie)
		if (!ventilationRepository.canStartVentilation(typeChainePaie)) {
			String msg = String
					.format("Ventiation for statut [%s] may not be started. An existing one is currently processing...",
							statut);
			logger.error(msg);
			result.getErrors().add(msg);
			return result;
		}

		// Check that the ventilation date must be a sunday. Otherwise stop here
		DateTime givenVentilationDate = new DateTime(ventilationDate);
		if (givenVentilationDate.dayOfWeek().get() != DateTimeConstants.SUNDAY) {
			String msg = String
					.format("La date de ventilation choisie est un [%s]. Impossible de ventiler les pointages à une date autre qu'un dimanche.",
							givenVentilationDate.dayOfWeek().getAsText());
			logger.error(msg);
			result.getErrors().add(msg);
			return result;
		}

		// Retrieving the current ventilation dates (from / to)
		VentilDate fromVentilDate = ventilationRepository.getLatestVentilDate(typeChainePaie, true);
		VentilDate toVentilDate = ventilationRepository.getLatestVentilDate(typeChainePaie, false);

		if (toVentilDate == null) {
			logger.info("No unpaid ventilation date found for statut. Creating a new one...");
			toVentilDate = new VentilDate();
			toVentilDate.setDateVentilation(givenVentilationDate.withHourOfDay(23).withMinuteOfHour(59).toDate());
			toVentilDate.setPaye(false);
			toVentilDate.setTypeChainePaie(typeChainePaie);
			pointageRepository.persisEntity(toVentilDate);
			logger.info("Created ventilation date as of [{}]", toVentilDate.getDateVentilation());
		}

		// If no agents were set as parameters, we need to take everyone
		// concerned
		if (agents.size() == 0) {
			agents = ventilationRepository.getListIdAgentsForVentilationByDateAndEtat(
					fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation());
		}

		logger.info("Found {} agents to ventilate pointages for (based on available pointages) and before filtering.",
				agents.size());

		// For all seleted agents, proceed to ventilation
		for (Integer agent : agents) {
			// 1. Verify whether this agent is eligible, through its
			// AgentStatutEnum (Spcarr)
			Spcarr carr = isAgentEligibleToVentilation(agent, statut, toVentilDate.getDateVentilation());
			if (carr == null) {
				logger.info("Agent {} not eligible for ventilation (status not matching), skipping to next.", agent);
				continue;
			}

			VentilTask task = new VentilTask();
			task.setIdAgent(agent);
			task.setIdAgentCreation(idAgent);
			task.setTypeChainePaie(typeChainePaie);
			if (pointageType != null)
				task.setRefTypePointage(pointageRepository.getEntity(RefTypePointage.class, pointageType.getValue()));
			task.setVentilDateFrom(fromVentilDate);
			task.setVentilDateTo(toVentilDate);
			task.setDateCreation(helperService.getCurrentDate());
			pointageRepository.persisEntity(task);

			result.getInfos().add(String.format("Agent %s", agent));
		}

		logger.info("Added ventilation tasks for {} agents after filtering.", result.getInfos().size());

		return result;
	}

	@Override
	public void processVentilationForAgent(Integer idVentilTask) {

		logger.info("Starting ventilation of idVentilTask [{}]", idVentilTask);
		VentilTask task = pointageRepository.getEntity(VentilTask.class, idVentilTask);
		logger.info("Ventilation of Agent [{}] created by agent [{}] at [{}].", task.getIdAgent(),
				task.getIdAgentCreation(), task.getDateCreation());

		RefTypePointageEnum pointageType = task.getRefTypePointage() == null ? null : RefTypePointageEnum
				.getRefTypePointageEnum(task.getRefTypePointage().getIdRefTypePointage());
		Integer agent = task.getIdAgent();
		VentilDate fromVentilDate = task.getVentilDateFrom();
		VentilDate toVentilDate = task.getVentilDateTo();

		// 1. Retrieve agent current Spcarr
		Spcarr carr = sirhRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(agent),
				toVentilDate.getDateVentilation());

		// 2. remove existing ventilations
		removePreviousVentilations(toVentilDate, agent, pointageType);

		// 3. select all distinct dates of pointages needing ventilation
		List<Date> pointagesToVentilateDates = ventilationRepository.getDistinctDatesOfPointages(agent,
				fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation());

		List<Pointage> pointagesVentiles = new ArrayList<Pointage>();

		// 4. Ventilation of H_SUP and ABS
		if (pointageType == null || pointageType == RefTypePointageEnum.ABSENCE
				|| pointageType == RefTypePointageEnum.H_SUP) {
			for (Date dateLundi : getDistinctDateLundiFromListOfDates(pointagesToVentilateDates)) {
				pointagesVentiles.addAll(processHSupAndAbsVentilationForWeekAndAgent(toVentilDate, agent, carr,
						dateLundi, fromVentilDate.getDateVentilation()));
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

				pointagesVentiles.addAll(processPrimesVentilationForMonthAndAgent(toVentilDate, agent, dateDebutMois,
						fromVentilDate.getDateVentilation()));
			}
		}

		// 7. Mark pointages as etat VENTILE and add this VentilDate to their
		// list of ventilations
		markPointagesAsVentile(pointagesVentiles, agent, toVentilDate);

		logger.info("Ventilation of idVentilTask [{}] done.", idVentilTask);
	}

	protected List<Date> getDistinctDateLundiFromListOfDates(List<Date> dates) {

		List<Date> result = new ArrayList<Date>();

		for (Date d : dates) {
			Date monday = new LocalDate(d).withDayOfWeek(1).toDate();
			if (!result.contains(monday)) {
				result.add(monday);
			}
		}

		return result;
	}

	protected List<Date> getDistinctDateDebutMoisFromListOfDates(List<Date> dates) {

		List<Date> result = new ArrayList<Date>();

		for (Date d : dates) {
			Date firstOfMonth = new LocalDate(d).withDayOfMonth(1).toDate();
			if (!result.contains(firstOfMonth)) {
				result.add(firstOfMonth);
			}
		}

		return result;
	}

	protected List<Pointage> processHSupAndAbsVentilationForWeekAndAgent(VentilDate ventilDate, Integer idAgent,
			Spcarr carr, Date dateLundi, Date fromVentilDate) {

		logger.debug("Ventilation of HSUPs and ABS pointages for date monday [{}]...", dateLundi);

		// Retrieve all pointages for that period
		List<Pointage> agentsPointageForPeriod = ventilationRepository.getListPointagesAbsenceAndHSupForVentilation(
				idAgent, fromVentilDate, ventilDate.getDateVentilation(), dateLundi);

		// Then filter them (if they have parent pointages to be excluded for
		// ex.)
		List<Pointage> filteredAgentsPointageForPeriod = pointageService.filterOldPointagesAndEtatFromList(
				agentsPointageForPeriod, null);

		boolean has1150Prime = sirhRepository.getPrimePointagesByAgent(idAgent, dateLundi).contains(1150);
		VentilHsup hSupsVentilees = ventilationHSupService.processHSup(idAgent, carr, dateLundi,
				filteredAgentsPointageForPeriod, carr.getStatutCarriere(), has1150Prime);
		VentilAbsence vAbs = ventilationAbsenceService.processAbsenceAgent(idAgent, filteredAgentsPointageForPeriod,
				dateLundi);

		// persisting all the generated entities linking them to the current
		// ventil date
		if (hSupsVentilees != null) {
			hSupsVentilees.setVentilDate(ventilDate);
			ventilationRepository.persistEntity(hSupsVentilees);
		}

		if (vAbs != null) {
			vAbs.setVentilDate(ventilDate);
			ventilationRepository.persistEntity(vAbs);
		}

		return filteredAgentsPointageForPeriod;
	}

	protected List<Pointage> processPrimesVentilationForMonthAndAgent(VentilDate ventilDate, Integer idAgent,
			Date dateDebutMois, Date fromVentilDate) {

		logger.debug("Ventilation of PRIME pointages for date 1st of month [{}]...", dateDebutMois);

		List<Pointage> agentsPointageForPeriod = ventilationRepository.getListPointagesPrimeForVentilation(idAgent,
				fromVentilDate, ventilDate.getDateVentilation(), dateDebutMois);

		List<Pointage> filteredAgentsPointageForPeriod = pointageService.filterOldPointagesAndEtatFromList(
				agentsPointageForPeriod, null);

		List<PointageCalcule> agentsPointagesCalculesForPeriod = ventilationRepository
				.getListPointagesCalculesPrimeForVentilation(idAgent, dateDebutMois);

		// Ventilate pointages per type
		List<VentilPrime> primesVentilees = new ArrayList<VentilPrime>();

		primesVentilees.addAll(ventilationPrimeService.processPrimesAgent(idAgent, filteredAgentsPointageForPeriod,
				dateDebutMois));
		primesVentilees.addAll(ventilationPrimeService.processPrimesCalculeesAgent(idAgent,
				agentsPointagesCalculesForPeriod, dateDebutMois));

		// persisting all the generated entities linking them to the current
		// ventil date
		for (VentilPrime v : primesVentilees) {
			v.setVentilDate(ventilDate);
			ventilationRepository.persistEntity(v);
		}

		// Because Pointages Calcules are not modifiable by anyone, we directly
		// mark them
		// as ventilated and set their VentilDate to the current one
		for (PointageCalcule ptgC : agentsPointagesCalculesForPeriod) {
			ptgC.setEtat(EtatPointageEnum.VENTILE);
			ptgC.setLastVentilDate(ventilDate);
		}

		return filteredAgentsPointageForPeriod;
	}

	/**
	 * This method removes all existing ventilations for a given agent, date,
	 * and typePointage. Type is optional: if it is not given, all types will be
	 * deleted.
	 * 
	 * @param date
	 * @param idAgent
	 * @param pointageType
	 */
	protected void removePreviousVentilations(VentilDate date, Integer idAgent, RefTypePointageEnum pointageType) {

		logger.debug("Removing previous ventilation records...");

		if (pointageType == null || pointageType == RefTypePointageEnum.H_SUP
				|| pointageType == RefTypePointageEnum.ABSENCE) {
			ventilationRepository.removeVentilationsForDateAgentAndType(date, idAgent, RefTypePointageEnum.ABSENCE);
			ventilationRepository.removeVentilationsForDateAgentAndType(date, idAgent, RefTypePointageEnum.H_SUP);
		}
		if (pointageType == null || pointageType == RefTypePointageEnum.PRIME) {
			ventilationRepository.removeVentilationsForDateAgentAndType(date, idAgent, RefTypePointageEnum.PRIME);
		}
	}

	/**
	 * Removes existing pointages calcules for agent and between two dates
	 * 
	 * @param idAgent
	 * @param from
	 * @param to
	 */
	protected void removePreviousCalculatedPointages(Integer idAgent, Date dateLundi) {
		logger.debug("Removing previously calculated PRIME pointages for date monday [{}]...", dateLundi);
		pointageRepository.removePointageCalculesForDateAgent(idAgent, dateLundi);
	}

	/**
	 * Generates calculated pointages for all weeks in between two dates
	 * 
	 * @param idAgent
	 * @param fromEtatDate
	 * @param toEtatDate
	 * @param dateLundi
	 */
	protected void calculatePointages(Integer idAgent, Date dateLundi, Date fromEtatDate, Date toEtatDate) {

		logger.debug("Creation of calculated PRIME pointages for date monday [{}]...", dateLundi);

		List<PointageCalcule> result = new ArrayList<PointageCalcule>();

		Spcarr carr = sirhRepository
				.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent), dateLundi);
		List<Pointage> ptgs = ventilationRepository.getListPointagesForPrimesCalculees(idAgent, fromEtatDate,
				toEtatDate, dateLundi);
		result.addAll(pointageCalculeService.calculatePointagesForAgentAndWeek(idAgent, carr.getStatutCarriere(),
				dateLundi, ptgs));

		for (PointageCalcule ptgC : result) {
			ventilationRepository.persistEntity(ptgC);
		}
	}

	/**
	 * Updates each pointage to set them as VENTILE state after being used for
	 * ventilation Also link them to the VentilDate that has just ventilated
	 * them (for further searches)
	 * 
	 * @param pointages
	 * @param idAgent
	 * @param ventilDate
	 */
	protected void markPointagesAsVentile(List<Pointage> pointages, int idAgent, VentilDate ventilDate) {

		logger.debug("Marking pointages as Etat = Ventile...");

		Date currentDate = helperService.getCurrentDate();

		for (Pointage ptg : pointages) {

			if (!ptg.getVentilations().contains(ventilDate)) {
				ptg.getVentilations().add(ventilDate);
			}

			EtatPointageEnum currentEtat = ptg.getLatestEtatPointage().getEtat();

			if (currentEtat == EtatPointageEnum.VENTILE || currentEtat == EtatPointageEnum.VALIDE
					|| currentEtat == EtatPointageEnum.JOURNALISE) {
				continue;
			}

			EtatPointage ep = new EtatPointage();
			ep.setDateEtat(currentDate);
			ep.setDateMaj(currentDate);
			ep.setPointage(ptg);
			ep.setEtat(EtatPointageEnum.VENTILE);
			ep.setIdAgent(idAgent);
			ptg.getEtats().add(ep);
		}
	}

	/**
	 * Returns whether or not an agent is eligible to ventilation considering
	 * its status (F, C or CC) we are currently ventilating
	 * 
	 * @param idAgent
	 * @param statut
	 * @return the Agent's Spcarr if the agent is eligible, null otherwise
	 */
	protected Spcarr isAgentEligibleToVentilation(Integer idAgent, AgentStatutEnum statut, Date date) {
		Spcarr carr = sirhRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent), date);
		AgentStatutEnum agentStatus = carr != null ? carr.getStatutCarriere() : null;
		return agentStatus == statut ? carr : null;
	}

	@Override
	public CanStartVentilationDto canStartVentilationForAgentStatus(AgentStatutEnum statut) {

		CanStartVentilationDto result = new CanStartVentilationDto();
		TypeChainePaieEnum chainePaie = helperService.getTypeChainePaieFromStatut(statut);
		result.setCanStartVentilation(ventilationRepository.canStartVentilation(chainePaie));

		if (result.isCanStartVentilation())
			logger.debug("Ventiation for statut [{}] may be started. None currently processing...", statut);
		else
			logger.debug("Ventiation for statut [{}] may not be started. An existing one is currently processing...",
					statut);

		return result;
	}

	/**
	 * Return The list of ventilations done for the agents at ventildate
	 * 
	 * @param ventilationDate
	 * @param agents
	 * @param pointageType
	 * @return The list of VentilDto
	 */
	@Override
	public List<VentilDto> showVentilation(Integer idDateVentil, List<Integer> agents, RefTypePointageEnum pointageType) {
		logger.debug("Showing ventilation of Pointages for Agents [{}], idDateVentil [{}] and pointage type [{}]",
				agents, idDateVentil, pointageType);
		List<VentilDto> pointagesVentiles = new ArrayList<>();
		// For all selected agents, get ventilated pointages

		switch (pointageType) {
			case ABSENCE: {
				for (VentilAbsence abs : ventilationRepository.getListOfVentilAbsenceForDateAgent(idDateVentil, agents))
					pointagesVentiles.add(new VentilAbsenceDto(abs));

				break;
			}
			case H_SUP: {
				for (VentilHsup hs : ventilationRepository.getListOfVentilHSForDateAgent(idDateVentil, agents))
					pointagesVentiles.add(new VentilHSupDto(hs));

				break;
			}
			case PRIME: {
				for (VentilPrime prime : ventilationRepository.getListOfVentilPrimeForDateAgent(idDateVentil, agents))
					pointagesVentiles.add(new VentilPrimeDto(prime, helperService));

				break;
			}
		}
		logger.debug("Returning {} ventilated pointages from showVentilation WS.", pointagesVentiles.size());
		return pointagesVentiles;
	}

	@Override
	public VentilDateDto getVentilationEnCoursForStatut(AgentStatutEnum statut) {
		TypeChainePaieEnum typeChainePaie = helperService.getTypeChainePaieFromStatut(statut);
		VentilDate ventilEnCoursDate = ventilationRepository.getLatestVentilDate(typeChainePaie, false);

		VentilDateDto result = new VentilDateDto(ventilEnCoursDate);

		if (result.isPaie())
			logger.debug("La ventilation pour le statut [{}] n'est pas en cours.", statut);
		else
			logger.debug("La ventilation pour le statut [{}] est en cours.", statut);

		return result;
	}
	
	@Override
	public VentilTask findVentilTask(Integer idVentilTask) {
		return pointageRepository.getEntity(VentilTask.class, idVentilTask);
	}
}
