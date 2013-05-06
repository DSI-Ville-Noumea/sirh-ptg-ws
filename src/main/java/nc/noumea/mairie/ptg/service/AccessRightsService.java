package nc.noumea.mairie.ptg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessRightsService implements IAccessRightsService {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;
	
	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;
	
	@Autowired
	private HelperService helperService;
	
	@Autowired
	private IAccessRightsRepository accessRightsRepository;
	
	@Autowired
	private ISirhWSConsumer sirhWSConsumer;
	
	@Override
	public AccessRightsDto getAgentAccessRights(Integer idAgent) {

		AccessRightsDto result = new AccessRightsDto();
		
		for (DroitsAgent da : accessRightsRepository.getAgentAccessRights(idAgent)) {

			result.setFiches(result.isFiches() || da.isApprobateur() || da.isOperateur());
			result.setSaisie(result.isSaisie() || da.isApprobateur() || da.isDelegataire() || da.isOperateur());
			result.setVisualisation(result.isVisualisation() || da.isApprobateur() || da.isDelegataire() || da.isOperateur());
			result.setApprobation(result.isApprobation() || da.isApprobateur() || da.isDelegataire());
			result.setGestionDroitsAcces(result.isGestionDroitsAcces() || da.isApprobateur());
		}
		
		return result;
	}
	
	@Override
	public DelegatorAndOperatorsDto getDelegatorAndOperators(Integer idAgent) {
		
		DelegatorAndOperatorsDto result = new DelegatorAndOperatorsDto();

		// Retrieve division service of agent
		ServiceDto service = sirhWSConsumer.getAgentDirection(idAgent);
		
		// search through accessRightRepo to retrieve all agents and their rights
		List<DroitsAgent> droits = accessRightsRepository.getAllDroitsForService(service.getService());
		
		// build the dto with it
		for (DroitsAgent d : droits) {
			
			if (d.isApprobateur() && !d.getIdAgent().equals(idAgent))
				throw new AccessRightsServiceException("");
			
			Agent ag = sirhEntityManager.find(Agent.class, d.getIdAgent());
			AgentDto agDto = new AgentDto(ag);
			agDto.setCodeService(service.getService());
			agDto.setService(service.getServiceLibelle());
			
			if (d.isDelegataire())
				result.setDelegataire(agDto);
			
			if (d.isOperateur())
				result.getSaisisseurs().add(agDto);
		}
		
		return result;
	}
	
	@Override
	public List<DroitsAgent> setDelegatorAndOperators(Integer idAgent, DelegatorAndOperatorsDto dto) {
		
		// Retrieve division service of agent
		ServiceDto service = sirhWSConsumer.getAgentDirection(idAgent);
		
		// get all DroitsAgent for service
		List<DroitsAgent> droits = accessRightsRepository.getAllDroitsForService(service.getService());
		
		// initializinf list of new access rights
		List<DroitsAgent> newDroits = new ArrayList<DroitsAgent>();
		
		
		// changing delegataire
		DroitsAgent delegataire = null;
		
		for (DroitsAgent d : droits) {
			if (d.isDelegataire()){
				delegataire = d;
				break;
			}
		}

		if (dto.getDelegataire() == null) {
			if (delegataire != null)
				accessRightsRepository.removeDroitsAgent(delegataire);
		}
		else {
			if (delegataire == null) {
				delegataire = new DroitsAgent();
				delegataire.setDelegataire(true);
			}
	
			if (delegataire.getIdAgent() == null || !delegataire.getIdAgent().equals(dto.getDelegataire().getIdAgent())) {
				delegataire.setDateModification(helperService.getCurrentDate());
			}
			
			delegataire.setIdAgent(dto.getDelegataire().getIdAgent());
			delegataire.setCodeService(service.getService());
			ptgEntityManager.persist(delegataire);
			newDroits.add(delegataire);
		}
		
		
		// changing operators
		Map<Integer, DroitsAgent> operators = new HashMap<Integer, DroitsAgent>();
		
		for (DroitsAgent d : droits) {
			if (d.isOperateur())
				operators.put(d.getIdAgent(), d);
		}
		
		for (AgentDto newOperator : dto.getSaisisseurs()) {
			
			DroitsAgent operator = null;
			if (operators.containsKey(newOperator.getIdAgent())) {
				operator = operators.get(newOperator.getIdAgent());
				operators.remove(operator.getIdAgent());
			}
			else {
				operator = new DroitsAgent();
				operator.setOperateur(true);
			}
			
			if (operator.getIdAgent() == null || !operator.getIdAgent().equals(newOperator.getIdAgent())) {
				operator.setDateModification(helperService.getCurrentDate());
			}
			
			operator.setIdAgent(newOperator.getIdAgent());
			operator.setCodeService(service.getService());
			ptgEntityManager.persist(operator);
			newDroits.add(operator);
		}
		
		// remove the remaining operators from persistence
		for (DroitsAgent da : operators.values())
			accessRightsRepository.removeDroitsAgent(da);
		
		return newDroits;
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
	public List<AgentDto> listAgentsToAssign(Integer idAgent) {
		
		// Retrieve division service of agent
		ServiceDto service = sirhWSConsumer.getAgentDirection(idAgent);
		
		List<AgentDto> agentDtos = sirhWSConsumer.getServicesAgent(service.getService(), null);
		
		return agentDtos;
	}
	
}
