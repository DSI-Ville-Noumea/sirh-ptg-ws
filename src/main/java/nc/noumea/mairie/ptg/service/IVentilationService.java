package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IVentilationService {

	/**
	 * This service method is responsible for taking a request for starting a ventilation batch
	 * This batch info will be stored in the database for SIRH-JOBS to access them and start the processes
	 * It is also responsible for creating the VentilDate record if not already existing ;
	 * filtering the agents who are eligible to ventilation (do they match the given AgentStatutEnum).
	 * It then stores the list of idAgents, AgentStatutEnum, RefTypePointageEnum, fromVentilDate and toVentilDate.
	 * 
	 * This method does NOT run the ventilation process itself.
	 * @param idAgent
	 * @param agents
	 * @param ventilationDate
	 * @param statut
	 * @param pointageType
	 * @return
	 */
	ReturnMessageDto startVentilation(Integer idAgent, List<Integer> agents, Date ventilationDate, AgentStatutEnum statut, RefTypePointageEnum pointageType);

    ReturnMessageDto processVentilation(Integer idAgent, List<Integer> agents, Date ventilationDate, AgentStatutEnum statut, RefTypePointageEnum pointageType);

    List showVentilation(Integer idDateVentil, List<Integer> agents, RefTypePointageEnum pointageType);
}
