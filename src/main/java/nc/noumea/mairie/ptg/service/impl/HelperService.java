package nc.noumea.mairie.ptg.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class HelperService {

	public Date getCurrentDate() {
		return new Date();
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
		return String.format("S%s - %s au %s", d.getWeekOfWeekyear(), sf.format(date), sf.format(d.plusDays(7).toDate()));
	}
	
	private static SimpleDateFormat mairieDateFormat = new SimpleDateFormat("yyyyMMdd");
	
	public Date getDateFromMairieInteger(Integer dateAsInteger) {
		if (dateAsInteger == null || dateAsInteger.equals(0))
			return null;
		
		try {
			return mairieDateFormat.parse(String.valueOf(dateAsInteger));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Integer getIntegerDateMairieFromDate(Date date) {
		return date == null ? 0 : Integer.parseInt(mairieDateFormat.format(date));
	}
	
	public Integer getMairieMatrFromIdAgent(Integer idAgent) {
		return idAgent - 9000000;
	}
	
	public int convertMairieNbHeuresFormatToMinutes(Double nbHeuresMairies) {
		
		if (nbHeuresMairies == 0.0d)
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
}
