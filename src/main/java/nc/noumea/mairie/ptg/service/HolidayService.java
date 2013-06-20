package nc.noumea.mairie.ptg.service;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Service
public class HolidayService implements IHolidayService {

	public boolean isHoliday(DateTime day) {
		return false;
	}
}
