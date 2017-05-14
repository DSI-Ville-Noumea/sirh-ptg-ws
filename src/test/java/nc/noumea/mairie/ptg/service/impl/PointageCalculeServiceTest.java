package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefTypeSaisiDto;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.repository.IDpmRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

public class PointageCalculeServiceTest {

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
		assertEquals(180, result.get(0).getQuantite().intValue());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}

	@Test
	public void generatePointage7711withOne7715_Having1ShiftCovering20h45Hand5H15() {

		// Given
		Date dateLundi = new LocalDate(2013, 7, 8).toDate();

		Pointage p1 = new Pointage();
		p1.setIdAgent(9008767);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 7, 9, 20, 45, 0).toDate());
		p1.setDateFin(new DateTime(2013, 7, 10, 5, 15, 0).toDate());
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
		assertEquals(8 * 60, (int) result.get(0).getQuantite().intValue());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}

	@Test
	public void generatePointage7711withOne7715_Having1ShiftCovering0h00Hand1h45JPlus1() {

		// Given
		Date dateLundi = new LocalDate(2013, 7, 8).toDate();

		Pointage p1 = new Pointage();
		p1.setIdAgent(9008767);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 7, 9, 0, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 7, 10, 1, 45, 0).toDate());
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
		assertEquals(585, (int) result.get(0).getQuantite().intValue());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}

	// bug #15491
	// donnees de QUAL
	@Test
	public void generatePointage7711withOne7715_Having1ShiftCovering0h00Hand1h45JPlus1ToOtherMonth() {

		// Given
		Date dateLundi = new LocalDate(2015, 4, 27).toDate();

		Pointage p1 = new Pointage();
		p1.setIdAgent(9005605);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2015, 4, 30, 21, 0, 0).toDate());
		p1.setDateFin(new DateTime(2015, 5, 1, 5, 0, 0).toDate());
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
		assertEquals(new LocalDate(2015, 4, 30).toDate(), result.get(0).getDateDebut());
		assertEquals(480, (int) result.get(0).getQuantite().intValue());
		assertEquals(9005605, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}

	// bug/evol #16622
	// donnees de PROD
	@Test
	public void generatePointage7711withOne7715_Having1ShiftInDailyHours() {

		// Given
		RefPrime pr1 = new RefPrime();
		pr1.setNoRubr(7715);

		Date dateLundi = new LocalDate(2015, 6, 22).toDate();

		Pointage p1 = new Pointage();
		p1.setIdAgent(9002252);
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2015, 6, 24, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2015, 6, 24, 18, 0, 0).toDate());
		p1.setType(prime);
		p1.setRefPrime(pr1);

		Date dateLundi2 = new LocalDate(2015, 6, 29).toDate();

		Pointage p2 = new Pointage();
		p2.setIdAgent(9002252);
		p2.setDateLundi(dateLundi2);
		p2.setDateDebut(new DateTime(2015, 7, 1, 8, 0, 0).toDate());
		p2.setDateFin(new DateTime(2015, 7, 1, 17, 30, 0).toDate());
		p2.setType(prime);
		p2.setRefPrime(pr1);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, RefTypePointageEnum.PRIME.getValue())).thenReturn(prime);

		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		RefPrime rp7711 = new RefPrime();
		rp7711.setNoRubr(7711);

		// When
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7711, Arrays.asList(p1, p2));

		// Then
		assertEquals(0, result.size());
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
		assertEquals(new LocalDate(2013, 7, 14).toDate(), result.get(0).getDateDebut());
		assertEquals(240, (int) result.get(0).getQuantite().intValue());
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

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isHoliday(new LocalDate(2013, 7, 13))).thenReturn(true);

		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);

		RefPrime rp7712 = new RefPrime();
		rp7712.setNoRubr(7712);

		// When
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7712, Arrays.asList(p1));

		// Then
		assertEquals(1, result.size());
		assertEquals(rp7712, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 13).toDate(), result.get(0).getDateDebut());
		assertEquals(240, (int) result.get(0).getQuantite().intValue());
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
		assertEquals(1, (int) result.get(0).getQuantite().intValue());
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
		assertEquals(1, (int) result.get(0).getQuantite().intValue());
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
		assertEquals(1, (int) result.get(0).getQuantite().intValue());
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
		assertEquals(2, (int) result.get(0).getQuantite().intValue());
		assertEquals(9008767, (int) result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
	}

	@Test
	public void generatePointageTID_7720_7721_7722_noResult_noPrimeOfAgent() {

		Date dateLundi = new DateTime(2015, 10, 5, 0, 0, 0).toDate();
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();
		AgentStatutEnum statut = AgentStatutEnum.F;
		Integer idAgentRH = 9005138;
		Integer idAgent = 9002174;

		List<Pointage> listPointages = new ArrayList<Pointage>();

		List<Integer> norubrs = new ArrayList<Integer>();
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getPrimePointagesByAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(norubrs);

		List<RefPrime> listRefPrime = new ArrayList<RefPrime>();
		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getRefPrimes(norubrs, statut)).thenReturn(listRefPrime);

		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);

		List<Pointage> result = service.generatePointageTID_7720_7721_7722(idAgentRH, idAgent, statut, dateLundi, listPointages);

		assertEquals(0, result.size());
	}

	@Test
	public void generatePointageTID_7720_7721_7722_noAbs_noHSup() {

		Date dateLundi = new DateTime(2015, 10, 5, 0, 0, 0).toDate();
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();
		AgentStatutEnum statut = AgentStatutEnum.F;
		Integer idAgentRH = 9005138;
		Integer idAgent = 9002174;

		List<Pointage> listPointages = new ArrayList<Pointage>();

		BaseHorairePointageDto baseDto = new BaseHorairePointageDto();
		baseDto.setHeureLundi(8.0);
		baseDto.setHeureMardi(8.0);
		baseDto.setHeureMercredi(8.0);
		baseDto.setHeureJeudi(8.0);
		baseDto.setHeureVendredi(7.0);
		baseDto.setHeureSamedi(0.0);
		baseDto.setHeureDimanche(0.0);
		baseDto.setBaseCalculee(39.0);

		List<Integer> norubrs = new ArrayList<Integer>();
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getPrimePointagesByAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(norubrs);
		Mockito.when(sirhWsConsumer.getBaseHorairePointageAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(baseDto);

		RefPrime prime = new RefPrime();
		prime.setNoRubr(7720);

		List<RefPrime> listRefPrime = new ArrayList<RefPrime>();
		listRefPrime.add(prime);

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getRefPrimes(norubrs, statut)).thenReturn(listRefPrime);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(8.0)).thenReturn(8 * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(7.0)).thenReturn(7 * 60);

		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		List<Pointage> result = service.generatePointageTID_7720_7721_7722(idAgentRH, idAgent, statut, dateLundi, listPointages);

		assertEquals(5, result.size());
		// lundi
		assertEquals(new DateTime(dateLundi).plusDays(0).toDate(), result.get(0).getDateDebut());
		assertEquals(8 * 60, result.get(0).getQuantite().intValue());
		// mardi
		assertEquals(new DateTime(dateLundi).plusDays(1).toDate(), result.get(1).getDateDebut());
		assertEquals(8 * 60, result.get(1).getQuantite().intValue());
		// mercredi
		assertEquals(new DateTime(dateLundi).plusDays(2).toDate(), result.get(2).getDateDebut());
		assertEquals(8 * 60, result.get(2).getQuantite().intValue());
		// jeudi
		assertEquals(new DateTime(dateLundi).plusDays(3).toDate(), result.get(3).getDateDebut());
		assertEquals(8 * 60, result.get(3).getQuantite().intValue());
		// vendredi
		assertEquals(new DateTime(dateLundi).plusDays(4).toDate(), result.get(4).getDateDebut());
		assertEquals(7 * 60, result.get(4).getQuantite().intValue());
	}

	@Test
	public void generatePointageTID_7720_7721_7722_withMaladieAndAbsAndHSup() {

		Date dateLundi = new DateTime(2015, 10, 5, 0, 0, 0).toDate();
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();
		AgentStatutEnum statut = AgentStatutEnum.F;
		Integer idAgentRH = 9005138;
		Integer idAgent = 9002174;

		Pointage ptgHSup = new Pointage();
		ptgHSup.setType(hSup);
		ptgHSup.setDateDebut(new DateTime(2015, 10, 11, 8, 0, 0).toDate());
		ptgHSup.setDateFin(new DateTime(2015, 10, 11, 11, 0, 0).toDate());

		Pointage absencePtg = new Pointage();
		absencePtg.setType(abs);
		absencePtg.setDateDebut(new DateTime(2015, 10, 9, 9, 0, 0).toDate());
		absencePtg.setDateFin(new DateTime(2015, 10, 9, 11, 0, 0).toDate());

		List<Pointage> listPointages = new ArrayList<Pointage>();
		listPointages.add(ptgHSup);
		listPointages.add(absencePtg);

		BaseHorairePointageDto baseDto = new BaseHorairePointageDto();
		baseDto.setHeureLundi(8.0);
		baseDto.setHeureMardi(8.0);
		baseDto.setHeureMercredi(8.0);
		baseDto.setHeureJeudi(8.0);
		baseDto.setHeureVendredi(7.0);
		baseDto.setHeureSamedi(0.0);
		baseDto.setHeureDimanche(0.0);
		baseDto.setBaseCalculee(39.0);

		List<Integer> norubrs = new ArrayList<Integer>();
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getPrimePointagesByAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(norubrs);
		Mockito.when(sirhWsConsumer.getBaseHorairePointageAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(baseDto);

		RefPrime prime = new RefPrime();
		prime.setNoRubr(7720);

		List<RefPrime> listRefPrime = new ArrayList<RefPrime>();
		listRefPrime.add(prime);

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getRefPrimes(norubrs, statut)).thenReturn(listRefPrime);

		DemandeDto maladieDto = new DemandeDto();
		maladieDto.setDateDebut(new DateTime(2015, 10, 6, 0, 0, 0).toDate());
		maladieDto.setDateFin(new DateTime(2015, 10, 6, 23, 59, 59).toDate());
		List<DemandeDto> listMaladies = new ArrayList<DemandeDto>();
		listMaladies.add(maladieDto);

		DemandeDto demandeDto = new DemandeDto();
		demandeDto.setDateDebut(new DateTime(2015, 10, 7, 0, 0, 0).toDate());
		demandeDto.setDateFin(new DateTime(2015, 10, 7, 11, 59, 59).toDate());

		List<DemandeDto> listConges = new ArrayList<DemandeDto>();
		listConges.add(demandeDto);

		RefTypeSaisiDto typeConge = new RefTypeSaisiDto();
		typeConge.setUniteDecompte("jours");

		List<RefTypeSaisiDto> listTypeAbsence = new ArrayList<RefTypeSaisiDto>();
		listTypeAbsence.add(typeConge);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(listConges);
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(listMaladies);
		Mockito.when(absWsConsumer.getTypeSaisiAbsence(demandeDto.getIdTypeDemande())).thenReturn(listTypeAbsence);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(8.0)).thenReturn(8 * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(7.0)).thenReturn(7 * 60);

		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		List<Pointage> result = service.generatePointageTID_7720_7721_7722(idAgentRH, idAgent, statut, dateLundi, listPointages);

		assertEquals(5, result.size());
		// lundi
		assertEquals(new DateTime(dateLundi).plusDays(0).toDate(), result.get(0).getDateDebut());
		assertEquals(8 * 60, result.get(0).getQuantite().intValue());
		// mercredi
		assertEquals(new DateTime(dateLundi).plusDays(2).toDate(), result.get(1).getDateDebut());
		assertEquals(4 * 60, result.get(1).getQuantite().intValue());
		// jeudi
		assertEquals(new DateTime(dateLundi).plusDays(3).toDate(), result.get(2).getDateDebut());
		assertEquals(8 * 60, result.get(2).getQuantite().intValue());
		// vendredi
		assertEquals(new DateTime(dateLundi).plusDays(4).toDate(), result.get(3).getDateDebut());
		assertEquals(5 * 60, result.get(3).getQuantite().intValue());
		// dimanche
		assertEquals(new DateTime(dateLundi).plusDays(6).toDate(), result.get(4).getDateDebut());
		assertEquals(3 * 60, result.get(4).getQuantite().intValue());
	}

	/**
	 * Renfort de garde complet tous les jours sans jour ferie/chome
	 */
	@Test
	public void generatePointage7717_RenfortGarde_cas1() {

		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2015, 11, 16, 0, 0, 0).toDate();

		RefPrime refPrime = new RefPrime();
		refPrime.setNoRubr(VentilationPrimeService.PRIME_RENFORT_GARDE);

		Pointage primeLundi = new Pointage();
		primeLundi.setDateDebut(new DateTime(2015, 11, 16, 8, 0, 0).toDate());
		primeLundi.setDateFin(new DateTime(2015, 11, 17, 8, 0, 0).toDate());
		primeLundi.setRefPrime(refPrime);
		primeLundi.setDateLundi(dateLundi);
		primeLundi.setType(prime);

		Pointage primeMardi = new Pointage();
		primeMardi.setDateDebut(new DateTime(2015, 11, 17, 8, 0, 0).toDate());
		primeMardi.setDateFin(new DateTime(2015, 11, 18, 8, 0, 0).toDate());
		primeMardi.setRefPrime(refPrime);
		primeMardi.setDateLundi(dateLundi);
		primeMardi.setType(prime);

		Pointage primeMercredi = new Pointage();
		primeMercredi.setDateDebut(new DateTime(2015, 11, 18, 8, 0, 0).toDate());
		primeMercredi.setDateFin(new DateTime(2015, 11, 19, 8, 0, 0).toDate());
		primeMercredi.setRefPrime(refPrime);
		primeMercredi.setDateLundi(dateLundi);
		primeMercredi.setType(prime);

		Pointage primeJeudi = new Pointage();
		primeJeudi.setDateDebut(new DateTime(2015, 11, 19, 8, 0, 0).toDate());
		primeJeudi.setDateFin(new DateTime(2015, 11, 20, 8, 0, 0).toDate());
		primeJeudi.setRefPrime(refPrime);
		primeJeudi.setDateLundi(dateLundi);
		primeJeudi.setType(prime);

		Pointage primeVendredi = new Pointage();
		primeVendredi.setDateDebut(new DateTime(2015, 11, 20, 8, 0, 0).toDate());
		primeVendredi.setDateFin(new DateTime(2015, 11, 21, 8, 0, 0).toDate());
		primeVendredi.setRefPrime(refPrime);
		primeVendredi.setDateLundi(dateLundi);
		primeVendredi.setType(prime);

		Pointage primeSamedi = new Pointage();
		primeSamedi.setDateDebut(new DateTime(2015, 11, 21, 8, 0, 0).toDate());
		primeSamedi.setDateFin(new DateTime(2015, 11, 22, 8, 0, 0).toDate());
		primeSamedi.setRefPrime(refPrime);
		primeSamedi.setDateLundi(dateLundi);
		primeSamedi.setType(prime);

		Pointage primeDimanche = new Pointage();
		primeDimanche.setDateDebut(new DateTime(2015, 11, 22, 8, 0, 0).toDate());
		primeDimanche.setDateFin(new DateTime(2015, 11, 23, 8, 0, 0).toDate());
		primeDimanche.setRefPrime(refPrime);
		primeDimanche.setDateLundi(dateLundi);
		primeDimanche.setType(prime);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.add(primeLundi);
		pointages.add(primeMardi);
		pointages.add(primeMercredi);
		pointages.add(primeJeudi);
		pointages.add(primeVendredi);
		pointages.add(primeSamedi);
		pointages.add(primeDimanche);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);

		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);

		List<PointageCalcule> result = service.generatePointage7717_RenfortGarde(idAgent, dateLundi, pointages);

		assertEquals(result.size(), 7);
		assertEquals(result.get(0).getDateDebut(), primeLundi.getDateDebut());
		assertEquals(result.get(0).getQuantite().intValue(), 12 * 60);
		assertEquals(result.get(1).getDateDebut(), primeMardi.getDateDebut());
		assertEquals(result.get(1).getQuantite().intValue(), 12 * 60);
		assertEquals(result.get(2).getDateDebut(), primeMercredi.getDateDebut());
		assertEquals(result.get(2).getQuantite().intValue(), 12 * 60);
		assertEquals(result.get(3).getDateDebut(), primeJeudi.getDateDebut());
		assertEquals(result.get(3).getQuantite().intValue(), 12 * 60);
		assertEquals(result.get(4).getDateDebut(), primeVendredi.getDateDebut());
		assertEquals(result.get(4).getQuantite().intValue(), 12 * 60);
		assertEquals(result.get(5).getDateDebut(), primeSamedi.getDateDebut());
		assertEquals(result.get(5).getQuantite().intValue(), 14 * 60);
		assertEquals(result.get(6).getDateDebut(), primeDimanche.getDateDebut());
		assertEquals(result.get(6).getQuantite().intValue(), 16 * 60);
	}

	/**
	 * Renfort de garde : - complet lundi, mardi, vendredi - ferie mercredi -
	 * 16h jeudi - 12h samedi
	 */
	@Test
	public void generatePointage7717_RenfortGarde_cas2() {

		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2015, 11, 16, 0, 0, 0).toDate();

		RefPrime refPrime = new RefPrime();
		refPrime.setNoRubr(VentilationPrimeService.PRIME_RENFORT_GARDE);

		Pointage primeLundi = new Pointage();
		primeLundi.setDateDebut(new DateTime(2015, 11, 16, 8, 0, 0).toDate());
		primeLundi.setDateFin(new DateTime(2015, 11, 17, 8, 0, 0).toDate());
		primeLundi.setRefPrime(refPrime);
		primeLundi.setDateLundi(dateLundi);
		primeLundi.setType(prime);

		Pointage primeMardi = new Pointage();
		primeMardi.setDateDebut(new DateTime(2015, 11, 17, 8, 0, 0).toDate());
		primeMardi.setDateFin(new DateTime(2015, 11, 18, 8, 0, 0).toDate());
		primeMardi.setRefPrime(refPrime);
		primeMardi.setDateLundi(dateLundi);
		primeMardi.setType(prime);

		Pointage primeMercredi = new Pointage();
		primeMercredi.setDateDebut(new DateTime(2015, 11, 18, 8, 0, 0).toDate());
		primeMercredi.setDateFin(new DateTime(2015, 11, 19, 8, 0, 0).toDate());
		primeMercredi.setRefPrime(refPrime);
		primeMercredi.setDateLundi(dateLundi);
		primeMercredi.setType(prime);

		Pointage primeJeudi = new Pointage();
		primeJeudi.setDateDebut(new DateTime(2015, 11, 19, 8, 0, 0).toDate());
		primeJeudi.setDateFin(new DateTime(2015, 11, 20, 0, 0, 0).toDate());
		primeJeudi.setRefPrime(refPrime);
		primeJeudi.setDateLundi(dateLundi);
		primeJeudi.setType(prime);

		Pointage primeVendredi = new Pointage();
		primeVendredi.setDateDebut(new DateTime(2015, 11, 20, 8, 0, 0).toDate());
		primeVendredi.setDateFin(new DateTime(2015, 11, 21, 8, 0, 0).toDate());
		primeVendredi.setRefPrime(refPrime);
		primeVendredi.setDateLundi(dateLundi);
		primeVendredi.setType(prime);

		Pointage primeSamedi = new Pointage();
		primeSamedi.setDateDebut(new DateTime(2015, 11, 21, 8, 0, 0).toDate());
		primeSamedi.setDateFin(new DateTime(2015, 11, 21, 20, 0, 0).toDate());
		primeSamedi.setRefPrime(refPrime);
		primeSamedi.setDateLundi(dateLundi);
		primeSamedi.setType(prime);

		Pointage primeDimanche = new Pointage();
		primeDimanche.setDateDebut(new DateTime(2015, 11, 22, 8, 0, 0).toDate());
		primeDimanche.setDateFin(new DateTime(2015, 11, 23, 8, 0, 0).toDate());
		primeDimanche.setRefPrime(refPrime);
		primeDimanche.setDateLundi(dateLundi);
		primeDimanche.setType(prime);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.add(primeLundi);
		pointages.add(primeMardi);
		pointages.add(primeMercredi);
		pointages.add(primeJeudi);
		pointages.add(primeVendredi);
		pointages.add(primeSamedi);
		pointages.add(primeDimanche);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(2015, 11, 18, 8, 0, 0))).thenReturn(true);

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);

		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);

		List<PointageCalcule> result = service.generatePointage7717_RenfortGarde(idAgent, dateLundi, pointages);

		assertEquals(result.size(), 7);
		assertEquals(result.get(0).getDateDebut(), primeLundi.getDateDebut());
		assertEquals(result.get(0).getQuantite().intValue(), 12 * 60);
		assertEquals(result.get(1).getDateDebut(), primeMardi.getDateDebut());
		assertEquals(result.get(1).getQuantite().intValue(), 12 * 60);
		assertEquals(result.get(2).getDateDebut(), primeMercredi.getDateDebut());
		assertEquals(result.get(2).getQuantite().intValue(), 16 * 60);
		assertEquals(result.get(3).getDateDebut(), primeJeudi.getDateDebut());
		assertEquals(result.get(3).getQuantite().intValue(), new Double(16.0 / 24.0 * 12.0 * 60.0).intValue());
		assertEquals(result.get(4).getDateDebut(), primeVendredi.getDateDebut());
		assertEquals(result.get(4).getQuantite().intValue(), 12 * 60);
		assertEquals(result.get(5).getDateDebut(), primeSamedi.getDateDebut());
		assertEquals(result.get(5).getQuantite().intValue(), new Double(12.0 / 24.0 * 14.0 * 60.0).intValue());
		assertEquals(result.get(6).getDateDebut(), primeDimanche.getDateDebut());
		assertEquals(result.get(6).getQuantite().intValue(), 16 * 60);
	}

	@Test
	public void generatePointage7718_19_IndemniteForfaitaireTravailDPM_ChoixIndemnite() {

		Integer idAgent = 9005138;
		AgentStatutEnum statut = AgentStatutEnum.F;
		Date dateLundi = new DateTime(2016, 6, 6, 0, 0, 0).toDate();
		List<Pointage> agentPointages = new ArrayList<Pointage>();

		RefPrime refPrime = new RefPrime();
		refPrime.setNoRubr(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM);

		Pointage ptgLundi = new Pointage();
		ptgLundi.setDateDebut(new DateTime(2016, 6, 6, 8, 0, 0).toDate());
		ptgLundi.setDateFin(new DateTime(2016, 6, 6, 15, 0, 0).toDate());
		ptgLundi.setDateLundi(dateLundi);
		ptgLundi.setType(prime);
		ptgLundi.setRefPrime(refPrime);

		Pointage ptgSamedi = new Pointage();
		ptgSamedi.setDateDebut(new DateTime(2016, 6, 11, 8, 0, 0).toDate());
		ptgSamedi.setDateFin(new DateTime(2016, 6, 11, 13, 0, 0).toDate());
		ptgSamedi.setDateLundi(dateLundi);
		ptgSamedi.setType(prime);
		ptgSamedi.setRefPrime(refPrime);

		Pointage ptgDimanche = new Pointage();
		ptgDimanche.setDateDebut(new DateTime(2016, 6, 12, 8, 0, 0).toDate());
		ptgDimanche.setDateFin(new DateTime(2016, 6, 12, 18, 0, 0).toDate());
		ptgDimanche.setDateLundi(dateLundi);
		ptgDimanche.setType(prime);
		ptgDimanche.setRefPrime(refPrime);

		Pointage ptgJourFerie = new Pointage();
		ptgJourFerie.setDateDebut(new DateTime(2016, 6, 10, 8, 0, 0).toDate());
		ptgJourFerie.setDateFin(new DateTime(2016, 6, 10, 12, 0, 0).toDate());
		ptgJourFerie.setDateLundi(dateLundi);
		ptgJourFerie.setType(prime);
		ptgJourFerie.setRefPrime(refPrime);

		Pointage ptgJourFerieMoins4h = new Pointage();
		ptgJourFerieMoins4h.setDateDebut(new DateTime(2016, 6, 9, 8, 0, 0).toDate());
		ptgJourFerieMoins4h.setDateFin(new DateTime(2016, 6, 9, 11, 45, 0).toDate());
		ptgJourFerieMoins4h.setDateLundi(dateLundi);
		ptgJourFerieMoins4h.setType(prime);
		ptgJourFerieMoins4h.setRefPrime(refPrime);

		agentPointages.add(ptgLundi);
		agentPointages.add(ptgSamedi);
		agentPointages.add(ptgDimanche);
		agentPointages.add(ptgJourFerie);
		agentPointages.add(ptgJourFerieMoins4h);

		List<Integer> norubrs = new ArrayList<Integer>();
		norubrs.add(7718);
		norubrs.add(7719);
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(2015, 11, 18, 8, 0, 0))).thenReturn(true);
		Mockito.when(sirhWsConsumer.getPrimePointagesByAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(norubrs);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(dateLundi).toLocalDate())).thenReturn(false);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(dateLundi).plusDays(3).toLocalDate())).thenReturn(true);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(dateLundi).plusDays(4).toLocalDate())).thenReturn(true);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(dateLundi).plusDays(5).toLocalDate())).thenReturn(false);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(dateLundi).plusDays(6).toLocalDate())).thenReturn(false);

		RefPrime refPrime7718_samedi = new RefPrime();
		refPrime7718_samedi.setNoRubr(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM_SAMEDI);

		RefPrime refPrime7719_DJF = new RefPrime();
		refPrime7719_DJF.setNoRubr(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM_DJF);

		List<RefPrime> refPrimes = new ArrayList<RefPrime>();
		refPrimes.add(refPrime7718_samedi);
		refPrimes.add(refPrime7719_DJF);

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getRefPrimesCalculees(norubrs, statut)).thenReturn(refPrimes);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.calculMinutesPointageInInterval(ptgLundi, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
				new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0))).thenReturn(7 * 60);
		Mockito.when(helperService.calculMinutesPointageInInterval(ptgSamedi, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
				new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0))).thenReturn(5 * 60);
		Mockito.when(
				helperService.calculMinutesPointageInInterval(ptgDimanche, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
						new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0)))
				.thenReturn(10 * 60);
		Mockito.when(
				helperService.calculMinutesPointageInInterval(ptgJourFerie, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
						new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0)))
				.thenReturn(4 * 60);
		Mockito.when(helperService.calculMinutesPointageInInterval(ptgJourFerieMoins4h,
				new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
				new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0))).thenReturn(new Double(3.75 * 60).intValue());

		DpmIndemChoixAgent choixAgent = new DpmIndemChoixAgent();
		choixAgent.setChoixRecuperation(false);
		choixAgent.setChoixIndemnite(true);

		IDpmRepository dpmRepository = Mockito.mock(IDpmRepository.class);
		Mockito.when(dpmRepository.getDpmIndemChoixAgent(idAgent, new DateTime(ptgLundi.getDateDebut()).getYear())).thenReturn(choixAgent);

		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "dpmRepository", dpmRepository);

		List<PointageCalcule> result = service.calculatePointagesForAgentAndWeek(idAgent, statut, dateLundi, agentPointages);

		assertEquals(result.size(), 3);
		// prime samedi
		assertEquals(result.get(0).getQuantite(), new Double(1.25));
		assertEquals(result.get(0).getRefPrime(), refPrime7718_samedi);
		// prime jour ferie
		assertEquals(result.get(2).getQuantite(), new Double(1.0));
		assertEquals(result.get(2).getRefPrime(), refPrime7719_DJF);
		// prime dimanche
		assertEquals(result.get(1).getQuantite(), new Double(2.5));
		assertEquals(result.get(1).getRefPrime(), refPrime7719_DJF);
	}

	@Test
	public void generatePointage7718_19_IndemniteForfaitaireTravailDPM_ChoixRecup() {

		Integer idAgent = 9005138;
		AgentStatutEnum statut = AgentStatutEnum.F;
		Date dateLundi = new DateTime(2016, 6, 6, 0, 0, 0).toDate();
		List<Pointage> agentPointages = new ArrayList<Pointage>();

		RefPrime refPrime = new RefPrime();
		refPrime.setNoRubr(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM);

		Pointage ptgHsupLundi = new Pointage();
		ptgHsupLundi.setDateDebut(new DateTime(2016, 6, 6, 2, 0, 0).toDate());
		ptgHsupLundi.setDateFin(new DateTime(2016, 6, 6, 4, 0, 0).toDate());
		ptgHsupLundi.setDateLundi(dateLundi);
		ptgHsupLundi.setType(hSup);
		ptgHsupLundi.setHeureSupRappelService(false);

		Pointage ptgLundi = new Pointage();
		ptgLundi.setDateDebut(new DateTime(2016, 6, 6, 8, 0, 0).toDate());
		ptgLundi.setDateFin(new DateTime(2016, 6, 6, 15, 0, 0).toDate());
		ptgLundi.setDateLundi(dateLundi);
		ptgLundi.setType(prime);
		ptgLundi.setRefPrime(refPrime);

		Pointage ptgHsupSamedi = new Pointage();
		ptgHsupSamedi.setDateDebut(new DateTime(2016, 6, 11, 2, 0, 0).toDate());
		ptgHsupSamedi.setDateFin(new DateTime(2016, 6, 11, 5, 0, 0).toDate());
		ptgHsupSamedi.setDateLundi(dateLundi);
		ptgHsupSamedi.setType(hSup);
		ptgHsupSamedi.setHeureSupRappelService(true);

		Pointage ptgSamedi = new Pointage();
		ptgSamedi.setDateDebut(new DateTime(2016, 6, 11, 8, 0, 0).toDate());
		ptgSamedi.setDateFin(new DateTime(2016, 6, 11, 13, 0, 0).toDate());
		ptgSamedi.setDateLundi(dateLundi);
		ptgSamedi.setType(prime);
		ptgSamedi.setRefPrime(refPrime);

		Pointage ptgDimanche = new Pointage();
		ptgDimanche.setDateDebut(new DateTime(2016, 6, 12, 8, 0, 0).toDate());
		ptgDimanche.setDateFin(new DateTime(2016, 6, 12, 18, 0, 0).toDate());
		ptgDimanche.setDateLundi(dateLundi);
		ptgDimanche.setType(prime);
		ptgDimanche.setRefPrime(refPrime);

		Pointage ptgJourFerie = new Pointage();
		ptgJourFerie.setDateDebut(new DateTime(2016, 6, 10, 8, 0, 0).toDate());
		ptgJourFerie.setDateFin(new DateTime(2016, 6, 10, 12, 0, 0).toDate());
		ptgJourFerie.setDateLundi(dateLundi);
		ptgJourFerie.setType(prime);
		ptgJourFerie.setRefPrime(refPrime);

		Pointage ptgJourFerieMoins4h = new Pointage();
		ptgJourFerieMoins4h.setDateDebut(new DateTime(2016, 6, 9, 8, 0, 0).toDate());
		ptgJourFerieMoins4h.setDateFin(new DateTime(2016, 6, 9, 11, 45, 0).toDate());
		ptgJourFerieMoins4h.setDateLundi(dateLundi);
		ptgJourFerieMoins4h.setType(prime);
		ptgJourFerieMoins4h.setRefPrime(refPrime);

		agentPointages.add(ptgLundi);
		agentPointages.add(ptgHsupLundi);
		agentPointages.add(ptgSamedi);
		agentPointages.add(ptgHsupSamedi);
		agentPointages.add(ptgDimanche);
		agentPointages.add(ptgJourFerie);
		agentPointages.add(ptgJourFerieMoins4h);

		List<Integer> norubrs = new ArrayList<Integer>();
		norubrs.add(7718);
		norubrs.add(7719);
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(2015, 11, 18, 8, 0, 0))).thenReturn(true);
		Mockito.when(sirhWsConsumer.getPrimePointagesByAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(norubrs);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(dateLundi).toLocalDate())).thenReturn(false);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(dateLundi).plusDays(3).toLocalDate())).thenReturn(true);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(dateLundi).plusDays(4).toLocalDate())).thenReturn(true);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(dateLundi).plusDays(5).toLocalDate())).thenReturn(false);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(dateLundi).plusDays(6).toLocalDate())).thenReturn(false);

		RefPrime refPrime7718_samedi = new RefPrime();
		refPrime7718_samedi.setNoRubr(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM_SAMEDI);

		RefPrime refPrime7719_DJF = new RefPrime();
		refPrime7719_DJF.setNoRubr(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM_DJF);

		List<RefPrime> refPrimes = new ArrayList<RefPrime>();
		refPrimes.add(refPrime7718_samedi);
		refPrimes.add(refPrime7719_DJF);

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getRefPrimesCalculees(norubrs, statut)).thenReturn(refPrimes);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.calculMinutesPointageInInterval(ptgLundi, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
				new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0))).thenReturn(7 * 60);
		Mockito.when(helperService.calculMinutesPointageInInterval(ptgSamedi, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
				new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0))).thenReturn(5 * 60);
		Mockito.when(
				helperService.calculMinutesPointageInInterval(ptgDimanche, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
						new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0)))
				.thenReturn(10 * 60);
		Mockito.when(
				helperService.calculMinutesPointageInInterval(ptgJourFerie, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
						new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0)))
				.thenReturn(4 * 60);
		Mockito.when(helperService.calculMinutesPointageInInterval(ptgJourFerieMoins4h,
				new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
				new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0))).thenReturn(new Double(3.75 * 60).intValue());

		DpmIndemChoixAgent choixAgent = new DpmIndemChoixAgent();
		choixAgent.setChoixRecuperation(true);
		choixAgent.setChoixIndemnite(false);

		IDpmRepository dpmRepository = Mockito.mock(IDpmRepository.class);
		Mockito.when(dpmRepository.getDpmIndemChoixAgent(idAgent, new DateTime(ptgLundi.getDateDebut()).getYear())).thenReturn(choixAgent);

		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "dpmRepository", dpmRepository);

		List<PointageCalcule> result = service.calculatePointagesForAgentAndWeek(idAgent, statut, dateLundi, agentPointages);

		assertEquals(result.size(), 2);
		// prime jour ferie
		assertEquals(result.get(1).getQuantite(), new Double(1.0));
		assertEquals(result.get(1).getRefPrime(), refPrime7719_DJF);
		// prime dimanche
		assertEquals(result.get(0).getQuantite(), new Double(2.5));
		assertEquals(result.get(0).getRefPrime(), refPrime7719_DJF);
	}

	@Test
	public void generatePointage7718_19_IndemniteForfaitaireTravailDPM_choixAgentRecup() {

		Integer idAgent = 9005138;
		AgentStatutEnum statut = AgentStatutEnum.F;
		Date dateLundi = new DateTime(2016, 6, 6, 0, 0, 0).toDate();
		List<Pointage> agentPointages = new ArrayList<Pointage>();

		RefPrime refPrime = new RefPrime();
		refPrime.setNoRubr(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM_SAMEDI);

		Pointage ptgLundi = new Pointage();
		ptgLundi.setDateDebut(new DateTime(2016, 6, 6, 8, 0, 0).toDate());
		ptgLundi.setDateFin(new DateTime(2016, 6, 6, 15, 0, 0).toDate());
		ptgLundi.setDateLundi(dateLundi);
		ptgLundi.setType(hSup);

		Pointage ptgSamedi = new Pointage();
		ptgSamedi.setDateDebut(new DateTime(2016, 6, 11, 8, 0, 0).toDate());
		ptgSamedi.setDateFin(new DateTime(2016, 6, 11, 13, 0, 0).toDate());
		ptgSamedi.setDateLundi(dateLundi);
		ptgSamedi.setType(hSup);

		Pointage ptgDimanche = new Pointage();
		ptgDimanche.setDateDebut(new DateTime(2016, 6, 12, 8, 0, 0).toDate());
		ptgDimanche.setDateFin(new DateTime(2016, 6, 12, 18, 0, 0).toDate());
		ptgDimanche.setDateLundi(dateLundi);
		ptgDimanche.setType(hSup);

		Pointage ptgJourFerie = new Pointage();
		ptgJourFerie.setDateDebut(new DateTime(2016, 6, 10, 8, 0, 0).toDate());
		ptgJourFerie.setDateFin(new DateTime(2016, 6, 10, 12, 0, 0).toDate());
		ptgJourFerie.setDateLundi(dateLundi);
		ptgJourFerie.setType(hSup);

		Pointage ptgJourFerieMoins4h = new Pointage();
		ptgJourFerieMoins4h.setDateDebut(new DateTime(2016, 6, 9, 8, 0, 0).toDate());
		ptgJourFerieMoins4h.setDateFin(new DateTime(2016, 6, 9, 11, 45, 0).toDate());
		ptgJourFerieMoins4h.setDateLundi(dateLundi);
		ptgJourFerieMoins4h.setType(hSup);

		agentPointages.add(ptgLundi);
		agentPointages.add(ptgSamedi);
		agentPointages.add(ptgDimanche);
		agentPointages.add(ptgJourFerie);
		agentPointages.add(ptgJourFerieMoins4h);

		List<Integer> norubrs = new ArrayList<Integer>();
		norubrs.add(7718);
		norubrs.add(7719);
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();
		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.isHoliday(new DateTime(2015, 11, 18, 8, 0, 0))).thenReturn(true);
		Mockito.when(sirhWsConsumer.getPrimePointagesByAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(norubrs);

		RefPrime refPrime7718_samedi = new RefPrime();
		refPrime7718_samedi.setNoRubr(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM_SAMEDI);

		RefPrime refPrime7719_DJF = new RefPrime();
		refPrime7719_DJF.setNoRubr(VentilationPrimeService.INDEMNITE_FORFAITAIRE_TRAVAIL_DPM_DJF);

		List<RefPrime> refPrimes = new ArrayList<RefPrime>();
		refPrimes.add(refPrime7718_samedi);
		refPrimes.add(refPrime7719_DJF);

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getRefPrimesCalculees(norubrs, statut)).thenReturn(refPrimes);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.calculMinutesPointageInInterval(ptgLundi, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
				new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0))).thenReturn(7 * 60);
		Mockito.when(helperService.calculMinutesPointageInInterval(ptgSamedi, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
				new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0))).thenReturn(5 * 60);
		Mockito.when(
				helperService.calculMinutesPointageInInterval(ptgDimanche, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
						new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0)))
				.thenReturn(10 * 60);
		Mockito.when(
				helperService.calculMinutesPointageInInterval(ptgJourFerie, new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
						new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0)))
				.thenReturn(4 * 60);
		Mockito.when(helperService.calculMinutesPointageInInterval(ptgJourFerieMoins4h,
				new LocalTime(PointageCalculeService.HEURE_JOUR_DEBUT_PRIME_DPM, 0, 0),
				new LocalTime(PointageCalculeService.HEURE_JOUR_FIN_PRIME_DPM, 0, 0))).thenReturn(new Double(3.75 * 60).intValue());

		DpmIndemChoixAgent choixAgent = new DpmIndemChoixAgent();
		choixAgent.setChoixRecuperation(true);
		choixAgent.setChoixIndemnite(false);

		IDpmRepository dpmRepository = Mockito.mock(IDpmRepository.class);
		Mockito.when(dpmRepository.getDpmIndemChoixAgent(idAgent, new DateTime(ptgLundi.getDateDebut()).getYear())).thenReturn(choixAgent);

		Mockito.when(sirhWsConsumer.isJourFerie(new DateTime(dateLundi))).thenReturn(false);
		Mockito.when(sirhWsConsumer.isJourFerie(new DateTime(dateLundi).plusDays(3))).thenReturn(true);
		Mockito.when(sirhWsConsumer.isJourFerie(new DateTime(dateLundi).plusDays(4))).thenReturn(true);
		Mockito.when(sirhWsConsumer.isJourFerie(new DateTime(dateLundi).plusDays(5))).thenReturn(false);
		Mockito.when(sirhWsConsumer.isJourFerie(new DateTime(dateLundi).plusDays(6))).thenReturn(false);

		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "dpmRepository", dpmRepository);

		List<PointageCalcule> result = service.calculatePointagesForAgentAndWeek(idAgent, statut, dateLundi, agentPointages);

		assertEquals(result.size(), 0);
	}

}
