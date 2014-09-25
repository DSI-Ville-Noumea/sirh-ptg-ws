package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.domain.Spphre;
import nc.noumea.mairie.domain.Sppprm;
import nc.noumea.mairie.domain.Spprim;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ExportPaieRepository implements IExportPaieRepository {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager mairieEntityManager;
	
	@Autowired
	private HelperService helperService;
	
	@Override
	public Sppact getSppactForDayAndAgent(Integer idAgent, Date day, String codeActi) {

		String jpql = "from Sppact a where a.id.nomatr = :nomatr and a.id.dateJour = :dateJour and a.id.activite.codeActvite = :codeActi";
		TypedQuery<Sppact> q = mairieEntityManager.createQuery(jpql, Sppact.class);
		q.setParameter("nomatr", helperService.getMairieMatrFromIdAgent(idAgent));
		q.setParameter("dateJour", helperService.getIntegerDateMairieFromDate(day));
		q.setParameter("codeActi", codeActi);
		
		List<Sppact> result = q.getResultList();
		
		if (result.size() == 0)
			return null;
		
		return result.get(0);
	}
	
	@Override
	public int deleteSppactForDayAndAgent(Integer idAgent, Date day, String codeActi) {

		String jpql = "delete Sppact a where a.id.nomatr = :nomatr and a.id.dateJour = :dateJour and a.id.activite.codeActvite = :codeActi";
		Query q = mairieEntityManager.createQuery(jpql);
		q.setParameter("nomatr", helperService.getMairieMatrFromIdAgent(idAgent));
		q.setParameter("dateJour", helperService.getIntegerDateMairieFromDate(day));
		q.setParameter("codeActi", codeActi);
		
		return q.executeUpdate();
	}
	
	@Override
	public Spphre getSpphreForDayAndAgent(Integer idAgent, Date day) {

		String jpql = "from Spphre h where h.id.nomatr = :nomatr and h.id.datJour = :datJour";
		TypedQuery<Spphre> q = mairieEntityManager.createQuery(jpql, Spphre.class);
		q.setParameter("nomatr", helperService.getMairieMatrFromIdAgent(idAgent));
		q.setParameter("datJour", helperService.getIntegerDateMairieFromDate(day));
		
		List<Spphre> result = q.getResultList();
		
		if (result.size() == 0)
			return null;
		
		return result.get(0);
	}

	@Override
	public Sppprm getSppprmForDayAgentAndNorubr(Integer idAgent, Date day, Integer noRubr) {

		String jpql = "from Sppprm p where p.id.nomatr = :nomatr and p.id.datJour = :datJour and p.id.noRubr = :noRubr";
		TypedQuery<Sppprm> q = mairieEntityManager.createQuery(jpql, Sppprm.class);
		q.setParameter("nomatr", helperService.getMairieMatrFromIdAgent(idAgent));
		q.setParameter("datJour", helperService.getIntegerDateMairieFromDate(day));
		q.setParameter("noRubr", noRubr);
		
		List<Sppprm> result = q.getResultList();
		
		if (result.size() == 0)
			return null;
		
		return result.get(0);
	}
	
	@Override
	public Spprim getSpprimForDayAgentAndNorubr(Integer idAgent, Date day, Integer noRubr) {

		String jpql = "from Spprim p where p.id.nomatr = :nomatr and p.id.dateDebut = :dateDebut and p.id.noRubr = :noRubr";
		TypedQuery<Spprim> q = mairieEntityManager.createQuery(jpql, Spprim.class);
		q.setParameter("nomatr", helperService.getMairieMatrFromIdAgent(idAgent));
		q.setParameter("dateDebut", helperService.getIntegerDateMairieFromDate(day));
		q.setParameter("noRubr", noRubr);
		
		List<Spprim> result = q.getResultList();
		
		if (result.size() == 0)
			return null;
		
		return result.get(0);
	}

	@Override
	public void removeEntity(Object entity) {
		mairieEntityManager.remove(entity);
	}
}
