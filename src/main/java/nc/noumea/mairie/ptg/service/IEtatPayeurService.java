package nc.noumea.mairie.ptg.service;

import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.dto.etatsPayeur.ListEtatsPayeurDto;

public interface IEtatPayeurService {
	
	 List<ListEtatsPayeurDto> getListEtatsPayeurByStatut(AgentStatutEnum statutAgent);
	
	 byte[] downloadFichierEtatPayeur(String nomFichier) throws Exception;
	 
	 EtatPayeur getEtatPayeurByIdEtatPayeur(Integer idEtatPayeur);
	
}
