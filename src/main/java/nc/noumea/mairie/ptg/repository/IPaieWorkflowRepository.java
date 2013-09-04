package nc.noumea.mairie.ptg.repository;

import nc.noumea.mairie.domain.SpWFEtat;
import nc.noumea.mairie.domain.SpWFPaie;
import nc.noumea.mairie.domain.TypeChainePaieEnum;

public interface IPaieWorkflowRepository {

	SpWFEtat getEtat(Integer codeEtat);
	SpWFPaie readCurrentState(TypeChainePaieEnum chainePaie);
	SpWFPaie selectForUpdateState(TypeChainePaieEnum chainePaie);
}
