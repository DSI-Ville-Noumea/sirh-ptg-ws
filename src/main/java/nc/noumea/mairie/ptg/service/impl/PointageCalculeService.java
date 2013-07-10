package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IHolidayService;
import nc.noumea.mairie.ptg.service.IPointageCalculeService;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.sirh.domain.Agent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageCalculeService implements IPointageCalculeService {

	@Autowired 
	private IMairieRepository mairieRepository;
	
	@Autowired
	private IPointageRepository pointageRepository;
	
	@Autowired
	private IPointageService pointageService;
	
	@Autowired
	private IHolidayService holidayService;
	
	/**
	 * Calculating a list of PointageCalcule for an agent over a week (from = monday, to = sunday)
	 * Based on its RefPrime at the time of the monday
	 */
	public List<PointageCalcule> calculatePointagesForAgentAndWeek(Integer idAgent, AgentStatutEnum statut, Date dateLundi) {
		
		// List of the pointages of the concerned week
		List<Pointage> agentPointages = pointageService.getLatestPointagesForAgentAndDates(
				idAgent, dateLundi, new DateTime(dateLundi).plusWeeks(1).toDate(), null, 
				Arrays.asList(EtatPointageEnum.APPROUVE, EtatPointageEnum.VENTILE, EtatPointageEnum.JOURNALISE));
		
		List<Integer> norubrs = mairieRepository.getPrimePointagesByAgent(idAgent, dateLundi);
		List<RefPrime> refPrimes = pointageRepository.getRefPrimes(norubrs, statut);
		
		List<PointageCalcule> pointagesCalcules = new ArrayList<PointageCalcule>();
		
		for (RefPrime prime : refPrimes) {
			
			switch (prime.getNoRubr()) {
//				case 7701:
//					pointagesCalcules.addAll(generatePointage7701(idAgent, dateLundi, prime, agentPointages));
//					break;
				case 7711:
				case 7712:
				case 7713:
					pointagesCalcules.addAll(generatePointage7711_12_13(idAgent, dateLundi, prime, agentPointages));
					break;
				case 7720:
				case 7721:
				case 7722:
					pointagesCalcules.addAll(generatePointage7720_21_22(idAgent, dateLundi, prime, agentPointages));
					continue;
			}
		}
		
		return pointagesCalcules;
	}
	
	public List<PointageCalcule> generatePointage7701(Integer idAgent, Date dateLundi, RefPrime prime, List<Pointage> pointages) {
		
		List<PointageCalcule> result = new ArrayList<PointageCalcule>();

		// Retrieve the agent Spcarr
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(Agent.getNoMatrFromIdAgent(idAgent), dateLundi);
				
		for (int i = 0; i < 7; i++) {
			
			DateTime dday = new DateTime(dateLundi).plusDays(i);
			int dayTotalMinutes = carr.getSpbase().getDayBaseInMinutes(i);
			
			for (Pointage ptg : getPointagesHSupForDay(pointages, dday)) {
				dayTotalMinutes += new Interval(new DateTime(ptg.getDateDebut()), new DateTime(ptg.getDateFin())).toDuration().getStandardMinutes();
			}
			
			if (dayTotalMinutes <= 0)
				continue;
			
			PointageCalcule ptgCalc = new PointageCalcule();
			ptgCalc.setIdAgent(idAgent);
			ptgCalc.setDateDebut(dday.toDate());
			ptgCalc.setDateLundi(dateLundi);
			ptgCalc.setEtat(EtatPointageEnum.VENTILE);
			ptgCalc.setRefPrime(prime);
			ptgCalc.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME));
			ptgCalc.setQuantite(dayTotalMinutes / 60); // because our sum was in minutes, we only take full hours
			result.add(ptgCalc);
		}
		
		return result;
	}
	
	public List<PointageCalcule> generatePointage7711_12_13(Integer idAgent, Date dateLundi, RefPrime prime, List<Pointage> pointages) {
		
		List<PointageCalcule> result = new ArrayList<PointageCalcule>();
		
		for (Pointage ptg : getPointagesPrime(pointages, 7715)) {
			
			LocalDate datePointage = new DateTime(ptg.getDateDebut()).toLocalDate();
			
			Interval inputInterval = new Interval(new DateTime(ptg.getDateDebut()), new DateTime(ptg.getDateFin()));
			Interval primeIntervalInverse = new Interval(
					new DateTime(datePointage.getYear(), datePointage.getMonthOfYear(), datePointage.getDayOfMonth(), 5, 0, 0),
					new DateTime(datePointage.getYear(), datePointage.getMonthOfYear(), datePointage.getDayOfMonth(), 21, 0, 0));
			
			long totalMinutes = inputInterval.toDuration().minus(primeIntervalInverse.overlap(inputInterval).toDuration()).getStandardMinutes();
			
			if (prime.getNoRubr().equals(7712)
					&& (datePointage.getDayOfWeek() == DateTimeConstants.SUNDAY || holidayService.isHoliday(datePointage))) {
				PointageCalcule existingPc = getPointageCalculeOfSamePrime(result, datePointage.toDate());
				returnOrCreateNewPointageWithPrime(existingPc, ptg, prime);
				existingPc.addQuantite((int) (totalMinutes / 60));
				continue;
			}
			
			if (prime.getNoRubr().equals(7711)) {
				PointageCalcule existingPc = getPointageCalculeOfSamePrime(result, datePointage.toDate());
				returnOrCreateNewPointageWithPrime(existingPc, ptg, prime);
				existingPc.addQuantite((int) (totalMinutes / 60));
				continue;
			}

			if (prime.getNoRubr().equals(7713)) {

				DateTime d1 = new DateTime(datePointage.getYear(), datePointage.getMonthOfYear(), datePointage.getDayOfMonth(), 5, 0, 0);
				DateTime d2 = new DateTime(datePointage.getYear(), datePointage.getMonthOfYear(), datePointage.getDayOfMonth(), 13, 0, 0);
				DateTime d3 = new DateTime(datePointage.getYear(), datePointage.getMonthOfYear(), datePointage.getDayOfMonth(), 21, 0, 0);

				if (inputInterval.toDuration().toStandardHours().getHours() >= 9
					|| inputInterval.contains(d1) && inputInterval.contains(d2)
					|| inputInterval.contains(d2) && inputInterval.contains(d3)) {
					PointageCalcule existingPc = getPointageCalculeOfSamePrime(result, datePointage.toDate());
					returnOrCreateNewPointageWithPrime(existingPc, ptg, prime);
					
					if (existingPc.getQuantite() < 2)
						existingPc.addQuantite(1);
				}
			}
		}
		
		return result;
	}
	
	private List<PointageCalcule> generatePointage7720_21_22(Integer idAgent, Date dateLundi, RefPrime prime, List<Pointage> pointages) {

		List<PointageCalcule> result = new ArrayList<PointageCalcule>();
		
		for (Pointage ptg : getPointagesPrime(pointages, 7701)) {
			PointageCalcule existingPc = returnOrCreateNewPointageWithPrime(null, ptg, prime);
			existingPc.addQuantite(ptg.getQuantite());
		}
		
		return result;
		
	}
	
	private List<Pointage> getPointagesHSupForDay(List<Pointage> pointages, DateTime day) {
		
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
	
	private List<Pointage> getPointagesPrime(List<Pointage> pointages, Integer noRubr) {
		
		List<Pointage> result = new ArrayList<Pointage>();
		
		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.PRIME
					&& ptg.getRefPrime().getNoRubr().equals(noRubr))
				result.add(ptg);
		}
		
		return result;
	}

	/**
	 * Retrieves a PointageCalcule for a given date
	 * this is used by Pointage generators to avoid creating multiple primes for one day
	 * @param list
	 * @param pointageDate
	 * @return
	 */
	private PointageCalcule getPointageCalculeOfSamePrime(List<PointageCalcule> list, Date pointageDate) {
		
		PointageCalcule existingPc = null;
		
		for (PointageCalcule pc : list) {
			if (pc.getDateDebut().equals(pointageDate)) {
				existingPc = pc;
				break;
			}
		}
		
		return existingPc;
	}
	
	/**
	 * This methods either returns the pointageCalcule if not null or create a new one
	 * according to the pointage and the prime generating it.
	 * This is used by Pointage generators to create new Pointage calcules the same way for all methods
	 * @param pCalcule
	 * @param ptg
	 * @param prime
	 * @return
	 */
	private PointageCalcule returnOrCreateNewPointageWithPrime(PointageCalcule pCalcule, Pointage ptg, RefPrime prime) {
		
		if (pCalcule != null)
			return pCalcule;
		
		DateTime start = new DateTime(ptg.getDateDebut());
		Date newPrimeStartDate = new DateTime(start.getYear(), start.getMonthOfYear(), start.getDayOfMonth(), 0, 0, 0).toDate();
		
		pCalcule = new PointageCalcule();
		pCalcule.setIdAgent(ptg.getIdAgent());
		pCalcule.setDateLundi(ptg.getDateLundi());
		pCalcule.setDateDebut(newPrimeStartDate);
		pCalcule.setEtat(EtatPointageEnum.VENTILE);
		pCalcule.setRefPrime(prime);
		pCalcule.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME));
		
		return pCalcule;
	}
	
}
