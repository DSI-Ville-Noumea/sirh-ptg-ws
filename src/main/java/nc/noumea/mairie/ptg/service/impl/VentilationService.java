package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import nc.noumea.mairie.ptg.dto.VentilErreurDto;
import nc.noumea.mairie.ptg.dto.VentilHSupDto;
import nc.noumea.mairie.ptg.dto.VentilPrimeDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IPointageCalculeService;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.service.IVentilationAbsenceService;
import nc.noumea.mairie.ptg.service.IVentilationHSupService;
import nc.noumea.mairie.ptg.service.IVentilationPrimeService;
import nc.noumea.mairie.ptg.service.IVentilationService;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.ws.ISirhWSConsumer;

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
	private IMairieRepository mairieRepository;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

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

	@Autowired
	private IPaieWorkflowService paieWorkflowService;

	@Override
	public ReturnMessageDto startVentilation(Integer idAgent, List<Integer> agents, Date ventilationDate,
			AgentStatutEnum statut, RefTypePointageEnum pointageType) {

		logger.info("Starting ventilation of Pointages for Agents [{}], date [{}], status [{}] and pointage type [{}]",
				agents, ventilationDate, statut, pointageType);

		ReturnMessageDto result = new ReturnMessageDto();
		TypeChainePaieEnum typeChainePaie = helperService.getTypeChainePaieFromStatut(statut);

		// Check whether a ventilation can be started (is there one currently
		// running for this chainePaie)
		if (!paieWorkflowService.canStartVentilation(typeChainePaie)
				|| !ventilationRepository.canStartVentilation(typeChainePaie)) {
			String msg = String
					.format("Ventilation for statut [%s] may not be started. An existing one is currently processing...",
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
		
		// #19381 bloquer la date de ventilation saisie dans le passé
		if (givenVentilationDate.isAfterNow()) {
			String msg = String
					.format("La date de ventilation choisie doit être antérieure à aujourd'hui.",
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

			// we are looking for pointages still validated, and then rejeted
			// to delete in AS400
			List<Integer> listAgentWithPointageRejete = ventilationRepository
					.getListIdAgentsWithPointagesValidatedAndRejetes(toVentilDate.getIdVentilDate());
			if (null != listAgentWithPointageRejete) {
				for (Integer idAgentWithPointageRejete : listAgentWithPointageRejete) {
					if (!agents.contains(idAgentWithPointageRejete)) {
						agents.add(idAgentWithPointageRejete);
					}
				}
			}

			// evolution #18234 generation des pointages TID pour les primes pointages TID affectées à l'affectation de l agent 
			if (null == pointageType
					|| pointageType.equals(RefTypePointageEnum.PRIME)){
				
				List<Integer> listIdAgentWithTIDAGenerer = sirhWsConsumer.getListAgentsWithPrimeTIDOnAffectation(fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation());
				if (null != listIdAgentWithTIDAGenerer) {
					for (Integer idAgentWithTIDAGenerer : listIdAgentWithTIDAGenerer) {
						if (!agents.contains(idAgentWithTIDAGenerer)) {
							agents.add(idAgentWithTIDAGenerer);
						}
					}
				}
			}
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
		Integer idAgent = task.getIdAgent();
		Integer idAgentRH = task.getIdAgentCreation();
		VentilDate fromVentilDate = task.getVentilDateFrom();
		VentilDate toVentilDate = task.getVentilDateTo();

		// 1. Retrieve agent current Spcarr
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent),
				toVentilDate.getDateVentilation());
		
		if(null == carr) {
			throw new NoCarriereException();
		}

		// 2. remove existing ventilations
		removePreviousVentilations(toVentilDate, idAgent, pointageType);
		

		// 3. select all distinct dates of pointages needing ventilation
		List<Date> pointagesToVentilateDates = ventilationRepository.getDistinctDatesOfPointages(idAgent,
				fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation());

		List<Pointage> pointagesVentiles = new ArrayList<Pointage>();

		// 4. Pointages generated (Primes)
		for (Date dateLundi : getDistinctDateLundiFromListOfDates(pointagesToVentilateDates)) {
			// evolution faite suite #31372
			// 4.2 Calculate pointages for week
			calculatePointages(idAgent, dateLundi, fromVentilDate.getDateVentilation(), toVentilDate);
		}

		// 5. Ventilation of H_SUP and ABS
		if (pointageType == null || pointageType == RefTypePointageEnum.ABSENCE
				|| pointageType == RefTypePointageEnum.H_SUP) {
			for (Date dateLundi : getDistinctDateLundiFromListOfDates(pointagesToVentilateDates)) {
				pointagesVentiles.addAll(processHSupAndAbsVentilationForWeekAndAgent(toVentilDate, idAgent, carr,
						dateLundi, fromVentilDate.getDateVentilation()));
			}
		}
		
		// 6. 
		// evolution #18234 generation des pointages TID pour les primes pointages TID affectées à l'affectation de l agent
		List<Date> listDatesPointagesTID = new ArrayList<Date>();
		for(Date dateLundi : helperService.getListDateLundiBetWeenTwoDate(fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation())) {
			
			// Retrieve all pointages for that period
			List<Pointage> agentsPointageForPeriod = ventilationRepository.getListPointagesAbsenceAndHSupForVentilation(
					idAgent, fromVentilDate.getDateVentilation(), toVentilDate.getDateVentilation(), dateLundi);

			// Then filter them (if they have parent pointages to be excluded for
			// ex.)
			List<Pointage> filteredAgentsPointageForPeriod = pointageService.filterOldPointagesAndEtatFromList(
					agentsPointageForPeriod, Arrays.asList(EtatPointageEnum.APPROUVE, EtatPointageEnum.VENTILE), null);
			
			List<Pointage> listPointagesTID = pointageCalculeService.generatePointageTID_7720_7721_7722(idAgentRH, idAgent, carr.getStatutCarriere(), dateLundi, filteredAgentsPointageForPeriod);
			// bug #29292 prendre en compte les TIDs
			if(null != listPointagesTID) {
				for(Pointage pointageTID : listPointagesTID) {
					listDatesPointagesTID.add(pointageTID.getDateDebut());
				}
			}
		}

		// 7. Ventilation of PRIMES
		if (pointageType == null || pointageType == RefTypePointageEnum.PRIME) {
			pointagesToVentilateDates.addAll(listDatesPointagesTID);
			for (Date dateDebutMois : getDistinctDateDebutMoisFromListOfDates(pointagesToVentilateDates)) {

				pointagesVentiles.addAll(processPrimesVentilationForMonthAndAgent(toVentilDate, idAgent, dateDebutMois,
						fromVentilDate.getDateVentilation(), carr.getStatutCarriere()));
			}
		}

		// 8. Mark pointages as etat VENTILE and add this VentilDate to their
		// list of ventilations
		markPointagesAsVentile(pointagesVentiles, idAgentRH, toVentilDate);

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

		////////////////////////////////////////////////
		// 1. recupere les pointages 
		////////////////////////////////////////////////
		
		// Retrieve all pointages for that period
		List<Pointage> agentsPointageForPeriod = ventilationRepository.getListPointagesAbsenceAndHSupForVentilation(
				idAgent, fromVentilDate, ventilDate.getDateVentilation(), dateLundi);

		// Then filter them (if they have parent pointages to be excluded for
		// ex.)
		List<Pointage> filteredAgentsPointageForPeriod = pointageService.filterOldPointagesAndEtatFromList(
				agentsPointageForPeriod, Arrays.asList(EtatPointageEnum.APPROUVE, EtatPointageEnum.VENTILE), null);

		// #16789 on recupere les pointages ventiles puis rejetes pour mettre a jour la ventilation
		List<Pointage> listPointageRejetesVentilesOrderedByDateAsc = pointageService
				.getPointagesVentilesAndRejetesForAgentByDateLundi(idAgent, ventilDate, dateLundi);
		
		// #19323 recuperer les pointages JOURNALISES d une ventilation precedente, 
		// puis REJETES dans cette ventilation
		// ils ne sont pas récupérés par la requête précedente// s il n y a donc pas d autres pointage sur cette semaine
		// ils sont zappés
		List<Pointage> filteredListPointageRejetesOrderedByDateAsc = null;
		if((null == filteredAgentsPointageForPeriod || filteredAgentsPointageForPeriod.isEmpty())) {

			List<Pointage> listPointageRejetesOrderedByDateAsc = ventilationRepository
					.getListPointagesAbsenceAndHSupRejetesBetweenDatesVentilation(idAgent, fromVentilDate, ventilDate.getDateVentilation(), dateLundi);
			filteredListPointageRejetesOrderedByDateAsc = pointageService.filterOldPointagesAndEtatFromList(
					listPointageRejetesOrderedByDateAsc, Arrays.asList(EtatPointageEnum.REJETE), null);
		}
		
		// bug #20405 si des Heures Sups sont a ventiler, 
		// il faut les calculer avec les autres pointages meme journalises
		// pour avoir un calcul coherent
		List<Pointage> filteredAgentsPointageJournalisesForPeriod = pointageService.filterOldPointagesAndEtatFromList(
				agentsPointageForPeriod, Arrays.asList(EtatPointageEnum.JOURNALISE), null);
		// attention a ne pas retourner cette liste pour ne pas repasser les pointages
		// journalises a ventiles
		List<Pointage> listAllAgentsPointageForPeriod = new ArrayList<Pointage>();
		if(null != filteredAgentsPointageForPeriod 
				&& !filteredAgentsPointageForPeriod.isEmpty())
			listAllAgentsPointageForPeriod.addAll(filteredAgentsPointageForPeriod);
		
		if(null != filteredAgentsPointageJournalisesForPeriod 
				&& !filteredAgentsPointageJournalisesForPeriod.isEmpty())
			listAllAgentsPointageForPeriod.addAll(filteredAgentsPointageJournalisesForPeriod);
		
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();
		
		////////////////////////////////////////////////
		// 2. calcul les HSup
		////////////////////////////////////////////////
		boolean has1150Prime = sirhWsConsumer.getPrimePointagesByAgent(idAgent, dateLundi, dateFinSemaine).contains(1150);
		VentilHsup hSupsVentilees = ventilationHSupService.processHSup(idAgent, carr, dateLundi,
				listAllAgentsPointageForPeriod, carr.getStatutCarriere(), has1150Prime, ventilDate, filteredListPointageRejetesOrderedByDateAsc);
		
		////////////////////////////////////////////////
		// 3. calcul les absences
		////////////////////////////////////////////////
		VentilAbsence vAbs = ventilationAbsenceService.processAbsenceAgent(idAgent, listAllAgentsPointageForPeriod,
				dateLundi, listPointageRejetesVentilesOrderedByDateAsc, filteredListPointageRejetesOrderedByDateAsc);

		// ce code ne sert plus voir #13816
		// on ne supprime pas pour autant le code, car ils sont de nouveau en renegociation avec les syndicats
		
		// on gere les primes d epandage qui comptabiliseront des heures supp.
//		List<Pointage> agentsPointagePrimeForPeriod = ventilationRepository.getListPointagesPrimeByWeekForVentilation(idAgent,
//				fromVentilDate, ventilDate.getDateVentilation(), dateLundi);
//		
//		List<Pointage> filteredAgentsPointagePrimeForPeriod = pointageService.filterOldPointagesAndEtatFromList(
//				agentsPointagePrimeForPeriod, null, null);
//		hSupsVentilees = ventilationHSupService.processHeuresSupEpandageForSIPRES(
//				hSupsVentilees, idAgent, dateLundi, filteredAgentsPointagePrimeForPeriod, carr.getStatutCarriere());
		
		////////////////////////////////////////////////
		// 4. calcul les HSup a partir des pointages calcules
		////////////////////////////////////////////////
		// #19718 calcul des heures sup sur les primes calculees RENFORT DE GARDE
		List<PointageCalcule> agentsPointagesCalculesHSupForWeek = ventilationRepository
				.getListPointagesCalculesHSupForVentilation(idAgent, dateLundi);
		
		hSupsVentilees = ventilationHSupService.processHSupFromPointageCalcule(idAgent, dateLundi, agentsPointagesCalculesHSupForWeek, hSupsVentilees);
		
		////////////////////////////////////////////////
		// 5. on persist
		////////////////////////////////////////////////
		// persisting all the generated entities linking them to the current ventil date
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
			Date dateDebutMois, Date fromVentilDate, AgentStatutEnum statut) {

		logger.debug("Ventilation of PRIME pointages for date 1st of month [{}]...", dateDebutMois);

		List<Pointage> agentsPointageForPeriod = ventilationRepository.getListPointagesPrimeForVentilation(idAgent,
				fromVentilDate, ventilDate.getDateVentilation(), dateDebutMois);

		List<Pointage> filteredAgentsPointageForPeriod = pointageService.filterOldPointagesAndEtatFromList(
				agentsPointageForPeriod, null, null);

		List<PointageCalcule> agentsPointagesCalculesForPeriod = ventilationRepository
				.getListPointagesCalculesPrimeForVentilation(idAgent, dateDebutMois);

		// Ventilate pointages per type
		List<VentilPrime> primesVentilees = new ArrayList<VentilPrime>();

		primesVentilees.addAll(ventilationPrimeService.processPrimesAgent(idAgent, filteredAgentsPointageForPeriod,
				dateDebutMois, statut));
		primesVentilees.addAll(ventilationPrimeService.processPrimesCalculeesAgent(idAgent,
				agentsPointagesCalculesForPeriod, dateDebutMois));

		// if no VentilPrime for this month, we are looking for a old validated
		// VentilPrime for this same month
		// so if we find it, we create a VentilPrime with quantite = 0 to delete
		// in SPPRIM (AS400)
		List<VentilPrime> listOldVentilPrime = ventilationRepository.getListOfOldVentilPrimeForAgentAndDateDebutMois(
				idAgent, dateDebutMois, ventilDate.getIdVentilDate());

		if (null != listOldVentilPrime && !listOldVentilPrime.isEmpty()) {
			List<Integer> refPrime = new ArrayList<Integer>();
			for (VentilPrime ventilPrime : primesVentilees) {
				refPrime.add(ventilPrime.getIdRefPrime());
			}

			for (VentilPrime primeOld : listOldVentilPrime) {
				if (!refPrime.contains(primeOld.getIdRefPrime())) {
					VentilPrime ventilPrime = new VentilPrime();
					ventilPrime.setIdAgent(idAgent);
					ventilPrime.setDateDebutMois(dateDebutMois);
					ventilPrime.setEtat(EtatPointageEnum.VENTILE);
					ventilPrime.setQuantite(0.0);
					ventilPrime.setRefPrime(primeOld.getRefPrime());
					ventilPrime.setDatePrime(dateDebutMois);
					primesVentilees.add(ventilPrime);
					refPrime.add(primeOld.getIdRefPrime());
				}
			}
		}

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
	 * @param dateLundi
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
	protected void calculatePointages(Integer idAgent, Date dateLundi, Date fromEtatDate, VentilDate ventilDate) {

		logger.debug("Creation of calculated PRIME pointages for date monday [{}]...", dateLundi);

		// on cherche tous les pointages APPROUVE et REJETE avec date de saisie entre les 2 dates de ventilation
		// ainsi que tous les VALIDE, VENTILE, JOURNALISE
		List<PointageCalcule> result = new ArrayList<PointageCalcule>();
		List<Pointage> listePointages = ventilationRepository.getListPointagesForPrimesCalculees(idAgent, fromEtatDate,
				ventilDate.getDateVentilation(), dateLundi);
		
		// bug #20993
		// pour le calcul des pointages calcules
		// nous avons besoin de recuperer tous les pointages APPROUVE, VENTILE, VALIDE, JOURNALISE
		// car le calcul des primes ou heures sup prend tous les pointages de la semaine en compte
		// neanmoins, s il n y a que des pointages JOURNALISE ou VALIDE, ca ne sert à rien de les 
		// ressortir une nouvelle fois dans la ventilation 
		if(isAllPointagesAreValideOrJournalise(listePointages))
			return;
		
		// evolution faite suite #31372
		// SINON
		// 4.1 Remove previously calculated Pointages (Primes)
		removePreviousCalculatedPointages(idAgent, dateLundi);
		
		// puis on filtre la liste de pointage : on retire les REJETE
		// on garde que les APPROUVE, VALIDE, VENTILE et JOURNALISE
		List<Pointage> filteredListePointages = pointageService.filterOldPointagesAndEtatFromList(
				listePointages, 
				Arrays.asList(EtatPointageEnum.APPROUVE, EtatPointageEnum.VENTILE,
						EtatPointageEnum.VALIDE, EtatPointageEnum.JOURNALISE), 
				null);

		Spcarr carr = mairieRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent),
				dateLundi);
		
		result.addAll(pointageCalculeService.calculatePointagesForAgentAndWeek(idAgent, carr.getStatutCarriere(),
				dateLundi, filteredListePointages));

		for (PointageCalcule ptgC : result) {
			// bug #21060
			ptgC.setLastVentilDate(ventilDate);
			ventilationRepository.persistEntity(ptgC);
		}
	}
	
	private boolean isAllPointagesAreValideOrJournalise(List<Pointage> listPointages) {
		
		if(null != listPointages
				&& !listPointages.isEmpty()) {
			for(Pointage ptg : listPointages) {
				if(!ptg.getLatestEtatPointage().getEtat().equals(EtatPointageEnum.VALIDE)
						&& !ptg.getLatestEtatPointage().getEtat().equals(EtatPointageEnum.JOURNALISE)) {
					return false;
				}
			}
		}
		
		return true;
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
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(helperService.getMairieMatrFromIdAgent(idAgent), date);
		AgentStatutEnum agentStatus = carr != null ? carr.getStatutCarriere() : null;
		return agentStatus == statut ? carr : null;
	}

	@Override
	public CanStartVentilationDto canStartVentilationForAgentStatus(TypeChainePaieEnum chainePaie) {

		CanStartVentilationDto result = new CanStartVentilationDto();
		result.setCanStartVentilation(paieWorkflowService.canStartVentilation(chainePaie)
				&& ventilationRepository.canStartVentilation(chainePaie));

		if (result.isCanStartVentilation())
			logger.debug("Ventilation for chainePaie [{}] may be started. None currently processing...", chainePaie);
		else
			logger.debug(
					"Ventilation for chainePaie [{}] may not be started. An existing one is currently processing...",
					chainePaie);

		return result;
	}

	/**
	 * Return The list of ventilations done for the agents at ventildate
	 * 
	 * @param idDateVentil
	 * @param agents
	 * @param pointageType
	 * @return The list of VentilDto
	 */
	@Override
	public List<VentilDto> showVentilation(Integer idDateVentil, List<Integer> agents,
			RefTypePointageEnum pointageType, boolean allVentilation) {
		logger.debug(
				"Showing ventilation of Pointages for Agents [{}], idDateVentil [{}], allVentilation [{}] and pointage type [{}]",
				agents, idDateVentil, allVentilation, pointageType);
		List<VentilDto> pointagesVentiles = new ArrayList<>();
		// For all selected agents, get ventilated pointages

		switch (pointageType) {
			case ABSENCE: {
				List<VentilAbsence> liste = new ArrayList<VentilAbsence>();
				if (allVentilation) {
					liste = ventilationRepository
							.getListOfVentilAbsenceForDateAgentAllVentilation(idDateVentil, agents);
				} else {
					liste = ventilationRepository.getListOfVentilAbsenceForDateAgent(idDateVentil, agents);
				}
				for (VentilAbsence abs : liste)
					pointagesVentiles.add(new VentilAbsenceDto(abs));

				break;
			}
			case H_SUP: {
				List<VentilHsup> liste = new ArrayList<VentilHsup>();
				if (allVentilation) {
					liste = ventilationRepository.getListOfVentilHSForDateAgentAllVentilation(idDateVentil, agents);
				} else {
					liste = ventilationRepository.getListOfVentilHSForDateAgent(idDateVentil, agents);
				}
				for (VentilHsup hs : liste)
					pointagesVentiles.add(new VentilHSupDto(hs));

				break;
			}
			case PRIME: {
				List<VentilPrime> liste = new ArrayList<VentilPrime>();
				if (allVentilation) {
					liste = ventilationRepository.getListOfVentilPrimeForDateAgentAllVentilation(idDateVentil, agents,
							true);
				} else {
					liste = ventilationRepository.getListOfVentilPrimeForDateAgent(idDateVentil, agents, true);
				}
				for (VentilPrime prime : liste)
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

	@Override
	public List<VentilErreurDto> getErreursVentilation(AgentStatutEnum statut) {

		List<VentilErreurDto> result = new ArrayList<VentilErreurDto>();

		TypeChainePaieEnum chainePaie = helperService.getTypeChainePaieFromStatut(statut);

		VentilDate ventilDateTo = ventilationRepository.getLatestVentilDate(chainePaie, false);
		List<VentilTask> listventilTask = ventilationRepository.getListOfVentilTaskErreur(chainePaie, ventilDateTo);

		for (VentilTask vt : listventilTask) {
			VentilErreurDto dto = new VentilErreurDto();
			dto.setIdAgent(vt.getIdAgent());
			dto.setDateCreation(vt.getDateCreation());
			dto.setTaskStatus(vt.getTaskStatus());
			dto.setTypeChainePaie(vt.getTypeChainePaie().name());
			result.add(dto);
		}

		return result;
	}

	@Override
	public List<VentilDto> showVentilationHistory(Integer mois, Integer annee, Integer idAgent,
			RefTypePointageEnum pointageType, boolean allVentilation, Integer idVentilDate) {
		logger.debug(
				"Showing ventilation history of Pointages for idAgent [{}], mois [{}], annee [{}] and pointage type [{}]",
				idAgent, mois, annee, pointageType);
		List<VentilDto> pointagesVentiles = new ArrayList<>();
		// For all selected agents, get ventilated pointages

		switch (pointageType) {
			case ABSENCE: {
				List<VentilAbsence> liste = new ArrayList<VentilAbsence>();
				if (allVentilation) {
					liste = ventilationRepository.getListOfVentilAbsenceForAgentBeetweenDateAllVentilation(mois, annee,
							idAgent, idVentilDate);
				} else {
					liste = ventilationRepository.getListOfVentilAbsenceForAgentBeetweenDate(mois, annee, idAgent, idVentilDate);
				}
				for (VentilAbsence abs : liste)
					pointagesVentiles.add(new VentilAbsenceDto(abs));

				break;
			}

			case H_SUP: {
				List<VentilHsup> liste = new ArrayList<VentilHsup>();
				if (allVentilation) {
					liste = ventilationRepository.getListOfVentilHSForAgentBeetweenDateAllVentilation(mois, annee,
							idAgent, idVentilDate);
				} else {
					liste = ventilationRepository.getListOfVentilHSForAgentBeetweenDate(mois, annee, idAgent, idVentilDate);
				}
				for (VentilHsup hs : liste)
					pointagesVentiles.add(new VentilHSupDto(hs));

				break;
			}
			default:
				break;

		}
		logger.debug("Returning {} ventilated pointages from showVentilation WS.", pointagesVentiles.size());
		return pointagesVentiles;
	}

	@Override
	public CanStartVentilationDto isVentilationRunning(TypeChainePaieEnum typeChainePaieFromStatut) {
		CanStartVentilationDto result = new CanStartVentilationDto();
		result.setCanStartVentilation(!ventilationRepository.canStartVentilation(typeChainePaieFromStatut));
		return result;
	}

	@Override
	public List<Integer> getListeAgentsToShowVentilation(Integer idDateVentil, Integer idRefTypePointage,
			AgentStatutEnum statut, Integer agentMin, Integer agentMax, Date dateVentilation, boolean allVentilation) {

		List<Integer> listeAgents = new ArrayList<Integer>();

		switch (RefTypePointageEnum.getRefTypePointageEnum(idRefTypePointage)) {
			case ABSENCE: {
				for (Integer idAgent : ventilationRepository.getListAgentsForShowVentilationAbsencesForDate(
						idDateVentil, agentMin, agentMax, allVentilation)) {
					Spcarr carr = isAgentEligibleToVentilation(idAgent, statut, dateVentilation);
					if (null != carr) {
						listeAgents.add(idAgent);
					}
				}
				break;
			}
			case H_SUP: {
				for (Integer idAgent : ventilationRepository.getListAgentsForShowVentilationHeuresSupForDate(
						idDateVentil, agentMin, agentMax, allVentilation)) {
					Spcarr carr = isAgentEligibleToVentilation(idAgent, statut, dateVentilation);
					if (null != carr) {
						listeAgents.add(idAgent);
					}
				}
				break;
			}
			case PRIME: {
				for (Integer idAgent : ventilationRepository.getListAgentsForShowVentilationPrimesForDate(idDateVentil,
						agentMin, agentMax, allVentilation)) {
					Spcarr carr = isAgentEligibleToVentilation(idAgent, statut, dateVentilation);
					if (null != carr) {
						listeAgents.add(idAgent);
					}
				}
				break;
			}
		}

		return listeAgents;
	}
}
