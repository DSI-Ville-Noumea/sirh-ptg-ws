package nc.noumea.mairie.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.SirhWsServiceDto;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class SirhWSConsumer extends BaseWsConsumer implements ISirhWSConsumer {

	@Autowired
	@Qualifier("sirhWsBaseUrl")
	private String sirhWsBaseUrl;

	private static final String sirhAgentDirectionUrl = "agents/direction";
	private static final String sirhAgentServiceUrl = "services/agent";
	private static final String sirhAgentUrl = "agents/getAgent";
	private static final String sirhHolidayUrl = "utils/isHoliday";
	private static final String sirhListPrimePointageUrl = "pointages/listPrimePointages";
	private static final String sirhJourFerieUrl = "utils/isJourFerie";
	private static final String sirhBaseHorairePointageUrl = "pointages/baseHoraire";

	@Override
	public SirhWsServiceDto getAgentDirection(Integer idAgent, Date date) {

		String url = String.format(sirhWsBaseUrl + sirhAgentDirectionUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		if (date != null) {
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			parameters.put("dateAffectation", sf.format(date));
		}

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(SirhWsServiceDto.class, res, url);
	}

	@Override
	public AgentWithServiceDto getAgentService(Integer idAgent, Date date) {

		String url = String.format(sirhWsBaseUrl + sirhAgentServiceUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));

		if (date != null) {
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			parameters.put("date", sf.format(date));
		}

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(AgentWithServiceDto.class, res, url);
	}

	@Override
	public AgentGeneriqueDto getAgent(Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + sirhAgentUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(AgentGeneriqueDto.class, res, url);
	}

	@Override
	public boolean isHoliday(LocalDate datePointage) {
		String url = String.format(sirhWsBaseUrl + sirhHolidayUrl);
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("date", sf.format(datePointage.toDate()));

		ClientResponse res = createAndFireGetRequest(parameters, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isHoliday(DateTime deb) {
		String url = String.format(sirhWsBaseUrl + sirhHolidayUrl);
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("date", sf.format(deb.toDate()));

		ClientResponse res = createAndFireGetRequest(parameters, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		}
		return false;
	}

	@Override
	public List<Integer> getPrimePointagesByAgent(Integer idAgent, Date date) {

		String url = String.format(sirhWsBaseUrl + sirhListPrimePointageUrl);
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", idAgent.toString());
		parameters.put("date", sf.format(date));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		List<Integer> listeNumPrime = readResponseAsList(Integer.class, res, url);

		return listeNumPrime;
	}

	@Override
	public boolean isJourFerie(DateTime deb) {
		String url = String.format(sirhWsBaseUrl + sirhJourFerieUrl);
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("date", sf.format(deb.toDate()));

		ClientResponse res = createAndFireGetRequest(parameters, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		}
		return false;
	}

	@Override
	public BaseHorairePointageDto getBaseHorairePointageAgent(Integer idAgent, Date date) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		String url = String.format(sirhWsBaseUrl + sirhBaseHorairePointageUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("date", sf.format(date));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(BaseHorairePointageDto.class, res, url);
	}
}
