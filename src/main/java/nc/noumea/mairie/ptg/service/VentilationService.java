package nc.noumea.mairie.ptg.service;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.domain.Spbase;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilHsup;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.springframework.stereotype.Service;

@Service
public class VentilationService implements IVentilationService {

	private static int HEURE_JOUR_DEBUT = 4;
	private static int HEURE_JOUR_FIN = 21;
	private static int NB_HS_SIMPLE = 3;
	
	public VentilHsup processHSupFonctionnaire(Integer idAgent, Spcarr carr, List<Pointage> pointages) {
		
		DateTime dateLundi = new DateTime(pointages.get(0).getDateLundi());
		
		VentilHsup result = new VentilHsup();
		result.setIdAgent(idAgent);
		result.setDateLundi(dateLundi.toDate());
		
		Spbase base = carr.getSpbase();
		Double weekBase = base.getNbashh();
		Double weekHours = 0.0;
		
		List<Interval> allHeuresSupIntervals = new ArrayList<Interval>();
		
		// For each day of the week, get related pointages of HSup and ABS to count them
		// and compare them to the agent Hour Base
		for (int i = 0; i < 7; i++) {

			Double dayBase = base.getDayBase(i);
			weekHours += dayBase;
			DateTime dday = dateLundi.plusDays(i);
			
			for (Pointage ptg : getPointagesForDay(pointages, dday)) {
				
				DateTime startDate = new DateTime(ptg.getDateDebut());
				DateTime endDate = new DateTime(ptg.getDateFin());
				Double nbHours = Minutes.minutesBetween(startDate, endDate).getMinutes() / 60d;
				
				if (ptg.getTypePointageEnum() == RefTypePointageEnum.H_SUP) {
					result.setHHorsContrat(result.getHHorsContrat() + nbHours);
					weekHours += nbHours;
					allHeuresSupIntervals.add(new Interval(startDate, endDate));
				}
				
				if (ptg.getTypePointageEnum() == RefTypePointageEnum.ABSENCE) {
					result.setHAbsences(result.getHAbsences() + nbHours);
				}
			}
			
		}

		Double nbHeuresSupToProcess = weekHours - weekBase - result.getHAbsences();
		result.setHSup(nbHeuresSupToProcess);

		// No heures sup, stop the process here
		if (nbHeuresSupToProcess <= 0)
			return result;

		// For all heures sup divide them into HS DJF, HS Simple and HS Composees
		// Then the HS Composees can be either Jour (HS Composees) and Nuit (HS Nuit), 
		// Starting at the end of the week and the end of the hours interval 
		// until the number of week hours reaches the base number for the week (from sunday night to monday morning)
		// because only the latest HS counts
		Double remainingHoursToProcess = nbHeuresSupToProcess;
		
		for(int i = (allHeuresSupIntervals.size() - 1) ; i >= 0 && remainingHoursToProcess > 0 ; i--) {
		
			// Retrieve the HS interval
			Interval interval = allHeuresSupIntervals.get(i);
			
			// If it's a SUNDAY, count the hours as HS DJF
			if (interval.getStart().getDayOfWeek() == 7) { // SUNDAY
				Double nbHoursDjf = interval.toDuration().getStandardMinutes() / 60d;
				Double hoursToCount = nbHoursDjf <= remainingHoursToProcess ? nbHoursDjf : remainingHoursToProcess;
				result.setHsdjf(result.getHsdjf() + hoursToCount);
				remainingHoursToProcess -= hoursToCount;
				continue;
			}
			
			Double totalHSupToCount = interval.toDuration().getStandardMinutes() / 60d;
			
			// If we're into the first 3 hours to consider as HS SIMPLE
			if (remainingHoursToProcess - NB_HS_SIMPLE <= 0) {
				Double heuresSimpleToCount =  (totalHSupToCount + result.getHSimple()) > NB_HS_SIMPLE ? (NB_HS_SIMPLE - result.getHSimple()) : totalHSupToCount;
				result.setHSimple(result.getHSimple() + heuresSimpleToCount);
				remainingHoursToProcess -= heuresSimpleToCount;
				
				// As we took some HS as Heure Simple, we need to remove them from the next
				// calculation of HS Composees. Se we shift the endDate of the interval by the
				// number of hours we just took (we reduce the interval of what we took as simple a hours)
				interval = new Interval(interval.getStart(), interval.getEnd().minusMinutes((int) (heuresSimpleToCount * 60d)));
			}
			
			// Otherwise, we can count the hours as HS COMPOSEES (being either HS NUIT or HS JOUR = composees)

			// Create the HS Jour interval for that day (the hours between which hours are considered HS JOUR)
			Interval hSupJourInterval = new Interval(
					new DateTime(interval.getStart().getYear(), interval.getStart().getMonthOfYear(), interval.getStart().getDayOfMonth(), HEURE_JOUR_DEBUT, 0, 0), 
					new DateTime(interval.getStart().getYear(), interval.getStart().getMonthOfYear(), interval.getStart().getDayOfMonth(), HEURE_JOUR_FIN, 0, 0));
			
			// Calculate the overlap of the HS to determine what to be counted as HS Nuit and HS Jour
			Interval hSupJourOverlap = hSupJourInterval.overlap(interval);
			Double nbHoursComposees = hSupJourOverlap == null ? 0d : hSupJourOverlap.toDuration().getStandardMinutes() / 60d;
			Double nbHoursNuit = (interval.toDuration().getStandardMinutes() / 60d) - nbHoursComposees;
			
			// Depending on whether the period started during day time or night time, 
			// We start by counting hours differently
			
			// Either the HS Nuits
			if (hSupJourInterval.contains(interval.getStart())) {
				
				Double hoursToCount = nbHoursNuit <= remainingHoursToProcess ? nbHoursNuit : remainingHoursToProcess;
				result.setHsNuit(result.getHsNuit() + hoursToCount);
				remainingHoursToProcess -= hoursToCount;
				
				hoursToCount = nbHoursComposees <= remainingHoursToProcess ? nbHoursComposees : remainingHoursToProcess;
				result.setHComposees(result.getHComposees() + hoursToCount);
				remainingHoursToProcess -= hoursToCount;
			
			} 
			// Or the HS Jour
			else {
				
				Double hoursToCount = nbHoursComposees <= remainingHoursToProcess ? nbHoursComposees : remainingHoursToProcess;
				result.setHComposees(result.getHComposees() + hoursToCount);
				remainingHoursToProcess -= hoursToCount;
				
				hoursToCount = nbHoursNuit <= remainingHoursToProcess ? nbHoursNuit : remainingHoursToProcess;
				result.setHsNuit(result.getHsNuit() + hoursToCount);
				remainingHoursToProcess -= hoursToCount;
			}
		}
		
		return result;
	}
	
	public List<Pointage> getPointagesForDay(List<Pointage> pointages, DateTime day) {
		
		List<Pointage> result = new ArrayList<Pointage>();
		
		for (Pointage ptg : pointages) {
			DateTime dday = new DateTime(ptg.getDateDebut());
			if (dday.getDayOfYear() == day.getDayOfYear() && dday.getYear() == day.getYear())
				result.add(ptg);
		}
		
		return result;
	}
	
}
