package nc.noumea.mairie.ptg.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.dto.etatsPayeur.ListEtatsPayeurDto;
import nc.noumea.mairie.ptg.repository.IEtatPayeurRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.service.IEtatPayeurService;
import nc.noumea.mairie.sirh.domain.Agent;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class EtatPayeurService implements IEtatPayeurService {

	private Logger logger = LoggerFactory.getLogger(EtatPayeurService.class);
	
	@Autowired
	@Qualifier("sirhFileEtatPayeurPath")
	private String sirhFileEtatPayeurPath;
	
	@Autowired
	private IEtatPayeurRepository etatPayeurRepository;
	
	@Autowired
	private ISirhRepository sirhRepository;

	@Override
	public List<ListEtatsPayeurDto> getListEtatsPayeurByStatut(
			AgentStatutEnum statutAgent) {
		
		logger.debug("getListEtatsPayeurByStatut with statutAgent {}" + statutAgent);

		List<ListEtatsPayeurDto> listEtatsPayeurDto = new ArrayList<ListEtatsPayeurDto>();
		ListEtatsPayeurDto etatsPayeurDto = null;

		List<EtatPayeur> listEtatsPayeur = etatPayeurRepository
				.getListEditionEtatPayeur(statutAgent);
		
		Agent agent = null;
		
		if (null != listEtatsPayeur) {
			for (EtatPayeur etatPayeur : listEtatsPayeur) {
				
				agent = new Agent();
				agent = sirhRepository.getAgent(etatPayeur.getIdAgent());
				
				etatsPayeurDto = new ListEtatsPayeurDto(
						etatPayeur.getIdEtatPayeur(), etatPayeur.getStatut().toString(),
						etatPayeur.getType().getIdRefTypePointage(), etatPayeur.getDateEtatPayeur(),
						etatPayeur.getLabel(), etatPayeur.getFichier(), etatPayeur.getIdAgent(), etatPayeur.getDateEdition(), 
						agent.getDisplayNom(), agent.getDisplayPrenom());
				
				listEtatsPayeurDto.add(etatsPayeurDto);
			}
		}

		return listEtatsPayeurDto;
	}
	
	@Override
	public Pair<String, String> getPathFichierEtatPayeur(Integer idEtatPayeur) throws Exception {
		
		logger.debug("downloadFichierEtatPayeur with idEtatPayeur {}", idEtatPayeur);
		
		EtatPayeur etatPayeur = etatPayeurRepository.getEtatPayeurById(idEtatPayeur);
		
		// on verifie que les repertoires existent
		verifieRepertoire(sirhFileEtatPayeurPath);
	    
		return Pair.of(sirhFileEtatPayeurPath, etatPayeur.getFichier());
	}
	
	private void verifieRepertoire(String fileEtatPayeurPath) throws Exception {
		// on verifie que le repertoire source existe
		File dossierParent = new File(fileEtatPayeurPath);
		if (!dossierParent.exists()) {
			throw new Exception("Le repertoire de stockage " + fileEtatPayeurPath + " n'existe pas");
		}
	}
	
	
}
