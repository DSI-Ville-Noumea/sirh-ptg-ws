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

import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.ws.ISirhWSConsumer;

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
		List<Droit> droits = new ArrayList<Droit>();

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
		Droit da = new Droit();
		da.setIdAgent(idAgent);
		da.setApprobateur(true);
		List<Droit> droits = Arrays.asList(da);

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
		Droit da = new Droit();
		da.setIdAgent(900);
		da.setApprobateur(true);
		da.setIdAgentDelegataire(idAgent);
		Droit da2 = new Droit();
		da2.setIdAgent(idAgent);
		da2.setOperateur(true);
		List<Droit> droits = Arrays.asList(da, da2);

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

		ServiceDto siserv = new ServiceDto();
		siserv.setService("SERVICE");
		siserv.setServiceLibelle("LIB SERVICE");

		List<Droit> droits = new ArrayList<Droit>();

		ISirhWSConsumer wsMock = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(wsMock.getAgentDirection(idAgent)).thenReturn(siserv);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAllDroitsForService(siserv.getService())).thenReturn(droits);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", wsMock);

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

		ServiceDto siserv = new ServiceDto();
		siserv.setService("SERVICE");
		siserv.setServiceLibelle("LIB SERVICE");

		List<Droit> droits = new ArrayList<Droit>();
		Droit da = new Droit();
		da.setIdAgent(9007754);
		da.setCodeService("SERVICE");
		da.setIdDroit(9);
		da.setIdAgentDelegataire(9007654);
		droits.add(da);
		Droit da2 = new Droit();
		da2.setIdAgent(9007655);
		da2.setCodeService("SERVICE");
		da2.setIdDroit(10);
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
		Mockito.when(arRepo.getAllDroitsForService(siserv.getService())).thenReturn(droits);

		ISirhWSConsumer wsMock = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(wsMock.getAgentDirection(idAgent)).thenReturn(siserv);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhEntityManager", emMock);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", wsMock);

		// When
		DelegatorAndOperatorsDto dto = service.getDelegatorAndOperators(idAgent);

		// Then
		assertNotNull(dto.getDelegataire());
		assertEquals(9007654, (int) dto.getDelegataire().getIdAgent());
		assertEquals(1, dto.getSaisisseurs().size());
		assertEquals(9007655, (int) dto.getSaisisseurs().get(0).getIdAgent());
	}

	// @Test
	// public void setDelegatorAndOperators_delegatorExist_replaceIt() {
	//
	// // Given
	// int idAgent = 9008765;
	//
	// DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
	// dto.setDelegataire(new AgentDto());
	// dto.getDelegataire().setIdAgent(9008888);
	//
	// Droit da1 = new Droit();
	// da1.setIdAgent(900);
	// da1.setApprobateur(true);
	// da1.setIdAgentDelegataire(idAgent);
	// da1.setCodeService("SERV1");
	//
	// FichePoste fp = new FichePoste();
	// fp.setCodeService("SERVICE");
	// ServiceDto siserv = new ServiceDto();
	// siserv.setService("SERVICE");
	// siserv.setServiceLibelle("LIB SERVICE");
	//
	// IAccessRightsRepository arRepo =
	// Mockito.mock(IAccessRightsRepository.class);
	// Mockito.when(arRepo.getAllDroitsForService(siserv.getService())).thenReturn(Arrays.asList(da1));
	//
	// ISirhWSConsumer wsMock = Mockito.mock(ISirhWSConsumer.class);
	// Mockito.when(wsMock.getAgentDirection(idAgent)).thenReturn(siserv);
	//
	// HelperService hsMock = Mockito.mock(HelperService.class);
	// Mockito.when(hsMock.getCurrentDate()).thenReturn(new DateTime(2013, 04,
	// 19, 14, 5, 9).toDate());
	//
	// EntityManager ptgEMMock = Mockito.mock(EntityManager.class);
	//
	// AccessRightsService service = new AccessRightsService();
	// ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
	// ReflectionTestUtils.setField(service, "sirhWSConsumer", wsMock);
	// ReflectionTestUtils.setField(service, "helperService", hsMock);
	// ReflectionTestUtils.setField(service, "ptgEntityManager", ptgEMMock);
	//
	//
	// // When
	// List<DroitsAgent> actualList = service.setDelegatorAndOperators(idAgent,
	// dto);
	//
	// // Then
	// assertEquals(1, actualList.size());
	// assertEquals(new Integer(900), actualList.get(0).getIdAgent());
	// assertEquals(new DateTime(2013, 04, 19, 14, 5, 9).toDate(),
	// actualList.get(0).getDateModification());
	// assertTrue(actualList.get(0).isApprobateur());
	// assertFalse(actualList.get(0).isOperateur());
	// assertEquals(new Integer(9008888), actualList.get(0).getIdDelegataire());
	//
	// Mockito.verify(ptgEMMock, times(1)).persist(da1);
	// }
	//
	// @Test
	// public void setDelegatorAndOperators_removeDelegator_deleteIt() {
	//
	// // Given
	// int idAgent = 9008765;
	//
	// DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
	//
	// DroitsAgent da1 = new DroitsAgent();
	// da1.setIdAgent(900);
	// da1.setApprobateur(true);
	// da1.setIdDelegataire(idAgent);
	// da1.setCodeService("SERV1");
	//
	// FichePoste fp = new FichePoste();
	// fp.setCodeService("SERVICE");
	// ServiceDto siserv = new ServiceDto();
	// siserv.setService("SERVICE");
	// siserv.setServiceLibelle("LIB SERVICE");
	//
	// IAccessRightsRepository arRepo =
	// Mockito.mock(IAccessRightsRepository.class);
	// Mockito.when(arRepo.getAllDroitsForService(siserv.getService())).thenReturn(Arrays.asList(da1));
	//
	// ISirhWSConsumer wsMock = Mockito.mock(ISirhWSConsumer.class);
	// Mockito.when(wsMock.getAgentDirection(idAgent)).thenReturn(siserv);
	//
	// HelperService hsMock = Mockito.mock(HelperService.class);
	// Mockito.when(hsMock.getCurrentDate()).thenReturn(new DateTime(2013, 04,
	// 19, 14, 5, 9).toDate());
	//
	// EntityManager ptgEMMock = Mockito.mock(EntityManager.class);
	//
	// AccessRightsService service = new AccessRightsService();
	// ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
	// ReflectionTestUtils.setField(service, "sirhWSConsumer", wsMock);
	// ReflectionTestUtils.setField(service, "helperService", hsMock);
	// ReflectionTestUtils.setField(service, "ptgEntityManager", ptgEMMock);
	//
	//
	// // When
	// List<DroitsAgent> actualList = service.setDelegatorAndOperators(idAgent,
	// dto);
	//
	// // Then
	// assertEquals(1, actualList.size());
	// assertEquals(new Integer(900), actualList.get(0).getIdAgent());
	// assertEquals(new DateTime(2013, 04, 19, 14, 5, 9).toDate(),
	// actualList.get(0).getDateModification());
	// assertTrue(actualList.get(0).isApprobateur());
	// assertFalse(actualList.get(0).isOperateur());
	// assertNull(actualList.get(0).getIdDelegataire());
	//
	// Mockito.verify(arRepo, times(0)).removeDroitsAgent(da1);
	// Mockito.verify(ptgEMMock, times(1)).persist(da1);
	// }
	//
	// @Test
	// public void setDelegatorAndOperators_2operatorsExist_keep1AndreplaceOne()
	// {
	//
	// // Given
	// int idAgent = 9008765;
	//
	// DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
	// dto.getSaisisseurs().add(new AgentDto());
	// dto.getSaisisseurs().get(0).setIdAgent(9008888);
	// dto.getSaisisseurs().add(new AgentDto());
	// dto.getSaisisseurs().get(1).setIdAgent(9008887);
	//
	// DroitsAgent daAppro = new DroitsAgent();
	// daAppro.setApprobateur(true);
	// daAppro.setIdAgent(900);
	// daAppro.setCodeService("SERV1");
	//
	// DroitsAgent da1 = new DroitsAgent();
	// da1.setOperateur(true);
	// da1.setIdAgent(9008888);
	// da1.setCodeService("SERV1");
	//
	// DroitsAgent da2 = new DroitsAgent();
	// da2.setOperateur(true);
	// da2.setIdAgent(901);
	// da2.setCodeService("SERV1");
	//
	// FichePoste fp = new FichePoste();
	// fp.setCodeService("SERVICE");
	// ServiceDto siserv = new ServiceDto();
	// siserv.setService("SERVICE");
	// siserv.setServiceLibelle("LIB SERVICE");
	//
	// IAccessRightsRepository arRepo =
	// Mockito.mock(IAccessRightsRepository.class);
	// Mockito.when(arRepo.getAllDroitsForService(siserv.getService())).thenReturn(Arrays.asList(daAppro,
	// da1, da2));
	//
	// ISirhWSConsumer wsMock = Mockito.mock(ISirhWSConsumer.class);
	// Mockito.when(wsMock.getAgentDirection(idAgent)).thenReturn(siserv);
	//
	// HelperService hsMock = Mockito.mock(HelperService.class);
	// Mockito.when(hsMock.getCurrentDate()).thenReturn(new DateTime(2013, 04,
	// 19, 14, 5, 9).toDate());
	//
	// EntityManager ptgEMMock = Mockito.mock(EntityManager.class);
	//
	// AccessRightsService service = new AccessRightsService();
	// ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
	// ReflectionTestUtils.setField(service, "sirhWSConsumer", wsMock);
	// ReflectionTestUtils.setField(service, "helperService", hsMock);
	// ReflectionTestUtils.setField(service, "ptgEntityManager", ptgEMMock);
	//
	//
	// // When
	// List<DroitsAgent> actualList = service.setDelegatorAndOperators(idAgent,
	// dto);
	//
	// // Then
	// assertEquals(3, actualList.size());
	//
	// assertEquals(new Integer(900), actualList.get(0).getIdAgent());
	// assertNull(actualList.get(0).getDateModification());
	// assertNull(actualList.get(0).getIdDelegataire());
	// assertTrue(actualList.get(0).isApprobateur());
	// assertFalse(actualList.get(0).isOperateur());
	//
	// assertEquals(new Integer(9008888), actualList.get(1).getIdAgent());
	// assertNull(actualList.get(1).getDateModification());
	// assertNull(actualList.get(1).getIdDelegataire());
	// assertTrue(actualList.get(1).isOperateur());
	// assertFalse(actualList.get(1).isApprobateur());
	//
	// assertEquals(new Integer(9008887), actualList.get(2).getIdAgent());
	// assertEquals(new DateTime(2013, 04, 19, 14, 5, 9).toDate(),
	// actualList.get(2).getDateModification());
	// assertNull(actualList.get(2).getIdDelegataire());
	// assertTrue(actualList.get(2).isOperateur());
	// assertFalse(actualList.get(2).isApprobateur());
	//
	// Mockito.verify(arRepo, times(1)).removeDroitsAgent(da2);
	// Mockito.verify(ptgEMMock, times(1)).persist(da1);
	// }

	@Test
	public void getApprobateurs_returnemptydto() {

		// Given

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentsApprobateurs()).thenReturn(new ArrayList<Droit>());

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		// When
		List<AgentDto> dto = service.listAgentsApprobateurs();

		// Then
		assertEquals(0, dto.size());
	}

	@Test
	public void getApprobateurs_noEmptyDto() {

		// Given
		List<Droit> listeDroits = new ArrayList<Droit>();
		Droit d1 = new Droit();
		d1.setApprobateur(true);
		d1.setIdAgent(9005138);
		Droit d2 = new Droit();
		d2.setApprobateur(true);
		d2.setIdAgent(9003041);
		listeDroits.add(d1);
		listeDroits.add(d2);
		
		Agent ag9005138 = new Agent();
		ag9005138.setIdAgent(9005138);
		Agent ag9003041 = new Agent();
		ag9003041.setIdAgent(9003041);
		
		ServiceDto siservAg9005138 = new ServiceDto();
		siservAg9005138.setService("SERVICE");
		siservAg9005138.setServiceLibelle("LIB SERVICE");
		ServiceDto siservAg9003041 = new ServiceDto();
		siservAg9003041.setService("SERVICE2");
		siservAg9003041.setServiceLibelle("LIB SERVICE2");

		ISirhWSConsumer wsMock = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(wsMock.getAgentDirection(9005138)).thenReturn(siservAg9005138);
		Mockito.when(wsMock.getAgentDirection(9003041)).thenReturn(siservAg9003041);
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentsApprobateurs()).thenReturn(listeDroits);


		EntityManager emMock = Mockito.mock(EntityManager.class);
		Mockito.when(emMock.find(Agent.class, 9005138)).thenReturn(ag9005138);
		Mockito.when(emMock.find(Agent.class, 9003041)).thenReturn(ag9003041);
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", wsMock);
		ReflectionTestUtils.setField(service, "sirhEntityManager", emMock);

		// When
		List<AgentDto> dto = service.listAgentsApprobateurs();

		// Then
		assertEquals(2, dto.size());
		assertEquals("SERVICE", dto.get(0).getCodeService());
		assertEquals("SERVICE2", dto.get(1).getCodeService());
	}
}
