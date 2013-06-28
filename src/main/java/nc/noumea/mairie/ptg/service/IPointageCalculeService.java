package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.PointageCalcule;

import org.joda.time.DateTime;

public interface IPointageCalculeService {

	List<PointageCalcule> calculatePointagesForAgentAndWeek(Integer idAgent, AgentStatutEnum statut, DateTime dateLundi);
	
}
