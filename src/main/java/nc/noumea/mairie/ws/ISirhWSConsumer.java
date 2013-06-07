package nc.noumea.mairie.ws;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.SirhWsServiceDto;


public interface ISirhWSConsumer {

	SirhWsServiceDto getAgentDirection(Integer idAgent);

	List<AgentWithServiceDto> getServicesAgent(String rootService, Date date);
	
	List<SirhWsServiceDto> getSousServices(String rootService);

	AgentWithServiceDto getAgentService(Integer idAgent, Date date);
}
