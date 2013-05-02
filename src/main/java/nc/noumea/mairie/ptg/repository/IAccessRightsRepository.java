package nc.noumea.mairie.ptg.repository;

import java.util.List;

import nc.noumea.mairie.ptg.domain.DroitsAgent;

public interface IAccessRightsRepository {

	List<DroitsAgent> getAgentAccessRights(int idAgent);
	List<DroitsAgent> getAllDroitsForService(String codeService);
	void removeDroitsAgent(DroitsAgent droitsAgent); 
	boolean isUserApprobator(Integer idAgent);
}
