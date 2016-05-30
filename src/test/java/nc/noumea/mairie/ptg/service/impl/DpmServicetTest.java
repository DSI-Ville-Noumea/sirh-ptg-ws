package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
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
import nc.noumea.mairie.ptg.web.AccessForbiddenException;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;
import nc.noumea.mairie.ws.SirhWSUtils;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class DpmServicetTest {
	
	private DpmService service;

	private IAccessRightsService accessRightsService;
	private SirhWSUtils sirhWSUtils;
	private ISirhWSConsumer sirhWSConsumer;
	private IAbsWsConsumer absWsConsumer;
	private IDpmRepository dpmRepository;
	private HelperService helperService; 
	
	@Before
	public void before() {
		service = new DpmService();
		accessRightsService = Mockito.mock(IAccessRightsService.class);
		sirhWSUtils = Mockito.mock(SirhWSUtils.class);
		sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		dpmRepository = Mockito.mock(IDpmRepository.class);
		helperService = Mockito.mock(HelperService.class);
		
		ReflectionTestUtils.setField(service, "accessRightsService", accessRightsService);
		ReflectionTestUtils.setField(service, "sirhWSUtils", sirhWSUtils);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", sirhWSConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "dpmRepository", dpmRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);
	}
	
	@Test
	public void isAgentWithIndemniteForfaitaireTravailDPMInAffectation_null() {
		
		Integer idAgent = 9005138;
		Date dateJour = new Date(); 
		
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		
		Mockito.when(sirhWSConsumer.getPrimePointagesByAgent(idAgent, dateJour, dateJour)).thenReturn(null);
		
		assertFalse(service.isAgentWithIndemniteForfaitaireTravailDPMInAffectation(idAgent));
	}
	
	@Test
	public void isAgentWithIndemniteForfaitaireTravailDPMInAffectation_false() {
		
		Integer idAgent = 9005138;
		Date dateJour = new Date(); 
		
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		
		List<Integer> listNoRubr = Arrays.asList(1011);
		Mockito.when(sirhWSConsumer.getPrimePointagesByAgent(idAgent, dateJour, dateJour)).thenReturn(listNoRubr);
		
		assertFalse(service.isAgentWithIndemniteForfaitaireTravailDPMInAffectation(idAgent));
	}
	
	@Test
	public void isAgentWithIndemniteForfaitaireTravailDPMInAffectation_true() {
		
		Integer idAgent = 9005138;
		Date dateJour = new Date(); 
		
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		
		List<Integer> listNoRubr = Arrays.asList(DpmService.RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_DPM);
		Mockito.when(sirhWSConsumer.getPrimePointagesByAgent(idAgent, dateJour, dateJour)).thenReturn(listNoRubr);
		
		assertTrue(service.isAgentWithIndemniteForfaitaireTravailDPMInAffectation(idAgent));
	}
	
	@Test
	public void isAgentCycleConge_returnNull() {
		
		Integer idAgent = 9005138;
		
		RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDto = new RefTypeSaisiCongeAnnuelDto();
		typeSaisiCongeAnnuelDto.setQuotaMultiple(null);
		RefTypeAbsenceDto typeDto = new RefTypeAbsenceDto();
		typeDto.setTypeSaisiCongeAnnuelDto(typeSaisiCongeAnnuelDto);
		
		RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDto2 = new RefTypeSaisiCongeAnnuelDto();
		typeSaisiCongeAnnuelDto2.setQuotaMultiple(3);
		RefTypeAbsenceDto typeDto2 = new RefTypeAbsenceDto();
		typeDto2.setTypeSaisiCongeAnnuelDto(typeSaisiCongeAnnuelDto2);
		
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(typeDto);
		listTypeAbsence.add(typeDto2);
		
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);
		
		Mockito.when(sirhWSConsumer.getBaseHoraireAbsence(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(null);
		
		assertFalse(service.isAgentCycleConge(idAgent));
	}
	
	@Test
	public void isAgentCycleConge_false() {
		
		Integer idAgent = 9005138;
		
		RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDto = new RefTypeSaisiCongeAnnuelDto();
		typeSaisiCongeAnnuelDto.setQuotaMultiple(null);
		typeSaisiCongeAnnuelDto.setIdRefTypeSaisiCongeAnnuel(1);
		RefTypeAbsenceDto typeDto = new RefTypeAbsenceDto();
		typeDto.setTypeSaisiCongeAnnuelDto(typeSaisiCongeAnnuelDto);
		
		RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDto2 = new RefTypeSaisiCongeAnnuelDto();
		typeSaisiCongeAnnuelDto2.setQuotaMultiple(3);
		typeSaisiCongeAnnuelDto2.setIdRefTypeSaisiCongeAnnuel(2);
		RefTypeAbsenceDto typeDto2 = new RefTypeAbsenceDto();
		typeDto2.setTypeSaisiCongeAnnuelDto(typeSaisiCongeAnnuelDto2);
		
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(typeDto);
		listTypeAbsence.add(typeDto2);
		
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);
		
		RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDtoAgent = new RefTypeSaisiCongeAnnuelDto();
		typeSaisiCongeAnnuelDtoAgent.setIdRefTypeSaisiCongeAnnuel(3);
		
		Mockito.when(sirhWSConsumer.getBaseHoraireAbsence(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(typeSaisiCongeAnnuelDtoAgent);
		
		assertFalse(service.isAgentCycleConge(idAgent));
		
		Mockito.when(sirhWSConsumer.getBaseHoraireAbsence(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(typeSaisiCongeAnnuelDto);
		
		assertFalse(service.isAgentCycleConge(idAgent));
	}
	
	@Test
	public void isAgentCycleConge_true() {
		
		Integer idAgent = 9005138;
		
		RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDto = new RefTypeSaisiCongeAnnuelDto();
		typeSaisiCongeAnnuelDto.setQuotaMultiple(null);
		typeSaisiCongeAnnuelDto.setIdRefTypeSaisiCongeAnnuel(1);
		RefTypeAbsenceDto typeDto = new RefTypeAbsenceDto();
		typeDto.setTypeSaisiCongeAnnuelDto(typeSaisiCongeAnnuelDto);
		
		RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDto2 = new RefTypeSaisiCongeAnnuelDto();
		typeSaisiCongeAnnuelDto2.setQuotaMultiple(3);
		typeSaisiCongeAnnuelDto2.setIdRefTypeSaisiCongeAnnuel(2);
		RefTypeAbsenceDto typeDto2 = new RefTypeAbsenceDto();
		typeDto2.setTypeSaisiCongeAnnuelDto(typeSaisiCongeAnnuelDto2);
		
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(typeDto);
		listTypeAbsence.add(typeDto2);
		
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);
		
		Mockito.when(sirhWSConsumer.getBaseHoraireAbsence(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(typeSaisiCongeAnnuelDto2);
		
		assertTrue(service.isAgentCycleConge(idAgent));
	}
	
	@Test(expected = AccessForbiddenException.class)
	public void saveDpmIndemAnnee_AccessForbiddenException() {
		
		Integer idAgentConnecte = null;
		DpmIndemniteAnneeDto dto = new DpmIndemniteAnneeDto();
		
		service.saveDpmIndemAnnee(idAgentConnecte, dto);
	}
	
	@Test(expected = AccessForbiddenException.class)
	public void saveDpmIndemAnnee_AccessForbiddenException_bis() {
		
		Integer idAgentConnecte = 9005138;
		DpmIndemniteAnneeDto dto = new DpmIndemniteAnneeDto();
		
		ReturnMessageDto isUtilisateurSIRH = new ReturnMessageDto();
		isUtilisateurSIRH.getErrors().add("error");
		
		Mockito.when(sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte)).thenReturn(isUtilisateurSIRH);
		
		service.saveDpmIndemAnnee(idAgentConnecte, dto);
	}
	
	@Test
	public void saveDpmIndemAnnee_CreateKO() {
		
		Integer idAgentConnecte = 9005138;
		DpmIndemniteAnneeDto dto = null;
		
		ReturnMessageDto isUtilisateurSIRH = new ReturnMessageDto();
		
		Mockito.when(sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte)).thenReturn(isUtilisateurSIRH);
		
		ReturnMessageDto result = service.saveDpmIndemAnnee(idAgentConnecte, dto);
		
		assertEquals(result.getErrors().size(), 1);
		assertEquals(result.getErrors().get(0), DpmService.CREATION_IMPOSSIBLE);
		
		dto = new DpmIndemniteAnneeDto();
		result = service.saveDpmIndemAnnee(idAgentConnecte, dto);
		
		assertEquals(result.getErrors().size(), 1);
		assertEquals(result.getErrors().get(0), DpmService.CREATION_IMPOSSIBLE);
	}
	
	@Test
	public void saveDpmIndemAnnee_champsObligatoires() {
		
		Integer idAgentConnecte = 9005138;
		DpmIndemniteAnneeDto dto = new DpmIndemniteAnneeDto();
		dto.setIdDpmIndemAnnee(1);
		
		ReturnMessageDto isUtilisateurSIRH = new ReturnMessageDto();
		Mockito.when(sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte)).thenReturn(isUtilisateurSIRH);
		
		Mockito.when(dpmRepository.getEntity(DpmIndemAnnee.class, dto.getIdDpmIndemAnnee())).thenReturn(null);
		
		ReturnMessageDto result = service.saveDpmIndemAnnee(idAgentConnecte, dto);
		
		assertEquals(result.getErrors().size(), 1);
		assertEquals(result.getErrors().get(0), DpmService.CHAMPS_NON_REMPLIE);
	}
	
	@Test
	public void saveDpmIndemAnnee_notFind() {
		
		Integer idAgentConnecte = 9005138;
		DpmIndemniteAnneeDto dto = new DpmIndemniteAnneeDto();
		dto.setIdDpmIndemAnnee(1);
		dto.setDateDebut(new Date());
		dto.setDateFin(new Date());
		
		ReturnMessageDto isUtilisateurSIRH = new ReturnMessageDto();
		Mockito.when(sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte)).thenReturn(isUtilisateurSIRH);
		
		Mockito.when(dpmRepository.getEntity(DpmIndemAnnee.class, dto.getIdDpmIndemAnnee())).thenReturn(null);
		
		ReturnMessageDto result = service.saveDpmIndemAnnee(idAgentConnecte, dto);
		
		assertEquals(result.getErrors().size(), 1);
		assertEquals(result.getErrors().get(0), DpmService.NON_TROUVE);
	}
	
	@Test
	public void saveDpmIndemAnnee_ok() {
		
		Integer idAgentConnecte = 9005138;
		DpmIndemniteAnneeDto dto = new DpmIndemniteAnneeDto();
		dto.setIdDpmIndemAnnee(1);
		dto.setDateDebut(new Date());
		dto.setDateFin(new Date());
		
		ReturnMessageDto isUtilisateurSIRH = new ReturnMessageDto();
		Mockito.when(sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte)).thenReturn(isUtilisateurSIRH);
		
		DpmIndemAnnee dpmAnnee = Mockito.spy(new DpmIndemAnnee());
		Mockito.when(dpmRepository.getEntity(DpmIndemAnnee.class, dto.getIdDpmIndemAnnee())).thenReturn(dpmAnnee);
		
		ReturnMessageDto result = service.saveDpmIndemAnnee(idAgentConnecte, dto);
		
		assertEquals(result.getErrors().size(), 0);
		assertEquals(result.getInfos().size(), 1);
		assertEquals(result.getInfos().get(0), DpmService.MODIFICATION_OK);
		
		Mockito.verify(dpmRepository, Mockito.times(1)).persisEntity(dpmAnnee);
		assertEquals(dpmAnnee.getDateDebut(), dto.getDateDebut());
		assertEquals(dpmAnnee.getDateFin(), dto.getDateFin());
	}
	
	@Test(expected = AccessForbiddenException.class)
	public void getListDpmIndemAnnee_AccessForbiddenException() {
		
		Integer idAgentConnecte = null;
		
		service.getListDpmIndemAnnee(idAgentConnecte);
	}
	
	@Test(expected = AccessForbiddenException.class)
	public void getListDpmIndemAnnee_AccessForbiddenException_bis() {
		
		Integer idAgentConnecte = 9005138;
		
		ReturnMessageDto isUtilisateurSIRH = new ReturnMessageDto();
		isUtilisateurSIRH.getErrors().add("error");
		
		Mockito.when(sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte)).thenReturn(isUtilisateurSIRH);
		
		service.getListDpmIndemAnnee(idAgentConnecte);
	}
	
	@Test
	public void getListDpmIndemAnnee_noResult() {
		
		Integer idAgentConnecte = 9005138;
		
		ReturnMessageDto isUtilisateurSIRH = new ReturnMessageDto();
		
		Mockito.when(sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte)).thenReturn(isUtilisateurSIRH);
		
		Mockito.when(dpmRepository.getListDpmIndemAnnee()).thenReturn(null);
		
		assertEquals(0, service.getListDpmIndemAnnee(idAgentConnecte).size());
	}
	
	@Test
	public void getListDpmIndemAnnee_2results() {
		
		Integer idAgentConnecte = 9005138;
		
		ReturnMessageDto isUtilisateurSIRH = new ReturnMessageDto();
		
		Mockito.when(sirhWSConsumer.isUtilisateurSIRH(idAgentConnecte)).thenReturn(isUtilisateurSIRH);
		
		DpmIndemAnnee dpmAnnee = new DpmIndemAnnee();
		DpmIndemAnnee dpmAnnee2 = new DpmIndemAnnee();
		List<DpmIndemAnnee> listDpmIndemAnnee = new ArrayList<DpmIndemAnnee>();
		listDpmIndemAnnee.add(dpmAnnee);
		listDpmIndemAnnee.add(dpmAnnee2);
		Mockito.when(dpmRepository.getListDpmIndemAnnee()).thenReturn(listDpmIndemAnnee);
		
		assertEquals(2, service.getListDpmIndemAnnee(idAgentConnecte).size());
	}
	
	@Test
	public void isDroitAgentToIndemniteForfaitaireDPM_false() {
		Integer idAgent = 9005138;
		
		isAgentCycleConge_true();
		
		assertFalse(service.isDroitAgentToIndemniteForfaitaireDPM(idAgent));
	}
	
	@Test
	public void isDroitAgentToIndemniteForfaitaireDPM_false_bis() {
		Integer idAgent = 9005138;
		
		isAgentCycleConge_false();
		isAgentWithIndemniteForfaitaireTravailDPMInAffectation_false();
		
		assertFalse(service.isDroitAgentToIndemniteForfaitaireDPM(idAgent));
	}
	
	@Test
	public void isDroitAgentToIndemniteForfaitaireDPM_true() {
		Integer idAgent = 9005138;
		
		isAgentCycleConge_false();
		isAgentWithIndemniteForfaitaireTravailDPMInAffectation_true();
		
		assertTrue(service.isDroitAgentToIndemniteForfaitaireDPM(idAgent));
	}
	
	@Test
	public void isPeriodeChoixOuverte_null_false() {
		
		Integer annee = 2016;
		
		Mockito.when(dpmRepository.getDpmIndemAnneeByAnnee(annee)).thenReturn(null);
		
		assertFalse(service.isPeriodeChoixOuverte(annee));
	}
	
	@Test
	public void isPeriodeChoixOuverte_false() {
		
		Integer annee = 2016;
		
		Mockito.when(helperService.getCurrentDate()).thenReturn(new Date());
		
		DpmIndemAnnee dpmIndemAnnee = new DpmIndemAnnee();
		dpmIndemAnnee.setDateDebut(new DateTime().minusDays(10).toDate());
		dpmIndemAnnee.setDateFin(new DateTime().minusDays(5).toDate());
		Mockito.when(dpmRepository.getDpmIndemAnneeByAnnee(annee)).thenReturn(dpmIndemAnnee);
		
		assertFalse(service.isPeriodeChoixOuverte(annee));
	}
	
	@Test
	public void isPeriodeChoixOuverte_true() {
		
		Integer annee = 2016;
		
		Mockito.when(helperService.getCurrentDate()).thenReturn(new Date());
		
		DpmIndemAnnee dpmIndemAnnee = new DpmIndemAnnee();
		dpmIndemAnnee.setDateDebut(new DateTime().minusDays(10).toDate());
		dpmIndemAnnee.setDateFin(new DateTime().plusDays(5).toDate());
		Mockito.when(dpmRepository.getDpmIndemAnneeByAnnee(annee)).thenReturn(dpmIndemAnnee);
		
		assertTrue(service.isPeriodeChoixOuverte(annee));
	}
	
	@Test(expected = AccessForbiddenException.class)
	public void getListDpmIndemniteChoixAgent_AccessForbiddenException() {
		
		Integer idAgentConnecte = null;
		Integer annee = 2016;
		
		service.getListDpmIndemniteChoixAgent(idAgentConnecte, annee);
	}
	
	@Test(expected = AccessForbiddenException.class)
	public void getListDpmIndemniteChoixAgent_AccessForbiddenException_bis() {
		
		Integer idAgentConnecte = 9005138;
		Integer annee = 2016;
		
		Mockito.when(sirhWSUtils.isAgentDPM(idAgentConnecte)).thenReturn(false);
		
		service.getListDpmIndemniteChoixAgent(idAgentConnecte, annee);
	}
	
	@Test
	public void getListDpmIndemniteChoixAgent_agent_returnNull() {
		
		Integer idAgentConnecte = 9005138;
		Integer annee = 2016;
		
		Mockito.when(sirhWSUtils.isAgentDPM(idAgentConnecte)).thenReturn(true);
		Mockito.when(accessRightsService.isUserOperateur(idAgentConnecte)).thenReturn(false);
		
		List<Integer> listIdsAgent = Arrays.asList(idAgentConnecte);
		Mockito.when(dpmRepository.getListDpmIndemChoixAgent(listIdsAgent, annee)).thenReturn(null);
		
		assertEquals(0, service.getListDpmIndemniteChoixAgent(idAgentConnecte, annee).size());
	}
	
	@Test
	public void getListDpmIndemniteChoixAgent_agent_return2results() {
		
		Integer idAgentConnecte = 9005138;
		Integer annee = 2016;
		
		Mockito.when(sirhWSUtils.isAgentDPM(idAgentConnecte)).thenReturn(true);
		Mockito.when(accessRightsService.isUserOperateur(idAgentConnecte)).thenReturn(false);
		
		DpmIndemChoixAgent choixAgent = new DpmIndemChoixAgent();
		DpmIndemChoixAgent choixAgent2 = new DpmIndemChoixAgent();
		List<DpmIndemChoixAgent> listDpmChoixAgent= new ArrayList<DpmIndemChoixAgent>();
		listDpmChoixAgent.add(choixAgent);
		listDpmChoixAgent.add(choixAgent2);
		
		List<Integer> listIdsAgent = Arrays.asList(idAgentConnecte);
		Mockito.when(dpmRepository.getListDpmIndemChoixAgent(listIdsAgent, annee)).thenReturn(listDpmChoixAgent);
		
		assertEquals(2, service.getListDpmIndemniteChoixAgent(idAgentConnecte, annee).size());
	}
	
	@Test
	public void getListDpmIndemniteChoixAgent_operateur_return2results() {
		
		Integer idAgentConnecte = 9005138;
		Integer annee = 2016;
		
		Mockito.when(sirhWSUtils.isAgentDPM(idAgentConnecte)).thenReturn(true);
		Mockito.when(accessRightsService.isUserOperateur(idAgentConnecte)).thenReturn(true);
		
		AgentDto agent1 = new AgentDto();
		agent1.setIdAgent(9005131);
		AgentDto agent2 = new AgentDto();
		agent2.setIdAgent(9005141);
		List<AgentDto> listAgents = new ArrayList<AgentDto>();
		listAgents.add(agent1);
		listAgents.add(agent2);
		
		Mockito.when(accessRightsService.getAgentsToApproveOrInput(idAgentConnecte, null, null)).thenReturn(listAgents);
		
		DpmIndemChoixAgent choixAgent = new DpmIndemChoixAgent();
		DpmIndemChoixAgent choixAgent2 = new DpmIndemChoixAgent();
		List<DpmIndemChoixAgent> listDpmChoixAgent= new ArrayList<DpmIndemChoixAgent>();
		listDpmChoixAgent.add(choixAgent);
		listDpmChoixAgent.add(choixAgent2);
		
		List<Integer> listIdsAgent = Arrays.asList(agent1.getIdAgent(), agent2.getIdAgent());
		Mockito.when(dpmRepository.getListDpmIndemChoixAgent(listIdsAgent, annee)).thenReturn(listDpmChoixAgent);
		
		assertEquals(2, service.getListDpmIndemniteChoixAgent(idAgentConnecte, annee).size());
	}
	
	@Test
	public void saveListIndemniteChoixAgentForOperator_horsPeriode() {
		
		Integer idAgentConnecte = 9005138;
		Integer annee = 2016;
		List<DpmIndemniteChoixAgentDto> listDto = new ArrayList<DpmIndemniteChoixAgentDto>();
		
		isPeriodeChoixOuverte_false();
		
		ReturnMessageDto result = service.saveListIndemniteChoixAgentForOperator(idAgentConnecte, annee, listDto);
		assertEquals(1, result.getErrors().size());
		assertEquals(DpmService.HORS_PERIODE, result.getErrors().get(0));
	}
	
	@Test(expected = AccessForbiddenException.class)
	public void saveListIndemniteChoixAgentForOperator_AccessForbiddenException() {
		
		Integer idAgentConnecte = null;
		Integer annee = 2016;
		List<DpmIndemniteChoixAgentDto> listDto = new ArrayList<DpmIndemniteChoixAgentDto>();
		
		isPeriodeChoixOuverte_true();
		
		Mockito.when(accessRightsService.isUserOperateur(idAgentConnecte)).thenReturn(true);
		
		ReturnMessageDto result = service.saveListIndemniteChoixAgentForOperator(idAgentConnecte, annee, listDto);
		assertEquals(1, result.getErrors().size());
		assertEquals(DpmService.HORS_PERIODE, result.getErrors().get(0));
	}
	
	@Test(expected = AccessForbiddenException.class)
	public void saveListIndemniteChoixAgentForOperator_AccessForbiddenException_notOperateur() {
		
		Integer idAgentConnecte = 9005138;
		Integer annee = 2016;
		List<DpmIndemniteChoixAgentDto> listDto = new ArrayList<DpmIndemniteChoixAgentDto>();
			
			isPeriodeChoixOuverte_true();

		Mockito.when(accessRightsService.isUserOperateur(idAgentConnecte)).thenReturn(false);
		
		ReturnMessageDto result = service.saveListIndemniteChoixAgentForOperator(idAgentConnecte, annee, listDto);
		assertEquals(1, result.getErrors().size());
		assertEquals(DpmService.HORS_PERIODE, result.getErrors().get(0));
	}
	
	private void isAgentCycleConge_false(Integer idAgent) {
		
		RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDto = new RefTypeSaisiCongeAnnuelDto();
		typeSaisiCongeAnnuelDto.setQuotaMultiple(null);
		typeSaisiCongeAnnuelDto.setIdRefTypeSaisiCongeAnnuel(1);
		RefTypeAbsenceDto typeDto = new RefTypeAbsenceDto();
		typeDto.setTypeSaisiCongeAnnuelDto(typeSaisiCongeAnnuelDto);
		
		List<RefTypeAbsenceDto> listTypeAbsence = new ArrayList<RefTypeAbsenceDto>();
		listTypeAbsence.add(typeDto);
		
		Mockito.when(absWsConsumer.getListeTypAbsenceCongeAnnuel()).thenReturn(listTypeAbsence);
		
		Mockito.when(sirhWSConsumer.getBaseHoraireAbsence(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(typeSaisiCongeAnnuelDto);
		
		assertFalse(service.isAgentCycleConge(idAgent));
	}
	
	private void isAgentWithIndemniteForfaitaireTravailDPMInAffectation_true(Integer idAgent, Date dateJour) {
		
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		
		List<Integer> listNoRubr = Arrays.asList(DpmService.RUBRIQUE_INDEMNITE_FORFAITAIRE_TRAVAIL_DPM);
		Mockito.when(sirhWSConsumer.getPrimePointagesByAgent(idAgent, dateJour, dateJour)).thenReturn(listNoRubr);
		
		assertTrue(service.isAgentWithIndemniteForfaitaireTravailDPMInAffectation(idAgent));
	}
	
	public void isPeriodeChoixOuverte_true(Date dateJour) {
		
		Integer annee = 2016;
		
		Mockito.when(helperService.getCurrentDate()).thenReturn(dateJour);
		
		DpmIndemAnnee dpmIndemAnnee = new DpmIndemAnnee();
		dpmIndemAnnee.setDateDebut(new DateTime().minusDays(10).toDate());
		dpmIndemAnnee.setDateFin(new DateTime().plusDays(5).toDate());
		Mockito.when(dpmRepository.getDpmIndemAnneeByAnnee(annee)).thenReturn(dpmIndemAnnee);
		
		assertTrue(service.isPeriodeChoixOuverte(annee));
	}
	
	@Test
	public void saveListIndemniteChoixAgentForOperator_save() {
		
		Integer idAgentConnecte = 9005138;
		Integer annee = 2016;
		
		Date dateJour = new Date(); 
		
		// cas n°1  l agent n a pas le droit a la prime 
		DpmIndemniteChoixAgentDto dtoAGENT_INTERDIT = new DpmIndemniteChoixAgentDto();
		dtoAGENT_INTERDIT.setIdAgent(9001111);
		
		// cas n°2 : dto mal rempli
		DpmIndemniteChoixAgentDto dtoAGENT_CHOIX_OBLIGATOIRE = new DpmIndemniteChoixAgentDto();
		dtoAGENT_CHOIX_OBLIGATOIRE.setIdAgent(9001999);
		
		isAgentWithIndemniteForfaitaireTravailDPMInAffectation_true(dtoAGENT_CHOIX_OBLIGATOIRE.getIdAgent(), dateJour);
		isAgentCycleConge_false(dtoAGENT_CHOIX_OBLIGATOIRE.getIdAgent());

		// cas n°3 : on met a jour
		DpmIndemniteChoixAgentDto dtoUpdate = new DpmIndemniteChoixAgentDto();
		dtoUpdate.setIdAgent(9002000);
		dtoUpdate.setChoixIndemnite(true);
		
		isAgentWithIndemniteForfaitaireTravailDPMInAffectation_true(dtoUpdate.getIdAgent(), dateJour);
		isAgentCycleConge_false(dtoUpdate.getIdAgent());

		// cas n°4 : on cree
		DpmIndemniteChoixAgentDto dtoCreate = new DpmIndemniteChoixAgentDto();
		dtoCreate.setIdAgent(9002022);
		dtoCreate.setChoixRecuperation(true);
		
		isAgentWithIndemniteForfaitaireTravailDPMInAffectation_true(dtoCreate.getIdAgent(), dateJour);
		isAgentCycleConge_false(dtoCreate.getIdAgent());
		
		List<DpmIndemniteChoixAgentDto> listDto = new ArrayList<DpmIndemniteChoixAgentDto>();
		listDto.add(dtoAGENT_INTERDIT);
		listDto.add(dtoAGENT_CHOIX_OBLIGATOIRE);
		listDto.add(dtoUpdate);
		listDto.add(dtoCreate);
		
		isPeriodeChoixOuverte_true(dateJour);
		
		DpmIndemChoixAgent choixAgent = new DpmIndemChoixAgent();
		choixAgent.setIdAgent(9002000);
		
		Mockito.when(dpmRepository.getDpmIndemChoixAgentByAgentAndAnnee(dtoUpdate.getIdAgent(), annee)).thenReturn(choixAgent);
		Mockito.when(dpmRepository.getDpmIndemChoixAgentByAgentAndAnnee(dtoCreate.getIdAgent(), annee)).thenReturn(null);

		Mockito.when(accessRightsService.isUserOperateur(idAgentConnecte)).thenReturn(true);
		
		ReturnMessageDto result = service.saveListIndemniteChoixAgentForOperator(idAgentConnecte, annee, listDto);
		
		assertEquals(2, result.getErrors().size());
		assertEquals(1, result.getInfos().size());
		assertEquals(String.format(DpmService.AGENT_INTERDIT, dtoAGENT_INTERDIT.getIdAgent()), result.getErrors().get(0));
		assertEquals(String.format(DpmService.AGENT_CHOIX_OBLIGATOIRE, dtoAGENT_CHOIX_OBLIGATOIRE.getIdAgent()), result.getErrors().get(1));
		assertEquals(DpmService.MODIFICATION_OK, result.getInfos().get(0));
		
		Mockito.verify(dpmRepository, Mockito.times(2)).persisEntity(Mockito.any(DpmIndemChoixAgent.class));
	}
	
	@Test(expected = AccessForbiddenException.class)
	public void saveIndemniteChoixAgent_AccessForbiddenException() {
		
		Integer idAgentConnecte = null;
		
		DpmIndemniteAnneeDto dpmIndemniteAnnee = new DpmIndemniteAnneeDto();
		dpmIndemniteAnnee.setAnnee(2016);
		
		DpmIndemniteChoixAgentDto dto = new DpmIndemniteChoixAgentDto();
		dto.setDpmIndemniteAnnee(dpmIndemniteAnnee);
		dto.setIdAgent(idAgentConnecte);
		
		isPeriodeChoixOuverte_false();
		
		service.saveIndemniteChoixAgent(idAgentConnecte, dto);
	}
	
	@Test
	public void saveIndemniteChoixAgent_HorsPeriode() {
		
		Integer idAgentConnecte = 9005138;
		
		DpmIndemniteAnneeDto dpmIndemniteAnnee = new DpmIndemniteAnneeDto();
		dpmIndemniteAnnee.setAnnee(2016);
		
		DpmIndemniteChoixAgentDto dto = new DpmIndemniteChoixAgentDto();
		dto.setDpmIndemniteAnnee(dpmIndemniteAnnee);
		dto.setIdAgent(idAgentConnecte);
		
		isPeriodeChoixOuverte_false();
		
		ReturnMessageDto result = service.saveIndemniteChoixAgent(idAgentConnecte, dto);

		assertEquals(1, result.getErrors().size());
		assertEquals(DpmService.HORS_PERIODE, result.getErrors().get(0));
	}
	
	@Test
	public void saveIndemniteChoixAgent_interdit() {
		
		Integer idAgentConnecte = 9005138;
		
		DpmIndemniteAnneeDto dpmIndemniteAnnee = new DpmIndemniteAnneeDto();
		dpmIndemniteAnnee.setAnnee(2016);
		
		DpmIndemniteChoixAgentDto dto = new DpmIndemniteChoixAgentDto();
		dto.setDpmIndemniteAnnee(dpmIndemniteAnnee);
		dto.setIdAgent(idAgentConnecte);
		
		isPeriodeChoixOuverte_true();
		
		ReturnMessageDto result = service.saveIndemniteChoixAgent(idAgentConnecte, dto);

		assertEquals(1, result.getErrors().size());
		assertEquals(DpmService.INTERDIT, result.getErrors().get(0));
	}
	
	@Test
	public void saveIndemniteChoixAgent_CHOIX_OBLIGATOIRE() {
		
		Integer idAgentConnecte = 9005138;
		Date dateJour = new Date();
		
		DpmIndemniteAnneeDto dpmIndemniteAnnee = new DpmIndemniteAnneeDto();
		dpmIndemniteAnnee.setAnnee(2016);
		
		DpmIndemniteChoixAgentDto dto = new DpmIndemniteChoixAgentDto();
		dto.setDpmIndemniteAnnee(dpmIndemniteAnnee);
		dto.setIdAgent(idAgentConnecte);
		
		isPeriodeChoixOuverte_true();
		isAgentWithIndemniteForfaitaireTravailDPMInAffectation_true(idAgentConnecte, dateJour);
		
		ReturnMessageDto result = service.saveIndemniteChoixAgent(idAgentConnecte, dto);

		assertEquals(1, result.getErrors().size());
		assertEquals(DpmService.CHOIX_OBLIGATOIRE, result.getErrors().get(0));
	}
	
	@Test
	public void saveIndemniteChoixAgent_create_ok() {
		
		Integer idAgentConnecte = 9005138;
		Date dateJour = new Date();
		
		DpmIndemniteAnneeDto dpmIndemniteAnnee = new DpmIndemniteAnneeDto();
		dpmIndemniteAnnee.setAnnee(2016);
		
		DpmIndemniteChoixAgentDto dto = new DpmIndemniteChoixAgentDto();
		dto.setDpmIndemniteAnnee(dpmIndemniteAnnee);
		dto.setIdAgent(idAgentConnecte);
		dto.setChoixIndemnite(true);
		
		isPeriodeChoixOuverte_true();
		isAgentWithIndemniteForfaitaireTravailDPMInAffectation_true(idAgentConnecte, dateJour);
		
		Mockito.when(dpmRepository.getDpmIndemChoixAgentByAgentAndAnnee(dto.getIdAgent(), dto.getDpmIndemniteAnnee().getAnnee())).thenReturn(null);
		
		ReturnMessageDto result = service.saveIndemniteChoixAgent(idAgentConnecte, dto);

		assertEquals(0, result.getErrors().size());
		assertEquals(DpmService.MODIFICATION_OK, result.getInfos().get(0));
		
		Mockito.verify(dpmRepository, Mockito.times(1)).persisEntity(Mockito.any(DpmIndemChoixAgent.class));
	}
	
	@Test
	public void saveIndemniteChoixAgent_update_ok() {
		
		Integer idAgentConnecte = 9005138;
		Date dateJour = new Date();
		
		DpmIndemniteAnneeDto dpmIndemniteAnnee = new DpmIndemniteAnneeDto();
		dpmIndemniteAnnee.setAnnee(2016);
		
		DpmIndemniteChoixAgentDto dto = new DpmIndemniteChoixAgentDto();
		dto.setDpmIndemniteAnnee(dpmIndemniteAnnee);
		dto.setIdAgent(idAgentConnecte);
		dto.setChoixIndemnite(true);
		
		isPeriodeChoixOuverte_true();
		isAgentWithIndemniteForfaitaireTravailDPMInAffectation_true(idAgentConnecte, dateJour);
		
		DpmIndemChoixAgent choixAgent = Mockito.spy(new DpmIndemChoixAgent());
		choixAgent.setDateMaj(new Date());
		
		Mockito.when(dpmRepository.getDpmIndemChoixAgentByAgentAndAnnee(dto.getIdAgent(), dto.getDpmIndemniteAnnee().getAnnee())).thenReturn(choixAgent);
		
		ReturnMessageDto result = service.saveIndemniteChoixAgent(idAgentConnecte, dto);

		assertEquals(0, result.getErrors().size());
		assertEquals(DpmService.MODIFICATION_OK, result.getInfos().get(0));
		assertEquals(choixAgent.getDateMaj(), dateJour);
		assertFalse(choixAgent.isChoixRecuperation());
		assertTrue(choixAgent.isChoixIndemnite());
		
		Mockito.verify(dpmRepository, Mockito.times(1)).persisEntity(Mockito.any(DpmIndemChoixAgent.class));
	}
}
