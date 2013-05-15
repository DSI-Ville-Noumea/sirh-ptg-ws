package nc.noumea.mairie.ptg.web;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IFichesService;
import nc.noumea.mairie.ptg.service.IReportingService;
import nc.noumea.mairie.sirh.domain.Agent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
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
@RequestMapping("/edition")
public class EditionController {

	private Logger logger = LoggerFactory.getLogger(EditionController.class);

	@Autowired
	private IAccessRightsService accessRightService;

	@Autowired
	private IAgentMatriculeConverterService converterService;

	@Autowired
	private IFichesService ficheService;

	@Autowired
	private IReportingService reportingService;

	@ResponseBody
	@RequestMapping(value = "listeFiches", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getFichesToPrint(@RequestParam("idAgent") Integer idAgent,
			@RequestParam("date") @DateTimeFormat(pattern = "YYYYMMdd") Date date,
			@RequestParam(value = "codeService", required = false) String codeService, @RequestParam(value = "agent", required = false) Integer agent) {

		logger.debug(
				"entered GET [edition/listeFiches] => getFichesToPrint with parameters idAgent = {}, date = {}, codeService = {} and agent = {}",
				idAgent, date, codeService, agent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		if (!accessRightService.canUserAccessPrint(convertedIdAgent))
			throw new AccessForbiddenException();

		if (Agent.findAgent(convertedIdAgent) == null)
			throw new NotFoundException();

		Integer convertedIdAgent2 = converterService.tryConvertFromADIdAgentToSIRHIdAgent(agent);

		List<AgentDto> agents = ficheService.listAgentsFichesToPrint(convertedIdAgent, date, codeService, convertedIdAgent2);

		String json = new JSONSerializer().exclude("*.class").serialize(agents);

		return new ResponseEntity<String>(json, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/downloadFichePointage", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<byte[]> downloadFichePointage(@RequestParam("idAgent") int idAgent,
			@RequestParam("date") @DateTimeFormat(pattern = "YYYYMMdd") Date date) {

		Integer convertedId = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);
		Agent ag = Agent.findAgent(convertedId);
		if (ag == null)
			throw new NotFoundException();

		byte[] responseData = null;

		try {
			responseData = reportingService.getFichePointageReportAsByteArray(idAgent, date);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/pdf");
		headers.add("Content-Disposition", String.format("attachment; filename=\"fichePointage_%s_%s.pdf\"", ag.getDisplayNom(), convertedId));

		return new ResponseEntity<byte[]>(responseData, headers, HttpStatus.OK);
	}
}
