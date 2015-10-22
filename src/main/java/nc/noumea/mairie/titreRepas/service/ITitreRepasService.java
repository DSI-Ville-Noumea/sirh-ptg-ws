package nc.noumea.mairie.titreRepas.service;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.sirh.dto.AffectationDto;
import nc.noumea.mairie.sirh.dto.JourDto;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasDemandeDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasEtatPayeurDto;

public interface ITitreRepasService {

	/**
	 * Enregistre une liste de demande de Titre Repas.
	 * 
	 * @param listTitreRepasDemandeDto List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto enregistreListTitreDemande(List<TitreRepasDemandeDto> listTitreRepasDemandeDto);
	
	/**
	 * Enregistre une demande de Titre Repas pour l'agent lui-meme
	 * 
	 * @param titreRepasDemandeDto TitreRepasDemandeDto
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto enregistreTitreDemandeAgent(TitreRepasDemandeDto titreRepasDemandeDto);
	
	/**
	 * Retourne une liste de demande de Titre Repas.
	 * A utiliser pour l'agent lui meme, pour l operateur 
	 * et pour SIRH.
	 * 
	 * @param idAgentConnecte Integer
	 * @param listIdsAgent List<Integer>
	 * @param fromDate Date
	 * @param toDate Date
	 * @param etat Integer
	 * @param commande Boolean
	 * @param dateMonth Date
	 * @return List<TitreRepasDemandeDto>
	 */
	List<TitreRepasDemandeDto> getListTitreRepasDemandeDto(Integer idAgentConnecte, List<Integer> listIdsAgent, Date fromDate, Date toDate,
			Integer etat, boolean commande, Date dateMonth);
	
	/**
	 * Retourne la liste des Etat Payeur de Titre Repas
	 * 
	 * @return List<TitreRepasEtatPayeurDto>
	 */
	List<TitreRepasEtatPayeurDto> getListTitreRepasEtatPayeurDto();
	
	/**
	 * Mets a jour l etat d une iste de demande de Titre Repas.
	 * Si on veut mettre à jour une seule demande, envoyer une seule demande dans la liste
	 * 
	 * @param idAgentConnecte Integer
	 * @param listTitreRepasDemandeDto List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto updateEtatForListTitreRepasDemande(Integer idAgentConnecte, List<TitreRepasDemandeDto> listTitreRepasDemandeDto);
	
	/**
	 * RG :
     * - saisie possible entre le 1 et le 10 de chaque mois pour le mois en cours
     * - possible si l'agent a au - 1 jour de présence sur le mois précédent : en activité (PA) + pas en absence : message d'erreur au clic
     * - exclure les agents qui ont au moins une prime panier saisie sur le mois précédent 
     * - exclure les agents de la filière incendie (dans le grade générique de la carrière m-1) 
     * (si 2 carrières à cheval sur le mois m-1, on prend la dernière saisie) 
	 *
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto checkDroitATitreRepas(ReturnMessageDto rmd,
			Integer idAgent, Date dateMonthEnCours,
			List<DemandeDto> listAbences,
			RefTypeSaisiCongeAnnuelDto baseCongeAgent,
			List<JourDto> listJoursFeries, AffectationDto affectation);

	/**
	 * On verifie si l agent a le droit au prime panier
	 * ou fait parti de la filiere Incendie.
	 * 
	 * Si oui, il n a pas le droit au Titre Repas.
	 * 
	 * @param rmd ReturnMessageDto
	 * @param idAgent Integer
	 * @param affectation AffectationDto
	 * @param dateMonthEnCours Date
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto checkPrimePanierEtFiliereIncendie(ReturnMessageDto rmd,
			Integer idAgent, AffectationDto affectation, Date dateMonthEnCours);
}
