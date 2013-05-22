package nc.noumea.mairie.ptg.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class HelperService {

	public Date getCurrentDate() {
		return new Date();
	}

	public int getWeekDayFromDateBase0(final Date date) {
		return new DateTime(date).dayOfWeek().get() - 1;
	}

	public boolean isDateAMonday(final Date date) {
		return new DateTime(date).dayOfWeek().get() == 1;
	}

	public String getWeekStringFromDate(final Date date) {
		DateTime d = new DateTime(date);
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/YYYY");
		return String.format("S%s - %s au %s", d.getWeekOfWeekyear(), sf.format(date), sf.format(d.plusDays(7).toDate()));
	}
}
