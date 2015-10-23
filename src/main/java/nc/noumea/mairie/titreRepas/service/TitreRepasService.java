package nc.noumea.mairie.titreRepas.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefTypeAbsenceDto;
import nc.noumea.mairie.abs.dto.RefTypeGroupeAbsenceEnum;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatDemande;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.ptg.service.impl.PointageDataConsistencyRules;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AffectationDto;
import nc.noumea.mairie.sirh.dto.JourDto;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasDemandeDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasEtatPayeurDto;
import nc.noumea.mairie.titreRepas.repository.ITitreRepasRepository;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;

public class TitreRepasService implements ITitreRepasService {

	@Autowired
	private HelperService helperService;
	
	@Autowired
	private IMairieRepository mairieRepository;
	
	@Autowired
	private ITitreRepasRepository titreRepasRepository;
	
	@Autowired
	private IAbsWsConsumer absWsConsumer;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;
	
	public static final String ERREUR_DROIT_AGENT = "Vous n'avez pas les droits de traiter cette demande de Titre Repas.";
	public static final String DATE_SAISIE_NON_COMPRISE_ENTRE_1_ET_10_DU_MOIS = "Vous ne pouvez commander les Titres Repas qu'entre le 1 et 10 de chaque mois.";
	public static final String AUCUNE_PA_ACTIVE_MOIS_PRECEDENT = "L'agent %s n'a pas travaillé le mois précédent.";
	public static final String AUCUNE_BASE_CONGE = "La base congé n'est pas renseignée pour l'agent %s.";
	public static final String PRIME_PANIER = "L'agent a le droit au prime panier et ne peut donc pas commander des Titres Repas.";
	public static final String FILIERE_INCENDIE = "L'agent fait parti de la filière Incendie et ne peut donc pas commander des Titres Repas.";
	
	public static final List<Integer> LIST_PRIMES_PANIER = Arrays.asList(7704, 7713);
	public static final String CODE_FILIERE_INCENDIE = "I";

	public static final String ENREGISTREMENT_OK = "La demande est bien enregistrée.";
	
	/**
	 * Enregistre une liste de demande de Titre Repas depuis le Kiosque RH.
	 * 
	 * @param listTitreRepasDemandeDto List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	@Override
	public ReturnMessageDto enregistreListTitreDemandeFromKiosque(Integer idAgentConnecte,
			List<TitreRepasDemandeDto> listTitreRepasDemandeDto) {
		
		
		
		
		
//		absWsConsumer.getListAbsencesForListAgentsBetween2Dates(listIdsAgent, start, end);
		
		return null;
	}
	
	/**
	 * Enregistre une liste de demande de Titre Repas depuis SIRH.
	 * 
	 * @param listTitreRepasDemandeDto List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	@Override
	public ReturnMessageDto enregistreListTitreDemandeFromSIRH(Integer idAgentConnecte,
			List<TitreRepasDemandeDto> listTitreRepasDemandeDto) {
		// TODO Auto-generated method stub
		
		
		
//		absWsConsumer.getListAbsencesForListAgentsBetween2Dates(listIdsAgent, start, end);
		
		return null;
	}

	/**
	 * Enregistre une demande de Titre Repas pour un agent.
	 * 
	 * Si TitreRepasDemandeDto.idTrDemande == NULL, alors creation
	 * sinon modification.
	 * 
	 * @param titreRepasDemandeDto TitreRepasDemandeDto
	 * @return ReturnMessageDto
	 */
	@Override
	public ReturnMessageDto enregistreTitreDemandeAgent(Integer idAgentConnecte, 
			TitreRepasDemandeDto dto) {
		
		////// Verifie les droits ////////
		if(null == idAgentConnecte
				|| !idAgentConnecte.equals(dto.getIdAgent())){
			ReturnMessageDto rmd = new ReturnMessageDto();
			rmd.getErrors().add(ERREUR_DROIT_AGENT);
			return rmd;
		}
		
		/////////////////////////////////////////////////////////////////
		///////// on recupere toutes les donnees qui nous interessent ///
		Date dateDebutMois = helperService.getDatePremierJourOfMonth(new Date());
		Date dateFinMois = helperService.getDateDernierJourOfMonth(new Date());
		List<Integer> listIdsAgent = Arrays.asList(dto.getIdAgent());
		
		List<JourDto> listJourFerieMoisEnCours = sirhWsConsumer.getListeJoursFeries(dateDebutMois, dateFinMois);
		List<AffectationDto> listAffectation = sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(
				listIdsAgent, dateDebutMois, dateFinMois);
		AffectationDto affectation = getDernierAffectationByAgent(dto.getIdAgent(), listAffectation);
		List<DemandeDto> listAbsences = absWsConsumer.getListAbsencesForListAgentsBetween2Dates(listIdsAgent, dateDebutMois, dateFinMois);
		
		List<RefTypeSaisiCongeAnnuelDto> listBasesConges = getListBasesConges();
		RefTypeSaisiCongeAnnuelDto baseCongeAgent = getRefTypeSaisiCongeAnnuelDto(listBasesConges, affectation.getBaseConge());
		//////////////////////////////////////////////////////////////////
		
		return enregistreTitreDemandeOneByOne(dto, listAbsences, baseCongeAgent, listJourFerieMoisEnCours, affectation);
	}
	
