package nc.noumea.mairie.ptg.repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spcong;
import nc.noumea.mairie.domain.Sprirc;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SirhRepository implements ISirhRepository {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;

	@Autowired
	private HelperService helperService;

	@Override
	public Spcarr getAgentCurrentCarriere(AgentGeneriqueDto agent, Date asOfDate) {
		return getAgentCurrentCarriere(agent.getNomatr(), asOfDate);
	}

	@Override
	public Spcarr getAgentCurrentCarriere(Integer nomatr, Date asOfDate) {

		TypedQuery<Spcarr> qCarr = sirhEntityManager.createNamedQuery("getCurrentCarriere", Spcarr.class);
		qCarr.setParameter("nomatr", nomatr);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int dateFormatMairie = Integer.valueOf(sdf.format(asOfDate));
		qCarr.setParameter("todayFormatMairie", dateFormatMairie);

		List<Spcarr> result = qCarr.getResultList();

		if (result.size() != 1)
			return null;

		return result.get(0);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Integer> getPrimePointagesByAgent(Integer idAgent, Date date) {
		StringBuilder sb = new StringBuilder();
		sb.append("select paff.num_rubrique from affectation aff ");
		sb.append("inner join prime_pointage_aff paff on aff.id_affectation=paff.id_affectation ");
		sb.append("where aff.id_agent = :idAgent and aff.date_Debut_Aff <= :date ");
		sb.append("and (aff.date_Fin_Aff = '01/01/0001' or aff.date_Fin_Aff is null or aff.date_Fin_Aff >= :date) ");
		sb.append("order by paff.num_rubrique ");

		Query q = sirhEntityManager.createNativeQuery(sb.toString());
		q.setParameter("idAgent", idAgent);
		q.setParameter("date", date);

		List<Integer> result = q.getResultList();

		return result;
	}

	@Override
	public List<Sprirc> getListRecuperationBetween(Integer idAgent, Date start, Date end) {

		TypedQuery<Sprirc> query = sirhEntityManager.createNamedQuery("getSprircForAgentAndPeriod", Sprirc.class);
		query.setParameter("nomatr", helperService.getMairieMatrFromIdAgent(idAgent));
		query.setParameter("start", helperService.getIntegerDateMairieFromDate(start));
		query.setParameter("end", helperService.getIntegerDateMairieFromDate(end));

		return query.getResultList();
	}

	@Override
	public List<Spcong> getListCongeBetween(Integer idAgent, Date start, Date end) {

		TypedQuery<Spcong> query = sirhEntityManager.createNamedQuery("getSpcongForAgentAndPeriod", Spcong.class);
		query.setParameter("nomatr", helperService.getMairieMatrFromIdAgent(idAgent));
		query.setParameter("start", helperService.getIntegerDateMairieFromDate(start));
		query.setParameter("end", helperService.getIntegerDateMairieFromDate(end));

		return query.getResultList();
	}

	@Override
	public List<Spabsen> getListMaladieBetween(Integer idAgent, Date start, Date end) {
		TypedQuery<Spabsen> query = sirhEntityManager.createNamedQuery("getSpabsenForAgentAndPeriod", Spabsen.class);
		query.setParameter("nomatr", helperService.getMairieMatrFromIdAgent(idAgent));
		query.setParameter("start", helperService.getIntegerDateMairieFromDate(start));
		query.setParameter("end", helperService.getIntegerDateMairieFromDate(end));

		return query.getResultList();
	}

	@Override
	public Spadmn getAgentCurrentPosition(AgentGeneriqueDto agent, Date asOfDate) {
		TypedQuery<Spadmn> qSpadmn = sirhEntityManager.createNamedQuery("getAgentSpadmnAsOfDate", Spadmn.class);
		qSpadmn.setParameter("nomatr", agent.getNomatr());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int dateFormatMairie = Integer.valueOf(sdf.format(asOfDate));
		qSpadmn.setParameter("dateFormatMairie", dateFormatMairie);

		Spadmn adm = qSpadmn.getSingleResult();

		return adm;
	}

	@Override
	public boolean isJourHoliday(Date date) {
		TypedQuery<Integer> q = sirhEntityManager.createNamedQuery("isJourHoliday", Integer.class);
		q.setParameter("date", date);

		return (q.getResultList().size() != 0);
	}

	@Override
	public void mergeEntity(Object entity) {
		sirhEntityManager.merge(entity);
	}
}
