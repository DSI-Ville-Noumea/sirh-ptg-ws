package nc.noumea.mairie.ptg.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FichesService implements IFichesService {
	
	@Autowired
	private ISirhWSConsumer sirhWSConsumer;
	
	@Override
	public List<AgentWithServiceDto> listAgentsFichesToPrint(Integer idAgent, Date mondayDate, String codeService, Integer agent) {
		
		List<AgentWithServiceDto> result = null;
		
		if (agent != null && agent != 0) {
			AgentWithServiceDto r = sirhWSConsumer.getAgentService(agent, mondayDate);
			return Arrays.asList(r);
		}
		
		if (codeService == null) {
			ServiceDto serviceDto = sirhWSConsumer.getAgentDirection(idAgent);
			codeService = serviceDto.getService();
		}
		
		result = sirhWSConsumer.getServicesAgent(codeService, mondayDate);
		
		return result;
	}

}
