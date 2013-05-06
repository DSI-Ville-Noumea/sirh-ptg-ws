package nc.noumea.mairie.ws;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;


public interface ISirhWSConsumer {

	ServiceDto getAgentDirection(Integer idAgent);

	List<AgentDto> getServicesAgent(String rootService, Date date);
	
	List<ServiceDto> getSousServices(String rootService);

	AgentDto getAgentService(Integer idAgent, Date date);
}
