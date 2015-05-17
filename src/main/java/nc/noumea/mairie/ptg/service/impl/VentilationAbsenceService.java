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
		
		List<Pointage> listPointagesDejaComptabilises = new ArrayList<Pointage>();

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
			
			// #15518
			// on cherche s il y a une 2e absence sur la meme journee
			// faire attention que le pointage n ait pas deja ete comptabilise
			if(!listPointagesDejaComptabilises.contains(ptg)) {
				listPointagesDejaComptabilises.add(ptg);
				// on cherche si 2e absence
				Pointage secondeAbsence = getSecondAbsenceSameDay(absPointages, ptg);
				if(null != secondeAbsence
						&& !listPointagesDejaComptabilises.contains(secondeAbsence)) {
					listPointagesDejaComptabilises.add(secondeAbsence);
					
					double minutesSecondeAbsence = 
							new Interval(new DateTime(secondeAbsence.getDateDebut()), new DateTime(secondeAbsence.getDateFin()))
								.toDuration().getStandardMinutes();
					minutes += minutesSecondeAbsence;
				}
					
				if (0 < minutes && minutes <= 60) {
					result.addNombreAbsenceInferieur1(1);
				} else if (60 < minutes && minutes <= 240) {
					result.addNombreAbsenceEntre1Et4(1);
				} else if(240 < minutes) {
					result.addNombreAbsenceSuperieur1(1);
				}
			}
		}

		return result;
	}
	
	private Pointage getSecondAbsenceSameDay(List<Pointage> absPointages, Pointage firstPointage) {
		// #14681 attention, dans l AS400, il ne peut y avoir qu une seule ligne
		// mais dans le nouveau systeme, l agent peut saisir 2 absences
		// on cherche une 2e absence sur le meme jour
		DateTime dateTimeDebutPtg = new DateTime(firstPointage.getDateDebut());
		for(Pointage ptgBis : absPointages) {
			if (ptgBis.getTypePointageEnum() != RefTypePointageEnum.ABSENCE)
				continue;
			
			if(firstPointage.getIdPointage().equals(ptgBis.getIdPointage()))
				continue;
			
			DateTime dateTimeDebutPtgBis = new DateTime(ptgBis.getDateDebut());
			if(dateTimeDebutPtg.getDayOfYear() == dateTimeDebutPtgBis.getDayOfYear()) {
				return ptgBis;
			}
		}
		
		return null;
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
