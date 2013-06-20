package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spbase;
import nc.noumea.mairie.domain.Spbhor;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.service.IHolidayService;
import nc.noumea.mairie.ptg.service.IVentilationHSupService;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentilationHSupService implements IVentilationHSupService {

	//-- HSUP consts --//
	private static int HEURE_JOUR_DEBUT_F = 4;
	private static int HEURE_JOUR_FIN_F = 21;

	private static int HEURE_JOUR_DEBUT_C = 4;
	private static int HEURE_JOUR_FIN_C = 20;

	private static int HEURE_JOUR_DEBUT_CC = 5;
	private static int HEURE_JOUR_FIN_CC = 22;
	
	private static int NB_HS_SIMPLE = 3;
	private static double BASE_HEBDO_LEGALE = 39;
	
	@Autowired
	private IHolidayService holidayService;
	
	@Override
	public VentilHsup processHSupFonctionnaire(Integer idAgent, Spcarr carr, List<Pointage> pointages) {
		return  processHSup(idAgent, carr, pointages, AgentStatutEnum.F);
	}
	
	@Override
	public VentilHsup processHSupContractuel(Integer idAgent, Spcarr carr, List<Pointage> pointages) {
		return  processHSup(idAgent, carr, pointages, AgentStatutEnum.C);
	}
	
	@Override
	public VentilHsup processHSupConventionCollective(Integer idAgent, Spcarr carr, List<Pointage> pointages) {
		return  processHSup(idAgent, carr, pointages, AgentStatutEnum.CC);
	}
	
	public VentilHsup processHSup(Integer idAgent, Spcarr carr, List<Pointage> pointages, AgentStatutEnum statut) {
		
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
			
			switch (statut) {
				case F:
					generateHSupFonctionnaire(result, weekBase, weekHours, dday, nbHeuresSup);
					break;
				case C:
				case CC:
					generateHSupNotFonctionnaire(result, weekBase, weekHours, nbHeuresSup);
					break;
			}
			
			for (Pointage ptg : getPointagesHSupForDay(pointages, dday)) {
								
				DateTime startDate = new DateTime(ptg.getDateDebut());
				DateTime endDate = new DateTime(ptg.getDateFin());
				
				HeureSupPeriod heures = getHeuresJourNuitFromHeureSup(startDate, endDate, statut);
				
				// Compute the new total of hours for the agent
				result.setHHorsContrat(result.getHHorsContrat() + heures.getTotalHours());
				weekHours += heures.getTotalHours();
				
				// For agents statuses CC and C, count Hours done on DJF and NUIT even if those hours will not
				// be counted as Heures Sup
				if (statut != AgentStatutEnum.F)
					countHSupDJFandNUITNotFonctionnaire(result, heures);
				
				// Then, if those hours can't be counted as HSup (because under the weekBase), stop here the process
				if (weekHours <= weekBase)
					continue;
				
				// Remove as many hours as needed to get exact number of HSUP
				// If for example the weekHours is over the weekBase because of these hours
				removeHeuresWhileUnderWeekBase(heures, weekBase, weekHours, statut);
				
				// Then , depending on agent status, computer the HSup specifically
				switch (statut) {
					case F:
						generateHSupFonctionnaire(result, weekBase, weekHours, dday, heures.getNbHeuresJour(), heures.getNbHeuresNuit(), startDate);
						break;
					case C:
					case CC:
						generateHSupNotFonctionnaire(result, weekBase, weekHours, heures);
						break;
				}
			}
		}
		
		return result;
	}

	protected void generateHSupFonctionnaire(VentilHsup result, Double weekBase, Double weekHours, DateTime dday, Double nbHeuresSupJour) {
		generateHSupFonctionnaire(result, weekBase, weekHours, dday, nbHeuresSupJour, 0d, dday.plusHours(4));
	}
	
	protected void generateHSupFonctionnaire(VentilHsup result, Double weekBase, Double weekHours, DateTime dday, Double nbHeuresSupJour, Double nbHeuresSupNuit, DateTime startDate) {
		
		if (nbHeuresSupJour <= 0 && nbHeuresSupNuit <= 0)
			return;
		
		result.setHSup(result.getHSup() + nbHeuresSupJour + nbHeuresSupNuit);
		
		Double weekhoursBeforeHSup = weekHours - nbHeuresSupJour - nbHeuresSupNuit;
		
		// If it's a SUNDAY or a HOLIDAY, count the hours as HS DJF
		if (dday.getDayOfWeek() == 7 || holidayService.isHoliday(dday)) {
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
	
	protected void generateHSupNotFonctionnaire(VentilHsup result, Double weekBase, Double weekHours, Double nbHeuresSupJour) {
		generateHSupNotFonctionnaire(result, weekBase, weekHours, new HeureSupPeriod(null, nbHeuresSupJour, 0d));
	}
	
	protected void generateHSupNotFonctionnaire(VentilHsup result, Double weekBase, Double weekHours, HeureSupPeriod heures) {
		
		if (heures.getTotalHours() == 0)
			return;
		
		result.setHSup(result.getHSup() + heures.getTotalHours());
		
		Double weekhoursBeforeHSup = weekHours - heures.getTotalHours();
		
		Double nbHeuresSup = heures.getTotalHours();
		
		// If we're doing HS JOUR under the BASE_HEBDO_LEGAL = 39, count them as HS Complementaires
		if (weekhoursBeforeHSup < BASE_HEBDO_LEGALE) {
			Double nbHeuresComplementairesToAdd = (nbHeuresSup + result.getHComplementaires() + weekBase) > BASE_HEBDO_LEGALE ? (BASE_HEBDO_LEGALE - result.getHComplementaires()) : nbHeuresSup;
			result.setHComplementaires(result.getHComplementaires() + nbHeuresComplementairesToAdd);
			nbHeuresSup -= nbHeuresComplementairesToAdd;
			
			if (nbHeuresSup == 0)
				return;
		}
		
		// If we're doing HS over the BASE_HEBDO_LEGAL = 39, count the next ones as H Sup 25 and 50
		if (weekHours > BASE_HEBDO_LEGALE) {
			
			Double nbHoursToCountAs25 = (result.getHSup25() + nbHeuresSup <= 8) ? nbHeuresSup : (8 - result.getHSup25());
			nbHoursToCountAs25 = nbHoursToCountAs25 > 0 ? nbHoursToCountAs25 : 0d ;
			result.setHSup25(result.getHSup25() + nbHoursToCountAs25);
			
			Double nbHoursToCountAs50 = nbHeuresSup - nbHoursToCountAs25 < 0 ? 0d : (nbHeuresSup - nbHoursToCountAs25);
			result.setHSup50(result.getHSup50() + nbHoursToCountAs50);
		}
	}
	
	protected void countHSupDJFandNUITNotFonctionnaire(VentilHsup result, HeureSupPeriod heures) {
		
		Double totalHours = heures.getTotalHours();
		DateTime dday = heures.getInterval().getStart();
		
		// If it's a SUNDAY or a HOLIDAY, count the hours as HS DJF
		if (dday.getDayOfWeek() == 7 || holidayService.isHoliday(dday)) {
			Double nbHoursToCountAs25 = (result.getHsdjf25() + totalHours <= 8) ? totalHours : (8 - result.getHsdjf25());
			nbHoursToCountAs25 = nbHoursToCountAs25 > 0 ? nbHoursToCountAs25 : 0d ;
			result.setHsdjf25(result.getHsdjf25() + nbHoursToCountAs25);
			
			Double nbHoursToCountAs50 = totalHours - nbHoursToCountAs25 < 0 ? 0d : (totalHours - nbHoursToCountAs25);
			result.setHsdjf50(result.getHsdjf50() + nbHoursToCountAs50);
			
			result.setHsdjf(result.getHsdjf() + totalHours);
		}
		
		// If it's a 1st of May
		if (dday.getDayOfMonth() == 1 && dday.getMonthOfYear() == 5) {
			result.setHMai(totalHours);
		}
		
		// If we're doing HS Nuit then count them as HS Nuit
		if (heures.getNbHeuresNuit() != 0) {
			result.setHsNuit(result.getHsNuit() + heures.getNbHeuresNuit());
		}
		
		return;
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
	
	public HeureSupPeriod getHeuresJourNuitFromHeureSup(DateTime startDate, DateTime endDate, AgentStatutEnum statut) {
		
		Interval heuresSupInterval = new Interval(startDate, endDate);
		
		// Create the HS Jour interval for that day (the hours between which hours are considered HS JOUR)
		Interval hSupJourInterval = getDayHSupJourIntervalForStatut(heuresSupInterval.getStart(), statut);
		
		// Calculate the overlap of the HS to determine what to be counted as HS Nuit and HS Jour
		Interval hSupJourOverlap = hSupJourInterval.overlap(heuresSupInterval);
		Double nbHoursJour = hSupJourOverlap == null ? 0d : hSupJourOverlap.toDuration().getStandardMinutes() / 60d;
		Double nbHoursNuit = (heuresSupInterval.toDuration().getStandardMinutes() / 60d) - nbHoursJour;
		
		return new HeureSupPeriod(heuresSupInterval, nbHoursJour, nbHoursNuit);
	}
	
	public void removeHeuresWhileUnderWeekBase(HeureSupPeriod heures, double weekBase, double weekHoursWithHSup, AgentStatutEnum statut) {
		
		// Define whether we need to cut the HS period (because it starts before being an HS and ends after)
		if ((weekHoursWithHSup - heures.getTotalHours()) <= weekBase && weekHoursWithHSup > weekBase) {
			
			// Number of hours to remove from HS
			Double nbHeuresSupToNOTTake = weekBase - (weekHoursWithHSup - heures.getTotalHours());
			
			// Create the HS Jour interval for that day (the hours between which hours are considered HS JOUR)
			Interval hSupJourInterval = getDayHSupJourIntervalForStatut(heures.getInterval().getStart(), statut);
			
			// If the HS started during the day, we first remove hours from the day period
			if (hSupJourInterval.contains(heures.getInterval())) {
				Double nbHoursToRemove = heures.getNbHeuresJour() - nbHeuresSupToNOTTake < 0 ? heures.getNbHeuresJour() : nbHeuresSupToNOTTake;
				heures.setNbHeuresJour(heures.getNbHeuresJour() - nbHoursToRemove);
				nbHeuresSupToNOTTake -= nbHoursToRemove;
				
				heures.setNbHeuresNuit(heures.getNbHeuresNuit() - nbHeuresSupToNOTTake);
			}
			// Otherwise, we first remove hours from the night period
			else {
				Double nbHoursToRemove = heures.getNbHeuresNuit() - nbHeuresSupToNOTTake < 0 ? heures.getNbHeuresNuit() : nbHeuresSupToNOTTake;
				heures.setNbHeuresNuit(heures.getNbHeuresNuit() - nbHoursToRemove);
				nbHeuresSupToNOTTake -= nbHoursToRemove;
				
				heures.setNbHeuresJour(heures.getNbHeuresJour() - nbHeuresSupToNOTTake);
			}
		}
		
	}
	
	private Interval getDayHSupJourIntervalForStatut(DateTime day, AgentStatutEnum statut) {
		
		int startHour, endHour;
		
		switch(statut) {
			
			case C:
				startHour = HEURE_JOUR_DEBUT_C;
				endHour = HEURE_JOUR_FIN_C;
				break;
			case CC:
				startHour = HEURE_JOUR_DEBUT_CC;
				endHour = HEURE_JOUR_FIN_CC;
				break;
			case F:
			default:
				startHour = HEURE_JOUR_DEBUT_F;
				endHour = HEURE_JOUR_FIN_F;
				break;
		}
		
		return new Interval(
				new DateTime(day.getYear(), day.getMonthOfYear(), 
						day.getDayOfMonth(), startHour, 0, 0), 
				new DateTime(day.getYear(), day.getMonthOfYear(), 
						day.getDayOfMonth(), endHour, 0, 0));
	}
	
	protected class HeureSupPeriod {
		
		private Interval interval;
		private double nbHeuresJour;
		private double nbHeuresNuit;

		public HeureSupPeriod(Interval interval, double nbHeuresJour, double nbHeuresNuit) {
			this.interval = interval;
			this.nbHeuresJour = nbHeuresJour;
			this.nbHeuresNuit = nbHeuresNuit;
		}
		
		public double getTotalHours() {
			return nbHeuresJour + nbHeuresNuit;
		}
		
		public Interval getInterval() {
			return interval;
		}

		public void setInterval(Interval interval) {
			this.interval = interval;
		}

		public double getNbHeuresJour() {
			return nbHeuresJour;
		}

		public void setNbHeuresJour(double nbHeuresJour) {
			this.nbHeuresJour = nbHeuresJour;
		}

		public double getNbHeuresNuit() {
			return nbHeuresNuit;
		}

		public void setNbHeuresNuit(double nbHeuresNuit) {
			this.nbHeuresNuit = nbHeuresNuit;
		}
	}
	
}
