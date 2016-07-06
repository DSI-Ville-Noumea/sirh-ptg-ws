package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public interface IVentilationHSupService {

	VentilHsup processHSup(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages, AgentStatutEnum statut, boolean has1150Prime, VentilDate ventilDate,
			List<Pointage> pointagesJournalisesRejetes);
	
	VentilHsup processHSup(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages, AgentStatutEnum statut, VentilDate ventilDate);
	
	VentilHsup processHSupFonctionnaire(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages, VentilDate ventilDate);
	
	VentilHsup processHSupContractuel(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages, VentilDate ventilDate);
	
	VentilHsup processHSupConventionCollective(Integer idAgent, Spcarr carr, Date dateLundi, List<Pointage> pointages, boolean has1150Prime, VentilDate ventilDate);
	
	Interval getDayHSupJourIntervalForStatut(DateTime day, AgentStatutEnum statut);

	VentilHsup processHeuresSupEpandageForSIPRES(VentilHsup ventilHsup,
			Integer idAgent, Date dateLundi, List<Pointage> pointages,
			AgentStatutEnum statut);

	VentilHsup processHSupFromPointageCalcule(Integer idAgent, Date dateLundi,
			List<PointageCalcule> pointagesCalcules, VentilHsup ventilHSup);
}
