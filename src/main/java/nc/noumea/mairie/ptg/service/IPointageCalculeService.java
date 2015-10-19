package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;

public interface IPointageCalculeService {

	List<PointageCalcule> calculatePointagesForAgentAndWeek(Integer idAgent, AgentStatutEnum statut, Date dateLundi, List<Pointage> agentPointages);

	void generatePointageTID_7720_7721_7722(Integer idAgentRH, Integer idAgent,
			AgentStatutEnum statut, Date dateLundi, List<Pointage> pointages);
	
}
