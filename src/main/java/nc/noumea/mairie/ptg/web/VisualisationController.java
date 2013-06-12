package nc.noumea.mairie.ptg.web;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;

import nc.noumea.mairie.ptg.dto.ConsultPointageDto;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	public ResponseEntity<String> getListePointages(
			@RequestParam("idAgent") int idAgent,
			@RequestParam("from") @DateTimeFormat(pattern = "YYYYMMdd") Date fromDate,
			@RequestParam("to") @DateTimeFormat(pattern = "YYYYMMdd") Date toDate, 
			@RequestParam(value = "codeService", required = false) String codeService,
			@RequestParam(value = "agent", required = false) Integer agent,
			@RequestParam(value = "etat", required = false) Integer idRefEtat,
			@RequestParam(value = "type", required = false) Integer idRefType) {

		logger.debug(
				"entered GET [visualisation/pointages] => getListePointages with parameters idAgent = {}, from = {}, to = {}, codeService = {}, agent = {}, etat = {} and type = {}",
				idAgent, fromDate, toDate, codeService, agent, idRefEtat, idRefType);

		Integer convertedIdAgent = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		Integer convertedAgent = agentMatriculeConverterService.tryConvertFromADIdAgentToSIRHIdAgent(agent);
		
		if (!accessRightService.canUserAccessVisualisation(convertedIdAgent))
			throw new AccessForbiddenException();
		
		List<ConsultPointageDto> result = approbationService.getPointages(convertedIdAgent, fromDate, toDate, codeService, convertedAgent, idRefEtat, idRefType);
		
		if (result.size() == 0)
			throw new NoResultException();
		
		String response = new JSONSerializer().exclude("*.class")
				.transform(new MSDateTransformer(), Date.class)
				.deepSerialize(result);
		
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
}
