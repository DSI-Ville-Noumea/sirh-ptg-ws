package nc.noumea.mairie.ptg.repository;

import nc.noumea.mairie.domain.SpWFEtat;
import nc.noumea.mairie.domain.SpWFPaie;
import nc.noumea.mairie.domain.TypeChainePaieEnum;

public interface IPaieWorkflowRepository {

	/**
	 * Get the state of the workflow for the given code CDETAT
	 * @param codeEtat
	 * @return
	 */
	SpWFEtat getEtat(Integer codeEtat);
	
	/**
	 * Reads the current state of the workflow for the given Chaine Paie (SHC, SCV)
	 * @param chainePaie
	 * @return
	 */
	SpWFPaie readCurrentState(TypeChainePaieEnum chainePaie);
	
	/**
	 * Reads and simultaneously locks for update (PESSIMISTIC_WRITE) the state of the
	 * workflow for the given chainePaie
	 * @param chainePaie
	 * @return
	 */
	SpWFPaie selectForUpdateState(TypeChainePaieEnum chainePaie);
}
