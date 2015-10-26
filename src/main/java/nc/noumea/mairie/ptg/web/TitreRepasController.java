package nc.noumea.mairie.ptg.web;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.titreRepas.dto.TitreRepasDemandeDto;
import nc.noumea.mairie.titreRepas.service.ITitreRepasService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/titreRepas")
public class TitreRepasController {
	
	private Logger logger = LoggerFactory.getLogger(TitreRepasController.class);
	
	@Autowired
	private ITitreRepasService titreRepasService;
	
	/**
	 * Enregistre une liste de demande de Titre Repas depuis le Kiosque RH (operateur ou approbateur) ou SIRH.
	 * 
	 * Si dans TitreRepasDemandeDto, l attribut idTrDemande est renseigné, c'est une modification
	 * sinon une création
	 * 
	 * @param idAgentConnecte Integer
	 * @param isFromSIRH boolean
	 * @param listTitreRepasDemandeDto List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/enregistreListTitreDemande", produces = "application/json;charset=utf-8", consumes = "application/json")
	public ReturnMessageDto enregistreListTitreDemande(
			@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte,
			@RequestParam(required = true, value = "isFromSIRH") boolean isFromSIRH,
			@RequestBody(required = true) List<TitreRepasDemandeDto> listTitreRepasDemandeDto) {
		
		logger.debug("entered POST [titreRepas/enregistreListTitreDemande] => enregistreListTitreDemande with parameters idAgentConnecte = {}, isFromSIRH = {} and listTitreRepasDemandeDto.size = {}",
				idAgentConnecte, isFromSIRH, listTitreRepasDemandeDto.size());
		
		if(isFromSIRH) {
			return titreRepasService.enregistreListTitreDemandeFromSIRH(idAgentConnecte, listTitreRepasDemandeDto);
		}else{
			return titreRepasService.enregistreListTitreDemandeFromKiosque(idAgentConnecte, listTitreRepasDemandeDto);
		}
	}
	
	/**
	 * Enregistre une demande de Titre Repas depuis le Kiosque RH de l'agent.
	 * 
	 * Si dans TitreRepasDemandeDto, l attribut idTrDemande est renseigné, c'est une modification
	 * sinon une création
	 * 
	 * @param idAgentConnecte Integer
	 * @param isFromSIRH boolean
	 * @param  titreRepasDemandeDto TitreRepasDemandeDto
	 * @return ReturnMessageDto
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/enregistreTitreDemandeFromAgent", produces = "application/json;charset=utf-8", consumes = "application/json")
	public ReturnMessageDto enregistreTitreDemandeFromAgent(
			@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte,
			@RequestBody(required = true) TitreRepasDemandeDto titreRepasDemandeDto) {
		
		logger.debug("entered POST [titreRepas/enregistreTitreDemandeFromAgent] => enregistreTitreDemandeFromAgent with parameters idAgentConnecte = {}",
				idAgentConnecte);
		
		return titreRepasService.enregistreTitreDemandeAgent(idAgentConnecte, titreRepasDemandeDto);
	}
	
	/**
	 * Retourne une liste de demandes de Titre Repas.
	 * 
	 * @param idAgentConnecte Integer
	 * @param listIdsAgent List<Integer> liste d agents recherches
	 * @param fromDate Date
	 * @param toDate Date
	 * @param etat Integer
	 * @param commande Boolean
	 * @param dateMonth Date
	 * @return List<TitreRepasDemandeDto> liste de demandes de Titre Repas
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/getListTitreRepasDemandeDto", produces = "application/json;charset=utf-8", consumes = "application/json")
	public List<TitreRepasDemandeDto> getListTitreRepasDemandeDto(
			@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte,
			@RequestParam(required = true, value = "fromDate") @DateTimeFormat(pattern = "yyyyMMdd") Date fromDate,
			@RequestParam(required = true, value = "toDate") @DateTimeFormat(pattern = "yyyyMMdd") Date toDate,
			@RequestParam(required = false, value = "etat") Integer etat,
			@RequestParam(required = false, value = "commande") Boolean commande,
			@RequestParam(required = false, value = "dateMonth") @DateTimeFormat(pattern = "yyyyMMdd") Date  dateMonth,
			@RequestBody List<Integer> listIdsAgent) {
		
		logger.debug("entered POST [titreRepas/enregistreTitreDemandeFromAgent] => enregistreTitreDemandeFromAgent with parameters idAgentConnecte = {}",
				idAgentConnecte);
		
		return titreRepasService.getListTitreRepasDemandeDto(idAgentConnecte, listIdsAgent, fromDate, toDate, etat, commande, dateMonth);
	}
}
