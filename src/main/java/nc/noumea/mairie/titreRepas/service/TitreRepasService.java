package nc.noumea.mairie.titreRepas.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefTypeAbsenceDto;
import nc.noumea.mairie.abs.dto.RefTypeGroupeAbsenceEnum;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.RefEtat;
import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.reporting.EtatPayeurTitreRepasReporting;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.ptg.service.impl.PointageDataConsistencyRules;
import nc.noumea.mairie.ptg.web.AccessForbiddenException;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AffectationDto;
import nc.noumea.mairie.sirh.dto.JourDto;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasDemandeDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasEtatPayeurDto;
import nc.noumea.mairie.titreRepas.repository.ITitreRepasRepository;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;
import nc.noumea.mairie.ws.SirhWSUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.DocumentException;

@Service
public class TitreRepasService implements ITitreRepasService {

	private Logger logger = LoggerFactory.getLogger(TitreRepasService.class);

	@Autowired
	private HelperService helperService;

	@Autowired
	private IMairieRepository mairieRepository;

	@Autowired
	private ITitreRepasRepository titreRepasRepository;

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private IAccessRightsRepository accessRightsRepository;

	@Autowired
	private IAbsWsConsumer absWsConsumer;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	private SirhWSUtils sirhWSUtils;

	@Autowired
	private IAccessRightsService accessRightsService;

	@Autowired
	private IPaieWorkflowService paieWorkflowService;

	@Autowired
	private EtatPayeurTitreRepasReporting reportingService;

	@Autowired
	private IAccessRightsService accessRightService;

	private static SimpleDateFormat sfd = new SimpleDateFormat("YYYY-MM");

	public static final String ERREUR_DROIT_AGENT = "Vous n'avez pas les droits pour traiter cette demande de titre repas.";
	public static final String DATE_SAISIE_NON_COMPRISE_ENTRE_1_ET_10_DU_MOIS = "Vous ne pouvez commander les titres repas qu'entre le 1 et 10 de chaque mois.";
	public static final String DATE_SAISIE_NON_COMPRISE_ENTRE_1_ET_EDITION_PAYEUR = "Vous ne pouvez saisir des demandes de titres repas qu'entre le 1 du mois et l'édition du payeur.";
	public static final String AUCUNE_PA_ACTIVE_MOIS_PRECEDENT = "L'agent %s n'a pas travaillé le mois précédent.";
	public static final String AUCUNE_BASE_CONGE = "La base congé n'est pas renseignée pour l'agent %s.";
	public static final String PRIME_PANIER = "L'agent a le droit aux primes panier et ne peut donc pas commander des titres repas.";
	public static final String FILIERE_INCENDIE = "L'agent fait parti de la filière incendie et ne peut donc pas commander de titres repas.";
	public static final String TITRE_DEMANDE_DEJA_EXISTANT = "Une demande de titre repas existe déjà pour ce mois-ci pour l'agent %s.";
	public static final String TITRE_DEMANDE_INEXISTANT = "La demande de titre repas n'existe pas.";
	public static final String DTO_NULL = "Merci de saisir la demande de titre repas.";
	public static final String MOIS_COURS_NON_SAISI = "Le mois en cours de la demande n'est pas saisi.";
	public static final String AGENT_NON_SAISI = "L'ID agent n'est pas renseigné.";
	public static final String ETAT_NON_SAISI = "L'état de la demande de titre repas n'est pas renseigné pour l'agent : %s.";
	public static final String AUCUNE_DEMANDE = "Aucune demande de titre repas à approuver.";
	public static final String AUCUN_ID_DEMANDE = "L'ID de la demande de titre repas n'est pas renseigné.";
	public static final String AUCUNE_DEMANDE_TROUVEE = "La demande de titre repas n'existe pas.";
	public static final String DEMANDE_MOIS_EN_COURS_ERROR = "La demande de titre repas n'existe pas.";
	public static final String DEMANDE_NON_COMMANDE = "Vous ne pouvez pas approuvé une demande de titre repas non commandée.";
	public static final String ERROR_ETAT_DEMANDE = "Vous ne pouvez pas %s une demande de titre repas à l'état %s.";
	public static final String NOUVELLE_ETAT_INCORRECT = "Le nouvel état de la demande de titre repas est incorrect.";
	public static final String PAIE_EN_COURS = "Génération impossible. Une paie est en cours sous l'AS400.";

	public static final String ENREGISTREMENT_OK = "La demande est bien enregistrée.";
	public static final String GENERATION_ETAT_PAYEUR_OK = "L'état payeur des titres repas est bien généré.";

	public static final List<Integer> LIST_PRIMES_PANIER = Arrays.asList(7704, 7713);
	public static final String CODE_FILIERE_INCENDIE = "I";

