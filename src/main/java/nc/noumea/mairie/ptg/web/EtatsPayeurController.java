package nc.noumea.mairie.ptg.web;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.ListEtatsPayeurDto;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IEtatPayeurService;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.ptg.transformer.MSDateTransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import flexjson.JSONSerializer;

@Controller
@RequestMapping("/etatsPayeur")
public class EtatsPayeurController {

	private Logger logger = LoggerFactory.getLogger(EtatsPayeurController.class);
	
	@Autowired
	private IExportEtatPayeurService exportEtatPayeurService;

	@Autowired
	private IAccessRightsService accessRightService;
	
	@Autowired
	private HelperService helperService;
	
	@Autowired
	private IAgentMatriculeConverterService converterService;
	
	@Autowired
	private IEtatPayeurService etatPayeurService;
	
	@ResponseBody
	@RequestMapping(value = "/xml/getAbsences", produces = "application/xml", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView getXmlEtatsPayeurAbsences(@RequestParam("statut") String statutString) throws ParseException {

		logger.debug(
				"entered GET [etatsPayeur/xml/getAbsences] => getXmlEtatsPayeurAbsences with parameter statut = {}",
				statutString);

		AgentStatutEnum statut = AgentStatutEnum.valueOf(statutString);
		
		EtatPayeurDto result = exportEtatPayeurService.getAbsencesEtatPayeurDataForStatut(statut);
		
		return new ModelAndView("xmlView", "object", result);
	}
	
	@ResponseBody
	@RequestMapping(value = "/xml/getHeuresSup", produces = "application/xml", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView getXmlEtatsPayeurHeuresSup(@RequestParam("statut") String statutString) throws ParseException {

		logger.debug(
				"entered GET [etatsPayeur/xml/getHeuresSup] => getXmlEtatsPayeurHeuresSup with parameter statut = {}",
				statutString);

		AgentStatutEnum statut = AgentStatutEnum.valueOf(statutString);
		
		EtatPayeurDto result = exportEtatPayeurService.getHeuresSupEtatPayeurDataForStatut(statut);
		
		return new ModelAndView("xmlView", "object", result);
	}
	
	@ResponseBody
	@RequestMapping(value = "/xml/getPrimes", produces = "application/xml", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView getXmlEtatsPayeurPrimes(@RequestParam("statut") String statutString) throws ParseException {

		logger.debug(
				"entered GET [etatsPayeur/xml/getPrimes] => getXmlEtatsPayeurPrimes with parameter statut = {}",
				statutString);

		AgentStatutEnum statut = AgentStatutEnum.valueOf(statutString);
		
		EtatPayeurDto result = exportEtatPayeurService.getPrimesEtatPayeurDataForStatut(statut);
		
		return new ModelAndView("xmlView", "object", result);
	}
	
	@ResponseBody
	@RequestMapping(value = "/canStartExportEtatsPayeur", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> canStartExportEtatsPayeur(@RequestParam("statut") String statut) {

		logger.debug("entered GET [etatsPayeur/canStartExportEtatsPayeur] => canStartExportEtatsPayeur with parameter statut = {}", statut);
		
		CanStartWorkflowPaieActionDto result = exportEtatPayeurService
				.canStartExportEtatPayeurAction(helperService.getTypeChainePaieFromStatut(AgentStatutEnum.valueOf(statut)));
        
		String resultJson = new JSONSerializer().exclude("*.class").serialize(result);
		
		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/downloadFicheEtatsPayeur", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<byte[]> downloadFicheEtatsPayeur(
			@RequestParam("idEtatPayeur") Integer idEtatPayeur, @RequestParam("idAgent") Integer idAgent) {
		
		logger.debug(
				"entered GET [etatsPayeur/downloadFicheEtatsPayeur] => downloadFicheEtatsPayeur with parameters idEtatPayeur = {}, idAgent = {}",
				idEtatPayeur, idAgent);
		
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessPrint(convertedIdAgent))
			throw new AccessForbiddenException();
		
		EtatPayeur etatPayeur = etatPayeurService.getEtatPayeurByIdEtatPayeur(idEtatPayeur);
		
		byte[] responseData = null;
		try {
			responseData = etatPayeurService.downloadFichierEtatPayeur(etatPayeur.getFichier());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/pdf");
		headers.add("Content-Disposition", String.format("attachment; filename=\""+etatPayeur.getFichier()+"\""));

		return new ResponseEntity<byte[]>(responseData, headers, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@RequestMapping(value = "/listEtatsPayeur", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getListEtatsPayeurByStatut(@RequestParam(value = "statutAgent", required = true) String statutString) {

		logger.debug(
				"entered GET [etatsPayeur/listEtatsPayeur] => getListEtatsPayeurByStatut with parameters  statutAgent = {}",
				statutString);
		
		AgentStatutEnum statut = AgentStatutEnum.valueOf(statutString);
		
		List<ListEtatsPayeurDto> result = etatPayeurService.getListEtatsPayeurByStatut(statut);
		
		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

		String response = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(result);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/start", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> startExportEtatsPayeur(
			@RequestParam(value = "idAgent", required = true) Integer idAgentExporting,
			@RequestParam(value = "statut", required = true) String statutString) {

		logger.debug(
				"entered GET [etatsPayeur/runExportEtatsPayeur] => runExportEtatsPayeur with parameters  statut = {}",
				statutString);
		
		AgentStatutEnum statut = AgentStatutEnum.valueOf(statutString);
		
		ReturnMessageDto result = exportEtatPayeurService.startExportEtatsPayeur(idAgentExporting, statut);
		
		String response = new JSONSerializer().exclude("*.class").deepSerialize(result);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
}
