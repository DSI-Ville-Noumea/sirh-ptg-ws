package nc.noumea.mairie.ptg.web;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
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

import flexjson.JSONDeserializer;
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

		accessRightService.setDelegatorAndOperators(idAgent, new DelegatorAndOperatorsDto().deserializeFromJSON(delegatorAndOperatorsDtoJson));

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "approbateurs", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> listApprobateurs() {

		logger.debug("entered GET [droits/listeApprobateurs] => listeApprobateurs with no parameter --> for SIRH ");

		List<AgentWithServiceDto> result = accessRightService.listAgentsApprobateurs();

		return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").serialize(result), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "approbateurs", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setApprobateur(@RequestBody String agentsDtoJson) {
		logger.debug("entered POST [droits/approbateur] => setApprobateur ");

		List<AgentWithServiceDto> agDtos = new JSONDeserializer<List<AgentWithServiceDto>>()
				.use(null, ArrayList.class)
				.use("values", AgentWithServiceDto.class)
				.deserialize(agentsDtoJson);
		
		try {
			accessRightService.setApprobateurs(agDtos);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "agentsApprouves", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getApprovedAgents(@RequestParam("idAgent") Integer idAgent) {
		
		logger.debug("entered GET [droits/agentsApprouves] => getApprovedAgents with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		
		List<AgentDto> result = accessRightService.getAgentsToApprove(convertedIdAgent);
		
		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		
		String response = new JSONSerializer().exclude("*.class").serialize(result);
		
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "agentsApprouves", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setApprovedAgents(@RequestParam("idAgent") Integer idAgent, @RequestBody String agentsApprouvesJson) {
		
		logger.debug("entered POST [droits/agentsApprouves] => setApprovedAgents with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		
		List<AgentDto> agDtos = new JSONDeserializer<List<AgentDto>>()
				.use(null, ArrayList.class)
				.use("values", AgentDto.class)
				.deserialize(agentsApprouvesJson);
		
		try {
			accessRightService.setAgentsToApprove(convertedIdAgent, agDtos);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
