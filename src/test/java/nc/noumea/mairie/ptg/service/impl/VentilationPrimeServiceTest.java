package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.repository.IPointageRepository;

public class VentilationPrimeServiceTest {

	private static RefTypePointage	hSup;
	private static RefTypePointage	prime;
	private static RefTypePointage	abs;

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
		List<VentilPrime> result = service.processPrimesAgent(9007865, Arrays.asList(p1, p2, p3), dateDebutMois, AgentStatutEnum.F);

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
		List<VentilPrime> result = service.processPrimesAgent(9007865, Arrays.asList(p1, p2, p3), dateDebutMois, AgentStatutEnum.F);

		// Then
		assertEquals(2, result.size());
		assertEquals(5, (int) result.get(0).getQuantite().intValue());
		assertEquals(9007865, (int) result.get(0).getIdAgent());
		assertEquals(dateDebutMois, result.get(0).getDateDebutMois());
		assertEquals(refPrime1, result.get(0).getRefPrime());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(9007865, (int) result.get(1).getIdAgent());
		assertEquals(dateDebutMois, result.get(1).getDateDebutMois());
		assertEquals(refPrime2, result.get(1).getRefPrime());
		assertEquals(210, (int) result.get(1).getQuantite().intValue());
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
		List<VentilPrime> result = service.processPrimesAgent(9007865, Arrays.asList(p1, p2, p3, p4), dateDebutMois, AgentStatutEnum.F);

