package nc.noumea.mairie.ptg.web;

import java.text.ParseException;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/etatsPayeur")
public class EtatsPayeurController {

	private Logger logger = LoggerFactory.getLogger(EtatsPayeurController.class);
	
	@Autowired
	private IExportEtatPayeurService exportEtatPayeurService;
	
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
	
}
