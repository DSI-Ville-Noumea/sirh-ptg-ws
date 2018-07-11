package nc.noumea.mairie.ptg.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypeAbsenceEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IDpmRepository;
import nc.noumea.mairie.ptg.service.IPointageDataConsistencyRules;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

@Service
public class PointageDataConsistencyRules implements IPointageDataConsistencyRules {
	
	private final Logger logger = LoggerFactory.getLogger(PointageDataConsistencyRules.class);

	@Autowired
	private IMairieRepository			mairieRepository;

	@Autowired
	private ISirhWSConsumer				sirhWsConsumer;

	@Autowired
	private IAbsWsConsumer				absWsConsumer;

	@Autowired
	private HelperService				helperService;

	@Autowired
	private IDpmRepository				dpmRepository;

	@Autowired
	@Qualifier("sirhPtgDateBlocagePointagePrimeDpm")
	private String						sirhPtgDateBlocagePointagePrimeDpm;

	// -- MESSAGES --//
	public static final String			BASE_HOR_MAX					= "L'agent dépasse sa base horaire";
	public static final String			MALADIE_MSG						= "%s : L'agent est en maladie sur cette période.";
	public static final String			HS_INA_315_MSG					= "L'agent n'a pas droit aux HS sur la période (INA > 315)";
	public static final String			BASE_HOR_00Z_MSG				= "L'agent est en base horaire \"00Z\" sur la période";
	public static final String			INACTIVITE_MSG					= "L'agent n'est pas en activité le %s.";
	public static final String			PAS_AFFECTATION_MSG				= "L'agent n'a pas d'affectation ou la base horaire de pointage n'y est pas renseignée.";
	public static final String			AVERT_MESSAGE_ABS				= "Soyez vigilant, vous avez saisi des primes et/ou heures supplémentaires sur des périodes où l’agent était absent.";
	public static final String			ERROR_7651_MSG					= "";
	public static final String			ERROR_7652_MSG					= "";
	public static final String			ERROR_POINTAGE_PLUS_3_MOIS		= "La semaine sélectionnée est trop ancienne pour être modifiée.";
	public static final String			ERROR_POINTAGE_SUP_DATE_JOUR	= "La semaine sélectionnée est dans le futur et ne peut donc être modifiée.";
	public static final String			HS_TPS_PARTIEL_MSG				= "L'agent est en temps partiel, il ne peut pas avoir plus de %s d'heures supplémentaires.";
	public static final String			ERROR_DATE_POINTAGE				= "Pour le pointage du %s, la date de fin est antérieure à la date de début.";
	public static final String			ERROR_INTERVALLE_POINTAGE		= "Pour le pointage du %s, il faut 30 minutes d'intervalle entre la date de début et la date de fin.";
	public static final String			ERROR_PRIME_SAISIE_J1_POINTAGE	= "Pour la prime %s du %s, la saisie à J+1 n'est pas autorisée.";
	public static final String			ERROR_PRIME_QUANTITE_POINTAGE	= "Pour la prime %s du %s, la quantité ne peut être supérieure à 24.";
	public static final String			ERROR_PRIME_EPANDAGE_QUANTITE	= "Pour la prime %s du %s, la quantité ne peut être supérieure à 2.";
	public static final String			ERROR_PRIME_JOURS_FERIE			= "La prime %s du %s, ne peut être saisie qu'un samedi, dimanche ou jour férié.";
	public static final String			ERROR_PRIME_DPM_INTERVALLE		= "Pour le pointage du %s, il faut au moins 5 minutes comprises entre 5h et 21h.";
	public static final String			ERROR_PRIME_RI_NUIT_INTERVAL	= "Pour le pointage du %s, il faut 8h pleines comprises entre 20h et 6h.";
	public static final String			ERROR_ABSENCE_GREVE				= "Pour l'absence sans titre du %s, le type d'absence ne peut être %s. Ce type est reservé à la DRH.";

