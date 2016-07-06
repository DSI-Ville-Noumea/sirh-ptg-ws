package nc.noumea.mairie.ptg.web;

/**
 * This class is the controller for Prime service
 *
 * @autor C Levointurier
 */
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.service.IPrimeService;

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
@RequestMapping("/primes")
public class PrimeController {

	private Logger logger = LoggerFactory.getLogger(PrimeController.class);
	@Autowired
	private IPrimeService primeService;
	@Autowired
	private IPointageService pointageService;

	@ResponseBody
	@RequestMapping(value = "/getListePrimeWithStatus", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getListePrime(@RequestParam("statutAgent") String statutAgent) {

		logger.debug("entered GET [primes/getListePrimeWithStatus] => getListePrime with parameters statsAgent = {}", statutAgent);

		List<RefPrimeDto> result = primeService.getPrimeListForAgent(AgentStatutEnum.valueOf(statutAgent));

		if (result.size() == 0) {
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		}
		String response = new JSONSerializer().exclude("*.class").deepSerialize(result);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/getListePrime", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getListePrime() {

		logger.debug("entered GET [primes/getListePrime] => getListePrime ");

		List<RefPrimeDto> result = primeService.getPrimeList();

		if (result.size() == 0) {
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		}
		String response = new JSONSerializer().exclude("*.class").deepSerialize(result);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/getPrime", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getPrimeFromNoRubr(@RequestParam("noRubr") Integer noRubr) {

		logger.debug("entered GET [primes/getPrime] => getPrimeFromNoRubr with parameter noRubr = {}", noRubr);

		RefPrimeDto result = primeService.getPrimeWithNorubr(noRubr);

		if (result == null) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		String response = new JSONSerializer().exclude("*.class").deepSerialize(result);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = { "/getPrimeFromIdRefPrime", "/detail" }, produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getPrimeFromIdRefPrime(@RequestParam("idRefPrime") Integer idRefPrime) {

		logger.debug("entered GET [primes/getPrimeFromIdRefPrime] or [primes/detail] => getPrimeFromIdRefPrime with parameters idRefPrime = {}", idRefPrime);

		RefPrimeDto result = primeService.getPrimeById(idRefPrime);
		
		if (result == null)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		
		return new ResponseEntity<String>(new JSONSerializer().exclude("*.class").deepSerialize(result), HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/isPrimeUtilisee", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> isPrimeUtilisee(@RequestParam("noRubr") Integer noRubr, @RequestParam("idAgent") Integer idAgent) {

		logger.debug("entered GET [primes/isPrimeUtilisee] => isPrimeUtilisee with parameters noRubr = {} and idAgent = {} --> for SIRH ", noRubr, idAgent);
		if (pointageService.isPrimeUtiliseePointage(idAgent, primeService.getPrimesId(noRubr))) {
			return new ResponseEntity<String>(HttpStatus.OK);
		} else {
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		}
	}
	
}
