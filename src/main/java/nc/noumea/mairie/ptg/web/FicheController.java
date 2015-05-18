package nc.noumea.mairie.ptg.web;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import nc.noumea.mairie.ptg.dto.FichePointageListDto;
import nc.noumea.mairie.ptg.reporting.IReporting;
import nc.noumea.mairie.ptg.service.IPointageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lowagie.text.DocumentException;

@Controller
@RequestMapping("/fiches")
public class FicheController {

	@Autowired
	private IPointageService pointageService;

	@Autowired
	@Qualifier("FichePointageHebdoReporting")
	private IReporting fichePointageHebdoReporting;
	

	@ResponseBody
	@RequestMapping(value = "/xml/getFichesPointage", produces = "application/xml", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView getXmlFichesPointage(@RequestParam("csvIdAgents") String csvIdAgents,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") Date date) throws ParseException {

		FichePointageListDto fiches = pointageService.getFichesPointageForUsers(csvIdAgents, date);

		return new ModelAndView("xmlView", "object", fiches);
	}
	
	@ResponseBody
	@RequestMapping(value = "/pdf/getFichesPointage", produces = "application/xml", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getPdfFichesPointage(@RequestParam("csvIdAgents") String csvIdAgents,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") Date date) throws ParseException {

		try {
			fichePointageHebdoReporting.getFichePointageHebdoReporting();
		} catch (DocumentException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		} catch (IOException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		}


		return new ResponseEntity<String>(HttpStatus.OK);
	}
}
