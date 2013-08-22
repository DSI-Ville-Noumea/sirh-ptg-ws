package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.domain.Spphre;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.sirh.domain.CptRecup;

public interface IExportPaieHSupService {
	
	List<Spphre> exportHsupToPaie(List<VentilHsup> ventilHsupOrderedByDateAsc);
	List<CptRecup> exportRecupToSirh(List<VentilHsup> ventilHsupOrderedByDateAsc);
}
