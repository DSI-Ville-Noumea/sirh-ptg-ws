package nc.noumea.mairie.ptg.repository;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.ReposCompHisto;
import nc.noumea.mairie.ptg.domain.ReposCompTask;

public interface IReposCompRepository {

	ReposCompTask getReposCompTask(Integer idReposCompTask);
	Integer countTotalHSupsSinceStartOfYear(Integer idAgent, Integer currentYear);
	ReposCompHisto findReposCompHistoForAgentAndDate(Integer idAgent, Date dateLundi);
}
