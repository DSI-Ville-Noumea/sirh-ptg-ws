package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeAbsenceEnum;
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
	public void processAbsenceAgent_NoAbsences_ReturnNull() {
		
		// Given
		DateTime dateDebutMois = new DateTime(2013, 5, 1, 0, 0, 0);
		List<Pointage> pointages = new ArrayList<Pointage>();
		
		VentilationAbsenceService service = new VentilationAbsenceService();
		
		// When
		VentilAbsence result = service.processAbsenceAgent(9008765, pointages, dateDebutMois.toDate());
		
		// Then
		assertNull(result);
	}
	
	@Test
	public void processAbsenceAgent_1AbsenceConcertee2AbsencesNonConcertees_ReturnAggregatedAbsences() {
		
		// Given
		DateTime dateDebutMois = new DateTime(2013, 5, 1, 0, 0, 0);
		
		RefTypeAbsence rta1 = new RefTypeAbsence();
			rta1.setIdRefTypeAbsence(TypeAbsenceEnum.CONCERTEE.getValue());
		
		Pointage p1 = new Pointage();
			p1.setDateDebut(new DateTime(2013, 5, 11, 12, 0, 0).toDate());
			p1.setDateFin(new DateTime(2013, 5, 11, 13, 0, 0).toDate());
			p1.setAbsenceConcertee(true);
			p1.setType(abs);
			p1.setTypeAbsence(rta1);
		
		RefTypeAbsence rta2 = new RefTypeAbsence();
			rta2.setIdRefTypeAbsence(TypeAbsenceEnum.NON_CONCERTEE.getValue());
		
		Pointage p2 = new Pointage();
			p2.setDateDebut(new DateTime(2013, 5, 19, 8, 30, 0).toDate());
			p2.setDateFin(new DateTime(2013, 5, 19, 10, 0, 0).toDate());
			p2.setAbsenceConcertee(false);
			p2.setType(abs);
			p2.setTypeAbsence(rta2);
		
		RefTypeAbsence rta3 = new RefTypeAbsence();
			rta3.setIdRefTypeAbsence(TypeAbsenceEnum.IMMEDIATE.getValue());
			
		Pointage p3 = new Pointage();
			p3.setDateDebut(new DateTime(2013, 5, 20, 15, 0, 0).toDate());
			p3.setDateFin(new DateTime(2013, 5, 20, 17, 00, 0).toDate());
			p3.setAbsenceConcertee(false);
			p3.setType(abs);
			p3.setTypeAbsence(rta3);
		
		VentilationAbsenceService service = new VentilationAbsenceService();
		
		// When
		VentilAbsence result = service.processAbsenceAgent(9008765, Arrays.asList(p1, p2, p3), dateDebutMois.toDate());
		
		// Then
		assertEquals(9008765, (int)result.getIdAgent());
		assertEquals(60, result.getMinutesConcertee());
		assertEquals(90, result.getMinutesNonConcertee());
		assertEquals(120, result.getMinutesImmediate());
		assertEquals(dateDebutMois.toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}
}
