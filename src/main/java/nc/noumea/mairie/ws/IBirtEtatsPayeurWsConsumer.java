package nc.noumea.mairie.ws;

import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;


public interface IBirtEtatsPayeurWsConsumer {

	byte[] getFichesEtatsPayeurByStatutAsByteArray(Integer typeFicheEtatPayeur, String statut) throws Exception;
	void downloadEtatPayeurByStatut(RefTypePointageEnum typeEtat, String statut, String fileName) throws Exception;
}
