package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.Pointage;

public interface IPointageDataConsistencyRules {

	List<String> checkSprircRecuperation(Integer idAgent, Date dateLundi, List<Pointage> pointages);
	List<String> checkSpcongConge(Integer idAgent, Date dateLundi, List<Pointage> pointages);
}
