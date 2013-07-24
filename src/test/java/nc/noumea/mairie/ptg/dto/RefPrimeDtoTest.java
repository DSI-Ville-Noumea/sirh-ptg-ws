package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.RefPrime;

public class RefPrimeDtoTest {

	//@Test
	public void ctor_withpointage() {
		// Given
		RefPrime ref = new RefPrime();
		ref.setCalculee(false);
		ref.setTypeSaisie(null);

		// When
		RefPrimeDto result = new RefPrimeDto(ref);

		// Then
		assertEquals(result.getTypeSaisie(), ref.getTypeSaisie().name());
		assertEquals(result.getStatut(), ref.getStatut().name());
	}

}
