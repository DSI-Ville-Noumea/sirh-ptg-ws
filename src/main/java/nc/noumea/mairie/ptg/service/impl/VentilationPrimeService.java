package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IVentilationPrimeService;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentilationPrimeService implements IVentilationPrimeService {

	public final static Integer PRIME_EPANDAGE_7716 = 7716;

	public final static int PRIME_TID_7720 = 7720;
	public final static int PRIME_TID_7721 = 7721;
	public final static int PRIME_TID_7722 = 7722;
	public final static int PRIME_TID_EXCEP_7723 = 7723;
	public final static int PRIME_TID_EXCEP_7724 = 7724;
	public final static int PRIME_TID_EXCEP_7725 = 7725;
	
	public final static int PRIME_RENFORT_GARDE = 7717;
	public final static int INDEMNITE_DE_ROULEMENT = 7715;
	public final static int INDEMNITE_FORFAITAIRE_TRAVAIL_DPM = 7714;
	public final static int	INDEMNITE_FORFAITAIRE_TRAVAIL_DPM_SAMEDI	= 7718;
	public final static int	INDEMNITE_FORFAITAIRE_TRAVAIL_DPM_DJF		= 7719;

	public final static int INDEMNITE_TRAVAIL_DJF = 7756;
	public final static int INDEMNITE_TRAVAIL_NUIT = 7757;
	
	// List of rubrique to not aggregate because used for calculating other Primes
	// #13327 HSup SIPRES (épandage) : creation d une prime FICTIVE 7760
	//#35605 : on ajoute la nouvelle prime DPM
	private final static List<Integer> RUBRIQUES_SAISIES_NOT_TAKEN = Arrays.asList(
			INDEMNITE_DE_ROULEMENT, PRIME_EPANDAGE_7716, PRIME_RENFORT_GARDE,
			INDEMNITE_FORFAITAIRE_TRAVAIL_DPM);

	@Autowired
	private IPointageRepository pointageRepository;
	
	@Override
	public List<VentilPrime> processPrimesAgent(Integer idAgent, List<Pointage> pointages, Date dateDebutMois, AgentStatutEnum statut) {
		
		Map<Integer, VentilPrime> primesByMonth = new HashMap<Integer, VentilPrime>();
		
		for (Pointage ptg : getPrimesPointages(pointages)) {
			
			Integer idRefPrime = ptg.getRefPrime().getIdRefPrime();
			
			if (RUBRIQUES_SAISIES_NOT_TAKEN.contains(ptg.getRefPrime().getNoRubr()))
				continue;
			
			// #18234 cas particulier des primes TID exceptionnelles qui fusionnent avec les autres TID "normales"
			List<RefPrime> listPrimes = null;
			// Bug #33902 : si pas de TID normales alors l'agent n'était jamais payé
			RefPrime primeRemplacement = null;
			switch (ptg.getRefPrime().getNoRubr()) {
				case PRIME_TID_EXCEP_7723:
					listPrimes = pointageRepository.getRefPrimes(Arrays.asList(PRIME_TID_7720), statut);
					if(null != listPrimes
							&& !listPrimes.isEmpty()) {
						idRefPrime = listPrimes.get(0).getIdRefPrime();
						primeRemplacement = listPrimes.get(0);
					}
					break;
				case PRIME_TID_EXCEP_7724:
					listPrimes = pointageRepository.getRefPrimes(Arrays.asList(PRIME_TID_7721), statut);
					if(null != listPrimes
							&& !listPrimes.isEmpty()) {
						idRefPrime = listPrimes.get(0).getIdRefPrime();
						primeRemplacement = listPrimes.get(0);
					}
					break;
				case PRIME_TID_EXCEP_7725:
					listPrimes = pointageRepository.getRefPrimes(Arrays.asList(PRIME_TID_7722), statut);
					if(null != listPrimes
							&& !listPrimes.isEmpty()) {
						idRefPrime = listPrimes.get(0).getIdRefPrime();
						primeRemplacement = listPrimes.get(0);
					}
					break;
			}
			
			if (!primesByMonth.containsKey(idRefPrime)) {
				VentilPrime vp = new VentilPrime();
				vp.setIdAgent(idAgent);
				vp.setRefPrime(primeRemplacement == null ? ptg.getRefPrime() : primeRemplacement);
				vp.setDateDebutMois(dateDebutMois);
				vp.setDatePrime(ptg.getDateDebut());
				vp.setEtat(EtatPointageEnum.VENTILE);
				primesByMonth.put(idRefPrime, vp);
			}
			
			Integer quantite = getQuantiteFromPointage(ptg);
			primesByMonth.get(idRefPrime).addQuantite(null != quantite ? quantite.doubleValue() : 0.0);
		}
		
		return new ArrayList<VentilPrime>(primesByMonth.values());
	}
	
	@Override
	public List<VentilPrime> processPrimesCalculeesAgent(Integer idAgent, List<PointageCalcule> pointages, Date dateDebutMois) {
		
		Map<Integer, VentilPrime> primesByMonth = new HashMap<Integer, VentilPrime>();
		
		for (PointageCalcule ptg : pointages) {
			
			if(ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME)
				continue;
			
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

	protected Integer getQuantiteFromPointage(Pointage ptg) {
		
		switch (ptg.getRefPrime().getTypeSaisie()) {
			case CASE_A_COCHER:
			case NB_HEURES:
			case NB_INDEMNITES:
				return ptg.getQuantite();

			case PERIODE_HEURES:
				// #46679 : Nouvelle prime, les quantités doivent être transformées pour arrondir chaque saisie et avoir la valeur en heures
				if (ptg.getRefPrime().getNoRubr().equals(INDEMNITE_TRAVAIL_NUIT)
						|| ptg.getRefPrime().getNoRubr().equals(INDEMNITE_TRAVAIL_DJF)) {
					Integer tmp = (int) new Interval(new DateTime(ptg.getDateDebut()), (new DateTime(ptg.getDateFin())))
							.toDuration().getStandardMinutes();
					return (int) Math.round(tmp/60d);
				}
				else
					return (int) new Interval(new DateTime(ptg.getDateDebut()), (new DateTime(ptg.getDateFin())))
						.toDuration().getStandardMinutes();
		}
		
		return 0;
	}
}
