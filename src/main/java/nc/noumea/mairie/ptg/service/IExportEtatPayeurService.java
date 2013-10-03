package nc.noumea.mairie.ptg.service;

import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;

public interface IExportEtatPayeurService {

	/**
	 * Returns whether or not it is possible to start an ExportEtatPayeur process (if the Paie 
	 * workflow is ready for this action).
	 * @param chainePaie
	 * @return
	 */
	CanStartWorkflowPaieActionDto canStartExportEtatPayeurAction(TypeChainePaieEnum chainePaie);
}
