package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spbase;
import nc.noumea.mairie.domain.Spbhor;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.service.IHolidayService;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class VentilationHSupServiceTest {

	private static RefTypePointage hSup;
	private static RefTypePointage abs;
	
	@BeforeClass
	public static void Setup() {
		hSup = new RefTypePointage();
		hSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		abs= new RefTypePointage();
		abs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
	}
	
	@Test
	public void processHSup_NoPointages_ReturnNull() {
		
		// Given
		List<Pointage> pointages = new ArrayList<Pointage>();
		Date dateLundi = new LocalDate(2013, 7, 22).toDate();
		
		VentilationHSupService service = new VentilationHSupService();
		
		// When
		VentilHsup result = service.processHSup(9008765, null, dateLundi, pointages, null, false);
		
		// Then
		assertNull(result);
	}
	
	@Test
	public void processHSupFonctionnaire_base39H() {
		
		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();
		
		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 6, 0, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 7, 0, 0).toDate());
		p1.setType(hSup);
					
		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 30, 14, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 16, 0, 0).toDate());
		p2.setType(abs);
		
		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 04, 30, 18, 0, 0).toDate());
		p3.setDateFin(new DateTime(2012, 04, 30, 23, 0, 0).toDate());
		p3.setType(hSup);
		
		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2012, 05, 1, 9, 0, 0).toDate());
		p4.setDateFin(new DateTime(2012, 05, 1, 13, 0, 0).toDate());
		p4.setType(hSup);
		
		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2012, 05, 3, 12, 0, 0).toDate());
		p5.setDateFin(new DateTime(2012, 05, 3, 13, 0, 0).toDate());
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2012, 05, 3, 18, 0, 0).toDate());
		p6.setDateFin(new DateTime(2012, 05, 3, 19, 0, 0).toDate());
		p6.setType(hSup);
		
		Pointage p7 = new Pointage();
		p7.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p7.setDateDebut(new DateTime(2012, 05, 4, 11, 0, 0).toDate());
		p7.setDateFin(new DateTime(2012, 05, 4, 12, 0, 0).toDate());
		p7.setType(abs);
		
		Pointage p8 = new Pointage();
		p8.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p8.setDateDebut(new DateTime(2012, 05, 5, 23, 0, 0).toDate());
		p8.setDateFin(new DateTime(2012, 05, 6, 0, 0, 0).toDate());
		p8.setType(hSup);
		
		Pointage p9 = new Pointage();
		p9.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p9.setDateDebut(new DateTime(2012, 05, 6, 0, 0, 0).toDate());
		p9.setDateFin(new DateTime(2012, 05, 6, 2, 0, 0).toDate());
		p9.setType(hSup);
		
		Spbase spbase = new Spbase();
		spbase.setNbahlu(8);
		spbase.setNbahma(8);
		spbase.setNbahme(8);
		spbase.setNbahje(8);
		spbase.setNbahve(7);
		spbase.setNbahsa(0);
		spbase.setNbahdi(0);
		spbase.setNbashh(39);
		Spcarr spcarr = new Spcarr();
		spcarr.setSpbase(spbase);
		
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);
		
		IHolidayService hService = Mockito.mock(IHolidayService.class);
		Mockito.when(hService.isHoliday(Mockito.any(DateTime.class))).thenReturn(false);
		
		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "holidayService", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		
		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9));
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(15* 60, result.getMHorsContrat(), 0);
		assertEquals(3* 60, result.getMAbsences(), 0);
		assertEquals(12* 60, result.getMSup(), 0);

		assertEquals(1* 60, result.getMsNuit(), 0);
		assertEquals(2* 60, result.getMsdjf(), 0);
		assertEquals(0, result.getMNormales(), 0);
		assertEquals(3* 60, result.getMSimple(), 0);
		assertEquals(6* 60, result.getMComposees(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}
	
//	@Test
	// Temporarily disabled as the BR on MRECUPEREES is not yet stable
	public void processHSupFonctionnaire_base39H_2HS_Recuperees() {
		
		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();
		
		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 6, 15, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 7, 0, 0).toDate());
		p1.setType(hSup);
		
		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 30, 18, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 19, 15, 0).toDate());
		p2.setType(hSup);
		
		Spbase spbase = new Spbase();
		spbase.setNbahlu(8);
		spbase.setNbahma(8);
		spbase.setNbahme(8);
		spbase.setNbahje(8);
		spbase.setNbahve(7);
		spbase.setNbahsa(0);
		spbase.setNbahdi(0);
		spbase.setNbashh(39);
		Spcarr spcarr = new Spcarr();
		spcarr.setSpbase(spbase);
		
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);
		
		IHolidayService hService = Mockito.mock(IHolidayService.class);
		Mockito.when(hService.isHoliday(Mockito.any(DateTime.class))).thenReturn(false);
		
		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "holidayService", hService);
		
		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2));
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(2* 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(2* 60, result.getMSup());
		assertEquals(2* 60, result.getMRecuperees());

		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(2* 60, result.getMSimple());
		assertEquals(0, result.getMComposees());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}
	
	@Test
	public void processHSupFonctionnaire_withBase20H() {
		
		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();
		
		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 6, 0, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 7, 0, 0).toDate());
		p1.setType(hSup);
					
		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 30, 14, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 16, 0, 0).toDate());
		p2.setType(abs);
		
		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 04, 30, 18, 0, 0).toDate());
		p3.setDateFin(new DateTime(2012, 04, 30, 23, 0, 0).toDate());
		p3.setType(hSup);
		
		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2012, 05, 1, 9, 0, 0).toDate());
		p4.setDateFin(new DateTime(2012, 05, 1, 13, 0, 0).toDate());
		p4.setType(hSup);
		
		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2012, 05, 3, 12, 0, 0).toDate());
		p5.setDateFin(new DateTime(2012, 05, 3, 13, 0, 0).toDate());
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2012, 05, 3, 18, 0, 0).toDate());
		p6.setDateFin(new DateTime(2012, 05, 3, 19, 0, 0).toDate());
		p6.setType(hSup);
		
		Pointage p7 = new Pointage();
		p7.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p7.setDateDebut(new DateTime(2012, 05, 4, 11, 0, 0).toDate());
		p7.setDateFin(new DateTime(2012, 05, 4, 12, 0, 0).toDate());
		p7.setType(abs);
		
		Pointage p8 = new Pointage();
		p8.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p8.setDateDebut(new DateTime(2012, 05, 5, 23, 0, 0).toDate());
		p8.setDateFin(new DateTime(2012, 05, 6, 0, 0, 0).toDate());
		p8.setType(hSup);
		
		Pointage p9 = new Pointage();
		p9.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p9.setDateDebut(new DateTime(2012, 05, 6, 0, 0, 0).toDate());
		p9.setDateFin(new DateTime(2012, 05, 6, 2, 0, 0).toDate());
		p9.setType(hSup);
		
		Spbase spbase = new Spbase();
		spbase.setNbahlu(4);
		spbase.setNbahma(4);
		spbase.setNbahme(4);
		spbase.setNbahje(4);
		spbase.setNbahve(4);
		spbase.setNbahsa(0);
		spbase.setNbahdi(0);
		spbase.setNbashh(20);
		Spcarr spcarr = new Spcarr();
		spcarr.setSpbase(spbase);
		
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);
		
		IHolidayService hService = Mockito.mock(IHolidayService.class);
		Mockito.when(hService.isHoliday(Mockito.any(DateTime.class))).thenReturn(false);
		
		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "holidayService", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		
		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9));
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(15 * 60, result.getMHorsContrat(), 0);
		assertEquals(3 * 60, result.getMAbsences(), 0);
		assertEquals(12 * 60, result.getMSup(), 0);

		assertEquals(1 * 60, result.getMsNuit(), 0);
		assertEquals(2 * 60, result.getMsdjf(), 0);
		assertEquals(9 * 60, result.getMNormales(), 0);
		assertEquals(0, result.getMSimple(), 0);
		assertEquals(0, result.getMComposees(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}
	
	@Test
	public void processHSupConventionCollective_CC_base39H() {
		
		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();
		
		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 6, 0, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 7, 0, 0).toDate());
		p1.setType(hSup);
					
		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 30, 14, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 16, 0, 0).toDate());
		p2.setType(abs);
		
		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 04, 30, 18, 0, 0).toDate());
		p3.setDateFin(new DateTime(2012, 04, 30, 23, 0, 0).toDate());
		p3.setType(hSup);
		
		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2012, 05, 1, 9, 0, 0).toDate());
		p4.setDateFin(new DateTime(2012, 05, 1, 13, 0, 0).toDate());
		p4.setType(hSup);
		
		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2012, 05, 3, 12, 0, 0).toDate());
		p5.setDateFin(new DateTime(2012, 05, 3, 13, 0, 0).toDate());
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2012, 05, 3, 18, 0, 0).toDate());
		p6.setDateFin(new DateTime(2012, 05, 3, 19, 0, 0).toDate());
		p6.setType(hSup);
		
		Pointage p7 = new Pointage();
		p7.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p7.setDateDebut(new DateTime(2012, 05, 4, 11, 0, 0).toDate());
		p7.setDateFin(new DateTime(2012, 05, 4, 12, 0, 0).toDate());
		p7.setType(abs);
		
		Pointage p8 = new Pointage();
		p8.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p8.setDateDebut(new DateTime(2012, 05, 5, 23, 0, 0).toDate());
		p8.setDateFin(new DateTime(2012, 05, 6, 0, 0, 0).toDate());
		p8.setType(hSup);
		
		Pointage p9 = new Pointage();
		p9.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p9.setDateDebut(new DateTime(2012, 05, 6, 0, 0, 0).toDate());
		p9.setDateFin(new DateTime(2012, 05, 6, 2, 0, 0).toDate());
		p9.setType(hSup);
		
		Spbase spbase = new Spbase();
		spbase.setNbahlu(8);
		spbase.setNbahma(8);
		spbase.setNbahme(8);
		spbase.setNbahje(8);
		spbase.setNbahve(7);
		spbase.setNbahsa(0);
		spbase.setNbahdi(0);
		spbase.setNbashh(39);
		Spcarr spcarr = new Spcarr();
		spcarr.setSpbase(spbase);
		
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);
		
		IHolidayService hService = Mockito.mock(IHolidayService.class);
		Mockito.when(hService.isHoliday(new DateTime(2012, 05, 1, 9, 0, 0))).thenReturn(true);
		
		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "holidayService", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		
		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9), false);
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(15 * 60, result.getMHorsContrat(), 0);
		assertEquals(3 * 60, result.getMAbsences(), 0);
		assertEquals(12 * 60, result.getMSup(), 0);

		assertEquals(4 * 60, result.getMsNuit(), 0);
		assertEquals(6 * 60, result.getMsdjf(), 0);
		assertEquals(4 * 60, result.getMMai(), 0);
		assertEquals(0 , result.getMComplementaires(), 0);
		assertEquals(8 * 60, result.getMSup25(), 0);
		assertEquals(4 * 60, result.getMSup50(), 0);
		assertEquals(6 * 60, result.getMsdjf25(), 0);
		assertEquals(0 , result.getMsdjf50(), 0);
		assertEquals(0 , result.getMNormales(), 0);
		assertEquals(0 , result.getMSimple(), 0);
		assertEquals(0 , result.getMComposees(), 0);

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}
	
	@Test
	public void processHSupContractuel_C_base20H() {
		
		// Given
		Date dateLundi = new LocalDate(2012, 04, 30).toDate();
		
		Pointage p1 = new Pointage();
		p1.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p1.setDateDebut(new DateTime(2012, 04, 30, 6, 0, 0).toDate());
		p1.setDateFin(new DateTime(2012, 04, 30, 7, 0, 0).toDate());
		p1.setType(hSup);
					
		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2012, 04, 30, 14, 0, 0).toDate());
		p2.setDateFin(new DateTime(2012, 04, 30, 16, 0, 0).toDate());
		p2.setType(abs);
		
		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2012, 04, 30, 18, 0, 0).toDate());
		p3.setDateFin(new DateTime(2012, 04, 30, 23, 0, 0).toDate());
		p3.setType(hSup);
		
		Pointage p4 = new Pointage();
		p4.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p4.setDateDebut(new DateTime(2012, 05, 1, 9, 0, 0).toDate());
		p4.setDateFin(new DateTime(2012, 05, 1, 13, 0, 0).toDate());
		p4.setType(hSup);
		
		Pointage p5 = new Pointage();
		p5.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p5.setDateDebut(new DateTime(2012, 05, 3, 12, 0, 0).toDate());
		p5.setDateFin(new DateTime(2012, 05, 3, 13, 0, 0).toDate());
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2012, 05, 3, 18, 0, 0).toDate());
		p6.setDateFin(new DateTime(2012, 05, 3, 19, 0, 0).toDate());
		p6.setType(hSup);
		
		Pointage p7 = new Pointage();
		p7.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p7.setDateDebut(new DateTime(2012, 05, 4, 11, 0, 0).toDate());
		p7.setDateFin(new DateTime(2012, 05, 4, 12, 0, 0).toDate());
		p7.setType(abs);
		
		Pointage p8 = new Pointage();
		p8.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p8.setDateDebut(new DateTime(2012, 05, 5, 23, 0, 0).toDate());
		p8.setDateFin(new DateTime(2012, 05, 6, 0, 0, 0).toDate());
		p8.setType(hSup);
		
		Pointage p9 = new Pointage();
		p9.setDateLundi(new DateTime(2012, 04, 30, 0, 0, 0).toDate());
		p9.setDateDebut(new DateTime(2012, 05, 6, 0, 0, 0).toDate());
		p9.setDateFin(new DateTime(2012, 05, 6, 2, 0, 0).toDate());
		p9.setType(hSup);
		
		Spbase spbase = new Spbase();
		spbase.setNbahlu(4);
		spbase.setNbahma(4);
		spbase.setNbahme(4);
		spbase.setNbahje(4);
		spbase.setNbahve(4);
		spbase.setNbahsa(0);
		spbase.setNbahdi(0);
		spbase.setNbashh(20);
		Spcarr spcarr = new Spcarr();
		spcarr.setSpbase(spbase);
		
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);
		
		IHolidayService hService = Mockito.mock(IHolidayService.class);
		Mockito.when(hService.isHoliday(new DateTime(2012, 05, 1, 9, 0, 0))).thenReturn(true);
		
		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "holidayService", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		
		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9));
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(15 * 60, result.getMHorsContrat(), 0);
		assertEquals(3 * 60, result.getMAbsences(), 0);
		assertEquals(12 * 60, result.getMSup(), 0);

		assertEquals(6 * 60, result.getMsNuit(), 0);
		assertEquals(6 * 60, result.getMsdjf(), 0);
		assertEquals(4 * 60, result.getMMai(), 0);
		assertEquals(12 * 60, result.getMComplementaires(), 0);
		assertEquals(0 , result.getMSup25(), 0);
		assertEquals(0 , result.getMSup50(), 0);
		assertEquals(6 * 60, result.getMsdjf25(), 0);
		assertEquals(0 , result.getMsdjf50(), 0);
		assertEquals(0 , result.getMNormales(), 0);
		assertEquals(0 , result.getMSimple(), 0);
		assertEquals(0 , result.getMComposees(), 0);

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
		p1.setType(hSup);
					
		Pointage p2 = new Pointage();
		p2.setDateLundi(new DateTime(2013, 04, 1, 0, 0, 0).toDate());
		p2.setDateDebut(new DateTime(2013, 04, 2, 22, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 04, 3, 0, 0, 0).toDate());
		p2.setType(hSup);
				
		Spbase spbase = new Spbase();
		spbase.setNbahlu(8);
		spbase.setNbahma(8);
		spbase.setNbahme(8);
		spbase.setNbahje(8);
		spbase.setNbahve(7);
		spbase.setNbahsa(0);
		spbase.setNbahdi(0);
		spbase.setNbashh(39);
		Spcarr spcarr = new Spcarr();
		spcarr.setSpbase(spbase);
		
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);
		
		IHolidayService hService = Mockito.mock(IHolidayService.class);
		Mockito.when(hService.isHoliday(new DateTime(2013, 04, 1, 8, 0, 0))).thenReturn(true);
		
		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "holidayService", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		
		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, dateLundi, Arrays.asList(p1, p2), true);
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(14 * 60, result.getMHorsContrat());
		assertEquals(0, result.getMAbsences());
		assertEquals(14 * 60, result.getMSup());

		assertEquals(0, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(0, result.getMMai());
		assertEquals(0 , result.getMComplementaires());
		assertEquals(8 * 60, result.getMSup25());
		assertEquals(6 * 60, result.getMSup50());
		assertEquals(0, result.getMsdjf25());
		assertEquals(0 , result.getMsdjf50());
		assertEquals(0 , result.getMNormales());
		assertEquals(0 , result.getMSimple());
		assertEquals(0 , result.getMComposees());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}
	
	@Test
	public void processHSupFonctionnaire_CustomExample_base39H() {
		
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
		p2.setType(hSup);
		
		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 05, 22, 4, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 05, 22, 6, 30, 0).toDate());
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
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 05, 24, 21, 0, 0).toDate());
		p6.setDateFin(new DateTime(2013, 05, 24, 22, 0, 0).toDate());
		p6.setType(hSup);
		
		Spbase spbase = new Spbase();
		spbase.setNbahlu(7.45);
		spbase.setNbahma(7.45);
		spbase.setNbahme(7.45);
		spbase.setNbahje(7.45);
		spbase.setNbahve(7.45);
		spbase.setNbahsa(0);
		spbase.setNbahdi(0);
		spbase.setNbashh(38.45);
		Spcarr spcarr = new Spcarr();
		spcarr.setSpbase(spbase);
		
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);
		
		IHolidayService hService = Mockito.mock(IHolidayService.class);
		Mockito.when(hService.isHoliday(Mockito.any(DateTime.class))).thenReturn(false);
		
		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "holidayService", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		
		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5, p6));
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(360, result.getMHorsContrat());
		assertEquals(210, result.getMAbsences());
		assertEquals(150, result.getMSup());

		assertEquals(60, result.getMsNuit());
		assertEquals(0, result.getMsdjf());
		assertEquals(15, result.getMNormales());
		assertEquals(75, result.getMSimple());
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
		p2.setType(hSup);
		
		Pointage p3 = new Pointage();
		p3.setDateLundi(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		p3.setDateDebut(new DateTime(2013, 05, 22, 4, 0, 0).toDate());
		p3.setDateFin(new DateTime(2013, 05, 22, 6, 30, 0).toDate());
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
		p5.setType(hSup);

		Pointage p6 = new Pointage();
		p6.setDateLundi(new DateTime(2013, 05, 20, 0, 0, 0).toDate());
		p6.setDateDebut(new DateTime(2013, 05, 24, 21, 0, 0).toDate());
		p6.setDateFin(new DateTime(2013, 05, 24, 22, 0, 0).toDate());
		p6.setType(hSup);
		
		Spbase spbase = new Spbase();
		spbase.setNbahlu(8.45);
		spbase.setNbahma(8.45);
		spbase.setNbahme(8.45);
		spbase.setNbahje(8.45);
		spbase.setNbahve(8.45);
		spbase.setNbahsa(4.15);
		spbase.setNbahdi(10.30);
		spbase.setNbashh(58.30);
		Spcarr spcarr = new Spcarr();
		spcarr.setSpbase(spbase);
		
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1d);
		spcarr.setSpbhor(spbhor);
		
		IHolidayService hService = Mockito.mock(IHolidayService.class);
		Mockito.when(hService.isHoliday(Mockito.any(DateTime.class))).thenReturn(false);
		
		VentilationHSupService service = new VentilationHSupService();
		ReflectionTestUtils.setField(service, "holidayService", hService);
		ReflectionTestUtils.setField(service, "helperService", new HelperService());
		
		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, dateLundi, Arrays.asList(p1, p2, p3, p4, p5, p6));
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(360, result.getMHorsContrat());
		assertEquals(210, result.getMAbsences());
		assertEquals(150, result.getMSup());

		assertEquals(0, result.getMsNuit());
		assertEquals(150, result.getMsdjf());
		assertEquals(0, result.getMNormales());
		assertEquals(0, result.getMSimple());
		assertEquals(0, result.getMComposees());

		assertEquals(EtatPointageEnum.VENTILE, result.getEtat());
	}
}
