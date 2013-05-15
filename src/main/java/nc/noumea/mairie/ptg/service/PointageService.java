package nc.noumea.mairie.ptg.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.PrimePointage;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageService implements IPointageService {

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private IMairieRepository mairieRepository;
	
	@Autowired
	private ISirhWSConsumer sirhWSConsumer;
	
	@Autowired
	private HelperService helperService;

	@Override
	public FichePointageDto getFichePointageForAgent(Agent agent, Date date) {

		// Retrieve division service of agent
		ServiceDto service = sirhWSConsumer.getAgentDirection(agent.getIdAgent());
		
		// on construit le dto de l'agent
		AgentDto agentDto = new AgentDto(agent);
		agentDto.setCodeService(service.getService());
		agentDto.setService(service.getServiceLibelle());
		
		// on recherche sa carriere pour avoir son statut (Fonctionnaire, contractuel,convention coll
		Spcarr carr = mairieRepository.getAgentCurrentCarriere(agent, helperService.getCurrentDate());
		agentDto.setStatut(carr.getStatutCarriere().name());
		
		// on construit le DTO de jourPointage
		FichePointageDto result = new FichePointageDto();
		result.setAgent(agentDto);
		result.setSemaine("semaine test");

		JourPointageDto jourPointageTemplate = new JourPointageDto();
		jourPointageTemplate.setDate(date);
		List<PrimePointage> pps = pointageRepository.getPrimePointagesByAgent(agent.getIdAgent(), date);
		
		List<Integer> rubriques = new ArrayList<Integer>();
		for (PrimePointage pp : pps)
			rubriques.add(pp.getNumRubrique());
		List<RefPrime> refPrimes = pointageRepository.getRefPrimes(rubriques, carr.getStatutCarriere());
		
		for (RefPrime prime : refPrimes) {
			jourPointageTemplate.getPrimes().add(new PrimeDto(prime));
		}
		
		result.getSaisies().add(jourPointageTemplate);

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
	
	@Override
	public FichePointageDto getFilledFichePointageForAgent(Agent agent, Date dateLundi) {
		return null;
	}
}
