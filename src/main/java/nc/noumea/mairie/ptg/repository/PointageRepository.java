package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;

import org.springframework.stereotype.Repository;

@Repository
public class PointageRepository implements IPointageRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getIdPointagesParents(Pointage pointage) {

		Query q = ptgEntityManager
				.createNativeQuery("SELECT t1.ID_POINTAGE FROM PTG_POINTAGE t1 START WITH t1.ID_POINTAGE = :idPointage CONNECT BY PRIOR t1.ID_POINTAGE_PARENT = t1.ID_POINTAGE");
		q.setParameter("idPointage", pointage.getIdPointage());

		List<Integer> result = q.getResultList();

		return result;
	}

	@Override
	public List<RefPrime> getRefPrimes(List<Integer> noRubrList, AgentStatutEnum statut) {

		TypedQuery<RefPrime> query = ptgEntityManager.createNamedQuery("getRefPrimesNotCalculated", RefPrime.class);
		query.setParameter("noRubrList", noRubrList.size() == 0 ? null : noRubrList);
		query.setParameter("statut", statut);

		return query.getResultList();
	}

	@Override
	public List<Pointage> getPointagesForAgentAndDateOrderByIdDesc(int idAgent, Date dateLundi) {

		TypedQuery<Pointage> query = ptgEntityManager.createNamedQuery("getPointageForAgentAndDateLundiByIdDesc", Pointage.class);
		query.setParameter("idAgent", idAgent);
		query.setParameter("dateLundi", dateLundi);

		return query.getResultList();
	}

	@Override
	public void savePointage(Pointage ptg) {
		if (ptg.getIdPointage() == null || ptg.getIdPointage().equals(0))
			ptgEntityManager.persist(ptg);
	}

	@Override
	public <T> T getEntity(Class<T> Tclass, Object Id) {
		return ptgEntityManager.getReference(Tclass, Id);
	}
}
