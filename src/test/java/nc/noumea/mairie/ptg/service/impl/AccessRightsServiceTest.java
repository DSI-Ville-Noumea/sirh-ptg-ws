package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.ApprobateurDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndOperatorsDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.service.IAgentMatriculeConverterService;
import nc.noumea.mairie.ptg.web.AccessForbiddenException;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.IAdsWSConsumer;
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

		ISirhWSConsumer wsMock = Mockito.mock(ISirhWSConsumer.class);

		Date currentDate = new DateTime(2013, 4, 9, 12, 9, 34).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", wsMock);

		// When
		List<ApprobateurDto> dto = service.listAgentsApprobateurs(null, null);

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

		AgentWithServiceDto agDto1 = new AgentWithServiceDto();
		agDto1.setIdAgent(9005138);
		agDto1.setNom("TOTO");
		agDto1.setService("service");
		agDto1.setIdServiceADS(11);
		AgentWithServiceDto agDto2 = new AgentWithServiceDto();
		agDto2.setIdAgent(9003041);
		agDto2.setNom("TITO");
		agDto2.setService("service");
		agDto2.setIdServiceADS(22);

		Date currentDate = new DateTime(2013, 4, 9, 12, 9, 34).toDate();

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		ISirhWSConsumer wsMock = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(wsMock.getAgentService(9005138, currentDate)).thenReturn(agDto1);
		Mockito.when(wsMock.getAgentService(9003041, currentDate)).thenReturn(agDto2);
		
		List<AgentWithServiceDto> listAgentsServiceDto = new ArrayList<AgentWithServiceDto>();
		listAgentsServiceDto.add(agDto1);
		listAgentsServiceDto.add(agDto2);
		Mockito.when(wsMock.getListAgentsWithService(Arrays.asList(9005138,9003041), 
				currentDate)).thenReturn(listAgentsServiceDto);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentsApprobateurs()).thenReturn(listeDroits);

		IAgentMatriculeConverterService matriculeConvertor = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matriculeConvertor.tryConvertIdAgentToNomatr(9005138)).thenReturn(5138);
		Mockito.when(matriculeConvertor.tryConvertIdAgentToNomatr(9003041)).thenReturn(3041);
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", wsMock);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matriculeConvertor);

		// When
		List<ApprobateurDto> dto = service.listAgentsApprobateurs(null, null);

		// Then
		assertEquals(2, dto.size());
		assertEquals(22, dto.get(0).getApprobateur().getIdServiceADS().intValue());
		assertEquals(11, dto.get(1).getApprobateur().getIdServiceADS().intValue());
	}

	@Test
	public void setAgentsApprobateurs_nonExisting() {
		// Given
		AgentWithServiceDto agentDto = new AgentWithServiceDto();
		agentDto.setIdAgent(9005138);

		final Date d = new Date();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentsApprobateurs()).thenReturn(new ArrayList<Droit>());
		Mockito.when(arRepo.isUserOperator(9005138)).thenReturn(false);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Droit obj = (Droit) args[0];

				assertEquals(9005138, (int) obj.getIdAgent());
				assertEquals(d, obj.getDateModification());
				assertTrue(obj.isApprobateur());

				return true;

			}
		}).when(arRepo).persisEntity(Mockito.any(Droit.class));

		HelperService helpServ = Mockito.mock(HelperService.class);
		Mockito.when(helpServ.getCurrentDate()).thenReturn(d);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", helpServ);

		// When
		ReturnMessageDto res = service.setApprobateur(agentDto);

		// Then
		Mockito.verify(arRepo, Mockito.times(1)).persisEntity(Mockito.isA(Droit.class));
		assertEquals(0, res.getErrors().size());
	}

	@Test
	public void setAgentsApprobateurs_Existing() {
		// Given
		AgentWithServiceDto agentDto = new AgentWithServiceDto();
		agentDto.setIdAgent(9005138);

		Droit d = new Droit();
		d.setIdAgent(9005138);
		d.setApprobateur(true);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getDroitApprobateurByAgent(agentDto.getIdAgent())).thenReturn(d);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getCurrentDate()).thenReturn(new Date());

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		// When
		ReturnMessageDto res = service.setApprobateur(agentDto);

		// Then
		Mockito.verify(arRepo, Mockito.never()).persisEntity(Mockito.isA(Droit.class));
		assertEquals(0, res.getErrors().size());
	}

	@Test
	public void setAgentsApprobateurs_OperateurExisting() {
		// Given
		AgentWithServiceDto agentDto = new AgentWithServiceDto();
		agentDto.setIdAgent(9005138);

		Date currentDate = new Date();

		Droit d = new Droit();
		d.setIdAgent(9005138);
		d.setApprobateur(false);
		d.setOperateur(true);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentsApprobateurs()).thenReturn(new ArrayList<Droit>());

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto res = service.setApprobateur(agentDto);

		// Then
		Mockito.verify(arRepo, Mockito.times(1)).persisEntity(Mockito.isA(Droit.class));
		assertEquals(0, res.getErrors().size());
	}

	@Test
	public void deleteApprobateurs_SuppressionDtoEmpty() {
		// Given
		Droit d = Mockito.spy(new Droit());
		d.setIdAgent(9005138);
		d.setApprobateur(true);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getDroitApprobateurByAgent(null)).thenReturn(d);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(arRepo).removeEntity(Mockito.any(Droit.class));

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		// When
		ReturnMessageDto res = service.deleteApprobateur(new AgentWithServiceDto());

		// Then
		Mockito.verify(arRepo, Mockito.never()).persisEntity(Mockito.isA(Droit.class));
		Mockito.verify(arRepo, Mockito.times(1)).removeEntity(Mockito.isA(Droit.class));
		assertEquals(0, res.getErrors().size());
	}

	@Test
	public void deleteApprobateur_Suppression() {
		// Given
		AgentWithServiceDto agentDto = new AgentWithServiceDto();
		agentDto.setIdAgent(9005131);

		Droit d = Mockito.spy(new Droit());
		d.setIdAgent(9005138);
		d.setApprobateur(true);

		Date date = new Date();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getDroitApprobateurByAgent(agentDto.getIdAgent())).thenReturn(d);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(arRepo).removeEntity(Mockito.any(Droit.class));

		HelperService helpServ = Mockito.mock(HelperService.class);
		Mockito.when(helpServ.getCurrentDate()).thenReturn(date);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", helpServ);

		// When
		ReturnMessageDto res = service.deleteApprobateur(agentDto);

		// Then
		Mockito.verify(arRepo, Mockito.times(1)).removeEntity(Mockito.isA(Droit.class));
		assertEquals(0, res.getErrors().size());
	}

	@Test
	public void getAgentsToApproveOrInput_NoAgents_ReturnEmptyList() {

		// Given
		Integer idAgent = 9007654;

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentDroitApprobateurOrOperateurFetchAgents(idAgent, null)).thenReturn(null);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		// When
		List<AgentDto> result = service.getAgentsToApproveOrInput(idAgent);

		// Then
		assertEquals(0, result.size());
	}

	@Test
	public void getAgentsToApproveOrInput_2Agents_ReturnListOf2() {

		// Given
		Integer idAgent = 9007654;

		AgentGeneriqueDto a1 = new AgentGeneriqueDto();
		a1.setIdAgent(1);
		AgentGeneriqueDto a2 = new AgentGeneriqueDto();
		a2.setIdAgent(2);

		DroitsAgent da1 = new DroitsAgent();
		da1.setIdAgent(1);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(2);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent, null)).thenReturn(Arrays.asList(da1, da2));

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(mRepo.getAgent(1)).thenReturn(a1);
		Mockito.when(mRepo.getAgent(2)).thenReturn(a2);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);

		// When
		List<AgentDto> result = service.getAgentsToApproveOrInput(idAgent);

		// Then
		assertEquals(2, result.size());
	}

	// #15684 bug doublon d agents
	@Test
	public void getAgentsToApproveOrInput_AgentsDoublon() {

		// Given
		Integer idAgent = 9007654;

		AgentGeneriqueDto a1 = new AgentGeneriqueDto();
		a1.setIdAgent(9005138);
		AgentGeneriqueDto a2 = new AgentGeneriqueDto();
		a2.setIdAgent(9005131);
		AgentGeneriqueDto a3 = new AgentGeneriqueDto();
		a3.setIdAgent(9005157);

		DroitsAgent da1 = new DroitsAgent();
		da1.setIdAgent(9005138);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9005131);
		DroitsAgent da3 = new DroitsAgent();
		da3.setIdAgent(9005138);
		DroitsAgent da4 = new DroitsAgent();
		da4.setIdAgent(9005131);
		DroitsAgent da5 = new DroitsAgent();
		da5.setIdAgent(9005157);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent, 13)).thenReturn(
				Arrays.asList(da1, da2, da3, da4, da5));

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(mRepo.getAgent(9005138)).thenReturn(a1);
		Mockito.when(mRepo.getAgent(9005131)).thenReturn(a2);
		Mockito.when(mRepo.getAgent(9005157)).thenReturn(a3);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);

		// When
		List<AgentDto> result = service.getAgentsToApproveOrInput(idAgent, 13);

		// Then
		assertEquals(3, result.size());
		assertEquals(9005138, result.get(0).getIdAgent().intValue());
		assertEquals(9005131, result.get(1).getIdAgent().intValue());
		assertEquals(9005157, result.get(2).getIdAgent().intValue());
	}

	// #15684 bug doublon d agents
	@Test
	public void getAgentsToInput_AgentsDoublon() {

		// Given
		Integer idAgent = 9007654;

		AgentGeneriqueDto a1 = new AgentGeneriqueDto();
		a1.setIdAgent(9005138);
		AgentGeneriqueDto a2 = new AgentGeneriqueDto();
		a2.setIdAgent(9005131);
		AgentGeneriqueDto a3 = new AgentGeneriqueDto();
		a3.setIdAgent(9005157);

		DroitsAgent da1 = new DroitsAgent();
		da1.setIdAgent(9005138);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9005131);
		DroitsAgent da3 = new DroitsAgent();
		da3.setIdAgent(9005138);
		DroitsAgent da4 = new DroitsAgent();
		da4.setIdAgent(9005131);
		DroitsAgent da5 = new DroitsAgent();
		da5.setIdAgent(9005157);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInput(9002990, idAgent))
				.thenReturn(Arrays.asList(da1, da2, da3, da4, da5));

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(mRepo.getAgent(9005138)).thenReturn(a1);
		Mockito.when(mRepo.getAgent(9005131)).thenReturn(a2);
		Mockito.when(mRepo.getAgent(9005157)).thenReturn(a3);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);

		// When
		List<AgentDto> result = service.getAgentsToInput(9002990, idAgent);

		// Then
		assertEquals(3, result.size());
		assertEquals(9005138, result.get(0).getIdAgent().intValue());
		assertEquals(9005131, result.get(1).getIdAgent().intValue());
		assertEquals(9005157, result.get(2).getIdAgent().intValue());
	}

	// #15684 bug doublon d agents
	@Test
	public void getAgentsToApprove_AgentsDoublon() {

		// Given
		Integer idAgent = 9007654;

		AgentGeneriqueDto a1 = new AgentGeneriqueDto();
		a1.setIdAgent(9005138);
		AgentGeneriqueDto a2 = new AgentGeneriqueDto();
		a2.setIdAgent(9005131);
		AgentGeneriqueDto a3 = new AgentGeneriqueDto();
		a3.setIdAgent(9005157);

		DroitsAgent da1 = new DroitsAgent();
		da1.setIdAgent(9005138);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9005131);
		DroitsAgent da3 = new DroitsAgent();
		da3.setIdAgent(9005138);
		DroitsAgent da4 = new DroitsAgent();
		da4.setIdAgent(9005131);
		DroitsAgent da5 = new DroitsAgent();
		da5.setIdAgent(9005157);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToApprove(idAgent, 15))
				.thenReturn(Arrays.asList(da1, da2, da3, da4, da5));

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(mRepo.getAgent(9005138)).thenReturn(a1);
		Mockito.when(mRepo.getAgent(9005131)).thenReturn(a2);
		Mockito.when(mRepo.getAgent(9005157)).thenReturn(a3);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);

		// When
		List<AgentDto> result = service.getAgentsToApprove(idAgent, 15);

		// Then
		assertEquals(3, result.size());
		assertEquals(9005138, result.get(0).getIdAgent().intValue());
		assertEquals(9005131, result.get(1).getIdAgent().intValue());
		assertEquals(9005157, result.get(2).getIdAgent().intValue());
	}

	@Test
	public void getAgentsToApprove_2Agents_ReturnListOf2() {

		// Given
		Integer idAgent = 9007654;

		AgentGeneriqueDto a1 = new AgentGeneriqueDto();
		a1.setIdAgent(1);
		AgentGeneriqueDto a2 = new AgentGeneriqueDto();
		a2.setIdAgent(2);

		DroitsAgent da1 = new DroitsAgent();
		da1.setIdAgent(1);
		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(2);

		Droit d = new Droit();
		d.getAgents().add(da1);
		d.getAgents().add(da2);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToApprove(idAgent, null)).thenReturn(Arrays.asList(da1, da2));

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(mRepo.getAgent(1)).thenReturn(a1);
		Mockito.when(mRepo.getAgent(2)).thenReturn(a2);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);

		// When
		List<AgentDto> result = service.getAgentsToApprove(idAgent, null);

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
		Mockito.when(arRepo.getApprobateur(777)).thenReturn(droit);
		Mockito.doAnswer(new Answer() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				DroitsAgent obj = (DroitsAgent) args[0];

				assertEquals(9008765, (int) obj.getIdAgent());
				assertEquals(13, obj.getIdServiceADS().intValue());
				assertEquals("service", obj.getLibelleService());
				assertEquals(droit, obj.getDroits().iterator().next());
				assertEquals(currentDate, obj.getDateModification());

				return true;

			}
		}).when(arRepo).persisEntity(Mockito.any(DroitsAgent.class));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(9008765);
		agDto.setService("service");
		agDto.setIdServiceADS(13);

		ISirhWSConsumer ws = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(ws.getAgentService(9008765, currentDate)).thenReturn(agDto);

		IAdsWSConsumer adsWsConsumer = Mockito.mock(IAdsWSConsumer.class);
		Mockito.when(adsWsConsumer.getInfoSiservByIdEntite(agDto.getIdServiceADS())).thenReturn(null);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", ws);
		ReflectionTestUtils.setField(service, "adsWsConsumer", adsWsConsumer);

		// When
		service.setAgentsToApprove(777, agsDto);

		// Then
		// see callback for persisEntity
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void setAgentsToApprove_1NewAgent_ButExistinginDroitsAgent() {

		// Given
		AgentDto ag = new AgentDto();
		ag.setIdAgent(9008765);
		List<AgentDto> agsDto = Arrays.asList(ag);

		final Date currentDate = new DateTime(2013, 4, 9, 12, 9, 34).toDate();

		final Droit droit = new Droit();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateur(777)).thenReturn(droit);
		Mockito.doAnswer(new Answer() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				DroitsAgent obj = (DroitsAgent) args[0];

				assertEquals(13, (int) obj.getIdDroitsAgent());
				assertEquals(9008765, (int) obj.getIdAgent());
				assertEquals(13, obj.getIdServiceADS().intValue());
				assertEquals("service", obj.getLibelleService());
				assertEquals(droit, obj.getDroits().iterator().next());
				assertEquals(currentDate, obj.getDateModification());

				return true;

			}
		}).when(arRepo).persisEntity(Mockito.any(DroitsAgent.class));
		
		DroitsAgent droitsAgent = new DroitsAgent();
		droitsAgent.setIdAgent(ag.getIdAgent());
		droitsAgent.setIdDroitsAgent(13);
		
		Mockito.when(arRepo.getDroitsAgent(ag.getIdAgent())).thenReturn(droitsAgent);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(9008765);
		agDto.setService("service");
		agDto.setIdServiceADS(13);

		ISirhWSConsumer ws = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(ws.getAgentService(9008765, currentDate)).thenReturn(agDto);

		IAdsWSConsumer adsWsConsumer = Mockito.mock(IAdsWSConsumer.class);
		Mockito.when(adsWsConsumer.getInfoSiservByIdEntite(agDto.getIdServiceADS())).thenReturn(null);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", ws);
		ReflectionTestUtils.setField(service, "adsWsConsumer", adsWsConsumer);
		
		

		// When
		service.setAgentsToApprove(777, agsDto);

		// Then
		// see callback for persisEntity
	}

	@Test
	public void setAgentsToApprove_1ExistingAgent_DoNothing() {

		// Given
		AgentDto ag = new AgentDto();
		ag.setIdAgent(9008765);
		List<AgentDto> agsDto = Arrays.asList(ag);

		final Date currentDate = new DateTime(2013, 4, 9, 12, 9, 34).toDate();
		final DroitsAgent fda = new DroitsAgent();
		fda.setIdAgent(9008765);
		fda.setIdServiceADS(14);
		fda.setLibelleService("service");
		final Droit droit = new Droit();
		droit.getAgents().add(fda);
		fda.getDroits().add(droit);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateur(777)).thenReturn(droit);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(9008765);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		service.setAgentsToApprove(777, agsDto);

		// Then
		// see callback for persisEntity
		assertEquals(fda, droit.getAgents().iterator().next());
		assertEquals(droit, fda.getDroits().iterator().next());

		Mockito.verify(arRepo, Mockito.times(0)).persisEntity(Mockito.any(DroitsAgent.class));
		Mockito.verify(arRepo, Mockito.times(0)).persisEntity(Mockito.any(Droit.class));
	}

	@Test
	public void setAgentsToApprove_1RemovedAgent_RemoveFromDB() {

		// Given
		List<AgentDto> agsDto = new ArrayList<AgentDto>();

		DroitsAgent fda = Mockito.spy(new DroitsAgent());
		fda.setIdAgent(9008765);
		fda.setIdServiceADS(14);
		fda.setLibelleService("service");

		Droit droit = new Droit();
		droit.getAgents().add(fda);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateur(777)).thenReturn(droit);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(arRepo).removeEntity(Mockito.any(Droit.class));

		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setIdAgent(9008765);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		// When
		service.setAgentsToApprove(777, agsDto);

		// Then
		assertEquals(0, fda.getDroits().size());
		Mockito.verify(arRepo, Mockito.times(1)).removeEntity(Mockito.isA(DroitsAgent.class));
	}

	@Test
	public void getDelegatorAndOperators_NoAgents_ReturnEmptyDto() {

		// Given
		Integer idAgent = 9008765;

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(null);

		IAgentMatriculeConverterService matrService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9009999)).thenReturn(9999);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matrService);

		// When
		DelegatorAndOperatorsDto result = service.getDelegatorAndOperators(idAgent);

		// Then
		assertNull(result.getDelegataire());
		assertEquals(0, result.getSaisisseurs().size());
	}

	@Test
	public void getDelegatorAndOperators_1Delegataire_ReturnDto() {

		// Given
		Integer idAgent = 9008765;

		AgentGeneriqueDto delegataire = new AgentGeneriqueDto();
		delegataire.setIdAgent(9008778);

		Droit droit = new Droit();
		droit.setIdAgentDelegataire(delegataire.getIdAgent());

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droit);

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(mRepo.getAgent(9008778)).thenReturn(delegataire);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);

		// When
		DelegatorAndOperatorsDto result = service.getDelegatorAndOperators(idAgent);

		// Then
		assertEquals(9008778, (int) result.getDelegataire().getIdAgent());
		assertEquals(0, result.getSaisisseurs().size());
	}

	@Test
	public void getDelegatorAndOperators_1Operateur_ReturnDto() {

		// Given
		Integer idAgent = 9008765;

		AgentGeneriqueDto ope = new AgentGeneriqueDto();
		ope.setIdAgent(9008778);

		Droit opeDroit = new Droit();
		opeDroit.setIdAgent(9008778);
		Droit droit = new Droit();
		droit.getOperateurs().add(opeDroit);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droit);

		ISirhWSConsumer mRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(mRepo.getAgent(9008778)).thenReturn(ope);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", mRepo);

		// When
		DelegatorAndOperatorsDto result = service.getDelegatorAndOperators(idAgent);

		// Then
		assertNull(result.getDelegataire());
		assertEquals(1, result.getSaisisseurs().size());
		assertEquals(9008778, (int) result.getSaisisseurs().get(0).getIdAgent());
	}

	@Test
	public void setDelegatorAndOperators_NewDelegataire_SaveIt() {

		// Given
		Integer idAgent = 9008765;

		Droit droitAppro = new Droit();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		dto.setDelegataire(new AgentDto());
		dto.getDelegataire().setIdAgent(9009999);

		// When
		ReturnMessageDto result = service.setDelegatorAndOperators(idAgent, dto);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals(9009999, (int) droitAppro.getIdAgentDelegataire());
	}

	@Test
	public void setDelegatorAndOperators_NewDelegataireIsAlreadyOperateur_ReturnErrorMessage() {

		// Given
		Integer idAgent = 9008765;

		Droit droitAppro = new Droit();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);
		Mockito.when(arRepo.isUserOperator(9009999)).thenReturn(true);

		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9009999);
		ag.setNomUsage("NOM");
		ag.setPrenomUsage("PRENOM");

		ISirhWSConsumer sRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sRepo.getAgent(9009999)).thenReturn(ag);

		IAgentMatriculeConverterService matrService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9009999)).thenReturn(9999);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", sRepo);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matrService);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		dto.setDelegataire(new AgentDto());
		dto.getDelegataire().setIdAgent(9009999);

		// When
		ReturnMessageDto result = service.setDelegatorAndOperators(idAgent, dto);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals(9009999, droitAppro.getIdAgentDelegataire().intValue());
	}

	@Test
	public void setDelegatorAndOperators_NewOperatorIsAlreadyDelegataire_ReturnNoErrorMessage() {

		// Given
		Integer idAgent = 9008765;
		Date currentDate = new Date();
		Droit droitAppro = new Droit();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);
		Mockito.when(arRepo.isUserApprobatorOrDelegataire(9009999)).thenReturn(true);

		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9009999);
		ag.setNomUsage("NOM");
		ag.setPrenomUsage("PRENOM");
		ISirhWSConsumer sRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sRepo.getAgent(9009999)).thenReturn(ag);

		IAgentMatriculeConverterService matrService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9009999)).thenReturn(9999);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", sRepo);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matrService);
		ReflectionTestUtils.setField(service, "helperService", hS);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		dto.getSaisisseurs().add(new AgentDto());
		dto.getSaisisseurs().get(0).setIdAgent(9009999);

		// When
		ReturnMessageDto result = service.setDelegatorAndOperators(idAgent, dto);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertNull(droitAppro.getIdAgentDelegataire());
	}

	@Test
	public void setDelegatorAndOperators_SameExistingDelegataire_DoNothing() {

		// Given
		Integer idAgent = 9008765;

		Droit droitAppro = new Droit();
		droitAppro.setIdAgentDelegataire(9009999);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		dto.setDelegataire(new AgentDto());
		dto.getDelegataire().setIdAgent(9009999);

		// When
		ReturnMessageDto result = service.setDelegatorAndOperators(idAgent, dto);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals(9009999, (int) droitAppro.getIdAgentDelegataire());
	}

	@Test
	public void setDelegatorAndOperators_MissingDelegataire_RemoveIt() {

		// Given
		Integer idAgent = 9008765;

		Droit droitAppro = new Droit();
		droitAppro.setIdAgentDelegataire(9009999);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();

		// When
		ReturnMessageDto result = service.setDelegatorAndOperators(idAgent, dto);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertNull(droitAppro.getIdAgentDelegataire());

	}

	@Test
	public void setDelegatorAndOperators_NewOperateur_SaveIt() {

		// Given
		Integer idAgent = 9008765;
		Date currentDate = new Date();
		Droit droitAppro = new Droit();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		AgentDto ope1 = new AgentDto();
		ope1.setIdAgent(9009999);
		dto.getSaisisseurs().add(ope1);

		// When
		ReturnMessageDto result = service.setDelegatorAndOperators(idAgent, dto);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		Droit operateur = droitAppro.getOperateurs().iterator().next();
		assertEquals(9009999, (int) operateur.getIdAgent());
		assertEquals(currentDate, operateur.getDateModification());
		assertNull(operateur.getIdAgentDelegataire());
		assertEquals(0, operateur.getOperateurs().size());
		assertEquals(9009999, (int) operateur.getIdAgent());
	}

	@Test
	public void setDelegatorAndOperators_ExistingOperateur_DoNothing() {

		// Given
		Integer idAgent = 9008765;
		Date previousDate = new Date();
		Droit droitAppro = new Droit();
		Droit droitOpe = new Droit();
		droitOpe.setIdAgent(9009999);
		droitOpe.setDateModification(previousDate);
		droitAppro.getOperateurs().add(droitOpe);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		AgentDto ope1 = new AgentDto();
		ope1.setIdAgent(9009999);
		dto.getSaisisseurs().add(ope1);

		// When
		ReturnMessageDto result = service.setDelegatorAndOperators(idAgent, dto);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		Droit operateur = droitAppro.getOperateurs().iterator().next();
		assertEquals(9009999, (int) operateur.getIdAgent());
		assertEquals(previousDate, operateur.getDateModification());
		assertNull(operateur.getIdAgentDelegataire());
		assertEquals(0, operateur.getOperateurs().size());
		assertEquals(9009999, (int) operateur.getIdAgent());
	}

	@Test
	public void setDelegatorAndOperators_MissingOperateur_DeleteIt() {

		// Given
		Integer idAgent = 9008765;
		Date previousDate = new Date();
		Droit droitAppro = new Droit();

		Droit droitOpe = Mockito.spy(new Droit());
		droitOpe.setIdAgent(9009999);
		droitOpe.setDateModification(previousDate);
		droitAppro.getOperateurs().add(droitOpe);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(arRepo).removeEntity(Mockito.any(Droit.class));

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();

		// When
		ReturnMessageDto result = service.setDelegatorAndOperators(idAgent, dto);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals(0, droitAppro.getOperateurs().size());
		Mockito.verify(arRepo, Mockito.times(1)).removeEntity(Mockito.isA(Droit.class));
	}

	@Test
	public void setAgentsToInput_OpeIsNotInApproOpeList_ThrowException() {

		// Given
		Integer idAgentApprobateur = 9009999;
		Integer idAgentOperateur = 9008888;

		Droit droitAppro = new Droit();
		DroitsAgent da1 = new DroitsAgent();
		da1.setIdAgent(9001111);
		da1.getDroits().add(droitAppro);
		droitAppro.getAgents().add(da1);
		Droit droitOpe = new Droit();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateur(idAgentApprobateur)).thenReturn(droitAppro);
		Mockito.when(arRepo.getAgentDroitApprobateurOrOperateurFetchAgents(idAgentOperateur, droitAppro.getIdDroit()))
				.thenReturn(droitOpe);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		// When
		try {
			service.setAgentsToInput(idAgentApprobateur, idAgentOperateur, null);
		} catch (AccessForbiddenException ex) {
			return;
		}

		fail("This test should have thrown a AccessForbiddenException");
	}

	@Test
	public void setAgentsToInput_newAgentInApproList_linkIt() {

		// Given
		Integer idAgentApprobateur = 9009999;
		Integer idAgentOperateur = 9008888;

		Droit droitAppro = new Droit();
		DroitsAgent da1 = new DroitsAgent();
		da1.setIdAgent(9001111);
		da1.getDroits().add(droitAppro);
		droitAppro.getAgents().add(da1);
		Droit droitOpe = new Droit();
		droitAppro.getOperateurs().add(droitOpe);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateur(idAgentApprobateur)).thenReturn(droitAppro);
		Mockito.when(arRepo.getAgentDroitApprobateurOrOperateurFetchAgents(idAgentOperateur, droitAppro.getIdDroit()))
				.thenReturn(droitOpe);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		List<AgentDto> dtos = new ArrayList<AgentDto>();
		dtos.add(new AgentDto());
		dtos.get(0).setIdAgent(9001111);

		// When
		service.setAgentsToInput(idAgentApprobateur, idAgentOperateur, dtos);

		// Then
		assertEquals(2, da1.getDroits().size());
	}

	@Test
	public void setAgentsToInput_newAgentNotInApproList_dontLinkIt() {

		// Given
		Integer idAgentApprobateur = 9009999;
		Integer idAgentOperateur = 9008888;

		Droit droitAppro = new Droit();
		Droit droitOpe = new Droit();
		droitAppro.getOperateurs().add(droitOpe);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateur(idAgentApprobateur)).thenReturn(droitAppro);
		Mockito.when(arRepo.getAgentDroitApprobateurOrOperateurFetchAgents(idAgentOperateur, droitAppro.getIdDroit()))
				.thenReturn(droitOpe);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		List<AgentDto> dtos = new ArrayList<AgentDto>();
		dtos.add(new AgentDto());
		dtos.get(0).setIdAgent(9001111);

		// When
		service.setAgentsToInput(idAgentApprobateur, idAgentOperateur, dtos);

		// Then
		assertEquals(0, droitAppro.getAgents().size());
		assertEquals(0, droitOpe.getAgents().size());
	}

	@Test
	public void setAgentsToInput_missingAgentInDto_unlinkIt() {

		// Given
		Integer idAgentApprobateur = 9009999;
		Integer idAgentOperateur = 9008888;

		Droit droitAppro = new Droit();
		DroitsAgent da1 = new DroitsAgent();
		da1.setIdAgent(9001111);
		da1.getDroits().add(droitAppro);
		droitAppro.getAgents().add(da1);
		Droit droitOpe = new Droit();
		droitAppro.getOperateurs().add(droitOpe);
		da1.getDroits().add(droitOpe);
		droitOpe.getAgents().add(da1);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateur(idAgentApprobateur)).thenReturn(droitAppro);
		Mockito.when(arRepo.getAgentDroitApprobateurOrOperateurFetchAgents(idAgentOperateur, droitAppro.getIdDroit()))
				.thenReturn(droitOpe);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		List<AgentDto> dtos = new ArrayList<AgentDto>();

		// When
		service.setAgentsToInput(idAgentApprobateur, idAgentOperateur, dtos);

		// Then
		assertEquals(1, da1.getDroits().size());
	}

	@Test
	public void getAgentsServicesToApproveOrInput_2agents_return2Dtos() {

		// Given
		DroitsAgent d1 = new DroitsAgent();
		d1.setIdServiceADS(16);
		d1.setLibelleService("Service 1 old");
		DroitsAgent d2 = new DroitsAgent();
		d2.setIdServiceADS(25);
		d2.setLibelleService("Service 2 old");
		

		EntiteDto entiteDto2 = new EntiteDto();
		entiteDto2.setIdEntite(25);
		entiteDto2.setLabel("Service 2");
		entiteDto2.setIdStatut(1);
		

		EntiteDto entiteDto = new EntiteDto();
		entiteDto.setIdEntite(16);
		entiteDto.setLabel("Service 1");
		entiteDto.setIdStatut(0);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9008888)).thenReturn(Arrays.asList(d1, d2));

		IAdsWSConsumer adsWsConsumer = Mockito.mock(IAdsWSConsumer.class);
		Mockito.when(adsWsConsumer.getEntiteByIdEntite(16)).thenReturn(entiteDto);
		Mockito.when(adsWsConsumer.getEntiteByIdEntite(25)).thenReturn(entiteDto2);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "adsWsConsumer", adsWsConsumer);

		// When
		List<EntiteDto> result = service.getAgentsServicesToApproveOrInput(9008888);

		// Then
		assertEquals(2, result.size());
		assertEquals(d1.getIdServiceADS(), result.get(0).getIdEntite());
		assertEquals("Service 1(Prévision)", result.get(0).getLabel());
		assertEquals(d2.getIdServiceADS(), result.get(1).getIdEntite());
		assertEquals("Service 2", result.get(1).getLabel());
	}

	@Test
	public void getAgentsServicesToApproveOrInput_2agentsSameService_return1Dtos() {

		// Given
		DroitsAgent d1 = new DroitsAgent();
		d1.setIdServiceADS(33);
		d1.setLibelleService("Service 1");
		DroitsAgent d2 = new DroitsAgent();
		d2.setIdServiceADS(33);
		d2.setLibelleService("Service 1");
		
		EntiteDto entiteDto = new EntiteDto();
		entiteDto.setIdEntite(33);
		entiteDto.setLabel("TEST");
		entiteDto.setIdStatut(1);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(9008888)).thenReturn(Arrays.asList(d1, d2));

		IAdsWSConsumer adsWsConsumer = Mockito.mock(IAdsWSConsumer.class);
		Mockito.when(adsWsConsumer.getEntiteByIdEntite(d1.getIdServiceADS())).thenReturn(entiteDto);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "adsWsConsumer", adsWsConsumer);

		// When
		List<EntiteDto> result = service.getAgentsServicesToApproveOrInput(9008888);

		// Then
		assertEquals(1, result.size());
		assertEquals(d1.getIdServiceADS(), result.get(0).getIdEntite());
		assertEquals(entiteDto.getLabel(), result.get(0).getLabel());
	}

	@Test
	public void setDelegator_NewDelegataire_SaveIt() {

		// Given
		Integer idAgent = 9008765;
		ReturnMessageDto result = new ReturnMessageDto();

		Droit droitAppro = new Droit();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		dto.setDelegataire(new AgentDto());
		dto.getDelegataire().setIdAgent(9009999);

		// When
		result = service.setDelegator(idAgent, dto, result);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals(9009999, (int) droitAppro.getIdAgentDelegataire());
	}

	@Test
	public void setDelegator_NewDelegataireIsAlreadyOperateur_ReturnNoErrorMessage() {

		// Given
		Integer idAgent = 9008765;
		ReturnMessageDto result = new ReturnMessageDto();

		Droit droitAppro = new Droit();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);
		Mockito.when(arRepo.isUserOperator(9009999)).thenReturn(true);

		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9009999);
		ag.setNomUsage("NOM");
		ag.setPrenomUsage("PRENOM");

		ISirhWSConsumer sRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sRepo.getAgent(9009999)).thenReturn(ag);

		IAgentMatriculeConverterService matrService = Mockito.mock(IAgentMatriculeConverterService.class);
		Mockito.when(matrService.tryConvertIdAgentToNomatr(9009999)).thenReturn(9999);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", sRepo);
		ReflectionTestUtils.setField(service, "matriculeConvertor", matrService);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		dto.setDelegataire(new AgentDto());
		dto.getDelegataire().setIdAgent(9009999);

		// When
		result = service.setDelegator(idAgent, dto, result);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals(9009999, droitAppro.getIdAgentDelegataire().intValue());
	}

	@Test
	public void setDelegator_SameExistingDelegataire_DoNothing() {

		// Given
		Integer idAgent = 9008765;
		ReturnMessageDto result = new ReturnMessageDto();

		Droit droitAppro = new Droit();
		droitAppro.setIdAgentDelegataire(9009999);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();
		dto.setDelegataire(new AgentDto());
		dto.getDelegataire().setIdAgent(9009999);

		// When
		result = service.setDelegator(idAgent, dto, result);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals(9009999, (int) droitAppro.getIdAgentDelegataire());
	}

	@Test
	public void setDelegator_MissingDelegataire_RemoveIt() {

		// Given
		Integer idAgent = 9008765;
		ReturnMessageDto result = new ReturnMessageDto();

		Droit droitAppro = new Droit();
		droitAppro.setIdAgentDelegataire(9009999);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getApprobateurFetchOperateurs(idAgent)).thenReturn(droitAppro);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		DelegatorAndOperatorsDto dto = new DelegatorAndOperatorsDto();

		// When
		result = service.setDelegator(idAgent, dto, result);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertNull(droitAppro.getIdAgentDelegataire());

	}

	@Test
	public void isUserApprobateur_ReturnTrue() {

		// Given
		Integer idAgent = 906543;
		Droit d2 = new Droit();
		d2.setApprobateur(true);
		Droit d1 = new Droit();
		d1.setApprobateur(false);
		List<Droit> droits = new ArrayList<Droit>();
		droits.add(d1);
		droits.add(d2);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentAccessRights(idAgent)).thenReturn(droits);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		// When
		boolean result = service.isUserApprobateur(idAgent);

		// Then
		assertTrue(result);
	}

	@Test
	public void isUserApprobateur_ReturnFalse() {

		// Given
		Integer idAgent = 906543;
		List<Droit> droits = new ArrayList<Droit>();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentAccessRights(idAgent)).thenReturn(droits);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		// When
		boolean result = service.isUserApprobateur(idAgent);

		// Then
		assertFalse(result);
	}

	@Test
	public void isUserOperateur_ReturnTrue() {

		// Given
		Integer idAgent = 906543;
		Droit d2 = new Droit();
		d2.setApprobateur(true);
		Droit d1 = new Droit();
		d1.setOperateur(true);
		List<Droit> droits = new ArrayList<Droit>();
		droits.add(d1);
		droits.add(d2);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentAccessRights(idAgent)).thenReturn(droits);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		// When
		boolean result = service.isUserOperateur(idAgent);

		// Then
		assertTrue(result);
	}

	@Test
	public void isUserOperateur_ReturnFalse() {

		// Given
		Integer idAgent = 906543;
		List<Droit> droits = new ArrayList<Droit>();

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAgentAccessRights(idAgent)).thenReturn(droits);

		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);

		// When
		boolean result = service.isUserOperateur(idAgent);

		// Then
		assertFalse(result);
	}
}
