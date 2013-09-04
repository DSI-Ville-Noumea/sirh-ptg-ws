package nc.noumea.mairie.ptg.workflow;

import nc.noumea.mairie.domain.SpWFPaie;
import nc.noumea.mairie.domain.TypeChainePaieEnum;

public interface IPaieWorkflowService {

	SpWFPaie getCurrentState(TypeChainePaieEnum chainePaie);
	void changeStateToExportPaieStarted(TypeChainePaieEnum chainePaie) throws WorkflowInvalidStateException;
	boolean canChangeStateToExportPaieStarted(TypeChainePaieEnum chainePaie);
	void changeStateToExportPaieDone(TypeChainePaieEnum chainePaie) throws WorkflowInvalidStateException;
}
