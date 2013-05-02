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
import javax.persistence.TypedQuery;

import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.dto.AccessRightsDto;
import nc.noumea.mairie.ptg.dto.DelegatorAndInputtersDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.FichePoste;
import nc.noumea.mairie.sirh.domain.Siserv;

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
		
		FichePoste fp = new FichePoste();
		fp.setCodeService("SERVICE");
		
		Siserv siserv = new Siserv();
		siserv.setServi("SERVICE");
		siserv.setLiServ("LIB SERVICE");
		
		List<DroitsAgent> droits = new ArrayList<DroitsAgent>();
		
		@SuppressWarnings("unchecked")
		TypedQuery<FichePoste> mockQ = Mockito.mock(TypedQuery.class);
		Mockito.when(mockQ.getResultList()).thenReturn(Arrays.asList(fp));
		
		EntityManager emMock = Mockito.mock(EntityManager.class);
		Mockito.when(emMock.createNamedQuery("getCurrentAffectation", FichePoste.class)).thenReturn(mockQ);
		Mockito.when(emMock.find(Siserv.class, "SERVICE")).thenReturn(siserv);
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAllDroitsForService(fp.getCodeService())).thenReturn(droits);
		
		HelperService hSMock = Mockito.mock(HelperService.class);
		Mockito.when(hSMock.getCurrentDate()).thenReturn(new DateTime(2012, 04, 19, 0, 0, 0).toDate());
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhEntityManager", emMock);
		ReflectionTestUtils.setField(service, "helperService", hSMock);
		
		// When
		DelegatorAndInputtersDto dto = service.getDelegatorAndInputters(idAgent);
		
		// Then
		assertNull(dto.getDelegataire());
		assertEquals(0, dto.getSaisisseurs().size());

		Mockito.verify(mockQ).setParameter("today", new DateTime(2012, 04, 19, 0, 0, 0).toDate());
		Mockito.verify(mockQ).setParameter("idAgent", idAgent);
	}
	
	@Test
	public void getDelegatorAndInputters_1DelegatorAnd1Inputter_returnFilledInDtos() {
		
		// Given
		int idAgent = 9007754;
		
		FichePoste fp = new FichePoste();
		fp.setCodeService("SERVICE");
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
		
		@SuppressWarnings("unchecked")
		TypedQuery<FichePoste> mockQ = Mockito.mock(TypedQuery.class);
		Mockito.when(mockQ.getResultList()).thenReturn(Arrays.asList(fp));
		
		EntityManager emMock = Mockito.mock(EntityManager.class);
		Mockito.when(emMock.createNamedQuery("getCurrentAffectation", FichePoste.class)).thenReturn(mockQ);
		Mockito.when(emMock.find(Siserv.class, "SERVICE")).thenReturn(siserv);
		Agent ag9007654 = new Agent();
		ag9007654.setIdAgent(9007654);
		Mockito.when(emMock.find(Agent.class, 9007654)).thenReturn(ag9007654);
		Agent ag9007655 = new Agent();
		ag9007655.setIdAgent(9007655);
		Mockito.when(emMock.find(Agent.class, 9007655)).thenReturn(ag9007655);
		
		IAccessRightsRepository arRepo = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(arRepo.getAllDroitsForService(fp.getCodeService())).thenReturn(droits);
		
		HelperService hSMock = Mockito.mock(HelperService.class);
		Mockito.when(hSMock.getCurrentDate()).thenReturn(new DateTime(2012, 04, 19, 0, 0, 0).toDate());
		
		AccessRightsService service = new AccessRightsService();
		ReflectionTestUtils.setField(service, "accessRightsRepository", arRepo);
		ReflectionTestUtils.setField(service, "sirhEntityManager", emMock);
		ReflectionTestUtils.setField(service, "helperService", hSMock);
		
		// When
		DelegatorAndInputtersDto dto = service.getDelegatorAndInputters(idAgent);
		
		// Then
		assertNotNull(dto.getDelegataire());
		assertEquals(9007654, dto.getDelegataire().getIdAgent());
		assertEquals(1, dto.getSaisisseurs().size());
		assertEquals(9007655, dto.getSaisisseurs().get(0).getIdAgent());
		
		Mockito.verify(mockQ).setParameter("today", new DateTime(2012, 04, 19, 0, 0, 0).toDate());
		Mockito.verify(mockQ).setParameter("idAgent", idAgent);
	}
}
