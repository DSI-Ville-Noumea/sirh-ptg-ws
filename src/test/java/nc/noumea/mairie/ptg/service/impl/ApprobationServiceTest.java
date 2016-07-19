package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.ConsultPointageDto;
import nc.noumea.mairie.ptg.dto.PointagesEtatChangeDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.AccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.service.IDpmService;
import nc.noumea.mairie.ptg.service.IPointageDataConsistencyRules;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;
import nc.noumea.mairie.ws.SirhWSUtils;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ApprobationServiceTest {

	@Test
	public void getPointages_NoFilters_ReturnAllPointagesForGivenPeriod() {

		// Given
		Integer idAgent = 9008765;
		Date fromDate = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Date toDate = new DateTime(2013, 05, 20, 0, 0, 0).toDate();
		Date toDateQuery = new DateTime(2013, 05, 21, 0, 0, 0).toDate();
		Integer idServiceAds = null;
		Integer agent = null;
		Integer idRefEtat = null;
		Integer idRefType = 1;

		List<DroitsAgent> das = new ArrayList<DroitsAgent>();
		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9001234);
		das.add(da);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9001235);
		das.add(da2);
		IAccessRightsRepository arRepo = Mockito.mock(AccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent)).thenReturn(das);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9001234);
		ptg.setIdPointage(1);
		ptg.setType(new RefTypePointage());
		ptg.getType().setIdRefTypePointage(1);
		ptg.setDateDebut(new Date());
		EtatPointage etat = new EtatPointage();
		etat.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat.setEtat(EtatPointageEnum.APPROUVE);
		etat.setIdAgent(9005138);
		ptg.getEtats().add(etat);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9001235);
		ptg2.setIdPointage(2);
		ptg2.setType(new RefTypePointage());
		ptg2.getType().setIdRefTypePointage(1);
		ptg2.setDateDebut(new Date());
		EtatPointage etat2 = new EtatPointage();
		etat2.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat2.setEtat(EtatPointageEnum.SAISI);
		etat2.setIdAgent(9005138);
		ptg2.getEtats().add(etat2);

		List<EtatPointageEnum> letat = null;
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(
				pService.getLatestPointagesForAgentsAndDates(Mockito.eq(Arrays.asList(9001234, 9001235)), Mockito.eq(fromDate), Mockito.eq(toDateQuery), Mockito.eq(RefTypePointageEnum.ABSENCE),
						Mockito.eq(letat), Mockito.anyString())).thenReturn(Arrays.asList(ptg, ptg2));

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);

		List<AgentGeneriqueDto> listAgents = new ArrayList<AgentGeneriqueDto>();
		Mockito.when(mRepo.getListAgents(Arrays.asList(9001234, 9001235, 9005138))).thenReturn(listAgents);

		HelperService hS = Mockito.mock(HelperService.class);
		SirhWSUtils sirhWSUtils = Mockito.mock(SirhWSUtils.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "sirhWSUtils", sirhWSUtils);

		// When
		List<ConsultPointageDto> result = service.getPointages(idAgent, fromDate, toDate, idServiceAds, agent, idRefEtat, idRefType, null);

		// Then
		assertEquals(2, result.size());
		assertEquals(1, (int) result.get(0).getIdPointage());
		assertEquals(2, (int) result.get(1).getIdPointage());
		assertFalse(result.get(0).isAffichageBoutonAccepter());
		assertFalse(result.get(0).isAffichageBoutonRefuser());
		assertFalse(result.get(1).isAffichageBoutonAccepter());
		assertFalse(result.get(1).isAffichageBoutonRefuser());
	}

	@Test
	public void getPointages_EtatFilters_ReturnFilteredPointages() {

		// Given
		Integer idAgent = 9008765;
		Date fromDate = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Date toDate = new DateTime(2013, 05, 20, 0, 0, 0).toDate();
		Date toDateQuery = new DateTime(2013, 05, 21, 0, 0, 0).toDate();
		Integer idServiceAds = null;
		Integer agent = null;
		Integer idRefType = 1;
		Integer idRefEtat = EtatPointageEnum.SAISI.getCodeEtat();

		List<DroitsAgent> das = new ArrayList<DroitsAgent>();
		Droit droit = new Droit();
		droit.setIdAgent(idAgent);
		droit.setApprobateur(true);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9001234);
		da.getDroits().add(droit);
		das.add(da);
		IAccessRightsRepository arRepo = Mockito.mock(AccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent)).thenReturn(das);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9001234);
		ptg2.setIdPointage(2);
		ptg2.setType(new RefTypePointage());
		ptg2.getType().setIdRefTypePointage(1);
		EtatPointage etat2 = new EtatPointage();
		etat2.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat2.setEtat(EtatPointageEnum.SAISI);
		etat2.setIdAgent(9005138);
		ptg2.getEtats().add(etat2);

		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(
				pService.getLatestPointagesForAgentsAndDates(Mockito.eq(Arrays.asList(9001234)), Mockito.eq(fromDate), Mockito.eq(toDateQuery), Mockito.eq(RefTypePointageEnum.ABSENCE),
						Mockito.eq(Arrays.asList(EtatPointageEnum.SAISI)), Mockito.anyString())).thenReturn(Arrays.asList(ptg2));

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);
		List<AgentGeneriqueDto> listAgents = new ArrayList<AgentGeneriqueDto>();
		Mockito.when(mRepo.getListAgents(Arrays.asList(9001234, 9005138))).thenReturn(listAgents);

		HelperService hS = Mockito.mock(HelperService.class);
		SirhWSUtils sirhWSUtils = Mockito.mock(SirhWSUtils.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "sirhWSUtils", sirhWSUtils);

		// When
		List<ConsultPointageDto> result = service.getPointages(idAgent, fromDate, toDate, idServiceAds, agent, idRefEtat, idRefType, null);

		// Then
		assertEquals(1, result.size());
		assertEquals(2, (int) result.get(0).getIdPointage());
		assertTrue(result.get(0).isAffichageBoutonAccepter());
		assertTrue(result.get(0).isAffichageBoutonRefuser());
	}

	@Test
	public void getPointages_AgentFilter_AgentNotInListOfAgentsForUser_ReturnEmptyList() {

		// Given
		Integer idAgent = 9008765;
		Date fromDate = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Date toDate = new DateTime(2013, 05, 20, 0, 0, 0).toDate();
		Integer idServiceAds = null;
		Integer agent = 9001235;
		Integer idRefEtat = null;
		Integer idRefType = null;

		IAccessRightsRepository arRepo = Mockito.mock(AccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent)).thenReturn(new ArrayList<DroitsAgent>());

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		// When
		List<ConsultPointageDto> result = service.getPointages(idAgent, fromDate, toDate, idServiceAds, agent, idRefEtat, idRefType, null);

		// Then
		assertEquals(0, result.size());
	}

	@Test
	public void getPointagesArchives_1PointageWithSeveralEtats() {

		// Given
		Pointage ptg1 = new Pointage();
		ptg1.setIdPointage(123);
		ptg1.setIdAgent(9005138);
		ptg1.setType(new RefTypePointage());
		ptg1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EtatPointage etat1 = new EtatPointage();
		etat1.setDateEtat(new DateTime(2013, 04, 28, 8, 10, 0).toDate());
		etat1.setPointage(ptg1);
		etat1.setEtat(EtatPointageEnum.SAISI);
		etat1.setIdAgent(9007861);

		EtatPointage etat2 = new EtatPointage();
		etat2.setDateEtat(new DateTime(2013, 04, 29, 10, 20, 0).toDate());
		etat2.setPointage(ptg1);
		etat2.setEtat(EtatPointageEnum.REFUSE);
		etat2.setIdAgent(9007860);

		EtatPointage etat3 = new EtatPointage();
		etat3.setDateEtat(new DateTime(2013, 04, 29, 10, 30, 0).toDate());
		etat3.setPointage(ptg1);
		etat3.setEtat(EtatPointageEnum.SAISI);
		etat3.setIdAgent(9007861);

		ptg1.setEtats(Arrays.asList(etat3, etat2, etat1));

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointageArchives(123)).thenReturn(Arrays.asList(ptg1));

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);
		AgentGeneriqueDto ag9007860 = new AgentGeneriqueDto();
		ag9007860.setIdAgent(9007860);
		Mockito.when(mRepo.getAgent(9007860)).thenReturn(ag9007860);
		AgentGeneriqueDto ag9007861 = new AgentGeneriqueDto();
		ag9007861.setIdAgent(9007861);
		Mockito.when(mRepo.getAgent(9007861)).thenReturn(ag9007861);
		AgentGeneriqueDto ag9005138 = new AgentGeneriqueDto();
		ag9005138.setIdAgent(9005138);
		Mockito.when(mRepo.getAgent(9005138)).thenReturn(ag9005138);

		HelperService hS = Mockito.mock(HelperService.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		List<ConsultPointageDto> result = service.getPointagesArchives(9001234, 123);

		// Then
		assertEquals(3, result.size());
		assertEquals(EtatPointageEnum.SAISI.getCodeEtat(), (int) result.get(0).getIdRefEtat());
		assertEquals(new DateTime(2013, 04, 29, 10, 30, 0).toDate(), result.get(0).getDateSaisie());
		assertEquals(9007861, (int) result.get(0).getOperateur().getIdAgent());
		assertEquals(EtatPointageEnum.REFUSE.getCodeEtat(), (int) result.get(1).getIdRefEtat());
		assertEquals(new DateTime(2013, 04, 29, 10, 20, 0).toDate(), result.get(1).getDateSaisie());
		assertEquals(9007860, (int) result.get(1).getOperateur().getIdAgent());
		assertEquals(EtatPointageEnum.SAISI.getCodeEtat(), (int) result.get(2).getIdRefEtat());
		assertEquals(new DateTime(2013, 04, 28, 8, 10, 0).toDate(), result.get(2).getDateSaisie());
		assertEquals(9007861, (int) result.get(2).getOperateur().getIdAgent());
	}

	@Test
	public void setPointagesEtat_PointageDoesNotExist_returnError() {

		// Given
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(null);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9001234)).thenReturn(new ArrayList<DroitsAgent>());

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);

		// When
		ReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals("Le pointage 123 n'existe pas.", result.getErrors().get(0));
	}

	@Test
	public void setPointagesEtat_UserDoesNotHaveRights_returnError() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9001234)).thenReturn(new ArrayList<DroitsAgent>());

		IAgentMatriculeConverterService matrService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9001234)).thenReturn(1234);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9005678)).thenReturn(5678);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matrService);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);

		// When
		ReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals("L'agent 1234 n'a pas le droit de mettre à jour le pointage 9 de l'agent 5678.", result.getErrors().get(0));
	}

	@Test
	public void setPointagesEtat_PointageIsNot_SAISI_APPROUVE_REFUSE_returnError() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.REFUSE_DEFINITIVEMENT);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005678);
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9001234)).thenReturn(Arrays.asList(da));

		IAgentMatriculeConverterService matrService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9005678)).thenReturn(5678);

		IPointageDataConsistencyRules ptgDataCosistencyRules = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(ptgDataCosistencyRules.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.any(Date.class))).thenReturn(new ReturnMessageDto());

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matrService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", ptgDataCosistencyRules);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);

		// When
		ReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(1, ptg.getEtats().size());
		assertEquals("Impossible de mettre à jour le pointage 9 de l'agent 5678 car celui-ci est à l'état REFUSE_DEFINITIVEMENT.", result.getErrors().get(0));
	}

	@Test
	public void setPointagesEtat_TargetEtatIsNotAuthorized_returnError() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005678);
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9001234)).thenReturn(Arrays.asList(da));

		IAgentMatriculeConverterService matrService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9005678)).thenReturn(5678);

		IPointageDataConsistencyRules ptgDataCosistencyRules = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(ptgDataCosistencyRules.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.any(Date.class))).thenReturn(new ReturnMessageDto());

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matrService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", ptgDataCosistencyRules);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(5);

		// When
		ReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(1, ptg.getEtats().size());
		assertEquals("Impossible de mettre à jour le pointage 9 de l'agent 5678 à l'état REJETE. Seuls APPROUVE, REFUSE ou SAISI sont acceptés.", result.getErrors().get(0));
	}

	@Test
	public void setPointagesEtat_SAISI_to_REFUSE_AddNewEtatPointage() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		ptg.setType(new RefTypePointage());
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005678);
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9001234)).thenReturn(Arrays.asList(da));

		HelperService hService = Mockito.mock(HelperService.class);
		Mockito.when(hService.getCurrentDate()).thenReturn(new Date());

		IPointageDataConsistencyRules ptgDataCosistencyRules = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(ptgDataCosistencyRules.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.any(Date.class))).thenReturn(new ReturnMessageDto());

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", ptgDataCosistencyRules);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(2);

		// When
		ReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(2, ptg.getEtats().size());
		assertEquals(EtatPointageEnum.SAISI, ptg.getEtats().get(0).getEtat());
		assertEquals(EtatPointageEnum.REFUSE, ptg.getEtats().get(1).getEtat());
	}

	@Test
	public void setPointagesEtat_APPROUVE_to_SAISI_AddNewEtatPointage() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		ptg.setType(new RefTypePointage());
		ptg.setDateLundi(new Date());
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005678);
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9001234)).thenReturn(Arrays.asList(da));

		HelperService hService = Mockito.mock(HelperService.class);
		Mockito.when(hService.getCurrentDate()).thenReturn(new Date());

		IPointageDataConsistencyRules ptgDataCosistencyRules = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.doNothing().when(ptgDataCosistencyRules).checkAllAbsences(Mockito.isA(ReturnMessageDto.class), Mockito.anyInt(), Mockito.isA(Date.class), Mockito.anyListOf(Pointage.class));

		Mockito.when(ptgDataCosistencyRules.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class))).thenReturn(new ReturnMessageDto());
		;

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", ptgDataCosistencyRules);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(0);

		// When
		ReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(2, ptg.getEtats().size());
		assertEquals(EtatPointageEnum.APPROUVE, ptg.getEtats().get(0).getEtat());
		assertEquals(EtatPointageEnum.SAISI, ptg.getEtats().get(1).getEtat());
	}

	@Test
	public void setPointagesEtat_REFUSE_to_APPROUVE_AddNewEtatPointage() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		ptg.setType(new RefTypePointage());
		ptg.setDateLundi(new Date());
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.REFUSE);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005678);
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9001234)).thenReturn(Arrays.asList(da));

		HelperService hService = Mockito.mock(HelperService.class);
		Mockito.when(hService.getCurrentDate()).thenReturn(new Date());

		IPointageDataConsistencyRules ptgDataCosistencyRules = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.doNothing().when(ptgDataCosistencyRules).checkAllAbsences(Mockito.isA(ReturnMessageDto.class), Mockito.anyInt(), Mockito.isA(Date.class), Mockito.anyListOf(Pointage.class));

		Mockito.when(ptgDataCosistencyRules.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class))).thenReturn(new ReturnMessageDto());

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", ptgDataCosistencyRules);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(1);

		// When
		ReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(2, ptg.getEtats().size());
		assertEquals(EtatPointageEnum.REFUSE, ptg.getEtats().get(0).getEtat());
		assertEquals(EtatPointageEnum.APPROUVE, ptg.getEtats().get(1).getEtat());
	}

	// evol #18224
	@Test
	public void setPointagesEtat_JOURNALISE_to_REJETE_AddNewEtatPointage() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		ptg.setType(new RefTypePointage());
		ptg.setDateLundi(new Date());
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.JOURNALISE);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005678);
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9001234)).thenReturn(Arrays.asList(da));

		HelperService hService = Mockito.mock(HelperService.class);
		Mockito.when(hService.getCurrentDate()).thenReturn(new Date());

		IPointageDataConsistencyRules ptgDataCosistencyRules = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.doNothing().when(ptgDataCosistencyRules).checkAllAbsences(Mockito.isA(ReturnMessageDto.class), Mockito.anyInt(), Mockito.isA(Date.class), Mockito.anyListOf(Pointage.class));

		Mockito.when(ptgDataCosistencyRules.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class))).thenReturn(new ReturnMessageDto());
		;

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", ptgDataCosistencyRules);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		// When
		ReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(2, ptg.getEtats().size());
		assertEquals(EtatPointageEnum.JOURNALISE, ptg.getEtats().get(0).getEtat());
		assertEquals(EtatPointageEnum.REJETE, ptg.getEtats().get(1).getEtat());
	}

	// evol #18224
	@Test
	public void setPointagesEtat_JOURNALISE_to_REJETE_plus3mois() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		ptg.setType(new RefTypePointage());
		ptg.setDateLundi(new Date());
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.JOURNALISE);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005678);
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9001234)).thenReturn(Arrays.asList(da));

		HelperService hService = Mockito.mock(HelperService.class);
		Mockito.when(hService.getCurrentDate()).thenReturn(new Date());

		IPointageDataConsistencyRules ptgDataCosistencyRules = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.doNothing().when(ptgDataCosistencyRules).checkAllAbsences(Mockito.isA(ReturnMessageDto.class), Mockito.anyInt(), Mockito.isA(Date.class), Mockito.anyListOf(Pointage.class));

		ReturnMessageDto rmd = new ReturnMessageDto();
		rmd.getErrors().add("pointage plus de 3 mois");
		Mockito.when(ptgDataCosistencyRules.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class))).thenReturn(rmd);
		;

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", ptgDataCosistencyRules);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		// When
		ReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals("pointage plus de 3 mois", result.getErrors().get(0));
		assertEquals(1, ptg.getEtats().size());
		assertEquals(EtatPointageEnum.JOURNALISE, ptg.getEtats().get(0).getEtat());
	}

	// evol #18224
	@Test
	public void setPointagesEtat_JOURNALISE_to_REFUSER_error() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		ptg.setType(new RefTypePointage());
		ptg.setDateLundi(new Date());
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.JOURNALISE);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005678);
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9001234)).thenReturn(Arrays.asList(da));

		HelperService hService = Mockito.mock(HelperService.class);
		Mockito.when(hService.getCurrentDate()).thenReturn(new Date());

		IPointageDataConsistencyRules ptgDataCosistencyRules = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.doNothing().when(ptgDataCosistencyRules).checkAllAbsences(Mockito.isA(ReturnMessageDto.class), Mockito.anyInt(), Mockito.isA(Date.class), Mockito.anyListOf(Pointage.class));

		ReturnMessageDto rmd = new ReturnMessageDto();
		Mockito.when(ptgDataCosistencyRules.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class))).thenReturn(rmd);

		IAgentMatriculeConverterService matriculeConvertor = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matriculeConvertor.tryConvertIdAgentToNomatr(9005678)).thenReturn(5678);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", ptgDataCosistencyRules);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matriculeConvertor);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(EtatPointageEnum.REFUSE.getCodeEtat());

		// When
		ReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals("Impossible de mettre à jour le pointage 9 de l'agent 5678 à l'état REFUSE. Seuls APPROUVE, REFUSE ou SAISI sont acceptés.", result.getErrors().get(0));
		assertEquals(1, ptg.getEtats().size());
		assertEquals(EtatPointageEnum.JOURNALISE, ptg.getEtats().get(0).getEtat());
	}

	// evol #18224
	@Test
	public void setPointagesEtat_APPROUVE_to_REJETE_error() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		ptg.setType(new RefTypePointage());
		ptg.setDateLundi(new Date());
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005678);
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9001234)).thenReturn(Arrays.asList(da));

		HelperService hService = Mockito.mock(HelperService.class);
		Mockito.when(hService.getCurrentDate()).thenReturn(new Date());

		IPointageDataConsistencyRules ptgDataCosistencyRules = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.doNothing().when(ptgDataCosistencyRules).checkAllAbsences(Mockito.isA(ReturnMessageDto.class), Mockito.anyInt(), Mockito.isA(Date.class), Mockito.anyListOf(Pointage.class));

		ReturnMessageDto rmd = new ReturnMessageDto();
		Mockito.when(ptgDataCosistencyRules.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class))).thenReturn(rmd);

		IAgentMatriculeConverterService matriculeConvertor = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matriculeConvertor.tryConvertIdAgentToNomatr(9005678)).thenReturn(5678);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", ptgDataCosistencyRules);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matriculeConvertor);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		// When
		ReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals("Impossible de mettre à jour le pointage 9 de l'agent 5678 à l'état REJETE. Seuls APPROUVE, REFUSE ou SAISI sont acceptés.", result.getErrors().get(0));
		assertEquals(1, ptg.getEtats().size());
		assertEquals(EtatPointageEnum.APPROUVE, ptg.getEtats().get(0).getEtat());
	}

	@Test
	public void getPointagesSIRH_EtatFilters_ReturnFilteredPointages() {

		// Given
		Date fromDate = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Date toDate = new DateTime(2013, 05, 20, 0, 0, 0).toDate();
		Date toDateQuery = new DateTime(2013, 05, 21, 0, 0, 0).toDate();
		Integer idRefType = RefTypePointageEnum.ABSENCE.getValue();
		Integer idRefEtat = EtatPointageEnum.SAISI.getCodeEtat();

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9001234);
		ptg2.setIdPointage(2);
		ptg2.setType(new RefTypePointage());
		ptg2.getType().setIdRefTypePointage(1);
		EtatPointage etat2 = new EtatPointage();
		etat2.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat2.setEtat(EtatPointageEnum.SAISI);
		etat2.setIdAgent(9005138);
		ptg2.getEtats().add(etat2);

		List<Integer> idAgents = new ArrayList<Integer>();
		idAgents.add(9001234);
		idAgents.add(9001235);

		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getLatestPointagesForAgentsAndDates(idAgents, fromDate, toDateQuery, RefTypePointageEnum.ABSENCE, Arrays.asList(EtatPointageEnum.SAISI), null)).thenReturn(
				Arrays.asList(ptg2));

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);

		List<AgentGeneriqueDto> listAgents = new ArrayList<AgentGeneriqueDto>();
		Mockito.when(mRepo.getListAgents(Arrays.asList(9001234, 9005138))).thenReturn(listAgents);

		HelperService hS = Mockito.mock(HelperService.class);

		SirhWSUtils sirhWSUtils = Mockito.mock(SirhWSUtils.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "sirhWSUtils", sirhWSUtils);

		// When
		List<ConsultPointageDto> result = service.getPointagesSIRH(fromDate, toDate, idAgents, idRefEtat, idRefType, null, null);

		// Then
		assertEquals(1, result.size());
		assertEquals(2, (int) result.get(0).getIdPointage());
	}

	@Test
	public void getPointagesSIRH_DateEtatFilters_ReturnFilteredPointages() {

		// Given
		Date fromDate = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Date toDate = new DateTime(2013, 05, 20, 0, 0, 0).toDate();
		Date toDateQuery = new DateTime(2013, 05, 21, 0, 0, 0).toDate();
		Integer idRefType = RefTypePointageEnum.ABSENCE.getValue();
		Integer idRefEtat = EtatPointageEnum.SAISI.getCodeEtat();

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9001234);
		ptg2.setIdPointage(2);
		ptg2.setType(new RefTypePointage());
		ptg2.getType().setIdRefTypePointage(1);
		EtatPointage etat2 = new EtatPointage();
		etat2.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat2.setEtat(EtatPointageEnum.SAISI);
		etat2.setIdAgent(9005138);
		ptg2.getEtats().add(etat2);

		List<Integer> idAgents = new ArrayList<Integer>();
		idAgents.add(9001234);
		idAgents.add(9001235);

		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getLatestPointagesForAgentsAndDates(idAgents, fromDate, toDateQuery, RefTypePointageEnum.ABSENCE, Arrays.asList(EtatPointageEnum.SAISI), null)).thenReturn(
				Arrays.asList(ptg2));

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);

		List<AgentGeneriqueDto> listAgents = new ArrayList<AgentGeneriqueDto>();
		Mockito.when(mRepo.getListAgents(Arrays.asList(9001234, 9005138))).thenReturn(listAgents);

		HelperService hS = Mockito.mock(HelperService.class);

		SirhWSUtils sirhWSUtils = Mockito.mock(SirhWSUtils.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "sirhWSUtils", sirhWSUtils);

		// When
		Date dateEtat = new DateTime(2013, 05, 20, 0, 0, 0).toDate();
		List<ConsultPointageDto> result = service.getPointagesSIRH(fromDate, toDate, idAgents, idRefEtat, idRefType, null, dateEtat);

		// Then
		assertEquals(1, result.size());
		assertEquals(2, (int) result.get(0).getIdPointage());
	}

	@Test
	public void getPointagesSIRH_DateEtatFilters_ReturnNoPointages() {

		// Given
		Date fromDate = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Date toDate = new DateTime(2013, 05, 20, 0, 0, 0).toDate();
		Date toDateQuery = new DateTime(2013, 05, 21, 0, 0, 0).toDate();
		Integer idRefType = RefTypePointageEnum.ABSENCE.getValue();
		Integer idRefEtat = EtatPointageEnum.SAISI.getCodeEtat();

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9001234);
		ptg2.setIdPointage(2);
		ptg2.setType(new RefTypePointage());
		ptg2.getType().setIdRefTypePointage(1);
		EtatPointage etat2 = new EtatPointage();
		etat2.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat2.setEtat(EtatPointageEnum.SAISI);
		etat2.setIdAgent(9005138);
		ptg2.getEtats().add(etat2);

		List<Integer> idAgents = new ArrayList<Integer>();
		idAgents.add(9001234);
		idAgents.add(9001235);

		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getLatestPointagesForAgentsAndDates(idAgents, fromDate, toDateQuery, RefTypePointageEnum.ABSENCE, Arrays.asList(EtatPointageEnum.SAISI), null)).thenReturn(
				Arrays.asList(ptg2));

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);

		List<AgentGeneriqueDto> listAgents = new ArrayList<AgentGeneriqueDto>();
		Mockito.when(mRepo.getListAgents(Arrays.asList(9001234, 9005138))).thenReturn(listAgents);

		HelperService hS = Mockito.mock(HelperService.class);

		SirhWSUtils sirhWSUtils = Mockito.mock(SirhWSUtils.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "sirhWSUtils", sirhWSUtils);

		// When
		Date dateEtat = new DateTime(2013, 06, 20, 0, 0, 0).toDate();
		List<ConsultPointageDto> result = service.getPointagesSIRH(fromDate, toDate, idAgents, idRefEtat, idRefType, null, dateEtat);

		// Then
		assertEquals(0, result.size());
	}

	@Test
	public void setPointagesEtatSIRH_PointageDoesNotExist_returnError() {

		// Given
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(null);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(null);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "ventilRepository", vR);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);

		// When
		ReturnMessageDto result = service.setPointagesEtatSIRH(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals("Le pointage 123 n'existe pas.", result.getErrors().get(0));
	}

	@Test
	public void setPointagesEtatSIRH_PointageIsNot_SAISI_ENATTENTE_REJETE_TO_APPROUVE_returnError() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.JOURNALISE);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(null);

		IAgentMatriculeConverterService matrService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9005678)).thenReturn(5678);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "ventilRepository", vR);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matrService);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(1);

		// When
		ReturnMessageDto result = service.setPointagesEtatSIRH(9005678, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(1, ptg.getEtats().size());
		assertEquals("Impossible de mettre à APPROUVE le pointage 9 de l'agent 5678 car celui-ci est à l'état JOURNALISE.", result.getErrors().get(0));
	}

	@Test
	public void setPointagesEtatSIRH_PointageIsNot_APPROUVE_REJETE_VENTILE_TO_ENATTENTE_returnError() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.JOURNALISE);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(null);

		IAgentMatriculeConverterService matrService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9005678)).thenReturn(5678);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "ventilRepository", vR);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matrService);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(8);

		// When
		ReturnMessageDto result = service.setPointagesEtatSIRH(9005678, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(1, ptg.getEtats().size());
		assertEquals("Impossible de mettre à EN_ATTENTE le pointage 9 de l'agent 5678 car celui-ci est à l'état JOURNALISE.", result.getErrors().get(0));
	}

	@Test
	public void setPointagesEtatSIRH_PointageIsNot_APPROUVE_ENATTENTE_VENTILE_TO_REJETE_returnError() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(null);

		IAgentMatriculeConverterService matrService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9005678)).thenReturn(5678);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "ventilRepository", vR);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matrService);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(5);

		// When
		ReturnMessageDto result = service.setPointagesEtatSIRH(9005678, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(1, ptg.getEtats().size());
		assertEquals("Impossible de mettre à REJETE le pointage 9 de l'agent 5678 car celui-ci est à l'état SAISI.", result.getErrors().get(0));
	}

	@Test
	public void setPointagesEtatSIRH_Pointage_APPROUVE_ENATTENTE_REJETE_returnError() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.JOURNALISE);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(null);

		IAgentMatriculeConverterService matrService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9005678)).thenReturn(5678);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "ventilRepository", vR);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matrService);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(9);

		// When
		ReturnMessageDto result = service.setPointagesEtatSIRH(9005678, Arrays.asList(etatDto));

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(1, ptg.getEtats().size());
		assertEquals("Impossible de mettre à jour le pointage 9 de l'agent 5678 à l'état JOURNALISE. Seuls APPROUVE, REJETE ou EN_ATTENTE sont acceptés depuis SIRH.", result.getErrors().get(0));
	}

	@Test
	public void setPointagesEtatSIRH_SAISI_to_APPROUVE_AddNewEtatPointage_WithNoVentilDate() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		ptg.setType(new RefTypePointage());
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(etat);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		HelperService hService = Mockito.mock(HelperService.class);
		Mockito.when(hService.getTypeChainePaieFromStatut(AgentStatutEnum.F)).thenReturn(TypeChainePaieEnum.SHC);
		Mockito.when(hService.getMairieMatrFromIdAgent(ptg.getIdAgent())).thenReturn(5678);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SHC, false)).thenReturn(null);

		IPointageDataConsistencyRules ptgDataCosistencyRules = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.doNothing().when(ptgDataCosistencyRules).checkAllAbsences(Mockito.isA(ReturnMessageDto.class), Mockito.anyInt(), Mockito.isA(Date.class), Mockito.anyListOf(Pointage.class));

		Spcarr spCarr = new Spcarr();

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(5678, ptg.getDateDebut())).thenReturn(spCarr);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);
		ReflectionTestUtils.setField(service, "ventilRepository", vR);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", ptgDataCosistencyRules);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(1);

		// When
		ReturnMessageDto result = service.setPointagesEtatSIRH(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(2, ptg.getEtats().size());
		assertEquals(EtatPointageEnum.SAISI, ptg.getEtats().get(0).getEtat());
		assertEquals(EtatPointageEnum.APPROUVE, ptg.getEtats().get(1).getEtat());
	}

	@Test
	public void setPointagesEtatSIRH_SAISI_to_APPROUVE_AddNewEtatPointage_WithVentilDate() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
		ptg.setType(new RefTypePointage());
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(etat);

		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new Date());

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, 123)).thenReturn(ptg);

		HelperService hService = Mockito.mock(HelperService.class);
		Mockito.when(hService.getTypeChainePaieFromStatut(AgentStatutEnum.F)).thenReturn(TypeChainePaieEnum.SHC);
		Mockito.when(hService.getMairieMatrFromIdAgent(ptg.getIdAgent())).thenReturn(5678);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SHC, false)).thenReturn(ventilDate);

		IPointageDataConsistencyRules ptgDataCosistencyRules = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.doNothing().when(ptgDataCosistencyRules).checkAllAbsences(Mockito.isA(ReturnMessageDto.class), Mockito.anyInt(), Mockito.isA(Date.class), Mockito.anyListOf(Pointage.class));

		Spcarr spCarr = new Spcarr();

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(5678, ptg.getDateDebut())).thenReturn(spCarr);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);
		ReflectionTestUtils.setField(service, "ventilRepository", vR);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", ptgDataCosistencyRules);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(1);

		// When
		ReturnMessageDto result = service.setPointagesEtatSIRH(9001234, Arrays.asList(etatDto));

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(2, ptg.getEtats().size());
		assertEquals(EtatPointageEnum.SAISI, ptg.getEtats().get(0).getEtat());
		assertEquals(EtatPointageEnum.APPROUVE, ptg.getEtats().get(1).getEtat());
	}

	@Test
	public void listerTousAgentsPointages_ReturnEmptyListAgentDto() {
		// Given
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.listAllDistinctIdAgentPointage()).thenReturn(new ArrayList<Integer>());

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		// When
		List<AgentDto> result = service.listerTousAgentsPointages();

		// Then
		assertEquals(0, result.size());
	}

	@Test
	public void listerTousAgentsPointages_ReturnListAgentDto() {
		// Given
		ArrayList<Integer> listAg = new ArrayList<Integer>();
		listAg.add(9005138);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.listAllDistinctIdAgentPointage()).thenReturn(listAg);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		// When
		List<AgentDto> result = service.listerTousAgentsPointages();

		// Then
		assertEquals(1, result.size());
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilation_badEtat() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();

		EtatPointageEnum currentEtat = EtatPointageEnum.APPROUVE;

		Pointage pointage = Mockito.spy(new Pointage());
		pointage.setEtats(new ArrayList<EtatPointage>());

		Date dateVentilation = new Date();

		ApprobationService service = new ApprobationService();

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteRejet(9005138, dto, currentEtat, new Pointage(), dateVentilation);

		Mockito.verify(pointage, Mockito.times(0)).getEtats();
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilation_noOtherPointage() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.VALIDE;

		RefTypePointage typePointageAbs = new RefTypePointage();
		typePointageAbs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointageAbs);

		Pointage pointage = Mockito.spy(new Pointage());
		pointage.setEtats(new ArrayList<EtatPointage>());

		Date dateVentilation = new Date();

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteRejet(9005138, dto, currentEtat, ptg, dateVentilation);

		Mockito.verify(pointage, Mockito.times(0)).getEtats();
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilation_2OtherPointage_HSup_Abs() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.VALIDE;

		RefTypePointage typePointageHSup = new RefTypePointage();
		typePointageHSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointageHSup);

		Date dateVentilation = new Date();

		RefTypePointage typePointageAbs = new RefTypePointage();
		typePointageAbs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.VALIDE);

		Pointage otherPointageHSup = Mockito.spy(new Pointage());
		otherPointageHSup.setIdPointage(2);
		otherPointageHSup.setType(typePointageHSup);
		otherPointageHSup.getEtats().add(etat);

		Pointage otherPointageAbs = Mockito.spy(new Pointage());
		otherPointageAbs.setIdPointage(3);
		otherPointageAbs.setType(typePointageAbs);
		otherPointageAbs.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointageHSup);
		listePointagesAgent.add(otherPointageAbs);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteRejet(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(2, otherPointageHSup.getEtats().size());
		assertEquals(2, otherPointageAbs.getEtats().size());
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilation_1OtherPointage_Prime() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.VALIDE;

		RefTypePointage typePointage = new RefTypePointage();
		typePointage.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointage);

		Date dateVentilation = new Date();

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.VALIDE);

		Pointage otherPointage = Mockito.spy(new Pointage());
		otherPointage.setIdPointage(2);
		otherPointage.setType(typePointage);
		otherPointage.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointage);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteRejet(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(1, otherPointage.getEtats().size());
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilation_2OtherPointage_HSup_Abs_badEtat() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.VALIDE;

		RefTypePointage typePointageHSup = new RefTypePointage();
		typePointageHSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointageHSup);

		Date dateVentilation = new Date();

		RefTypePointage typePointageAbs = new RefTypePointage();
		typePointageAbs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.VENTILE);

		Pointage otherPointageHSup = Mockito.spy(new Pointage());
		otherPointageHSup.setIdPointage(2);
		otherPointageHSup.setType(typePointageHSup);
		otherPointageHSup.getEtats().add(etat);

		Pointage otherPointageAbs = Mockito.spy(new Pointage());
		otherPointageAbs.setIdPointage(3);
		otherPointageAbs.setType(typePointageAbs);
		otherPointageAbs.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointageHSup);
		listePointagesAgent.add(otherPointageAbs);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteRejet(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(1, otherPointageHSup.getEtats().size());
		assertEquals(1, otherPointageAbs.getEtats().size());
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilation_2OtherPointage_HSup_Abs_sameIdPointage() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.VALIDE;

		RefTypePointage typePointageHSup = new RefTypePointage();
		typePointageHSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointageHSup);

		Date dateVentilation = new Date();

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.VALIDE);

		Pointage otherPointageHSup = Mockito.spy(new Pointage());
		otherPointageHSup.setIdPointage(1);
		otherPointageHSup.setType(typePointageHSup);
		otherPointageHSup.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointageHSup);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteRejet(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(1, otherPointageHSup.getEtats().size());
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation_badEtat() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();

		EtatPointageEnum currentEtat = EtatPointageEnum.REJETE;

		Pointage pointage = Mockito.spy(new Pointage());
		pointage.setEtats(new ArrayList<EtatPointage>());

		Date dateVentilation = new Date();

		ApprobationService service = new ApprobationService();

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation(9005138, dto, currentEtat, new Pointage(), dateVentilation);

		Mockito.verify(pointage, Mockito.times(0)).getEtats();
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation_noOtherPointage() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.APPROUVE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.REJETE;

		RefTypePointage typePointageHSup = new RefTypePointage();
		typePointageHSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointageHSup);

		Pointage pointage = Mockito.spy(new Pointage());
		pointage.setEtats(new ArrayList<EtatPointage>());

		Date dateVentilation = new Date();

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation(9005138, dto, currentEtat, ptg, dateVentilation);

		Mockito.verify(pointage, Mockito.times(0)).getEtats();
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation_2OtherPointage_HSup_Abs() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.APPROUVE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.REJETE;

		RefTypePointage typePointageHSup = new RefTypePointage();
		typePointageHSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointageHSup);

		Date dateVentilation = new Date();

		RefTypePointage typePointageAbs = new RefTypePointage();
		typePointageAbs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.VALIDE);

		Pointage otherPointageHSup = Mockito.spy(new Pointage());
		otherPointageHSup.setIdPointage(2);
		otherPointageHSup.setType(typePointageHSup);
		otherPointageHSup.getEtats().add(etat);

		Pointage otherPointageAbs = Mockito.spy(new Pointage());
		otherPointageAbs.setIdPointage(3);
		otherPointageAbs.setType(typePointageAbs);
		otherPointageAbs.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointageHSup);
		listePointagesAgent.add(otherPointageAbs);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(2, otherPointageHSup.getEtats().size());
		assertEquals(2, otherPointageAbs.getEtats().size());
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation_1OtherPointage_Prime() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.APPROUVE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.REJETE;

		RefTypePointage typePointage = new RefTypePointage();
		typePointage.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointage);

		Date dateVentilation = new Date();

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.VALIDE);

		Pointage otherPointage = Mockito.spy(new Pointage());
		otherPointage.setIdPointage(2);
		otherPointage.setType(typePointage);
		otherPointage.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointage);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(1, otherPointage.getEtats().size());
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation_2OtherPointage_HSup_Abs_badEtat() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.APPROUVE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.REJETE;

		RefTypePointage typePointageHSup = new RefTypePointage();
		typePointageHSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointageHSup);

		Date dateVentilation = new Date();

		RefTypePointage typePointageAbs = new RefTypePointage();
		typePointageAbs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.VENTILE);

		Pointage otherPointageHSup = Mockito.spy(new Pointage());
		otherPointageHSup.setIdPointage(2);
		otherPointageHSup.setType(typePointageHSup);
		otherPointageHSup.getEtats().add(etat);

		Pointage otherPointageAbs = Mockito.spy(new Pointage());
		otherPointageAbs.setIdPointage(3);
		otherPointageAbs.setType(typePointageAbs);
		otherPointageAbs.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointageHSup);
		listePointagesAgent.add(otherPointageAbs);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(1, otherPointageHSup.getEtats().size());
		assertEquals(1, otherPointageAbs.getEtats().size());
	}

	@Test
	public void reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation_2OtherPointage_HSup_Abs_sameIdPointage() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.APPROUVE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.REJETE;

		RefTypePointage typePointageHSup = new RefTypePointage();
		typePointageHSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointageHSup);

		Date dateVentilation = new Date();

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.VALIDE);

		Pointage otherPointageHSup = Mockito.spy(new Pointage());
		otherPointageHSup.setIdPointage(1);
		otherPointageHSup.setType(typePointageHSup);
		otherPointageHSup.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointageHSup);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointageHSupEtAbsAApprouveForVentilationSuiteApprobation(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(1, otherPointageHSup.getEtats().size());
	}

	@Test
	public void reinitialisePointagePrimeAApprouveForVentilationSuiteRejet_badEtat() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.APPROUVE;

		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		Pointage pointage = Mockito.spy(new Pointage());
		pointage.setEtats(new ArrayList<EtatPointage>());
		pointage.setType(typePointagePrime);

		Date dateVentilation = new Date();

		ApprobationService service = new ApprobationService();

		service.reinitialisePointagePrimeAApprouveForVentilationSuiteRejet(9005138, dto, currentEtat, new Pointage(), dateVentilation);

		Mockito.verify(pointage, Mockito.times(0)).getEtats();
	}

	@Test
	public void reinitialisePointagePrimeAApprouveForVentilationSuiteRejet_noOtherPointage() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.VALIDE;

		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointagePrime);
		ptg.setRefPrime(refPrime);

		Pointage pointage = Mockito.spy(new Pointage());
		pointage.setEtats(new ArrayList<EtatPointage>());

		Date dateVentilation = new Date();

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();

		IVentilationRepository ventilationRepository = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilationRepository.getListPointagesPrimeValideByMoisAndRefPrime(ptg.getIdAgent(), ptg.getDateDebut(), ptg.getRefPrime().getIdRefPrime())).thenReturn(listePointagesAgent);

		IPointageService pointageService = Mockito.mock(IPointageService.class);
		Mockito.when(pointageService.filterOldPointagesAndEtatFromList(listePointagesAgent, Arrays.asList(EtatPointageEnum.VALIDE), null)).thenReturn(listePointagesAgent);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilationRepository);
		ReflectionTestUtils.setField(service, "pointageService", pointageService);

		service.reinitialisePointagePrimeAApprouveForVentilationSuiteRejet(ptg.getIdAgent(), dto, currentEtat, ptg, dateVentilation);

		Mockito.verify(pointage, Mockito.times(0)).getEtats();
	}

	@Test
	public void reinitialisePointagePrimeAApprouveForVentilationSuiteRejet_2OtherPointage_HSup_Abs() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.VALIDE;

		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointagePrime);
		ptg.setRefPrime(refPrime);

		Date dateVentilation = new Date();

		RefTypePointage typePointageAbs = new RefTypePointage();
		typePointageAbs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		RefTypePointage typePointageHSup = new RefTypePointage();
		typePointageHSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.VALIDE);

		Pointage otherPointageHSup = Mockito.spy(new Pointage());
		otherPointageHSup.setIdPointage(2);
		otherPointageHSup.setType(typePointageHSup);
		otherPointageHSup.getEtats().add(etat);

		Pointage otherPointageAbs = Mockito.spy(new Pointage());
		otherPointageAbs.setIdPointage(3);
		otherPointageAbs.setType(typePointageAbs);
		otherPointageAbs.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointageHSup);
		listePointagesAgent.add(otherPointageAbs);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		IVentilationRepository ventilationRepository = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilationRepository.getListPointagesPrimeValideByMoisAndRefPrime(ptg.getIdAgent(), ptg.getDateDebut(), ptg.getRefPrime().getIdRefPrime())).thenReturn(listePointagesAgent);

		IPointageService pointageService = Mockito.mock(IPointageService.class);
		Mockito.when(pointageService.filterOldPointagesAndEtatFromList(listePointagesAgent, Arrays.asList(EtatPointageEnum.VALIDE), null)).thenReturn(listePointagesAgent);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilationRepository);
		ReflectionTestUtils.setField(service, "pointageService", pointageService);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointagePrimeAApprouveForVentilationSuiteRejet(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(1, otherPointageHSup.getEtats().size());
		assertEquals(1, otherPointageAbs.getEtats().size());
	}

	@Test
	public void reinitialisePointagePrimeAApprouveForVentilationSuiteRejet_1OtherPointage_Prime() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.VALIDE;

		RefTypePointage typePointage = new RefTypePointage();
		typePointage.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointage);
		ptg.setRefPrime(refPrime);

		Date dateVentilation = new Date();

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.VALIDE);

		Pointage otherPointage = Mockito.spy(new Pointage());
		otherPointage.setIdPointage(2);
		otherPointage.setType(typePointage);
		otherPointage.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointage);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		IVentilationRepository ventilationRepository = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilationRepository.getListPointagesPrimeValideByMoisAndRefPrime(ptg.getIdAgent(), ptg.getDateDebut(), ptg.getRefPrime().getIdRefPrime())).thenReturn(listePointagesAgent);

		IPointageService pointageService = Mockito.mock(IPointageService.class);
		Mockito.when(pointageService.filterOldPointagesAndEtatFromList(listePointagesAgent, Arrays.asList(EtatPointageEnum.VALIDE, EtatPointageEnum.JOURNALISE), null)).thenReturn(listePointagesAgent);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilationRepository);
		ReflectionTestUtils.setField(service, "pointageService", pointageService);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointagePrimeAApprouveForVentilationSuiteRejet(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(2, otherPointage.getEtats().size());
	}

	@Test
	public void reinitialisePointagePrimeAApprouveForVentilationSuiteRejet_2OtherPointage_HSup_Abs_badEtat() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.VALIDE;

		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointagePrime);
		ptg.setRefPrime(refPrime);

		Date dateVentilation = new Date();

		EtatPointage etatVentile = new EtatPointage();
		etatVentile.setEtat(EtatPointageEnum.VENTILE);

		EtatPointage etatJournalise = new EtatPointage();
		etatJournalise.setEtat(EtatPointageEnum.JOURNALISE);

		Pointage otherPointagePrimeVentile = Mockito.spy(new Pointage());
		otherPointagePrimeVentile.setIdPointage(2);
		otherPointagePrimeVentile.setType(typePointagePrime);
		otherPointagePrimeVentile.getEtats().add(etatVentile);

		Pointage otherPointagePrimeJournalise = Mockito.spy(new Pointage());
		otherPointagePrimeJournalise.setIdPointage(3);
		otherPointagePrimeJournalise.setType(typePointagePrime);
		otherPointagePrimeJournalise.getEtats().add(etatJournalise);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointagePrimeVentile);
		listePointagesAgent.add(otherPointagePrimeJournalise);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		IVentilationRepository ventilationRepository = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilationRepository.getListPointagesPrimeValideByMoisAndRefPrime(ptg.getIdAgent(), ptg.getDateDebut(), ptg.getRefPrime().getIdRefPrime())).thenReturn(listePointagesAgent);

		IPointageService pointageService = Mockito.mock(IPointageService.class);
		Mockito.when(pointageService.filterOldPointagesAndEtatFromList(listePointagesAgent, Arrays.asList(EtatPointageEnum.VALIDE), null)).thenReturn(listePointagesAgent);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilationRepository);
		ReflectionTestUtils.setField(service, "pointageService", pointageService);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointagePrimeAApprouveForVentilationSuiteRejet(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(1, otherPointagePrimeVentile.getEtats().size());
		assertEquals(1, otherPointagePrimeJournalise.getEtats().size());
	}

	@Test
	public void reinitialisePointagePrimeAApprouveForVentilationSuiteRejet_1OtherPointage_Prime_sameIdPointage() {

		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());

		EtatPointageEnum currentEtat = EtatPointageEnum.VALIDE;

		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointagePrime);
		ptg.setRefPrime(refPrime);

		Date dateVentilation = new Date();

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.VALIDE);

		Pointage otherPointage = Mockito.spy(new Pointage());
		otherPointage.setIdPointage(1);
		otherPointage.setType(typePointagePrime);
		otherPointage.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointage);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);

		HelperService hS = Mockito.mock(HelperService.class);

		IVentilationRepository ventilationRepository = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilationRepository.getListPointagesPrimeValideByMoisAndRefPrime(ptg.getIdAgent(), ptg.getDateDebut(), ptg.getRefPrime().getIdRefPrime())).thenReturn(listePointagesAgent);

		IPointageService pointageService = Mockito.mock(IPointageService.class);
		Mockito.when(pointageService.filterOldPointagesAndEtatFromList(listePointagesAgent, Arrays.asList(EtatPointageEnum.VALIDE), null)).thenReturn(listePointagesAgent);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilationRepository);
		ReflectionTestUtils.setField(service, "pointageService", pointageService);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		service.reinitialisePointagePrimeAApprouveForVentilationSuiteRejet(9005138, dto, currentEtat, ptg, dateVentilation);

		assertEquals(1, otherPointage.getEtats().size());
	}
	
	@Test
	public void reinitialisePointageHSupEtAbsEtPrime7717_AApprouveForVentilationSuiteRejetOuApprobation() {
		
		Integer idAgent = 9005138;
		Date dateEtat = new Date();
		
		PointagesEtatChangeDto dto = new PointagesEtatChangeDto();
		dto.setIdRefEtat(EtatPointageEnum.REJETE.getCodeEtat());
		EtatPointageEnum currentEtat = EtatPointageEnum.VALIDE;
		
		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		
		RefTypePointage typePointageAbs = new RefTypePointage();
		typePointageAbs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		RefTypePointage typePointageHsup = new RefTypePointage();
		typePointageHsup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		RefPrime refPrime7700 = new RefPrime();
		refPrime7700.setIdRefPrime(1);
		refPrime7700.setNoRubr(7700);

		RefPrime refPrime7717 = new RefPrime();
		refPrime7717.setIdRefPrime(1);
		refPrime7717.setNoRubr(7717);

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.JOURNALISE);
		
		Pointage ptg = new Pointage();
		ptg.setIdPointage(10);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointagePrime);
		ptg.setRefPrime(refPrime7700);
		ptg.getEtats().add(etat);
		
		
		Pointage otherPointagePrime7717 = Mockito.spy(new Pointage());
		otherPointagePrime7717.setIdPointage(1);
		otherPointagePrime7717.setType(typePointagePrime);
		otherPointagePrime7717.setRefPrime(refPrime7717);
		otherPointagePrime7717.getEtats().add(etat);
		
		Pointage otherPointagePrimeNot7717 = Mockito.spy(new Pointage());
		otherPointagePrimeNot7717.setIdPointage(2);
		otherPointagePrimeNot7717.setType(typePointagePrime);
		otherPointagePrimeNot7717.setRefPrime(refPrime7700);
		otherPointagePrimeNot7717.getEtats().add(etat);
		
		Pointage otherPointageAbs = Mockito.spy(new Pointage());
		otherPointageAbs.setIdPointage(3);
		otherPointageAbs.setType(typePointageAbs);
		otherPointageAbs.getEtats().add(etat);
		
		Pointage otherPointageHSup = Mockito.spy(new Pointage());
		otherPointageHSup.setIdPointage(4);
		otherPointageHSup.setType(typePointageHsup);
		otherPointageHSup.getEtats().add(etat);

		List<Pointage> listePointagesAgent = new ArrayList<Pointage>();
		listePointagesAgent.add(otherPointagePrime7717);
		listePointagesAgent.add(otherPointagePrimeNot7717);
		listePointagesAgent.add(otherPointageAbs);
		listePointagesAgent.add(otherPointageHSup);
		listePointagesAgent.add(ptg);

		HelperService hS = Mockito.mock(HelperService.class);
		
		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getPointagesForAgentAndDateOrderByIdDesc(ptg.getIdAgent(), ptg.getDateLundi())).thenReturn(listePointagesAgent);
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// la prime n est pas un Renfort de Garde 7717
		service.reinitialisePointageHSupEtAbsEtPrime7717_AApprouveForVentilationSuiteRejetOuApprobation(idAgent, dto, currentEtat, ptg, dateEtat);
		assertEquals(1, otherPointageAbs.getEtats().size());
		assertEquals(1, otherPointageHSup.getEtats().size());
		assertEquals(1, otherPointagePrime7717.getEtats().size());
		assertEquals(1, otherPointagePrimeNot7717.getEtats().size());
		assertEquals(1, ptg.getEtats().size());
		

		// la prime est un Renfort de Garde 7717
		ptg.setRefPrime(refPrime7717);
		service.reinitialisePointageHSupEtAbsEtPrime7717_AApprouveForVentilationSuiteRejetOuApprobation(idAgent, dto, currentEtat, ptg, dateEtat);
		assertEquals(2, otherPointageAbs.getEtats().size());
		assertEquals(EtatPointageEnum.APPROUVE, otherPointageAbs.getEtats().get(1).getEtat());
		assertEquals(2, otherPointageHSup.getEtats().size());
		assertEquals(EtatPointageEnum.APPROUVE, otherPointageHSup.getEtats().get(1).getEtat());
		assertEquals(2, otherPointagePrime7717.getEtats().size());
		assertEquals(EtatPointageEnum.APPROUVE, otherPointagePrime7717.getEtats().get(1).getEtat());
		assertEquals(1, otherPointagePrimeNot7717.getEtats().size());
		assertEquals(EtatPointageEnum.JOURNALISE, otherPointagePrimeNot7717.getEtats().get(0).getEtat());
		// meme pointage
		assertEquals(1, ptg.getEtats().size());
	}

	@Test
	public void checkDroitApprobationByPointage() {

		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointagePrime);
		ptg.setRefPrime(refPrime);

		Droit droit = new Droit();
		droit.setIdAgent(9005138);

		List<DroitsAgent> listdroitsAgent = new ArrayList<DroitsAgent>();
		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9001234);
		listdroitsAgent.add(da);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9001235);
		listdroitsAgent.add(da2);

		ConsultPointageDto dto = new ConsultPointageDto();

		ApprobationService service = new ApprobationService();
		boolean result = service.checkDroitApprobationByPointage(ptg, dto, listdroitsAgent, 9005138);

		assertFalse(result);
	}

	@Test
	public void checkDroitApprobationByPointage_DroitApprobationDejaATrue() {

		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointagePrime);
		ptg.setRefPrime(refPrime);

		Droit droit = new Droit();
		droit.setIdAgent(9005138);

		List<DroitsAgent> listdroitsAgent = new ArrayList<DroitsAgent>();
		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9001234);
		listdroitsAgent.add(da);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9001235);
		listdroitsAgent.add(da2);

		ConsultPointageDto dto = new ConsultPointageDto();
		dto.setApprobation(true);

		ApprobationService service = new ApprobationService();
		boolean result = service.checkDroitApprobationByPointage(ptg, dto, listdroitsAgent, 9005138);

		assertTrue(result);
	}

	@Test
	public void checkDroitApprobationByPointage_true_approbateur() {

		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointagePrime);
		ptg.setRefPrime(refPrime);
		ptg.getEtats().add(etat);

		Droit droit = new Droit();
		droit.setIdAgent(9005140);
		droit.setApprobateur(true);

		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);

		List<DroitsAgent> listdroitsAgent = new ArrayList<DroitsAgent>();
		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005138);
		da.setDroits(droits);
		listdroitsAgent.add(da);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9001235);
		listdroitsAgent.add(da2);

		ConsultPointageDto dto = new ConsultPointageDto();

		ApprobationService service = new ApprobationService();
		boolean result = service.checkDroitApprobationByPointage(ptg, dto, listdroitsAgent, 9005140);

		assertTrue(result);
	}

	@Test
	public void checkDroitApprobationByPointage_false_approbateurButBadEtat() {

		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointagePrime);
		ptg.setRefPrime(refPrime);

		Droit droit = new Droit();
		droit.setIdAgent(9005140);
		droit.setApprobateur(true);

		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);

		List<DroitsAgent> listdroitsAgent = new ArrayList<DroitsAgent>();
		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005138);
		da.setDroits(droits);
		listdroitsAgent.add(da);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9001235);
		listdroitsAgent.add(da2);

		ConsultPointageDto dto = new ConsultPointageDto();

		ApprobationService service = new ApprobationService();

		for (int i = 0; i < 10; i++) {

			EtatPointage etat = new EtatPointage();
			etat.setEtat(EtatPointageEnum.getEtatPointageEnum(i));
			ptg.getEtats().clear();
			ptg.getEtats().add(etat);

			boolean result = service.checkDroitApprobationByPointage(ptg, dto, listdroitsAgent, 9005140);

			if (i == EtatPointageEnum.REFUSE_DEFINITIVEMENT.getCodeEtat() || i == EtatPointageEnum.REJETE_DEFINITIVEMENT.getCodeEtat() || i == EtatPointageEnum.EN_ATTENTE.getCodeEtat()
					|| i == EtatPointageEnum.VALIDE.getCodeEtat() || i == EtatPointageEnum.VENTILE.getCodeEtat()) {
				assertFalse(result);
			} else {
				assertTrue(result);
			}
		}
	}

	@Test
	public void checkDroitApprobationByPointage_true_delegataire() {

		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointagePrime);
		ptg.setRefPrime(refPrime);
		ptg.getEtats().add(etat);

		Droit droit = new Droit();
		droit.setIdAgent(9001111);
		droit.setIdAgentDelegataire(9005140);
		droit.setApprobateur(true);

		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);

		List<DroitsAgent> listdroitsAgent = new ArrayList<DroitsAgent>();
		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005138);
		da.setDroits(droits);
		listdroitsAgent.add(da);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9001235);
		listdroitsAgent.add(da2);

		ConsultPointageDto dto = new ConsultPointageDto();

		ApprobationService service = new ApprobationService();
		boolean result = service.checkDroitApprobationByPointage(ptg, dto, listdroitsAgent, 9005140);

		assertTrue(result);
	}

	@Test
	public void checkDroitApprobationByPointage_false_operateur() {

		RefTypePointage typePointagePrime = new RefTypePointage();
		typePointagePrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);

		Pointage ptg = new Pointage();
		ptg.setIdPointage(1);
		ptg.setIdAgent(9005138);
		ptg.setDateLundi(new Date());
		ptg.setType(typePointagePrime);
		ptg.setRefPrime(refPrime);

		Droit droit = new Droit();
		droit.setIdAgent(9005140);
		droit.setApprobateur(false);

		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);

		List<DroitsAgent> listdroitsAgent = new ArrayList<DroitsAgent>();
		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005138);
		da.setDroits(droits);
		listdroitsAgent.add(da);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9001235);
		listdroitsAgent.add(da2);

		ConsultPointageDto dto = new ConsultPointageDto();

		ApprobationService service = new ApprobationService();
		boolean result = service.checkDroitApprobationByPointage(ptg, dto, listdroitsAgent, 9005140);

		assertFalse(result);
	}

	@Test
	public void addRecuperationProvisoireToAgent_addRecup_toApprove() {

		EtatPointageEnum targetEtat = EtatPointageEnum.APPROUVE;

		RefTypePointage typePointage = new RefTypePointage();
		typePointage.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005138);
		ptg.setIdPointage(1);
		ptg.setDateDebut(new Date());
		ptg.setDateFin(new Date());
		ptg.getEtats().add(etat);
		ptg.setType(typePointage);
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(false);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDureeBetweenDateDebutAndDateFin(ptg.getDateDebut(), ptg.getDateFin())).thenReturn(10);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		IDpmService dpmService = Mockito.mock(IDpmService.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "dpmService", dpmService);

		service.addRecuperationToAgent(targetEtat, ptg);

		Mockito.verify(absWsConsumer, Mockito.times(1)).addRecuperationsToCompteurAgentForOnePointage(ptg.getIdAgent(), ptg.getDateDebut(), 10, ptg.getIdPointage(), null);
	}

	@Test
	public void addRecuperationProvisoireToAgent_substractRecup() {

		EtatPointageEnum targetEtat = EtatPointageEnum.REFUSE;

		RefTypePointage typePointage = new RefTypePointage();
		typePointage.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005138);
		ptg.setIdPointage(1);
		ptg.setDateDebut(new Date());
		ptg.setDateFin(new Date());
		ptg.setType(typePointage);
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(false);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDureeBetweenDateDebutAndDateFin(ptg.getDateDebut(), ptg.getDateFin())).thenReturn(10);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		IDpmService dpmService = Mockito.mock(IDpmService.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "dpmService", dpmService);

		for (int i = 0; i < 10; i++) {
			EtatPointage etat = new EtatPointage();
			etat.setEtat(EtatPointageEnum.getEtatPointageEnum(i));
			ptg.getEtats().clear();
			ptg.getEtats().add(etat);

			Mockito.reset(absWsConsumer);
			service.addRecuperationToAgent(targetEtat, ptg);

			if (i == 1 || i == 4 || i == 7 || i == 9) {
				Mockito.verify(absWsConsumer, Mockito.times(1)).addRecuperationsToCompteurAgentForOnePointage(ptg.getIdAgent(), ptg.getDateDebut(), 0, ptg.getIdPointage(), null);
			} else {
				Mockito.verify(absWsConsumer, Mockito.never()).addRecuperationsToCompteurAgentForOnePointage(ptg.getIdAgent(), ptg.getDateDebut(), 0, ptg.getIdPointage(), null);
			}
		}
	}

	@Test
	public void addRecuperationProvisoireToAgent_badType() {

		EtatPointageEnum targetEtat = EtatPointageEnum.APPROUVE;

		RefTypePointage typePointage = new RefTypePointage();
		typePointage.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005138);
		ptg.setIdPointage(1);
		ptg.setDateDebut(new Date());
		ptg.setDateFin(new Date());
		ptg.getEtats().add(etat);
		ptg.setType(typePointage);
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(false);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDureeBetweenDateDebutAndDateFin(ptg.getDateDebut(), ptg.getDateFin())).thenReturn(10);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		service.addRecuperationToAgent(targetEtat, ptg);

		Mockito.verify(absWsConsumer, Mockito.never()).addRecuperationsToCompteurAgentForOnePointage(Mockito.anyInt(), Mockito.any(Date.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
	}

	@Test
	public void addRecuperationProvisoireToAgent_isNotHSupRecupere() {

		EtatPointageEnum targetEtat = EtatPointageEnum.APPROUVE;

		RefTypePointage typePointage = new RefTypePointage();
		typePointage.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005138);
		ptg.setIdPointage(1);
		ptg.setDateDebut(new Date());
		ptg.setDateFin(new Date());
		ptg.getEtats().add(etat);
		ptg.setType(typePointage);
		ptg.setHeureSupRecuperee(false);
		ptg.setHeureSupRappelService(false);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDureeBetweenDateDebutAndDateFin(ptg.getDateDebut(), ptg.getDateFin())).thenReturn(10);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		service.addRecuperationToAgent(targetEtat, ptg);

		Mockito.verify(absWsConsumer, Mockito.never()).addRecuperationsToCompteurAgentForOnePointage(Mockito.anyInt(), Mockito.any(Date.class), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
	}

	/**
	 * le calcul de rappel en service est deplace dans dpmService.calculNombreMinutesRecupereesMajoreesToAgentForOnePointage()
	 */
	@Test
	public void addRecuperationProvisoireToAgent_isHSupRappelService() {

		EtatPointageEnum targetEtat = EtatPointageEnum.APPROUVE;

		RefTypePointage typePointage = new RefTypePointage();
		typePointage.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005138);
		ptg.setIdPointage(1);
		ptg.setDateDebut(new DateTime(2015, 8, 7, 8, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 8, 7, 10, 0, 0).toDate());
		ptg.getEtats().add(etat);
		ptg.setType(typePointage);
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(true);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDureeBetweenDateDebutAndDateFin(ptg.getDateDebut(), ptg.getDateFin())).thenReturn(120);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		IDpmService dpmService = Mockito.mock(IDpmService.class);
		Mockito.when(dpmService.calculNombreMinutesRecupereesMajoreesToAgentForOnePointage(ptg)).thenReturn(120);

		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "dpmService", dpmService);

		service.addRecuperationToAgent(targetEtat, ptg);

		Mockito.verify(absWsConsumer, Mockito.times(1)).addRecuperationsToCompteurAgentForOnePointage(ptg.getIdAgent(), ptg.getDateDebut(), 240, ptg.getIdPointage(), null);
	}
	
	/**
	 * le calcul de rappel en service est deplace dans dpmService.calculNombreMinutesRecupereesMajoreesToAgentForOnePointage()
	 */
	@Test
	public void addRecuperationProvisoireToAgent_isHSupRappelService_PrimeDPM() {

		EtatPointageEnum targetEtat = EtatPointageEnum.APPROUVE;

		RefTypePointage typePointage = new RefTypePointage();
		typePointage.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.SAISI);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005138);
		ptg.setIdPointage(1);
		ptg.setDateDebut(new DateTime(2015, 8, 7, 8, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 8, 7, 10, 0, 0).toDate());
		ptg.getEtats().add(etat);
		ptg.setType(typePointage);
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(true);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getDureeBetweenDateDebutAndDateFin(ptg.getDateDebut(), ptg.getDateFin())).thenReturn(90);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		
		IDpmService dpmService = Mockito.mock(IDpmService.class);
		Mockito.when(dpmService.calculNombreMinutesRecupereesMajoreesToAgentForOnePointage(ptg)).thenReturn(120);
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "dpmService", dpmService);

		service.addRecuperationToAgent(targetEtat, ptg);

		Mockito.verify(absWsConsumer, Mockito.times(1)).addRecuperationsToCompteurAgentForOnePointage(
				ptg.getIdAgent(), ptg.getDateDebut(), 210, ptg.getIdPointage(), null);
	}

}
