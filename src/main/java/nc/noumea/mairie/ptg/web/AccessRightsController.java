package nc.noumea.mairie.ptg.web;

import java.util.List;

import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;
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

import flexjson.JSONSerializer;

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

		logger.debug("entered GET [droits/listeDroitsAgent] => listAgentAccessRights with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (Agent.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();

		AccessRightsDto result = accessRightService.getAgentAccessRights(convertedIdAgent);

		return new ResponseEntity<String>(result.serializeInJSON(), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "delegataireOperateurs", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getDelegateAndOperator(@RequestParam("idAgent") Integer idAgent) {
		logger.debug("entered GET [droits/delegataireOperateurs] => getDelegateAndInputter with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessAccessRights(convertedIdAgent))
			throw new AccessForbiddenException();

		if (Agent.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();

		DelegatorAndOperatorsDto result = accessRightService.getDelegatorAndOperators(convertedIdAgent);

		return new ResponseEntity<String>(result.serializeInJSON(), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "delegataireOperateurs", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setDelegateAndOperator(@RequestParam("idAgent") Integer idAgent, @RequestBody String delegatorAndOperatorsDtoJson) {
		logger.debug("entered POST [droits/delegataireOperateurs] => setDelegateAndInputter with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessAccessRights(convertedIdAgent))
			throw new AccessForbiddenException();

		if (Agent.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();

		accessRightService.setDelegatorAndOperators(idAgent, new DelegatorAndOperatorsDto().deserializeFromJSON(delegatorAndOperatorsDtoJson));

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "listeAgents", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> listAgents(@RequestParam("idAgent") Integer idAgent) {
		logger.debug("entered POST [droits/listeAgents] => listAgents with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessAccessRights(convertedIdAgent))
			throw new AccessForbiddenException();

		if (Agent.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();

		List<AgentDto> agents = accessRightService.listAgentsToAssign(idAgent);

		return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").serialize(agents), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "approbateurs", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> listApprobateurs() {

		logger.debug("entered GET [droits/listeApprobateurs] => listeApprobateurs with no parameter --> for SIRH ");

		List<AgentDto> result = accessRightService.listAgentsApprobateurs();

		return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").serialize(result), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "approbateurs", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setApprobateur(@RequestBody String agentDtoJson) {
		logger.debug("entered POST [droits/approbateur] => setApprobateur ");

		accessRightService.setApprobateurs(new AgentDto().deserializeFromJSON(agentDtoJson));

		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
