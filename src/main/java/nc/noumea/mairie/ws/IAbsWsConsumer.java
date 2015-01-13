package nc.noumea.mairie.ws;

import java.util.Date;

import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IAbsWsConsumer {

	void addRecuperationsToAgent(Integer idAgent, Date dateLundi, Integer minutes);

	void addReposCompToAgent(Integer idAgent, Date dateLundi, Integer minutes);

	ReturnMessageDto checkRecuperation(Integer idAgent, Date dateDebut, Date dateFin);

	ReturnMessageDto checkReposComp(Integer idAgent, Date dateDebut, Date dateFin);
}
