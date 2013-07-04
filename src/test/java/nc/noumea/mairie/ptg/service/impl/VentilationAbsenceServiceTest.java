package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

public class VentilationAbsenceServiceTest {

	private static RefTypePointage hSup;
	private static RefTypePointage prime;
	private static RefTypePointage abs;
	
	@BeforeClass
	public static void Setup() {
		prime = new RefTypePointage();
		prime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		hSup = new RefTypePointage();
		hSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		abs= new RefTypePointage();
		abs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
	}
	
	@Test
	public void processAbsenceAgent_NoAbsences_ReturnEmpty() {
		
		// Given
		DateTime dateDebutMois = new DateTime(2013, 5, 1, 0, 0, 0);
		List<Pointage> pointages = new ArrayList<Pointage>();
		
		VentilationAbsenceService service = new VentilationAbsenceService();
		
		// When
		VentilAbsence result = service.processAbsenceAgent(9008765, pointages, dateDebutMois.toDate());
		
		// Then
		assertEquals(9008765, (int)result.getIdAgent());
		assertEquals(0, result.getMinutesConcertee());
		assertEquals(0, result.getMinutesNonConcertee());
		assertEquals(dateDebutMois.toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}
	
	@Test
	public void processAbsenceAgent_1AbsenceConcertee2AbsencesNonConcertees_ReturnAggregatedAbsences() {
		
		// Given
		DateTime dateDebutMois = new DateTime(2013, 5, 1, 0, 0, 0);
		
		Pointage p1 = new Pointage();
		p1.setDateDebut(new DateTime(2013, 5, 11, 12, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 11, 13, 0, 0).toDate());
		p1.setAbsenceConcertee(true);
		p1.setType(abs);
		
		Pointage p2 = new Pointage();
		p2.setDateDebut(new DateTime(2013, 5, 19, 8, 30, 0).toDate());
		p2.setDateFin(new DateTime(2013, 5, 19, 9, 0, 0).toDate());
		p2.setAbsenceConcertee(false);
		p2.setType(abs);
		
		Pointage p3 = new Pointage();
		p3.setDateDebut(new DateTime(2013, 5, 20, 15, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 5, 20, 16, 30, 0).toDate());
		p3.setAbsenceConcertee(false);
		p3.setType(abs);
		
		VentilationAbsenceService service = new VentilationAbsenceService();
		
		// When
		VentilAbsence result = service.processAbsenceAgent(9008765, Arrays.asList(p1, p2, p3), dateDebutMois.toDate());
		
		// Then
		assertEquals(9008765, (int)result.getIdAgent());
		assertEquals(60, result.getMinutesConcertee());
		assertEquals(120, result.getMinutesNonConcertee());
		assertEquals(dateDebutMois.toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}
}
