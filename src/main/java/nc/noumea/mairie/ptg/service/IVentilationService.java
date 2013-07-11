package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;

public interface IVentilationService {

	void processVentilation(Integer idAgent, List<Integer> agents, Date ventilationDate, AgentStatutEnum statut, RefTypePointageEnum pointageType);
}
