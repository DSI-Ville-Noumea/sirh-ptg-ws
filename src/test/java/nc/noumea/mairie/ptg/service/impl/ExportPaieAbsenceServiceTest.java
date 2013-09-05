package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.domain.Spacti;
import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.domain.SppactId;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.repository.IExportPaieRepository;
import nc.noumea.mairie.ptg.repository.IMairieRepository;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ExportPaieAbsenceServiceTest {

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
		ExportPaieAbsenceService service = new ExportPaieAbsenceService();
		
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
		
		ExportPaieAbsenceService service = new ExportPaieAbsenceService();
		
		// Then
		assertEquals(0, service.exportAbsencesToPaie(Arrays.asList(p1, p2)).size());
	}
	
	@Test
	public void exportAbsencesToPaie_1Absence_nothingInDataBase_ReturnNewSppact() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008765);
		p1.setDateDebut(new DateTime(2013, 5, 15, 8, 45, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 15, 10, 15, 0).toDate());
		p1.setAbsenceConcertee(true);
		p1.setType(abs);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
		Mockito.when(hS.getIntegerDateMairieFromDate(p1.getDateDebut())).thenReturn(20130515);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(0d)).thenReturn(0);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(90)).thenReturn(1.3d);
		
		Spacti acti = new Spacti();
		IMairieRepository mR = Mockito.mock(IMairieRepository.class);
		Mockito.when(mR.getEntity(Spacti.class, "A02")).thenReturn(acti);

		IExportPaieRepository eR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(eR.getSppactForDayAndAgent(p1.getIdAgent(), p1.getDateDebut(), "A02")).thenReturn(null);
		
		ExportPaieAbsenceService service = new ExportPaieAbsenceService();
		ReflectionTestUtils.setField(service, "mairieRepository", mR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", eR);
		
		// When
		List<Sppact> result = service.exportAbsencesToPaie(Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(20130515, (int) result.get(0).getId().getDateJour());
		assertEquals(8765, (int) result.get(0).getId().getNomatr());
		assertEquals(acti, result.get(0).getId().getActivite());
		assertEquals(1.3d, result.get(0).getNbHeures(), 0);
	}
	
	@Test
	public void exportAbsencesToPaie_2AbsencesSameDaySameAgentSameActi_nothingInDataBase_ReturnOneSppactForBoth() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008765);
		p1.setDateDebut(new DateTime(2013, 5, 15, 8, 45, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 15, 10, 15, 0).toDate());
		p1.setAbsenceConcertee(true);
		p1.setType(abs);
		
		Pointage p2 = new Pointage();
		p2.setIdAgent(9008765);
		p2.setDateDebut(new DateTime(2013, 5, 15, 14, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 5, 15, 15, 15, 0).toDate());
		p2.setAbsenceConcertee(true);
		p2.setType(abs);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
		Mockito.when(hS.getIntegerDateMairieFromDate(p1.getDateDebut())).thenReturn(20130515);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130515);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(0d)).thenReturn(0);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(1.3d)).thenReturn(90);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(90)).thenReturn(1.3d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(165)).thenReturn(2.45d);
		
		Spacti acti = new Spacti();
		IMairieRepository mR = Mockito.mock(IMairieRepository.class);
		Mockito.when(mR.getEntity(Spacti.class, "A02")).thenReturn(acti);

		IExportPaieRepository eR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(eR.getSppactForDayAndAgent(p1.getIdAgent(), p1.getDateDebut(), "A02")).thenReturn(null);
		
		ExportPaieAbsenceService service = new ExportPaieAbsenceService();
		ReflectionTestUtils.setField(service, "mairieRepository", mR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", eR);
		
		// When
		List<Sppact> result = service.exportAbsencesToPaie(Arrays.asList(p1, p2));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(20130515, (int) result.get(0).getId().getDateJour());
		assertEquals(8765, (int) result.get(0).getId().getNomatr());
		assertEquals(acti, result.get(0).getId().getActivite());
		assertEquals(2.45d, result.get(0).getNbHeures(), 0);
	}
	
	@Test
	public void exportAbsencesToPaie_2AbsencesSameDaySameAgentDiffActi_nothingInDataBase_ReturnTwoSppactForBoth() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008765);
		p1.setDateDebut(new DateTime(2013, 5, 15, 8, 45, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 15, 10, 15, 0).toDate());
		p1.setAbsenceConcertee(true);
		p1.setType(abs);
		
		Pointage p2 = new Pointage();
		p2.setIdAgent(9008765);
		p2.setDateDebut(new DateTime(2013, 5, 15, 14, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 5, 15, 15, 15, 0).toDate());
		p2.setAbsenceConcertee(false);
		p2.setType(abs);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
		Mockito.when(hS.getIntegerDateMairieFromDate(p1.getDateDebut())).thenReturn(20130515);
		Mockito.when(hS.getIntegerDateMairieFromDate(p2.getDateDebut())).thenReturn(20130515);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(0d)).thenReturn(0);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(90)).thenReturn(1.3d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(75)).thenReturn(1.15d);

		Spacti acti = new Spacti();
		Spacti acti2 = new Spacti();
		IMairieRepository mR = Mockito.mock(IMairieRepository.class);
		Mockito.when(mR.getEntity(Spacti.class, "A02")).thenReturn(acti);
		Mockito.when(mR.getEntity(Spacti.class, "A01")).thenReturn(acti2);

		IExportPaieRepository eR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(eR.getSppactForDayAndAgent(p1.getIdAgent(), p1.getDateDebut(), "A02")).thenReturn(null);
		Mockito.when(eR.getSppactForDayAndAgent(p1.getIdAgent(), p1.getDateDebut(), "A01")).thenReturn(null);
		
		ExportPaieAbsenceService service = new ExportPaieAbsenceService();
		ReflectionTestUtils.setField(service, "mairieRepository", mR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", eR);
		
		// When
		List<Sppact> result = service.exportAbsencesToPaie(Arrays.asList(p1, p2));
		
		// Then
		assertEquals(2, result.size());
		assertEquals(20130515, (int) result.get(0).getId().getDateJour());
		assertEquals(8765, (int) result.get(0).getId().getNomatr());
		assertEquals(acti, result.get(0).getId().getActivite());
		assertEquals(1.3d, result.get(0).getNbHeures(), 0);
		
		assertEquals(20130515, (int) result.get(1).getId().getDateJour());
		assertEquals(8765, (int) result.get(1).getId().getNomatr());
		assertEquals(acti2, result.get(1).getId().getActivite());
		assertEquals(1.15d, result.get(1).getNbHeures(), 0);
	}
	
	@Test
	public void exportAbsencesToPaie_1Absence_alreadyInDataBase_ReturnModifiedSppact() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008765);
		p1.setDateDebut(new DateTime(2013, 5, 15, 8, 45, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 15, 10, 15, 0).toDate());
		p1.setAbsenceConcertee(true);
		p1.setType(abs);
		
		Spacti acti = new Spacti();
		IMairieRepository mR = Mockito.mock(IMairieRepository.class);
		Mockito.when(mR.getEntity(Spacti.class, "A02")).thenReturn(acti);

		Sppact sppact = new Sppact();
		sppact.setNbHeures(3.3d);
		sppact.setId(new SppactId(8765, 20130515, acti));
		IExportPaieRepository eR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(eR.getSppactForDayAndAgent(p1.getIdAgent(), p1.getDateDebut(), "A02")).thenReturn(sppact);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
		Mockito.when(hS.getIntegerDateMairieFromDate(p1.getDateDebut())).thenReturn(20130515);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(3.3d)).thenReturn(210);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(300)).thenReturn(4.3d);
		
		ExportPaieAbsenceService service = new ExportPaieAbsenceService();
		ReflectionTestUtils.setField(service, "mairieRepository", mR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", eR);
		
		// When
		List<Sppact> result = service.exportAbsencesToPaie(Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(20130515, (int) result.get(0).getId().getDateJour());
		assertEquals(8765, (int) result.get(0).getId().getNomatr());
		assertEquals(acti, result.get(0).getId().getActivite());
		assertEquals(4.3d, result.get(0).getNbHeures(), 0);
	}
	
	@Test
	public void exportAbsencesToPaie_1Absence_NewValueIs0_DeleteExistingSppact() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setIdAgent(9008765);
		p1.setDateDebut(new DateTime(2013, 5, 15, 8, 45, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 15, 8, 45, 0).toDate());
		p1.setAbsenceConcertee(true);
		p1.setType(abs);
		
		Spacti acti = new Spacti();
		IMairieRepository mR = Mockito.mock(IMairieRepository.class);
		Mockito.when(mR.getEntity(Spacti.class, "A02")).thenReturn(acti);

		Sppact sppact = Mockito.spy(new Sppact());
		Mockito.doNothing().when(sppact).remove();
		sppact.setNbHeures(3.3d);
		sppact.setId(new SppactId(8765, 20130515, acti));
		IExportPaieRepository eR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(eR.getSppactForDayAndAgent(p1.getIdAgent(), p1.getDateDebut(), "A02")).thenReturn(sppact);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
		Mockito.when(hS.getIntegerDateMairieFromDate(p1.getDateDebut())).thenReturn(20130515);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(3.3d)).thenReturn(210);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(300)).thenReturn(4.3d);
		
		ExportPaieAbsenceService service = new ExportPaieAbsenceService();
		ReflectionTestUtils.setField(service, "mairieRepository", mR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", eR);
		
		// When
		List<Sppact> result = service.exportAbsencesToPaie(Arrays.asList(p1));
		
		// Then
		assertEquals(0, result.size());
		Mockito.verify(sppact, Mockito.times(1)).remove();
	}
}
