package nc.noumea.mairie.ptg.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndInputtersDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.FichePoste;
import nc.noumea.mairie.sirh.domain.Siserv;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessRightsService implements IAccessRightsService {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;
	
	@Autowired
	private HelperService helperService;
	
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
	public DelegatorAndInputtersDto getDelegatorAndInputters(Integer idAgent) {
		
		DelegatorAndInputtersDto result = new DelegatorAndInputtersDto();

		// Retrieve service of agent (through FichePoste) by affectation
		TypedQuery<FichePoste> q = sirhEntityManager.createNamedQuery("getCurrentAffectation", FichePoste.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("today", helperService.getCurrentDate());
		
		List<FichePoste> fichePostes = q.getResultList();
		
		if (fichePostes.size() != 1)
			throw new AccessRightsServiceException(String.format("L'agent donné a 0 ou plus d'une affectation courante [%s].", idAgent));
		
		String codeService = fichePostes.get(0).getCodeService();
		
		Siserv siserv = sirhEntityManager.find(Siserv.class, codeService);
		
		if (siserv == null)
			throw new AccessRightsServiceException(String.format("Impossible de trouver le service de l'agent [%s].", codeService));
		
		// search through accessRightRepo to retrieve all agents and their rights
		List<DroitsAgent> droits = accessRightsRepository.getAllDroitsForService(codeService);
		
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
	public void setDelegatorAndInputters(Integer idAgent, DelegatorAndInputtersDto dto) {
		
		//TODO: save delegator and inputters
		
	}
	
}
