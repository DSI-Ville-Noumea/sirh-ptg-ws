package nc.noumea.mairie.ptg.web;

import java.util.Date;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.SpWFPaie;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.service.IExportPaieService;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.ptg.transformer.MSDateTransformer;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;
import nc.noumea.mairie.ptg.workflow.WorkflowInvalidStateException;

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
@RequestMapping("/exportPaie")
public class ExportPaieController {

	private Logger logger = LoggerFactory.getLogger(VentilationController.class);
	
	@Autowired
	private IExportPaieService exportPaieService;
	
	@Autowired
	private IPaieWorkflowService paieWorkflowService;
	
	@Autowired
	private HelperService helperService;
	
	@ResponseBody
	@RequestMapping(value = "/run", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(value = "chainedTransactionManager")
	public ResponseEntity<String> runExportToPaie(
			@RequestParam("idAgent") Integer idAgent,
			@RequestParam("statut") String statut) {

		logger.debug("entered GET [exportPaie/run] => runExportToPaie with parameters idAgent = {}, statut = {}",
				idAgent, statut);
		
		ReturnMessageDto result = exportPaieService.exportToPaie(idAgent, AgentStatutEnum.valueOf(statut));
		
		if (result.getErrors().size() != 0)
			return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").serialize(result), HttpStatus.CONFLICT);
		
		return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").serialize(result), HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/start", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(value = "chainedTransactionManager")
	public ResponseEntity<String> startExportPaie(
			@RequestParam("idAgent") Integer idAgent,
			@RequestParam("statut") String statut) {

		logger.debug("entered GET [exportPaie/start] => startExportPaie with parameters idAgent = {}, statut = {}",
				idAgent, statut);
		
		ReturnMessageDto result = new ReturnMessageDto();
		
        try {
        	//TODO: develop real call to export paie service run method that will trigger the job
			paieWorkflowService.changeStateToExportPaieStarted(helperService.getTypeChainePaieFromStatut(AgentStatutEnum.valueOf(statut)));
		} catch (WorkflowInvalidStateException e) {
			result.getErrors().add(e.getMessage());
		}
        
		String resultJson = new JSONSerializer().exclude("*.class").serialize(result);
		
		if (result.getErrors().size() != 0)
			return new ResponseEntity<String>(resultJson, HttpStatus.CONFLICT);
		
		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/etat", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getExportPaieEtat(@RequestParam("statut") String statut) {

		logger.debug("entered GET [exportPaie/etat] => getExportPaieEtat with parameter statut = {}", statut);
		
		SpWFPaie etat = paieWorkflowService.getCurrentState(helperService.getTypeChainePaieFromStatut(AgentStatutEnum.valueOf(statut)));
        
		String resultJson = new JSONSerializer()
				.exclude("*.class")
				.transform(new MSDateTransformer(), Date.class)
				.serialize(etat);
		
		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/canStartExportPaie", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> canStartExportPaie(@RequestParam("statut") String statut) {

		logger.debug("entered GET [exportPaie/canStartExportPaie] => canStartExportPaie with parameter statut = {}", statut);
		
		CanStartWorkflowPaieActionDto result = exportPaieService
				.canStartExportPaieActionDto(helperService.getTypeChainePaieFromStatut(AgentStatutEnum.valueOf(statut)));
        
		String resultJson = new JSONSerializer().exclude("*.class").serialize(result);
		
		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}
}
