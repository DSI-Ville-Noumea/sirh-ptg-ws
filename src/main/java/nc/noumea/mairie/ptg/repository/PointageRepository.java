package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;

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

		Query q = ptgEntityManager.createNativeQuery(
				"SELECT t1.* FROM PTG_POINTAGE t1 START WITH t1.ID_POINTAGE = :idPointage CONNECT BY PRIOR t1.ID_POINTAGE_PARENT = t1.ID_POINTAGE",
				Pointage.class);
		q.setParameter("idPointage", idPointage);

		@SuppressWarnings("unchecked")
		List<Pointage> result = q.getResultList();

		return result;
	}

	@Override
	public List<Pointage> getListPointagesForVentilationByDateEtat(Integer idAgent, Date fromDate, Date toDate, RefTypePointageEnum pointageType) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT p.* ");
		sb.append("FROM PTG_ETAT_POINTAGE ep ");
		sb.append("INNER JOIN PTG_POINTAGE p ON ep.ID_POINTAGE = p.ID_POINTAGE ");
		sb.append("INNER JOIN ( ");
		sb.append("SELECT epmax.id_pointage, max(epmax.date_etat) AS maxdate  ");
		sb.append("FROM ptg_etat_pointage epmax ");
		sb.append("INNER JOIN ptg_pointage ptg ON ptg.id_pointage = epmax.id_pointage ");
		sb.append("WHERE ptg.id_agent = :idAgent ");

		if (pointageType != null)
			sb.append("AND ptg.ID_TYPE_POINTAGE = :typePointage ");

		sb.append("GROUP BY epmax.id_pointage)  ");
		sb.append("maxEtats ON maxEtats.maxdate = ep.date_etat AND maxEtats.id_pointage = ep.id_pointage ");
		sb.append("WHERE p.ID_AGENT = :idAgent ");
		sb.append("AND ep.date_etat between :fromDate and :toDate ");
		sb.append("AND (ep.etat = :approuve or ep.etat = :ventile) ");
		sb.append("ORDER BY id_pointage DESC ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), Pointage.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("fromDate", fromDate);
		q.setParameter("toDate", toDate);
		q.setParameter("approuve", EtatPointageEnum.APPROUVE.getCodeEtat());
		q.setParameter("ventile", EtatPointageEnum.VENTILE.getCodeEtat());

		if (pointageType != null)
			q.setParameter("typePointage", pointageType.getValue());

		@SuppressWarnings("unchecked")
		List<Pointage> result = q.getResultList();

		return result;
	}

	@Override
	public VentilDate getLatestVentilDate(TypeChainePaieEnum chainePaie, boolean isPaid) {

		TypedQuery<VentilDate> q = ptgEntityManager.createQuery(
				"SELECT d FROM VentilDate d where d.typeChainePaie = :chainePaie and d.paye = :paid ORDER BY d.dateVentilation desc",
				VentilDate.class);

		q.setParameter("chainePaie", chainePaie);
		q.setParameter("paid", isPaid);
		q.setMaxResults(1);

		List<VentilDate> list = q.getResultList();

		return list.size() == 0 ? null : list.get(0);
	}

	public void removeVentilationsForDateAgentAndType(VentilDate ventilDate, Integer idAgent, RefTypePointageEnum typePointage) {

		String query = null;

		switch (typePointage) {
		case PRIME:
			query = "DELETE FROM VentilPrime vp WHERE vp.ventilDate.idVentilDate = :idVentilDate and vp.idAgent = :idAgent";
			break;
		case H_SUP:
			query = "DELETE FROM VentilHsup vh WHERE vh.ventilDate.idVentilDate = :idVentilDate and vh.idAgent = :idAgent";
			break;
		case ABSENCE:
			query = "DELETE FROM VentilAbsence va WHERE va.ventilDate.idVentilDate = :idVentilDate and va.idAgent = :idAgent";
			break;
		}

		Query q = ptgEntityManager.createQuery(query);
		q.setParameter("idVentilDate", ventilDate.getIdVentilDate());
		q.setParameter("idAgent", idAgent);
		q.executeUpdate();
		ptgEntityManager.flush();
	}

	@Override
	public void removePointageCalculesForDateAgent(Integer idAgent, Date from, Date to) {

		String query = "DELETE FROM PointageCalcule ptg WHERE ptg.idAgent = :idAgent and ptg.dateLundi between :from and :to";
		Query q = ptgEntityManager.createQuery(query);
		q.setParameter("idAgent", idAgent);
		q.setParameter("from", from);
		q.setParameter("to", to);

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

		if (idAgents != null) {
			sb.append("AND ptg.ID_AGENT IN (:idAgents) ");
		}

		if (idRefType != null) {
			sb.append("AND ptg.ID_TYPE_POINTAGE = :idRefType ");
		}

		Query q = ptgEntityManager.createNativeQuery(sb.toString(), Pointage.class);
		q.setParameter("fromDate", fromDate);
		q.setParameter("toDate", toDate);

		if (idAgents != null) {
			q.setParameter("idAgents", idAgents);
		}
		if (idRefType != null)
			q.setParameter("idRefType", idRefType);

		@SuppressWarnings("unchecked")
		List<Pointage> result = q.getResultList();

		return result;
	}
}
