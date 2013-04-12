package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.ptg.dto.AccessRightsDto;

public interface IAccessRightsService {

	AccessRightsDto getAgentAccessRights(Integer idAgent);
}
