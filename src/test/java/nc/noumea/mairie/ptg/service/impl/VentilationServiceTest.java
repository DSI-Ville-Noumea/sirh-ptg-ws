package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IVentilationAbsenceService;
import nc.noumea.mairie.ptg.service.IVentilationHSupService;
import nc.noumea.mairie.ptg.service.IVentilationPrimeService;
import nc.noumea.mairie.sirh.domain.Agent;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
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
		abs= new RefTypePointage();
		abs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
	}
	
	@Test
	public void removePreviousVentilations_NoTypeSelected_DeleteAllTypes() {
		
		// Given
		RefTypePointageEnum typePointage = null;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 4, 6).toDate());
		
		IPointageRepository pointageRepo = Mockito.mock(IPointageRepository.class);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepo);
		
		// When
		service.removePreviousVentilations(ventilDate, idAgent, typePointage);
		
		// Then
		Mockito.verify(pointageRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.ABSENCE);
		Mockito.verify(pointageRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.H_SUP);
		Mockito.verify(pointageRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.PRIME);
		
	}
	
	@Test
	public void removePreviousVentilations_TypeSelected_DeleteOnlySelectedType() {
		
		// Given
		RefTypePointageEnum typePointage = RefTypePointageEnum.H_SUP;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 4, 6).toDate());
		
		IPointageRepository pointageRepo = Mockito.mock(IPointageRepository.class);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepo);
		
		// When
		service.removePreviousVentilations(ventilDate, idAgent, typePointage);
		
		// Then
		Mockito.verify(pointageRepo, Mockito.never()).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.ABSENCE);
		Mockito.verify(pointageRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.H_SUP);
		Mockito.verify(pointageRepo, Mockito.never()).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.PRIME);
	}

	@Test
	public void distributePointages_2pointagesEachType_With2dateLundiOfSameMonth() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setDateLundi(new LocalDate(2013, 7, 1).toDate());
		p1.setType(hSup);
		Pointage p2 = new Pointage();
		p2.setDateLundi(new LocalDate(2013, 7, 8).toDate());
		p2.setType(hSup);
		Pointage p3 = new Pointage();
		p3.setType(prime);
		p3.setDateDebut(new DateTime(2013, 7, 12, 14, 0, 0).toDate());
		Pointage p4 = new Pointage();
		p4.setType(abs);
		p4.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		Pointage p5 = new Pointage();
		p5.setType(prime);
		p5.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		Pointage p6 = new Pointage();
		p6.setType(abs);
		p6.setDateLundi(new LocalDate(2013, 7, 1).toDate());
		
		List<Pointage> pointages = Arrays.asList(p1, p2, p3, p4, p5, p6);
		
		Map<Date, List<Pointage>> hSups = new HashMap<Date, List<Pointage>>();
		Map<Date, List<Pointage>> primes = new HashMap<Date, List<Pointage>>();
		Map<Date, List<Pointage>> abs = new HashMap<Date, List<Pointage>>();
		
		VentilationService service = new VentilationService();
		
		// When
		service.distributePointages(pointages, hSups, primes, abs);
		
		// Then
		Iterator<Entry<Date, List<Pointage>>> i = hSups.entrySet().iterator();
		Entry<Date, List<Pointage>> h = i.next();
		assertEquals(p2.getDateLundi(), h.getKey());
		assertEquals(1, h.getValue().size());
		assertEquals(p2, h.getValue().get(0));
		
		h = i.next();
		assertEquals(p1.getDateLundi(), h.getKey());
		assertEquals(1, h.getValue().size());
		assertEquals(p1, h.getValue().get(0));
		
		i = primes.entrySet().iterator();
		h = i.next();
		assertEquals(new LocalDate(p3.getDateDebut()).withDayOfMonth(1).toDate(), h.getKey());
		assertEquals(2, h.getValue().size());
		assertEquals(p3, h.getValue().get(0));
		assertEquals(p5, h.getValue().get(1));
		
		i = abs.entrySet().iterator();
		h = i.next();
		assertEquals(p6.getDateLundi(), h.getKey());
		assertEquals(1, h.getValue().size());
		assertEquals(p6, h.getValue().get(0));

		h = i.next();
		assertEquals(p4.getDateLundi(), h.getKey());
		assertEquals(1, h.getValue().size());
		assertEquals(p4, h.getValue().get(0));
	}
	
	@Test
	public void processVentilationForAgent_HSup() {
		
		// Given
		RefTypePointageEnum typePointage = RefTypePointageEnum.H_SUP;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 7, 28).toDate());
		Date from = new LocalDate(2013, 7, 1).toDate();
		
		Pointage p1 = new Pointage();
		p1.setType(hSup);
		p1.setDateLundi(new LocalDate(2013, 7, 1).toDate());
		
		IPointageRepository pointageRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepo.getListPointagesForVentilationByDateEtat(idAgent, from, ventilDate.getDateVentilation(), typePointage))
				.thenReturn(Arrays.asList(p1));
		
		Spcarr carr = new Spcarr();
		carr.setCdcate(7); //CC
		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(Mockito.eq(Agent.getNoMatrFromIdAgent(idAgent)), Mockito.eq(p1.getDateLundi()))).thenReturn(carr);
		
		VentilHsup ventilHsup = Mockito.spy(new VentilHsup());
		Mockito.doNothing().when(ventilHsup).persist();
		IVentilationHSupService hSupV = Mockito.mock(IVentilationHSupService.class);
		Mockito.when(hSupV.processHSup(Mockito.eq(idAgent), Mockito.eq(carr), Mockito.anyListOf(Pointage.class), Mockito.eq(AgentStatutEnum.CC)))
				.thenReturn(ventilHsup);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "ventilationHSupService", hSupV);
		
		// When
		service.processVentilationForAgent(ventilDate, idAgent, from, ventilDate.getDateVentilation(), typePointage);
		
		// Then
		Mockito.verify(ventilHsup, Mockito.times(1)).persist();
		assertEquals(ventilDate, ventilHsup.getVentilDate());
	}
	
	@Test
	public void processVentilationForAgent_Abs() {
		
		// Given
		RefTypePointageEnum typePointage = RefTypePointageEnum.ABSENCE;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 7, 28).toDate());
		Date from = new LocalDate(2013, 7, 1).toDate();
		
		Pointage p1 = new Pointage();
		p1.setType(abs);
		p1.setDateDebut(new LocalDate(2013, 7, 4).toDate());
		
		IPointageRepository pointageRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepo.getListPointagesForVentilationByDateEtat(idAgent, from, ventilDate.getDateVentilation(), typePointage))
				.thenReturn(Arrays.asList(p1));
		
		VentilAbsence ventilAbs = Mockito.spy(new VentilAbsence());
		Mockito.doNothing().when(ventilAbs).persist();
		IVentilationAbsenceService absV = Mockito.mock(IVentilationAbsenceService.class);
		Mockito.when(absV.processAbsenceAgent(Mockito.eq(idAgent), Mockito.anyListOf(Pointage.class), Mockito.any(Date.class)))
				.thenReturn(ventilAbs);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepo);
		ReflectionTestUtils.setField(service, "ventilationAbsenceService", absV);
		
		// When
		service.processVentilationForAgent(ventilDate, idAgent, from, ventilDate.getDateVentilation(), typePointage);
		
		// Then
		Mockito.verify(ventilAbs, Mockito.times(1)).persist();
		assertEquals(ventilDate, ventilAbs.getVentilDate());
	}
	
	@Test
	public void processVentilationForAgent_Prime() {
		
		// Given
		RefTypePointageEnum typePointage = RefTypePointageEnum.PRIME;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 7, 28).toDate());
		Date from = new LocalDate(2013, 7, 1).toDate();
		
		Pointage p1 = new Pointage();
		p1.setType(prime);
		p1.setDateDebut(new LocalDate(2013, 7, 4).toDate());
		
		IPointageRepository pointageRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepo.getListPointagesForVentilationByDateEtat(idAgent, from, ventilDate.getDateVentilation(), typePointage))
				.thenReturn(Arrays.asList(p1));
		
		VentilPrime ventilPrime = Mockito.spy(new VentilPrime());
		Mockito.doNothing().when(ventilPrime).persist();
		IVentilationPrimeService primeV = Mockito.mock(IVentilationPrimeService.class);
		Mockito.when(primeV.processPrimesAgent(Mockito.eq(idAgent), Mockito.anyListOf(Pointage.class), Mockito.any(Date.class)))
				.thenReturn(Arrays.asList(ventilPrime));
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepo);
		ReflectionTestUtils.setField(service, "ventilationPrimeService", primeV);
		
		// When
		service.processVentilationForAgent(ventilDate, idAgent, from, ventilDate.getDateVentilation(), typePointage);
		
		// Then
		Mockito.verify(ventilPrime, Mockito.times(1)).persist();
		assertEquals(ventilDate, ventilPrime.getVentilDate());
	}
	
	@Test
	public void processVentilation_() {
		
	}
}
