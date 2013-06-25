package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.service.IVentilationAbsenceService;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

@Service
public class VentilationAbsenceService implements IVentilationAbsenceService {

	@Override
	public VentilAbsence processAbsenceAgent(Integer idAgent, List<Pointage> pointages, Date dateDebutMois) {
		
		VentilAbsence result = new VentilAbsence();
		result.setIdAgent(idAgent);
		result.setDateDebutMois(dateDebutMois);
		result.setEtat(EtatPointageEnum.VENTILE);
		
		for (Pointage ptg : getAbsencePointages(pointages)) {
			double minutes = new Interval(new DateTime(ptg.getDateDebut()), new DateTime(ptg.getDateFin())).toDuration().getStandardMinutes();
			if (ptg.getAbsenceConcertee())
				result.addMinutesConcertee((int) minutes);
			else
				result.addMinutesNonConcertee((int) minutes);
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
