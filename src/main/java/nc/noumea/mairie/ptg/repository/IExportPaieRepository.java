package nc.noumea.mairie.ptg.repository;

import java.util.Date;

import nc.noumea.mairie.domain.Sppact;

public interface IExportPaieRepository {

	Sppact getSppactForDayAndAgent(Integer idAgent, Date day, String codeActi);
}
