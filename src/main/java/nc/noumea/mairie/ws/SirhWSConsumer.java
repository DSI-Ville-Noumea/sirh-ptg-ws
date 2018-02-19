package nc.noumea.mairie.ws;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sun.jersey.api.client.ClientResponse;

import flexjson.JSONSerializer;
import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.domain.Spphre;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.sirh.dto.AffectationDto;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;
import nc.noumea.mairie.sirh.dto.JourDto;
import nc.noumea.mairie.sirh.dto.ProfilAgentDto;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;

@Service
public class SirhWSConsumer extends BaseWsConsumer implements ISirhWSConsumer {
	
	@Autowired
	@Qualifier("sirhWsBaseUrl")
	private String				sirhWsBaseUrl;

	private static final String	sirhAgentEtatCivilUrl								= "agents/getEtatCivil";
	private static final String	sirhAgentDirectionUrl								= "agents/direction";
	private static final String	sirhAgentServiceUrl									= "services/agent";
	private static final String	sirhListAgentsWithServiceUrl						= "services/listAgentsWithService";
	private static final String	sirhListAgentsWithServiceOldAffectationUrl			= "services/listAgentsWithServiceOldAffectation";
	private static final String	sirhAgentUrl										= "agents/getAgent";
	private static final String	sirhAgentByTitreRepasUrl							= "agents/listAgentAvecIdTitreRepas";
	private static final String	sirhGetListAgentsUrl								= "agents/getListAgents";
	private static final String	sirhHolidayUrl										= "utils/isHoliday";
	private static final String	sirhListPrimePointageUrl							= "pointages/listPrimePointages";
	private static final String	sirhJourFerieUrl									= "utils/isJourFerie";
	private static final String	listeJoursFeriesUrl									= "utils/listeJoursFeries";
	private static final String	sirhBaseHorairePointageUrl							= "pointages/baseHoraire";
	private static final String	sirhlistAgentsWithPrimeTIDOnAffectationUrl			= "pointages/listAgentsWithPrimeTIDOnAffectation";
	private static final String	sirhlisteAffectationDtosForListAgentByPeriodeUrl	= "pointages/listeAffectationDtosForListAgentByPeriode";
	private static final String	isUtilisateurSIRHServiceUrl							= "utilisateur/isUtilisateurSIRH";
	private static final String	sirhBaseCongeUrl									= "absences/baseHoraire";
	private static final String	sirhListeAgentWithIndemniteForfaitTravailDPMUrl		= "agents/listeAgentWithIndemniteForfaitTravailDPM";
	private static final String	sirhListeAgentEnActiviteSurPeriodeUrl				= "agents/listeAgentsMairieSurPeriode";

	private static final String	sirhGetSpphreForPeriod								= "pointages/getSpphre";

