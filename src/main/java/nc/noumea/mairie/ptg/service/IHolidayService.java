package nc.noumea.mairie.ptg.service;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public interface IHolidayService {

	boolean isHoliday(DateTime day);
	boolean isHoliday(LocalDate day);
	boolean isHoliday(Date day);
	
}
