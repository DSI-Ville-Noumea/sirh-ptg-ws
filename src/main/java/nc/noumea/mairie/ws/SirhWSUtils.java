package nc.noumea.mairie.ws;

import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SirhWSUtils {

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

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
	
	public boolean isAgentDPM(Integer idAgent) {
		// prend la date du jour par defaut dans SirhWs
		EntiteDto service = sirhWsConsumer.getAgentDirection(idAgent, null);
		if (null != service && service.getSigle().toUpperCase().equals("DPM")) {
			return true;
		}
		return false;
	}
	
}
