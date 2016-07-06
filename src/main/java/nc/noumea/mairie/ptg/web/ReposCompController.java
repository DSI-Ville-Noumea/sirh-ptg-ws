package nc.noumea.mairie.ptg.web;

import nc.noumea.mairie.ptg.service.IReposCompService;

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
@RequestMapping("/reposcomp")
public class ReposCompController {

	private Logger logger = LoggerFactory.getLogger(ReposCompController.class);
	
	@Autowired
	private IReposCompService reposCompService;
	
	@ResponseBody
	@RequestMapping(value = "/startReposCompTask", method = RequestMethod.GET)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> startReposCompTask(
			@RequestParam(value = "idReposCompTask", required = true) Integer idReposCompTask) {

		logger.debug(
				"entered GET [reposcomp/startReposCompTask] => startReposCompTask with parameter idReposCompTask = {}",
				idReposCompTask);
		
		reposCompService.processReposCompTask(idReposCompTask);
		
		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
