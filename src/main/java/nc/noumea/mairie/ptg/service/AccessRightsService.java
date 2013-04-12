package nc.noumea.mairie.ptg.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;

import org.springframework.stereotype.Service;

@Service
public class AccessRightsService implements IAccessRightsService {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;
	
	@Override
	public AccessRightsDto getAgentAccessRights(Integer idAgent) {

		AccessRightsDto result = new AccessRightsDto();
		
		TypedQuery<DroitsAgent> q = ptgEntityManager.createNamedQuery("getAgentAccessRights", DroitsAgent.class);
		q.setParameter("idAgent", idAgent);
		
		for (DroitsAgent da : q.getResultList()) {
			result.setFiches(result.isFiches() || da.getProfil().isIsEdition());
			result.setSaisie(result.isSaisie() || da.getProfil().isIsSaisie());
			result.setVisualisation(result.isVisualisation() || da.getProfil().isIsVisualisation());
			result.setApprobation(result.isApprobation() || da.getProfil().isIsApprobation());
			result.setGestionDroitsAcces(result.isGestionDroitsAcces() || da.getProfil().isIsGrantor());
		}
		
		return result;
	}

}
