package nc.noumea.mairie.ws;

import java.util.HashMap;
import java.util.Map;

import nc.noumea.mairie.ads.dto.EntiteDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

@Service
public class AdsWSConsumer extends BaseWsConsumer implements IAdsWSConsumer {

	private Logger logger = LoggerFactory.getLogger(AdsWSConsumer.class);

	@Autowired
	@Qualifier("adsWsBaseUrl")
	private String adsWsBaseUrl;

	private static final String sirhAdsGetEntiteUrl = "api/entite/";
	private static final String sirhAdsGetEntiteWithWildrenUrl = "/withChildren";
	private static final String sirhAdsGetInfoSiservUrl = "api/entite/infoSiserv/";
	private static final String sirhAdsGetWholeTreevUrl = "/api/arbre";

	@Override
	public EntiteDto getEntiteWithChildrenByIdEntite(Integer idEntite) {

		if (null == idEntite) {
			return null;
		}

		String url = String.format(adsWsBaseUrl + sirhAdsGetEntiteUrl + idEntite.toString() + sirhAdsGetEntiteWithWildrenUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);
		try {
			return readResponse(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}

	@Override
	public EntiteDto getInfoSiservByIdEntite(Integer idEntite) {

		if (null == idEntite) {
			return null;
		}

		String url = String.format(adsWsBaseUrl + sirhAdsGetInfoSiservUrl + idEntite);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);
		try {
			return readResponse(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}

	@Override
	public EntiteDto getEntiteByIdEntite(Integer idEntite) {

		if (null == idEntite) {
			return null;
		}

		String url = String.format(adsWsBaseUrl + sirhAdsGetEntiteUrl + idEntite.toString());

		logger.debug("Call ADS : " + url);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);
		try {
			return readResponse(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}
	
	@Override
	public EntiteDto getEntiteByIdEntiteOptimiseWithWholeTree(Integer idEntite, EntiteDto root) {

		if (null == idEntite) {
			return null;
		}

		return getEntiteInWholeTree(root, idEntite);
	}
	
	private EntiteDto getEntiteInWholeTree(EntiteDto entite, Integer idEntite) {
		
		if(null == entite) 
			return null;
		
		if (entite.getIdEntite().equals(idEntite)) {
			return entite;
		}
		
		EntiteDto result = null;
		for(EntiteDto enfant : entite.getEnfants()) {
			result = getEntiteInWholeTree(enfant, idEntite);
			if(null != result) {
				return result;
			}
		}
		
		return result;
	}

	@Override
	public EntiteDto getWholeTree() {

		String url = String.format(adsWsBaseUrl + sirhAdsGetWholeTreevUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);
		try {
			return readResponse(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}
}
