package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;
import nc.noumea.mairie.ws.SirhWSUtils;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.staticmock.MockStaticEntityMethods;
import org.springframework.test.util.ReflectionTestUtils;

@MockStaticEntityMethods
public class FichesServiceTest {

	@Test
	public void listAgentsFichesToPrint() {

		// Given
		Integer idAgent = 906543;
		Date dateJour = new Date();

		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9005138);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005138);
		
		AgentWithServiceDto agDtoServ = new AgentWithServiceDto();
		agDtoServ.setIdAgent(9005138);
		agDtoServ.setIdServiceADS(17);

		List<AgentWithServiceDto> listAgentsServiceDto = new ArrayList<AgentWithServiceDto>();
		listAgentsServiceDto.add(agDtoServ);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent)).thenReturn(Arrays.asList(da));

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgent(9005138)).thenReturn(ag);
		Mockito.when(sirhRepo.getListAgentsWithService(Arrays.asList(9005138),dateJour)).thenReturn(Arrays.asList(agDtoServ));

		SirhWSUtils sirhWSUtils = Mockito.mock(SirhWSUtils.class);
		Mockito.when(sirhWSUtils.getAgentOfListAgentWithServiceDto(listAgentsServiceDto, da.getIdAgent())).thenReturn(agDtoServ);

		FichesService service = new FichesService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);
		ReflectionTestUtils.setField(service, "sirhWSUtils", sirhWSUtils);
		
		

		// When
		List<AgentDto> result = service.listAgentsFichesToPrint(idAgent, 17,dateJour);

		// Then
		assertEquals(ag.getIdAgent(), result.get(0).getIdAgent());
		assertEquals(1, result.size());
	}

	// #15193 bug avec des agents en doublon
	// car un operateur peut etre operateur de plusieurs approbateurs 
	// approuvant des memes agents
	@Test
	public void listAgentsFichesToPrint_testDoublon() {

		// Given
		Integer idAgent = 906543;
		Date dateJour = new Date();

		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9005138);

		AgentGeneriqueDto ag2 = new AgentGeneriqueDto();
		ag2.setIdAgent(9005140);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005138);

		DroitsAgent da2 = new DroitsAgent();
		da2.setIdAgent(9005140);

		DroitsAgent da3 = new DroitsAgent();
		da3.setIdAgent(9005138);
		
		AgentWithServiceDto agDtoServ2 = new AgentWithServiceDto();
		agDtoServ2.setIdServiceADS(17);
		agDtoServ2.setIdAgent(9005138);
		
		AgentWithServiceDto agDtoServ = new AgentWithServiceDto();
		agDtoServ.setIdServiceADS(17);
		agDtoServ.setIdAgent(9005140);

		List<AgentWithServiceDto> listAgentsServiceDto = new ArrayList<AgentWithServiceDto>();
		listAgentsServiceDto.add(agDtoServ);
		listAgentsServiceDto.add(agDtoServ2);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent)).thenReturn(Arrays.asList(da, da2, da3));

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgent(9005138)).thenReturn(ag);
		Mockito.when(sirhRepo.getAgent(9005140)).thenReturn(ag2);
		Mockito.when(sirhRepo.getListAgentsWithService(Arrays.asList(9005138,9005140),dateJour)).thenReturn(Arrays.asList(agDtoServ,agDtoServ2));

		SirhWSUtils sirhWSUtils = Mockito.mock(SirhWSUtils.class);
		Mockito.when(sirhWSUtils.getAgentOfListAgentWithServiceDto(listAgentsServiceDto, da.getIdAgent())).thenReturn(agDtoServ);
		Mockito.when(sirhWSUtils.getAgentOfListAgentWithServiceDto(listAgentsServiceDto, da2.getIdAgent())).thenReturn(agDtoServ2);
		Mockito.when(sirhWSUtils.getAgentOfListAgentWithServiceDto(listAgentsServiceDto, da3.getIdAgent())).thenReturn(agDtoServ);

		FichesService service = new FichesService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);
		ReflectionTestUtils.setField(service, "sirhWSUtils", sirhWSUtils);

		// When
		List<AgentDto> result = service.listAgentsFichesToPrint(idAgent, 17,dateJour);

		// Then
		assertEquals(2, result.size());
		assertEquals(ag.getIdAgent(), result.get(0).getIdAgent());
		assertEquals(ag2.getIdAgent(), result.get(1).getIdAgent());
	}
}
