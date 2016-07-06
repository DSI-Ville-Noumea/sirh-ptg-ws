package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class JourPointageDtoTest {

	@Test
	public void ctor_withNothing() {

		// Given

		// When
		JourPointageDto result = new JourPointageDto();

		// Then
		assertEquals(0, result.getAbsences().size());
		assertEquals(0, result.getHeuresSup().size());
		assertEquals(0, result.getPrimes().size());
	}

	@Test
	public void ctor_withJourPointageDto() {
		
		AbsenceDto abs = new AbsenceDto();
		abs.setCommentaire("toto");
		List<AbsenceDto> listAbs = new ArrayList<AbsenceDto>();
		listAbs.add(abs);
		HeureSupDto hsup = new HeureSupDto();
		hsup.setCommentaire("toto");
		List<HeureSupDto> listHSup = new ArrayList<HeureSupDto>();
		listHSup.add(hsup);
		PrimeDto pri = new PrimeDto();
		pri.setCommentaire("toto");
		List<PrimeDto> listPri = new ArrayList<PrimeDto>();
		listPri.add(pri);
		// Given
		JourPointageDto jour = new JourPointageDto();
		jour.setAbsences(listAbs);
		jour.setHeuresSup(listHSup);
		jour.setPrimes(listPri);

		// When
		JourPointageDto result = new JourPointageDto(jour);

		// Then
		assertEquals(0, result.getAbsences().size());
		assertEquals(0, result.getHeuresSup().size());
		assertEquals(1, result.getPrimes().size());
		assertEquals("toto", result.getPrimes().get(0).getCommentaire());
	}
}
