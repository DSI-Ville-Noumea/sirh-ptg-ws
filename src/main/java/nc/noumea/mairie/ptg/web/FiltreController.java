package nc.noumea.mairie.ptg.web;

import java.util.List;

import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.RefTypePointageDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IPointageService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/filtres")
public class FiltreController {

	private Logger logger = LoggerFactory.getLogger(FiltreController.class);

	@Autowired
	private IPointageService pointageService;
	
	@Autowired
	private IAccessRightsService accessRightsService;

	@Autowired
	private IAgentMatriculeConverterService converterService;

	@ResponseBody
	@RequestMapping(value = "/getEtats", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getEtats() {

		logger.debug("entered GET [filtres/getEtats] => getEtats");

		List<RefEtatDto> etats = pointageService.getRefEtats();

		String json = new JSONSerializer().exclude("*.class").serialize(etats);

		return new ResponseEntity<String>(json, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/getTypes", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getTypes() {

		logger.debug("entered GET [filtres/getTypes] => getTypes");

		List<RefTypePointageDto> types = pointageService.getRefTypesPointage();

		String json = new JSONSerializer().exclude("*.class").serialize(types);

		return new ResponseEntity<String>(json, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/services", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> geServices(@RequestParam("idAgent") Integer idAgent) {

		logger.debug("entered GET [filtres/services] => geServices with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		
		List<ServiceDto> services = accessRightsService.getAgentsServicesToApproveOrInput(convertedIdAgent);

		if (services.size() == 0)
			throw new NoContentException();
		
		String json = new JSONSerializer().exclude("*.class").serialize(services);

		return new ResponseEntity<String>(json, HttpStatus.OK);
	}
}
