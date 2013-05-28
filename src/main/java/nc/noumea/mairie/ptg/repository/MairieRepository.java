package nc.noumea.mairie.ptg.repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.service.HelperService;
import nc.noumea.mairie.sirh.domain.Agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MairieRepository implements IMairieRepository {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;

	@Autowired
	private HelperService helperService;

	@Override
	public Spcarr getAgentCurrentCarriere(Agent agent, Date asOfDate) {

		TypedQuery<Spcarr> qCarr = sirhEntityManager.createNamedQuery("getCurrentCarriere", Spcarr.class);
		qCarr.setParameter("nomatr", agent.getNomatr());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int dateFormatMairie = Integer.valueOf(sdf.format(asOfDate));
		qCarr.setParameter("todayFormatMairie", dateFormatMairie);

		Spcarr carr = qCarr.getSingleResult();

		return carr;
	}

	@Override
	public Agent getAgent(int idAgent) {
		return sirhEntityManager.getReference(Agent.class, idAgent);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getPrimePointagesByAgent(Integer idAgent, Date date) {
		StringBuilder sb = new StringBuilder();
		sb.append("select paff.num_rubrique from sirh.affectation aff ");
		sb.append("inner join sirh.prime_pointage_aff paff on aff.id_affectation=paff.id_affectation ");
		sb.append("where aff.id_agent = :idAgent and aff.date_Debut_Aff <= :date and (aff.date_Fin_Aff = '01/01/0001' or aff.date_Fin_Aff is null or aff.date_Fin_Aff >= :date) ");
		sb.append(" union ");
		sb.append("select pfp.num_rubrique from sirh.affectation aff ");
		sb.append("inner join sirh.fiche_poste fp on fp.id_fiche_poste=aff.id_fiche_poste ");
		sb.append("inner join sirh.prime_pointage_fp pfp on fp.id_fiche_poste=pfp.id_fiche_poste ");
		sb.append("where aff.id_agent = :idAgent and aff.date_Debut_Aff <= :date and (aff.date_Fin_Aff = '01/01/0001' or aff.date_Fin_Aff is null or aff.date_Fin_Aff >= :date) ");

		Query q = sirhEntityManager.createNativeQuery(sb.toString());
		q.setParameter("idAgent", idAgent);
		q.setParameter("date", date);

		List<Integer> result = q.getResultList();

		return result;
	}
}
