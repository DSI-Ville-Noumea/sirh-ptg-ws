package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.ptg.domain.Pointage;

public interface IExportPaieAbsenceService {

	/**
	 * Given a list of Pointages of type Absence ordered by date ASC
	 * This method returns a list of Sppact objects either fetched from MAIRIE db
	 * or created because not yet existing.
	 * NB: it is mandatory that the list of given pointages is sorted by Date ASC
	 * @param pointagesOrderedByDateAsc
	 * @return
	 */
	List<Sppact> exportAbsencesToPaie(List<Pointage> pointagesOrderedByDateAsc);
}
