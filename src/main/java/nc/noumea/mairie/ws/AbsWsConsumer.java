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
	private static final String addRecuperationsForPointagePTGUrl = "recuperations/addForPointagePTG";
	private static final String addReposCompensateursUrl = "reposcomps/addForPTG";
	private static final String checkRecuperationsUrl = "recuperations/checkRecuperations";
	private static final String checkReposCompensateursUrl = "reposcomps/checkReposCompensateurs";
	private static final String checkAbsencesSyndicalesUrl = "asa/checkAbsencesSyndicales";
	private static final String checkCongesExceptionnelsUrl = "congeexceptionnel/checkCongesExceptionnels";
	private static final String checkCongesAnnuelsUrl = "congeannuel/checkCongesAnnuels";
	private static final String listeDemandesSIRHUrl = "demandes/listeDemandesSIRH";
	private static final String getTypeAbsenceUrl = "filtres/getTypesSaisi";
	private static final String getListeTypeAbsenceUrl = "typeAbsence/getListeTypeAbsence";

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
	public void addRecuperationsToCompteurAgentForOnePointage(Integer idAgent, Date date, Integer minutes, Integer idPointage, Integer idPointageParent) { 

		logger.info("Updating recuperations for Agent [{}] Date [{}] Pointage [{}] with value [{}]...", idAgent, date,
				minutes, idPointage);

		String url = String.format(sirhAbsWsBaseUrl + addRecuperationsForPointagePTGUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		parameters.put("idAgent", String.valueOf(idAgent));

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		parameters.put("date", sdf.format(date));
		parameters.put("minutes", String.valueOf(minutes));
		parameters.put("idPointage", String.valueOf(idPointage));
		if(null != idPointageParent) {
			parameters.put("idPointageParent", String.valueOf(idPointageParent));
		}

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
	public List<RefTypeSaisiDto> getTypeSaisiAbsence(Integer idRefTypeAbsence) {
		
		String url = String.format(sirhAbsWsBaseUrl + getTypeAbsenceUrl);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idRefTypeAbsence", String.valueOf(idRefTypeAbsence));
		
		ClientResponse response = createAndFireGetRequest(parameters, url);
		
		return readResponseAsList(RefTypeSaisiDto.class, response, url);
	}
	
	@Override
	public List<DemandeDto> getListAbsencesForListAgentsBetween2Dates(
			List<Integer> listIdsAgent, Date start, Date end) {
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		
		String url = String.format(sirhAbsWsBaseUrl + listeDemandesSIRHUrl);
		Map<String, String> parameters = new HashMap<String, String>();
		
		if (null != listIdsAgent && !listIdsAgent.isEmpty()) {
			String listIdsAgentString = "";
			for (Integer idAgent : listIdsAgent) {
				listIdsAgentString += idAgent + ",";
			}
			listIdsAgentString = listIdsAgentString.substring(0, listIdsAgentString.length() - 1);
			parameters.put("idAgents", listIdsAgentString);
		}
		
		parameters.put("from", sf.format(start));
		parameters.put("to", sf.format(end));
		parameters.put("etat", "6"); // etat PRIS
		parameters.put("aValider", "false");
		
		ClientResponse response = createAndFireGetRequest(parameters, url);
		
		return readResponseAsList(DemandeDto.class, response, url);
	}

	@Override
	public List<nc.noumea.mairie.abs.dto.RefTypeAbsenceDto> getListeTypAbsenceCongeAnnuel() {
		
		String url = String.format(sirhAbsWsBaseUrl + getListeTypeAbsenceUrl);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idRefGroupeAbsence", "5"); // CONGES ANNUELS
		
		ClientResponse response = createAndFireGetRequest(parameters, url);
		
		return readResponseAsList(nc.noumea.mairie.abs.dto.RefTypeAbsenceDto.class, response, url);
	}

}