	/**
	 * Enregistre une demande de Titre Repas 
	 * pour un agent pour un mois donne. 
	 * 
	 * Les RG sont verifiees. 
	 * 
	 * Est utilise par l enresgistrement d une demande par :
	 * - l agent directement => enregistreTitreDemandeAgent()
	 * - l operateur ou approbateur => 
	 * - SIRH => 
	 * 
	 * @param dto TitreRepasDemandeDto
	 * @param listAbsences List<DemandeDto>
	 * @param baseCongeAgent RefTypeSaisiCongeAnnuelDto
	 * @param listJoursFeries List<JourDto>
	 * @param affectation AffectationDto
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto enregistreTitreDemandeOneByOne(
			TitreRepasDemandeDto dto, List<DemandeDto> listAbsences,
			RefTypeSaisiCongeAnnuelDto baseCongeAgent, List<JourDto> listJoursFeries,
			AffectationDto affectation) {
		
		ReturnMessageDto result = new ReturnMessageDto();
		
		// on force la date du mois en cours
		dto.setDateMonth(helperService.getDatePremierJourOfMonth(new Date()));
		
		// on verifie les donnees du DTO
		result = checkDataTitreRepasDemandeDto(result, dto);
		if(!result.getErrors().isEmpty())
			return result;
		
		result = checkDroitATitreRepas(result, dto.getIdAgent(), dto.getDateMonth(), 
				listAbsences, baseCongeAgent, listJoursFeries, affectation);
		if(!result.getErrors().isEmpty())
			return result;
		
		// on verifie si une demande existe deja 
		// si TitreRepasDemandeDto.idTrDemande <> NULL
		// alors modification
		TitreRepasDemande trDemande = null;
		if(null != dto.getIdTrDemande()) {
			trDemande = titreRepasRepository.getTitreRepasDemandeById(dto.getIdTrDemande());
			
			if(null == trDemande) {
				result.getErrors().add("La demande de Titre Repas n'existe pas.");
				return result;
			}
		}else{
			// on verifie qu une demande n existe pas
			List<TitreRepasDemande> listTitreRepasDemande = titreRepasRepository.getListTitreRepasDemande(
					Arrays.asList(dto.getIdAgent()), null, null, 
					dto.getIdRefEtat(), null, dto.getDateMonth());
			
			if(null != listTitreRepasDemande
					&& !listTitreRepasDemande.isEmpty()) {
				result.getErrors().add("Une demande de Titre Repas existe déjà pour ce mois-ci pour l'agent " + dto.getIdAgent() + ".");
				return result;
			}
		}
		
		if(null != trDemande) {
			trDemande = new TitreRepasDemande();
			trDemande.setIdAgent(dto.getIdAgent());
			trDemande.setDateMonth(dto.getDateMonth());
		}
		trDemande.setCommande(dto.getCommande());
		trDemande.setCommentaire(dto.getCommentaire());
		
		TitreRepasEtatDemande etat = new TitreRepasEtatDemande();
		etat.setCommande(dto.getCommande());
		etat.setDateMaj(new Date());
		etat.setEtat(EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()));
		etat.setIdAgent(dto.getIdAgent());
		etat.setTitreRepasDemande(trDemande);
		
		trDemande.getEtats().add(etat);
		
		titreRepasRepository.persist(trDemande);
		
		result.getInfos().add(ENREGISTREMENT_OK);
		
		return result;
	}

	@Override
	public List<TitreRepasDemandeDto> getListTitreRepasDemandeDto(
			Integer idAgentConnecte, List<Integer> listIdsAgent, Date fromDate,
			Date toDate, Integer etat, boolean commande, Date dateMonth) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TitreRepasEtatPayeurDto> getListTitreRepasEtatPayeurDto() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReturnMessageDto updateEtatForListTitreRepasDemande(
			Integer idAgentConnecte,
			List<TitreRepasDemandeDto> listTitreRepasDemandeDto) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * RG :
     * - saisie possible entre le 1 et le 10 de chaque mois pour le mois en cours
     * - possible si l'agent a au - 1 jour de présence sur le mois précédent : en activité (PA) + pas d absence
     * - exclure les agents qui ont au moins une prime panier sur leur affectation
     * - exclure les agents de la filière incendie (dans le grade générique de la carrière m-1) 
     * (si 2 carrières à cheval sur le mois m-1, on prend la dernière saisie) 
	 *
	 * @return ReturnMessageDto
	 */
	@Override
	public ReturnMessageDto checkDroitATitreRepas(ReturnMessageDto rmd, Integer idAgent, 
			Date dateMonthEnCours, List<DemandeDto> listAbsences,
			RefTypeSaisiCongeAnnuelDto baseCongeAgent, List<JourDto> listJoursFeries, AffectationDto affectation) {
		
		rmd = checkDateJourBetween1And10ofMonth(rmd);
		if(!rmd.getErrors().isEmpty())
			return rmd;
		
		Date dateMoisPrecedent = new DateTime(dateMonthEnCours).minusMonths(1).toDate();
		
		rmd = checkUnJourDePresenceSurLeMoisPrecedent(
				rmd, idAgent, dateMoisPrecedent, listAbsences, baseCongeAgent, listJoursFeries);
		if(!rmd.getErrors().isEmpty())
			return rmd;
		
		if(checkPrimePanierSurAffectation(affectation, idAgent)) {
			rmd.getErrors().add(PRIME_PANIER);
			return rmd;
		}
		
		if(checkAgentIsFiliereIncendie(idAgent, dateMoisPrecedent)) {
			rmd.getErrors().add(FILIERE_INCENDIE);
			return rmd;
		}
		
		return rmd;
	}
	
