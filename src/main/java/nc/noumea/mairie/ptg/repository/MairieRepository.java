package nc.noumea.mairie.ptg.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

@Repository
public class MairieRepository implements IMairieRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager entityManager;
	
	public <T> T getEntity(Class<T> Tclass, Object Id) {
		return entityManager.find(Tclass, Id);
	}
	
}
