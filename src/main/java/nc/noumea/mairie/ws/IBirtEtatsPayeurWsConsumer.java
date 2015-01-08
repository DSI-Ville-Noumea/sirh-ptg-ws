package nc.noumea.mairie.ws;

public interface IBirtEtatsPayeurWsConsumer {

	byte[] getFichesEtatsPayeurByStatutAsByteArray(String statut) throws Exception;

	void downloadEtatPayeurByStatut(String statut, String fileName) throws Exception;
}
