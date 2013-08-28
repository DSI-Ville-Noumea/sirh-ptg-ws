package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spmatr;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ExportPaieServiceTest {

	@Test
	public void exportToPaie_NoVentilDate_DoNothing() {
		
		// Given
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.CC)).thenReturn(TypeChainePaieEnum.SCV);
		
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(null);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		
		// When
		ReturnMessageDto result = service.exportToPaie(1, AgentStatutEnum.CC);
		
		// Then
		assertEquals(1, result.getInfos().size());
		assertEquals("Aucune ventilation n'existe pour le statut [CC].", result.getInfos().get(0));
		assertEquals(0, result.getErrors().size());
		Mockito.verify(vR, Mockito.times(1)).getLatestVentilDate(TypeChainePaieEnum.SCV, false);
	}
	
	//@Test
	public void exportToPaie_PaieIsNotReady_DoNothing() {
		
		// Given
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.CC)).thenReturn(TypeChainePaieEnum.SCV);
		
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(null);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		
		// When
		ReturnMessageDto result = service.exportToPaie(1, AgentStatutEnum.CC);
		
		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals(1, result.getErrors().size());
		assertEquals("Impossible de lancer le processus de déversement : La Paie est en état [] au lieu de [] ou [].", result.getErrors().get(0));
		Mockito.verify(vR, Mockito.never()).getLatestVentilDate(TypeChainePaieEnum.SCV, false);
	}
	
	@Test
	public void markPointagesAsValidated_2Pointages_AddEtatPointage() {
		
		// Given
		Pointage p1 = new Pointage();
		Pointage p2 = new Pointage();
		Date date = new DateTime(2013, 7, 8, 18, 9, 0).toDate();
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(date);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		service.markPointagesAsValidated(Arrays.asList(p1, p2), 9008799);
		
		// Then
		assertEquals(date, p1.getEtats().get(0).getEtatPointagePk().getDateEtat());
		assertEquals(9008799, (int) p1.getEtats().get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VALIDE, p1.getEtats().get(0).getEtat());
		assertEquals(date, p2.getEtats().get(0).getEtatPointagePk().getDateEtat());
		assertEquals(9008799, (int) p2.getEtats().get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VALIDE, p2.getEtats().get(0).getEtat());
	}
	
	@Test
	public void updateSpmatrForAgentAndPointages_calculEarliestPointageDate_noExistingSpmatr_saveNewSpmatr() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setDateDebut(new DateTime(2013, 5, 7, 8, 10, 0).toDate());
		Pointage p2 = new Pointage();
		p2.setDateDebut(new DateTime(2013, 4, 19, 8, 10, 0).toDate());
		Pointage p3 = new Pointage();
		p3.setDateDebut(new DateTime(2013, 4, 19, 8, 11, 0).toDate());
		
		IMairieRepository mR = Mockito.mock(IMairieRepository.class);
		Mockito.when(mR.findSpmatrForAgent(9009899)).thenReturn(null);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getIntegerDateMairieFromDate(new DateTime(2013, 4, 19, 8, 10, 0).toDate())).thenReturn(201304);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009899)).thenReturn(9899);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "mairieRepository", mR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		service.updateSpmatrForAgentAndPointages(9009899, TypeChainePaieEnum.SCV, Arrays.asList(p1, p2, p3));
		
		// Then
		Mockito.verify(mR, Mockito.times(1)).persistEntity(Mockito.any(Spmatr.class));
	}
	
	@Test
	public void updateSpmatrForAgentAndPointages_calculEarliestPointageDate_existingSpmatrWithDateAfter_updateSpmatr() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setDateDebut(new DateTime(2013, 5, 7, 8, 10, 0).toDate());
		Pointage p2 = new Pointage();
		p2.setDateDebut(new DateTime(2013, 4, 19, 8, 10, 0).toDate());
		Pointage p3 = new Pointage();
		p3.setDateDebut(new DateTime(2013, 4, 19, 8, 11, 0).toDate());
		
		Spmatr matr = new Spmatr();
		matr.setNomatr(9899);
		matr.setPerrap(201305);
		IMairieRepository mR = Mockito.mock(IMairieRepository.class);
		Mockito.when(mR.findSpmatrForAgent(9899)).thenReturn(matr);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getIntegerMonthDateMairieFromDate(p2.getDateDebut())).thenReturn(201304);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009899)).thenReturn(9899);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "mairieRepository", mR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		service.updateSpmatrForAgentAndPointages(9009899, TypeChainePaieEnum.SCV, Arrays.asList(p1, p2, p3));
		
		// Then
		Mockito.verify(mR, Mockito.never()).persistEntity(Mockito.any(Spmatr.class));
		
		assertEquals(201304, (int)matr.getPerrap());
	}
	
	@Test
	public void updateSpmatrForAgentAndPointages_calculEarliestPointageDate_existingSpmatrWithDateBefore_leaveSpmatr() {
		
		// Given
		Pointage p1 = new Pointage();
		p1.setDateDebut(new DateTime(2013, 5, 7, 8, 10, 0).toDate());
		Pointage p2 = new Pointage();
		p2.setDateDebut(new DateTime(2013, 4, 19, 8, 10, 0).toDate());
		Pointage p3 = new Pointage();
		p3.setDateDebut(new DateTime(2013, 4, 19, 8, 11, 0).toDate());
		
		Spmatr matr = new Spmatr();
		matr.setNomatr(9899);
		matr.setPerrap(201304);
		IMairieRepository mR = Mockito.mock(IMairieRepository.class);
		Mockito.when(mR.findSpmatrForAgent(9899)).thenReturn(matr);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getIntegerMonthDateMairieFromDate(p2.getDateDebut())).thenReturn(201304);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009899)).thenReturn(9899);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "mairieRepository", mR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		service.updateSpmatrForAgentAndPointages(9009899, TypeChainePaieEnum.SCV, Arrays.asList(p1, p2, p3));
		
		// Then
		Mockito.verify(mR, Mockito.never()).persistEntity(Mockito.any(Spmatr.class));
		
		assertEquals(201304, (int)matr.getPerrap());
	}
	
	@Test
	public void canStartExportPaieActionDto_callWFService() {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;
		
		IPaieWorkflowService pwfs = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(pwfs.canChangeStateToExportPaieStarted(chainePaie)).thenReturn(true);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "paieWorkflowService", pwfs);
		
		// When
		CanStartWorkflowPaieActionDto result = service.canStartExportPaieActionDto(chainePaie);
		
		// Then
		assertTrue(result.isCanStartExportPaieAction());
	}
}
