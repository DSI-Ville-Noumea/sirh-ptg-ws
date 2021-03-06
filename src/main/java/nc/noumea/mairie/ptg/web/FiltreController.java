package nc.noumea.mairie.ptg.web;

import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.MotifHeureSupDto;
import nc.noumea.mairie.ptg.dto.RefEtatDto;
import nc.noumea.mairie.ptg.dto.RefTypeAbsenceDto;
import nc.noumea.mairie.ptg.dto.RefTypePointageDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IPointageService;
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
	public List<EntiteDto> getServices(@RequestParam("idAgent") Integer idAgent) {

		logger.debug("entered GET [filtres/services] => getServices with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		List<EntiteDto> services = accessRightsService.getAgentsServicesToApproveOrInput(convertedIdAgent, new Date(), false);

		if (services.size() == 0)
			throw new NoContentException();

		return services;
	}

	@ResponseBody
	@RequestMapping(value = "/servicesWithPrimeDpm", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public List<EntiteDto> getServicesWithPrimeDpm(@RequestParam("idAgent") Integer idAgent) {

		logger.debug("entered GET [filtres/servicesWithPrimeDpm] => getServicesWithPrimeDpm with parameter idAgent = {}", idAgent);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		List<EntiteDto> services = accessRightsService.getAgentsServicesToApproveOrInput(convertedIdAgent, new Date(), true);

		if (services.size() == 0)
			throw new NoContentException();

		return services;
	}

	@ResponseBody
	@RequestMapping(value = "/agents", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getAgents(@RequestParam("idAgent") Integer idAgent, @RequestParam(value = "idServiceADS", required = false) Integer idServiceADS) {

		logger.debug("entered GET [filtres/agents] => getAgents with parameter idAgent = {} and idServiceADS = {}", idAgent, idServiceADS);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		List<AgentDto> services = accessRightsService.getAgentsToApproveOrInput(convertedIdAgent, idServiceADS, new Date(), false);

		if (services.size() == 0)
			throw new NoContentException();

		String json = new JSONSerializer().exclude("*.class").serialize(services);

		return new ResponseEntity<String>(json, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/agentsWithPrimeDpm", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getAgentsWithPrimeDpm(@RequestParam("idAgent") Integer idAgent, @RequestParam(value = "idServiceADS", required = false) Integer idServiceADS) {

		logger.debug("entered GET [filtres/agents] => getAgents with parameter idAgent = {} and idServiceADS = {}", idAgent, idServiceADS);

		int convertedIdAgent = converterService.tryConvertFromADIdAgentToSIRHIdAgent(idAgent);

		List<AgentDto> services = accessRightsService.getAgentsToApproveOrInput(convertedIdAgent, idServiceADS, new Date(), true);

		if (services.size() == 0)
			throw new NoContentException();

		String json = new JSONSerializer().exclude("*.class").serialize(services);

		return new ResponseEntity<String>(json, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/getTypesAbsence", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getTypesAbsence() {

		logger.debug("entered GET [filtres/getTypesAbsence] => getTypesAbsence");

		List<RefTypeAbsenceDto> types = pointageService.getRefTypeAbsence();

		String json = new JSONSerializer().exclude("*.class").serialize(types);

		return new ResponseEntity<String>(json, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/getMotifHsup", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getMotifHsup() {

		logger.debug("entered GET [filtres/getMotifHsup] => getMotifHsup");

		List<MotifHeureSupDto> motifs = pointageService.getMotifHeureSup();

		String json = new JSONSerializer().exclude("*.class").serialize(motifs);

		return new ResponseEntity<String>(json, HttpStatus.OK);
	}

	/**
	 * Saisie/modification d un motif pour les heures supplémentaires
	 */
	@ResponseBody
	@RequestMapping(value = "/setMotifHsup", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> setMotifHsup(@RequestBody(required = true) String motifHeureSupDto) {

		logger.debug("entered POST [filtres/setMotifHsup] => setMotifHsup");

		MotifHeureSupDto dto = new JSONDeserializer<MotifHeureSupDto>().use(Date.class, new MSDateTransformer()).deserializeInto(motifHeureSupDto, new MotifHeureSupDto());

		ReturnMessageDto srm = pointageService.setMotifHeureSup(dto);

		if (!srm.getErrors().isEmpty()) {
			return new ResponseEntity<String>(HttpStatus.CONFLICT);
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}

}