	/**
	 * Enregistre une liste de demande de Titre Repas depuis le Kiosque RH.
	 * 
	 * @param listTitreRepasDemandeDto
	 *            List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	@Override
	@Transactional("ptgTransactionManager")
	public ReturnMessageDto enregistreListTitreDemandeFromKiosque(Integer idAgentConnecte, List<TitreRepasDemandeDto> listTitreRepasDemandeDto) {

		// //// Verifie les droits ////////
		ReturnMessageDto rmd = new ReturnMessageDto();
		// si l'agent connecté est lui-même
		Integer idAgentDemande = null;
		if (listTitreRepasDemandeDto != null && listTitreRepasDemandeDto.size() == 1) {
			idAgentDemande = listTitreRepasDemandeDto.get(0).getAgent().getIdAgent();
			if (!idAgentConnecte.equals(idAgentDemande) && !accessRightsService.isUserApprobateur(idAgentConnecte) && !accessRightsService.isUserOperateur(idAgentConnecte)) {
				rmd.getErrors().add(ERREUR_DROIT_AGENT);
				return rmd;
			}

		} else {
			if (!accessRightsService.isUserApprobateur(idAgentConnecte) && !accessRightsService.isUserOperateur(idAgentConnecte)) {
				rmd.getErrors().add(ERREUR_DROIT_AGENT);
				return rmd;
			}
		}

		// saisie entre le 1 et le 10
		// si au dela du 10 du mois, cela ne sert a rien de faire tous les
		// appels ci-dessous
		rmd = checkDateJourBetween1And10ofMonth(rmd);
		if (!rmd.getErrors().isEmpty())
			return rmd;

		// ///////////////////////////////////////////////////////////////
		// /////// on recupere toutes les donnees qui nous interessent ///
		// /!\ EN UNE SEULE FOIS /!\ //
		List<Integer> listIdsAgent = new ArrayList<Integer>();
		for (TitreRepasDemandeDto dto : listTitreRepasDemandeDto) {
			if (!listIdsAgent.contains(dto.getAgent().getIdAgent())) {
				listIdsAgent.add(dto.getAgent().getIdAgent());
			}
		}

		Date dateDebutMois = helperService.getDatePremierJourOfMonth(new Date());
		Date dateFinMois = helperService.getDateDernierJourOfMonth(new Date());

		List<JourDto> listJourFerieMoisEnCours = sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois);
		List<AffectationDto> listAffectation = sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(listIdsAgent, dateDebutMois, dateFinMois);
		List<DemandeDto> listAbsences = absWsConsumer.getListAbsencesForListAgentsBetween2Dates(listIdsAgent, dateDebutMois, dateFinMois);
		List<RefTypeSaisiCongeAnnuelDto> listBasesConges = getListBasesConges();
		// ///////////////////////////////////////////////////////////////

		ReturnMessageDto result = new ReturnMessageDto();
		for (TitreRepasDemandeDto dto : listTitreRepasDemandeDto) {

			// on trie les donnees pour l agent concerne
			AffectationDto affectation = getDernierAffectationByAgent(dto.getAgent().getIdAgent(), listAffectation);
			RefTypeSaisiCongeAnnuelDto baseCongeAgent = getRefTypeSaisiCongeAnnuelDto(listBasesConges, affectation.getBaseConge());
			List<DemandeDto> listAbsencesAgent = getListeAbsencesByAgent(listAbsences, dto.getAgent().getIdAgent());

			ReturnMessageDto response = enregistreTitreDemandeOneByOne(idAgentConnecte, dto, listAbsencesAgent, baseCongeAgent, listJourFerieMoisEnCours, affectation, false);

			if (!response.getErrors().isEmpty()) {
				result.getErrors().addAll(response.getErrors());
			}
			if (!response.getInfos().isEmpty()) {
				result.getInfos().addAll(response.getInfos());
			}
		}

		return result;
	}

	/**
	 * Enregistre une liste de demande de Titre Repas depuis SIRH.
	 * 
	 * @param listTitreRepasDemandeDto
	 *            List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	@Override
	@Transactional("ptgTransactionManager")
	public ReturnMessageDto enregistreListTitreDemandeFromSIRH(Integer idAgentConnecte, List<TitreRepasDemandeDto> listTitreRepasDemandeDto) {

		ReturnMessageDto rmd = new ReturnMessageDto();
		// //// Verifie les droits ////////
		ReturnMessageDto messageSIRH = sirhWsConsumer.isUtilisateurSIRH(idAgentConnecte);
		if (!messageSIRH.getErrors().isEmpty()) {
			rmd.getErrors().add(ERREUR_DROIT_AGENT);
			return rmd;
		}

		// action possible entre le 1 du mois et la génération
		rmd = checkDateJourBetween1OfMonthAndGeneration(rmd);
		if (!rmd.getErrors().isEmpty())
			return rmd;

		// ///////////////////////////////////////////////////////////////
		// /////// on recupere toutes les donnees qui nous interessent ///
		// /!\ EN UNE SEULE FOIS /!\ //
		List<Integer> listIdsAgent = new ArrayList<Integer>();
		for (TitreRepasDemandeDto dto : listTitreRepasDemandeDto) {
			if (!listIdsAgent.contains(dto.getAgent().getIdAgent())) {
				listIdsAgent.add(dto.getAgent().getIdAgent());
			}
		}

		Date dateDebutMois = helperService.getDatePremierJourOfMonth(new Date());
		Date dateFinMois = helperService.getDateDernierJourOfMonth(new Date());

		List<JourDto> listJourFerieMoisEnCours = sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois);
		List<AffectationDto> listAffectation = sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(listIdsAgent, dateDebutMois, dateFinMois);
		List<DemandeDto> listAbsences = absWsConsumer.getListAbsencesForListAgentsBetween2Dates(listIdsAgent, dateDebutMois, dateFinMois);
		List<RefTypeSaisiCongeAnnuelDto> listBasesConges = getListBasesConges();
		// ///////////////////////////////////////////////////////////////

		ReturnMessageDto result = new ReturnMessageDto();
		for (TitreRepasDemandeDto dto : listTitreRepasDemandeDto) {

			// on trie les donnees pour l agent concerne
			AffectationDto affectation = getDernierAffectationByAgent(dto.getAgent().getIdAgent(), listAffectation);
			RefTypeSaisiCongeAnnuelDto baseCongeAgent = getRefTypeSaisiCongeAnnuelDto(listBasesConges, affectation.getBaseConge());
			List<DemandeDto> listAbsencesAgent = getListeAbsencesByAgent(listAbsences, dto.getAgent().getIdAgent());

			ReturnMessageDto response = enregistreTitreDemandeOneByOne(idAgentConnecte, dto, listAbsencesAgent, baseCongeAgent, listJourFerieMoisEnCours, affectation, true);

			if (!response.getErrors().isEmpty()) {
				result.getInfos().addAll(response.getErrors());
			}
			if (!response.getInfos().isEmpty()) {
				result.getInfos().addAll(response.getInfos());
			}
		}

		return result;
	}

	/**
	 * Enregistre une demande de Titre Repas pour un agent pour un mois donne.
	 * 
	 * Les RG sont verifiees.
	 * 
	 * Est utilise par l enresgistrement d une demande par : - l agent
	 * directement => enregistreTitreDemandeAgent() - l operateur ou approbateur
	 * => - SIRH =>
	 * 
	 * @param dto
	 *            TitreRepasDemandeDto
	 * @param listAbsences
	 *            List<DemandeDto>
	 * @param baseCongeAgent
	 *            RefTypeSaisiCongeAnnuelDto
	 * @param listJoursFeries
	 *            List<JourDto>
	 * @param affectation
	 *            AffectationDto
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto enregistreTitreDemandeOneByOne(Integer idAgentConnecte, TitreRepasDemandeDto dto, List<DemandeDto> listAbsences, RefTypeSaisiCongeAnnuelDto baseCongeAgent,
			List<JourDto> listJoursFeries, AffectationDto affectation, boolean isSIRH) {

		ReturnMessageDto result = new ReturnMessageDto();

		// on force la date du mois en cours
		dto.setDateMonth(helperService.getDatePremierJourOfMonth(new Date()));

		// on verifie les donnees du DTO
		result = checkDataTitreRepasDemandeDto(result, dto);
		if (!result.getErrors().isEmpty())
			return result;

		// check les RG
		result = checkDroitATitreRepas(result, dto.getAgent().getIdAgent(), dto.getDateMonth(), listAbsences, baseCongeAgent, listJoursFeries, affectation);
		if (!result.getErrors().isEmpty() && !isSIRH)
			return result;

		// on verifie si une demande existe deja
		// si TitreRepasDemandeDto.idTrDemande <> NULL
		// alors modification
		TitreRepasDemande trDemande = null;
		if (null != dto.getIdTrDemande()) {
			trDemande = titreRepasRepository.getTitreRepasDemandeById(dto.getIdTrDemande());

			if (null == trDemande) {
				result.getErrors().add(TITRE_DEMANDE_INEXISTANT);
				return result;
			}
		} else {
			// on verifie qu une demande n existe pas
			List<TitreRepasDemande> listTitreRepasDemande = titreRepasRepository.getListTitreRepasDemande(Arrays.asList(dto.getAgent().getIdAgent()), null, null, dto.getIdRefEtat(), null,
					dto.getDateMonth());

			if (null != listTitreRepasDemande && !listTitreRepasDemande.isEmpty()) {
				result.getErrors().add(String.format(TITRE_DEMANDE_DEJA_EXISTANT, dto.getAgent().getIdAgent()));
				return result;
			}
		}

		if (null == trDemande) {
			trDemande = new TitreRepasDemande();
			trDemande.setIdAgent(dto.getAgent().getIdAgent());
			trDemande.setDateMonth(dto.getDateMonth());
		}

		TitreRepasEtatDemande etat = new TitreRepasEtatDemande();
		etat.setCommande(dto.getCommande());
		etat.setCommentaire(dto.getCommentaire());
		etat.setDateMaj(new Date());
		etat.setEtat(EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()));
		etat.setIdAgent(idAgentConnecte);
		etat.setCommande(dto.getCommande());
		etat.setTitreRepasDemande(trDemande);

		trDemande.getEtats().add(etat);

		titreRepasRepository.persist(trDemande);

		result.getInfos().add(ENREGISTREMENT_OK);

		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TitreRepasDemandeDto> getListTitreRepasDemandeDto(Integer idAgentConnecte, Date fromDate, Date toDate, Integer etat, Boolean commande, Date dateMonth, Integer idServiceADS,
			Integer idAgent) throws AccessForbiddenException {

		Date dateJour = new Date();
		// ///////// TEST de DROIT /////////////////
		if ((null == idAgent || !idAgent.equals(idAgentConnecte))
		// => si ce n est pas l agent qui consulte ces demandes
				&& !accessRightService.canUserAccessVisualisation(idAgentConnecte)
				// =>si ce n est pas l opêrateur ou approbateur
				&& !sirhWsConsumer.isUtilisateurSIRH(idAgentConnecte).getErrors().isEmpty())
			// => si ce n est pas un utilisateur SIRH
			throw new AccessForbiddenException(); // => on bloque

		// ///////////////// on recupere la liste d agents
		// ///////////////////////
		List<AgentWithServiceDto> listAgentServiceDto = new ArrayList<AgentWithServiceDto>();
		List<Integer> listIdsAgent = new ArrayList<Integer>();
		List<DroitsAgent> listDroitsAgentTemp = accessRightsRepository.getListOfAgentsToInputOrApprove(idAgentConnecte);
		List<DroitsAgent> listDroitsAgent = new ArrayList<DroitsAgent>();
		if (null != idAgent) {
			AgentWithServiceDto ag = sirhWsConsumer.getAgentService(idAgent, new Date());
			listAgentServiceDto.add(ag);
			listIdsAgent.add(idAgent);
		} else if (null != idServiceADS) {
			// #18722 : pour chaque agent on va recuperer son
			// service
			List<Integer> listAgentDtoAppro = new ArrayList<Integer>();
			for (DroitsAgent da : listDroitsAgentTemp) {
				if (!listAgentDtoAppro.contains(da.getIdAgent()))
					listAgentDtoAppro.add(da.getIdAgent());
			}
			listAgentServiceDto = sirhWsConsumer.getListAgentsWithService(listAgentDtoAppro, dateJour);

			for (DroitsAgent da : listDroitsAgentTemp) {
				AgentWithServiceDto agDtoServ = sirhWSUtils.getAgentOfListAgentWithServiceDto(listAgentServiceDto, da.getIdAgent());
				if (agDtoServ != null && agDtoServ.getIdServiceADS() != null && agDtoServ.getIdServiceADS().toString().equals(idServiceADS.toString())) {
					listDroitsAgent.add(da);
				}
			}
		} else {
			List<Integer> listTemp = new ArrayList<Integer>();
			for (DroitsAgent da : listDroitsAgentTemp) {
				listTemp.add(da.getIdAgent());
			}
			listAgentServiceDto = sirhWsConsumer.getListAgentsWithService(listTemp, dateJour);
			listDroitsAgent.addAll(listDroitsAgentTemp);
		}

		for (DroitsAgent da : listDroitsAgent) {
			if (!listIdsAgent.contains(da.getIdAgent()))
				listIdsAgent.add(da.getIdAgent());
		}

		// ////////////// on checke les DATES ////////////////////////
		if (null == fromDate && null == toDate && null == dateMonth) {
			toDate = helperService.getCurrentDate();
			fromDate = new DateTime(helperService.getCurrentDate()).minusYears(1).toDate();
		}

		// ////////////////// on recupere les demandes //////////////////////
		List<TitreRepasDemande> listTR = titreRepasRepository.getListTitreRepasDemande(listIdsAgent, fromDate, toDate, etat, commande, dateMonth);

		List<TitreRepasDemandeDto> result = new ArrayList<TitreRepasDemandeDto>();
		// optimisation performances
		Map<Integer, AgentWithServiceDto> mapAgentDto = new HashMap<Integer, AgentWithServiceDto>();

		if (null != listTR && !listTR.isEmpty()) {
			for (TitreRepasDemande TR : listTR) {
				AgentWithServiceDto agDtoServ = sirhWSUtils.getAgentOfListAgentWithServiceDto(listAgentServiceDto, TR.getIdAgent());
				TitreRepasDemandeDto dto = new TitreRepasDemandeDto(TR, agDtoServ);

				AgentWithServiceDto opeDto = null;
				if (mapAgentDto.containsKey(TR.getLatestEtatTitreRepasDemande().getIdAgent())) {
					opeDto = mapAgentDto.get(TR.getLatestEtatTitreRepasDemande().getIdAgent());
				} else {
					opeDto = sirhWsConsumer.getAgentService(TR.getLatestEtatTitreRepasDemande().getIdAgent(), dateJour);
					mapAgentDto.put(opeDto.getIdAgent(), opeDto);
				}
				dto.updateEtat(TR.getLatestEtatTitreRepasDemande(), opeDto);

				result.add(dto);
			}
		}

		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TitreRepasEtatPayeurDto> getListTitreRepasEtatPayeurDto() {
		// TODO
		return null;
	}

	/**
	 * Mets a jour l etat d une liste de demande de Titre Repas. Si on veut
	 * mettre à jour une seule demande, envoyer une seule demande dans la liste
	 * 
	 * @param idAgentConnecte
	 *            Integer
	 * @param listTitreRepasDemandeDto
	 *            List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	@Override
	@Transactional("ptgTransactionManager")
	public ReturnMessageDto updateEtatForListTitreRepasDemande(Integer idAgentConnecte, List<TitreRepasDemandeDto> listTitreRepasDemandeDto) {

		ReturnMessageDto result = new ReturnMessageDto();
		// //// Verifie les droits ////////
		ReturnMessageDto messageSIRH = sirhWsConsumer.isUtilisateurSIRH(idAgentConnecte);
		if (!messageSIRH.getErrors().isEmpty()) {
			result.getErrors().add(ERREUR_DROIT_AGENT);
			return result;
		}

		result = checkDateJourBetween1OfMonthAndGeneration(result);
		if (!result.getErrors().isEmpty())
			return result;

		if (null == listTitreRepasDemandeDto || listTitreRepasDemandeDto.isEmpty()) {
			result.getErrors().add(AUCUNE_DEMANDE);
			return result;
		}

		for (TitreRepasDemandeDto dto : listTitreRepasDemandeDto) {
			ReturnMessageDto rmd = updateEtatForTitreRepasDemande(idAgentConnecte, dto);

			if (!rmd.getErrors().isEmpty()) {
				result.getInfos().addAll(rmd.getErrors());
			}
			if (!rmd.getInfos().isEmpty()) {
				result.getInfos().addAll(rmd.getInfos());
			}
		}

		return result;
	}

	/**
	 * Mets a jour l etat d une demande de Titre Repas.
	 * 
	 * @param dto
	 *            TitreRepasDemandeDto
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto updateEtatForTitreRepasDemande(Integer idAgentConnecte, TitreRepasDemandeDto dto) {

		ReturnMessageDto rmd = new ReturnMessageDto();

		if (null == dto || null == dto.getIdTrDemande()) {
			rmd.getErrors().add(AUCUN_ID_DEMANDE);
			return rmd;
		}

		if (!dto.getIdRefEtat().equals(EtatPointageEnum.APPROUVE.getCodeEtat()) && !dto.getIdRefEtat().equals(EtatPointageEnum.REJETE.getCodeEtat())) {
			rmd.getErrors().add(NOUVELLE_ETAT_INCORRECT);
			return rmd;
		}

		TitreRepasDemande titreRepasDemande = titreRepasRepository.getTitreRepasDemandeById(dto.getIdTrDemande());

		if (null == titreRepasDemande) {
			rmd.getErrors().add(AUCUNE_DEMANDE_TROUVEE);
			return rmd;
		}

		// on check qu il s agit bien d une demande du mois en cours
		if (!titreRepasDemande.getDateMonth().equals(helperService.getDatePremierJourOfMonth(helperService.getCurrentDate()))) {
			rmd.getErrors().add(DEMANDE_MOIS_EN_COURS_ERROR);
			return rmd;
		}

		// on check l ETAT
		if (null == titreRepasDemande.getLatestEtatTitreRepasDemande().getCommande() || !titreRepasDemande.getLatestEtatTitreRepasDemande().getCommande()) {
			rmd.getErrors().add(DEMANDE_NON_COMMANDE);
			return rmd;
		}

		if ((dto.getIdRefEtat().equals(EtatPointageEnum.APPROUVE.getCodeEtat()) && !titreRepasDemande.getLatestEtatTitreRepasDemande().getEtat().equals(EtatPointageEnum.SAISI) && !titreRepasDemande
				.getLatestEtatTitreRepasDemande().getEtat().equals(EtatPointageEnum.REJETE))
				|| (dto.getIdRefEtat().equals(EtatPointageEnum.REJETE.getCodeEtat()) && !titreRepasDemande.getLatestEtatTitreRepasDemande().getEtat().equals(EtatPointageEnum.SAISI) && !titreRepasDemande
						.getLatestEtatTitreRepasDemande().getEtat().equals(EtatPointageEnum.APPROUVE))) {
			rmd.getErrors()
					.add(String.format(ERROR_ETAT_DEMANDE, EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()).name(), titreRepasDemande.getLatestEtatTitreRepasDemande().getEtat().name()));
			return rmd;
		}

		TitreRepasEtatDemande etat = new TitreRepasEtatDemande();
		etat.setCommande(titreRepasDemande.getLatestEtatTitreRepasDemande().getCommande());
		etat.setDateMaj(new Date());
		etat.setEtat(EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()));
		etat.setIdAgent(idAgentConnecte);
		etat.setCommentaire(dto.getCommentaire());
		etat.setTitreRepasDemande(titreRepasDemande);

		titreRepasDemande.getEtats().add(etat);

		rmd.getInfos().add(ENREGISTREMENT_OK);
		return rmd;
	}

	/**
	 * RG : - saisie possible entre le 1 et le 10 de chaque mois pour le mois en
	 * cours - possible si l'agent a au - 1 jour de présence sur le mois
	 * précédent : en activité (PA) + pas d absence - exclure les agents qui ont
	 * au moins une prime panier sur leur affectation - exclure les agents de la
	 * filière incendie (dans le grade générique de la carrière m-1) (si 2
	 * carrières à cheval sur le mois m-1, on prend la dernière saisie)
	 *
	 * @return ReturnMessageDto
	 */
	@Override
	public ReturnMessageDto checkDroitATitreRepas(ReturnMessageDto rmd, Integer idAgent, Date dateMonthEnCours, List<DemandeDto> listAbsences, RefTypeSaisiCongeAnnuelDto baseCongeAgent,
			List<JourDto> listJoursFeries, AffectationDto affectation) {

		Date dateMoisPrecedent = new DateTime(dateMonthEnCours).minusMonths(1).toDate();

		rmd = checkUnJourDePresenceSurLeMoisPrecedent(rmd, idAgent, dateMoisPrecedent, listAbsences, baseCongeAgent, listJoursFeries);
		if (!rmd.getErrors().isEmpty())
			return rmd;

		if (checkPrimePanierSurAffectation(affectation, idAgent)) {
			rmd.getErrors().add(PRIME_PANIER);
			return rmd;
		}

		if (checkAgentIsFiliereIncendie(idAgent, dateMoisPrecedent)) {
			rmd.getErrors().add(FILIERE_INCENDIE);
			return rmd;
		}

		return rmd;
	}

