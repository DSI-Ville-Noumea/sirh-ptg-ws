package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.domain.AgentStatutEnum;

public interface IExportPaieService {

	void exportToPaie(Integer agentIdValidating, AgentStatutEnum statut);
	
}
