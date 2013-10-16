package nc.noumea.mairie.ptg.web;

import java.text.ParseException;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	private HelperService helperService;
	
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
	
}
