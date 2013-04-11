package nc.noumea.mairie.ptg.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ptg.domain.Pointage;

import org.springframework.stereotype.Repository;

@Repository
public class PointageRepository implements IPointageRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;
	
	@Override
	public List<Integer> getIdPointagesParents(Pointage pointage) {
		List<Integer> result = new ArrayList<Integer>();
		return result;
	}

}
