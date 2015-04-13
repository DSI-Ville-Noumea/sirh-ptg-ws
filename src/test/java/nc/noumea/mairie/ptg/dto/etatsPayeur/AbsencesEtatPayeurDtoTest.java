package nc.noumea.mairie.ptg.dto.etatsPayeur;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;

public class AbsencesEtatPayeurDtoTest {

	@Test
	public void AbsencesEtatPayeurDto_DifferenceBetweenNewVentilAndOldVentil() {

		// Given
		VentilAbsence vaNew = new VentilAbsence();
		vaNew.setIdAgent(9008767);
		vaNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vaNew.addMinutesNonConcertee(90);
		vaNew.setNombreAbsenceInferieur1(1);
		vaNew.setNombreAbsenceEntre1Et4(1);
		vaNew.setNombreAbsenceSuperieur1(3);

		VentilAbsence vaOld = new VentilAbsence();
		vaOld.setIdAgent(9008767);
		vaOld.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vaOld.addMinutesNonConcertee(90);
		vaOld.setNombreAbsenceInferieur1(0);
		vaOld.setNombreAbsenceEntre1Et4(2);
		vaOld.setNombreAbsenceSuperieur1(1);
		
		HelperService hS = Mockito.mock(HelperService.class);

		// When
		AbsencesEtatPayeurDto result = new AbsencesEtatPayeurDto(vaNew, vaOld, hS);

		// Then
		assertEquals("1", result.getQuantiteInf1Heure());
		assertEquals("-1", result.getQuantiteEntre1HeureEt4Heure());
		assertEquals("2", result.getQuantiteSup4Heure());
	}

	@Test
	public void AbsencesEtatPayeurDto_1AbsenceInferieur1() {

		// Given
		VentilAbsence vaNew = new VentilAbsence();
		vaNew.setIdAgent(9008767);
		vaNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vaNew.setMinutesConcertee(90);
		vaNew.setNombreAbsenceInferieur1(1);
		vaNew.setNombreAbsenceEntre1Et4(0);
		vaNew.setNombreAbsenceSuperieur1(0);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(60)).thenReturn("1h");

		VentilAbsence vaOld = new VentilAbsence();
		vaOld.setMinutesConcertee(30);

		// When
		AbsencesEtatPayeurDto result = new AbsencesEtatPayeurDto(vaNew, vaOld, hS);

		// Then
		assertEquals("1", result.getQuantiteInf1Heure());
		assertEquals("", result.getQuantiteEntre1HeureEt4Heure());
		assertEquals("", result.getQuantiteSup4Heure());
	}

	@Test
	public void AbsencesEtatPayeurDto_1AbsenceEntre1Et4() {

		// Given
		VentilAbsence vaNew = new VentilAbsence();
		vaNew.setIdAgent(9008767);
		vaNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vaNew.setMinutesNonConcertee(90);
		vaNew.setNombreAbsenceInferieur1(0);
		vaNew.setNombreAbsenceEntre1Et4(1);
		vaNew.setNombreAbsenceSuperieur1(0);
		HelperService hS = Mockito.mock(HelperService.class);

		VentilAbsence vaOld = new VentilAbsence();
		vaOld.setMinutesNonConcertee(120);

		// When
		AbsencesEtatPayeurDto result = new AbsencesEtatPayeurDto(vaNew, vaOld, hS);

		// Then
		assertEquals("", result.getQuantiteInf1Heure());
		assertEquals("1", result.getQuantiteEntre1HeureEt4Heure());
		assertEquals("", result.getQuantiteSup4Heure());
	}

	@Test
	public void AbsencesEtatPayeurDto_2Inferieur1() {

		// Given
		VentilAbsence vaNew = new VentilAbsence();
		vaNew.setIdAgent(9008767);
		vaNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vaNew.setMinutesImmediat(90);
		vaNew.setNombreAbsenceInferieur1(2);
		vaNew.setNombreAbsenceEntre1Et4(0);
		vaNew.setNombreAbsenceSuperieur1(0);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(60)).thenReturn("1h");

		VentilAbsence vaOld = new VentilAbsence();
		vaOld.setMinutesImmediat(30);

		// When
		AbsencesEtatPayeurDto result = new AbsencesEtatPayeurDto(vaNew, vaOld, hS);

		// Then
		assertEquals("2", result.getQuantiteInf1Heure());
		assertEquals("", result.getQuantiteEntre1HeureEt4Heure());
		assertEquals("", result.getQuantiteSup4Heure());
	}

	@Test
	public void AbsencesEtatPayeurDto_2Inferieur1_3Superieur4() {

		// Given
		VentilAbsence vaNew = new VentilAbsence();
		vaNew.setIdAgent(9008767);
		vaNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vaNew.setMinutesImmediat(90);
		vaNew.setNombreAbsenceInferieur1(2);
		vaNew.setNombreAbsenceEntre1Et4(0);
		vaNew.setNombreAbsenceSuperieur1(3);
		HelperService hS = Mockito.mock(HelperService.class);

		VentilAbsence vaOld = new VentilAbsence();
		vaOld.setMinutesImmediat(120);

		// When
		AbsencesEtatPayeurDto result = new AbsencesEtatPayeurDto(vaNew, vaOld, hS);

		// Then
		assertEquals("2", result.getQuantiteInf1Heure());
		assertEquals("", result.getQuantiteEntre1HeureEt4Heure());
		assertEquals("3", result.getQuantiteSup4Heure());
	}
}
