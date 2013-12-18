package nc.noumea.mairie.ptg.repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.ReposCompHisto;
import nc.noumea.mairie.ptg.domain.ReposCompTask;

import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

@Repository
public class ReposCompRepository implements IReposCompRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;

	@Override
	public ReposCompTask getReposCompTask(Integer idReposCompTask) {
		return ptgEntityManager.find(ReposCompTask.class, idReposCompTask);
	}

	@Override
	public Integer countTotalHSupsSinceStartOfYear(Integer idAgent,
			Integer currentYear) {

		Query q = ptgEntityManager
				.createNativeQuery("select sum(m_sup) from PTG_RC_HISTO where id_agent = :idAgent and date_lundi between :dateDeb and :dateFin");
		q.setParameter("idAgent", idAgent);
		q.setParameter("dateDeb", new DateTime(currentYear, 1, 1, 0, 0, 0)
				.withWeekOfWeekyear(1).withDayOfWeek(1).toDate());
		q.setParameter("dateFin", new DateTime(currentYear + 1, 1, 1, 0, 0, 0)
				.withWeekOfWeekyear(1).withDayOfWeek(1).toDate());

		BigInteger result = (BigInteger) q.getSingleResult();

		return result.intValue();
	}

	@Override
	public ReposCompHisto findReposCompHistoForAgentAndDate(Integer idAgent, Date dateLundi) {

		TypedQuery<ReposCompHisto> q = ptgEntityManager.createNamedQuery("findReposCompHistoByAgentAndDate", ReposCompHisto.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("dateLundi", dateLundi);
		q.setMaxResults(0);
		
		List<ReposCompHisto> r = q.getResultList();
		
		return r.size() == 0 ? null : r.get(0);
	}

}