	public static final List<String>	ACTIVITE_CODES					= Arrays.asList("01", "02", "03", "04", "23", "24", "60", "61", "62", "63", "64", "65", "66");

	SimpleDateFormat					sdf								= new SimpleDateFormat("dd/MM/yyyy");

	@Override
	public ReturnMessageDto checkMaxAbsenceHebdo(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages, Spcarr carr,
			BaseHorairePointageDto baseDto) {

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

	// #19828 on check maintenant en un appel WS tous les types d absences pour optimiser 
	@Override
	public ReturnMessageDto checkAbsences(ReturnMessageDto srm, Integer idAgent, List<Pointage> pointages) {
		ReturnMessageDto result = new ReturnMessageDto();
		for (Pointage p : pointages) {
			
			boolean isRegimeIndemnitaire = p.getRefPrime() != null &&
					p.getRefPrime().getNoRubr() != null &&
					(p.getRefPrime().getNoRubr().equals(VentilationPrimeService.INDEMNITE_TRAVAIL_NUIT) || 
					p.getRefPrime().getNoRubr().equals(VentilationPrimeService.INDEMNITE_TRAVAIL_DJF));

			// pour chaque pointage on verifie si en recup
			// si oui, on ajoute des erreurs
			// #6843 attention on ne check pas les primes
			if (!RefTypePointageEnum.PRIME.equals(p.getTypePointageEnum()) || isRegimeIndemnitaire) {
				result = absWsConsumer.checkAbsences(idAgent, p.getDateDebut(), p.getDateFin());
			}

			for (String info : result.getInfos()) {
				srm.getInfos().add(info);
			}
			for (String erreur : result.getErrors()) {
				// #47288 : On ne bloque pas la saisie pour les régimes indemnitaires.
				if (isRegimeIndemnitaire)
					srm.getInfos().add(erreur);
				else
					srm.getErrors().add(erreur);
			}
		}
		return srm;
	}

	@Override
	public ReturnMessageDto checkAgentINAAndHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages, Spcarr carr,
			BaseHorairePointageDto baseDto) {

		// cas de la DPM #11622
		EntiteDto service = sirhWsConsumer.getAgentDirection(idAgent, dateLundi);

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.H_SUP) {
				// tester si base Z
				if (null != baseDto && baseDto.getCodeBaseHorairePointage().equals("00Z")) {
					srm.getErrors().add(BASE_HOR_00Z_MSG);
					return srm;
				}
				// cas de la DPM #11622
				// bug hsup #20374
				if ((null != service && service.getSigle().toUpperCase().equals("DPM")) || carr.getSpbarem().getIna() > 315) {
					ptg.setHeureSupRecuperee(true);
				}
			}
		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkAgentInactivity(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages,
			AgentGeneriqueDto ag) {

		if (null != pointages) {
			for (Pointage ptg : pointages) {
				Spadmn adm = mairieRepository.getAgentCurrentPosition(ag, ptg.getDateDebut());

				if (null == adm || !ACTIVITE_CODES.contains(adm.getCdpadm()))
					srm.getErrors().add(String.format(INACTIVITE_MSG, sdf.format(ptg.getDateDebut())));
			}
		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkPrime7650(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME || !ptg.getRefPrime().getNoRubr().equals(7650))
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());

			if (deb.getDayOfWeek() == DateTimeConstants.SATURDAY || deb.getDayOfWeek() == DateTimeConstants.SUNDAY)
				srm.getErrors().add(String.format("La prime 7650 du %s n'est pas valide. Elle ne peut être saisie que du lundi au vendredi.",
						deb.toString("dd/MM/yyyy")));

		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkPrime7651(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME || !ptg.getRefPrime().getNoRubr().equals(7651))
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());

