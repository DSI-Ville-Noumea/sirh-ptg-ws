package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.RefEtat;

import org.junit.Test;

@XmlRootElement
public class RefEtatDtoTest {

	@Test
	public void ctor_withRefEtat() {

		// Given
		RefEtat ref = new RefEtat();
		ref.setIdRefEtat(12);
		ref.setLabel("test lib");

		// When
		RefEtatDto result = new RefEtatDto(ref);

		// Then
		assertEquals(ref.getLabel(), result.getLibelle());
		assertEquals(ref.getIdRefEtat(), result.getIdRefEtat());
	}
}
