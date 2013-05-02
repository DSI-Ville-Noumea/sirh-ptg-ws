package nc.noumea.mairie.ptg.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.DroitsAgent;

import org.springframework.stereotype.Repository;

@Repository
public class AccessRightsRepository implements IAccessRightsRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;
	
	@Override
	public List<DroitsAgent> getAgentAccessRights(int idAgent) {
		
		TypedQuery<DroitsAgent> q = ptgEntityManager.createNamedQuery("getAgentAccessRights", DroitsAgent.class);
		q.setParameter("idAgent", idAgent);
		
		return q.getResultList();
	}

	@Override
	public List<DroitsAgent> getAllDroitsForService(String codeService) {
		
		TypedQuery<DroitsAgent> q = ptgEntityManager.createNamedQuery("getAllDroitsAgentForService", DroitsAgent.class);
		q.setParameter("codeService", codeService);
		
		return q.getResultList();
	}

	@Override
	public void removeDroitsAgent(DroitsAgent droitsAgent) {
		ptgEntityManager.remove(droitsAgent);
	}

	@Override
	public boolean isUserApprobator(Integer idAgent) {
		
		TypedQuery<Boolean> q = ptgEntityManager.createQuery(
				"select sum(da.approbateur) from DroitsAgent da where da.idAgent = :idAgent", Boolean.class);
		q.setParameter("idAgent", idAgent);
		
		Boolean result = q.getSingleResult();
		
		return (result != null && result);
	}
	
}
