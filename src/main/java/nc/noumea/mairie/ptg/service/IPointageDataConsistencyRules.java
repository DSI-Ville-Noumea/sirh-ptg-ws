package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.dto.SaisieReturnMessageDto;

public interface IPointageDataConsistencyRules {

	SaisieReturnMessageDto checkMaxAbsenceHebdo(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	SaisieReturnMessageDto checkSprircRecuperation(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	SaisieReturnMessageDto checkSpcongConge(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	SaisieReturnMessageDto checkSpabsenMaladie(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	SaisieReturnMessageDto checkAgentINAAndHSup(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	SaisieReturnMessageDto checkAgentInactivity(SaisieReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
}
