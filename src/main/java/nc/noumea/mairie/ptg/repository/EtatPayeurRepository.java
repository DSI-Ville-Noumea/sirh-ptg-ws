package nc.noumea.mairie.ptg.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;

import org.springframework.stereotype.Repository;

@Repository
public class EtatPayeurRepository implements IEtatPayeurRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
    private EntityManager ptgEntityManager;

	@Override
	public List<EtatPayeur> getListEditionEtatPayeur(AgentStatutEnum statut) {

		TypedQuery<EtatPayeur> query = ptgEntityManager.createNamedQuery("getListEditionsEtatPayeurByStatut", EtatPayeur.class);
        query.setParameter("statut", statut);
        return query.getResultList();
	}
	
	@Override
	public EtatPayeur getEtatPayeurById(Integer idEtatPayeur) {

		TypedQuery<EtatPayeur> query = ptgEntityManager.createNamedQuery("getEtatPayeurById", EtatPayeur.class);
        query.setParameter("idEtatPayeur", idEtatPayeur);
        return query.getSingleResult();
	}
    
}
