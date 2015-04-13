package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypeAbsenceEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.service.IVentilationAbsenceService;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

@Service
public class VentilationAbsenceService implements IVentilationAbsenceService {

	@Override
	public VentilAbsence processAbsenceAgent(Integer idAgent, List<Pointage> pointages, Date dateLundi) {

		List<Pointage> absPointages = getAbsencePointages(pointages);

		if (absPointages.size() == 0)
			return null;

		VentilAbsence result = new VentilAbsence();
		result.setIdAgent(idAgent);
		result.setDateLundi(dateLundi);
		result.setEtat(EtatPointageEnum.VENTILE);

		for (Pointage ptg : absPointages) {
			double minutes = new Interval(new DateTime(ptg.getDateDebut()), new DateTime(ptg.getDateFin()))
					.toDuration().getStandardMinutes();

			switch (RefTypeAbsenceEnum.getRefTypeAbsenceEnum(ptg.getRefTypeAbsence().getIdRefTypeAbsence())) {
				case CONCERTEE:
					result.addMinutesConcertee((int) minutes);
					break;
				case NON_CONCERTEE:
					result.addMinutesNonConcertee((int) minutes);
					break;
				case IMMEDIATE:
					result.addMinutesImmediate((int) minutes);
					break;
			}
			
			if (0 < minutes && minutes <= 60) {
				result.addNombreAbsenceInferieur1(1);
			} else if (60 < minutes && minutes <= 240) {
				result.addNombreAbsenceEntre1Et4(1);
			} else if(240 < minutes) {
				result.addNombreAbsenceSuperieur1(1);
			}
		}

		return result;
	}

	private List<Pointage> getAbsencePointages(List<Pointage> pointages) {

		List<Pointage> result = new ArrayList<Pointage>();

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.ABSENCE)
				result.add(ptg);
		}

		return result;
	}
}
