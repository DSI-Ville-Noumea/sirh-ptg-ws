package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Sppprm;
import nc.noumea.mairie.domain.SppprmId;
import nc.noumea.mairie.domain.Spprim;
import nc.noumea.mairie.domain.SpprimId;
import nc.noumea.mairie.ptg.domain.MairiePrimeTableEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.repository.IExportPaieRepository;
import nc.noumea.mairie.ptg.service.IExportPaiePrimeService;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExportPaiePrimeService implements IExportPaiePrimeService {

	@Autowired
	private IExportPaieRepository exportPaieRepository;
	
	@Autowired
	private HelperService helperService;
	
	@Override
	public List<Sppprm> exportPrimesJourToPaie(List<Pointage> pointagesOrderedByDateAsc) {
		
		List<Sppprm> modifiedOrAddedSppprm = new ArrayList<Sppprm>();
		
		for (Pointage ptg : pointagesOrderedByDateAsc) {
			
			if (ptg.getTypePointageEnum() != RefTypePointageEnum.PRIME
				|| ptg.getRefPrime().getMairiePrimeTableEnum() != MairiePrimeTableEnum.SPPPRM)
				continue;
			
			// Fetch or create Sppprm
			Sppprm prm = findOrCreateSppprmRecord(modifiedOrAddedSppprm, ptg.getIdAgent(), ptg.getDateDebut(), ptg
					.getRefPrime().getNoRubr(), ptg.getQuantite());

			if (prm == null)
				continue;
			
			switch (ptg.getRefPrime().getTypeSaisie()) {
				case NB_HEURES:
				case PERIODE_HEURES:
					prm.setNbPrime(helperService.convertMinutesToMairieNbHeuresFormat(ptg.getQuantite()));
					break;
				case CASE_A_COCHER:
				case NB_INDEMNITES:
				default:
					prm.setNbPrime(ptg.getQuantite());
					break;

			}
			
			// If quantity is 0, remove this record from the list of Sppprm
			if (prm.getNbPrime() == 0) {
				modifiedOrAddedSppprm.remove(prm);
				exportPaieRepository.removeEntity(prm);
			}

		}
		
		return modifiedOrAddedSppprm;
	}
	
	@Override
	public List<Sppprm> exportPrimesCalculeesJourToPaie(List<PointageCalcule> pointagesCalculesOrderedByDateAsc) {

		List<Sppprm> modifiedOrAddedSppprm = new ArrayList<Sppprm>();
		
		for (PointageCalcule ptgC : pointagesCalculesOrderedByDateAsc) {
			
			if (ptgC.getTypePointageEnum() != RefTypePointageEnum.PRIME
				|| ptgC.getRefPrime().getMairiePrimeTableEnum() != MairiePrimeTableEnum.SPPPRM)
				continue;
			
			// Fetch or create Sppprm
			Sppprm prm = findOrCreateSppprmRecord(modifiedOrAddedSppprm, ptgC.getIdAgent(), ptgC.getDateDebut(), ptgC
					.getRefPrime().getNoRubr(), ptgC.getQuantite());
		
			if (prm == null)
				continue;
			
			switch (ptgC.getRefPrime().getTypeSaisie()) {
				case NB_HEURES:
				case PERIODE_HEURES:
					prm.setNbPrime(helperService.convertMinutesToMairieNbHeuresFormat(ptgC.getQuantite()));
					break;
				case CASE_A_COCHER:
				case NB_INDEMNITES:
				default:
					prm.setNbPrime(ptgC.getQuantite());
					break;

			}
			
			// If quantity is 0, remove this record from the list of Sppprm
			if (prm.getNbPrime() == 0) {
				modifiedOrAddedSppprm.remove(prm);
				exportPaieRepository.removeEntity(prm);
			}

		}
		
		return modifiedOrAddedSppprm;
		
	}
	
	protected Sppprm findOrCreateSppprmRecord(List<Sppprm> existingRecords, Integer idAgent, Date dateJour, Integer norubr, Integer qte) {
		
		Sppprm prm = null;
		
		Integer nomatr = helperService.getMairieMatrFromIdAgent(idAgent);
		Integer dateJourMairie = helperService.getIntegerDateMairieFromDate(dateJour);
		
		// First search through existing spacti
		for (Sppprm a : existingRecords) {
			if (a.getId().getNomatr().equals(nomatr) 
				&& a.getId().getDatJour().equals(dateJourMairie)
				&& a.getId().getNoRubr().equals(norubr)) {
				return a;
			}
		}

		// Then Look for an exising record already existing in the DB
		prm = exportPaieRepository.getSppprmForDayAgentAndNorubr(idAgent, dateJour, norubr);
		
		// At last create a new record, only if the qte is different than 0
		if (prm == null && !qte.equals(0)) {
			prm = new Sppprm();
			prm.setId(new SppprmId(nomatr, dateJourMairie, norubr));
		}
		
		if (prm != null)
			existingRecords.add(prm);
		
		return prm;
	}

	@Override
	public List<Spprim> exportPrimesMoisToPaie(List<VentilPrime> ventilPrimeOrderedByDateAsc) {

		List<Spprim> pris = new ArrayList<Spprim>();
		
		for (VentilPrime ventilPrime : ventilPrimeOrderedByDateAsc) {
			
			if (ventilPrime.getRefPrime().getMairiePrimeTableEnum() != MairiePrimeTableEnum.SPPRIM)
				continue;
			
			// Fetch or create Spprim
			Spprim pri = findOrCreateSpprimmRecord(ventilPrime.getIdAgent(), ventilPrime.getDateDebutMois(),
					ventilPrime.getRefPrime().getNoRubr(), ventilPrime.getQuantite());
			
			if (pri == null)
				continue;
			
			// Fill in the number of Primes for the month
			double qte = 0;
			if (ventilPrime.getRefPrime().getTypeSaisie() == TypeSaisieEnum.NB_HEURES)
				qte = helperService.convertMinutesToMairieNbHeuresFormat(ventilPrime.getQuantite());
			else
				qte = ventilPrime.getQuantite();
			
			pri.setMontantPrime(Math.ceil(qte));
			
			// Add the item to the list of hre modified/created
			if (pri.getMontantPrime() != 0)
				pris.add(pri);
			else
				exportPaieRepository.removeEntity(pri);
		}
		
		return pris;
	}
	
	protected Spprim findOrCreateSpprimmRecord(Integer idAgent, Date dateDebutMois, Integer norubr, Integer qte) {
		
		Spprim pri = null;
		
		Integer nomatr = helperService.getMairieMatrFromIdAgent(idAgent);
		Integer dateDebMoisMairie = helperService.getIntegerDateMairieFromDate(dateDebutMois);

		// Look for an exising record already existing in the DB
		pri = exportPaieRepository.getSpprimForDayAgentAndNorubr(idAgent, dateDebutMois, norubr);
		
		// At last create a new record
		if (pri == null && !qte.equals(0)) {
			pri = new Spprim();
			pri.setId(new SpprimId(nomatr, dateDebMoisMairie, norubr));
			Date dateFinMois = new LocalDate(dateDebutMois).plusMonths(1).withDayOfMonth(1).toDate();
			pri.setDateFin(helperService.getIntegerDateMairieFromDate(dateFinMois));
		}
		
		return pri;
	}

}
