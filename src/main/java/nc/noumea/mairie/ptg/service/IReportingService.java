package nc.noumea.mairie.ptg.service;

public interface IReportingService {

	byte[] getFichePointageReportAsByteArray(int idAgent, String date) throws Exception;
}
