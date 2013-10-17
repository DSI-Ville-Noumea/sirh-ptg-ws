package nc.noumea.mairie.ptg.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.dto.etatsPayeur.ListEtatsPayeurDto;
import nc.noumea.mairie.ptg.repository.IEtatPayeurRepository;
import nc.noumea.mairie.ptg.service.IEtatPayeurService;

import org.apache.commons.io.IOUtils;
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

	@Override
	public List<ListEtatsPayeurDto> getListEtatsPayeurByStatut(
			AgentStatutEnum statutAgent) {
		
		logger.debug("getListEtatsPayeurByStatut with statutAgent {}" + statutAgent);

		List<ListEtatsPayeurDto> listEtatsPayeurDto = new ArrayList<ListEtatsPayeurDto>();
		ListEtatsPayeurDto etatsPayeurDto = null;

		List<EtatPayeur> listEtatsPayeur = etatPayeurRepository
				.getListEditionEtatPayeur(statutAgent);

		if (null != listEtatsPayeur) {
			for (EtatPayeur etatPayeur : listEtatsPayeur) {
				etatsPayeurDto = new ListEtatsPayeurDto(
						etatPayeur.getIdEtatPayeur(), etatPayeur.getStatut().toString(),
						etatPayeur.getType().getIdRefTypePointage(), etatPayeur.getDateEtatPayeur(),
						etatPayeur.getLabel(), etatPayeur.getFichier());
				
				listEtatsPayeurDto.add(etatsPayeurDto);
			}
		}

		return listEtatsPayeurDto;
	}
	
	@Override
	public EtatPayeur getEtatPayeurByIdEtatPayeur(Integer idEtatPayeur) {
		
		logger.debug("getEtatPayeurByIdEtatPayeur with idEtatPayeur {}" + idEtatPayeur);
		
		return etatPayeurRepository.getEtatPayeurById(idEtatPayeur);
	}
	
	@Override
	public byte[] downloadFichierEtatPayeur(String nomFichier) throws Exception {
		
		// on verifie que les repertoires existent
		verifieRepertoire(sirhFileEtatPayeurPath);
	    
		byte[] byteBuffer = null;
		File newFile = new File(sirhFileEtatPayeurPath + "/" + nomFichier);
		FileInputStream in = new FileInputStream(newFile);
		
		try {
			byteBuffer = IOUtils.toByteArray(in);
		} finally {
			in.close();
		}
		
		return byteBuffer;
	}
	
	private void verifieRepertoire(String fileEtatPayeurPath) throws Exception {
		// on verifie que le repertoire source existe
		File dossierParent = new File(fileEtatPayeurPath);
		if (!dossierParent.exists()) {
			throw new Exception("Le repertoire de stockage " + fileEtatPayeurPath + "n'existe pas");
		}
	}
	
	
}
