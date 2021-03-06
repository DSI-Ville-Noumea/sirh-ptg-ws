package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import nc.noumea.mairie.abs.dto.DemandeDto;
import nc.noumea.mairie.abs.dto.RefTypeSaisiDto;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spbhor;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

public class VentilationHSupServiceTest {

	private static RefTypePointage	hSup;
	private static RefTypePointage	abs;

	@BeforeClass
	public static void Setup() {
		hSup = new RefTypePointage();
		hSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		abs = new RefTypePointage();
		abs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
	}

	@Test
	public void processHSup_NoPointages_ReturnNull() {

		// Given
		List<Pointage> pointages = new ArrayList<Pointage>();
		Date dateLundi = new LocalDate(2013, 7, 22).toDate();

		IVentilationRepository ventilationRepository = Mockito.mock(IVentilationRepository.class);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilationRepository);

		// When
		VentilHsup result = service.processHSup(9008765, null, dateLundi, pointages, null, false, new VentilDate(), null);

		// Then
		assertNull(result);
	}

	@Test
	public void processHSup_NoPointages_ReturnNull_listOldVentilHSupEmpty() {

		// Given
		List<Pointage> pointages = new ArrayList<Pointage>();
		Date dateLundi = new LocalDate(2013, 7, 22).toDate();

		VentilDate ventilDate = new VentilDate();
		ventilDate.setIdVentilDate(1);

		List<VentilHsup> listOldVentilHSup = new ArrayList<VentilHsup>();

		IVentilationRepository ventilationRepository = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilationRepository.getListOfOldVentilHSForAgentAndDateLundi(9008765, dateLundi, ventilDate.getIdVentilDate()))
				.thenReturn(listOldVentilHSup);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilationRepository);

		// When
		VentilHsup result = service.processHSup(9008765, null, dateLundi, pointages, null, false, ventilDate, null);

		// Then
		assertNull(result);
	}

	/**
	 * dans le cas ou il y avait une ancienne ventilation (ex : pointage rejete
	 * apres une ventilation ou export de paie)
	 */
	@Test
	public void processHSup_NoPointages_ReturnVentilHSupVide() {

		// Given
		List<Pointage> pointages = new ArrayList<Pointage>();
		Date dateLundi = new LocalDate(2013, 7, 22).toDate();

		VentilDate ventilDate = new VentilDate();
		ventilDate.setIdVentilDate(1);

		List<VentilHsup> listOldVentilHSup = new ArrayList<VentilHsup>();
		listOldVentilHSup.add(new VentilHsup());

		IVentilationRepository ventilationRepository = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilationRepository.getListOfOldVentilHSForAgentAndDateLundi(9008765, dateLundi, ventilDate.getIdVentilDate()))
				.thenReturn(listOldVentilHSup);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilationRepository);

		// When
		VentilHsup result = service.processHSup(9008765, null, dateLundi, pointages, null, false, ventilDate, null);

		// Then
		assertNotNull(result);
		assertEquals(result.getIdAgent().intValue(), 9008765);
		assertEquals(result.getDateLundi(), dateLundi);
		assertEquals(result.getEtat(), EtatPointageEnum.VENTILE);
		assertEquals(0, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(0, result.getMSup());
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());
		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(0, result.getMSimple());
		assertEquals(0, result.getMComposees());
		assertEquals(0, result.getMsNuitRecup());
		assertEquals(0, result.getMsdjfRecup());
		assertEquals(0, result.getMNormalesRecup());
		assertEquals(0, result.getMSimpleRecup());
		assertEquals(0, result.getMComposeesRecup());
	}

	/**
	 * dans le cas ou il y avait une ancienne ventilation sur un mois anterieur
	 * avedc des pointages journalises rejetes
	 */
	@Test
	public void processHSup_PointagesJournalisesRejetes_ReturnVentilHSupVide() {

		// Given
		List<Pointage> pointages = new ArrayList<Pointage>();
		Date dateLundi = new LocalDate(2013, 7, 22).toDate();

		VentilDate ventilDate = new VentilDate();
		ventilDate.setIdVentilDate(1);

		List<VentilHsup> listOldVentilHSup = new ArrayList<VentilHsup>();

		IVentilationRepository ventilationRepository = Mockito.mock(IVentilationRepository.class);
		Mockito.when(ventilationRepository.getListOfOldVentilHSForAgentAndDateLundi(9008765, dateLundi, ventilDate.getIdVentilDate()))
				.thenReturn(listOldVentilHSup);

		Pointage ptg = new Pointage();
		ptg.setType(hSup);
		List<Pointage> pointagesJournalisesRejetes = new ArrayList<Pointage>();
		pointagesJournalisesRejetes.add(ptg);

		Mockito.when(ventilationRepository.getPriorOldVentilHSupAgentAndDate(9008765, dateLundi, ventilDate)).thenReturn(new VentilHsup());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "ventilationRepository", ventilationRepository);

		// When
		VentilHsup result = service.processHSup(9008765, null, dateLundi, pointages, null, false, ventilDate, pointagesJournalisesRejetes);

		// Then
		assertNotNull(result);
		assertEquals(result.getIdAgent().intValue(), 9008765);
		assertEquals(result.getDateLundi(), dateLundi);
		assertEquals(result.getEtat(), EtatPointageEnum.VENTILE);
		assertEquals(0, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(0, result.getMSup());
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());
		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(0, result.getMSimple());
		assertEquals(0, result.getMComposees());
		assertEquals(0, result.getMsNuitRecup());
		assertEquals(0, result.getMsdjfRecup());
		assertEquals(0, result.getMNormalesRecup());
		assertEquals(0, result.getMSimpleRecup());
		assertEquals(0, result.getMComposeesRecup());
	}

	@Test
	public void processHSupFonctionnaire_base39H_2HS_Recuperees() {

		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 6, 15, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 7, 0, 0).toDate());
		p1.setHeureSupRecuperee(true);
		p1.setHeureSupRappelService(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 30, 18, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 19, 15, 0).toDate());
		p2.setHeureSupRecuperee(true);
		p2.setHeureSupRappelService(false);
		p2.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(Mockito.any(DateTime.class))).thenReturn(false);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(2 * 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(2 * 60, result.getMSup());
		assertEquals(2 * 60, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(2 * 60, result.getMSimple());
		assertEquals(0, result.getMComposees());

		assertEquals(0, result.getMsNuitRecup());
		assertEquals(0, result.getMsdjfRecup());
		assertEquals(0, result.getMNormalesRecup());
		assertEquals(2 * 60, result.getMSimpleRecup());
		assertEquals(0, result.getMComposeesRecup());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHSupFonctionnaire_base39H_2HS_Recuperees_1HS_RappelService() {

		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 6, 15, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 7, 0, 0).toDate());
		p1.setHeureSupRecuperee(true);
		p1.setHeureSupRappelService(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 30, 18, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 19, 15, 0).toDate());
		p2.setHeureSupRecuperee(true);
		p2.setHeureSupRappelService(true);
		p2.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(Mockito.any(DateTime.class))).thenReturn(false);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(2 * 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(2 * 60, result.getMSup());
		assertEquals(2 * 60, result.getMRecuperees());
		assertEquals(new Double(1.25 * 60).intValue(), result.getMRappelService());

		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(2 * 60, result.getMSimple());
		assertEquals(0, result.getMComposees());

		assertEquals(0, result.getMsNuitRecup());
		assertEquals(0, result.getMsdjfRecup());
		assertEquals(0, result.getMNormalesRecup());
		assertEquals(2 * 60, result.getMSimpleRecup());
		assertEquals(0, result.getMComposeesRecup());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHSupFonctionnaire_base39H_5HS_2HS_Recuperees() {

		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 6, 15, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 7, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setHeureSupRappelService(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 30, 18, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 19, 15, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setHeureSupRappelService(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 05, 2, 18, 0, 0).toDate());
		p3.setDateFin(new DateTime(2012, 05, 2, 20, 0, 0).toDate());
		p3.setHeureSupRecuperee(true);
		p3.setHeureSupRappelService(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2012, 05, 3, 6, 30, 0).toDate());
		p4.setDateFin(new DateTime(2012, 05, 3, 7, 30, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setHeureSupRappelService(false);
		p4.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(Mockito.any(DateTime.class))).thenReturn(false);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(5 * 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(5 * 60, result.getMSup());
		assertEquals(2 * 60, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(3 * 60, result.getMSimple());
		assertEquals(2 * 60, result.getMComposees());

		assertEquals(0, result.getMsNuitRecup());
		assertEquals(0, result.getMsdjfRecup());
		assertEquals(0, result.getMNormalesRecup());
		assertEquals(2 * 60, result.getMSimpleRecup());
		assertEquals(0, result.getMComposeesRecup());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHSupFonctionnaire_base39H_3HDJF_5HS_6HS_Recuperees() {

		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 6, 15, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 7, 15, 0).toDate());
		p1.setHeureSupRecuperee(true);
		p1.setHeureSupRappelService(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 30, 18, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 19, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setHeureSupRappelService(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 05, 2, 18, 0, 0).toDate());
		p3.setDateFin(new DateTime(2012, 05, 2, 20, 0, 0).toDate());
		p3.setHeureSupRecuperee(true);
		p3.setHeureSupRappelService(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2012, 05, 3, 6, 30, 0).toDate());
		p4.setDateFin(new DateTime(2012, 05, 3, 7, 30, 0).toDate());
		p4.setHeureSupRecuperee(true);
		p4.setHeureSupRappelService(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2012, 05, 6, 6, 0, 0).toDate());
		p5.setDateFin(new DateTime(2012, 05, 6, 8, 0, 0).toDate());
		p5.setHeureSupRecuperee(true);
		p5.setHeureSupRappelService(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2012, 05, 6, 9, 0, 0).toDate());
		p6.setDateFin(new DateTime(2012, 05, 6, 10, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setHeureSupRappelService(false);
		p6.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(Mockito.any(DateTime.class))).thenReturn(false);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5, p6), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(8 * 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(8 * 60, result.getMSup());
		assertEquals(6 * 60, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit());
		assertEquals(3 * 60, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(3 * 60, result.getMSimple());
		assertEquals(2 * 60, result.getMComposees());

		assertEquals(0, result.getMsNuitRecup());
		assertEquals(2 * 60, result.getMsdjfRecup());
		assertEquals(0, result.getMNormalesRecup());
		assertEquals(3 * 60, result.getMSimpleRecup());
		assertEquals(1 * 60, result.getMComposeesRecup());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHSupFonctionnaire_base39H_6HS_Recuperees_3HDJF_3HSI_2HCO_Recuperees() {

		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 05, 3, 6, 0, 0).toDate());
		p1.setDateFin(new DateTime(2012, 05, 3, 7, 0, 0).toDate());
		p1.setHeureSupRecuperee(true);
		p1.setHeureSupRappelService(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 05, 4, 6, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 05, 4, 9, 0, 0).toDate());
		p2.setHeureSupRecuperee(true);
		p2.setHeureSupRappelService(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 05, 4, 9, 0, 0).toDate());
		p3.setDateFin(new DateTime(2012, 05, 4, 10, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setHeureSupRappelService(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2012, 05, 6, 7, 0, 0).toDate());
		p4.setDateFin(new DateTime(2012, 05, 6, 8, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setHeureSupRappelService(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2012, 05, 6, 8, 0, 0).toDate());
		p5.setDateFin(new DateTime(2012, 05, 6, 10, 0, 0).toDate());
		p5.setHeureSupRecuperee(true);
		p5.setHeureSupRappelService(false);
		p5.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(Mockito.any(DateTime.class))).thenReturn(false);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(8 * 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(8 * 60, result.getMSup());
		assertEquals(6 * 60, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit());
		assertEquals(3 * 60, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(3 * 60, result.getMSimple());
		assertEquals(2 * 60, result.getMComposees());

		assertEquals(0, result.getMsNuitRecup());
		assertEquals(2 * 60, result.getMsdjfRecup());
		assertEquals(0, result.getMNormalesRecup());
		assertEquals(3 * 60, result.getMSimpleRecup());
		assertEquals(1 * 60, result.getMComposeesRecup());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHSupFonctionnaire_base39H_5HS_2HS_Recuperees_2HCO_Recuperees() {

		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 4, 0, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 6, 0, 0).toDate());
		p1.setHeureSupRecuperee(true);
		p1.setHeureSupRappelService(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 30, 18, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 21, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setHeureSupRappelService(false);
		p2.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(Mockito.any(DateTime.class))).thenReturn(false);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(5 * 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(5 * 60, result.getMSup());
		assertEquals(2 * 60, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(3 * 60, result.getMSimple());
		assertEquals(2 * 60, result.getMComposees());

		assertEquals(0, result.getMsNuitRecup());
		assertEquals(0, result.getMsdjfRecup());
		assertEquals(0, result.getMNormalesRecup());
		assertEquals(2 * 60, result.getMSimpleRecup());
		assertEquals(0, result.getMComposeesRecup());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHSupCC_base39H_10HS_9HS_Recuperees() {

		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 6, 15, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 7, 15, 0).toDate());
		p1.setHeureSupRecuperee(true);
		p1.setHeureSupRappelService(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 30, 17, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 19, 0, 0).toDate());
		p2.setHeureSupRecuperee(true);
		p2.setHeureSupRappelService(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 05, 2, 18, 0, 0).toDate());
		p3.setDateFin(new DateTime(2012, 05, 2, 20, 0, 0).toDate());
		p3.setHeureSupRecuperee(true);
		p3.setHeureSupRappelService(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2012, 05, 3, 6, 30, 0).toDate());
		p4.setDateFin(new DateTime(2012, 05, 3, 7, 30, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setHeureSupRappelService(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2012, 05, 4, 6, 0, 0).toDate());
		p5.setDateFin(new DateTime(2012, 05, 4, 10, 0, 0).toDate());
		p5.setHeureSupRecuperee(true);
		p5.setHeureSupRappelService(false);
		p5.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(Mockito.any(DateTime.class))).thenReturn(false);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(10 * 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(10 * 60, result.getMSup());
		assertEquals(9 * 60, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(0, result.getMSimple());
		assertEquals(0, result.getMComposees());
		assertEquals(8 * 60, result.getMSup25());
		assertEquals(2 * 60, result.getMSup50());

		assertEquals(0, result.getMsNuitRecup());
		assertEquals(0, result.getMsdjfRecup());
		assertEquals(0, result.getMNormalesRecup());
		assertEquals(0, result.getMSimpleRecup());
		assertEquals(0, result.getMComposeesRecup());
		assertEquals(7 * 60, result.getMSup25Recup());
		assertEquals(2 * 60, result.getMSup50Recup());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHSupCC_base39H_10HS_Recuperees_9HS_7at25_2at50() {

		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 5, 0, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 8, 0, 0).toDate());
		p1.setHeureSupRecuperee(true);
		p1.setHeureSupRappelService(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 05, 2, 5, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 05, 2, 6, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setHeureSupRappelService(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 05, 2, 6, 0, 0).toDate());
		p3.setDateFin(new DateTime(2012, 05, 2, 8, 0, 0).toDate());
		p3.setHeureSupRecuperee(true);
		p3.setHeureSupRappelService(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2012, 05, 5, 5, 0, 0).toDate());
		p4.setDateFin(new DateTime(2012, 05, 5, 8, 0, 0).toDate());
		p4.setHeureSupRecuperee(true);
		p4.setHeureSupRappelService(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2012, 05, 5, 19, 0, 0).toDate());
		p5.setDateFin(new DateTime(2012, 05, 5, 20, 0, 0).toDate());
		p5.setHeureSupRecuperee(true);
		p5.setHeureSupRappelService(false);
		p5.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(Mockito.any(DateTime.class))).thenReturn(false);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(10 * 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(10 * 60, result.getMSup());
		assertEquals(9 * 60, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(0, result.getMSimple());
		assertEquals(0, result.getMComposees());
		assertEquals(8 * 60, result.getMSup25());
		assertEquals(2 * 60, result.getMSup50());

		assertEquals(0, result.getMsNuitRecup());
		assertEquals(0, result.getMsdjfRecup());
		assertEquals(0, result.getMNormalesRecup());
		assertEquals(0, result.getMSimpleRecup());
		assertEquals(0, result.getMComposeesRecup());
		assertEquals(7 * 60, result.getMSup25Recup());
		assertEquals(2 * 60, result.getMSup50Recup());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHSupCC_base39H_10HS_9HS_Recuperees_8at25_1at50() {

		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 5, 0, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 8, 0, 0).toDate());
		p1.setHeureSupRecuperee(true);
		p1.setHeureSupRappelService(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 05, 2, 5, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 05, 2, 8, 0, 0).toDate());
		p2.setHeureSupRecuperee(true);
		p2.setHeureSupRappelService(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 05, 5, 5, 0, 0).toDate());
		p3.setDateFin(new DateTime(2012, 05, 5, 7, 0, 0).toDate());
		p3.setHeureSupRecuperee(true);
		p3.setHeureSupRappelService(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2012, 05, 5, 7, 0, 0).toDate());
		p4.setDateFin(new DateTime(2012, 05, 5, 8, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setHeureSupRappelService(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2012, 05, 5, 19, 0, 0).toDate());
		p5.setDateFin(new DateTime(2012, 05, 5, 20, 0, 0).toDate());
		p5.setHeureSupRecuperee(true);
		p5.setHeureSupRappelService(false);
		p5.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(Mockito.any(DateTime.class))).thenReturn(false);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(10 * 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(10 * 60, result.getMSup());
		assertEquals(9 * 60, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(0, result.getMSimple());
		assertEquals(0, result.getMComposees());
		assertEquals(8 * 60, result.getMSup25());
		assertEquals(2 * 60, result.getMSup50());

		assertEquals(0, result.getMsNuitRecup());
		assertEquals(0, result.getMsdjfRecup());
		assertEquals(0, result.getMNormalesRecup());
		assertEquals(0, result.getMSimpleRecup());
		assertEquals(0, result.getMComposeesRecup());
		assertEquals(8 * 60, result.getMSup25Recup());
		assertEquals(1 * 60, result.getMSup50Recup());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHSupConventionCollective_CC_base39H_has1150Prime() {

		// Given
		Date dateLundi = new LocalDate(2013, 04, 1).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 04, 1, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 04, 1, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 04, 1, 20, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 04, 1, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 04, 2, 22, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 04, 3, 0, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isHoliday(new DateTime(2013, 04, 1, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), true, new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(14 * 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(14 * 60, result.getMSup());
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMMai());
		assertEquals(8 * 60, result.getMSup25());
		assertEquals(6 * 60, result.getMSup50());
		assertEquals(0, result.getMNormales());
		assertEquals(0, result.getMSimple());
		assertEquals(0, result.getMComposees());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHSupFonctionnaire_CustomExample_base58H30() {

		// Given
		Date dateLundi = new LocalDate(2013, 05, 20).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 05, 20, 12, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 20, 14, 0, 0).toDate());
		p1.setType(abs);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 05, 21, 4, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 21, 6, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 05, 22, 4, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 05, 22, 6, 30, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 05, 22, 7, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 05, 22, 8, 30, 0).toDate());
		p4.setType(abs);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 05, 23, 21, 0, 0).toDate());
		p5.setDateFin(new DateTime(2013, 05, 23, 21, 30, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 05, 24, 21, 0, 0).toDate());
		p6.setDateFin(new DateTime(2013, 05, 24, 22, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.45);
		spbase.setHeureMardi(8.45);
		spbase.setHeureMercredi(8.45);
		spbase.setHeureJeudi(8.45);
		spbase.setHeureVendredi(8.45);
		spbase.setHeureSamedi(4.15);
		spbase.setHeureDimanche(10.30);
		spbase.setBaseCalculee(58.30);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(Mockito.any(DateTime.class))).thenReturn(false);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5, p6), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(360, result.getMHorsContrat());
		assertEquals(210, result.getMAbsences());
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(90, result.getMSup());
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(90, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(0, result.getMSimple());
		assertEquals(0, result.getMComposees());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHSupFonctionnaire_HSupNuit_22h_to_4h_base39H() {

		// Given
		Date dateLundi = new LocalDate(2012, 4, 30).toDate();

		// hsup from 2/5 22h to 3/5 4h -> 6hsups nuit
		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 4, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 5, 4, 22, 0, 0).toDate());
		p1.setDateFin(new DateTime(2012, 5, 5, 4, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(Mockito.any(DateTime.class))).thenReturn(false);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(6 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(6 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(6 * 60, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/******************************************************
	 * TESTS UNITAIRES BASES SUR LES TABLEAUX EXCELS DE MICHEL RECUS PAR FICHIER
	 * EXCEL LE 18/07/2014 : "Copie de Exemple calculs HS JDFet nuit V3.xlsx"
	 */

	/************************************************************
	 ***************** FONCTIONNAIRE ****************************
	 ***********************************************************/

	/**
	 * test 2333
	 */
	@Test
	public void processHSupFonctionnaire_testExcel2333() {

		// Given
		Date dateLundi = new LocalDate(2013, 10, 28).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 1, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 1, 15, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 2, 8, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 2, 14, 30, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 3, 8, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 3, 11, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 1, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 1, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(16.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(16.5 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0 * 60, result.getMsNuit(), 0);
		assertEquals(10 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(3 * 60, result.getMSimple(), 0);
		assertEquals(3.5 * 60, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test 3876
	 */
	@Test
	public void processHSupFonctionnaire_testExcel3876() {

		// Given
		Date dateLundi = new LocalDate(2013, 10, 28).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 1, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 1, 20, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 2, 8, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 2, 18, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 3, 8, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 3, 13, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 1, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 1, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(27 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(27 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0 * 60, result.getMsNuit(), 0);
		assertEquals(17 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(3 * 60, result.getMSimple(), 0);
		assertEquals(7 * 60, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test 4029
	 */
	@Test
	public void processHSupFonctionnaire_testExcel4029() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 14, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 20, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(10 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(10 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit(), 0);
		assertEquals(6 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(3 * 60, result.getMSimple(), 0);
		assertEquals(1 * 60, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test 3414
	 */
	@Test
	public void processHSupFonctionnaire_testExcel3414() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 11, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 6, 30, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 7, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 12, 22, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 13, 6, 30, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 13, 7, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 11, 13, 16, 0, 0).toDate());
		p5.setDateFin(new DateTime(2013, 11, 13, 18, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 14, 6, 30, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 14, 7, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		Pointage p7 = new Pointage();
		p7.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p7.setDateDebut(new DateTime(2013, 11, 14, 16, 0, 0).toDate());
		p7.setDateFin(new DateTime(2013, 11, 14, 22, 0, 0).toDate());
		p7.setHeureSupRecuperee(false);
		p7.setType(hSup);

		Pointage p8 = new Pointage();
		p8.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p8.setDateDebut(new DateTime(2013, 11, 15, 6, 30, 0).toDate());
		p8.setDateFin(new DateTime(2013, 11, 15, 7, 0, 0).toDate());
		p8.setHeureSupRecuperee(false);
		p8.setType(hSup);

		Pointage p9 = new Pointage();
		p9.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p9.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p9.setDateFin(new DateTime(2013, 11, 15, 17, 0, 0).toDate());
		p9.setHeureSupRecuperee(false);
		p9.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9),
				new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(21 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(21 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(2 * 60, result.getMsNuit(), 0);
		assertEquals(3 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(3 * 60, result.getMSimple(), 0);
		assertEquals(13 * 60, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test BOUYGUES
	 */
	@Test
	public void processHSupFonctionnaire_testExcelBOUYGUES() {

		// Given
		Date dateLundi = new LocalDate(2013, 12, 2).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 12, 2, 19, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 12, 2, 22, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 12, 3, 19, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 12, 3, 23, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 12, 4, 19, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 12, 4, 23, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 12, 5, 19, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 12, 5, 22, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 12, 7, 13, 30, 0).toDate());
		p5.setDateFin(new DateTime(2013, 12, 7, 18, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(18.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(18.5 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(6 * 60, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(3 * 60, result.getMSimple(), 0);
		assertEquals(9.5 * 60, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test PLEIN TEMPS Nuit et DJF
	 */
	@Test
	public void processHSupFonctionnaire_testExcelPLEIN_TEMPS_NUIT_DJF() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 11, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p1_bis = new Pointage();
		p1_bis.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1_bis.setDateDebut(new DateTime(2013, 11, 11, 20, 0, 0).toDate());
		p1_bis.setDateFin(new DateTime(2013, 11, 11, 23, 0, 0).toDate());
		p1_bis.setHeureSupRecuperee(false);
		p1_bis.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 6, 30, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 7, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 12, 22, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 13, 6, 30, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 13, 7, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 11, 13, 16, 0, 0).toDate());
		p5.setDateFin(new DateTime(2013, 11, 13, 18, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 14, 6, 30, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 14, 7, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		Pointage p7 = new Pointage();
		p7.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p7.setDateDebut(new DateTime(2013, 11, 14, 16, 0, 0).toDate());
		p7.setDateFin(new DateTime(2013, 11, 14, 22, 0, 0).toDate());
		p7.setHeureSupRecuperee(false);
		p7.setType(hSup);

		Pointage p8 = new Pointage();
		p8.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p8.setDateDebut(new DateTime(2013, 11, 15, 6, 30, 0).toDate());
		p8.setDateFin(new DateTime(2013, 11, 15, 7, 0, 0).toDate());
		p8.setHeureSupRecuperee(false);
		p8.setType(hSup);

		Pointage p9 = new Pointage();
		p9.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p9.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p9.setDateFin(new DateTime(2013, 11, 15, 17, 0, 0).toDate());
		p9.setHeureSupRecuperee(false);
		p9.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 20, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p1_bis, p2, p3, p4, p5, p6, p7, p8, p9),
				new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(24 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(24 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(4 * 60, result.getMsNuit(), 0);
		assertEquals(4 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(3 * 60, result.getMSimple(), 0);
		assertEquals(13 * 60, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test MI-TEMPS 20H NUIT ET DJF
	 */
	@Test
	public void processHSupFonctionnaire_testExcel_MI_TEMPS_NUIT_DJF() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 20, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 23, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(4.0);
		spbase.setHeureMardi(4.0);
		spbase.setHeureMercredi(4.0);
		spbase.setHeureJeudi(4.0);
		spbase.setHeureVendredi(4.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(20.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 20, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p6), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(9 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(7 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(6 * 60, result.getMsNuit(), 0);
		assertEquals(1 * 60, result.getMsdjf(), 0);
		assertEquals(2 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test PLEIN TEMPS CONGE ANNUEL
	 */
	@Test
	public void processHSupFonctionnaire_testExcel_PLEIN_TEMPS_CONGE_ANNUEL() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(4 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(4 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(2 * 60, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(2 * 60, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test MALADIE Nuit et DJF 39H
	 */
	@Test
	public void processHSupFonctionnaire_testExcel_MALADIE_Nuit_DJF_39H() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		DemandeDto absence = new DemandeDto();
		absence.setDateDebut(new DateTime(2013, 11, 12, 0, 0, 0).toDate());
		absence.setDateFin(new DateTime(2013, 11, 12, 23, 59, 59).toDate());

		List<DemandeDto> listAbsence = new ArrayList<DemandeDto>();
		listAbsence.add(absence);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureLundi()))
				.thenReturn(new Double(spbase.getHeureLundi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureMardi()))
				.thenReturn(new Double(spbase.getHeureMardi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureMercredi()))
				.thenReturn(new Double(spbase.getHeureMercredi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureJeudi()))
				.thenReturn(new Double(spbase.getHeureJeudi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureVendredi()))
				.thenReturn(new Double(spbase.getHeureVendredi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getBaseCalculee()))
				.thenReturn(new Double(spbase.getBaseCalculee()).intValue() * 60);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 20, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		List<RefTypeSaisiDto> listTypeDemande = new ArrayList<RefTypeSaisiDto>();
		RefTypeSaisiDto typeDemande1 = new RefTypeSaisiDto();
		typeDemande1.setUniteDecompte("jours");
		listTypeDemande.add(typeDemande1);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(listAbsence);
		Mockito.when(absWsConsumer.getTypeSaisiAbsence(absence.getIdTypeDemande())).thenReturn(listTypeDemande);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p3, p4), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(7 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(8 * 60, result.getMAbsencesAS400());
		assertEquals(5 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(4 * 60, result.getMsNuit(), 0);
		assertEquals(1 * 60, result.getMsdjf(), 0);
		assertEquals(2 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test ABSENCES Nuit et DJF 39H
	 */
	@Test
	public void processHSupFonctionnaire_testExcel_ABSENCES_Nuit_DJF_39H() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 8, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(abs);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 11, 16, 8, 0, 0).toDate());
		p5.setDateFin(new DateTime(2013, 11, 16, 16, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 17, 8, 0, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 17, 16, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p2, p3, p4, p5, p6), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p2.getDateLundi(), result.getDateLundi());
		assertEquals(20 * 60, result.getMHorsContrat(), 0);
		assertEquals(8 * 60, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(14 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(2 * 60, result.getMsNuit(), 0);
		assertEquals(8 * 60, result.getMsdjf(), 0);
		assertEquals(6 * 60, result.getMNormales(), 0);
		assertEquals(3 * 60, result.getMSimple(), 0);
		assertEquals(1 * 60, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/***********************************************************
	 ***************** CONTRACTUELS ****************************
	 ***********************************************************/

	/**
	 * test 2333
	 */
	@Test
	public void processHSupContractuel_testExcel2333() {

		// Given
		Date dateLundi = new LocalDate(2013, 10, 28).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 1, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 1, 15, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 2, 8, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 2, 14, 30, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 3, 8, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 3, 11, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 1, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 1, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(16.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(16.5 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0 * 60, result.getMsNuit(), 0);
		assertEquals(10 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(6.5 * 60, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test 3876
	 */
	@Test
	public void processHSupContractuel_testExcel3876() {

		// Given
		Date dateLundi = new LocalDate(2013, 10, 28).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 1, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 1, 20, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 2, 8, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 2, 18, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 3, 8, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 3, 13, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 1, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 1, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(27 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(27 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0 * 60, result.getMsNuit(), 0);
		assertEquals(17 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(2 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test 4029
	 */
	@Test
	public void processHSupContractuel_testExcel4029() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 14, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 20, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(10 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(10 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0 * 60, result.getMsNuit(), 0);
		assertEquals(6 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(4 * 60, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test 3414
	 */
	@Test
	public void processHSupContractuel_testExcel3414() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 11, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 6, 30, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 7, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 12, 22, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 13, 6, 30, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 13, 7, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 11, 13, 16, 0, 0).toDate());
		p5.setDateFin(new DateTime(2013, 11, 13, 18, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 14, 6, 30, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 14, 7, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		Pointage p7 = new Pointage();
		p7.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p7.setDateDebut(new DateTime(2013, 11, 14, 16, 0, 0).toDate());
		p7.setDateFin(new DateTime(2013, 11, 14, 22, 0, 0).toDate());
		p7.setHeureSupRecuperee(false);
		p7.setType(hSup);

		Pointage p8 = new Pointage();
		p8.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p8.setDateDebut(new DateTime(2013, 11, 15, 6, 30, 0).toDate());
		p8.setDateFin(new DateTime(2013, 11, 15, 7, 0, 0).toDate());
		p8.setHeureSupRecuperee(false);
		p8.setType(hSup);

		Pointage p9 = new Pointage();
		p9.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p9.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p9.setDateFin(new DateTime(2013, 11, 15, 17, 0, 0).toDate());
		p9.setHeureSupRecuperee(false);
		p9.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9),
				new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(21 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(21 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit(), 0);
		assertEquals(3 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(10 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test BOUYGUES
	 */
	@Test
	public void processHSupContractuel_testExcelBOUYGUES() {

		// Given
		Date dateLundi = new LocalDate(2013, 12, 2).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 12, 2, 19, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 12, 2, 22, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 12, 3, 19, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 12, 3, 23, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 12, 4, 19, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 12, 4, 23, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 12, 5, 19, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 12, 5, 22, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 12, 7, 13, 30, 0).toDate());
		p5.setDateFin(new DateTime(2013, 12, 7, 18, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(18.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(18.5 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(2 * 60, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(8.5 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test PLEIN TEMPS Nuit et DJF
	 */
	@Test
	public void processHSupContractuel_testExcelPLEIN_TEMPS_NUIT_DJF() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 11, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p1_bis = new Pointage();
		p1_bis.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1_bis.setDateDebut(new DateTime(2013, 11, 11, 20, 0, 0).toDate());
		p1_bis.setDateFin(new DateTime(2013, 11, 11, 23, 0, 0).toDate());
		p1_bis.setHeureSupRecuperee(false);
		p1_bis.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 6, 30, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 7, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 12, 22, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 13, 6, 30, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 13, 7, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 11, 13, 16, 0, 0).toDate());
		p5.setDateFin(new DateTime(2013, 11, 13, 18, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 14, 6, 30, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 14, 7, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		Pointage p7 = new Pointage();
		p7.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p7.setDateDebut(new DateTime(2013, 11, 14, 16, 0, 0).toDate());
		p7.setDateFin(new DateTime(2013, 11, 14, 22, 0, 0).toDate());
		p7.setHeureSupRecuperee(false);
		p7.setType(hSup);

		Pointage p8 = new Pointage();
		p8.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p8.setDateDebut(new DateTime(2013, 11, 15, 6, 30, 0).toDate());
		p8.setDateFin(new DateTime(2013, 11, 15, 7, 0, 0).toDate());
		p8.setHeureSupRecuperee(false);
		p8.setType(hSup);

		Pointage p9 = new Pointage();
		p9.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p9.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p9.setDateFin(new DateTime(2013, 11, 15, 17, 0, 0).toDate());
		p9.setHeureSupRecuperee(false);
		p9.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 20, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p1_bis, p2, p3, p4, p5, p6, p7, p8, p9),
				new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(24 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(24 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(1 * 60, result.getMsNuit(), 0);
		assertEquals(5 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(10 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test MI-TEMPS 20H NUIT ET DJF
	 */
	@Test
	public void processHSupContractuel_testExcel_MI_TEMPS_NUIT_DJF() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 20, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 23, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(4.0);
		spbase.setHeureMardi(4.0);
		spbase.setHeureMercredi(4.0);
		spbase.setHeureJeudi(4.0);
		spbase.setHeureVendredi(4.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(20.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 20, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p6), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(9 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(5 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(3 * 60, result.getMsNuit(), 0);
		assertEquals(2 * 60, result.getMsdjf(), 0);
		assertEquals(4 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test PLEIN TEMPS CONGE ANNUEL
	 */
	@Test
	public void processHSupContractuel_testExcel_PLEIN_TEMPS_CONGE_ANNUEL() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(4 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(4 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(1 * 60, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(3 * 60, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test PLEIN TEMPS CONGE ANNUEL
	 */
	@Test
	public void processHSupContractuel_testExcel_PLEIN_TEMPS_CONGE_MATERNITE_DemiJournee() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		// les conges maternite
		List<DemandeDto> listConges = new ArrayList<DemandeDto>();
		DemandeDto conge = new DemandeDto();
		conge.setDateDebut(new DateTime(2013, 11, 12, 0, 0, 0).toDate());
		conge.setDateFin(new DateTime(2013, 11, 12, 11, 59, 59).toDate());
		conge.setIdTypeDemande(1);
		listConges.add(conge);

		List<RefTypeSaisiDto> listTypeDemande = new ArrayList<RefTypeSaisiDto>();
		RefTypeSaisiDto typeDemande1 = new RefTypeSaisiDto();
		typeDemande1.setUniteDecompte("jours");
		listTypeDemande.add(typeDemande1);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(listConges);
		Mockito.when(absWsConsumer.getTypeSaisiAbsence(conge.getIdTypeDemande())).thenReturn(listTypeDemande);
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(4 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(4 * 60, result.getMAbsencesAS400());
		assertEquals(1 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(1 * 60, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(3 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test PLEIN TEMPS CONGE ANNUEL
	 */
	@Test
	public void processHSupContractuel_testExcel_PLEIN_TEMPS_CONGE_MATERNITE_2Jours() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 12, 5, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 12, 8, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 15, 20, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 15, 23, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		// les conges maternite
		List<DemandeDto> listConges = new ArrayList<DemandeDto>();
		DemandeDto conge = new DemandeDto();
		conge.setDateDebut(new DateTime(2013, 11, 12, 12, 0, 0).toDate());
		conge.setDateFin(new DateTime(2013, 11, 14, 11, 59, 59).toDate());
		conge.setIdTypeDemande(1);
		listConges.add(conge);

		List<RefTypeSaisiDto> listTypeDemande = new ArrayList<RefTypeSaisiDto>();
		RefTypeSaisiDto typeDemande1 = new RefTypeSaisiDto();
		typeDemande1.setUniteDecompte("jours");
		listTypeDemande.add(typeDemande1);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(listConges);
		Mockito.when(absWsConsumer.getTypeSaisiAbsence(conge.getIdTypeDemande())).thenReturn(listTypeDemande);
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p3, p1, p2, p4), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(10 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(16 * 60, result.getMAbsencesAS400());
		assertEquals(2 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(2 * 60, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(8 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test PLEIN TEMPS CONGE ANNUEL
	 */
	@Test
	public void processHSupContractuel_testExcel_PLEIN_TEMPS_CONGE_Formation_2Heures() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 12, 5, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 12, 8, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 15, 20, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 15, 23, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		// les conges maternite
		List<DemandeDto> listConges = new ArrayList<DemandeDto>();
		DemandeDto conge = new DemandeDto();
		conge.setDateDebut(new DateTime(2013, 11, 12, 10, 0, 0).toDate());
		conge.setDateFin(new DateTime(2013, 11, 12, 12, 0, 0).toDate());
		conge.setIdTypeDemande(1);
		conge.setDuree(120.0);
		listConges.add(conge);

		List<RefTypeSaisiDto> listTypeDemande = new ArrayList<RefTypeSaisiDto>();
		RefTypeSaisiDto typeDemande1 = new RefTypeSaisiDto();
		typeDemande1.setUniteDecompte("minutes");
		listTypeDemande.add(typeDemande1);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(listConges);
		Mockito.when(absWsConsumer.getTypeSaisiAbsence(conge.getIdTypeDemande())).thenReturn(listTypeDemande);
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p3, p1, p2, p4), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(10 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(2 * 60, result.getMAbsencesAS400());
		assertEquals(8 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(2 * 60, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(2 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(6 * 60, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test MALADIE Nuit et DJF 39H
	 */
	@Test
	public void processHSupContractuel_testExcel_MALADIE_Nuit_DJF_39H() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		DemandeDto absence = new DemandeDto();
		absence.setDateDebut(new DateTime(2013, 11, 12, 0, 0, 0).toDate());
		absence.setDateFin(new DateTime(2013, 11, 12, 23, 59, 59).toDate());

		List<DemandeDto> listAbsence = new ArrayList<DemandeDto>();
		listAbsence.add(absence);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureLundi()))
				.thenReturn(new Double(spbase.getHeureLundi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureMardi()))
				.thenReturn(new Double(spbase.getHeureMardi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureMercredi()))
				.thenReturn(new Double(spbase.getHeureMercredi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureJeudi()))
				.thenReturn(new Double(spbase.getHeureJeudi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureVendredi()))
				.thenReturn(new Double(spbase.getHeureVendredi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getBaseCalculee()))
				.thenReturn(new Double(spbase.getBaseCalculee()).intValue() * 60);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 20, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		List<RefTypeSaisiDto> listTypeDemande = new ArrayList<RefTypeSaisiDto>();
		RefTypeSaisiDto typeDemande1 = new RefTypeSaisiDto();
		typeDemande1.setUniteDecompte("jours");
		listTypeDemande.add(typeDemande1);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(listAbsence);
		Mockito.when(absWsConsumer.getTypeSaisiAbsence(absence.getIdTypeDemande())).thenReturn(listTypeDemande);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p3, p4), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(7 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(8 * 60, result.getMAbsencesAS400());
		assertEquals(4 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(2 * 60, result.getMsNuit(), 0);
		assertEquals(2 * 60, result.getMsdjf(), 0);
		assertEquals(3 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test ABSENCES Nuit et DJF 39H
	 */
	@Test
	public void processHSupContractuel_testExcel_ABSENCES_Nuit_DJF_39H() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 8, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(abs);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 11, 16, 8, 0, 0).toDate());
		p5.setDateFin(new DateTime(2013, 11, 16, 16, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 17, 8, 0, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 17, 16, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isJourFerie(new DateTime(2013, 11, 11, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p2, p3, p4, p5, p6), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p2.getDateLundi(), result.getDateLundi());
		assertEquals(20 * 60, result.getMHorsContrat(), 0);
		assertEquals(8 * 60, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(13 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(1 * 60, result.getMsNuit(), 0);
		assertEquals(8 * 60, result.getMsdjf(), 0);
		assertEquals(7 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(4 * 60, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/***********************************************************
	 ***************** CONVENTION COLLECTIVE *******************
	 ***********************************************************/

	/**
	 * test 2333
	 */
	@Test
	public void processHSupConventionCollective_testExcel2333() {

		// Given
		Date dateLundi = new LocalDate(2013, 10, 28).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 1, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 1, 15, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 2, 8, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 2, 14, 30, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 3, 8, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 3, 11, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 1, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 1, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 2, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 2, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3), false, new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(16.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(16.5 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0 * 60, result.getMsNuit(), 0);
		assertEquals(16.5 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(8.5 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test 3876
	 */
	@Test
	public void processHSupConventionCollective_testExcel3876() {

		// Given
		Date dateLundi = new LocalDate(2013, 10, 28).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 1, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 1, 20, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 2, 8, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 2, 18, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 10, 28, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 3, 8, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 3, 13, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 1, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 1, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 2, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 2, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3), false, new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(27 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(27 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0 * 60, result.getMsNuit(), 0);
		assertEquals(27 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(19 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test 4029
	 */
	@Test
	public void processHSupConventionCollective_testExcel4029() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 14, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 20, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 11, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), false, new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(10 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(10 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0 * 60, result.getMsNuit(), 0);
		assertEquals(6 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(2 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test 3414
	 */
	@Test
	public void processHSupConventionCollective_testExcel3414() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 11, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 6, 30, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 7, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 12, 22, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 13, 6, 30, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 13, 7, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 11, 13, 16, 0, 0).toDate());
		p5.setDateFin(new DateTime(2013, 11, 13, 18, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 14, 6, 30, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 14, 7, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		Pointage p7 = new Pointage();
		p7.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p7.setDateDebut(new DateTime(2013, 11, 14, 16, 0, 0).toDate());
		p7.setDateFin(new DateTime(2013, 11, 14, 22, 0, 0).toDate());
		p7.setHeureSupRecuperee(false);
		p7.setType(hSup);

		Pointage p8 = new Pointage();
		p8.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p8.setDateDebut(new DateTime(2013, 11, 15, 6, 30, 0).toDate());
		p8.setDateFin(new DateTime(2013, 11, 15, 7, 0, 0).toDate());
		p8.setHeureSupRecuperee(false);
		p8.setType(hSup);

		Pointage p9 = new Pointage();
		p9.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p9.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p9.setDateFin(new DateTime(2013, 11, 15, 17, 0, 0).toDate());
		p9.setHeureSupRecuperee(false);
		p9.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 11, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9),
				false, new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(21 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(21 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(4 * 60, result.getMsNuit(), 0);
		assertEquals(3 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(13 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test BOUYGUES
	 */
	@Test
	public void processHSupConventionCollective_testExcelBOUYGUES() {

		// Given
		Date dateLundi = new LocalDate(2013, 12, 2).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 12, 2, 19, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 12, 2, 22, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 12, 3, 19, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 12, 3, 23, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 12, 4, 19, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 12, 4, 23, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 12, 5, 19, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 12, 5, 22, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 12, 2, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 12, 7, 13, 30, 0).toDate());
		p5.setDateFin(new DateTime(2013, 12, 7, 18, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5), false,
				new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(18.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(18.5 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(10 * 60, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(10.5 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test PLEIN TEMPS Nuit et DJF
	 */
	@Test
	public void processHSupConventionCollective_testExcelPLEIN_TEMPS_NUIT_DJF() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 11, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p1_bis = new Pointage();
		p1_bis.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1_bis.setDateDebut(new DateTime(2013, 11, 11, 20, 0, 0).toDate());
		p1_bis.setDateFin(new DateTime(2013, 11, 11, 23, 0, 0).toDate());
		p1_bis.setHeureSupRecuperee(false);
		p1_bis.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 6, 30, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 7, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 12, 22, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 13, 6, 30, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 13, 7, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 11, 13, 16, 0, 0).toDate());
		p5.setDateFin(new DateTime(2013, 11, 13, 18, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 14, 6, 30, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 14, 7, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		Pointage p7 = new Pointage();
		p7.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p7.setDateDebut(new DateTime(2013, 11, 14, 16, 0, 0).toDate());
		p7.setDateFin(new DateTime(2013, 11, 14, 22, 0, 0).toDate());
		p7.setHeureSupRecuperee(false);
		p7.setType(hSup);

		Pointage p8 = new Pointage();
		p8.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p8.setDateDebut(new DateTime(2013, 11, 15, 6, 30, 0).toDate());
		p8.setDateFin(new DateTime(2013, 11, 15, 7, 0, 0).toDate());
		p8.setHeureSupRecuperee(false);
		p8.setType(hSup);

		Pointage p9 = new Pointage();
		p9.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p9.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p9.setDateFin(new DateTime(2013, 11, 15, 17, 0, 0).toDate());
		p9.setHeureSupRecuperee(false);
		p9.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 11, 8, 0, 0))).thenReturn(true);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 11, 20, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi,
				Arrays.asList(p1, p1_bis, p2, p3, p4, p5, p6, p7, p8, p9), false, new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(24 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(24 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(7 * 60, result.getMsNuit(), 0);
		assertEquals(6 * 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(16 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test MI-TEMPS 20H NUIT ET DJF
	 */
	@Test
	public void processHSupConventionCollective_testExcel_MI_TEMPS_NUIT_DJF() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 20, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 23, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(4.0);
		spbase.setHeureMardi(4.0);
		spbase.setHeureMercredi(4.0);
		spbase.setHeureJeudi(4.0);
		spbase.setHeureVendredi(4.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(20.0);

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 11, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 11, 20, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p6), false, new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(9 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(0 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(9 * 60, result.getMsNuit(), 0);
		assertEquals(3 * 60, result.getMsdjf(), 0);
		assertEquals(9 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test PLEIN TEMPS CONGE ANNUEL
	 */
	@Test
	public void processHSupConventionCollective_testExcel_PLEIN_TEMPS_CONGE_ANNUEL() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), false, new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(4 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(4 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(3 * 60, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(4 * 60, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test PLEIN TEMPS CONGE MATERNITE
	 */
	@Test
	public void processHSupConventionCollective_testExcel_PLEIN_TEMPS_CONGE_MATERNITE() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 15, 20, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 16, 8, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 16, 20, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		List<DemandeDto> listConges = new ArrayList<DemandeDto>();
		DemandeDto conge = new DemandeDto();
		conge.setDateDebut(new DateTime(2013, 11, 12, 0, 0, 0).toDate());
		conge.setDateFin(new DateTime(2013, 11, 13, 11, 59, 59).toDate());
		conge.setIdTypeDemande(1);
		listConges.add(conge);

		DemandeDto conge2 = new DemandeDto();
		conge2.setDateDebut(new DateTime(2013, 11, 14, 12, 0, 0).toDate());
		conge2.setDateFin(new DateTime(2013, 11, 14, 23, 59, 59).toDate());
		conge2.setIdTypeDemande(2);
		listConges.add(conge2);

		List<RefTypeSaisiDto> listTypeDemande = new ArrayList<RefTypeSaisiDto>();
		RefTypeSaisiDto typeDemande1 = new RefTypeSaisiDto();
		typeDemande1.setUniteDecompte("jours");
		listTypeDemande.add(typeDemande1);
		List<RefTypeSaisiDto> listTypeDemande2 = new ArrayList<RefTypeSaisiDto>();
		RefTypeSaisiDto typeDemande2 = new RefTypeSaisiDto();
		typeDemande2.setUniteDecompte("jours");
		listTypeDemande2.add(typeDemande2);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(listConges);
		Mockito.when(absWsConsumer.getTypeSaisiAbsence(conge.getIdTypeDemande())).thenReturn(listTypeDemande);
		Mockito.when(absWsConsumer.getTypeSaisiAbsence(conge2.getIdTypeDemande())).thenReturn(listTypeDemande2);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3), false, new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(20 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(16 * 60, result.getMAbsencesAS400());
		assertEquals(4 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(3 * 60, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(16 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(4 * 60, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test MALADIE Nuit et DJF 39H
	 */
	@Test
	public void processHSupConventionCollective_testExcel_MALADIE_Nuit_DJF_39H() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2013, 11, 11, 20, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 11, 11, 23, 0, 0).toDate());
		p1.setHeureSupRecuperee(false);
		p1.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		DemandeDto absence = new DemandeDto();
		absence.setDateDebut(new DateTime(2013, 11, 12, 0, 0, 0).toDate());
		absence.setDateFin(new DateTime(2013, 11, 12, 23, 59, 59).toDate());

		List<DemandeDto> listAbsence = new ArrayList<DemandeDto>();
		listAbsence.add(absence);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureLundi()))
				.thenReturn(new Double(spbase.getHeureLundi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureMardi()))
				.thenReturn(new Double(spbase.getHeureMardi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureMercredi()))
				.thenReturn(new Double(spbase.getHeureMercredi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureJeudi()))
				.thenReturn(new Double(spbase.getHeureJeudi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getHeureVendredi()))
				.thenReturn(new Double(spbase.getHeureVendredi()).intValue() * 60);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getBaseCalculee()))
				.thenReturn(new Double(spbase.getBaseCalculee()).intValue() * 60);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 11, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 11, 20, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		List<RefTypeSaisiDto> listTypeDemande = new ArrayList<RefTypeSaisiDto>();
		RefTypeSaisiDto typeDemande1 = new RefTypeSaisiDto();
		typeDemande1.setUniteDecompte("jours");
		listTypeDemande.add(typeDemande1);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(listAbsence);
		Mockito.when(absWsConsumer.getTypeSaisiAbsence(absence.getIdTypeDemande())).thenReturn(listTypeDemande);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p3, p4), false, new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(7 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(8 * 60, result.getMAbsencesAS400());
		assertEquals(0 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(6 * 60, result.getMsNuit(), 0);
		assertEquals(3 * 60, result.getMsdjf(), 0);
		assertEquals(7 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test ABSENCES Nuit et DJF 39H
	 */
	@Test
	public void processHSupConventionCollective_testExcel_ABSENCES_Nuit_DJF_39H() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 8, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(abs);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 11, 16, 8, 0, 0).toDate());
		p5.setDateFin(new DateTime(2013, 11, 16, 16, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 17, 8, 0, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 17, 16, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 11, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p2, p3, p4, p5, p6), false,
				new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p2.getDateLundi(), result.getDateLundi());
		assertEquals(20 * 60, result.getMHorsContrat(), 0);
		assertEquals(8 * 60, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(12 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(3 * 60, result.getMsNuit(), 0);
		assertEquals(8 * 60, result.getMsdjf(), 0);
		assertEquals(8 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(4 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test ABSENCES Nuit et DJF 39H
	 */
	@Test
	public void processHSupConventionCollective_testExcel_39H() {

		// Given
		Date dateLundi = new LocalDate(2013, 11, 11).toDate();

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 11, 12, 8, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 11, 12, 16, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(abs);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 11, 14, 20, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 11, 14, 23, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2013, 11, 15, 15, 0, 0).toDate());
		p4.setDateFin(new DateTime(2013, 11, 15, 16, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2013, 11, 16, 8, 0, 0).toDate());
		p5.setDateFin(new DateTime(2013, 11, 16, 16, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 11, 11, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 11, 17, 8, 0, 0).toDate());
		p6.setDateFin(new DateTime(2013, 11, 17, 16, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.isHoliday(new DateTime(2013, 11, 11, 0, 0, 0))).thenReturn(true);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p2, p3, p4, p5, p6), false,
				new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p2.getDateLundi(), result.getDateLundi());
		assertEquals(20 * 60, result.getMHorsContrat(), 0);
		assertEquals(8 * 60, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(12 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(3 * 60, result.getMsNuit(), 0);
		assertEquals(8 * 60, result.getMsdjf(), 0);
		assertEquals(8 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(4 * 60, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Cas tests ressorti de la recette base sur un pointage reel
	 */
	@Test
	public void processHSupConventionCollective_testDeLaRecette_contrat38H45() {

		// Given
		Date dateLundi = new LocalDate(2014, 5, 26).toDate();

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2014, 5, 26, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2014, 5, 26, 16, 30, 0).toDate());
		p2.setDateFin(new DateTime(2014, 5, 26, 21, 0, 0).toDate());
		p2.setHeureSupRecuperee(true);
		p2.setHeureSupRappelService(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2014, 5, 26, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2014, 5, 27, 16, 30, 0).toDate());
		p3.setDateFin(new DateTime(2014, 5, 27, 21, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setHeureSupRappelService(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2014, 5, 26, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2014, 5, 28, 16, 30, 0).toDate());
		p4.setDateFin(new DateTime(2014, 5, 28, 21, 0, 0).toDate());
		p4.setHeureSupRecuperee(true);
		p4.setHeureSupRappelService(false);
		p4.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(7.45);
		spbase.setHeureMardi(7.45);
		spbase.setHeureMercredi(7.45);
		spbase.setHeureJeudi(7.45);
		spbase.setHeureVendredi(7.45);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(38.45);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p2, p3, p4), false, new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p2.getDateLundi(), result.getDateLundi());
		assertEquals(13.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(13.25 * 60, result.getMSup(), 0);
		assertEquals(9 * 60, result.getMRecuperees(), 0);
		assertEquals(0, result.getMRappelService());

		assertEquals(3 * 60, result.getMsNuit(), 0);
		assertEquals(2 * 60, result.getMsNuitRecup(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(0, result.getMsdjfRecup(), 0);
		assertEquals(0.25 * 60, result.getMNormales(), 0);
		assertEquals(0.25 * 60, result.getMNormalesRecup(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMSimpleRecup(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(0, result.getMComposeesRecup(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(8 * 60, result.getMSup25Recup(), 0);
		assertEquals(5.25 * 60, result.getMSup50(), 0);
		assertEquals(0.75 * 60, result.getMSup50Recup(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Cas tests ressorti de la recette base sur un pointage reel
	 */
	@Test
	public void processHSupContractuel_testDeLaRecette_contrat38H45() {

		// Given
		Date dateLundi = new LocalDate(2014, 5, 12).toDate();

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2014, 5, 12, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2014, 5, 12, 17, 15, 0).toDate());
		p2.setDateFin(new DateTime(2014, 5, 12, 19, 30, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2014, 5, 12, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2014, 5, 13, 17, 15, 0).toDate());
		p3.setDateFin(new DateTime(2014, 5, 13, 19, 30, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(7.45);
		spbase.setHeureMardi(7.45);
		spbase.setHeureMercredi(7.45);
		spbase.setHeureJeudi(7.45);
		spbase.setHeureVendredi(7.45);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(38.45);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p2, p3), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p2.getDateLundi(), result.getDateLundi());
		assertEquals(4.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(4.25 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(0.25 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(4.25 * 60, result.getMSup25(), 0);
		assertEquals(0, result.getMSup50(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Cas tests ressorti de la recette base sur un pointage reel
	 */
	@Test
	public void processHSupFonctionnaire_testDeLaRecette_contrat38H45() {

		// Given
		Date dateLundi = new LocalDate(2014, 5, 26).toDate();

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2014, 5, 26, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2014, 5, 26, 15, 30, 0).toDate());
		p2.setDateFin(new DateTime(2014, 5, 26, 20, 0, 0).toDate());
		p2.setHeureSupRecuperee(true);
		p2.setHeureSupRappelService(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2014, 5, 26, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2014, 5, 27, 15, 30, 0).toDate());
		p3.setDateFin(new DateTime(2014, 5, 27, 20, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setHeureSupRappelService(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2014, 5, 26, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2014, 5, 28, 15, 30, 0).toDate());
		p4.setDateFin(new DateTime(2014, 5, 28, 20, 0, 0).toDate());
		p4.setHeureSupRecuperee(true);
		p4.setHeureSupRappelService(false);
		p4.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(7.45);
		spbase.setHeureMardi(7.45);
		spbase.setHeureMercredi(7.45);
		spbase.setHeureJeudi(7.45);
		spbase.setHeureVendredi(7.45);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(38.45);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p2, p3, p4), new VentilDate());
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p2.getDateLundi(), result.getDateLundi());
		assertEquals(13.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(13.25 * 60, result.getMSup(), 0);
		assertEquals(9 * 60, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0, result.getMsNuit(), 0);
		assertEquals(0, result.getMsdjf(), 0);
		assertEquals(0.25 * 60, result.getMNormales(), 0);
		assertEquals(0.25 * 60, result.getMNormalesRecup(), 0);
		assertEquals(3 * 60, result.getMSimple(), 0);
		assertEquals(3 * 60, result.getMSimpleRecup(), 0);
		assertEquals(10.25 * 60, result.getMComposees(), 0);
		assertEquals(5.75 * 60, result.getMComposeesRecup(), 0);
		assertEquals(0, result.getMSup25(), 0);
		assertEquals(0, result.getMSup25Recup(), 0);
		assertEquals(0, result.getMSup50(), 0);
		assertEquals(0, result.getMSup50Recup(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test ABSENCES Nuit et DJF 39H
	 */
	@Test
	public void processHSupConventionCollective_testExcel_39H_1Mai() {

		// Given
		Date dateLundi = new LocalDate(2015, 4, 27).toDate();

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2015, 4, 27, 16, 30, 0).toDate());
		p2.setDateFin(new DateTime(2015, 4, 27, 21, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2015, 4, 28, 16, 30, 0).toDate());
		p3.setDateFin(new DateTime(2015, 4, 28, 21, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2015, 4, 29, 16, 30, 0).toDate());
		p4.setDateFin(new DateTime(2015, 4, 29, 21, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2015, 5, 1, 2, 0, 0).toDate());
		p5.setDateFin(new DateTime(2015, 5, 1, 11, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2015, 5, 1, 20, 0, 0).toDate());
		p6.setDateFin(new DateTime(2015, 5, 1, 22, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p2, p3, p4, p5, p6), false,
				new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(dateLundi, result.getDateLundi());
		assertEquals(24.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(24.5 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(7 * 60, result.getMsNuit(), 0);
		assertEquals(0 * 60, result.getMsdjf(), 0);
		assertEquals(0 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(16.5 * 60, result.getMSup50(), 0);
		assertEquals(11 * 60, result.getMMai(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test ABSENCES Nuit et DJF 39H
	 */
	@Test
	public void processHSupContractuel_testExcel_39H_1Mai() {

		// Given
		Date dateLundi = new LocalDate(2015, 4, 27).toDate();

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2015, 4, 27, 16, 30, 0).toDate());
		p2.setDateFin(new DateTime(2015, 4, 27, 21, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2015, 4, 28, 16, 30, 0).toDate());
		p3.setDateFin(new DateTime(2015, 4, 28, 21, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2015, 4, 29, 16, 30, 0).toDate());
		p4.setDateFin(new DateTime(2015, 4, 29, 21, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2015, 5, 1, 2, 0, 0).toDate());
		p5.setDateFin(new DateTime(2015, 5, 1, 11, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2015, 5, 1, 20, 0, 0).toDate());
		p6.setDateFin(new DateTime(2015, 5, 1, 22, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);
		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p2, p3, p4, p5, p6), new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p2.getDateLundi(), result.getDateLundi());
		assertEquals(24.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(24.5 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0 * 60, result.getMsNuit(), 0);
		assertEquals(0 * 60, result.getMsdjf(), 0);
		assertEquals(0 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(5.5 * 60, result.getMSup50(), 0);
		assertEquals(11 * 60, result.getMMai(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	/**
	 * Test ABSENCES Nuit et DJF 39H
	 */
	@Test
	public void processHSupFonctionnaire_testExcel_38H45_1Mai() {

		// Given
		Date dateLundi = new LocalDate(2015, 4, 27).toDate();

		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2015, 4, 27, 16, 30, 0).toDate());
		p2.setDateFin(new DateTime(2015, 4, 27, 21, 0, 0).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setType(hSup);

		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2015, 4, 28, 16, 30, 0).toDate());
		p3.setDateFin(new DateTime(2015, 4, 28, 21, 0, 0).toDate());
		p3.setHeureSupRecuperee(false);
		p3.setType(hSup);

		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2015, 4, 29, 16, 30, 0).toDate());
		p4.setDateFin(new DateTime(2015, 4, 29, 21, 0, 0).toDate());
		p4.setHeureSupRecuperee(false);
		p4.setType(hSup);

		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2015, 5, 1, 2, 0, 0).toDate());
		p5.setDateFin(new DateTime(2015, 5, 1, 11, 0, 0).toDate());
		p5.setHeureSupRecuperee(false);
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2015, 5, 1, 20, 0, 0).toDate());
		p6.setDateFin(new DateTime(2015, 5, 1, 22, 0, 0).toDate());
		p6.setHeureSupRecuperee(false);
		p6.setType(hSup);

		Pointage p7 = new Pointage();
		p7.setDateLundi(new DateTime(2015, 4, 27, 0, 0, 0).toDate());
		p7.setDateDebut(new DateTime(2015, 5, 3, 8, 0, 0).toDate());
		p7.setDateFin(new DateTime(2015, 5, 3, 12, 0, 0).toDate());
		p7.setHeureSupRecuperee(false);
		p7.setType(hSup);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(7.45);
		spbase.setHeureMardi(7.45);
		spbase.setHeureMercredi(7.45);
		spbase.setHeureJeudi(7.45);
		spbase.setHeureVendredi(7.45);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(38.45);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.getListCongesExeptionnelsEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());
		Mockito.when(absWsConsumer.getListMaladiesEtatPrisBetween(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(new ArrayList<DemandeDto>());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, new Spcarr(), dateLundi, Arrays.asList(p2, p3, p4, p5, p6, p7),
				new VentilDate());

		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p2.getDateLundi(), result.getDateLundi());
		assertEquals(28.5 * 60, result.getMHorsContrat(), 0);
		assertEquals(0, result.getMAbsences(), 0);
		assertEquals(0, result.getMAbsencesAS400());
		assertEquals(28.25 * 60, result.getMSup(), 0);
		assertEquals(0, result.getMRecuperees());
		assertEquals(0, result.getMRappelService());

		assertEquals(0 * 60, result.getMsNuit(), 0);
		assertEquals(4 * 60, result.getMsdjf(), 0);
		assertEquals(0.25 * 60, result.getMNormales(), 0);
		assertEquals(3 * 60, result.getMSimple(), 0);
		assertEquals(10.25 * 60, result.getMComposees(), 0);
		assertEquals(0 * 60, result.getMSup25(), 0);
		assertEquals(0 * 60, result.getMSup50(), 0);
		assertEquals(11 * 60, result.getMMai(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}

	@Test
	public void processHeuresSupEpandageForSIPRES_Fonctionnaire_2HSimple() {

		RefPrime prime = new RefPrime();
		prime.setNoRubr(7716);

		Date dateLundi = new DateTime(2015, 2, 9, 0, 0, 0).toDate();

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		Pointage ptg = new Pointage();
		ptg.setRefPrime(prime);
		ptg.setQuantite(120);
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 2, 9, 5, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 2, 9, 7, 0, 0).toDate());
		ptg.setType(type);

		VentilHsup ventilHsup = new VentilHsup();
		Integer idAgent = 9005138;
		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.add(ptg);

		VentilationHSupService service = new VentilationHSupService();
		ventilHsup = service.processHeuresSupEpandageForSIPRES(ventilHsup, idAgent, dateLundi, pointages, AgentStatutEnum.F);

		assertEquals(ventilHsup.getMHorsContrat(), 2 * 60);
		assertEquals(ventilHsup.getMSimple(), 2 * 60);
		assertEquals(ventilHsup.getMComposees(), 0);
		assertEquals(ventilHsup.getMNormales(), 0);
		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);
	}

	@Test
	public void processHeuresSupEpandageForSIPRES_Fonctionnaire_8HSimple_6HComposees() {

		RefPrime prime = new RefPrime();
		prime.setNoRubr(7716);

		Date dateLundi = new DateTime(2015, 2, 9, 0, 0, 0).toDate();

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		Pointage ptg = new Pointage();
		ptg.setRefPrime(prime);
		ptg.setQuantite(120);
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 2, 9, 5, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 2, 9, 7, 0, 0).toDate());
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setRefPrime(prime);
		ptg2.setQuantite(120);
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 2, 10, 5, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 2, 10, 7, 0, 0).toDate());
		ptg2.setType(type);

		Pointage ptg3 = new Pointage();
		ptg3.setRefPrime(prime);
		ptg3.setQuantite(120);
		ptg3.setDateLundi(dateLundi);
		ptg3.setDateDebut(new DateTime(2015, 2, 11, 5, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 2, 11, 7, 0, 0).toDate());
		ptg3.setType(type);

		Pointage ptg4 = new Pointage();
		ptg4.setRefPrime(prime);
		ptg4.setQuantite(120);
		ptg4.setDateLundi(dateLundi);
		ptg4.setDateDebut(new DateTime(2015, 2, 12, 5, 0, 0).toDate());
		ptg4.setDateFin(new DateTime(2015, 2, 12, 7, 0, 0).toDate());
		ptg4.setType(type);

		Pointage ptg5 = new Pointage();
		ptg5.setRefPrime(prime);
		ptg5.setQuantite(120);
		ptg5.setDateLundi(dateLundi);
		ptg5.setDateDebut(new DateTime(2015, 2, 13, 5, 0, 0).toDate());
		ptg5.setDateFin(new DateTime(2015, 2, 13, 7, 0, 0).toDate());
		ptg5.setType(type);

		Pointage ptg6 = new Pointage();
		ptg6.setRefPrime(prime);
		ptg6.setQuantite(120);
		ptg6.setDateLundi(dateLundi);
		ptg6.setDateDebut(new DateTime(2015, 2, 14, 5, 0, 0).toDate());
		ptg6.setDateFin(new DateTime(2015, 2, 14, 7, 0, 0).toDate());
		ptg6.setType(type);

		Pointage ptg7 = new Pointage();
		ptg7.setRefPrime(prime);
		ptg7.setQuantite(120);
		ptg7.setDateLundi(dateLundi);
		ptg7.setDateDebut(new DateTime(2015, 2, 15, 5, 0, 0).toDate());
		ptg7.setDateFin(new DateTime(2015, 2, 15, 7, 0, 0).toDate());
		ptg7.setType(type);

		VentilHsup ventilHsup = new VentilHsup();
		Integer idAgent = 9005138;
		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2, ptg3, ptg4, ptg5, ptg6, ptg7));

		VentilationHSupService service = new VentilationHSupService();
		ventilHsup = service.processHeuresSupEpandageForSIPRES(ventilHsup, idAgent, dateLundi, pointages, AgentStatutEnum.F);

		assertEquals(ventilHsup.getMHorsContrat(), 14 * 60);
		assertEquals(ventilHsup.getMSimple(), 8 * 60);
		assertEquals(ventilHsup.getMComposees(), 6 * 60);
		assertEquals(ventilHsup.getMNormales(), 0);
		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);
	}

	@Test
	public void processHeuresSupEpandageForSIPRES_Fonctionnaire_8HSimple_6HComposees_HeuresSuppDejaPresentes() {

		RefPrime prime = new RefPrime();
		prime.setNoRubr(7716);

		Date dateLundi = new DateTime(2015, 2, 9, 0, 0, 0).toDate();

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		Pointage ptg = new Pointage();
		ptg.setRefPrime(prime);
		ptg.setQuantite(120);
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 2, 9, 5, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 2, 9, 7, 0, 0).toDate());
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setRefPrime(prime);
		ptg2.setQuantite(120);
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 2, 10, 5, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 2, 10, 7, 0, 0).toDate());
		ptg2.setType(type);

		Pointage ptg3 = new Pointage();
		ptg3.setRefPrime(prime);
		ptg3.setQuantite(120);
		ptg3.setDateLundi(dateLundi);
		ptg3.setDateDebut(new DateTime(2015, 2, 11, 5, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 2, 11, 7, 0, 0).toDate());
		ptg3.setType(type);

		Pointage ptg4 = new Pointage();
		ptg4.setRefPrime(prime);
		ptg4.setQuantite(120);
		ptg4.setDateLundi(dateLundi);
		ptg4.setDateDebut(new DateTime(2015, 2, 12, 5, 0, 0).toDate());
		ptg4.setDateFin(new DateTime(2015, 2, 12, 7, 0, 0).toDate());
		ptg4.setType(type);

		Pointage ptg5 = new Pointage();
		ptg5.setRefPrime(prime);
		ptg5.setQuantite(120);
		ptg5.setDateLundi(dateLundi);
		ptg5.setDateDebut(new DateTime(2015, 2, 13, 5, 0, 0).toDate());
		ptg5.setDateFin(new DateTime(2015, 2, 13, 7, 0, 0).toDate());
		ptg5.setType(type);

		Pointage ptg6 = new Pointage();
		ptg6.setRefPrime(prime);
		ptg6.setQuantite(120);
		ptg6.setDateLundi(dateLundi);
		ptg6.setDateDebut(new DateTime(2015, 2, 14, 5, 0, 0).toDate());
		ptg6.setDateFin(new DateTime(2015, 2, 14, 7, 0, 0).toDate());
		ptg6.setType(type);

		Pointage ptg7 = new Pointage();
		ptg7.setRefPrime(prime);
		ptg7.setQuantite(120);
		ptg7.setDateLundi(dateLundi);
		ptg7.setDateDebut(new DateTime(2015, 2, 15, 5, 0, 0).toDate());
		ptg7.setDateFin(new DateTime(2015, 2, 15, 7, 0, 0).toDate());
		ptg7.setType(type);

		VentilHsup ventilHsup = new VentilHsup();
		ventilHsup.setMNormales(1 * 60);
		ventilHsup.setMSimple(3 * 60);
		ventilHsup.setMComposees(2 * 60);
		ventilHsup.setMHorsContrat(5 * 60);

		Integer idAgent = 9005138;
		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2, ptg3, ptg4, ptg5, ptg6, ptg7));

		VentilationHSupService service = new VentilationHSupService();
		ventilHsup = service.processHeuresSupEpandageForSIPRES(ventilHsup, idAgent, dateLundi, pointages, AgentStatutEnum.F);

		assertEquals(ventilHsup.getMHorsContrat(), 19 * 60);
		assertEquals(ventilHsup.getMSimple(), 11 * 60);
		assertEquals(ventilHsup.getMComposees(), 8 * 60);
		assertEquals(ventilHsup.getMNormales(), 1 * 60);
		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);
	}

	@Test
	public void processHeuresSupEpandageForSIPRES_Contractuel_8HSimple_6HComposees_HeuresSuppDejaPresentes() {

		RefPrime prime = new RefPrime();
		prime.setNoRubr(7716);

		Date dateLundi = new DateTime(2015, 2, 9, 0, 0, 0).toDate();

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		Pointage ptg = new Pointage();
		ptg.setRefPrime(prime);
		ptg.setQuantite(120);
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 2, 9, 5, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 2, 9, 7, 0, 0).toDate());
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setRefPrime(prime);
		ptg2.setQuantite(120);
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 2, 10, 5, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 2, 10, 7, 0, 0).toDate());
		ptg2.setType(type);

		Pointage ptg3 = new Pointage();
		ptg3.setRefPrime(prime);
		ptg3.setQuantite(120);
		ptg3.setDateLundi(dateLundi);
		ptg3.setDateDebut(new DateTime(2015, 2, 11, 5, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 2, 11, 7, 0, 0).toDate());
		ptg3.setType(type);

		Pointage ptg4 = new Pointage();
		ptg4.setRefPrime(prime);
		ptg4.setQuantite(120);
		ptg4.setDateLundi(dateLundi);
		ptg4.setDateDebut(new DateTime(2015, 2, 12, 5, 0, 0).toDate());
		ptg4.setDateFin(new DateTime(2015, 2, 12, 7, 0, 0).toDate());
		ptg4.setType(type);

		Pointage ptg5 = new Pointage();
		ptg5.setRefPrime(prime);
		ptg5.setQuantite(120);
		ptg5.setDateLundi(dateLundi);
		ptg5.setDateDebut(new DateTime(2015, 2, 13, 5, 0, 0).toDate());
		ptg5.setDateFin(new DateTime(2015, 2, 13, 7, 0, 0).toDate());
		ptg5.setType(type);

		Pointage ptg6 = new Pointage();
		ptg6.setRefPrime(prime);
		ptg6.setQuantite(120);
		ptg6.setDateLundi(dateLundi);
		ptg6.setDateDebut(new DateTime(2015, 2, 14, 5, 0, 0).toDate());
		ptg6.setDateFin(new DateTime(2015, 2, 14, 7, 0, 0).toDate());
		ptg6.setType(type);

		Pointage ptg7 = new Pointage();
		ptg7.setRefPrime(prime);
		ptg7.setQuantite(120);
		ptg7.setDateLundi(dateLundi);
		ptg7.setDateDebut(new DateTime(2015, 2, 15, 5, 0, 0).toDate());
		ptg7.setDateFin(new DateTime(2015, 2, 15, 7, 0, 0).toDate());
		ptg7.setType(type);

		VentilHsup ventilHsup = new VentilHsup();
		ventilHsup.setMNormales(1 * 60);
		ventilHsup.setMSup25(3 * 60);
		ventilHsup.setMSup50(2 * 60);
		ventilHsup.setMHorsContrat(5 * 60);

		Integer idAgent = 9005138;
		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2, ptg3, ptg4, ptg5, ptg6, ptg7));

		VentilationHSupService service = new VentilationHSupService();
		ventilHsup = service.processHeuresSupEpandageForSIPRES(ventilHsup, idAgent, dateLundi, pointages, AgentStatutEnum.C);

		assertEquals(ventilHsup.getMHorsContrat(), 19 * 60);
		assertEquals(ventilHsup.getMSimple(), 0);
		assertEquals(ventilHsup.getMComposees(), 0);
		assertEquals(ventilHsup.getMNormales(), 1 * 60);
		assertEquals(ventilHsup.getMSup25(), 11 * 60);
		assertEquals(ventilHsup.getMSup50(), 8 * 60);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);
	}

	@Test
	public void processHeuresSupEpandageForSIPRES_ConvColl_8HSimple_6HComposees_HeuresSuppDejaPresentes() {

		RefPrime prime = new RefPrime();
		prime.setNoRubr(7716);

		Date dateLundi = new DateTime(2015, 2, 9, 0, 0, 0).toDate();

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());

		Pointage ptg = new Pointage();
		ptg.setRefPrime(prime);
		ptg.setQuantite(120);
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 2, 9, 5, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 2, 9, 7, 0, 0).toDate());
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setRefPrime(prime);
		ptg2.setQuantite(120);
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 2, 10, 5, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 2, 10, 7, 0, 0).toDate());
		ptg2.setType(type);

		Pointage ptg3 = new Pointage();
		ptg3.setRefPrime(prime);
		ptg3.setQuantite(120);
		ptg3.setDateLundi(dateLundi);
		ptg3.setDateDebut(new DateTime(2015, 2, 11, 5, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 2, 11, 7, 0, 0).toDate());
		ptg3.setType(type);

		Pointage ptg4 = new Pointage();
		ptg4.setRefPrime(prime);
		ptg4.setQuantite(120);
		ptg4.setDateLundi(dateLundi);
		ptg4.setDateDebut(new DateTime(2015, 2, 12, 5, 0, 0).toDate());
		ptg4.setDateFin(new DateTime(2015, 2, 12, 7, 0, 0).toDate());
		ptg4.setType(type);

		Pointage ptg5 = new Pointage();
		ptg5.setRefPrime(prime);
		ptg5.setQuantite(120);
		ptg5.setDateLundi(dateLundi);
		ptg5.setDateDebut(new DateTime(2015, 2, 13, 5, 0, 0).toDate());
		ptg5.setDateFin(new DateTime(2015, 2, 13, 7, 0, 0).toDate());
		ptg5.setType(type);

		Pointage ptg6 = new Pointage();
		ptg6.setRefPrime(prime);
		ptg6.setQuantite(120);
		ptg6.setDateLundi(dateLundi);
		ptg6.setDateDebut(new DateTime(2015, 2, 14, 5, 0, 0).toDate());
		ptg6.setDateFin(new DateTime(2015, 2, 14, 7, 0, 0).toDate());
		ptg6.setType(type);

		Pointage ptg7 = new Pointage();
		ptg7.setRefPrime(prime);
		ptg7.setQuantite(120);
		ptg7.setDateLundi(dateLundi);
		ptg7.setDateDebut(new DateTime(2015, 2, 15, 5, 0, 0).toDate());
		ptg7.setDateFin(new DateTime(2015, 2, 15, 7, 0, 0).toDate());
		ptg7.setType(type);

		VentilHsup ventilHsup = new VentilHsup();
		ventilHsup.setMNormales(1 * 60);
		ventilHsup.setMSup25(3 * 60);
		ventilHsup.setMSup50(2 * 60);
		ventilHsup.setMHorsContrat(5 * 60);

		Integer idAgent = 9005138;
		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2, ptg3, ptg4, ptg5, ptg6, ptg7));

		VentilationHSupService service = new VentilationHSupService();
		ventilHsup = service.processHeuresSupEpandageForSIPRES(ventilHsup, idAgent, dateLundi, pointages, AgentStatutEnum.CC);

		assertEquals(ventilHsup.getMHorsContrat(), 19 * 60);
		assertEquals(ventilHsup.getMSimple(), 0);
		assertEquals(ventilHsup.getMComposees(), 0);
		assertEquals(ventilHsup.getMNormales(), 1 * 60);
		assertEquals(ventilHsup.getMSup25(), 11 * 60);
		assertEquals(ventilHsup.getMSup50(), 8 * 60);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);
	}

	// bug #17361
	@Test
	public void processHeuresSup_Contractuel_bugHSupRecupereesDimanche() {

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(7.48);
		spbase.setHeureMardi(7.48);
		spbase.setHeureMercredi(7.48);
		spbase.setHeureJeudi(7.48);
		spbase.setHeureVendredi(7.48);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		Date dateLundi = new DateTime(2015, 7, 6, 0, 0, 0).toDate();
		Integer idAgent = 9004670;

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 7, 6, 15, 30, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 7, 6, 16, 0, 0).toDate());
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(false);
		ptg.setIdAgent(idAgent);
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 7, 8, 3, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 7, 8, 12, 0, 0).toDate());
		ptg2.setHeureSupRecuperee(false);
		ptg2.setHeureSupRappelService(false);
		ptg2.setIdAgent(idAgent);
		ptg2.setType(type);

		Pointage ptg3 = new Pointage();
		ptg3.setDateLundi(dateLundi);
		ptg3.setDateDebut(new DateTime(2015, 7, 12, 3, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 7, 12, 23, 0, 0).toDate());
		ptg3.setHeureSupRecuperee(true);
		ptg3.setHeureSupRappelService(false);
		ptg3.setIdAgent(idAgent);
		ptg3.setType(type);

		VentilHsup ventilHsup = new VentilHsup();

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2, ptg3));

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());

		ventilHsup = service.processHSupContractuel(idAgent, spcarr, dateLundi, pointages, new VentilDate());

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 29.5).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 29.5).intValue());

		assertEquals(ventilHsup.getMSimple(), 0);
		assertEquals(ventilHsup.getMNormales(), 0);
		assertEquals(ventilHsup.getMComposees(), 0);

		assertEquals(ventilHsup.getMSup25(), new Double(60 * 7.5).intValue());
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 17 * 60);
		assertEquals(ventilHsup.getMsNuit(), 5 * 60);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), new Double(60 * 0.5).intValue());
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 17 * 60);
		assertEquals(ventilHsup.getMsNuitRecup(), 3 * 60);
	}

	// bug #17361 meme bug mais reproduit en PROD
	@Test
	public void processHeuresSup_Contractuel_bugHSupRecupereesEnProdNoMatr5490() {

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(7.48);
		spbase.setHeureMardi(7.48);
		spbase.setHeureMercredi(7.48);
		spbase.setHeureJeudi(7.48);
		spbase.setHeureVendredi(7.48);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		Date dateLundi = new DateTime(2015, 6, 29, 0, 0, 0).toDate();
		Integer idAgent = 9005490;

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 6, 29, 20, 45, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 6, 30, 0, 0, 0).toDate());
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(false);
		ptg.setIdAgent(idAgent);
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 6, 30, 0, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 6, 30, 5, 15, 0).toDate());
		ptg2.setHeureSupRecuperee(true);
		ptg2.setHeureSupRappelService(false);
		ptg2.setIdAgent(idAgent);
		ptg2.setType(type);

		VentilHsup ventilHsup = new VentilHsup();

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2));

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());

		ventilHsup = service.processHSupContractuel(idAgent, spcarr, dateLundi, pointages, new VentilDate());

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 8.5).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 8.5).intValue());

		assertEquals(ventilHsup.getMSimple(), 0);
		assertEquals(ventilHsup.getMNormales(), 0);
		assertEquals(ventilHsup.getMComposees(), 0);

		assertEquals(ventilHsup.getMSup25(), new Double(60 * 1.5).intValue());
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 7 * 60);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), new Double(60 * 1.5).intValue());
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 7 * 60);
	}

	// bug #17361 meme bug mais reproduit en PROD
	@Test
	public void processHeuresSup_Contractuel_bugHSupRecupereesEnProdNoMatr4744() {

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(7.48);
		spbase.setHeureMardi(7.48);
		spbase.setHeureMercredi(7.48);
		spbase.setHeureJeudi(7.48);
		spbase.setHeureVendredi(7.48);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		Date dateLundi = new DateTime(2015, 6, 15, 0, 0, 0).toDate();
		Integer idAgent = 9004744;

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 6, 16, 17, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 6, 16, 21, 0, 0).toDate());
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(false);
		ptg.setIdAgent(idAgent);
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 6, 19, 17, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 6, 19, 21, 15, 0).toDate());
		ptg2.setHeureSupRecuperee(true);
		ptg2.setHeureSupRappelService(false);
		ptg2.setIdAgent(idAgent);
		ptg2.setType(type);

		Pointage ptg3 = new Pointage();
		ptg3.setDateLundi(dateLundi);
		ptg3.setDateDebut(new DateTime(2015, 6, 20, 16, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 6, 20, 23, 45, 0).toDate());
		ptg3.setHeureSupRecuperee(true);
		ptg3.setHeureSupRappelService(false);
		ptg3.setIdAgent(idAgent);
		ptg3.setType(type);

		Pointage ptg4 = new Pointage();
		ptg4.setDateLundi(dateLundi);
		ptg4.setDateDebut(new DateTime(2015, 6, 21, 16, 0, 0).toDate());
		ptg4.setDateFin(new DateTime(2015, 6, 21, 21, 0, 0).toDate());
		ptg4.setHeureSupRecuperee(true);
		ptg4.setHeureSupRappelService(false);
		ptg4.setIdAgent(idAgent);
		ptg4.setType(type);

		VentilHsup ventilHsup = new VentilHsup();

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2, ptg3, ptg4));

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());

		ventilHsup = service.processHSupContractuel(idAgent, spcarr, dateLundi, pointages, new VentilDate());

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 21).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 21).intValue());

		assertEquals(ventilHsup.getMSimple(), 0);
		assertEquals(ventilHsup.getMNormales(), 0);
		assertEquals(ventilHsup.getMComposees(), 0);

		assertEquals(ventilHsup.getMSup25(), 60 * 8);
		assertEquals(ventilHsup.getMSup50(), new Double(60 * 6.25).intValue());
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 60 * 5);
		assertEquals(ventilHsup.getMsNuit(), new Double(60 * 1.75).intValue());

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 60 * 8);
		assertEquals(ventilHsup.getMSup50Recup(), new Double(60 * 6.25).intValue());
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 60 * 5);
		assertEquals(ventilHsup.getMsNuitRecup(), new Double(60 * 1.75).intValue());
	}

	// bug #28268 bug en PROD
	@Test
	public void processHeuresSup_Contractuel_bugHSup_miTemps_NoMatr4569_semaine24() {

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(4.0);
		spbase.setHeureMardi(4.0);
		spbase.setHeureMercredi(4.0);
		spbase.setHeureJeudi(4.0);
		spbase.setHeureVendredi(3.3);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(19.3);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		Date dateLundi = new DateTime(2015, 6, 8, 0, 0, 0).toDate();
		Integer idAgent = 9004569;

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 6, 9, 11, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 6, 9, 16, 0, 0).toDate());
		ptg.setHeureSupRecuperee(false);
		ptg.setHeureSupRappelService(false);
		ptg.setIdAgent(idAgent);
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 6, 10, 11, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 6, 10, 16, 0, 0).toDate());
		ptg2.setHeureSupRecuperee(false);
		ptg2.setHeureSupRappelService(false);
		ptg2.setIdAgent(idAgent);
		ptg2.setType(type);

		Pointage ptg3 = new Pointage();
		ptg3.setDateLundi(dateLundi);
		ptg3.setDateDebut(new DateTime(2015, 6, 11, 11, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 6, 11, 16, 0, 0).toDate());
		ptg3.setHeureSupRecuperee(false);
		ptg3.setHeureSupRappelService(false);
		ptg3.setIdAgent(idAgent);
		ptg3.setType(type);

		Pointage ptg4 = new Pointage();
		ptg4.setDateLundi(dateLundi);
		ptg4.setDateDebut(new DateTime(2015, 6, 12, 11, 0, 0).toDate());
		ptg4.setDateFin(new DateTime(2015, 6, 12, 16, 0, 0).toDate());
		ptg4.setHeureSupRecuperee(false);
		ptg4.setHeureSupRappelService(false);
		ptg4.setIdAgent(idAgent);
		ptg4.setType(type);

		VentilHsup ventilHsup = new VentilHsup();

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2, ptg3, ptg4));

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());

		ventilHsup = service.processHSupContractuel(idAgent, spcarr, dateLundi, pointages, new VentilDate());

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 20).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 0.5).intValue());

		assertEquals(ventilHsup.getMSimple(), 0);
		assertEquals(ventilHsup.getMNormales(), new Double(60 * 19.5).intValue());
		assertEquals(ventilHsup.getMComposees(), 0);

		assertEquals(ventilHsup.getMSup25(), new Double(60 * 0.5).intValue());
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);
	}

	// bug #28268 bug en PROD
	@Test
	public void processHeuresSup_Contractuel_bugHSup_miTemps_NoMatr4570_semaine42() {

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(4.0);
		spbase.setHeureMardi(4.0);
		spbase.setHeureMercredi(4.0);
		spbase.setHeureJeudi(4.0);
		spbase.setHeureVendredi(3.3);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(19.3);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		Date dateLundi = new DateTime(2015, 10, 12, 0, 0, 0).toDate();
		Integer idAgent = 9004570;

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 10, 13, 11, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 10, 13, 16, 0, 0).toDate());
		ptg.setHeureSupRecuperee(false);
		ptg.setHeureSupRappelService(false);
		ptg.setIdAgent(idAgent);
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 10, 14, 11, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 10, 14, 16, 0, 0).toDate());
		ptg2.setHeureSupRecuperee(false);
		ptg2.setHeureSupRappelService(false);
		ptg2.setIdAgent(idAgent);
		ptg2.setType(type);

		Pointage ptg3 = new Pointage();
		ptg3.setDateLundi(dateLundi);
		ptg3.setDateDebut(new DateTime(2015, 10, 15, 11, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 10, 15, 16, 0, 0).toDate());
		ptg3.setHeureSupRecuperee(false);
		ptg3.setHeureSupRappelService(false);
		ptg3.setIdAgent(idAgent);
		ptg3.setType(type);

		VentilHsup ventilHsup = new VentilHsup();

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2, ptg3));

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());

		ventilHsup = service.processHSupContractuel(idAgent, spcarr, dateLundi, pointages, new VentilDate());

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 15).intValue());
		assertEquals(ventilHsup.getMSup(), 0);

		assertEquals(ventilHsup.getMSimple(), 0);
		assertEquals(ventilHsup.getMNormales(), new Double(60 * 15).intValue());
		assertEquals(ventilHsup.getMComposees(), 0);

		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);
	}

	// bug #28286 bug en PROD
	@Test
	public void processHeuresSup_Contractuel_bugHSup_miTemps_NoMatr4569_semaine25() {

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(4.0);
		spbase.setHeureMardi(4.0);
		spbase.setHeureMercredi(4.0);
		spbase.setHeureJeudi(4.0);
		spbase.setHeureVendredi(3.3);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(19.3);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		Date dateLundi = new DateTime(2015, 6, 15, 0, 0, 0).toDate();
		Integer idAgent = 9004569;

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 6, 15, 11, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 6, 15, 16, 0, 0).toDate());
		ptg.setHeureSupRecuperee(false);
		ptg.setHeureSupRappelService(false);
		ptg.setIdAgent(idAgent);
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 6, 16, 11, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 6, 16, 16, 0, 0).toDate());
		ptg2.setHeureSupRecuperee(false);
		ptg2.setHeureSupRappelService(false);
		ptg2.setIdAgent(idAgent);
		ptg2.setType(type);

		Pointage ptg3 = new Pointage();
		ptg3.setDateLundi(dateLundi);
		ptg3.setDateDebut(new DateTime(2015, 6, 17, 11, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 6, 17, 16, 0, 0).toDate());
		ptg3.setHeureSupRecuperee(false);
		ptg3.setHeureSupRappelService(false);
		ptg3.setIdAgent(idAgent);
		ptg3.setType(type);

		Pointage ptg4 = new Pointage();
		ptg4.setDateLundi(dateLundi);
		ptg4.setDateDebut(new DateTime(2015, 6, 18, 11, 0, 0).toDate());
		ptg4.setDateFin(new DateTime(2015, 6, 18, 16, 0, 0).toDate());
		ptg4.setHeureSupRecuperee(false);
		ptg4.setHeureSupRappelService(false);
		ptg4.setIdAgent(idAgent);
		ptg4.setType(type);

		Pointage ptg5 = new Pointage();
		ptg5.setDateLundi(dateLundi);
		ptg5.setDateDebut(new DateTime(2015, 6, 19, 11, 0, 0).toDate());
		ptg5.setDateFin(new DateTime(2015, 6, 19, 16, 0, 0).toDate());
		ptg5.setHeureSupRecuperee(false);
		ptg5.setHeureSupRappelService(false);
		ptg5.setIdAgent(idAgent);
		ptg5.setType(type);

		VentilHsup ventilHsup = new VentilHsup();

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2, ptg3, ptg4, ptg5));

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());

		ventilHsup = service.processHSupContractuel(idAgent, spcarr, dateLundi, pointages, new VentilDate());

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 25).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 5.5).intValue());

		assertEquals(ventilHsup.getMSimple(), 0);
		assertEquals(ventilHsup.getMNormales(), new Double(60 * 19.5).intValue());
		assertEquals(ventilHsup.getMComposees(), 0);

		assertEquals(ventilHsup.getMSup25(), new Double(60 * 5.5).intValue());
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);
	}

	// bug #28286 bug en PROD
	// on fait le meme cas de test que ci-dessus mais pour les fonctionnaires
	@Test
	public void processHeuresSup_fonctionnaire_bugHSup_miTemps_NoMatr5364_semaine43() {

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(4.0);
		spbase.setHeureMardi(4.0);
		spbase.setHeureMercredi(4.0);
		spbase.setHeureJeudi(4.0);
		spbase.setHeureVendredi(3.3);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(19.3);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		Date dateLundi = new DateTime(2015, 10, 19, 0, 0, 0).toDate();
		Integer idAgent = 9005364;

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 10, 19, 11, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 10, 19, 16, 0, 0).toDate());
		ptg.setHeureSupRecuperee(false);
		ptg.setHeureSupRappelService(false);
		ptg.setIdAgent(idAgent);
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 10, 20, 11, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 10, 20, 16, 0, 0).toDate());
		ptg2.setHeureSupRecuperee(false);
		ptg2.setHeureSupRappelService(false);
		ptg2.setIdAgent(idAgent);
		ptg2.setType(type);

		Pointage ptg3 = new Pointage();
		ptg3.setDateLundi(dateLundi);
		ptg3.setDateDebut(new DateTime(2015, 10, 21, 11, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 10, 21, 16, 0, 0).toDate());
		ptg3.setHeureSupRecuperee(false);
		ptg3.setHeureSupRappelService(false);
		ptg3.setIdAgent(idAgent);
		ptg3.setType(type);

		Pointage ptg4 = new Pointage();
		ptg4.setDateLundi(dateLundi);
		ptg4.setDateDebut(new DateTime(2015, 10, 22, 11, 0, 0).toDate());
		ptg4.setDateFin(new DateTime(2015, 10, 22, 16, 0, 0).toDate());
		ptg4.setHeureSupRecuperee(false);
		ptg4.setHeureSupRappelService(false);
		ptg4.setIdAgent(idAgent);
		ptg4.setType(type);

		Pointage ptg5 = new Pointage();
		ptg5.setDateLundi(dateLundi);
		ptg5.setDateDebut(new DateTime(2015, 10, 23, 11, 0, 0).toDate());
		ptg5.setDateFin(new DateTime(2015, 10, 23, 16, 0, 0).toDate());
		ptg5.setHeureSupRecuperee(false);
		ptg5.setHeureSupRappelService(false);
		ptg5.setIdAgent(idAgent);
		ptg5.setType(type);

		VentilHsup ventilHsup = new VentilHsup();

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2, ptg3, ptg4, ptg5));

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());

		ventilHsup = service.processHSupFonctionnaire(idAgent, spcarr, dateLundi, pointages, new VentilDate());

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 25).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 5.5).intValue());

		assertEquals(ventilHsup.getMNormales(), new Double(60 * 19.5).intValue());
		assertEquals(ventilHsup.getMSimple(), new Double(60 * 3.0).intValue());
		assertEquals(ventilHsup.getMComposees(), new Double(60 * 2.5).intValue());

		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);
	}

	// bug #28286 bug en PROD
	// on fait le meme cas de test que ci-dessus mais pour les conventions
	// collectives
	@Test
	public void processHeuresSup_convColl_bugHSup_miTemps_NoMatr5364_semaine43() {

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(4.0);
		spbase.setHeureMardi(4.0);
		spbase.setHeureMercredi(4.0);
		spbase.setHeureJeudi(4.0);
		spbase.setHeureVendredi(3.3);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(19.3);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		Date dateLundi = new DateTime(2015, 10, 19, 0, 0, 0).toDate();
		Integer idAgent = 9005364;

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2015, 10, 19, 11, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2015, 10, 19, 16, 0, 0).toDate());
		ptg.setHeureSupRecuperee(false);
		ptg.setHeureSupRappelService(false);
		ptg.setIdAgent(idAgent);
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2015, 10, 20, 11, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 10, 20, 16, 0, 0).toDate());
		ptg2.setHeureSupRecuperee(false);
		ptg2.setHeureSupRappelService(false);
		ptg2.setIdAgent(idAgent);
		ptg2.setType(type);

		Pointage ptg3 = new Pointage();
		ptg3.setDateLundi(dateLundi);
		ptg3.setDateDebut(new DateTime(2015, 10, 21, 11, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 10, 21, 16, 0, 0).toDate());
		ptg3.setHeureSupRecuperee(false);
		ptg3.setHeureSupRappelService(false);
		ptg3.setIdAgent(idAgent);
		ptg3.setType(type);

		Pointage ptg4 = new Pointage();
		ptg4.setDateLundi(dateLundi);
		ptg4.setDateDebut(new DateTime(2015, 10, 22, 11, 0, 0).toDate());
		ptg4.setDateFin(new DateTime(2015, 10, 22, 16, 0, 0).toDate());
		ptg4.setHeureSupRecuperee(false);
		ptg4.setHeureSupRappelService(false);
		ptg4.setIdAgent(idAgent);
		ptg4.setType(type);

		Pointage ptg5 = new Pointage();
		ptg5.setDateLundi(dateLundi);
		ptg5.setDateDebut(new DateTime(2015, 10, 23, 11, 0, 0).toDate());
		ptg5.setDateFin(new DateTime(2015, 10, 23, 16, 0, 0).toDate());
		ptg5.setHeureSupRecuperee(false);
		ptg5.setHeureSupRappelService(false);
		ptg5.setIdAgent(idAgent);
		ptg5.setType(type);

		VentilHsup ventilHsup = new VentilHsup();

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2, ptg3, ptg4, ptg5));

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());

		ventilHsup = service.processHSupConventionCollective(idAgent, spcarr, dateLundi, pointages, false, new VentilDate());

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 25).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 5.5).intValue());

		assertEquals(ventilHsup.getMNormales(), new Double(60 * 19.5).intValue());
		assertEquals(ventilHsup.getMSimple(), 0);
		assertEquals(ventilHsup.getMComposees(), 0);

		assertEquals(ventilHsup.getMSup25(), new Double(60 * 5.5).intValue());
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);
	}

	// bug #31224 bug en PROD
	@Test
	public void processHeuresSup_fonctionnaire_bugHSup_pleinTemps_NoMatr3368_semaine18_1erMai() {

		// base pointage 39B - 39h DPM
		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		Date dateLundi = new DateTime(2016, 4, 25, 0, 0, 0).toDate();
		Integer idAgent = 9005364;

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2016, 4, 29, 17, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2016, 4, 29, 20, 0, 0).toDate());
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(false);
		ptg.setIdAgent(idAgent);
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2016, 5, 1, 7, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2016, 5, 1, 12, 0, 0).toDate());
		ptg2.setHeureSupRecuperee(true);
		ptg2.setHeureSupRappelService(false);
		ptg2.setIdAgent(idAgent);
		ptg2.setType(type);

		VentilHsup ventilHsup = new VentilHsup();

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2));

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());

		ventilHsup = service.processHSupFonctionnaire(idAgent, spcarr, dateLundi, pointages, new VentilDate());

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 8).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 8).intValue());

		assertEquals(ventilHsup.getMNormales(), new Double(60 * 0).intValue());
		assertEquals(ventilHsup.getMSimple(), new Double(60 * 3.0).intValue());
		assertEquals(ventilHsup.getMComposees(), new Double(60 * 0).intValue());

		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 5 * 60);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 3 * 60);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 5 * 60);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);
	}

	// bug #31224 bug en PROD
	// meme cas que ci-dessus mais pour les contractuels
	@Test
	public void processHeuresSup_contractuel_bugHSup_pleinTemps_NoMatr3368_semaine18() {

		// base pointage 39B - 39h DPM
		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setHeureLundi(8.0);
		spbase.setHeureMardi(8.0);
		spbase.setHeureMercredi(8.0);
		spbase.setHeureJeudi(8.0);
		spbase.setHeureVendredi(7.0);
		spbase.setHeureSamedi(0.0);
		spbase.setHeureDimanche(0.0);
		spbase.setBaseCalculee(39.0);

		ISirhWSConsumer hService = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(hService.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(spbase);

		Date dateLundi = new DateTime(2016, 4, 25, 0, 0, 0).toDate();
		Integer idAgent = 9005364;

		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage ptg = new Pointage();
		ptg.setDateLundi(dateLundi);
		ptg.setDateDebut(new DateTime(2016, 4, 29, 17, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2016, 4, 29, 20, 0, 0).toDate());
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(false);
		ptg.setIdAgent(idAgent);
		ptg.setType(type);

		Pointage ptg2 = new Pointage();
		ptg2.setDateLundi(dateLundi);
		ptg2.setDateDebut(new DateTime(2016, 5, 1, 7, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2016, 5, 1, 12, 0, 0).toDate());
		ptg2.setHeureSupRecuperee(true);
		ptg2.setHeureSupRappelService(false);
		ptg2.setIdAgent(idAgent);
		ptg2.setType(type);

		VentilHsup ventilHsup = new VentilHsup();

		Spcarr spcarr = new Spcarr();

		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);

		List<Pointage> pointages = new ArrayList<Pointage>();
		pointages.addAll(Arrays.asList(ptg, ptg2));

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", hService);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());

		ventilHsup = service.processHSupContractuel(idAgent, spcarr, dateLundi, pointages, new VentilDate());

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 8).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 8).intValue());

		assertEquals(ventilHsup.getMNormales(), new Double(60 * 0).intValue());
		assertEquals(ventilHsup.getMSimple(), new Double(60 * 0).intValue());
		assertEquals(ventilHsup.getMComposees(), new Double(60 * 0).intValue());

		assertEquals(ventilHsup.getMSup25(), new Double(60 * 3).intValue());
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 5 * 60);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 3 * 60);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 5 * 60);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);
	}

	@Test
	public void processHSupFromPointageCalcule_returnNull() {

		Integer idAgent = 9005138;
		Date dateLundi = new DateTime().toDate();
		List<PointageCalcule> pointagesCalcules = new ArrayList<PointageCalcule>();

		VentilHsup ventilHSup = null;

		VentilationHSupService service = new VentilationHSupService();

		ventilHSup = service.processHSupFromPointageCalcule(idAgent, dateLundi, pointagesCalcules, ventilHSup);

		assertNull(ventilHSup);
	}

	@Test
	public void processHSupFromPointageCalcule_cas1_VentilHsupNull() {

		Integer idAgent = 9005138;
		Date dateLundi = new DateTime().toDate();
		VentilHsup ventilHsup = null;

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setBaseCalculee(39.0);

		PointageCalcule ptg1 = new PointageCalcule();
		ptg1.setQuantite(new Double(2 * 60));
		ptg1.setType(hSup);

		List<PointageCalcule> pointagesCalcules = new ArrayList<PointageCalcule>();
		pointagesCalcules.add(ptg1);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(spbase);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getBaseCalculee()))
				.thenReturn(new Double(spbase.getBaseCalculee() * 60).intValue());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		/////////////////////////////
		// 1er calcul avec juste 2h Supp
		ventilHsup = service.processHSupFromPointageCalcule(idAgent, dateLundi, pointagesCalcules, ventilHsup);

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 2).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 2).intValue());

		assertEquals(ventilHsup.getMSimple(), 2 * 60);
		assertEquals(ventilHsup.getMNormales(), 0);
		assertEquals(ventilHsup.getMComposees(), 0);

		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);

		/////////////////////////////
		// 2e calcul avec 5h Supp

		ventilHsup = null;
		PointageCalcule ptg2 = new PointageCalcule();
		ptg2.setQuantite(new Double(3 * 60));
		ptg2.setType(hSup);

		pointagesCalcules.add(ptg2);

		ventilHsup = service.processHSupFromPointageCalcule(idAgent, dateLundi, pointagesCalcules, ventilHsup);

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 5).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 5).intValue());

		assertEquals(ventilHsup.getMSimple(), 3 * 60);
		assertEquals(ventilHsup.getMNormales(), 0);
		assertEquals(ventilHsup.getMComposees(), 2 * 60);

		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);
	}

	@Test
	public void processHSupFromPointageCalcule_cas2_baseHoraire25h() {

		Integer idAgent = 9005138;
		Date dateLundi = new DateTime().toDate();
		VentilHsup ventilHsup = null;

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setBaseCalculee(25.0);

		PointageCalcule ptg1 = new PointageCalcule();
		ptg1.setQuantite(new Double(12 * 60));
		ptg1.setType(hSup);

		List<PointageCalcule> pointagesCalcules = new ArrayList<PointageCalcule>();
		pointagesCalcules.add(ptg1);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(spbase);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getBaseCalculee()))
				.thenReturn(new Double(spbase.getBaseCalculee() * 60).intValue());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", helperService);
		/////////////////////////////
		// 1er calcul avec juste 12h Supp
		ventilHsup = service.processHSupFromPointageCalcule(idAgent, dateLundi, pointagesCalcules, ventilHsup);

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 12).intValue());
		assertEquals(ventilHsup.getMSup(), 0);

		assertEquals(ventilHsup.getMNormales(), 12 * 60);
		assertEquals(ventilHsup.getMSimple(), 0);
		assertEquals(ventilHsup.getMComposees(), 0);

		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);

		/////////////////////////////
		// 2e calcul avec 24h Supp

		ventilHsup = null;
		PointageCalcule ptg2 = new PointageCalcule();
		ptg2.setQuantite(new Double(12 * 60));
		ptg2.setType(hSup);

		pointagesCalcules.add(ptg2);

		ventilHsup = service.processHSupFromPointageCalcule(idAgent, dateLundi, pointagesCalcules, ventilHsup);

		assertEquals(ventilHsup.getMHorsContrat(), new Double(60 * 24).intValue());
		assertEquals(ventilHsup.getMSup(), new Double(60 * 10).intValue());

		assertEquals(ventilHsup.getMNormales(), 14 * 60);
		assertEquals(ventilHsup.getMSimple(), 3 * 60);
		assertEquals(ventilHsup.getMComposees(), 7 * 60);

		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);
	}

	@Test
	public void processHSupFromPointageCalcule_cas3_VentilHsupExistant_baseHoraire35h() {

		Integer idAgent = 9005138;
		Date dateLundi = new DateTime().toDate();

		VentilHsup ventilHsup = new VentilHsup();
		ventilHsup.setMHorsContrat(3 * 60);
		ventilHsup.setMSup(0);
		ventilHsup.setMNormales(3 * 60);
		ventilHsup.setMSimple(0);

		BaseHorairePointageDto spbase = new BaseHorairePointageDto();
		spbase.setBaseCalculee(35.0);

		PointageCalcule ptg1 = new PointageCalcule();
		ptg1.setQuantite(new Double(12 * 60));
		ptg1.setType(hSup);

		List<PointageCalcule> pointagesCalcules = new ArrayList<PointageCalcule>();
		pointagesCalcules.add(ptg1);

		ISirhWSConsumer sirhWsConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWsConsumer.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class), Mockito.any(Date.class)))
				.thenReturn(spbase);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.convertMairieNbHeuresFormatToMinutes(spbase.getBaseCalculee()))
				.thenReturn(new Double(spbase.getBaseCalculee() * 60).intValue());

		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhWsConsumer);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		/////////////////////////////
		// 1er calcul avec juste 12h Supp
		ventilHsup = service.processHSupFromPointageCalcule(idAgent, dateLundi, pointagesCalcules, ventilHsup);

		assertEquals(ventilHsup.getMHorsContrat(), 60 * 15);
		assertEquals(ventilHsup.getMSup(), 60 * 11);

		assertEquals(ventilHsup.getMNormales(), 4 * 60);
		assertEquals(ventilHsup.getMSimple(), 3 * 60);
		assertEquals(ventilHsup.getMComposees(), 8 * 60);

		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);

		/////////////////////////////
		// 2e calcul avec 24h Supp

		ventilHsup = new VentilHsup();
		ventilHsup.setMHorsContrat(3 * 60);
		ventilHsup.setMSup(0);
		ventilHsup.setMNormales(3 * 60);
		ventilHsup.setMSimple(0);

		PointageCalcule ptg2 = new PointageCalcule();
		ptg2.setQuantite(new Double(12 * 60));
		ptg2.setType(hSup);

		pointagesCalcules.add(ptg2);

		ventilHsup = service.processHSupFromPointageCalcule(idAgent, dateLundi, pointagesCalcules, ventilHsup);

		assertEquals(ventilHsup.getMHorsContrat(), 60 * 27);
		assertEquals(ventilHsup.getMSup(), 60 * 23);

		assertEquals(ventilHsup.getMNormales(), 4 * 60);
		assertEquals(ventilHsup.getMSimple(), 3 * 60);
		assertEquals(ventilHsup.getMComposees(), 20 * 60);

		assertEquals(ventilHsup.getMSup25(), 0);
		assertEquals(ventilHsup.getMSup50(), 0);
		assertEquals(ventilHsup.getMMai(), 0);
		assertEquals(ventilHsup.getMsdjf(), 0);
		assertEquals(ventilHsup.getMsNuit(), 0);

		assertEquals(ventilHsup.getMNormalesRecup(), 0);
		assertEquals(ventilHsup.getMSimpleRecup(), 0);
		assertEquals(ventilHsup.getMComposeesRecup(), 0);

		assertEquals(ventilHsup.getMSup25Recup(), 0);
		assertEquals(ventilHsup.getMSup50Recup(), 0);
		assertEquals(ventilHsup.getMMaiRecup(), 0);
		assertEquals(ventilHsup.getMsdjfRecup(), 0);
		assertEquals(ventilHsup.getMsNuitRecup(), 0);
	}
}
