package nc.noumea.mairie.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	private static final String addRecuperationsUrl = "recuperations/add";
	private static final String addReposCompensateursUrl = "reposcomp/add";
	
	@Override
	public void addRecuperationsToAgent(Integer idAgent, Date dateLundi, Integer minutes) {

		logger.info("Updating recuperations for Agent [{}] Date Monday [{}] with value [{}]...", idAgent, dateLundi, minutes);
		
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

		logger.info("Updating repos compensateurs for Agent [{}] Date Monday [{}] with value [{}]...", idAgent, dateLundi, minutes);
		
		String url = String.format(sirhAbsWsBaseUrl + addReposCompensateursUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		
		parameters.put("idAgent", String.valueOf(idAgent));
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		parameters.put("dateLundi", sdf.format(dateLundi));
		parameters.put("minutes", String.valueOf(minutes));

		ClientResponse res = createAndFirePostRequest(parameters, url);

		readResponse(res, url);
		
	}

}
