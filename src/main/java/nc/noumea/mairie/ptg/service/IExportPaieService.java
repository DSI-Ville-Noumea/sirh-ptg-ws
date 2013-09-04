package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IExportPaieService {

	ReturnMessageDto startExportToPaie(Integer agentIdValidating, AgentStatutEnum statut);
	@Deprecated
	ReturnMessageDto exportToPaie(Integer agentIdValidating, AgentStatutEnum statut);
	CanStartWorkflowPaieActionDto canStartExportPaieActionDto(TypeChainePaieEnum chainePaie);
	void processExportPaieForAgent(Integer idExportPaieTask);
}
