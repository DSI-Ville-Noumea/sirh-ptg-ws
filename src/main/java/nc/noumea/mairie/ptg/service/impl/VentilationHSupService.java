package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spbase;
import nc.noumea.mairie.domain.Spbhor;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
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

	private static int HEURE_JOUR_DEBUT_C = 5;
	private static int HEURE_JOUR_FIN_C = 22;

	private static int HEURE_JOUR_DEBUT_CC = 4;
	private static int HEURE_JOUR_FIN_CC = 20;
	
	private static int NB_HS_SIMPLE = 3 * 60;
	private static int BASE_HEBDO_LEGALE = 39 * 60;
	
	@Autowired
	private IHolidayService holidayService;
	
	@Autowired
	private HelperService helperService;
	
	@Override
	public VentilHsup processHSupFonctionnaire(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages) {
		return  processHSup(idAgent, carr, dateLundi, pointages, AgentStatutEnum.F);
	}
	
	@Override
	public VentilHsup processHSupContractuel(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages) {
		return  processHSup(idAgent, carr, dateLundi, pointages, AgentStatutEnum.C);
	}
	
	@Override
	public VentilHsup processHSupConventionCollective(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages, boolean has1150Prime) {
		return  processHSup(idAgent, carr, dateLundi, pointages, AgentStatutEnum.CC, has1150Prime);
	}
	
	public VentilHsup processHSup(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages, AgentStatutEnum statut) {
		return processHSup(idAgent, carr, dateLundi, pointages, statut, false);
	}
	
	public VentilHsup processHSup(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages, AgentStatutEnum statut, boolean has1150Prime) {

		// If there are no HSUPS pointages, there will be no VentilHSup.
		if (!areThereHSupsPointages(pointages))
			return null;
		
		VentilHsup result = new VentilHsup();
		result.setIdAgent(idAgent);
		result.setDateLundi(dateLundi);
		result.setEtat(EtatPointageEnum.VENTILE);
		
		// First retrieve all the absences in the Pointages
		for (Pointage ptg : pointages) {
			
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.ABSENCE) {
				DateTime startDate = new DateTime(ptg.getDateDebut());
				DateTime endDate = new DateTime(ptg.getDateFin());
				int nbMinutes = Minutes.minutesBetween(startDate, endDate).getMinutes();
				result.setMAbsences(result.getMAbsences() + nbMinutes);
			}
		}
		
		// Compute the agent week hour base
		Spbase base = carr.getSpbase();
		Spbhor spbhor = carr.getSpbhor();
		int weekBase = (int) (helperService.convertMairieNbHeuresFormatToMinutes(base.getNbashh()) * spbhor.getTaux());
		int weekMinutes = 0 - result.getMAbsences();
		int nbMinutesRecuperees = 0;
		
		// For each day of the week, get related pointages of HSup to count them
		// and compare to the agent Hour Base
		for (int i = 0; i < 7; i++) {

			int dayBase = helperService.convertMairieNbHeuresFormatToMinutes(base.getDayBase(i));
			DateTime dday = new DateTime(dateLundi).plusDays(i);
			
			// Compute the new total of hours for the agent
			weekMinutes += dayBase;
				
			// Compute the number of MinutesSup considering the week normal hours
			int nbMinutesSup = (weekMinutes - weekBase) <= 0 ? 0 : (weekMinutes - weekBase);
			// In the whole amount of MinutesSup, take only the ones we added with this day of work
			nbMinutesSup = nbMinutesSup > dayBase ? dayBase : nbMinutesSup;
			
			// Then take what's over the agent's weekBase only
			
			switch (statut) {
				case F:
					generateHSupFonctionnaire(result, weekBase, weekMinutes, dday, nbMinutesSup);
					break;
				case C:
				case CC:
					generateHSupNotFonctionnaire(result, weekBase, weekMinutes, nbMinutesSup);
					break;
			}
			
			for (Pointage ptg : getPointagesHSupForDay(pointages, dday)) {
								
				DateTime startDate = new DateTime(ptg.getDateDebut());
				DateTime endDate = new DateTime(ptg.getDateFin());
				
				MinutesupPeriod minutes = getMinutesJourNuitFromMinutesup(startDate, endDate, statut);
				
				// Compute the new total of hours for the agent
				result.setMHorsContrat(result.getMHorsContrat() + minutes.getTotalMinutes());
				weekMinutes += minutes.getTotalMinutes();
				
				if (ptg.getHeureSupRecuperee())
					nbMinutesRecuperees += minutes.getTotalMinutes();
				
				// For agents statuses CC and C, count Hours done on DJF and NUIT even if those hours will not
				// be counted as Minutes Sup (except if agent has Prime Rubr n° 1150)
				if (statut != AgentStatutEnum.F && !has1150Prime)
					countHSupDJFandNUITNotFonctionnaire(result, minutes, ptg.getHeureSupRecuperee());
				
				// Then, if those hours can't be counted as HSup (because under the weekBase), stop here the process
				if (weekMinutes <= weekBase)
					continue;
				
				// Remove as many hours as needed to get exact number of HSUP
				// If for example the weekMinutes is over the weekBase because of these hours
				removeMinutesWhileUnderWeekBase(minutes, weekBase, weekMinutes, statut);
				
				// Then , depending on agent status, computer the HSup specifically
				switch (statut) {
					case F:
						generateHSupFonctionnaire(result, weekBase, weekMinutes, dday, minutes.getNbMinutesJour(), minutes.getNbMinutesNuit(), startDate, ptg.getHeureSupRecuperee());
						break;
					case C:
					case CC:
						generateHSupNotFonctionnaire(result, weekBase, weekMinutes, minutes, ptg.getHeureSupRecuperee());
						break;
				}
			}
		}
		
		result.setMRecuperees(nbMinutesRecuperees);
		adjustHeuresRecuperees(result, statut);
		
		return result;
	}

	protected boolean areThereHSupsPointages(List<Pointage> pointages) {
		
		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.H_SUP)
				return true;
		}
		
		return false;
	}

	/**
	 * This method takes all minutes marked as recuperees but that were not
	 * taken as minutes sup because happening before the agent reached his
	 * week legal number of hours, and add them to the existing minutes
	 * marked as recuperees in the VentilHSup result object based on the 
	 * agent "statut".
	 * @param result
	 * @param statut
	 */
	public void adjustHeuresRecuperees(VentilHsup result, AgentStatutEnum statut) {

		int totalAmountOfHSRecuperees = 0;
		
		switch (statut) {
			case F:
				totalAmountOfHSRecuperees = result.getMComposeesRecup() 
				+ result.getMNormalesRecup() + result.getMsdjfRecup()
				+ result.getMSimpleRecup() + result.getMsNuitRecup();
				break;
			case C:
			case CC:
				totalAmountOfHSRecuperees = result.getMComplementairesRecup()
				+ result.getMsdjf25Recup() + result.getMsdjf50Recup()
				+ result.getMSup25Recup() + result.getMSup50Recup();
				break;
		
		}
		
		if (totalAmountOfHSRecuperees == result.getMRecuperees())
			return;
		
		// for all the remaining hours ..
		int minutesNotAccountedFor = result.getMRecuperees() - totalAmountOfHSRecuperees;
		
		switch (statut) {
			case F:
				int nbMinutesSimplesToAdd = (minutesNotAccountedFor + result.getMSimpleRecup()) > NB_HS_SIMPLE ? (NB_HS_SIMPLE - result.getMSimpleRecup()) : minutesNotAccountedFor;
				result.setMSimpleRecup(result.getMSimpleRecup() + nbMinutesSimplesToAdd);
				minutesNotAccountedFor -= nbMinutesSimplesToAdd;
				result.setMComposeesRecup(result.getMComposeesRecup() + minutesNotAccountedFor);
				break;
			case C:
			case CC:
				int nbMinutesToCountAs25 = (result.getMSup25Recup() + minutesNotAccountedFor <= 8 * 60) ? minutesNotAccountedFor : (8 * 60 - result.getMSup25Recup());
				nbMinutesToCountAs25 = nbMinutesToCountAs25 > 0 ? nbMinutesToCountAs25 : 0 ;
				result.setMSup25Recup(result.getMSup25Recup() + nbMinutesToCountAs25);
				minutesNotAccountedFor -= nbMinutesToCountAs25;
				result.setMSup50Recup(result.getMSup50Recup() + minutesNotAccountedFor);
				break;
		}
		
	}
	
	protected void generateHSupFonctionnaire(VentilHsup result, int weekBase, int weekMinutes, DateTime dday, int nbMinutesSupJour) {
		generateHSupFonctionnaire(result, weekBase, weekMinutes, dday, nbMinutesSupJour, 0, dday.plusHours(HEURE_JOUR_DEBUT_F), false);
	}
	
	protected void generateHSupFonctionnaire(VentilHsup result, int weekBase, int weekMinutes, DateTime dday, int nbMinutesSupJour, int nbMinutesSupNuit, DateTime startDate, boolean isHRecuperee) {
		
		if (nbMinutesSupJour <= 0 && nbMinutesSupNuit <= 0)
			return;
		
		result.setMSup(result.getMSup() + nbMinutesSupJour + nbMinutesSupNuit);
		
		int weekMinutesBeforeHSup = weekMinutes - nbMinutesSupJour - nbMinutesSupNuit;
		
		// If it's a SUNDAY or a HOLIDAY, count the hours as HS DJF
		if (dday.getDayOfWeek() == 7 || holidayService.isHoliday(dday)) {
			result.setMsdjf(result.getMsdjf() + nbMinutesSupJour + nbMinutesSupNuit);
			
			if (isHRecuperee) {
				result.setMsdjfRecup(result.getMsdjfRecup() + nbMinutesSupJour + nbMinutesSupNuit);
			}
			
			return;
		}
		
		// If we're doing HS Nuit then count them as HS Nuit
		if (nbMinutesSupNuit != 0) {
			result.setMsNuit(result.getMsNuit() + nbMinutesSupNuit);
			
			if (isHRecuperee) {
				result.setMsNuitRecup(result.getMsNuitRecup() + nbMinutesSupNuit);
			}
		}
		
		// If we're doing HS JOUR under the BASE_HEBDO_LEGAL = 39, count them as HS Normale (for people having weekBase != BASE_HEBDO_LEGALE)
		if (weekMinutesBeforeHSup < BASE_HEBDO_LEGALE && weekBase < BASE_HEBDO_LEGALE) {
			int nbMinutesNormalesToAdd = (nbMinutesSupJour + result.getMNormales() + weekBase) > BASE_HEBDO_LEGALE ? (BASE_HEBDO_LEGALE - weekMinutesBeforeHSup) : nbMinutesSupJour;
			result.setMNormales(result.getMNormales() + nbMinutesNormalesToAdd);
			nbMinutesSupJour -= nbMinutesNormalesToAdd;
			
			if (isHRecuperee) {
				result.setMNormalesRecup(result.getMNormalesRecup() + nbMinutesNormalesToAdd);
			}
			
			if (nbMinutesSupJour == 0)
				return;
		}
		
		// If we're doing HS over the BASE_HEBDO_LEGAL = 39, and under NB_HS_SIMPLE, count them as HS_Simple
		if (weekMinutes > BASE_HEBDO_LEGALE && result.getMSimple() < NB_HS_SIMPLE) {
			
			int nbMinutesSimplesToAdd = (nbMinutesSupJour + result.getMSimple()) > NB_HS_SIMPLE ? (NB_HS_SIMPLE - result.getMSimple()) : nbMinutesSupJour;
			result.setMSimple(result.getMSimple() + nbMinutesSimplesToAdd);
			nbMinutesSupJour -= nbMinutesSimplesToAdd;
			
			if (isHRecuperee) {
				result.setMSimpleRecup(result.getMSimpleRecup() + nbMinutesSimplesToAdd);
			}
			
			if (nbMinutesSupJour == 0)
				return;
		}
		
		// If we're doing HS over the BASE_HEBDO_LEGAL + NB_HS_SIMPLE = 42, count the next ones as HS Composees
		if (weekMinutes >= BASE_HEBDO_LEGALE + NB_HS_SIMPLE) {
			result.setMComposees(result.getMComposees() + nbMinutesSupJour);

			if (isHRecuperee) {
				result.setMComposeesRecup(result.getMComposeesRecup() + nbMinutesSupJour);
			}
		}
	}
	
	protected void generateHSupNotFonctionnaire(VentilHsup result, int weekBase, int weekMinutes, int nbMinutesSupJour) {
		generateHSupNotFonctionnaire(result, weekBase, weekMinutes, new MinutesupPeriod(null, nbMinutesSupJour, 0), false);
	}
	
	protected void generateHSupNotFonctionnaire(VentilHsup result, int weekBase, int weekMinutes, MinutesupPeriod Minutes, boolean isHRecuperee) {
		
		if (Minutes.getTotalMinutes() == 0)
			return;
		
		result.setMSup(result.getMSup() + Minutes.getTotalMinutes());
		
		int weekMinutesBeforeHSup = weekMinutes - Minutes.getTotalMinutes();
		
		int nbMinutesSup = Minutes.getTotalMinutes();
		
		// If we're doing HS JOUR under the BASE_HEBDO_LEGAL = 39, count them as HS Complementaires
		if (weekMinutesBeforeHSup < BASE_HEBDO_LEGALE && weekBase < BASE_HEBDO_LEGALE) {
			int nbMinutesComplementairesToAdd = (nbMinutesSup + result.getMComplementaires() + weekBase) > BASE_HEBDO_LEGALE ? (BASE_HEBDO_LEGALE - weekMinutesBeforeHSup) : nbMinutesSup;
			result.setMComplementaires(result.getMComplementaires() + nbMinutesComplementairesToAdd);
			nbMinutesSup -= nbMinutesComplementairesToAdd;
			
			if (isHRecuperee) {
				result.setMComplementairesRecup(result.getMComplementairesRecup() + nbMinutesComplementairesToAdd);
			}
			
			if (nbMinutesSup == 0)
				return;
		}
		
		// If we're doing HS over the BASE_HEBDO_LEGAL = 39, count the next ones as H Sup 25 and 50
		if (weekMinutes > BASE_HEBDO_LEGALE) {
			
			int nbMinutesToCountAs25 = (result.getMSup25() + nbMinutesSup <= 8 * 60) ? nbMinutesSup : (8 * 60 - result.getMSup25());
			nbMinutesToCountAs25 = nbMinutesToCountAs25 > 0 ? nbMinutesToCountAs25 : 0 ;
			result.setMSup25(result.getMSup25() + nbMinutesToCountAs25);
			
			int nbMinutesToCountAs50 = nbMinutesSup - nbMinutesToCountAs25 < 0 ? 0 : (nbMinutesSup - nbMinutesToCountAs25);
			result.setMSup50(result.getMSup50() + nbMinutesToCountAs50);
			
			if (isHRecuperee) {
				result.setMSup25Recup(result.getMSup25Recup() + nbMinutesToCountAs25);
				result.setMSup50Recup(result.getMSup50Recup() + nbMinutesToCountAs50);
			}
		}
	}
	
	protected void countHSupDJFandNUITNotFonctionnaire(VentilHsup result, MinutesupPeriod minutes, boolean isHRecuperee) {
		
		int totalMinutes = minutes.getTotalMinutes();
		DateTime dday = minutes.getInterval().getStart();
		
		// If it's a SUNDAY or a HOLIDAY, count the hours as HS DJF
		if (dday.getDayOfWeek() == 7 || holidayService.isHoliday(dday)) {
			int nbMinutesToCountAs25 = (result.getMsdjf25() + totalMinutes <= 8 * 60) ? totalMinutes : (8 * 60 - result.getMsdjf25());
			nbMinutesToCountAs25 = nbMinutesToCountAs25 > 0 ? nbMinutesToCountAs25 : 0 ;
			result.setMsdjf25(result.getMsdjf25() + nbMinutesToCountAs25);
			
			int nbMinutesToCountAs50 = totalMinutes - nbMinutesToCountAs25 < 0 ? 0 : (totalMinutes - nbMinutesToCountAs25);
			result.setMsdjf50(result.getMsdjf50() + nbMinutesToCountAs50);
			
			result.setMsdjf(result.getMsdjf() + totalMinutes);
			
			// Do the same calculus if the minutes are marked as "recuperee"
			if (isHRecuperee) {
				result.setMsdjf25Recup(result.getMsdjf25Recup() + nbMinutesToCountAs25);
				result.setMsdjf50Recup(result.getMsdjf50Recup() + nbMinutesToCountAs50);
				result.setMsdjfRecup(result.getMsdjfRecup() + totalMinutes);
			}
		}
		
		// If it's a 1st of May
		if (dday.getDayOfMonth() == 1 && dday.getMonthOfYear() == 5) {
			result.setMMai(result.getMMai() + totalMinutes);
			
			if (isHRecuperee) {
				result.setMMaiRecup(result.getMMaiRecup() + totalMinutes);
			}
		}
		
		// If we're doing HS Nuit then count them as HS Nuit
		if (minutes.getNbMinutesNuit() != 0) {
			result.setMsNuit(result.getMsNuit() + minutes.getNbMinutesNuit());

			if (isHRecuperee) {
				result.setMsNuitRecup(result.getMsNuitRecup() + minutes.getNbMinutesNuit());
			}
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
	
	public MinutesupPeriod getMinutesJourNuitFromMinutesup(DateTime startDate, DateTime endDate, AgentStatutEnum statut) {
		
		Interval MinutesSupInterval = new Interval(startDate, endDate);
		
		// Create the HS Jour interval for that day (the hours between which hours are considered HS JOUR)
		Interval hSupJourInterval = getDayHSupJourIntervalForStatut(MinutesSupInterval.getStart(), statut);
		
		// Calculate the overlap of the HS to determine what to be counted as HS Nuit and HS Jour
		Interval hSupJourOverlap = hSupJourInterval.overlap(MinutesSupInterval);
		int nbMinutesJour = (int) (hSupJourOverlap == null ? 0 : hSupJourOverlap.toDuration().getStandardMinutes());
		int nbMinutesNuit = (int) (MinutesSupInterval.toDuration().getStandardMinutes() - nbMinutesJour);
		
		return new MinutesupPeriod(MinutesSupInterval, nbMinutesJour, nbMinutesNuit);
	}
	
	public void removeMinutesWhileUnderWeekBase(MinutesupPeriod minutes, int weekBase, int weekMinutesWithHSup, AgentStatutEnum statut) {
		
		// Define whether we need to cut the HS period (because it starts before being an HS and ends after)
		if ((weekMinutesWithHSup - minutes.getTotalMinutes()) <= weekBase && weekMinutesWithHSup > weekBase) {
			
			// Number of hours to remove from HS
			int nbMinutesSupToNOTTake = weekBase - (weekMinutesWithHSup - minutes.getTotalMinutes());
			
			// Create the HS Jour interval for that day (the hours between which hours are considered HS JOUR)
			Interval hSupJourInterval = getDayHSupJourIntervalForStatut(minutes.getInterval().getStart(), statut);
			
			// If the HS started during the day, we first remove hours from the day period
			if (hSupJourInterval.contains(minutes.getInterval())) {
				int nbMinutesToRemove = minutes.getNbMinutesJour() - nbMinutesSupToNOTTake < 0 ? minutes.getNbMinutesJour() : nbMinutesSupToNOTTake;
				minutes.setNbMinutesJour(minutes.getNbMinutesJour() - nbMinutesToRemove);
				nbMinutesSupToNOTTake -= nbMinutesToRemove;
				
				minutes.setNbMinutesNuit(minutes.getNbMinutesNuit() - nbMinutesSupToNOTTake);
			}
			// Otherwise, we first remove hours from the night period
			else {
				int nbMinutesToRemove = minutes.getNbMinutesNuit() - nbMinutesSupToNOTTake < 0 ? minutes.getNbMinutesNuit() : nbMinutesSupToNOTTake;
				minutes.setNbMinutesNuit(minutes.getNbMinutesNuit() - nbMinutesToRemove);
				nbMinutesSupToNOTTake -= nbMinutesToRemove;
				
				minutes.setNbMinutesJour(minutes.getNbMinutesJour() - nbMinutesSupToNOTTake);
			}
		}
		
	}
	
	public Interval getDayHSupJourIntervalForStatut(DateTime day, AgentStatutEnum statut) {
		
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
	
	protected class MinutesupPeriod {
		
		private Interval interval;
		private int nbMinutesJour;
		private int nbMinutesNuit;

		public MinutesupPeriod(Interval interval, int nbMinutesJour, int nbMinutesNuit) {
			this.interval = interval;
			this.nbMinutesJour = nbMinutesJour;
			this.nbMinutesNuit = nbMinutesNuit;
		}
		
		public int getTotalMinutes() {
			return nbMinutesJour + nbMinutesNuit;
		}
		
		public Interval getInterval() {
			return interval;
		}

		public void setInterval(Interval interval) {
			this.interval = interval;
		}

		public int getNbMinutesJour() {
			return nbMinutesJour;
		}

		public void setNbMinutesJour(int nbMinutesJour) {
			this.nbMinutesJour = nbMinutesJour;
		}

		public int getNbMinutesNuit() {
			return nbMinutesNuit;
		}

		public void setNbMinutesNuit(int nbMinutesNuit) {
			this.nbMinutesNuit = nbMinutesNuit;
		}
	}
}
