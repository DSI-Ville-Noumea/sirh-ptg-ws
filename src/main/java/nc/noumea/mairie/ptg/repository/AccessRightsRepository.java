package nc.noumea.mairie.ptg.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class AccessRightsRepository implements IAccessRightsRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;

	@Override
	public void removeEntity(Object obj) {
		ptgEntityManager.remove(obj);
	}

	@Override
	public List<Droit> getAgentAccessRights(int idAgent) {

		TypedQuery<Droit> q = ptgEntityManager.createNamedQuery("getAgentAccessRights", Droit.class);
		q.setParameter("idAgent", idAgent);

		return q.getResultList();
	}

	@Override
	public Droit getDroitApprobateurByAgent(Integer idAgent) {
		TypedQuery<Droit> q = ptgEntityManager.createQuery(
				"from Droit d JOIN FETCH d.agents where d.approbateur = true and d.idAgent = :idAgent ", Droit.class);
		q.setParameter("idAgent", idAgent);

		List<Droit> list = q.getResultList();
		if (list == null || list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public boolean isUserApprobator(Integer idAgent) {

		TypedQuery<Long> q = ptgEntityManager.createQuery(
				"select count(*) from Droit d where d.approbateur is true and d.idAgent = :idAgent", Long.class);
		q.setParameter("idAgent", idAgent);

		Boolean result = null;
		if (null != q.getSingleResult() && 0 < q.getSingleResult().longValue()) {
			result = true;
		}

		return (result != null);
	}

	@Override
	public boolean isUserApprobatorOrDelegataire(Integer idAgent) {

		TypedQuery<Long> q = ptgEntityManager
				.createQuery(
						"select count(*) from Droit d where d.approbateur is true and (d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent )",
						Long.class);
		q.setParameter("idAgent", idAgent);

		Boolean result = null;
		if (null != q.getSingleResult() && 0 < q.getSingleResult().longValue()) {
			result = true;
		}

		return (result != null);
	}

	@Override
	public boolean isUserOperator(Integer idAgent) {

		TypedQuery<Long> q = ptgEntityManager.createQuery(
				"select count(*) from Droit d where d.operateur is true and d.idAgent = :idAgent", Long.class);
		q.setParameter("idAgent", idAgent);

		Boolean result = null;
		if (null != q.getSingleResult() && 0 < q.getSingleResult().longValue()) {
			result = true;
		}

		return (result != null);
	}

	@Override
	@Transactional(readOnly=true)
	public boolean isUserApprobatorOrOperatorOrDelegataire(Integer idAgent) {
		TypedQuery<Long> q = ptgEntityManager
				.createQuery(
						"select count(*) from Droit d where (d.approbateur = true or d.operateur = true) and (d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent )",
						Long.class);
		q.setParameter("idAgent", idAgent);

		Boolean result = null;
		if (null != q.getSingleResult() && 0 < q.getSingleResult().longValue()) {
			result = true;
		}

		return (result != null);
	}

	@Override
	public List<Droit> getAgentsApprobateurs() {

		TypedQuery<Droit> q = ptgEntityManager.createNamedQuery("getAgentsApprobateurs", Droit.class);

		return q.getResultList();
	}

	@Override
	public Droit getAgentDroitApprobateurOrOperateurFetchAgents(Integer idAgent, Integer idDroitApprobateur) {
		String sqlWhere = "";
		if (idDroitApprobateur != null)
			sqlWhere = " and d.droitApprobateur.idDroit = :idDroitApprobateur";

		TypedQuery<Droit> q = ptgEntityManager.createQuery(
				"from Droit d LEFT JOIN FETCH d.agents where (d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent) "
						+ sqlWhere, Droit.class);
		q.setParameter("idAgent", idAgent);
		if (idDroitApprobateur != null)
			q.setParameter("idDroitApprobateur", idDroitApprobateur);

		List<Droit> r = q.getResultList();

		if (r.size() == 0)
			return null;

		return r.get(0);
	}

	@Override
	public void persisEntity(Object obj) {
		ptgEntityManager.persist(obj);
	}

	@Override
	public Droit getApprobateurFetchOperateurs(Integer idAgentApprobateur) {

		TypedQuery<Droit> q = ptgEntityManager.createQuery(
				"from Droit d LEFT JOIN FETCH d.operateurs where d.idAgent = :idAgent and d.approbateur = true",
				Droit.class);
		q.setParameter("idAgent", idAgentApprobateur);

		List<Droit> r = q.getResultList();

		if (r.size() == 0)
			return null;

		return r.get(0);
	}

	@Override
	public Droit getApprobateur(Integer idAgentApprobateur) {

		TypedQuery<Droit> q = ptgEntityManager.createQuery(
				"from Droit d where d.idAgent = :idAgent and d.approbateur = true", Droit.class);
		q.setParameter("idAgent", idAgentApprobateur);

		List<Droit> r = q.getResultList();

		if (r.size() == 0)
			return null;

		return r.get(0);
	}

	// #14325 modifications sur le cumul des roles
	@Override
	public List<DroitsAgent> getListOfAgentsToInput(Integer idApprobateur, Integer pIdAgent) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT  distinct(da.id_Agent)  as id_agent  from PTG_DROITS_AGENT da ");
		sb.append("inner join PTG_DROIT_DROITS_AGENT dda on da.id_droits_agent = dda.id_droits_agent ");
		sb.append("inner join PTG_DROIT d on dda.id_droit = d.id_droit ");
		sb.append("where d.id_Agent = :idAgent ");
		sb.append("and d.id_droit_approbateur in ( select dap.id_droit from PTG_DROIT dap where dap.id_agent = :idApprobateur and dap.is_approbateur is true ) ");

		Query q = ptgEntityManager.createNativeQuery(sb.toString());
		q.setParameter("idAgent", pIdAgent);
		q.setParameter("idApprobateur", idApprobateur);

		List<DroitsAgent> result = new ArrayList<DroitsAgent>();
		@SuppressWarnings("unchecked")
		List<Integer> listeId = q.getResultList();

		for (Integer r : listeId) {
			DroitsAgent da = new DroitsAgent(r);
			result.add(da);
		}

		return result;
	}

	@Override
	public List<DroitsAgent> getListOfAgentsToApprove(Integer idAgent) {
		// #14694 modification des requetes pour la gestion des droits
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT da from DroitsAgent da ");
		sb.append("inner join da.droits d ");
		sb.append("where d.approbateur is true ");
		sb.append("and d.idAgent = :idAgent ");
		sb.append("group by da ");

		TypedQuery<DroitsAgent> q = ptgEntityManager.createQuery(sb.toString(), DroitsAgent.class);
		q.setParameter("idAgent", idAgent);

		return q.getResultList();
	}

	@Override
	public List<DroitsAgent> getListOfAgentsToInputOrApprove(Integer pIdAgent) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT da from DroitsAgent da ");
		sb.append("inner join da.droits d ");
		sb.append("where d.idAgent = :idAgent ");
		sb.append("or d.idAgentDelegataire = :idAgent ");
		sb.append("group by da ");

		// #14325 modification des requetes pour la gestion des droits
		TypedQuery<DroitsAgent> q = ptgEntityManager.createQuery(sb.toString(), DroitsAgent.class);
		q.setParameter("idAgent", pIdAgent);

		List<DroitsAgent> result = q.getResultList();

		return result;
	}

	@Override
	public DroitsAgent getDroitsAgent(Integer idAgent) {

		TypedQuery<DroitsAgent> q = ptgEntityManager.createNamedQuery("getDroitsAgent", DroitsAgent.class);
		q.setParameter("idAgent", idAgent);
		List<DroitsAgent> ds = q.getResultList();

		return ds.size() == 0 ? null : ds.get(0);
	}

}
