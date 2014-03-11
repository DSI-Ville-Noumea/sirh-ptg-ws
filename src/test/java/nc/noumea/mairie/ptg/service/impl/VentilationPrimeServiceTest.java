package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.domain.VentilPrime;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

public class VentilationPrimeServiceTest {

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
	public void processPrimesAgent_NoPrimes_ReturnEmptyList() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 6, 0, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 7, 0, 0).toDate());
		p1.setType(hSup);
					
		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 29, 14, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 16, 0, 0).toDate());
		p2.setType(abs);
		
		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 04, 30, 18, 0, 0).toDate());
		p3.setDateFin(new DateTime(2012, 04, 30, 23, 0, 0).toDate());
		p3.setType(hSup);
		
		VentilationPrimeService service = new VentilationPrimeService();
		
		Date dateDebutMois = new DateTime(2012, 04, 1, 0, 0, 0).toDate();
		
		// When
		List<VentilPrime> result = service.processPrimesAgent(9007865, Arrays.asList(p1, p2, p3), dateDebutMois);
		
		// Then
		assertEquals(0, result.size());
	}
	
	@Test
	public void processPrimesAgent_2PrimesIn3Pointages_ReturnEmptyListOf2Aggregated() {
		
		// Given
		RefPrime refPrime1 = new RefPrime();
		refPrime1.setIdRefPrime(8907);
		refPrime1.setNoRubr(8877);
		refPrime1.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		
		RefPrime refPrime2 = new RefPrime();
		refPrime2.setIdRefPrime(8909);
		refPrime2.setNoRubr(8879);
		refPrime2.setTypeSaisie(TypeSaisieEnum.PERIODE_HEURES);
		
		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setQuantite(2);
		p1.setRefPrime(refPrime1);
		p1.setType(prime);
					
		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setQuantite(3);
		p2.setRefPrime(refPrime1);
		p2.setType(prime);
		
		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 04, 15, 8, 15, 0).toDate());
		p3.setDateFin(new DateTime(2012, 04, 15, 11, 45, 0).toDate());
		p3.setRefPrime(refPrime2);
		p3.setType(prime);
		
		VentilationPrimeService service = new VentilationPrimeService();
		
		Date dateDebutMois = new DateTime(2012, 04, 1, 0, 0, 0).toDate();
		
		// When
		List<VentilPrime> result = service.processPrimesAgent(9007865, Arrays.asList(p1, p2, p3), dateDebutMois);
		
		// Then
		assertEquals(2, result.size());
		assertEquals(5, (int) result.get(0).getQuantite());
		assertEquals(9007865, (int) result.get(0).getIdAgent());
		assertEquals(dateDebutMois, result.get(0).getDateDebutMois());
		assertEquals(refPrime1, result.get(0).getRefPrime());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(9007865, (int) result.get(1).getIdAgent());
		assertEquals(dateDebutMois, result.get(1).getDateDebutMois());
		assertEquals(refPrime2, result.get(1).getRefPrime());
		assertEquals(210, (int) result.get(1).getQuantite());
		assertEquals(EtatPointageEnum.VENTILE, result.get(1).getEtat());
	}
	
	@Test
	public void processPrimesAgent_3PrimesIn4Pointages_1isToNotTake_ReturnEmptyListOf1Aggregated() {
		
		// Given
		RefPrime refPrime1 = new RefPrime();
		refPrime1.setIdRefPrime(8907);
		refPrime1.setNoRubr(8877);
		refPrime1.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		
		RefPrime refPrime2 = new RefPrime();
		refPrime2.setIdRefPrime(8909);
		refPrime2.setNoRubr(7715);
		refPrime2.setTypeSaisie(TypeSaisieEnum.PERIODE_HEURES);
		
		RefPrime refPrime3 = new RefPrime();
		refPrime3.setIdRefPrime(8909);
		refPrime3.setNoRubr(7715);
		refPrime3.setTypeSaisie(TypeSaisieEnum.PERIODE_HEURES);
		
		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setQuantite(2);
		p1.setRefPrime(refPrime1);
		p1.setType(prime);
					
		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setQuantite(3);
		p2.setRefPrime(refPrime1);
		p2.setType(prime);
		
		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setRefPrime(refPrime2);
		p3.setType(prime);
		
		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setRefPrime(refPrime3);
		p4.setType(prime);
		
		VentilationPrimeService service = new VentilationPrimeService();
		
		Date dateDebutMois = new DateTime(2012, 04, 1, 0, 0, 0).toDate();
		
		// When
		List<VentilPrime> result = service.processPrimesAgent(9007865, Arrays.asList(p1, p2, p3, p4), dateDebutMois);
		
		// Then
		assertEquals(1, result.size());
		assertEquals(5, (int) result.get(0).getQuantite());
		assertEquals(9007865, (int) result.get(0).getIdAgent());
		assertEquals(dateDebutMois, result.get(0).getDateDebutMois());
		assertEquals(refPrime1, result.get(0).getRefPrime());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
	}
}
