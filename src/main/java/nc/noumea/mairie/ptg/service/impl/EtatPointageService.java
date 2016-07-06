package nc.noumea.mairie.ptg.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IEtatPointageService;

@Service
public class EtatPointageService implements IEtatPointageService {

	@Autowired
	private IPointageRepository pointageRepository;
	
	public ReturnMessageDto majEtatPointagesByListId(Integer idEtatPointage, EtatPointageEnum etat){
		
		ReturnMessageDto result = new ReturnMessageDto();
		
		// on recupere EtatPointage
		EtatPointage ep = pointageRepository.getEntity(EtatPointage.class, idEtatPointage);
		
		if(null == ep){
			result.getErrors().add(String.format("L EtatPointage %s n'existe pas.", idEtatPointage));
			return result;
		}
		
		Date dateJour = new Date();
		
		EtatPointage epNew = new EtatPointage();
		epNew.setDateEtat(dateJour);
		epNew.setDateMaj(dateJour);
		epNew.setEtat(etat);
		epNew.setIdAgent(ep.getIdAgent());
		epNew.setPointage(ep.getPointage());
		
		// insert nouvelle ligne EtatPointage avec nouvel etat 
		pointageRepository.persisEntity(epNew);
		
		return result;
	}
}
