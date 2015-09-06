package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.ApprobateurDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;

public interface IAccessRightsService {

	AccessRightsDto getAgentAccessRights(Integer idAgent);

	DelegatorAndOperatorsDto getDelegatorAndOperators(Integer idAgent);

	ReturnMessageDto setDelegatorAndOperators(Integer idAgent, DelegatorAndOperatorsDto dto);

	List<ApprobateurDto> listAgentsApprobateurs(Integer idAgent, Integer idServiceADS);

	ReturnMessageDto setApprobateur(AgentWithServiceDto dto);

	ReturnMessageDto deleteApprobateur(AgentWithServiceDto dto);

	List<AgentDto> getAgentsToApproveOrInput(Integer idAgent);

	List<AgentDto> getAgentsToApproveOrInput(Integer idAgent, Integer idServiceAds);

	List<EntiteDto> getAgentsServicesToApproveOrInput(Integer idAgent);

	void setAgentsToApprove(Integer idAgent, List<AgentDto> agents);

	void setAgentsToInput(Integer idAgentApprobateur, Integer idAgentOperateur, List<AgentDto> agents);

	boolean canUserAccessAccessRights(Integer idAgent);

	boolean canUserAccessPrint(Integer idAgent);

	boolean canUserAccessInput(Integer idAgent, Integer agentViewed);

	boolean canUserAccessVisualisation(Integer idAgent);

	boolean canUserAccessAppro(Integer idAgent);

	AgentGeneriqueDto findAgent(Integer idAgent);

	ReturnMessageDto setDelegator(Integer idAgentAppro, DelegatorAndOperatorsDto json, ReturnMessageDto result);

	List<AgentDto> getAgentsToInput(Integer idApprobateur, Integer idAgent);

	List<AgentDto> getAgentsToApprove(Integer idAgent, Integer idServiceAds);

	boolean isUserApprobateur(Integer idAgent);

	boolean isUserOperateur(Integer idAgent);

}
