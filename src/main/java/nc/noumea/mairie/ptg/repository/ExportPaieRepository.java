package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.Sppact;
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
}
