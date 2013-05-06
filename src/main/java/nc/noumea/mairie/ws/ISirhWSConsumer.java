package nc.noumea.mairie.ws;

import java.util.List;

import nc.noumea.mairie.ptg.dto.ServiceDto;


public interface ISirhWSConsumer {

	ServiceDto getAgentDirection(Integer idAgent);

	List<Integer> getServicesAgent(String rootService);
	
	List<ServiceDto> getSousServices(String rootService);
}
