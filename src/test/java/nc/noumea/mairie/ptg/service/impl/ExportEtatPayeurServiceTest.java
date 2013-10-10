package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.repository.ISirhRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ExportEtatPayeurServiceTest {

	@Test
	public void canStartExportPaieAction_callWorkflowService() {
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;
		
		IPaieWorkflowService pwfs = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(pwfs.canChangeStateToExportEtatPayeurStarted(chainePaie)).thenReturn(true);
		
		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "paieWorkflowService", pwfs);
		
		// When
		CanStartWorkflowPaieActionDto result = service.canStartExportEtatPayeurAction(chainePaie);
		
		// Then
		assertTrue(result.isCanStartExportPaieAction());
	}
	
	@Test
	public void getAbsencesEtatPayeurDataForStatut_NoUnPaidVentilDate_ReturnEmptyDTO() {
		
		// Given
		VentilDate toVentilDate = null;
		AgentStatutEnum statut = AgentStatutEnum.F;
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		
		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		
		// When
		EtatPayeurDto result = service.getAbsencesEtatPayeurDataForStatut(statut);
		
		// Then
		assertEquals(0, result.getAbsences().size());
		assertNull(result.getChainePaie());
		assertNull(result.getPeriode());
		assertNull(result.getStatut());
	}
	
	@Test
	public void getAbsencesEtatPayeurDataForStatut_2Agents_ReturnFilledDTO() {
		
		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2013, 9, 1, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2013, 9, 29, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.F;
		
		VentilAbsence va1 = new VentilAbsence();
		va1.setDateLundi(new LocalDate(2013, 9, 2).toDate());
		va1.setMinutesConcertee(60);
		va1.setIdAgent(9008989);
		toVentilDate.getVentilAbsences().add(va1);
		
		VentilAbsence va2 = new VentilAbsence();
		va2.setMinutesNonConcertee(30);
		va2.setMinutesConcertee(30);
		va2.setDateLundi(new LocalDate(2013, 9, 9).toDate());
		va2.setIdAgent(9006767);
		toVentilDate.getVentilAbsences().add(va2);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.getMairieMatrFromIdAgent(9006767)).thenReturn(6767);
		Mockito.when(hS.formatMinutesToString(60)).thenReturn("60m");
		Mockito.when(hS.formatMinutesToString(30)).thenReturn("30m");
		
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		
		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		Spcarr spcarr2 = new Spcarr();
		spcarr2.setCdcate(1);
		ISirhRepository sR = Mockito.mock(ISirhRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);
		Mockito.when(sR.getAgentCurrentCarriere(6767, toVentilDate.getDateVentilation())).thenReturn(spcarr2);
		
		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "sirhRepository", sR);
		
		// When
		EtatPayeurDto result = service.getAbsencesEtatPayeurDataForStatut(statut);
		
		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(3, result.getAbsences().size());
		assertEquals(0, result.getPrimes().size());
		assertEquals(0, result.getHeuresSup().size());
	}
	
	@Test
	public void getAbsencesEtatPayeurDataForStatut_1Agents_NotRightStatus_ReturnFilledWith0ItemsDTO() {
		
		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2013, 9, 1, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2013, 9, 29, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.F;
		
		VentilAbsence va1 = new VentilAbsence();
		va1.setDateLundi(new LocalDate(2013, 9, 2).toDate());
		va1.setMinutesConcertee(60);
		va1.setIdAgent(9008989);
		toVentilDate.getVentilAbsences().add(va1);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		
		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(7);
		ISirhRepository sR = Mockito.mock(ISirhRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);
		
		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "sirhRepository", sR);
		
		// When
		EtatPayeurDto result = service.getAbsencesEtatPayeurDataForStatut(statut);
		
		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAbsences().size());
		assertEquals(0, result.getPrimes().size());
		assertEquals(0, result.getHeuresSup().size());
	}
	
	@Test
	public void getAbsencesEtatPayeurDataForStatut_1Agents1VentilAbsenceRappel_OutputDifferenceInDTO() {
		
		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2013, 9, 1, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2013, 9, 29, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.F;
		
		VentilAbsence va1 = new VentilAbsence();
		va1.setDateLundi(new LocalDate(2013, 8, 26).toDate());
		va1.setMinutesConcertee(60);
		va1.setIdAgent(9008989);
		toVentilDate.getVentilAbsences().add(va1);
		
		VentilAbsence vaOld = new VentilAbsence();
		vaOld.setDateLundi(new LocalDate(2013, 8, 26).toDate());
		vaOld.setMinutesNonConcertee(30);
		vaOld.setMinutesConcertee(30);
		vaOld.setIdAgent(9008989);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.formatMinutesToString(-30)).thenReturn("- 30m");
		Mockito.when(hS.formatMinutesToString(30)).thenReturn("30m");
		
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getPriorVentilAbsenceForAgentAndDate(va1.getIdAgent(), va1.getDateLundi(), va1)).thenReturn(vaOld);
		
		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		ISirhRepository sR = Mockito.mock(ISirhRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);
		
		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "sirhRepository", sR);
		
		// When
		EtatPayeurDto result = service.getAbsencesEtatPayeurDataForStatut(statut);
		
		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(2, result.getAbsences().size());
		assertEquals("30m", result.getAbsences().get(0).getQuantite());
		assertEquals("- 30m", result.getAbsences().get(1).getQuantite());
		assertEquals(0, result.getPrimes().size());
		assertEquals(0, result.getHeuresSup().size());
	}
}
