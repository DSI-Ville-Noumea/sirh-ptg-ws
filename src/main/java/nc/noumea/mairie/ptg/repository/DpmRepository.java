package nc.noumea.mairie.ptg.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.DpmIndemAnnee;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

@Repository
public class DpmRepository implements IDpmRepository {

	private Logger logger = LoggerFactory.getLogger(DpmRepository.class);
	
	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;
	
	@Override
	public void persisEntity(Object obj) {
		ptgEntityManager.persist(obj);
	}

	@Override
	public <T> T getEntity(Class<T> Tclass, Object Id) {
		return ptgEntityManager.find(Tclass, Id);
	}

	@Override
	public List<DpmIndemChoixAgent> getListDpmIndemChoixAgent(List<Integer> listIdsAgent, Integer annee) {
		
		logger.debug("getListDpmIndemChoixAgent with parameter listIdsAgent = {} and annee = {}", listIdsAgent, annee);
		
		TypedQuery<DpmIndemChoixAgent> query = ptgEntityManager.createNamedQuery("getListDpmIndemChoixAgentByListIdsAgentAndAnnee", DpmIndemChoixAgent.class);
		
		query.setParameter("annee", annee);
		query.setParameter("listIdsAgent", listIdsAgent);
		
		return query.getResultList();
	}

	@Override
	public DpmIndemAnnee getDpmIndemAnneeCourant() {
		
		logger.debug("getDpmIndemAnneeCourant without parameter");
		
		TypedQuery<DpmIndemAnnee> query = ptgEntityManager.createNamedQuery("getDpmIndemAnneeByAnnee", DpmIndemAnnee.class);

		query.setParameter("annee", new DateTime().getYear());
		
		try {
			return query.getSingleResult();
		} catch(NoResultException | NonUniqueResultException e) {
			logger.error(e.getMessage());
		}
		
		return null;
	}

	@Override
	public DpmIndemAnnee getDpmIndemAnneeByAnnee(Integer annee) {
		
		logger.debug("getDpmIndemAnneeByAnnee with parameter annee = {}", annee);
		
		TypedQuery<DpmIndemAnnee> query = ptgEntityManager.createNamedQuery("getDpmIndemAnneeByAnnee", DpmIndemAnnee.class);

		query.setParameter("annee", annee);
		
		try {
			return query.getSingleResult();
		} catch(NoResultException | NonUniqueResultException | EmptyResultDataAccessException e) {
			logger.error(e.getMessage());
		}
		
		return null;
	}

	@Override
	public List<DpmIndemAnnee> getListDpmIndemAnnee() {
		
		logger.debug("getListDpmIndemAnnee without parameter");
		
		TypedQuery<DpmIndemAnnee> query = ptgEntityManager.createNamedQuery("getListDpmIndemAnneeOrderByAnneeDesc", DpmIndemAnnee.class);
		
		return query.getResultList();
	}

	@Override
	public DpmIndemChoixAgent getDpmIndemChoixAgentByAgentAndAnnee(Integer idAgent, Integer annee) {
		
		logger.debug("getDpmIndemChoixAgentByAgentAndAnnee with parameter idAgent = {} and annee = {}", idAgent, annee);
		
		TypedQuery<DpmIndemChoixAgent> query = ptgEntityManager.createNamedQuery("getDpmIndemChoixAgentByAgentAndAnnee", DpmIndemChoixAgent.class);
		
		query.setParameter("annee", annee);
		query.setParameter("idAgent", idAgent);
		
		try {
			return query.getSingleResult();
		} catch(NoResultException | NonUniqueResultException | EmptyResultDataAccessException e) {
			logger.error(e.getMessage());
		}
		
		return null;
	}

}
