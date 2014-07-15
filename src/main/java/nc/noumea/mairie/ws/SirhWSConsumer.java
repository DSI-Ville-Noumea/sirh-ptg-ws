package nc.noumea.mairie.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.SirhWsServiceDto;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;

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

	private static final String sirhAgentDivisionsUrl = "agents/direction";
	private static final String sirhAgentsServiceUrl = "services/agents";
	private static final String sirhAgentServiceUrl = "services/agent";
	private static final String sirhSousServicesUrl = "services/sousServices";
	private static final String sirhAgentUrl = "agents/getAgent";
	private static final String sirhHolidayUrl = "utils/isHoliday";
	private static final String sirhListPrimePointageUrl = "pointages/listPrimePointages";

	@Override
	public SirhWsServiceDto getAgentDirection(Integer idAgent) {

		String url = String.format(sirhWsBaseUrl + sirhAgentDivisionsUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("idAgent", String.valueOf(idAgent));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(SirhWsServiceDto.class, res, url);
	}

	@Override
	public AgentWithServiceDto getAgentService(Integer idAgent, Date date) {

		String url = String.format(sirhWsBaseUrl + sirhAgentServiceUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));

		if (date != null) {
			SimpleDateFormat sf = new SimpleDateFormat("YYYYMMdd");
			parameters.put("date", sf.format(date));
		}

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(AgentWithServiceDto.class, res, url);
	}

	@Override
	public List<AgentWithServiceDto> getServicesAgent(String rootService, Date date) {

		String url = String.format(sirhWsBaseUrl + sirhAgentsServiceUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("codeService", rootService);

		if (date != null) {
			SimpleDateFormat sf = new SimpleDateFormat("YYYYMMdd");
			parameters.put("date", sf.format(date));
		}

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(AgentWithServiceDto.class, res, url);
	}

	@Override
	public List<SirhWsServiceDto> getSousServices(String rootService) {

		String url = String.format(sirhWsBaseUrl + sirhSousServicesUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("codeService", rootService);

		ClientResponse res = createAndFireGetRequest(parameters, url);

		List<SirhWsServiceDto> services = readResponseAsList(SirhWsServiceDto.class, res, url);

		return services;
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
		SimpleDateFormat sf = new SimpleDateFormat("YYYYMMdd");

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
		SimpleDateFormat sf = new SimpleDateFormat("YYYYMMdd");

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
		SimpleDateFormat sf = new SimpleDateFormat("YYYYMMdd");

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", idAgent.toString());
		parameters.put("date", sf.format(date));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		List<Integer> listeNumPrime = readResponseAsList(Integer.class, res, url);

		return listeNumPrime;
	}
}
