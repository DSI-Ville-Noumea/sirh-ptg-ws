package nc.noumea.mairie.ptg.service.impl;

import java.util.Date;

import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.service.IHolidayService;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HolidayService implements IHolidayService {

	@Autowired
	private ISirhRepository sirhRepository;

	public boolean isHoliday(DateTime day) {
		return isHoliday(day.toDate());
	}

	@Override
	public boolean isHoliday(LocalDate day) {
		return isHoliday(day.toDate());
	}

	@Override
	public boolean isHoliday(Date day) {
		return sirhRepository.isJourHoliday(day);
	}
}
