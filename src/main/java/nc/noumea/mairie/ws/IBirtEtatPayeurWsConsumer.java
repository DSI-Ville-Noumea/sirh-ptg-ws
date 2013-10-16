package nc.noumea.mairie.ws;


public interface IBirtEtatPayeurWsConsumer {

	byte[] getFichesEtatsPayeurByStatutAsByteArray(Integer typeFicheEtatPayeur, String statut) throws Exception;
}