	/**
	 * On verifie si l agent a le droit au prime panier ou fait parti de la
	 * filiere Incendie.
	 * 
	 * Si oui, il n a pas le droit au Titre Repas.
	 * 
	 * @param idAgent
	 *            Integer
	 * @return boolean
	 */
	@Override
	public boolean checkPrimePanierEtFiliereIncendie(Integer idAgent) {

		Date dateMoisPrecedent = new DateTime(helperService.getCurrentDate()).minusMonths(1).toDate();

		Date dateDebutMois = helperService.getDatePremierJourOfMonth(new Date());
		Date dateFinMois = helperService.getDateDernierJourOfMonth(new Date());
		List<AffectationDto> listAffectation = sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(Arrays.asList(idAgent), dateDebutMois, dateFinMois);
		AffectationDto affectation = getDernierAffectationByAgent(idAgent, listAffectation);

		if (checkPrimePanierSurAffectation(affectation, idAgent)) {
			return true;
		}

		if (checkAgentIsFiliereIncendie(idAgent, dateMoisPrecedent)) {
			return true;
		}

		return false;
	}

	protected ReturnMessageDto checkDataTitreRepasDemandeDto(ReturnMessageDto rmd, TitreRepasDemandeDto dto) {

		if (null == dto) {
			rmd.getErrors().add(DTO_NULL);
			return rmd;
		}

		if (null == dto.getDateMonth()) {
			rmd.getErrors().add(MOIS_COURS_NON_SAISI);
		}

		if (null == dto.getAgent() || null == dto.getAgent().getIdAgent()) {
			rmd.getErrors().add(AGENT_NON_SAISI);
		}

		if (null == dto.getIdRefEtat()) {
			rmd.getErrors().add(String.format(ETAT_NON_SAISI, dto.getAgent().getIdAgent()));
		}

		return rmd;
	}

