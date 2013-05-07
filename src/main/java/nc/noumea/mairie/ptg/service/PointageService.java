package nc.noumea.mairie.ptg.service;

import java.util.Date;

import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.PrimePointage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageService implements IPointageService {

	@Autowired
	private IPointageRepository pointageRepository;

	@Override
	public FichePointageDto getFichePointageForAgent(Agent agent, Date date) {
		FichePointageDto result = new FichePointageDto();

		JourPointageDto jourPointageTemplate = new JourPointageDto();
		for (PrimePointage pp : pointageRepository.getPrimePointagesByAgent(agent.getIdAgent(), date)) {
			
			PrimeDto prime = new PrimeDto(pp);
			//...
			
			jourPointageTemplate.getPrimes().add(prime);
		}

		
		// tu as un jour de la semaine type avec toutes les primes
		
		for (int jour = 1; jour < 7; jour++) {
			JourPointageDto jourSuivant = new JourPointageDto(jourPointageTemplate);
//			jourSuivant.setDate(jourPointageTemplate.getDate()+ jour)
			result.getSaisies().add(jourSuivant);
		}

		return result;
	}
}
