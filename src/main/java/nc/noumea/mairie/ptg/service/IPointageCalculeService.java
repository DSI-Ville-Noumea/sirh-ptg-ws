package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.PointageCalcule;

public interface IPointageCalculeService {

	List<PointageCalcule> calculatePointagesForAgentAndWeek(Integer idAgent, AgentStatutEnum statut, Date dateLundi);
	
}