	/**
	 * saisie possible entre le 1 et le 10 de chaque mois pour le mois en cours
	 */
	protected ReturnMessageDto checkDateJourBetween1And10ofMonth(ReturnMessageDto rmd) {

		DateTime dateJour = new DateTime(helperService.getCurrentDate());
		if (dateJour.getDayOfMonth() > 10) {
			rmd.getErrors().add(DATE_SAISIE_NON_COMPRISE_ENTRE_1_ET_10_DU_MOIS);
		}

		return rmd;
	}

	protected ReturnMessageDto checkDateJourBetween1OfMonthAndGeneration(ReturnMessageDto rmd) {

		DateTime dateJour = new DateTime(helperService.getCurrentDate());

		List<TitreRepasEtatPayeur> listEtatPayeur = titreRepasRepository.getListTitreRepasEtatPayeur();
		if (null != listEtatPayeur && !listEtatPayeur.isEmpty()) {
			TitreRepasEtatPayeur lastEtatPayeur = listEtatPayeur.get(0);

			DateTime dateEtatPayeur = new DateTime(lastEtatPayeur.getDateEtatPayeur());
			if (dateEtatPayeur.isBefore(dateJour) && dateEtatPayeur.getDayOfMonth() < dateJour.getDayOfMonth()) {

				rmd.getErrors().add(DATE_SAISIE_NON_COMPRISE_ENTRE_1_ET_EDITION_PAYEUR);
			}
		}
		return rmd;
	}

