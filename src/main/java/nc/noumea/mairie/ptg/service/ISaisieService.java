package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.SaisieReturnMessageDto;

public interface ISaisieService {
	
	public SaisieReturnMessageDto saveFichePointage(FichePointageDto fichePointageDto);
	
}
