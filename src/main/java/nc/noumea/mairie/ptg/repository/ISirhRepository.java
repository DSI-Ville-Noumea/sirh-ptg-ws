package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spcong;
import nc.noumea.mairie.domain.Sprirc;
import nc.noumea.mairie.sirh.domain.Agent;

public interface ISirhRepository {

	Agent getAgent(int idAgent);

	Spcarr getAgentCurrentCarriere(Agent aAgent, Date asOfDate);
	Spcarr getAgentCurrentCarriere(Integer noMatr, Date asOfDate);

	List<Integer> getPrimePointagesByAgent(Integer idAgent, Date date);

	Spadmn getAgentCurrentPosition(Agent ag, Date asOfDate);

	List<Sprirc> getListRecuperationBetween(Integer idAgent, Date start, Date end);

	List<Spcong> getListCongeBetween(Integer idAgent, Date start, Date end);

	List<Spabsen> getListMaladieBetween(Integer idAgent, Date start, Date end);

	boolean isJourHoliday(Date date);
	
	List<Integer> getAllAgentIdsByStatus(AgentStatutEnum statut);
	
	List<Integer> getAllAgentsIdsByStatusAndBetween(AgentStatutEnum statut, Integer from, Integer to);
	
	void mergeEntity(Object entity);
}
