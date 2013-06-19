package nc.noumea.mairie.ptg.service;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.domain.Spbase;
import nc.noumea.mairie.domain.Spbhor;
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
		
		// First retrieve all the absences in the Pointages
		for (Pointage ptg : pointages) {
			
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.ABSENCE) {
				DateTime startDate = new DateTime(ptg.getDateDebut());
				DateTime endDate = new DateTime(ptg.getDateFin());
				Double nbHours = Minutes.minutesBetween(startDate, endDate).getMinutes() / 60d;
				result.setHAbsences(result.getHAbsences() + nbHours);
			}
		}
		
		// Compute the agent week hour base
		Spbase base = carr.getSpbase();
		Spbhor spbhor = carr.getSpbhor();
		Double weekBase = base.getNbashh() * spbhor.getTaux();
		Double weekHours = 0.0 - result.getHAbsences();
		
		// For each day of the week, get related pointages of HSup to count them
		// and compare to the agent Hour Base
		for (int i = 0; i < 7; i++) {

			Double dayBase = base.getDayBase(i);
			DateTime dday = dateLundi.plusDays(i);
			
			// Compute the new total of hours for the agent
			weekHours += dayBase;
				
			// Compute the number of HeuresSup considering the week normal hours
			Double nbHeuresSup = (weekHours - weekBase) <= 0 ? 0 : (weekHours - weekBase);
			// In the whole amount of HeuresSup, take only the ones we added with this day of work
			nbHeuresSup = nbHeuresSup > dayBase ? dayBase : nbHeuresSup;
			
			processHeuresSup(result, weekBase, weekHours, dday, nbHeuresSup);
			
			for (Pointage ptg : getPointagesHSupForDay(pointages, dday)) {
								
				DateTime startDate = new DateTime(ptg.getDateDebut());
				DateTime endDate = new DateTime(ptg.getDateFin());
				Double nbHours = Minutes.minutesBetween(startDate, endDate).getMinutes() / 60d;
				
				// Compute the new total of hours for the agent
				result.setHHorsContrat(result.getHHorsContrat() + nbHours);
				weekHours += nbHours;

				// If those hours can't be counted as HSup, continue
				if (weekHours <= weekBase)
					continue;
				
				Interval heuresSupInterval = new Interval(startDate, endDate);
				
				// Create the HS Jour interval for that day (the hours between which hours are considered HS JOUR)
				Interval hSupJourInterval = new Interval(
						new DateTime(heuresSupInterval.getStart().getYear(), heuresSupInterval.getStart().getMonthOfYear(), 
								heuresSupInterval.getStart().getDayOfMonth(), HEURE_JOUR_DEBUT, 0, 0), 
						new DateTime(heuresSupInterval.getStart().getYear(), heuresSupInterval.getStart().getMonthOfYear(), 
								heuresSupInterval.getStart().getDayOfMonth(), HEURE_JOUR_FIN, 0, 0));
				
				// Calculate the overlap of the HS to determine what to be counted as HS Nuit and HS Jour
				Interval hSupJourOverlap = hSupJourInterval.overlap(heuresSupInterval);
				Double nbHoursJour = hSupJourOverlap == null ? 0d : hSupJourOverlap.toDuration().getStandardMinutes() / 60d;
				Double nbHoursNuit = (heuresSupInterval.toDuration().getStandardMinutes() / 60d) - nbHoursJour;
				
				// Define whether we need to cut the HS period (because it starts before being an HS and ends after)
				if ((weekHours - nbHours) <= weekBase && weekHours > weekBase) {
					
					// Number of hours to remove from HS
					Double nbHeuresSupToNOTTake = weekBase - (weekHours - nbHours);
					
					// If the HS started during the day, we first remove hours from the day period
					if (hSupJourInterval.contains(startDate)) {
						Double nbHoursToRemove = nbHoursJour - nbHeuresSupToNOTTake < 0 ? nbHoursJour : nbHeuresSupToNOTTake;
						nbHoursJour -= nbHoursToRemove;
						nbHeuresSupToNOTTake -= nbHoursToRemove;
						
						nbHoursNuit -= nbHeuresSupToNOTTake;
					}
					// Otherwise, we first remove hours from the night period
					else {
						Double nbHoursToRemove = nbHoursNuit - nbHeuresSupToNOTTake < 0 ? nbHoursNuit : nbHeuresSupToNOTTake;
						nbHoursNuit -= nbHoursToRemove;
						nbHeuresSupToNOTTake -= nbHoursToRemove;
						
						nbHoursJour -= nbHeuresSupToNOTTake;
					}
				}
				
				processHeuresSup(result, weekBase, weekHours, dday, nbHoursJour, nbHoursNuit, startDate);
			}
			
		}
		
		return result;
	}

	protected void processHeuresSup(VentilHsup result, Double weekBase, Double weekHours, DateTime dday, Double nbHeuresSupJour) {
		processHeuresSup(result, weekBase, weekHours, dday, nbHeuresSupJour, 0d, dday.plusHours(4));
	}
	
	protected void processHeuresSup(VentilHsup result, Double weekBase, Double weekHours, DateTime dday, Double nbHeuresSupJour, Double nbHeuresSupNuit, DateTime startDate) {
		
		if (nbHeuresSupJour <= 0 && nbHeuresSupNuit <= 0)
			return;
		
		result.setHSup(result.getHSup() + nbHeuresSupJour + nbHeuresSupNuit);
		
		Double weekhoursBeforeHSup = weekHours - nbHeuresSupJour - nbHeuresSupNuit;
		
		// If it's a SUNDAY or a HOLIDAY, count the hours as HS DJF
		if (dday.getDayOfWeek() == 7) { // SUNDAY
			result.setHsdjf(result.getHsdjf() + nbHeuresSupJour + nbHeuresSupNuit);
			return;
		}
		
		// If we're doing HS Nuit then count them as HS Nuit
		if (nbHeuresSupNuit != 0) {
			result.setHsNuit(result.getHsNuit() + nbHeuresSupNuit);
		}
		
		// If we're doing HS JOUR under the BASE_HEBDO_LEGAL = 39, count them as HS Normale
		if (weekhoursBeforeHSup < BASE_HEBDO_LEGALE) {
			Double nbHeuresNormalesToAdd = (nbHeuresSupJour + result.getHNormales() + weekBase) > BASE_HEBDO_LEGALE ? (BASE_HEBDO_LEGALE - result.getHNormales()) : nbHeuresSupJour;
			result.setHNormales(result.getHNormales() + nbHeuresNormalesToAdd);
			nbHeuresSupJour -= nbHeuresNormalesToAdd;
			
			if (nbHeuresSupJour == 0)
				return;
		}
		
		// If we're doing HS over the BASE_HEBDO_LEGAL = 39, count the next ones as H Composees
		if (weekHours > BASE_HEBDO_LEGALE && result.getHSimple() < 3) {
			
			Double nbHeuresSimplesToAdd = (nbHeuresSupJour + result.getHSimple()) > NB_HS_SIMPLE ? (NB_HS_SIMPLE - result.getHSimple()) : nbHeuresSupJour;
			result.setHSimple(result.getHSimple() + nbHeuresSimplesToAdd);
			nbHeuresSupJour -= nbHeuresSimplesToAdd;
			
			if (nbHeuresSupJour == 0)
				return;
		}
		
		// If we're doing HS over the BASE_HEBDO_LEGAL + NB_HS_SIMPLE = 42, count the next ones as HS Composees
		if (weekHours >= BASE_HEBDO_LEGALE + NB_HS_SIMPLE) {
			result.setHComposees(result.getHComposees() + nbHeuresSupJour);
			return;
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
