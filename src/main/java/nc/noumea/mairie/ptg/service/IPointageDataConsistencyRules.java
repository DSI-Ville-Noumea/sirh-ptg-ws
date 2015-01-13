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

	void processDataConsistency(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkMaxAbsenceHebdo(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr, BaseHorairePointageDto baseDto);

	ReturnMessageDto checkRecuperation(ReturnMessageDto srm, Integer idAgent, Date dateLundi);

	ReturnMessageDto checkReposComp(ReturnMessageDto srm, Integer idAgent, Date dateLundi);

	ReturnMessageDto checkSpcongConge(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkSpabsenMaladie(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkAgentINAAndHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr, BaseHorairePointageDto baseDto);

	ReturnMessageDto checkAgentTempsPartielAndHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr, BaseHorairePointageDto baseDto);

	ReturnMessageDto checkAgentInactivity(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, AgentGeneriqueDto ag);

	ReturnMessageDto checkPrime7650(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkPrime7651(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkPrime7652(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkPrime7704(ReturnMessageDto srm, Integer idAgent, Date dateLundi, List<Pointage> pointages);

	ReturnMessageDto checkDateLundiAnterieurA3Mois(ReturnMessageDto srm, Date dateLundi);

	DateTime getDateDebut(Integer dateDeb, Integer codem1);

	DateTime getDateFin(Integer dateFin, Integer codem2);

	ReturnMessageDto checkHeureFinSaisieHSup(ReturnMessageDto srm, Integer idAgent, Date dateLundi,
			List<Pointage> pointages, Spcarr carr);
}
