package nc.noumea.mairie.ptg.repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.SpWFEtat;
import nc.noumea.mairie.domain.SpWFPaie;
import nc.noumea.mairie.domain.SpWfEtatEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;

import org.springframework.stereotype.Repository;

@Repository
public class PaieWorkflowRepository implements IPaieWorkflowRepository {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager entityManager;

	@Override
	public SpWFPaie readCurrentState(TypeChainePaieEnum chainePaie) {

		TypedQuery<SpWFPaie> query = entityManager.createQuery(
				"select p from SpWFPaie p where p.codeChaine = :chainePaie", SpWFPaie.class);
		query.setParameter("chainePaie", chainePaie);

		return query.getSingleResult();
	}

	@Override
	public SpWFPaie selectForUpdateState(TypeChainePaieEnum chainePaie) {

		SpWFPaie currentState = entityManager.find(SpWFPaie.class, chainePaie, LockModeType.PESSIMISTIC_WRITE);

		return currentState;
	}

	@Override
	public SpWFEtat getEtat(SpWfEtatEnum codeEtat) {
		return entityManager.find(SpWFEtat.class, codeEtat);
	}

	@Override
	public String getLockModeSpWFPaie() {
		LockModeType lock = entityManager.getLockMode(SpWFPaie.class);
		return lock.toString();
	}
}
