package nc.noumea.mairie.ptg.dto.etatsPayeur;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AbstractItemEtatPayeurDtoTest {

	@Test
	public void AbstractItemEtatPayeurDto_default_ctor() {

		// When
		AbstractItemEtatPayeurDto result = new AbstractItemEtatPayeurDto();

		// Then
		assertEquals(0, result.getPrimes().size());

	}
}
