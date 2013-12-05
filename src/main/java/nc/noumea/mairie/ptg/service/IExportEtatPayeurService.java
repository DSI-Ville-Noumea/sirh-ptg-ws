package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.workflow.WorkflowInvalidStateException;

public interface IExportEtatPayeurService {

	/**
	 * Returns whether or not it is possible to start an ExportEtatPayeur process (if the Paie 
	 * workflow is ready for this action).
	 * @param chainePaie
	 * @return
	 */
	CanStartWorkflowPaieActionDto canStartExportEtatPayeurAction(TypeChainePaieEnum chainePaie);

	/**
	 * Retrieves the Absences data for Etat Payeur report based on the Statut given
	 * as parameter. This method relies on a Ventilation being still open and not paid.
	 * The aggregation is done by Week.
	 * @param statut
	 * @return The list of Absences formatted for a reporting engine
	 */
	EtatPayeurDto getAbsencesEtatPayeurDataForStatut(AgentStatutEnum statut);
	
	/**
	 * Retrieves the HeuresSup data for Etat Payeur report based on the Statut given
	 * as parameter. This method relies on a Ventilation being still open and not paid.
	 * The aggregation is done by Week.
	 * @param statut
	 * @return The list of Heures Sup formatted for a reporting engine
	 */
	EtatPayeurDto getHeuresSupEtatPayeurDataForStatut(AgentStatutEnum statut);
	
	/**
	 * Retrieves the Primes data for Etat Payeur report based on the Statut given
	 * as parameter. This method relies on a Ventilation being still open and not paid.
	 * The aggregation is done by Month.
	 * @param statut
	 * @return The list of Primes formatted for a reporting engine
	 */
	EtatPayeurDto getPrimesEtatPayeurDataForStatut(AgentStatutEnum statut);
	
	/**
	 * Starts the exportation of Etats Payeurs for a given Agent Statut
	 * @param agentIdExporting
	 * @param statut
	 * @return
	 */
	ReturnMessageDto startExportEtatsPayeur(Integer agentIdExporting, AgentStatutEnum statut);
	
	/**
	 * Process exporting Etats payeurs of a ChainePaie depending on given Statut as parameter
	 * This process also create records for each report being generated and copied to the storagePath
	 * At the end, the process updates the Paie workflow status to "Export Etats Payeur Done".
	 * @param agentIdExporting
	 * @param statut
	 * @throws WorkflowInvalidStateException
	 */
	//void exportEtatsPayeur(Integer agentIdExporting, AgentStatutEnum statut) throws WorkflowInvalidStateException;
	
	/**
	 * Starts the process of Exporting Etats Payeurs of a ChainePaie. This will download all chainePaie
	 * related reports and store them in a shared directory. It will also call SIRH-ABS-WS to update
	 * each agent's recuperations with what has been paid.
	 * Called by SIRH-JOBS
	 * @param idExportEtatsPayeurTask
	 */
	void exportEtatsPayeur(Integer idExportEtatsPayeurTask);
	
	/**
	 * Terminates the process of Exporting Etats Payeurs of a ChainePaie. This will mark all related pointages
	 * as JOURNALISE, set the VentilDate as PAYE
	 * Called by SIRH-JOBS
	 * @param idExportEtatsPayeurTask
	 * @throws WorkflowInvalidStateException
	 */
	void journalizeEtatsPayeur(Integer idExportEtatsPayeurTask) throws WorkflowInvalidStateException;
	
	/**
	 * Terminates the process of Exporting Etats Payeurs of a ChainePaie. This will 
	 * set the Paie Workflow state to EXPORT_ETATS_PAYEUR_TERMINE
	 * Called by SIRH-JOBS
	 */
	void stopExportEtatsPayeur(TypeChainePaieEnum typeChainePaie) throws WorkflowInvalidStateException;
}
