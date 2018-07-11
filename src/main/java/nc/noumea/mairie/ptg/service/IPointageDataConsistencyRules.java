package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;

import org.joda.time.DateTime;

public interface IPointageDataConsistencyRules {

	void processDataConsistency(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages, boolean isFromSIRH);

	ReturnMessageDto checkMaxAbsenceHebdo(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr, BaseHorairePointageDto baseDto);

	ReturnMessageDto checkAgentINAAndHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr, BaseHorairePointageDto baseDto);

	ReturnMessageDto checkAgentTempsPartielAndHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr, BaseHorairePointageDto baseDto, boolean isFromSIRH);

	ReturnMessageDto checkAgentInactivity(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, AgentGeneriqueDto ag);

	ReturnMessageDto checkPrime7650(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkPrime7651(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkPrime7652(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkPrime7704(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkPrimeHsup7714(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkDateLundiNotSuperieurDateJour(ReturnMessageDto srm, Date dateLundi);
	
	ReturnMessageDto checkDateNotSuperieurDateJour(ReturnMessageDto srm, Date date, String errorMessage);

	DateTime getDateDebut(Integer dateDeb, Integer codem1);

	DateTime getDateFin(Integer dateFin, Integer codem2);

	ReturnMessageDto checkHeureFinSaisieHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr);

	/**
	 * #19828
	 * On check si une absence (quelque soit son type) ne chevauche pas un pointage.
	 * 
	 * @param srm ReturnMessageDto
	 * @param idAgent Integer l ID de l agent
	 * @param pointages List<Pointage> la liste des pointages a verifier
	 * @return ReturnMessageDto Retourne message d erreur ou info
	 */
	ReturnMessageDto checkAbsences(ReturnMessageDto srm, Integer idAgent,
			List<Pointage> pointages);

	ReturnMessageDto checkDateLundiAnterieurA3MoisWithPointage(ReturnMessageDto result, Date dateLundi, Pointage ptg);
}
