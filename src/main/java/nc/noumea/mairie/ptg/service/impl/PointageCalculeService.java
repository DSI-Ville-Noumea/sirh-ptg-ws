package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
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
	private IHolidayService holidayService;
	
	/**
	 * Calculating a list of PointageCalcule for an agent over a week (from = monday, to = sunday)
	 * Based on its RefPrime at the time of the monday
	 */
	public List<PointageCalcule> calculatePointagesForAgentAndWeek(Integer idAgent, AgentStatutEnum statut, Date dateLundi, List<Pointage> agentPointages) {
		
		List<Integer> norubrs = mairieRepository.getPrimePointagesByAgent(idAgent, dateLundi);
		List<RefPrime> refPrimes = pointageRepository.getRefPrimes(norubrs, statut);
		
		List<PointageCalcule> pointagesCalcules = new ArrayList<PointageCalcule>();
		
		for (RefPrime prime : refPrimes) {
			
			switch (prime.getNoRubr()) {
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
	
	public List<PointageCalcule> generatePointage7711_12_13(Integer idAgent, Date dateLundi, RefPrime prime, List<Pointage> pointages) {
		
		List<PointageCalcule> result = new ArrayList<PointageCalcule>();
		
		for (Pointage ptg : getPointagesPrime(pointages, 7715)) {
			
			LocalDate datePointage = new DateTime(ptg.getDateDebut()).toLocalDate();
			
			Interval inputInterval = new Interval(new DateTime(ptg.getDateDebut()), new DateTime(ptg.getDateFin()));
			Interval primeIntervalInverse = new Interval(
					new DateTime(datePointage.getYear(), datePointage.getMonthOfYear(), datePointage.getDayOfMonth(), 5, 0, 0),
					new DateTime(datePointage.getYear(), datePointage.getMonthOfYear(), datePointage.getDayOfMonth(), 21, 0, 0));
			
			Interval overlap = primeIntervalInverse.overlap(inputInterval);
			long dayMinutes = overlap == null ? 0 : overlap.toDuration().getStandardMinutes();
			long totalMinutes = inputInterval.toDuration().getStandardMinutes();
			
			if (prime.getNoRubr().equals(7712)
					&& (datePointage.getDayOfWeek() == DateTimeConstants.SUNDAY || holidayService.isHoliday(datePointage))) {
				PointageCalcule existingPc = getPointageCalculeOfSamePrime(result, datePointage.toDate());
				existingPc = returnOrCreateNewPointageWithPrime(null, ptg, prime);
				existingPc.addQuantite((int) (totalMinutes / 60));

				if (!result.contains(existingPc))
					result.add(existingPc);
				
				continue;
			}
			
			if (prime.getNoRubr().equals(7711)) {
				PointageCalcule existingPc = getPointageCalculeOfSamePrime(result, datePointage.toDate());
				existingPc = returnOrCreateNewPointageWithPrime(null, ptg, prime);
				existingPc.addQuantite((int) ((totalMinutes - dayMinutes) / 60));

				if (!result.contains(existingPc))
					result.add(existingPc);
				
				continue;
			}

			if (prime.getNoRubr().equals(7713)) {

				if (inputInterval.toDuration().toStandardHours().getHours() >= 9
						|| inputInterval.getStart().getHourOfDay() <= 5 && inputInterval.getEnd().getHourOfDay() >= 13
						|| inputInterval.getStart().getHourOfDay() <= 13 && inputInterval.getEnd().getHourOfDay() >= 21) {
					PointageCalcule existingPc = getPointageCalculeOfSamePrime(result, datePointage.toDate());
					existingPc = returnOrCreateNewPointageWithPrime(existingPc, ptg, prime);
					
					if (existingPc.getQuantite() == null ||  existingPc.getQuantite() < 2)
						existingPc.addQuantite(1);
					
					if (!result.contains(existingPc))
						result.add(existingPc);
				}
			}
		}
		
		return result;
	}
	
	public List<PointageCalcule> generatePointage7720_21_22(Integer idAgent, Date dateLundi, RefPrime prime, List<Pointage> pointages) {

		List<PointageCalcule> result = new ArrayList<PointageCalcule>();
		
		for (Pointage ptg : getPointagesPrime(pointages, 7701)) {
			PointageCalcule existingPc = returnOrCreateNewPointageWithPrime(null, ptg, prime);
			existingPc.addQuantite(ptg.getQuantite());
			result.add(existingPc);
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
		pCalcule.setType(pointageRepository.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue()));
		
		return pCalcule;
	}
	
}
