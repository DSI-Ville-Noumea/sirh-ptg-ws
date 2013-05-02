package nc.noumea.mairie.ptg.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.FichePoste;
import nc.noumea.mairie.sirh.domain.Siserv;
import nc.noumea.mairie.sirh.service.ISiservService;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.staticmock.MockStaticEntityMethods;
import org.springframework.test.util.ReflectionTestUtils;

@MockStaticEntityMethods
public class AccessRightsServiceTest {

	@Test
	public void getAgentAccessRights_AgentHasNoRights_ReturnFalseEverywhere() {
		
		// Given
		Integer idAgent = 906543;
		List<DroitsAgent> droits = new ArrayList<DroitsAgent>();
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentAccessRights(idAgent)).thenReturn(droits);
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		
		// When
		AccessRightsDto result = service.getAgentAccessRights(idAgent);
		
		// Then
		assertFalse(result.isFiches());
		assertFalse(result.isSaisie());
		assertFalse(result.isVisualisation());
		assertFalse(result.isApprobation());
		assertFalse(result.isGestionDroitsAcces());
	}
	
	@Test
	public void getAgentAccessRights_AgentHas1Right_ReturnThisRights() {
		
		// Given
		Integer idAgent = 906543;
		DroitsAgent da = new DroitsAgent();
		da.setApprobateur(true);
		List<DroitsAgent> droits = Arrays.asList(da);
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentAccessRights(idAgent)).thenReturn(droits);
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		
		// When
		AccessRightsDto result = service.getAgentAccessRights(idAgent);
		
		// Then
		assertTrue(result.isFiches());
		assertTrue(result.isSaisie());
		assertTrue(result.isVisualisation());
		assertTrue(result.isApprobation());
		assertTrue(result.isGestionDroitsAcces());
	}
	
	@Test
	public void getAgentAccessRights_AgentHas2Rights_ReturnOrLogicRights() {
		
		// Given
		Integer idAgent = 906543;
		DroitsAgent da = new DroitsAgent();
		da.setDelegataire(true);
		DroitsAgent da2 = new DroitsAgent();
		da.setOperateur(true);
		List<DroitsAgent> droits = Arrays.asList(da, da2);
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentAccessRights(idAgent)).thenReturn(droits);
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		
		// When
		AccessRightsDto result = service.getAgentAccessRights(idAgent);
		
		// Then
		assertTrue(result.isFiches());
		assertTrue(result.isSaisie());
		assertTrue(result.isVisualisation());
		assertTrue(result.isApprobation());
		assertFalse(result.isGestionDroitsAcces());
	}
	
	@Test
	public void getDelegatorAndInputters_noOneYet_returnemptydto() {
		
		// Given
		int idAgent = 9007754;
		
		Siserv siserv = new Siserv();
		siserv.setServi("SERVICE");
		siserv.setLiServ("LIB SERVICE");
		
		List<DroitsAgent> droits = new ArrayList<DroitsAgent>();
		
		ISiservService siMock = Mockito.mock(ISiservService.class);
		Mockito.when(siMock.getAgentService(idAgent)).thenReturn(siserv);
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAllDroitsForService(siserv.getServi())).thenReturn(droits);
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "siservService", siMock);
		
		// When
		DelegatorAndOperatorsDto dto = service.getDelegatorAndOperators(idAgent);
		
		// Then
		assertNull(dto.getDelegataire());
		assertEquals(0, dto.getSaisisseurs().size());
	}
	
	@Test
	public void getDelegatorAndInputters_1DelegatorAnd1Inputter_returnFilledInDtos() {
		
		// Given
		int idAgent = 9007754;
		
		Siserv siserv = new Siserv();
		siserv.setLiServ("SERVICE LIB");
		siserv.setServi("SERVICE");
		
		List<DroitsAgent> droits = new ArrayList<DroitsAgent>();
		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9007654);
		da.setCodeService("SERVICE");
		da.setIdDroitsAgent(9);
		da.setDelegataire(true);
		droits.add(da);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9007655);
		da2.setCodeService("SERVICE");
		da2.setIdDroitsAgent(10);
		da2.setOperateur(true);
		droits.add(da2);

		EntityManager emMock = Mockito.mock(EntityManager.class);
		Agent ag9007654 = new Agent();
		ag9007654.setIdAgent(9007654);
		Mockito.when(emMock.find(Agent.class, 9007654)).thenReturn(ag9007654);
		Agent ag9007655 = new Agent();
		ag9007655.setIdAgent(9007655);
		Mockito.when(emMock.find(Agent.class, 9007655)).thenReturn(ag9007655);
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAllDroitsForService(siserv.getServi())).thenReturn(droits);

		ISiservService siMock = Mockito.mock(ISiservService.class);
		Mockito.when(siMock.getAgentService(idAgent)).thenReturn(siserv);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhEntityManager", emMock);
		ReflectionTestUtils.setField(service, "siservService", siMock);
		
		// When
		DelegatorAndOperatorsDto dto = service.getDelegatorAndOperators(idAgent);
		
		// Then
		assertNotNull(dto.getDelegataire());
		assertEquals(9007654, dto.getDelegataire().getIdAgent());
		assertEquals(1, dto.getSaisisseurs().size());
		assertEquals(9007655, dto.getSaisisseurs().get(0).getIdAgent());
	}
	
	@Test
	public void setDelegatorAndOperators_delegatorExist_replaceIt() {
		
		// Given
		int idAgent = 9008765;
		
		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		dto.setDelegataire(new AgentDto());
		dto.getDelegataire().setIdAgent(9008888);
		
		DroitsAgent da1 = new DroitsAgent();
		da1.setDelegataire(true);
		da1.setIdAgent(idAgent);
		da1.setCodeService("SERV1");
		
		FichePoste fp = new FichePoste();
		fp.setCodeService("SERVICE");
		Siserv siserv = new Siserv();
		siserv.setLiServ("SERVICE LIB");
		siserv.setServi("SERVICE");
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAllDroitsForService(siserv.getServi())).thenReturn(Arrays.asList(da1));

		ISiservService siMock = Mockito.mock(ISiservService.class);
		Mockito.when(siMock.getAgentService(idAgent)).thenReturn(siserv);
		
		HelperService hsMock = Mockito.mock(HelperService.class);
		Mockito.when(hsMock.getCurrentDate()).thenReturn(new DateTime(2013, 04, 19, 14, 5, 9).toDate());
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "siservService", siMock);
		ReflectionTestUtils.setField(service, "helperService", hsMock);
		
		
		// When
		List<DroitsAgent> actualList = service.setDelegatorAndOperators(idAgent, dto);
		
		// Then
		assertEquals(1, actualList.size());
		assertEquals(new Integer(9008888), actualList.get(0).getIdAgent());
		assertEquals(new DateTime(2013, 04, 19, 14, 5, 9).toDate(), actualList.get(0).getDateModification());
		assertTrue(actualList.get(0).isDelegataire());
		assertFalse(actualList.get(0).isOperateur());
		assertFalse(actualList.get(0).isApprobateur());
	}
	
	@Test
	public void setDelegatorAndOperators_delegatorDoesNotExist_createIt() {
		
		// Given
		int idAgent = 9008765;
		
		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		dto.setDelegataire(new AgentDto());
		dto.getDelegataire().setIdAgent(9008888);
		
		FichePoste fp = new FichePoste();
		fp.setCodeService("SERVICE");
		Siserv siserv = new Siserv();
		siserv.setLiServ("SERVICE LIB");
		siserv.setServi("SERVICE");
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAllDroitsForService(siserv.getServi())).thenReturn(new ArrayList<DroitsAgent>());

		ISiservService siMock = Mockito.mock(ISiservService.class);
		Mockito.when(siMock.getAgentService(idAgent)).thenReturn(siserv);
		
		HelperService hsMock = Mockito.mock(HelperService.class);
		Mockito.when(hsMock.getCurrentDate()).thenReturn(new DateTime(2013, 04, 19, 14, 5, 9).toDate());
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "siservService", siMock);
		ReflectionTestUtils.setField(service, "helperService", hsMock);
		
		// When
		List<DroitsAgent> actualList = service.setDelegatorAndOperators(idAgent, dto);
		
		// Then
		assertEquals(1, actualList.size());
		assertEquals(new Integer(9008888), actualList.get(0).getIdAgent());
		assertEquals("SERVICE", actualList.get(0).getCodeService());
		assertEquals(new DateTime(2013, 04, 19, 14, 5, 9).toDate(), actualList.get(0).getDateModification());
		assertTrue(actualList.get(0).isDelegataire());
		assertFalse(actualList.get(0).isOperateur());
		assertFalse(actualList.get(0).isApprobateur());
	}
	
	@Test
	public void setDelegatorAndOperators_2operatorsExist_keep1AndreplaceOne() {
		
		// Given
		int idAgent = 9008765;
		
		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		dto.getSaisisseurs().add(new AgentDto());
		dto.getSaisisseurs().get(0).setIdAgent(9008888);
		dto.getSaisisseurs().add(new AgentDto());
		dto.getSaisisseurs().get(1).setIdAgent(9008887);
		
		DroitsAgent da1 = new DroitsAgent();
		da1.setOperateur(true);
		da1.setIdAgent(9008888);
		da1.setCodeService("SERV1");
		
		DroitsAgent da2 = new DroitsAgent();
		da2.setOperateur(true);
		da2.setIdAgent(901);
		da2.setCodeService("SERV1");
		
		FichePoste fp = new FichePoste();
		fp.setCodeService("SERVICE");
		Siserv siserv = new Siserv();
		siserv.setLiServ("SERVICE LIB");
		siserv.setServi("SERVICE");
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAllDroitsForService(siserv.getServi())).thenReturn(Arrays.asList(da1, da2));

		ISiservService siMock = Mockito.mock(ISiservService.class);
		Mockito.when(siMock.getAgentService(idAgent)).thenReturn(siserv);
		
		HelperService hsMock = Mockito.mock(HelperService.class);
		Mockito.when(hsMock.getCurrentDate()).thenReturn(new DateTime(2013, 04, 19, 14, 5, 9).toDate());
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "siservService", siMock);
		ReflectionTestUtils.setField(service, "helperService", hsMock);
		
		
		// When
		List<DroitsAgent> actualList = service.setDelegatorAndOperators(idAgent, dto);
		
		// Then
		assertEquals(2, actualList.size());
		
		assertEquals(new Integer(9008888), actualList.get(0).getIdAgent());
		assertNull(actualList.get(0).getDateModification());
		assertFalse(actualList.get(0).isDelegataire());
		assertTrue(actualList.get(0).isOperateur());
		assertFalse(actualList.get(0).isApprobateur());
		
		assertEquals(new Integer(9008887), actualList.get(1).getIdAgent());
		assertEquals(new DateTime(2013, 04, 19, 14, 5, 9).toDate(), actualList.get(1).getDateModification());
		assertFalse(actualList.get(1).isDelegataire());
		assertTrue(actualList.get(1).isOperateur());
		assertFalse(actualList.get(1).isApprobateur());
	}
}
