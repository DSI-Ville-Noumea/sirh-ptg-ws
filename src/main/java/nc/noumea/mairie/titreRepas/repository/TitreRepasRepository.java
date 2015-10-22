package nc.noumea.mairie.titreRepas.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;

import org.springframework.stereotype.Repository;

@Repository
public class TitreRepasRepository implements ITitreRepasRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;
	
	@Override
	public List<TitreRepasEtatPayeur> getListTitreRepasEtatPayeur() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TitreRepasEtatPayeur getTitreRepasEtatPayeurById(
			Integer idTitreRepasEtatPayeur) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persist(TitreRepasEtatPayeur titreRepasDemande) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<TitreRepasDemande> getListTitreRepasDemande(
			List<Integer> listIdsAgent, Date fromDate, Date toDate,
			Integer etat, boolean commande, Date dateMonth) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persist(TitreRepasDemande titreRepasDemande) {
		// TODO Auto-generated method stub

	}

}
