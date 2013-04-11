package nc.noumea.mairie.ptg.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import nc.noumea.mairie.ptg.domain.Pointage;

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

}
