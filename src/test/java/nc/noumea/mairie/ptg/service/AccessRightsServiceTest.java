package nc.noumea.mairie.ptg.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.domain.DroitsProfil;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class AccessRightsServiceTest {

	@Test
	public void getAgentAccessRights_AgentHasNoRights_ReturnFalseEverywhere() {
		
		// Given
		Integer idAgent = 906543;
		List<DroitsAgent> droits = new ArrayList<DroitsAgent>();
		
		@SuppressWarnings("unchecked")
		TypedQuery<DroitsAgent> mockQ = Mockito.mock(TypedQuery.class);
		Mockito.when(mockQ.getResultList()).thenReturn(droits);
		
		EntityManager emMock = Mockito.mock(EntityManager.class);
		Mockito.when(emMock.createNamedQuery("getAgentAccessRights", DroitsAgent.class)).thenReturn(mockQ);
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "ptgEntityManager", emMock);
		
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
		da.setProfil(new DroitsProfil());
		da.getProfil().setIsSaisie(true);
		da.getProfil().setIsEdition(true);
		da.getProfil().setIsVisualisation(true);
		da.getProfil().setIsApprobation(true);
		da.getProfil().setIsGrantor(true);
		List<DroitsAgent> droits = Arrays.asList(da);
		
		@SuppressWarnings("unchecked")
		TypedQuery<DroitsAgent> mockQ = Mockito.mock(TypedQuery.class);
		Mockito.when(mockQ.getResultList()).thenReturn(droits);
		
		EntityManager emMock = Mockito.mock(EntityManager.class);
		Mockito.when(emMock.createNamedQuery("getAgentAccessRights", DroitsAgent.class)).thenReturn(mockQ);
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "ptgEntityManager", emMock);
		
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
		da.setProfil(new DroitsProfil());
		da.getProfil().setIsSaisie(false);
		da.getProfil().setIsEdition(true);
		da.getProfil().setIsVisualisation(false);
		da.getProfil().setIsApprobation(true);
		da.getProfil().setIsGrantor(false);
		DroitsAgent da2 = new DroitsAgent();
		da2.setProfil(new DroitsProfil());
		da2.getProfil().setIsSaisie(true);
		da2.getProfil().setIsEdition(false);
		da2.getProfil().setIsVisualisation(true);
		da2.getProfil().setIsApprobation(false);
		da2.getProfil().setIsGrantor(false);
		List<DroitsAgent> droits = Arrays.asList(da, da2);
		
		@SuppressWarnings("unchecked")
		TypedQuery<DroitsAgent> mockQ = Mockito.mock(TypedQuery.class);
		Mockito.when(mockQ.getResultList()).thenReturn(droits);
		
		EntityManager emMock = Mockito.mock(EntityManager.class);
		Mockito.when(emMock.createNamedQuery("getAgentAccessRights", DroitsAgent.class)).thenReturn(mockQ);
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "ptgEntityManager", emMock);
		
		// When
		AccessRightsDto result = service.getAgentAccessRights(idAgent);
		
		// Then
		assertTrue(result.isFiches());
		assertTrue(result.isSaisie());
		assertTrue(result.isVisualisation());
		assertTrue(result.isApprobation());
		assertFalse(result.isGestionDroitsAcces());
	}
}
