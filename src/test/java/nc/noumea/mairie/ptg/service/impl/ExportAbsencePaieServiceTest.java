package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;

import org.junit.BeforeClass;
import org.junit.Test;

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
}
