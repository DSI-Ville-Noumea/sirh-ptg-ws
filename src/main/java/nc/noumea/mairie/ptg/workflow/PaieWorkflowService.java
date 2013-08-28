package nc.noumea.mairie.ptg.workflow;

import java.util.Date;

import nc.noumea.mairie.domain.SpWFEtat;
import nc.noumea.mairie.domain.SpWFPaie;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.repository.IPaieWorkflowRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaieWorkflowService implements IPaieWorkflowService {
	
	@Autowired
	private IPaieWorkflowRepository paieWorkflowRepository;
	
	@Override
	public SpWFPaie getCurrentState(TypeChainePaieEnum chainePaie) {
		return paieWorkflowRepository.readCurrentState(chainePaie);
	}
		
	@Override
	public void changeStateToExportPaieStarted(TypeChainePaieEnum chainePaie) throws WorkflowInvalidStateException {
		
		SpWFPaie state = paieWorkflowRepository.selectForUpdateState(chainePaie);

		if (canChangeStateToExportPaieStarted(state.getEtat())) {
			state.setEtat(SpWFEtat.findSpWFEtat(1));
			state.setDateMaj(new Date());
		} else {
			throw new WorkflowInvalidStateException(
					String.format("Impossible de passer à l'état [%s: %s] car l'état en cours est [%s: %s]", 
							1, SpWFEtat.findSpWFEtat(1).getLibelleEtat(),
							state.getEtat().getCodeEtat(), state.getEtat().getLibelleEtat()));
		}
		
	}

	@Override
	public boolean canChangeStateToExportPaieStarted(TypeChainePaieEnum chainePaie) {

		SpWFPaie state = getCurrentState(chainePaie);
		
		return canChangeStateToExportPaieStarted(state.getEtat());
	}
	
	protected boolean canChangeStateToExportPaieStarted(SpWFEtat currentState) {

		switch (currentState.getCodeEtat()) {
			case 0: // PRET
			case 2: // Ecriture pointage terminée
			case 4: // Calcul salaire terminé
			case 6: // Journal terminé
				return true;
			default:
				return false;
		}
	}
}
