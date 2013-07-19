package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPointagePK;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IPointageCalculeService;
import nc.noumea.mairie.ptg.service.IVentilationAbsenceService;
import nc.noumea.mairie.ptg.service.IVentilationHSupService;
import nc.noumea.mairie.ptg.service.IVentilationPrimeService;
import nc.noumea.mairie.sirh.domain.Agent;

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
		
		IVentilationRepository ventilationRepo = Mockito.mock(IVentilationRepository.class);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilationRepo);
		
		// When
		service.removePreviousVentilations(ventilDate, idAgent, typePointage);
		
		// Then
		Mockito.verify(ventilationRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.ABSENCE);
		Mockito.verify(ventilationRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.H_SUP);
		Mockito.verify(ventilationRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.PRIME);
		
	}
	
	@Test
	public void removePreviousVentilations_TypeSelected_H_SUP_Delete2Types() {
		
		// Given
		RefTypePointageEnum typePointage = RefTypePointageEnum.H_SUP;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 4, 6).toDate());
		
		IVentilationRepository ventilRepo = Mockito.mock(IVentilationRepository.class);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilRepo);
		
		// When
		service.removePreviousVentilations(ventilDate, idAgent, typePointage);
		
		// Then
		Mockito.verify(ventilRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.ABSENCE);
		Mockito.verify(ventilRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.H_SUP);
		Mockito.verify(ventilRepo, Mockito.never()).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.PRIME);
	}

	@Test
	public void removePreviousVentilations_TypeSelected_ABSENCE_Delete2Types() {
		
		// Given
		RefTypePointageEnum typePointage = RefTypePointageEnum.ABSENCE;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 4, 6).toDate());
		
		IVentilationRepository ventilRepo = Mockito.mock(IVentilationRepository.class);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilRepo);
		
		// When
		service.removePreviousVentilations(ventilDate, idAgent, typePointage);
		
		// Then
		Mockito.verify(ventilRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.ABSENCE);
		Mockito.verify(ventilRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.H_SUP);
		Mockito.verify(ventilRepo, Mockito.never()).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.PRIME);
	}
	
	@Test
	public void removePreviousVentilations_TypeSelected_PRIME_Delete1Type() {
		
		// Given
		RefTypePointageEnum typePointage = RefTypePointageEnum.PRIME;
		Integer idAgent = 9008765;
		VentilDate ventilDate = new VentilDate();
		ventilDate.setDateVentilation(new LocalDate(2013, 4, 6).toDate());
		
		IVentilationRepository ventilRepo = Mockito.mock(IVentilationRepository.class);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilRepo);
		
		// When
		service.removePreviousVentilations(ventilDate, idAgent, typePointage);
		
		// Then
		Mockito.verify(ventilRepo, Mockito.never()).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.ABSENCE);
		Mockito.verify(ventilRepo, Mockito.never()).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.H_SUP);
		Mockito.verify(ventilRepo, Mockito.times(1)).removeVentilationsForDateAgentAndType(ventilDate, idAgent, RefTypePointageEnum.PRIME);
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
		carr.setCdcate(7); //CC
		
		IVentilationRepository ventilRepo = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilRepo.getListPointagesAbsenceAndHSupForVentilation(idAgent, fromEtatDate, toEtatDate, dateLundi))
				.thenReturn(Arrays.asList(p1));
		
		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getPrimePointagesByAgent(idAgent,dateLundi)).thenReturn(Arrays.asList(1128, 1135));
		
		VentilHsup ventilHsup = Mockito.spy(new VentilHsup());
		Mockito.doNothing().when(ventilHsup).persist();
		IVentilationHSupService hSupV = Mockito.mock(IVentilationHSupService.class);
		Mockito.when(hSupV.processHSup(Mockito.eq(idAgent), Mockito.eq(carr), Mockito.anyListOf(Pointage.class), Mockito.eq(AgentStatutEnum.CC), Mockito.eq(false)))
				.thenReturn(ventilHsup);
		
		IVentilationAbsenceService absV = Mockito.mock(IVentilationAbsenceService.class);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "ventilationHSupService", hSupV);
		ReflectionTestUtils.setField(service, "ventilationAbsenceService", absV);
		
		// When
		service.processHSupAndAbsVentilationForWeekAndAgent(ventilDate, idAgent, carr, dateLundi, fromEtatDate, toEtatDate);
		
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
		carr.setCdcate(7); //CC
		
		IVentilationRepository ventilRepo = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilRepo.getListPointagesAbsenceAndHSupForVentilation(idAgent, fromEtatDate, toEtatDate, dateLundi))
				.thenReturn(Arrays.asList(p1));
		
		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getPrimePointagesByAgent(idAgent, dateLundi)).thenReturn(Arrays.asList(1128, 1150, 1135));
		
		VentilHsup ventilHsup = Mockito.spy(new VentilHsup());
		Mockito.doNothing().when(ventilHsup).persist();
		IVentilationHSupService hSupV = Mockito.mock(IVentilationHSupService.class);
		Mockito.when(hSupV.processHSup(Mockito.eq(idAgent), Mockito.eq(carr), Mockito.anyListOf(Pointage.class), Mockito.eq(AgentStatutEnum.CC), Mockito.eq(true)))
				.thenReturn(ventilHsup);
		
		IVentilationAbsenceService absV = Mockito.mock(IVentilationAbsenceService.class);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "ventilationHSupService", hSupV);
		ReflectionTestUtils.setField(service, "ventilationAbsenceService", absV);
		
		// When
		service.processHSupAndAbsVentilationForWeekAndAgent(ventilDate, idAgent, carr, dateLundi, fromEtatDate, toEtatDate);
		
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
		
		IVentilationRepository ventilRepo = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilRepo.getListPointagesAbsenceAndHSupForVentilation(idAgent, fromEtatDate, toEtatDate, dateLundi))
				.thenReturn(Arrays.asList(p1));
		
		IVentilationHSupService hSupV = Mockito.mock(IVentilationHSupService.class);
		
		VentilAbsence ventilAbs = Mockito.spy(new VentilAbsence());
		Mockito.doNothing().when(ventilAbs).persist();
		IVentilationAbsenceService absV = Mockito.mock(IVentilationAbsenceService.class);
		Mockito.when(absV.processAbsenceAgent(Mockito.eq(idAgent), Mockito.anyListOf(Pointage.class), Mockito.any(Date.class)))
				.thenReturn(ventilAbs);
		
		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getPrimePointagesByAgent(idAgent, dateLundi)).thenReturn(Arrays.asList(1128));
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilRepo);
		ReflectionTestUtils.setField(service, "ventilationAbsenceService", absV);
		ReflectionTestUtils.setField(service, "ventilationHSupService", hSupV);
		
		// When
		service.processHSupAndAbsVentilationForWeekAndAgent(ventilDate, idAgent, new Spcarr(), dateLundi, fromEtatDate, toEtatDate);
		
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
		
		IVentilationRepository ventilRepo = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilRepo.getListPointagesPrimeForVentilation(idAgent, fromEtatDate, toEtatDate, dateDebutMois))
				.thenReturn(Arrays.asList(p1));
		Mockito.when(ventilRepo.getListPointagesCalculesPrimeForVentilation(idAgent, dateDebutMois))
				.thenReturn(Arrays.asList(p2));
		
		VentilPrime ventilPrime = Mockito.spy(new VentilPrime());
		Mockito.doNothing().when(ventilPrime).persist();
		IVentilationPrimeService primeV = Mockito.mock(IVentilationPrimeService.class);
		Mockito.when(primeV.processPrimesAgent(Mockito.eq(idAgent), Mockito.anyListOf(Pointage.class), Mockito.eq(dateDebutMois)))
				.thenReturn(Arrays.asList(ventilPrime));
		
		VentilPrime ventilPrime2 = Mockito.spy(new VentilPrime());
		Mockito.doNothing().when(ventilPrime2).persist();
		Mockito.when(primeV.processPrimesCalculeesAgent(Mockito.eq(idAgent), Mockito.anyListOf(PointageCalcule.class), Mockito.eq(dateDebutMois)))
				.thenReturn(Arrays.asList(ventilPrime2));
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilRepo);
		ReflectionTestUtils.setField(service, "ventilationPrimeService", primeV);
		
		// When
		service.processPrimesVentilationForMonthAndAgent(ventilDate, idAgent, dateDebutMois, fromEtatDate, toEtatDate);
		
		// Then
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
		
		IVentilationRepository vRepo = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vRepo.getListPointagesForPrimesCalculees(idAgent, from, to, dateLundi)).thenReturn(ptgList);
		
		PointageCalcule pc1 = Mockito.spy(new PointageCalcule());
		Mockito.doNothing().when(pc1).persist();
		
		IPointageCalculeService ptgCService = Mockito.mock(IPointageCalculeService.class);
		Mockito.when(ptgCService.calculatePointagesForAgentAndWeek(Mockito.eq(idAgent), Mockito.eq(AgentStatutEnum.F), Mockito.eq(dateLundi), Mockito.eq(ptgList)))
			.thenReturn(Arrays.asList(pc1));
		
		Spcarr carr = new Spcarr();
		carr.setCdcate(20); // F
		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(Mockito.eq(Agent.getNoMatrFromIdAgent(idAgent)), Mockito.eq(dateLundi))).thenReturn(carr);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "ventilationRepository", vRepo);
		ReflectionTestUtils.setField(service, "pointageCalculeService", ptgCService);
		
		// When
		service.calculatePointages(idAgent, dateLundi, from, to);
		
		// Then
		Mockito.verify(mairieRepo, Mockito.times(1))
			.getAgentCurrentCarriere(Mockito.eq(Agent.getNoMatrFromIdAgent(idAgent)),Mockito.eq(new LocalDate(2013, 7, 1).toDate()));
		
		Mockito.verify(ptgCService, Mockito.times(1))
			.calculatePointagesForAgentAndWeek(Mockito.eq(idAgent), Mockito.eq(AgentStatutEnum.F), Mockito.eq(dateLundi), Mockito.eq(ptgList));
		
		Mockito.verify(pc1, Mockito.times(1)).persist();
	}
	
	@Test
	public void markPointagesAsVentile_AddNewVENTILEetatToPointagesNotVENTILE() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setType(abs);
		p1.setDateDebut(new LocalDate(2013, 7, 4).toDate());
		EtatPointagePK pk1 = new EtatPointagePK();
		pk1.setDateEtat(new Date());
		pk1.setPointage(p1);
		EtatPointage ep1 = new EtatPointage();
		ep1.setEtatPointagePk(pk1);
		ep1.setEtat(EtatPointageEnum.APPROUVE);
		p1.getEtats().add(ep1);
		
		Pointage p2 = new Pointage();
		p2.setType(abs);
		p2.setDateDebut(new LocalDate(2013, 7, 4).toDate());
		EtatPointagePK pk2 = new EtatPointagePK();
		pk2.setDateEtat(new Date());
		pk2.setPointage(p2);
		EtatPointage ep2 = new EtatPointage();
		ep2.setEtatPointagePk(pk2);
		ep2.setEtat(EtatPointageEnum.VENTILE);
		p2.getEtats().add(ep2);
		
		Date etatDate = new LocalDate(2013, 07, 01).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(etatDate);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		service.markPointagesAsVentile(Arrays.asList(p1, p2), 9008888);
		
		// Then
		assertEquals(2, p1.getEtats().size());
		assertEquals(ep1, p1.getEtats().get(0));
		assertEquals(EtatPointageEnum.APPROUVE, p1.getEtats().get(0).getEtat());
		assertEquals(EtatPointageEnum.VENTILE, p1.getEtats().get(1).getEtat());
		assertEquals(9008888, (int) p1.getEtats().get(1).getIdAgent());
		assertEquals(etatDate, p1.getEtats().get(1).getEtatPointagePk().getDateEtat());
		
		assertEquals(1, p2.getEtats().size());
		assertEquals(ep2, p2.getEtats().get(0));
		assertEquals(EtatPointageEnum.VENTILE, p2.getEtats().get(0).getEtat());
	}
	
	@Test
	public void isAgentEligibleToVentilation_AgentisFandTargetIsF_ReturnSpcarr() {
		
		// Given
		Date asOfDate = new LocalDate(2013, 1, 28).toDate();
		
		Spcarr carr = new Spcarr();
		carr.setCdcate(20); // F
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgentCurrentCarriere(7898, asOfDate)).thenReturn(carr);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		Spcarr result = service.isAgentEligibleToVentilation(9007898, AgentStatutEnum.F, asOfDate);
		
		// Then
		assertEquals(result, carr);
	}
	
	@Test
	public void isAgentEligibleToVentilation_AgentisCCandTargetIsF_ReturnNull() {
		
		// Given
		Date asOfDate = new LocalDate(2013, 1, 28).toDate();
		
		Spcarr carr = new Spcarr();
		carr.setCdcate(7); // CC
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgentCurrentCarriere(7898, asOfDate)).thenReturn(carr);
		
		VentilationService service = new VentilationService();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		Spcarr result = service.isAgentEligibleToVentilation(9007898, AgentStatutEnum.F, asOfDate);
		
		// Then
		assertNull(result);
	}
	
	@Test
	public void getTypeChainePaieFromStatut_Statut_F_ReturnHCC() {
		
		// Given
		VentilationService service = new VentilationService();
		
		// Then
		assertEquals(TypeChainePaieEnum.HCC, service.getTypeChainePaieFromStatut(AgentStatutEnum.F));
		
	}
	
	@Test
	public void getTypeChainePaieFromStatut_Statut_C_ReturnHCC() {
		
		// Given
		VentilationService service = new VentilationService();
		
		// Then
		assertEquals(TypeChainePaieEnum.HCC, service.getTypeChainePaieFromStatut(AgentStatutEnum.C));
	}
	
	@Test
	public void getTypeChainePaieFromStatut_Statut_CC_ReturnCC() {
		
		// Given
		VentilationService service = new VentilationService();
		
		// Then
		assertEquals(TypeChainePaieEnum.CC, service.getTypeChainePaieFromStatut(AgentStatutEnum.CC));
	}
	
	@Test
	public void getDistinctDateLundiFromListOfDates_3dates_2dateLundi() {
	
		// Given
		List<Date> dates = Arrays.asList(
				new LocalDate(2013, 7, 18).toDate(),
				new LocalDate(2013, 7, 16).toDate(),
				new LocalDate(2013, 7, 25).toDate()
				);
				
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
		List<Date> dates = Arrays.asList(
				new LocalDate(2013, 7, 18).toDate(),
				new LocalDate(2013, 7, 16).toDate(),
				new LocalDate(2013, 8, 30).toDate()
				);
				
		VentilationService service = new VentilationService();

		// When
		List<Date> result = service.getDistinctDateDebutMoisFromListOfDates(dates);
		
		// Then
		assertEquals(2, result.size());
		assertEquals(new LocalDate(2013, 7, 1).toDate(), result.get(0));
		assertEquals(new LocalDate(2013, 8, 1).toDate(), result.get(1));
	}
	

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
		
		IVentilationRepository vRepo = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vRepo.getLatestVentilDate(TypeChainePaieEnum.HCC, true)).thenReturn(lastPaidVentilDate);
		Mockito.when(vRepo.getLatestVentilDate(TypeChainePaieEnum.HCC, false)).thenReturn(lastUnPaidVentilDate);
		Mockito.when(vRepo.getDistinctDatesOfPointages(9005432, lastUnPaidDate, ventilationDate)).thenReturn(pointagesDates);
		Mockito.when(vRepo.getListIdAgentsForVentilationByDateAndEtat(lastPaidDate, lastUnPaidDate)).thenReturn(agentList);
		
		VentilationService service = Mockito.spy(new VentilationService());
		ReflectionTestUtils.setField(service, "ventilationRepository", vRepo);
		
		Spcarr carr = new Spcarr();
		Mockito.doReturn(carr).when(service).isAgentEligibleToVentilation(9005432, statut, lastUnPaidDate);
		Mockito.doReturn(null).when(service).isAgentEligibleToVentilation(9005431, statut, lastUnPaidDate);
		Mockito.doNothing().when(service).removePreviousVentilations(lastUnPaidVentilDate, 9005432, null);
		
		List<Date> datesLundi = new ArrayList<Date>();
		datesLundi.add(new LocalDate(2013, 7, 22).toDate());
		Mockito.doReturn(datesLundi).when(service).getDistinctDateLundiFromListOfDates(pointagesDates);
		
		List<Date> datesDebutMois = new ArrayList<Date>();
		datesDebutMois.add(new LocalDate(2013, 7, 1).toDate());
		Mockito.doReturn(datesDebutMois).when(service).getDistinctDateDebutMoisFromListOfDates(pointagesDates);
				
		List<Pointage> ptgVentiles = new ArrayList<Pointage>();
		Mockito.doReturn(ptgVentiles).when(service).processHSupAndAbsVentilationForWeekAndAgent(lastUnPaidVentilDate, 9005432, carr, datesLundi.get(0), lastPaidDate, lastUnPaidDate);

		Mockito.doNothing().when(service).removePreviousCalculatedPointages(9005432, datesLundi.get(0));
		Mockito.doNothing().when(service).calculatePointages(9005432, datesLundi.get(0), lastPaidDate, lastUnPaidDate);
		Mockito.doReturn(ptgVentiles).when(service).processPrimesVentilationForMonthAndAgent(lastUnPaidVentilDate, 9005432, datesDebutMois.get(0), lastPaidDate, lastUnPaidDate);

		Mockito.doNothing().when(service).markPointagesAsVentile(ptgVentiles, 9005432);
		
		// When
		service.processVentilation(9008765, new ArrayList<Integer>(), ventilationDate, statut, pointageType);
		
		// Then
		Mockito.verify(service, Mockito.times(1)).isAgentEligibleToVentilation(9005432, statut, lastUnPaidDate);
		Mockito.verify(service, Mockito.times(1)).isAgentEligibleToVentilation(9005431, statut, lastUnPaidDate);
		Mockito.verify(service, Mockito.times(1)).removePreviousVentilations(lastUnPaidVentilDate, 9005432, null);
		Mockito.verify(service, Mockito.times(2)).getDistinctDateLundiFromListOfDates(pointagesDates);
		Mockito.verify(service, Mockito.times(1)).getDistinctDateDebutMoisFromListOfDates(pointagesDates);
		Mockito.verify(service, Mockito.times(1)).processHSupAndAbsVentilationForWeekAndAgent(lastUnPaidVentilDate, 9005432, carr, datesLundi.get(0), lastPaidDate, lastUnPaidDate);
		Mockito.verify(service, Mockito.times(1)).removePreviousCalculatedPointages(9005432, datesLundi.get(0));
		Mockito.verify(service, Mockito.times(1)).calculatePointages(9005432, datesLundi.get(0), lastPaidDate, lastUnPaidDate);
		Mockito.verify(service, Mockito.times(1)).processPrimesVentilationForMonthAndAgent(lastUnPaidVentilDate, 9005432, datesDebutMois.get(0), lastPaidDate, lastUnPaidDate);
		Mockito.verify(service, Mockito.times(1)).markPointagesAsVentile(ptgVentiles, 9005432);
	}
	
}
