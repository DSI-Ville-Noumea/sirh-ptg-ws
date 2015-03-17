package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.ReposCompHisto;
import nc.noumea.mairie.ptg.domain.ReposCompTask;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.IReposCompRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class ReposCompServiceTest {

	@Test
	public void processReposCompTask_TaskDoesNotExists_doNothing() {

		// Given
		IReposCompRepository rcR = Mockito.mock(IReposCompRepository.class);
		Mockito.when(rcR.getReposCompTask(15)).thenReturn(null);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);

		ReposCompService service = new ReposCompService();
		ReflectionTestUtils.setField(service, "reposCompRepository", rcR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		service.processReposCompTask(15);

		// Then
		Mockito.verify(vR, Mockito.never()).getListVentilHSupForAgentAndVentilDateOrderByDateAsc(Mockito.anyInt(),
				Mockito.anyInt());
	}

	@Test
	public void processReposCompTask_NoHSupsForAgent_doNothing() {

		// Given
		ReposCompTask t = new ReposCompTask();
		t.setIdAgent(9005138);
		VentilDate vd = new VentilDate();
		vd.setIdVentilDate(56);
		t.setVentilDate(vd);

		IReposCompRepository rcR = Mockito.mock(IReposCompRepository.class);
		Mockito.when(rcR.getReposCompTask(15)).thenReturn(t);

		List<VentilHsup> hSs = new ArrayList<VentilHsup>();
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getListVentilHSupForAgentAndVentilDateOrderByDateAsc(t.getIdAgent(), vd.getIdVentilDate()))
				.thenReturn(hSs);

		ReposCompService service = new ReposCompService();
		ReflectionTestUtils.setField(service, "reposCompRepository", rcR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		service.processReposCompTask(15);

		// Then
		Mockito.verify(rcR, Mockito.never()).countTotalHSupsSinceStartOfYear(Mockito.anyInt(), Mockito.anyInt());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void processReposCompTask_1HsupAndAgentIsStatutF_doNothing() {

		// Given
		final ReposCompTask t = new ReposCompTask();
		t.setIdAgent(9005138);
		VentilDate vd = new VentilDate();
		vd.setIdVentilDate(56);
		t.setVentilDate(vd);

		Integer currentYear = 2013;
		IReposCompRepository rcR = Mockito.mock(IReposCompRepository.class);
		Mockito.when(rcR.getReposCompTask(15)).thenReturn(t);
		Mockito.when(rcR.countTotalHSupsSinceStartOfYear(t.getIdAgent(), currentYear)).thenReturn(0);

		List<VentilHsup> hSs = new ArrayList<VentilHsup>();
		final VentilHsup hs = new VentilHsup();
		hs.setMSup(180);
		hs.setDateLundi(new LocalDate(2013, 12, 16).toDate());
		hSs.add(hs);
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getListVentilHSupForAgentAndVentilDateOrderByDateAsc(t.getIdAgent(), vd.getIdVentilDate()))
				.thenReturn(hSs);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new LocalDate(2013, 1, 1).toDate());
		Mockito.when(hS.getMairieMatrFromIdAgent(t.getIdAgent())).thenReturn(5138);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(39.00d)).thenReturn(2340);

		Spcarr carr = new Spcarr();
		carr.setCdcate(2);
		BaseHorairePointageDto base = new BaseHorairePointageDto();
		base.setBaseLegale(39.00d);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(5138, hs.getDateLundi())).thenReturn(carr);

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				ReposCompHisto h = (ReposCompHisto) args[0];
				assertEquals(h.getDateLundi(), hs.getDateLundi());
				assertEquals(h.getIdAgent(), t.getIdAgent());
				assertEquals((int) h.getMBaseHoraire(), 2340);
				assertEquals((int) h.getMSup(), 180);
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(ReposCompHisto.class));

		IAbsWsConsumer absWs = Mockito.mock(IAbsWsConsumer.class);

		ReposCompService service = new ReposCompService();
		ReflectionTestUtils.setField(service, "reposCompRepository", rcR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWs);

		// When
		service.processReposCompTask(15);

		// Then
		Mockito.verify(pR, Mockito.never()).persisEntity(Mockito.isA(ReposCompHisto.class));
		Mockito.verify(absWs, Mockito.never()).addReposCompToAgent(Mockito.anyInt(), Mockito.any(Date.class),
				Mockito.anyInt());
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void processReposCompTask_1HSupsNotAlreadyExisting_AgentHas0MinutesSinceYearStarted_HSisLessThan180m_CreateHistoAndCallABSWSWith0() {

		// Given
		final ReposCompTask t = new ReposCompTask();
		t.setIdAgent(9005138);
		VentilDate vd = new VentilDate();
		vd.setIdVentilDate(56);
		t.setVentilDate(vd);

		Integer currentYear = 2013;
		IReposCompRepository rcR = Mockito.mock(IReposCompRepository.class);
		Mockito.when(rcR.getReposCompTask(15)).thenReturn(t);
		Mockito.when(rcR.countTotalHSupsSinceStartOfYear(t.getIdAgent(), currentYear)).thenReturn(0);

		List<VentilHsup> hSs = new ArrayList<VentilHsup>();
		final VentilHsup hs = new VentilHsup();
		hs.setMSup(180);
		hs.setDateLundi(new LocalDate(2013, 12, 16).toDate());
		hSs.add(hs);
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getListVentilHSupForAgentAndVentilDateOrderByDateAscForReposComp(t.getIdAgent(), vd.getIdVentilDate()))
				.thenReturn(hSs);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new LocalDate(2013, 1, 1).toDate());
		Mockito.when(hS.getMairieMatrFromIdAgent(t.getIdAgent())).thenReturn(5138);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(39.00d)).thenReturn(2340);

		Spcarr carr = new Spcarr();
		BaseHorairePointageDto base = new BaseHorairePointageDto();
		base.setIdBaseHorairePointage(1);
		base.setBaseLegale(39.00d);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(5138, hs.getDateLundi())).thenReturn(carr);

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				ReposCompHisto h = (ReposCompHisto) args[0];
				assertEquals(h.getDateLundi(), hs.getDateLundi());
				assertEquals(h.getIdAgent(), t.getIdAgent());
				assertEquals((int) h.getMBaseHoraire(), 2340);
				assertEquals((int) h.getMSup(), 180);
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(ReposCompHisto.class));

		IAbsWsConsumer absWs = Mockito.mock(IAbsWsConsumer.class);

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWSConsumer.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(
				base);

		ReposCompService service = new ReposCompService();
		ReflectionTestUtils.setField(service, "reposCompRepository", rcR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWs);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", sirhWSConsumer);

		// When
		service.processReposCompTask(15);

		// Then
		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(ReposCompHisto.class));
		Mockito.verify(absWs, Mockito.times(1)).addReposCompToAgent(Mockito.anyInt(), Mockito.any(Date.class),
				Mockito.eq(0));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void processReposCompTask_1HSupsNotAlreadyExisting_AgentHas0MinutesSinceYearStarted_HSIs45MinOver42H_CreateHistoAndCallABSWS() {

		// Given
		final ReposCompTask t = new ReposCompTask();
		t.setIdAgent(9005138);
		VentilDate vd = new VentilDate();
		vd.setIdVentilDate(56);
		t.setVentilDate(vd);

		Integer currentYear = 2013;
		IReposCompRepository rcR = Mockito.mock(IReposCompRepository.class);
		Mockito.when(rcR.getReposCompTask(15)).thenReturn(t);
		Mockito.when(rcR.countTotalHSupsSinceStartOfYear(t.getIdAgent(), currentYear)).thenReturn(0);

		List<VentilHsup> hSs = new ArrayList<VentilHsup>();
		final VentilHsup hs = new VentilHsup();
		hs.setMSup(225);
		hs.setDateLundi(new LocalDate(2013, 12, 16).toDate());
		hSs.add(hs);
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getListVentilHSupForAgentAndVentilDateOrderByDateAscForReposComp(t.getIdAgent(), vd.getIdVentilDate()))
				.thenReturn(hSs);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new LocalDate(2013, 1, 1).toDate());
		Mockito.when(hS.getMairieMatrFromIdAgent(t.getIdAgent())).thenReturn(5138);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(39.00d)).thenReturn(2340);

		Spcarr carr = new Spcarr();
		BaseHorairePointageDto base = new BaseHorairePointageDto();
		base.setIdBaseHorairePointage(1);
		base.setBaseLegale(39.00d);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(5138, hs.getDateLundi())).thenReturn(carr);

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				ReposCompHisto h = (ReposCompHisto) args[0];
				assertEquals(h.getDateLundi(), hs.getDateLundi());
				assertEquals(h.getIdAgent(), t.getIdAgent());
				assertEquals((int) h.getMBaseHoraire(), 2340);
				assertEquals((int) h.getMSup(), 225);
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(ReposCompHisto.class));

		IAbsWsConsumer absWs = Mockito.mock(IAbsWsConsumer.class);

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWSConsumer.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(
				base);

		ReposCompService service = new ReposCompService();
		ReflectionTestUtils.setField(service, "reposCompRepository", rcR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWs);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", sirhWSConsumer);

		// When
		service.processReposCompTask(15);

		// Then
		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(ReposCompHisto.class));
		Mockito.verify(absWs, Mockito.times(1)).addReposCompToAgent(Mockito.eq(t.getIdAgent()),
				Mockito.eq(hs.getDateLundi()), Mockito.eq(9));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void processReposCompTask_1HSupsNotAlreadyExisting_AgentHas0MinutesSinceYearStarted_HSIs75MinOver42H_CreateHistoAndCallABSWS() {

		// Given
		final ReposCompTask t = new ReposCompTask();
		t.setIdAgent(9005138);
		VentilDate vd = new VentilDate();
		vd.setIdVentilDate(56);
		t.setVentilDate(vd);

		Integer currentYear = 2013;
		IReposCompRepository rcR = Mockito.mock(IReposCompRepository.class);
		Mockito.when(rcR.getReposCompTask(15)).thenReturn(t);
		Mockito.when(rcR.countTotalHSupsSinceStartOfYear(t.getIdAgent(), currentYear)).thenReturn(0);

		List<VentilHsup> hSs = new ArrayList<VentilHsup>();
		final VentilHsup hs = new VentilHsup();
		hs.setMSup(270);
		hs.setDateLundi(new LocalDate(2013, 12, 16).toDate());
		hSs.add(hs);
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getListVentilHSupForAgentAndVentilDateOrderByDateAscForReposComp(t.getIdAgent(), vd.getIdVentilDate()))
				.thenReturn(hSs);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new LocalDate(2013, 1, 1).toDate());
		Mockito.when(hS.getMairieMatrFromIdAgent(t.getIdAgent())).thenReturn(5138);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(39.00d)).thenReturn(2340);

		Spcarr carr = new Spcarr();
		BaseHorairePointageDto base = new BaseHorairePointageDto();
		base.setIdBaseHorairePointage(1);
		base.setBaseLegale(39.00d);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(5138, hs.getDateLundi())).thenReturn(carr);

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				ReposCompHisto h = (ReposCompHisto) args[0];
				assertEquals(h.getDateLundi(), hs.getDateLundi());
				assertEquals(h.getIdAgent(), t.getIdAgent());
				assertEquals((int) h.getMBaseHoraire(), 2340);
				assertEquals((int) h.getMSup(), 270);
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(ReposCompHisto.class));

		IAbsWsConsumer absWs = Mockito.mock(IAbsWsConsumer.class);

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWSConsumer.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(
				base);

		ReposCompService service = new ReposCompService();
		ReflectionTestUtils.setField(service, "reposCompRepository", rcR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWs);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", sirhWSConsumer);

		// When
		service.processReposCompTask(15);

		// Then
		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(ReposCompHisto.class));
		Mockito.verify(absWs, Mockito.times(1)).addReposCompToAgent(Mockito.eq(t.getIdAgent()),
				Mockito.eq(hs.getDateLundi()), Mockito.eq(18));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void processReposCompTask_1HSupsNotAlreadyExisting_AgentHas130HSinceYearStarted_HSIs75MinOver42H_CreateHistoAndCallABSWS() {

		// Given
		final ReposCompTask t = new ReposCompTask();
		t.setIdAgent(9005138);
		VentilDate vd = new VentilDate();
		vd.setIdVentilDate(56);
		t.setVentilDate(vd);

		Integer currentYear = 2013;
		IReposCompRepository rcR = Mockito.mock(IReposCompRepository.class);
		Mockito.when(rcR.getReposCompTask(15)).thenReturn(t);
		Mockito.when(rcR.countTotalHSupsSinceStartOfYear(t.getIdAgent(), currentYear)).thenReturn(7800);

		List<VentilHsup> hSs = new ArrayList<VentilHsup>();
		final VentilHsup hs = new VentilHsup();
		hs.setMSup(270);
		hs.setDateLundi(new LocalDate(2013, 12, 16).toDate());
		hSs.add(hs);
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getListVentilHSupForAgentAndVentilDateOrderByDateAscForReposComp(t.getIdAgent(), vd.getIdVentilDate()))
				.thenReturn(hSs);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new LocalDate(2013, 1, 1).toDate());
		Mockito.when(hS.getMairieMatrFromIdAgent(t.getIdAgent())).thenReturn(5138);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(39.00d)).thenReturn(2340);

		Spcarr carr = new Spcarr();
		BaseHorairePointageDto base = new BaseHorairePointageDto();
		base.setIdBaseHorairePointage(1);
		base.setBaseLegale(39.00d);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(5138, hs.getDateLundi())).thenReturn(carr);

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				ReposCompHisto h = (ReposCompHisto) args[0];
				assertEquals(h.getDateLundi(), hs.getDateLundi());
				assertEquals(h.getIdAgent(), t.getIdAgent());
				assertEquals((int) h.getMBaseHoraire(), 2340);
				assertEquals((int) h.getMSup(), 270);
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(ReposCompHisto.class));

		IAbsWsConsumer absWs = Mockito.mock(IAbsWsConsumer.class);

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWSConsumer.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(
				base);

		ReposCompService service = new ReposCompService();
		ReflectionTestUtils.setField(service, "reposCompRepository", rcR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWs);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", sirhWSConsumer);

		// When
		service.processReposCompTask(15);

		// Then
		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(ReposCompHisto.class));
		Mockito.verify(absWs, Mockito.times(1)).addReposCompToAgent(Mockito.eq(t.getIdAgent()),
				Mockito.eq(hs.getDateLundi()), Mockito.eq(135));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void processReposCompTask_1HSupsNotAlreadyExisting_AgentHas129HSinceYearStarted_HSIs105MinOver42H_CreateHistoAndCallABSWS() {

		// Given
		final ReposCompTask t = new ReposCompTask();
		t.setIdAgent(9005138);
		VentilDate vd = new VentilDate();
		vd.setIdVentilDate(56);
		t.setVentilDate(vd);

		Integer currentYear = 2013;
		IReposCompRepository rcR = Mockito.mock(IReposCompRepository.class);
		Mockito.when(rcR.getReposCompTask(15)).thenReturn(t);
		Mockito.when(rcR.countTotalHSupsSinceStartOfYear(t.getIdAgent(), currentYear)).thenReturn(7740);

		List<VentilHsup> hSs = new ArrayList<VentilHsup>();
		final VentilHsup hs = new VentilHsup();
		hs.setMSup(555);
		hs.setDateLundi(new LocalDate(2013, 12, 16).toDate());
		hSs.add(hs);
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getListVentilHSupForAgentAndVentilDateOrderByDateAscForReposComp(t.getIdAgent(), vd.getIdVentilDate()))
				.thenReturn(hSs);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new LocalDate(2013, 1, 1).toDate());
		Mockito.when(hS.getMairieMatrFromIdAgent(t.getIdAgent())).thenReturn(5138);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(39.00d)).thenReturn(2340);

		Spcarr carr = new Spcarr();
		BaseHorairePointageDto base = new BaseHorairePointageDto();
		base.setIdBaseHorairePointage(1);
		base.setBaseLegale(39.00d);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(5138, hs.getDateLundi())).thenReturn(carr);

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object[] args = invocation.getArguments();
				ReposCompHisto h = (ReposCompHisto) args[0];
				assertEquals(hs.getDateLundi(), h.getDateLundi());
				assertEquals(t.getIdAgent(), h.getIdAgent());
				assertEquals(2340, (int) h.getMBaseHoraire());
				assertEquals(555, (int) h.getMSup());
				return true;
			}
		}).when(pR).persisEntity(Mockito.isA(ReposCompHisto.class));

		IAbsWsConsumer absWs = Mockito.mock(IAbsWsConsumer.class);

		ISirhWSConsumer sirhWSConsumer = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhWSConsumer.getBaseHorairePointageAgent(Mockito.anyInt(), Mockito.any(Date.class))).thenReturn(
				base);

		ReposCompService service = new ReposCompService();
		ReflectionTestUtils.setField(service, "reposCompRepository", rcR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);
		ReflectionTestUtils.setField(service, "absWsConsumer", absWs);
		ReflectionTestUtils.setField(service, "sirhWSConsumer", sirhWSConsumer);

		// When
		service.processReposCompTask(15);

		// Then
		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(ReposCompHisto.class));
		Mockito.verify(absWs, Mockito.times(1)).addReposCompToAgent(Mockito.eq(t.getIdAgent()),
				Mockito.eq(hs.getDateLundi()), Mockito.eq(247));
	}

	@Test
	public void getOrCreateReposCompHisto_HistoDoesNotExists_CreateAndPersist() {

		// Given
		ReposCompHisto existing = null;
		Integer idAgent = 9005138;
		Date dateLundi = new LocalDate(2013, 12, 16).toDate();

		IReposCompRepository rC = Mockito.mock(IReposCompRepository.class);
		Mockito.when(rC.findReposCompHistoForAgentAndDate(idAgent, dateLundi)).thenReturn(existing);

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);

		ReposCompService service = new ReposCompService();
		ReflectionTestUtils.setField(service, "reposCompRepository", rC);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);

		// When
		Pair<ReposCompHisto, Integer> result = service.getOrCreateReposCompHisto(idAgent, dateLundi, 256, 60);

		// Then
		assertEquals(idAgent, result.getLeft().getIdAgent());
		assertEquals(dateLundi, result.getLeft().getDateLundi());
		assertEquals(256, (int) result.getLeft().getMBaseHoraire());
		assertEquals(60, (int) result.getLeft().getMSup());
		assertEquals(60, (int) result.getRight());

		Mockito.verify(pR, Mockito.times(1)).persisEntity(Mockito.isA(ReposCompHisto.class));
	}

	@Test
	public void getOrCreateReposCompHisto_HistoExists_UpdateMSups() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new LocalDate(2013, 12, 16).toDate();

		ReposCompHisto existing = new ReposCompHisto();
		existing.setIdAgent(idAgent);
		existing.setMSup(90);
		existing.setMBaseHoraire(256);
		existing.setIdRcHisto(12);
		existing.setDateLundi(dateLundi);

		IReposCompRepository rC = Mockito.mock(IReposCompRepository.class);
		Mockito.when(rC.findReposCompHistoForAgentAndDate(idAgent, dateLundi)).thenReturn(existing);

		IPointageRepository pR = Mockito.mock(IPointageRepository.class);

		ReposCompService service = new ReposCompService();
		ReflectionTestUtils.setField(service, "reposCompRepository", rC);
		ReflectionTestUtils.setField(service, "pointageRepository", pR);

		// When
		Pair<ReposCompHisto, Integer> result = service.getOrCreateReposCompHisto(idAgent, dateLundi, 256, 60);

		// Then
		assertEquals(idAgent, result.getLeft().getIdAgent());
		assertEquals(dateLundi, result.getLeft().getDateLundi());
		assertEquals(256, (int) result.getLeft().getMBaseHoraire());
		assertEquals(60, (int) result.getLeft().getMSup());
		assertEquals(-30, (int) result.getRight());

		Mockito.verify(pR, Mockito.never()).persisEntity(Mockito.isA(ReposCompHisto.class));

	}
}
