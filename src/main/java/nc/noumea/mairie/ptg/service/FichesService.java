package nc.noumea.mairie.ptg.service;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.sirh.domain.Agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FichesService implements IFichesService {
	
	@Autowired
	private IMairieRepository mairieRepository;

	@Autowired
	private IAccessRightsRepository accessRightsRepository;
	
	@Override
	public List<AgentDto> listAgentsFichesToPrint(Integer idAgent, String codeService) {
		
		List<AgentDto> result = new ArrayList<AgentDto>();
		
		Droit droit = accessRightsRepository.getAgentDroitApprobateurOrOperateurFetchAgents(idAgent);
		
		for (DroitsAgent da : droit.getAgents()) {
			
			if (codeService != null && !codeService.equals(da.getCodeService()))
				continue;
			
			Agent ag = mairieRepository.getAgent(da.getIdAgent());
			AgentDto agDto = new AgentDto();
			agDto.setIdAgent(da.getIdAgent());
			agDto.setNom(ag.getDisplayNom());
			agDto.setPrenom(ag.getDisplayPrenom());
			result.add(agDto);
		}
		
		return result;
	}

}
