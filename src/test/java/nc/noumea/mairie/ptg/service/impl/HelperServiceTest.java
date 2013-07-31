package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;

public class HelperServiceTest {

	@Test
	public void convertMinutesToMairieNbHeuresFormat_HoursOnly() {
		
		HelperService service = new HelperService();
		
		assertEquals(3.0d, service.convertMinutesToMairieNbHeuresFormat(180), 0);
	}
	
	@Test
	public void convertMinutesToMairieNbHeuresFormat_HoursAndMinutes() {
		
		HelperService service = new HelperService();
		
		assertEquals(7.45d, service.convertMinutesToMairieNbHeuresFormat(465), 0);
	}
	
	@Test
	public void convertMairieNbHeuresFormatToMinutes_7h30_ComputeMinutesFrommairieFormat() {
		
		HelperService service = new HelperService();

		assertEquals(7 * 60 + 30, service.convertMairieNbHeuresFormatToMinutes(7.3d));
	}
	
	@Test
	public void convertMairieNbHeuresFormatToMinutes_7h45_ComputeMinutesFrommairieFormat() {
		
		HelperService service = new HelperService();

		assertEquals(7 * 60 + 45, service.convertMairieNbHeuresFormatToMinutes(7.45d));
	}
	
	@Test
	public void getMairieMatrFromIdAgent_9009876_9876() {
		
		HelperService service = new HelperService();

		assertEquals(9876, (int) service.getMairieMatrFromIdAgent(9009876));
	}
	
	@Test
	public void getIntegerDateMairieFromDate_ConvertToIntegerYYYYMMDD() {
		
		HelperService service = new HelperService();

		assertEquals(20120730, (int) service.getIntegerDateMairieFromDate(new DateTime(2012, 7, 30, 9, 8, 54).toDate()));
	}
	
	@Test
	public void getIntegerDateMairieFromDate_dateIsNull_Return0() {
		
		HelperService service = new HelperService();

		assertEquals(0, (int) service.getIntegerDateMairieFromDate(null));
	}
	
	@Test
	public void getDateFromMairieInteger_20120730_ReturnDateObject() {
	
		HelperService service = new HelperService();

		assertEquals(new LocalDate(2012, 7, 30).toDate(), service.getDateFromMairieInteger(20120730));
	}
	
	@Test
	public void getDateFromMairieInteger_null_ReturnNull() {
	
		HelperService service = new HelperService();

		assertNull(service.getDateFromMairieInteger(null));
	}
	
	@Test
	public void getDateFromMairieInteger_0_ReturnNull() {
	
		HelperService service = new HelperService();

		assertNull(service.getDateFromMairieInteger(null));
	}
	
	@Test
	public void isDateAMonday_DateIsMonday_ReturnTrue() {
		HelperService service = new HelperService();

		assertTrue(service.isDateAMonday(new LocalDate(2013, 7, 1).toDate()));
	}
	
	@Test
	public void isDateAMonday_DateIsNotAMonday_ReturnFalse() {
		HelperService service = new HelperService();

		assertFalse(service.isDateAMonday(new LocalDate(2013, 7, 2).toDate()));
	}
	

	@Test
	public void getTypeChainePaieFromStatut_Statut_F_ReturnSHC() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals(TypeChainePaieEnum.SHC, service.getTypeChainePaieFromStatut(AgentStatutEnum.F));
		
	}
	
	@Test
	public void getTypeChainePaieFromStatut_Statut_C_ReturnSHC() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals(TypeChainePaieEnum.SHC, service.getTypeChainePaieFromStatut(AgentStatutEnum.C));
	}
	
	@Test
	public void getTypeChainePaieFromStatut_Statut_CC_ReturnCC() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals(TypeChainePaieEnum.SCV, service.getTypeChainePaieFromStatut(AgentStatutEnum.CC));
	}
}
