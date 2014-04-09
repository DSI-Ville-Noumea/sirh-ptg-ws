package nc.noumea.mairie.ptg.workflow;

import nc.noumea.mairie.domain.SpWFEtat;
import nc.noumea.mairie.domain.SpWFPaie;
import nc.noumea.mairie.domain.TypeChainePaieEnum;

public interface IPaieWorkflowService {

	SpWFPaie getCurrentState(TypeChainePaieEnum chainePaie);

	boolean canChangeStateToExportPaieStarted(TypeChainePaieEnum chainePaie);

	boolean canChangeStateToExportEtatsPayeurStarted(TypeChainePaieEnum chainePaie);

	void changeStateToExportPaieStarted(TypeChainePaieEnum chainePaie) throws WorkflowInvalidStateException;

	void changeStateToExportEtatsPayeurStarted(TypeChainePaieEnum chainePaie) throws WorkflowInvalidStateException;

	void changeStateToExportPaieDone(TypeChainePaieEnum chainePaie) throws WorkflowInvalidStateException;

	void changeStateToExportEtatsPayeurDone(TypeChainePaieEnum chainePaie) throws WorkflowInvalidStateException;

	boolean canStartVentilation(TypeChainePaieEnum chainePaie);

	boolean canChangeStateToVentilationStarted(SpWFEtat currentState);
}
