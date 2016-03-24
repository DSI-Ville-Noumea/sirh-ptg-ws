package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.ExportEtatsPayeurTask;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.ReposCompTask;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.AbstractItemEtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.reporting.EtatPayeurReporting;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;
import nc.noumea.mairie.ptg.workflow.WorkflowInvalidStateException;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.IBirtEtatsPayeurWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class ExportEtatPayeurServiceTest {

	@Test
	public void canStartExportPaieAction_callWorkflowService() {
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;

		IPaieWorkflowService pwfs = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(pwfs.canChangeStateToExportEtatsPayeurStarted(chainePaie)).thenReturn(true);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "paieWorkflowService", pwfs);

		// When
		CanStartWorkflowPaieActionDto result = service.canStartExportEtatPayeurAction(chainePaie);

		// Then
		assertTrue(result.isCanStartAction());
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
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals(0, result.getAgents().size());
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

		VentilAbsence va2 = new VentilAbsence();
		va2.setMinutesNonConcertee(30);
		va2.setMinutesConcertee(30);
		va2.setDateLundi(new LocalDate(2013, 9, 9).toDate());
		va2.setIdAgent(9006767);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.getMairieMatrFromIdAgent(9006767)).thenReturn(6767);
		Mockito.when(hS.formatMinutesToString(60)).thenReturn("60m");
		Mockito.when(hS.formatMinutesToString(30)).thenReturn("30m");
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013,9,8,0,0,0).toDate());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilAbsenceWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9008989))
				.thenReturn(Arrays.asList(va1));
		Mockito.when(vR.getListOfVentilAbsenceWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9006767))
				.thenReturn(Arrays.asList(va2));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(9008989, 9006767));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		Spcarr spcarr2 = new Spcarr();
		spcarr2.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);
		Mockito.when(sR.getAgentCurrentCarriere(6767, toVentilDate.getDateVentilation())).thenReturn(spcarr2);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service)
				.fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class), Mockito.any(Integer.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(2, result.getAgents().size());

		Mockito.verify(service, Mockito.times(2)).fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class),
				Mockito.any(Integer.class));
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

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013,9,8,0,0,0).toDate());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilAbsenceWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9008989))
				.thenReturn(Arrays.asList(va1));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(9008989));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(7);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAgents().size());
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
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013,9,8,0,0,0).toDate());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getPriorVentilAbsenceForAgentAndDate(va1.getIdAgent(), va1.getDateLundi(), va1)).thenReturn(
				vaOld);
		Mockito.when(vR.getListOfVentilAbsenceWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9008989))
				.thenReturn(Arrays.asList(va1));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(9008989));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service)
				.fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class), Mockito.any(Integer.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(1, result.getAgents().size());
		assertEquals("", result.getAgents().get(0).getAbsences().getQuantiteInf1Heure());

		Mockito.verify(service, Mockito.times(1)).fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class),
				Mockito.any(Integer.class));
	}

	// bug en PROD #17035
	@Test
	public void getAbsencesEtatPayeurDataForStatut_1Agents4VentilAbsence() {

		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2015, 7, 5, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2015, 5, 31, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.CC;

		VentilAbsence va1 = new VentilAbsence();
		va1.setDateLundi(new LocalDate(2015, 6, 1).toDate());
		va1.setMinutesConcertee(2100);
		va1.setIdAgent(9003315);
		va1.setNombreAbsenceInferieur1(0);
		va1.setNombreAbsenceEntre1Et4(1);
		va1.setNombreAbsenceSuperieur1(4);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2015, 6, 8).toDate());
		va2.setMinutesConcertee(2100);
		va2.setIdAgent(9003315);
		va2.setNombreAbsenceInferieur1(0);
		va2.setNombreAbsenceEntre1Et4(1);
		va2.setNombreAbsenceSuperieur1(4);

		VentilAbsence va3 = new VentilAbsence();
		va3.setDateLundi(new LocalDate(2015, 6, 15).toDate());
		va3.setMinutesConcertee(2100);
		va3.setIdAgent(9003315);
		va3.setNombreAbsenceInferieur1(0);
		va3.setNombreAbsenceEntre1Et4(1);
		va3.setNombreAbsenceSuperieur1(4);

		VentilAbsence va4 = new VentilAbsence();
		va4.setDateLundi(new LocalDate(2015, 6, 22).toDate());
		va4.setMinutesConcertee(2100);
		va4.setIdAgent(9003315);
		va4.setNombreAbsenceInferieur1(0);
		va4.setNombreAbsenceEntre1Et4(0);
		va4.setNombreAbsenceSuperieur1(1);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9003315)).thenReturn(3315);
		Mockito.when(hS.formatMinutesToString(-30)).thenReturn("- 30m");
		Mockito.when(hS.formatMinutesToString(30)).thenReturn("30m");
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2015,7,8,0,0,0).toDate());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getPriorVentilAbsenceForAgentAndDate(va1.getIdAgent(), va1.getDateLundi(), va1)).thenReturn(
				null);
		Mockito.when(vR.getListOfVentilAbsenceWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9003315))
				.thenReturn(Arrays.asList(va1, va2, va3, va4));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(9003315));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(7);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(3315, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service)
				.fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class), Mockito.any(Integer.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("juillet 2015", result.getPeriode());
		assertEquals("CC", result.getStatut());

		assertEquals(1, result.getAgents().size());
		assertEquals("", result.getAgents().get(0).getAbsences().getQuantiteInf1Heure());
		assertEquals("3", result.getAgents().get(0).getAbsences().getQuantiteEntre1HeureEt4Heure());
		assertEquals("13", result.getAgents().get(0).getAbsences().getQuantiteSup4Heure());

		Mockito.verify(service, Mockito.times(1)).fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class),
				Mockito.any(Integer.class));
	}

	@Test
	public void getHeuresSupEtatPayeurDataForStatut_2Agents_ReturnEmptyDTO() {

		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2013, 9, 1, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2013, 9, 29, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.F;

		VentilHsup vh1 = new VentilHsup();
		vh1.setDateLundi(new LocalDate(2013, 9, 2).toDate());
		vh1.setIdAgent(9008989);

		VentilHsup vh2 = new VentilHsup();
		vh2.setDateLundi(new LocalDate(2013, 9, 9).toDate());
		vh2.setIdAgent(9006767);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.getMairieMatrFromIdAgent(9006767)).thenReturn(6767);
		Mockito.when(hS.formatMinutesToString(60)).thenReturn("60m");
		Mockito.when(hS.formatMinutesToString(30)).thenReturn("30m");
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013,9,8,0,0,0).toDate());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9008989))
				.thenReturn(Arrays.asList(vh1));
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9006767))
				.thenReturn(Arrays.asList(vh2));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(9008989, 9006767));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		Spcarr spcarr2 = new Spcarr();
		spcarr2.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);
		Mockito.when(sR.getAgentCurrentCarriere(6767, toVentilDate.getDateVentilation())).thenReturn(spcarr2);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service)
				.fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class), Mockito.any(Integer.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAgents().size());

		Mockito.verify(service, Mockito.times(2)).fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class),
				Mockito.any(Integer.class));
	}

	@Test
	public void getHeuresSupEtatPayeurDataForStatut_1Agents_NotRightStatus_ReturnFilledWith0ItemsDTO() {

		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2013, 9, 1, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2013, 9, 29, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.F;

		VentilHsup vh1 = new VentilHsup();
		vh1.setDateLundi(new LocalDate(2013, 9, 2).toDate());
		vh1.setIdAgent(9008989);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013,9,8,0,0,0).toDate());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9008989))
				.thenReturn(Arrays.asList(vh1));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(9008989));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(7);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAgents().size());
	}

	@Test
	public void getHeuresSupEtatPayeurDataForStatut_1Agents1VentilHSupRappel_OutputDifferenceInDTO() {

		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2013, 9, 1, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2013, 9, 29, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.F;

		VentilHsup vh1 = new VentilHsup();
		vh1.setDateLundi(new LocalDate(2013, 8, 26).toDate());
		vh1.setIdAgent(9008989);
		vh1.setMHorsContrat(60);
		vh1.setMMai(60);

		VentilHsup vhOld = new VentilHsup();
		vhOld.setDateLundi(new LocalDate(2013, 8, 26).toDate());
		vhOld.setMMai(30);
		vhOld.setMNormales(30);
		vhOld.setIdAgent(9008989);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.formatMinutesToString(-30)).thenReturn("- 30m");
		Mockito.when(hS.formatMinutesToString(30)).thenReturn("30m");
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013,9,8,0,0,0).toDate());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getPriorVentilHSupAgentAndDate(vh1.getIdAgent(), vh1.getDateLundi(), vh1)).thenReturn(vhOld);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9008989))
				.thenReturn(Arrays.asList(vh1));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(9008989));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service)
				.fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class), Mockito.any(Integer.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(1, result.getAgents().size());
		assertEquals("- 30m", result.getAgents().get(0).getHeuresSup().getNormales());
		assertEquals("30m", result.getAgents().get(0).getHeuresSup().getH1Mai());

		Mockito.verify(service, Mockito.times(1)).fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class),
				Mockito.any(Integer.class));
	}

	@Test
	public void getPrimesEtatPayeurDataForStatut_2Agents_ReturnFilledDTO() {

		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2013, 9, 1, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2013, 9, 29, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.F;

		VentilPrime vp1 = new VentilPrime();
		vp1.setDateDebutMois(new LocalDate(2013, 9, 2).toDate());
		vp1.setIdAgent(9008989);
		vp1.setRefPrime(new RefPrime());
		vp1.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		vp1.setQuantite(12);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 9, 2).toDate());
		vp2.setIdAgent(9006767);
		vp2.setRefPrime(new RefPrime());
		vp2.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		vp2.setQuantite(24);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.getMairieMatrFromIdAgent(9006767)).thenReturn(6767);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013,9,8,0,0,0).toDate());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilPrimeWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9008989)).thenReturn(
				Arrays.asList(vp1));
		Mockito.when(vR.getListOfVentilPrimeWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9006767)).thenReturn(
				Arrays.asList(vp2));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(9008989, 9006767));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		Spcarr spcarr2 = new Spcarr();
		spcarr2.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);
		Mockito.when(sR.getAgentCurrentCarriere(6767, toVentilDate.getDateVentilation())).thenReturn(spcarr2);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service)
				.fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class), Mockito.any(Integer.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(2, result.getAgents().size());

		Mockito.verify(service, Mockito.times(2)).fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class),
				Mockito.any(Integer.class));
	}

	@Test
	public void getPrimesEtatPayeurDataForStatut_1Agents_NotRightStatus_ReturnFilledWith0ItemsDTO() {

		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2013, 9, 1, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2013, 9, 29, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.F;

		VentilPrime vp1 = new VentilPrime();
		vp1.setDateDebutMois(new LocalDate(2013, 9, 2).toDate());
		vp1.setIdAgent(9008989);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013,9,8,0,0,0).toDate());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilPrimeWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9008989)).thenReturn(
				Arrays.asList(vp1));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(9008989));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(7);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAgents().size());
	}

	@Test
	public void getPrimesEtatPayeurDataForStatut_1Agents1VentilHSupRappel_OutputDifferenceInDTO() {

		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2013, 9, 1, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2013, 9, 29, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.F;

		VentilPrime vp1 = new VentilPrime();
		vp1.setDateDebutMois(new LocalDate(2013, 8, 26).toDate());
		vp1.setQuantite(23);
		vp1.setIdAgent(9008989);
		vp1.setRefPrime(new RefPrime());
		vp1.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		vp1.getRefPrime().setLibelle("libelle");

		VentilPrime vpOld = new VentilPrime();
		vpOld.setDateDebutMois(new LocalDate(2013, 8, 26).toDate());
		vpOld.setQuantite(32);
		vpOld.setRefPrime(new RefPrime());
		vpOld.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		vpOld.setIdAgent(9008989);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2016,3,8,0,0,0).toDate());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getPriorVentilPrimeForAgentAndDate(vp1.getIdAgent(), vp1.getDateDebutMois(), vp1)).thenReturn(
				vpOld);
		Mockito.when(vR.getListOfVentilPrimeWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9008989)).thenReturn(
				Arrays.asList(vp1));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(9008989));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service)
				.fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class), Mockito.any(Integer.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("mars 2016", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(1, result.getAgents().size());
		assertEquals("libelle", result.getAgents().get(0).getPrimes().get(0).getType());
		assertEquals("-9", result.getAgents().get(0).getPrimes().get(0).getQuantite());

		Mockito.verify(service, Mockito.times(1)).fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class),
				Mockito.any(Integer.class));
	}

	// #18592 supprimer les lignes a 0
	@Test
	public void getPrimesEtatPayeurDataForStatut_1Agents1PirmeRappel_OutputDifferenceInDTOEquals0() {

		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2013, 9, 1, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2013, 9, 29, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.F;

		VentilPrime vp1 = new VentilPrime();
		vp1.setDateDebutMois(new LocalDate(2013, 8, 26).toDate());
		vp1.setQuantite(23);
		vp1.setIdAgent(9008989);
		vp1.setRefPrime(new RefPrime());
		vp1.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		vp1.getRefPrime().setLibelle("libelle");

		VentilPrime vpOld = new VentilPrime();
		vpOld.setDateDebutMois(new LocalDate(2013, 8, 26).toDate());
		vpOld.setQuantite(23);
		vpOld.setRefPrime(new RefPrime());
		vpOld.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		vpOld.setIdAgent(9008989);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013,9,8,0,0,0).toDate());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getPriorVentilPrimeForAgentAndDate(vp1.getIdAgent(), vp1.getDateDebutMois(), vp1)).thenReturn(
				vpOld);
		Mockito.when(vR.getListOfVentilPrimeWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), 9008989)).thenReturn(
				Arrays.asList(vp1));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(9008989));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service)
				.fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class), Mockito.any(Integer.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAgents().size());

		Mockito.verify(service, Mockito.times(1)).fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class),
				Mockito.any(Integer.class));
	}

	@Test
	public void fillAgentsData_RetrieveApprobateurAndFillDataFromSirhRepo() {

		// Given
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9008888);
		AbstractItemEtatPayeurDto va = new AbstractItemEtatPayeurDto();
		va.setAgent(agent);

		va.getAbsences().setQuantiteEntre1HeureEt4Heure("2h");

		AgentGeneriqueDto ag1 = new AgentGeneriqueDto();
		ag1.setNomUsage("nomusage1");
		ag1.setPrenomUsage("prenomusage1");

		Date currentDate = new LocalDate(2013, 7, 8).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setNom("nomusage2");
		agDto.setPrenom("prenomusage2");
		agDto.setIdServiceADS(55);
		agDto.setService("Label");
		agDto.setSigleService("Sigle");
		ISirhWSConsumer swc = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(swc.getAgentService(9008888, currentDate)).thenReturn(agDto);
		Mockito.when(swc.getAgent(9008888)).thenReturn(ag1);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		ReflectionTestUtils.setField(service, "sirhWsConsumer", swc);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		service.fillAgentsData(va, 9008888);

		// Then
		assertEquals(9008888, (int) va.getAgent().getIdAgent());
		assertEquals("2h", va.getAbsences().getQuantiteEntre1HeureEt4Heure());
		assertEquals("nomusage1", va.getAgent().getNom());
		assertEquals("prenomusage1", va.getAgent().getPrenom());
		assertEquals("Sigle", va.getAgent().getSigleService());

	}

	@Test
	public void exportEtatPayeur_CallBirtWsConsumerAndReturnFilledInEtatPayeurObject() throws Exception {

		// Given
		AgentStatutEnum statut = AgentStatutEnum.F;
		Integer idAgent = 9008765;
		Date date = new LocalDate(2013, 02, 25).toDate();
		IBirtEtatsPayeurWsConsumer wsC = Mockito.mock(IBirtEtatsPayeurWsConsumer.class);

		RefTypePointage rp = new RefTypePointage();
		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.when(pR.getEntity(RefTypePointage.class, RefTypePointageEnum.H_SUP.getValue())).thenReturn(rp);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 9, 2, 8, 7, 45).toDate());

		EtatPayeurReporting etatPayeurReport = Mockito.mock(EtatPayeurReporting.class);
		
		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "birtEtatsPayeurWsConsumer", wsC);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "etatPayeurReport", etatPayeurReport);

		// When
		EtatPayeur result = service.exportEtatPayeur(idAgent, statut, date);

		// Then
		// #19036 
		assertEquals("2013-09-F", result.getLabel());
		assertEquals("2013-09-F.pdf", result.getFichier());
		assertEquals(statut, result.getStatut());
		assertEquals(new LocalDate(2013, 2, 1).toDate(), result.getDateEtatPayeur());
		assertEquals(idAgent, result.getIdAgent());
		assertEquals(new DateTime(2013, 9, 2, 8, 7, 45).toDate(), result.getDateEdition());

		//Mockito.verify(wsC, Mockito.times(1)).downloadEtatPayeurByStatut(statut.toString(), "2013-02-F.pdf");
	}

	@Test
	public void callBirtEtatsPayeurForChainePaie_Call3ReportsForSCV_Return3EtatPayeur() throws Exception {

		// Given
		Integer idAgentExporting = 9008987;
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;
		AgentStatutEnum statut = AgentStatutEnum.CC;
		Date ventilationDate = new LocalDate(2013, 02, 25).toDate();

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doReturn(new EtatPayeur()).when(service).exportEtatPayeur(idAgentExporting, statut, ventilationDate);

		// When
		List<EtatPayeur> result = service.callBirtEtatsPayeurForChainePaie(idAgentExporting, chainePaie,
				ventilationDate);

		// Then
		assertEquals(1, result.size());

		Mockito.verify(service, Mockito.times(1)).exportEtatPayeur(idAgentExporting, statut, ventilationDate);
	}

	@Test
	public void callBirtEtatsPayeurForChainePaie_Call6ReportsForSHC_Return6EtatPayeur() throws Exception {

		// Given
		Integer idAgentExporting = 9008987;
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		AgentStatutEnum statut = AgentStatutEnum.C;
		Date ventilationDate = new LocalDate(2013, 02, 25).toDate();

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doReturn(new EtatPayeur()).when(service)
				.exportEtatPayeur(idAgentExporting, AgentStatutEnum.F, ventilationDate);
		Mockito.doReturn(new EtatPayeur()).when(service)
				.exportEtatPayeur(idAgentExporting, AgentStatutEnum.C, ventilationDate);

		// When
		List<EtatPayeur> result = service.callBirtEtatsPayeurForChainePaie(idAgentExporting, chainePaie,
				ventilationDate);

		// Then
		assertEquals(2, result.size());

		Mockito.verify(service, Mockito.times(1)).exportEtatPayeur(idAgentExporting, statut, ventilationDate);
		Mockito.verify(service, Mockito.times(1))
				.exportEtatPayeur(idAgentExporting, AgentStatutEnum.F, ventilationDate);
	}

	@Test
	public void markPointagesAsJournalises_AddJournaliseStateToPointages() {

		// Given
		Pointage p1 = new Pointage();
		p1.getEtats().add(new EtatPointage());
		p1.getEtats().get(0).setEtat(EtatPointageEnum.VALIDE);

		Pointage p2 = new Pointage();
		p2.getEtats().add(new EtatPointage());
		p2.getEtats().get(0).setEtat(EtatPointageEnum.JOURNALISE);

		Integer idAgent = 9008765;

		Date date = new LocalDate(2013, 9, 2).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(date);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		service.markPointagesAsJournalises(new HashSet<Pointage>(Arrays.asList(p1, p2)), idAgent);

		// Then
		assertEquals(2, p1.getEtats().size());
		assertEquals(idAgent, p1.getEtats().get(1).getIdAgent());
		assertEquals(EtatPointageEnum.JOURNALISE, p1.getEtats().get(1).getEtat());
		assertEquals(date, p1.getEtats().get(1).getDateEtat());
		assertEquals(date, p1.getEtats().get(1).getDateMaj());
		assertEquals(1, p2.getEtats().size());
	}

	@Test
	public void markPointagesCalculesAsJournalises_AddJournaliseStateToPointages() {

		// Given
		PointageCalcule p1 = new PointageCalcule();
		p1.setEtat(EtatPointageEnum.VALIDE);

		PointageCalcule p2 = new PointageCalcule();
		p2.setEtat(EtatPointageEnum.JOURNALISE);

		ExportEtatPayeurService service = new ExportEtatPayeurService();

		// When
		service.markPointagesCalculesAsJournalises(new HashSet<PointageCalcule>(Arrays.asList(p1, p2)));

		// Then
		assertEquals(EtatPointageEnum.JOURNALISE, p1.getEtat());
		assertEquals(EtatPointageEnum.JOURNALISE, p2.getEtat());
	}


	// #17538
	@Test
	public void exportEtatsPayeur_CallEtatPayeursAndNotSendRecupsToAbs() throws WorkflowInvalidStateException {

		// Given
		Integer idAgentExporting = 9008987;
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		AgentStatutEnum statut = AgentStatutEnum.C;
		Date ventilationDate = new LocalDate(2013, 02, 25).toDate();

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(ventilationDate);
		VentilHsup vh = new VentilHsup();
		VentilHsup vh2 = new VentilHsup();
		vh2.setMRecuperees(90);
		vh2.setIdAgent(9009999);
		vh2.setDateLundi(new LocalDate(2013, 9, 2).toDate());

		ExportEtatsPayeurTask task = new ExportEtatsPayeurTask();
		task.setIdAgent(idAgentExporting);
		task.setTypeChainePaie(chainePaie);
		task.setVentilDate(vd);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(chainePaie);
		Mockito.when(hS.getMairieMatrFromIdAgent(vh2.getIdAgent())).thenReturn(9999);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(vd);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(vd.getIdVentilDate(), null)).thenReturn(
				Arrays.asList(vh, vh2));

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.when(pR.getEntity(ExportEtatsPayeurTask.class, 99)).thenReturn(task);

		IAbsWsConsumer ac = Mockito.mock(IAbsWsConsumer.class);

		List<EtatPayeur> eps = new ArrayList<EtatPayeur>();
		EtatPayeur ep = Mockito.spy(new EtatPayeur());
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(EtatPayeur.class));
		eps.add(ep);

		Spcarr spcarr = new Spcarr();
		spcarr.setCdcate(4);
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(9999, vh2.getDateLundi())).thenReturn(spcarr);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doReturn(eps).when(service)
				.callBirtEtatsPayeurForChainePaie(idAgentExporting, chainePaie, ventilationDate);
		ReflectionTestUtils.setField(service, "absWsConsumer", ac);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		service.exportEtatsPayeur(99);

		// Then
		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(EtatPayeur.class));
		Mockito.verify(ac, Mockito.never()).addRecuperationsToAgent(9009999, vh2.getDateLundi(), 90);
	}

	@Test
	public void exportEtatsPayeur_CallEtatPayeursAndCreateRepoCompTasks_Contractuel()
			throws WorkflowInvalidStateException {

		// Given
		Integer idAgentExporting = 9008987;
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		AgentStatutEnum statut = AgentStatutEnum.C;
		Date ventilationDate = new LocalDate(2013, 02, 25).toDate();

		final VentilDate vd = new VentilDate();
		vd.setDateVentilation(ventilationDate);
		VentilHsup vh = new VentilHsup();
		VentilHsup vh2 = new VentilHsup();
		vh2.setMSup(180);
		vh2.setIdAgent(9009999);
		vh2.setDateLundi(new LocalDate(2013, 9, 2).toDate());

		ExportEtatsPayeurTask task = new ExportEtatsPayeurTask();
		task.setIdAgent(idAgentExporting);
		task.setTypeChainePaie(chainePaie);
		task.setVentilDate(vd);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(chainePaie);
		final Date currentDate = new LocalDate(2013, 01, 13).toDate();
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009999)).thenReturn(9999);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(vd);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(vd.getIdVentilDate(), null)).thenReturn(
				Arrays.asList(vh, vh2));

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.when(pR.getEntity(ExportEtatsPayeurTask.class, 99)).thenReturn(task);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				ReposCompTask arg = (ReposCompTask) args[0];

				assertEquals(9009999, (int) arg.getIdAgent());
				assertEquals(currentDate, arg.getDateCreation());
				assertEquals(vd, arg.getVentilDate());
				assertNull(arg.getDateCalcul());
				assertNull(arg.getTaskStatus());
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(ReposCompTask.class));

		List<EtatPayeur> eps = new ArrayList<EtatPayeur>();
		EtatPayeur ep = Mockito.spy(new EtatPayeur());
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(EtatPayeur.class));
		eps.add(ep);

		Spcarr spcarr = new Spcarr();
		spcarr.setCdcate(4);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(9999, vd.getDateVentilation())).thenReturn(spcarr);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doReturn(eps).when(service)
				.callBirtEtatsPayeurForChainePaie(idAgentExporting, chainePaie, ventilationDate);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		service.exportEtatsPayeur(99);

		// Then
		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(EtatPayeur.class));
		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(ReposCompTask.class));
	}

	@Test
	public void exportEtatsPayeur_CallEtatPayeursAndCreateRepoCompTasks_ConventionCollective()
			throws WorkflowInvalidStateException {

		// Given
		Integer idAgentExporting = 9008987;
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		AgentStatutEnum statut = AgentStatutEnum.C;
		Date ventilationDate = new LocalDate(2013, 02, 25).toDate();

		final VentilDate vd = new VentilDate();
		vd.setDateVentilation(ventilationDate);
		VentilHsup vh = new VentilHsup();
		VentilHsup vh2 = new VentilHsup();
		vh2.setMSup(180);
		vh2.setIdAgent(9009999);
		vh2.setDateLundi(new LocalDate(2013, 9, 2).toDate());

		ExportEtatsPayeurTask task = new ExportEtatsPayeurTask();
		task.setIdAgent(idAgentExporting);
		task.setTypeChainePaie(chainePaie);
		task.setVentilDate(vd);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(chainePaie);
		final Date currentDate = new LocalDate(2013, 01, 13).toDate();
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009999)).thenReturn(9999);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(vd);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(vd.getIdVentilDate(), null)).thenReturn(
				Arrays.asList(vh, vh2));

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.when(pR.getEntity(ExportEtatsPayeurTask.class, 99)).thenReturn(task);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				ReposCompTask arg = (ReposCompTask) args[0];

				assertEquals(9009999, (int) arg.getIdAgent());
				assertEquals(currentDate, arg.getDateCreation());
				assertEquals(vd, arg.getVentilDate());
				assertNull(arg.getDateCalcul());
				assertNull(arg.getTaskStatus());
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(ReposCompTask.class));

		List<EtatPayeur> eps = new ArrayList<EtatPayeur>();
		EtatPayeur ep = Mockito.spy(new EtatPayeur());
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(EtatPayeur.class));
		eps.add(ep);

		Spcarr spcarr = new Spcarr();
		spcarr.setCdcate(7);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(9999, vd.getDateVentilation())).thenReturn(spcarr);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doReturn(eps).when(service)
				.callBirtEtatsPayeurForChainePaie(idAgentExporting, chainePaie, ventilationDate);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		service.exportEtatsPayeur(99);

		// Then
		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(EtatPayeur.class));
		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(ReposCompTask.class));
	}

	@Test
	public void exportEtatsPayeur_CallEtatPayeursAndCreateRepoCompTasks_Fonctionnaire()
			throws WorkflowInvalidStateException {

		// Given
		Integer idAgentExporting = 9008987;
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		AgentStatutEnum statut = AgentStatutEnum.C;
		Date ventilationDate = new LocalDate(2013, 02, 25).toDate();

		final VentilDate vd = new VentilDate();
		vd.setDateVentilation(ventilationDate);
		VentilHsup vh = new VentilHsup();
		VentilHsup vh2 = new VentilHsup();
		vh2.setMSup(180);
		vh2.setIdAgent(9009999);
		vh2.setDateLundi(new LocalDate(2013, 9, 2).toDate());

		ExportEtatsPayeurTask task = new ExportEtatsPayeurTask();
		task.setIdAgent(idAgentExporting);
		task.setTypeChainePaie(chainePaie);
		task.setVentilDate(vd);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(chainePaie);
		final Date currentDate = new LocalDate(2013, 01, 13).toDate();
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009999)).thenReturn(9999);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(vd);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(vd.getIdVentilDate(), 9009999)).thenReturn(
				Arrays.asList(vh, vh2));
		Mockito.when(vR.getListOfAgentWithDateForEtatPayeur(vd.getIdVentilDate())).thenReturn(Arrays.asList(9009999));

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.when(pR.getEntity(ExportEtatsPayeurTask.class, 99)).thenReturn(task);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				ReposCompTask arg = (ReposCompTask) args[0];

				assertEquals(9009999, (int) arg.getIdAgent());
				assertEquals(currentDate, arg.getDateCreation());
				assertEquals(vd, arg.getVentilDate());
				assertNull(arg.getDateCalcul());
				assertNull(arg.getTaskStatus());
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(ReposCompTask.class));

		List<EtatPayeur> eps = new ArrayList<EtatPayeur>();
		EtatPayeur ep = Mockito.spy(new EtatPayeur());
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(EtatPayeur.class));
		eps.add(ep);

		Spcarr spcarr = new Spcarr();
		spcarr.setCdcate(20);

		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(9999, vd.getDateVentilation())).thenReturn(spcarr);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doReturn(eps).when(service)
				.callBirtEtatsPayeurForChainePaie(idAgentExporting, chainePaie, ventilationDate);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		service.exportEtatsPayeur(99);

		// Then
		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(EtatPayeur.class));
		Mockito.verify(pR, Mockito.times(0)).persisEntity(Mockito.isA(ReposCompTask.class));
	}

	@Test
	public void exportEtatsPayeur_NoExistingTask_Return() {

		// Given
		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.when(pR.getEntity(ExportEtatsPayeurTask.class, 99)).thenReturn(null);

		IAbsWsConsumer ac = Mockito.mock(IAbsWsConsumer.class);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		ReflectionTestUtils.setField(service, "absWsConsumer", ac);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);

		// When
		service.exportEtatsPayeur(99);

		// Then
		Mockito.verify(ac, Mockito.never()).addRecuperationsToAgent(9009999, null, 90);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void stopExportEtatsPayeur_markPointagesAsJournalise_SetPayeAsTrue_changeWFState()
			throws WorkflowInvalidStateException {

		// Given
		VentilDate vd = new VentilDate();
		VentilHsup vh2 = new VentilHsup();
		vh2.setMRecuperees(90);
		vh2.setIdAgent(9009999);
		vh2.setDateLundi(new LocalDate(2013, 9, 2).toDate());

		ExportEtatsPayeurTask task = new ExportEtatsPayeurTask();
		task.setIdAgent(9009999);
		task.setVentilDate(vd);
		task.setTypeChainePaie(TypeChainePaieEnum.SCV);

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.when(pR.getEntity(ExportEtatsPayeurTask.class, 99)).thenReturn(task);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service).markPointagesAsJournalises(Mockito.anySet(), Mockito.anyInt());
		Mockito.doNothing().when(service).markPointagesCalculesAsJournalises(Mockito.anySet());
		ReflectionTestUtils.setField(service, "pointageRepository", pR);

		// When
		service.journalizeEtatsPayeur(99);

		// Then
		assertTrue(vd.isPaye());

		Mockito.verify(service, Mockito.times(1)).markPointagesAsJournalises(Mockito.anySet(), Mockito.anyInt());
		Mockito.verify(service, Mockito.times(1)).markPointagesCalculesAsJournalises(Mockito.anySet());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void stopExportEtatsPayeur_NoExistingTask_Return() throws WorkflowInvalidStateException {

		// Given
		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.when(pR.getEntity(ExportEtatsPayeurTask.class, 99)).thenReturn(null);

		IPaieWorkflowService pS = Mockito.mock(IPaieWorkflowService.class);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "paieWorkflowService", pS);

		// When
		service.journalizeEtatsPayeur(99);

		// Then
		Mockito.verify(pS, Mockito.never()).changeStateToExportEtatsPayeurDone(TypeChainePaieEnum.SCV);
		Mockito.verify(service, Mockito.never()).markPointagesAsJournalises(Mockito.anySet(), Mockito.anyInt());
		Mockito.verify(service, Mockito.never()).markPointagesCalculesAsJournalises(Mockito.anySet());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void stopExportEtatsPayeur_ExistingTaskVentilDAteIsAlreadyPaid_Return() throws WorkflowInvalidStateException {

		// Given
		VentilDate vd = new VentilDate();
		vd.setPaye(true);
		ExportEtatsPayeurTask task = new ExportEtatsPayeurTask();
		task.setVentilDate(vd);

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.when(pR.getEntity(ExportEtatsPayeurTask.class, 99)).thenReturn(task);

		IPaieWorkflowService pS = Mockito.mock(IPaieWorkflowService.class);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "paieWorkflowService", pS);

		// When
		service.journalizeEtatsPayeur(99);

		// Then
		Mockito.verify(pS, Mockito.never()).changeStateToExportEtatsPayeurDone(TypeChainePaieEnum.SCV);
		Mockito.verify(service, Mockito.never()).markPointagesAsJournalises(Mockito.anySet(), Mockito.anyInt());
		Mockito.verify(service, Mockito.never()).markPointagesCalculesAsJournalises(Mockito.anySet());

	}

	@Test
	public void startExportEtatsPayeur_NoUnpaidVentilation_ReturnErrorMessage() {

		// Given
		Integer idAgentExporting = 9007654;
		AgentStatutEnum statut = AgentStatutEnum.C;
		Date date = new DateTime(2013, 4, 5, 8, 7, 9).toDate();

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SHC);
		Mockito.when(hS.getCurrentDate()).thenReturn(date);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SHC, false)).thenReturn(null);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		ReturnMessageDto result = service.startExportEtatsPayeur(idAgentExporting, statut);

		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals(1, result.getErrors().size());
		assertEquals("Aucune ventilation n'existe pour le statut [C].", result.getErrors().get(0));
	}

	@Test
	public void startExportEtatsPayeur_1VentilDateButStatusIsNotOK_ReturnErrorMessage()
			throws WorkflowInvalidStateException {

		// Given
		Integer idAgentExporting = 9007654;
		AgentStatutEnum statut = AgentStatutEnum.C;
		Date date = new DateTime(2013, 4, 5, 8, 7, 9).toDate();

		VentilDate vd = new VentilDate();

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SHC);
		Mockito.when(hS.getCurrentDate()).thenReturn(date);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SHC, false)).thenReturn(vd);

		IPaieWorkflowService wS = Mockito.mock(IPaieWorkflowService.class);
		Mockito.doThrow(new WorkflowInvalidStateException("message")).when(wS)
				.changeStateToExportEtatsPayeurStarted(TypeChainePaieEnum.SHC);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "paieWorkflowService", wS);

		// When
		ReturnMessageDto result = service.startExportEtatsPayeur(idAgentExporting, statut);

		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals(1, result.getErrors().size());
		assertEquals("message", result.getErrors().get(0));
	}

	@Test
	public void startExportEtatsPayeur_StartExport_ReturnErrorMessage() throws WorkflowInvalidStateException {

		// Given
		Integer idAgentExporting = 9007654;
		AgentStatutEnum statut = AgentStatutEnum.C;
		final Date date = new DateTime(2013, 4, 5, 8, 7, 9).toDate();

		final VentilDate vd = new VentilDate();

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SHC);
		Mockito.when(hS.getCurrentDate()).thenReturn(date);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SHC, false)).thenReturn(vd);

		IPaieWorkflowService wS = Mockito.mock(IPaieWorkflowService.class);

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.doAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				ExportEtatsPayeurTask arg = (ExportEtatsPayeurTask) args[0];

				assertEquals(9007654, (int) arg.getIdAgent());
				assertEquals(date, arg.getDateCreation());
				assertEquals(TypeChainePaieEnum.SHC, arg.getTypeChainePaie());
				assertEquals(vd, arg.getVentilDate());
				assertNull(arg.getDateExport());
				assertNull(arg.getTaskStatus());
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(ExportEtatsPayeurTask.class));

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "paieWorkflowService", wS);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);

		// When
		ReturnMessageDto result = service.startExportEtatsPayeur(idAgentExporting, statut);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(1, result.getInfos().size());
		assertEquals("L'export des Etats du Payeur pour la chaine paie [SHC] a bien été lancé.",
				result.getInfos().get(0));
	}

	@Test
	public void stopExportEtatsPayeur_SetEtatPayeurStateToDone() throws WorkflowInvalidStateException {

		// Given
		IPaieWorkflowService pS = Mockito.mock(IPaieWorkflowService.class);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "paieWorkflowService", pS);

		// When
		service.stopExportEtatsPayeur(TypeChainePaieEnum.SCV);

		// Then
		Mockito.verify(pS, Mockito.times(1)).changeStateToExportEtatsPayeurDone(TypeChainePaieEnum.SCV);
	}

	// #17538
	@Test
	public void calculMinutesRecuperation_fonctionnaire() {

		Date dateLundi = new Date();

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(dateLundi);
		vh.setIdAgent(9008989);
		vh.setMRecuperees(100);

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(8989, vh.getDateLundi())).thenReturn(spcarr1);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		int result = service.calculMinutesRecuperation(vh);

		assertEquals(result, 0);
	}

	// #17538
	@Test
	public void calculMinutesRecuperation_contractuel() {

		Date dateLundi = new Date();

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(dateLundi);
		vh.setIdAgent(9008989);
		vh.setMRecuperees(100);

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(4);
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(8989, vh.getDateLundi())).thenReturn(spcarr1);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		int result = service.calculMinutesRecuperation(vh);

		assertEquals(result, 0);
	}

	// #17538
	@Test
	public void calculMinutesRecuperation_fonctionnaire_avec_HSRappelService() {

		Date dateLundi = new Date();

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(dateLundi);
		vh.setIdAgent(9008989);
		vh.setMRecuperees(100);
		vh.setMRappelService(50);

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(8989, vh.getDateLundi())).thenReturn(spcarr1);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		int result = service.calculMinutesRecuperation(vh);

		assertEquals(result, 0);
	}

	// #17538
	@Test
	public void calculMinutesRecuperation_contractuel_avec_HSRappelService() {

		Date dateLundi = new Date();

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(dateLundi);
		vh.setIdAgent(9008989);
		vh.setMRecuperees(100);
		vh.setMRappelService(70);

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(4);
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(8989, vh.getDateLundi())).thenReturn(spcarr1);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		int result = service.calculMinutesRecuperation(vh);

		assertEquals(result, 0);
	}

	@Test
	public void calculMinutesRecuperation_convColl_cas1() {

		Date dateLundi = new Date();

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(dateLundi);
		vh.setIdAgent(9008989);
		vh.setMRecuperees(100);

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(7);
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(8989, vh.getDateLundi())).thenReturn(spcarr1);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		int result = service.calculMinutesRecuperation(vh);

		assertEquals(result, 0);
	}

	// #17538 : nouvelle gestion des compteurs de RECUP
	@Test
	public void calculMinutesRecuperation_convColl_cas2() {

		Date dateLundi = new Date();

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(dateLundi);
		vh.setIdAgent(9008989);
		vh.setMRecuperees(100);

		vh.setMNormalesRecup(10);
		vh.setMSup25Recup(20);
		vh.setMSup50Recup(30);
		vh.setMsdjfRecup(40);
		vh.setMsNuitRecup(50);
		vh.setMMaiRecup(60);

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(7);
		IMairieRepository mairieRepository = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepository.getAgentCurrentCarriere(8989, vh.getDateLundi())).thenReturn(spcarr1);

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", helperService);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepository);

		int result = service.calculMinutesRecuperation(vh);
		// #17538 : nouvelle gestion des compteurs de RECUP
		double calcul = 0 + 20 * 0.25 + 30 * 0.5 + 40 * 0.75 + 50 * 1 + 60 * 0.75;
		assertEquals(result, calcul, 0);
	}

	@Test
	public void getHeuresSupEtatPayeurDataForStatut_onlyRecup() {

		Integer idAgent = 9005138;
		AbstractItemEtatPayeurDto result = new AbstractItemEtatPayeurDto();
		result.setHeuresSup(null);
		VentilDate toVentilDate = new VentilDate();
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new Date());

		VentilHsup vh1 = new VentilHsup();
		vh1.setDateLundi(new LocalDate(2013, 8, 26).toDate());
		vh1.setIdAgent(9008989);
		vh1.setMHorsContrat(100);
		vh1.setMRecuperees(100);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), idAgent))
				.thenReturn(Arrays.asList(vh1));

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.formatMinutesToString(0)).thenReturn("");

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service)
				.fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class), Mockito.any(Integer.class));
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		result = service.getHeuresSupEtatPayeurDataForStatut(idAgent, result, toVentilDate, fromVentilDate);

		assertNull(result);
	}

	// cas reel d un CC en recette utilisateur #14640
	@Test
	public void getHeuresSupEtatPayeurDataForStatut_2VentilHSup() {

		Integer idAgent = 9005138;
		AbstractItemEtatPayeurDto result = new AbstractItemEtatPayeurDto();
		result.setHeuresSup(null);
		VentilDate toVentilDate = new VentilDate();
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new Date());

		VentilHsup vh1 = new VentilHsup();
		vh1.setDateLundi(new LocalDate(2015, 2, 2).toDate());
		vh1.setIdAgent(9004241);
		vh1.setMHorsContrat(120);
		vh1.setMSup(120);
		vh1.setMSup25(120);
		vh1.setMSup50(0);
		vh1.setMsdjf(0);
		vh1.setMMai(0);
		vh1.setMsNuit(0);
		vh1.setMNormales(0);
		vh1.setMSimple(0);
		vh1.setMComposees(0);

		VentilHsup vh2 = new VentilHsup();
		vh2.setDateLundi(new LocalDate(2015, 2, 16).toDate());
		vh2.setIdAgent(9004241);
		vh2.setMHorsContrat(210);
		vh2.setMSup(210);
		vh2.setMSup25(210);
		vh2.setMSup50(0);
		vh2.setMsdjf(0);
		vh2.setMMai(0);
		vh2.setMsNuit(0);
		vh2.setMNormales(0);
		vh2.setMSimple(0);
		vh2.setMComposees(0);

		VentilHsup vh3 = new VentilHsup();
		vh3.setDateLundi(new LocalDate(2015, 2, 23).toDate());
		vh3.setIdAgent(9004241);
		vh3.setMHorsContrat(360);
		vh3.setMSup(360);
		vh3.setMSup25(360);
		vh3.setMSup50(0);
		vh3.setMsdjf(360);
		vh3.setMMai(0);
		vh3.setMsNuit(0);
		vh3.setMNormales(0);
		vh3.setMSimple(0);
		vh3.setMComposees(0);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(toVentilDate.getIdVentilDate(), idAgent))
				.thenReturn(Arrays.asList(vh1, vh2, vh3));

		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.formatMinutesToString(0)).thenReturn("");
		Mockito.when(helperService.formatMinutesToString(360)).thenReturn("6h");
		Mockito.when(helperService.formatMinutesToString(690)).thenReturn("11h30");

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service)
				.fillAgentsData(Mockito.any(AbstractItemEtatPayeurDto.class), Mockito.any(Integer.class));
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "helperService", helperService);

		result = service.getHeuresSupEtatPayeurDataForStatut(idAgent, result, toVentilDate, fromVentilDate);

		assertNotNull(result.getHeuresSup());
		assertEquals("", result.getHeuresSup().getNormales());
		assertEquals("11h30", result.getHeuresSup().getSup25());
		assertEquals("", result.getHeuresSup().getSup50());
		assertEquals("6h", result.getHeuresSup().getDjf());
		assertEquals("", result.getHeuresSup().getNuit());
		assertEquals("", result.getHeuresSup().getH1Mai());
	}
}
