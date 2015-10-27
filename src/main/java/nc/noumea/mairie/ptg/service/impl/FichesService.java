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
import nc.noumea.mairie.ws.SirhWSUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FichesService implements IFichesService {

	@Autowired
	private ISirhWSConsumer sirhWsConsumer;

	@Autowired
	private SirhWSUtils sirhWSUtils;

	@Autowired
	private IAccessRightsRepository accessRightsRepository;

	@Override
	public List<AgentDto> listAgentsFichesToPrint(Integer idAgent, Integer idServiceAds, Date date) {

		List<AgentDto> result = new ArrayList<AgentDto>();

		if (idServiceAds == null) {
			for (DroitsAgent da : accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent)) {
				AgentGeneriqueDto ag = sirhWsConsumer.getAgent(da.getIdAgent());
				AgentDto agDto = new AgentDto();
				agDto.setIdAgent(da.getIdAgent());
				agDto.setNom(ag.getDisplayNom());
				agDto.setPrenom(ag.getDisplayPrenom());

				if (!result.contains(agDto))
					result.add(agDto);
			}
		} else {

			// #18722 : pour chaque agent on va recuperer son
			// service
			List<Integer> listAgentDtoAppro = new ArrayList<Integer>();
			for (DroitsAgent da : accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent)) {
				if (!listAgentDtoAppro.contains(da.getIdAgent()))
					listAgentDtoAppro.add(da.getIdAgent());
			}
			List<AgentWithServiceDto> listAgentsApproServiceDto = sirhWsConsumer.getListAgentsWithService(listAgentDtoAppro, date);
			List<Integer> listAgentSansAffectation = new ArrayList<Integer>();

			for (DroitsAgent da : accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent)) {
				AgentWithServiceDto agDtoServ = sirhWSUtils.getAgentOfListAgentWithServiceDto(listAgentsApproServiceDto, da.getIdAgent());
				if (agDtoServ != null && agDtoServ.getIdServiceADS() != null && agDtoServ.getIdServiceADS().toString().equals(idServiceAds.toString())) {

					AgentGeneriqueDto ag = sirhWsConsumer.getAgent(da.getIdAgent());
					AgentDto agDto = new AgentDto();
					agDto.setIdAgent(da.getIdAgent());
					agDto.setNom(ag.getDisplayNom());
					agDto.setPrenom(ag.getDisplayPrenom());

					if (!result.contains(agDto))
						result.add(agDto);
				} else {
					if (!listAgentSansAffectation.contains(da.getIdAgent()))
					listAgentSansAffectation.add(da.getIdAgent());
				}
			}

			// #19250
			// pour chaque agent présent dans les droits, si il n'a pas de
			// service
			// alors on cherche sa derniere affectation
			if (listAgentSansAffectation.size() > 0) {
				List<AgentWithServiceDto> listAgentsSansAffectation = sirhWsConsumer.getListAgentsWithServiceOldAffectation(listAgentSansAffectation);
				for (AgentWithServiceDto t : listAgentsSansAffectation) {
					if (t != null && t.getIdServiceADS() != null && t.getIdServiceADS().toString().equals(idServiceAds.toString())) {
						AgentGeneriqueDto ag = sirhWsConsumer.getAgent(t.getIdAgent());
						AgentDto agDto = new AgentDto();
						agDto.setIdAgent(t.getIdAgent());
						agDto.setNom(ag.getDisplayNom());
						agDto.setPrenom(ag.getDisplayPrenom());

						if (!result.contains(agDto))
							result.add(agDto);
					}
				}
			}
		}

		return result;
	}

}
