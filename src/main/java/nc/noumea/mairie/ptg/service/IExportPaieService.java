package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IExportPaieService {

	ReturnMessageDto exportToPaie(Integer agentIdValidating, AgentStatutEnum statut);
	
}
