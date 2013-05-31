package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;

public interface IFichesService {

	List<AgentWithServiceDto> listAgentsFichesToPrint(Integer idAgent, Date mondayDate,
			String codeService, Integer agent);

}
