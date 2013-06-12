package nc.noumea.mairie.ptg.service;

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
import nc.noumea.mairie.ptg.dto.ConsultPointageDto;
import nc.noumea.mairie.ptg.repository.AccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
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
		
		List<Integer> idAgents = Arrays.asList(9001234);
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
		Integer idRefEtat = null;
		Integer idRefType = 1;
		
		List<Integer> idAgents = Arrays.asList(9001234);
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
		
		List<Integer> idAgents = Arrays.asList(9001234);
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
		
		List<Integer> idAgents = Arrays.asList(9001234);
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
}
