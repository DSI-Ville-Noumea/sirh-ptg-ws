package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IEtatPointageService {

	ReturnMessageDto majEtatPointagesByListId(Integer IdEtatPointage, EtatPointageEnum etat);
}
