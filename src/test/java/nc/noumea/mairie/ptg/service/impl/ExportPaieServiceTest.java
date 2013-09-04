package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spmatr;
import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.domain.Spphre;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.ExportPaieTask;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IExportPaieAbsenceService;
import nc.noumea.mairie.ptg.service.IExportPaieHSupService;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;
import nc.noumea.mairie.ptg.workflow.WorkflowInvalidStateException;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class ExportPaieServiceTest {

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
	
	@Test
	public void startExportToPaie_NoVentilDate_DoNothing() {
		
		// Given
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.CC)).thenReturn(TypeChainePaieEnum.SCV);
		
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(null);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		
		// When
		ReturnMessageDto result = service.startExportToPaie(1, AgentStatutEnum.CC);
		
		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals("Aucune ventilation n'existe pour le statut [CC].", result.getErrors().get(0));
		assertEquals(1, result.getErrors().size());
		Mockito.verify(vR, Mockito.times(1)).getLatestVentilDate(TypeChainePaieEnum.SCV, false);
	}
	
	@Test
	public void startExportToPaie_PaieIsNotReady_DoNothing() throws WorkflowInvalidStateException {
		
		// Given
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.CC)).thenReturn(TypeChainePaieEnum.SCV);
		
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(new VentilDate());

		IPaieWorkflowService wfS = Mockito.mock(IPaieWorkflowService.class);
		Mockito.doThrow(new WorkflowInvalidStateException("message")).when(wfS).changeStateToExportPaieStarted(TypeChainePaieEnum.SCV);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "paieWorkflowService", wfS);
		
		// When
		ReturnMessageDto result = service.startExportToPaie(1, AgentStatutEnum.CC);
		
		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals(1, result.getErrors().size());
		assertEquals("message", result.getErrors().get(0));
	}
	
	@Test
	public void startExportToPaie_PaieIsReady_ChangeStateAndCreateExportVentilTask() throws WorkflowInvalidStateException {
		
		// Given
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.CC)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 7, 9, 10, 25, 2).toDate());
		
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		VentilDate ventilDate = new VentilDate();
		ventilDate.setIdVentilDate(999);
		ventilDate.setDateVentilation(new LocalDate(2013, 07, 01).toDate());
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(ventilDate);
		List<Integer> agentsList = Arrays.asList(9008765, 9008989);
		Mockito.when(vR.getListIdAgentsForExportPaie(999)).thenReturn(agentsList);

		IPaieWorkflowService wfS = Mockito.mock(IPaieWorkflowService.class);

		Spcarr validSpcarr = new Spcarr();
		validSpcarr.setCdcate(7);
		ISirhRepository sR = Mockito.mock(ISirhRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8765, ventilDate.getDateVentilation())).thenReturn(validSpcarr);
		Mockito.when(sR.getAgentCurrentCarriere(8989, ventilDate.getDateVentilation())).thenReturn(null);
		
		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				ExportPaieTask arg = (ExportPaieTask) args[0];

				assertEquals(9008765, (int) arg.getIdAgent());
				assertEquals(new DateTime(2013, 7, 9, 10, 25, 2).toDate(), arg.getDateCreation());
				assertEquals(9001111, (int) arg.getIdAgentCreation());
				assertEquals(TypeChainePaieEnum.SCV, arg.getTypeChainePaie());
				assertEquals(999, (int) arg.getVentilDate().getIdVentilDate());
				assertNull(arg.getDateExport());
				assertNull(arg.getTaskStatus());
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(ExportPaieTask.class));
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "paieWorkflowService", wfS);
		ReflectionTestUtils.setField(service, "sirhRepository", sR);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		
		// When
		ReturnMessageDto result = service.startExportToPaie(9001111, AgentStatutEnum.CC);
		
		// Then
		assertEquals(1, result.getInfos().size());
		assertEquals("Agent 9008765", result.getInfos().get(0));
		assertEquals(0, result.getErrors().size());
		
		Mockito.verify(wfS, Mockito.times(1)).changeStateToExportPaieStarted(TypeChainePaieEnum.SCV);
	}

	@Test
	public void processExportPaieForAgent_ExportGivenAgent() {
		
		// Given
		ExportPaieTask task = new ExportPaieTask();
		task.setIdExportPaieTask(3);
		task.setIdAgent(9008765);
		task.setIdAgentCreation(9009999);
		VentilDate ventilDate = new VentilDate();
		ventilDate.setIdVentilDate(99);
		ventilDate.setDateVentilation(new LocalDate(2013, 7, 1).toDate());
		task.setVentilDate(ventilDate);
		task.setTypeChainePaie(TypeChainePaieEnum.SCV);
		
		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.when(pR.getEntity(ExportPaieTask.class, 3)).thenReturn(task);
		
		List<Pointage> pointages = new ArrayList<Pointage>();
		IPointageService pS = Mockito.mock(IPointageService.class);
		Mockito.when(pS.getPointagesVentilesForAgent(9008765, ventilDate)).thenReturn(pointages);
		
		List<Sppact> sppacts = new ArrayList<Sppact>();
		IExportPaieAbsenceService epS = Mockito.mock(IExportPaieAbsenceService.class);
		Mockito.when(epS.exportAbsencesToPaie(pointages)).thenReturn(sppacts);
		
		List<VentilHsup> ventilHsupOrderedByDateAsc = new ArrayList<VentilHsup>();
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getListVentilHSupForAgentAndVentilDateOrderByDateAsc(9008765, ventilDate.getIdVentilDate()))
			.thenReturn(ventilHsupOrderedByDateAsc);
		
		List<Spphre> spphres = new ArrayList<Spphre>();
		IExportPaieHSupService ephS = Mockito.mock(IExportPaieHSupService.class);
		Mockito.when(ephS.exportHsupToPaie(ventilHsupOrderedByDateAsc)).thenReturn(spphres);
		
		ExportPaieService service = Mockito.spy(new ExportPaieService());
		Mockito.doNothing().when(service).markPointagesAsValidated(pointages, 9009999);
		Mockito.doNothing().when(service).updateSpmatrForAgentAndPointages(9008765, TypeChainePaieEnum.SCV, pointages);
		Mockito.doNothing().when(service).persistSppac(sppacts);
		Mockito.doNothing().when(service).persistSpphre(spphres);

		ReflectionTestUtils.setField(service, "pointageService", pS);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "exportPaieAbsenceService", epS);
		ReflectionTestUtils.setField(service, "exportPaieHSupService", ephS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		
		// When
		service.processExportPaieForAgent(3);
		
		// Then
		Mockito.verify(service, Mockito.times(1)).markPointagesAsValidated(pointages, 9009999);
		Mockito.verify(service, Mockito.times(1)).updateSpmatrForAgentAndPointages(9008765, TypeChainePaieEnum.SCV, pointages);
		Mockito.verify(service, Mockito.times(1)).persistSppac(sppacts);
		Mockito.verify(service, Mockito.times(1)).persistSpphre(spphres);
	}
	
	@Test
	public void stopExportToPaie_InvalidState_ThrowException() throws WorkflowInvalidStateException {
		
		// Given
		IPaieWorkflowService wfS = Mockito.mock(IPaieWorkflowService.class);
		Mockito.doThrow(new WorkflowInvalidStateException("message")).when(wfS).changeStateToExportPaieDone(TypeChainePaieEnum.SCV);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "paieWorkflowService", wfS);
		
		// When
		try {
			service.stopExportToPaie(TypeChainePaieEnum.SCV);
		} catch(WorkflowInvalidStateException ex) {
			return;
		}
		
		fail("Should have thrown a WorkflowInvalidStateException");
	}
	
	@Test
	public void stopExportToPaie_validState() throws WorkflowInvalidStateException {
	
		// Given
		IPaieWorkflowService wfS = Mockito.mock(IPaieWorkflowService.class);
		Mockito.doNothing().when(wfS).changeStateToExportPaieDone(TypeChainePaieEnum.SCV);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "paieWorkflowService", wfS);
		
		// When
		service.stopExportToPaie(TypeChainePaieEnum.SCV);

		// Then
		Mockito.verify(wfS, Mockito.times(1)).changeStateToExportPaieDone(TypeChainePaieEnum.SCV);
	}
}
