package nc.noumea.mairie.ptg.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.web.AccessForbiddenException;
import nc.noumea.mairie.sirh.comparator.AgentWithServiceDtoComparator;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessRightsService implements IAccessRightsService {

	private Logger logger = LoggerFactory.getLogger(AccessRightsService.class);

	@Autowired
	private HelperService helperService;

	@Autowired
	private IAccessRightsRepository accessRightsRepository;

	@Autowired
	private IMairieRepository mairieRepository;

	@Autowired
	private ISirhWSConsumer sirhWSConsumer;

	@Override
	public AccessRightsDto getAgentAccessRights(Integer idAgent) {

		AccessRightsDto result = new AccessRightsDto();

		for (Droit da : accessRightsRepository.getAgentAccessRights(idAgent)) {

			result.setFiches(result.isFiches() || da.isApprobateur() || da.isOperateur());
			result.setSaisie(result.isSaisie() || da.isApprobateur() || da.isOperateur());
			result.setVisualisation(result.isVisualisation() || da.isApprobateur() || da.isOperateur());
			result.setApprobation(result.isApprobation() || da.isApprobateur());
			result.setGestionDroitsAcces(result.isGestionDroitsAcces() || (da.getIdAgent().equals(idAgent) && da.isApprobateur()));
		}

		return result;
	}

	@Override
	public DelegatorAndOperatorsDto getDelegatorAndOperators(Integer idAgent) {

		DelegatorAndOperatorsDto result = new DelegatorAndOperatorsDto();

		Droit droit = accessRightsRepository.getApprobateurFetchOperateurs(idAgent);

		if (droit == null) {
			logger.warn("L'agent {} n'est pas approbateur.", idAgent);
			return result;
		}

		if (droit.getIdAgentDelegataire() != null) {
			Agent delegataire = mairieRepository.getAgent(droit.getIdAgentDelegataire());

			if (delegataire == null)
				logger.warn("L'agent délégataire {} n'existe pas.", droit.getIdAgentDelegataire());
			else
				result.setDelegataire(new AgentDto(delegataire));
		}

		for (Droit operateur : droit.getOperateurs()) {
			Agent ope = mairieRepository.getAgent(operateur.getIdAgent());
			if (ope == null)
				logger.warn("L'agent opérateur {} n'existe pas.", operateur.getIdAgent());
			else
				result.getSaisisseurs().add(new AgentDto(ope));
		}

		return result;
	}

	@Override
	public void setDelegatorAndOperators(Integer idAgent, DelegatorAndOperatorsDto dto) {

		Droit droitApprobateur = accessRightsRepository.getApprobateurFetchOperateurs(idAgent);

		List<Droit> originalOperateurs = new ArrayList<Droit>(droitApprobateur.getOperateurs());

		if (dto.getDelegataire() != null) {
			droitApprobateur.setIdAgentDelegataire(dto.getDelegataire().getIdAgent());
		} else {
			droitApprobateur.setIdAgentDelegataire(null);
		}

		for (AgentDto operateurDto : dto.getSaisisseurs()) {

			Droit existingOperateur = null;

			for (Droit operateur : droitApprobateur.getOperateurs()) {
				if (operateur.getIdAgent().equals(operateurDto.getIdAgent())) {
					existingOperateur = operateur;
					originalOperateurs.remove(existingOperateur);
					break;
				}
			}

			if (existingOperateur != null)
				continue;

			existingOperateur = new Droit();
			existingOperateur.setDroitApprobateur(droitApprobateur);
			existingOperateur.setOperateur(true);
			existingOperateur.setIdAgent(operateurDto.getIdAgent());
			existingOperateur.setDateModification(helperService.getCurrentDate());
			droitApprobateur.getOperateurs().add(existingOperateur);
		}

		for (Droit droitOperateurToDelete : originalOperateurs) {
			droitApprobateur.getOperateurs().remove(droitOperateurToDelete);
			droitOperateurToDelete.remove();
		}
	}

	@Override
	public boolean canUserAccessAccessRights(Integer idAgent) {
		return accessRightsRepository.isUserApprobator(idAgent);
	}

	@Override
	public boolean canUserAccessPrint(Integer idAgent) {
		return accessRightsRepository.isUserOperator(idAgent);
	}

	@Override
	public boolean canUserAccessSaisie(Integer idAgent, Integer agentViewed) {
		// boolean isOperator = accessRightsRepository.isUserOperator(idAgent);
		// List<AgentDto> agents
		return true;
	}

	@Override
	public List<AgentWithServiceDto> listAgentsApprobateurs() {
		List<AgentWithServiceDto> agentDtos = new ArrayList<AgentWithServiceDto>();
		for (Droit da : accessRightsRepository.getAgentsApprobateurs()) {
			// Retrieve division service of agent
			AgentWithServiceDto agentDto = sirhWSConsumer.getAgentService(da.getIdAgent(), helperService.getCurrentDate());
			agentDtos.add(agentDto);
		}
		Collections.sort(agentDtos, new AgentWithServiceDtoComparator());
		return agentDtos;
	}

	@Override
	public List<AgentWithServiceDto> setApprobateurs(List<AgentWithServiceDto> listeDto) {
		List<Droit> listeAgentAppro = accessRightsRepository.getAgentsApprobateurs();
		List<Droit> listeAgentOperateur = accessRightsRepository.getAgentsOperateurs();

		List<Droit> droitsToDelete = new ArrayList<Droit>(listeAgentAppro);

		List<AgentWithServiceDto> listeAgentErreur = new ArrayList<AgentWithServiceDto>();
		List<AgentWithServiceDto> listeSansOperateur = new ArrayList<AgentWithServiceDto>();
		// on ne peut ajouter un approbateur si il est operateur
		for (AgentWithServiceDto agentDto : listeDto) {
			boolean exist = false;
			for (Droit existingDroitOperateur : listeAgentOperateur) {
				if (existingDroitOperateur.getIdAgent().equals(agentDto.getIdAgent())) {
					listeAgentErreur.add(agentDto);
					exist = true;
					break;
				}
			}
			if (!exist)
				listeSansOperateur.add(agentDto);
		}

		for (AgentWithServiceDto agentDto : listeSansOperateur) {

			Droit d = null;

			for (Droit existingDroit : listeAgentAppro) {
				if (existingDroit.getIdAgent().equals(agentDto.getIdAgent())) {
					d = existingDroit;
					break;
				}
			}

			if (d != null) {
				droitsToDelete.remove(d);
				continue;
			}

			d = new Droit();
			d.setApprobateur(true);
			d.setDateModification(helperService.getCurrentDate());
			d.setIdAgent(agentDto.getIdAgent());
			accessRightsRepository.persisEntity(d);
		}

		for (Droit droitToDelete : droitsToDelete) {
			// First, we remove all the agents this approbateur was approving
			// this will also delete all the agents its operateurs were filling
			// in for
			for (DroitsAgent agentSaisiToDelete : droitToDelete.getAgents()) {
				agentSaisiToDelete.getDroits().clear();
				agentSaisiToDelete.remove();
			}
			// Then we delete the approbateur
			droitToDelete.remove();
		}
		return listeAgentErreur;

		// idem que
		// for (Droit d : listeAgentAppro) {
		//
		// AgentWithServiceDto existingItem = null;
		//
		// for (AgentWithServiceDto agentDto : listeDto) {
		// if (agentDto.getIdAgent().equals(d.getIdAgent())) {
		// existingItem = agentDto;
		// break;
		// }
		// }
		//
		// if (existingItem == null)
		// d.remove();
		// }
		//

	}

	/**
	 * Retrieves the agent an approbator is set to Approve
	 * or an Operator is set to Input
	 */
	@Override
	public List<AgentDto> getAgentsToApproveOrInput(Integer idAgent) {

		List<AgentDto> result = new ArrayList<AgentDto>();

		Droit droit = accessRightsRepository.getAgentDroitApprobateurOrOperateurFetchAgents(idAgent);

		if (droit == null)
			return result;

		for (DroitsAgent da : droit.getAgents()) {
			AgentDto agDto = new AgentDto();
			Agent ag = mairieRepository.getAgent(da.getIdAgent());
			agDto.setIdAgent(da.getIdAgent());
			agDto.setNom(ag.getDisplayNom());
			agDto.setPrenom(ag.getDisplayPrenom());
			result.add(agDto);
		}

		return result;
	}

	/**
	 * Sets the agents an approbator is set to Approve
	 */
	@Override
	public void setAgentsToApprove(Integer idAgent, List<AgentDto> agents) {

		Droit droitApprobateur = accessRightsRepository.getAgentDroitApprobateurOrOperateurFetchAgents(idAgent);

		List<DroitsAgent> agentsToDelete = new ArrayList<DroitsAgent>(droitApprobateur.getAgents());

		for (AgentDto ag : agents) {

			DroitsAgent existingAgent = null;

			for (DroitsAgent da : droitApprobateur.getAgents()) {
				if (da.getIdAgent().equals(ag.getIdAgent())) {
					existingAgent = da;
					agentsToDelete.remove(existingAgent);
					break;
				}
			}

			if (existingAgent != null)
				continue;

			AgentWithServiceDto dto = sirhWSConsumer.getAgentService(ag.getIdAgent(), helperService.getCurrentDate());
			if (dto == null)
				continue;

			existingAgent = new DroitsAgent();
			existingAgent.setIdAgent(ag.getIdAgent());
			existingAgent.getDroits().add(droitApprobateur);
			existingAgent.setCodeService(dto.getCodeService());
			existingAgent.setLibelleService(dto.getService());

			existingAgent.setDateModification(helperService.getCurrentDate());
			accessRightsRepository.persisEntity(existingAgent);
		}

		for (DroitsAgent agToDelete : agentsToDelete) {
			agToDelete.getDroits().clear();
			agToDelete.remove();
		}
	}

	@Override
	public void setAgentsToInput(Integer idAgentApprobateur, Integer idAgentOperateur, List<AgentDto> agents) {

		Droit droitApprobateur = accessRightsRepository.getAgentDroitApprobateurOrOperateurFetchAgents(idAgentApprobateur);
		Droit droitOperateur = accessRightsRepository.getAgentDroitApprobateurOrOperateurFetchAgents(idAgentOperateur);

		if (!droitApprobateur.getOperateurs().contains(droitOperateur)) {
			logger.warn("Impossible de modifier la liste des agents saisis de l'opérateur {} car il n'est pas un opérateur de l'agent {}.", idAgentApprobateur, idAgentOperateur);
			throw new AccessForbiddenException();
		}
		
		List<DroitsAgent> agentsToUnlink = new ArrayList<DroitsAgent>(droitOperateur.getAgents());

		for (AgentDto ag : agents) {

			for (DroitsAgent daInAppro : droitApprobateur.getAgents()) {
				
				// if this is not the agent we're currently looking for, continue
				if (!daInAppro.getIdAgent().equals(ag.getIdAgent()))
					continue;
				
				// once found, if this agent is not in the operator list, add it
				if (!droitOperateur.getAgents().contains(daInAppro)) {
					daInAppro.getDroits().add(droitOperateur);
				}

				// remove this agent from the list of agents to be unlinked
				agentsToUnlink.remove(daInAppro);
				
				// we're done with the list for now
				break;
			}
		}

		for (DroitsAgent agToUnlink : agentsToUnlink) {
			agToUnlink.getDroits().remove(droitOperateur);
		}
	}

}
