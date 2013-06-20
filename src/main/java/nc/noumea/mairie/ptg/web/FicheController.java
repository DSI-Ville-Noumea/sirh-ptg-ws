package nc.noumea.mairie.ptg.web;

import java.text.ParseException;
import java.util.Date;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.dto.FichePointageListDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IPointageService;

import org.springframework.beans.factory.annotation.Autowired;
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

import flexjson.JSONSerializer;

@Controller
@RequestMapping("/fiches")
public class FicheController {

	@Autowired
	private IPointageRepository pointageRepository;

	@Autowired
	private IPointageService pointageService;

	@ResponseBody
	@RequestMapping(value = "parentPointages", produces = "application/json;charset=utf-8", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ResponseEntity<String> getParentPointages(@RequestParam("idPointage") int idPointage) {

		String response = new JSONSerializer().serialize(pointageRepository.getIdPointagesParents(Pointage.findPointage(idPointage)));

		return new ResponseEntity<String>(response, HttpStatus.OK);
	}

	@ResponseBody
	@RequestMapping(value = "/xml/getFichesPointage", produces = "application/xml", method = RequestMethod.GET)
	@Transactional(readOnly = true)
	public ModelAndView getXmlFichesPointage(@RequestParam("csvIdAgents") String csvIdAgents,
			@RequestParam("date") @DateTimeFormat(pattern = "YYYYMMdd") Date date) throws ParseException {

		FichePointageListDto fiches = pointageService.getFichesPointageForUsers(csvIdAgents, date);

		return new ModelAndView("xmlView", "object", fiches);
	}
}
