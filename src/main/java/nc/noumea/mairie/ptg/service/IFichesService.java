package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentDto;

public interface IFichesService {

	List<AgentDto> listAgentsFichesToPrint(Integer idAgent, String codeService);
}
