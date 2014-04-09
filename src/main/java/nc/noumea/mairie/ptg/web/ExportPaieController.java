package nc.noumea.mairie.ptg.web;

import java.util.Date;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.SpWFPaie;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
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
	@RequestMapping(value = "/start", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(value = "chainedTransactionManager")
	public ResponseEntity<String> startExportPaie(
			@RequestParam("idAgent") Integer idAgent,
			@RequestParam("statut") String statut) {

		logger.debug("entered GET [exportPaie/start] => startExportPaie with parameters idAgent = {}, statut = {}",
				idAgent, statut);
		
		ReturnMessageDto result = exportPaieService.startExportToPaie(idAgent, AgentStatutEnum.valueOf(statut));
        
		String resultJson = new JSONSerializer().exclude("*.class").deepSerialize(result);
		
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
				.canStartExportPaieAction(helperService.getTypeChainePaieFromStatut(AgentStatutEnum.valueOf(statut)));
        
		String resultJson = new JSONSerializer().exclude("*.class").serialize(result);
		
		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/isExportPaie", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> isExportPaie(@RequestParam("statut") String statut) {

		logger.debug("entered GET [exportPaie/isExportPaie] => isExportPaie with parameter statut = {}", statut);
		
		CanStartWorkflowPaieActionDto result = exportPaieService
				.isExportPaieRunning(helperService.getTypeChainePaieFromStatut(AgentStatutEnum.valueOf(statut)));
        
		String resultJson = new JSONSerializer().exclude("*.class").serialize(result);
		
		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/processTask", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(value = "chainedTransactionManager")
	public ResponseEntity<String> processTask(
			@RequestParam("idExportPaieTask") Integer idExportPaieTask) {

		logger.debug("entered GET [exportPaie/processTask] => processTask with parameters idExportPaieTask = {}",
				idExportPaieTask);
		
		if (exportPaieService.findExportPaieTask(idExportPaieTask) == null)
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		
		exportPaieService.processExportPaieForAgent(idExportPaieTask);

        return new ResponseEntity<String>(HttpStatus.OK);
	}
	
	@ResponseBody
	@RequestMapping(value = "/stop", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(value = "chainedTransactionManager")
	public ResponseEntity<String> stopExportPaie(
			@RequestParam("typeChainePaie") String typeChainePaie) {

		logger.debug("entered GET [exportPaie/stop] => stopExportPaie with parameters typeChainePaie = {}", typeChainePaie);
		
		try {
			exportPaieService.stopExportToPaie(TypeChainePaieEnum.valueOf(typeChainePaie));
		} catch (WorkflowInvalidStateException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		}

        return new ResponseEntity<String>(HttpStatus.OK);
	}
}
