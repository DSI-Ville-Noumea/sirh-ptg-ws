package nc.noumea.mairie.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SpBaseTest {

	@Test
	public void getDayBaseInMinutes_7h30_ComputeMinutesFrommairieFormat() {
		
		// Given
		Spbase base = new Spbase();
		base.setNbahlu(7.3d);
		
		// Then
		assertEquals(7 * 60 + 30, base.getDayBaseInMinutes(0));
	}
	
	@Test
	public void getDayBaseInMinutes_7h45_ComputeMinutesFrommairieFormat() {
		
		// Given
		Spbase base = new Spbase();
		base.setNbahlu(7.45d);
		
		// Then
		assertEquals(7 * 60 + 45, base.getDayBaseInMinutes(0));
	}
}
