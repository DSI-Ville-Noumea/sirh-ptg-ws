package nc.noumea.mairie.ptg.repository;

import java.util.List;

import nc.noumea.mairie.ptg.domain.DpmIndemAnnee;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;

public interface IDpmRepository {

	void persisEntity(Object obj);
	
	List<DpmIndemChoixAgent> getListDpmIndemChoixAgent(List<Integer> listIdsAgent, Integer annee);
	
	DpmIndemChoixAgent getDpmIndemChoixAgentByAgentAndAnnee(Integer idAgent, Integer annee);
	
	DpmIndemAnnee getDpmIndemAnneeCourant();

	List<DpmIndemAnnee> getListDpmIndemAnnee();

	<T> T getEntity(Class<T> Tclass, Object Id);

	DpmIndemAnnee getDpmIndemAnneeByAnnee(Integer annee);
}
