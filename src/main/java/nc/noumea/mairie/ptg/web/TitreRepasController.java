package nc.noumea.mairie.ptg.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.transformer.MSDateTransformer;
import nc.noumea.mairie.titreRepas.dto.TitreRepasDemandeDto;
import nc.noumea.mairie.titreRepas.service.ITitreRepasService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import flexjson.JSONDeserializer;

@Controller
@RequestMapping("/titreRepas")
public class TitreRepasController {

	private Logger logger = LoggerFactory.getLogger(TitreRepasController.class);

	@Autowired
	private ITitreRepasService titreRepasService;

	@Autowired
	private IAgentMatriculeConverterService agentMatriculeConverterService;

	@Autowired
	private IAccessRightsService accessRightService;

	/**
	 * Enregistre une liste de demande de Titre Repas depuis le Kiosque RH
	 * (operateur ou approbateur) ou SIRH.
	 * 
	 * Si dans TitreRepasDemandeDto, l attribut idTrDemande est renseigné, c'est
	 * une modification sinon une création
	 * 
	 * @param idAgentConnecte
	 *            Integer
	 * @param isFromSIRH
	 *            boolean
	 * @param listTitreRepasDemandeDto
	 *            List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/enregistreListTitreDemande", produces = "application/json;charset=utf-8", consumes = "application/json")
	public ReturnMessageDto enregistreListTitreDemande(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte,
			@RequestParam(required = true, value = "isFromSIRH") boolean isFromSIRH, @RequestBody(required = true) String listTitreRepas) {

		List<TitreRepasDemandeDto> listTitreRepasDemandeDto = new JSONDeserializer<List<TitreRepasDemandeDto>>().use(null, ArrayList.class).use(Date.class, new MSDateTransformer())
				.use("values", TitreRepasDemandeDto.class).deserialize(listTitreRepas);

		logger.debug("entered POST [titreRepas/enregistreListTitreDemande] => enregistreListTitreDemande with parameters idAgentConnecte = {}, isFromSIRH = {} and listTitreRepasDemandeDto.size = {}",
				idAgentConnecte, isFromSIRH, listTitreRepasDemandeDto.size());

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		if (isFromSIRH) {
			return titreRepasService.enregistreListTitreDemandeFromSIRH(convertedIdAgentConnecte, listTitreRepasDemandeDto);
		} else {
			return titreRepasService.enregistreListTitreDemandeFromKiosque(convertedIdAgentConnecte, listTitreRepasDemandeDto);
		}
	}

	/**
	 * Retourne une liste de demandes de Titre Repas.
	 * 
	 * @param idAgentConnecte
	 *            Integer
	 * @param listIdsAgent
	 *            List<Integer> liste d agents recherches
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
	 * @return List<TitreRepasDemandeDto> liste de demandes de Titre Repas
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.GET, value = "/listTitreRepas", produces = "application/json;charset=utf-8", consumes = "application/json")
	public List<TitreRepasDemandeDto> getListTitreRepasDemandeDto(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte,
			@RequestParam(required = false, value = "fromDate") @DateTimeFormat(pattern = "yyyyMMdd") Date fromDate,
			@RequestParam(required = false, value = "toDate") @DateTimeFormat(pattern = "yyyyMMdd") Date toDate, @RequestParam(required = false, value = "etat") Integer etat,
			@RequestParam(required = false, value = "commande") Boolean commande, @RequestParam(required = false, value = "dateMonth") @DateTimeFormat(pattern = "yyyyMMdd") Date dateMonth,
			@RequestParam(required = false, value = "idServiceADS") Integer idServiceADS, @RequestParam(required = false, value = "idAgent") Integer idAgent) {

		logger.debug(
				"entered GET [titreRepas/listTitreRepas] => getListTitreRepasDemandeDto with parameters parameters idAgentConnecte = {}, from = {}, to = {}, idServiceAds = {}, idAgent = {}, etat = {}, commande = {} and dateMonth = {}",
				idAgentConnecte, fromDate, toDate, idServiceADS, idAgent, etat, commande, dateMonth);

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);
		int convertedIdAgent = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		return titreRepasService.getListTitreRepasDemandeDto(convertedIdAgentConnecte, fromDate, toDate, etat, commande, dateMonth, idServiceADS, convertedIdAgent);
	}

	/**
	 * Mets à jour l'état d'une liste de demande de Titre Repas depuis SIRH :
	 * APPROUVE ou REJETTE
	 * 
	 * TitreRepasDemandeDto : - idTrDemande : ID de la demande de TR -
	 * commentaire : commentaire en cas de refus, voir approbation - idRefEtat :
	 * le nouvel état de la demande : APPROUVE (1) ou REJETE (5)
	 * 
	 * Le reste des champs n'est pas utilisé
	 * 
	 * @param idAgentConnecte
	 *            Integer
	 * @param listTitreRepasDemandeDto
	 *            List<TitreRepasDemandeDto>
	 * @return ReturnMessageDto
	 */
	@ResponseBody
	@RequestMapping(method = RequestMethod.POST, value = "/updateEtatForListTitreRepasDemande", produces = "application/json;charset=utf-8", consumes = "application/json")
	public ReturnMessageDto updateEtatForListTitreRepasDemande(@RequestParam(required = true, value = "idAgentConnecte") Integer idAgentConnecte,
			@RequestBody(required = true) List<TitreRepasDemandeDto> listTitreRepasDemandeDto) {

		logger.debug("entered POST [titreRepas/updateEtatForListTitreRepasDemande] => updateEtatForListTitreRepasDemande with parameters idAgentConnecte = {} and listTitreRepasDemandeDto.size = {}",
				idAgentConnecte, listTitreRepasDemandeDto.size());

		int convertedIdAgentConnecte = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgentConnecte);

		return titreRepasService.updateEtatForListTitreRepasDemande(convertedIdAgentConnecte, listTitreRepasDemandeDto);
	}

	/**
	 * Retourne la liste des états possible pour une demande de Titre Repas.
	 * 
	 * @return List<RefEtatDto>
	 */
	@ResponseBody
	@RequestMapping(value = "/getEtats", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	public List<RefEtatDto> getEtats() {

		logger.debug("entered GET [titreRepas/getEtats] => getEtats");

		return titreRepasService.getListRefEtats();
	}

	@ResponseBody
	@RequestMapping(value = "/historique", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public List<TitreRepasDemandeDto> getTitreRepasArchives(@RequestParam(required = true, value = "idTrDemande") Integer idTrDemande) {

		logger.debug("entered GET [titreRepas/historique] => getTitreRepasArchives with parameters idTrDemande = {}", idTrDemande);

		return titreRepasService.getTitreRepasArchives(idTrDemande);
	}
}
