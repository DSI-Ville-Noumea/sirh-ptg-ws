package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FichePointageDtoTest {

	@Test
	public void ctor_withNothing() {

		// Given

		// When
		FichePointageDto result = new FichePointageDto();

		// Then
		assertEquals(0, result.getSaisies().size());
	}
}
