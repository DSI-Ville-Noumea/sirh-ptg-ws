package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;

public interface IAccessRightsService {

	AccessRightsDto getAgentAccessRights(Integer idAgent);

//	DelegatorAndOperatorsDto getDelegatorAndOperators(Integer idAgent);
//
//	List<DroitsAgent> setDelegatorAndOperators(Integer idAgent, DelegatorAndOperatorsDto dto);

	boolean canUserAccessAccessRights(Integer idAgent);

	boolean canUserAccessPrint(Integer idAgent);

	boolean canUserAccessSaisie(Integer idAgent, Integer agentViewed);

	List<AgentWithServiceDto> listAgentsApprobateurs();

	void setApprobateurs(AgentWithServiceDto dto);
	
	List<AgentDto> getAgentsToApprove(Integer idAgent);
	
	void setAgentsToApprove(Integer idAgent, List<AgentDto> agents);
}
