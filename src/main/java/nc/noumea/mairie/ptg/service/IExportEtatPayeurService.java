package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;

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
	
}
