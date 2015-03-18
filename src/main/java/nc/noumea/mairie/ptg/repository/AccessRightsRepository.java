package nc.noumea.mairie.ptg.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;

import org.springframework.stereotype.Repository;

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
				"from Droit d where d.approbateur = true and d.idAgent = :idAgent ", Droit.class);
		q.setParameter("idAgent", idAgent);

		List<Droit> list = q.getResultList();
		if (list == null || list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public void removeDroitsAgent(DroitsAgent droitsAgent) {
		ptgEntityManager.remove(droitsAgent);
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
	public List<Droit> getAgentsOperateurs() {

		TypedQuery<Droit> q = ptgEntityManager.createNamedQuery("getAgentsOperateurs", Droit.class);

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

	@Override
	public List<DroitsAgent> getListOfAgentsToInputOrApprove(Integer idAgent) {
		return getListOfAgentsToInputOrApprove(idAgent, null);
	}

	@Override
	public List<DroitsAgent> getListOfAgentsToInputOrApprove(Integer pIdAgent, String pCodeService) {
		
		// #14325 modification des requetes pour la gestion des droits
		TypedQuery<DroitsAgent> q = ptgEntityManager.createQuery(
				 pCodeService == null ? getListOfAgentsToInputOrApproveWithoutService()
							: getListOfAgentsToInputOrApproveByService(),
							DroitsAgent.class);
		q.setParameter("idAgent", pIdAgent);

		if (null != pCodeService) {
			q.setParameter("codeService", pCodeService);
		}

		List<DroitsAgent> result = q.getResultList();

		return result;
	}

	// #14325 modifications sur le cumul des roles
	@Override
	public List<DroitsAgent> getListOfAgentsToInput(Integer idApprobateur, Integer pIdAgent) {
		
		String sqlQuery = "SELECT  distinct(da.id_Agent), da.code_Service, da.libelle_Service " + "from PTG_DROITS_AGENT da "
				+ "inner join PTG_DROIT_DROITS_AGENT dda on da.id_droits_agent = dda.id_droits_agent "
				+ "inner join PTG_DROIT d on dda.id_droit = d.id_droit "
				+ "where d.id_Agent = :idAgent "
				+ " and d.id_droit_approbateur in ( select dap.id_droit from PTG_DROIT dap where dap.id_agent = :idApprobateur and dap.is_approbateur is true ) " ;

		Query q = ptgEntityManager.createNativeQuery(sqlQuery);
		q.setParameter("idAgent", pIdAgent);
		q.setParameter("idApprobateur", idApprobateur);

		List<DroitsAgent> result = new ArrayList<DroitsAgent>();
		@SuppressWarnings("unchecked")
		List<Object[]> l = q.getResultList();

		for (Object[] r : l) {
			Integer idAgent = (Integer) r[0];
			String codeService = (String) r[1];
			String libelleService = (String) r[2];

			DroitsAgent da = new DroitsAgent(idAgent, codeService, libelleService);
			result.add(da);
		}

		return result;
	}

	private String getListOfAgentsToInputOrApproveWithoutService() {

		return "SELECT da from DroitsAgent da "
				+ "inner join da.droits d "
				+ "where d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent "
				+ " group by da ";
	}

	private String getListOfAgentsToInputOrApproveByService() {

		return "SELECT da from DroitsAgent da "
				+ "inner join da.droits d "
				+ "where (d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent) and da.codeService = :codeService "
				+ "group by da ";
	}

	@Override
	public Integer getAgentsApprobateur(Integer idAgent) {

		TypedQuery<Integer> q = ptgEntityManager.createNamedQuery("getAgentsApprobateur", Integer.class);
		q.setParameter("idAgent", idAgent);
		q.setMaxResults(1);

		List<Integer> results = q.getResultList();

		return results.size() != 0 ? results.get(0) : null;
	}

}
