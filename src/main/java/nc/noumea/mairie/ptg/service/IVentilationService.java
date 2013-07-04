package nc.noumea.mairie.ptg.service;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;

public interface IVentilationService {

	void processVentilation(Integer fromAgentId, Integer toAgentId, Date ventilationDate, TypeChainePaieEnum typeChainePaie, RefTypePointageEnum pointageType);
	void processVentilationForAgent(VentilDate ventilDate, Integer idAgent, Date from, Date to, RefTypePointageEnum pointageType);
}
