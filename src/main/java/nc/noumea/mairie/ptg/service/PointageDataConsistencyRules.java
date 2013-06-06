package nc.noumea.mairie.ptg.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spcong;
import nc.noumea.mairie.domain.Sprirc;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.SaisieReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.sirh.domain.Agent;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageDataConsistencyRules implements IPointageDataConsistencyRules {

	@Autowired
	private IMairieRepository mairieRepository;
	
	@Autowired
	private HelperService helperService;

	//-- MESSAGES --//
	public static final String BASE_HOR_MAX = "L'agent dépasse sa base horaire";
	public static final String RECUP_MSG = "%s : L'agent est en récupération sur cette période.";
	public static final String CONGE_MSG = "%s : L'agent est en congés payés sur cette période.";
	public static final String MALADIE_MSG = "%s : L'agent est en maladie sur cette période.";
	public static final String HS_INA_315_MSG = "L'agent n'a pas droit aux HS sur la période (INA > 315)";
	public static final String BASE_HOR_Z_MSG = "L'agent est en base horaire \"Z\" sur la période";
	public static final String INACTIVITE_MSG = "L'agent n'est pas en activité sur cette période.";
	public static final String AVERT_MESSAGE_ABS = "Soyez vigilant, vous avez saisi des primes et/ou heures supplémentaires sur des périodes où l’agent était absent.";
	
	public static final List<String> ACTIVITE_CODES = Arrays.asList("01", "02", "03", "04", "23", "24", "60", "61", "62", "63", "64", "65", "66");
	
	@Override
	public SaisieReturnMessageDto checkMaxAbsenceHebdo(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {

		double nbHours = 0;
		
		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.ABSENCE)
				continue;

			DateTime deb = new DateTime(ptg.getDateDebut());
			DateTime fin = new DateTime(ptg.getDateFin());
			
			nbHours += (Minutes.minutesBetween(deb, fin).getMinutes() / 60.0);
		}
		
		if (nbHours == 0)
			return srm;
		
		Agent ag = mairieRepository.getAgent(idAgent);
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(ag, dateLundi);
		
		double agentMaxHours = carr.getSpbhor().getTaux() * carr.getSpbase().getNbashh();
		
		if (nbHours > agentMaxHours)
			srm.getErrors().add(BASE_HOR_MAX);
		
		return srm;
	}
	
	@Override
	public SaisieReturnMessageDto checkSprircRecuperation(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {
		
		Date end = new DateTime(dateLundi).plusDays(7).toDate();
		
		List<Sprirc> recups = mairieRepository.getListRecuperationBetween(idAgent, dateLundi, end);
		
		for (Sprirc recup : recups) {
			checkInterval(srm, RECUP_MSG, recup.getId().getDatdeb(), recup
					.getId().getCodem1(), recup.getDatfin(), recup.getCodem2(),
					pointages);
		}
		
		return srm;
	}

	@Override
	public SaisieReturnMessageDto checkSpcongConge(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {
		
		Date end = new DateTime(dateLundi).plusDays(7).toDate();
		
		List<Spcong> conges = mairieRepository.getListCongeBetween(idAgent, dateLundi, end);
		
		for (Spcong cg : conges) {
			checkInterval(srm, CONGE_MSG, cg.getId().getDatdeb(), cg.getCodem1(), cg.getDatfin(), cg.getCodem2(), pointages);
		}
		
		return srm;
	}
	
	@Override
	public SaisieReturnMessageDto checkSpabsenMaladie(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {
		
		Date end = new DateTime(dateLundi).plusDays(7).toDate();
		
		List<Spabsen> maladies = mairieRepository.getListMaladieBetween(idAgent, dateLundi, end);
		
		for (Spabsen mal : maladies) {
			checkInterval(srm, MALADIE_MSG, mal.getId().getDatdeb(), null, mal.getDatfin(), null, pointages);
		}
		
		return srm;
	}
	
	@Override
	public SaisieReturnMessageDto checkAgentINAAndHSup(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {

		Agent ag = mairieRepository.getAgent(idAgent);
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(ag, dateLundi);
		
		if ((carr.getStatutCarriere() != AgentStatutEnum.F || carr.getSpbarem().getIna() <= 315)
				&& !carr.getSpbase().getCdBase().equals("Z"))
			return srm;

		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.H_SUP) {
				
				if (carr.getSpbarem().getIna() > 315)
					srm.getErrors().add(HS_INA_315_MSG);
				else
					srm.getErrors().add(BASE_HOR_Z_MSG);
				
				break;
			}
		}
		
		return srm;
	}
	
	@Override
	public SaisieReturnMessageDto checkAgentInactivity(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {

		Agent ag = mairieRepository.getAgent(idAgent);
		Spadmn adm = mairieRepository.getAgentCurrentPosition(ag, dateLundi);
		
		if (!ACTIVITE_CODES.contains(adm.getCdpadm()))
			srm.getErrors().add(INACTIVITE_MSG);

		return srm;
	}
	
	//-- Helpers --//
	
	protected DateTime getDateDebut(Integer dateDeb, Integer codem1) {
		DateTime recupDateDeb = new DateTime(helperService.getDateFromMairieInteger(dateDeb));
		
		if (codem1 != null && codem1.equals(2))
			recupDateDeb = recupDateDeb.plusHours(12); // 12h00
		
		return recupDateDeb;
	}

	protected DateTime getDateFin(Integer dateFin, Integer codem2) {
		DateTime recupDateFin = new DateTime(helperService.getDateFromMairieInteger(dateFin));
		
		if (codem2 == null ||  codem2.equals(2))
			return recupDateFin.plusDays(1); // 00h00 D+1
		
		recupDateFin = recupDateFin.plusMinutes(691); // 12h00

		return recupDateFin;
	}

	/**
	 * This methods checks whether a list of pointages are being 
	 * input in a given period. This period can start or end 
	 * by full or half a day. 
	 * @param srm The structure to return the INFO or ERROR messages
	 * @param message The message format to return
	 * @param start The start day of the given period
	 * @param codem1 Whether the start day is a full day or a half day (1, 2)
	 * @param end The end day of the given period
	 * @param codem2 Whether the end day is a full day or a half day (1, 2)
	 * @param pointages The list of pointages to test the period against
	 * @return The structure containing the INFO or ERROR messages
	 */
	protected SaisieReturnMessageDto checkInterval(
			SaisieReturnMessageDto srm, 
			String message, 
			Integer start, 
			Integer codem1, 
			Integer end, 
			Integer codem2, 
			List<Pointage> pointages) {
		
		DateTime recupDateDeb = getDateDebut(start, codem1);
		DateTime recupDateFin = getDateFin(end, codem2);
		
		int dayOfYearDeb = new DateTime(recupDateDeb).getDayOfYear();
		int dayOfYearFin = new DateTime(recupDateFin).getDayOfYear();
		boolean partialDayDeb = recupDateDeb.getHourOfDay() != 0;
		boolean partialDayFin = recupDateFin.getHourOfDay() != 0;
		
		DateTime recupDateDebFull = getDateDebut(start, null);
		DateTime recupDateFinFull = getDateFin(end, null);
		
		Interval rInterval = new Interval(recupDateDebFull, recupDateFinFull);
		
		for (Pointage ptg : pointages) {

			DateTime ptgTimeStart = new DateTime(ptg.getDateDebut());
			DateTime ptgTimeEnd = new DateTime(ptg.getDateFin() == null ? ptg.getDateDebut() : ptg.getDateFin());
			
			Interval pInterval = new Interval(ptgTimeStart, ptgTimeEnd);
			
			if (rInterval.overlaps(pInterval)) {
				
				String msg = String.format(message, ptgTimeStart.toString("dd/MM/yyyy HH:mm"));
				
				if (ptgTimeStart.dayOfYear().get() == dayOfYearDeb && partialDayDeb
					|| ptgTimeStart.dayOfYear().get() == dayOfYearFin && partialDayFin
					|| ptgTimeEnd.dayOfYear().get() == dayOfYearDeb && partialDayDeb
					|| ptgTimeEnd.dayOfYear().get() == dayOfYearFin && partialDayFin) {
					if (!srm.getInfos().contains(AVERT_MESSAGE_ABS)) {
						srm.getInfos().add(AVERT_MESSAGE_ABS);
					}
				}
				else
					srm.getErrors().add(msg);
			}
		}
		
		return srm;
	}

	/**
	 * Processes the data consistency of a set of Pointages being input by a user.
	 * It will check the different business rules in order to make sure they're consistent
	 */
	@Override
	public void processDataConsistency(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages) {
		checkSprircRecuperation(srm, idAgent, dateLundi, pointages);
		checkSpcongConge(srm, idAgent, dateLundi, pointages);
		checkSpabsenMaladie(srm, idAgent, dateLundi, pointages);
		checkMaxAbsenceHebdo(srm, idAgent, dateLundi, pointages);
		checkAgentINAAndHSup(srm, idAgent, dateLundi, pointages);
		checkAgentInactivity(srm, idAgent, dateLundi, pointages);
	}

}