package nc.noumea.mairie.ptg.web;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.repository.IPointageRepository;

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
@RequestMapping("/fiches")
public class FicheController {

	private Logger logger = LoggerFactory.getLogger(FicheController.class);
	
	@Autowired
	private IPointageRepository pointageRepository;
	
	@ResponseBody
	@RequestMapping(value = "parentPointages", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getParentPointages(@RequestParam("idPointage") int idPointage) {
		
		String response = new JSONSerializer().serialize(pointageRepository.getIdPointagesParents(Pointage.findPointage(idPointage)));
		
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
}
