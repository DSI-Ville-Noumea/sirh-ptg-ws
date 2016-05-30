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

		return dpmService.saveIndemniteChoixAgent(convertedIdAgentConnecte, dto);
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
	 * Retourne la liste des choix agent pour l Indemnité forfaitaire travail DPM pour les agents affectés a l operateur passe en parametre
	 * 
	 * @param idAgentConnecte Integer L operateur
	 * @param annee Integer L annee concernee
	 * @return List<DpmIndemniteChoixAgentDto> La liste des choix agents pour une annee
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/listDpmIndemniteChoixAgent", produces = "application/json;charset=utf-8", consumes = "application/json")
	public List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgent(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte, 
			@RequestParam(required = true, value = "annee") Integer annee) {
		
		logger.debug(
				"entered POST [dpm/saveListIndemniteChoixAgentForOperator] => saveListIndemniteChoixAgentForOperator with parameters idAgentConnecte = {} "
				+ "and annee = {}", idAgentConnecte, annee);

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.getListDpmIndemniteChoixAgent(convertedIdAgentConnecte, annee);
	}

	/**
	 * Retourne la liste des annees de saisie des choix agents pour la prime Indemnité forfaitaire travail DPM
	 * pour l ecran de parametrage dans SIRH
	 * 
	 * @return List<DpmIndemniteAnneeDto> la liste des annees pour parametrage
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/listDpmIndemAnnee", produces = "application/json;charset=utf-8", consumes = "application/json")
	public List<DpmIndemniteAnneeDto> getListDpmIndemAnnee(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte) {
		
		logger.debug(
				"entered POST [dpm/getListDpmIndemAnnee] => getListDpmIndemAnnee with parameters idAgentConnecte = {}", idAgentConnecte);

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return dpmService.getListDpmIndemAnnee(convertedIdAgentConnecte);
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
				+ "and annee = {} and dto = {}", idAgentConnecte, dto.toString());

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
	@RequestMapping(method = RequestMethod.GET, value = "/isDroitAgentToIndemniteForfaitaireDPM", produces = "application/json;charset=utf-8", consumes = "application/json")
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
	@RequestMapping(method = RequestMethod.GET, value = "/isPeriodeChoixOuverte", produces = "application/json;charset=utf-8", consumes = "application/json")
	public boolean isPeriodeChoixOuverte(@RequestParam(required = true, value = "annee") Integer annee){
		
		logger.debug(
				"entered POST [dpm/isPeriodeChoixOuverte] => isPeriodeChoixOuverte with parameters annee = {}", annee);

		return dpmService.isPeriodeChoixOuverte(annee);
	}

}
