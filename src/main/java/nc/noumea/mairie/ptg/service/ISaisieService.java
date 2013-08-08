package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface ISaisieService {
	
	public ReturnMessageDto saveFichePointage(Integer idAgentOperator, FichePointageDto fichePointageDto);
	
}
