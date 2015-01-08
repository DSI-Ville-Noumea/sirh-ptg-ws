package nc.noumea.mairie.ptg.dto.etatsPayeur;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;

public class AbsencesEtatPayeurDtoTest {

	@Test
	public void AbsencesEtatPayeurDto_absenceConcertee_convertMinutesToHeuresString() {

		// Given
		VentilAbsence va = new VentilAbsence();
		va.setIdAgent(9008767);
		va.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		va.addMinutesConcertee(90);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(90)).thenReturn("1h30");

		// When
		AbsencesEtatPayeurDto result = new AbsencesEtatPayeurDto(va, hS);

		// Then
		assertEquals("1h30", result.getQuantiteEntre1HeureEt4Heure());
		assertEquals("", result.getQuantiteInf1Heure());
		assertEquals("", result.getQuantiteSup4Heure());
	}

	@Test
	public void AbsencesEtatPayeurDto_absenceNonConcertee_convertMinutesToHeuresString() {

		// Given
		VentilAbsence va = new VentilAbsence();
		va.setIdAgent(9008767);
		va.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		va.addMinutesNonConcertee(90);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(90)).thenReturn("1h30");

		// When
		AbsencesEtatPayeurDto result = new AbsencesEtatPayeurDto(va, hS);

		// Then
		assertEquals("1h30", result.getQuantiteEntre1HeureEt4Heure());
		assertEquals("", result.getQuantiteInf1Heure());
		assertEquals("", result.getQuantiteSup4Heure());
	}

	@Test
	public void AbsencesEtatPayeurDto_absenceConcertee_NewOld_convertMinutesToHeuresString() {

		// Given
		VentilAbsence vaNew = new VentilAbsence();
		vaNew.setIdAgent(9008767);
		vaNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vaNew.setMinutesConcertee(90);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(60)).thenReturn("1h");

		VentilAbsence vaOld = new VentilAbsence();
		vaOld.setMinutesConcertee(30);

		// When
		AbsencesEtatPayeurDto result = new AbsencesEtatPayeurDto(vaNew, vaOld, hS);

		// Then
		assertEquals("1h", result.getQuantiteInf1Heure());
		assertEquals("", result.getQuantiteEntre1HeureEt4Heure());
		assertEquals("", result.getQuantiteSup4Heure());
	}

	@Test
	public void AbsencesEtatPayeurDto_absenceNonConcertee_NewOld_Negative_convertMinutesToHeuresString() {

		// Given
		VentilAbsence vaNew = new VentilAbsence();
		vaNew.setIdAgent(9008767);
		vaNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vaNew.setMinutesNonConcertee(90);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(-30)).thenReturn("- 30m");

		VentilAbsence vaOld = new VentilAbsence();
		vaOld.setMinutesNonConcertee(120);

		// When
		AbsencesEtatPayeurDto result = new AbsencesEtatPayeurDto(vaNew, vaOld, hS);

		// Then
		assertEquals("- 30m", result.getQuantiteInf1Heure());
		assertEquals("", result.getQuantiteEntre1HeureEt4Heure());
		assertEquals("", result.getQuantiteSup4Heure());
	}

	@Test
	public void AbsencesEtatPayeurDto_absenceImmediate_NewOld_convertMinutesToHeuresString() {

		// Given
		VentilAbsence vaNew = new VentilAbsence();
		vaNew.setIdAgent(9008767);
		vaNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vaNew.setMinutesImmediat(90);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(60)).thenReturn("1h");

		VentilAbsence vaOld = new VentilAbsence();
		vaOld.setMinutesImmediat(30);

		// When
		AbsencesEtatPayeurDto result = new AbsencesEtatPayeurDto(vaNew, vaOld, hS);

		// Then
		assertEquals("1h", result.getQuantiteInf1Heure());
		assertEquals("", result.getQuantiteEntre1HeureEt4Heure());
		assertEquals("", result.getQuantiteSup4Heure());
	}

	@Test
	public void AbsencesEtatPayeurDto_absenceImmediate_NewOld_Negative_convertMinutesToHeuresString() {

		// Given
		VentilAbsence vaNew = new VentilAbsence();
		vaNew.setIdAgent(9008767);
		vaNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vaNew.setMinutesImmediat(90);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(-30)).thenReturn("- 30m");

		VentilAbsence vaOld = new VentilAbsence();
		vaOld.setMinutesImmediat(120);

		// When
		AbsencesEtatPayeurDto result = new AbsencesEtatPayeurDto(vaNew, vaOld, hS);

		// Then
		assertEquals("- 30m", result.getQuantiteInf1Heure());
		assertEquals("", result.getQuantiteEntre1HeureEt4Heure());
		assertEquals("", result.getQuantiteSup4Heure());
	}
}
