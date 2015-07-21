package nc.noumea.mairie.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefTypeSaisiDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class AbsWsConsumer extends BaseWsConsumer implements IAbsWsConsumer {

	private Logger logger = LoggerFactory.getLogger(AbsWsConsumer.class);

	@Autowired
	@Qualifier("sirhAbsWsBaseUrl")
	private String sirhAbsWsBaseUrl;

	private static final String addRecuperationsUrl = "recuperations/addForPTG";
	private static final String addRecuperationsProvisoireUrl = "recuperations/addProvisoireForPTG";
	private static final String addReposCompensateursUrl = "reposcomps/addForPTG";
	private static final String checkRecuperationsUrl = "recuperations/checkRecuperations";
	private static final String checkReposCompensateursUrl = "reposcomps/checkReposCompensateurs";
	private static final String checkAbsencesSyndicalesUrl = "asa/checkAbsencesSyndicales";
	private static final String checkCongesExceptionnelsUrl = "congeexceptionnel/checkCongesExceptionnels";
	private static final String checkCongesAnnuelsUrl = "congeannuel/checkCongesAnnuels";
	private static final String listeDemandesSIRHUrl = "demandes/listeDemandesSIRH";
	private static final String getTypeAbsenceUrl = "filtres/getTypesSaisi";

	@Override
	public void addRecuperationsToAgent(Integer idAgent, Date dateLundi, Integer minutes, Integer minutesNonMajorees) { 

		logger.info("Updating recuperations for Agent [{}] Date Monday [{}] with value [{}]...", idAgent, dateLundi,
				minutes);

		String url = String.format(sirhAbsWsBaseUrl + addRecuperationsUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("idAgent", String.valueOf(idAgent));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		parameters.put("dateLundi", sdf.format(dateLundi));
		parameters.put("minutes", String.valueOf(minutes));
		parameters.put("minutesNonMajorees", String.valueOf(minutesNonMajorees));

		ClientResponse res = createAndFirePostRequest(parameters, url);

		readResponse(res, url);
	}
	
	@Override
	public void addRecuperationsToCompteurProvisoireAgent(Integer idAgent, Date date, Integer minutes, Integer idPointage) { 

		logger.info("Updating recuperations for Agent [{}] Date [{}] with value [{}]...", idAgent, date,
				minutes);

		String url = String.format(sirhAbsWsBaseUrl + addRecuperationsProvisoireUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("idAgent", String.valueOf(idAgent));

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		parameters.put("date", sdf.format(date));
		parameters.put("minutes", String.valueOf(minutes));
		parameters.put("idPointage", String.valueOf(idPointage));

		ClientResponse res = createAndFirePostRequest(parameters, url);

		readResponse(res, url);
	}

	@Override
	public void addReposCompToAgent(Integer idAgent, Date dateLundi, Integer minutes) {

		logger.info("Updating repos compensateurs for Agent [{}] Date Monday [{}] with value [{}]...", idAgent,
				dateLundi, minutes);

		String url = String.format(sirhAbsWsBaseUrl + addReposCompensateursUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("idAgent", String.valueOf(idAgent));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		parameters.put("dateLundi", sdf.format(dateLundi));
		parameters.put("minutes", String.valueOf(minutes));

		ClientResponse res = createAndFirePostRequest(parameters, url);

		readResponse(res, url);

	}

	@Override
	public ReturnMessageDto checkRecuperation(Integer idAgent, Date dateDebut, Date dateFin) {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String url = String.format(sirhAbsWsBaseUrl + checkRecuperationsUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("dateDebut", sf.format(dateDebut));
		parameters.put("dateFin", sf.format(dateFin));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto checkReposComp(Integer idAgent, Date dateDebut, Date dateFin) {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String url = String.format(sirhAbsWsBaseUrl + checkReposCompensateursUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("dateDebut", sf.format(dateDebut));
		parameters.put("dateFin", sf.format(dateFin));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto checkAbsencesSyndicales(Integer idAgent, Date dateDebut, Date dateFin) {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String url = String.format(sirhAbsWsBaseUrl + checkAbsencesSyndicalesUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("dateDebut", sf.format(dateDebut));
		parameters.put("dateFin", sf.format(dateFin));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto checkCongesExceptionnels(Integer idAgent, Date dateDebut, Date dateFin) {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String url = String.format(sirhAbsWsBaseUrl + checkCongesExceptionnelsUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("dateDebut", sf.format(dateDebut));
		parameters.put("dateFin", sf.format(dateFin));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public ReturnMessageDto checkCongeAnnuel(Integer idAgent, Date dateDebut, Date dateFin) {
		SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String url = String.format(sirhAbsWsBaseUrl + checkCongesAnnuelsUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("dateDebut", sf.format(dateDebut));
		parameters.put("dateFin", sf.format(dateFin));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(ReturnMessageDto.class, res, url);
	}

	@Override
	public List<DemandeDto> getListCongeWithoutCongesAnnuelsEtAnnulesBetween(
			Integer idAgent, Date start, Date end) {
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		
		String url = String.format(sirhAbsWsBaseUrl + listeDemandesSIRHUrl);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgentRecherche", String.valueOf(idAgent));
		parameters.put("from", sf.format(start));
		parameters.put("to", sf.format(end));
		parameters.put("etat", "6"); // etat PRIS
		parameters.put("groupe", "4"); // conges exceptionnels uniquement
		parameters.put("aValider", "false");
		
		ClientResponse response = createAndFireGetRequest(parameters, url);
		
		return readResponseAsList(DemandeDto.class, response, url);
	}

	@Override
	public List<RefTypeSaisiDto> getTypeAbsence(Integer idRefTypeAbsence) {
		
		String url = String.format(sirhAbsWsBaseUrl + getTypeAbsenceUrl);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idRefTypeAbsence", String.valueOf(idRefTypeAbsence));
		
		ClientResponse response = createAndFireGetRequest(parameters, url);
		
		return readResponseAsList(RefTypeSaisiDto.class, response, url);
	}

}