	@Override
	public EntiteDto getAgentDirection(Integer idAgent, Date date) {

		String url = String.format(sirhWsBaseUrl + sirhAgentDirectionUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		if (date != null) {
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			parameters.put("dateAffectation", sf.format(date));
		}

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(EntiteDto.class, res, url);
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
	public List<AgentWithServiceDto> getListAgentsWithService(List<Integer> listAgentDto, Date date) {

		String url = String.format(sirhWsBaseUrl + sirhListAgentsWithServiceUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		String json = new JSONSerializer().exclude("*.class").deepSerialize(listAgentDto);

		if (date != null) {
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			parameters.put("date", sf.format(date));
		}

		ClientResponse res = createAndFirePostRequest(parameters, url, json);

		return readResponseAsList(AgentWithServiceDto.class, res, url);
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
	public List<Integer> getPrimePointagesByAgent(Integer idAgent, Date dateDebut, Date dateFin) {

		String url = String.format(sirhWsBaseUrl + sirhListPrimePointageUrl);
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", idAgent.toString());
		parameters.put("dateDebut", sf.format(dateDebut));
		parameters.put("dateFin", sf.format(dateFin));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		List<Integer> listeNumPrime = readResponseAsList(Integer.class, res, url);

		return listeNumPrime;
	}

	@Override
	public boolean isJourFerie(DateTime deb) {

		String url = String.format(sirhWsBaseUrl + sirhJourFerieUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("date", deb.toString("yyyyMMdd"));

		ClientResponse res = createAndFireGetRequest(parameters, url);
		if (res.getStatus() == HttpStatus.OK.value()) {
			return true;
		}
		return false;
	}

	@Override
	public BaseHorairePointageDto getBaseHorairePointageAgent(Integer idAgent, Date dateDebut, Date dateFin) {

		List<BaseHorairePointageDto> list = getListBaseHorairePointageAgent(idAgent, dateDebut, dateFin);

		if (null != list && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * Retourne la liste des bases horaires par rapport à la liste des
	 * affectations de l'agent triees par date de debut dans l ordre croissant
	 */
	@Override
	public List<BaseHorairePointageDto> getListBaseHorairePointageAgent(Integer idAgent, Date dateDebut, Date dateFin) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		String url = String.format(sirhWsBaseUrl + sirhBaseHorairePointageUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("dateDebut", sf.format(dateDebut));
		parameters.put("dateFin", sf.format(dateFin));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(BaseHorairePointageDto.class, res, url);
	}

	@Override
	public List<AgentGeneriqueDto> getListAgents(List<Integer> listIdsAgent) {

		String url = String.format(sirhWsBaseUrl + sirhGetListAgentsUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		String json = new JSONSerializer().exclude("*.class").deepSerialize(listIdsAgent);

		ClientResponse res = createAndFirePostRequest(parameters, url, json);

		return readResponseAsList(AgentGeneriqueDto.class, res, url);
	}

	@Override
	public List<Integer> getListAgentsWithPrimeTIDOnAffectation(Date dateDebut, Date dateFin) {

		String url = String.format(sirhWsBaseUrl + sirhlistAgentsWithPrimeTIDOnAffectationUrl);
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("dateDebut", sf.format(dateDebut));
		parameters.put("dateFin", sf.format(dateFin));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		List<Integer> listeNumPrime = readResponseAsList(Integer.class, res, url);

		return listeNumPrime;
	}

	@Override
	public List<AffectationDto> getListAffectationDtoBetweenTwoDateAndForListAgent(List<Integer> listIdsAgent, Date dateDebut, Date dateFin) {

		String url = String.format(sirhWsBaseUrl + sirhlisteAffectationDtosForListAgentByPeriodeUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		String json = new JSONSerializer().exclude("*.class").deepSerialize(listIdsAgent);

		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		parameters.put("dateDebut", sf.format(dateDebut));
		parameters.put("dateFin", sf.format(dateFin));

		ClientResponse res = createAndFirePostRequest(parameters, url, json);

		return readResponseAsList(AffectationDto.class, res, url);
	}

	@Override
	public List<JourDto> getListeJoursFeries(Date dateDebut, Date dateFin) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		String url = String.format(sirhWsBaseUrl + listeJoursFeriesUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("dateDebut", sdf.format(dateDebut));
		parameters.put("dateFin", sdf.format(dateFin));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(JourDto.class, res, url);
	}

	@Override
	public ReturnMessageDto isUtilisateurSIRH(Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + isUtilisateurSIRHServiceUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		ReturnMessageDto result = new ReturnMessageDto();
		try {
			result = readResponse(ReturnMessageDto.class, res, url);
		} catch (WSConsumerException e) {
			result.getErrors().add("L'agent n'existe pas dans l'AD.");
		}

		return result;
	}

	@Override
	public List<AgentWithServiceDto> getListAgentsWithServiceOldAffectation(List<Integer> listIdsAgent) {

		String url = String.format(sirhWsBaseUrl + sirhListAgentsWithServiceOldAffectationUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		String json = new JSONSerializer().exclude("*.class").deepSerialize(listIdsAgent);

		ClientResponse res = createAndFirePostRequest(parameters, url, json);

		return readResponseAsList(AgentWithServiceDto.class, res, url);
	}

	@Override
	public RefTypeSaisiCongeAnnuelDto getBaseHoraireAbsence(Integer idAgent, Date date) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		String url = String.format(sirhWsBaseUrl + sirhBaseCongeUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("date", sf.format(date));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(RefTypeSaisiCongeAnnuelDto.class, res, url);
	}

	@Override
	public List<AgentWithServiceDto> getListeAgentWithIndemniteForfaitTravailDPM(Set<Integer> listIdsAgent) {

		String url = String.format(sirhWsBaseUrl + sirhListeAgentWithIndemniteForfaitTravailDPMUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		String json = new JSONSerializer().exclude("*.class").deepSerialize(listIdsAgent);

		ClientResponse res = createAndFirePostRequest(parameters, url, json);

		return readResponseAsList(AgentWithServiceDto.class, res, url);
	}

	@Override
	public ProfilAgentDto getEtatCivil(Integer idAgent) {
		String url = String.format(sirhWsBaseUrl + sirhAgentEtatCivilUrl);
		HashMap<String, String> params = new HashMap<>();
		params.put("idAgent", idAgent.toString());

		ClientResponse res = createAndFireGetRequest(params, url);
		return readResponse(ProfilAgentDto.class, res, url);
	}

	@Override
	public List<AgentWithServiceDto> getListeAgentsMairieSurPeriode(Date datePremierJourOfMonth, Date dateDernierJourOfMonth) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");

		String url = String.format(sirhWsBaseUrl + sirhListeAgentEnActiviteSurPeriodeUrl);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("dateDebutPeriode", sf.format(datePremierJourOfMonth));
		parameters.put("dateFinPeriode", sf.format(dateDernierJourOfMonth));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(AgentWithServiceDto.class, res, url);
	}

	@Override
	public List<AgentGeneriqueDto> listAgentAvecIdTitreRepas() {

		String url = String.format(sirhWsBaseUrl + sirhAgentByTitreRepasUrl);

		Map<String, String> parameters = new HashMap<String, String>();

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponseAsList(AgentGeneriqueDto.class, res, url);
	}

	@Override
	public Spphre getSpphre(Integer idAgent, Date dateLundi) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		String url = String.format(sirhWsBaseUrl + sirhGetSpphreForPeriod);

		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("idAgent", String.valueOf(idAgent));
		parameters.put("dateLundi", sf.format(dateLundi));

		ClientResponse res = createAndFireGetRequest(parameters, url);

		return readResponse(Spphre.class, res, url);
	}
}
