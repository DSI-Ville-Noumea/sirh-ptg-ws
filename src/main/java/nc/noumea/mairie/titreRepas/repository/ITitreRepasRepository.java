package nc.noumea.mairie.titreRepas.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;

public interface ITitreRepasRepository {

	List<TitreRepasEtatPayeur> getListTitreRepasEtatPayeur();
	
	TitreRepasEtatPayeur getTitreRepasEtatPayeurById(Integer idTitreRepasEtatPayeur);
	
	void persist(TitreRepasEtatPayeur titreRepasDemande);
	
	List<TitreRepasDemande> getListTitreRepasDemande(List<Integer> listIdsAgent, Date fromDate, Date toDate, Integer etat, Boolean commande, Date dateMonth);
	
	void persist(TitreRepasDemande titreRepasDemande);

	TitreRepasDemande getTitreRepasDemandeById(Integer idTrDemande);

	List<Date> getListeMoisTitreRepasSaisie();
	
}
