package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndInputtersDto;

public interface IAccessRightsService {

	AccessRightsDto getAgentAccessRights(Integer idAgent);
	DelegatorAndInputtersDto getDelegatorAndInputters(Integer idAgent);
}
