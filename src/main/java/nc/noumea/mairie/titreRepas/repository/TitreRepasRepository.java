package nc.noumea.mairie.titreRepas.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPrestataire;
import nc.noumea.mairie.ptg.domain.TitreRepasExportEtatPayeurData;
import nc.noumea.mairie.ptg.domain.TitreRepasExportEtatPayeurTask;

@Repository
public class TitreRepasRepository implements ITitreRepasRepository {

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;

	@Override
	public void persistEtatPayeur(TitreRepasEtatPayeur titreRepasDemande) {
		ptgEntityManager.persist(titreRepasDemande);
	}

	@Override
	public void persistEtatPrestataire(TitreRepasEtatPrestataire titreRepasDemande) {
		ptgEntityManager.persist(titreRepasDemande);
	}

	@Override
	public void persist(TitreRepasDemande titreRepasDemande) {
		ptgEntityManager.persist(titreRepasDemande);
	}

	@Override
	public List<TitreRepasEtatPayeur> getListTitreRepasEtatPayeur() {

		TypedQuery<TitreRepasEtatPayeur> query = ptgEntityManager.createNamedQuery("getListEditionsTitreRepasEtatPayeur", TitreRepasEtatPayeur.class);
		return query.getResultList();
	}

	@Override
	public TitreRepasDemande getTitreRepasDemandeById(Integer idTrDemande) {

		return ptgEntityManager.find(TitreRepasDemande.class, idTrDemande);
	}

	@Override
	public List<TitreRepasDemande> getListTitreRepasDemande(List<Integer> listIdsAgent, Date fromDate, Date toDate, Integer etat, Boolean commande,
			Date dateMonth) {

		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(tr) from TitreRepasDemande tr ");
		sb.append("LEFT JOIN FETCH tr.etats et0 ");
		sb.append("where 1=1 ");

		if (null != listIdsAgent && !listIdsAgent.isEmpty()) {
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
			sb.append("and tr.commande = :commande ");
		}
		if (null != dateMonth) {
			sb.append("and tr.dateMonth = :dateMonth ");
		}

		sb.append("order by tr.dateMonth desc, tr.idAgent asc ");

		TypedQuery<TitreRepasDemande> query = ptgEntityManager.createQuery(sb.toString(), TitreRepasDemande.class);

		if (null != listIdsAgent && !listIdsAgent.isEmpty()) {
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
	public List<Date> getListeMoisTitreRepasSaisie() {

		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(c.dateMonth) from TitreRepasDemande c ");
		sb.append("order by c.dateMonth desc ");

		TypedQuery<Date> query = ptgEntityManager.createQuery(sb.toString(), Date.class);

		return query.getResultList();
	}

	@Override
	public TitreRepasEtatPayeur getTitreRepasEtatPayeurByMonth(Date mois) {

		TypedQuery<TitreRepasEtatPayeur> query = ptgEntityManager.createNamedQuery("getTitreRepasEtatPayeurByMonth", TitreRepasEtatPayeur.class);
		query.setParameter("dateMonth", mois);

		List<TitreRepasEtatPayeur> r = query.getResultList();

		return r.size() == 0 ? null : r.get(0);
	}

	@Override
	public TitreRepasEtatPrestataire getEtatPrestataireByMonth(Date dateEtatPayeur) {
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(tr) from TitreRepasEtatPrestataire tr ");
		sb.append("where tr.dateEtatPrestataire = :dateMonth ");
		sb.append("order by tr.dateEdition desc");

		TypedQuery<TitreRepasEtatPrestataire> query = ptgEntityManager.createQuery(sb.toString(), TitreRepasEtatPrestataire.class);

		query.setParameter("dateMonth", dateEtatPayeur);
		TitreRepasEtatPrestataire result = null;
		List<TitreRepasEtatPrestataire> list = query.getResultList();
		if (list.size() > 0) {
			result = list.get(0);
		}
		return result;
	}

	@Override
	public TitreRepasExportEtatPayeurTask getTitreRepasEtatPayeurTaskByMonthAndStatus(Date date, String statut) {
		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(tr) from TitreRepasExportEtatPayeurTask tr ");
		sb.append("where tr.dateMonth = :dateMonth ");
		if (statut == null) {
			sb.append("and tr.taskStatus is null ");
		} else {
			sb.append("and tr.taskStatus = :statut ");
		}

		TypedQuery<TitreRepasExportEtatPayeurTask> query = ptgEntityManager.createQuery(sb.toString(), TitreRepasExportEtatPayeurTask.class);

		query.setParameter("dateMonth", date);
		if (statut != null) {
			query.setParameter("statut", statut);
		}
		TitreRepasExportEtatPayeurTask result = null;
		List<TitreRepasExportEtatPayeurTask> list = query.getResultList();
		if (list.size() > 0) {
			result = list.get(0);
		}
		return result;
	}

	@Override
	public void persisTitreRepasExportEtatPayeurTask(TitreRepasExportEtatPayeurTask task) {
		ptgEntityManager.persist(task);
	}

	@Override
	public List<TitreRepasExportEtatPayeurTask> getListTitreRepasTaskErreur() {

		StringBuilder sb = new StringBuilder();
		sb.append("select distinct(tr) from TitreRepasExportEtatPayeurTask tr ");
		sb.append("where tr.taskStatus != :statut ");
		sb.append("and tr.taskStatus is not null ");

		TypedQuery<TitreRepasExportEtatPayeurTask> query = ptgEntityManager.createQuery(sb.toString(), TitreRepasExportEtatPayeurTask.class);

		query.setParameter("statut", "OK");

		return query.getResultList();
	}

	@Override
	public void persisTitreRepasExportEtatPayeurData(List<TitreRepasExportEtatPayeurData> listData) {
		for (TitreRepasExportEtatPayeurData data : listData) {
			ptgEntityManager.persist(data);
		}
	}

	@Override
	public List<TitreRepasExportEtatPayeurData> getTitreRepasEtatPayeurDataByTask(Integer idTitreRepasExportEtatsPayeurTask) {
		StringBuilder sb = new StringBuilder();
		sb.append("select data from TitreRepasExportEtatPayeurData data ");
		sb.append("where data.titreRepasExportEtatsPayeurTask.idTitreRepasExportEtatsPayeurTask = :id ");
		sb.append("order by data.idTitreRepasExportEtatsPayeurData");

		TypedQuery<TitreRepasExportEtatPayeurData> query = ptgEntityManager.createQuery(sb.toString(), TitreRepasExportEtatPayeurData.class);
		query.setParameter("id", idTitreRepasExportEtatsPayeurTask);

		return query.getResultList();
	}

}
