package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.ptg.domain.RefTypeAbsenceEnum;
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
		abs = new RefTypePointage();
		abs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
	}

	@Test
	public void processAbsenceAgent_NoAbsences_ReturnNull() {

		// Given
		DateTime dateDebutMois = new DateTime(2013, 5, 1, 0, 0, 0);
		List<Pointage> pointages = new ArrayList<Pointage>();

		VentilationAbsenceService service = new VentilationAbsenceService();

		// When
		VentilAbsence result = service.processAbsenceAgent(9008765, pointages, dateDebutMois.toDate(), new ArrayList<Pointage>());

		// Then
		assertNull(result);
	}

	@Test
	public void processAbsenceAgent_1AbsenceConcertee2AbsencesNonConcertees_ReturnAggregatedAbsences() {

		// Given
		DateTime dateDebutMois = new DateTime(2013, 5, 1, 0, 0, 0);

		RefTypeAbsence rta1 = new RefTypeAbsence();
		rta1.setIdRefTypeAbsence(RefTypeAbsenceEnum.CONCERTEE.getValue());

		Pointage p1 = new Pointage();
		p1.setIdPointage(1);
		p1.setDateDebut(new DateTime(2013, 5, 11, 12, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 12, 13, 0, 0).toDate());
		p1.setType(abs);
		p1.setRefTypeAbsence(rta1);

		RefTypeAbsence rta2 = new RefTypeAbsence();
		rta2.setIdRefTypeAbsence(RefTypeAbsenceEnum.NON_CONCERTEE.getValue());

		Pointage p2 = new Pointage();
		p2.setIdPointage(2);
		p2.setDateDebut(new DateTime(2013, 5, 19, 8, 30, 0).toDate());
		p2.setDateFin(new DateTime(2013, 5, 19, 10, 0, 0).toDate());
		p2.setType(abs);
		p2.setRefTypeAbsence(rta2);

		RefTypeAbsence rta3 = new RefTypeAbsence();
		rta3.setIdRefTypeAbsence(RefTypeAbsenceEnum.IMMEDIATE.getValue());

		Pointage p3 = new Pointage();
		p3.setIdPointage(3);
		p3.setDateDebut(new DateTime(2013, 5, 20, 15, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 5, 20, 17, 00, 0).toDate());
		p3.setType(abs);
		p3.setRefTypeAbsence(rta3);

		VentilationAbsenceService service = new VentilationAbsenceService();

		// When
		VentilAbsence result = service.processAbsenceAgent(9008765, Arrays.asList(p1, p2, p3), dateDebutMois.toDate(), new ArrayList<Pointage>());

		// Then
		assertEquals(9008765, (int) result.getIdAgent());
		assertEquals(1500, result.getMinutesConcertee());
		assertEquals(90, result.getMinutesNonConcertee());
		assertEquals(120, result.getMinutesImmediat());
		assertEquals(dateDebutMois.toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
		assertEquals(0, result.getNombreAbsenceInferieur1().intValue());
		assertEquals(2, result.getNombreAbsenceEntre1Et4().intValue());
		assertEquals(1, result.getNombreAbsenceSuperieur1().intValue());
	}

	@Test
	public void processAbsenceAgent_1AbsenceConcertee1AbsencesNonConcertees1AbsenceImmediate_ReturnAggregatedAbsences() {

		// Given
		DateTime dateDebutMois = new DateTime(2013, 5, 1, 0, 0, 0);

		RefTypeAbsence rta1 = new RefTypeAbsence();
		rta1.setIdRefTypeAbsence(RefTypeAbsenceEnum.CONCERTEE.getValue());

		Pointage p1 = new Pointage();
		p1.setIdPointage(1);
		p1.setDateDebut(new DateTime(2013, 5, 11, 12, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 12, 13, 0, 0).toDate());
		p1.setType(abs);
		p1.setRefTypeAbsence(rta1);

		RefTypeAbsence rta2 = new RefTypeAbsence();
		rta2.setIdRefTypeAbsence(RefTypeAbsenceEnum.NON_CONCERTEE.getValue());

		Pointage p2 = new Pointage();
		p2.setIdPointage(2);
		p2.setDateDebut(new DateTime(2013, 5, 19, 8, 30, 0).toDate());
		p2.setDateFin(new DateTime(2013, 5, 19, 10, 0, 0).toDate());
		p2.setType(abs);
		p2.setRefTypeAbsence(rta2);

		RefTypeAbsence rta3 = new RefTypeAbsence();
		rta3.setIdRefTypeAbsence(RefTypeAbsenceEnum.IMMEDIATE.getValue());

		Pointage p3 = new Pointage();
		p3.setIdPointage(3);
		p3.setDateDebut(new DateTime(2013, 5, 20, 15, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 5, 20, 16, 00, 0).toDate());
		p3.setType(abs);
		p3.setRefTypeAbsence(rta3);

		VentilationAbsenceService service = new VentilationAbsenceService();

		// When
		VentilAbsence result = service.processAbsenceAgent(9008765, Arrays.asList(p1, p2, p3), dateDebutMois.toDate(), new ArrayList<Pointage>());

		// Then
		assertEquals(9008765, (int) result.getIdAgent());
		assertEquals(1500, result.getMinutesConcertee());
		assertEquals(90, result.getMinutesNonConcertee());
		assertEquals(60, result.getMinutesImmediat());
		assertEquals(dateDebutMois.toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
		assertEquals(1, result.getNombreAbsenceInferieur1().intValue());
		assertEquals(1, result.getNombreAbsenceEntre1Et4().intValue());
		assertEquals(1, result.getNombreAbsenceSuperieur1().intValue());
	}
	

	// #15518 
	@Test
	public void processAbsenceAgent_AbsencesMemeJour() {

		// Given
		DateTime dateDebutMois = new DateTime(2015, 5, 1, 0, 0, 0);

		RefTypeAbsence rta1 = new RefTypeAbsence();
		rta1.setIdRefTypeAbsence(RefTypeAbsenceEnum.CONCERTEE.getValue());

		RefTypeAbsence rta2 = new RefTypeAbsence();
		rta2.setIdRefTypeAbsence(RefTypeAbsenceEnum.NON_CONCERTEE.getValue());

		RefTypeAbsence rta3 = new RefTypeAbsence();
		rta3.setIdRefTypeAbsence(RefTypeAbsenceEnum.IMMEDIATE.getValue());

		// 1er jour : 2 absences de 30 minutes
		Pointage p1 = new Pointage();
		p1.setIdPointage(1);
		p1.setDateDebut(new DateTime(2015, 5, 11, 12, 0, 0).toDate());
		p1.setDateFin(new DateTime(2015, 5, 11, 12, 30, 0).toDate());
		p1.setType(abs);
		p1.setRefTypeAbsence(rta1);

		Pointage p2 = new Pointage();
		p2.setIdPointage(2);
		p2.setDateDebut(new DateTime(2015, 5, 11, 15, 30, 0).toDate());
		p2.setDateFin(new DateTime(2015, 5, 11, 16, 0, 0).toDate());
		p2.setType(abs);
		p2.setRefTypeAbsence(rta2);

		// 2e jour : 2 absences de 1h
		Pointage p3 = new Pointage();
		p3.setIdPointage(3);
		p3.setDateDebut(new DateTime(2015, 5, 20, 8, 0, 0).toDate());
		p3.setDateFin(new DateTime(2015, 5, 20, 9, 0, 0).toDate());
		p3.setType(abs);
		p3.setRefTypeAbsence(rta3);
		
		Pointage p4 = new Pointage();
		p4.setIdPointage(4);
		p4.setDateDebut(new DateTime(2015, 5, 20, 15, 0, 0).toDate());
		p4.setDateFin(new DateTime(2015, 5, 20, 16, 0, 0).toDate());
		p4.setType(abs);
		p4.setRefTypeAbsence(rta3);

		// 3e jour : 2 absences de 2h
		Pointage p5 = new Pointage();
		p5.setIdPointage(5);
		p5.setDateDebut(new DateTime(2015, 5, 22, 8, 0, 0).toDate());
		p5.setDateFin(new DateTime(2015, 5, 22, 10, 0, 0).toDate());
		p5.setType(abs);
		p5.setRefTypeAbsence(rta2);
		
		Pointage p6 = new Pointage();
		p6.setIdPointage(6);
		p6.setDateDebut(new DateTime(2015, 5, 22, 15, 0, 0).toDate());
		p6.setDateFin(new DateTime(2015, 5, 22, 17, 30, 0).toDate());
		p6.setType(abs);
		p6.setRefTypeAbsence(rta3);
		
		VentilationAbsenceService service = new VentilationAbsenceService();

		// When
		VentilAbsence result = service.processAbsenceAgent(9008765, Arrays.asList(p1, p2, p3, p4, p5, p6), dateDebutMois.toDate(), new ArrayList<Pointage>());

		// Then
		assertEquals(9008765, (int) result.getIdAgent());
		assertEquals(30, result.getMinutesConcertee());
		assertEquals(150, result.getMinutesNonConcertee());
		assertEquals(270, result.getMinutesImmediat());
		assertEquals(dateDebutMois.toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
		assertEquals(1, result.getNombreAbsenceInferieur1().intValue());
		assertEquals(1, result.getNombreAbsenceEntre1Et4().intValue());
		assertEquals(1, result.getNombreAbsenceSuperieur1().intValue());
	}

	// #16789 
	@Test
	public void processAbsenceAgent_AbsencesVentileesRejetees() {

		// Given
		DateTime dateDebutMois = new DateTime(2015, 5, 1, 0, 0, 0);

		RefTypeAbsence rta1 = new RefTypeAbsence();
		rta1.setIdRefTypeAbsence(RefTypeAbsenceEnum.CONCERTEE.getValue());

		Pointage p1 = new Pointage();
		p1.setIdPointage(1);
		p1.setDateDebut(new DateTime(2015, 5, 11, 12, 0, 0).toDate());
		p1.setDateFin(new DateTime(2015, 5, 11, 12, 30, 0).toDate());
		p1.setType(abs);
		p1.setRefTypeAbsence(rta1);
		
		VentilationAbsenceService service = new VentilationAbsenceService();

		// When
		VentilAbsence result = service.processAbsenceAgent(9008765, new ArrayList<Pointage>(), dateDebutMois.toDate(), Arrays.asList(p1));

		// Then
		assertEquals(9008765, (int) result.getIdAgent());
		assertEquals(0, result.getMinutesConcertee());
		assertEquals(0, result.getMinutesNonConcertee());
		assertEquals(0, result.getMinutesImmediat());
		assertEquals(dateDebutMois.toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
		assertEquals(0, result.getNombreAbsenceInferieur1().intValue());
		assertEquals(0, result.getNombreAbsenceEntre1Et4().intValue());
		assertEquals(0, result.getNombreAbsenceSuperieur1().intValue());
	}
}
