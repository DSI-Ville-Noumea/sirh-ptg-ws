package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPointagePK;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.ConsultPointageDto;
import nc.noumea.mairie.ptg.dto.PointagesEtatChangeDto;
import nc.noumea.mairie.ptg.dto.SaisieReturnMessageDto;
import nc.noumea.mairie.ptg.repository.AccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.impl.ApprobationService;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.sirh.domain.Agent;

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
		String codeService = null;
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
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent, codeService)).thenReturn(das);
		
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9001234);
		ptg.setIdPointage(1);
		ptg.setType(new RefTypePointage());
		ptg.getType().setIdRefTypePointage(1);
		EtatPointage etat = new EtatPointage();
		EtatPointagePK etatpk = new EtatPointagePK();
		etatpk.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat.setEtatPointagePk(etatpk);
		etat.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(etat);
		
		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9001235);
		ptg2.setIdPointage(2);
		ptg2.setType(new RefTypePointage());
		ptg2.getType().setIdRefTypePointage(1);
		EtatPointage etat2 = new EtatPointage();
		EtatPointagePK etatpk2 = new EtatPointagePK();
		etatpk2.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat2.setEtatPointagePk(etatpk2);
		etat2.setEtat(EtatPointageEnum.SAISI);
		ptg2.getEtats().add(etat2);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getListPointages(Mockito.eq(Arrays.asList(9001234, 9001235)), Mockito.eq(fromDate), Mockito.eq(toDate), Mockito.eq(idRefType))).thenReturn(Arrays.asList(ptg, ptg2));
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(9001234)).thenReturn(new Agent());
		Mockito.when(mRepo.getAgent(9001235)).thenReturn(new Agent());
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		List<ConsultPointageDto> result = service.getPointages(idAgent, fromDate, toDate, codeService, agent, idRefEtat, idRefType);
		
		// Then
		assertEquals(2, result.size());
		assertEquals(1, (int) result.get(0).getIdPointage());
		assertEquals(2, (int) result.get(1).getIdPointage());
	}
	
	@Test
	public void getPointages_EtatFilters_ReturnFilteredPointages() {
		
		// Given
		Integer idAgent = 9008765;
		Date fromDate = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Date toDate = new DateTime(2013, 05, 20, 0, 0, 0).toDate();
		String codeService = null;
		Integer agent = null;
		Integer idRefType = 1;
		
		List<DroitsAgent> das = new ArrayList<DroitsAgent>();
		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9001234);
		das.add(da);
		IAccessRightsRepository arRepo = Mockito.mock(AccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent, codeService)).thenReturn(das);
		
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9001234);
		ptg.setIdPointage(1);
		ptg.setType(new RefTypePointage());
		ptg.getType().setIdRefTypePointage(1);
		EtatPointage etat = new EtatPointage();
		EtatPointagePK etatpk = new EtatPointagePK();
		etatpk.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat.setEtatPointagePk(etatpk);
		etat.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(etat);
		
		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9001234);
		ptg2.setIdPointage(2);
		ptg2.setType(new RefTypePointage());
		ptg2.getType().setIdRefTypePointage(1);
		EtatPointage etat2 = new EtatPointage();
		EtatPointagePK etatpk2 = new EtatPointagePK();
		etatpk2.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat2.setEtatPointagePk(etatpk2);
		etat2.setEtat(EtatPointageEnum.SAISI);
		ptg2.getEtats().add(etat2);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getListPointages(Mockito.eq(Arrays.asList(9001234)), Mockito.eq(fromDate), Mockito.eq(toDate), Mockito.eq(idRefType))).thenReturn(Arrays.asList(ptg, ptg2));
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(9001234)).thenReturn(new Agent());
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		List<ConsultPointageDto> result = service.getPointages(idAgent, fromDate, toDate, codeService, agent, EtatPointageEnum.SAISI.getCodeEtat(), idRefType);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(2, (int) result.get(0).getIdPointage());
	}
	
	@Test
	public void getPointages_PtgWithParent_ReturnNoPointageWithParent() {
		
		// Given
		Integer idAgent = 9008765;
		Date fromDate = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Date toDate = new DateTime(2013, 05, 20, 0, 0, 0).toDate();
		String codeService = null;
		Integer agent = null;
		Integer idRefEtat = null;
		Integer idRefType = null;
		
		List<DroitsAgent> das = new ArrayList<DroitsAgent>();
		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9001234);
		das.add(da);
		IAccessRightsRepository arRepo = Mockito.mock(AccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent, codeService)).thenReturn(das);
		
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9001234);
		ptg.setIdPointage(1);
		ptg.setType(new RefTypePointage());
		ptg.getType().setIdRefTypePointage(1);
		EtatPointage etat = new EtatPointage();
		EtatPointagePK etatpk = new EtatPointagePK();
		etatpk.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat.setEtatPointagePk(etatpk);
		etat.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(etat);
		
		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9001234);
		ptg2.setIdPointage(2);
		ptg2.setType(new RefTypePointage());
		ptg2.getType().setIdRefTypePointage(1);
		ptg2.setPointageParent(ptg);
		EtatPointage etat2 = new EtatPointage();
		EtatPointagePK etatpk2 = new EtatPointagePK();
		etatpk2.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat2.setEtatPointagePk(etatpk2);
		etat2.setEtat(EtatPointageEnum.SAISI);
		ptg2.getEtats().add(etat2);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getListPointages(Mockito.eq(Arrays.asList(9001234)), Mockito.eq(fromDate), Mockito.eq(toDate), Mockito.eq(idRefType))).thenReturn(Arrays.asList(ptg2, ptg));
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(9001234)).thenReturn(new Agent());
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		List<ConsultPointageDto> result = service.getPointages(idAgent, fromDate, toDate, codeService, agent, idRefEtat, idRefType);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(2, (int) result.get(0).getIdPointage());
	}
	
	@Test
	public void getPointages_AgentFilter_ReturnFilteredPointages() {
		
		// Given
		Integer idAgent = 9008765;
		Date fromDate = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Date toDate = new DateTime(2013, 05, 20, 0, 0, 0).toDate();
		String codeService = null;
		Integer agent = 9001235;
		Integer idRefEtat = null;
		Integer idRefType = null;
		
		List<DroitsAgent> das = new ArrayList<DroitsAgent>();
		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9001234);
		das.add(da);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9001235);
		das.add(da2);
		IAccessRightsRepository arRepo = Mockito.mock(AccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent, codeService)).thenReturn(das);
		
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9001235);
		ptg.setIdPointage(1);
		ptg.setType(new RefTypePointage());
		ptg.getType().setIdRefTypePointage(1);
		EtatPointage etat = new EtatPointage();
		EtatPointagePK etatpk = new EtatPointagePK();
		etatpk.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat.setEtatPointagePk(etatpk);
		etat.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(etat);
		
		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9001234);
		ptg2.setIdPointage(2);
		ptg2.setType(new RefTypePointage());
		ptg2.getType().setIdRefTypePointage(1);
		EtatPointage etat2 = new EtatPointage();
		EtatPointagePK etatpk2 = new EtatPointagePK();
		etatpk2.setDateEtat(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		etat2.setEtatPointagePk(etatpk2);
		etat2.setEtat(EtatPointageEnum.SAISI);
		ptg2.getEtats().add(etat2);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getListPointages(Mockito.eq(Arrays.asList(agent)), Mockito.eq(fromDate), Mockito.eq(toDate), Mockito.eq(idRefType))).thenReturn(Arrays.asList(ptg));
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(ptg.getIdAgent())).thenReturn(new Agent());
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		List<ConsultPointageDto> result = service.getPointages(idAgent, fromDate, toDate, codeService, agent, idRefEtat, idRefType);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(1, (int) result.get(0).getIdPointage());
	}
	
	@Test
	public void getPointages_AgentFilter_AgentNotInListOfAgentsForUser_ReturnEmptyList() {
		
		// Given
		Integer idAgent = 9008765;
		Date fromDate = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Date toDate = new DateTime(2013, 05, 20, 0, 0, 0).toDate();
		String codeService = null;
		Integer agent = 9001235;
		Integer idRefEtat = null;
		Integer idRefType = null;
		
		IAccessRightsRepository arRepo = Mockito.mock(AccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent, codeService)).thenReturn(new ArrayList<DroitsAgent>());
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		
		// When
		List<ConsultPointageDto> result = service.getPointages(idAgent, fromDate, toDate, codeService, agent, idRefEtat, idRefType);
		
		// Then
		assertEquals(0, result.size());
	}

	@Test
	public void getPointagesArchives_1PointageWithSeveralEtats() {
		
		// Given
		Pointage ptg1 = new Pointage();
		ptg1.setIdPointage(123);
		ptg1.setType(new RefTypePointage());
		ptg1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		
		EtatPointage etat1 = new EtatPointage();
		EtatPointagePK etat1pk = new EtatPointagePK();
		etat1pk.setDateEtat(new DateTime(2013, 04, 28, 8, 10, 0).toDate());
		etat1pk.setPointage(ptg1);
		etat1.setEtatPointagePk(etat1pk);
		etat1.setEtat(EtatPointageEnum.SAISI);
		etat1.setIdAgent(9007861);
		
		EtatPointage etat2 = new EtatPointage();
		EtatPointagePK etat2pk = new EtatPointagePK();
		etat2pk.setDateEtat(new DateTime(2013, 04, 29, 10, 20, 0).toDate());
		etat2pk.setPointage(ptg1);
		etat2.setEtatPointagePk(etat2pk);
		etat2.setEtat(EtatPointageEnum.REFUSE);
		etat2.setIdAgent(9007860);
		
		EtatPointage etat3 = new EtatPointage();
		EtatPointagePK etat3pk = new EtatPointagePK();
		etat3pk.setDateEtat(new DateTime(2013, 04, 29, 10, 30, 0).toDate());
		etat3pk.setPointage(ptg1);
		etat3.setEtatPointagePk(etat3pk);
		etat3.setEtat(EtatPointageEnum.SAISI);
		etat3.setIdAgent(9007861);
		
		ptg1.setEtats(Arrays.asList(etat3, etat2, etat1));
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointageArchives(123)).thenReturn(Arrays.asList(ptg1));
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Agent ag9007860 = new Agent();
		ag9007860.setIdAgent(9007860);
		Mockito.when(mRepo.getAgent(9007860)).thenReturn(ag9007860);
		Agent ag9007861 = new Agent();
		ag9007861.setIdAgent(9007861);
		Mockito.when(mRepo.getAgent(9007861)).thenReturn(ag9007861);
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);

		// When
		List<ConsultPointageDto> result = service.getPointagesArchives(9001234, 123);
		
		// Then
		assertEquals(3, result.size());
		assertEquals(EtatPointageEnum.SAISI.getCodeEtat(), (int) result.get(0).getIdRefEtat());
		assertEquals(new DateTime(2013, 04, 29, 10, 30, 0).toDate(), result.get(0).getDateSaisie());
		assertEquals(9007861, (int) result.get(0).getAgent().getIdAgent());
		assertEquals(EtatPointageEnum.REFUSE.getCodeEtat(), (int) result.get(1).getIdRefEtat());
		assertEquals(new DateTime(2013, 04, 29, 10, 20, 0).toDate(), result.get(1).getDateSaisie());
		assertEquals(9007860, (int) result.get(1).getAgent().getIdAgent());
		assertEquals(EtatPointageEnum.SAISI.getCodeEtat(), (int) result.get(2).getIdRefEtat());
		assertEquals(new DateTime(2013, 04, 28, 8, 10, 0).toDate(), result.get(2).getDateSaisie());
		assertEquals(9007861, (int) result.get(2).getAgent().getIdAgent());
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
		SaisieReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));
				
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
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		
		// When
		SaisieReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));
				
		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals("L'agent 9001234 n'a pas le droit de mettre à jour le pointage 9 de l'agent 9005678.", result.getErrors().get(0));
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
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		
		// When
		SaisieReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));
				
		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(1,  ptg.getEtats().size());
		assertEquals("Impossible de mettre à jour le pointage 9 de l'agent 9005678 car celui-ci est à l'état REFUSE_DEFINITIVEMENT.", result.getErrors().get(0));
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
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(5);
		
		// When
		SaisieReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));
				
		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(1,  ptg.getEtats().size());
		assertEquals("Impossible de mettre à jour le pointage 9 de l'agent 9005678 à l'état REJETE. Seuls APPROUVE, REFUSE ou SAISI sont acceptés.", result.getErrors().get(0));
	}
	
	@Test
	public void setPointagesEtat_SAISI_to_REFUSE_AddNewEtatPointage() {
		
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
		
		HelperService hService = Mockito.mock(HelperService.class);
		Mockito.when(hService.getCurrentDate()).thenReturn(new Date());
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(2);
		
		// When
		SaisieReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));
				
		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(2,  ptg.getEtats().size());
		assertEquals(EtatPointageEnum.SAISI, ptg.getEtats().get(0).getEtat());
		assertEquals(EtatPointageEnum.REFUSE, ptg.getEtats().get(1).getEtat());
	}
	
	@Test
	public void setPointagesEtat_APPROUVE_to_SAISI_AddNewEtatPointage() {
		
		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
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
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(0);
		
		// When
		SaisieReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));
				
		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(2,  ptg.getEtats().size());
		assertEquals(EtatPointageEnum.APPROUVE, ptg.getEtats().get(0).getEtat());
		assertEquals(EtatPointageEnum.SAISI, ptg.getEtats().get(1).getEtat());
	}
	
	@Test
	public void setPointagesEtat_REFUSE_to_APPROUVE_AddNewEtatPointage() {
		
		// Given
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9005678);
		ptg.setIdPointage(9);
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
		
		ApprobationService service = new ApprobationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hService);

		PointagesEtatChangeDto etatDto = new PointagesEtatChangeDto();
		etatDto.setIdPointage(123);
		etatDto.setIdRefEtat(1);
		
		// When
		SaisieReturnMessageDto result = service.setPointagesEtat(9001234, Arrays.asList(etatDto));
				
		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(2,  ptg.getEtats().size());
		assertEquals(EtatPointageEnum.REFUSE, ptg.getEtats().get(0).getEtat());
		assertEquals(EtatPointageEnum.APPROUVE, ptg.getEtats().get(1).getEtat());
	}
}
