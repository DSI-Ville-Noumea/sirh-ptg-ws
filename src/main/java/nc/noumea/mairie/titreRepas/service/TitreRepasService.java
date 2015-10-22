package nc.noumea.mairie.titreRepas.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefTypeGroupeAbsenceEnum;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.Spadmn;
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
	private IAbsWsConsumer absWsConsumer;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;
	
	public static final String DATE_SAISIE_NON_COMPRISE_ENTRE_1_ET_10_DU_MOIS = "Vous ne pouvez commander les Titres Repas qu'entre le 1 et 10 de chaque mois.";
	public static final String AUCUNE_PA_ACTIVE_MOIS_PRECEDENT = "L'agent n'a pas travaillé le mois précédent.";
	public static final String PRIME_PANIER = "L'agent a le droit au prime panier et ne peut donc pas commander des Titres Repas.";
	public static final String FILIERE_INCENDIE = "L'agent fait parti de la filière Incendie et ne peut donc pas commander des Titres Repas.";
	
	public static final List<Integer> LIST_PRIMES_PANIER = Arrays.asList(7704, 7713);
	public static final String CODE_FILIERE_INCENDIE = "I";
	
	@Override
	public ReturnMessageDto enregistreListTitreDemande(
			List<TitreRepasDemandeDto> listTitreRepasDemandeDto) {
		// TODO Auto-generated method stub
		
		
		
//		absWsConsumer.getListAbsencesForListAgentsBetween2Dates(listIdsAgent, start, end);
		
		return null;
	}

	@Override
	public ReturnMessageDto enregistreTitreDemandeAgent(
			TitreRepasDemandeDto titreRepasDemandeDto) {
		// TODO Auto-generated method stub
		
//		absWsConsumer.getListAbsencesForListAgentsBetween2Dates(listIdsAgent, start, end);
		
		return null;
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
			Date dateMonthEnCours, List<DemandeDto> listAbences,
			RefTypeSaisiCongeAnnuelDto baseCongeAgent, List<JourDto> listJoursFeries, AffectationDto affectation) {
		
		rmd = checkDateJourBetween1And10ofMonth(rmd);
		if(!rmd.getErrors().isEmpty())
			return rmd;
		

		Date dateMoisPrecedent = new DateTime(dateMonthEnCours).minusMonths(1).toDate();
		
		rmd = checkUnJourDePresenceSurLeMoisPrecedent(
				rmd, idAgent, dateMoisPrecedent, listAbences, baseCongeAgent, listJoursFeries);
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
			List<DemandeDto> listAbences, RefTypeSaisiCongeAnnuelDto baseCongeAgent,
			List<JourDto> listJoursFeries) {

		// 1. on check la PA
		if(!checkPAUnJourActiviteMinimumsurMoisPrecedent(idAgent, dateMoisPrecedent)){
			rmd.getErrors().add(AUCUNE_PA_ACTIVE_MOIS_PRECEDENT);
			return rmd;
		}
		
		// 2. on check toutes les absences (meme MALADIES (AS400)) sauf ASA
		if(!checkUnJourSansAbsenceSurLeMois(listAbences, idAgent, dateMoisPrecedent, baseCongeAgent, listJoursFeries)){
			rmd.getErrors().add(AUCUNE_PA_ACTIVE_MOIS_PRECEDENT);
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
			List<DemandeDto> listAbences, 
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
		
		if(null != listAbences
				&& !listAbences.isEmpty()) {
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
					for(DemandeDto demandeDto : listAbences) {
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
	
	private List<AffectationDto> getListAffectation(List<Integer> listIdsAgent, Date dateDebut, Date dateFin) {
		
		return sirhWsConsumer.getListAffectationDtoBetweenTwoDateAndForListAgent(listIdsAgent, dateDebut, dateFin);
	}
	
	private List<JourDto> getListJoursFeries(Date dateDebut, Date dateFin) {
		
		return sirhWsConsumer.getListeJoursFeries(dateDebut, dateFin);
	}

}
