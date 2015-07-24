package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

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

		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9005138);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005138);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent, 17)).thenReturn(Arrays.asList(da));

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgent(9005138)).thenReturn(ag);

		FichesService service = new FichesService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		List<AgentDto> result = service.listAgentsFichesToPrint(idAgent, 17);

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

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent, 17)).thenReturn(Arrays.asList(da, da2, da3));

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgent(9005138)).thenReturn(ag);
		Mockito.when(sirhRepo.getAgent(9005140)).thenReturn(ag2);

		FichesService service = new FichesService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		List<AgentDto> result = service.listAgentsFichesToPrint(idAgent, 17);

		// Then
		assertEquals(ag.getIdAgent(), result.get(0).getIdAgent());
		assertEquals(ag2.getIdAgent(), result.get(1).getIdAgent());
		assertEquals(2, result.size());
	}
}
