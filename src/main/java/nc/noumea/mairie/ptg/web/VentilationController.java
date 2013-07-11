package nc.noumea.mairie.ptg.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.service.IVentilationService;

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

@Controller
@RequestMapping("/ventilation")
public class VentilationController {

	private Logger logger = LoggerFactory.getLogger(VentilationController.class);
	
	@Autowired
	private IVentilationService ventilationService;
	
	@ResponseBody
	@RequestMapping(value = "/run", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional("ptgTransactionManager")
	public ResponseEntity<String> runVentilation(
			@RequestParam("date") @DateTimeFormat(pattern = "YYYYMMdd") Date ventilationDate,
			@RequestParam(value = "typePointage", required = false) Integer idRefTypePointage,
			@RequestParam(value = "statut") String statut,
			@RequestBody String agentsJson) {

		logger.debug(
				"entered POST [ventilation/run] => runVentilation with parameters date = {}, agents = {}, typePointage = {}, statut = {}",
				ventilationDate, agentsJson, idRefTypePointage, statut);

		// Deserializing integer list
		List<Integer> agents = new ArrayList<Integer>();
		new JSONDeserializer<List<Integer>>().use(null, ArrayList.class).use("values", Integer.class).deserialize(agentsJson);
		
		// Running ventilation
		ventilationService.processVentilation(9005138, agents, ventilationDate, AgentStatutEnum.valueOf(statut), RefTypePointageEnum.getRefTypePointageEnum(idRefTypePointage));
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
