package nc.noumea.mairie.ptg.service;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import nc.noumea.mairie.domain.Spbase;
import nc.noumea.mairie.domain.Spbhor;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilHsup;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class VentilationHsupServiceTest {

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
	public void processHSupFonctionnaire_base39H() {
		
		// Given
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
		
		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9));
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(15d, result.getHHorsContrat(), 0);
		assertEquals(3d, result.getHAbsences(), 0);
		assertEquals(12d, result.getHSup(), 0);
		
		assertEquals(1d, result.getHsNuit(), 0);
		assertEquals(2d, result.getHsdjf(), 0);
		assertEquals(0d, result.getHNormales(), 0);
		assertEquals(3d, result.getHSimple(), 0);
		assertEquals(6d, result.getHComposees(), 0);
	}
	
	@Test
	public void processHSupFonctionnaire_withBase20H() {
		
		// Given
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
		
		// When
		VentilHsup result = service.processHSupFonctionnaire(9007865, spcarr, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9));
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(15d, result.getHHorsContrat(), 0);
		assertEquals(3d, result.getHAbsences(), 0);
		assertEquals(12d, result.getHSup(), 0);
		
		assertEquals(1d, result.getHsNuit(), 0);
		assertEquals(2d, result.getHsdjf(), 0);
		assertEquals(9d, result.getHNormales(), 0);
		assertEquals(0d, result.getHSimple(), 0);
		assertEquals(0d, result.getHComposees(), 0);
	}
	
	@Test
	public void processHSupConventionCollective_CC_base39H() {
		
		// Given
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
		
		// When
		VentilHsup result = service.processHSupConventionCollective(9007865, spcarr, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9));
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(15d, result.getHHorsContrat(), 0);
		assertEquals(3d, result.getHAbsences(), 0);
		assertEquals(12d, result.getHSup(), 0);
		
		assertEquals(4d, result.getHsNuit(), 0);
		assertEquals(6d, result.getHsdjf(), 0);
		assertEquals(4d, result.getHMai(), 0);
		assertEquals(0d, result.getHComplementaires(), 0);
		assertEquals(8d, result.getHSup25(), 0);
		assertEquals(4d, result.getHSup50(), 0);
		assertEquals(6d, result.getHsdjf25(), 0);
		assertEquals(0d, result.getHsdjf50(), 0);
		assertEquals(0d, result.getHNormales(), 0);
		assertEquals(0d, result.getHSimple(), 0);
		assertEquals(0d, result.getHComposees(), 0);
	}
	
	@Test
	public void processHSupContractuel_C_base20H() {
		
		// Given
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
		
		// When
		VentilHsup result = service.processHSupContractuel(9007865, spcarr, Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9));
				
		// Then
		assertEquals(9007865, (int) result.getIdAgent());
		assertEquals(p1.getDateLundi(), result.getDateLundi());
		assertEquals(15d, result.getHHorsContrat(), 0);
		assertEquals(3d, result.getHAbsences(), 0);
		assertEquals(12d, result.getHSup(), 0);
		
		assertEquals(6d, result.getHsNuit(), 0);
		assertEquals(6d, result.getHsdjf(), 0);
		assertEquals(4d, result.getHMai(), 0);
		assertEquals(12d, result.getHComplementaires(), 0);
		assertEquals(0d, result.getHSup25(), 0);
		assertEquals(0d, result.getHSup50(), 0);
		assertEquals(6d, result.getHsdjf25(), 0);
		assertEquals(0d, result.getHsdjf50(), 0);
		assertEquals(0d, result.getHNormales(), 0);
		assertEquals(0d, result.getHSimple(), 0);
		assertEquals(0d, result.getHComposees(), 0);
	}
}
