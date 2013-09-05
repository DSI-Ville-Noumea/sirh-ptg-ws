package nc.noumea.mairie.ptg.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilTask;
import nc.noumea.mairie.ptg.dto.CanStartVentilationDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.VentilDateDto;
import nc.noumea.mairie.ptg.dto.VentilDto;
import nc.noumea.mairie.ptg.service.IVentilationService;
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

	private Logger logger = LoggerFactory
			.getLogger(VentilationController.class);

	@Autowired
	private IVentilationService ventilationService;

	@ResponseBody
	@RequestMapping(value = "/run", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional("ptgTransactionManager")
	public ResponseEntity<String> runVentilation(
			@RequestParam("idAgent") Integer idAgent,
			@RequestParam("date") @DateTimeFormat(pattern = "YYYYMMdd") Date ventilationDate,
			@RequestParam(value = "typePointage", required = false) Integer idRefTypePointage,
			@RequestParam(value = "statut") String statut,
			@RequestBody String agentsJson) {

		logger.debug(
				"entered POST [ventilation/run] => runVentilation with parameters date = {}, agents = {}, typePointage = {}, statut = {}",
				ventilationDate, agentsJson, idRefTypePointage, statut);

		// Deserializing integer list
		List<Integer> agents = new JSONDeserializer<List<Integer>>()
				.use(null, ArrayList.class).use("values", Integer.class)
				.deserialize(agentsJson);

		// Running ventilation
		ReturnMessageDto result = ventilationService.processVentilation(
				idAgent, agents, ventilationDate,
				AgentStatutEnum.valueOf(statut),
				RefTypePointageEnum.getRefTypePointageEnum(idRefTypePointage));

		if (result.getErrors().size() != 0) {
			return new ResponseEntity<String>(new JSONSerializer().exclude(
					"*.class").serialize(result), HttpStatus.CONFLICT);
		}

		return new ResponseEntity<String>(new JSONSerializer().exclude(
				"*.class").serialize(result), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/start", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> startVentilation(
			@RequestParam("idAgent") Integer idAgent,
			@RequestParam("date") @DateTimeFormat(pattern = "YYYYMMdd") Date ventilationDate,
			@RequestParam(value = "typePointage", required = false) Integer idRefTypePointage,
			@RequestParam(value = "statut") String statut,
			@RequestBody String agentsJson) {

		logger.debug(
				"entered POST [ventilation/start] => startVentilation with parameters date = {}, agents = {}, typePointage = {}, statut = {}",
				ventilationDate, agentsJson, idRefTypePointage, statut);

		// Deserializing integer list
		List<Integer> agents = new JSONDeserializer<List<Integer>>()
				.use(null, ArrayList.class).use("values", Integer.class)
				.deserialize(agentsJson);

		ReturnMessageDto result = ventilationService.startVentilation(idAgent,
				agents, ventilationDate, AgentStatutEnum.valueOf(statut),
				RefTypePointageEnum.getRefTypePointageEnum(idRefTypePointage));

		String resultJson = new JSONSerializer().exclude("*.class")
				.deepSerialize(result);

		if (result.getErrors().size() != 0) {
			return new ResponseEntity<String>(resultJson, HttpStatus.CONFLICT);
		}

		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/canStartVentilation", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> canStartVentilation(
			@RequestParam("statut") String statut) {

		logger.debug(
				"entered GET [ventilation/canStartVentilation] => canStartVentilation with parameter statut = {}",
				statut);

		CanStartVentilationDto result = ventilationService
				.canStartVentilationForAgentStatus(AgentStatutEnum
						.valueOf(statut));

		String resultJson = new JSONSerializer().exclude("*.class").serialize(
				result);

		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/processTask", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> processTask(
			@RequestParam("idVentilTask") Integer idVentilTask) {

		logger.debug(
				"entered GET [ventilation/processTask] => processTask with parameters idAgent = {}",
				idVentilTask);

		if (VentilTask.findVentilTask(idVentilTask) == null)
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);

		ventilationService.processVentilationForAgent(idVentilTask);

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/show", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional("ptgTransactionManager")
	public ResponseEntity<String> showVentilation(
			@RequestParam("idDateVentil") Integer idDateVentil,
			@RequestParam("csvIdAgents") String csvIdAgents,
			@RequestParam("typePointage") Integer idRefTypePointage) {

		RefTypePointageEnum typepointage = RefTypePointageEnum
				.getRefTypePointageEnum(idRefTypePointage);
		logger.debug(
				"entered GET [ventilation/show] => showVentilation with parameters idDateVentil = {}, agents = {}, typePointage = {}",
				idDateVentil, csvIdAgents, typepointage.name());

		// Deserializing integer list
		List<Integer> agents = new ArrayList<>();

		for (String ag : csvIdAgents.split(",")) {
			agents.add(Integer.parseInt(ag));
		}

		List<VentilDto> result = ventilationService.showVentilation(
				idDateVentil, agents, typepointage);
		if (result.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<>(new JSONSerializer().exclude("*.class")
				.transform(new MSDateTransformer(), Date.class)
				.deepSerialize(result), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/getVentilationEnCours", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getVentilationEnCours(
			@RequestParam("statut") String statut) {

		logger.debug(
				"entered GET [ventilation/getVentilationEnCours] => getVentilationEnCours with parameter statut = {}",
				statut);

		VentilDateDto result = ventilationService
				.getVentilationEnCoursForStatut(AgentStatutEnum.valueOf(statut));

		if (result.getDateVentil() == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}
		String resultJson = new JSONSerializer().exclude("*.class")
				.transform(new MSDateTransformer(), Date.class)
				.serialize(result);

		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}
}
