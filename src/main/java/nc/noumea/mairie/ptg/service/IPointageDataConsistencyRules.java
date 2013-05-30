package nc.noumea.mairie.ptg.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.Pointage;

public interface IPointageDataConsistencyRules {

	List<String> checkMaxAbsenceHebdo(List<String> errors, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	List<String> checkSprircRecuperation(List<String> errors, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	List<String> checkSpcongConge(List<String> errors, Integer idAgent, Date dateLundi, List<Pointage> pointages);
	List<String> checkSpabsenMaladie(List<String> errors, Integer idAgent, Date dateLundi, List<Pointage> pointages);
}
