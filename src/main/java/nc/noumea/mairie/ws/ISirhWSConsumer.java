package nc.noumea.mairie.ws;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.sirh.dto.AffectationDto;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;
import nc.noumea.mairie.sirh.dto.JourDto;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public interface ISirhWSConsumer {

	AgentWithServiceDto getAgentService(Integer idAgent, Date date);

	AgentGeneriqueDto getAgent(Integer idAgent);

	boolean isHoliday(LocalDate datePointage);

	boolean isHoliday(DateTime deb);

	List<Integer> getPrimePointagesByAgent(Integer idAgent, Date dateDebut, Date dateFin);

	boolean isJourFerie(DateTime deb);

	BaseHorairePointageDto getBaseHorairePointageAgent(Integer idAgent, Date dateDebut, Date dateFin);

	EntiteDto getAgentDirection(Integer idAgent, Date date);

	List<AgentWithServiceDto> getListAgentsWithService(
			List<Integer> listAgentDto, Date date);

	List<AgentGeneriqueDto> getListAgents(List<Integer> listIdsAgent);

	List<BaseHorairePointageDto> getListBaseHorairePointageAgent(
			Integer idAgent, Date dateDebut, Date dateFin);

	List<Integer> getListAgentsWithPrimeTIDOnAffectation(Date dateDebut,
			Date dateFin);

	List<AffectationDto> getListAffectationDtoBetweenTwoDateAndForListAgent(List<Integer> listIdsAgent,
			Date dateDebut, Date dateFin);

	List<JourDto> getListeJoursFeries(Date dateDebut, Date dateFin);

	ReturnMessageDto isUtilisateurSIRH(Integer idAgent);
}