	/**
	 * On verifie si l agent a le droit au prime panier
	 * ou fait parti de la filiere Incendie.
	 * 
	 * Si oui, il n a pas le droit au Titre Repas.
	 * 
	 * @param rmd ReturnMessageDto
	 * @param idAgent Integer
	 * @param affectation AffectationDto
	 * @param dateMonthEnCours Date
	 * @return ReturnMessageDto
	 */
	@Override
	public ReturnMessageDto checkPrimePanierEtFiliereIncendie(ReturnMessageDto rmd, Integer idAgent, 
			AffectationDto affectation, Date dateMonthEnCours) {
		
		Date dateMoisPrecedent = new DateTime(dateMonthEnCours).minusMonths(1).toDate();
		
		if(checkPrimePanierSurAffectation(affectation, idAgent)) {
			rmd.getErrors().add(PRIME_PANIER);
			return rmd;
		}
		
		if(checkAgentIsFiliereIncendie(idAgent, dateMoisPrecedent)) {
			rmd.getErrors().add(FILIERE_INCENDIE);
			return rmd;
		}
		
		return rmd;
	}
	
	protected ReturnMessageDto checkDataTitreRepasDemandeDto(
			ReturnMessageDto rmd, TitreRepasDemandeDto dto){
		
		if(null == dto) {
			rmd.getErrors().add("Merci de saisir la demande de Titre Repas.");
		}
		
		if(null == dto.getDateMonth()){
			rmd.getErrors().add("Le mois en cours de la demande n'est pas saisi.");
		}
		
		if(null == dto.getIdAgent()){
			rmd.getErrors().add("L'ID agent n'est pas renseigné.");
		}
		if(null == dto.getIdRefEtat()){
			rmd.getErrors().add("L'état de la demande de Titre Repas n'est pas renseigné pour l'agent : " + dto.getIdAgent() + ".");
		}
		
		return rmd;
	}
	
	/**
	 * saisie possible entre le 1 et le 10 de chaque mois pour le mois en cours
	 */
	protected ReturnMessageDto checkDateJourBetween1And10ofMonth(ReturnMessageDto rmd) {
		
		DateTime dateJour = new DateTime(helperService.getCurrentDate());
		if(dateJour.getDayOfMonth() > 10) {
			rmd.getErrors().add(DATE_SAISIE_NON_COMPRISE_ENTRE_1_ET_10_DU_MOIS);
		}
		
		return rmd;
	}
	
