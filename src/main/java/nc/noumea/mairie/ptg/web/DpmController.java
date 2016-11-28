package nc.noumea.mairie.ptg.web;

import java.util.List;

import nc.noumea.mairie.ptg.dto.DpmIndemniteAnneeDto;
import nc.noumea.mairie.ptg.dto.DpmIndemniteChoixAgentDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IDpmService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/dpm")
public class DpmController {

	private Logger							logger	= LoggerFactory.getLogger(DpmController.class);

	@Autowired
	private IDpmService						dpmService;

	@Autowired
	private IAgentMatriculeConverterService	agentMatriculeConverterService;

	/**
	 * Sauvegarde le choix fait par l agent pour la prime Indemnité forfaitaire
	 * travail DPM : Indemnite ou recuperation
	 * 
	 * @param idAgentConnecte
	 *            Integer
	 * @param dto
	 *            DpmIndemniteChoixAgentDto
	 * @return ReturnMessageDto
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/saveIndemniteChoixAgent", produces = "application/json;charset=utf-8", consumes = "application/json")
	public ReturnMessageDto saveIndemniteChoixAgent(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte,
			@RequestBody(required = true) DpmIndemniteChoixAgentDto dto) {

		logger.debug("entered POST [dpm/saveIndemniteChoixAgent] => saveIndemniteChoixAgent with parameters idAgentConnecte = {} and dto = {}",
				idAgentConnecte, dto.toString());

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.saveIndemniteChoixAgentForKiosque(convertedIdAgentConnecte, dto);
	}

	/**
	 * Sauvegarde le choix de l agent pour la prime Indemnité forfaitaire
	 * travail DPM : Indemnite ou recuperation
	 * 
	 * Utilise par SIRH
	 * 
	 * @param idAgentConnecte
	 *            Integer
	 * @param dto
	 *            DpmIndemniteChoixAgentDto
	 * @return ReturnMessageDto
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/saveIndemniteChoixAgentForSIRH", produces = "application/json;charset=utf-8", consumes = "application/json")
	public ReturnMessageDto saveIndemniteChoixAgentForSIRH(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte,
			@RequestBody(required = true) DpmIndemniteChoixAgentDto dto) {

		logger.debug("entered POST [dpm/saveIndemniteChoixAgentForSIRH] => saveIndemniteChoixAgentForSIRH with parameters idAgentConnecte = {} and dto = {}",
				idAgentConnecte, dto.toString());

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.saveIndemniteChoixAgentForSIRH(convertedIdAgentConnecte, dto);
	}

	/**
	 * Sauvegarde une liste de choix saisie par l operateur pour plusieurs
	 * agents pour la prime Indemnité forfaitaire travail DPM : Indemnite ou
	 * recuperation
	 * 
	 * @param idAgentConnecte
	 *            Integer
	 * @param annee
	 *            Integer Annee concernee
	 * @param List
	 *            de dto List<DpmIndemniteChoixAgentDto>
	 * @return ReturnMessageDto
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/saveListIndemniteChoixAgentForOperator", produces = "application/json;charset=utf-8", consumes = "application/json")
	public ReturnMessageDto saveListIndemniteChoixAgentForOperator(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte, 
			@RequestParam(required = true, value = "annee") Integer annee, 
			@RequestBody(required = true) List<DpmIndemniteChoixAgentDto> dto) {

		logger.debug(
				"entered POST [dpm/saveListIndemniteChoixAgentForOperator] => saveListIndemniteChoixAgentForOperator with parameters idAgentConnecte = {} "
				+ "and annee = {} and dto = {}", idAgentConnecte, annee, dto.toString());

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.saveListIndemniteChoixAgentForOperator(convertedIdAgentConnecte, annee, dto);
	}
	
	/**
	 * Retourne le choix de l agent pour l Indemnité forfaitaire travail DPM
	 * 
	 * @param idAgentConnecte Integer L agent
	 * @param annee Integer L annee concernee
	 * @return DpmIndemniteChoixAgentDto le choix de l agent
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/indemniteChoixAgent", produces = "application/json;charset=utf-8", consumes = "application/json")
	public DpmIndemniteChoixAgentDto getIndemniteChoixAgent(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte, 
			@RequestParam(required = true, value = "annee") Integer annee) {
		
		logger.debug(
				"entered POST [dpm/indemniteChoixAgent] => getIndemniteChoixAgent with parameters idAgentConnecte = {} "
				+ "and annee = {}", idAgentConnecte, annee);

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.getIndemniteChoixAgent(convertedIdAgentConnecte, annee);
	}
	
	/**
	 * Retourne la liste des choix agent pour l Indemnité forfaitaire travail DPM pour les agents affectés a l operateur passe en parametre
	 * 
	 * @param idAgentConnecte Integer L operateur
	 * @param annee Integer L annee concernee
	 * @return List<DpmIndemniteChoixAgentDto> La liste des choix agents pour une annee
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/listDpmIndemniteChoixAgent", produces = "application/json;charset=utf-8")
	public List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgent(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte, 
			@RequestParam(required = true, value = "annee") Integer annee,
			@RequestParam(required = false, value = "idServiceAds") Integer idServiceAds,
			@RequestParam(required = false, value = "idAgentFiltre") Integer idAgentFiltre) {
		
		logger.debug(
				"entered POST [dpm/listDpmIndemniteChoixAgent] => getListDpmIndemniteChoixAgent with parameters idAgentConnecte = {} "
				+ "and annee = {} and idServiceAds = {} and idAgentFiltre = {}", idAgentConnecte, annee, idServiceAds, idAgentFiltre);

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.getListDpmIndemniteChoixAgent(convertedIdAgentConnecte, annee, idServiceAds, idAgentFiltre);
	}
	
	/**
	 * Retourne la liste des choix agent pour l Indemnité forfaitaire travail DPM pour les agents affectés a l operateur passe en parametre
	 * 
	 * @param idAgentConnecte Integer L operateur
	 * @param annee Integer L annee concernee
	 * @return List<DpmIndemniteChoixAgentDto> La liste des choix agents pour une annee
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/listDpmIndemniteChoixAgentForSIRH", produces = "application/json;charset=utf-8")
	public List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgentForSIRH(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte, 
			@RequestParam(required = false, value = "annee") Integer annee,
			@RequestParam(required = false, value = "isChoixIndemnite") Boolean isChoixIndemnite,
			@RequestParam(required = false, value = "isChoixRecuperation") Boolean isChoixRecuperation,
			@RequestParam(required = false, value = "listIdsAgent") List<Integer> listIdsAgent) {

		logger.debug(
				"entered POST [dpm/listDpmIndemniteChoixAgent] => getListDpmIndemniteChoixAgent with parameters idAgentConnecte = {} "
				+ "and annee = {} and listIdsAgent = {}", idAgentConnecte, annee, listIdsAgent);

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.getListDpmIndemniteChoixAgentforSIRH(convertedIdAgentConnecte, annee, isChoixIndemnite, isChoixRecuperation, listIdsAgent);
	}

	/**
	 * Retourne la liste des annees de saisie des choix agents pour la prime Indemnité forfaitaire travail DPM
	 * pour l ecran de parametrage dans SIRH
	 * 
	 * @return List<DpmIndemniteAnneeDto> la liste des annees pour parametrage
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/listDpmIndemAnnee", produces = "application/json;charset=utf-8")
	public List<DpmIndemniteAnneeDto> getListDpmIndemAnnee(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte) {
		
		logger.debug(
				"entered GET [dpm/listDpmIndemAnnee] => getListDpmIndemAnnee with parameters idAgentConnecte = {}", idAgentConnecte);

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.getListDpmIndemAnnee(convertedIdAgentConnecte);
	}

	/**
	 * Retourne l'année de saisie des choix agents pour la prime Indemnité forfaitaire travail DPM
	 * pour l ecran de parametrage dans SIRH
	 * 
	 * @param annee l'année voulue
	 * @return DpmIndemniteAnneeDto l'année pour parametrage
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/getDpmIndemAnneeByAnnee", produces = "application/json;charset=utf-8")
	public DpmIndemniteAnneeDto getDpmIndemAnneeByAnnee(@RequestParam(required = true, value = "annee") Integer annee) {
		
		logger.debug("entered GET [dpm/getDpmIndemAnnee] => getDpmIndemAnnee with parameters annee = {}", annee);

		return dpmService.getDpmIndemAnneeByAnnee(annee);
	}

	/**
	 * Retourne la liste des campagnes ouvertes pour la saisie des choix agents de la prime Indemnité forfaitaire travail DPM
	 * 
	 * @return List<DpmIndemniteAnneeDto> la liste des campagnes ouvertes
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/listDpmIndemAnneeOuverte", produces = "application/json;charset=utf-8")
	public List<DpmIndemniteAnneeDto> getListDpmIndemAnneeOuverte() {
		
		logger.debug(
				"entered POST [dpm/listDpmIndemAnneeOuverte] => getListDpmIndemAnneeOuverte without parameter");

		return dpmService.getListDpmIndemAnneeOuverte();
	}

	/**
	 * Retourne la campagne pour la saisie des choix agents de la prime Indemnité forfaitaire travail DPM
	 * de la annee en cours
	 * 
	 * @return DpmIndemniteAnneeDto la campagne en cours
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/dpmIndemAnneeEnCours", produces = "application/json;charset=utf-8")
	public DpmIndemniteAnneeDto getDpmIndemAnneeEnCours() {
		
		logger.debug(
				"entered POST [dpm/dpmIndemAnneeEnCours] => getDpmIndemAnneeEnCours without parameter");

		return dpmService.getDpmIndemAnneeEnCours();
	}

	/**
	 * Création d'une annee DpmIndemniteAnneeDto
	 * 
	 * @param idAgentConnecte Integer
	 * @param dto DpmIndemniteAnneeDto
	 * @return ReturnMessageDto
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/createDpmIndemAnnee", produces = "application/json;charset=utf-8", consumes = "application/json")
	public ReturnMessageDto createDpmIndemAnnee(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte, 
			@RequestBody(required = true) DpmIndemniteAnneeDto dto) {
		
		logger.debug(
				"entered POST [dpm/createDpmIndemAnnee] => createDpmIndemAnnee with parameters idAgentConnecte = {} "
				+ "and annee = {} and dto = {}", idAgentConnecte, dto.getAnnee(), dto.toString());

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.createDpmIndemAnnee(convertedIdAgentConnecte, dto);
	}

	/**
	 * Sauvegarde le parametrage pour une annee DpmIndemniteAnneeDto
	 * 
	 * @param idAgentConnecte Integer
	 * @param dto DpmIndemniteAnneeDto
	 * @return ReturnMessageDto
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/saveDpmIndemAnnee", produces = "application/json;charset=utf-8", consumes = "application/json")
	public ReturnMessageDto saveDpmIndemAnnee(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte, 
			@RequestBody(required = true) DpmIndemniteAnneeDto dto) {
		
		logger.debug(
				"entered POST [dpm/saveDpmIndemAnnee] => saveDpmIndemAnnee with parameters idAgentConnecte = {} "
				+ "and annee = {} and dto = {}", idAgentConnecte, dto.getAnnee(), dto.toString());

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.saveDpmIndemAnnee(convertedIdAgentConnecte, dto);
	}

	/**
	 * Permet de savoir si l agent a le droit a la prime Indemnité forfaitaire travail DPM :
	 *  - ne doit pas etre en cycle
	 *  - doit avoir la prime sur son affectation
	 * 
	 * @param idAgent Agent concerne
	 * @return boolean true ou false
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/isDroitAgentToIndemniteForfaitaireDPM", produces = "application/json;charset=utf-8")
	public boolean isDroitAgentToIndemniteForfaitaireDPM(@RequestParam(required = true, value = "idAgent") Integer idAgent){
		
		logger.debug(
				"entered POST [dpm/isDroitAgentToIndemniteForfaitaireDPM] => isDroitAgentToIndemniteForfaitaireDPM with parameters idAgent = {}", idAgent);

		int convertedIdAgent = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		return dpmService.isDroitAgentToIndemniteForfaitaireDPM(convertedIdAgent);
	}

	/**
	 * Retourne si le choix dans le kiosqueRH pour la prime Indemnité forfaitaire travail DPM
	 * est ouvert 
	 * 
	 * @param annee Integer 
	 * @return boolean true ou false
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/isPeriodeChoixOuverte", produces = "application/json;charset=utf-8")
	public boolean isPeriodeChoixOuverte(@RequestParam(required = true, value = "annee") Integer annee){
		
		logger.debug(
				"entered POST [dpm/isPeriodeChoixOuverte] => isPeriodeChoixOuverte with parameters annee = {}", annee);

		return dpmService.isPeriodeChoixOuverte(annee);
	}

	/**
	 * Supprime le choix d un agent, uniquement si la campagne est ouverte et
	 * par un utilisateur DRH
	 * 
	 * Droit : uniquement SIRH
	 * 
	 * @param idAgentConnecte
	 *            Integer
	 * @param dto
	 *            DpmIndemniteChoixAgentDto
	 * @return ReturnMessageDto
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/deleteIndemniteChoixAgent", produces = "application/json;charset=utf-8")
	public ReturnMessageDto deleteIndemniteChoixAgent(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte,
			@RequestParam(required = true, value = "idDpmIndemChoixAgent") Integer idDpmIndemChoixAgent) {

		logger.debug("entered POST [dpm/deleteIndemniteChoixAgent] => deleteIndemniteChoixAgent with parameters idAgentConnecte = {} and idDpmIndemChoixAgent = {}",
				idAgentConnecte, idDpmIndemChoixAgent);

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.deleteIndemniteChoixAgentForKiosque(convertedIdAgentConnecte, idDpmIndemChoixAgent);
	}

}
