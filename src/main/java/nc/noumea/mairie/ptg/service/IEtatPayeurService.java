package nc.noumea.mairie.ptg.service;


public interface IEtatPayeurService {

	byte[] getFichesEtatsPayeurByStatutAsByteArray(Integer typeFicheEtatPayeur, String statut) throws Exception;
}
