package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.domain.VentilTask;
import nc.noumea.mairie.ptg.dto.CanStartVentilationDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.VentilDateDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IPointageCalculeService;
import nc.noumea.mairie.ptg.service.IVentilationAbsenceService;
import nc.noumea.mairie.ptg.service.IVentilationHSupService;
import nc.noumea.mairie.ptg.service.IVentilationPrimeService;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class VentilationServiceTest {

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
	public void removePreviousVentilations_NoTypeSelected_DeleteAllTypes() {

		// Given
		RefTypePointageEnum typePointage = null;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 4, 6).toDate());

		IVentilationRepository ventilationRepo = Mockito
				.mock(IVentilationRepository.class);

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository",
				ventilationRepo);

		// When
		service.removePreviousVentilations(ventilDate, idAgent, typePointage);

		// Then
		Mockito.verify(ventilationRepo, Mockito.times(1))
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.ABSENCE);
		Mockito.verify(ventilationRepo, Mockito.times(1))
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.H_SUP);
		Mockito.verify(ventilationRepo, Mockito.times(1))
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.PRIME);

	}

	@Test
	public void removePreviousVentilations_TypeSelected_H_SUP_Delete2Types() {

		// Given
		RefTypePointageEnum typePointage = RefTypePointageEnum.H_SUP;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 4, 6).toDate());

		IVentilationRepository ventilRepo = Mockito
				.mock(IVentilationRepository.class);

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository",
				ventilRepo);

		// When
		service.removePreviousVentilations(ventilDate, idAgent, typePointage);

		// Then
		Mockito.verify(ventilRepo, Mockito.times(1))
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.ABSENCE);
		Mockito.verify(ventilRepo, Mockito.times(1))
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.H_SUP);
		Mockito.verify(ventilRepo, Mockito.never())
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.PRIME);
	}

	@Test
	public void removePreviousVentilations_TypeSelected_ABSENCE_Delete2Types() {

		// Given
		RefTypePointageEnum typePointage = RefTypePointageEnum.ABSENCE;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 4, 6).toDate());

		IVentilationRepository ventilRepo = Mockito
				.mock(IVentilationRepository.class);

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository",
				ventilRepo);

		// When
		service.removePreviousVentilations(ventilDate, idAgent, typePointage);

		// Then
		Mockito.verify(ventilRepo, Mockito.times(1))
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.ABSENCE);
		Mockito.verify(ventilRepo, Mockito.times(1))
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.H_SUP);
		Mockito.verify(ventilRepo, Mockito.never())
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.PRIME);
	}

	@Test
	public void removePreviousVentilations_TypeSelected_PRIME_Delete1Type() {

		// Given
		RefTypePointageEnum typePointage = RefTypePointageEnum.PRIME;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 4, 6).toDate());

		IVentilationRepository ventilRepo = Mockito
				.mock(IVentilationRepository.class);

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository",
				ventilRepo);

		// When
		service.removePreviousVentilations(ventilDate, idAgent, typePointage);

		// Then
		Mockito.verify(ventilRepo, Mockito.never())
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.ABSENCE);
		Mockito.verify(ventilRepo, Mockito.never())
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.H_SUP);
		Mockito.verify(ventilRepo, Mockito.times(1))
				.removeVentilationsForDateAgentAndType(ventilDate, idAgent,
						RefTypePointageEnum.PRIME);
	}

	@Test
	public void processHSupAndAbsVentilationForWeekAndAgent_HSup() {

		// Given
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 7, 28).toDate());
		Date dateLundi = new LocalDate().toDate();
		Date fromEtatDate = new LocalDate().toDate();
		Date toEtatDate = new LocalDate().toDate();

		Pointage p1 = new Pointage();
		p1.setType(hSup);
		p1.setDateLundi(new LocalDate(2013, 7, 1).toDate());

		Spcarr carr = new Spcarr();
		carr.setCdcate(7); // CC

		IVentilationRepository ventilRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(
				ventilRepo.getListPointagesAbsenceAndHSupForVentilation(
						idAgent, fromEtatDate, toEtatDate, dateLundi))
				.thenReturn(Arrays.asList(p1));

		ISirhRepository mairieRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(mairieRepo.getPrimePointagesByAgent(idAgent, dateLundi))
				.thenReturn(Arrays.asList(1128, 1135));

		VentilHsup ventilHsup = Mockito.spy(new VentilHsup());
		Mockito.doNothing().when(ventilHsup).persist();
		IVentilationHSupService hSupV = Mockito
				.mock(IVentilationHSupService.class);
		Mockito.when(
				hSupV.processHSup(Mockito.eq(idAgent), Mockito.eq(carr),
						Mockito.eq(dateLundi),
						Mockito.anyListOf(Pointage.class),
						Mockito.eq(AgentStatutEnum.CC), Mockito.eq(false)))
				.thenReturn(ventilHsup);

		IVentilationAbsenceService absV = Mockito
				.mock(IVentilationAbsenceService.class);

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository",
				ventilRepo);
		ReflectionTestUtils.setField(service, "sirhRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "ventilationHSupService", hSupV);
		ReflectionTestUtils
				.setField(service, "ventilationAbsenceService", absV);

		// When
		service.processHSupAndAbsVentilationForWeekAndAgent(ventilDate,
				idAgent, carr, dateLundi, fromEtatDate);

		// Then
		Mockito.verify(ventilHsup, Mockito.times(1)).persist();
		assertEquals(ventilDate, ventilHsup.getVentilDate());
	}

	@Test
	public void processHSupAndAbsVentilationForWeekAndAgent_HSup_AgentHasPrime1150() {

		// Given
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 7, 28).toDate());
		Date dateLundi = new LocalDate().toDate();
		Date fromEtatDate = new LocalDate().toDate();
		Date toEtatDate = new LocalDate().toDate();

		Pointage p1 = new Pointage();
		p1.setType(hSup);
		p1.setDateLundi(new LocalDate(2013, 7, 1).toDate());

		Spcarr carr = new Spcarr();
		carr.setCdcate(7); // CC

		IVentilationRepository ventilRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(
				ventilRepo.getListPointagesAbsenceAndHSupForVentilation(
						idAgent, fromEtatDate, toEtatDate, dateLundi))
				.thenReturn(Arrays.asList(p1));

		ISirhRepository mairieRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(mairieRepo.getPrimePointagesByAgent(idAgent, dateLundi))
				.thenReturn(Arrays.asList(1128, 1150, 1135));

		VentilHsup ventilHsup = Mockito.spy(new VentilHsup());
		Mockito.doNothing().when(ventilHsup).persist();
		IVentilationHSupService hSupV = Mockito
				.mock(IVentilationHSupService.class);
		Mockito.when(
				hSupV.processHSup(Mockito.eq(idAgent), Mockito.eq(carr),
						Mockito.eq(dateLundi),
						Mockito.anyListOf(Pointage.class),
						Mockito.eq(AgentStatutEnum.CC), Mockito.eq(true)))
				.thenReturn(ventilHsup);

		IVentilationAbsenceService absV = Mockito
				.mock(IVentilationAbsenceService.class);

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository",
				ventilRepo);
		ReflectionTestUtils.setField(service, "sirhRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "ventilationHSupService", hSupV);
		ReflectionTestUtils
				.setField(service, "ventilationAbsenceService", absV);

		// When
		service.processHSupAndAbsVentilationForWeekAndAgent(ventilDate,
				idAgent, carr, dateLundi, fromEtatDate);

		// Then
		Mockito.verify(ventilHsup, Mockito.times(1)).persist();
		assertEquals(ventilDate, ventilHsup.getVentilDate());
	}

	@Test
	public void processHSupAndAbsVentilationForWeekAndAgent_Abs() {

		// Given
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 7, 28).toDate());
		Date dateLundi = new LocalDate().toDate();
		Date fromEtatDate = new LocalDate().toDate();
		Date toEtatDate = new LocalDate().toDate();

		Pointage p1 = new Pointage();
		p1.setType(abs);
		p1.setDateDebut(new LocalDate(2013, 7, 4).toDate());

		IVentilationRepository ventilRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(
				ventilRepo.getListPointagesAbsenceAndHSupForVentilation(
						idAgent, fromEtatDate, toEtatDate, dateLundi))
				.thenReturn(Arrays.asList(p1));

		IVentilationHSupService hSupV = Mockito
				.mock(IVentilationHSupService.class);

		VentilAbsence ventilAbs = Mockito.spy(new VentilAbsence());
		Mockito.doNothing().when(ventilAbs).persist();
		IVentilationAbsenceService absV = Mockito
				.mock(IVentilationAbsenceService.class);
		Mockito.when(
				absV.processAbsenceAgent(Mockito.eq(idAgent),
						Mockito.anyListOf(Pointage.class),
						Mockito.any(Date.class))).thenReturn(ventilAbs);

		ISirhRepository mairieRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(mairieRepo.getPrimePointagesByAgent(idAgent, dateLundi))
				.thenReturn(Arrays.asList(1128));

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "sirhRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "ventilationRepository",
				ventilRepo);
		ReflectionTestUtils
				.setField(service, "ventilationAbsenceService", absV);
		ReflectionTestUtils.setField(service, "ventilationHSupService", hSupV);

		// When
		service.processHSupAndAbsVentilationForWeekAndAgent(ventilDate,
				idAgent, new Spcarr(), dateLundi, fromEtatDate);

		// Then
		Mockito.verify(ventilAbs, Mockito.times(1)).persist();
		assertEquals(ventilDate, ventilAbs.getVentilDate());
	}

	@Test
	public void processPrimesVentilationForMonthAndAgent() {

		// Given
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 7, 28).toDate());
		Date dateDebutMois = new LocalDate().toDate();
		Date fromEtatDate = new LocalDate().toDate();
		Date toEtatDate = new LocalDate().toDate();

		Pointage p1 = new Pointage();
		p1.setType(prime);
		p1.setDateDebut(new LocalDate(2013, 7, 4).toDate());

		PointageCalcule p2 = new PointageCalcule();

		IVentilationRepository ventilRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(
				ventilRepo.getListPointagesPrimeForVentilation(idAgent,
						fromEtatDate, toEtatDate, dateDebutMois)).thenReturn(
				Arrays.asList(p1));
		Mockito.when(
				ventilRepo.getListPointagesCalculesPrimeForVentilation(idAgent,
						dateDebutMois)).thenReturn(Arrays.asList(p2));

		VentilPrime ventilPrime = Mockito.spy(new VentilPrime());
		Mockito.doNothing().when(ventilPrime).persist();
		IVentilationPrimeService primeV = Mockito
				.mock(IVentilationPrimeService.class);
		Mockito.when(
				primeV.processPrimesAgent(Mockito.eq(idAgent),
						Mockito.anyListOf(Pointage.class),
						Mockito.eq(dateDebutMois))).thenReturn(
				Arrays.asList(ventilPrime));

		VentilPrime ventilPrime2 = Mockito.spy(new VentilPrime());
		Mockito.doNothing().when(ventilPrime2).persist();
		Mockito.when(
				primeV.processPrimesCalculeesAgent(Mockito.eq(idAgent),
						Mockito.anyListOf(PointageCalcule.class),
						Mockito.eq(dateDebutMois))).thenReturn(
				Arrays.asList(ventilPrime2));

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository",
				ventilRepo);
		ReflectionTestUtils
				.setField(service, "ventilationPrimeService", primeV);

		// When
		service.processPrimesVentilationForMonthAndAgent(ventilDate, idAgent,
				dateDebutMois, fromEtatDate);

		// Then
		assertEquals(EtatPointageEnum.VENTILE, p2.getEtat());
		assertEquals(ventilDate, p2.getLastVentilDate());
		
		Mockito.verify(ventilPrime, Mockito.times(1)).persist();
		assertEquals(ventilDate, ventilPrime.getVentilDate());

		Mockito.verify(ventilPrime2, Mockito.times(1)).persist();
		assertEquals(ventilDate, ventilPrime2.getVentilDate());
	}

	@Test
	public void calculatePointages_With4weeksBetweenTwoDates() {

		// Given
		Integer idAgent = 9008765;
		Date from = new LocalDate(2013, 6, 30).toDate();
		Date to = new LocalDate(2013, 7, 28).toDate();
		Date dateLundi = new LocalDate(2013, 7, 1).toDate();

		List<Pointage> ptgList = new ArrayList<Pointage>();

		IVentilationRepository vRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(
				vRepo.getListPointagesForPrimesCalculees(idAgent, from, to,
						dateLundi)).thenReturn(ptgList);

		PointageCalcule pc1 = Mockito.spy(new PointageCalcule());
		Mockito.doNothing().when(pc1).persist();

		IPointageCalculeService ptgCService = Mockito
				.mock(IPointageCalculeService.class);
		Mockito.when(
				ptgCService.calculatePointagesForAgentAndWeek(
						Mockito.eq(idAgent), Mockito.eq(AgentStatutEnum.F),
						Mockito.eq(dateLundi), Mockito.eq(ptgList)))
				.thenReturn(Arrays.asList(pc1));

		Spcarr carr = new Spcarr();
		carr.setCdcate(20); // F
		ISirhRepository mairieRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(
				mairieRepo.getAgentCurrentCarriere(Mockito.eq(8765),
						Mockito.eq(dateLundi))).thenReturn(carr);

		HelperService hsMock = Mockito.mock(HelperService.class);
		Mockito.when(hsMock.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "sirhRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "ventilationRepository", vRepo);
		ReflectionTestUtils.setField(service, "pointageCalculeService",
				ptgCService);
		ReflectionTestUtils.setField(service, "helperService", hsMock);

		// When
		service.calculatePointages(idAgent, dateLundi, from, to);

		// Then
		Mockito.verify(mairieRepo, Mockito.times(1)).getAgentCurrentCarriere(
				Mockito.eq(8765),
				Mockito.eq(new LocalDate(2013, 7, 1).toDate()));

		Mockito.verify(ptgCService, Mockito.times(1))
				.calculatePointagesForAgentAndWeek(Mockito.eq(idAgent),
						Mockito.eq(AgentStatutEnum.F), Mockito.eq(dateLundi),
						Mockito.eq(ptgList));

		Mockito.verify(pc1, Mockito.times(1)).persist();
	}

	@Test
	public void markPointagesAsVentile_LinkToVentilDate_AddNewVENTILEetatToPointagesNotVENTILE() {

		// Given
		VentilDate ventilDate = new VentilDate();

		Pointage p1 = new Pointage();
		p1.setType(abs);
		p1.setDateDebut(new LocalDate(2013, 7, 4).toDate());
		EtatPointage ep1 = new EtatPointage();
		ep1.setDateEtat(new Date());
		ep1.setPointage(p1);
		ep1.setEtat(EtatPointageEnum.APPROUVE);
		p1.getEtats().add(ep1);

		Pointage p2 = new Pointage();
		p2.setType(abs);
		p2.setDateDebut(new LocalDate(2013, 7, 4).toDate());
		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new Date());
		ep2.setPointage(p2);
		ep2.setEtat(EtatPointageEnum.VENTILE);
		p2.getEtats().add(ep2);
		p2.getVentilations().add(new VentilDate());

		Date etatDate = new LocalDate(2013, 07, 01).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(etatDate);

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		service.markPointagesAsVentile(Arrays.asList(p1, p2), 9008888,
				ventilDate);

		// Then
		assertEquals(2, p1.getEtats().size());
		assertEquals(ep1, p1.getEtats().get(0));
		assertEquals(EtatPointageEnum.APPROUVE, p1.getEtats().get(0).getEtat());
		assertEquals(EtatPointageEnum.VENTILE, p1.getEtats().get(1).getEtat());
		assertEquals(9008888, (int) p1.getEtats().get(1).getIdAgent());
		assertEquals(etatDate, p1.getEtats().get(1).getDateEtat());
		assertEquals(etatDate, p1.getEtats().get(1).getDateMaj());
		assertEquals(ventilDate, p1.getVentilations().get(0));

		assertEquals(1, p2.getEtats().size());
		assertEquals(ep2, p2.getEtats().get(0));
		assertEquals(EtatPointageEnum.VENTILE, p2.getEtats().get(0).getEtat());
		assertEquals(ventilDate, p2.getVentilations().get(1));
	}

	@Test
	public void isAgentEligibleToVentilation_AgentisFandTargetIsF_ReturnSpcarr() {

		// Given
		Date asOfDate = new LocalDate(2013, 1, 28).toDate();

		Spcarr carr = new Spcarr();
		carr.setCdcate(20); // F

		ISirhRepository mRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(mRepo.getAgentCurrentCarriere(7898, asOfDate)).thenReturn(
				carr);

		HelperService hsMock = Mockito.mock(HelperService.class);
		Mockito.when(hsMock.getMairieMatrFromIdAgent(9007898)).thenReturn(7898);

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "sirhRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hsMock);

		// When
		Spcarr result = service.isAgentEligibleToVentilation(9007898,
				AgentStatutEnum.F, asOfDate);

		// Then
		assertEquals(result, carr);
	}

	@Test
	public void isAgentEligibleToVentilation_AgentisCCandTargetIsF_ReturnNull() {

		// Given
		Date asOfDate = new LocalDate(2013, 1, 28).toDate();

		Spcarr carr = new Spcarr();
		carr.setCdcate(7); // CC

		ISirhRepository mRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(mRepo.getAgentCurrentCarriere(7898, asOfDate)).thenReturn(
				carr);

		HelperService hsMock = Mockito.mock(HelperService.class);
		Mockito.when(hsMock.getMairieMatrFromIdAgent(9007898)).thenReturn(7898);

		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "sirhRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hsMock);

		// When
		Spcarr result = service.isAgentEligibleToVentilation(9007898,
				AgentStatutEnum.F, asOfDate);

		// Then
		assertNull(result);
	}

	@Test
	public void getDistinctDateLundiFromListOfDates_3dates_2dateLundi() {

		// Given
		List<Date> dates = Arrays.asList(new LocalDate(2013, 7, 18).toDate(),
				new LocalDate(2013, 7, 16).toDate(),
				new LocalDate(2013, 7, 25).toDate());

		VentilationService service = new VentilationService();

		// When
		List<Date> result = service.getDistinctDateLundiFromListOfDates(dates);

		// Then
		assertEquals(2, result.size());
		assertEquals(new LocalDate(2013, 7, 15).toDate(), result.get(0));
		assertEquals(new LocalDate(2013, 7, 22).toDate(), result.get(1));
	}

	@Test
	public void getDistinctDateDebutMoisFromListOfDates_3dates_2months() {

		// Given
		List<Date> dates = Arrays.asList(new LocalDate(2013, 7, 18).toDate(),
				new LocalDate(2013, 7, 16).toDate(),
				new LocalDate(2013, 8, 30).toDate());

		VentilationService service = new VentilationService();

		// When
		List<Date> result = service
				.getDistinctDateDebutMoisFromListOfDates(dates);

		// Then
		assertEquals(2, result.size());
		assertEquals(new LocalDate(2013, 7, 1).toDate(), result.get(0));
		assertEquals(new LocalDate(2013, 8, 1).toDate(), result.get(1));
	}

	/**
	 * this method only uses sql query. Unable to test it yet
	 * 
	 * @Test public void showVentilation() {
	 * 
	 *       // Given Integer agent = 9005138; Integer idDateVentil = 25;
	 *       IVentilationRepository ventilationRepo =
	 *       Mockito.mock(IVentilationRepository.class); VentilationService
	 *       service = new VentilationService();
	 *       ReflectionTestUtils.setField(service, "ventilationRepository",
	 *       ventilationRepo); List ret = new ArrayList(); VentilPrimeDto
	 *       ventilPrime = new VentilPrimeDto(); VentilHSupDto ventilHsup = new
	 *       VentilHSupDto(); VentilAbsenceDto ventilAbs = new
	 *       VentilAbsenceDto();
	 * 
	 * 
	 *       // When ret.add(ventilPrime);
	 *       Mockito.when(ventilationRepo.getListOfVentilForDateAgentAndType
	 *       (idDateVentil, agent, RefTypePointageEnum.PRIME)).thenReturn(ret);
	 *       ret.clear(); ret.add(ventilHsup);
	 *       Mockito.when(ventilationRepo.getListOfVentilForDateAgentAndType
	 *       (idDateVentil, agent, RefTypePointageEnum.H_SUP)).thenReturn(ret);
	 *       ret.clear(); ret.add(ventilAbs);
	 *       Mockito.when(ventilationRepo.getListOfVentilForDateAgentAndType
	 *       (idDateVentil, agent,
	 *       RefTypePointageEnum.ABSENCE)).thenReturn(ret);
	 * 
	 * 
	 *       // Then
	 *       Mockito.verify(ventilationRepo).getListOfVentilForDateAgentAndType
	 *       (idDateVentil, agent, RefTypePointageEnum.ABSENCE);
	 * 
	 *       }
	 **/

	@SuppressWarnings("deprecation")
	@Test
	public void processVentilation_VentilationDateExisting_useExistingOne() {

		// Given
		Date ventilationDate = new LocalDate(2013, 7, 28).toDate();
		AgentStatutEnum statut = AgentStatutEnum.F;
		RefTypePointageEnum pointageType = null;

		Date lastPaidDate = new LocalDate(2013, 7, 21).toDate();
		VentilDate lastPaidVentilDate = new VentilDate();
		lastPaidVentilDate.setDateVentilation(lastPaidDate);

		Date lastUnPaidDate = new LocalDate(2013, 7, 28).toDate();
		VentilDate lastUnPaidVentilDate = new VentilDate();
		lastUnPaidVentilDate.setDateVentilation(lastUnPaidDate);

		List<Integer> agentList = Arrays.asList(9005432, 9005431);
		List<Date> pointagesDates = new ArrayList<Date>();

		IVentilationRepository vRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(vRepo.getLatestVentilDate(TypeChainePaieEnum.SHC, true))
				.thenReturn(lastPaidVentilDate);
		Mockito.when(vRepo.getLatestVentilDate(TypeChainePaieEnum.SHC, false))
				.thenReturn(lastUnPaidVentilDate);
		Mockito.when(
				vRepo.getDistinctDatesOfPointages(9005432, lastUnPaidDate,
						ventilationDate)).thenReturn(pointagesDates);
		Mockito.when(
				vRepo.getListIdAgentsForVentilationByDateAndEtat(lastPaidDate,
						lastUnPaidDate)).thenReturn(agentList);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(
				TypeChainePaieEnum.SHC);

		VentilationService service = Mockito.spy(new VentilationService());
		ReflectionTestUtils.setField(service, "ventilationRepository", vRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		Spcarr carr = new Spcarr();
		Mockito.doReturn(carr).when(service)
				.isAgentEligibleToVentilation(9005432, statut, lastUnPaidDate);
		Mockito.doReturn(null).when(service)
				.isAgentEligibleToVentilation(9005431, statut, lastUnPaidDate);
		Mockito.doNothing()
				.when(service)
				.removePreviousVentilations(lastUnPaidVentilDate, 9005432, null);

		List<Date> datesLundi = new ArrayList<Date>();
		datesLundi.add(new LocalDate(2013, 7, 22).toDate());
		Mockito.doReturn(datesLundi).when(service)
				.getDistinctDateLundiFromListOfDates(pointagesDates);

		List<Date> datesDebutMois = new ArrayList<Date>();
		datesDebutMois.add(new LocalDate(2013, 7, 1).toDate());
		Mockito.doReturn(datesDebutMois).when(service)
				.getDistinctDateDebutMoisFromListOfDates(pointagesDates);

		List<Pointage> ptgVentiles = new ArrayList<Pointage>();
		Mockito.doReturn(ptgVentiles)
				.when(service)
				.processHSupAndAbsVentilationForWeekAndAgent(
						lastUnPaidVentilDate, 9005432, carr, datesLundi.get(0),
						lastPaidDate);

		Mockito.doNothing().when(service)
				.removePreviousCalculatedPointages(9005432, datesLundi.get(0));
		Mockito.doNothing()
				.when(service)
				.calculatePointages(9005432, datesLundi.get(0), lastPaidDate,
						lastUnPaidDate);
		Mockito.doReturn(ptgVentiles)
				.when(service)
				.processPrimesVentilationForMonthAndAgent(lastUnPaidVentilDate,
						9005432, datesDebutMois.get(0), lastPaidDate);

		Mockito.doNothing()
				.when(service)
				.markPointagesAsVentile(ptgVentiles, 9005432,
						lastUnPaidVentilDate);

		// When
		service.processVentilation(9008765, new ArrayList<Integer>(),
				ventilationDate, statut, pointageType);

		// Then
		Mockito.verify(service, Mockito.times(1)).isAgentEligibleToVentilation(
				9005432, statut, lastUnPaidDate);
		Mockito.verify(service, Mockito.times(1)).isAgentEligibleToVentilation(
				9005431, statut, lastUnPaidDate);
		Mockito.verify(service, Mockito.times(1)).removePreviousVentilations(
				lastUnPaidVentilDate, 9005432, null);
		Mockito.verify(service, Mockito.times(2))
				.getDistinctDateLundiFromListOfDates(pointagesDates);
		Mockito.verify(service, Mockito.times(1))
				.getDistinctDateDebutMoisFromListOfDates(pointagesDates);
		Mockito.verify(service, Mockito.times(1))
				.processHSupAndAbsVentilationForWeekAndAgent(
						lastUnPaidVentilDate, 9005432, carr, datesLundi.get(0),
						lastPaidDate);
		Mockito.verify(service, Mockito.times(1))
				.removePreviousCalculatedPointages(9005432, datesLundi.get(0));
		Mockito.verify(service, Mockito.times(1)).calculatePointages(9005432,
				datesLundi.get(0), lastPaidDate, lastUnPaidDate);
		Mockito.verify(service, Mockito.times(1))
				.processPrimesVentilationForMonthAndAgent(lastUnPaidVentilDate,
						9005432, datesDebutMois.get(0), lastPaidDate);
		Mockito.verify(service, Mockito.times(1)).markPointagesAsVentile(
				ptgVentiles, 9005432, lastUnPaidVentilDate);
	}

	@Test
	public void startVentilation_VentilationDateExists_2agents_1filtered_Create1VentilTask() {

		// Given
		Date ventilationDate = new LocalDate(2013, 7, 28).toDate();
		final AgentStatutEnum statut = AgentStatutEnum.F;
		RefTypePointageEnum pointageType = RefTypePointageEnum.H_SUP;

		Date lastPaidDate = new LocalDate(2013, 7, 21).toDate();
		final VentilDate lastPaidVentilDate = new VentilDate();
		lastPaidVentilDate.setDateVentilation(lastPaidDate);

		Date lastUnPaidDate = new LocalDate(2013, 7, 28).toDate();
		final VentilDate lastUnPaidVentilDate = new VentilDate();
		lastUnPaidVentilDate.setDateVentilation(lastUnPaidDate);

		List<Integer> agentList = Arrays.asList(9005432, 9005431);

		IVentilationRepository vRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(vRepo.canStartVentilation(TypeChainePaieEnum.SHC))
				.thenReturn(true);
		Mockito.when(vRepo.getLatestVentilDate(TypeChainePaieEnum.SHC, true))
				.thenReturn(lastPaidVentilDate);
		Mockito.when(vRepo.getLatestVentilDate(TypeChainePaieEnum.SHC, false))
				.thenReturn(lastUnPaidVentilDate);
		Mockito.when(
				vRepo.getListIdAgentsForVentilationByDateAndEtat(lastPaidDate,
						lastUnPaidDate)).thenReturn(agentList);

		final RefTypePointage refTypePointage = new RefTypePointage();
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(
				pRepo.getEntity(RefTypePointage.class, pointageType.getValue()))
				.thenReturn(refTypePointage);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				VentilTask arg = (VentilTask) args[0];

				assertEquals(9005432, (int) arg.getIdAgent());
				assertEquals(new DateTime(2013, 8, 2, 10, 57, 23).toDate(),
						arg.getDateCreation());
				assertEquals(9008765, (int) arg.getIdAgentCreation());
				assertEquals(refTypePointage, arg.getRefTypePointage());
				assertEquals(TypeChainePaieEnum.SHC, arg.getTypeChainePaie());
				assertEquals(lastPaidVentilDate, arg.getVentilDateFrom());
				assertEquals(lastUnPaidVentilDate, arg.getVentilDateTo());
				return true;
			}
		}).when(pRepo).persisEntity(Mockito.isA(VentilTask.class));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(
				TypeChainePaieEnum.SHC);
		Mockito.when(hS.getCurrentDate()).thenReturn(
				new DateTime(2013, 8, 2, 10, 57, 23).toDate());

		VentilationService service = Mockito.spy(new VentilationService());
		ReflectionTestUtils.setField(service, "ventilationRepository", vRepo);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		Spcarr carr = new Spcarr();
		Mockito.doReturn(carr).when(service)
				.isAgentEligibleToVentilation(9005432, statut, lastUnPaidDate);
		Mockito.doReturn(null).when(service)
				.isAgentEligibleToVentilation(9005431, statut, lastUnPaidDate);

		// When
		ReturnMessageDto result = service
				.startVentilation(9008765, new ArrayList<Integer>(),
						ventilationDate, statut, pointageType);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(1, result.getInfos().size());
		assertEquals("Agent 9005432", result.getInfos().get(0));

		Mockito.verify(pRepo, Mockito.never()).persisEntity(
				Mockito.isA(VentilDate.class));
		Mockito.verify(pRepo, Mockito.times(1)).persisEntity(
				Mockito.isA(VentilTask.class));
	}

	@Test
	public void startVentilation_VentilationDateDoesNotExist_2agents_2filtered_CreateVentilDate_Create0VentilTask() {

		// Given
		final Date ventilationDate = new LocalDate(2013, 7, 28).toDate();
		AgentStatutEnum statut = AgentStatutEnum.F;
		RefTypePointageEnum pointageType = RefTypePointageEnum.H_SUP;

		Date lastPaidDate = new LocalDate(2013, 7, 21).toDate();
		VentilDate lastPaidVentilDate = new VentilDate();
		lastPaidVentilDate.setDateVentilation(lastPaidDate);

		List<Integer> agentList = Arrays.asList(9005432, 9005431);

		IVentilationRepository vRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(vRepo.canStartVentilation(TypeChainePaieEnum.SHC))
				.thenReturn(true);
		Mockito.when(vRepo.getLatestVentilDate(TypeChainePaieEnum.SHC, true))
				.thenReturn(lastPaidVentilDate);
		Mockito.when(vRepo.getLatestVentilDate(TypeChainePaieEnum.SHC, false))
				.thenReturn(null);
		Mockito.when(
				vRepo.getListIdAgentsForVentilationByDateAndEtat(lastPaidDate,
						ventilationDate)).thenReturn(agentList);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				VentilDate arg = (VentilDate) args[0];

				assertEquals(new DateTime(2013, 7, 28, 23, 59, 0).toDate(),
						arg.getDateVentilation());
				assertEquals(TypeChainePaieEnum.SHC, arg.getTypeChainePaie());
				assertFalse(arg.isPaye());
				return true;
			}
		}).when(pRepo).persisEntity(Mockito.isA(VentilDate.class));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(
				TypeChainePaieEnum.SHC);

		VentilationService service = Mockito.spy(new VentilationService());
		ReflectionTestUtils.setField(service, "ventilationRepository", vRepo);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		Mockito.doReturn(null).when(service)
				.isAgentEligibleToVentilation(9005432, statut, ventilationDate);
		Mockito.doReturn(null).when(service)
				.isAgentEligibleToVentilation(9005431, statut, ventilationDate);

		// When
		ReturnMessageDto result = service
				.startVentilation(9008765, new ArrayList<Integer>(),
						ventilationDate, statut, pointageType);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());

		Mockito.verify(pRepo, Mockito.times(1)).persisEntity(
				Mockito.isA(VentilDate.class));
		Mockito.verify(pRepo, Mockito.never()).persisEntity(
				Mockito.isA(VentilTask.class));
	}

	@Test
	public void startVentilation_VentilationDateIsNotASunday_ReturnErrorMessage() {

		// Given
		final Date ventilationDate = new LocalDate(2013, 7, 27).toDate();
		AgentStatutEnum statut = AgentStatutEnum.F;
		RefTypePointageEnum pointageType = RefTypePointageEnum.H_SUP;

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(
				TypeChainePaieEnum.SCV);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.canStartVentilation(TypeChainePaieEnum.SCV))
				.thenReturn(true);

		VentilationService service = Mockito.spy(new VentilationService());
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		ReturnMessageDto result = service
				.startVentilation(9008765, new ArrayList<Integer>(),
						ventilationDate, statut, pointageType);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(
				"La date de ventilation choisie est un [samedi]. Impossible de ventiler les pointages à une date autre qu'un dimanche.",
				result.getErrors().get(0));
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void startVentilation_AVentilationAlreadyRunning_ReturnErrorMessage() {

		// Given
		final Date ventilationDate = new LocalDate(2013, 7, 27).toDate();
		AgentStatutEnum statut = AgentStatutEnum.F;
		RefTypePointageEnum pointageType = RefTypePointageEnum.H_SUP;

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(
				TypeChainePaieEnum.SCV);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.canStartVentilation(TypeChainePaieEnum.SCV))
				.thenReturn(false);

		VentilationService service = Mockito.spy(new VentilationService());
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		ReturnMessageDto result = service
				.startVentilation(9008765, new ArrayList<Integer>(),
						ventilationDate, statut, pointageType);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(
				"Ventiation for statut [F] may not be started. An existing one is currently processing...",
				result.getErrors().get(0));
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void processVentilationForAgent_NoPointageType_DoAll() {

		// Given
		VentilTask task = new VentilTask();
		task.setIdVentilTask(19);
		task.setIdAgent(9005432);
		task.setIdAgentCreation(9001234);
		task.setDateCreation(new LocalDate(2013, 8, 13).toDate());
		task.setTypeChainePaie(TypeChainePaieEnum.SHC);
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new LocalDate(2013, 7, 21).toDate());
		task.setVentilDateFrom(fromVentilDate);
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new LocalDate(2013, 7, 28).toDate());
		task.setVentilDateTo(toVentilDate);
		Date ventilationDate = new LocalDate(2013, 7, 28).toDate();

		List<Date> pointagesDates = new ArrayList<Date>();

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(VentilTask.class, 19)).thenReturn(task);

		IVentilationRepository vRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(
				vRepo.getDistinctDatesOfPointages(9005432,
						fromVentilDate.getDateVentilation(), ventilationDate))
				.thenReturn(pointagesDates);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9005432)).thenReturn(5432);

		Spcarr carr = new Spcarr();
		ISirhRepository sRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(sRepo.getAgentCurrentCarriere(5432, ventilationDate))
				.thenReturn(carr);

		VentilationService service = Mockito.spy(new VentilationService());
		ReflectionTestUtils.setField(service, "ventilationRepository", vRepo);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "sirhRepository", sRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		List<Date> datesLundi = new ArrayList<Date>();
		datesLundi.add(new LocalDate(2013, 7, 22).toDate());
		Mockito.doReturn(datesLundi).when(service)
				.getDistinctDateLundiFromListOfDates(pointagesDates);

		List<Date> datesDebutMois = new ArrayList<Date>();
		datesDebutMois.add(new LocalDate(2013, 7, 1).toDate());
		Mockito.doReturn(datesDebutMois).when(service)
				.getDistinctDateDebutMoisFromListOfDates(pointagesDates);

		List<Pointage> ptgVentiles = new ArrayList<Pointage>();
		Mockito.doReturn(ptgVentiles)
				.when(service)
				.processHSupAndAbsVentilationForWeekAndAgent(toVentilDate,
						9005432, carr, datesLundi.get(0),
						fromVentilDate.getDateVentilation());

		Mockito.doNothing().when(service)
				.removePreviousCalculatedPointages(9005432, datesLundi.get(0));
		Mockito.doNothing()
				.when(service)
				.calculatePointages(9005432, datesLundi.get(0),
						fromVentilDate.getDateVentilation(),
						toVentilDate.getDateVentilation());
		Mockito.doReturn(ptgVentiles)
				.when(service)
				.processPrimesVentilationForMonthAndAgent(toVentilDate,
						9005432, datesDebutMois.get(0),
						fromVentilDate.getDateVentilation());

		Mockito.doNothing().when(service)
				.markPointagesAsVentile(ptgVentiles, 9005432, toVentilDate);

		// When
		service.processVentilationForAgent(19);

		// Then
		Mockito.verify(service, Mockito.times(1)).removePreviousVentilations(
				toVentilDate, 9005432, null);
		Mockito.verify(service, Mockito.times(2))
				.getDistinctDateLundiFromListOfDates(pointagesDates);
		Mockito.verify(service, Mockito.times(1))
				.getDistinctDateDebutMoisFromListOfDates(pointagesDates);
		Mockito.verify(service, Mockito.times(1))
				.processHSupAndAbsVentilationForWeekAndAgent(toVentilDate,
						9005432, carr, datesLundi.get(0),
						fromVentilDate.getDateVentilation());
		Mockito.verify(service, Mockito.times(1))
				.removePreviousCalculatedPointages(9005432, datesLundi.get(0));
		Mockito.verify(service, Mockito.times(1)).calculatePointages(9005432,
				datesLundi.get(0), fromVentilDate.getDateVentilation(),
				toVentilDate.getDateVentilation());
		Mockito.verify(service, Mockito.times(1))
				.processPrimesVentilationForMonthAndAgent(toVentilDate,
						9005432, datesDebutMois.get(0),
						fromVentilDate.getDateVentilation());
		Mockito.verify(service, Mockito.times(1)).markPointagesAsVentile(
				ptgVentiles, 9005432, toVentilDate);
	}

	@Test
	public void canStartVentilationForAgentStatus_CannotStart_ReturnFalse() {

		// Given
		IVentilationRepository vRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(vRepo.canStartVentilation(TypeChainePaieEnum.SHC))
				.thenReturn(false);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F))
				.thenReturn(TypeChainePaieEnum.SHC);

		VentilationService service = Mockito.spy(new VentilationService());
		ReflectionTestUtils.setField(service, "ventilationRepository", vRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		CanStartVentilationDto result = service
				.canStartVentilationForAgentStatus(AgentStatutEnum.F);

		// Then
		assertFalse(result.isCanStartVentilation());
	}

	@Test
	public void canStartVentilationForAgentStatus_CanStart_ReturnTrue() {

		// Given
		IVentilationRepository vRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(vRepo.canStartVentilation(TypeChainePaieEnum.SHC))
				.thenReturn(true);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F))
				.thenReturn(TypeChainePaieEnum.SHC);

		VentilationService service = Mockito.spy(new VentilationService());
		ReflectionTestUtils.setField(service, "ventilationRepository", vRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		CanStartVentilationDto result = service
				.canStartVentilationForAgentStatus(AgentStatutEnum.F);

		// Then
		assertTrue(result.isCanStartVentilation());
	}

	@Test
	public void getVentilationEnCoursForStatut_VentilNonPaye() {
		VentilDate ventilDate = new VentilDate();
		ventilDate.setIdVentilDate(1);
		ventilDate.setDateVentilation(new LocalDate(2013, 4, 6).toDate());
		ventilDate.setTypeChainePaie(TypeChainePaieEnum.SHC);
		ventilDate.setPaye(false);

		// Given
		IVentilationRepository vRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(vRepo.getLatestVentilDate(TypeChainePaieEnum.SHC, false))
				.thenReturn(ventilDate);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F))
				.thenReturn(TypeChainePaieEnum.SHC);

		VentilationService service = Mockito.spy(new VentilationService());
		ReflectionTestUtils.setField(service, "ventilationRepository", vRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		VentilDateDto result = service
				.getVentilationEnCoursForStatut(AgentStatutEnum.F);

		// Then
		assertEquals(TypeChainePaieEnum.SHC, result.getTypeChaine());
		assertFalse(result.isPaie());
	}

	@Test
	public void getVentilationEnCoursForStatut_VentilPaye() {

		// Given
		IVentilationRepository vRepo = Mockito
				.mock(IVentilationRepository.class);
		Mockito.when(vRepo.getLatestVentilDate(TypeChainePaieEnum.SHC, false))
				.thenReturn(new VentilDate());

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F))
				.thenReturn(TypeChainePaieEnum.SHC);

		VentilationService service = Mockito.spy(new VentilationService());
		ReflectionTestUtils.setField(service, "ventilationRepository", vRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		VentilDateDto result = service
				.getVentilationEnCoursForStatut(AgentStatutEnum.F);

		// Then
		assertTrue(result.isPaie());
	}
}
