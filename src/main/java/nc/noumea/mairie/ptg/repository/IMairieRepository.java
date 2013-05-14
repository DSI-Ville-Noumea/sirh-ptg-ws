package nc.noumea.mairie.ptg.repository;

import java.util.Date;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.sirh.domain.Agent;

public interface IMairieRepository {

	Spcarr getAgentCurrentCarriere(Agent aAgent, Date asOfDate);
	
}
