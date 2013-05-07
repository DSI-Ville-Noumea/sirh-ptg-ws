package nc.noumea.mairie.ptg.service;

import java.util.Calendar;
import java.util.Date;

import nc.noumea.mairie.domain.Sprubr;
import nc.noumea.mairie.ptg.dto.AgentDto;
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
		AgentDto agentDto = new AgentDto(agent);
		FichePointageDto result = new FichePointageDto();
		result.setAgent(agentDto);
		result.setSemaine("semaine test");

		JourPointageDto jourPointageTemplate = new JourPointageDto();
		for (PrimePointage pp : pointageRepository.getPrimePointagesByAgent(agent.getIdAgent(), date)) {
			Sprubr rubrique = Sprubr.findSprubr(pp.getNumRubrique());
			PrimeDto prime = new PrimeDto(pp, rubrique);

			jourPointageTemplate.getPrimes().add(prime);
		}

		// tu as un jour de la semaine type avec toutes les primes

		for (int jour = 1; jour < 7; jour++) {
			JourPointageDto jourSuivant = new JourPointageDto(jourPointageTemplate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(jourPointageTemplate.getDate());
			calendar.add(Calendar.DATE, jour);
			jourSuivant.setDate(calendar.getTime());
			result.getSaisies().add(jourSuivant);
		}

		return result;
	}
}
