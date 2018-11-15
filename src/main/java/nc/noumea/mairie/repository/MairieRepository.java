package nc.noumea.mairie.repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spmatr;
import nc.noumea.mairie.domain.Spperm;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;

@Repository
public class MairieRepository implements IMairieRepository {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager entityManager;

	@Override
	public <T> T getEntity(Class<T> Tclass, Object Id) {
		return entityManager.find(Tclass, Id);
	}

	@Override
	public void persistEntity(Object entity) {
		entityManager.persist(entity);
	}

	@Override
	public void removeEntity(Object obj) {
		entityManager.remove(obj);
	}

	@Override
	public Spmatr findSpmatrForAgent(Integer idAgent) {
		return entityManager.find(Spmatr.class, idAgent);
	}

	@Override
	public void mergeEntity(Object entity) {
		entityManager.merge(entity);
	}

	@Override
	public Spcarr getAgentCurrentCarriere(AgentGeneriqueDto agent, Date asOfDate) {
		return getAgentCurrentCarriere(agent.getNomatr(), asOfDate);
	}

	@Override
	public Spcarr getAgentCurrentCarriere(Integer nomatr, Date asOfDate) {

		TypedQuery<Spcarr> qCarr = entityManager.createNamedQuery("getCurrentCarriere", Spcarr.class);
		qCarr.setParameter("nomatr", nomatr);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int dateFormatMairie = Integer.valueOf(sdf.format(asOfDate));
		qCarr.setParameter("todayFormatMairie", dateFormatMairie);

		List<Spcarr> result = qCarr.getResultList();

		// bug #19943 si 2 carrieres le meme jour 
		// une finissant, une commencant
		return (null != result && !result.isEmpty()) ? result.get(0) : null;
	}

	@Override
	public Spcarr getAgentNextCarriere(Integer nomatr, Date asOfDate) {

		TypedQuery<Spcarr> qCarr = entityManager.createNamedQuery("getNextCarriere", Spcarr.class);
		qCarr.setParameter("nomatr", nomatr);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int dateFormatMairie = Integer.valueOf(sdf.format(asOfDate));
		qCarr.setParameter("todayFormatMairie", dateFormatMairie);

		List<Spcarr> result = qCarr.getResultList();

		return (null != result && !result.isEmpty()) ? result.get(0) : null;
	}

	@Override
	public Spperm getTREtatPayeurRates(Date dateMoisSuivant) {

		TypedQuery<Spperm> qPerm = entityManager.createNamedQuery("getSppermForTREtatPayeurOnPeriod", Spperm.class);

		String moisSuivant = new SimpleDateFormat("yyyyMM").format(dateMoisSuivant);
		qPerm.setParameter("date", Integer.valueOf(moisSuivant));

		List<Spperm> result = qPerm.getResultList();

		return null != result ? result.get(0) : null;
	}

	@Override
	public Spadmn getAgentCurrentPosition(AgentGeneriqueDto agent, Date asOfDate) {
		TypedQuery<Spadmn> qSpadmn = entityManager.createNamedQuery("getAgentSpadmnAsOfDate", Spadmn.class);
		qSpadmn.setParameter("nomatr", agent.getNomatr());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int dateFormatMairie = Integer.valueOf(sdf.format(asOfDate));
		qSpadmn.setParameter("dateFormatMairie", dateFormatMairie);

		Spadmn adm = qSpadmn.getSingleResult();

		return adm;
	}

	@Override
	public Spadmn getAgentCurrentPosition(Integer nomatr, Date asOfDate) {
		TypedQuery<Spadmn> qSpadmn = entityManager.createNamedQuery("getAgentSpadmnAsOfDate", Spadmn.class);
		qSpadmn.setParameter("nomatr", nomatr);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int dateFormatMairie = Integer.valueOf(sdf.format(asOfDate));
		qSpadmn.setParameter("dateFormatMairie", dateFormatMairie);

		Spadmn adm = qSpadmn.getSingleResult();

		return adm;
	}

	@Override
	public List<Spadmn> getListPAOfAgentBetween2Date(Integer noMatr, Date fromDate, Date toDate) {

		TypedQuery<Spadmn> qSpadmn = entityManager.createNamedQuery("getAgentListSpadmnBetweenTwoDate", Spadmn.class);
		qSpadmn.setParameter("nomatr", noMatr);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int fromDateMairie = Integer.valueOf(sdf.format(fromDate));
		qSpadmn.setParameter("fromDate", fromDateMairie);
		int toDateMairie = Integer.valueOf(sdf.format(toDate));
		qSpadmn.setParameter("toDate", toDateMairie);

		return qSpadmn.getResultList();
	}

	@Override
	public String getDerniereFiliereOfAgentOnPeriod(Integer noMatr, Date fromDate, Date toDate) {

		StringBuffer query = new StringBuffer();
		query.append("select c.cdfili from spcarr a ");
		query.append("inner join SPGRADN b on a.CDGRAD=b.CDGRAD ");
		query.append("inner join SPGENG c on b.CODGRG=c.CDGENG ");
		query.append("where a.nomatr = :nomatr and a.datdeb <= :toDate and (a.datfin >= :fromDate or a.datfin = 0) ");
		query.append("order by a.datdeb desc");

		Query q = entityManager.createNativeQuery(query.toString());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int fromDateMairie = Integer.valueOf(sdf.format(fromDate));
		q.setParameter("fromDate", fromDateMairie);
		int toDateMairie = Integer.valueOf(sdf.format(toDate));
		q.setParameter("toDate", toDateMairie);

		q.setParameter("nomatr", noMatr);

		@SuppressWarnings("unchecked")
		List<Character> result = q.getResultList();

		if (null != result && !result.isEmpty()) {
			return  result.get(0).toString();
		}
		return null;
	}

}
