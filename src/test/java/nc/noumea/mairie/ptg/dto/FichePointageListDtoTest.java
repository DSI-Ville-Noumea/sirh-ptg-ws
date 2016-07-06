package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FichePointageListDtoTest {

	@Test
	public void ctor_withNothing() {

		// Given

		// When
		FichePointageListDto result = new FichePointageListDto();

		// Then
		assertEquals(0, result.getFiches().size());
	}
}
