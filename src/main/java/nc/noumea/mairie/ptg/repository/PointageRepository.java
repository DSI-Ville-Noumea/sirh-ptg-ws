package nc.noumea.mairie.ptg.repository;

import java.util.ArrayList;
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
	public List<RefPrime> getRefPrimes(List<Integer> noRubrList, AgentStatutEnum statut) {

		TypedQuery<RefPrime> query = ptgEntityManager.createNamedQuery("getRefPrimesNotCalculated", RefPrime.class);
		query.setParameter("noRubrList", noRubrList.size() == 0 ? null : noRubrList);
		query.setParameter("statut", statut);
		return query.getResultList();
	}

	@Override
	public List<RefPrime> getRefPrimesListForAgent(AgentStatutEnum statut) {

		TypedQuery<RefPrime> query = ptgEntityManager.createNamedQuery("getListPrimesWithStatusByIdDesc", RefPrime.class);
		query.setParameter("statut", statut);
		return query.getResultList();
	}

	@Override
	public List<RefPrime> getRefPrimesList() {

		TypedQuery<RefPrime> query = ptgEntityManager.createNamedQuery("getListPrimesByIdDesc", RefPrime.class);

		List<RefPrime> temp = query.getResultList();

		List<Integer> rubs = new ArrayList<>();
		List<RefPrime> res = new ArrayList<>();
		for (RefPrime p : temp) {
			if (!rubs.contains(p.getNoRubr())) {
				rubs.add(p.getNoRubr());
				res.add(p);
			}
		}
		return res;
	}

	@Override
	public List<Pointage> getPointagesForAgentAndDateOrderByIdDesc(int idAgent, Date dateLundi) {

		TypedQuery<Pointage> query = ptgEntityManager.createNamedQuery("getPointageForAgentAndDateLundiByIdDesc", Pointage.class);
		query.setParameter("idAgent", idAgent);
		query.setParameter("dateLundi", dateLundi);

		return query.getResultList();
	}

	@Override
	public List<Pointage> getListPointages(List<Integer> idAgents, Date fromDate, Date toDate, Integer idRefType) {

		String queryName = "";
		if (idRefType != null)
			queryName = "getListPointageByAgentsTypeAndDate";
		else
			queryName = "getListPointageByAgentsAndDate";

		TypedQuery<Pointage> query = ptgEntityManager.createNamedQuery(queryName, Pointage.class);
		query.setParameter("idAgents", idAgents.size() == 0 ? null : idAgents);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);

		if (idRefType != null)
			query.setParameter("idRefTypePointage", idRefType);

		return query.getResultList();
	}

	public List<Pointage> getPointageArchives(Integer idPointage) {

		Query q = ptgEntityManager.createNativeQuery("SELECT t1.* FROM PTG_POINTAGE t1 START WITH t1.ID_POINTAGE = :idPointage CONNECT BY PRIOR t1.ID_POINTAGE_PARENT = t1.ID_POINTAGE", Pointage.class);
		q.setParameter("idPointage", idPointage);

		@SuppressWarnings("unchecked")
		List<Pointage> result = q.getResultList();

		return result;
	}

	@Override
	public void removePointageCalculesForDateAgent(Integer idAgent, Date dateLundi) {

		String query = "DELETE FROM PointageCalcule ptg WHERE ptg.idAgent = :idAgent and ptg.dateLundi = :dateLundi";
		Query q = ptgEntityManager.createQuery(query);
		q.setParameter("idAgent", idAgent);
		q.setParameter("dateLundi", dateLundi);

		q.executeUpdate();
		ptgEntityManager.flush();
	}

	@Override
	public void savePointage(Pointage ptg) {
		if (ptg.getIdPointage() == null || ptg.getIdPointage().equals(0))
			ptgEntityManager.persist(ptg);
	}

	@Override
	public <T> T getEntity(Class<T> Tclass, Object Id) {
		return ptgEntityManager.find(Tclass, Id);
	}

	@Override
	public List<Pointage> getListPointagesSIRH(Date fromDate, Date toDate, Integer idRefType, List<Integer> idAgents) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ptg.* ");
		sb.append("FROM PTG_POINTAGE ptg ");
		sb.append("LEFT JOIN PTG_COMMENT motif ON ptg.ID_COMMENT_MOTIF = motif.ID_COMMENT ");
		sb.append("LEFT JOIN PTG_COMMENT commentaire ON ptg.ID_COMMENT_COMMENTAIRE = motif.ID_COMMENT ");
		sb.append("LEFT JOIN PTG_REF_PRIME refPrime ON ptg.ID_REF_PRIME = refPrime.ID_REF_PRIME ");
		sb.append("INNER JOIN PTG_REF_TYPE_POINTAGE type ON ptg.ID_TYPE_POINTAGE = type.ID_REF_TYPE_POINTAGE ");
		sb.append("WHERE to_date(PTG.DATE_DEBUT)>= :fromDate AND to_date(PTG.DATE_DEBUT)<= :toDate ");

		if (idAgents != null && idAgents.size() > 0) {
			sb.append("AND ptg.ID_AGENT IN (:idAgents) ");
		}

		if (idRefType != null) {
			sb.append("AND ptg.ID_TYPE_POINTAGE = :idRefType ");
		}

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), Pointage.class);
		q.setParameter("fromDate", fromDate);
		q.setParameter("toDate", toDate);

		if (idAgents != null && idAgents.size() > 0) {
			q.setParameter("idAgents", idAgents);
		}
		if (idRefType != null)
			q.setParameter("idRefType", idRefType);

		@SuppressWarnings("unchecked")
		List<Pointage> result = q.getResultList();
		return result;
	}

}
