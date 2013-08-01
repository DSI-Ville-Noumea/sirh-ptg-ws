package nc.noumea.mairie.ptg.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.domain.Spmatr;

import org.springframework.stereotype.Repository;

@Repository
public class MairieRepository implements IMairieRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager entityManager;
	
	@Override
	public <T> T getEntity(Class<T> Tclass, Object Id) {
		return entityManager.find(Tclass, Id);
	}
	
	@Override
	public void persistEntity(Object entity) {
		entityManager.persist(entity);
	}

	@Override
	public Spmatr findSpmatrForAgent(Integer idAgent) {
		return Spmatr.findSpmatr(idAgent);
	}
	
}
