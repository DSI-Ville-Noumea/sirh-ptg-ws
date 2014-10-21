package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.Spbase;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spcong;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IPointageDataConsistencyRules;
import nc.noumea.mairie.ptg.service.IVentilationHSupService;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentilationHSupService implements IVentilationHSupService {

	// -- HSUP consts --//
	public static int HEURE_JOUR_DEBUT_F = 4;
	public static int HEURE_JOUR_FIN_F = 21;

	public static int HEURE_JOUR_DEBUT_C = 5;
	public static int HEURE_JOUR_FIN_C = 22;

	public static int HEURE_JOUR_DEBUT_CC = 4;
	public static int HEURE_JOUR_FIN_CC = 20;

	private static int NB_HS_SIMPLE = 3 * 60;
	private static int NB_HS_SUP25 = 8 * 60;
	private static int BASE_HEBDO_LEGALE = 39 * 60;

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	private HelperService helperService;
	
	@Autowired
	private IMairieRepository mairieRepository;
	
	@Autowired
	private IPointageDataConsistencyRules ptgDataCosistencyRules;
	
	@Autowired
	private IVentilationRepository ventilationRepository;

	@Override
	public VentilHsup processHSupFonctionnaire(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages, VentilDate ventilDate) {
		return processHSup(idAgent, carr, dateLundi, pointages, AgentStatutEnum.F, ventilDate);
	}

	@Override
	public VentilHsup processHSupContractuel(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages, VentilDate ventilDate) {
		return processHSup(idAgent, carr, dateLundi, pointages, AgentStatutEnum.C, ventilDate);
	}

	@Override
	public VentilHsup processHSupConventionCollective(Integer idAgent, Spcarr carr, Date dateLundi,
			List<Pointage> pointages, boolean has1150Prime, VentilDate ventilDate) {
		return processHSup(idAgent, carr, dateLundi, pointages, AgentStatutEnum.CC, has1150Prime, ventilDate);
	}

	public VentilHsup processHSup(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages,
			AgentStatutEnum statut, VentilDate ventilDate) {
		return processHSup(idAgent, carr, dateLundi, pointages, statut, false, ventilDate);
	}

	public VentilHsup processHSup(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages,
			AgentStatutEnum statut, boolean has1150Prime, VentilDate ventilDate) {

		// If there are no HSUPS pointages, there will be no VentilHSup.
		if (!areThereHSupsPointages(pointages)){
			// if there was one previous VentilHSup from a previous ventilation
			// there will be one VentilHSup with 0 minutes to delete in SPPHRE
			List<VentilHsup> listOldVentilHSup = ventilationRepository.getListOfOldVentilHSForAgentAndDateLundi(idAgent, dateLundi, ventilDate.getIdVentilDate());
			if(null != listOldVentilHSup && !listOldVentilHSup.isEmpty()) {
				VentilHsup result = new VentilHsup();
					result.setIdAgent(idAgent);
					result.setDateLundi(dateLundi);
					result.setEtat(EtatPointageEnum.VENTILE);
				return result;
			}
			return null;
		}
		
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

		Spbase base = carr.getSpbase();
		// second retrieve all the absences in SPCONG
		// on ne compte pas les conges annuels et les conges annules
		List<Spcong> listSpCong = mairieRepository.getListCongeWithoutCongesAnnuelsEtAnnulesBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate());
		for(Spcong spCong : listSpCong) {
			DateTime startDate = ptgDataCosistencyRules.getDateDebut(spCong.getId().getDatdeb(), spCong.getCodem1());
			if(dateLundi.after(startDate.toDate()) ) {
				startDate = new DateTime(dateLundi);
			}
			
			DateTime endDate = ptgDataCosistencyRules.getDateFin(spCong.getDatfin(), spCong.getCodem2());
			if(endDate.toDate().after(new DateTime(dateLundi).plusDays(7).toDate())) {
				endDate = new DateTime(dateLundi).plusDays(7);
			}
			
			int minutesConges = 0;
			for (int i = 0; i < 7; i++) {
				DateTime dateJour = new DateTime(dateLundi).plusDays(i);
				
				if((dateJour.getDayOfYear()==startDate.getDayOfYear() || dateJour.toDate().after(startDate.toDate()))
						&& (dateJour.getDayOfYear()==endDate.plusMinutes(-1).getDayOfYear() || dateJour.toDate().before(endDate.toDate()))) {
					
					int minutesCongesDay = helperService.convertMairieNbHeuresFormatToMinutes(base.getDayBase(i));
					// on gere ici les demis journees grace au champ SPCONG.CODEM1 et SPCONG.CODEM2
					if(spCong.getId().getDatdeb().equals(spCong.getDatfin())
							&& startDate.getDayOfWeek()-1 == i) {
						if(null != spCong.getCodem1() && null != spCong.getCodem2()
								&& spCong.getCodem1().equals(spCong.getCodem2())) {
							minutesCongesDay = minutesCongesDay / 2;
						}
					} else if(startDate.getDayOfWeek()-1 == i && spCong.getCodem1().equals(2)){
						minutesCongesDay = minutesCongesDay / 2;
					} else if(endDate.getDayOfWeek()-1 == i && spCong.getCodem2().equals(1)) {
						minutesCongesDay = minutesCongesDay / 2;
					}
					
					minutesConges += minutesCongesDay;
				}
			}
			
			result.setMAbsencesAS400(result.getMAbsencesAS400() + minutesConges);
		}
		
		// second retrieve all the absences in SPABSEN
		List<Spabsen> listSpAbsen = mairieRepository.getListMaladieBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate());
		for(Spabsen spabsen : listSpAbsen) {
			DateTime startDate = new DateTime(helperService.getDateFromMairieInteger(spabsen.getId().getDatdeb()));
			if(dateLundi.after(startDate.toDate()) ) {
				startDate = new DateTime(dateLundi);
			}
			
			DateTime endDate = new DateTime(helperService.getDateFromMairieInteger(spabsen.getDatfin()));
			if(endDate.toDate().after(new DateTime(dateLundi).plusDays(7).toDate())) {
				endDate = new DateTime(dateLundi).plusDays(7);
			}
			
			int minutesSpAbsen = 0;
			for (int i = 0; i < 7; i++) {
				Date dateJour = new DateTime(dateLundi).plusDays(i).toDate();
				if((dateJour.equals(startDate.toDate()) || dateJour.after(startDate.toDate()))
						&& (dateJour.equals(endDate.toDate()) || dateJour.before(endDate.toDate()))) {
					minutesSpAbsen += helperService.convertMairieNbHeuresFormatToMinutes(base.getDayBase(i));
				}
			}
			
			result.setMAbsencesAS400(result.getMAbsencesAS400() + minutesSpAbsen);
		}
		
		// Compute the agent week hour base
		int weekBase = (int) (helperService.convertMairieNbHeuresFormatToMinutes(base.getNbashh()));
		int weekMinutes = 0 - result.getTotalAbsences();
		int nbMinutesRecuperees = 0;
		// pour la DPM, les heures sup recuperees en rappel de service sont doublees
		// #11635
		int nbMinutesRappelService = 0;

		// For each day of the week, get related pointages of HSup to count them
		// and compare to the agent Hour Base
		for (int i = 0; i < 7; i++) {

			int dayBase = helperService.convertMairieNbHeuresFormatToMinutes(base.getDayBase(i));
			DateTime dday = new DateTime(dateLundi).plusDays(i);

			// Compute the new total of hours for the agent
			weekMinutes += dayBase;

			int nbMinutesSup = 0;

			// Then take what's over the agent's weekBase only 
			switch (statut) {
				case F:
					// Compute the number of MinutesSup considering the week normal
					// hours
					nbMinutesSup = (weekMinutes - (weekBase - result.getTotalAbsences()) - result.getMSup() - result.getMNormales()) <= 0 
										? 0 
										: (weekMinutes - (weekBase - result.getTotalAbsences()) - result.getMSup() - result.getMNormales());
					// In the whole amount of MinutesSup, take only the ones we added
					// with this day of work
					nbMinutesSup = nbMinutesSup > dayBase ? dayBase : nbMinutesSup;
					generateHSupFonctionnaire(result, weekBase, weekMinutes, dday, nbMinutesSup);
					break;
				case C:
					// Compute the number of MinutesSup considering the week normal
					// hours
					nbMinutesSup = (weekMinutes - (weekBase - result.getTotalAbsences()) - result.getMSup() - result.getMComplementaires()) <= 0 
										? 0 
										: (weekMinutes - (weekBase - result.getTotalAbsences()) - result.getMSup() - result.getMComplementaires());
					// In the whole amount of MinutesSup, take only the ones we added
					// with this day of work
					nbMinutesSup = nbMinutesSup > dayBase ? dayBase : nbMinutesSup;
					generateHSupContractuels(result, weekBase, weekMinutes, dday, nbMinutesSup);
					break;
				case CC:
					
					// Compute the number of MinutesSup considering the week normal
					// hours
					int heuresSupEnresgistrees = result.getMComplementaires() + result.getMSup25() + result.getMSup50();
					nbMinutesSup = (weekMinutes - (weekBase - result.getTotalAbsences()) - heuresSupEnresgistrees) <= 0 
										? 0 
										: (weekMinutes - (weekBase - result.getTotalAbsences()) - heuresSupEnresgistrees);
					// In the whole amount of MinutesSup, take only the ones we added
					// with this day of work
					nbMinutesSup = nbMinutesSup > dayBase ? dayBase : nbMinutesSup;
					generateHSupConventionCollective(result, weekBase, weekMinutes, nbMinutesSup);
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
				
				if (ptg.getHeureSupRecuperee() && ptg.getHeureSupRappelService())
					nbMinutesRappelService += minutes.getTotalMinutes();

				// For agents statuses CC and C, count Hours done on DJF and
				// NUIT even if those hours will not
				// be counted as Minutes Sup (except if agent has Prime Rubr n°
				// 1150)
				switch (statut) {
					case F:
						countHSupDJFandNUITFonctionnaire(result, minutes, ptg.getHeureSupRecuperee(), weekMinutes);
						break;
					case C:
						countHSupDJFandNUITContractuels(result, minutes, ptg.getHeureSupRecuperee());
						break;
					case CC:
						if (!has1150Prime)
							countHSupDJFandNUITConventionCollective(result, minutes, ptg.getHeureSupRecuperee());
						break;
				}
				
				// Remove as many hours as needed to get exact number of HSUP
				// If for example the weekMinutes is over the weekBase because
				// of these hours
				removeMinutesWhileUnderWeekBase(minutes, weekBase - result.getTotalAbsences(), weekMinutes, statut, dayBase);

				// Then , depending on agent status, computer the HSup
				// specifically
				switch (statut) {
					case F:
						generateHSupFonctionnaire(result, weekBase, weekMinutes, dday, minutes.getNbMinutesJour(),
								minutes.getNbMinutesNuit(), startDate, ptg.getHeureSupRecuperee(), true);
						break;
					case C:
						generateHSupContractuels(result, weekBase, weekMinutes, dday, minutes.getNbMinutesJour(),
								minutes.getNbMinutesNuit(), startDate, ptg.getHeureSupRecuperee(), true);
						break;
					case CC:
						generateHSupConventionCollective(result, weekBase, weekMinutes, minutes, ptg.getHeureSupRecuperee());
						break;
				}
			}
		}

		result.setMRecuperees(nbMinutesRecuperees);
		result.setMRappelService(nbMinutesRappelService);
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
	 * taken as minutes sup because happening before the agent reached his week
	 * legal number of hours, and add them to the existing minutes marked as
	 * recuperees in the VentilHSup result object based on the agent "statut".
	 * 
	 * @param result
	 * @param statut
	 */
	public void adjustHeuresRecuperees(VentilHsup result, AgentStatutEnum statut) {

		int totalAmountOfHSRecuperees = 0;

		switch (statut) {
			case F:
				totalAmountOfHSRecuperees = result.getMComposeesRecup() + result.getMNormalesRecup()
						+ result.getMsdjfRecup() + result.getMSimpleRecup() + result.getMsNuitRecup();
				break;
			case C:
			case CC:
				totalAmountOfHSRecuperees = result.getMComplementairesRecup() + result.getMSup25Recup() + result.getMSup50Recup()
						+ result.getMsdjfRecup() + result.getMsNuitRecup();
				break;

		}

		if (totalAmountOfHSRecuperees == result.getMRecuperees())
			return;

		// for all the remaining hours ..
		int minutesNotAccountedFor = result.getMRecuperees() - totalAmountOfHSRecuperees;

		switch (statut) {
			case F:
				int nbMinutesSimplesToAdd = (minutesNotAccountedFor + result.getMSimpleRecup()) > NB_HS_SIMPLE ? (NB_HS_SIMPLE - result
						.getMSimpleRecup()) : minutesNotAccountedFor;
				result.setMSimpleRecup(result.getMSimpleRecup() + nbMinutesSimplesToAdd);
				minutesNotAccountedFor -= nbMinutesSimplesToAdd;
				result.setMComposeesRecup(result.getMComposeesRecup() + minutesNotAccountedFor);
				break;
			case C:
			case CC:
				int nbMinutesToCountAs25 = (result.getMSup25Recup() + minutesNotAccountedFor <= NB_HS_SUP25) ? minutesNotAccountedFor
						: (NB_HS_SUP25 - result.getMSup25Recup());
				nbMinutesToCountAs25 = nbMinutesToCountAs25 > 0 ? nbMinutesToCountAs25 : 0;
				result.setMSup25Recup(result.getMSup25Recup() + nbMinutesToCountAs25);
				minutesNotAccountedFor -= nbMinutesToCountAs25;
				result.setMSup50Recup(result.getMSup50Recup() + minutesNotAccountedFor);
				break;
		}

	}

	protected void generateHSupFonctionnaire(VentilHsup result, int weekBase, int weekMinutes, DateTime dday,
			int nbMinutesSupJour) {
		generateHSupFonctionnaire(result, weekBase, weekMinutes, dday, nbMinutesSupJour, 0,
				dday.plusHours(HEURE_JOUR_DEBUT_F), false, false);
	}

	protected void generateHSupFonctionnaire(VentilHsup result, int weekBase, int weekMinutes, DateTime dday,
			int nbMinutesSupJour, int nbMinutesSupNuit, DateTime startDate, boolean isHRecuperee, boolean isHeuresSupSaisies) {

		if (nbMinutesSupJour <= 0 && nbMinutesSupNuit <= 0)
			return;

		int weekMinutesBeforeHSup = weekMinutes - nbMinutesSupJour - nbMinutesSupNuit;

		// If it's a SUNDAY or a HOLIDAY, count the hours as HS DJF
		if (dday.getDayOfWeek() == 7 || sirhWsConsumer.isJourFerie(dday)) {
			return;
		}

		// If we're doing HS JOUR under the BASE_HEBDO_LEGAL = 39, count them as
		// HS Normale (for people having weekBase != BASE_HEBDO_LEGALE)
		if (weekMinutesBeforeHSup < BASE_HEBDO_LEGALE 
				&& isHeuresSupSaisies 
				&& (weekBase - result.getTotalAbsences()) + result.getMNormales() < BASE_HEBDO_LEGALE) {
			
			int nbMinutesNormalesToAdd = 0;
			
			if((nbMinutesSupJour + weekMinutesBeforeHSup) > BASE_HEBDO_LEGALE) {
				nbMinutesNormalesToAdd = (BASE_HEBDO_LEGALE - weekMinutesBeforeHSup);
			} else if(nbMinutesSupJour + (weekBase - result.getTotalAbsences()) > BASE_HEBDO_LEGALE) {
				nbMinutesNormalesToAdd = BASE_HEBDO_LEGALE - (weekBase - result.getTotalAbsences());
			} else {
				nbMinutesNormalesToAdd = nbMinutesSupJour;
			}
			
			result.setMNormales(result.getMNormales() + nbMinutesNormalesToAdd);
			nbMinutesSupJour -= nbMinutesNormalesToAdd;

			if (isHRecuperee) {
				result.setMNormalesRecup(result.getMNormalesRecup() + nbMinutesNormalesToAdd);
			}

			if (nbMinutesSupJour == 0)
				return;
		}

		// If we're doing HS over the BASE_HEBDO_LEGAL = 39, and under
		// NB_HS_SIMPLE, count them as HS_Simple
		if (weekMinutes > BASE_HEBDO_LEGALE && result.getMSimple() < NB_HS_SIMPLE) {
			
			int nbMinutesSimplesToAdd = 0;
			int differenceMinutesSupJourEtBASE_HEBDO_LEGALE = weekMinutes - nbMinutesSupNuit - nbMinutesSupJour - BASE_HEBDO_LEGALE;
			if(differenceMinutesSupJourEtBASE_HEBDO_LEGALE < 0) {
				nbMinutesSupJour = nbMinutesSupJour + weekMinutes - nbMinutesSupNuit - nbMinutesSupJour - BASE_HEBDO_LEGALE;
			}
			
			nbMinutesSimplesToAdd = (nbMinutesSupJour + result.getMSimple()) > NB_HS_SIMPLE ? (NB_HS_SIMPLE - result
					.getMSimple()) : nbMinutesSupJour;
			result.setMSimple(result.getMSimple() + nbMinutesSimplesToAdd);
			result.setMSup(result.getMSup() + nbMinutesSimplesToAdd);
			nbMinutesSupJour -= nbMinutesSimplesToAdd;

			if (isHRecuperee) {
				result.setMSimpleRecup(result.getMSimpleRecup() + nbMinutesSimplesToAdd);
			}

			if (nbMinutesSupJour == 0)
				return;
		}

		// If we're doing HS over the BASE_HEBDO_LEGAL + NB_HS_SIMPLE = 42,
		// count the next ones as HS Composees
		if (weekMinutes >= BASE_HEBDO_LEGALE + NB_HS_SIMPLE) {
			result.setMComposees(result.getMComposees() + nbMinutesSupJour);
			result.setMSup(result.getMSup() + nbMinutesSupJour);
			
			if (isHRecuperee) {
				result.setMComposeesRecup(result.getMComposeesRecup() + nbMinutesSupJour);
			}
		}
	}
	
	protected void generateHSupContractuels(VentilHsup result, int weekBase, int weekMinutes, DateTime dday,
			int nbMinutesSupJour) {
		generateHSupContractuels(result, weekBase, weekMinutes, dday, nbMinutesSupJour, 0,
				dday.plusHours(HEURE_JOUR_DEBUT_F), false, false);
	}

	protected void generateHSupContractuels(VentilHsup result, int weekBase, int weekMinutes, DateTime dday,
			int nbMinutesSupJour, int nbMinutesSupNuit, DateTime startDate, boolean isHRecuperee, boolean isHeuresSupSaisies) {

		if (nbMinutesSupJour <= 0 && nbMinutesSupNuit <= 0)
			return;

		int weekMinutesBeforeHSup = weekMinutes - nbMinutesSupJour - nbMinutesSupNuit;

		// If it's a SUNDAY or a HOLIDAY, count the hours as HS DJF
		if (dday.getDayOfWeek() == 7 || sirhWsConsumer.isJourFerie(dday)) {
			return;
		}

		// If we're doing HS JOUR under the BASE_HEBDO_LEGAL = 39, count them as
		// HS Normale (for people having weekBase != BASE_HEBDO_LEGALE)
		if (weekMinutesBeforeHSup < BASE_HEBDO_LEGALE 
				&& isHeuresSupSaisies 
				&& (weekBase - result.getTotalAbsences()) + result.getMComplementaires() < BASE_HEBDO_LEGALE) {
			
			int nbMinutesNormalesToAdd = 0;
			
			if((nbMinutesSupJour + weekMinutesBeforeHSup) > BASE_HEBDO_LEGALE) {
				nbMinutesNormalesToAdd = (BASE_HEBDO_LEGALE - weekMinutesBeforeHSup);
			} else if(nbMinutesSupJour + (weekBase - result.getTotalAbsences()) > BASE_HEBDO_LEGALE) {
				nbMinutesNormalesToAdd = BASE_HEBDO_LEGALE - (weekBase - result.getTotalAbsences());
			} else {
				nbMinutesNormalesToAdd = nbMinutesSupJour;
			}
			
			result.setMComplementaires(result.getMComplementaires() + nbMinutesNormalesToAdd);
			nbMinutesSupJour -= nbMinutesNormalesToAdd;

			if (isHRecuperee) {
				result.setMComplementairesRecup(result.getMComplementairesRecup() + nbMinutesNormalesToAdd);
			}

			if (nbMinutesSupJour == 0)
				return;
		}

		// If we're doing HS over the BASE_HEBDO_LEGAL = 39, and under
		// NB_HS_SUP25, count them as HS_25
		if (weekMinutes > BASE_HEBDO_LEGALE && result.getMSimple() < NB_HS_SUP25) {
			
			int nbMinutesSup25ToAdd = 0;
			int differenceMinutesSupJourEtBASE_HEBDO_LEGALE = weekMinutes - nbMinutesSupNuit - nbMinutesSupJour - BASE_HEBDO_LEGALE;
			if(differenceMinutesSupJourEtBASE_HEBDO_LEGALE < 0) {
				nbMinutesSupJour = nbMinutesSupJour + weekMinutes - nbMinutesSupNuit - nbMinutesSupJour - BASE_HEBDO_LEGALE;
			}
			
			nbMinutesSup25ToAdd = (nbMinutesSupJour + result.getMSup25()) > NB_HS_SUP25 ? (NB_HS_SUP25 - result
					.getMSup25()) : nbMinutesSupJour;
			result.setMSup25(result.getMSup25() + nbMinutesSup25ToAdd);
			result.setMSup(result.getMSup() + nbMinutesSup25ToAdd);
			nbMinutesSupJour -= nbMinutesSup25ToAdd;

			if (isHRecuperee) {
				result.setMSup25Recup(result.getMSup25Recup() + nbMinutesSup25ToAdd);
			}

			if (nbMinutesSupJour == 0)
				return;
		}

		// If we're doing HS over the BASE_HEBDO_LEGAL + NB_HS_SUP25 = 47,
		// count the next ones as HS SUP 50
		if (weekMinutes >= BASE_HEBDO_LEGALE + NB_HS_SUP25) {
			result.setMSup50(result.getMSup50() + nbMinutesSupJour);
			result.setMSup(result.getMSup() + nbMinutesSupJour);
			
			if (isHRecuperee) {
				result.setMSup50Recup(result.getMSup50Recup() + nbMinutesSupJour);
			}
		}
	}

	protected void generateHSupConventionCollective(VentilHsup result, int weekBase, int weekMinutes, int nbMinutesSupJour) {
		generateHSupConventionCollective(result, weekBase, weekMinutes, new MinutesupPeriod(null, nbMinutesSupJour, 0),
				false);
	}

	protected void generateHSupConventionCollective(VentilHsup result, int weekBase, int weekMinutes,
			MinutesupPeriod Minutes, boolean isHRecuperee) {

		if (Minutes.getTotalMinutes() == 0)
			return;

		int weekMinutesBeforeHSup = weekMinutes - Minutes.getTotalMinutes();

		int nbMinutesSup = Minutes.getTotalMinutes();

		// If we're doing HS under the BASE_HEBDO_LEGAL = 39, count them as HS
		// Complementaires
		if (weekMinutesBeforeHSup < BASE_HEBDO_LEGALE 
				&& (weekBase - result.getTotalAbsences()) + result.getMComplementaires() < BASE_HEBDO_LEGALE) {
			
			int nbMinutesComplementairesToAdd = 0;
			
			if((nbMinutesSup + weekMinutesBeforeHSup) > BASE_HEBDO_LEGALE) {
				nbMinutesComplementairesToAdd = (BASE_HEBDO_LEGALE - weekMinutesBeforeHSup);
			} else if(nbMinutesSup + (weekBase - result.getTotalAbsences()) > BASE_HEBDO_LEGALE) {
				nbMinutesComplementairesToAdd = BASE_HEBDO_LEGALE - (weekBase - result.getTotalAbsences());
			} else {
				nbMinutesComplementairesToAdd = nbMinutesSup;
			}
			
			result.setMComplementaires(result.getMComplementaires() + nbMinutesComplementairesToAdd);
			nbMinutesSup -= nbMinutesComplementairesToAdd;

			if (isHRecuperee) {
				result.setMComplementairesRecup(result.getMComplementairesRecup() + nbMinutesComplementairesToAdd);
			}

			if (nbMinutesSup == 0)
				return;
		}

		// If we're doing HS over the BASE_HEBDO_LEGAL = 39, count the next ones
		// as H Sup 25 and 50
		if (weekMinutes > BASE_HEBDO_LEGALE) {

			int nbMinutesToCountAs25 = (result.getMSup25() + nbMinutesSup <= NB_HS_SUP25) ? nbMinutesSup : (NB_HS_SUP25 - result
					.getMSup25());
			nbMinutesToCountAs25 = nbMinutesToCountAs25 > 0 ? nbMinutesToCountAs25 : 0;
			result.setMSup25(result.getMSup25() + nbMinutesToCountAs25);
			result.setMSup(result.getMSup() + nbMinutesToCountAs25);

			int nbMinutesToCountAs50 = nbMinutesSup - nbMinutesToCountAs25 < 0 ? 0
					: (nbMinutesSup - nbMinutesToCountAs25);
			result.setMSup50(result.getMSup50() + nbMinutesToCountAs50);
			result.setMSup(result.getMSup() + nbMinutesToCountAs50);

			if (isHRecuperee) {
				result.setMSup25Recup(result.getMSup25Recup() + nbMinutesToCountAs25);
				result.setMSup50Recup(result.getMSup50Recup() + nbMinutesToCountAs50);
			}
		}
	}
	
	protected void countHSupDJFandNUITContractuels(VentilHsup result, MinutesupPeriod minutes, boolean isHRecuperee) {

		DateTime dday = minutes.getInterval().getStart();

		// If it's a SUNDAY or a HOLIDAY, count the hours as HS DJF
		if (dday.getDayOfWeek() == 7 || sirhWsConsumer.isJourFerie(dday)) {

			result.setMsdjf(result.getMsdjf() + minutes.getNbMinutesJour());
			result.setMSup(result.getMSup() + minutes.getNbMinutesJour());
			// Do the same calculus if the minutes are marked as "recuperee"
			if (isHRecuperee) {
				result.setMsdjfRecup(result.getMsdjfRecup() + minutes.getNbMinutesJour());
			}
		}

		// If we're doing HS Nuit then count them as HS Nuit
		if (minutes.getNbMinutesNuit() != 0) {
			result.setMsNuit(result.getMsNuit() + minutes.getNbMinutesNuit());
			result.setMSup(result.getMSup() + minutes.getNbMinutesNuit());

			if (isHRecuperee) {
				result.setMsNuitRecup(result.getMsNuitRecup() + minutes.getNbMinutesNuit());
			}
		}

		return;
	}

	protected void countHSupDJFandNUITConventionCollective(VentilHsup result, MinutesupPeriod minutes, boolean isHRecuperee) {

		int totalMinutes = minutes.getTotalMinutes();
		DateTime dday = minutes.getInterval().getStart();

		// If it's a SUNDAY or a HOLIDAY, count the hours as HS DJF
		if (dday.getDayOfWeek() == 7 || sirhWsConsumer.isHoliday(dday)) {
			result.setMsdjf(result.getMsdjf() + totalMinutes);
			
			// Do the same calculus if the minutes are marked as "recuperee"
			if (isHRecuperee) {
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
	
	protected void countHSupDJFandNUITFonctionnaire(VentilHsup result, MinutesupPeriod minutes, boolean isHRecuperee, int weekMinutes) {
		
		if (minutes.getNbMinutesJour() <= 0 && minutes.getNbMinutesNuit() <= 0)
			return;

		DateTime dday = minutes.getInterval().getStart();
		// If it's a SUNDAY or a HOLIDAY, count the hours as HS DJF
		if (dday.getDayOfWeek() == 7 || sirhWsConsumer.isJourFerie(dday)) {
			result.setMsdjf(result.getMsdjf() + minutes.getNbMinutesJour());
			result.setMSup(result.getMSup() + minutes.getNbMinutesJour());

			if (isHRecuperee) {
				result.setMsdjfRecup(result.getMsdjfRecup() + minutes.getNbMinutesJour());
			}
		}

		// If we're doing HS Nuit then count them as HS Nuit
		if (minutes.getNbMinutesNuit() != 0) {
			result.setMsNuit(result.getMsNuit() + minutes.getNbMinutesNuit());
			result.setMSup(result.getMSup() + minutes.getNbMinutesNuit());

			if (isHRecuperee) {
				result.setMsNuitRecup(result.getMsNuitRecup() + minutes.getNbMinutesNuit());
			}
		}
	}

	public List<Pointage> getPointagesHSupForDay(List<Pointage> pointages, DateTime day) {

		List<Pointage> result = new ArrayList<Pointage>();

		for (Pointage ptg : pointages) {
			DateTime dday = new DateTime(ptg.getDateDebut());
			if (dday.getDayOfYear() == day.getDayOfYear() && dday.getYear() == day.getYear()
					&& ptg.getTypePointageEnum() == RefTypePointageEnum.H_SUP)
				result.add(ptg);
		}

		return result;
	}

	public MinutesupPeriod getMinutesJourNuitFromMinutesup(DateTime startDate, DateTime endDate, AgentStatutEnum statut) {

		List<Interval> intervals = new ArrayList<>();

		// If the interval overlaps on the next day
		if (startDate.getDayOfYear() != endDate.getDayOfYear()) {
			intervals.add(new Interval(startDate, new DateTime(endDate.getYear(), endDate.getMonthOfYear(), endDate
					.getDayOfMonth(), 0, 0, 0)));
			intervals.add(new Interval(new DateTime(endDate.getYear(), endDate.getMonthOfYear(), endDate
					.getDayOfMonth(), 0, 0, 0), endDate));
		} else {
			intervals.add(new Interval(startDate, endDate));
		}
		
		int nbMinutesJour = 0;
		int nbMinutesNuit = 0;

		for (Interval interval : intervals) {
			// Create the HS Jour interval for that period (the hours between
			// which hours are considered HS JOUR)
			Interval hSupJourInterval = getDayHSupJourIntervalForStatut(interval.getStart(), statut);

			// Calculate the overlap of the HS to determine what to be counted
			// as HS Nuit and HS Jour
			Interval hSupJourOverlap = hSupJourInterval.overlap(interval);
			int nbMinutesJourInterval = (int) (hSupJourOverlap == null ? 0 : hSupJourOverlap.toDuration().getStandardMinutes());
			nbMinutesJour += nbMinutesJourInterval;
			nbMinutesNuit += (int) (interval.toDuration().getStandardMinutes() - nbMinutesJourInterval);
		}

		return new MinutesupPeriod(new Interval(startDate, endDate), nbMinutesJour, nbMinutesNuit);
	}

	public void removeMinutesWhileUnderWeekBase(MinutesupPeriod minutes, int weekBase, int weekMinutesWithHSup,
			AgentStatutEnum statut, int dayBase) {

		// Define whether we need to cut the HS period (because it starts before
		// being an HS and ends after)
		if ((weekMinutesWithHSup - minutes.getTotalMinutes()) <= weekBase && weekMinutesWithHSup > weekBase) {

			// Number of hours to remove from HS
			int nbMinutesSupToNOTTake = weekBase - (weekMinutesWithHSup - minutes.getTotalMinutes());

			// Create the HS Jour interval for that day (the hours between which
			// hours are considered HS JOUR)
			Interval hSupJourInterval = getDayHSupJourIntervalForStatut(minutes.getInterval().getStart(), statut);

			// If the HS started during the day, we first remove hours from the
			// day period
			if (hSupJourInterval.contains(minutes.getInterval())) {
				int nbMinutesToRemove = minutes.getNbMinutesJour() - nbMinutesSupToNOTTake < 0 ? minutes
						.getNbMinutesJour() : nbMinutesSupToNOTTake;
				minutes.setNbMinutesJour(minutes.getNbMinutesJour() - nbMinutesToRemove);
				nbMinutesSupToNOTTake -= nbMinutesToRemove;

				minutes.setNbMinutesNuit(minutes.getNbMinutesNuit() - nbMinutesSupToNOTTake);
			}
			// Otherwise, we first remove hours from the night period
			else {
				int nbMinutesToRemove = minutes.getNbMinutesNuit() - nbMinutesSupToNOTTake < 0 ? minutes
						.getNbMinutesNuit() : nbMinutesSupToNOTTake;
				minutes.setNbMinutesNuit(minutes.getNbMinutesNuit() - nbMinutesToRemove);
				nbMinutesSupToNOTTake -= nbMinutesToRemove;

				minutes.setNbMinutesJour(minutes.getNbMinutesJour() - nbMinutesSupToNOTTake);
			}
		}

	}

	public Interval getDayHSupJourIntervalForStatut(DateTime day, AgentStatutEnum statut) {

		int startHour, endHour;

		switch (statut) {

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

		return new Interval(new DateTime(day.getYear(), day.getMonthOfYear(), day.getDayOfMonth(), startHour, 0, 0),
				new DateTime(day.getYear(), day.getMonthOfYear(), day.getDayOfMonth(), endHour, 0, 0));
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
