package nc.noumea.mairie.ptg.repository;

import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;

public interface IEtatPayeurRepository {
	
	List<EtatPayeur> getListEditionEtatPayeur(AgentStatutEnum statut);
	
	EtatPayeur getEtatPayeurById(Integer idEtatPayeur);
}
