package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.DpmIndemAnnee;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.DpmIndemniteAnneeDto;
import nc.noumea.mairie.ptg.dto.DpmIndemniteChoixAgentDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IDpmRepository;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IDpmService;
import nc.noumea.mairie.ptg.web.AccessForbiddenException;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;
import nc.noumea.mairie.ws.SirhWSUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DpmService implements IDpmService {
	
	Logger logger = LoggerFactory.getLogger(DpmService.class);
	
	protected final static String CREATION_IMPOSSIBLE = "Vous ne pouvez pas créer de nouvelle entrée.";
	protected final static String NON_TROUVE = "Enregistrement non trouvé.";
	protected final static String MODIFICATION_OK = "Enregistré.";
	protected final static String HORS_PERIODE = "La période est fermée. Vous ne pouvez plus choisir.";
	protected final static String INTERDIT = "Pas de droit pour l'Indemnité forfaitaire travail DPM.";
	protected final static String CHOIX_OBLIGATOIRE = "Veuillez choisir Indemnité ou Récupération.";
	protected final static String AGENT_INTERDIT = "L'agent %s le droit à l'Indemnité forfaitaire travail DPM.";
	protected final static String AGENT_CHOIX_OBLIGATOIRE = "Veuillez choisir Indemnité ou Récupération pour l'agent %S.";
	protected final static String CHAMPS_NON_REMPLIE = "Veuillez remplir tous les champs obligatoires.";
	protected final static String SUPPRESSION_OK = "Supprimé.";
	

	@Autowired
	private IAccessRightsService accessRightsService;

	@Autowired
	private SirhWSUtils sirhWSUtils;
	
	@Autowired
	private ISirhWSConsumer sirhWSConsumer;
	
	@Autowired
	private IAbsWsConsumer absWsConsumer;
	
	@Autowired
	private IDpmRepository dpmRepository;
	
	@Autowired
	private HelperService helperService; 
	
	@Override
	@Transactional(value = "ptgTransactionManager")
	public ReturnMessageDto saveIndemniteChoixAgentForKiosque(Integer idAgentConnecte, DpmIndemniteChoixAgentDto dto) {
		
		// gestion des droits
		if(null == idAgentConnecte
				|| null == dto
				|| !idAgentConnecte.equals(dto.getIdAgent())) {
			throw new AccessForbiddenException();
		}
		
		return saveIndemniteChoixAgent(idAgentConnecte, dto);
	}
	
	@Override
	@Transactional(value = "ptgTransactionManager")
	public ReturnMessageDto saveIndemniteChoixAgentForSIRH(Integer idAgentConnecte, DpmIndemniteChoixAgentDto dto) {
		
		// gestion des droits
		if(null == idAgentConnecte
				|| !sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte).getErrors().isEmpty()) {
			throw new AccessForbiddenException();
		}
		
		return saveIndemniteChoixAgent(idAgentConnecte, dto);
	}
	
	private ReturnMessageDto saveIndemniteChoixAgent(Integer idAgentConnecte, DpmIndemniteChoixAgentDto dto) {

		ReturnMessageDto result = new ReturnMessageDto();
		
		// ouverture du choix : periode
		if(null == dto.getDpmIndemniteAnnee()
				|| !isPeriodeChoixOuverte(dto.getDpmIndemniteAnnee().getAnnee())) {
			result.getErrors().add(HORS_PERIODE);
			return result;
		}
		
		// etre en non cycle et avoir la prime sur l affectation 
		if(!isDroitAgentToIndemniteForfaitaireDPM(dto.getIdAgent())) {
			result.getErrors().add(INTERDIT);
			return result;
		}
		
		if((dto.isChoixIndemnite() && dto.isChoixRecuperation())
				|| (!dto.isChoixIndemnite() && !dto.isChoixRecuperation())) {
			logger.debug(String.format(CHOIX_OBLIGATOIRE, dto.getIdAgent()));
			result.getErrors().add(String.format(CHOIX_OBLIGATOIRE, dto.getIdAgent()));
			return result;
		}
		
		// si oui, on enregistre le choix
		// on recherche si un choix existe deja pour cette annee
		DpmIndemChoixAgent choixAgent = dpmRepository.getDpmIndemChoixAgentByAgentAndAnnee(dto.getIdAgent(), dto.getDpmIndemniteAnnee().getAnnee());
		
		if(null == choixAgent) {
			choixAgent = new DpmIndemChoixAgent();
			choixAgent.setDpmIndemAnnee(dpmRepository.getDpmIndemAnneeByAnnee(dto.getDpmIndemniteAnnee().getAnnee()));
			choixAgent.setIdAgent(dto.getIdAgent());
		}
		
		choixAgent.setDateMaj(helperService.getCurrentDate());
		choixAgent.setChoixIndemnite(dto.isChoixIndemnite());
		choixAgent.setChoixRecuperation(dto.isChoixRecuperation());
		choixAgent.setIdAgentCreation(idAgentConnecte);
		
		dpmRepository.persistEntity(choixAgent);
		
		result.getInfos().add(MODIFICATION_OK);
		
		return result;
	}
	
	@Override
	@Transactional(value = "ptgTransactionManager")
	public ReturnMessageDto saveListIndemniteChoixAgentForOperator(Integer idAgentConnecte, Integer annee, List<DpmIndemniteChoixAgentDto> listDto) {
		
		ReturnMessageDto result = new ReturnMessageDto();
		
		// ouverture du choix : periode
		if(!isPeriodeChoixOuverte(annee)) {
			result.getErrors().add(HORS_PERIODE);
			return result;
		}
		
		// gestion des droits
		if(null == idAgentConnecte
				|| null == listDto
				|| !accessRightsService.isUserOperateur(idAgentConnecte)) {
			throw new AccessForbiddenException();
		}
		
		// etre en non cyle et avoir la prime sur l affectation 
		for(DpmIndemniteChoixAgentDto dto : listDto) {
			// est ce que l agent a le droit a la prime
			if(!isDroitAgentToIndemniteForfaitaireDPM(dto.getIdAgent())) {
				logger.error(String.format(AGENT_INTERDIT, dto.getIdAgent()));
				result.getErrors().add(String.format(AGENT_INTERDIT, dto.getIdAgent()));
				continue;
			}
			
			if((dto.isChoixIndemnite() && dto.isChoixRecuperation())
					|| (!dto.isChoixIndemnite() && !dto.isChoixRecuperation())) {
				logger.debug(String.format(AGENT_CHOIX_OBLIGATOIRE, dto.getIdAgent()));
				result.getErrors().add(String.format(AGENT_CHOIX_OBLIGATOIRE, dto.getIdAgent()));
				continue;
			}
			
			// si oui, on enregistre le choix
			// on recherche si un choix existe deja pour cette annee
			DpmIndemChoixAgent choixAgent = dpmRepository.getDpmIndemChoixAgentByAgentAndAnnee(dto.getIdAgent(), annee);
			
			if(null == choixAgent) {
				choixAgent = new DpmIndemChoixAgent();
				choixAgent.setDpmIndemAnnee(dpmRepository.getDpmIndemAnneeByAnnee(annee));
				choixAgent.setIdAgent(dto.getIdAgent());
			}
			
			choixAgent.setIdAgentCreation(idAgentConnecte);
			choixAgent.setDateMaj(helperService.getCurrentDate());
			choixAgent.setChoixIndemnite(dto.isChoixIndemnite());
			choixAgent.setChoixRecuperation(dto.isChoixRecuperation());
			
			dpmRepository.persistEntity(choixAgent);
		}
		
		result.getInfos().add(MODIFICATION_OK);

		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgent(Integer idAgentConnecte, Integer annee, 
			Integer idServiceAds, Integer idAgentFiltre) {
		
		if(null == idAgentConnecte
				//|| !sirhWSUtils.isAgentDPM(idAgentConnecte)
				) {
			throw new AccessForbiddenException();
		}
		
		List<Integer> listIdsAgent = new ArrayList<Integer>();
		// on cherche le role de l agent
		if(accessRightsService.isUserOperateur(idAgentConnecte)) {
			// si operateur, on recupere les agents qui lui sont affectes
			List<AgentDto> listAgents = accessRightsService.getAgentsToApproveOrInput(idAgentConnecte, idServiceAds, null, true);
			
			if(null != listAgents
					&& !listAgents.isEmpty()) {
				for(AgentDto agent : listAgents) {
					if(!listIdsAgent.contains(agent.getIdAgent())){
						if(null == idAgentFiltre 
								|| agent.getIdAgent().equals(idAgentFiltre)) {
							listIdsAgent.add(agent.getIdAgent());
						}
					}
				}
			}
		}else{
			throw new AccessForbiddenException();
		}
		
		return getListDpmIndemniteChoixAgentDto(listIdsAgent, annee, null, null);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgentforSIRH(Integer idAgentConnecte, Integer annee, 
			Boolean isChoixIndemnite, Boolean isChoixRecuperation, List<Integer> listIdsAgent) {
		
		if(null == idAgentConnecte
				|| !sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte).getErrors().isEmpty()) {
			throw new AccessForbiddenException();
		}
		
		return getListDpmIndemniteChoixAgentDto(listIdsAgent, annee, isChoixIndemnite, isChoixRecuperation);
	}
	
	private List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgentDto(List<Integer> listIdsAgent, Integer annee, Boolean isChoixIndemnite, Boolean isChoixRecuperation) {

		List<DpmIndemniteChoixAgentDto> result = new ArrayList<DpmIndemniteChoixAgentDto>();
	
		List<DpmIndemChoixAgent> listDpmIndemChoixAgent = dpmRepository.getListDpmIndemChoixAgent(listIdsAgent, annee, isChoixIndemnite, isChoixRecuperation);
		
		if(null != listDpmIndemChoixAgent
				&& !listDpmIndemChoixAgent.isEmpty()) {
			// on recupere les id agent des operateurs
			for(DpmIndemChoixAgent choixAgent : listDpmIndemChoixAgent) {
				if(null != listIdsAgent
						&& !listIdsAgent.contains(choixAgent.getIdAgent())) {
					listIdsAgent.add(choixAgent.getIdAgent());
				}
				if(null != listIdsAgent
						&& !listIdsAgent.contains(choixAgent.getIdAgentCreation())) {
					listIdsAgent.add(choixAgent.getIdAgentCreation());
				}
			}
			// ///////////////// on recupere la liste d agents // ///////
			List<AgentWithServiceDto> listAgentServiceDto = sirhWSConsumer.getListAgentsWithService(listIdsAgent, null);
			
			for(DpmIndemChoixAgent choixAgent : listDpmIndemChoixAgent) {
				AgentWithServiceDto agDtoServ = sirhWSUtils.getAgentOfListAgentWithServiceDto(listAgentServiceDto, choixAgent.getIdAgent());
				AgentWithServiceDto operateurDtoServ = sirhWSUtils.getAgentOfListAgentWithServiceDto(listAgentServiceDto, choixAgent.getIdAgentCreation());
				DpmIndemniteChoixAgentDto dto = new DpmIndemniteChoixAgentDto(choixAgent, agDtoServ, operateurDtoServ);
				result.add(dto);
			}
		}
		
		return result;
	}
	
	@Override
	@Transactional(readOnly = true)
	public DpmIndemniteChoixAgentDto getIndemniteChoixAgent(Integer idAgentConnecte, Integer annee) {
		
		if(null == idAgentConnecte
				|| !sirhWSUtils.isAgentDPM(idAgentConnecte)) {
			throw new AccessForbiddenException();
		}
		
		DpmIndemChoixAgent dpmIndemChoixAgent = dpmRepository.getDpmIndemChoixAgent(idAgentConnecte, annee);
		
		DpmIndemniteChoixAgentDto result = new DpmIndemniteChoixAgentDto(dpmIndemChoixAgent);
		result.setDpmIndemniteAnnee(new DpmIndemniteAnneeDto(dpmRepository.getDpmIndemAnneeByAnnee(annee), false));
		
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isPeriodeChoixOuverte(Integer annee) {
		
		DpmIndemAnnee dpmIndemAnnee = dpmRepository.getDpmIndemAnneeByAnnee(annee);
		
		if(null != dpmIndemAnnee
				&& dpmIndemAnnee.getDateDebut().before(helperService.getCurrentDate())
				&& dpmIndemAnnee.getDateFin().after(helperService.getCurrentDate())) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isDroitAgentToIndemniteForfaitaireDPM(Integer idAgent) {
		
		// ne doit pas etre organise en cycle pour ses conges
//		if(isAgentCycleConge(idAgent)) {
//			return false;
//		}
		
		// doit avoir la prime Indemnité forfaitaire travail DPM sur son affectation courante
		if(!isAgentWithIndemniteForfaitaireTravailDPMInAffectation(idAgent, null)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<DpmIndemniteAnneeDto> getListDpmIndemAnneeOuverte() {
		
		List<DpmIndemAnnee> listDpmIndemAnnee = dpmRepository.getListDpmIndemAnneeOuverte();
		
		List<DpmIndemniteAnneeDto> result = new ArrayList<DpmIndemniteAnneeDto>();
		
		if(null != listDpmIndemAnnee
				&& !listDpmIndemAnnee.isEmpty()) {
			for(DpmIndemAnnee choixAnnee : listDpmIndemAnnee) {
				DpmIndemniteAnneeDto dto = new DpmIndemniteAnneeDto(choixAnnee, true);
				result.add(dto);
			}
		}
		
		return result;
	}
	
	@Override
	@Transactional(readOnly = true)
	public DpmIndemniteAnneeDto getDpmIndemAnneeEnCours() {
		
		DpmIndemAnnee dpmIndemAnnee = dpmRepository.getDpmIndemAnneeByAnnee(new DateTime().getYear());
		
		if(null != dpmIndemAnnee) {
			return new DpmIndemniteAnneeDto(dpmIndemAnnee, false);
		}
		
		return null;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<DpmIndemniteAnneeDto> getListDpmIndemAnnee(Integer idAgentConnecte) {
		
		// gestion des droits
		if(null == idAgentConnecte) {
			throw new AccessForbiddenException();
		}else{
			ReturnMessageDto isUtilisateurSIRH = sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte);
			if (!isUtilisateurSIRH.getErrors().isEmpty()) {
				throw new AccessForbiddenException();
			}
		}
		
		// on regarde si il existe un enregistrement pour l annee suivante
		// si non => on cree
		DpmIndemAnnee dpmAnneeProchaine = dpmRepository.getDpmIndemAnneeByAnnee(new DateTime().getYear()+1);
		if(null == dpmAnneeProchaine) {
			dpmAnneeProchaine = new DpmIndemAnnee();
			dpmAnneeProchaine.setAnnee(new DateTime().getYear()+1);
			dpmAnneeProchaine.setDateDebut(new DateTime().plusYears(1).withMonthOfYear(DateTimeConstants.DECEMBER).withDayOfMonth(1).withMillisOfDay(0).toDate());
			dpmAnneeProchaine.setDateFin(new DateTime().plusYears(1).withMonthOfYear(DateTimeConstants.DECEMBER).withDayOfMonth(15).withMillisOfDay(0).toDate());
			
			dpmRepository.persistEntity(dpmAnneeProchaine);
		}
		
		List<DpmIndemAnnee> listDpmIndemAnnee = dpmRepository.getListDpmIndemAnnee();
		
		List<DpmIndemniteAnneeDto> result = new ArrayList<DpmIndemniteAnneeDto>();
		
		if(null != listDpmIndemAnnee
				&& !listDpmIndemAnnee.isEmpty()) {
			for(DpmIndemAnnee choixAnnee : listDpmIndemAnnee) {
				DpmIndemniteAnneeDto dto = new DpmIndemniteAnneeDto(choixAnnee, true);
				result.add(dto);
			}
		}
		
		return result;
	}

	@Override
	@Transactional(value = "ptgTransactionManager")
	public ReturnMessageDto saveDpmIndemAnnee(Integer idAgentConnecte, DpmIndemniteAnneeDto dto) {
		
		// gestion des droits
		if(null == idAgentConnecte) {
			throw new AccessForbiddenException();
		}else{
			ReturnMessageDto isUtilisateurSIRH = sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte);
			if (!isUtilisateurSIRH.getErrors().isEmpty()) {
				throw new AccessForbiddenException();
			}
		}
		
		ReturnMessageDto result = new ReturnMessageDto();
		
		// impossible de creer une nouvelle ligne
		if(null == dto
				|| null == dto.getIdDpmIndemAnnee()) {
			result.getErrors().add(CREATION_IMPOSSIBLE);
			return result;
		}
		
		// champs obligatoire
		if(null == dto.getDateDebut()
				|| null == dto.getDateFin()) {
			result.getErrors().add(CHAMPS_NON_REMPLIE);
			return result;
		}
		
		DpmIndemAnnee dpmAnnee = dpmRepository.getEntity(DpmIndemAnnee.class, dto.getIdDpmIndemAnnee());
		
		if(null == dpmAnnee) {
			result.getErrors().add(NON_TROUVE);
			return result;
		}
		
		// on ne peut modifier que les dates d ouverture et fermeture dans le kiosque
		dpmAnnee.setDateDebut(dto.getDateDebut());
		dpmAnnee.setDateFin(dto.getDateFin());
		
		dpmRepository.persistEntity(dpmAnnee);
		
		result.getInfos().add(MODIFICATION_OK);
		
		return result;
	}
	
//	protected boolean isAgentCycleConge(Integer idAgent) {
//
//		List<RefTypeAbsenceDto> listTypeAbsence = absWsConsumer.getListeTypAbsenceCongeAnnuel();
//		
//		RefTypeSaisiCongeAnnuelDto baseConge = sirhWSConsumer.getBaseHoraireAbsence(idAgent, new Date());
//
//		if (null != listTypeAbsence
//				&& null != baseConge) {
//			for (RefTypeAbsenceDto typeAbsence : listTypeAbsence) {
//				if(null != typeAbsence.getTypeSaisiCongeAnnuelDto()
//						&& typeAbsence.getTypeSaisiCongeAnnuelDto().getIdRefTypeSaisiCongeAnnuel().equals(baseConge.getIdRefTypeSaisiCongeAnnuel())) {
//					if(null != typeAbsence.getTypeSaisiCongeAnnuelDto().getQuotaMultiple()
//							&& 0 < typeAbsence.getTypeSaisiCongeAnnuelDto().getQuotaMultiple()) {
//						return true;
//					}
//					break;
//				}
//			}
//		}
//		return false;
//	}
	
	protected boolean isAgentWithIndemniteForfaitaireTravailDPMInAffectation(Integer idAgent, Date date) {
		
		if(null == date) {
			date = helperService.getCurrentDate();
		}
		
		List<Integer> listNoRubr = sirhWSConsumer.getPrimePointagesByAgent(idAgent, date, date);

		if(null != listNoRubr) {
			for(Integer noRubr : listNoRubr) {
				if(noRubr.equals(VentilationPrimeService.RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_SAMEDI_DPM)
						|| noRubr.equals(VentilationPrimeService.RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_DJF_DPM)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	@Transactional(value = "ptgTransactionManager")
	public ReturnMessageDto deleteIndemniteChoixAgentForKiosque(Integer idAgentConnecte, Integer idDpmIndemChoixAgent) {
		
		// gestion des droits
		if(null == idAgentConnecte
				|| !sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte).getErrors().isEmpty()) {
			throw new AccessForbiddenException();
		}
		
		ReturnMessageDto result = new ReturnMessageDto();
		
		// si oui, on enregistre le choix
		// on recherche si un choix existe deja pour cette annee
		DpmIndemChoixAgent choixAgent = dpmRepository.getEntity(DpmIndemChoixAgent.class, idDpmIndemChoixAgent);
		
		if(null == choixAgent) {
			logger.debug(NON_TROUVE);
			result.getErrors().add(NON_TROUVE);
			return result;
		}
		
		if(!isPeriodeChoixOuverte(choixAgent.getDpmIndemAnnee().getAnnee())) {
			logger.debug(HORS_PERIODE);
			result.getErrors().add(HORS_PERIODE);
			return result;
		}
		
		dpmRepository.removeEntity(choixAgent);
		
		result.getInfos().add(SUPPRESSION_OK);
		
		return result;
	}
	
	@Override
	public boolean isDroitAgentToIndemniteForfaitaireDPMForOneDay(Integer idAgent, LocalDate date) {
		
		boolean isJourFerie = false;
		// la prime est applicable les samedi, dimanche et jours feries
		if(DateTimeConstants.SATURDAY != date.getDayOfWeek()
				&& DateTimeConstants.SUNDAY != date.getDayOfWeek()){
			
			isJourFerie = sirhWSConsumer.isJourFerie(date.toDateTime(new LocalTime(0)));
			
			if(!isJourFerie)
				return false;
		}
		
		// doit avoir la prime Indemnité forfaitaire travail DPM sur son affectation courante
		List<Integer> listNoRubr = sirhWSConsumer.getPrimePointagesByAgent(idAgent, date.toDate(), date.toDate());

		if(null != listNoRubr) {
			for(Integer noRubr : listNoRubr) {

				// si prime DJF et on est dimanche ou JF => ok
				if(noRubr.equals(VentilationPrimeService.RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_DJF_DPM)
						&& (DateTimeConstants.SUNDAY == date.getDayOfWeek() || isJourFerie) ) {
					return true;
				}
				// si prime samedi et on est samedi => ok
				if(noRubr.equals(VentilationPrimeService.RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_SAMEDI_DPM)
						&& DateTimeConstants.SATURDAY == date.getDayOfWeek()) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public int calculNombreMinutesRecupereesMajoreesToAgentForOnePointage(Pointage ptg) {
		
		int result = 0;
		
		if(!isDroitAgentToIndemniteForfaitaireDPMForOneDay(ptg.getIdAgent(), new DateTime(ptg.getDateDebut()).toLocalDate()))
			return result;
		
		// l interval de la deliberation pour la prime est de 5h a 21h
		int dayTotalMinutes = helperService.calculMinutesPointageInInterval(ptg, 
				new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM,0,0), 
				new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM,0,0));
		
		if(dayTotalMinutes < PointageCalculeService.SEUIL_MINI_PRIME_DPM)
			return result;
		
		DpmIndemChoixAgent choixAgent = dpmRepository.getDpmIndemChoixAgent(ptg.getIdAgent(), new DateTime(ptg.getDateDebut()).getYear());
		
		if(null == choixAgent) {
			logger.error(String.format("Aucun choix de l agent %d pour la prime DPM => aucune prime DPM calcule", ptg.getIdAgent()));
			return result;
		}
		if(choixAgent.isChoixIndemnite()) {
			logger.debug(String.format("Choix de l agent %d pour la prime DPM : indemnite => aucune prime DPM calcule", ptg.getIdAgent()));
			return result;
		}
		
		if(choixAgent.isChoixRecuperation()) {
			result = dayTotalMinutes;
		}
		
		return result;
	}

}
