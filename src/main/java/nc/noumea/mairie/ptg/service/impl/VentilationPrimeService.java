package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.service.IVentilationPrimeService;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Service;

@Service
public class VentilationPrimeService implements IVentilationPrimeService {

	public static Integer PRIME_EPANDAGE_7716 = 7716;
	// List of rubrique to not aggregate because used for calculating other Primes
	// #13327 HSup SIPRES (épandage) : creation d une prime FICTIVE 7760
	private static List<Integer> RUBRIQUES_SAISIES_NOT_TAKEN = Arrays.asList(7715, PRIME_EPANDAGE_7716);
	
	@Override
	public List<VentilPrime> processPrimesAgent(Integer idAgent, List<Pointage> pointages, Date dateDebutMois) {
		
		Map<Integer, VentilPrime> primesByMonth = new HashMap<Integer, VentilPrime>();
		
		for (Pointage ptg : getPrimesPointages(pointages)) {
			
			Integer idRefPrime = ptg.getRefPrime().getIdRefPrime();
			
			if (RUBRIQUES_SAISIES_NOT_TAKEN.contains(ptg.getRefPrime().getNoRubr()))
				continue;
			
			if (!primesByMonth.containsKey(idRefPrime)) {
				VentilPrime vp = new VentilPrime();
				vp.setIdAgent(idAgent);
				vp.setRefPrime(ptg.getRefPrime());
				vp.setDateDebutMois(dateDebutMois);
				vp.setDatePrime(ptg.getDateDebut());
				vp.setEtat(EtatPointageEnum.VENTILE);
				primesByMonth.put(idRefPrime, vp);
			}
			
			primesByMonth.get(idRefPrime).addQuantite(getQuantiteFromPointage(ptg));
		}
		
		return new ArrayList<VentilPrime>(primesByMonth.values());
	}
	
	@Override
	public List<VentilPrime> processPrimesCalculeesAgent(Integer idAgent, List<PointageCalcule> pointages, Date dateDebutMois) {
		
		Map<Integer, VentilPrime> primesByMonth = new HashMap<Integer, VentilPrime>();
		
		for (PointageCalcule ptg : pointages) {
			
			Integer idRefPrime = ptg.getRefPrime().getIdRefPrime();
			
			if (!primesByMonth.containsKey(idRefPrime)) {
				VentilPrime vp = new VentilPrime();
				vp.setIdAgent(idAgent);
				vp.setRefPrime(ptg.getRefPrime());
				vp.setDateDebutMois(dateDebutMois);
				vp.setDatePrime(ptg.getDateDebut());
				vp.setEtat(EtatPointageEnum.VENTILE);
				primesByMonth.put(idRefPrime, vp);
			}
			
			primesByMonth.get(idRefPrime).addQuantite(ptg.getQuantite());
		}
		
		return new ArrayList<VentilPrime>(primesByMonth.values());
	}
	
	private List<Pointage> getPrimesPointages(List<Pointage> pointages) {
		
		List<Pointage> result = new ArrayList<Pointage>();
		
		for (Pointage ptg : pointages) {
			if (ptg.getTypePointageEnum() == RefTypePointageEnum.PRIME)
				result.add(ptg);
		}
		
		return result;
	}

	private Integer getQuantiteFromPointage(Pointage ptg) {
		
		switch (ptg.getRefPrime().getTypeSaisie()) {
			case CASE_A_COCHER:
			case NB_HEURES:
			case NB_INDEMNITES:
				return ptg.getQuantite();

			case PERIODE_HEURES:
				return (int) new Interval(new DateTime(ptg.getDateDebut()), (new DateTime(ptg.getDateFin())))
						.toDuration().getStandardMinutes();
		}
		
		return 0;
	}
}
