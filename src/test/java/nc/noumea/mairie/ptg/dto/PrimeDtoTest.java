package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;

import org.junit.Test;

public class PrimeDtoTest {

	@Test
	public void ctor_withpointage() {

		// Given
		RefPrime ref = new RefPrime();
		ref.setCalculee(false);
		ref.setDescription("description");
		ref.setLibelle("libelle");
		ref.setNoRubr(12);
		ref.setStatut(AgentStatutEnum.F);
		ref.setTypeSaisie(TypeSaisieEnum.CASE_A_COCHER);

		// When
		PrimeDto result = new PrimeDto(ref);

		// Then
		assertEquals(ref.getLibelle(), result.getTitre());
	}
}
