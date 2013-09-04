package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.domain.Sppprm;
import nc.noumea.mairie.domain.Spprim;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.VentilPrime;

public interface IExportPaiePrimeService {

	/**
	 * Given a list of Pointages of type Prime ordered by date ASC
	 * This method returns a list of Sppprm objects either fetched from MAIRIE db
	 * or created because not yet existing.
	 * NB: it is mandatory that the list of given pointages is sorted by Date ASC
	 * @param pointagesOrderedByDateAsc
	 * @return
	 */
	List<Sppprm> exportPrimesJourToPaie(List<Pointage> pointagesOrderedByDateAsc);

	/**
	 * Given a list of ventilPrimes (aggregated by Month) and ordered by date ASC
	 * This method returns a list of Spprim objects either fetched from MAIRIE db
	 * or created because not yet existing.
	 * NB: it is mandatory that the list of given item is sorted by Date ASC
	 * @param ventilPrimeOrderedByDateAsc
	 * @return
	 */
	List<Spprim> exportPrimesMoisToPaie(List<VentilPrime> ventilPrimeOrderedByDateAsc);
}
