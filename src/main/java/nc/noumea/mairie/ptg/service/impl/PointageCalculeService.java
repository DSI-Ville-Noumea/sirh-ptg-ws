package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefTypeSaisiDto;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.PtgComment;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.repository.IDpmRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IDpmService;
import nc.noumea.mairie.ptg.service.IPointageCalculeService;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageCalculeService implements IPointageCalculeService {
	
	private Logger logger = LoggerFactory.getLogger(PointageCalculeService.class);
	
	public static int HEURE_JOUR_DEBUT_PRIME_DPM = 5;
	public static int HEURE_JOUR_FIN_PRIME_DPM = 21;
	// pour la prime DPM : 4h minimum
	public static int SEUIL_MINI_PRIME_DPM = 4*60;

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;
	
	@Autowired
	private IMairieRepository mairieRepository;

	@Autowired
	private HelperService helperService;

	@Autowired
	private IDpmService dpmService;

	@Autowired
	private IDpmRepository dpmRepository;
	
	@Autowired
	private IAbsWsConsumer absWsConsumer;
	
	private static final String COMMENTAIRE_GENERATION_TID = "Prime TID générée lors de la ventilation";

	/**
	 * Calculating a list of PointageCalcule for an agent over a week (from =
	 * monday, to = sunday) Based on its RefPrime at the time of the monday
	 */
	@Override
	public List<PointageCalcule> calculatePointagesForAgentAndWeek(Integer idAgent, AgentStatutEnum statut,
			Date dateLundi, List<Pointage> agentPointages) {
		
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();

		List<Integer> norubrs = sirhWsConsumer.getPrimePointagesByAgent(idAgent, dateLundi, dateFinSemaine);
		List<RefPrime> refPrimes = pointageRepository.getRefPrimesCalculees(norubrs, statut);

		List<PointageCalcule> pointagesCalcules = new ArrayList<PointageCalcule>();

		for (RefPrime prime : refPrimes) {

			switch (prime.getNoRubr()) {
				case 7711:
				case 7712:
				case 7713:
					pointagesCalcules.addAll(generatePointage7711_12_13(idAgent, dateLundi, prime, agentPointages));
					break;
				case VentilationPrimeService.PRIME_RENFORT_GARDE:
					pointagesCalcules.addAll(generatePointage7717_RenfortGarde(idAgent, dateLundi, agentPointages));
					break;
				case VentilationPrimeService.RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_SAMEDI_DPM:
				case VentilationPrimeService.RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_DJF_DPM:
					pointagesCalcules.addAll(generatePointage7718_19_IndemniteForfaitaireTravailDPM(idAgent, dateLundi, agentPointages, prime));
					break;
			}
		}

		return pointagesCalcules;
	}

	public List<PointageCalcule> generatePointage7711_12_13(Integer idAgent, Date dateLundi, RefPrime prime,
			List<Pointage> pointages) {

		List<PointageCalcule> result = new ArrayList<PointageCalcule>();

		for (Pointage ptg : getPointagesPrime(pointages, VentilationPrimeService.INDEMNITE_DE_ROULEMENT)) {

			LocalDate datePointage = new DateTime(ptg.getDateDebut()).toLocalDate();
			LocalDate datePointageJPlus1 = datePointage.plusDays(1); 
					
			Interval inputInterval = new Interval(new DateTime(ptg.getDateDebut()), new DateTime(ptg.getDateFin()));
			Interval primeIntervalFirstNight = new Interval(new DateTime(datePointage.getYear(),
					datePointage.getMonthOfYear(), datePointage.getDayOfMonth(), 0, 0, 0), new DateTime(
					datePointage.getYear(), datePointage.getMonthOfYear(), datePointage.getDayOfMonth(), 5, 0, 0));
			Interval primeIntervalSecondNight = new Interval(new DateTime(datePointage.getYear(),
					datePointage.getMonthOfYear(), datePointage.getDayOfMonth(), 21, 0, 0), new DateTime(
							datePointageJPlus1.getYear(), datePointageJPlus1.getMonthOfYear(), datePointageJPlus1.getDayOfMonth(), 5, 0, 0));

			Interval overlap = primeIntervalFirstNight.overlap(inputInterval);
			Interval secondOverlap = primeIntervalSecondNight.overlap(inputInterval);
			long firstNightMinutes = overlap == null ? 0 : overlap.toDuration().getStandardMinutes();
			long secondNightMinutes = secondOverlap == null ? 0 : secondOverlap.toDuration().getStandardMinutes();
			long totalMinutes = inputInterval.toDuration().getStandardMinutes();

			if (prime.getNoRubr().equals(7712)
					&& (datePointage.getDayOfWeek() == DateTimeConstants.SUNDAY || sirhWsConsumer
							.isHoliday(datePointage))) {
				PointageCalcule existingPc = getPointageCalculeOfSamePrime(result, datePointage.toDate());
				existingPc = returnOrCreateNewPointageWithPrime(existingPc, ptg, prime);
				existingPc.addQuantite((double) totalMinutes);

				if (!result.contains(existingPc))
					result.add(existingPc);

				continue;
			}

			if (prime.getNoRubr().equals(7711)) {
				PointageCalcule existingPc = getPointageCalculeOfSamePrime(result, datePointage.toDate());
				existingPc = returnOrCreateNewPointageWithPrime(existingPc, ptg, prime);
				existingPc.addQuantite((double) (firstNightMinutes + secondNightMinutes));

				if (!result.contains(existingPc)
						&& !existingPc.getQuantite().equals(new Double(0)))
					result.add(existingPc);

				continue;
			}

			if (prime.getNoRubr().equals(7713)) {

				if (inputInterval.toDuration().toStandardHours().getHours() >= 9
						|| inputInterval.getStart().getHourOfDay() <= 5 && inputInterval.getEnd().getHourOfDay() >= 13
						|| inputInterval.getStart().getHourOfDay() <= 13 && inputInterval.getEnd().getHourOfDay() >= 21) {
					PointageCalcule existingPc = getPointageCalculeOfSamePrime(result, datePointage.toDate());
					existingPc = returnOrCreateNewPointageWithPrime(existingPc, ptg, prime);

					if (existingPc.getQuantite() == null || existingPc.getQuantite() < 2)
						existingPc.addQuantite(1.0);

					if (!result.contains(existingPc))
						result.add(existingPc);
				}
			}
		}

		return result;
	}

	private List<Pointage> getPointagesPrime(List<Pointage> pointages, Integer noRubr) {

		List<Pointage> result = new ArrayList<Pointage>();

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.PRIME && ptg.getRefPrime().getNoRubr().equals(noRubr))
				result.add(ptg);
		}

		return result;
	}

	/**
	 * Retrieves a PointageCalcule for a given date this is used by Pointage
	 * generators to avoid creating multiple primes for one day
	 * 
	 * @param list
	 * @param pointageDate
	 * @return
	 */
	private PointageCalcule getPointageCalculeOfSamePrime(List<PointageCalcule> list, Date pointageDate) {

		PointageCalcule existingPc = null;

		for (PointageCalcule pc : list) {
			if (pc.getDateDebut().equals(pointageDate)) {
				existingPc = pc;
				break;
			}
		}

		return existingPc;
	}

	/**
	 * This methods either returns the pointageCalcule if not null or create a
	 * new one according to the pointage and the prime generating it. This is
	 * used by Pointage generators to create new Pointage calcules the same way
	 * for all methods
	 * 
	 * @param pCalcule
	 * @param ptg
	 * @param prime
	 * @return
	 */
	private PointageCalcule returnOrCreateNewPointageWithPrime(PointageCalcule pCalcule, Pointage ptg, RefPrime prime) {

		if (pCalcule != null)
			return pCalcule;

		DateTime start = new DateTime(ptg.getDateDebut());
		Date newPrimeStartDate = new DateTime(start.getYear(), start.getMonthOfYear(), start.getDayOfMonth(), 0, 0, 0)
				.toDate();

		pCalcule = new PointageCalcule();
		pCalcule.setIdAgent(ptg.getIdAgent());
		pCalcule.setDateLundi(ptg.getDateLundi());
		pCalcule.setDateDebut(newPrimeStartDate);
		pCalcule.setEtat(EtatPointageEnum.VENTILE);
		pCalcule.setRefPrime(prime);
		pCalcule.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue()));

		return pCalcule;
	}

	@Override
	public List<Pointage> generatePointageTID_7720_7721_7722(Integer idAgentRH, Integer idAgent, AgentStatutEnum statut, Date dateLundi, List<Pointage> pointages) {
		
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();
		List<Integer> norubrs = sirhWsConsumer.getPrimePointagesByAgent(idAgent, dateLundi, dateFinSemaine);
		List<RefPrime> refPrimes = pointageRepository.getRefPrimes(norubrs, statut);
		
		List<Pointage> result = new ArrayList<Pointage>();
		
		for (RefPrime prime : refPrimes) {

			switch (prime.getNoRubr()) {
				case 7720:
				case 7721:
				case 7722:	
					result.addAll(generatePointageTID_7720_7721_7722(idAgentRH, idAgent, dateLundi, prime, pointages));
					break;
			}
		}
		return result;
	}
	
	private List<Pointage> generatePointageTID_7720_7721_7722(Integer idAgentRH, Integer idAgent, Date dateLundi, RefPrime prime, List<Pointage> pointages) {
		
		List<Pointage> result = new ArrayList<Pointage>();
		
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();
		
		// Base horaire de l agent
		BaseHorairePointageDto baseDto = sirhWsConsumer
				.getBaseHorairePointageAgent(idAgent, dateLundi, dateFinSemaine);
		
		// - A cette base sera aussi retranchée toutes les périodes maladie sans distinction
		// second retrieve all the absences in SPABSEN
		List<Spabsen> listSpAbsen = mairieRepository.getListMaladieBetween(
				idAgent, dateLundi, new DateTime(dateLundi).plusDays(7)
						.toDate());
		
		// - A cette base sera enfin retranchée toutes les périodes de congés exceptionnels
		// on ne compte pas les conges annuels et les conges annules
		List<DemandeDto> listConges = absWsConsumer
				.getListCongeWithoutCongesAnnuelsEtAnnulesBetween(idAgent,
						dateLundi, new DateTime(dateLundi).plusDays(7).toDate());
		
		for (int i = 0; i < 7; i++) {
			
			DateTime dday = new DateTime(dateLundi).plusDays(i);
			
			int dayTotalMinutes = helperService
					.convertMairieNbHeuresFormatToMinutes(baseDto
							.getDayBase(i));
			
			// - A cette base sera ajoutée toutes les heures saisies dans les pointages (si l’agent se trouve sur 2 fiches de poste différentes, 
			// les heures seront ajoutées à la base, la distinction de FDP est impossible ici)
			for (Pointage ptg : getPointagesHSupForDay(pointages, dday)) {
				dayTotalMinutes += new Interval(new DateTime(ptg.getDateDebut()), new DateTime(ptg.getDateFin())).toDuration().getStandardMinutes();
			}
			
			// - A cette base sera retranchée toutes les absences saisies dans les pointages (même remarque que précédemment :
			// si l’agent est sur 2 fiches de poste, l’absence sera retranchée de la base, la distinction de la FDP est impossible ici)
			for (Pointage ptg : getPointagesAbsenceForDay(pointages, dday)) {
				dayTotalMinutes -= new Interval(new DateTime(ptg.getDateDebut()), new DateTime(ptg.getDateFin())).toDuration().getStandardMinutes();
			}
			
			// - A cette base sera aussi retranchée toutes les périodes maladie sans distinction
			// second retrieve all the absences in SPABSEN
			int minutesSpAbsen = 0;
			for (Spabsen spabsen : listSpAbsen) {
				DateTime startDate = new DateTime(
						helperService.getDateFromMairieInteger(spabsen.getId()
								.getDatdeb()));
				if (dateLundi.after(startDate.toDate())) {
					startDate = new DateTime(dateLundi);
				}

				DateTime endDate = new DateTime(
						helperService.getDateFromMairieInteger(spabsen.getDatfin()));
				if (endDate.toDate().after(
						new DateTime(dateLundi).plusDays(7).toDate())) {
					endDate = new DateTime(dateLundi).plusDays(7);
				}

				Date dateJour = new DateTime(dateLundi).plusDays(i).toDate();
				if ((dateJour.equals(startDate.toDate()) || dateJour
						.after(startDate.toDate()))
						&& (dateJour.equals(endDate.toDate()) || dateJour
								.before(endDate.toDate()))) {
					minutesSpAbsen += helperService
							.convertMairieNbHeuresFormatToMinutes(baseDto
									.getDayBase(i));
				}
			}
			
			// - A cette base sera enfin retranchée toutes les périodes de congés exceptionnels
			// on ne compte pas les conges annuels et les conges annules
			int minutesConges = 0;
			for (DemandeDto conge : listConges) {
				DateTime startDate = new DateTime(conge.getDateDebut());
				if (dateLundi.after(startDate.toDate())) {
					startDate = new DateTime(dateLundi);
				}

				DateTime endDate = new DateTime(conge.getDateFin());
				if (endDate.toDate().after(
						new DateTime(dateLundi).plusDays(7).toDate())) {
					endDate = new DateTime(dateLundi).plusDays(7);
				}

				DateTime dateJour = new DateTime(dateLundi).plusDays(i);

				if ((dateJour.getDayOfYear() == startDate.getDayOfYear() || dateJour
						.toDate().after(startDate.toDate()))
						&& (dateJour.getDayOfYear() == endDate.plusMinutes(-1)
								.getDayOfYear() || dateJour.toDate().before(
								endDate.toDate()))) {

					int minutesCongesDay = helperService
							.convertMairieNbHeuresFormatToMinutes(baseDto
									.getDayBase(i));
					// on gere ici les demis journees
					DateTime dateDebut = new DateTime(conge.getDateDebut());
					DateTime dateFin = new DateTime(conge.getDateFin());

					List<RefTypeSaisiDto> listTypeAbsence = absWsConsumer
							.getTypeSaisiAbsence(conge.getIdTypeDemande());

					if (null != listTypeAbsence && !listTypeAbsence.isEmpty()) {

						if (listTypeAbsence.get(0).getUniteDecompte()
								.equals("minutes")) {
							minutesCongesDay = conge.getDuree().intValue();
						}
						if (listTypeAbsence.get(0).getUniteDecompte()
								.equals("jours")) {
							if (dateDebut.dayOfYear().equals(
									dateFin.dayOfYear())
									&& startDate.getDayOfWeek() - 1 == i) {

								if ((dateDebut.getHourOfDay() == 0 && dateFin
										.getHourOfDay() == 11)
										|| (dateDebut.getHourOfDay() == 12 && dateFin
												.getHourOfDay() == 23)) {
									minutesCongesDay = minutesCongesDay / 2;
								}
							} else if (startDate.getDayOfWeek() - 1 == i
									&& dateDebut.getHourOfDay() == 12) {
								minutesCongesDay = minutesCongesDay / 2;
							} else if (endDate.getDayOfWeek() - 1 == i
									&& dateFin.getHourOfDay() == 11) {
								minutesCongesDay = minutesCongesDay / 2;
							}
						}

						minutesConges += minutesCongesDay;
					}
				}
			}
			
			dayTotalMinutes = dayTotalMinutes - minutesSpAbsen - minutesConges;
			
			List<Pointage> listPtg = pointageRepository.getListPointages(idAgent, dday.toDate(), RefTypePointageEnum.PRIME.getValue(), prime.getIdRefPrime());
			Pointage ptg = null;
			if(null != listPtg
					&& !listPtg.isEmpty()) {
				ptg = listPtg.get(0);
			}
			
			if (dayTotalMinutes <= 0
					|| null != ptg)
				continue;
			
			if(null == ptg) {
				ptg = new Pointage();
			}
			
			if(null == ptg.getCommentaire()) {
				PtgComment commentaire = pointageRepository.getCommentaireByText(COMMENTAIRE_GENERATION_TID);
				if(null == commentaire) {
					commentaire = new PtgComment(COMMENTAIRE_GENERATION_TID);
				}
				ptg.setCommentaire(commentaire);
			}
			
			EtatPointage etatPtg = new EtatPointage();
			etatPtg.setDateEtat(new Date());
			etatPtg.setDateMaj(new Date());
			etatPtg.setEtat(EtatPointageEnum.VENTILE);
			etatPtg.setIdAgent(idAgentRH);
			
			ptg.setIdAgent(idAgent);
			ptg.setDateDebut(dday.toDate());
			ptg.setDateLundi(dateLundi);
			ptg.getEtats().add(etatPtg);
			ptg.setRefPrime(prime);
			ptg.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue()));
			ptg.setQuantite(dayTotalMinutes);
			
			etatPtg.setPointage(ptg);
			
			pointageRepository.persisEntity(ptg);
			
			result.add(ptg);
		}
		
		return result;
	}
	
	private List<Pointage> getPointagesHSupForDay(List<Pointage> pointages, DateTime day) {
		
		List<Pointage> result = new ArrayList<Pointage>();
		
		for (Pointage ptg : pointages) {
			DateTime dday = new DateTime(ptg.getDateDebut());
			if (dday.getDayOfYear() == day.getDayOfYear() 
					&& dday.getYear() == day.getYear() 
					&& ptg.getTypePointageEnum() == RefTypePointageEnum.H_SUP)
				result.add(ptg);
		}
		
		return result;
	}
	
	private List<Pointage> getPointagesAbsenceForDay(List<Pointage> pointages, DateTime day) {
		
		List<Pointage> result = new ArrayList<Pointage>();
		
		for (Pointage ptg : pointages) {
			DateTime dday = new DateTime(ptg.getDateDebut());
			if (dday.getDayOfYear() == day.getDayOfYear() 
					&& dday.getYear() == day.getYear() 
					&& ptg.getTypePointageEnum() == RefTypePointageEnum.ABSENCE)
				result.add(ptg);
		}
		
		return result;
	}
	
	/**
	 * #19718 RENFORT De GARDE pour le DCIS
	 * 
	 * @param idAgent Integer
	 * @param dateLundi Date
	 * @param pointages List<Pointage> 
	 * @return List<PointageCalcule>
	 */
	public List<PointageCalcule> generatePointage7717_RenfortGarde(Integer idAgent, Date dateLundi, 
			List<Pointage> pointages) {
		
		List<PointageCalcule> result = new ArrayList<PointageCalcule>();

		for (Pointage ptg : getPointagesPrime(pointages, VentilationPrimeService.PRIME_RENFORT_GARDE)) {
			
			DateTime dateDebut = new DateTime(ptg.getDateDebut());
			DateTime dateFin = new DateTime(ptg.getDateFin());
			
			Duration dureePrimeSaisie = new Duration(dateDebut, dateFin);
			
			Double ratioHSup = (double) (12.0/24.0);
			
			
			// #19718 RG : RENFORT GARDE EQUIVALENCES
			// Si renfort de 24H : Jour de semaine 12H Samedi : 14H Dimanche et jour férié et chômé : 16H
			// Si renfort de mois de 24H : Jour de semaine H*12/24 Samedi : H*14/24 Dimanche et jour férié et chômé : H* 16/24
			// H = Nombre d'heures effectuées.
			
			if(dateDebut.getDayOfWeek() == DateTimeConstants.SUNDAY
					|| sirhWsConsumer.isHoliday(dateDebut)) {
				ratioHSup = (double) (16.0/24.0);
			}
			if(dateDebut.getDayOfWeek() == DateTimeConstants.SATURDAY) {
				ratioHSup = (double) (14.0/24.0);
			}
			
			// calcul (reultat en minutes)
			Integer quantite = new Double(dureePrimeSaisie.getStandardMinutes() * ratioHSup).intValue();
			
			if(null != quantite 
					&& 0 < quantite) {
				PointageCalcule existingPc = getPointageCalculeOfSamePrime(result, dateDebut.toDate());
				existingPc = returnOrCreateNewPointageWithHSup(existingPc, ptg);
				existingPc.addQuantite((double)quantite);
	
				if (!result.contains(existingPc))
					result.add(existingPc);
			}
		}
		
		return result;
	}

	/**
	 * This methods either returns the pointageCalcule if not null or create a
	 * new one according to the pointage and the HSup generating it. This is
	 * used by Pointage generators to create new Pointage calcules the same way
	 * for all methods
	 * 
	 * @param pCalcule PointageCalcule
	 * @param ptg Pointage
	 * @param quantite Integer
	 * @return PointageCalcule
	 */
	private PointageCalcule returnOrCreateNewPointageWithHSup(PointageCalcule pCalcule, Pointage ptg) {

		if (pCalcule != null)
			return pCalcule;

		pCalcule = new PointageCalcule();
		pCalcule.setIdAgent(ptg.getIdAgent());
		pCalcule.setDateLundi(ptg.getDateLundi());
		pCalcule.setDateDebut(ptg.getDateDebut());
		pCalcule.setDateFin(ptg.getDateFin());
		pCalcule.setEtat(EtatPointageEnum.VENTILE);
		pCalcule.setRefPrime(null);
		pCalcule.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.H_SUP.getValue()));

		return pCalcule;
	}
	
	/**
	 * #30544 Indemnité forfaitaire travail DPM
	 * 
	 * @param idAgent Integer
	 * @param dateLundi Date
	 * @param pointages List<Pointage> 
	 * @return List<PointageCalcule>
	 */
	public List<PointageCalcule> generatePointage7718_19_IndemniteForfaitaireTravailDPM(Integer idAgent, Date dateLundi, 
			List<Pointage> pointages, RefPrime prime) {
		
		List<PointageCalcule> result = new ArrayList<PointageCalcule>();

		for (int i = 0; i < 7; i++) {
			
			DateTime dday = new DateTime(dateLundi).plusDays(i);
			
			// la prime est que pour les samedi, dimanche et jours feries
			if(!dpmService.isDroitAgentToIndemniteForfaitaireDPMForOneDay(idAgent, dday.toLocalDate())) {
				continue;
			}
			
			// on additionne toutes les heures supp. de la journee entre 5h et 21h
			for (Pointage ptg : getPointagesHSupForDay(pointages, dday)) {
				
				// l interval de la deliberation pour la prime est de 5h a 21h
				int dayTotalMinutes = helperService.calculMinutesPointageInInterval(ptg, 
						new LocalTime(HEURE_JOUR_DEBUT_PRIME_DPM,0,0), 
						new LocalTime(HEURE_JOUR_FIN_PRIME_DPM,0,0));
				
				// si superieur a 4h
				if(dayTotalMinutes >= SEUIL_MINI_PRIME_DPM) {
					// si samedi, on cree la prime 7718
					if(prime.getNoRubr().equals(VentilationPrimeService.RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_SAMEDI_DPM)
							&& DateTimeConstants.SATURDAY != dday.getDayOfWeek()) {
						continue;
					}
					// si DJF on cree la prime 7719
					if(prime.getNoRubr().equals(VentilationPrimeService.RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_DJF_DPM)
							&& DateTimeConstants.SUNDAY != dday.getDayOfWeek()
							&& !sirhWsConsumer.isJourFerie(dday)) { 
						continue;
					}
					
					DpmIndemChoixAgent choixAgent = dpmRepository.getDpmIndemChoixAgent(idAgent, new DateTime(ptg.getDateDebut()).getYear());
					
					if(null == choixAgent) {
						logger.error(String.format("Aucun choix de l agent %d pour la prime DPM => aucune prime DPM calcule", idAgent));
						continue;
					}
					if(choixAgent.isChoixRecuperation()) {
						logger.debug(String.format("Choix de l agent %d pour la prime DPM : recuperation => aucune prime DPM calcule", idAgent));
						continue;
					}
					
					if(choixAgent.isChoixIndemnite()) {
						PointageCalcule existingPc = getPointageCalculeOfSamePrime(result, dday.toDate());
						existingPc = returnOrCreateNewPointageWithPrime(existingPc, ptg, prime);
						
						// Au-delà de 4 Heures : calcul au prorata (¼ de prime par heure supplémentaire pleine)
						// calcul nombre heure pleine
						Integer nombreHeuresPleines = new Integer(dayTotalMinutes / 60); 
						Double quantitePrime = nombreHeuresPleines * 0.25;
						
						existingPc.addQuantite(quantitePrime);
		
						if (!result.contains(existingPc))
							result.add(existingPc);
						
						logger.debug(String.format("Prime DPM calcule pour l agent %s.", idAgent));
					}
				}
			}
		}

		return result;
	}

}
