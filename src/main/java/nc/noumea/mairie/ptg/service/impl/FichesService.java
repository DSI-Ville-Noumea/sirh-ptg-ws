package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.service.IFichesService;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FichesService implements IFichesService {

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	private IAccessRightsRepository accessRightsRepository;

	@Override
	public List<AgentDto> listAgentsFichesToPrint(Integer idAgent, String codeService) {

		List<AgentDto> result = new ArrayList<AgentDto>();

		for (DroitsAgent da : accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent, codeService)) {
			AgentGeneriqueDto ag = sirhWsConsumer.getAgent(da.getIdAgent());
			AgentDto agDto = new AgentDto();
			agDto.setIdAgent(da.getIdAgent());
			agDto.setNom(ag.getDisplayNom());
			agDto.setPrenom(ag.getDisplayPrenom());
			result.add(agDto);
		}

		return result;
	}

}
