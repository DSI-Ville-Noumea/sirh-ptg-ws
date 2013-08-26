package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.ptg.domain.Pointage;

public interface IExportAbsencePaieService {

	List<Sppact> exportAbsencesToPaie(List<Pointage> pointagesOrderedByDateAsc);
}