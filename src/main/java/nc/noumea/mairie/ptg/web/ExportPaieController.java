package nc.noumea.mairie.ptg.web;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.service.IExportPaieService;

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

@Controller
@RequestMapping("/exportPaie")
public class ExportPaieController {

private Logger logger = LoggerFactory.getLogger(VentilationController.class);
	
	@Autowired
	private IExportPaieService exportPaieService;
	
	@ResponseBody
	@RequestMapping(value = "/run", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional("ptgTransactionManager")
	public ResponseEntity<String> runExportToPaie(
			@RequestParam("idAgent") Integer idAgent,
			@RequestParam("statut") String statut) {

		logger.debug(
				"entered GET [exportPaie/run] => runExportToPaie with parameters idAgent = {}, statut = {}",
				idAgent, statut);

		// Running exportPaieService
		exportPaieService.exportToPaie(idAgent, AgentStatutEnum.valueOf(statut));
		
		return new ResponseEntity<String>(HttpStatus.OK);
	} 
}
