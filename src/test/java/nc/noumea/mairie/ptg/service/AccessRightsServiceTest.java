package nc.noumea.mairie.ptg.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
	public void getApprobateurs_returnemptydto() {

		// Given

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentsApprobateurs()).thenReturn(new ArrayList<Droit>());

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		// When
		List<AgentWithServiceDto> dto = service.listAgentsApprobateurs();

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
		List<AgentWithServiceDto> dto = service.listAgentsApprobateurs();

		// Then
		assertEquals(2, dto.size());
		assertEquals("SERVICE", dto.get(0).getCodeService());
		assertEquals("SERVICE2", dto.get(1).getCodeService());
	}
	
	@Test
	public void getAgentsToApprove_NoAgents_ReturnEmptyList() {
		
		// Given
		Integer idAgent = 9007654;
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentDroitApprobateurOrOperateur(idAgent)).thenReturn(null);
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		
		// When
		List<AgentDto> result = service.getAgentsToApprove(idAgent);
		
		// Then
		assertEquals(0, result.size());
	}

	@Test
	public void getAgentsToApprove_2Agents_ReturnListOf2() {
		
		// Given
		Integer idAgent = 9007654;

		Agent a1 = new Agent();
		a1.setIdAgent(1);
		Agent a2 = new Agent();
		a2.setIdAgent(2);
		
		DroitsAgent da1 = new DroitsAgent();
		da1.setIdAgent(1);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(2);
		
		Droit d = new Droit();
		d.getAgents().add(da1);
		d.getAgents().add(da2);
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentDroitApprobateurOrOperateur(idAgent)).thenReturn(d);
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(1)).thenReturn(a1);
		Mockito.when(mRepo.getAgent(2)).thenReturn(a2);
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		List<AgentDto> result = service.getAgentsToApprove(idAgent);
		
		// Then
		assertEquals(2, result.size());
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void setAgentsToApprove_1NewAgent_CreateAndCallSirhWs() {
		
		// Given
		AgentDto ag = new AgentDto();
		ag.setIdAgent(9008765);
		List<AgentDto> agsDto = Arrays.asList(ag);
		
		final Date currentDate = new DateTime(2013, 4, 9, 12, 9, 34).toDate();
		
		final Droit droit = new Droit();
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentDroitApprobateurOrOperateur(777)).thenReturn(droit);
		Mockito.doAnswer(new Answer() {
			 public Object answer(InvocationOnMock invocation) {
			     Object[] args = invocation.getArguments();
			     DroitsAgent obj = (DroitsAgent)args[0];

			     assertEquals(9008765, (int)obj.getIdAgent());
			     assertEquals("CODE", obj.getCodeService());
			     assertEquals("service", obj.getLibelleService());
			     assertEquals(droit, obj.getDroit());
			     assertEquals(currentDate, obj.getDateModification());
			     
			     return true;
			     
			     }}).when(arRepo).persisEntity(Mockito.any(DroitsAgent.class));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);
		
		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(9008765);
		agDto.setService("service");
		agDto.setCodeService("CODE");
		
		ISirhWSConsumer ws = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(ws.getAgentService(9008765, currentDate)).thenReturn(agDto);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", ws);
		
		// When
		service.setAgentsToApprove(777, agsDto);
		
		// Then
		// see callback for persisEntity
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void setAgentsToApprove_1ExistingAgent_UpdateModificationDate() {
		
		// Given
		AgentDto ag = new AgentDto();
		ag.setIdAgent(9008765);
		List<AgentDto> agsDto = Arrays.asList(ag);
		
		final Date currentDate = new DateTime(2013, 4, 9, 12, 9, 34).toDate();
		final DroitsAgent fda = new DroitsAgent();
		fda.setIdAgent(9008765);
		fda.setCodeService("CODE");
		fda.setLibelleService("service");
		final Droit droit = new Droit();
		droit.getAgents().add(fda);
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentDroitApprobateurOrOperateur(777)).thenReturn(droit);
		Mockito.doAnswer(new Answer() {
			 public Object answer(InvocationOnMock invocation) {
			     Object[] args = invocation.getArguments();
			     DroitsAgent obj = (DroitsAgent)args[0];

			     assertEquals(fda, obj);
			     assertEquals(currentDate, obj.getDateModification());
			     
			     return true;
			     
			     }}).when(arRepo).persisEntity(Mockito.any(DroitsAgent.class));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);
		
		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(9008765);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		Droit result = service.setAgentsToApprove(777, agsDto);
		
		// Then
		// see callback for persisEntity
		assertEquals(fda, result.getAgents().iterator().next());
	}
	
	@Test
	public void setAgentsToApprove_1RemovedAgent_RemoveFromDB() {
		
		// Given
		List<AgentDto> agsDto = new ArrayList<AgentDto>();
		
		DroitsAgent fda = new DroitsAgent();
		fda.setIdAgent(9008765);
		fda.setCodeService("CODE");
		fda.setLibelleService("service");
		Droit droit = new Droit();
		droit.getAgents().add(fda);
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentDroitApprobateurOrOperateur(777)).thenReturn(droit);
		
		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(9008765);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		
		// When
		Droit result = service.setAgentsToApprove(777, agsDto);
				
		// Then
		assertEquals(0, result.getAgents().size());
	
	}
}
