package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.DpmIndemAnnee;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;

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
	public List<DpmIndemChoixAgent> getListDpmIndemChoixAgent(List<Integer> listIdsAgent, Integer annee, Boolean isChoixIndemnite, Boolean isChoixRecuperation) {
		
		logger.debug("getListDpmIndemChoixAgent with parameter listIdsAgent = {} and annee = {} and isChoixIndemnite = {} and isChoixRecuperation = {}", 
				listIdsAgent, annee, isChoixIndemnite, isChoixRecuperation);
		
		StringBuffer request = new StringBuffer();
		request.append("select d from DpmIndemChoixAgent d where 1=1 ");
		
		if(null != annee)
			request.append(" and d.dpmIndemAnnee.annee = :annee ");
		
		if(null != listIdsAgent)
			request.append(" and d.idAgent in :listIdsAgent ");
		
		if(null != isChoixIndemnite)
			request.append(" and d.isChoixIndemnite is :isChoixIndemnite");
		
		if(null != isChoixRecuperation)
			request.append(" and d.isChoixRecuperation is :isChoixRecuperation");
		
		
		TypedQuery<DpmIndemChoixAgent> query = ptgEntityManager.createQuery(request.toString(), DpmIndemChoixAgent.class);
		
		if(null != annee) 
			query.setParameter("annee", annee);
		
		if(null != listIdsAgent) 
			query.setParameter("listIdsAgent", listIdsAgent);

		if(null != isChoixIndemnite) 
			query.setParameter("isChoixIndemnite", isChoixIndemnite);
		
		if(null != isChoixRecuperation) 
			query.setParameter("isChoixRecuperation", isChoixRecuperation);
		
		return query.getResultList();
	}

	@Override
	public List<DpmIndemAnnee> getListDpmIndemAnneeOuverte() {
		
		logger.debug("getListDpmIndemAnneeOuverte without parameter");
		
		TypedQuery<DpmIndemAnnee> query = ptgEntityManager.createNamedQuery("getListDpmIndemAnneeOuverte", DpmIndemAnnee.class);

		query.setParameter("dateJour", new Date());
		
		return query.getResultList();
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
