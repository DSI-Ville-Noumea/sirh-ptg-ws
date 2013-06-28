package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.VentilHsup;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public interface IVentilationHSupService {

	VentilHsup processHSupFonctionnaire(Integer idAgent, Spcarr carr, List<Pointage> pointages);
	
	VentilHsup processHSupContractuel(Integer idAgent, Spcarr carr, List<Pointage> pointages);
	
	VentilHsup processHSupConventionCollective(Integer idAgent, Spcarr carr, List<Pointage> pointages);
	
	Interval getDayHSupJourIntervalForStatut(DateTime day, AgentStatutEnum statut);
}
