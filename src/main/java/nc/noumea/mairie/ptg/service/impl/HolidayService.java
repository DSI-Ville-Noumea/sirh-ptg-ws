package nc.noumea.mairie.ptg.service.impl;

import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.service.IHolidayService;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HolidayService implements IHolidayService {

	@Autowired
	private IMairieRepository mairieRepository;

	public boolean isHoliday(DateTime day) {
		return mairieRepository.isJourHoliday(day.toDate());
	}
}
