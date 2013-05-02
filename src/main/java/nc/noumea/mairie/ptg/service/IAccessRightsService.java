package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;

public interface IAccessRightsService {

	AccessRightsDto getAgentAccessRights(Integer idAgent);
	DelegatorAndOperatorsDto getDelegatorAndOperators(Integer idAgent);
	List<DroitsAgent> setDelegatorAndOperators(Integer idAgent, DelegatorAndOperatorsDto dto);

	boolean canAccessAccessRights(Integer idAgent);
}
