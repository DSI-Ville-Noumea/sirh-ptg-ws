package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;

public interface IVentilationService {

	void processVentilation(Integer idAgent, Integer fromAgentId, Integer toAgentId, Date ventilationDate, TypeChainePaieEnum typeChainePaie, RefTypePointageEnum pointageType);
	List<Pointage> processVentilationForAgent(VentilDate ventilDate, Integer idAgent, Date from, Date to, RefTypePointageEnum pointageType);
}
