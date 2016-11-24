package nc.noumea.mairie.ptg.service;

import java.util.List;

import org.joda.time.LocalDate;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.dto.DpmIndemniteAnneeDto;
import nc.noumea.mairie.ptg.dto.DpmIndemniteChoixAgentDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

public interface IDpmService {

	/**
	 * Sauvegarde le choix de l agent.
	 * Utilise pour le Kiosque RH
	 * 
	 * @param idAgentConnecte Integer
	 * @param dto DpmIndemniteChoixAgentDto
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto saveIndemniteChoixAgentForKiosque(Integer idAgentConnecte, DpmIndemniteChoixAgentDto dto);

	/**
	 * Sauvegarde l ensemble des choix effectues par les agents affectes a l agent en parametre
	 * pour la campagne de choix pour Indemnité forfaitaire travail DPM
	 * pour l annee passee en parametre
	 * 
	 * @param idAgentConnecte Integer Agent connecte L operateur
	 * @param annee Integer L annee
	 * @param dto List<DpmIndemniteChoixAgentDto> Liste de choix
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto saveListIndemniteChoixAgentForOperator(Integer idAgentConnecte, Integer annee, List<DpmIndemniteChoixAgentDto> dto);
	
	/**
	 * Retourne l ensemble des choix effectues par les agents affectes a l agent en parametre
	 * pour la campagne de choix pour Indemnité forfaitaire travail DPM
	 * pour l annee passee en parametre
	 * 
	 * @param idAgentConnecte Integer Agent connecte L operateur
	 * @param annee Integer L annee
	 * @param idServiceAds Integer Filtre sur le service
	 * @param idAgentFiltre Integer Filtre sur un agent
	 * @return List<DpmIndemniteChoixAgentDto> La liste des choix
	 */
	List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgent(Integer idAgentConnecte, Integer annee, 
			Integer idServiceAds, Integer idAgentFiltre);
	
	/**
	 * Retourne l ensemble des campagnes de choix pour Indemnité forfaitaire travail DPM
	 * 
	 * @param idAgentConnecte Integer
	 * @return List<DpmIndemniteAnneeDto> La liste des campagnes
	 */
	List<DpmIndemniteAnneeDto> getListDpmIndemAnnee(Integer idAgentConnecte);

	/**
	 * Ajoute le parametrage d une campagne de choix pour Indemnité forfaitaire travail DPM
	 * 
	 * @param idAgentConnecte Integer
	 * @param dto DpmIndemniteAnneeDto
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto createDpmIndemAnnee(Integer idAgentConnecte, DpmIndemniteAnneeDto dto);

	/**
	 * Enregistre le parametrage d une campagne de choix pour Indemnité forfaitaire travail DPM
	 * 
	 * @param idAgentConnecte Integer
	 * @param dto DpmIndemniteAnneeDto
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto saveDpmIndemAnnee(Integer idAgentConnecte, DpmIndemniteAnneeDto dto);

	/**
	 * Retourne si l agent a le droit a la prime Indemnité forfaitaire travail DPM
	 * 
	 * @param idAgent Integer L agent
	 * @return boolean TRUE ou FALSE
	 */
	boolean isDroitAgentToIndemniteForfaitaireDPM(Integer idAgent);

	/**
	 * Retourne pour une annee si la campagne de choix est ouverte
	 * 
	 * @param annee Integer L annee
	 * @return boolean TRUE ou FALSE
	 */
	boolean isPeriodeChoixOuverte(Integer annee);

	/**
	 * Retourne le choix de l agent concernant sa prime Indemnité forfaitaire travail DPM
	 * pour une annee
	 * 
	 * @param idAgentConnecte Integer L agent
	 * @param annee Integer L annee concernee
	 * @return DpmIndemniteChoixAgentDto Le choix
	 */
	DpmIndemniteChoixAgentDto getIndemniteChoixAgent(Integer idAgentConnecte, Integer annee);
	
	/**
	 * Retourne la liste des campagnes (question/reponse) ouverte pour le choix sur la prime Indemnité forfaitaire travail DPM
	 * 
	 * @return List<DpmIndemniteAnneeDto>
	 */
	List<DpmIndemniteAnneeDto> getListDpmIndemAnneeOuverte();

	/**
	 * Retourne la campagne pour le choix sur la prime Indemnité forfaitaire travail DPM
	 * de l annee en cours
	 * 
	 * @return DpmIndemniteAnneeDto
	 */
	DpmIndemniteAnneeDto getDpmIndemAnneeEnCours();

	/**
	 * Retourne l ensemble des choix effectues par les agents selon les parametres passes
	 * 
	 * @param idAgentConnecte Integer Agent connecte L operateur
	 * @param annee Integer L annee
	 * @param isChoixIndemnite Boolean 
	 * @param isChoixRecuperation Boolean
	 * @param listIdsAgent List<Integer> Filtre sur des agents
	 * @return List<DpmIndemniteChoixAgentDto> La liste des choix
	 */
	List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgentforSIRH(Integer idAgentConnecte, Integer annee, Boolean isChoixIndemnite,
			Boolean isChoixRecuperation, List<Integer> listIdsAgent);
	
	/**
	 * Sauvegarde le choix de l agent.
	 * Utilise par SIRH
	 * 
	 * @param idAgentConnecte Integer
	 * @param dto DpmIndemniteChoixAgentDto
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto saveIndemniteChoixAgentForSIRH(Integer idAgentConnecte, DpmIndemniteChoixAgentDto dto);

	/**
	 * Supprime le choix d un agent, uniquement si la campagne est ouverte et
	 * par un utilisateur DRH
	 * 
	 * @param idAgentConnecte Integer
	 * @param idDpmIndemChoixAgent Integer ID du choix
	 * @return ReturnMessageDto
	 */
	ReturnMessageDto deleteIndemniteChoixAgentForKiosque(Integer idAgentConnecte, Integer idDpmIndemChoixAgent);

	/**
	 * Returne vrai si l agent a le droit a la prime DPM
	 * un jour donne :
	 *  - doit avoir la prime Indemnité forfaitaire travail DPM (celle du samedi ou DJF)
	 *  - doit etre un samedi, dimanche ou jour ferie
	 * 
	 * @param idAgent Integer
	 * @param date LocalDate
	 * @return boolean TRUE ou FALSE
	 */
	boolean isDroitAgentToIndemniteForfaitaireDPMForOneDay(Integer idAgent, LocalDate date);

	/**
	 * Calcul la majoration des heures supp a recuperer si l agent a la prime forfaitaire DPM.
	 * Si la prime forfaitaire DPM ne s applique pas,
	 * on applique le Rappel en Service si coche
	 * 
	 * @param ptg Pointage
	 * @return int nombre de minutes a rajouter au recup 
	 */
	int calculNombreMinutesRecupereesMajoreesToAgentForOnePointage(Pointage ptg);
}