	protected boolean checkAgentIsFiliereIncendie(Integer idAgent, Date dateMoisPrecedent) {
		
		boolean result = false;
		
		Integer noMatr = helperService.getMairieMatrFromIdAgent(idAgent);
		Date fromDate = helperService.getDatePremierJourOfMonth(dateMoisPrecedent);
		Date toDate = helperService.getDateDernierJourOfMonth(dateMoisPrecedent);
		
		String codeFiliere = mairieRepository.getDerniereFiliereOfAgentOnPeriod(noMatr, fromDate, toDate);
		
		if(null != codeFiliere
				&& CODE_FILIERE_INCENDIE.equals(codeFiliere)) {
			result = true;
		}
		
		return result;
	}
	
	/**
	 * On verifie que l agent possede ou non une prime Panier 7704 ou 7713
	 * sur son affectation
	 * 
	 * @param affectation AffectationDto
	 * @param idAgent Integer
	 * @return boolean
	 */ 
	protected boolean checkPrimePanierSurAffectation(AffectationDto affectation, Integer idAgent) {
		
		boolean result = false;
		if(null != affectation
				&& affectation.getIdAgent().equals(idAgent)
				&& null != affectation.getListPrimesAff()){
			
			for(RefPrimeDto prime : affectation.getListPrimesAff()) {
				if(LIST_PRIMES_PANIER.contains(prime.getNumRubrique())) {
					result = true;
					break;
				}
			}
		}
		return result;
	}
	
	/**
	 * saisie possible si l'agent a au - 1 jour de présence sur le mois précédent : en activité (PA) + pas d absence
	 * 
	 * @param rmd ReturnMessageDto
	 * @param idAgent Integer
	 * @param dateMonthEnCours Date
	 * @return ReturnMessageDto
	 */
	protected ReturnMessageDto checkUnJourDePresenceSurLeMoisPrecedent(ReturnMessageDto rmd, Integer idAgent, Date dateMoisPrecedent,
			List<DemandeDto> listAbsences, RefTypeSaisiCongeAnnuelDto baseCongeAgent,
			List<JourDto> listJoursFeries) {

		// 1. on check la PA
		if(!checkPAUnJourActiviteMinimumsurMoisPrecedent(idAgent, dateMoisPrecedent)){
			rmd.getErrors().add(String.format(AUCUNE_PA_ACTIVE_MOIS_PRECEDENT, idAgent));
			return rmd;
		}
		
		// 2. base conge doit etre renseigne
		if(null == baseCongeAgent) {
			rmd.getErrors().add(String.format(AUCUNE_BASE_CONGE, idAgent));
			return rmd;
		}
		
		// 3. on check toutes les absences (meme MALADIES (AS400)) sauf ASA
		if(!checkUnJourSansAbsenceSurLeMois(listAbsences, idAgent, dateMoisPrecedent, baseCongeAgent, listJoursFeries)){
			rmd.getErrors().add(String.format(AUCUNE_PA_ACTIVE_MOIS_PRECEDENT, idAgent));
			return rmd;
		}
		
		return rmd;
	}
	
