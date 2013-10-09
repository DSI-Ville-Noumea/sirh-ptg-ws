package nc.noumea.mairie.ptg.service.impl;

import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;

import org.springframework.beans.factory.annotation.Autowired;

public class ExportEtatPayeurService implements IExportEtatPayeurService {

	@Autowired
	private IPaieWorkflowService paieWorkflowService;
	
	@Override
	public CanStartWorkflowPaieActionDto canStartExportEtatPayeurAction(TypeChainePaieEnum chainePaie) {
		CanStartWorkflowPaieActionDto result = new CanStartWorkflowPaieActionDto();
		result.setCanStartExportPaieAction(paieWorkflowService.canChangeStateToExportEtatPayeurStarted(chainePaie));
		return result;
	}

	public void exportEtatsPayeur() {
		
	}
}
