package nc.noumea.mairie.ptg.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.sirh.dto.JourDto;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;
import org.springframework.stereotype.Service;

@Service
public class HelperService {

	public Date getCurrentDate() {
		return new Date();
	}

	public Date getCurrentDatePlus1Mois() {
		return new DateTime().plusMonths(1).toDate();
	}

	public boolean isDateAMonday(final Date date) {
		return new DateTime(date).dayOfWeek().get() == 1;
	}

	public int getWeekDayFromDateBase0(final Date date) {
		return new DateTime(date).dayOfWeek().get() - 1;
	}

	public String getWeekStringFromDate(final Date date) {
		DateTime d = new DateTime(date);
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/YYYY");
		return String.format("S%s - %s au %s", d.getWeekOfWeekyear(), sf.format(date),
				sf.format(d.plusDays(7).toDate()));
	}

	private static SimpleDateFormat mairieDateFormat = new SimpleDateFormat("yyyyMMdd");

	public Date getDateFromMairieInteger(Integer dateAsInteger) {
		if (dateAsInteger == null || dateAsInteger.equals(0))
			return null;

		try {
			return mairieDateFormat.parse(String.valueOf(dateAsInteger));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Integer getIntegerDateMairieFromDate(Date date) {
		return date == null ? 0 : Integer.parseInt(mairieDateFormat.format(date));
	}

	private static SimpleDateFormat mairieMonthDateFormat = new SimpleDateFormat("yyyyMM");

	public Date getMonthDateFromMairieInteger(Integer monthDateAsInteger) {
		if (monthDateAsInteger == null || monthDateAsInteger.equals(0))
			return null;

		try {
			return mairieMonthDateFormat.parse(String.valueOf(monthDateAsInteger));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Integer getIntegerMonthDateMairieFromDate(Date date) {
		return date == null ? 0 : Integer.parseInt(mairieMonthDateFormat.format(date));
	}

	public Integer getMairieMatrFromIdAgent(Integer idAgent) {
		return idAgent - 9000000;
	}

	public Integer getIdAgentFromMairieMatr(Integer noMatr) {
		if (null != noMatr && noMatr < 9000000) {
			return noMatr + 9000000;
		}

		return noMatr;
	}

	public int convertMairieNbHeuresFormatToMinutes(Double nbHeuresMairies) {

		if (null == nbHeuresMairies || nbHeuresMairies == 0.0d)
			return 0;

		BigDecimal v = new BigDecimal(String.valueOf(nbHeuresMairies));
		int nbHours = v.intValue();
		int nbMinutes = v.subtract(new BigDecimal(nbHours)).multiply(new BigDecimal(100)).intValue();

		return nbHours * 60 + nbMinutes;
	}

	public double convertMinutesToMairieNbHeuresFormat(int minutes) {

		if (minutes == 0)
			return 0.0d;

		int nbHours = minutes / 60;
		int nbMinutes = minutes - (nbHours * 60);

		double result = (double) (nbHours * 100 + nbMinutes) / 100;

		return result;
	}

	public String formatMinutesToString(Integer minutes) {

		int absMinutes = Math.abs(minutes);
		int nbMinutesModulo = absMinutes % 60;
		int nbHours = absMinutes / 60;

		StringBuilder sb = new StringBuilder();
		if (minutes < 0)
			sb.append("- ");

		if (nbHours > 0)
			sb.append(String.format("%sh", nbHours));

		if (nbMinutesModulo > 0)
			sb.append(String.format("%sm", nbMinutesModulo));

		return sb.toString();
	}

	public String formatMinutesToStringForEVP(Integer minutes) {
		if (minutes == null || minutes == 0)
			return null;
		double absMinutes = Math.abs(minutes);
		double nbHours = absMinutes / 60d;

		double roundedValue = Math.round(nbHours * 100.0) / 100.0;
		String stringValue = String.valueOf(roundedValue);
		
		stringValue = stringValue.replace('.', ',');
	
		if (stringValue.indexOf(',') == -1) {
			stringValue += ",00";
		} else if (stringValue.indexOf(',') == stringValue.length()-2) {
			stringValue += "0";
		}
	
		return stringValue;
	}

	private static PeriodFormatter formatter = new PeriodFormatterBuilder().appendDays().appendSuffix("j")
			.appendHours().appendSuffix("h").appendMinutes().appendSuffix("m").toFormatter();

	public String formatMinutesToString(Date dateDebut, Date dateFin) {
		return formatter.print(new Period(new DateTime(dateDebut), new DateTime(dateFin)));
	}

	public TypeChainePaieEnum getTypeChainePaieFromStatut(AgentStatutEnum statut) {
		if (statut == AgentStatutEnum.CC)
			return TypeChainePaieEnum.SCV;
		else
			return TypeChainePaieEnum.SHC;
	}
	
	public Integer getDureeBetweenDateDebutAndDateFin(Date dateDebut, Date dateFin) {
		
		DateTime startDate = new DateTime(dateDebut);
		DateTime endDate = new DateTime(dateFin);
		
		Interval interval = new Interval(startDate, endDate);
		return new Long(interval.toDuration().getStandardMinutes()).intValue();
	}
	
	public List<Date> getListDateLundiBetWeenTwoDate(Date dateDebut, Date dateFin) {

		List<Date> result = new ArrayList<Date>();
		// on calcule le nombre de vendredi
		DateTime startDate = new DateTime(dateDebut).withHourOfDay(0).withMinuteOfHour(0)
				.withSecondOfMinute(0); // on met les heures et minutes a
										// zero afin de bien comptabiliser
										// le nombre de vendredi dans la
										// boucle while
		DateTime endDate = new DateTime(dateFin);

		// on boucle sur tous les jours de la periode
		while (startDate.isBefore(endDate) || startDate.equals(endDate)) {

			// si jeudi
			if (startDate.getDayOfWeek() == DateTimeConstants.MONDAY) {
				result.add(startDate.toDate());
			}

			startDate = startDate.plusDays(1);
		}
		
		return result;
	}
	
	public Date getDatePremierJourOfMonth(Date dateMonth) {
		
		DateTime date = new DateTime(dateMonth)
		    .withDayOfMonth(1)
			.withHourOfDay(0)
			.withMinuteOfHour(0)
			.withSecondOfMinute(0)
			.withMillisOfSecond(0);

		return date.toDate();
	}
	
	public Date getDatePremierJourOfMonthSuivant(Date dateMonth) {
		
		DateTime date = new DateTime(dateMonth)
		    .withDayOfMonth(1)
			.withHourOfDay(0)
			.withMinuteOfHour(0)
			.withSecondOfMinute(0)
			.withMillisOfSecond(0).plusMonths(1);

		return date.toDate();
	}
	
	public Date getMonthOfVentilation(Date dateVentil) {
		Date previous = getDatePremierJourOfMonth(dateVentil);
		Date next = getDatePremierJourOfMonthSuivant(dateVentil);
	
		if ((dateVentil.getTime() - previous.getTime()) > (next.getTime() - dateVentil.getTime())) {
			return next;
		} else {
			return previous;
		}
	}
	
	public Date getDatePremierJourOfMonthPrecedent(Date dateMonth) {
		
		DateTime date = new DateTime(dateMonth)
		    .withDayOfMonth(1)
			.withHourOfDay(0)
			.withMinuteOfHour(0)
			.withSecondOfMinute(0)
			.withMillisOfSecond(0).minusMonths(1);

		return date.toDate();
	}
	
	public Date getDateDernierJourOfMonth(Date dateMonth) {
		
		DateTime date = new DateTime(dateMonth)
			.dayOfMonth().withMaximumValue()
			.withHourOfDay(23)
			.withMinuteOfHour(59)
			.withSecondOfMinute(59)
			.withMillisOfSecond(0);

		return date.toDate();
	}
	
	public Date getDateDernierJourOfMonthSuivant(Date dateMonth) {
		
		DateTime date = new DateTime(dateMonth)
			.dayOfMonth().withMaximumValue()
			.withHourOfDay(23)
			.withMinuteOfHour(59)
			.withSecondOfMinute(59)
			.withMillisOfSecond(0).plusMonths(1);

		return date.toDate();
	}
	
	public Date getDateDernierJourOfMonthPrecedent(Date dateMonth) {
		
		DateTime date = new DateTime(dateMonth)
			.dayOfMonth().withMaximumValue()
			.withHourOfDay(23)
			.withMinuteOfHour(59)
			.withSecondOfMinute(59)
			.withMillisOfSecond(0).minusMonths(1);

		return date.toDate();
	}

	public boolean isJourHoliday(List<JourDto> listJoursFeries, Date dateJour) {
		if (null != listJoursFeries) {
			DateTime dateTimeJour = new DateTime(dateJour);
			for (JourDto jourFerie : listJoursFeries) {
				DateTime dateTimeFerie = new DateTime(jourFerie.getJour());
				if (dateTimeFerie.getDayOfYear() == dateTimeJour.getDayOfYear()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Calcul le nombre de minutes d un pointage avec date de debut et de fin
	 * compris dans l interval defini par les parametres date de debut et date de fin
	 * 
	 * @param ptg Pointage (date de debut et date de fin obligatoire)
	 * @param heureDebut Heure de debut de l interval
	 * @param heureFin Heure de fin de l interval
	 * @return Integer Le nombre de minutes
	 */
	public int calculMinutesPointageInInterval(Pointage ptg, LocalTime heureDebut, LocalTime heureFin) {
		
		if(null == ptg
			|| null == ptg.getDateDebut()
			|| null == ptg.getDateFin()
			|| null == heureDebut
			|| null == heureFin) {
			throw new IllegalArgumentException("Date de debut et de fin du pointage non renseignees.");
		}
		
		int result = 0;
		
		LocalDate datePointage = new DateTime(ptg.getDateDebut()).toLocalDate();
		
		// l interval de la deliberation pour la prime est de 5h a 21h
		Interval pointageInterval = new Interval(
				new DateTime(
					datePointage.getYear(),
					datePointage.getMonthOfYear(), datePointage.getDayOfMonth(), 
					heureDebut.getHourOfDay(), 0, 0), 
				new DateTime(
					datePointage.getYear(), datePointage.getMonthOfYear(), 
					datePointage.getDayOfMonth(), 
					heureFin.getHourOfDay(), 0, 0));
		
		Interval inputInterval = new Interval(new DateTime(ptg.getDateDebut()), new DateTime(ptg.getDateFin()));
		
		// on compare l interval du pointage et du celui de la deliberation
		Interval overlap = pointageInterval.overlap(inputInterval);
		
		// on additionne les minutes
		result += overlap == null ? 0 : overlap.toDuration().getStandardMinutes();
		
		return result;
	}
}
