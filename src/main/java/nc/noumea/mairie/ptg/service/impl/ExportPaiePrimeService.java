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
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.repository.IExportPaieRepository;
import nc.noumea.mairie.ptg.service.IExportPaiePrimeService;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

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
			
			// Fetch or create Sppact
			Sppprm prm = findOrCreateSppprmRecord(modifiedOrAddedSppprm, ptg.getIdAgent(), ptg.getDateDebut(), ptg.getRefPrime().getNoRubr());
			prm.setNbPrime(ptg.getQuantite());
			
			// If quantity is 0, remove this record from the list of Sppprm
			if (prm.getNbPrime() == 0) {
				modifiedOrAddedSppprm.remove(prm);
				prm.remove();
			}
				
		}
		
		return modifiedOrAddedSppprm;
	}
	
	protected Sppprm findOrCreateSppprmRecord(List<Sppprm> existingRecords, Integer idAgent, Date dateJour, Integer norubr) {
		
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
		
		// At last create a new record
		if (prm == null) {
			prm = new Sppprm();
			prm.setId(new SppprmId(nomatr, dateJourMairie, norubr));
		}
		
		existingRecords.add(prm);
		
		return prm;
	}

	@Override
	public List<Spprim> exportPrimesMoisToPaie(List<VentilPrime> ventilPrimeOrderedByDateAsc) {

		List<Spprim> pris = new ArrayList<Spprim>();
		
		for (VentilPrime ventilPrime : ventilPrimeOrderedByDateAsc) {
			
			if (ventilPrime.getRefPrime().getMairiePrimeTableEnum() != MairiePrimeTableEnum.SPPRIM)
				continue;
			
			// Fetch or create Spphre
			Spprim pri = findOrCreateSpprimmRecord(ventilPrime.getIdAgent(), ventilPrime.getDateDebutMois(), ventilPrime.getRefPrime().getNoRubr());
			
			// Fill in the number of Primes for the month
			pri.setMontantPrime(ventilPrime.getQuantite());
			
			// Add the item to the list of hre modified/created
			if (pri.getMontantPrime() != 0)
				pris.add(pri);
			else
				pri.remove();
		}
		
		return pris;
	}
	
	protected Spprim findOrCreateSpprimmRecord(Integer idAgent, Date dateDebutMois, Integer norubr) {
		
		Spprim pri = null;
		
		Integer nomatr = helperService.getMairieMatrFromIdAgent(idAgent);
		Integer dateDebMoisMairie = helperService.getIntegerDateMairieFromDate(dateDebutMois);

		// Look for an exising record already existing in the DB
		pri = exportPaieRepository.getSpprimForDayAgentAndNorubr(idAgent, dateDebutMois, norubr);
		
		// At last create a new record
		if (pri == null) {
			pri = new Spprim();
			pri.setId(new SpprimId(nomatr, dateDebMoisMairie, norubr));
			Date dateFinMois = new LocalDate(dateDebutMois).plusMonths(1).withDayOfMonth(1).toDate();
			pri.setDateFin(helperService.getIntegerDateMairieFromDate(dateFinMois));
		}
		
		return pri;
	}

}
