package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PtgComment;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;

import org.joda.time.DateTime;
import org.junit.Test;

public class RefPrimeDtoTest {

	@Test
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
