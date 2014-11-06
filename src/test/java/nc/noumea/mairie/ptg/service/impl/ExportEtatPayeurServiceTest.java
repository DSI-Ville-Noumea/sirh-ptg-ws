package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
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
import nc.noumea.mairie.ptg.dto.etatsPayeur.AbsencesEtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.EtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.HeuresSupEtatPayeurDto;
import nc.noumea.mairie.ptg.dto.etatsPayeur.PrimesEtatPayeurDto;
import nc.noumea.mairie.ptg.repository.IAccessRightsRepository;
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

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilAbsenceWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(va1, va2));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		Spcarr spcarr2 = new Spcarr();
		spcarr2.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);
		Mockito.when(sR.getAgentCurrentCarriere(6767, toVentilDate.getDateVentilation())).thenReturn(spcarr2);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service).fillAgentsData(Mockito.any(AbsencesEtatPayeurDto.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getAbsencesEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(3, result.getAbsences().size());
		assertEquals(0, result.getPrimes().size());
		assertEquals(0, result.getHeuresSup().size());

		Mockito.verify(service, Mockito.times(3)).fillAgentsData(Mockito.any(AbsencesEtatPayeurDto.class));
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

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilAbsenceWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(va1));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(7);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

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
		Mockito.when(vR.getPriorVentilAbsenceForAgentAndDate(va1.getIdAgent(), va1.getDateLundi(), va1)).thenReturn(
				vaOld);
		Mockito.when(vR.getListOfVentilAbsenceWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(va1));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service).fillAgentsData(Mockito.any(AbsencesEtatPayeurDto.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

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

		Mockito.verify(service, Mockito.times(2)).fillAgentsData(Mockito.any(AbsencesEtatPayeurDto.class));
	}

	@Test
	public void getHeuresSupEtatPayeurDataForStatut_2Agents_ReturnFilledDTO() {

		// Given
		VentilDate toVentilDate = new VentilDate();
		toVentilDate.setDateVentilation(new DateTime(2013, 9, 1, 23, 59, 9).toDate());
		VentilDate fromVentilDate = new VentilDate();
		fromVentilDate.setDateVentilation(new DateTime(2013, 9, 29, 23, 59, 9).toDate());
		AgentStatutEnum statut = AgentStatutEnum.F;

		VentilHsup vh1 = new VentilHsup();
		vh1.setDateLundi(new LocalDate(2013, 9, 2).toDate());
		vh1.setIdAgent(9008989);
		vh1.setMComplementaires(30);

		VentilHsup vh2 = new VentilHsup();
		vh2.setDateLundi(new LocalDate(2013, 9, 9).toDate());
		vh2.setIdAgent(9006767);
		vh2.setMComplementaires(60);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.getMairieMatrFromIdAgent(9006767)).thenReturn(6767);
		Mockito.when(hS.formatMinutesToString(60)).thenReturn("60m");
		Mockito.when(hS.formatMinutesToString(30)).thenReturn("30m");

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(vh1, vh2));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		Spcarr spcarr2 = new Spcarr();
		spcarr2.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);
		Mockito.when(sR.getAgentCurrentCarriere(6767, toVentilDate.getDateVentilation())).thenReturn(spcarr2);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service).fillAgentsData(Mockito.any(HeuresSupEtatPayeurDto.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getHeuresSupEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAbsences().size());
		assertEquals(0, result.getPrimes().size());
		assertEquals(2, result.getHeuresSup().size());

		Mockito.verify(service, Mockito.times(2)).fillAgentsData(Mockito.any(HeuresSupEtatPayeurDto.class));
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

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(vh1));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(7);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getHeuresSupEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAbsences().size());
		assertEquals(0, result.getPrimes().size());
		assertEquals(0, result.getHeuresSup().size());
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
		vh1.setMMai(60);
		vh1.setMComplementaires(30);

		VentilHsup vhOld = new VentilHsup();
		vhOld.setDateLundi(new LocalDate(2013, 8, 26).toDate());
		vhOld.setMMai(30);
		vhOld.setMComplementaires(60);
		vhOld.setIdAgent(9008989);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(statut)).thenReturn(TypeChainePaieEnum.SCV);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008989)).thenReturn(8989);
		Mockito.when(hS.formatMinutesToString(-30)).thenReturn("- 30m");
		Mockito.when(hS.formatMinutesToString(30)).thenReturn("30m");

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getPriorVentilHSupAgentAndDate(vh1.getIdAgent(), vh1.getDateLundi(), vh1)).thenReturn(vhOld);
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(vh1));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service).fillAgentsData(Mockito.any(HeuresSupEtatPayeurDto.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getHeuresSupEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAbsences().size());
		assertEquals(0, result.getPrimes().size());
		assertEquals(1, result.getHeuresSup().size());
		assertEquals("- 30m", result.getHeuresSup().get(0).getComplementaires());
		assertEquals("30m", result.getHeuresSup().get(0).getH1Mai());

		Mockito.verify(service, Mockito.times(1)).fillAgentsData(Mockito.any(HeuresSupEtatPayeurDto.class));
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

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilPrimeWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(vp1, vp2));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		Spcarr spcarr2 = new Spcarr();
		spcarr2.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);
		Mockito.when(sR.getAgentCurrentCarriere(6767, toVentilDate.getDateVentilation())).thenReturn(spcarr2);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service).fillAgentsData(Mockito.any(PrimesEtatPayeurDto.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getPrimesEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAbsences().size());
		assertEquals(2, result.getPrimes().size());
		assertEquals(0, result.getHeuresSup().size());

		Mockito.verify(service, Mockito.times(2)).fillAgentsData(Mockito.any(PrimesEtatPayeurDto.class));
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

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getListOfVentilPrimeWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(vp1));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(7);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getPrimesEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAbsences().size());
		assertEquals(0, result.getPrimes().size());
		assertEquals(0, result.getHeuresSup().size());
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

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(toVentilDate);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, true)).thenReturn(fromVentilDate);
		Mockito.when(vR.getPriorVentilPrimeForAgentAndDate(vp1.getIdAgent(), vp1.getDateDebutMois(), vp1)).thenReturn(
				vpOld);
		Mockito.when(vR.getListOfVentilPrimeWithDateForEtatPayeur(toVentilDate.getIdVentilDate())).thenReturn(
				Arrays.asList(vp1));

		Spcarr spcarr1 = new Spcarr();
		spcarr1.setCdcate(1);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(8989, toVentilDate.getDateVentilation())).thenReturn(spcarr1);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doNothing().when(service).fillAgentsData(Mockito.any(PrimesEtatPayeurDto.class));
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		EtatPayeurDto result = service.getPrimesEtatPayeurDataForStatut(statut);

		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals("F", result.getStatut());

		assertEquals(0, result.getAbsences().size());
		assertEquals(1, result.getPrimes().size());
		assertEquals("libelle", result.getPrimes().get(0).getType());
		assertEquals("-9", result.getPrimes().get(0).getQuantite());
		assertEquals(0, result.getHeuresSup().size());

		Mockito.verify(service, Mockito.times(1)).fillAgentsData(Mockito.any(PrimesEtatPayeurDto.class));
	}

	@Test
	public void fillAgentsData_RetrieveApprobateurAndFillDataFromSirhRepo() {

		// Given
		Date d = new Date();

		AbsencesEtatPayeurDto va = new AbsencesEtatPayeurDto();
		va.setIdAgent(9008888);
		va.setType("TYPE");
		va.setQuantite("QUANTITE");
		va.setDate(d);

		AgentGeneriqueDto ag1 = new AgentGeneriqueDto();
		ag1.setNomUsage("nomusage1");
		ag1.setPrenomUsage("prenomusage1");

		IAccessRightsRepository aR = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(aR.getAgentsApprobateur(9008888)).thenReturn(9009999);

		Date currentDate = new LocalDate(2013, 7, 8).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		AgentWithServiceDto agDto = new AgentWithServiceDto();
		agDto.setNom("nomusage2");
		agDto.setPrenom("prenomusage2");
		agDto.setCodeService("CODE");
		agDto.setService("Label");
		ISirhWSConsumer swc = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(swc.getAgentService(9009999, currentDate)).thenReturn(agDto);
		Mockito.when(swc.getAgent(9008888)).thenReturn(ag1);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		ReflectionTestUtils.setField(service, "accessRightRepository", aR);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", swc);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		service.fillAgentsData(va);

		// Then
		assertEquals(9008888, (int) va.getIdAgent());
		assertEquals("TYPE", va.getType());
		assertEquals("QUANTITE", va.getQuantite());
		assertEquals(d, va.getDate());
		assertEquals("nomusage1", va.getNom());
		assertEquals("prenomusage1", va.getPrenom());
		assertEquals(9009999, (int) va.getApprobateurIdAgent());
		assertEquals("nomusage2", va.getApprobateurNom());
		assertEquals("prenomusage2", va.getApprobateurPrenom());
		assertEquals("CODE - Label", va.getApprobateurServiceLabel());

	}

	@Test
	public void fillAgentsData_RetrieveApprobateurAndFillDataFromSirhRepo_NoApprobateur() {

		// Given
		AbsencesEtatPayeurDto va = new AbsencesEtatPayeurDto();
		va.setIdAgent(9008888);

		AgentGeneriqueDto ag1 = new AgentGeneriqueDto();
		ag1.setNomUsage("nomusage1");
		ag1.setPrenomUsage("prenomusage1");

		ISirhWSConsumer sR = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sR.getAgent(9008888)).thenReturn(ag1);

		IAccessRightsRepository aR = Mockito.mock(IAccessRightsRepository.class);
		Mockito.when(aR.getAgentsApprobateur(9008888)).thenReturn(null);

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		ReflectionTestUtils.setField(service, "accessRightRepository", aR);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sR);

		// When
		service.fillAgentsData(va);

		// Then
		assertEquals(9008888, (int) va.getIdAgent());
		assertEquals("nomusage1", va.getNom());
		assertEquals("prenomusage1", va.getPrenom());
		assertNull(va.getApprobateurIdAgent());
		assertNull(va.getApprobateurNom());
		assertNull(va.getApprobateurPrenom());
	}

	@Test
	public void exportEtatPayeur_CallBirtWsConsumerAndReturnFilledInEtatPayeurObject() throws Exception {

		// Given
		RefTypePointageEnum typeEtat = RefTypePointageEnum.H_SUP;
		AgentStatutEnum statut = AgentStatutEnum.F;
		Integer idAgent = 9008765;
		Date date = new LocalDate(2013, 02, 25).toDate();
		IBirtEtatsPayeurWsConsumer wsC = Mockito.mock(IBirtEtatsPayeurWsConsumer.class);

		RefTypePointage rp = new RefTypePointage();
		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.when(pR.getEntity(RefTypePointage.class, RefTypePointageEnum.H_SUP.getValue())).thenReturn(rp);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 9, 2, 8, 7, 45).toDate());

		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "birtEtatsPayeurWsConsumer", wsC);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		EtatPayeur result = service.exportEtatPayeur(idAgent, typeEtat, statut, date);

		// Then
		assertEquals(rp, result.getType());
		assertEquals("2013-02-F-H_SUP", result.getLabel());
		assertEquals("2013-02-F-H_SUP.pdf", result.getFichier());
		assertEquals(statut, result.getStatut());
		assertEquals(new LocalDate(2013, 2, 1).toDate(), result.getDateEtatPayeur());
		assertEquals(idAgent, result.getIdAgent());
		assertEquals(new DateTime(2013, 9, 2, 8, 7, 45).toDate(), result.getDateEdition());

		Mockito.verify(wsC, Mockito.times(1)).downloadEtatPayeurByStatut(typeEtat, statut.toString(),
				"2013-02-F-H_SUP.pdf");
	}

	@Test
	public void callBirtEtatsPayeurForChainePaie_Call3ReportsForSCV_Return3EtatPayeur() throws Exception {

		// Given
		Integer idAgentExporting = 9008987;
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;
		AgentStatutEnum statut = AgentStatutEnum.CC;
		Date ventilationDate = new LocalDate(2013, 02, 25).toDate();

		ExportEtatPayeurService service = Mockito.spy(new ExportEtatPayeurService());
		Mockito.doReturn(new EtatPayeur()).when(service)
				.exportEtatPayeur(idAgentExporting, RefTypePointageEnum.ABSENCE, statut, ventilationDate);
		Mockito.doReturn(new EtatPayeur()).when(service)
				.exportEtatPayeur(idAgentExporting, RefTypePointageEnum.H_SUP, statut, ventilationDate);
		Mockito.doReturn(new EtatPayeur()).when(service)
				.exportEtatPayeur(idAgentExporting, RefTypePointageEnum.PRIME, statut, ventilationDate);

		// When
		List<EtatPayeur> result = service.callBirtEtatsPayeurForChainePaie(idAgentExporting, chainePaie,
				ventilationDate);

		// Then
		assertEquals(3, result.size());

		Mockito.verify(service, Mockito.times(1)).exportEtatPayeur(idAgentExporting, RefTypePointageEnum.ABSENCE,
				statut, ventilationDate);
		Mockito.verify(service, Mockito.times(1)).exportEtatPayeur(idAgentExporting, RefTypePointageEnum.H_SUP, statut,
				ventilationDate);
		Mockito.verify(service, Mockito.times(1)).exportEtatPayeur(idAgentExporting, RefTypePointageEnum.PRIME, statut,
				ventilationDate);
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
				.exportEtatPayeur(idAgentExporting, RefTypePointageEnum.ABSENCE, AgentStatutEnum.F, ventilationDate);
		Mockito.doReturn(new EtatPayeur()).when(service)
				.exportEtatPayeur(idAgentExporting, RefTypePointageEnum.H_SUP, AgentStatutEnum.F, ventilationDate);
		Mockito.doReturn(new EtatPayeur()).when(service)
				.exportEtatPayeur(idAgentExporting, RefTypePointageEnum.PRIME, AgentStatutEnum.F, ventilationDate);
		Mockito.doReturn(new EtatPayeur()).when(service)
				.exportEtatPayeur(idAgentExporting, RefTypePointageEnum.ABSENCE, AgentStatutEnum.C, ventilationDate);
		Mockito.doReturn(new EtatPayeur()).when(service)
				.exportEtatPayeur(idAgentExporting, RefTypePointageEnum.H_SUP, AgentStatutEnum.C, ventilationDate);
		Mockito.doReturn(new EtatPayeur()).when(service)
				.exportEtatPayeur(idAgentExporting, RefTypePointageEnum.PRIME, AgentStatutEnum.C, ventilationDate);

		// When
		List<EtatPayeur> result = service.callBirtEtatsPayeurForChainePaie(idAgentExporting, chainePaie,
				ventilationDate);

		// Then
		assertEquals(6, result.size());

		Mockito.verify(service, Mockito.times(1)).exportEtatPayeur(idAgentExporting, RefTypePointageEnum.ABSENCE,
				statut, ventilationDate);
		Mockito.verify(service, Mockito.times(1)).exportEtatPayeur(idAgentExporting, RefTypePointageEnum.H_SUP, statut,
				ventilationDate);
		Mockito.verify(service, Mockito.times(1)).exportEtatPayeur(idAgentExporting, RefTypePointageEnum.PRIME, statut,
				ventilationDate);
		Mockito.verify(service, Mockito.times(1)).exportEtatPayeur(idAgentExporting, RefTypePointageEnum.ABSENCE,
				AgentStatutEnum.F, ventilationDate);
		Mockito.verify(service, Mockito.times(1)).exportEtatPayeur(idAgentExporting, RefTypePointageEnum.H_SUP,
				AgentStatutEnum.F, ventilationDate);
		Mockito.verify(service, Mockito.times(1)).exportEtatPayeur(idAgentExporting, RefTypePointageEnum.PRIME,
				AgentStatutEnum.F, ventilationDate);
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

	@Test
	public void exportEtatsPayeur_CallEtatPayeursAndSendRecupsToAbs() throws WorkflowInvalidStateException {

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
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(vd.getIdVentilDate())).thenReturn(
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
		Mockito.verify(ac, Mockito.times(1)).addRecuperationsToAgent(9009999, vh2.getDateLundi(), 90);
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
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(vd.getIdVentilDate())).thenReturn(
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
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(vd.getIdVentilDate())).thenReturn(
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
		Mockito.when(vR.getListOfVentilHeuresSupWithDateForEtatPayeur(vd.getIdVentilDate())).thenReturn(
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

		assertEquals(result, 100);
	}

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

		assertEquals(result, 100);
	}
	
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

		assertEquals(result, 150);
	}

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

		assertEquals(result, 170);
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

	@Test
	public void calculMinutesRecuperation_convColl_cas2() {

		Date dateLundi = new Date();

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(dateLundi);
		vh.setIdAgent(9008989);
		vh.setMRecuperees(100);

		vh.setMComplementairesRecup(10);
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

		double calcul = 10 + 20 * 1.25 + 30 * 1.5 + 40 * 0.75 + 50 * 1 + 60 * 0.75;
		assertEquals(result, calcul, 0);
	}
}