		// Then
		assertEquals(1, result.size());
		assertEquals(5, (int) result.get(0).getQuantite().intValue());
		assertEquals(9007865, (int) result.get(0).getIdAgent());
		assertEquals(dateDebutMois, result.get(0).getDateDebutMois());
		assertEquals(refPrime1, result.get(0).getRefPrime());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
	}

	@Test
	public void processPrimesAgent_specialTID() {

		// Given
		RefPrime refPrimeTID7720 = new RefPrime();
		refPrimeTID7720.setIdRefPrime(8920);
		refPrimeTID7720.setNoRubr(7720);
		refPrimeTID7720.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		RefPrime refPrimeTID7721 = new RefPrime();
		refPrimeTID7721.setIdRefPrime(8921);
		refPrimeTID7721.setNoRubr(7721);
		refPrimeTID7721.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		RefPrime refPrimeTID7722 = new RefPrime();
		refPrimeTID7722.setIdRefPrime(8922);
		refPrimeTID7722.setNoRubr(7722);
		refPrimeTID7722.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		RefPrime refPrimeTID7723 = new RefPrime();
		refPrimeTID7723.setIdRefPrime(8923);
		refPrimeTID7723.setNoRubr(7723);
		refPrimeTID7723.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		RefPrime refPrimeTID7724 = new RefPrime();
		refPrimeTID7724.setIdRefPrime(8924);
		refPrimeTID7724.setNoRubr(7724);
		refPrimeTID7724.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		RefPrime refPrimeTID7725 = new RefPrime();
		refPrimeTID7725.setIdRefPrime(8925);
		refPrimeTID7725.setNoRubr(7725);
		refPrimeTID7725.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		// 7720 va avec 7723 pour cumul quantité
		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setQuantite(1);
		p1.setRefPrime(refPrimeTID7720);
		p1.setType(prime);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setRefPrime(refPrimeTID7723);
		p4.setType(prime);
		p4.setQuantite(4);

		// 7721 va avec 7724 pour cumul quantité
		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setQuantite(2);
		p2.setRefPrime(refPrimeTID7721);
		p2.setType(prime);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p5.setRefPrime(refPrimeTID7724);
		p5.setType(prime);
		p5.setQuantite(5);

		// 7722 va avec 7725 pour cumul quantité
		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setRefPrime(refPrimeTID7722);
		p3.setType(prime);
		p3.setQuantite(3);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p6.setRefPrime(refPrimeTID7725);
		p6.setType(prime);
		p6.setQuantite(6);

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getRefPrimes(Arrays.asList(refPrimeTID7720.getNoRubr()), AgentStatutEnum.F))
				.thenReturn(Arrays.asList(refPrimeTID7720));
		Mockito.when(pointageRepository.getRefPrimes(Arrays.asList(refPrimeTID7721.getNoRubr()), AgentStatutEnum.F))
				.thenReturn(Arrays.asList(refPrimeTID7721));
		Mockito.when(pointageRepository.getRefPrimes(Arrays.asList(refPrimeTID7722.getNoRubr()), AgentStatutEnum.F))
				.thenReturn(Arrays.asList(refPrimeTID7722));

		VentilationPrimeService service = new VentilationPrimeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);

		Date dateDebutMois = new DateTime(2012, 04, 1, 0, 0, 0).toDate();

		// When
		List<VentilPrime> result = service.processPrimesAgent(9007865, Arrays.asList(p1, p2, p3, p4, p5, p6), dateDebutMois, AgentStatutEnum.F);

		// Then
		assertEquals(3, result.size());

		for (VentilPrime ventilPrime : result) {
			if (ventilPrime.getRefPrime().equals(refPrimeTID7722)) {
				assertEquals(9, (int) ventilPrime.getQuantite().intValue());
				assertEquals(9007865, (int) ventilPrime.getIdAgent());
				assertEquals(dateDebutMois, ventilPrime.getDateDebutMois());
				assertEquals(refPrimeTID7722, ventilPrime.getRefPrime());
				assertEquals(refPrimeTID7722.getNoRubr(), ventilPrime.getRefPrime().getNoRubr());
				assertEquals(EtatPointageEnum.VENTILE, ventilPrime.getEtat());
			}
			if (ventilPrime.getRefPrime().equals(refPrimeTID7720)) {
				assertEquals(5, (int) ventilPrime.getQuantite().intValue());
				assertEquals(9007865, (int) ventilPrime.getIdAgent());
				assertEquals(dateDebutMois, ventilPrime.getDateDebutMois());
				assertEquals(refPrimeTID7720, ventilPrime.getRefPrime());
				assertEquals(refPrimeTID7720.getNoRubr(), ventilPrime.getRefPrime().getNoRubr());
				assertEquals(EtatPointageEnum.VENTILE, ventilPrime.getEtat());
			}
			if (ventilPrime.getRefPrime().equals(refPrimeTID7721)) {
				assertEquals(7, (int) ventilPrime.getQuantite().intValue());
				assertEquals(9007865, (int) ventilPrime.getIdAgent());
				assertEquals(dateDebutMois, ventilPrime.getDateDebutMois());
				assertEquals(refPrimeTID7721, ventilPrime.getRefPrime());
				assertEquals(refPrimeTID7721.getNoRubr(), ventilPrime.getRefPrime().getNoRubr());
				assertEquals(EtatPointageEnum.VENTILE, ventilPrime.getEtat());
			}
		}

	}

	@Test
	public void processPrimesAgent_specialTID_CorrectionBug33902() {

		// Given
		RefPrime refPrimeTID7720 = new RefPrime();
		refPrimeTID7720.setIdRefPrime(7720);
		refPrimeTID7720.setLibelle("INDEMNITE TVX INSAL. DANG. 100%");
		refPrimeTID7720.setNoRubr(7720);
		refPrimeTID7720.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		RefPrime refPrimeTID7721 = new RefPrime();
		refPrimeTID7721.setIdRefPrime(7721);
		refPrimeTID7721.setLibelle("INDEMNITE TVX INSAL. DANG. 50%");
		refPrimeTID7721.setNoRubr(7721);
		refPrimeTID7721.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		RefPrime refPrimeTID7722 = new RefPrime();
		refPrimeTID7722.setIdRefPrime(7722);
		refPrimeTID7722.setLibelle("INDEMNITE TVX INSAL. DANG. 25%");
		refPrimeTID7722.setNoRubr(7722);
		refPrimeTID7722.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		RefPrime refPrimeTID7723 = new RefPrime();
		refPrimeTID7723.setIdRefPrime(7723);
		refPrimeTID7723.setLibelle("INDEMNITE EXCEPTIONNELLE TVX INSAL. DANG. 100%");
		refPrimeTID7723.setNoRubr(7723);
		refPrimeTID7723.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		RefPrime refPrimeTID7724 = new RefPrime();
		refPrimeTID7724.setIdRefPrime(7724);
		refPrimeTID7724.setLibelle("INDEMNITE EXCEPTIONNELLE TVX INSAL. DANG. 50%");
		refPrimeTID7724.setNoRubr(7724);
		refPrimeTID7724.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		RefPrime refPrimeTID7725 = new RefPrime();
		refPrimeTID7725.setIdRefPrime(7725);
		refPrimeTID7725.setLibelle("INDEMNITE EXCEPTIONNELLE TVX INSAL. DANG. 25%");
		refPrimeTID7725.setNoRubr(7725);
		refPrimeTID7725.setTypeSaisie(TypeSaisieEnum.NB_HEURES);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setRefPrime(refPrimeTID7723);
		p4.setType(prime);
		p4.setQuantite(4);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p5.setRefPrime(refPrimeTID7724);
		p5.setType(prime);
		p5.setQuantite(5);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p6.setRefPrime(refPrimeTID7725);
		p6.setType(prime);
		p6.setQuantite(6);

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getRefPrimes(Arrays.asList(refPrimeTID7720.getNoRubr()), AgentStatutEnum.F))
				.thenReturn(Arrays.asList(refPrimeTID7720));
		Mockito.when(pointageRepository.getRefPrimes(Arrays.asList(refPrimeTID7721.getNoRubr()), AgentStatutEnum.F))
				.thenReturn(Arrays.asList(refPrimeTID7721));
		Mockito.when(pointageRepository.getRefPrimes(Arrays.asList(refPrimeTID7722.getNoRubr()), AgentStatutEnum.F))
				.thenReturn(Arrays.asList(refPrimeTID7722));

		VentilationPrimeService service = new VentilationPrimeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);

		Date dateDebutMois = new DateTime(2012, 04, 1, 0, 0, 0).toDate();

		// When
		List<VentilPrime> result = service.processPrimesAgent(9007865, Arrays.asList(p4, p5, p6), dateDebutMois, AgentStatutEnum.F);

		// Then
		assertEquals(3, result.size());

		for (VentilPrime ventilPrime : result) {
			if (ventilPrime.getRefPrime().getNoRubr().equals(refPrimeTID7722.getNoRubr())) {
				assertEquals(6, (int) ventilPrime.getQuantite().intValue());
				assertEquals(9007865, (int) ventilPrime.getIdAgent());
				assertEquals(dateDebutMois, ventilPrime.getDateDebutMois());
				assertEquals(refPrimeTID7722.getNoRubr(), ventilPrime.getRefPrime().getNoRubr());
				assertEquals(refPrimeTID7722.getLibelle(), ventilPrime.getRefPrime().getLibelle());
				assertEquals(EtatPointageEnum.VENTILE, ventilPrime.getEtat());
			}
			if (ventilPrime.getRefPrime().getNoRubr().equals(refPrimeTID7720.getNoRubr())) {
				assertEquals(4, (int) ventilPrime.getQuantite().intValue());
				assertEquals(9007865, (int) ventilPrime.getIdAgent());
				assertEquals(dateDebutMois, ventilPrime.getDateDebutMois());
				assertEquals(refPrimeTID7720.getNoRubr(), ventilPrime.getRefPrime().getNoRubr());
				assertEquals(refPrimeTID7720.getLibelle(), ventilPrime.getRefPrime().getLibelle());
				assertEquals(EtatPointageEnum.VENTILE, ventilPrime.getEtat());
			}
			if (ventilPrime.getRefPrime().getNoRubr().equals(refPrimeTID7721.getNoRubr())) {
				assertEquals(5, (int) ventilPrime.getQuantite().intValue());
				assertEquals(9007865, (int) ventilPrime.getIdAgent());
				assertEquals(dateDebutMois, ventilPrime.getDateDebutMois());
				assertEquals(refPrimeTID7721.getNoRubr(), ventilPrime.getRefPrime().getNoRubr());
				assertEquals(refPrimeTID7721.getLibelle(), ventilPrime.getRefPrime().getLibelle());
				assertEquals(EtatPointageEnum.VENTILE, ventilPrime.getEtat());
			}
		}

	}
}
