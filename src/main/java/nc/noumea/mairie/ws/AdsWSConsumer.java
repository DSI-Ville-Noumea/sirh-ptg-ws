package nc.noumea.mairie.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ads.dto.ReferenceDto;

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
	private static final String sirhAdsGetTypeEntiteUrl = "api/typeEntite";
	private static final String sirhAdsGetParentOfEntiteByTypeEntiteUrl = "api/entite/parentOfEntiteByTypeEntite";

	@Override
	public EntiteDto getEntiteWithChildrenByIdEntite(Integer idEntite) {

		if (null == idEntite) {
			return null;
		}

		String url = String.format(adsWsBaseUrl + sirhAdsGetEntiteUrl + idEntite.toString()
				+ sirhAdsGetEntiteWithWildrenUrl);

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
	public EntiteDto getDirection(Integer idEntite) {
		if (idEntite == null)
			return null;
		// on appel ADS pour connaitre la liste des types d'entité pour passer
		// en paramètre ensuite le type "direction"
		List<ReferenceDto> listeType = getListTypeEntite();
		ReferenceDto type = null;
		for (ReferenceDto r : listeType) {
			if (r.getLabel().toUpperCase().equals("DIRECTION")) {
				type = r;
				break;
			}
		}
		if (type == null) {
			return null;
		}
		return getParentOfEntiteByTypeEntite(idEntite, type.getId());
	}

	@Override
	public EntiteDto getParentOfEntiteByTypeEntite(Integer idEntite, Integer idTypeEntite) {
		String url = String.format(adsWsBaseUrl + sirhAdsGetParentOfEntiteByTypeEntiteUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idEntite", idEntite.toString());
		parameters.put("idTypeEntite", idTypeEntite.toString());

		ClientResponse res = createAndFireGetRequest(parameters, url);

		try {
			return readResponse(EntiteDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return null;
	}

	@Override
	public List<ReferenceDto> getListTypeEntite() {
		String url = String.format(adsWsBaseUrl + sirhAdsGetTypeEntiteUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		try {
			return readResponseAsList(ReferenceDto.class, res, url);
		} catch (Exception e) {
			logger.error("L'application ADS ne repond pas." + e.getMessage());
		}

		return new ArrayList<ReferenceDto>();
	}
}
