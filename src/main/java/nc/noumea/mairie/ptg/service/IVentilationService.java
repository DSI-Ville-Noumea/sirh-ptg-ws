package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.CanStartVentilationDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.VentilDateDto;
import nc.noumea.mairie.ptg.dto.VentilDto;

public interface IVentilationService {

	/**
	 * This service methods returns whether or not it is possible to start a new
	 * ventilation job. It will browse the list of VentilTask in the db and
	 * return true if no VentilTask are ongoing for the given AgentStatutEnum,
	 * and false otherwise.
	 * 
	 * @param statut
	 * @return
	 */
	CanStartVentilationDto canStartVentilationForAgentStatus(
			AgentStatutEnum statut);

	/**
	 * This service method is responsible for taking a request for starting a
	 * ventilation batch This batch info will be stored in the database for
	 * SIRH-JOBS to access them and start the processes It is also responsible
	 * for creating the VentilDate record if not already existing ; filtering
	 * the agents who are eligible to ventilation (do they match the given
	 * AgentStatutEnum). It then stores the list of idAgents, AgentStatutEnum,
	 * RefTypePointageEnum, fromVentilDate and toVentilDate.
	 * 
	 * This method does NOT run the ventilation process itself.
	 * 
	 * @param idAgent
	 * @param agents
	 * @param ventilationDate
	 * @param statut
	 * @param pointageType
	 * @return
	 */
	ReturnMessageDto startVentilation(Integer idAgent, List<Integer> agents,
			Date ventilationDate, AgentStatutEnum statut,
			RefTypePointageEnum pointageType);

	/**
	 * This service method is responsible for ventilating the pointages for a
	 * given VentilTask VentilTask stores all the information necessary to
	 * perform the task : (idAgent, statut, fromDate, toDate, typePointage) This
	 * service method is aimed at being called by the Jobs (through
	 * VentilationController.processTask).
	 * 
	 * @param idVentilTask
	 */
	void processVentilationForAgent(Integer idVentilTask);

	List<VentilDto> showVentilation(Integer idDateVentil, List<Integer> agents,
			RefTypePointageEnum pointageType);

	VentilDateDto getVentilationEnCoursForStatut(AgentStatutEnum statut);
}
