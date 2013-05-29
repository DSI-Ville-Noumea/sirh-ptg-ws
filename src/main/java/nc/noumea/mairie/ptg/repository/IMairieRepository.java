package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spcong;
import nc.noumea.mairie.domain.Sprirc;
import nc.noumea.mairie.sirh.domain.Agent;

public interface IMairieRepository {

	Agent getAgent(int idAgent);
	Spcarr getAgentCurrentCarriere(Agent aAgent, Date asOfDate);
	List<Integer> getPrimePointagesByAgent(Integer idAgent, Date date);
	
	List<Sprirc> getListRecuperationBetween(Integer idAgent, Date start, Date end);
	List<Spcong> getListCongeBetween(Integer idAgent, Date start, Date end);
}
