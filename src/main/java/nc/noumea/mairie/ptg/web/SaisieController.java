package nc.noumea.mairie.ptg.web;

import java.util.Date;

import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.sirh.domain.Agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import flexjson.JSONSerializer;

@Controller
@RequestMapping("/saisie")
public class SaisieController {

	private Logger logger = LoggerFactory.getLogger(SaisieController.class);

	@Autowired
	private IAgentMatriculeConverterService agentMatriculeConverterService;

	@Autowired
	private IPointageService pointageService;
	
	@Autowired
	private IAccessRightsService accessRightService;

	@ResponseBody
	@RequestMapping(value = "/fiche", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getFichePointage(
			@RequestParam("idAgent") int idAgent,
			@RequestParam("date") @DateTimeFormat(pattern = "YYYYMMdd") Date date, 
			@RequestParam("agent") int agent) {

		logger.debug(
				"entered GET [saisie/fiche] => getFichePointage with parameters idAgent = {}, date = {} and agent = {}",
				idAgent, date, agent);

		int convertedIdAgent = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		int convertedagent = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(agent);
		
		if (Agent.findAgent(convertedagent) == null)
			throw new NotFoundException();
		
		if (!accessRightService.canUserAccessSaisie(convertedIdAgent, convertedagent))
			throw new AccessForbiddenException();
		
		FichePointageDto fichePointageAgent = pointageService.getFilledFichePointageForAgent(agent, date);
		String response = new JSONSerializer().exclude("*.class").deepSerialize(fichePointageAgent);
		
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
}
