package nc.noumea.mairie.ws;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.SirhWsServiceDto;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public interface ISirhWSConsumer {

	SirhWsServiceDto getAgentDirection(Integer idAgent, Date date);

	List<AgentWithServiceDto> getServicesAgent(String rootService, Date date);

	List<SirhWsServiceDto> getSousServices(String rootService);

	AgentWithServiceDto getAgentService(Integer idAgent, Date date);

	AgentGeneriqueDto getAgent(Integer idAgent);

	boolean isHoliday(LocalDate datePointage);

	boolean isHoliday(DateTime deb);

	List<Integer> getPrimePointagesByAgent(Integer idAgent, Date date);

	boolean isJourFerie(DateTime deb);

	BaseHorairePointageDto getBaseHorairePointageAgent(Integer idAgent, Date date);
}
