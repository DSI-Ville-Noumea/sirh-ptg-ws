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
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.Siserv;
import nc.noumea.mairie.sirh.service.ISiservService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessRightsService implements IAccessRightsService {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;
	
	@Autowired
	private HelperService helperService;
	
	@Autowired
	private ISiservService siservService;
	
	@Autowired
	private IAccessRightsRepository accessRightsRepository;
	
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

		// Retrieve service of agent (through FichePoste) by affectation
		Siserv siserv = siservService.getAgentService(idAgent);
		
		// search through accessRightRepo to retrieve all agents and their rights
		List<DroitsAgent> droits = accessRightsRepository.getAllDroitsForService(siserv.getServi());
		
		// build the dto with it
		for (DroitsAgent d : droits) {
			
			if (d.isApprobateur() && d.getIdAgent() != idAgent)
				throw new AccessRightsServiceException("");
			
			Agent ag = sirhEntityManager.find(Agent.class, d.getIdAgent());
			AgentDto agDto = new AgentDto(ag);
			agDto.setCodeService(siserv.getSigle());
			agDto.setService(siserv.getLiServ().trim());
			
			if (d.isDelegataire())
				result.setDelegataire(agDto);
			
			if (d.isOperateur())
				result.getSaisisseurs().add(agDto);
		}
		
		return result;
	}
	
	@Override
	public List<DroitsAgent> setDelegatorAndOperators(Integer idAgent, DelegatorAndOperatorsDto dto) {
		
		// Retrieve service of agent (through FichePoste) by affectation
		Siserv siserv = siservService.getAgentService(idAgent);
		
		// get all DroitsAgent for service
		List<DroitsAgent> droits = accessRightsRepository.getAllDroitsForService(siserv.getServi());
		
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
	
			if (delegataire.getIdAgent() == null || delegataire.getIdAgent() != dto.getDelegataire().getIdAgent()) {
				delegataire.setDateModification(helperService.getCurrentDate());
			}
			
			delegataire.setIdAgent(dto.getDelegataire().getIdAgent());
			delegataire.setCodeService(siserv.getServi());
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
				operators.remove(operator);
			}
			else {
				operator = new DroitsAgent();
				operator.setOperateur(true);
			}
			
			if (operator.getIdAgent() == null || operator.getIdAgent() != newOperator.getIdAgent()) {
				operator.setDateModification(helperService.getCurrentDate());
			}
			
			operator.setIdAgent(newOperator.getIdAgent());
			operator.setCodeService(siserv.getServi());
			
			newDroits.add(operator);
		}
		
		// remove the remaining operators from persistence
		for (DroitsAgent da : operators.values())
			accessRightsRepository.removeDroitsAgent(da);
		
		return newDroits;
	}
}
