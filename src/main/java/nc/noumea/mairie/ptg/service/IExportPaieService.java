package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.ExportPaieTask;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.workflow.WorkflowInvalidStateException;

public interface IExportPaieService {

	/**
	 * Starts the action of exporting pointages to Paie systems.
	 * This method automatically checks whether starting this action is possible (according
	 * to the Paie workflow current status).
	 * It creates ExportPaieTasks objects that will be processed by SIRH-PTG-JOB application.
	 * @param agentIdValidating
	 * @param statut
	 * @return
	 */
	ReturnMessageDto startExportToPaie(Integer agentIdValidating, AgentStatutEnum statut);
	
	/**
	 * Returns whether or not it is possible to start an ExportPaie process (if the Paie 
	 * workflow is ready for this action).
	 * @param chainePaie
	 * @return
	 */
	CanStartWorkflowPaieActionDto canStartExportPaieAction(TypeChainePaieEnum chainePaie);
	
	/**
	 * This method processes one ExportPaieTask by exporting the Pointages of the tasks' agent
	 * @param idExportPaieTask
	 */
	void processExportPaieForAgent(Integer idExportPaieTask);
	
	/**
	 * This method aims at updating the Paie Workflow when the exportPaie process is done
	 * It automatically updates the workflow status thus letting all systems know that its
	 * process of exporting pointages to the Paie is done.
	 * @param typeChainePaie
	 */
	void stopExportToPaie(TypeChainePaieEnum typeChainePaie) throws WorkflowInvalidStateException;
	
	ExportPaieTask findExportPaieTask(Integer idExportPaieTask);
}
