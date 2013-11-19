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
import nc.noumea.mairie.ptg.domain.PointageCalcule;
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
    public List<RefPrime> getRefPrimesCalculees(List<Integer> noRubrList, AgentStatutEnum statut) {

        TypedQuery<RefPrime> query = ptgEntityManager.createNamedQuery("getRefPrimesCalculated", RefPrime.class);
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
        if (ptg.getIdPointage() == null || ptg.getIdPointage().equals(0)) {
            ptgEntityManager.persist(ptg);
        }
    }

    @Override
    public List<Pointage> getListPointages(List<Integer> idAgents, Date fromDate, Date toDate, Integer idRefType) {

        StringBuilder sb = new StringBuilder();
        sb.append("select ptg from Pointage ptg ");
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
	public List<PointageCalcule> getPointagesCalculesVentilesForAgent(Integer idAgent, Integer idVentilDate) {

    	TypedQuery<PointageCalcule> query = ptgEntityManager.createQuery(
    			"select p from PointageCalcule p where p.idAgent = :idAgent and p.lastVentilDate.idVentilDate = :idVentilDate", PointageCalcule.class);
        query.setParameter("idAgent", idAgent);
        query.setParameter("idVentilDate", idVentilDate);

        return query.getResultList();
	}

    @Override
    public List<Pointage> getListPointagesNative(List<Integer> idAgents, Date fromDate, Date toDate, Integer idRefType) {

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ptg.* ");
        sb.append("FROM PTG_POINTAGE ptg ");
        sb.append("WHERE PTG.DATE_DEBUT >= :fromDate AND PTG.DATE_DEBUT <= :toDate ");

        if (idAgents != null && idAgents.size() > 0) {
            sb.append("AND ptg.ID_AGENT IN (:idAgents) ");                                                                                                                       
        }

        if (idRefType != null) {
            sb.append("AND ptg.ID_TYPE_POINTAGE = :idRefType ");
        }

        sb.append("ORDER BY ptg.ID_POINTAGE DESC");

        Query q = ptgEntityManager.createNativeQuery(sb.toString(), Pointage.class);
        q.setParameter("fromDate", fromDate);
        q.setParameter("toDate", toDate);

        if (idAgents != null && idAgents.size() > 0) {
            q.setParameter("idAgents", idAgents);
        }

        if (idRefType != null) {
            q.setParameter("idRefType", idRefType);
        }

        @SuppressWarnings("unchecked")
        List<Pointage> result = q.getResultList();
        return result;
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
	public boolean isPrimeSurPointageouPointageCalcule(Integer idAgent,
			Integer idRefPrime) {

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
}
