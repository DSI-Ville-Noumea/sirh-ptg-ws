package nc.noumea.mairie.ws;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;


public interface ISirhWSConsumer {

	ServiceDto getAgentDirection(Integer idAgent);

	List<AgentWithServiceDto> getServicesAgent(String rootService, Date date);
	
	List<ServiceDto> getSousServices(String rootService);

	AgentWithServiceDto getAgentService(Integer idAgent, Date date);
}
