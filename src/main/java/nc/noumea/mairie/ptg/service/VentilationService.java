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
	private static double BASE_HEBDO_LEGALE = 39;
	
	public VentilHsup processHSupFonctionnaire(Integer idAgent, Spcarr carr, List<Pointage> pointages) {
		
		DateTime dateLundi = new DateTime(pointages.get(0).getDateLundi());
		
		VentilHsup result = new VentilHsup();
		result.setIdAgent(idAgent);
		result.setDateLundi(dateLundi.toDate());
		
		Double absencesHours = 0.0;
		// First retrieve all the absences in the Pointages
		for (Pointage ptg : pointages) {
			
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.ABSENCE) {
				DateTime startDate = new DateTime(ptg.getDateDebut());
				DateTime endDate = new DateTime(ptg.getDateFin());
				Double nbHours = Minutes.minutesBetween(startDate, endDate).getMinutes() / 60d;
				absencesHours += nbHours;
			}
		}
		

		Spbase base = carr.getSpbase();
		Double weekBase = base.getNbashh();
		Double weekHours = 0.0 - absencesHours;
		
		//List<Interval> allHeuresSupIntervals = new ArrayList<Interval>();
		
		// For each day of the week, get related pointages of HSup to count them
		// and compare to the agent Hour Base
		for (int i = 0; i < 7; i++) {

			Double dayBase = base.getDayBase(i);
			DateTime dday = dateLundi.plusDays(i);
			
			// Compute the new total of hours for the agent
			weekHours += dayBase;
				
			// Compute the number of HeuresSup considering the week normal hours
			Double nbHeuresSup = weekBase - (weekHours + dayBase);
			nbHeuresSup = nbHeuresSup < 0 ? 0 : nbHeuresSup;
			
			processHeuresSup(result, weekBase, weekHours, dday, nbHeuresSup);
			
			for (Pointage ptg : getPointagesHSupForDay(pointages, dday)) {
								
				DateTime startDate = new DateTime(ptg.getDateDebut());
				DateTime endDate = new DateTime(ptg.getDateFin());
				Double nbHours = Minutes.minutesBetween(startDate, endDate).getMinutes() / 60d;
				
				// Compute the new total of hours for the agent
				result.setHHorsContrat(result.getHHorsContrat() + nbHours);
				weekHours += nbHours;

				// Compute the number of HeuresSup considering the week normal hours
				nbHeuresSup = weekBase - (weekHours + nbHours);
				nbHeuresSup = nbHeuresSup < 0 ? 0 : nbHeuresSup;
				
				processHeuresSup(result, weekBase, weekHours, dday, new Interval(startDate, endDate));
			}
			
		}

