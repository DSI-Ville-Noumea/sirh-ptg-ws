package nc.noumea.mairie.ptg.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;

@Service
public class SpadmnService implements ISpadmnService {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	transient EntityManager sirhEntityManager;

	@Override
	public boolean estPAActive(Integer nomatr, Integer dateDeb) {
		String sql = "select count(pa.nomatr) from mairie.spadmn pa inner join mairie.spposa po on pa.cdpadm=po.cdpadm where pa.nomatr=:nomatr and pa.datdeb <=:dateJour and ( pa.datfin =0 or pa.datfin>=:dateJour) and po.posit!='FS'";
		Query query = sirhEntityManager.createNativeQuery(sql);
		query.setParameter("nomatr", nomatr);
		query.setParameter("dateJour", dateDeb);
		Integer nbRes = (Integer) query.getSingleResult();
		return nbRes > 0;
	}
	
}
