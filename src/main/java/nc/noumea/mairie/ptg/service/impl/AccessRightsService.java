package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.ApprobateurDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.web.AccessForbiddenException;
import nc.noumea.mairie.sirh.comparator.ApprobateurDtoComparator;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.IAdsWSConsumer;
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
	private ISirhWSConsumer sirhWSConsumer;

	@Autowired
	private IAdsWSConsumer adsWsConsumer;

	@Autowired
	private IAgentMatriculeConverterService matriculeConvertor;

	@Override
	public AccessRightsDto getAgentAccessRights(Integer idAgent) {

		AccessRightsDto result = new AccessRightsDto();

		for (Droit da : accessRightsRepository.getAgentAccessRights(idAgent)) {

			result.setFiches(result.isFiches() || da.isApprobateur() || da.isOperateur());
			result.setSaisie(result.isSaisie() || da.isApprobateur() || da.isOperateur());
			result.setVisualisation(result.isVisualisation() || da.isApprobateur() || da.isOperateur());
			result.setApprobation(result.isApprobation() || da.isApprobateur());
			result.setGestionDroitsAcces(result.isGestionDroitsAcces()
					|| (da.getIdAgent().equals(idAgent) && da.isApprobateur()));
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
			AgentGeneriqueDto delegataire = sirhWSConsumer.getAgent(droit.getIdAgentDelegataire());

			if (delegataire == null)
				logger.warn("L'agent délégataire {} n'existe pas.", droit.getIdAgentDelegataire());
			else
				result.setDelegataire(new AgentDto(delegataire));
		}

		for (Droit operateur : droit.getOperateurs()) {
			AgentGeneriqueDto ope = sirhWSConsumer.getAgent(operateur.getIdAgent());
			if (ope == null)
				logger.warn("L'agent opérateur {} n'existe pas.", operateur.getIdAgent());
			else
				result.getSaisisseurs().add(new AgentDto(ope));
		}

		return result;
	}

	@Override
	public ReturnMessageDto setDelegatorAndOperators(Integer idAgent, DelegatorAndOperatorsDto dto) {

		ReturnMessageDto result = new ReturnMessageDto();

		Droit droitApprobateur = accessRightsRepository.getApprobateurFetchOperateurs(idAgent);

		List<Droit> originalOperateurs = new ArrayList<Droit>(droitApprobateur.getOperateurs());

		if (dto.getDelegataire() != null) {
			// Check that the new delegataire is not an operator
			// #14325
			// if
			// (accessRightsRepository.isUserOperator(dto.getDelegataire().getIdAgent()))
			// {
			// AgentGeneriqueDto ag =
			// sirhWSConsumer.getAgent(dto.getDelegataire().getIdAgent());
			// result.getErrors().add(
			// String.format(
			// "L'agent %s %s [%d] ne peut pas être délégataire car il ou elle est déjà opérateur.",
			// ag.getDisplayNom(), ag.getDisplayPrenom(),
			// matriculeConvertor.tryConvertIdAgentToNomatr(ag.getIdAgent())));
			// } else {
			droitApprobateur.setIdAgentDelegataire(dto.getDelegataire().getIdAgent());
			// }
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

			// Check that the new operateur is not already delegataire or
			// approbateur
			// #14325
			// if
			// (accessRightsRepository.isUserApprobatorOrDelegataire(operateurDto.getIdAgent()))
			// {
			// AgentGeneriqueDto ag =
			// sirhWSConsumer.getAgent(operateurDto.getIdAgent());
			// result.getErrors()
			// .add(String
			// .format("L'agent %s %s [%d] ne peut pas être opérateur car il ou elle est déjà approbateur ou délégataire.",
			// ag.getDisplayNom(), ag.getDisplayPrenom(),
			// matriculeConvertor.tryConvertIdAgentToNomatr(ag.getIdAgent())));
			// continue;
			// }

			existingOperateur = new Droit();
			existingOperateur.setDroitApprobateur(droitApprobateur);
			existingOperateur.setOperateur(true);
			existingOperateur.setIdAgent(operateurDto.getIdAgent());
			existingOperateur.setDateModification(helperService.getCurrentDate());
			droitApprobateur.getOperateurs().add(existingOperateur);
		}

		for (Droit droitOperateurToDelete : originalOperateurs) {
			droitApprobateur.getOperateurs().remove(droitOperateurToDelete);
			accessRightsRepository.removeEntity(droitOperateurToDelete);
		}

		return result;
	}

	@Override
	public boolean canUserAccessAccessRights(Integer idAgent) {
		return accessRightsRepository.isUserApprobator(idAgent);
	}

	@Override
	public boolean canUserAccessPrint(Integer idAgent) {
		return accessRightsRepository.isUserApprobatorOrOperatorOrDelegataire(idAgent);
	}

	@Override
	public boolean canUserAccessInput(Integer idAgent, Integer agentViewed) {
		// #11939 : attention si changement de RG ici, tenir compte de ce
		// redmine
		// boolean isOperator = accessRightsRepository.isUserOperator(idAgent);
		// List<AgentDto> agents
		return true;
	}

	@Override
	public boolean canUserAccessAppro(Integer idAgent) {
		return accessRightsRepository.isUserApprobatorOrDelegataire(idAgent);
	}

	@Override
	public boolean canUserAccessVisualisation(Integer idAgent) {
		return accessRightsRepository.isUserApprobatorOrOperatorOrDelegataire(idAgent);
	}

	@Override
	public List<ApprobateurDto> listAgentsApprobateurs(Integer idAgent, Integer idServiceADS) {
		List<ApprobateurDto> agentDtos = new ArrayList<ApprobateurDto>();
		List<Droit> listeDroit = new ArrayList<Droit>();
		if (idAgent != null) {
			Droit d = accessRightsRepository.getDroitApprobateurByAgent(idAgent);
			if (d != null) {
				listeDroit.add(d);
			}
		} else {
			listeDroit = accessRightsRepository.getAgentsApprobateurs();
		}
		List<Integer> listeSouService = new ArrayList<>();

		if (idServiceADS != null) {
			// on charge la liste des sous-services du service
			List<EntiteDto> liste = new ArrayList<EntiteDto>();
			EntiteDto entiteParent = adsWsConsumer.getEntiteWithChildrenByIdEntite(idServiceADS);
			liste.addAll(entiteParent.getEnfants());

			for (EntiteDto s : liste) {
				listeSouService.add(s.getIdEntite());
			}
			listeSouService.add(idServiceADS);
		}
		
		for (Droit da : listeDroit) {
			AgentWithServiceDto agentServiceDto = sirhWSConsumer.getAgentService(da.getIdAgent(),
					helperService.getCurrentDate());
			if (idServiceADS != null) {
				if (agentServiceDto != null && listeSouService.contains(agentServiceDto.getIdServiceADS())) {
					ApprobateurDto agentDto = new ApprobateurDto();
					agentDto.setApprobateur(agentServiceDto);
					DelegatorAndOperatorsDto deleg = getDelegator(da.getIdAgent());
					agentDto.setDelegataire(deleg != null ? deleg.getDelegataire() : null);
					agentDtos.add(agentDto);
				}
			} else {
				if (agentServiceDto != null) {
					ApprobateurDto agentDto = new ApprobateurDto();
					agentDto.setApprobateur(agentServiceDto);
					DelegatorAndOperatorsDto deleg = getDelegator(da.getIdAgent());
					agentDto.setDelegataire(deleg != null ? deleg.getDelegataire() : null);
					agentDtos.add(agentDto);
				} else {
					// c'est que l'agent n'a pas d'affectation en cours alors on
					// cherche l'agent sans son service
					AgentGeneriqueDto agGenerique = sirhWSConsumer.getAgent(da.getIdAgent());
					if (null == agGenerique) {
						logger.debug("Aucun agent actif trouvé dans SIRH {}" + da.getIdAgent());
					} else {
						AgentWithServiceDto ag = new AgentWithServiceDto(agGenerique);
						ApprobateurDto agentDto = new ApprobateurDto();
						agentDto.setApprobateur(ag);
						DelegatorAndOperatorsDto deleg = getDelegator(da.getIdAgent());
						agentDto.setDelegataire(deleg != null ? deleg.getDelegataire() : null);
						agentDtos.add(agentDto);
					}
				}
			}
		}
		Collections.sort(agentDtos, new ApprobateurDtoComparator());
		return agentDtos;
	}

	private DelegatorAndOperatorsDto getDelegator(Integer idAgent) {

		DelegatorAndOperatorsDto result = new DelegatorAndOperatorsDto();

		Droit droit = accessRightsRepository.getApprobateur(idAgent);

		if (droit == null) {
			logger.warn("L'agent {} n'est pas approbateur.", idAgent);
			return result;
		}

		if (droit.getIdAgentDelegataire() != null) {
			AgentGeneriqueDto delegataire = sirhWSConsumer.getAgent(droit.getIdAgentDelegataire());

			if (delegataire == null)
				logger.warn("L'agent délégataire {} n'existe pas.", droit.getIdAgentDelegataire());
			else
				result.setDelegataire(new AgentDto(delegataire));
		}
		return result;
	}

	@Override
	public ReturnMessageDto setApprobateur(AgentWithServiceDto dto) {
		ReturnMessageDto res = new ReturnMessageDto();

		// if (accessRightsRepository.isUserOperator(dto.getIdAgent())) {
		// res.getErrors().add("L'agent " + dto.getIdAgent() +
		// " est opérateur.");
		// return res;
		// }

		Droit d = accessRightsRepository.getDroitApprobateurByAgent(dto.getIdAgent());
		if (d == null) {
			d = new Droit();
			d.setApprobateur(true);
			d.setDateModification(helperService.getCurrentDate());
			d.setIdAgent(dto.getIdAgent());
			accessRightsRepository.persisEntity(d);
		}
		return res;

	}

	@Override
	public ReturnMessageDto deleteApprobateur(AgentWithServiceDto dto) {
		ReturnMessageDto res = new ReturnMessageDto();
		Droit d = accessRightsRepository.getDroitApprobateurByAgent(dto.getIdAgent());
		if (d != null) {
			// First, we remove all the agents this approbateur was approving
			// this will also delete all the agents its operateurs were filling
			// in for
			for (DroitsAgent agentSaisiToDelete : d.getAgents()) {
				agentSaisiToDelete.getDroits().clear();
				accessRightsRepository.removeEntity(agentSaisiToDelete);
			}
			// Then we delete the approbateur
			accessRightsRepository.removeEntity(d);
		} else {
			res.getErrors().add("Aucun droit trouvé pour l'agent " + dto.getIdAgent());
		}
		return res;
	}

	/**
	 * Retrieves the agent an approbator is set to Approve or an Operator is set
	 * to Input
	 */
	@Override
	public List<AgentDto> getAgentsToApproveOrInput(Integer idAgent) {
		return getAgentsToApproveOrInput(idAgent, null);
	}

	/**
	 * Retrieves the agent an approbator is set to Approve or an Operator is set
	 * to Input. This service also filters by service
	 */
	@Override
	public List<AgentDto> getAgentsToApproveOrInput(Integer idAgent, Integer idServiceAds) {

		List<AgentDto> result = new ArrayList<AgentDto>();

		for (DroitsAgent da : accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent, idServiceAds)) {
			// #15684 bug doublon
			if (isContainAgentInList(result, da)) {
				AgentDto agDto = new AgentDto();
				AgentGeneriqueDto ag = sirhWSConsumer.getAgent(da.getIdAgent());
				agDto.setIdAgent(da.getIdAgent());
				agDto.setNom(ag.getDisplayNom());
				agDto.setPrenom(ag.getDisplayPrenom());
				result.add(agDto);
			}
		}

		return result;
	}

	// #15684 bug doublon
	private boolean isContainAgentInList(List<AgentDto> listAgents, DroitsAgent ag) {

		if (null == ag) {
			return false;
		}

		if (null != listAgents) {
			for (AgentDto agent : listAgents) {
				if (null != agent && null != agent.getIdAgent() && null != ag.getIdAgent()
						&& agent.getIdAgent().equals(ag.getIdAgent())) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Retrieves the agent an approbator is set to Approve This service also
	 * filters by service
	 */
	// #14694 modifications sur le cumul des roles
	@Override
	public List<AgentDto> getAgentsToApprove(Integer idAgent, Integer idServiceAds) {

		List<AgentDto> result = new ArrayList<AgentDto>();

		for (DroitsAgent da : accessRightsRepository.getListOfAgentsToApprove(idAgent, idServiceAds)) {
			// #15684 bug doublon
			if (isContainAgentInList(result, da)) {
				AgentDto agDto = new AgentDto();
				AgentGeneriqueDto ag = sirhWSConsumer.getAgent(da.getIdAgent());

				if (null == ag)
					continue;

				agDto.setIdAgent(da.getIdAgent());
				agDto.setNom(ag.getDisplayNom());
				agDto.setPrenom(ag.getDisplayPrenom());
				result.add(agDto);
			}
		}

		return result;
	}

	/**
	 * Retrieves the agent an Operator is set to Input
	 */
	// #14325 modifications sur le cumul des roles
	@Override
	public List<AgentDto> getAgentsToInput(Integer idApprobateur, Integer idAgent) {

		List<AgentDto> result = new ArrayList<AgentDto>();

		for (DroitsAgent da : accessRightsRepository.getListOfAgentsToInput(idApprobateur, idAgent)) {
			// #15684 bug doublon
			if (isContainAgentInList(result, da)) {
				AgentDto agDto = new AgentDto();
				AgentGeneriqueDto ag = sirhWSConsumer.getAgent(da.getIdAgent());
				if (null == ag)
					continue;

				agDto.setIdAgent(da.getIdAgent());
				agDto.setNom(ag.getDisplayNom());
				agDto.setPrenom(ag.getDisplayPrenom());
				result.add(agDto);
			}
		}

		return result;
	}

	/**
	 * Sets the agents an approbator is set to Approve
	 */
	@Override
	public void setAgentsToApprove(Integer idAgent, List<AgentDto> agents) {

		Droit droitApprobateur = accessRightsRepository.getApprobateur(idAgent);

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
			if (dto == null) {
				logger.warn("L'agent {} n'existe pas.", ag.getIdAgent());
				continue;
			}
			
			DroitsAgent newDroitAgent = accessRightsRepository.getDroitsAgent(dto.getIdAgent());

			if (newDroitAgent == null) {
				newDroitAgent = new DroitsAgent();
				newDroitAgent.setIdAgent(dto.getIdAgent());
			}
			
			newDroitAgent.setIdAgent(ag.getIdAgent());
			newDroitAgent.getDroits().add(droitApprobateur);
			newDroitAgent.setIdServiceADS(dto.getIdServiceADS());
			newDroitAgent.setLibelleService(dto.getService());

			newDroitAgent.setDateModification(helperService.getCurrentDate());
			accessRightsRepository.persisEntity(newDroitAgent);
		}

		for (DroitsAgent agToDelete : agentsToDelete) {
			agToDelete.getDroits().clear();
			accessRightsRepository.removeEntity(agToDelete);
		}
	}

	@Override
	public void setAgentsToInput(Integer idAgentApprobateur, Integer idAgentOperateur, List<AgentDto> agents) {

		Droit droitApprobateur = accessRightsRepository.getApprobateur(idAgentApprobateur);
		Droit droitOperateur = accessRightsRepository.getAgentDroitApprobateurOrOperateurFetchAgents(idAgentOperateur,
				droitApprobateur.getIdDroit());

		if (!droitApprobateur.getOperateurs().contains(droitOperateur)) {
			logger.warn(
					"Impossible de modifier la liste des agents saisis de l'opérateur {} car il n'est pas un opérateur de l'agent {}.",
					idAgentApprobateur, idAgentOperateur);
			throw new AccessForbiddenException();
		}

		List<DroitsAgent> agentsToUnlink = new ArrayList<DroitsAgent>(droitOperateur.getAgents());

		for (AgentDto ag : agents) {

			for (DroitsAgent daInAppro : droitApprobateur.getAgents()) {

				// if this is not the agent we're currently looking for,
				// continue
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

	/**
	 * Returns the list of distinct services approved/input agents have Used to
	 * build the filters (by service)
	 */
	@Override
	public List<EntiteDto> getAgentsServicesToApproveOrInput(Integer idAgent) {

		List<EntiteDto> result = new ArrayList<EntiteDto>();

		List<Integer> idsServices = new ArrayList<Integer>();

		for (DroitsAgent da : accessRightsRepository.getListOfAgentsToInputOrApprove(idAgent)) {

			if (idsServices.contains(da.getIdServiceADS()))
				continue;

			idsServices.add(da.getIdServiceADS());
			EntiteDto entiteDto = new EntiteDto();
				entiteDto.setIdEntite(da.getIdServiceADS());
				entiteDto.setLabel(da.getLibelleService());
			result.add(entiteDto);
		}

		return result;
	}

	@Override
	public AgentGeneriqueDto findAgent(Integer idAgent) {
		return sirhWSConsumer.getAgent(idAgent);
	}

	@Override
	public ReturnMessageDto setDelegator(Integer idAgent, DelegatorAndOperatorsDto dto, ReturnMessageDto result) {

		Droit droitApprobateur = accessRightsRepository.getApprobateurFetchOperateurs(idAgent);

		if (dto.getDelegataire() != null) {
			// Check that the new delegataire is not an operator
			// #14325
			// if
			// (accessRightsRepository.isUserOperator(dto.getDelegataire().getIdAgent()))
			// {
			// AgentGeneriqueDto ag =
			// sirhWSConsumer.getAgent(dto.getDelegataire().getIdAgent());
			// result.getErrors().add(
			// String.format(
			// "L'agent %s %s [%d] ne peut pas être délégataire car il ou elle est déjà opérateur.",
			// ag.getDisplayNom(), ag.getDisplayPrenom(),
			// matriculeConvertor.tryConvertIdAgentToNomatr(ag.getIdAgent())));
			// } else {
			droitApprobateur.setIdAgentDelegataire(dto.getDelegataire().getIdAgent());
			// }
		} else {
			droitApprobateur.setIdAgentDelegataire(null);
		}
		accessRightsRepository.persisEntity(droitApprobateur);

		return result;
	}

	@Override
	public boolean isUserApprobateur(Integer idAgent) {
		boolean res = false;
		for (Droit da : accessRightsRepository.getAgentAccessRights(idAgent)) {
			if (da.isApprobateur()) {
				res = true;
				break;
			}

		}
		return res;
	}

	@Override
	public boolean isUserOperateur(Integer idAgent) {
		boolean res = false;
		for (Droit da : accessRightsRepository.getAgentAccessRights(idAgent)) {
			if (da.isOperateur()) {
				res = true;
				break;
			}
		}
		return res;
	}
}
