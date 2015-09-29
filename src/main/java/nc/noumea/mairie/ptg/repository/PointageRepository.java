package nc.noumea.mairie.ptg.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.MotifHeureSup;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefEtat;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.ptg.domain.RefTypePointage;

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
	public List<RefPrime> getRefPrimesCalculees(List<Integer> noRubrList, AgentStatutEnum statut) {

		TypedQuery<RefPrime> query = ptgEntityManager.createNamedQuery("getRefPrimesCalculated", RefPrime.class);
		query.setParameter("noRubrList", noRubrList.size() == 0 ? null : noRubrList);
		query.setParameter("statut", statut);
		return query.getResultList();
	}

	@Override
	public List<RefPrime> getRefPrimesListForAgent(AgentStatutEnum statut) {

		TypedQuery<RefPrime> query = ptgEntityManager.createNamedQuery("getListPrimesWithStatusByIdDesc",
				RefPrime.class);
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

		TypedQuery<Pointage> query = ptgEntityManager.createNamedQuery("getPointageForAgentAndDateLundiByIdDesc",
				Pointage.class);
		query.setParameter("idAgent", idAgent);
		query.setParameter("dateLundi", dateLundi);

		return query.getResultList();
	}

	@Override
	public List<Pointage> getPointageArchives(Integer idPointage) {

		Query q = ptgEntityManager
				.createNativeQuery(
						"WITH RECURSIVE pointagearchive AS ( (SELECT t1.* FROM PTG_POINTAGE t1 WHERE t1.ID_POINTAGE = :idPointage ) "
								+ "UNION ALL ( SELECT t2.* FROM PTG_POINTAGE AS t2 JOIN pointagearchive AS t3 ON (t2.ID_POINTAGE = t3.ID_POINTAGE_PARENT) ) ) "
								+ "SELECT * FROM pointagearchive order by ID_POINTAGE desc", Pointage.class);
		q.setParameter("idPointage", idPointage);

		@SuppressWarnings("unchecked")
		List<Pointage> result = q.getResultList();

		return result;
	}

	@Override
	public int removePointageCalculesForDateAgent(Integer idAgent, Date dateLundi) {

		String query = "DELETE FROM PointageCalcule ptg WHERE ptg.idAgent = :idAgent and ptg.dateLundi = :dateLundi and ptg.etat = :etat";
		Query q = ptgEntityManager.createQuery(query);
		q.setParameter("idAgent", idAgent);
		q.setParameter("dateLundi", dateLundi);
		q.setParameter("etat", EtatPointageEnum.VENTILE);

		int result = q.executeUpdate();
		ptgEntityManager.flush();

		return result;
	}

	@Override
	public void savePointage(Pointage ptg) {
		if (ptg.getIdPointage() == null || ptg.getIdPointage().equals(0)) {
			ptgEntityManager.persist(ptg);
		}
	}

	@Override
	public List<Pointage> getListPointages(List<Integer> idAgents, Date fromDate, Date toDate, Integer idRefType) {

		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(ptg) from Pointage ptg ");
		sb.append("LEFT JOIN FETCH ptg.etats ");
		sb.append("LEFT JOIN FETCH ptg.motif LEFT JOIN FETCH ptg.commentaire LEFT JOIN FETCH ptg.refPrime JOIN FETCH ptg.type ");
		sb.append("where ptg.dateDebut >= :fromDate and ptg.dateDebut < :toDate ");

		if (idRefType != null) {
			sb.append("and ptg.type.idRefTypePointage = :idRefTypePointage ");
		}

		if (idAgents != null && idAgents.size() > 0) {
			sb.append("and ptg.idAgent in :idAgents ");
		}

		sb.append("order by ptg.idPointage desc ");

		TypedQuery<Pointage> query = ptgEntityManager.createQuery(sb.toString(), Pointage.class);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);

		if (idRefType != null) {
			query.setParameter("idRefTypePointage", idRefType);
		}

		if (idAgents != null && idAgents.size() > 0) {
			query.setParameter("idAgents", idAgents);
		}

		return query.getResultList();
	}

	@Override
	public List<Pointage> getPointagesVentilesForAgent(Integer idAgent, Integer idVentilDate) {

		StringBuilder sb = new StringBuilder();
		sb.append("select ptg from VentilDate vd ");
		sb.append("JOIN vd.pointages ptg ");
		sb.append("where ptg.idAgent = :idAgent and vd.idVentilDate = :idVentilDate ");
		sb.append("order by ptg.idPointage desc ");

		TypedQuery<Pointage> query = ptgEntityManager.createQuery(sb.toString(), Pointage.class);
		query.setParameter("idAgent", idAgent);
		query.setParameter("idVentilDate", idVentilDate);

		return query.getResultList();
	}

	@Override
	public List<Pointage> getPointagesVentilesForAgentByDateLundi(Integer idAgent, Integer idVentilDate, Date dateLundi) {

		StringBuilder sb = new StringBuilder();
		sb.append("select ptg from VentilDate vd ");
		sb.append("JOIN vd.pointages ptg ");
		sb.append("where ptg.idAgent = :idAgent and vd.idVentilDate = :idVentilDate ");
		sb.append("and ptg.dateLundi = :dateLundi ");
		sb.append("order by ptg.idPointage desc ");

		TypedQuery<Pointage> query = ptgEntityManager.createQuery(sb.toString(), Pointage.class);
		query.setParameter("idAgent", idAgent);
		query.setParameter("idVentilDate", idVentilDate);
		query.setParameter("dateLundi", dateLundi);

		return query.getResultList();
	}

	@Override
	public List<PointageCalcule> getPointagesCalculesVentilesForAgent(Integer idAgent, Integer idVentilDate) {

		TypedQuery<PointageCalcule> query = ptgEntityManager
				.createQuery(
						"select p from PointageCalcule p where p.idAgent = :idAgent and p.lastVentilDate.idVentilDate = :idVentilDate and p.etat = :etat",
						PointageCalcule.class);
		query.setParameter("idAgent", idAgent);
		query.setParameter("idVentilDate", idVentilDate);
		query.setParameter("etat", EtatPointageEnum.VENTILE);

		return query.getResultList();
	}

	@Override
	public List<RefPrime> getRefPrimesListWithNoRubr(Integer noRubr) {

		TypedQuery<RefPrime> query = ptgEntityManager.createNamedQuery("getRefPrimesByNorubr", RefPrime.class);
		query.setParameter("noRubr", noRubr);
		return query.getResultList();
	}

	@Override
	public <T> T getEntity(Class<T> Tclass, Object Id) {
		return ptgEntityManager.find(Tclass, Id);
	}

	@Override
	public void persisEntity(Object entity) {
		ptgEntityManager.persist(entity);
	}

	@Override
	public void removeEntity(Object entity) {
		ptgEntityManager.remove(entity);
	}

	@Override
	public boolean isPrimeSurPointageouPointageCalcule(Integer idAgent, Integer idRefPrime) {

		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ptg.id_Pointage ");
		sb.append("FROM PTG_POINTAGE ptg ");
		sb.append("WHERE ptg.id_Ref_Prime=:idRefPrime ");
		sb.append("AND ptg.id_Agent=:idAgent ");
		sb.append("union ");
		sb.append("SELECT ptgCalc.id_Pointage_Calcule ");
		sb.append("FROM PTG_POINTAGE_CALCULE ptgCalc ");
		sb.append("WHERE ptgCalc.id_Ref_Prime=:idRefPrime ");
		sb.append("AND ptgCalc.id_Agent=:idAgent ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString());
		q.setParameter("idRefPrime", idRefPrime);
		q.setParameter("idAgent", idAgent);

		@SuppressWarnings("unchecked")
		List<Integer> result = q.getResultList();
		if (result.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public List<RefEtat> findAllRefEtats() {
		return ptgEntityManager.createQuery("SELECT o FROM RefEtat o", RefEtat.class).getResultList();
	}

	@Override
	public List<RefTypePointage> findAllRefTypePointages() {
		return ptgEntityManager.createQuery("SELECT o FROM RefTypePointage o", RefTypePointage.class).getResultList();
	}

	@Override
	public List<Integer> listAllDistinctIdAgentPointage() {

		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(ptg.idAgent) from Pointage ptg ");

		TypedQuery<Integer> query = ptgEntityManager.createQuery(sb.toString(), Integer.class);

		return query.getResultList();
	}

	@Override
	public List<RefTypeAbsence> findAllRefTypeAbsence() {
		return ptgEntityManager.createQuery("SELECT o FROM RefTypeAbsence o", RefTypeAbsence.class).getResultList();
	}

	@Override
	public List<Integer> getListApprobateursPointagesSaisiesJourDonne() {

		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(droit.id_agent) as idAgent from ptg_droit droit ");
		sb.append("inner join ptg_droit_droits_agent dda on droit.id_droit = dda.id_droit ");
		sb.append("inner join ptg_droits_agent da on dda.id_droits_agent = da.id_droits_agent ");
		sb.append("where droit.is_approbateur is true ");
		sb.append("and da.id_agent in ( ");
		sb.append("select p.id_agent from ptg_pointage p ");
		sb.append("inner join ptg_etat_pointage ep on p.id_pointage = ep.id_pointage ");
		sb.append("where ep.etat =:SAISIE ");
		sb.append("and ep.date_etat <= :DATE_JOUR ");
		sb.append("and ep.id_etat_pointage in ( select max(ep2.id_etat_pointage) from ptg_etat_pointage ep2 group by ep2.id_pointage ) ");
		sb.append(" ) GROUP BY idAgent ");

		@SuppressWarnings("unchecked")
		List<Integer> result = ptgEntityManager.createNativeQuery(sb.toString())
				.setParameter("SAISIE", EtatPointageEnum.SAISI.getCodeEtat()).setParameter("DATE_JOUR", new Date())
				.getResultList();

		return result;
	}

	@Override
	public List<MotifHeureSup> findAllMotifHeureSup() {
		return ptgEntityManager.createQuery("SELECT o FROM MotifHeureSup o", MotifHeureSup.class).getResultList();
	}

	@Override
	public List<Pointage> getListPointagesVerification(Integer idAgent, Date fromDate, Date toDate, Integer idRefType) {

		StringBuilder sb = new StringBuilder();
		sb.append("select ptg from Pointage ptg ");
		sb.append("LEFT JOIN FETCH ptg.motif LEFT JOIN FETCH ptg.commentaire LEFT JOIN FETCH ptg.refPrime JOIN FETCH ptg.type ");
		sb.append("where ((:fromDate between ptg.dateDebut and ptg.dateFin or :toDate between ptg.dateDebut and ptg.dateFin) or (ptg.dateDebut between :fromDate and :toDate or ptg.dateFin between :fromDate and :toDate)) ");
		sb.append("and ptg.type.idRefTypePointage = :idRefTypePointage ");
		sb.append("and ptg.idAgent = :idAgent ");
		sb.append("order by ptg.idPointage desc ");

		TypedQuery<Pointage> query = ptgEntityManager.createQuery(sb.toString(), Pointage.class);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		query.setParameter("idRefTypePointage", idRefType);
		query.setParameter("idAgent", idAgent);

		return query.getResultList();
	}
}
