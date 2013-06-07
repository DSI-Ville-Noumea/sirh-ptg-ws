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
				"select sum(d.approbateur) from Droit d where d.idAgent = :idAgent", Boolean.class);
		q.setParameter("idAgent", idAgent);
		
		Boolean result = q.getSingleResult();
		
		return (result != null && result);
	}
	
	@Override
	public boolean isUserApprobatorOrDelegataire(Integer idAgent) {
		
		TypedQuery<Boolean> q = ptgEntityManager.createQuery(
				"select sum(d.approbateur) from Droit d where d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent", Boolean.class);
		q.setParameter("idAgent", idAgent);
		
		Boolean result = q.getSingleResult();
		
		return (result != null && result);
	}
	
	@Override
	public boolean isUserOperator(Integer idAgent) {
		
		TypedQuery<Boolean> q = ptgEntityManager.createQuery(
				"select sum(d.operateur) from Droit d where d.idAgent = :idAgent", Boolean.class);
		q.setParameter("idAgent", idAgent);
		
		Boolean result = q.getSingleResult();
		
		return (result != null && result);
	}
	
	@Override
	public boolean isUserApprobatorOrOperatorOrDelegataire(Integer idAgent) {
		
		TypedQuery<Boolean> q = ptgEntityManager.createQuery(
				"select sum(d.operateur) + sum(d.approbateur) from Droit d where d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent", Boolean.class);
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
	public List<Droit> getAgentsOperateurs() {
		
		TypedQuery<Droit> q = ptgEntityManager.createNamedQuery("getAgentsOperateurs", Droit.class);
		
		return q.getResultList();
	}
	
	@Override
	public Droit getAgentDroitApprobateurOrOperateurFetchAgents(Integer idAgent) {
		
		TypedQuery<Droit> q = ptgEntityManager.createQuery(
				"from Droit d LEFT JOIN FETCH d.agents where d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent", Droit.class);
		q.setParameter("idAgent", idAgent);
		
		List<Droit> r = q.getResultList();
		
		if (r.size() == 0)
			return null;
		
		return r.get(0);
	}

	@Override
	public void persisEntity(Object obj) {
		ptgEntityManager.persist(obj);
	}

	@Override
	public Droit getApprobateurFetchOperateurs(Integer idAgentApprobateur) {
		
		TypedQuery<Droit> q = ptgEntityManager.createQuery(
				"from Droit d LEFT JOIN FETCH d.operateurs where d.idAgent = :idAgent and d.approbateur = true", Droit.class);
		q.setParameter("idAgent", idAgentApprobateur);
		
		List<Droit> r = q.getResultList();
		
		if (r.size() == 0)
			return null;
		
		return r.get(0);
	}
	
}
