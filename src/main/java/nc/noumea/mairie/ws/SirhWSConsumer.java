package nc.noumea.mairie.ws;

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

	@Override
	public ServiceDto getAgentDivision(Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + sirhAgentDivisionsUrl);
		ClientResponse res = createAndFireRequest(idAgent, url);
		return readResponse(ServiceDto.class, res, idAgent, url);
	}
	
	public ClientResponse createAndFireRequest(Integer agentId, String url) {

		Client client = Client.create();

		WebResource webResource = client.resource(url).queryParam("idAgent", String.valueOf(agentId));

		ClientResponse response = null;

		try {
			response = webResource.accept(MediaType.APPLICATION_JSON_VALUE).get(ClientResponse.class);
		} catch (ClientHandlerException ex) {
			throw new SirhWSConsumerException(String.format("An error occured when querying '%s' with agentId '%d'.", url, agentId), ex);
		}

		return response;
	}

	public <T> T readResponse(Class<T> targetClass, ClientResponse response, int agentId, String url) {

		T result = null;
		
		try {
			
			result = targetClass.newInstance();

		} catch (InstantiationException | IllegalAccessException ex) {
			throw new SirhWSConsumerException("An error occured when instantiating return type when deserializing JSON from SIRH WS request.", ex);
		}
		
		if (response.getStatus() == HttpStatus.NO_CONTENT.value()) {
			return null;
		}

		if (response.getStatus() != HttpStatus.OK.value()) {
			throw new SirhWSConsumerException(String.format("An error occured when querying '%s' with agentId '%d'. Return code is : %s", url,
					agentId, response.getStatus()));
		}

		String output = response.getEntity(String.class);

		result = new JSONDeserializer<T>().deserializeInto(output, result);

		return result;
	}

}