			if (deb.getDayOfWeek() != DateTimeConstants.SATURDAY && deb.getDayOfWeek() != DateTimeConstants.SUNDAY && !sirhWsConsumer.isHoliday(deb)
					&& !sirhWsConsumer.isHoliday(deb.plusDays(1)))
				srm.getErrors()
						.add(String.format(
								"La prime 7651 du %s n'est pas valide. Elle ne peut être saisie qu'un samedi et dimanche, ou alors une veille et jour férié.",
								deb.toString("dd/MM/yyyy")));

		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkPrime7652(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME || !ptg.getRefPrime().getNoRubr().equals(7652))
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());

			if (deb.getDayOfWeek() != DateTimeConstants.SUNDAY && !sirhWsConsumer.isHoliday(deb))
				srm.getErrors().add(String.format("La prime 7652 du %s n'est pas valide. Elle ne peut être saisie qu'un dimanche ou jour férié.",
						deb.toString("dd/MM/yyyy")));

		}

		return srm;
	}

	// #47288 : Prime 7656 : Régime indémnitaire DJF
	protected ReturnMessageDto checkPrime7656(ReturnMessageDto srm, List<Pointage> pointages) {

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME || !ptg.getRefPrime().getNoRubr().equals(7656))
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());

			if (deb.getDayOfWeek() != DateTimeConstants.SUNDAY && !sirhWsConsumer.isHoliday(deb))
				srm.getErrors().add(String.format("La prime 7656 du %s n'est pas valide. Elle ne peut être saisie qu'un dimanche ou jour férié.",
						deb.toString("dd/MM/yyyy")));

		}

		return srm;
	}

	// #47288 : Prime 7657 : Régime indémnitaire du nuit
	// Entre 20h et 06h : Le temps doit être de 8h
	// Entre 21h et 05h : pas de limite basse.
	protected ReturnMessageDto checkPrime7657(ReturnMessageDto srm, List<Pointage> pointages) {

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME || !ptg.getRefPrime().getNoRubr().equals(7657))
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());
			DateTime fin = new DateTime(ptg.getDateFin());
			
			DateTime debutReferenceAM = new DateTime(ptg.getDateDebut()).withHourOfDay(PointageCalculeService.HEURE_DEBUT_REGIME_INDEMNITAIRE_NUIT).withMinuteOfHour(0);
			DateTime debutReferencePM = new DateTime(ptg.getDateDebut()).withHourOfDay(PointageCalculeService.HEURE_FIN_REGIME_INDEMNITAIRE_NUIT).withMinuteOfHour(0);
			DateTime finReferenceAM = new DateTime(ptg.getDateFin()).withHourOfDay(PointageCalculeService.HEURE_DEBUT_REGIME_INDEMNITAIRE_NUIT).withMinuteOfHour(0);
			DateTime finReferencePM = new DateTime(ptg.getDateFin()).withHourOfDay(PointageCalculeService.HEURE_FIN_REGIME_INDEMNITAIRE_NUIT).withMinuteOfHour(0);
			
			// Si l'heure de début ou de fin est comprise entre 06h et 20h, on bloque la saisie.
			// Il faut aussi penser à bloquer si la date de début est le avant 06h, et la date de fin après 20h
			if ((deb.isAfter(debutReferenceAM) && deb.isBefore(debutReferencePM))
					|| (fin.isBefore(finReferencePM) && fin.isAfter(finReferenceAM))
					|| (deb.isBefore(debutReferencePM) && fin.isAfter(finReferenceAM))) {
				srm.getErrors().add(String.format("La prime 7657 du %s n'est pas valide. Elle ne peut être saisie qu'entre 20h et 06h.",deb.toString("dd/MM/yyyy")));
			}
			
			// S'il y a une heure de début comprise entre 20h et 21h ou une heure de début comprise entre 05h et 06h, on vérifie que la saisie fait bien 8h
			else if (((Integer)deb.getHourOfDay()).equals(20) ||
					((Integer)fin.getHourOfDay()).equals(6) ||
					(((Integer)fin.getHourOfDay()).equals(5) && fin.getMinuteOfHour() != 0)) {
				Interval interval = new Interval(deb, fin);
				// Il faut avoir 8h après arrondi. => Entre 7h30(450min) et 8h29(509min)
				if (interval.toDuration().getStandardMinutes() < 450 || interval.toDuration().getStandardMinutes() > 509)
					srm.getErrors().add(String.format(ERROR_PRIME_RI_NUIT_INTERVAL, sdf.format(ptg.getDateDebut())));
			}
			
		}
		
		return srm;
	}

	@Override
	public ReturnMessageDto checkPrime7704(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME || !ptg.getRefPrime().getNoRubr().equals(7704))
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());

			if (ptg.getQuantite() > 2)
				srm.getErrors().add(
						String.format("La prime 7704 du %s n'est pas valide. Sa quantité ne peut être supérieur à 2.", deb.toString("dd/MM/yyyy")));

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
	protected ReturnMessageDto checkInterval(ReturnMessageDto srm, String message, Integer start, Integer codem1, Integer end, Integer codem2,
			List<Pointage> pointages) {

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

			// pour chaque pointage on verifie si en conge annuel
			// si oui, on ajoute des erreurs
			// #19468 attention on ne check pas les primes
			if (!RefTypePointageEnum.PRIME.equals(ptg.getTypePointageEnum())) {
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
		}

		return srm;
	}

	/**
	 * Processes the data consistency of a set of Pointages being input by a
	 * user. It will check the different business rules in order to make sure
	 * they're consistent
	 */
	@Override
	public void processDataConsistency(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages, boolean isFromSIRH) {

		AgentGeneriqueDto ag = sirhWsConsumer.getAgent(idAgent);
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(ag, dateLundi);
		
		// #41417 : Si l'affectation de l'agent commence après le lundi, on va chercher sa carrière avec la date des pointages.
		// On créé une liste pour supprimer les pointages saisis avant l'affectation de l'agent. Sinon ça entraine des exceptions dans la suite du code.
		List<Pointage> pointagesASupprimer = Lists.newArrayList();
		if (carr == null) {
			for (Pointage ptg : pointages) {
				carr = mairieRepository.getAgentCurrentCarriere(ag, ptg.getDateDebut());
				if (carr != null)
					break;
				else
					pointagesASupprimer.add(ptg);
			}
			if (carr == null) {
				logger.warn("Aucune carrière n'a été trouvé pour l'agent matricule " + idAgent + " à cette date : " + sdf.format(dateLundi));
				srm.getErrors().add("Aucune carrière n'a été trouvé pour l'agent " + ag.getDisplayNom() + " " +  ag.getDisplayPrenom() + " à cette date : " + sdf.format(dateLundi));
				return;
			}
		}
		for (Pointage ptg : pointagesASupprimer) {
			pointages.remove(ptg);
		}

		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();
		BaseHorairePointageDto baseDto = sirhWsConsumer.getBaseHorairePointageAgent(idAgent, dateLundi, dateFinSemaine);
		// #19084
		if (null == baseDto) {
			srm.getErrors().add(PAS_AFFECTATION_MSG);
		}

		// #34095 : si c'est une absence de type greve alors on ne peut pas la
		// saisir depuis le kiosque
		checkAbsenceGreve(srm, idAgent, dateLundi, pointages, isFromSIRH);
		checkHeureFinSaisieHSup(srm, idAgent, dateLundi, pointages, carr);
		checkIntervalleDateDebDateFin(srm, idAgent, pointages);
		// DEBUT on check les absences du projet SIRH-ABS-WS
		checkAbsences(srm, idAgent, pointages);
		// FIN on check les types d'absences du projet SIRH-ABS-WS
		checkMaxAbsenceHebdo(srm, idAgent, dateLundi, pointages, carr, baseDto);
		checkAgentINAAndHSup(srm, idAgent, dateLundi, pointages, carr, baseDto);
		checkAgentTempsPartielAndHSup(srm, idAgent, dateLundi, pointages, carr, baseDto, isFromSIRH);
		checkAgentInactivity(srm, idAgent, dateLundi, pointages, ag);
		checkPrime7650(srm, idAgent, dateLundi, pointages);
		checkPrime7651(srm, idAgent, dateLundi, pointages);
		checkPrime7652(srm, idAgent, dateLundi, pointages);
		checkPrime7704(srm, idAgent, dateLundi, pointages);
		// # : Ajout des régimes indemnitaires
		checkPrime7656(srm, pointages);
		checkPrime7657(srm, pointages);
		// #35605 : pour la prime DPM 7714, on ne peut la saisir que sur
		// samed/dimanche et jours féries
		checkPrimeHsup7714(srm, idAgent, dateLundi, pointages);
	}

	private ReturnMessageDto checkAbsenceGreve(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages, boolean isFromSIRH) {
		if (!isFromSIRH) {
			for (Pointage ptg : pointages) {
				if (ptg.getTypePointageEnum() == RefTypePointageEnum.ABSENCE
						&& ptg.getRefTypeAbsence().getIdRefTypeAbsence() == RefTypeAbsenceEnum.GREVE.getValue())
					srm.getErrors().add(String.format(ERROR_ABSENCE_GREVE, sdf.format(ptg.getDateDebut()), RefTypeAbsenceEnum.GREVE.getLib()));

			}
		}
		return srm;

	}

	private ReturnMessageDto checkIntervalleDateDebDateFin(ReturnMessageDto srm, Integer idAgent, List<Pointage> pointages) {

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
			if (null != ptg.getRefPrime() && ptg.getRefPrime().getTypeSaisie().equals(TypeSaisieEnum.PERIODE_HEURES) && fin != null
					&& (fin.getTimeInMillis() - debut.getTimeInMillis()) < 1800000) {
				srm.getErrors().add(String.format(ERROR_INTERVALLE_POINTAGE, sdf.format(ptg.getDateDebut())));
			}
			// pour les primes
			if (ptg.getTypePointageEnum().equals(RefTypePointageEnum.PRIME)) {
				// si 7715 alors saisie j+1 autorisée sinon non
				// #19718 si RENFORT DE GARDE 7717 autorisé egalement
				if (ptg.getRefPrime().getTypeSaisie().equals(TypeSaisieEnum.PERIODE_HEURES)
						&& ptg.getRefPrime().getNoRubr() != VentilationPrimeService.INDEMNITE_DE_ROULEMENT
						&& ptg.getRefPrime().getNoRubr() != VentilationPrimeService.PRIME_RENFORT_GARDE
						&& ptg.getRefPrime().getNoRubr() != VentilationPrimeService.INDEMNITE_TRAVAIL_DJF
						&& ptg.getRefPrime().getNoRubr() != VentilationPrimeService.INDEMNITE_TRAVAIL_NUIT
						&& debut.get(Calendar.DAY_OF_MONTH) != fin.get(Calendar.DAY_OF_MONTH)) {
					srm.getErrors()
							.add(String.format(ERROR_PRIME_SAISIE_J1_POINTAGE, ptg.getRefPrime().getLibelle(), sdf.format(ptg.getDateDebut())));
					// pour les primes de type NOMBRE_HEURE, la quantite ne doit
					// pas depasser 24H
				} else if (ptg.getRefPrime().getTypeSaisie().equals(TypeSaisieEnum.NB_HEURES) && ptg.getQuantite() > 24 * 60) {
					srm.getErrors().add(String.format(ERROR_PRIME_QUANTITE_POINTAGE, ptg.getRefPrime().getLibelle(), sdf.format(ptg.getDateDebut())));
					// PRIME FICTIVE D EPANDAGE pour le SIPRES
					// ne peut pas depasser 2H
				} else if (ptg.getRefPrime().getTypeSaisie().equals(TypeSaisieEnum.NB_HEURES)
						&& ptg.getRefPrime().getNoRubr().equals(VentilationPrimeService.PRIME_EPANDAGE_7716) && ptg.getQuantite() > 2 * 60) {
					srm.getErrors().add(String.format(ERROR_PRIME_EPANDAGE_QUANTITE, ptg.getRefPrime().getLibelle(), sdf.format(ptg.getDateDebut())));
				}

			}
		}
		return srm;

	}

	@Override
	public ReturnMessageDto checkHeureFinSaisieHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages, Spcarr carr) {

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
							.add(String.format(
									"L'heure de fin pour les Heures Sup. saisie le %s ne peut pas dépasser %sh (limite des heures de nuit).",
									new DateTime(ptg.getDateDebut()).toString("dd/MM/yyyy"), heureFinNuit));
				}
			}
		}

		return srm;
	}


	@Override
	public ReturnMessageDto checkAgentTempsPartielAndHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages,
			Spcarr carr, BaseHorairePointageDto baseDto, boolean isFromSIRH) {

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

			// #18728
			int weekBase = (int) (helperService.convertMairieNbHeuresFormatToMinutes(baseDto.getBaseCalculee()));
			// base calculee x 20% = HSup autorisees
			int baseLegaleHsupMax = (int) weekBase + new Double(weekBase * 0.2).intValue();
			// mais si temps effectif de travail hebdomadaire > 39h
			// alors on bloque a 39h
			baseLegaleHsupMax = baseLegaleHsupMax > 39 * 60 ? 39 * 60 : baseLegaleHsupMax;

			// #28723 arrondir au 1/4 heure superieur le plus proche
			if (baseLegaleHsupMax % 15 != 0) {
				baseLegaleHsupMax = baseLegaleHsupMax - (baseLegaleHsupMax % 15) + 15;
			}

			if ((weekBase + minutesHSupWeek - minutesAbsWeek) > baseLegaleHsupMax) {

				Double nombreMinutes = new Double(baseLegaleHsupMax - weekBase + minutesAbsWeek);
				// #28723 arrondir au 1/4 heure superieur le plus proche
				if (nombreMinutes % 15 != 0) {
					nombreMinutes = nombreMinutes - (nombreMinutes % 15) + 15;
				}
				String nombre = helperService.formatMinutesToString(nombreMinutes.intValue());

				String msg = String.format(HS_TPS_PARTIEL_MSG, nombre);
				// #20056
				if (isFromSIRH) {
					srm.getInfos().add(msg);
				} else {
					srm.getErrors().add(msg);
				}
			}
		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkDateLundiNotSuperieurDateJour(ReturnMessageDto srm, Date dateLundi) {
		if (dateLundi.after(new Date())) {
			srm.getErrors().add(String.format(ERROR_POINTAGE_SUP_DATE_JOUR));
		}
		return srm;
	}

	@Override
	public ReturnMessageDto checkDateNotSuperieurDateJour(ReturnMessageDto srm, Date date, String errorMessage) {
		if (date.after(new Date())) {
			srm.getErrors().add(errorMessage);
		}
		return srm;
	}

	@Override
	public ReturnMessageDto checkPrimeHsup7714(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {


		for (Pointage ptg : pointages) {
			DateTime dateDebut = new DateTime(ptg.getDateDebut());

			// si c'est une Hsup et que l'agent à la prime DPM sur son
			// affectation et qu'il n'a fait aucun choix alors on bloque
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.H_SUP) {
				if (dateDebut.getDayOfWeek() == DateTimeConstants.SATURDAY || dateDebut.getDayOfWeek() == DateTimeConstants.SUNDAY
						|| sirhWsConsumer.isHoliday(dateDebut)) {
					List<Integer> norubrs = sirhWsConsumer.getPrimePointagesByAgent(idAgent, ptg.getDateDebut(), ptg.getDateDebut());
					if (norubrs.contains(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM_SAMEDI)
							|| norubrs.contains(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM_DJF)
							|| norubrs.contains(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM)) {
						// si l'agent n'a pas fait de choix, on sort
						DpmIndemChoixAgent choixAgent = dpmRepository.getDpmIndemChoixAgent(idAgent, new DateTime(ptg.getDateDebut()).getYear());

						if (null == choixAgent) {
							srm.getErrors()
									.add("Aucun choix de l agent pour la prime DPM => aucune heure sup. ne peut être saisie.");
						}

					}
				}
			}
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.PRIME
					&& ptg.getRefPrime().getNoRubr().equals(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM)) {
				// si l'agent n'a pas fait de choix, on sort
				DpmIndemChoixAgent choixAgent = dpmRepository.getDpmIndemChoixAgent(idAgent, new DateTime(ptg.getDateDebut()).getYear());

				if (null == choixAgent) {
					srm.getErrors()
							.add("Aucun choix de l agent pour la prime DPM => aucune prime. ne peut être saisie.");
				}

				// la prime doit etre un samedi, dimanche ou jour ferie/chomé
				if (!(dateDebut.getDayOfWeek() == DateTimeConstants.SATURDAY || dateDebut.getDayOfWeek() == DateTimeConstants.SUNDAY
						|| sirhWsConsumer.isHoliday(dateDebut))) {
					srm.getErrors().add(String.format(ERROR_PRIME_JOURS_FERIE, "INDEMNITE FORFAITAIRE TRAVAIL DPM", sdf.format(ptg.getDateDebut())));
				}
				// l interval de la deliberation pour la prime est de 5h a 21h
				int dayTotalMinutes = helperService.calculMinutesPointageInInterval(ptg,
						new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
						new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0));
				// si au on a pas 5min alors on rejete
				if (dayTotalMinutes < 5) {
					srm.getErrors().add(String.format(ERROR_PRIME_DPM_INTERVALLE, sdf.format(ptg.getDateDebut())));
				}
			}

		}

		return srm;
	}

	@Override
	public ReturnMessageDto checkDateLundiAnterieurA3MoisWithPointage(ReturnMessageDto srm, Date dateLundi, Pointage ptg) {
		GregorianCalendar calStr1 = new GregorianCalendar();
		calStr1.setTime(new Date());
		calStr1.add(GregorianCalendar.MONTH, -3);
		calStr1.add(GregorianCalendar.WEEK_OF_YEAR, -1); // back to
															// previous
															// week
		calStr1.set(GregorianCalendar.DAY_OF_WEEK, Calendar.MONDAY); // jump
																		// to
																		// next
																		// monday
		
		if (ptg!=null && RefTypePointageEnum.PRIME.equals(ptg.getTypePointageEnum()) && ptg.getRefPrime() != null
				&& ptg.getRefPrime().getNoRubr().equals(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM)) {

			if (dateLundi.before(calStr1.getTime())) {
				// si on est dans le cas d'un pointage au dela de 3
				// mois
				if (ptg.getDateDebut().before(new DateTime(2016, 4, 1, 0, 0, 0).toDate())) {
					srm.getErrors().add(String.format(ERROR_POINTAGE_PLUS_3_MOIS));
					return srm;
				} else {
					try {
						if (new Date().after(new SimpleDateFormat("dd/MM/yyyy").parse(sirhPtgDateBlocagePointagePrimeDpm))) {
							srm.getErrors().add(String.format(ERROR_POINTAGE_PLUS_3_MOIS));
							return srm;
						}
					} catch (ParseException e) {

					}
				}
			}
		} else {
			if (dateLundi.before(calStr1.getTime())) {
				srm.getErrors().add(String.format(ERROR_POINTAGE_PLUS_3_MOIS));
				return srm;
			}
		}
		return srm;
	}
}
