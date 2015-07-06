package nc.noumea.mairie.ptg.workflow;

import nc.noumea.mairie.domain.SpWFEtat;
import nc.noumea.mairie.domain.SpWFPaie;
import nc.noumea.mairie.domain.SpWfEtatEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.repository.IPaieWorkflowRepository;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaieWorkflowService implements IPaieWorkflowService {

	@Autowired
	private IPaieWorkflowRepository paieWorkflowRepository;

	@Autowired
	private HelperService helperService;

	@Override
	public SpWFPaie getCurrentState(TypeChainePaieEnum chainePaie) {
		return paieWorkflowRepository.readCurrentState(chainePaie);
	}

	@Override
	public void changeStateToExportPaieStarted(TypeChainePaieEnum chainePaie) throws WorkflowInvalidStateException {

		SpWFPaie state = paieWorkflowRepository.readCurrentState(chainePaie);

		if (canChangeStateToExportPaieStarted(state.getEtat())) {
			state.setEtat(paieWorkflowRepository.getEtat(SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS));
			state.setDateMaj(helperService.getCurrentDate());
		} else {
			throw new WorkflowInvalidStateException(String.format(
					"Impossible de passer à l'état [%s] car l'état en cours est [%s]",
					SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS, state.getEtat().getCodeEtat()));
		}

	}

	@Override
	public boolean canChangeStateToExportPaieStarted(TypeChainePaieEnum chainePaie) {

		SpWFPaie state = getCurrentState(chainePaie);

		return canChangeStateToExportPaieStarted(state.getEtat());
	}

	protected boolean canChangeStateToExportPaieStarted(SpWFEtat currentState) {

		switch (currentState.getCodeEtat()) {
			case PRET: // PRET
			case ECRITURE_POINTAGES_TERMINEE: // Ecriture pointage terminée
			case CALCUL_SALAIRE_TERMINE: // Calcul salaire terminé
				return true;
			default:
				return false;
		}
	}

	protected boolean canChangeStateToExportPaieDone(SpWFEtat currentState) {

		return currentState.getCodeEtat() == SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS;
	}

	@Override
	public void changeStateToExportPaieDone(TypeChainePaieEnum chainePaie) throws WorkflowInvalidStateException {

		SpWFPaie state = paieWorkflowRepository.readCurrentState(chainePaie);

		if (canChangeStateToExportPaieDone(state.getEtat())) {
			state.setEtat(paieWorkflowRepository.getEtat(SpWfEtatEnum.ECRITURE_POINTAGES_TERMINEE));
			state.setDateMaj(helperService.getCurrentDate());
		} else {
			throw new WorkflowInvalidStateException(String.format(
					"Impossible de passer à l'état [%s] car l'état en cours est [%s]",
					SpWfEtatEnum.ECRITURE_POINTAGES_TERMINEE, state.getEtat().getCodeEtat()));
		}

	}

	@Override
	public boolean canChangeStateToExportEtatsPayeurStarted(TypeChainePaieEnum chainePaie) {

		SpWFPaie state = getCurrentState(chainePaie);

		return canChangeStateToExportEtatsPayeurStarted(state.getEtat());
	}

	protected boolean canChangeStateToExportEtatsPayeurStarted(SpWFEtat currentState) {

		return currentState.getCodeEtat() == SpWfEtatEnum.CALCUL_SALAIRE_TERMINE;
	}

	protected boolean canChangeStateToExportEtatsPayeurDone(SpWFEtat currentState) {

		return currentState.getCodeEtat() == SpWfEtatEnum.ETATS_PAYEUR_EN_COURS;
	}

	@Override
	public void changeStateToExportEtatsPayeurStarted(TypeChainePaieEnum chainePaie)
			throws WorkflowInvalidStateException {

		SpWFPaie state = paieWorkflowRepository.readCurrentState(chainePaie);

		if (canChangeStateToExportEtatsPayeurStarted(state.getEtat())) {
			state.setEtat(paieWorkflowRepository.getEtat(SpWfEtatEnum.ETATS_PAYEUR_EN_COURS));
			state.setDateMaj(helperService.getCurrentDate());
		} else {
			throw new WorkflowInvalidStateException(String.format(
					"Impossible de passer à l'état [%s] car l'état en cours est [%s]",
					SpWfEtatEnum.ETATS_PAYEUR_EN_COURS, state.getEtat().getCodeEtat()));
		}

	}

	@Override
	public void changeStateToExportEtatsPayeurDone(TypeChainePaieEnum chainePaie) throws WorkflowInvalidStateException {

		SpWFPaie state = paieWorkflowRepository.readCurrentState(chainePaie);

		if (canChangeStateToExportEtatsPayeurDone(state.getEtat())) {
			state.setEtat(paieWorkflowRepository.getEtat(SpWfEtatEnum.ETATS_PAYEUR_TERMINES));
			state.setDateMaj(helperService.getCurrentDate());
		} else {
			throw new WorkflowInvalidStateException(String.format(
					"Impossible de passer à l'état [%s] car l'état en cours est [%s]",
					SpWfEtatEnum.ETATS_PAYEUR_TERMINES, state.getEtat().getCodeEtat()));
		}

	}

	@Override
	public boolean canStartVentilation(TypeChainePaieEnum chainePaie) {

		SpWFPaie state = getCurrentState(chainePaie);

		return canChangeStateToVentilationStarted(state.getEtat());
	}

	@Override
	public boolean canChangeStateToVentilationStarted(SpWFEtat currentState) {

		switch (currentState.getCodeEtat()) {
			case PRET: // PRET
			case ECRITURE_POINTAGES_TERMINEE: // Ecriture pointage terminée
			case CALCUL_SALAIRE_TERMINE: // Calcul salaire terminé
				return true;
			default:
				return false;
		}
	}
}
