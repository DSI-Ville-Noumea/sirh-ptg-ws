package nc.noumea.mairie.ptg.web;

import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndInputtersDto;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.sirh.domain.Agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/droits")
public class AccessRightsController {

	private Logger logger = LoggerFactory.getLogger(AccessRightsController.class);
	
	@Autowired
	private IAccessRightsService accessRightService;
	
	@Autowired
	private IAgentMatriculeConverterService converterService;
	
	@ResponseBody
	@RequestMapping(value = "listeDroitsAgent", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> listAgentAccessRights(@RequestParam("idAgent") Integer idAgent) {
		
		logger.debug("entered GET [listeDroitsAgent] => listAgentAccessRights with parameter idAgent = {}", idAgent);
		
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		
		if (Agent.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();

		AccessRightsDto result = accessRightService.getAgentAccessRights(convertedIdAgent);
		
		return new ResponseEntity<String>(result.serializeInJSON(), HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "delegataireSaisisseurs", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getDelegateAndInputter(@RequestParam("idAgent") Integer idAgent)
	{
		logger.debug("entered GET [delegataireSaisisseurs] => getDelegateAndInputter with parameter idAgent = {}", idAgent);
		
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		
		if (Agent.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();
		
		DelegatorAndInputtersDto result = accessRightService.getDelegatorAndInputters(convertedIdAgent);

		return new ResponseEntity<String>(result.serializeInJSON(), HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "delegataireSaisisseurs", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setDelegateAndInputter(@RequestParam("idAgent") Integer idAgent, @RequestBody String delegatorAndInputtersDto)
	{
		logger.debug("entered POST [delegataireSaisisseurs] => setDelegateAndInputter with parameter idAgent = {}", idAgent);
		
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		
		if (Agent.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();
		
		accessRightService.setDelegatorAndInputters(idAgent, null);

		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
}
