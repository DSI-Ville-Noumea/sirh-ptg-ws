package nc.noumea.mairie.ptg.web;

import java.util.List;

import nc.noumea.mairie.ptg.dto.ServiceDto;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.ws.ISirhWSConsumer;

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
public class FiltersController {

	private Logger logger = LoggerFactory.getLogger(FiltersController.class);
	
	@Autowired
	private IAgentMatriculeConverterService converterService;
	
	@Autowired
	private ISirhWSConsumer sirhWsConsumer;
	
	@ResponseBody
	@RequestMapping(value = "listeServices", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> listAgentServices(@RequestParam("idAgent") Integer idAgent) {
		
		logger.debug("entered GET [filtres/listeServices] => listAgentServices with parameter idAgent = {}", idAgent);
		
		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		
		if (Agent.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();

		ServiceDto agentDivision = sirhWsConsumer.getAgentDivision(convertedIdAgent);
		List<ServiceDto> services = sirhWsConsumer.getSousServices(agentDivision.getService());
		
		String json = new JSONSerializer().exclude("*.class").deepSerialize(services);
		
		return new ResponseEntity<String>(json, HttpStatus.OK);
	}
}
