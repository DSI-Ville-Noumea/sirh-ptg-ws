package nc.noumea.mairie.ptg.service;

import java.util.Date;

import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.sirh.domain.Agent;

public interface IPointageService {

	public FichePointageDto getFichePointageForAgent(Agent agent, Date date);
	public FichePointageDto getFilledFichePointageForAgent(Agent agent, Date dateLundi);

}
