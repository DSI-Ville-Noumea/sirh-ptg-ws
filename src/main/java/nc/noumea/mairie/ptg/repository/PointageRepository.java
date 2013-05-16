package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.sirh.domain.PrimePointage;

import org.springframework.stereotype.Repository;

@Repository
public class PointageRepository implements IPointageRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getIdPointagesParents(Pointage pointage) {

		Query q = ptgEntityManager
				.createNativeQuery("SELECT t1.ID_POINTAGE FROM PTG_POINTAGE t1 START WITH t1.ID_POINTAGE = :idPointage CONNECT BY PRIOR t1.ID_POINTAGE_PARENT = t1.ID_POINTAGE");
		q.setParameter("idPointage", pointage.getIdPointage());

		List<Integer> result = q.getResultList();

		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<PrimePointage> getPrimePointagesByAgent(Integer idAgent, Date date) {
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct pp.* from sirh.affectation aff, sirh.prime_pointage pp ");
		sb.append("inner join sirh.fiche_poste fp on fp.id_fiche_poste=aff.id_fiche_poste ");
		sb.append("left join sirh.prime_pointage_aff paff on aff.id_affectation=paff.id_affectation ");
		sb.append("left join sirh.prime_pointage_fp pfp on fp.id_fiche_poste=pfp.id_fiche_poste ");
		sb.append("where aff.id_agent = :idAgent and aff.date_Debut_Aff <= :date and (aff.date_Fin_Aff = '01/01/0001' or aff.date_Fin_Aff is null or aff.date_Fin_Aff >= :date) ");
		sb.append("and (pp.id_prime_pointage=paff.id_prime_pointage or pp.id_prime_pointage=pfp.id_prime_pointage) ");

		Query q = sirhEntityManager.createNativeQuery(sb.toString(), PrimePointage.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("date", date);

		List<PrimePointage> result = q.getResultList();

		return result;
	}

	@Override
	public List<RefPrime> getRefPrimes(List<Integer> noRubrList, AgentStatutEnum statut) {

		TypedQuery<RefPrime> query = ptgEntityManager.createNamedQuery("getRefPrimes", RefPrime.class);
		query.setParameter("noRubrList", noRubrList);
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
}
