package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.ConsultPointageDto;
import nc.noumea.mairie.ptg.dto.PointagesEtatChangeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IApprobationService;
import nc.noumea.mairie.ptg.service.IPointageService;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprobationService implements IApprobationService {

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private IAccessRightsRepository accessRightsRepository;

	@Autowired
	private ISirhRepository sirhRepository;

	@Autowired
	private HelperService helperService;

	@Autowired
	private IPointageService pointageService;

	@Autowired
	private IVentilationRepository ventilRepository;

	@Override
	public List<ConsultPointageDto> getPointages(Integer idAgent, Date fromDate, Date toDate, String codeService,
			Integer agent, Integer idRefEtat, Integer idRefType) {

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

		return createConsultPointageDtoListFromSearch(agentIds, fromDate, toDate, idRefEtat, idRefType);
	}

	@Override
	public List<ConsultPointageDto> getPointagesSIRH(Date fromDate, Date toDate, List<Integer> agentIds,
			Integer idRefEtat, Integer idRefType) {
		return createConsultPointageDtoListFromSearch(agentIds, fromDate, toDate, idRefEtat, idRefType);
	}

	protected List<ConsultPointageDto> createConsultPointageDtoListFromSearch(List<Integer> agentIds, Date fromDate,
			Date toDate, Integer idRefEtat, Integer idRefType) {

		List<ConsultPointageDto> result = new ArrayList<ConsultPointageDto>();

		// get pointages with filters
		// We convert the 'toDate' date into the next day at 00H00 because the
		// query will take it as a timestamp.
		List<Pointage> pointages = pointageService.getLatestPointagesForAgentsAndDates(agentIds, fromDate,
				new LocalDate(toDate).plusDays(1).toDate(), RefTypePointageEnum.getRefTypePointageEnum(idRefType),
				idRefEtat == null ? null : Arrays.asList(EtatPointageEnum.getEtatPointageEnum(idRefEtat)));

		for (Pointage ptg : pointages) {
			AgentDto agDto = new AgentDto(sirhRepository.getAgent(ptg.getIdAgent()));
			ConsultPointageDto dto = new ConsultPointageDto(ptg, helperService);
			dto.updateEtat(ptg.getLatestEtatPointage());
			dto.setAgent(agDto);
			result.add(dto);
		}

		return result;
	}

	@Override
	public List<ConsultPointageDto> getPointagesArchives(Integer idAgent, Integer idPointage) {
		return getPointagesArchives(idPointage);
	}

	@Override
	public List<ConsultPointageDto> getPointagesArchives(Integer idPointage) {

		List<ConsultPointageDto> result = new ArrayList<ConsultPointageDto>();
		List<Pointage> list = pointageRepository.getPointageArchives(idPointage);

		for (Pointage ptg : list) {
			for (EtatPointage etat : ptg.getEtats()) {
				AgentDto agDto = new AgentDto(sirhRepository.getAgent(etat.getIdAgent()));
				ConsultPointageDto dto = new ConsultPointageDto(ptg, helperService);
				dto.updateEtat(etat);
				dto.setAgent(agDto);
				result.add(dto);
			}
		}
		return result;
	}

	@Override
	public ReturnMessageDto setPointagesEtat(Integer idAgent, List<PointagesEtatChangeDto> etatsDto) {

		ReturnMessageDto result = new ReturnMessageDto();

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
						String.format("L'agent %s n'a pas le droit de mettre à jour le pointage %s de l'agent %s.",
								idAgent, ptg.getIdPointage(), ptg.getIdAgent()));
				continue;
			}

			// Check whether the current target and if it can be updated
			EtatPointage currentEtat = ptg.getLatestEtatPointage();
			if (currentEtat.getEtat() != EtatPointageEnum.SAISI && currentEtat.getEtat() != EtatPointageEnum.APPROUVE
					&& currentEtat.getEtat() != EtatPointageEnum.REFUSE) {
				result.getErrors()
						.add(String
								.format("Impossible de mettre à jour le pointage %s de l'agent %s car celui-ci est à l'état %s.",
										ptg.getIdPointage(), ptg.getIdAgent(), currentEtat.getEtat().name()));
				continue;
			}

			// Check whether the target EtatPointage is authorized
			EtatPointageEnum targetEtat = EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat());

			if (targetEtat != EtatPointageEnum.APPROUVE && targetEtat != EtatPointageEnum.REFUSE
					&& targetEtat != EtatPointageEnum.SAISI) {
				result.getErrors()
						.add(String
								.format("Impossible de mettre à jour le pointage %s de l'agent %s à l'état %s. Seuls APPROUVE, REFUSE ou SAISI sont acceptés.",
										ptg.getIdPointage(), ptg.getIdAgent(), targetEtat.name()));
				continue;
			}

			// at last, create and add the new EtatPointage
			EtatPointage etat = new EtatPointage();
			etat.setDateEtat(helperService.getCurrentDate());
			etat.setDateMaj(helperService.getCurrentDate());
			etat.setPointage(ptg);
			etat.setIdAgent(ptg.getIdAgent());
			etat.setEtat(targetEtat);
			ptg.getEtats().add(etat);
		}

		return result;
	}

	@Override
	public ReturnMessageDto setPointagesEtatSIRH(Integer idAgent, List<PointagesEtatChangeDto> etatsDto,
			AgentStatutEnum statut) {

		ReturnMessageDto result = new ReturnMessageDto();
		for (PointagesEtatChangeDto dto : etatsDto) {
			Pointage ptg = pointageRepository.getEntity(Pointage.class, dto.getIdPointage());

			// check whether the Pointage exists
			if (ptg == null) {
				result.getErrors().add(String.format("Le pointage %s n'existe pas.", dto.getIdPointage()));
				continue;
			}

			// Check whether the current target and the target EtatPointage are
			// authorized
			EtatPointageEnum currentEtat = ptg.getLatestEtatPointage().getEtat();
			EtatPointageEnum targetEtat = EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat());
			boolean ok = true;

			if (currentEtat != EtatPointageEnum.SAISI && currentEtat != EtatPointageEnum.EN_ATTENTE
					&& currentEtat != EtatPointageEnum.REJETE && currentEtat != EtatPointageEnum.APPROUVE
					&& targetEtat == EtatPointageEnum.APPROUVE) {
				ok = false;
			}

			if (currentEtat != EtatPointageEnum.APPROUVE && currentEtat != EtatPointageEnum.REJETE
					&& currentEtat != EtatPointageEnum.VENTILE && targetEtat == EtatPointageEnum.EN_ATTENTE) {
				ok = false;
			}
			if (currentEtat != EtatPointageEnum.APPROUVE && currentEtat != EtatPointageEnum.EN_ATTENTE
					&& currentEtat != EtatPointageEnum.VENTILE && targetEtat == EtatPointageEnum.REJETE) {
				ok = false;
			}
			if (!ok) {
				result.getErrors().add(
						String.format(
								"Impossible de mettre à %s le pointage %s de l'agent %s car celui-ci est à l'état %s.",
								targetEtat.name(), ptg.getIdPointage(), ptg.getIdAgent(), currentEtat.name()));
				continue;
			}

			if (targetEtat != EtatPointageEnum.APPROUVE && targetEtat != EtatPointageEnum.REJETE
					&& targetEtat != EtatPointageEnum.EN_ATTENTE) {
				result.getErrors()
						.add(String
								.format("Impossible de mettre à jour le pointage %s de l'agent %s à l'état %s. Seuls APPROUVE, REJETE ou EN_ATTENTE sont acceptés depuis SIRH.",
										ptg.getIdPointage(), ptg.getIdAgent(), targetEtat.name()));
				continue;
			}

			// at last, create and add the new EtatPointage
			EtatPointage etat = new EtatPointage();
			VentilDate lastVentil = ventilRepository.getLatestVentilDate(
					helperService.getTypeChainePaieFromStatut(statut), false);
			if (targetEtat == EtatPointageEnum.APPROUVE && lastVentil != null)
				etat.setDateEtat(lastVentil.getDateVentilation());
			else
				etat.setDateEtat(helperService.getCurrentDate());

			etat.setDateMaj(helperService.getCurrentDate());
			etat.setPointage(ptg);
			etat.setIdAgent(ptg.getIdAgent());
			etat.setEtat(EtatPointageEnum.getEtatPointageEnum(dto.getIdRefEtat()));
			ptg.getEtats().add(etat);
		}
		return result;
	}

	@Override
	public List<AgentDto> listerTousAgentsPointages() {

		List<AgentDto> result = new ArrayList<AgentDto>();
		List<Integer> list = pointageRepository.listAllDistinctIdAgentPointage();

		for (Integer idAgent : list) {
			AgentDto agDto = new AgentDto();
			agDto.setIdAgent(idAgent);
			result.add(agDto);
		}
		return result;
	}

}
