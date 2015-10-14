package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
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
	public List<AgentDto> listAgentsFichesToPrint(Integer idAgent, Integer idServiceAds, Date date) {

		List<AgentDto> result = new ArrayList<AgentDto>();

		for (DroitsAgent da : accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent)) {

			if (idServiceAds != null) {
				// #18722 : pour chaque agent on va recuperer son
				// service
				AgentWithServiceDto agDtoServ = sirhWsConsumer.getAgentService(da.getIdAgent(), date);
				if (agDtoServ != null && agDtoServ.getIdServiceADS() != null && agDtoServ.getIdServiceADS().toString().equals(idServiceAds.toString())) {

					AgentGeneriqueDto ag = sirhWsConsumer.getAgent(da.getIdAgent());
					AgentDto agDto = new AgentDto();
					agDto.setIdAgent(da.getIdAgent());
					agDto.setNom(ag.getDisplayNom());
					agDto.setPrenom(ag.getDisplayPrenom());

					if (!result.contains(agDto))
						result.add(agDto);
				}
			} else {
				AgentGeneriqueDto ag = sirhWsConsumer.getAgent(da.getIdAgent());
				AgentDto agDto = new AgentDto();
				agDto.setIdAgent(da.getIdAgent());
				agDto.setNom(ag.getDisplayNom());
				agDto.setPrenom(ag.getDisplayPrenom());

				if (!result.contains(agDto))
					result.add(agDto);
			}
		}

		return result;
	}

}
