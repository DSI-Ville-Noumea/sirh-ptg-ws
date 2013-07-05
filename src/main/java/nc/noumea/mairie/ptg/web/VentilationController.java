package nc.noumea.mairie.ptg.web;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.service.IVentilationService;

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

@Controller
@RequestMapping("/ventilation")
public class VentilationController {

	private Logger logger = LoggerFactory.getLogger(VentilationController.class);
	
	@Autowired
	private IVentilationService ventilationService;
	
	@ResponseBody
	@RequestMapping(value = "/run", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional("ptgTransactionManager")
	public ResponseEntity<String> runVentilation(
			@RequestParam("date") @DateTimeFormat(pattern = "YYYYMMdd") Date ventilationDate,
			@RequestParam(value = "agentFrom") Integer agentFrom,
			@RequestParam(value = "agentTo", required = false) Integer agentTo,
			@RequestParam(value = "typePointage", required = false) Integer idRefTypePointage,
			@RequestParam(value = "typeChainePaie") String typeChainePaie) {

		logger.debug(
				"entered GET [ventilation/run] => runVentilation with parameters date = {}, agentFrom = {}, agentTo = {}, typePointage = {}, typeChainePaie = {}",
				ventilationDate, agentFrom, agentTo, idRefTypePointage, typeChainePaie);

		ventilationService.processVentilation(9005138, agentFrom, agentTo, ventilationDate, TypeChainePaieEnum.valueOf(typeChainePaie), RefTypePointageEnum.getRefTypePointageEnum(idRefTypePointage));
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
