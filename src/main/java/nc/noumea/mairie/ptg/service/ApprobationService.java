package nc.noumea.mairie.ptg.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.ConsultPointageDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprobationService implements IApprobationService {

	private Logger logger = LoggerFactory.getLogger(ApprobationService.class);

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private IAccessRightsRepository accessRightsRepository;

	@Autowired
	private IMairieRepository mairieRepository;
	
	@Override
	public List<ConsultPointageDto> getPointages(Integer idAgent, Date fromDate,
			Date toDate, String codeService, Integer agent, Integer idRefEtat, Integer idRefType) {

		List<ConsultPointageDto> result = new ArrayList<ConsultPointageDto>();
		
		// list of agents corresponding to filters
		List<Integer> agentIds = new ArrayList<Integer>();
		for (DroitsAgent da : accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent, codeService)) {
			agentIds.add(da.getIdAgent());
		}

		// if the filter for one agent is set, check whether this agent is in the list of the authorized ones
		if (agent != null) {
			// if not return empty result
			if (!agentIds.contains(agent))
				return result;
			// otherwise set it as the only agent in the filter
			else {
				agentIds.clear();
				agentIds.add(agent);
			}
		}
		
		// get pointages with filters
		List<Pointage> list = pointageRepository.getListPointages(agentIds, fromDate, toDate, idRefType);
		
		List<Integer> oldPointagesToAvoid = new ArrayList<Integer>();
		
		// iterate and create dtos
		for (Pointage ptg : list) {
			
			if (oldPointagesToAvoid.contains(ptg.getIdPointage())) {
				logger.debug("Not taking Pointage {} because not the latest.",
						ptg.getIdPointage());
				continue;
			}

			if (ptg.getPointageParent() != null) {
				logger.debug(
						"Pointage {} has a parent {}, adding it to avoid list.",
						ptg.getIdPointage(), ptg.getPointageParent()
								.getIdPointage());
				oldPointagesToAvoid.add(ptg.getPointageParent().getIdPointage());
			}
			
			if (idRefEtat != null && ptg.getLatestEtatPointage().getEtat().getCodeEtat() != idRefEtat) {
				logger.debug(
						"Not taking Pointage {} because its state is {} and filter is {}",
						ptg.getIdPointage(), ptg.getLatestEtatPointage()
								.getEtat().getCodeEtat(), idRefEtat);
				continue;
			}
			
			AgentDto agDto = new AgentDto(mairieRepository.getAgent(ptg.getIdAgent()));
			ConsultPointageDto dto = new ConsultPointageDto(ptg);
			dto.setAgent(agDto);
			result.add(dto);
		}
		
		return result;
	}

}
