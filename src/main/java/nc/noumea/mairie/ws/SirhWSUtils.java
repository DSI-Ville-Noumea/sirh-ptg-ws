package nc.noumea.mairie.ws;

import java.util.List;

import org.springframework.stereotype.Service;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;

@Service
public class SirhWSUtils {

	public AgentGeneriqueDto getAgentOfListAgentGeneriqueDto(List<AgentGeneriqueDto> listAgents, Integer idAgent) {
		
		if(null != listAgents
				&& null != idAgent) {
			for(AgentGeneriqueDto agent : listAgents) {
				if(agent.getIdAgent().equals(idAgent)){
					return agent;
				}
			}
		}
		return null;
	}

	public AgentWithServiceDto getAgentOfListAgentWithServiceDto(List<AgentWithServiceDto> listAgents, Integer idAgent) {

		if (null != listAgents && null != idAgent) {
			for (AgentWithServiceDto agent : listAgents) {
				if (agent.getIdAgent().equals(idAgent)) {
					return agent;
				}
			}
		}
		return null;
	}
	
}
