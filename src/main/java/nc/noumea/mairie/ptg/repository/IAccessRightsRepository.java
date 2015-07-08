package nc.noumea.mairie.ptg.repository;

import java.util.List;

import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;

public interface IAccessRightsRepository {

	void removeEntity(Object obj);

	List<Droit> getAgentAccessRights(int idAgent);

	Droit getDroitApprobateurByAgent(Integer idAgent);

	boolean isUserApprobator(Integer idAgent);

	boolean isUserApprobatorOrDelegataire(Integer idAgent);

	boolean isUserOperator(Integer idAgent);

	boolean isUserApprobatorOrOperatorOrDelegataire(Integer idAgent);

	List<Droit> getAgentsApprobateurs();

	Droit getAgentDroitApprobateurOrOperateurFetchAgents(Integer idAgent, Integer idDroitApprobateur);

	Droit getApprobateurFetchOperateurs(Integer idAgentApprobateur);

	Droit getApprobateur(Integer idAgentApprobateur);

	void persisEntity(Object obj);

	List<DroitsAgent> getListOfAgentsToInputOrApprove(Integer idAgent);

	List<DroitsAgent> getListOfAgentsToInputOrApprove(Integer idAgent, String codeService);

	List<DroitsAgent> getListOfAgentsToInput(Integer idApprobateur, Integer pIdAgent);

	List<DroitsAgent> getListOfAgentsToApprove(Integer idAgent, String pCodeService);
}
