package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class ExportAbsencePaieServiceTest {

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
	public void exportAbsencesToPaie_NoPointages_ReturnEmptyList() {
		
		// Given
		List<Pointage> pointagesOrderedByDateAsc = new ArrayList<Pointage>();
		ExportAbsencePaieService service = new ExportAbsencePaieService();
		
		// Then
		assertEquals(0, service.exportAbsencesToPaie(pointagesOrderedByDateAsc).size());
	}
	
	@Test
	public void exportAbsencesToPaie_NoAbsencesInPointages_ReturnEmptyList() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setType(hSup);
		Pointage p2 = new Pointage();
		p2.setType(hSup);
		
		ExportAbsencePaieService service = new ExportAbsencePaieService();
		
		// Then
		assertEquals(0, service.exportAbsencesToPaie(Arrays.asList(p1, p2)).size());
	}
	
	//@Test
	public void exportAbsencesToPaie_1Absence_nothingInDataBase_ReturnNewSppact() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008765);
		p1.setDateDebut(new DateTime(2013, 5, 15, 8, 45, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 15, 10, 15, 0).toDate());
		p1.setAbsenceConcertee(false);
		p1.setType(abs);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
		Mockito.when(hS.getIntegerDateMairieFromDate(p1.getDateDebut())).thenReturn(20130515);
		
		ExportAbsencePaieService service = new ExportAbsencePaieService();
		
		// When
		List<Sppact> result = service.exportAbsencesToPaie(Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.size());
	}
}
