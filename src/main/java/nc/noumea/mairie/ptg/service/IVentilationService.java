package nc.noumea.mairie.ptg.service;

import java.util.Date;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;

public interface IVentilationService {

	public void processVentilation(Date ventilationDate, AgentStatutEnum statut, RefTypePointageEnum pointageType);
}
