package nc.noumea.mairie.ptg.repository;

import java.util.List;

import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;

public interface IAccessRightsRepository {

	List<Droit> getAgentAccessRights(int idAgent);

	void removeDroitsAgent(DroitsAgent droitsAgent);

	boolean isUserApprobator(Integer idAgent);

	boolean isUserApprobatorOrDelegataire(Integer idAgent);
	
	boolean isUserOperator(Integer idAgent);

	boolean isUserApprobatorOrOperatorOrDelegataire(Integer idAgent);
	 
	List<Droit> getAgentsApprobateurs();

	List<Droit> getAgentsOperateurs();

	Droit getAgentDroitApprobateurOrOperateurFetchAgents(Integer idAgent);

	Droit getApprobateurFetchOperateurs(Integer idAgentApprobateur);
	
	void persisEntity(Object obj);

	List<DroitsAgent> getListOfAgentsToInputOrApprove(Integer idAgent);
	
	List<DroitsAgent> getListOfAgentsToInputOrApprove(Integer idAgent, String codeService);
	
	/**
	 * Returns the Id Agent of the Approbateur of a given Agent
	 * @param idAgent
	 * @return
	 */
	Integer getAgentsApprobateur(Integer idAgent);
}
