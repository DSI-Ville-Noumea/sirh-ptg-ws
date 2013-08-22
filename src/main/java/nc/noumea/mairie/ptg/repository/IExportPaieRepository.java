package nc.noumea.mairie.ptg.repository;

import java.util.Date;

import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.domain.Spphre;

public interface IExportPaieRepository {

	Sppact getSppactForDayAndAgent(Integer idAgent, Date day, String codeActi);
	Spphre getSpphreForDayAndAgent(Integer idAgent, Date day);
}
