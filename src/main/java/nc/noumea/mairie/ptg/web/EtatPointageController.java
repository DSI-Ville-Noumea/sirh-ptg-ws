package nc.noumea.mairie.ptg.web;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.service.IEtatPointageService;

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
@RequestMapping("/etatPointage")
public class EtatPointageController {

	private Logger logger = LoggerFactory.getLogger(EtatPointageController.class);

	@Autowired
	private IEtatPointageService etatPointageService;

	@ResponseBody
	@RequestMapping(value = "/majEtatPointagesByListId", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
	@Transactional(value = "ptgTransactionManager")
	public ResponseEntity<String> majEtatPointagesByListId(@RequestParam("idEtatPointage") Integer idEtatPointage, @RequestParam("etat") Integer etat) {
		
		logger.debug("entered GET [etatPointage/majEtatPointagesByListId] => processTask with parameters idEtatPointage, etat = {}", idEtatPointage, etat);
		
		ReturnMessageDto result = etatPointageService.majEtatPointagesByListId(idEtatPointage, EtatPointageEnum.getEtatPointageEnum(etat));
		
		if (result.getErrors().size() != 0) {
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
