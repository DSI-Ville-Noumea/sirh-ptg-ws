package nc.noumea.mairie.ws;

import java.util.List;


public interface ISirhWSConsumer {

	ServiceDto getAgentDivision(Integer idAgent);

	List<Integer> getServicesAgent(String rootService);
}
