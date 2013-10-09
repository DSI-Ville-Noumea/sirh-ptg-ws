package nc.noumea.mairie.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class AbsWsConsumer extends BaseWsConsumer implements IAbsWsConsumer {

	@Autowired
	@Qualifier("sirhAbsWsBaseUrl")
	private String sirhAbsWsBaseUrl;

	private static final String addRecuperationsUrl = "recuperations/add";
	
	@Override
	public void addRecuperationsToAgent(Integer idAgent, Date dateLundi, Integer minutes) {

		String url = String.format(sirhAbsWsBaseUrl + addRecuperationsUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		
		parameters.put("idAgent", String.valueOf(idAgent));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		parameters.put("dateLundi", sdf.format(dateLundi));
		parameters.put("minutes", String.valueOf(minutes));

		ClientResponse res = createAndFirePostRequest(parameters, url);

		readResponse(res, url);
	}

}
