package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefTypeSaisiDto;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.SpabsenId;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

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
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7711,
				Arrays.asList(p1));

		// Then
		assertEquals(1, result.size());
		assertEquals(rp7711, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 9).toDate(), result.get(0).getDateDebut());
		assertEquals(180, (int) result.get(0).getQuantite());
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
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7711,
				Arrays.asList(p1));

		// Then
		assertEquals(1, result.size());
		assertEquals(rp7711, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 9).toDate(), result.get(0).getDateDebut());
		assertEquals(8*60, (int) result.get(0).getQuantite());
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
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7711,
				Arrays.asList(p1));

		// Then
		assertEquals(1, result.size());
		assertEquals(rp7711, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 9).toDate(), result.get(0).getDateDebut());
		assertEquals(585, (int) result.get(0).getQuantite());
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
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7711,
				Arrays.asList(p1));

		// Then
		assertEquals(1, result.size());
		assertEquals(rp7711, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2015, 4, 30).toDate(), result.get(0).getDateDebut());
		assertEquals(480, (int) result.get(0).getQuantite());
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
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7711,
				Arrays.asList(p1, p2));

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
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7712,
				Arrays.asList(p1));

		// Then
		assertEquals(1, result.size());
		assertEquals(rp7712, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 14).toDate(), result.get(0).getDateDebut());
		assertEquals(240, (int) result.get(0).getQuantite());
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
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7712,
				Arrays.asList(p1));

		// Then
		assertEquals(1, result.size());
		assertEquals(rp7712, result.get(0).getRefPrime());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 13).toDate(), result.get(0).getDateDebut());
		assertEquals(240, (int) result.get(0).getQuantite());
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
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7713,
				Arrays.asList(p1));

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
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7713,
				Arrays.asList(p1));

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
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7713,
				Arrays.asList(p1));

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
		List<PointageCalcule> result = service.generatePointage7711_12_13(p1.getIdAgent(), dateLundi, rp7713,
				Arrays.asList(p1, p2, p3));

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
	
	@Test
	public void generatePointageTID_7720_7721_7722_noResult_noPrimeOfAgent() {

		Date dateLundi = new DateTime(2015,10,5,0,0,0).toDate();
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

		Date dateLundi = new DateTime(2015,10,5,0,0,0).toDate();
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
		Mockito.when(sirhWsConsumer
				.getBaseHorairePointageAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(baseDto);
		
		RefPrime prime = new RefPrime();
		prime.setNoRubr(7720);
		
		List<RefPrime> listRefPrime = new ArrayList<RefPrime>();
		listRefPrime.add(prime);
		
		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getRefPrimes(norubrs, statut)).thenReturn(listRefPrime);
		
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService
		.convertMairieNbHeuresFormatToMinutes(8.0)).thenReturn(8*60);
		Mockito.when(helperService
		.convertMairieNbHeuresFormatToMinutes(7.0)).thenReturn(7*60);
		
		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		
		List<Pointage> result = service.generatePointageTID_7720_7721_7722(idAgentRH, idAgent, statut, dateLundi, listPointages);
		
		assertEquals(5, result.size());
		// lundi
		assertEquals(new DateTime(dateLundi).plusDays(0).toDate(), result.get(0).getDateDebut());
		assertEquals(8*60, result.get(0).getQuantite().intValue());
		// mardi
		assertEquals(new DateTime(dateLundi).plusDays(1).toDate(), result.get(1).getDateDebut());
		assertEquals(8*60, result.get(1).getQuantite().intValue());
		// mercredi
		assertEquals(new DateTime(dateLundi).plusDays(2).toDate(), result.get(2).getDateDebut());
		assertEquals(8*60, result.get(2).getQuantite().intValue());
		// jeudi
		assertEquals(new DateTime(dateLundi).plusDays(3).toDate(), result.get(3).getDateDebut());
		assertEquals(8*60, result.get(3).getQuantite().intValue());
		// vendredi
		assertEquals(new DateTime(dateLundi).plusDays(4).toDate(), result.get(4).getDateDebut());
		assertEquals(7*60, result.get(4).getQuantite().intValue());
	}
	
	@Test
	public void generatePointageTID_7720_7721_7722_withMaladieAndAbsAndHSup() {

		Date dateLundi = new DateTime(2015,10,5,0,0,0).toDate();
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();
		AgentStatutEnum statut = AgentStatutEnum.F;
		Integer idAgentRH = 9005138;
		Integer idAgent = 9002174;
		
		Pointage ptgHSup = new Pointage();
		ptgHSup.setType(hSup);
		ptgHSup.setDateDebut(new DateTime(2015,10,11,8,0,0).toDate());
		ptgHSup.setDateFin(new DateTime(2015,10,11,11,0,0).toDate());
		
		Pointage absencePtg = new Pointage();
		absencePtg.setType(abs);
		absencePtg.setDateDebut(new DateTime(2015,10,9,9,0,0).toDate());
		absencePtg.setDateFin(new DateTime(2015,10,9,11,0,0).toDate());
		
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
		Mockito.when(sirhWsConsumer
				.getBaseHorairePointageAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(baseDto);
		
		RefPrime prime = new RefPrime();
		prime.setNoRubr(7720);
		
		List<RefPrime> listRefPrime = new ArrayList<RefPrime>();
		listRefPrime.add(prime);
		
		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getRefPrimes(norubrs, statut)).thenReturn(listRefPrime);
		
		SpabsenId absenId = new SpabsenId();
		absenId.setDatdeb(20151006);
		absenId.setNomatr(idAgent - 9000000);
		Spabsen absen = new Spabsen();
		absen.setId(absenId);
		absen.setDatfin(20151006);
		List<Spabsen> listSpAbsen = new ArrayList<Spabsen>();
		listSpAbsen.add(absen);
		
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getListMaladieBetween(
				idAgent, dateLundi, new DateTime(dateLundi).plusDays(7)
						.toDate())).thenReturn(listSpAbsen);
		
		DemandeDto demandeDto = new DemandeDto();
		demandeDto.setDateDebut(new DateTime(2015,10,7,0,0,0).toDate());
		demandeDto.setDateFin(new DateTime(2015,10,7,11,59,59).toDate());
		
		List<DemandeDto> listConges = new ArrayList<DemandeDto>();
		listConges.add(demandeDto);
		
		RefTypeSaisiDto typeConge = new RefTypeSaisiDto();
		typeConge.setUniteDecompte("jours");
		
		List<RefTypeSaisiDto> listTypeAbsence = new ArrayList<RefTypeSaisiDto>();
		listTypeAbsence.add(typeConge);
				
		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer
				.getListCongeWithoutCongesAnnuelsEtAnnulesBetween(idAgent,
						dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(listConges);
		Mockito.when(absWsConsumer.getTypeAbsence(demandeDto.getIdTypeDemande()))
			.thenReturn(listTypeAbsence);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService
		.convertMairieNbHeuresFormatToMinutes(8.0)).thenReturn(8*60);
		Mockito.when(helperService
		.convertMairieNbHeuresFormatToMinutes(7.0)).thenReturn(7*60);
		
		Mockito.when(helperService.getDateFromMairieInteger(absen.getId()
			.getDatdeb())).thenReturn(new DateTime(2015,10,6,0,0,0).toDate());
		Mockito.when(helperService.getDateFromMairieInteger(absen.getDatfin()))
			.thenReturn(new DateTime(2015,10,6,0,0,0).toDate());
		
		PointageCalculeService service = new PointageCalculeService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		
		List<Pointage> result = service.generatePointageTID_7720_7721_7722(idAgentRH, idAgent, statut, dateLundi, listPointages);
		
		assertEquals(5, result.size());
		// lundi
		assertEquals(new DateTime(dateLundi).plusDays(0).toDate(), result.get(0).getDateDebut());
		assertEquals(8*60, result.get(0).getQuantite().intValue());
		// mercredi
		assertEquals(new DateTime(dateLundi).plusDays(2).toDate(), result.get(1).getDateDebut());
		assertEquals(4*60, result.get(1).getQuantite().intValue());
		// jeudi
		assertEquals(new DateTime(dateLundi).plusDays(3).toDate(), result.get(2).getDateDebut());
		assertEquals(8*60, result.get(2).getQuantite().intValue());
		// vendredi
		assertEquals(new DateTime(dateLundi).plusDays(4).toDate(), result.get(3).getDateDebut());
		assertEquals(5*60, result.get(3).getQuantite().intValue());
		// dimanche
		assertEquals(new DateTime(dateLundi).plusDays(6).toDate(), result.get(4).getDateDebut());
		assertEquals(3*60, result.get(4).getQuantite().intValue());
	}
}
