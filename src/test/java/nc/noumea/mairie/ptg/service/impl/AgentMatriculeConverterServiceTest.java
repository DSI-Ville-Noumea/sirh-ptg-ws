package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.service.AgentMatriculeConverterServiceException;

import org.junit.Test;

public class AgentMatriculeConverterServiceTest {

	@Test
	public void testfromADIdAgentToEAEIdAgent_withIdNot5digits_throwException() {

		// Given
		int theIdToConvert = 89;
		AgentMatriculeConverterService service = new AgentMatriculeConverterService();

		try {
			// When
			service.fromADIdAgentToSIRHIdAgent(theIdToConvert);
		} catch (AgentMatriculeConverterServiceException ex) {
			// Then
			assertEquals("Impossible de convertir le matricule '89' en matricule SIRH.", ex.getMessage());
		}

	}

	@Test
	public void testfromADIdAgentToEAEIdAgent_withIdIs5digits_convertItTo6Digits()
			throws AgentMatriculeConverterServiceException {

		// Given
		int theIdToConvert = 906898;
		AgentMatriculeConverterService service = new AgentMatriculeConverterService();

		// When
		int result = service.fromADIdAgentToSIRHIdAgent(theIdToConvert);

		// Then
		assertEquals(9006898, result);
	}

	@Test
	public void testTryConvertIdAgentToNomatr_withIdNot7digits_throwException() {

		// Given
		int theIdToConvert = 89;
		AgentMatriculeConverterService service = new AgentMatriculeConverterService();

		try {
			// When
			service.tryConvertIdAgentToNomatr(theIdToConvert);
		} catch (AgentMatriculeConverterServiceException ex) {
			// Then
			assertEquals("Impossible de convertir l'idAgent '89' en matricule(nomatr) MAIRIE.", ex.getMessage());
		}

	}

	@Test
	public void testTryConvertIdAgentToNomatr_withIdIs7digits_convertItTo4Digits()
			throws AgentMatriculeConverterServiceException {

		// Given
		int theIdToConvert = 9006898;
		AgentMatriculeConverterService service = new AgentMatriculeConverterService();

		// When
		int result = service.tryConvertIdAgentToNomatr(theIdToConvert);

		// Then
		assertEquals(6898, result);
	}
}
