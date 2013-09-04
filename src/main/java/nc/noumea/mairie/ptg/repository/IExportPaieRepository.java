package nc.noumea.mairie.ptg.repository;

import java.util.Date;

import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.domain.Spphre;
import nc.noumea.mairie.domain.Sppprm;
import nc.noumea.mairie.domain.Spprim;

public interface IExportPaieRepository {

	Sppact getSppactForDayAndAgent(Integer idAgent, Date day, String codeActi);
	Spphre getSpphreForDayAndAgent(Integer idAgent, Date day);
	Sppprm getSppprmForDayAgentAndNorubr(Integer idAgent, Date day, Integer noRubr);
	Spprim getSpprimForDayAgentAndNorubr(Integer idAgent, Date day, Integer noRubr);
}
