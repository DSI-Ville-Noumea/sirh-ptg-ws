package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IHolidayService;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class PointageCalculeServiceTest {

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
	public void generatePointage7720_21_22_7720_With1Pointage7701() {
		
		// Given
		Date dateLundi = new LocalDate(2013, 7, 8).toDate();
		
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008767);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new LocalDate(2013, 7, 9).toDate());
		p1.setQuantite(2);
		p1.setType(prime);
		RefPrime pr1 = new RefPrime();
		pr1.setNoRubr(7701);
		p1.setRefPrime(pr1);
		
		Pointage p2 = new Pointage();
		p2.setDateLundi(dateLundi);
		p2.setDateDebut(new LocalDate(2013, 7, 10).toDate());
		p2.setQuantite(2);
		p2.setType(prime);
		RefPrime pr2 = new RefPrime();
		pr2.setNoRubr(7702);
		p2.setRefPrime(pr2);
		
		Pointage p3 = new Pointage();
		p3.setDateLundi(dateLundi);
		p3.setDateDebut(new DateTime(2013, 7, 10, 9, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 7, 10, 10, 0, 0).toDate());
		p3.setType(abs);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue())).thenReturn(prime);
		
		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		
		RefPrime rp7720 = new RefPrime();
		rp7720.setNoRubr(7720);
		
		// When
		List<PointageCalcule> result = service.generatePointage7720_21_22(p1.getIdAgent(), dateLundi, rp7720, Arrays.asList(p1, p2, p3));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(rp7720, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(p1.getDateDebut(), result.get(0).getDateDebut());
		assertEquals(2, (int) result.get(0).getQuantite());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}
	
	@Test
	public void generatePointage7711withOne7715_Having3hoursNuit_And1HourDay() {
		
		// Given
		Date dateLundi = new LocalDate(2013, 7, 8).toDate();
		
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008767);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 7, 9, 2, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 7, 9, 6, 0, 0).toDate());
		p1.setType(prime);
		RefPrime pr1 = new RefPrime();
		pr1.setNoRubr(7715);
		p1.setRefPrime(pr1);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue())).thenReturn(prime);
		
		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		
		RefPrime rp7711 = new RefPrime();
		rp7711.setNoRubr(7711);
		
		// When
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7711, Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(rp7711, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 9).toDate(), result.get(0).getDateDebut());
		assertEquals(3, (int) result.get(0).getQuantite());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}
	
	@Test
	public void generatePointage7712withOne7715_ASunday() {
		
		// Given
		Date dateLundi = new LocalDate(2013, 7, 8).toDate();
		
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008767);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 7, 14, 2, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 7, 14, 6, 0, 0).toDate());
		p1.setType(prime);
		RefPrime pr1 = new RefPrime();
		pr1.setNoRubr(7715);
		p1.setRefPrime(pr1);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue())).thenReturn(prime);
		
		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		
		RefPrime rp7712 = new RefPrime();
		rp7712.setNoRubr(7712);
		
		// When
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7712, Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(rp7712, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7,14).toDate(), result.get(0).getDateDebut());
		assertEquals(4, (int) result.get(0).getQuantite());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}
	
	@Test
	public void generatePointage7712withOne7715_AHoliday() {
		
		// Given
		Date dateLundi = new LocalDate(2013, 7, 8).toDate();
		
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008767);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 7, 13, 2, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 7, 13, 6, 0, 0).toDate());
		p1.setType(prime);
		RefPrime pr1 = new RefPrime();
		pr1.setNoRubr(7715);
		p1.setRefPrime(pr1);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue())).thenReturn(prime);
		
		IHolidayService hService = Mockito.mock(IHolidayService.class);
		Mockito.when(hService.isHoliday(new LocalDate(2013, 7, 13))).thenReturn(true);
		
		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "holidayService", hService);
		
		RefPrime rp7712 = new RefPrime();
		rp7712.setNoRubr(7712);
		
		// When
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7712, Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(rp7712, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7,13).toDate(), result.get(0).getDateDebut());
		assertEquals(4, (int) result.get(0).getQuantite());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}
	
	@Test
	public void generatePointage7713withOne7715_Having1ShiftMoreThan9Hours() {
		
		// Given
		Date dateLundi = new LocalDate(2013, 7, 8).toDate();
		
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008767);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 7, 9, 2, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 7, 9, 14, 0, 0).toDate());
		p1.setType(prime);
		RefPrime pr1 = new RefPrime();
		pr1.setNoRubr(7715);
		p1.setRefPrime(pr1);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue())).thenReturn(prime);
		
		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		
		RefPrime rp7713 = new RefPrime();
		rp7713.setNoRubr(7713);
		
		// When
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7713, Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(rp7713, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 9).toDate(), result.get(0).getDateDebut());
		assertEquals(1, (int) result.get(0).getQuantite());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}
	
	@Test
	public void generatePointage7713withOne7715_Having1ShiftCovering5Hand13H() {
		
		// Given
		Date dateLundi = new LocalDate(2013, 7, 8).toDate();
		
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008767);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 7, 9, 5, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 7, 9, 13, 0, 0).toDate());
		p1.setType(prime);
		RefPrime pr1 = new RefPrime();
		pr1.setNoRubr(7715);
		p1.setRefPrime(pr1);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue())).thenReturn(prime);
		
		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		
		RefPrime rp7713 = new RefPrime();
		rp7713.setNoRubr(7713);
		
		// When
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7713, Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(rp7713, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 9).toDate(), result.get(0).getDateDebut());
		assertEquals(1, (int) result.get(0).getQuantite());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}
	
	@Test
	public void generatePointage7713withOne7715_Having1ShiftCovering13Hand21H() {
		
		// Given
		Date dateLundi = new LocalDate(2013, 7, 8).toDate();
		
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008767);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 7, 9, 13, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 7, 9, 21, 0, 0).toDate());
		p1.setType(prime);
		RefPrime pr1 = new RefPrime();
		pr1.setNoRubr(7715);
		p1.setRefPrime(pr1);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue())).thenReturn(prime);
		
		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		
		RefPrime rp7713 = new RefPrime();
		rp7713.setNoRubr(7713);
		
		// When
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7713, Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(rp7713, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 9).toDate(), result.get(0).getDateDebut());
		assertEquals(1, (int) result.get(0).getQuantite());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}
	
	@Test
	public void generatePointage7713withOne7715_CannotHaveMoreThan2() {
		
		// Given
		Date dateLundi = new LocalDate(2013, 7, 8).toDate();
		
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008767);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 7, 9, 13, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 7, 9, 21, 0, 0).toDate());
		p1.setType(prime);
		RefPrime pr1 = new RefPrime();
		pr1.setNoRubr(7715);
		p1.setRefPrime(pr1);
		
		Pointage p2 = new Pointage();
		p2.setIdAgent(9008767);
		p2.setDateLundi(dateLundi);
		p2.setDateDebut(new DateTime(2013, 7, 9, 5, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 7, 9, 13, 0, 0).toDate());
		p2.setType(prime);
		p2.setRefPrime(pr1);
		
		Pointage p3 = new Pointage();
		p3.setIdAgent(9008767);
		p3.setDateLundi(dateLundi);
		p3.setDateDebut(new DateTime(2013, 7, 9, 1, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 7, 9, 10, 0, 0).toDate());
		p3.setType(prime);
		p3.setRefPrime(pr1);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue())).thenReturn(prime);
		
		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		
		RefPrime rp7713 = new RefPrime();
		rp7713.setNoRubr(7713);
		
		// When
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7713, Arrays.asList(p1, p2, p3));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(rp7713, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 9).toDate(), result.get(0).getDateDebut());
		assertEquals(2, (int) result.get(0).getQuantite());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}
}
