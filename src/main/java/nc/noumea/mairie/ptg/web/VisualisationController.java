package nc.noumea.mairie.ptg.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.ConsultPointageDto;
import nc.noumea.mairie.ptg.dto.PointagesEtatChangeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IApprobationService;
import nc.noumea.mairie.ptg.transformer.MSDateTransformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/visualisation")
public class VisualisationController {

	private Logger logger = LoggerFactory.getLogger(VisualisationController.class);

	@Autowired
	private IApprobationService approbationService;

	@Autowired
	private IAgentMatriculeConverterService agentMatriculeConverterService;

	@Autowired
	private IAccessRightsService accessRightService;

	@ResponseBody
	@RequestMapping(value = "/pointages", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getListePointages(@RequestParam("idAgent") int idAgent,
			@RequestParam("from") @DateTimeFormat(pattern = "yyyyMMdd") Date fromDate,
			@RequestParam("to") @DateTimeFormat(pattern = "yyyyMMdd") Date toDate,
			@RequestParam(value = "idServiceADS", required = false) Integer idServiceADS,
			@RequestParam(value = "agent", required = false) Integer agent,
			@RequestParam(value = "etat", required = false) Integer idRefEtat,
			@RequestParam(value = "type", required = false) Integer idRefType,
			@RequestParam(value = "typeHS", required = false) String typeHS) {

		logger.debug(
				"entered GET [visualisation/pointages] => getListePointages with parameters idAgent = {}, from = {}, to = {}, idServiceAds = {}, agent = {}, etat = {} and type = {} and typeHS = {}",
				idAgent, fromDate, toDate, idServiceADS, agent, idRefEtat, idRefType, typeHS);

		Integer convertedIdAgent = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		Integer convertedAgent = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(agent);

		if (!accessRightService.canUserAccessVisualisation(convertedIdAgent))
			throw new AccessForbiddenException();

		List<ConsultPointageDto> result = approbationService.getPointages(convertedIdAgent, fromDate, toDate,
				idServiceADS, convertedAgent, idRefEtat, idRefType, typeHS);

		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

		String response = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(result);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/pointagesSIRH", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getListePointagesSIRH(
			@RequestParam("from") @DateTimeFormat(pattern = "yyyyMMdd") Date fromDate,
			@RequestParam("to") @DateTimeFormat(pattern = "yyyyMMdd") Date toDate,
			@RequestParam(value = "idAgents", required = false) String idAgents,
			@RequestParam(value = "etat", required = false) Integer idRefEtat,
			@RequestParam(value = "type", required = false) Integer idRefType,
			@RequestParam(value = "typeHS", required = false) String typeHS) {
		// TODO
		// traiter le cas de typeHS pour info SIRH renvoi R ou RS.
		logger.debug(
				"entered GET [visualisation/pointagesSIRH] => getListePointagesSIRH with parameters  from = {}, to = {},  idAgents = {}, etat = {} and type = {} and typeHS = {}",
				fromDate, toDate, idAgents, idRefEtat, idRefType, typeHS);

		List<Integer> agentIds = new ArrayList<Integer>();
		if (idAgents != null) {
			for (String id : idAgents.split(",")) {
				agentIds.add(id.equals("") ? 0 : Integer.valueOf(id));
			}
		}

		List<ConsultPointageDto> result = approbationService.getPointagesSIRH(fromDate, toDate, agentIds, idRefEtat,
				idRefType, typeHS);

		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

		String response = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(result);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/historique", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getPointageArchives(@RequestParam("idAgent") int idAgent,
			@RequestParam("idPointage") Integer idPointage) {

		logger.debug(
				"entered GET [visualisation/historique] => getPointageArchives with parameters idAgent = {} and idPointage = {}",
				idAgent, idPointage);

		Integer convertedIdAgent = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessVisualisation(convertedIdAgent))
			throw new AccessForbiddenException();

		List<ConsultPointageDto> result = approbationService.getPointagesArchives(idAgent, idPointage);

		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

		String response = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(result);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/changerEtats", produces = "application/json;charset=utf-8", consumes = "application/json", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setPointagesEtat(@RequestParam("idAgent") int idAgent,
			@RequestBody(required = true) String pointagesEtatChangeDtoString) {

		logger.debug("entered POST [visualisation/changerEtats] => setPointagesEtat with parameters idAgent = {}",
				idAgent);

		Integer convertedIdAgent = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessAppro(convertedIdAgent))
			throw new AccessForbiddenException();

		List<PointagesEtatChangeDto> dto = new JSONDeserializer<List<PointagesEtatChangeDto>>()
				.use(null, ArrayList.class).use("values", PointagesEtatChangeDto.class)
				.deserialize(pointagesEtatChangeDtoString);

		ReturnMessageDto result = approbationService.setPointagesEtat(convertedIdAgent, dto);

		String response = new JSONSerializer().exclude("*.class").deepSerialize(result);

		if (result.getErrors().size() != 0)
			return new ResponseEntity<String>(response, HttpStatus.CONFLICT);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/changerEtatsSIRH", produces = "application/json;charset=utf-8", consumes = "application/json", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setPointagesEtatSIRH(@RequestParam("idAgent") int idAgent,
			@RequestBody(required = true) String pointagesEtatChangeDtoString) {

		logger.debug(
				"entered POST [visualisation/changerEtatsSIRH] => setPointagesEtat with parameters idAgent = {} and ptgEtat={}",
				idAgent, pointagesEtatChangeDtoString);

		List<PointagesEtatChangeDto> dto = new JSONDeserializer<List<PointagesEtatChangeDto>>()
				.use(null, ArrayList.class).use("values", PointagesEtatChangeDto.class)
				.deserialize(pointagesEtatChangeDtoString);

		ReturnMessageDto result = approbationService
				.setPointagesEtatSIRH(idAgent, dto);
		String response = new JSONSerializer().exclude("*.class").deepSerialize(result);

		if (result.getErrors().size() != 0)
			return new ResponseEntity<String>(response, HttpStatus.CONFLICT);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/historiqueSIRH", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getPointageArchives(@RequestParam("idPointage") Integer idPointage) {

		logger.debug(
				"entered GET [visualisation/historiqueSIRH] => getPointageArchives with parameter idPointage = {}",
				idPointage);

		List<ConsultPointageDto> result = approbationService.getPointagesArchives(idPointage);
		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		String response = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(result);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/listeAgentsPointagesForSIRH", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> listeAgentsPointagesForSIRH() {

		logger.debug("entered GET [visualisation/listeAgentsPointagesForSIRH] => listeAgentsPointagesForSIRH with no parameter ");

		List<AgentDto> result = approbationService.listerTousAgentsPointages();

		String response = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(result);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

}
