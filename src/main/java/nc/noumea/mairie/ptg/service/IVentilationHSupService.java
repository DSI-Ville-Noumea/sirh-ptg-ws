package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.VentilHsup;

public interface IVentilationHSupService {

	VentilHsup processHSupFonctionnaire(Integer idAgent, Spcarr carr, List<Pointage> pointages);
	
	VentilHsup processHSupContractuel(Integer idAgent, Spcarr carr, List<Pointage> pointages);
	
	VentilHsup processHSupConventionCollective(Integer idAgent, Spcarr carr, List<Pointage> pointages);
}