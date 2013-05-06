package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentDto;

public interface IFichesService {

	List<AgentDto> listAgentsFichesToPrint(Integer idAgent, Date mondayDate,
			String codeService, Integer agent);

}
