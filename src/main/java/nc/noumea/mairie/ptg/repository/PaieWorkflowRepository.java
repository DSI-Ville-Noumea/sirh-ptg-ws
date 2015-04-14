package nc.noumea.mairie.ptg.repository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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

		Query query = entityManager.createNativeQuery(
				"select * from SpWFPaie p where p.cdChaine = :chainePaie with ur", SpWFPaie.class);
		query.setParameter("chainePaie", chainePaie.toString());

		return (SpWFPaie) query.getResultList().get(0);
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
	public String getLockModeSpWFPaie(Object o) {
		LockModeType lock = entityManager.getLockMode(o);
		return lock.toString();
	}
}
