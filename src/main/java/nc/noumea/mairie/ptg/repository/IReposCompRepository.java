package nc.noumea.mairie.ptg.repository;

import nc.noumea.mairie.ptg.domain.ReposCompTask;

public interface IReposCompRepository {

	ReposCompTask getReposCompTask(Integer idReposCompTask);
	Integer countTotalHSupsSinceStartOfYear(Integer idAgent, Integer currentYear);
}
