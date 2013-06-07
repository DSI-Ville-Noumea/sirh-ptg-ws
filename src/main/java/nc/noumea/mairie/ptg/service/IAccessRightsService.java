package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;

public interface IAccessRightsService {

	AccessRightsDto getAgentAccessRights(Integer idAgent);

	DelegatorAndOperatorsDto getDelegatorAndOperators(Integer idAgent);

	void setDelegatorAndOperators(Integer idAgent, DelegatorAndOperatorsDto dto);

	List<AgentWithServiceDto> listAgentsApprobateurs();

	List<AgentWithServiceDto> setApprobateurs(List<AgentWithServiceDto> dto);

	List<AgentDto> getAgentsToApproveOrInput(Integer idAgent);

	List<ServiceDto> getAgentsServicesToApproveOrInput(Integer idAgent);
	
	void setAgentsToApprove(Integer idAgent, List<AgentDto> agents);

	void setAgentsToInput(Integer idAgentApprobateur, Integer idAgentOperateur, List<AgentDto> agents);	

	boolean canUserAccessAccessRights(Integer idAgent);

	boolean canUserAccessPrint(Integer idAgent);

	boolean canUserAccessInput(Integer idAgent, Integer agentViewed);

}
