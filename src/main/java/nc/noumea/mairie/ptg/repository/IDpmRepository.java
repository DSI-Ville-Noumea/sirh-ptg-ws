package nc.noumea.mairie.ptg.repository;

import java.util.List;

import nc.noumea.mairie.ptg.domain.DpmIndemAnnee;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;

public interface IDpmRepository {

	/**
	 * Persist un objet
	 * @param obj Object
	 */
	void persistEntity(Object obj);
	
	/**
	 * Retourne une liste de DpmIndemChoixAgent selon les parametres
	 * 
	 * @param listIdsAgent List<Integer>
	 * @param annee Integer 
	 * @param isChoixIndemnite Boolean
	 * @param isChoixRecuperation Boolean
	 * @return List<DpmIndemChoixAgent>
	 */
	List<DpmIndemChoixAgent> getListDpmIndemChoixAgent(List<Integer> listIdsAgent, Integer annee, Boolean isChoixIndemnite, Boolean isChoixRecuperation);
	
	/**
	 * Retourne le choix d un agent pour une annee
	 * 
	 * @param idAgent Integer
	 * @param annee Integer
	 * @return DpmIndemChoixAgent
	 */
	DpmIndemChoixAgent getDpmIndemChoixAgentByAgentAndAnnee(Integer idAgent, Integer annee);

	/**
	 * Retourne la liste des DpmIndemAnnee
	 * @return List<DpmIndemAnnee>
	 */
	List<DpmIndemAnnee> getListDpmIndemAnnee();

	/**
	 * Retourne une entite de type Tclass en parametre
	 * via son ID en parametre
	 * 
	 * @param Tclass Class<T>
	 * @param Id 
	 * @return <T> T un objet
	 */
	<T> T getEntity(Class<T> Tclass, Object Id);

	/**
	 * Retourne la liste des DpmIndemAnnee par annee
	 * @param annee Integer
	 * @return List<DpmIndemAnnee>
	 */
	DpmIndemAnnee getDpmIndemAnneeByAnnee(Integer annee);

	/**
	 * Retourne la liste des DpmIndemAnnee ouverte
	 * @return List<DpmIndemAnnee>
	 */
	List<DpmIndemAnnee> getListDpmIndemAnneeOuverte();

	/**
	 * Supprime un objet
	 * 
	 * @param obj Object
	 */
	void removeEntity(Object obj);

	/**
	 * Retourne le choix d un agent pour une annee
	 * 
	 * @param idAgent Integer
	 * @param annee Integer
	 * @return DpmIndemChoixAgent
	 */
	DpmIndemChoixAgent getDpmIndemChoixAgent(Integer idAgent, Integer annee);
}
