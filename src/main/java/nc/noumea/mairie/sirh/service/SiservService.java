package nc.noumea.mairie.sirh.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.Siserv;
import nc.noumea.mairie.ptg.service.AccessRightsServiceException;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.sirh.domain.FichePoste;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SiservService implements ISiservService {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;
	
	@Autowired
	private HelperService helperService;
	
	@Override
	public Siserv getAgentService(int idAgent) {

		TypedQuery<FichePoste> q = sirhEntityManager.createNamedQuery("getCurrentAffectation", FichePoste.class);
		q.setParameter("idAgent", idAgent);
		q.setParameter("today", helperService.getCurrentDate());
		List<FichePoste> fichePostes = q.getResultList();
		
		if (fichePostes.size() != 1)
			throw new AccessRightsServiceException(String.format("L'agent donné a 0 ou plus d'une affectation courante [%s].", idAgent));
		
		String codeService = fichePostes.get(0).getCodeService();
		
		Siserv siserv = sirhEntityManager.find(Siserv.class, codeService);
		
		if (siserv == null)
			throw new AccessRightsServiceException(String.format("Impossible de trouver le service de l'agent [%s].", codeService));
		
		return siserv;
	}
}
