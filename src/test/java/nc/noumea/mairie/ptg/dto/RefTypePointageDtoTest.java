package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.RefTypePointage;

import org.junit.Test;

@XmlRootElement
public class RefTypePointageDtoTest {

	@Test
	public void ctor_withRefTypePointage() {

		// Given
		RefTypePointage ref = new RefTypePointage();
		ref.setIdRefTypePointage(12);
		ref.setLabel("test lib");

		// When
		RefTypePointageDto result = new RefTypePointageDto(ref);

		// Then
		assertEquals(ref.getLabel(), result.getLibelle());
		assertEquals(ref.getIdRefTypePointage(), result.getIdRefTypePointage());
	}
}
