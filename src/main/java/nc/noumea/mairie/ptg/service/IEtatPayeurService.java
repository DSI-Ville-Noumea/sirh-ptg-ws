package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.dto.etatsPayeur.ListEtatsPayeurDto;

import org.apache.commons.lang3.tuple.Pair;

public interface IEtatPayeurService {
	
	 List<ListEtatsPayeurDto> getListEtatsPayeurByStatut(AgentStatutEnum statutAgent);
	
	 Pair<String, String> getPathFichierEtatPayeur(Integer idEtatPayeur) throws Exception;
	
}
