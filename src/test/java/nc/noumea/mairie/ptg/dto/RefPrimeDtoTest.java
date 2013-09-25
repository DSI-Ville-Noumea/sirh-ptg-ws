package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;

import org.junit.Test;

public class RefPrimeDtoTest {

	@Test
	public void ctor_withpointage() {
		
		// Given
		RefPrime ref = new RefPrime();
		ref.setCalculee(false);
		ref.setIdRefPrime(654654);
		ref.setNoRubr(321321);
		ref.setDescription("description");
		ref.setLibelle("libelle");
		ref.setTypeSaisie(TypeSaisieEnum.CASE_A_COCHER);
		ref.setStatut(AgentStatutEnum.C);
		ref.setAide("help text");
		
		// When
		RefPrimeDto result = new RefPrimeDto(ref);

		// Then
		assertEquals(result.getTypeSaisie(), ref.getTypeSaisie().name());
		assertEquals(result.getStatut(), ref.getStatut().name());
		assertEquals(result.getIdRefPrime(), ref.getIdRefPrime());
		assertEquals(result.getNumRubrique(), ref.getNoRubr());
		assertEquals(result.getLibelle(), ref.getLibelle());
		assertEquals(result.getDescription(), ref.getDescription());
		assertEquals(result.getAide(), ref.getAide());
	}

}
