package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPointagePK;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.ConsultPointageDto;
import nc.noumea.mairie.ptg.dto.PointagesEtatChangeDto;
import nc.noumea.mairie.ptg.dto.SaisieReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IApprobationService;

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

	@Autowired
	private HelperService helperService;

	@Override
	public List<ConsultPointageDto> getPointages(Integer idAgent, Date fromDate, Date toDate, String codeService, Integer agent, Integer idRefEtat,
			Integer idRefType) {

		List<ConsultPointageDto> result = new ArrayList<ConsultPointageDto>();

		// list of agents corresponding to filters
		List<Integer> agentIds = new ArrayList<Integer>();
		for (DroitsAgent da : accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent, codeService)) {
			agentIds.add(da.getIdAgent());
		}

		// if the filter for one agent is set, check whether this agent is in
		// the list of the authorized ones
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

		result = construitJson(list, idRefEtat);

		return result;
	}

	private List<ConsultPointageDto> construitJson(List<Pointage> list, Integer idRefEtat) {

		List<ConsultPointageDto> result = new ArrayList<ConsultPointageDto>();

		List<Integer> oldPointagesToAvoid = new ArrayList<Integer>();

		// iterate and create dtos
		for (Pointage ptg : list) {

			if (oldPointagesToAvoid.contains(ptg.getIdPointage())) {
				logger.debug("Not taking Pointage {} because not the latest.", ptg.getIdPointage());
				continue;
			}

			if (ptg.getPointageParent() != null) {
				logger.debug("Pointage {} has a parent {}, adding it to avoid list.", ptg.getIdPointage(), ptg.getPointageParent().getIdPointage());
				oldPointagesToAvoid.add(ptg.getPointageParent().getIdPointage());
			}

			if (idRefEtat != null && ptg.getLatestEtatPointage().getEtat().getCodeEtat() != idRefEtat) {
				logger.debug("Not taking Pointage {} because its state is {} and filter is {}", ptg.getIdPointage(), ptg.getLatestEtatPointage()
						.getEtat().getCodeEtat(), idRefEtat);
				continue;
			}

			AgentDto agDto = new AgentDto(mairieRepository.getAgent(ptg.getIdAgent()));
			ConsultPointageDto dto = new ConsultPointageDto(ptg);
			dto.updateEtat(ptg.getLatestEtatPointage());
			dto.setAgent(agDto);
			result.add(dto);
		}

		return result;
	}

	@Override
	public List<ConsultPointageDto> getPointagesArchives(Integer idAgent, Integer idPointage) {

		List<ConsultPointageDto> result = new ArrayList<ConsultPointageDto>();

		List<Pointage> list = pointageRepository.getPointageArchives(idPointage);

		for (Pointage ptg : list) {

			for (EtatPointage etat : ptg.getEtats()) {
				AgentDto agDto = new AgentDto(mairieRepository.getAgent(etat.getIdAgent()));
				ConsultPointageDto dto = new ConsultPointageDto(ptg);
				dto.updateEtat(etat);
				dto.setAgent(agDto);
				result.add(dto);
			}

		}

		return result;
	}

	@Override
	public SaisieReturnMessageDto setPointagesEtat(Integer idAgent, List<PointagesEtatChangeDto> etatsDto) {

		SaisieReturnMessageDto result = new SaisieReturnMessageDto();

		List<DroitsAgent> droitsAgents = accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent);
		List<Integer> droitsAgentsIds = new ArrayList<Integer>();

		for (DroitsAgent da : droitsAgents)
			droitsAgentsIds.add(da.getIdAgent());

		for (PointagesEtatChangeDto dto : etatsDto) {

			Pointage ptg = pointageRepository.getEntity(Pointage.class, dto.getIdPointage());

			// check whether the Pointage exists
			if (ptg == null) {
				result.getErrors().add(String.format("Le pointage %s n'existe pas.", dto.getIdPointage()));
				continue;
			}

			// Check whether the user has sufficient rights to update this
			// Pointage
			if (!droitsAgentsIds.contains(ptg.getIdAgent())) {
				result.getErrors().add(
						String.format("L'agent %s n'a pas le droit de mettre à jour le pointage %s de l'agent %s.", idAgent, ptg.getIdPointage(),
								ptg.getIdAgent()));
				continue;
			}

			// Check whether the current target and if it can be updated
			EtatPointage currentEtat = ptg.getLatestEtatPointage();
			if (currentEtat.getEtat() != EtatPointageEnum.SAISI && currentEtat.getEtat() != EtatPointageEnum.APPROUVE
					&& currentEtat.getEtat() != EtatPointageEnum.REFUSE) {
				result.getErrors().add(
						String.format("Impossible de mettre à jour le pointage %s de l'agent %s car celui-ci est à l'état %s.", ptg.getIdPointage(),
								ptg.getIdAgent(), currentEtat.getEtat().name()));
				continue;
			}

			// Check whether the target EtatPointage is authorized
			EtatPointageEnum targetEtat = EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat());

			if (targetEtat != EtatPointageEnum.APPROUVE && targetEtat != EtatPointageEnum.REFUSE && targetEtat != EtatPointageEnum.SAISI) {
				result.getErrors()
						.add(String
								.format("Impossible de mettre à jour le pointage %s de l'agent %s à l'état %s. Seuls APPROUVE, REFUSE ou SAISI sont acceptés.",
										ptg.getIdPointage(), ptg.getIdAgent(), targetEtat.name()));
				continue;
			}

			// at last, create and add the new EtatPointage
			EtatPointage etat = new EtatPointage();
			EtatPointagePK etatPk = new EtatPointagePK();
			etatPk.setDateEtat(helperService.getCurrentDate());
			etatPk.setPointage(ptg);
			etat.setEtatPointagePk(etatPk);
			etat.setIdAgent(idAgent);
			etat.setEtat(targetEtat);

			ptg.getEtats().add(etat);
		}

		return result;
	}

	@Override
	public List<ConsultPointageDto> getPointagesSIRH(Date from, Date to, List<Integer> idAgents, Integer idRefEtat, Integer idRefType) {

		List<ConsultPointageDto> result = new ArrayList<ConsultPointageDto>();

		// get pointages with filters
		List<Pointage> list = pointageRepository.getListPointagesSIRH(from, to, idRefType, idAgents);

		result = construitJson(list, idRefEtat);

		return result;
	}

}
