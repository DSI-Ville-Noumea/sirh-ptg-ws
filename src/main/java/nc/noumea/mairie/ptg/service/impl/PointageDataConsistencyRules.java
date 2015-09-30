package nc.noumea.mairie.ptg.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.service.IPointageDataConsistencyRules;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageDataConsistencyRules implements IPointageDataConsistencyRules {

	@Autowired
	private IMairieRepository mairieRepository;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	private IAbsWsConsumer absWsConsumer;

	@Autowired
	private HelperService helperService;

	// -- MESSAGES --//
	public static final String BASE_HOR_MAX = "L'agent dépasse sa base horaire";
	public static final String MALADIE_MSG = "%s : L'agent est en maladie sur cette période.";
	public static final String HS_INA_315_MSG = "L'agent n'a pas droit aux HS sur la période (INA > 315)";
	public static final String BASE_HOR_00Z_MSG = "L'agent est en base horaire \"00Z\" sur la période";
	public static final String INACTIVITE_MSG = "L'agent n'est pas en activité sur cette période.";
	public static final String AVERT_MESSAGE_ABS = "Soyez vigilant, vous avez saisi des primes et/ou heures supplémentaires sur des périodes où l’agent était absent.";
	public static final String ERROR_7651_MSG = "";
	public static final String ERROR_7652_MSG = "";
	public static final String ERROR_POINTAGE_PLUS_3_MOIS = "La semaine sélectionnée est trop ancienne pour être modifiée.";
	public static final String ERROR_POINTAGE_SUP_DATE_JOUR = "La semaine sélectionnée est dans le futur et ne peut donc être modifiée.";
	public static final String HS_TPS_PARTIEL_MSG = "L'agent est en temps partiel, il ne peut pas avoir plus de %s heures supplémentaires.";
	public static final String ERROR_DATE_POINTAGE = "Pour le pointage du %s, la date de fin est antérieure à la date de début.";
	public static final String ERROR_INTERVALLE_POINTAGE = "Pour le pointage du %s, il faut 30 minutes d'intervalle entre la date de début et la date de fin.";
	public static final String ERROR_PRIME_SAISIE_J1_POINTAGE = "Pour la prime %s du %s, la saisie à J+1 n'est pas autorisée.";
	public static final String ERROR_PRIME_QUANTITE_POINTAGE = "Pour la prime %s du %s, la quantité ne peut être supérieure à 24.";
	public static final String ERROR_PRIME_EPANDAGE_QUANTITE = "Pour la prime %s du %s, la quantité ne peut être supérieure à 2.";

	public static final List<String> ACTIVITE_CODES = Arrays.asList("01", "02", "03", "04", "23", "24", "60", "61",
			"62", "63", "64", "65", "66");

	@Override
	public ReturnMessageDto checkMaxAbsenceHebdo(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr, BaseHorairePointageDto baseDto) {

		double nbHours = 0;

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.ABSENCE)
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());
			DateTime fin = new DateTime(ptg.getDateFin());

			nbHours += (Minutes.minutesBetween(deb, fin).getMinutes() / 60.0);
		}

		if (nbHours == 0)
			return srm;

		double agentMaxHours = carr.getSpbhor().getTaux() * baseDto.getBaseCalculee();

		if (nbHours > agentMaxHours)
			srm.getErrors().add(BASE_HOR_MAX);

		return srm;
	}

	@Override
	public ReturnMessageDto checkRecuperation(ReturnMessageDto srm, Integer idAgent, List<Pointage> pointages) {
		ReturnMessageDto result = new ReturnMessageDto();
		for (Pointage p : pointages) {

			// pour chaque pointage on verifie si en recup
			// si oui, on ajoute des erreurs
			// #6843 attention on ne check pas les primes
			if (!RefTypePointageEnum.PRIME.equals(p.getTypePointageEnum())) {
				result = absWsConsumer.checkRecuperation(idAgent, p.getDateDebut(), p.getDateFin());
			}
		}

		for (String info : result.getInfos()) {
			srm.getInfos().add(info);
		}
		for (String erreur : result.getErrors()) {
			srm.getErrors().add(erreur);
		}
		return srm;
	}

	@Override
	public ReturnMessageDto checkReposComp(ReturnMessageDto srm, Integer idAgent, List<Pointage> pointages) {
		ReturnMessageDto result = new ReturnMessageDto();
		for (Pointage p : pointages) {
			// pour chaque pointage on verifie si en repos comp
			// si oui, on ajoute des erreurs
			// #6843 attention on ne check pas les primes
			if (!RefTypePointageEnum.PRIME.equals(p.getTypePointageEnum())) {
				result = absWsConsumer.checkReposComp(idAgent, p.getDateDebut(), p.getDateFin());
			}
		}

		for (String info : result.getInfos()) {
			srm.getInfos().add(info);
		}
		for (String erreur : result.getErrors()) {
			srm.getErrors().add(erreur);
		}
		return srm;
	}

	@Override
	public ReturnMessageDto checkAbsencesSyndicales(ReturnMessageDto srm, Integer idAgent, List<Pointage> pointages) {
		ReturnMessageDto result = new ReturnMessageDto();
		for (Pointage p : pointages) {
			// pour chaque pointage on verifie si en ASA
			// si oui, on ajoute des erreurs
			// #6843 attention on ne check pas les primes
			if (!RefTypePointageEnum.PRIME.equals(p.getTypePointageEnum())) {
				result = absWsConsumer.checkAbsencesSyndicales(idAgent, p.getDateDebut(), p.getDateFin());
			}
		}

		for (String info : result.getInfos()) {
			srm.getInfos().add(info);
		}
		for (String erreur : result.getErrors()) {
			srm.getErrors().add(erreur);
		}
		return srm;
	}

	@Override
	public ReturnMessageDto checkCongesExceptionnels(ReturnMessageDto srm, Integer idAgent, List<Pointage> pointages) {
		ReturnMessageDto result = new ReturnMessageDto();
		for (Pointage p : pointages) {
			// pour chaque pointage on verifie si en conge exceptionnel
			// si oui, on ajoute des erreurs
			// #6843 attention on ne check pas les primes
			if (!RefTypePointageEnum.PRIME.equals(p.getTypePointageEnum())) {
				result = absWsConsumer.checkCongesExceptionnels(idAgent, p.getDateDebut(), p.getDateFin());
			}
		}

		for (String info : result.getInfos()) {
			srm.getInfos().add(info);
		}
		for (String erreur : result.getErrors()) {
			srm.getErrors().add(erreur);
		}
		return srm;
	}

	@Override
	public ReturnMessageDto checkCongeAnnuel(ReturnMessageDto srm, Integer idAgent, List<Pointage> pointages) {
		ReturnMessageDto result = new ReturnMessageDto();
		for (Pointage p : pointages) {
			// pour chaque pointage on verifie si en conge annuel
			// si oui, on ajoute des erreurs
			// #6843 attention on ne check pas les primes
			if (!RefTypePointageEnum.PRIME.equals(p.getTypePointageEnum())) {
				result = absWsConsumer.checkCongeAnnuel(idAgent, p.getDateDebut(), p.getDateFin());
			}
		}

		for (String info : result.getInfos()) {
			srm.getInfos().add(info);
		}
		for (String erreur : result.getErrors()) {
			srm.getErrors().add(erreur);
		}
		return srm;
	}

	@Override
	public ReturnMessageDto checkSpabsenMaladie(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages) {

		Date end = new DateTime(dateLundi).plusDays(7).toDate();

		List<Spabsen> maladies = mairieRepository.getListMaladieBetween(idAgent, dateLundi, end);

		for (Spabsen mal : maladies) {
			checkInterval(srm, MALADIE_MSG, mal.getId().getDatdeb(), null, mal.getDatfin(), null, pointages);
		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkAgentINAAndHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr, BaseHorairePointageDto baseDto) {

		// cas de la DPM #11622
		EntiteDto service = sirhWsConsumer.getAgentDirection(idAgent, dateLundi);

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.H_SUP) {
				// tester si base Z
				if (baseDto.getCodeBaseHorairePointage().equals("00Z")) {
					srm.getErrors().add(BASE_HOR_00Z_MSG);
					return srm;
				}
				// cas de la DPM #11622
				if (service.getSigle().toUpperCase().equals("DPM") || carr.getSpbarem().getIna() > 315) {
					ptg.setHeureSupRecuperee(true);
					return srm;
				}
			}
		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkAgentInactivity(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, AgentGeneriqueDto ag) {

		Spadmn adm = mairieRepository.getAgentCurrentPosition(ag, dateLundi);

		if (!ACTIVITE_CODES.contains(adm.getCdpadm()))
			srm.getErrors().add(INACTIVITE_MSG);

		return srm;
	}

	@Override
	public ReturnMessageDto checkPrime7650(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages) {

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME || !ptg.getRefPrime().getNoRubr().equals(7650))
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());

			if (deb.getDayOfWeek() == DateTimeConstants.SATURDAY || deb.getDayOfWeek() == DateTimeConstants.SUNDAY)
				srm.getErrors()
						.add(String
								.format("La prime 7650 du %s n'est pas valide. Elle ne peut être saisie que du lundi au vendredi.",
										deb.toString("dd/MM/yyyy")));

		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkPrime7651(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages) {

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME || !ptg.getRefPrime().getNoRubr().equals(7651))
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());

			if (deb.getDayOfWeek() != DateTimeConstants.SATURDAY && deb.getDayOfWeek() != DateTimeConstants.SUNDAY
					&& !sirhWsConsumer.isHoliday(deb) && !sirhWsConsumer.isHoliday(deb.plusDays(1)))
				srm.getErrors()
						.add(String
								.format("La prime 7651 du %s n'est pas valide. Elle ne peut être saisie qu'un samedi et dimanche, ou alors une veille et jour férié.",
										deb.toString("dd/MM/yyyy")));

		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkPrime7652(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages) {

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME || !ptg.getRefPrime().getNoRubr().equals(7652))
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());

			if (deb.getDayOfWeek() != DateTimeConstants.SUNDAY && !sirhWsConsumer.isHoliday(deb))
				srm.getErrors()
						.add(String
								.format("La prime 7652 du %s n'est pas valide. Elle ne peut être saisie qu'un dimanche ou jour férié.",
										deb.toString("dd/MM/yyyy")));

		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkPrime7704(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages) {

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME || !ptg.getRefPrime().getNoRubr().equals(7704))
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());

			if (ptg.getQuantite() > 2)
				srm.getErrors().add(
						String.format("La prime 7704 du %s n'est pas valide. Sa quantité ne peut être supérieur à 2.",
								deb.toString("dd/MM/yyyy")));

		}

		return srm;
	}

	// -- Helpers --//
	@Override
	public DateTime getDateDebut(Integer dateDeb, Integer codem1) {
		DateTime recupDateDeb = new DateTime(helperService.getDateFromMairieInteger(dateDeb));

		if (codem1 != null && codem1.equals(2))
			recupDateDeb = recupDateDeb.plusHours(12); // 12h00

		return recupDateDeb;
	}

	@Override
	public DateTime getDateFin(Integer dateFin, Integer codem2) {
		DateTime recupDateFin = new DateTime(helperService.getDateFromMairieInteger(dateFin));

		if (codem2 == null || codem2.equals(2))
			return recupDateFin.plusDays(1); // 00h00 D+1

		recupDateFin = recupDateFin.plusMinutes(691); // 12h00

		return recupDateFin;
	}

	/**
	 * This methods checks whether a list of pointages are being input in a
	 * given period. This period can start or end by full or half a day.
	 * 
	 * @param srm
	 *            The structure to return the INFO or ERROR messages
	 * @param message
	 *            The message format to return
	 * @param start
	 *            The start day of the given period
	 * @param codem1
	 *            Whether the start day is a full day or a half day (1, 2)
	 * @param end
	 *            The end day of the given period
	 * @param codem2
	 *            Whether the end day is a full day or a half day (1, 2)
	 * @param pointages
	 *            The list of pointages to test the period against
	 * @return The structure containing the INFO or ERROR messages
	 */
	protected ReturnMessageDto checkInterval(ReturnMessageDto srm, String message, Integer start, Integer codem1,
			Integer end, Integer codem2, List<Pointage> pointages) {

		DateTime recupDateDeb = getDateDebut(start, codem1);
		DateTime recupDateFin = getDateFin(end, codem2);

		int dayOfYearDeb = new DateTime(recupDateDeb).getDayOfYear();
		int dayOfYearFin = new DateTime(recupDateFin).getDayOfYear();
		boolean partialDayDeb = recupDateDeb.getHourOfDay() != 0;
		boolean partialDayFin = recupDateFin.getHourOfDay() != 0;

		DateTime recupDateDebFull = getDateDebut(start, null);
		DateTime recupDateFinFull = getDateFin(end, null);

		Interval rInterval = new Interval(recupDateDebFull, recupDateFinFull);

		for (Pointage ptg : pointages) {

			DateTime ptgTimeStart = new DateTime(ptg.getDateDebut());
			DateTime ptgTimeEnd = new DateTime(ptg.getDateFin() == null ? ptg.getDateDebut() : ptg.getDateFin());

			Interval pInterval = new Interval(ptgTimeStart, ptgTimeEnd);

			if (rInterval.overlaps(pInterval)) {

				if (ptgTimeStart.dayOfYear().get() == dayOfYearDeb && partialDayDeb
						|| ptgTimeStart.dayOfYear().get() == dayOfYearFin && partialDayFin
						|| ptgTimeEnd.dayOfYear().get() == dayOfYearDeb && partialDayDeb
						|| ptgTimeEnd.dayOfYear().get() == dayOfYearFin && partialDayFin) {
					if (!srm.getInfos().contains(AVERT_MESSAGE_ABS)) {
						srm.getInfos().add(AVERT_MESSAGE_ABS);
					}
				} else {
					String msg = String.format(message, ptgTimeStart.toString("dd/MM/yyyy HH:mm"));
					srm.getErrors().add(msg);
				}
			}
		}

		return srm;
	}

	/**
	 * Processes the data consistency of a set of Pointages being input by a
	 * user. It will check the different business rules in order to make sure
	 * they're consistent
	 */
	@Override
	public void processDataConsistency(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {

		AgentGeneriqueDto ag = sirhWsConsumer.getAgent(idAgent);
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(ag, dateLundi);
		BaseHorairePointageDto baseDto = sirhWsConsumer.getBaseHorairePointageAgent(idAgent, dateLundi);

		checkHeureFinSaisieHSup(srm, idAgent, dateLundi, pointages, carr);
		checkIntervalleDateDebDateFin(srm, idAgent, pointages);
		// DEBUT on check les types d'absences du projet SIRH-ABS-WS
		checkRecuperation(srm, idAgent, pointages);
		checkReposComp(srm, idAgent, pointages);
		checkAbsencesSyndicales(srm, idAgent, pointages);
		checkCongesExceptionnels(srm, idAgent, pointages);
		checkCongeAnnuel(srm, idAgent, pointages);
		// TODO reste à traiter les maladies
		checkSpabsenMaladie(srm, idAgent, dateLundi, pointages);
		// FIN on check les types d'absences du projet SIRH-ABS-WS
		checkMaxAbsenceHebdo(srm, idAgent, dateLundi, pointages, carr, baseDto);
		checkAgentINAAndHSup(srm, idAgent, dateLundi, pointages, carr, baseDto);
		checkAgentTempsPartielAndHSup(srm, idAgent, dateLundi, pointages, carr, baseDto);
		checkAgentInactivity(srm, idAgent, dateLundi, pointages, ag);
		checkPrime7650(srm, idAgent, dateLundi, pointages);
		checkPrime7651(srm, idAgent, dateLundi, pointages);
		checkPrime7652(srm, idAgent, dateLundi, pointages);
		checkPrime7704(srm, idAgent, dateLundi, pointages);
	}

	private ReturnMessageDto checkIntervalleDateDebDateFin(ReturnMessageDto srm, Integer idAgent,
			List<Pointage> pointages) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		for (Pointage ptg : pointages) {
			Calendar fin = null;
			if (ptg.getDateFin() != null) {
				fin = Calendar.getInstance();
				fin.setTime(ptg.getDateFin());
			}

			Calendar debut = Calendar.getInstance();
			debut.setTime(ptg.getDateDebut());
			// on verif datefin > datedebut
			if (fin != null && fin.getTime().before(debut.getTime())) {
				srm.getErrors().add(String.format(ERROR_DATE_POINTAGE, sdf.format(ptg.getDateDebut())));
			}
			// on verif intervalle de 30 min minimum entre les 2 dates
			if (null != ptg.getRefPrime() && ptg.getRefPrime().getTypeSaisie().equals(TypeSaisieEnum.PERIODE_HEURES)
					&& fin != null && (fin.getTimeInMillis() - debut.getTimeInMillis()) < 1800000) {
				srm.getErrors().add(String.format(ERROR_INTERVALLE_POINTAGE, sdf.format(ptg.getDateDebut())));
			}
			// pour les primes
			if (ptg.getTypePointageEnum().equals(RefTypePointageEnum.PRIME)) {
				// si 7715 alors saisie j+1 autorisée sinon non
				if (ptg.getRefPrime().getTypeSaisie().equals(TypeSaisieEnum.PERIODE_HEURES)
						&& ptg.getRefPrime().getNoRubr() != 7715
						&& debut.get(Calendar.DAY_OF_MONTH) != fin.get(Calendar.DAY_OF_MONTH)) {
					srm.getErrors().add(
							String.format(ERROR_PRIME_SAISIE_J1_POINTAGE, ptg.getRefPrime().getLibelle(),
									sdf.format(ptg.getDateDebut())));
					// pour les primes de type NOMBRE_HEURE, la quantite ne doit
					// pas depasser 24H
				} else if (ptg.getRefPrime().getTypeSaisie().equals(TypeSaisieEnum.NB_HEURES)
						&& ptg.getQuantite() > 24 * 60) {
					srm.getErrors().add(
							String.format(ERROR_PRIME_QUANTITE_POINTAGE, ptg.getRefPrime().getLibelle(),
									sdf.format(ptg.getDateDebut())));
					// PRIME FICTIVE D EPANDAGE pour le SIPRES
					// ne peut pas depasser 2H
				} else if (ptg.getRefPrime().getTypeSaisie().equals(TypeSaisieEnum.NB_HEURES)
						&& ptg.getRefPrime().getNoRubr().equals(VentilationPrimeService.PRIME_EPANDAGE_7716)
						&& ptg.getQuantite() > 2 * 60) {
					srm.getErrors().add(
							String.format(ERROR_PRIME_EPANDAGE_QUANTITE, ptg.getRefPrime().getLibelle(),
									sdf.format(ptg.getDateDebut())));
				}

			}
		}
		return srm;

	}

	@Override
	public ReturnMessageDto checkHeureFinSaisieHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr) {

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.H_SUP) {

				int heureFinNuit = 0;
				if (AgentStatutEnum.F.equals(carr.getStatutCarriere())) {
					heureFinNuit = VentilationHSupService.HEURE_JOUR_DEBUT_F;
				}
				if (AgentStatutEnum.C.equals(carr.getStatutCarriere())) {
					heureFinNuit = VentilationHSupService.HEURE_JOUR_DEBUT_C;
				}
				if (AgentStatutEnum.CC.equals(carr.getStatutCarriere())) {
					heureFinNuit = VentilationHSupService.HEURE_JOUR_DEBUT_CC;
				}

				GregorianCalendar calDateFinLimite = new GregorianCalendar();
				calDateFinLimite.setTime(ptg.getDateDebut());
				calDateFinLimite.add(GregorianCalendar.DAY_OF_YEAR, 1);
				calDateFinLimite.set(Calendar.AM_PM, Calendar.AM);
				calDateFinLimite.set(GregorianCalendar.HOUR, heureFinNuit);
				calDateFinLimite.set(GregorianCalendar.MINUTE, 0);
				calDateFinLimite.set(GregorianCalendar.MILLISECOND, 0);

				if (ptg.getDateFin().after(calDateFinLimite.getTime())) {
					srm.getErrors()
							.add(String
									.format("L'heure de fin pour les Heures Sup. saisie le %s ne peut pas dépasser %sh (limite des heures de nuit).",
											new DateTime(ptg.getDateDebut()).toString("dd/MM/yyyy"), heureFinNuit));
				}
			}
		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkDateLundiAnterieurA3Mois(ReturnMessageDto srm, Date dateLundi) {

		GregorianCalendar calStr1 = new GregorianCalendar();
		calStr1.setTime(new Date());
		calStr1.add(GregorianCalendar.MONTH, -3);
		calStr1.add(GregorianCalendar.WEEK_OF_YEAR, -1); // back to previous
															// week
		calStr1.set(GregorianCalendar.DAY_OF_WEEK, Calendar.MONDAY); // jump to
																		// next
																		// monday

		if (dateLundi.before(calStr1.getTime())) {
			srm.getErrors().add(String.format(ERROR_POINTAGE_PLUS_3_MOIS));
		}
		return srm;
	}

	@Override
	public ReturnMessageDto checkAgentTempsPartielAndHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr, BaseHorairePointageDto baseDto) {

		boolean tempsPartiel = carr.getSpbhor().getTaux().intValue() != 1;
		if (tempsPartiel) {
			int minutesHSupWeek = 0;
			int minutesAbsWeek = 0;
			for (Pointage ptg : pointages) {
				if (ptg.getTypePointageEnum() == RefTypePointageEnum.H_SUP) {
					DateTime startDate = new DateTime(ptg.getDateDebut());
					DateTime endDate = new DateTime(ptg.getDateFin());
					int nbMinutes = Minutes.minutesBetween(startDate, endDate).getMinutes();
					minutesHSupWeek += nbMinutes;
				}
				if (ptg.getTypePointageEnum() == RefTypePointageEnum.ABSENCE) {
					DateTime startDate = new DateTime(ptg.getDateDebut());
					DateTime endDate = new DateTime(ptg.getDateFin());
					int nbMinutes = Minutes.minutesBetween(startDate, endDate).getMinutes();
					minutesAbsWeek += nbMinutes;
				}
			}

			//  #18728
			int weekBase = (int) (helperService.convertMairieNbHeuresFormatToMinutes(baseDto.getBaseCalculee()));
			// base calculee x 20% = HSup autorisees
			int baseLegaleHsupMax =  (int) weekBase + new Double(weekBase * 0.2).intValue();
			// mais si temps effectif de travail hebdomadaire > 39h
			// alors on bloque a 39h
			baseLegaleHsupMax = baseLegaleHsupMax > 39*60 ? 39*60 : baseLegaleHsupMax;

			if ((weekBase + minutesHSupWeek - minutesAbsWeek) > baseLegaleHsupMax) {
				DecimalFormat df = new DecimalFormat("0.##");
				double nombre = helperService.convertMinutesToMairieNbHeuresFormat(baseLegaleHsupMax - weekBase + minutesAbsWeek);
				String msg = String.format(HS_TPS_PARTIEL_MSG, df.format(nombre));
				srm.getErrors().add(msg);
			}
		}

		return srm;
	}

	/**
	 * Processes the data consistency of a set of Pointages being input by a
	 * user. It will check the different business rules in order to make sure
	 * they're consistent
	 */
	@Override
	public void checkAllAbsences(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {

		// DEBUT on check les types d'absences du projet SIRH-ABS-WS
		checkRecuperation(srm, idAgent, pointages);
		checkReposComp(srm, idAgent, pointages);
		checkAbsencesSyndicales(srm, idAgent, pointages);
		checkCongesExceptionnels(srm, idAgent, pointages);
		checkCongeAnnuel(srm, idAgent, pointages);
		// TODO reste à traiter les maladies
		checkSpabsenMaladie(srm, idAgent, dateLundi, pointages);
		// FIN on check les types d'absences du projet SIRH-ABS-WS
	}

	@Override
	public ReturnMessageDto checkDateLundiNotSuperieurDateJour(ReturnMessageDto srm, Date dateLundi) {
		if (dateLundi.after(new Date())) {
			srm.getErrors().add(String.format(ERROR_POINTAGE_SUP_DATE_JOUR));
		}
		return srm;
	}
}
