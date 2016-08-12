package nc.noumea.mairie.alfresco.cmis;

import nc.noumea.mairie.ptg.TypeEtatPayeurPointageEnum;


public interface IAlfrescoCMISService {

	/**
	 * Upload un document vers Alfresco selon les paramètres passes
	 * 
	 * @param idAgentOperateur Integer Id de l Agent qui fait l operation 
	 * @param bFile byte[] data du fichier
	 * @param titreFile String Titre du fichier
	 * @param descriptionFile String Description du fichier
	 * @param typeEtatPayeur Type : valeur possible 
	 *  - AlfrescoCMISService.TYPE_ETAT_PAYEUR_POINTAGE
	 *  - AlfrescoCMISService.TYPE_ETAT_PAYEUR_TITRE_REPAS
	 */
	void uploadDocument(Integer idAgentOperateur, byte[] bFile,
			String titreFile, String descriptionFile, TypeEtatPayeurPointageEnum typeEtatPayeur);

	/**
	 * Retourne le nodeRef d un fichier Alfresco (Etat Payeur Pointage ou Titre Repas)
	 * 
	 * @param titreFile String Titre du fichier
	 * @param typeEtatPayeur Type : valeur possible 
	 *  - AlfrescoCMISService.TYPE_ETAT_PAYEUR_POINTAGE
	 *  - AlfrescoCMISService.TYPE_ETAT_PAYEUR_TITRE_REPAS
	 * @return String la reference du node alfresco du fichier
	 */
	String getNodeRefFromPathOfFile(String titreFile, TypeEtatPayeurPointageEnum typeEtatPayeur);

}
