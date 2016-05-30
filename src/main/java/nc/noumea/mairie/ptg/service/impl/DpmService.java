package nc.noumea.mairie.ptg.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dto.RefTypeAbsenceDto;
import nc.noumea.mairie.ptg.domain.DpmIndemAnnee;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.DpmIndemniteAnneeDto;
import nc.noumea.mairie.ptg.dto.DpmIndemniteChoixAgentDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IDpmRepository;
import nc.noumea.mairie.ptg.service.IAccessRightsService;
import nc.noumea.mairie.ptg.service.IDpmService;
import nc.noumea.mairie.ptg.web.AccessForbiddenException;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;
import nc.noumea.mairie.ws.SirhWSUtils;

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
	protected final static String INTERDIT = "Vous n'avez pas le droit à l'Indemnité forfaitaire travail DPM.";
	protected final static String CHOIX_OBLIGATOIRE = "Veuillez choisir Indemnité ou Récupération.";
	protected final static String AGENT_INTERDIT = "L'agent %s le droit à l'Indemnité forfaitaire travail DPM.";
	protected final static String AGENT_CHOIX_OBLIGATOIRE = "Veuillez choisir Indemnité ou Récupération pour l'agent %S.";
	protected final static String CHAMPS_NON_REMPLIE = "Veuillez remplir tous les champs obligatoires.";
	
	//TODO
	public final static Integer RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_DPM = 7799;

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
	public ReturnMessageDto saveIndemniteChoixAgent(Integer idAgentConnecte, DpmIndemniteChoixAgentDto dto) {
		
		ReturnMessageDto result = new ReturnMessageDto();
		
		// gestion des droits
		if(null == idAgentConnecte
				|| null == dto
				|| !idAgentConnecte.equals(dto.getIdAgent())) {
			throw new AccessForbiddenException();
		}
		
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
		}
		
		// si oui, on enregistre le choix
		// on recherche si un choix existe deja pour cette annee
		DpmIndemChoixAgent choixAgent = dpmRepository.getDpmIndemChoixAgentByAgentAndAnnee(dto.getIdAgent(), dto.getDpmIndemniteAnnee().getAnnee());
		
		if(null == choixAgent) {
			choixAgent = new DpmIndemChoixAgent();
			choixAgent.setDpmIndemAnnee(dpmRepository.getDpmIndemAnneeByAnnee(dto.getDpmIndemniteAnnee().getAnnee()));
			choixAgent.setIdAgent(dto.getIdAgent());
			choixAgent.setIdAgentCreation(idAgentConnecte);
		}
		
		choixAgent.setDateMaj(helperService.getCurrentDate());
		choixAgent.setChoixIndemnite(dto.isChoixIndemnite());
		choixAgent.setChoixRecuperation(dto.isChoixRecuperation());
		
		dpmRepository.persisEntity(choixAgent);
		
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
			
			dpmRepository.persisEntity(choixAgent);
		}
		
		result.getInfos().add(MODIFICATION_OK);

		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DpmIndemniteChoixAgentDto> getListDpmIndemniteChoixAgent(Integer idAgentConnecte, Integer annee) {
		
		if(null == idAgentConnecte
				|| !sirhWSUtils.isAgentDPM(idAgentConnecte)) {
			throw new AccessForbiddenException();
		}
		
		List<Integer> listIdsAgent = new ArrayList<Integer>();
		// on cherche le role de l agent
		if(accessRightsService.isUserOperateur(idAgentConnecte)) {
			// si operateur, on recupere les agents qui lui sont affectes
			List<AgentDto> listAgents = accessRightsService.getAgentsToApproveOrInput(idAgentConnecte, null, null);
			
			if(null != listAgents
					&& !listAgents.isEmpty()) {
				for(AgentDto agent : listAgents) {
					if(!listIdsAgent.contains(agent.getIdAgent()))
						listIdsAgent.add(agent.getIdAgent());
				}
			}
		}else{
			listIdsAgent.add(idAgentConnecte);
		}
		
		List<DpmIndemChoixAgent> listDpmIndemChoixAgent = dpmRepository.getListDpmIndemChoixAgent(listIdsAgent, annee);
		
		List<DpmIndemniteChoixAgentDto> result = new ArrayList<DpmIndemniteChoixAgentDto>();
		
		if(null != listDpmIndemChoixAgent
				&& !listDpmIndemChoixAgent.isEmpty()) {
			for(DpmIndemChoixAgent choixAgent : listDpmIndemChoixAgent) {
				DpmIndemniteChoixAgentDto dto = new DpmIndemniteChoixAgentDto(choixAgent);
				result.add(dto);
			}
		}
		
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
		if(isAgentCycleConge(idAgent)) {
			return false;
		}
		
		// doit avoir la prime Indemnité forfaitaire travail DPM sur son affectation courante
		if(!isAgentWithIndemniteForfaitaireTravailDPMInAffectation(idAgent)) {
			return false;
		}
		
		return true;
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
		
		dpmRepository.persisEntity(dpmAnnee);
		
		result.getInfos().add(MODIFICATION_OK);
		
		return result;
	}
	
	protected boolean isAgentCycleConge(Integer idAgent) {

		List<RefTypeAbsenceDto> listTypeAbsence = absWsConsumer.getListeTypAbsenceCongeAnnuel();
		
		RefTypeSaisiCongeAnnuelDto baseConge = sirhWSConsumer.getBaseHoraireAbsence(idAgent, new Date());

		if (null != listTypeAbsence
				&& null != baseConge) {
			for (RefTypeAbsenceDto typeAbsence : listTypeAbsence) {
				if(null != typeAbsence.getTypeSaisiCongeAnnuelDto()
						&& typeAbsence.getTypeSaisiCongeAnnuelDto().getIdRefTypeSaisiCongeAnnuel().equals(baseConge.getIdRefTypeSaisiCongeAnnuel())) {
					if(null != typeAbsence.getTypeSaisiCongeAnnuelDto().getQuotaMultiple()
							&& 0 < typeAbsence.getTypeSaisiCongeAnnuelDto().getQuotaMultiple()) {
						return true;
					}
					break;
				}
			}
		}
		return false;
	}
	
	protected boolean isAgentWithIndemniteForfaitaireTravailDPMInAffectation(Integer idAgent) {
		
		List<Integer> listNoRubr = sirhWSConsumer.getPrimePointagesByAgent(idAgent, helperService.getCurrentDate(), helperService.getCurrentDate());

		if(null != listNoRubr) {
			for(Integer noRubr : listNoRubr) {
				if(RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_DPM.equals(noRubr)) {
					return true;
				}
			}
		}
		
		return false;
	}

}
