package nc.noumea.mairie.ptg.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.CanStartVentilationDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.VentilDateDto;
import nc.noumea.mairie.ptg.dto.VentilDto;
import nc.noumea.mairie.ptg.dto.VentilErreurDto;
import nc.noumea.mairie.ptg.service.IVentilationService;
import nc.noumea.mairie.ptg.service.impl.HelperService;
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
@RequestMapping("/ventilation")
public class VentilationController {

	private Logger logger = LoggerFactory.getLogger(VentilationController.class);

	@Autowired
	private IVentilationService ventilationService;
	
	@Autowired
	private HelperService helperService;

	@ResponseBody
	@RequestMapping(value = "/start", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> startVentilation(@RequestParam("idAgent") Integer idAgent,
			@RequestParam("date") @DateTimeFormat(pattern = "YYYYMMdd") Date ventilationDate,
			@RequestParam(value = "typePointage", required = false) Integer idRefTypePointage,
			@RequestParam(value = "statut") String statut, @RequestBody String agentsJson) {

		logger.debug(
				"entered POST [ventilation/start] => startVentilation with parameters date = {}, agents = {}, typePointage = {}, statut = {}",
				ventilationDate, agentsJson, idRefTypePointage, statut);

		// Deserializing integer list
		List<Integer> agents = new JSONDeserializer<List<Integer>>().use(null, ArrayList.class)
				.use("values", Integer.class).deserialize(agentsJson);

		ReturnMessageDto result = ventilationService.startVentilation(idAgent, agents, ventilationDate,
				AgentStatutEnum.valueOf(statut), RefTypePointageEnum.getRefTypePointageEnum(idRefTypePointage));

		String resultJson = new JSONSerializer().exclude("*.class").deepSerialize(result);

		if (result.getErrors().size() != 0) {
			return new ResponseEntity<String>(resultJson, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/canStartVentilation", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> canStartVentilation(@RequestParam("statut") String statut) {

		logger.debug("entered GET [ventilation/canStartVentilation] => canStartVentilation with parameter statut = {}",
				statut);

		CanStartVentilationDto result = ventilationService.canStartVentilationForAgentStatus(helperService
				.getTypeChainePaieFromStatut(AgentStatutEnum.valueOf(statut)));

		String resultJson = new JSONSerializer().exclude("*.class").serialize(result);

		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/isVentilation", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> isVentilation(@RequestParam("statut") String statut) {

		logger.debug("entered GET [ventilation/isVentilation] => isVentilation with parameter statut = {}",
				statut);

		CanStartVentilationDto result = ventilationService.isVentilationRunning(helperService
				.getTypeChainePaieFromStatut(AgentStatutEnum.valueOf(statut)));

		String resultJson = new JSONSerializer().exclude("*.class").serialize(result);

		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/processTask", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> processTask(@RequestParam("idVentilTask") Integer idVentilTask) {

		logger.debug("entered GET [ventilation/processTask] => processTask with parameters idVentilTask = {}",
				idVentilTask);

		if (ventilationService.findVentilTask(idVentilTask) == null)
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);

		ventilationService.processVentilationForAgent(idVentilTask);

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/getVentilationEnCours", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getVentilationEnCours(@RequestParam("statut") String statut) {

		logger.debug(
				"entered GET [ventilation/getVentilationEnCours] => getVentilationEnCours with parameter statut = {}",
				statut);

		VentilDateDto result = ventilationService.getVentilationEnCoursForStatut(AgentStatutEnum.valueOf(statut));

		if (result.getDateVentil() == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		String resultJson = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.serialize(result);

		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/getErreursVentilation", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getErreursVentilation(@RequestParam("statut") String statut) {

		logger.debug(
				"entered GET [ventilation/getErreursVentilation] => getErreursVentilation with parameter statut = {}",
				statut);

		List<VentilErreurDto> result = ventilationService.getErreursVentilation(AgentStatutEnum.valueOf(statut));

		if (0 == result.size()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(new JSONSerializer().exclude("*.class")
				.transform(new MSDateTransformer(), Date.class).deepSerialize(result), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/show", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional("ptgTransactionManager")
	public ResponseEntity<String> showVentilation(@RequestParam("idDateVentil") Integer idDateVentil,
			@RequestParam("typePointage") Integer idRefTypePointage, @RequestBody(required = true) String agentsJson) {

		RefTypePointageEnum typepointage = RefTypePointageEnum.getRefTypePointageEnum(idRefTypePointage);
		logger.debug(
				"entered POST [ventilation/show] => showVentilation with parameters idDateVentil = {}, agents = {}, typePointage = {}",
				idDateVentil, agentsJson, typepointage.name());

		// Deserializing integer list
		List<Integer> agents = new JSONDeserializer<List<Integer>>().use(null, ArrayList.class)
				.use("values", Integer.class).deserialize(agentsJson);

		List<VentilDto> result = ventilationService.showVentilation(idDateVentil, agents, typepointage);
		if (result.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(new JSONSerializer().exclude("*.class")
				.transform(new MSDateTransformer(), Date.class).deepSerialize(result), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/showHistory", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional("ptgTransactionManager")
	public ResponseEntity<String> showVentilationHistory(@RequestParam("mois") Integer mois,
			@RequestParam("annee") Integer annee, @RequestParam("typePointage") Integer idRefTypePointage,
			@RequestParam("idAgent") Integer idAgent) {

		RefTypePointageEnum typepointage = RefTypePointageEnum.getRefTypePointageEnum(idRefTypePointage);
		logger.debug(
				"entered GET [ventilation/showHistory] => showVentilationHistory with parameters mois = {}, annee = {}, idAgent = {}, typePointage = {}",
				mois, annee, idAgent, typepointage.name());

		List<VentilDto> result = ventilationService.showVentilationHistory(mois, annee, idAgent, typepointage);
		if (result.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(new JSONSerializer().exclude("*.class")
				.transform(new MSDateTransformer(), Date.class).deepSerialize(result), HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/listeAgentsToShowVentilation", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional("ptgTransactionManager")
	public ResponseEntity<String> getListeAgentsToShowVentilation(@RequestParam("idDateVentil") Integer idDateVentil,
			@RequestParam("typePointage") Integer idRefTypePointage, @RequestParam("statut") String statut, 
			@RequestParam("agentMin") Integer agentMin, @RequestParam("agentMax") Integer agentMax, 
			@RequestParam("ventilationDate") @DateTimeFormat(pattern = "YYYYMMdd") Date ventilationDate) {
		
		RefTypePointageEnum typepointage = RefTypePointageEnum.getRefTypePointageEnum(idRefTypePointage);
		AgentStatutEnum statutEnum = AgentStatutEnum.valueOf(statut);
		logger.debug(
				"entered GET [ventilation/listeAgents] => getListeAgents with parameters idDateVentil = {}, typePointage = {}, statut = {}, agentMin = {}, agentMax = {}",
				idDateVentil, typepointage.name(), statut, agentMin, agentMax);

		List<Integer> result = ventilationService.getListeAgentsToShowVentilation(idDateVentil, idRefTypePointage, statutEnum, agentMin, agentMax, ventilationDate);
		if (result.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(new JSONSerializer().exclude("*.class").deepSerialize(result), HttpStatus.OK);
	}
	
}
