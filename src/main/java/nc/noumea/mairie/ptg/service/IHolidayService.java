package nc.noumea.mairie.ptg.service;

import org.joda.time.DateTime;

public interface IHolidayService {

	boolean isHoliday(DateTime day);
	
}
