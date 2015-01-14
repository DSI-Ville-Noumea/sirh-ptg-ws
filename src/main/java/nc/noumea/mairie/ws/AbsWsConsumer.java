package nc.noumea.mairie.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	private static final String addReposCompensateursUrl = "reposcomps/addForPTG";
	private static final String checkRecuperationsUrl = "recuperations/checkRecuperations";
	private static final String checkReposCompensateursUrl = "reposcomps/checkReposCompensateurs";
	private static final String checkAbsencesSyndicalesUrl = "asa/checkAbsencesSyndicales";
	private static final String checkCongesExceptionnelsUrl = "congeexceptionnel/checkCongesExceptionnels";
	private static final String checkCongesAnnuelsUrl = "congeannuel/checkCongesAnnuels";

	@Override
	public void addRecuperationsToAgent(Integer idAgent, Date dateLundi, Integer minutes) {

		logger.info("Updating recuperations for Agent [{}] Date Monday [{}] with value [{}]...", idAgent, dateLundi,
				minutes);

		String url = String.format(sirhAbsWsBaseUrl + addRecuperationsUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("idAgent", String.valueOf(idAgent));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		parameters.put("dateLundi", sdf.format(dateLundi));
		parameters.put("minutes", String.valueOf(minutes));

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

}
