package nc.noumea.mairie.ws;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.SirhWsServiceDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import flexjson.JSONDeserializer;

@Service
public class SirhWSConsumer implements ISirhWSConsumer {

	@Autowired
	@Qualifier("sirhWsBaseUrl")
	private String sirhWsBaseUrl;

	private static final String sirhAgentDivisionsUrl = "agents/direction";
	private static final String sirhAgentsServiceUrl = "services/agents";
	private static final String sirhAgentServiceUrl = "services/agent";
	private static final String sirhSousServicesUrl = "services/sousServices";

	@Override
	public SirhWsServiceDto getAgentDirection(Integer idAgent) {

		String url = String.format(sirhWsBaseUrl + sirhAgentDivisionsUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		
		parameters.put("idAgent", String.valueOf(idAgent));

		ClientResponse res = createAndFireRequest(parameters, url);

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

		ClientResponse res = createAndFireRequest(parameters, url);

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

		ClientResponse res = createAndFireRequest(parameters, url);

		return readResponseAsList(AgentWithServiceDto.class, res, url);
	}

	@Override
	public List<SirhWsServiceDto> getSousServices(String rootService) {

		String url = String.format(sirhWsBaseUrl + sirhSousServicesUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("codeService", rootService);

		ClientResponse res = createAndFireRequest(parameters, url);

		List<SirhWsServiceDto> services = readResponseAsList(SirhWsServiceDto.class, res, url);

		return services;
	}

	public ClientResponse createAndFireRequest(Map<String, String> parameters, String url) {

		Client client = Client.create();
		WebResource webResource = client.resource(url);

		for (String key : parameters.keySet()) {
			webResource = webResource.queryParam(key, parameters.get(key));
		}

		ClientResponse response = null;

		try {
			response = webResource.accept(MediaType.APPLICATION_JSON_VALUE).get(ClientResponse.class);
		} catch (ClientHandlerException ex) {
			throw new SirhWSConsumerException(String.format("An error occured when querying '%s'.", url), ex);
		}

		return response;
	}

	public <T> T readResponse(Class<T> targetClass, ClientResponse response, String url) {

		T result = null;

		try {

			result = targetClass.newInstance();

		} catch (Exception ex) {
			throw new SirhWSConsumerException("An error occured when instantiating return type when deserializing JSON from SIRH WS request.", ex);
		}

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhWSConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);

		result = new JSONDeserializer<T>().deserializeInto(output, result);

		return result;
	}

	public <T> List<T> readResponseAsList(Class<T> targetClass, ClientResponse response, String url) {

		List<T> result = null;

		result = new ArrayList<T>();

		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return result;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhWSConsumerException(String.format("An error occured when querying '%s'. Return code is : %s", url, response.getStatus()));
		}

		String output = response.getEntity(String.class);

		result = new JSONDeserializer<List<T>>().use(null, ArrayList.class).use("values", targetClass).deserialize(output);

		return result;
	}
}
