package nc.noumea.mairie.repository;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spmatr;
import nc.noumea.mairie.domain.Spperm;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;

public interface IMairieRepository {

	<T> T getEntity(Class<T> Tclass, Object Id);

	void persistEntity(Object entity);

	void removeEntity(Object obj);

	void mergeEntity(Object entity);

	Spmatr findSpmatrForAgent(Integer idAgent);

	Spcarr getAgentCurrentCarriere(AgentGeneriqueDto aAgent, Date asOfDate);

	Spcarr getAgentCurrentCarriere(Integer noMatr, Date asOfDate);

	Spadmn getAgentCurrentPosition(AgentGeneriqueDto ag, Date asOfDate);
	
	Spperm getTREtatPayeurRates(Date dateMoisSuivant);
	
	List<Spadmn> getListPAOfAgentBetween2Date(Integer noMatr, Date fromDate,
			Date toDate);

	String getDerniereFiliereOfAgentOnPeriod(Integer noMatr, Date fromDate,
			Date toDate);
}
