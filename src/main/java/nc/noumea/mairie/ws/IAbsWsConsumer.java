package nc.noumea.mairie.ws;

import java.util.Date;

public interface IAbsWsConsumer {

	void addRecuperationsToAgent(Integer idAgent, Date dateLundi, Integer minutes);
	
	void addReposCompToAgent(Integer idAgent, Date dateLundi, Integer minutes);
}
