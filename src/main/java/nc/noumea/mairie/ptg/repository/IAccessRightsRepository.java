package nc.noumea.mairie.ptg.repository;

import java.util.List;

import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;

public interface IAccessRightsRepository {

	List<Droit> getAgentAccessRights(int idAgent);

	void removeDroitsAgent(DroitsAgent droitsAgent);

	boolean isUserApprobator(Integer idAgent);

	boolean isUserOperator(Integer idAgent);

	List<Droit> getAgentsApprobateurs();

	Droit getAgentDroitApprobateurOrOperateur(Integer idAgent);
	
	void persisEntity(Object obj);
}
