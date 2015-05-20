package nc.noumea.mairie.ptg.web;

import java.text.ParseException;
import java.util.Date;

import nc.noumea.mairie.ptg.dto.FichePointageListDto;
import nc.noumea.mairie.ptg.reporting.FichePointageHebdoReporting;
import nc.noumea.mairie.ptg.service.IPointageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/fiches")
public class FicheController {

	@Autowired
	private IPointageService pointageService;

	@Autowired
	@Qualifier("FichePointageHebdoReporting")
	private FichePointageHebdoReporting fichePointageHebdoReporting;

	@ResponseBody
	@RequestMapping(value = "/xml/getFichesPointage", produces = "application/xml", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView getXmlFichesPointage(@RequestParam("csvIdAgents") String csvIdAgents,
			@RequestParam("date") @DateTimeFormat(pattern = "yyyyMMdd") Date date) throws ParseException {

		FichePointageListDto fiches = pointageService.getFichesPointageForUsers(csvIdAgents, date);

		return new ModelAndView("xmlView", "object", fiches);
	}
}
