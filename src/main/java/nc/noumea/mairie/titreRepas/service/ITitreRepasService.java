package nc.noumea.mairie.titreRepas.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.web.AccessForbiddenException;
import nc.noumea.mairie.sirh.dto.AffectationDto;
import nc.noumea.mairie.sirh.dto.JourDto;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasDemandeDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasEtatPayeurDto;

public interface ITitreRepasService {

	/**
	 * Enregistre une liste de demande de Titre Repas depuis le Kiosque RH.
	 * 
	 * @param listTitreRepasDemandeDto
	 *            List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto enregistreListTitreDemandeFromKiosque(Integer idAgentConnecte, List<TitreRepasDemandeDto> listTitreRepasDemandeDto);

	/**
	 * Enregistre une liste de demande de Titre Repas depuis SIRH.
	 * 
	 * @param listTitreRepasDemandeDto
	 *            List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto enregistreListTitreDemandeFromSIRH(Integer idAgentConnecte, List<TitreRepasDemandeDto> listTitreRepasDemandeDto);

	/**
	 * Retourne une liste de demande de Titre Repas. A utiliser pour l'agent lui
	 * meme, pour l operateur et pour SIRH.
	 * 
	 * @param idAgentConnecte
	 *            Integer
	 * @param listIdsAgent
	 *            List<Integer>
	 * @param fromDate
	 *            Date
	 * @param toDate
	 *            Date
	 * @param etat
	 *            Integer
	 * @param commande
	 *            Boolean
	 * @param dateMonth
	 *            Date
	 * @param listIdsAgent
	 *            List<Integer>
	 * @return List<TitreRepasDemandeDto>
	 */
	List<TitreRepasDemandeDto> getListTitreRepasDemandeDto(
			Integer idAgentConnecte, Date fromDate, Date toDate, Integer etat,
			Boolean commande, Date dateMonth, Integer idServiceADS,
			Integer idAgent, List<Integer> listIdsAgent, Boolean isFromSIRH)
			throws AccessForbiddenException;

	/**
	 * Retourne la liste des Etat Payeur de Titre Repas
	 * 
	 * @return List<TitreRepasEtatPayeurDto>
	 */
	List<TitreRepasEtatPayeurDto> getListTitreRepasEtatPayeurDto();

	/**
	 * Mets a jour l etat d une liste de demande de Titre Repas. Si on veut
	 * mettre à jour une seule demande, envoyer une seule demande dans la liste
	 * 
	 * @param idAgentConnecte
	 *            Integer
	 * @param listTitreRepasDemandeDto
	 *            List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto updateEtatForListTitreRepasDemande(Integer idAgentConnecte, List<TitreRepasDemandeDto> listTitreRepasDemandeDto);

	/**
	 * RG : - saisie possible entre le 1 et le 10 de chaque mois pour le mois en
	 * cours - possible si l'agent a au - 1 jour de présence sur le mois
	 * précédent : en activité (PA) + pas en absence : message d'erreur au clic
	 * - exclure les agents qui ont au moins une prime panier saisie sur le mois
	 * précédent - exclure les agents de la filière incendie (dans le grade
	 * générique de la carrière m-1) (si 2 carrières à cheval sur le mois m-1,
	 * on prend la dernière saisie)
	 *
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto checkDroitATitreRepas(ReturnMessageDto rmd, Integer idAgent, Date dateMonthEnCours, List<DemandeDto> listAbsences, RefTypeSaisiCongeAnnuelDto baseCongeAgent,
			List<JourDto> listJoursFeries, AffectationDto affectation);

	/**
	 * On verifie si l agent a le droit au prime panier ou fait parti de la
	 * filiere Incendie.
	 * 
	 * Si oui, il n a pas le droit au Titre Repas.
	 * 
	 * @param idAgent
	 *            Integer
	 * @return boolean
	 */
	boolean checkPrimePanierEtFiliereIncendie(Integer idAgent);

	/**
	 * Retourne la liste des états possible pour une demande de Titre Repas.
	 * 
	 * @return List<RefEtatDto>
	 */
	List<RefEtatDto> getListRefEtats();

	/**
	 * Genere l etat du payeur des Titres Repas.
	 * 
	 * @param idAgentConnecte
	 *            Integer
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto genereEtatPayeur(Integer idAgentConnecte);

	/**
	 * Retourne l'historique d'une demande de titre repas
	 * 
	 * @param idTrDemande
	 *            Integer
	 * 
	 * @return List<TitreRepasDemandeDto>
	 */
	List<TitreRepasDemandeDto> getTitreRepasArchives(Integer idTrDemande);

	List<Date> getListeMoisTitreRepasSaisie();
}
