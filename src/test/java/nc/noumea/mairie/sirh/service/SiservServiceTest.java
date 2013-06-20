package nc.noumea.mairie.sirh.service;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nc.noumea.mairie.domain.Siserv;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.sirh.domain.FichePoste;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class SiservServiceTest {

	@Test
	public void getAgentService_retrieveCurrentAffectation_returnService() {
		
		// Given
		int idAgent = 9007754;
		
		FichePoste fp = new FichePoste();
		fp.setCodeService("SERVICE");
		
		Siserv siserv = new Siserv();
		siserv.setServi("SERVICE");
		siserv.setLiServ("LIB SERVICE");
		
		@SuppressWarnings("unchecked")
		TypedQuery<FichePoste> mockQ = Mockito.mock(TypedQuery.class);
		Mockito.when(mockQ.getResultList()).thenReturn(Arrays.asList(fp));
		
		EntityManager emMock = Mockito.mock(EntityManager.class);
		Mockito.when(emMock.createNamedQuery("getCurrentAffectation", FichePoste.class)).thenReturn(mockQ);
		Mockito.when(emMock.find(Siserv.class, "SERVICE")).thenReturn(siserv);
		
		HelperService hSMock = Mockito.mock(HelperService.class);
		Mockito.when(hSMock.getCurrentDate()).thenReturn(new DateTime(2012, 04, 19, 0, 0, 0).toDate());
		
		SiservService service = new SiservService();
		ReflectionTestUtils.setField(service, "sirhEntityManager", emMock);
		ReflectionTestUtils.setField(service, "helperService", hSMock);
		
		// When
		Siserv actual = service.getAgentService(idAgent);
		
		// Then
		assertEquals(siserv, actual);
		
		Mockito.verify(mockQ).setParameter("today", new DateTime(2012, 04, 19, 0, 0, 0).toDate());
		Mockito.verify(mockQ).setParameter("idAgent", idAgent);
	}
}
