package nc.noumea.mairie.titreRepas.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPrestataire;
import nc.noumea.mairie.ptg.domain.TitreRepasExportEtatPayeurData;
import nc.noumea.mairie.ptg.domain.TitreRepasExportEtatPayeurTask;

public interface ITitreRepasRepository {

	List<TitreRepasEtatPayeur> getListTitreRepasEtatPayeur();

	TitreRepasEtatPayeur getTitreRepasEtatPayeurByMonth(Date mois);

	void persistEtatPayeur(TitreRepasEtatPayeur titreRepasDemande);

	void persistEtatPrestataire(TitreRepasEtatPrestataire titreRepasDemande);

	List<TitreRepasDemande> getListTitreRepasDemande(List<Integer> listIdsAgent, Date fromDate, Date toDate, Integer etat, Boolean commande,
			Date dateMonth);

	void persist(TitreRepasDemande titreRepasDemande);

	TitreRepasDemande getTitreRepasDemandeById(Integer idTrDemande);

	List<Date> getListeMoisTitreRepasSaisie();

	TitreRepasEtatPrestataire getEtatPrestataireByMonth(Date dateEtatPayeur);

	TitreRepasExportEtatPayeurTask getTitreRepasEtatPayeurTaskByMonthAndStatus(Date dateDebutMois, String string);

	void persisTitreRepasExportEtatPayeurTask(TitreRepasExportEtatPayeurTask task);

	List<TitreRepasExportEtatPayeurTask> getListTitreRepasTaskErreur();

	void persisTitreRepasExportEtatPayeurData(List<TitreRepasExportEtatPayeurData> data);

	List<TitreRepasExportEtatPayeurData> getTitreRepasEtatPayeurDataByTask(Integer idTitreRepasExportEtatsPayeurTask);

}
