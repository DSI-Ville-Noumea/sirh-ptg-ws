package nc.noumea.mairie.ptg.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spcong;
import nc.noumea.mairie.domain.Sprirc;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;

public interface ISirhRepository {

	Spcarr getAgentCurrentCarriere(AgentGeneriqueDto aAgent, Date asOfDate);

	Spcarr getAgentCurrentCarriere(Integer noMatr, Date asOfDate);

	Spadmn getAgentCurrentPosition(AgentGeneriqueDto ag, Date asOfDate);

	List<Sprirc> getListRecuperationBetween(Integer idAgent, Date start, Date end);

	List<Spcong> getListCongeBetween(Integer idAgent, Date start, Date end);

	List<Spabsen> getListMaladieBetween(Integer idAgent, Date start, Date end);

	void mergeEntity(Object entity);
}
