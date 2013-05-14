package nc.noumea.mairie.ptg.repository;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.sirh.domain.Agent;

import org.springframework.stereotype.Repository;

@Repository
public class MairieRepository implements IMairieRepository {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;

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
	
}
