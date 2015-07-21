package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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

		assertNull(service.getDateFromMairieInteger(0));
	}
	
	@Test
	public void getIntegerMonthDateMairieFromDate_ConvertToIntegerYYYYMM() {
		
		HelperService service = new HelperService();

		assertEquals(201207, (int) service.getIntegerMonthDateMairieFromDate(new DateTime(2012, 7, 30, 9, 8, 54).toDate()));
	}
	
	@Test
	public void getIntegerMonthDateMairieFromDate_dateIsNull_Return0() {
		
		HelperService service = new HelperService();

		assertEquals(0, (int) service.getIntegerMonthDateMairieFromDate(null));
	}
	
	@Test
	public void getMonthDateFromMairieInteger_201207_ReturnDateObject() {
	
		HelperService service = new HelperService();

		assertEquals(new LocalDate(2012, 7, 1).toDate(), service.getMonthDateFromMairieInteger(201207));
	}
	
	@Test
	public void getMonthDateFromMairieInteger_null_ReturnNull() {
	
		HelperService service = new HelperService();

		assertNull(service.getMonthDateFromMairieInteger(0));
	}
	
	@Test
	public void getMonthDateFromMairieInteger_0_ReturnNull() {
	
		HelperService service = new HelperService();

		assertNull(service.getMonthDateFromMairieInteger(null));
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
	
	@Test
	public void theDateTest() {
		DateTime d = new DateTime(1377128761000l, DateTimeZone.forID("+1100"));
		Date dd = d.toDate();
		DateFormat df = new SimpleDateFormat("z Z");
		String s = df.format(dd);
//		assertEquals("SBT +1100", s);
		assertTrue(s.contains("+1100"));
	}
	
	@Test
	public void formatMinutesToString_HoursWithoutMinutes() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals("2h", service.formatMinutesToString(120));
		
	}
	
	@Test
	public void formatMinutesToString_HoursWithMinutes() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals("4h30m", service.formatMinutesToString(270));
		
	}
	
	@Test
	public void formatMinutesToString_MinutesWithoutHours() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals("15m", service.formatMinutesToString(15));
		
	}
	
	@Test
	public void formatMinutesToString_NegativeHoursWithoutMinutes() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals("- 2h", service.formatMinutesToString(-120));
		
	}
	
	@Test
	public void formatMinutesToString_NegativeHoursWithMinutes() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals("- 4h30m", service.formatMinutesToString(-270));
		
	}
	
	@Test
	public void formatMinutesToString_NegativeMinutesWithoutHours() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals("- 15m", service.formatMinutesToString(-15));
		
	}
	
	@Test
	public void formatMinutesToString_PeriodHoursWithoutMinutes() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals("2h", service.formatMinutesToString(new DateTime(2013, 01, 01, 2, 15, 0).toDate(), new DateTime(2013, 01, 01, 4, 15, 0).toDate()));
		
	}
	
	@Test
	public void formatMinutesToString_PeriodHoursWithMinutes() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals("4h30m", service.formatMinutesToString(new DateTime(2013, 01, 01, 16, 15, 0).toDate(), new DateTime(2013, 01, 01, 20, 45, 0).toDate()));
		
	}
	
	@Test
	public void formatMinutesToString_PeriodMinutesWithoutHours() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals("15m", service.formatMinutesToString(new DateTime(2013, 01, 01, 2, 15, 0).toDate(), new DateTime(2013, 01, 01, 2, 30, 0).toDate()));
		
	}
	
	@Test
	public void formatMinutesToString_PeriodOneDay() {
		
		// Given
		HelperService service = new HelperService();
		
		// Then
		assertEquals("1j", service.formatMinutesToString(new DateTime(2013, 01, 01, 2, 15, 0).toDate(), new DateTime(2013, 01, 02, 2, 15, 0).toDate()));
		
	}
	
	@Test
	public void getIdAgentFromMairieMatr_null() {
		
		HelperService service = new HelperService();
		assertNull(service.getIdAgentFromMairieMatr(null));
	}
	
	@Test
	public void getIdAgentFromMairieMatr_idAgent() {
		
		HelperService service = new HelperService();
		assertEquals(9005138, service.getIdAgentFromMairieMatr(9005138).intValue());
	}
	
	@Test
	public void getIdAgentFromMairieMatr_noMatr() {
		
		HelperService service = new HelperService();
		assertEquals(9005138, service.getIdAgentFromMairieMatr(5138).intValue());
	}
	
	@Test
	public void getDureeBetweenDateDebutAndDateFin() {
		
		Date dateDebut = new DateTime(2015,7,20,8,0,0).toDate();
		Date dateFin = new DateTime(2015,7,20,12,0,0).toDate();
		
		HelperService service = new HelperService();
		Integer duree = service.getDureeBetweenDateDebutAndDateFin(dateDebut, dateFin);
		
		assertEquals(240, (int)duree);
	}
}
