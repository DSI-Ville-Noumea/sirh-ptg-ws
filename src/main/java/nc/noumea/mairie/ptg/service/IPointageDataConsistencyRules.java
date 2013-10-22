package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IPointageDataConsistencyRules {

	void processDataConsistency(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	
	ReturnMessageDto checkMaxAbsenceHebdo(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	ReturnMessageDto checkSprircRecuperation(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	ReturnMessageDto checkSpcongConge(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	ReturnMessageDto checkSpabsenMaladie(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	ReturnMessageDto checkAgentINAAndHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	ReturnMessageDto checkAgentInactivity(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	ReturnMessageDto checkPrime7650(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	ReturnMessageDto checkPrime7651(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	ReturnMessageDto checkPrime7652(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	ReturnMessageDto checkPrime7704(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);
}
