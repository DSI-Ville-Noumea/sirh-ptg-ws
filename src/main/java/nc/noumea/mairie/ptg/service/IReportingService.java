package nc.noumea.mairie.ptg.service;

import java.util.Date;

public interface IReportingService {

	byte[] getFichesPointageReportAsByteArray(String csvIdAgents, Date date) throws Exception;
}
