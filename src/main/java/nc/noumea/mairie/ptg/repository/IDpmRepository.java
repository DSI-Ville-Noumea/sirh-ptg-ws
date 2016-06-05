package nc.noumea.mairie.ptg.repository;

import java.util.List;

import nc.noumea.mairie.ptg.domain.DpmIndemAnnee;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;

public interface IDpmRepository {

	void persisEntity(Object obj);
	
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
	
	DpmIndemChoixAgent getDpmIndemChoixAgentByAgentAndAnnee(Integer idAgent, Integer annee);

	List<DpmIndemAnnee> getListDpmIndemAnnee();

	<T> T getEntity(Class<T> Tclass, Object Id);

	DpmIndemAnnee getDpmIndemAnneeByAnnee(Integer annee);

	List<DpmIndemAnnee> getListDpmIndemAnneeOuverte();
}