//		Double nbHeuresSupToProcess = weekHours - weekBase - result.getHAbsences();
//		result.setHSup(nbHeuresSupToProcess);
//
//		// No heures sup, stop the process here
//		if (nbHeuresSupToProcess <= 0)
//			return result;
//
//		// For all heures sup divide them into HS DJF, HS Simple, HS Normales and HS Composees
//		// Then the HS Composees can be either Jour (HS Composees) and Nuit (HS Nuit), 
//		// Starting at the end of the week and the end of the hours interval 
//		// until the number of week hours reaches the base number for the week (from sunday night to monday morning)
//		// because only the latest HS counts
//		Double remainingHoursToProcess = nbHeuresSupToProcess;
//		
//		for(int i = (allHeuresSupIntervals.size() - 1) ; i >= 0 && remainingHoursToProcess > 0 ; i--) {
//		
//			// Retrieve the HS interval
//			Interval interval = allHeuresSupIntervals.get(i);
//			
//			// If it's a SUNDAY, count the hours as HS DJF
//			if (interval.getStart().getDayOfWeek() == 7) { // SUNDAY
//				Double nbHoursDjf = interval.toDuration().getStandardMinutes() / 60d;
//				Double hoursToCount = nbHoursDjf <= remainingHoursToProcess ? nbHoursDjf : remainingHoursToProcess;
//				result.setHsdjf(result.getHsdjf() + hoursToCount);
//				remainingHoursToProcess -= hoursToCount;
//				continue;
//			}
//			
//			Double totalHSupToCount = interval.toDuration().getStandardMinutes() / 60d;
//			
//			// If we're into the first 3 hours to consider as HS SIMPLE
//			if (remainingHoursToProcess - NB_HS_SIMPLE <= 0) {
//				Double heuresSimpleToCount =  (totalHSupToCount + result.getHSimple()) > NB_HS_SIMPLE ? (NB_HS_SIMPLE - result.getHSimple()) : totalHSupToCount;
//				result.setHSimple(result.getHSimple() + heuresSimpleToCount);
//				remainingHoursToProcess -= heuresSimpleToCount;
//				
//				// As we took some HS as Heure Simple, we need to remove them from the next
//				// calculation of HS Composees. Se we shift the endDate of the interval by the
//				// number of hours we just took (we reduce the interval of what we took as simple a hours)
//				interval = new Interval(interval.getStart(), interval.getEnd().minusMinutes((int) (heuresSimpleToCount * 60d)));
//			}
//
//			// If we're into the hours before the LEGAL HOUR BASE of 39, count hours as HS Normales
//			if (result.getHNormales() + weekHours < BASE_HEBDO_LEGALE) {
//				Double heuresNormaleToCount = (totalHSupToCount + result.getHNormales()) > BASE_HEBDO_LEGALE ? (BASE_HEBDO_LEGALE - result.getHNormales()) : totalHSupToCount;
//				result.setHNormales(result.getHNormales() + heuresNormaleToCount);
//			}
//			
//			// Then, we can count the hours as HS COMPOSEES (being either HS NUIT or HS JOUR = composees (HS JOUR in case we're still < BASE_HEBDO_LEGAL))
//			
//			// Create the HS Jour interval for that day (the hours between which hours are considered HS JOUR)
//			Interval hSupJourInterval = new Interval(
//					new DateTime(interval.getStart().getYear(), interval.getStart().getMonthOfYear(), interval.getStart().getDayOfMonth(), HEURE_JOUR_DEBUT, 0, 0), 
//					new DateTime(interval.getStart().getYear(), interval.getStart().getMonthOfYear(), interval.getStart().getDayOfMonth(), HEURE_JOUR_FIN, 0, 0));
//			
//			// Calculate the overlap of the HS to determine what to be counted as HS Nuit and HS Jour
//			Interval hSupJourOverlap = hSupJourInterval.overlap(interval);
//			Double nbHoursComposees = hSupJourOverlap == null ? 0d : hSupJourOverlap.toDuration().getStandardMinutes() / 60d;
//			Double nbHoursNuit = (interval.toDuration().getStandardMinutes() / 60d) - nbHoursComposees;
//			
//			// Depending on whether the period started during day time or night time, 
//			// We start by counting hours differently
//			
//			// Either the HS Nuits
//			if (hSupJourInterval.contains(interval.getStart())) {
//				
//				Double hoursToCount = nbHoursNuit <= remainingHoursToProcess ? nbHoursNuit : remainingHoursToProcess;
//				result.setHsNuit(result.getHsNuit() + hoursToCount);
//				remainingHoursToProcess -= hoursToCount;
//				
//				
//				hoursToCount = nbHoursComposees <= remainingHoursToProcess ? nbHoursComposees : remainingHoursToProcess;
//				remainingHoursToProcess -= hoursToCount;
//				if (result.getHNormales() + weekHours >= BASE_HEBDO_LEGALE) {
//					result.setHComposees(result.getHComposees() + hoursToCount);
//				}
//			
//			}
//			// Or the HS Jour
//			else {
//				Double hoursToCount = 0d;
//				
//				hoursToCount = nbHoursComposees <= remainingHoursToProcess ? nbHoursComposees : remainingHoursToProcess;
//				remainingHoursToProcess -= hoursToCount;
//				if (result.getHNormales() + weekHours >= BASE_HEBDO_LEGALE) {
//					result.setHComposees(result.getHComposees() + hoursToCount);
//				}
//				
//				hoursToCount = nbHoursNuit <= remainingHoursToProcess ? nbHoursNuit : remainingHoursToProcess;
//				result.setHsNuit(result.getHsNuit() + hoursToCount);
//				remainingHoursToProcess -= hoursToCount;
//			}
//		}
		
		return result;
	}

	
	protected void processHeuresSup(VentilHsup result, Double weekBase, Double weekHours, DateTime dday, Interval heuresSupInterval) {
		
		// Create the HS Jour interval for that day (the hours between which hours are considered HS JOUR)
		Interval hSupJourInterval = new Interval(
				new DateTime(heuresSupInterval.getStart().getYear(), heuresSupInterval.getStart().getMonthOfYear(), heuresSupInterval.getStart().getDayOfMonth(), HEURE_JOUR_DEBUT, 0, 0), 
				new DateTime(heuresSupInterval.getStart().getYear(), heuresSupInterval.getStart().getMonthOfYear(), heuresSupInterval.getStart().getDayOfMonth(), HEURE_JOUR_FIN, 0, 0));
		
		// Calculate the overlap of the HS to determine what to be counted as HS Nuit and HS Jour
		Interval hSupJourOverlap = hSupJourInterval.overlap(heuresSupInterval);
		Double nbHoursJour = hSupJourOverlap == null ? 0d : hSupJourOverlap.toDuration().getStandardMinutes() / 60d;
		Double nbHoursNuit = (heuresSupInterval.toDuration().getStandardMinutes() / 60d) - nbHoursJour;
		
		processHeuresSup(result, weekBase, weekHours, dday, nbHoursJour, nbHoursNuit, heuresSupInterval.getStart());
	}

	protected void processHeuresSup(VentilHsup result, Double weekBase, Double weekHours, DateTime dday, Double nbHeuresSupJour) {
		processHeuresSup(result, weekBase, weekHours, dday, nbHeuresSupJour, 0d, dday.plusHours(4));
	}
	
	protected void processHeuresSup(VentilHsup result, Double weekBase, Double weekHours, DateTime dday, Double nbHeuresSupJour, Double nbHeuresSupNuit, DateTime startDate) {
		
		if (nbHeuresSupJour <= 0 && nbHeuresSupNuit <= 0)
			return;
		
		// If it's a SUNDAY or a HOLIDAY, count the hours as HS DJF
		if (dday.getDayOfWeek() == 7) { // SUNDAY
			result.setHsdjf(result.getHsdjf() + nbHeuresSupJour + nbHeuresSupNuit);
			return;
		}
		
		// If we're doing HS under the BASE_HEBDO_LEGAL = 39, count them as HS Normale
		if (weekHours < BASE_HEBDO_LEGALE) {
			Double nbHeuresNormalesToAdd = (nbHeuresSupJour + result.getHNormales() + weekBase) > BASE_HEBDO_LEGALE ? (BASE_HEBDO_LEGALE - result.getHNormales()) : nbHeuresSupJour;
			result.setHNormales(result.getHNormales() + nbHeuresNormalesToAdd);
			nbHeuresSupJour -= nbHeuresNormalesToAdd;
			
			if (nbHeuresSupJour == 0)
				return;
		}
		
		// If we're doing HS over the BASE_HEBDO_LEGA = 39, count the first 3 as HS Simple
		if (weekHours > BASE_HEBDO_LEGALE && result.getHSimple() < 3) {
			
			boolean takeHeuresJourFirst = 
					startDate.isAfter(new DateTime(dday.getYear(), dday.getMonthOfYear(), dday.getDayOfMonth(), this.HEURE_JOUR_DEBUT, 0, 0))
					&& startDate.isBefore(new DateTime(dday.getYear(), dday.getMonthOfYear(), dday.getDayOfMonth(), this.HEURE_JOUR_FIN, 0, 0));
			
			Double nbHeuresSimplesToAdd = ((takeHeuresJourFirst ? nbHeuresSupJour : nbHeuresSupNuit) + result.getHSimple()) > NB_HS_SIMPLE 
					? (NB_HS_SIMPLE - result.getHSimple()) : (takeHeuresJourFirst ? nbHeuresSupJour : nbHeuresSupNuit);
			result.setHSimple(result.getHSimple() + nbHeuresSimplesToAdd);
			
			if (takeHeuresJourFirst) {
				nbHeuresSupJour -= nbHeuresSimplesToAdd;
			} else {
				nbHeuresSupNuit -= nbHeuresSimplesToAdd;
			}
			
			if (nbHeuresSupJour == 0)
				return;
		}
		
		// If we're doing HS over the BASE_HEBDO_LEGAL + NB_HS_SIMPLE = 42, count the next ones as HS Composees
		if (weekHours > BASE_HEBDO_LEGALE + NB_HS_SIMPLE) {
			result.setHComposees(result.getHComposees() + nbHeuresSupJour);
		}
	}
	
	public List<Pointage> getPointagesHSupForDay(List<Pointage> pointages, DateTime day) {
		
		List<Pointage> result = new ArrayList<Pointage>();
		
		for (Pointage ptg : pointages) {
			DateTime dday = new DateTime(ptg.getDateDebut());
			if (dday.getDayOfYear() == day.getDayOfYear() 
					&& dday.getYear() == day.getYear() 
					&& ptg.getTypePointageEnum() == RefTypePointageEnum.H_SUP)
				result.add(ptg);
		}
		
		return result;
	}
	
}
