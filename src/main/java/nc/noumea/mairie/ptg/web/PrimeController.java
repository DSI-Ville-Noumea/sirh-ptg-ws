package nc.noumea.mairie.ptg.web;

/**
 * This class is the controller for Prime service
 * @autor C Levointurier
 */
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
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

	@ResponseBody
	@RequestMapping(value = "/getListePrimeWithStatus", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getListePrime(@RequestParam("statutAgent") String statutAgent) {

		logger.debug("entered GET [primes] => getListePrime with parameters statsAgent = {}", statutAgent);

		List<RefPrimeDto> result = primeService.getPrimeListForAgent(AgentStatutEnum.valueOf(statutAgent));

		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		String response = new JSONSerializer().exclude("*.class").deepSerialize(result);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/getListePrime", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getListePrime() {

		logger.debug("entered GET [primes] => getListePrime ");

		List<RefPrimeDto> result = primeService.getPrimeList();

		if (result.size() == 0)
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		String response = new JSONSerializer().exclude("*.class").deepSerialize(result);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

}
