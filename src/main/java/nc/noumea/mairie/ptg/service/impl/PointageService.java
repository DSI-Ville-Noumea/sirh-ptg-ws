package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.MotifHeureSup;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefEtat;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.AbsenceDto;
import nc.noumea.mairie.ptg.dto.AbsenceDtoKiosque;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.FichePointageDtoKiosque;
import nc.noumea.mairie.ptg.dto.FichePointageListDto;
import nc.noumea.mairie.ptg.dto.HeureSupDto;
import nc.noumea.mairie.ptg.dto.HeureSupDtoKiosque;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.JourPointageDtoKiosque;
import nc.noumea.mairie.ptg.dto.MotifHeureSupDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.PrimeDtoKiosque;
import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.RefTypeAbsenceDto;
import nc.noumea.mairie.ptg.dto.RefTypePointageDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.SirhWsServiceDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.service.NotAMondayException;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageService implements IPointageService {

	private Logger logger = LoggerFactory.getLogger(PointageService.class);

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private IMairieRepository mairieRepository;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	private HelperService helperService;

	@Autowired
	private IAgentMatriculeConverterService agentMatriculeConverterService;

	public static final String MOTIF_MODIFIE_INEXISTANT = "Le motif à modifier n'existe pas.";
	public static final String LIBELLE_MOTIF_VIDE = "Le libellé du motif n'est pas saisi.";

	// POUR LES MESSAGE A ENVOYE AU PROJET SIRH-ABS-WS
	public static final String POINTAGE_MSG = "%s : L'agent a déjà un pointage sur cette période.";

	protected FichePointageDto getFichePointageForAgent(AgentGeneriqueDto agent, Date date) {

		if (!helperService.isDateAMonday(date)) {
			throw new NotAMondayException();
		}

		// Retrieve division service of agent
		SirhWsServiceDto service = sirhWsConsumer.getAgentDirection(agent.getIdAgent(), date);

		// on construit le dto de l'agent
		AgentWithServiceDto agentDto = new AgentWithServiceDto(agent);
		agentDto.setCodeService(service.getService());
		agentDto.setService(service.getServiceLibelle());

		// on recherche sa carriere pour avoir son statut (Fonctionnaire,
		// contractuel,convention coll
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(agent, helperService.getCurrentDate());
		agentDto.setStatut(carr.getStatutCarriere().name());

		// on construit le DTO de jourPointage
		FichePointageDto result = new FichePointageDto();
		result.setDateLundi(date);
		result.setAgent(agentDto);
		result.setDPM(service.getSigle().toUpperCase().equals("DPM"));
		result.setSemaine(helperService.getWeekStringFromDate(date));

		// on recupere l'INA de l agent
		// #13290 HSup A recuperer pour INA > 315
		result.setINASuperieur315(null != carr.getSpbarem() && carr.getSpbarem().getIna() > 315);

		JourPointageDto jourPointageTemplate = new JourPointageDto();
		jourPointageTemplate.setDate(date);
		List<Integer> pps = sirhWsConsumer.getPrimePointagesByAgent(agent.getIdAgent(), date);
		if (pps.size() > 0) {
			List<RefPrime> refPrimes = pointageRepository.getRefPrimes(pps, carr.getStatutCarriere());

			for (RefPrime prime : refPrimes) {
				jourPointageTemplate.getPrimes().add(new PrimeDto(prime));
			}
		}

		result.getSaisies().add(jourPointageTemplate);

		// tu as un jour de la semaine type avec toutes les primes

		for (int jour = 1; jour < 7; jour++) {
			JourPointageDto jourSuivant = new JourPointageDto(jourPointageTemplate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(jourPointageTemplate.getDate());
			calendar.add(Calendar.DATE, jour);
			jourSuivant.setDate(calendar.getTime());
			result.getSaisies().add(jourSuivant);
		}

		return result;
	}

	@Override
	public FichePointageListDto getFichesPointageForUsers(String csvIdAgents, Date date) {

		List<AgentGeneriqueDto> listAgent = new ArrayList<AgentGeneriqueDto>();

		for (String id : csvIdAgents.split(",")) {
			Integer convertedId = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(Integer
					.valueOf(id));
			AgentGeneriqueDto ag = sirhWsConsumer.getAgent(convertedId);
			if (ag != null) {
				listAgent.add(ag);
			}
		}

		FichePointageListDto fiches = new FichePointageListDto();

		for (AgentGeneriqueDto agent : listAgent) {
			FichePointageDto ficheDto = getFichePointageForAgent(agent, date);
			fiches.getFiches().add(ficheDto);
		}

		return fiches;
	}

	@Override
	public FichePointageDto getFilledFichePointageForAgent(int idAgent, Date dateLundi) {

		AgentGeneriqueDto agent = sirhWsConsumer.getAgent(idAgent);

		FichePointageDto ficheDto = getFichePointageForAgent(agent, dateLundi);

		List<Pointage> agentPointages = getLatestPointagesForSaisieForAgentAndDateMonday(idAgent, dateLundi);

		for (Pointage ptg : agentPointages) {

			JourPointageDto jour = ficheDto.getSaisies().get(helperService.getWeekDayFromDateBase0(ptg.getDateDebut()));

			switch (ptg.getTypePointageEnum()) {
				case ABSENCE:
					AbsenceDto abs = new AbsenceDto(ptg);
					jour.getAbsences().add(abs);
					break;

				case H_SUP:
					HeureSupDto hsup = new HeureSupDto(ptg);
					jour.getHeuresSup().add(hsup);
					break;

				case PRIME:
					// Retrieve related primeDto in JourPointageDto and update
					// it
					// with value from Pointage
					PrimeDto thePrimeToUpdate = null;
					for (PrimeDto pDto : jour.getPrimes()) {
						if (pDto.getNumRubrique().equals(ptg.getRefPrime().getNoRubr())) {
							thePrimeToUpdate = pDto;
						}
					}
					assert thePrimeToUpdate != null;
					thePrimeToUpdate.updateWithPointage(ptg);
					break;
			}
		}

		return ficheDto;
	}

	@Override
	public Pointage getOrCreateNewPointage(Integer idAgentCreator, Integer idPointage, Integer idAgent, Date dateLundi,
			Date dateEtat) {
		return getOrCreateNewPointage(idAgentCreator, idPointage, idAgent, dateLundi, dateEtat, null);
	}

	@Override
	public Pointage getOrCreateNewPointage(Integer idAgentCreator, Integer idPointage, Integer idAgent, Date dateLundi,
			Date dateEtat, Integer idRefPrime) {

		Pointage ptg = null;
		Pointage parentPointage = null;

		// if the pointage already exists, fetch it
		if (idPointage != null && !idPointage.equals(0)) {
			ptg = pointageRepository.getEntity(Pointage.class, idPointage);

			// if its state is SAISI, return it.
			// Otherwise create a new one with this one as parent
			EtatPointage etatPtg = ptg.getLatestEtatPointage();
			if (etatPtg.getEtat() == EtatPointageEnum.SAISI) {
				etatPtg.setDateMaj(helperService.getCurrentDate());
				etatPtg.setIdAgent(idAgentCreator);
				return ptg;
			}
			parentPointage = ptg;
		}

		ptg = new Pointage();
		ptg.setPointageParent(parentPointage);
		ptg.setIdAgent(idAgent);
		ptg.setDateLundi(dateLundi);

		addEtatPointage(ptg, EtatPointageEnum.SAISI, idAgentCreator, dateEtat);

		// If this pointage is a new version of an existing one,
		// initialize its properties with the parent Pointage
		if (parentPointage != null) {
			ptg.setDateDebut(parentPointage.getDateDebut());
			ptg.setDateFin(parentPointage.getDateFin());
			ptg.setQuantite(parentPointage.getQuantite());
			ptg.setRefTypeAbsence(parentPointage.getRefTypeAbsence());
			ptg.setHeureSupRecuperee(parentPointage.getHeureSupRecuperee());
			ptg.setHeureSupRappelService(parentPointage.getHeureSupRappelService());
			ptg.setType(parentPointage.getType());
		}

		// if this is a Prime kind of Pointage, fetch its RefPrime
		if (idRefPrime != null) {
			ptg.setRefPrime(pointageRepository.getEntity(RefPrime.class, idRefPrime));
		}

		return ptg;
	}

	/**
	 * Adds an EtatPointage state to a given Pointage
	 * 
	 * @param ptg
	 * @param etat
	 * @param dateEtat
	 */
	@Override
	public void addEtatPointage(Pointage ptg, EtatPointageEnum etat, Integer idAgentCreator, Date dateEtat) {
		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(dateEtat);
		ep.setDateMaj(helperService.getCurrentDate());
		ep.setPointage(ptg);
		ep.setEtat(etat);
		ep.setIdAgent(idAgentCreator);
		ptg.getEtats().add(ep);
	}

	@Override
	public List<RefEtatDto> getRefEtats() {
		List<RefEtatDto> res = new ArrayList<RefEtatDto>();
		List<RefEtat> refEtats = pointageRepository.findAllRefEtats();
		for (RefEtat etat : refEtats) {
			RefEtatDto dto = new RefEtatDto(etat);
			res.add(dto);
		}
		return res;
	}

	@Override
	public List<RefTypePointageDto> getRefTypesPointage() {
		List<RefTypePointageDto> res = new ArrayList<RefTypePointageDto>();
		List<RefTypePointage> refTypePointage = pointageRepository.findAllRefTypePointages();
		for (RefTypePointage type : refTypePointage) {
			RefTypePointageDto dto = new RefTypePointageDto(type);
			res.add(dto);
		}
		return res;
	}

	@Override
	public List<Pointage> getLatestPointagesForSaisieForAgentAndDateMonday(Integer idAgent, Date dateMonday) {

		List<Pointage> agentPointages = pointageRepository
				.getPointagesForAgentAndDateOrderByIdDesc(idAgent, dateMonday);

		logger.debug("Found {} Pointage for agent {} and date monday {}", agentPointages.size(), idAgent, dateMonday);

		return filterOldPointagesAndEtatFromList(agentPointages, Arrays.asList(EtatPointageEnum.APPROUVE,
				EtatPointageEnum.EN_ATTENTE, EtatPointageEnum.JOURNALISE, EtatPointageEnum.REFUSE,
				EtatPointageEnum.REJETE, EtatPointageEnum.SAISI, EtatPointageEnum.VALIDE, EtatPointageEnum.VENTILE),
				null);
	}

	@Override
	public List<Pointage> getLatestPointagesForAgentsAndDates(List<Integer> idAgents, Date fromDate, Date toDate,
			RefTypePointageEnum type, List<EtatPointageEnum> etats, String typeHS) {

		List<Pointage> agentPointages = pointageRepository.getListPointages(idAgents, fromDate, toDate,
				type != null ? type.getValue() : null);

		logger.debug("Found {} Pointage for agents {} between dates {} and {}", agentPointages.size(), idAgents,
				fromDate, toDate);

		return filterOldPointagesAndEtatFromList(agentPointages, etats, typeHS);
	}

	@Override
	public List<Pointage> getPointagesVentilesForAgent(Integer idAgent, VentilDate ventilDate) {

		List<Pointage> agentPointages = pointageRepository.getPointagesVentilesForAgent(idAgent,
				ventilDate.getIdVentilDate());

		logger.debug("Found {} Pointage for agent {} and ventil date {} as of {}", agentPointages.size(), idAgent,
				ventilDate.getIdVentilDate(), ventilDate.getDateVentilation());

		return filterOldPointagesAndEtatFromList(agentPointages, Arrays.asList(EtatPointageEnum.VENTILE), null);
	}

	@Override
	public List<Pointage> getPointagesVentilesAndRejetesForAgent(Integer idAgent, VentilDate ventilDate) {

		List<Pointage> agentPointages = pointageRepository.getPointagesVentilesForAgent(idAgent,
				ventilDate.getIdVentilDate());

		List<Pointage> result = filterOldPointagesAndEtatFromList(agentPointages,
				Arrays.asList(EtatPointageEnum.REJETE), null);

		logger.debug("Found {} Pointage Ventile Rejete for agent {} and ventil date {} as of {}", result.size(),
				idAgent, ventilDate.getIdVentilDate(), ventilDate.getDateVentilation());

		return result;
	}

	@Override
	public List<Pointage> getPointagesVentilesAndRejetesForAgentByDateLundi(Integer idAgent, VentilDate ventilDate, Date dateLundi) {

		List<Pointage> agentPointages = pointageRepository.getPointagesVentilesForAgentByDateLundi(idAgent,
				ventilDate.getIdVentilDate(), dateLundi);

		List<Pointage> result = filterOldPointagesAndEtatFromList(agentPointages,
				Arrays.asList(EtatPointageEnum.REJETE), null);

		logger.debug("Found {} Pointage Ventile Rejete for agent {} and ventil date {} as of {} and dateLundi {}", result.size(),
				idAgent, ventilDate.getIdVentilDate(), ventilDate.getDateVentilation(), dateLundi);

		return result;
	}

	@Override
	public List<Pointage> filterOldPointagesAndEtatFromList(List<Pointage> pointages, List<EtatPointageEnum> etats,
			String typeHS) {

		List<Integer> oldPointagesToAvoid = new ArrayList<Integer>();

		List<Pointage> resultList = new ArrayList<Pointage>();

		for (Pointage ptg : pointages) {

			if (ptg.getPointageParent() != null) {
				logger.debug("Pointage {} has a parent {}, adding it to avoid list.", ptg.getIdPointage(), ptg
						.getPointageParent().getIdPointage());
				oldPointagesToAvoid.add(ptg.getPointageParent().getIdPointage());
			}

			if (oldPointagesToAvoid.contains(ptg.getIdPointage())) {
				logger.debug("Not taking Pointage {} because not the latest.", ptg.getIdPointage());
				continue;
			}

			if (etats == null || etats.contains(ptg.getLatestEtatPointage().getEtat())) {
				resultList.add(ptg);
			} else {
				logger.debug("Not taking Pointage {} because not in the given Etat list : {}.", ptg.getIdPointage(),
						etats);
			}
		}

		List<Pointage> resultListFinal = new ArrayList<Pointage>();
		if (typeHS != null) {
			for (Pointage p : resultList) {
				if (p.getTypePointageEnum().equals(RefTypePointageEnum.H_SUP)) {
					if (typeHS.toUpperCase().equals("RS")) {
						if (p.getHeureSupRappelService() != null && p.getHeureSupRappelService()) {
							if (!resultListFinal.contains(p))
								resultListFinal.add(p);
						}
					} else if (typeHS.toUpperCase().equals("R")) {
						// si elle est en rappel de service on ne l'ajoute pas
						if (p.getHeureSupRappelService() == null || !p.getHeureSupRappelService()) {
							if (p.getHeureSupRecuperee() != null && p.getHeureSupRecuperee()) {
								if (!resultListFinal.contains(p))
									resultListFinal.add(p);
							}
						}
					} else if (typeHS.toUpperCase().equals("AUTRE")) {
						// on ajoute celle qui ne sont ni en rappel ni
						// recupérées
						if (p.getHeureSupRappelService() == null && p.getHeureSupRecuperee() == null) {
							if (!resultListFinal.contains(p))
								resultListFinal.add(p);
						}

					}
				}
			}
		} else {
			resultListFinal = resultList;
		}

		return resultListFinal;
	}

	@Override
	public List<PointageCalcule> getPointagesCalculesVentilesForAgent(Integer idAgent, VentilDate ventilDate) {

		List<PointageCalcule> agentPointagesCalcules = pointageRepository.getPointagesCalculesVentilesForAgent(idAgent,
				ventilDate.getIdVentilDate());

		logger.debug("Found {} Pointage Calcules for agent {} and ventil date {} as of {}",
				agentPointagesCalcules.size(), idAgent, ventilDate.getIdVentilDate(), ventilDate.getDateVentilation());

		return agentPointagesCalcules;
	}

	@Override
	public boolean isPrimeUtiliseePointage(Integer idAgent, List<Integer> idRefPrime) {

		for (int idPrime : idRefPrime) {
			if (pointageRepository.isPrimeSurPointageouPointageCalcule(idAgent, idPrime)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<RefTypeAbsenceDto> getRefTypeAbsence() {
		List<RefTypeAbsenceDto> res = new ArrayList<RefTypeAbsenceDto>();
		List<RefTypeAbsence> refTypeAbsence = pointageRepository.findAllRefTypeAbsence();
		for (RefTypeAbsence type : refTypeAbsence) {
			RefTypeAbsenceDto dto = new RefTypeAbsenceDto(type);
			res.add(dto);
		}
		return res;
	}

	@Override
	public List<MotifHeureSupDto> getMotifHeureSup() {
		List<MotifHeureSupDto> res = new ArrayList<MotifHeureSupDto>();
		List<MotifHeureSup> listeMotif = pointageRepository.findAllMotifHeureSup();
		for (MotifHeureSup motif : listeMotif) {
			MotifHeureSupDto dto = new MotifHeureSupDto(motif);
			res.add(dto);
		}
		return res;
	}

	@Override
	public ReturnMessageDto setMotifHeureSup(MotifHeureSupDto motifHeureSupDto) {
		ReturnMessageDto result = new ReturnMessageDto();
		MotifHeureSup motifCompteur = null;

		if (null != motifHeureSupDto.getIdMotifHsup()) {
			motifCompteur = pointageRepository.getEntity(MotifHeureSup.class, motifHeureSupDto.getIdMotifHsup());
			if (null == motifCompteur) {
				logger.debug(MOTIF_MODIFIE_INEXISTANT);
				result.getErrors().add(MOTIF_MODIFIE_INEXISTANT);
				return result;
			}
		}

		if (!controlLibelleMotif(motifHeureSupDto.getLibelle(), result))
			return result;

		if (null == motifCompteur) {
			motifCompteur = new MotifHeureSup();
		}

		motifCompteur.setText(motifHeureSupDto.getLibelle());

		pointageRepository.persisEntity(motifCompteur);

		return result;
	}

	protected boolean controlLibelleMotif(String libelle, ReturnMessageDto result) {

		if (null == libelle || "".equals(libelle)) {
			logger.debug(LIBELLE_MOTIF_VIDE);
			result.getErrors().add(LIBELLE_MOTIF_VIDE);
			return false;
		}
		return true;
	}

	@Override
	public FichePointageDtoKiosque getFilledFichePointageForAgentKiosque(int idAgent, Date dateLundi) {

		AgentGeneriqueDto agent = sirhWsConsumer.getAgent(idAgent);

		FichePointageDtoKiosque ficheDto = getFichePointageForAgentKiosque(agent, dateLundi);

		List<Pointage> agentPointages = getLatestPointagesForSaisieForAgentAndDateMonday(idAgent, dateLundi);

		for (Pointage ptg : agentPointages) {

			JourPointageDtoKiosque jour = ficheDto.getSaisies().get(
					helperService.getWeekDayFromDateBase0(ptg.getDateDebut()));

			switch (ptg.getTypePointageEnum()) {
				case ABSENCE:
					AbsenceDtoKiosque abs = new AbsenceDtoKiosque(ptg);
					jour.getAbsences().add(abs);
					break;

				case H_SUP:
					HeureSupDtoKiosque hsup = new HeureSupDtoKiosque(ptg);
					jour.getHeuresSup().add(hsup);
					break;

				case PRIME:
					// Retrieve related primeDto in JourPointageDto and update
					// it
					// with value from Pointage
					PrimeDtoKiosque thePrimeToUpdate = null;
					for (PrimeDtoKiosque pDto : jour.getPrimes()) {
						if (pDto.getNumRubrique().equals(ptg.getRefPrime().getNoRubr())) {
							thePrimeToUpdate = pDto;
						}
					}
					assert thePrimeToUpdate != null;
					thePrimeToUpdate.updateWithPointage(ptg);
					break;
			}
		}

		return ficheDto;
	}

	protected FichePointageDtoKiosque getFichePointageForAgentKiosque(AgentGeneriqueDto agent, Date date) {

		if (!helperService.isDateAMonday(date)) {
			throw new NotAMondayException();
		}
		FichePointageDtoKiosque result = new FichePointageDtoKiosque();

		// Retrieve division service of agent
		SirhWsServiceDto service = sirhWsConsumer.getAgentDirection(agent.getIdAgent(), date);
		if (service == null) {
			return result;
		}

		// on construit le dto de l'agent
		AgentWithServiceDto agentDto = new AgentWithServiceDto(agent);
		agentDto.setCodeService(service.getService());
		agentDto.setService(service.getServiceLibelle());

		// on recherche sa carriere pour avoir son statut (Fonctionnaire,
		// contractuel,convention coll
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(agent, helperService.getCurrentDate());
		agentDto.setStatut(carr.getStatutCarriere().name());

		// on construit le DTO de jourPointage
		result.setDateLundi(date);
		result.setAgent(agentDto);
		result.setDPM(service.getSigle().toUpperCase().equals("DPM"));
		result.setSemaine(helperService.getWeekStringFromDate(date));

		// on recupere l'INA de l agent
		// #13290 HSup A recuperer pour INA > 315
		// #14217 si iban du barem alphanumerique alors on considère INA >315
		if (null != carr.getSpbarem()) {
			try {
				@SuppressWarnings("unused")
				Integer iban = new Integer(carr.getSpbarem().getIban());
				result.setINASuperieur315(null != carr.getSpbarem() && carr.getSpbarem().getIna() > 315);
			} catch (Exception e) {
				result.setINASuperieur315(true);
			}
		} else {
			result.setINASuperieur315(true);
		}

		JourPointageDtoKiosque jourPointageTemplate = new JourPointageDtoKiosque();
		jourPointageTemplate.setDate(date);
		List<Integer> pps = sirhWsConsumer.getPrimePointagesByAgent(agent.getIdAgent(), date);
		if (pps.size() > 0) {
			List<RefPrime> refPrimes = pointageRepository.getRefPrimes(pps, carr.getStatutCarriere());

			for (RefPrime prime : refPrimes) {
				jourPointageTemplate.getPrimes().add(new PrimeDtoKiosque(prime));
			}
		}

		result.getSaisies().add(jourPointageTemplate);

		// tu as un jour de la semaine type avec toutes les primes

		for (int jour = 1; jour < 7; jour++) {
			JourPointageDtoKiosque jourSuivant = new JourPointageDtoKiosque(jourPointageTemplate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(jourPointageTemplate.getDate());
			calendar.add(Calendar.DATE, jour);
			jourSuivant.setDate(calendar.getTime());
			result.getSaisies().add(jourSuivant);
		}

		return result;
	}

	@Override
	public ReturnMessageDto checkPointage(Integer convertedIdAgent, Date fromDate, Date toDate) {
		ReturnMessageDto result = new ReturnMessageDto();
		// on cherche toutes les demandes de repos comp de l'agent entre les
		// dates
		List<Pointage> listePointage = new ArrayList<Pointage>();
		List<Pointage> listePointageAbs = pointageRepository.getListPointagesVerification(convertedIdAgent, fromDate,
				toDate, RefTypePointageEnum.ABSENCE.getValue());
		listePointage.addAll(listePointageAbs);
		List<Pointage> listePointageHSup = pointageRepository.getListPointagesVerification(convertedIdAgent, fromDate,
				toDate, RefTypePointageEnum.H_SUP.getValue());
		listePointage.addAll(listePointageHSup);

		List<EtatPointageEnum> listEtatsAcceptes = new ArrayList<EtatPointageEnum>();
		listEtatsAcceptes.addAll(Arrays.asList(EtatPointageEnum.APPROUVE, EtatPointageEnum.VENTILE,
				EtatPointageEnum.VALIDE, EtatPointageEnum.EN_ATTENTE, EtatPointageEnum.JOURNALISE));

		listePointage = filterOldPointagesAndEtatFromList(listePointage, listEtatsAcceptes, null);
		if (listePointage.size() > 0) {
			// on bloque quel que soit l'etat du pointage
			String msg = String.format(POINTAGE_MSG, new DateTime(fromDate).toString("dd/MM/yyyy HH:mm"));
			result.getErrors().add(msg);
		}

		return result;
	}
}