	/**
	 * On verifie que l agent a travaille au moins un jour sur le mois passe en parametre
	 * 
	 * Pour cela on boucle sur tous les jours du mois et on s arrete des
	 * qu on trouve un jour sans maladie et sans conge
	 * 
	 * Attention au personne travaillant le weekend ou non :
	 * RefTypeSaisiCongeAnnuelDto.isDecompteSamedi() => ne travaille pas le weekend et jour ferie
	 * 
	 * @param listAbences List<DemandeDto> provenant du projet SIRH-ABS-WS
	 * @param idAgent Integer
	 * @param dateMoisPrecedent Date
	 * @param baseCongeAgent RefTypeSaisiCongeAnnuelDto 
	 * @param listJoursFeries List<JourDto>
	 * @return boolean
	 */
	protected boolean checkUnJourSansAbsenceSurLeMois(
			List<DemandeDto> listAbsences, 
			Integer idAgent, 
			Date dateMoisPrecedent, 
			RefTypeSaisiCongeAnnuelDto baseCongeAgent,
			List<JourDto> listJoursFeries) {
		
		// si pas d absence, donc result = TRUE
		boolean result = true;
		
		DateTime startDate = new DateTime(helperService.getDatePremierJourOfMonth(dateMoisPrecedent));
		DateTime endDate = new DateTime(helperService.getDateDernierJourOfMonth(dateMoisPrecedent));
		
		List<Spabsen> listSpAbsen = mairieRepository.getListMaladieBetween(
				idAgent, startDate.toDate(), endDate.toDate());
		
		if(null != listAbsences
				&& !listAbsences.isEmpty()) {
			// on passe a false avant test
			result = false;
			
			// on boucle sur tous les jours du mois
			while ((startDate.isBefore(endDate) || startDate.equals(endDate))
					&& !result) {
				
				// on verifie si on check les samedi et dimanche
				// on regarde la base conge de l agent
				// si la base conge decompte le samedi
				// alors c est que l agent ne travaille pas le weekend
				if( !(baseCongeAgent.isDecompteSamedi()
						&& (startDate.getDayOfWeek() == DateTimeConstants.SATURDAY
							|| startDate.getDayOfWeek() == DateTimeConstants.SUNDAY
							|| helperService.isJourHoliday(listJoursFeries, startDate.toDate()))) ) {
					
					boolean isAuMoinsUnCongeSurLaJournee = false;
					// on boucle sur les conges
					for(DemandeDto demandeDto : listAbsences) {
						if(demandeDto.getAgentWithServiceDto().getIdAgent().equals(idAgent)){
							// on ne prend pas en compte les ASA
							if(!demandeDto.getGroupeAbsence().getIdRefGroupeAbsence().equals(RefTypeGroupeAbsenceEnum.AS.getValue())) {
								
								if( (demandeDto.getDateDebut().before(startDate.toDate()) || demandeDto.getDateDebut().equals(startDate.toDate())) 
										&& (demandeDto.getDateFin().after(startDate.toDate()) || demandeDto.getDateFin().equals(startDate.toDate()))
									) {
									isAuMoinsUnCongeSurLaJournee = true;
									break;
								}
							}
						}
					}
					
					if(null != listSpAbsen) {
						for (Spabsen spabsen : listSpAbsen) {
							Date startDateSpAbsen = helperService.getDateFromMairieInteger(spabsen.getId()
									.getDatdeb());
							Date endDateSpAbsen = helperService.getDateFromMairieInteger(spabsen.getDatfin());
							
							if( (startDateSpAbsen.before(startDate.toDate()) || startDateSpAbsen.equals(startDate.toDate())) 
									&& (endDateSpAbsen.after(startDate.toDate()) || endDateSpAbsen.equals(startDate.toDate()))
								) {
								isAuMoinsUnCongeSurLaJournee = true;
								break;
							}
						}
					}
					
					// le resultat est que soit 
					// soit on n a pas trouve de conge sur ce jour "startDate"
					// soit on n a pas trouve de conge hors ASA donc l agent a bien eu une activite de syndicat
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
	 * @param idAgent Integer
	 * @param dateMoisPrecedent Date
	 * @return boolean
	 */
	protected boolean checkPAUnJourActiviteMinimumsurMoisPrecedent(Integer idAgent, Date dateMoisPrecedent) {
		
		Integer noMatr = helperService.getMairieMatrFromIdAgent(idAgent);
		Date fromDate = helperService.getDatePremierJourOfMonth(dateMoisPrecedent);
		Date toDate = helperService.getDateDernierJourOfMonth(dateMoisPrecedent);
		
		List<Spadmn> listSpAdmn = mairieRepository.getListPAOfAgentBetween2Date(noMatr, fromDate, toDate);
		
		boolean result = false;
		
		if(null != listSpAdmn) {
			for(Spadmn pa : listSpAdmn) {
				if(PointageDataConsistencyRules.ACTIVITE_CODES.contains(pa.getCdpadm())) {
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
		
		if(null != listTypeAbsence) {
			for(RefTypeAbsenceDto typeAbsence : listTypeAbsence) {
				result.add(typeAbsence.getTypeSaisiCongeAnnuelDto());
			}
		}
		return result;
	}
	
	
	private RefTypeSaisiCongeAnnuelDto getRefTypeSaisiCongeAnnuelDto(List<RefTypeSaisiCongeAnnuelDto> listRefBaseConge, RefTypeSaisiCongeAnnuelDto baseCongeAgent) {
		
		RefTypeSaisiCongeAnnuelDto result = null;
		if(null != listRefBaseConge
				&& null != baseCongeAgent) {
			for(RefTypeSaisiCongeAnnuelDto ref : listRefBaseConge) {
				if(ref.getIdRefTypeSaisiCongeAnnuel().equals(baseCongeAgent.getIdRefTypeSaisiCongeAnnuel())) {
					result = ref;
					break;
				}
			}
		}
		
		return result;
	}
	
	private AffectationDto getDernierAffectationByAgent(Integer idAgent, List<AffectationDto> listAffectation) {
		
		AffectationDto result = null;
		
		if(null != listAffectation) {
			for(AffectationDto affDto : listAffectation) {
				if(affDto.getIdAgent().equals(idAgent)) {
					if(null == result
							|| result.getDateDebut().before(affDto.getDateDebut())) {
						result = affDto;
					}
				}
			}
		}
		
		return result;
	}
	

}
