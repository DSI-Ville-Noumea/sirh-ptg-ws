package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;

public interface IAccessRightsService {

	AccessRightsDto getAgentAccessRights(Integer idAgent);

	DelegatorAndOperatorsDto getDelegatorAndOperators(Integer idAgent);

	void setDelegatorAndOperators(Integer idAgent, DelegatorAndOperatorsDto dto);

	List<AgentWithServiceDto> listAgentsApprobateurs();

	void setApprobateurs(List<AgentWithServiceDto> dto);

	List<AgentDto> getAgentsToApproveOrInput(Integer idAgent);

	void setAgentsToApprove(Integer idAgent, List<AgentDto> agents);

	void setAgentsToInput(Integer idAgentApprobateur, Integer idAgentOperateur, List<AgentDto> agents);	

	boolean canUserAccessAccessRights(Integer idAgent);

	boolean canUserAccessPrint(Integer idAgent);

	boolean canUserAccessSaisie(Integer idAgent, Integer agentViewed);

}
