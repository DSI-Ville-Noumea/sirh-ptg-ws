package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.domain.Spphre;
import nc.noumea.mairie.ptg.domain.VentilHsup;

public interface IExportPaieHSupService {
	
	List<Spphre> exportHsupToPaie(List<VentilHsup> ventilHsupOrderedByDateAsc);
}
