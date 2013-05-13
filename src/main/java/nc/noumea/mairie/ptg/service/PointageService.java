package nc.noumea.mairie.ptg.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Sprubr;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.PrimePointage;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointageService implements IPointageService {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	private EntityManager sirhEntityManager;

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private ISirhWSConsumer sirhWSConsumer;

	@Override
	public FichePointageDto getFichePointageForAgent(Agent agent, Date date) {

		// Retrieve division service of agent
		ServiceDto service = sirhWSConsumer.getAgentDirection(agent.getIdAgent());
		// on construit le dto de l'agent
		AgentDto agentDto = new AgentDto(agent);
		agentDto.setCodeService(service.getService());
		agentDto.setService(service.getServiceLibelle());
		// on recherche sa carriere pour avoir son statut
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		int dateFormatMairie = Integer.valueOf(sdf.format(new DateTime().toDate()));
		TypedQuery<Spcarr> qCarr = sirhEntityManager.createNamedQuery("getCurrentCarriere", Spcarr.class);
		qCarr.setParameter("nomatr", agent.getNomatr());
		qCarr.setParameter("todayFormatMairie", dateFormatMairie);
		Spcarr carr = qCarr.getSingleResult();
		agentDto.setStatut(carr.getStatutCarriere());
		// on construit le DTO de jourPointage
		FichePointageDto result = new FichePointageDto();
		result.setAgent(agentDto);
		result.setSemaine("semaine test");

		JourPointageDto jourPointageTemplate = new JourPointageDto();
		jourPointageTemplate.setDate(date);
		for (PrimePointage pp : pointageRepository.getPrimePointagesByAgent(agent.getIdAgent(), date)) {
			Sprubr rubrique = Sprubr.findSprubr(pp.getNumRubrique());
			PrimeDto prime = new PrimeDto(pp, rubrique);
			jourPointageTemplate.getPrimes().add(prime);

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
}
