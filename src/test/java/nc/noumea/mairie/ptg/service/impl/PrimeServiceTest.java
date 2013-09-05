package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IPrimeService;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class PrimeServiceTest {

	@Test
	public void getPrimeListForAgent() {
		// Given
		List<RefPrime> result = Mockito.mock(IPointageRepository.class).getRefPrimesListForAgent(AgentStatutEnum.F);
		List<RefPrimeDto> result2 = Mockito.mock(IPrimeService.class).getPrimeListForAgent(AgentStatutEnum.F);

		// Then
		testList(result, result2);
	}

	@Test
	public void getPrimeList() {
		// Given
		List<RefPrime> result = Mockito.mock(IPointageRepository.class).getRefPrimesList();
		List<RefPrimeDto> result2 = Mockito.mock(IPrimeService.class).getPrimeList();

		// Then
		testList(result, result2);
	}

	@Test
	public void getPrime() {
		// Given
		Integer noRubr = 7710;
		RefPrime p1 = new RefPrime();
		p1.setNoRubr(7710);
		p1.setLibelle("TEST");
		RefPrime p2 = new RefPrime();
		p2.setNoRubr(7710);
		p2.setLibelle("TEST");
		List<RefPrime> listeRefPrime = Arrays.asList(p1, p2);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getRefPrimesListWithNoRubr(noRubr)).thenReturn(listeRefPrime);

		PrimeService service = new PrimeService();
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);

		// When
		RefPrimeDto dto = service.getPrime(noRubr);

		// Then
		assertEquals("TEST", dto.getLibelle());
	}

	private void testList(List<RefPrime> list1, List<RefPrimeDto> list2) {
		assertEquals(list1.size(), list2.size());
	}

	@Test
	public void getPrimes() {
		// Given
		Integer noRubr = 7710;
		RefPrime p1 = new RefPrime();
		p1.setNoRubr(7710);
		p1.setLibelle("TEST");
		RefPrime p2 = new RefPrime();
		p2.setNoRubr(7710);
		p2.setLibelle("TEST");
		List<RefPrime> listeRefPrime = Arrays.asList(p1, p2);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getRefPrimesListWithNoRubr(noRubr)).thenReturn(listeRefPrime);

		PrimeService service = new PrimeService();
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);

		// When
		List<RefPrimeDto> dto = service.getPrimes(noRubr);

		// Then
		assertEquals("TEST", dto.get(0).getLibelle());
	}

	@Test
	public void getPrimeWithIdRefPrime() {
		// Given
		Integer idRefPrime = 123;
		RefPrime p1 = new RefPrime();
		p1.setIdRefPrime(idRefPrime);
		p1.setLibelle("TEST");
		RefPrime p2 = new RefPrime();
		p2.setIdRefPrime(idRefPrime);
		p2.setLibelle("TEST");
		List<RefPrime> listeRefPrime = Arrays.asList(p1, p2);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getRefPrimesListWithNoRubr(idRefPrime)).thenReturn(listeRefPrime);

		PrimeService service = new PrimeService();
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);

		// When
		List<RefPrimeDto> dto = service.getPrimes(idRefPrime);

		// Then
		assertEquals("TEST", dto.get(0).getLibelle());
	}

	@Test
	public void getPrimesId() {
		// Given
		RefPrimeDto p1 = new RefPrimeDto();
		p1.setLibelle("TEST");
		RefPrimeDto p2 = new RefPrimeDto();
		p2.setLibelle("TEST2");
		List<RefPrimeDto> listeRefPrime = Arrays.asList(p1, p2);

		IPrimeService arRepo = Mockito.mock(IPrimeService.class);
		Mockito.when(arRepo.getPrimeList()).thenReturn(listeRefPrime);

		// When
		List<RefPrimeDto> dto = arRepo.getPrimeList();

		// Then
		assertEquals("TEST", dto.get(0).getLibelle());
	}

}
