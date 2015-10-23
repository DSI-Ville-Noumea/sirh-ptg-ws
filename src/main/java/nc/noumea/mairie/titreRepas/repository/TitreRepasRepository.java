package nc.noumea.mairie.titreRepas.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
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
	public TitreRepasDemande getTitreRepasDemandeById(
			Integer idTrDemande) {
		
		return ptgEntityManager.find(TitreRepasDemande.class, idTrDemande);
	}

	@Override
	public List<TitreRepasDemande> getListTitreRepasDemande(
			List<Integer> listIdsAgent, Date fromDate, Date toDate,
			Integer etat, Boolean commande, Date dateMonth) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("select tr from TitreRepasDemande tr ");
		sb.append("LEFT JOIN FETCH tr.etats et0 ");
		sb.append("where 1=1 ");
		
		if (null != listIdsAgent) {
			sb.append("and tr.idAgent in :listIdsAgent ");
		}

		if (null != fromDate) {
			sb.append("and tr.dateMonth >= :fromDate ");
		}
		if (null != toDate) {
			sb.append("and tr.dateMonth <= :toDate ");
		}
		if (null != etat) {
			sb.append("and et0 in ( select max(et) from TitreRepasEtatDemande et group by et.titreRepasDemande ) ");
			sb.append("and et0.etat = :etat ");
		}
		if (null != commande) {
			sb.append("and tr.commande <= :commande ");
		}
		if (null != dateMonth) {
			sb.append("and tr.dateMonth = :dateMonth ");
		}

		sb.append("order by tr.dateMonth desc, tr.idAgent asc ");

		TypedQuery<TitreRepasDemande> query = ptgEntityManager.createQuery(sb.toString(), TitreRepasDemande.class);
		
		if (null != listIdsAgent) {
			query.setParameter("listIdsAgent", listIdsAgent);
		}
		if (null != fromDate) {
			query.setParameter("fromDate", fromDate);
		}
		if (null != toDate) {
			query.setParameter("toDate", toDate);
		}
		if (null != etat) {
			query.setParameter("etat", EtatPointageEnum.getEtatPointageEnum(etat));
		}
		if (null != commande) {
			query.setParameter("commande", commande);
		}
		if (null != dateMonth) {
			query.setParameter("dateMonth", dateMonth);
		}

		return query.getResultList();
	}

	@Override
	public void persist(TitreRepasDemande titreRepasDemande) {
		// TODO Auto-generated method stub

	}

}
