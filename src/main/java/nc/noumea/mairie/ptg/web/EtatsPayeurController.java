package nc.noumea.mairie.ptg.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.ListEtatsPayeurDto;
import nc.noumea.mairie.ptg.repository.IPaieWorkflowRepository;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IEtatPayeurService;
import nc.noumea.mairie.ptg.service.IExportEtatPayeurService;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.ptg.transformer.MSDateTransformer;
import nc.noumea.mairie.ptg.workflow.WorkflowInvalidStateException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import flexjson.JSONSerializer;

@Controller
@RequestMapping("/etatsPayeur")
public class EtatsPayeurController {

	private Logger logger = LoggerFactory.getLogger(EtatsPayeurController.class);

	@Autowired
	private IExportEtatPayeurService exportEtatPayeurService;

	@Autowired
	private IAccessRightsService accessRightService;

	@Autowired
	private HelperService helperService;

	@Autowired
	private IAgentMatriculeConverterService converterService;

	@Autowired
	private IEtatPayeurService etatPayeurService;

	@Autowired
	private IPaieWorkflowRepository paieWorkflowRepository;

	@ResponseBody
	@RequestMapping(value = "/xml/getEtatPayeur", produces = "application/xml", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView getXmlEtatsPayeur(@RequestParam("statut") String statutString) throws ParseException {

		logger.debug("entered GET [etatsPayeur/xml/getEtatPayeur] => getXmlEtatsPayeur with parameter statut = {}",
				statutString);

		AgentStatutEnum statut = AgentStatutEnum.valueOf(statutString);

		EtatPayeurDto result = exportEtatPayeurService.getEtatPayeurDataForStatut(statut);

		return new ModelAndView("xmlView", "object", result);
	}

	@ResponseBody
	@RequestMapping(value = "/canStartExportEtatsPayeur", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> canStartExportEtatsPayeur(@RequestParam("statut") String statut) {

		logger.debug(
				"entered GET [etatsPayeur/canStartExportEtatsPayeur] => canStartExportEtatsPayeur with parameter statut = {}",
				statut);

		CanStartWorkflowPaieActionDto result = exportEtatPayeurService.canStartExportEtatPayeurAction(helperService
				.getTypeChainePaieFromStatut(AgentStatutEnum.valueOf(statut)));

		String resultJson = new JSONSerializer().exclude("*.class").serialize(result);

		return new ResponseEntity<String>(resultJson, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/downloadFicheEtatsPayeur", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<byte[]> downloadFicheEtatsPayeur(@RequestParam("idEtatPayeur") Integer idEtatPayeur) {

		logger.debug(
				"entered GET [etatsPayeur/downloadFicheEtatsPayeur] => downloadFicheEtatsPayeur with parameters idEtatPayeur = {}",
				idEtatPayeur);

		Pair<String, String> pathFichier = null;
		try {
			pathFichier = etatPayeurService.getPathFichierEtatPayeur(idEtatPayeur);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		byte[] reponseData = null;
		try {
			FileInputStream newFile = new FileInputStream(Paths.get(pathFichier.getLeft(), pathFichier.getRight())
					.toString());
			reponseData = IOUtils.toByteArray(newFile);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/pdf");
		headers.add("Content-Disposition", String.format("attachment; filename=\"" + pathFichier.getRight() + "\""));

		return new ResponseEntity<byte[]>(reponseData, headers, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/listEtatsPayeur", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getListEtatsPayeurByStatut(
			@RequestParam(value = "statutAgent", required = true) String statutString) {

		logger.debug(
				"entered GET [etatsPayeur/listEtatsPayeur] => getListEtatsPayeurByStatut with parameters  statutAgent = {}",
				statutString);

		AgentStatutEnum statut = AgentStatutEnum.valueOf(statutString);

		List<ListEtatsPayeurDto> result = etatPayeurService.getListEtatsPayeurByStatut(statut);

		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);

		String response = new JSONSerializer().exclude("*.class").transform(new MSDateTransformer(), Date.class)
				.deepSerialize(result);

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/start", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(value = "chainedTransactionManager")
	public ResponseEntity<String> startExportEtatsPayeur(
			@RequestParam(value = "idAgent", required = true) Integer idAgentExporting,
			@RequestParam(value = "statut", required = true) String statutString) {

		logger.debug(
				"entered GET [etatsPayeur/runExportEtatsPayeur] => runExportEtatsPayeur with parameters  statut = {}",
				statutString);

		AgentStatutEnum statut = AgentStatutEnum.valueOf(statutString);

		ReturnMessageDto result = exportEtatPayeurService.startExportEtatsPayeur(idAgentExporting, statut);

		String response = new JSONSerializer().exclude("*.class").deepSerialize(result);

		if (result.getErrors().size() != 0)
			return new ResponseEntity<String>(response, HttpStatus.CONFLICT);
		try {
			logger.debug("LockMode : " + paieWorkflowRepository.getLockModeSpWFPaie());
		} catch (Exception e) {
			logger.debug("Erreur : paieWorkflowRepository BAD" + e.getMessage());
		}

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/startExportTask", method = RequestMethod.GET)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> startExportEtatsPayeurTask(
			@RequestParam(value = "idExportEtatsPayeurTask", required = true) Integer idExportEtatsPayeurTask) {

		logger.debug(
				"entered GET [etatsPayeur/startExportTask] => startExportEtatsPayeurTask with parameter idExportEtatsPayeurTask = {}",
				idExportEtatsPayeurTask);

		exportEtatPayeurService.exportEtatsPayeur(idExportEtatsPayeurTask);

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/finishExportTask", method = RequestMethod.GET)
	@Transactional(value = "chainedTransactionManager")
	public ResponseEntity<String> finishExportEtatsPayeurTask(
			@RequestParam(value = "idExportEtatsPayeurTask", required = true) Integer idExportEtatsPayeurTask)
			throws WorkflowInvalidStateException {

		logger.debug(
				"entered GET [etatsPayeur/finishExportTask] => finishExportEtatsPayeurTask with parameter idExportEtatsPayeurTask = {}",
				idExportEtatsPayeurTask);

		exportEtatPayeurService.journalizeEtatsPayeur(idExportEtatsPayeurTask);

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/stop", method = RequestMethod.GET)
	@Transactional(value = "chainedTransactionManager")
	public ResponseEntity<String> stopExportEtatsPayeur(@RequestParam("typeChainePaie") String typeChainePaie)
			throws WorkflowInvalidStateException {

		logger.debug(
				"entered GET [etatsPayeur/stopExportEtatsPayeur] => stopExportEtatsPayeur with parameter typeChainePaie = {}",
				typeChainePaie);

		try {
			exportEtatPayeurService.stopExportEtatsPayeur(TypeChainePaieEnum.valueOf(typeChainePaie));
		} catch (WorkflowInvalidStateException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		}
		try {
			logger.debug("LockMode : " + paieWorkflowRepository.getLockModeSpWFPaie());
		} catch (Exception e) {
			logger.debug("Erreur : paieWorkflowRepository BAD" + e.getMessage());
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
