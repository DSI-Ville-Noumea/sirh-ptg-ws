package nc.noumea.mairie.ptg.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;

import org.springframework.stereotype.Repository;

@Repository
public class AccessRightsRepository implements IAccessRightsRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;
	
	@Override
	public List<Droit> getAgentAccessRights(int idAgent) {
		
		TypedQuery<Droit> q = ptgEntityManager.createNamedQuery("getAgentAccessRights", Droit.class);
		q.setParameter("idAgent", idAgent);
		
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
	
	@Override
	public boolean isUserOperator(Integer idAgent) {
		
		TypedQuery<Boolean> q = ptgEntityManager.createQuery(
				"select sum(da.operateur) from DroitsAgent da where da.idAgent = :idAgent", Boolean.class);
		q.setParameter("idAgent", idAgent);
		
		Boolean result = q.getSingleResult();
		
		return (result != null && result);
	}

	@Override
	public List<Droit> getAgentsApprobateurs() {
		
		TypedQuery<Droit> q = ptgEntityManager.createNamedQuery("getAgentsApprobateurs", Droit.class);
		
		return q.getResultList();
	}
	
	@Override
	public Droit getAgentDroit(Integer idAgent) {
		
		TypedQuery<Droit> q = ptgEntityManager.createQuery(
				"from Droit d LEFT JOIN FETCH d.agents where d.idAgent = :idAgent", Droit.class);
		q.setParameter("idAgent", idAgent);
		
		List<Droit> r = q.getResultList();
		
		if (r.size() == 0)
			return null;
		
		return r.get(0);
	}
	
}
