package nc.noumea.mairie.ptg.service;

import java.util.Date;

public interface IReportingService {

	byte[] getFichePointageReportAsByteArray(int idAgent, Date date) throws Exception;
}
