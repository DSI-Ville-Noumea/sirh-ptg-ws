package nc.noumea.mairie.ptg.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.ApprobateurDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.transformer.MSDateTransformer;

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

		logger.debug("entered GET [droits/listeDroitsAgent] => listAgentAccessRights with parameter idAgent = {}",
				idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (accessRightService.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();

		AccessRightsDto result = accessRightService.getAgentAccessRights(convertedIdAgent);

		return new ResponseEntity<String>(result.serializeInJSON(), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "delegataireOperateurs", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getDelegateAndOperator(@RequestParam("idAgent") Integer idAgent) {
		logger.debug(
				"entered GET [droits/delegataireOperateurs] => getDelegateAndInputter with parameter idAgent = {}",
				idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessAccessRights(convertedIdAgent))
			throw new AccessForbiddenException();

		DelegatorAndOperatorsDto result = accessRightService.getDelegatorAndOperators(convertedIdAgent);

		return new ResponseEntity<String>(result.serializeInJSON(), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "delegataireOperateurs", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setDelegateAndOperator(@RequestParam("idAgent") Integer idAgent,
			@RequestBody String delegatorAndOperatorsDtoJson) {
		logger.debug(
				"entered POST [droits/delegataireOperateurs] => setDelegateAndInputter with parameter idAgent = {}",
				idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessAccessRights(convertedIdAgent))
			throw new AccessForbiddenException();

		ReturnMessageDto result = accessRightService.setDelegatorAndOperators(convertedIdAgent,
				new DelegatorAndOperatorsDto().deserializeFromJSON(delegatorAndOperatorsDtoJson));

		String jsonResult = new JSONSerializer().exclude("*.class").deepSerialize(result);

		if (result.getErrors().size() != 0)
			return new ResponseEntity<String>(jsonResult, HttpStatus.CONFLICT);
		else
			return new ResponseEntity<String>(jsonResult, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "approbateurs", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> listApprobateurs(@RequestParam(value = "idAgent", required = false) Integer idAgent,
			@RequestParam(value = "codeService", required = false) String codeService) {

		logger.debug(
				"entered GET [droits/approbateurs] => listApprobateurs with parameter idAgent = {} and codeService = {} --> for SIRH ",
				idAgent, codeService);

		List<ApprobateurDto> result = accessRightService.listAgentsApprobateurs(idAgent, codeService);

		return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").serialize(result), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "approbateurs", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setApprobateur(@RequestBody String agentsDtoJson) {
		logger.debug("entered POST [droits/approbateurs] => setApprobateur --> for SIRH ");

		AgentWithServiceDto agDtos = new JSONDeserializer<AgentWithServiceDto>().use(Date.class,
				new MSDateTransformer()).deserializeInto(agentsDtoJson, new AgentWithServiceDto());
		ReturnMessageDto res = new ReturnMessageDto();
		try {
			res = accessRightService.setApprobateur(agDtos);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		}

		return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").serialize(res), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "deleteApprobateurs", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> deleteApprobateur(@RequestBody String agentsDtoJson) {
		logger.debug("entered POST [droits/deleteApprobateurs] => deleteApprobateur --> for SIRH ");

		AgentWithServiceDto agDtos = new JSONDeserializer<AgentWithServiceDto>().use(Date.class,
				new MSDateTransformer()).deserializeInto(agentsDtoJson, new AgentWithServiceDto());
		ReturnMessageDto res = new ReturnMessageDto();
		try {
			res = accessRightService.deleteApprobateur(agDtos);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		}

		return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").serialize(res), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "agentsApprouves", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getApprovedAgents(@RequestParam("idAgent") Integer idAgent) {

		logger.debug("entered GET [droits/agentsApprouves] => getApprovedAgents with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessAccessRights(convertedIdAgent))
			throw new AccessForbiddenException();

		List<AgentDto> result = accessRightService.getAgentsToApprove(convertedIdAgent, null);

		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

		String response = new JSONSerializer().exclude("*.class").serialize(result);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	/**
	 * #15897 bug dans SIRH
	 * 
	 * @param idAgent
	 *            de l approbateur
	 * @return List<AgentDto> la liste des agents affectes a l approbateur
	 */
	@ResponseBody
	@RequestMapping(value = "agentsApprouvesForSIRH", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getApprovedAgentsForSIRH(@RequestParam("idAgent") Integer idAgent) {

		logger.debug("entered GET [droits/agentsApprouves] => getApprovedAgents with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessAccessRights(convertedIdAgent))
			throw new AccessForbiddenException();

		List<AgentDto> result = accessRightService.getAgentsToApproveWithoutDelegateRole(convertedIdAgent, null);

		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

		String response = new JSONSerializer().exclude("*.class").serialize(result);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "agentsApprouves", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setApprovedAgents(@RequestParam("idAgent") Integer idAgent,
			@RequestBody String agentsApprouvesJson) {

		logger.debug("entered POST [droits/agentsApprouves] => setApprovedAgents with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessAccessRights(convertedIdAgent))
			throw new AccessForbiddenException();

		List<AgentDto> agDtos = new JSONDeserializer<List<AgentDto>>().use(null, ArrayList.class)
				.use("values", AgentDto.class).deserialize(agentsApprouvesJson);

		try {
			accessRightService.setAgentsToApprove(convertedIdAgent, agDtos);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "agentsSaisis", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getInputAgents(@RequestParam("idAgent") Integer idAgent,
			@RequestParam("idOperateur") Integer idOperateur) {

		logger.debug(
				"entered GET [droits/agentsSaisis] => getInputAgents with parameter idAgent = {} and idOperateur = {}",
				idAgent, idOperateur);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessAccessRights(convertedIdAgent))
			throw new AccessForbiddenException();

		int convertedIdOperateur = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idOperateur);

		List<AgentDto> result = accessRightService.getAgentsToInput(convertedIdAgent, convertedIdOperateur);

		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

		String response = new JSONSerializer().exclude("*.class").serialize(result);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "agentsSaisis", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setInputAgents(@RequestParam("idAgent") Integer idAgent,
			@RequestParam("idOperateur") Integer idOperateur, @RequestBody String agentsApprouvesJson) {

		logger.debug(
				"entered POST [droits/agentsSaisis] => setInputAgents with parameter idAgent = {} and idOperateur = {}",
				idAgent, idOperateur);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessAccessRights(convertedIdAgent))
			throw new AccessForbiddenException();

		int convertedIdOperateur = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idOperateur);

		if (accessRightService.findAgent(convertedIdOperateur) == null)
			throw new NotFoundException();

		List<AgentDto> agDtos = new JSONDeserializer<List<AgentDto>>().use(null, ArrayList.class)
				.use("values", AgentDto.class).deserialize(agentsApprouvesJson);

		accessRightService.setAgentsToInput(convertedIdAgent, convertedIdOperateur, agDtos);

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "delegataire", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setDelegate(@RequestParam("idAgent") Integer idAgent,
			@RequestBody String delegatorAndOperatorsDtoJson) {
		logger.debug("entered POST [droits/delegataire] => setDelegate with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		ReturnMessageDto result = new ReturnMessageDto();

		if (!accessRightService.canUserAccessAccessRights(convertedIdAgent)) {
			result.getErrors().add(String.format("L'agent [%s] n'est pas approbateur.", convertedIdAgent));
			String response = new JSONSerializer().exclude("*.class").deepSerialize(result);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		result = accessRightService.setDelegator(convertedIdAgent,
				new DelegatorAndOperatorsDto().deserializeFromJSON(delegatorAndOperatorsDtoJson), result);

		String response = new JSONSerializer().exclude("*.class").deepSerialize(result);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "isUserApprobateur", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> isUserApprobateur(@RequestParam("idAgent") Integer idAgent) {

		logger.debug("entered GET [droits/isUserApprobateur] => isUserApprobateur with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (accessRightService.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();

		if (accessRightService.isUserApprobateur(convertedIdAgent))
			return new ResponseEntity<String>(HttpStatus.OK);
		else
			return new ResponseEntity<String>(HttpStatus.CONFLICT);
	}

	@ResponseBody
	@RequestMapping(value = "isUserOperateur", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> isUserOperateur(@RequestParam("idAgent") Integer idAgent) {

		logger.debug("entered GET [droits/isUserOperateur] => isUserOperateur with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (accessRightService.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();

		if (accessRightService.isUserOperateur(convertedIdAgent))
			return new ResponseEntity<String>(HttpStatus.OK);
		else
			return new ResponseEntity<String>(HttpStatus.CONFLICT);
	}
}
