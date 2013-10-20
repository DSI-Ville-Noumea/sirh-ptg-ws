package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.sirh.domain.Agent;

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

		Agent ag = new Agent();
		ag.setIdAgent(9005138);

		DroitsAgent da = new DroitsAgent();
		da.setIdAgent(9005138);

		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getListOfAgentsToInputOrApprove(idAgent, "DEDA")).thenReturn(Arrays.asList(da));

		ISirhRepository sirhRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(sirhRepo.getAgent(9005138)).thenReturn(ag);

		FichesService service = new FichesService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhRepository", sirhRepo);

		// When
		List<AgentDto> result = service.listAgentsFichesToPrint(idAgent, "DEDA");

		// Then
		assertEquals(ag.getIdAgent(), result.get(0).getIdAgent());
		assertEquals(1, result.size());
	}
}