	protected boolean checkAgentIsFiliereIncendie(Integer idAgent, Date dateMoisPrecedent) {

		boolean result = false;

		Integer noMatr = helperService.getMairieMatrFromIdAgent(idAgent);
		Date fromDate = helperService.getDatePremierJourOfMonth(dateMoisPrecedent);
		Date toDate = helperService.getDateDernierJourOfMonth(dateMoisPrecedent);

		String codeFiliere = mairieRepository.getDerniereFiliereOfAgentOnPeriod(noMatr, fromDate, toDate);

		if (null != codeFiliere && CODE_FILIERE_INCENDIE.equals(codeFiliere)) {
			result = true;
		}

		return result;
	}

	/**
	 * On verifie que l agent possede ou non une prime Panier 7704 ou 7713 sur
	 * son affectation
	 * 
	 * @param affectation
	 *            AffectationDto
	 * @param idAgent
	 *            Integer
	 * @return boolean
	 */
	protected boolean checkPrimePanierSurAffectation(AffectationDto affectation, Integer idAgent) {

		boolean result = false;
		if (null != affectation && affectation.getIdAgent().equals(idAgent) && null != affectation.getListPrimesAff()) {

			for (RefPrimeDto prime : affectation.getListPrimesAff()) {
				if (LIST_PRIMES_PANIER.contains(prime.getNumRubrique())) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	/**
	 * saisie possible si l'agent a au - 1 jour de présence sur le mois
	 * précédent : en activité (PA) + pas d absence
	 * 
	 * @param rmd
	 *            ReturnMessageDto
	 * @param idAgent
	 *            Integer
	 * @param dateMonthEnCours
	 *            Date
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkUnJourDePresenceSurLeMoisPrecedent(ReturnMessageDto rmd, Integer idAgent, Date dateMoisPrecedent, List<DemandeDto> listAbsences,
			RefTypeSaisiCongeAnnuelDto baseCongeAgent, List<JourDto> listJoursFeries) {

		// 1. on check la PA
		if (!checkPAUnJourActiviteMinimumsurMoisPrecedent(idAgent, dateMoisPrecedent)) {
			rmd.getErrors().add(String.format(AUCUNE_PA_ACTIVE_MOIS_PRECEDENT, idAgent));
			return rmd;
		}

		// 2. base conge doit etre renseigne
		if (null == baseCongeAgent) {
			rmd.getErrors().add(String.format(AUCUNE_BASE_CONGE, idAgent));
			return rmd;
		}

		// 3. on check toutes les absences (meme MALADIES (AS400)) sauf ASA
		if (!checkUnJourSansAbsenceSurLeMois(listAbsences, idAgent, dateMoisPrecedent, baseCongeAgent, listJoursFeries)) {
			rmd.getErrors().add(String.format(AUCUNE_PA_ACTIVE_MOIS_PRECEDENT, idAgent));
			return rmd;
		}

		return rmd;
	}

	/**
	 * On verifie que l agent a travaille au moins un jour sur le mois passe en
	 * parametre
	 * 
	 * Pour cela on boucle sur tous les jours du mois et on s arrete des qu on
	 * trouve un jour sans maladie et sans conge
	 * 
	 * Attention au personne travaillant le weekend ou non :
	 * RefTypeSaisiCongeAnnuelDto.isDecompteSamedi() => ne travaille pas le
	 * weekend et jour ferie
	 * 
	 * @param listAbences
	 *            List<DemandeDto> provenant du projet SIRH-ABS-WS
	 * @param idAgent
	 *            Integer
	 * @param dateMoisPrecedent
	 *            Date
	 * @param baseCongeAgent
	 *            RefTypeSaisiCongeAnnuelDto
	 * @param listJoursFeries
	 *            List<JourDto>
	 * @return boolean
	 */
	protected boolean checkUnJourSansAbsenceSurLeMois(List<DemandeDto> listAbsences, Integer idAgent, Date dateMoisPrecedent, RefTypeSaisiCongeAnnuelDto baseCongeAgent, List<JourDto> listJoursFeries) {

		// si pas d absence, donc result = TRUE
		boolean result = true;

		DateTime startDate = new DateTime(helperService.getDatePremierJourOfMonth(dateMoisPrecedent));
		DateTime endDate = new DateTime(helperService.getDateDernierJourOfMonth(dateMoisPrecedent));

		List<Spabsen> listSpAbsen = mairieRepository.getListMaladieBetween(idAgent, startDate.toDate(), endDate.toDate());

		if (null != listAbsences && !listAbsences.isEmpty()) {
			// on passe a false avant test
			result = false;

			// on boucle sur tous les jours du mois
			while ((startDate.isBefore(endDate) || startDate.equals(endDate)) && !result) {

				// on verifie si on check les samedi et dimanche
				// on regarde la base conge de l agent
				// si la base conge decompte le samedi
				// alors c est que l agent ne travaille pas le weekend
				if (!(baseCongeAgent.isDecompteSamedi() && (startDate.getDayOfWeek() == DateTimeConstants.SATURDAY || startDate.getDayOfWeek() == DateTimeConstants.SUNDAY || helperService
						.isJourHoliday(listJoursFeries, startDate.toDate())))) {

					boolean isAuMoinsUnCongeSurLaJournee = false;
					// on boucle sur les conges
					for (DemandeDto demandeDto : listAbsences) {
						if (demandeDto.getAgentWithServiceDto().getIdAgent().equals(idAgent)) {
							// on ne prend pas en compte les ASA
							if (!demandeDto.getGroupeAbsence().getIdRefGroupeAbsence().equals(RefTypeGroupeAbsenceEnum.AS.getValue())) {

								if ((demandeDto.getDateDebut().before(startDate.toDate()) || demandeDto.getDateDebut().equals(startDate.toDate()))
										&& (demandeDto.getDateFin().after(startDate.toDate()) || demandeDto.getDateFin().equals(startDate.toDate()))) {
									isAuMoinsUnCongeSurLaJournee = true;
									break;
								}
							}
						}
					}

					if (null != listSpAbsen) {
						for (Spabsen spabsen : listSpAbsen) {
							Date startDateSpAbsen = helperService.getDateFromMairieInteger(spabsen.getId().getDatdeb());
							Date endDateSpAbsen = helperService.getDateFromMairieInteger(spabsen.getDatfin());

							if ((startDateSpAbsen.before(startDate.toDate()) || startDateSpAbsen.equals(startDate.toDate()))
									&& (endDateSpAbsen.after(startDate.toDate()) || endDateSpAbsen.equals(startDate.toDate()))) {
								isAuMoinsUnCongeSurLaJournee = true;
								break;
							}
						}
					}

					// le resultat est que soit
					// soit on n a pas trouve de conge sur ce jour "startDate"
					// soit on n a pas trouve de conge hors ASA donc l agent a
					// bien eu une activite de syndicat
					result = !isAuMoinsUnCongeSurLaJournee;
				}
				startDate = startDate.plusDays(1);
			}
		}

		return result;
	}

	/**
	 * On verifie que l agent est au moins une PA en activite
	 * 
	 * @param idAgent
	 *            Integer
	 * @param dateMoisPrecedent
	 *            Date
	 * @return boolean
	 */
	protected boolean checkPAUnJourActiviteMinimumsurMoisPrecedent(Integer idAgent, Date dateMoisPrecedent) {

		Integer noMatr = helperService.getMairieMatrFromIdAgent(idAgent);
		Date fromDate = helperService.getDatePremierJourOfMonth(dateMoisPrecedent);
		Date toDate = helperService.getDateDernierJourOfMonth(dateMoisPrecedent);

		List<Spadmn> listSpAdmn = mairieRepository.getListPAOfAgentBetween2Date(noMatr, fromDate, toDate);

		boolean result = false;

		if (null != listSpAdmn) {
			for (Spadmn pa : listSpAdmn) {
				if (PointageDataConsistencyRules.ACTIVITE_CODES.contains(pa.getCdpadm())) {
					result = true;
					break;
				}
			}
		}

		return result;
	}

	private List<RefTypeSaisiCongeAnnuelDto> getListBasesConges() {

		List<RefTypeSaisiCongeAnnuelDto> result = new ArrayList<RefTypeSaisiCongeAnnuelDto>();

		List<RefTypeAbsenceDto> listTypeAbsence = absWsConsumer.getListeTypAbsenceCongeAnnuel();

		if (null != listTypeAbsence) {
			for (RefTypeAbsenceDto typeAbsence : listTypeAbsence) {
				result.add(typeAbsence.getTypeSaisiCongeAnnuelDto());
			}
		}
		return result;
	}

	private RefTypeSaisiCongeAnnuelDto getRefTypeSaisiCongeAnnuelDto(List<RefTypeSaisiCongeAnnuelDto> listRefBaseConge, RefTypeSaisiCongeAnnuelDto baseCongeAgent) {

		RefTypeSaisiCongeAnnuelDto result = null;
		if (null != listRefBaseConge && null != baseCongeAgent) {
			for (RefTypeSaisiCongeAnnuelDto ref : listRefBaseConge) {
				if (ref.getIdRefTypeSaisiCongeAnnuel().equals(baseCongeAgent.getIdRefTypeSaisiCongeAnnuel())) {
					result = ref;
					break;
				}
			}
		}

		return result;
	}

	private AffectationDto getDernierAffectationByAgent(Integer idAgent, List<AffectationDto> listAffectation) {

		AffectationDto result = null;

		if (null != listAffectation) {
			for (AffectationDto affDto : listAffectation) {
				if (affDto.getIdAgent().equals(idAgent)) {
					if (null == result || result.getDateDebut().before(affDto.getDateDebut())) {
						result = affDto;
					}
				}
			}
		}

		return result;
	}

	private List<DemandeDto> getListeAbsencesByAgent(List<DemandeDto> listAbsences, Integer idAgent) {

		List<DemandeDto> result = new ArrayList<DemandeDto>();

		if (null != listAbsences) {
			for (DemandeDto demande : listAbsences) {
				if (null != demande.getAgentWithServiceDto() && null != demande.getAgentWithServiceDto().getIdAgent() && demande.getAgentWithServiceDto().getIdAgent().equals(idAgent)) {
					result.add(demande);
				}
			}
		}

		return result;
	}

	@Override
	@Transactional("ptgTransactionManager")
	public ReturnMessageDto genereEtatPayeur(Integer idAgentConnecte) {

		ReturnMessageDto result = new ReturnMessageDto();

		// pre requis : verifie les droits
		ReturnMessageDto messageSIRH = sirhWsConsumer.isUtilisateurSIRH(idAgentConnecte);
		if (!messageSIRH.getErrors().isEmpty()) {
			result.getErrors().add(ERREUR_DROIT_AGENT);
			return result;
		}

		// 1. on verifie si une paye est en cours
		if (paieWorkflowService.isCalculSalaireEnCours()) {
			result.getErrors().add(PAIE_EN_COURS);
			return result;
		}

		// 2. on recupere la liste des demandes de Titre Repas de ce mois-ci
		List<TitreRepasDemande> listDemandeTR = titreRepasRepository.getListTitreRepasDemande(null, null, null, EtatPointageEnum.APPROUVE.getCodeEtat(), true,
				helperService.getDatePremierJourOfMonth(helperService.getCurrentDate()));

		List<Integer> listIdsAgent = new ArrayList<Integer>();
		if (null != listDemandeTR) {
			for (TitreRepasDemande demandeTR : listDemandeTR) {
				if (!listIdsAgent.contains(demandeTR.getIdAgent()))
					listIdsAgent.add(demandeTR.getIdAgent());
			}
		}

		Date dateJour = helperService.getCurrentDate();

		List<AgentWithServiceDto> listAgentServiceDto = sirhWsConsumer.getListAgentsWithService(listIdsAgent, dateJour);

		List<TitreRepasDemandeDto> listTitreRepasDemandeDto = new ArrayList<TitreRepasDemandeDto>();
		for (TitreRepasDemande TR : listDemandeTR) {
			AgentWithServiceDto agDtoServ = sirhWSUtils.getAgentOfListAgentWithServiceDto(listAgentServiceDto, TR.getIdAgent());
			TitreRepasDemandeDto dto = new TitreRepasDemandeDto(TR, agDtoServ);
			listTitreRepasDemandeDto.add(dto);
		}

		// 3. on cree/recupere l état payeur de ce mois-ci
		TitreRepasEtatPayeur etatPayeurTR = new TitreRepasEtatPayeur();
		etatPayeurTR.setFichier(String.format("Etat-Payeur-Titre-Repas-%s.pdf", sfd.format(dateJour)));
		etatPayeurTR.setLabel(String.format("%s", sfd.format(dateJour)));
		etatPayeurTR.setDateEtatPayeur(new LocalDate(dateJour).withDayOfMonth(1).toDate());
		etatPayeurTR.setIdAgent(idAgentConnecte);
		etatPayeurTR.setDateEdition(dateJour);

		// 4. generer le fichier d'état du payeur des TR
		try {
			reportingService.downloadEtatPayeurTitreRepas(etatPayeurTR, listTitreRepasDemandeDto);
		} catch (MalformedURLException e) {
			logger.debug(e.getMessage());
			result.getErrors().add("Une erreur est survenue lors de la génération de l'état payeur des titres repas.");
			return result;
		} catch (DocumentException e) {
			logger.debug(e.getMessage());
			result.getErrors().add("Une erreur est survenue lors de la génération de l'état payeur des titres repas.");
			return result;
		} catch (IOException e) {
			logger.debug(e.getMessage());
			result.getErrors().add("Une erreur est survenue lors de la génération de l'état payeur des titres repas.");
			return result;
		}

		// 5. generer une charge dans l AS400

		// 6. passer les demandes a l etat JOURNALISE
		if (null != listDemandeTR) {
			for (TitreRepasDemande demandeTR : listDemandeTR) {
				TitreRepasEtatDemande etat = new TitreRepasEtatDemande();
				etat.setCommande(demandeTR.getCommande());
				etat.setDateMaj(new Date());
				etat.setEtat(EtatPointageEnum.JOURNALISE);
				etat.setIdAgent(idAgentConnecte);
				etat.setTitreRepasDemande(demandeTR);

				demandeTR.getEtats().add(etat);
			}
		}

		// 7. on enregistre
		titreRepasRepository.persist(etatPayeurTR);

		result.getInfos().add(GENERATION_ETAT_PAYEUR_OK);

		return result;
	}

	/**
	 * Retourne la liste des états possible pour une demande de Titre Repas.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<RefEtatDto> getListRefEtats() {

		List<RefEtatDto> result = new ArrayList<RefEtatDto>();

		List<RefEtat> refEtats = pointageRepository.findAllRefEtats();

		for (RefEtat etat : refEtats) {
			if (etat.getIdRefEtat().equals(EtatPointageEnum.SAISI.getCodeEtat()) || etat.getIdRefEtat().equals(EtatPointageEnum.APPROUVE.getCodeEtat())
					|| etat.getIdRefEtat().equals(EtatPointageEnum.REJETE.getCodeEtat()) || etat.getIdRefEtat().equals(EtatPointageEnum.JOURNALISE.getCodeEtat())) {
				RefEtatDto dto = new RefEtatDto(etat);
				result.add(dto);
			}
		}

		return result;
	}

	@Override
	public List<TitreRepasDemandeDto> getTitreRepasArchives(Integer idTrDemande) {

		List<TitreRepasDemandeDto> result = new ArrayList<TitreRepasDemandeDto>();
		TitreRepasDemande titre = titreRepasRepository.getTitreRepasDemandeById(idTrDemande);

		// optimisation performances
		Map<Integer, AgentWithServiceDto> mapAgentDto = new HashMap<Integer, AgentWithServiceDto>();

		for (TitreRepasEtatDemande etat : titre.getEtats()) {

			AgentWithServiceDto agDto = null;
			if (mapAgentDto.containsKey(titre.getIdAgent())) {
				agDto = mapAgentDto.get(titre.getIdAgent());
			} else {
				agDto = sirhWsConsumer.getAgentService(titre.getIdAgent(), new Date());
				mapAgentDto.put(titre.getIdAgent(), agDto);
			}

			TitreRepasDemandeDto dto = new TitreRepasDemandeDto(titre, agDto);

			AgentWithServiceDto opeDto = null;
			if (mapAgentDto.containsKey(etat.getIdAgent())) {
				opeDto = mapAgentDto.get(etat.getIdAgent());
			} else {
				opeDto = sirhWsConsumer.getAgentService(etat.getIdAgent(), new Date());
				mapAgentDto.put(etat.getIdAgent(), opeDto);
			}

			dto.updateEtat(etat, opeDto);
			dto.setAgent(agDto);
			result.add(dto);
		}

		return result;
	}

}
