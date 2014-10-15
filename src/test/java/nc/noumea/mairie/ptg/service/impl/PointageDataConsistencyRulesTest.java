package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.SpabsenId;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.Spbarem;
import nc.noumea.mairie.domain.Spbase;
import nc.noumea.mairie.domain.Spbhor;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.Spcong;
import nc.noumea.mairie.domain.SpcongId;
import nc.noumea.mairie.domain.Sprirc;
import nc.noumea.mairie.domain.SprircId;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.SirhWsServiceDto;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class PointageDataConsistencyRulesTest {

	@Test
	public void processDataConsistency_CheckDataMethodsAreCalled() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		ReturnMessageDto rmd = new ReturnMessageDto();
		Spcarr carr = new Spcarr();
		AgentGeneriqueDto agent = new AgentGeneriqueDto();

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgentCurrentCarriere(agent, dateLundi)).thenReturn(carr);

		ISirhWSConsumer sRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sRepo.getAgent(idAgent)).thenReturn(agent);

		PointageDataConsistencyRules service = Mockito.spy(new PointageDataConsistencyRules());
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sRepo);

		Mockito.doReturn(rmd).when(service).checkSprircRecuperation(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkSpcongConge(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkSpabsenMaladie(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkMaxAbsenceHebdo(rmd, idAgent, dateLundi, pointages, carr);
		Mockito.doReturn(rmd).when(service).checkAgentINAAndHSup(rmd, idAgent, dateLundi, pointages, carr);
		Mockito.doReturn(rmd).when(service).checkAgentInactivity(rmd, idAgent, dateLundi, pointages, agent);
		Mockito.doReturn(rmd).when(service).checkPrime7650(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkPrime7651(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkPrime7652(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkAgentTempsPartielAndHSup(rmd, idAgent, dateLundi, pointages, carr);

		// When
		service.processDataConsistency(rmd, idAgent, dateLundi, pointages);

		// Then
		Mockito.verify(service, Mockito.times(1)).checkSprircRecuperation(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkSpcongConge(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkSpabsenMaladie(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkMaxAbsenceHebdo(rmd, idAgent, dateLundi, pointages, carr);
		Mockito.verify(service, Mockito.times(1)).checkAgentINAAndHSup(rmd, idAgent, dateLundi, pointages, carr);
		Mockito.verify(service, Mockito.times(1)).checkAgentInactivity(rmd, idAgent, dateLundi, pointages, agent);
		Mockito.verify(service, Mockito.times(1)).checkPrime7650(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkPrime7651(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkPrime7652(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkAgentTempsPartielAndHSup(rmd, idAgent, dateLundi, pointages,
				carr);
	}

	@Test
	public void checkSprircRecuperation_NoSprirc_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> ptgs = new ArrayList<Pointage>();

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(new ArrayList<Sprirc>());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);

		// When
		ReturnMessageDto result = service.checkSprircRecuperation(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkSprircRecuperation_1Sprirc_NoPointageThatDay_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> ptgs = new ArrayList<Pointage>();

		Sprirc sp = new Sprirc();
		sp.setId(new SprircId(5138, 20130521, 1));
		sp.setDatfin(20130521);
		sp.setCodem2(1);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSprircRecuperation(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkSprircRecuperation_1Sprirc_1PointageThatMorning_ReturnInfo() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 5, 21, 7, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 21, 9, 0, 0).toDate());
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		List<Pointage> ptgs = Arrays.asList(p1);

		// en recup le matin du 21/05/2013
		Sprirc sp = new Sprirc();
		sp.setId(new SprircId(5138, 20130521, 1));
		sp.setDatfin(20130521);
		sp.setCodem2(1);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSprircRecuperation(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(1, result.getInfos().size());
		assertEquals(PointageDataConsistencyRules.AVERT_MESSAGE_ABS, result.getInfos().get(0));
	}

	@Test
	public void checkSprircRecuperation_1Sprirc_1PointageThatDay_ReturnError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 5, 21, 7, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 21, 9, 0, 0).toDate());
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		List<Pointage> ptgs = Arrays.asList(p1);

		// en recup le matin du 21/05/2013
		Sprirc sp = new Sprirc();
		sp.setId(new SprircId(5138, 20130521, 1));
		sp.setDatfin(20130521);
		sp.setCodem2(2);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSprircRecuperation(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("21/05/2013 07:00 : L'agent est en récupération sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkSprircRecuperation_1Sprirc_1PointageThatDayBeforeRecup_ReturnInfo() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 5, 21, 4, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 21, 5, 0, 0).toDate());
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		List<Pointage> ptgs = Arrays.asList(p1);

		// en recup le matin du 21/05/2013
		Sprirc sp = new Sprirc();
		sp.setId(new SprircId(5138, 20130521, 2));
		sp.setDatfin(20130521);
		sp.setCodem2(2);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSprircRecuperation(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(1, result.getInfos().size());
		assertEquals(PointageDataConsistencyRules.AVERT_MESSAGE_ABS, result.getInfos().get(0));
	}

	@Test
	public void checkSprircRecuperation_1Sprirc_1PointageAtEndOfPeriod_ReturnInfo() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 5, 21, 4, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 21, 5, 0, 0).toDate());
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		List<Pointage> ptgs = Arrays.asList(p1);

		// en recup le matin du 21/05/2013
		Sprirc sp = new Sprirc();
		sp.setId(new SprircId(5138, 20130515, 2));
		sp.setDatfin(20130521);
		sp.setCodem2(1);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130515)).thenReturn(new DateTime(2013, 5, 15, 0, 0, 0).toDate());
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSprircRecuperation(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(1, result.getInfos().size());
		assertEquals(PointageDataConsistencyRules.AVERT_MESSAGE_ABS, result.getInfos().get(0));
	}

	@Test
	public void checkSprircRecuperation_1Sprirc_1PointageInsidePeriod_ReturnError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 5, 22, 11, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 22, 12, 0, 0).toDate());
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		List<Pointage> ptgs = Arrays.asList(p1);

		// en recup le matin du 21/05/2013
		Sprirc sp = new Sprirc();
		sp.setId(new SprircId(5138, 20130515, 2));
		sp.setDatfin(20130522);
		sp.setCodem2(2);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130515)).thenReturn(new DateTime(2013, 5, 15, 0, 0, 0).toDate());
		Mockito.when(hS.getDateFromMairieInteger(20130522)).thenReturn(new DateTime(2013, 5, 22, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSprircRecuperation(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("22/05/2013 11:00 : L'agent est en récupération sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkSpcongConge_NoSpcong_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> ptgs = new ArrayList<Pointage>();

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListCongeBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(new ArrayList<Spcong>());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);

		// When
		ReturnMessageDto result = service.checkSpcongConge(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkSpcongConge_1Spcong_NoPointageThatDay_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> ptgs = new ArrayList<Pointage>();

		Spcong sp = new Spcong();
		sp.setId(new SpcongId(5138, 20130521, 0, 0));
		sp.setCodem1(1);
		sp.setDatfin(20130521);
		sp.setCodem2(1);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListCongeBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSpcongConge(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkSpcongConge_1Spcong_1PointageThatDay_ReturnError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 5, 21, 7, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 21, 9, 0, 0).toDate());
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		List<Pointage> ptgs = Arrays.asList(p1);

		// en recup le matin du 21/05/2013
		Spcong sp = new Spcong();
		sp.setId(new SpcongId(5138, 20130521, 0, 0));
		sp.setCodem1(1);
		sp.setDatfin(20130521);
		sp.setCodem2(2);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListCongeBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSpcongConge(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("21/05/2013 07:00 : L'agent est en congés payés sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkSpcongConge_1Spcong_1PointageThatDayBeforeRecup_ReturnInfo() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 5, 21, 4, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 21, 5, 0, 0).toDate());
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		List<Pointage> ptgs = Arrays.asList(p1);

		// en recup le matin du 21/05/2013
		Spcong sp = new Spcong();
		sp.setId(new SpcongId(5138, 20130521, 0, 0));
		sp.setCodem1(1);
		sp.setDatfin(20130521);
		sp.setCodem2(1);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListCongeBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSpcongConge(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(1, result.getInfos().size());
		assertEquals(PointageDataConsistencyRules.AVERT_MESSAGE_ABS, result.getInfos().get(0));
	}

	@Test
	public void checkSpcongConge_1Spcong_1PointageInsidePeriod_ReturnError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 5, 21, 11, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 21, 12, 0, 0).toDate());
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		List<Pointage> ptgs = Arrays.asList(p1);

		// en recup le matin du 21/05/2013
		Spcong sp = new Spcong();
		sp.setId(new SpcongId(5138, 20130515, 0, 0));
		sp.setCodem1(2);
		sp.setDatfin(20130522);
		sp.setCodem2(2);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListCongeBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130515)).thenReturn(new DateTime(2013, 5, 15, 0, 0, 0).toDate());
		Mockito.when(hS.getDateFromMairieInteger(20130522)).thenReturn(new DateTime(2013, 5, 22, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSpcongConge(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("21/05/2013 11:00 : L'agent est en congés payés sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkSpabsenMaladie_NoSpabsen_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> ptgs = new ArrayList<Pointage>();

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListMaladieBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(new ArrayList<Spabsen>());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);

		// When
		ReturnMessageDto result = service.checkSpabsenMaladie(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkSpabsenMaladie_1Spabsen_NoPointageThatDay_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> ptgs = new ArrayList<Pointage>();

		Spabsen sp = new Spabsen();
		sp.setId(new SpabsenId(5138, 20130519, null));
		sp.setDatfin(20130521);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListMaladieBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130519)).thenReturn(new DateTime(2013, 5, 19, 0, 0, 0).toDate());
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSpabsenMaladie(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkSpabsenMaladie_1Spabsen_1PointageThatDay_ReturnError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 5, 21, 7, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 21, 9, 0, 0).toDate());
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		List<Pointage> ptgs = Arrays.asList(p1);

		// en recup le matin du 21/05/2013
		Spabsen sp = new Spabsen();
		sp.setId(new SpabsenId(5138, 20130521, null));
		sp.setDatfin(20130521);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListMaladieBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSpabsenMaladie(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("21/05/2013 07:00 : L'agent est en maladie sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkSpabsenMaladie_1Spabsen_1PointageInsidePeriod_ReturnError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 5, 22, 11, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 22, 12, 0, 0).toDate());
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		List<Pointage> ptgs = Arrays.asList(p1);

		// en recup le matin du 21/05/2013
		Spabsen sp = new Spabsen();
		sp.setId(new SpabsenId(5138, 20130515, null));
		sp.setDatfin(20130527);

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListMaladieBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(Arrays.asList(sp));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130515)).thenReturn(new DateTime(2013, 5, 15, 0, 0, 0).toDate());
		Mockito.when(hS.getDateFromMairieInteger(20130522)).thenReturn(new DateTime(2013, 5, 27, 0, 0, 0).toDate());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkSpabsenMaladie(new ReturnMessageDto(), idAgent, dateLundi, ptgs);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("22/05/2013 11:00 : L'agent est en maladie sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkMaxAbsenceHebdo_Noabsences_ReturnNoError() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 17, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Spcarr carr = new Spcarr();

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		ReturnMessageDto result = service.checkMaxAbsenceHebdo(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1, p2), carr);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkMaxAbsenceHebdo_2absences_NbHeureDepasse_returnError() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		Pointage p2 = new Pointage();
		p2.setDateDebut(new DateTime(2013, 05, 18, 10, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 18, 18, 0, 0).toDate()); // 7h45
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Spbhor hor = new Spbhor();
		hor.setTaux(0.5);
		Spbase bas = new Spbase();
		bas.setNbashh(32);
		Spcarr car = new Spcarr();
		car.setSpbhor(hor);
		car.setSpbase(bas);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		ReturnMessageDto result = service.checkMaxAbsenceHebdo(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1, p2), car);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent dépasse sa base horaire", result.getErrors().get(0));
	}

	@Test
	public void checkMaxAbsenceHebdo_2absences_NbHeureNonDepasse_returnError() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		Pointage p2 = new Pointage();
		p2.setDateDebut(new DateTime(2013, 05, 18, 10, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 18, 17, 0, 0).toDate()); // 7h
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Spbhor hor = new Spbhor();
		hor.setTaux(0.5);
		Spbase bas = new Spbase();
		bas.setNbashh(32);
		Spcarr car = new Spcarr();
		car.setSpbhor(hor);
		car.setSpbase(bas);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		ReturnMessageDto result = service.checkMaxAbsenceHebdo(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1, p2), car);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentINAAndHSup_INALessThan315_NoError() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(205);
		Spbase bas = new Spbase();
		bas.setCdBase("A");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setSpbase(bas);
		car.setCdcate(1);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentINAAndHSup_INAEq315_NoError() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(315);
		Spbase bas = new Spbase();
		bas.setCdBase("A");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setSpbase(bas);
		car.setCdcate(1);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentINAAndHSup_INASupTo315_NotDPM_returnError() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(316);
		Spbase bas = new Spbase();
		bas.setCdBase("A");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setSpbase(bas);
		car.setCdcate(1);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		SirhWsServiceDto dtoService = new SirhWsServiceDto();
		dtoService.setSigle("TITI");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(idAgent)).thenReturn(dtoService);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent n'a pas droit aux HS sur la période (INA > 315)", result.getErrors().get(0));
	}

	@Test
	public void checkAgentINAAndHSup_INASupTo315_DPM_returnNothing() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(316);
		Spbase bas = new Spbase();
		bas.setCdBase("A");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setSpbase(bas);
		car.setCdcate(1);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		SirhWsServiceDto dtoService = new SirhWsServiceDto();
		dtoService.setSigle("DPM");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(idAgent)).thenReturn(dtoService);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentINAAndHSup_INASupTo315_Not_F_returnNothing() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(316);
		Spbase bas = new Spbase();
		bas.setCdBase("A");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setSpbase(bas);
		car.setCdcate(4);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentINAAndHSup_INALessThan315ButZ_NotDPM_returnError() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(315);
		Spbase bas = new Spbase();
		bas.setCdBase("Z");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setSpbase(bas);
		car.setCdcate(1);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		
		SirhWsServiceDto dtoService = new SirhWsServiceDto();
		dtoService.setSigle("TITI");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(idAgent)).thenReturn(dtoService);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent est en base horaire \"Z\" sur la période", result.getErrors().get(0));
	}

	@Test
	public void checkAgentINAAndHSup_INALessThan315ButZ_DPM_ReturnNothing() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(315);
		Spbase bas = new Spbase();
		bas.setCdBase("Z");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setSpbase(bas);
		car.setCdcate(1);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		
		SirhWsServiceDto dtoService = new SirhWsServiceDto();
		dtoService.setSigle("DPM");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(idAgent)).thenReturn(dtoService);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car);

		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals(0, result.getErrors().size());
	}

	@Test
	public void checkAgentInactivity_AgentIsActive_ReturnNothing() {

		// Given
		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9007865);
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spadmn sp = new Spadmn();
		sp.setCdpadm("01");

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgentCurrentPosition(ag, dateLundi)).thenReturn(sp);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);

		// When
		ReturnMessageDto result = service.checkAgentInactivity(new ReturnMessageDto(), 9007865, dateLundi, null, ag);

		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals(0, result.getErrors().size());
	}

	@Test
	public void checkAgentInactivity_AgentIsInActive_ReturnError() {

		// Given
		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9007865);
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spadmn sp = new Spadmn();
		sp.setCdpadm("99");

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgentCurrentPosition(ag, dateLundi)).thenReturn(sp);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);

		// When
		ReturnMessageDto result = service.checkAgentInactivity(new ReturnMessageDto(), 9007865, dateLundi, null, ag);

		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals(1, result.getErrors().size());
		assertEquals("L'agent n'est pas en activité sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkPrime7650_PointageIsBetweenMonToFri_Ok() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7650);
		p4.setDateDebut(new LocalDate(2013, 9, 25).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		service.checkPrime7650(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7650_PointageIsBetweenSat_NotOk() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7650);
		p4.setDateDebut(new LocalDate(2013, 9, 28).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		service.checkPrime7650(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(
				"La prime 7650 du 28/09/2013 n'est pas valide. Elle ne peut être saisie que du lundi au vendredi.",
				result.getErrors().get(0));
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7650_PointageIsSun_NotOk() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7650);
		p4.setDateDebut(new LocalDate(2013, 9, 29).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		service.checkPrime7650(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(
				"La prime 7650 du 29/09/2013 n'est pas valide. Elle ne peut être saisie que du lundi au vendredi.",
				result.getErrors().get(0));
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7651_PointageIsSatAndSun_Ok() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7651);
		p4.setDateDebut(new LocalDate(2013, 9, 28).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		Pointage p5 = new Pointage();
		p5.setType(new RefTypePointage());
		p5.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p5.setRefPrime(new RefPrime());
		p5.getRefPrime().setNoRubr(7651);
		p5.setDateDebut(new LocalDate(2013, 9, 29).toDate());
		p5.setQuantite(1);
		p5.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3, p5);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		service.checkPrime7650(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7651_PointageIsWedNotHolidayNotEve_NotOk() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7651);
		p4.setDateDebut(new LocalDate(2013, 9, 28).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		Pointage p5 = new Pointage();
		p5.setType(new RefTypePointage());
		p5.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p5.setRefPrime(new RefPrime());
		p5.getRefPrime().setNoRubr(7651);
		p5.setDateDebut(new LocalDate(2013, 9, 25).toDate());
		p5.setQuantite(1);
		p5.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3, p5);

		ISirhWSConsumer holS = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(holS.isHoliday(new LocalDate(2013, 9, 25))).thenReturn(false);
		Mockito.when(holS.isHoliday(new LocalDate(2013, 9, 26))).thenReturn(false);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", holS);

		// When
		service.checkPrime7651(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(
				"La prime 7651 du 25/09/2013 n'est pas valide. Elle ne peut être saisie qu'un samedi et dimanche, ou alors une veille et jour férié.",
				result.getErrors().get(0));
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7651_PointageIsHoliday_Ok() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7651);
		p4.setDateDebut(new LocalDate(2013, 9, 24).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3);

		ISirhWSConsumer holS = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(holS.isHoliday(new DateTime(2013, 9, 24, 0, 0, 0))).thenReturn(true);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", holS);

		// When
		service.checkPrime7651(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7651_PointageIsSun_Ok() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7651);
		p4.setDateDebut(new LocalDate(2013, 9, 29).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		service.checkPrime7651(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7651_PointageIsSat_Ok() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7651);
		p4.setDateDebut(new LocalDate(2013, 9, 28).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		service.checkPrime7651(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7651_PointageIsEve_Ok() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7651);
		p4.setDateDebut(new LocalDate(2013, 9, 23).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3);

		ISirhWSConsumer holS = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(holS.isHoliday(new DateTime(2013, 9, 23, 0, 0, 0))).thenReturn(false);
		Mockito.when(holS.isHoliday(new DateTime(2013, 9, 24, 0, 0, 0))).thenReturn(true);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", holS);

		// When
		service.checkPrime7651(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7652_PointageIsHoliday_Ok() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7652);
		p4.setDateDebut(new LocalDate(2013, 9, 24).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3);

		ISirhWSConsumer holS = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(holS.isHoliday(new DateTime(2013, 9, 24, 0, 0, 0))).thenReturn(true);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", holS);

		// When
		service.checkPrime7652(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7652_PointageIsSun_Ok() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7652);
		p4.setDateDebut(new LocalDate(2013, 9, 29).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		service.checkPrime7652(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7652_PointageIsSat_NotOk() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		Pointage p3 = new Pointage();
		p3.setType(new RefTypePointage());
		p3.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(8878);

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7652);
		p4.setDateDebut(new LocalDate(2013, 9, 28).toDate());
		p4.setQuantite(1);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p1, p2, p4, p3);

		ISirhWSConsumer holS = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(holS.isHoliday(new DateTime(2013, 9, 28, 0, 0, 0))).thenReturn(false);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", holS);

		// When
		service.checkPrime7652(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(
				"La prime 7652 du 28/09/2013 n'est pas valide. Elle ne peut être saisie qu'un dimanche ou jour férié.",
				result.getErrors().get(0));
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7704_PointageQuantite_NotOk() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7704);
		p4.setDateDebut(new LocalDate(2013, 9, 28).toDate());
		p4.setQuantite(3);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p4);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		service.checkPrime7704(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals("La prime 7704 du 28/09/2013 n'est pas valide. Sa quantité ne peut être supérieur à 2.", result
				.getErrors().get(0));
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkPrime7704_PointageQuantite_Ok() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new LocalDate(2013, 9, 23).toDate();
		ReturnMessageDto result = new ReturnMessageDto();

		Pointage p4 = new Pointage();
		p4.setType(new RefTypePointage());
		p4.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p4.setRefPrime(new RefPrime());
		p4.getRefPrime().setNoRubr(7704);
		p4.setDateDebut(new LocalDate(2013, 9, 28).toDate());
		p4.setQuantite(2);
		p4.setDateLundi(dateLundi);

		List<Pointage> ptgs = Arrays.asList(p4);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		service.checkPrime7704(result, idAgent, dateLundi, ptgs);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkDateLundiAnterieurA3Mois_OK() {

		ReturnMessageDto srm = new ReturnMessageDto();

		Date dateLundi = new Date();

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		srm = service.checkDateLundiAnterieurA3Mois(srm, dateLundi);

		assertEquals(0, srm.getErrors().size());
	}

	@Test
	public void checkDateLundiAnterieurA3Mois_KO() {

		ReturnMessageDto srm = new ReturnMessageDto();

		GregorianCalendar calStr1 = new GregorianCalendar();
		calStr1.setTime(new Date());
		calStr1.add(GregorianCalendar.MONTH, -3);
		calStr1.add(GregorianCalendar.WEEK_OF_YEAR, -2); // back to previous
															// week
		calStr1.set(GregorianCalendar.DAY_OF_WEEK, Calendar.MONDAY);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		srm = service.checkDateLundiAnterieurA3Mois(srm, calStr1.getTime());

		assertEquals(1, srm.getErrors().size());
	}

	@Test
	public void checkAgentTempsPartielAndHSup_NoTempsPartiel_OK() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbase bas = new Spbase();
		bas.setCdBase("A");
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1.0);
		Spcarr car = new Spcarr();
		car.setSpbase(bas);
		car.setSpbhor(spbhor);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		ReturnMessageDto result = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentTempsPartielAndHSup_TempsPartiel_OK() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbase bas = new Spbase();
		bas.setCdBase("A");
		bas.setNbashh(20.0);
		bas.setNbasch(39.0);
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(0.5);
		Spcarr car = new Spcarr();
		car.setSpbase(bas);
		car.setSpbhor(spbhor);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getNbashh())).thenReturn(1200);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getNbasch())).thenReturn(2340);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentTempsPartielAndHSup_KO() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbase bas = new Spbase();
		bas.setCdBase("A");
		bas.setNbashh(20.0);
		bas.setNbasch(39.0);
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(0.5);
		Spcarr car = new Spcarr();
		car.setSpbase(bas);
		car.setSpbhor(spbhor);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 18, 16, 15, 0).toDate()); // 33h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getNbashh())).thenReturn(1200);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getNbasch())).thenReturn(2340);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(600)).thenReturn(10.0d);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent est en temps partiel, il ne peut pas avoir plus de 29 heures supplémentaires.", result
				.getErrors().get(0));
	}

	@Test
	public void checkAgentTempsPartielAndHSup_TempsPartiel_WithAbsence_OK() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbase bas = new Spbase();
		bas.setCdBase("A");
		bas.setNbashh(20.0);
		bas.setNbasch(39.0);
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(0.5);
		Spcarr car = new Spcarr();
		car.setSpbase(bas);
		car.setSpbhor(spbhor);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 17, 8, 15, 0).toDate()); // 1h
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getNbashh())).thenReturn(1200);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getNbasch())).thenReturn(2340);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1, p2), car);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentTempsPartielAndHSup_WithAbsence_KO() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbase bas = new Spbase();
		bas.setCdBase("A");
		bas.setNbashh(20.5);
		bas.setNbasch(39.0);
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(0.5);
		Spcarr car = new Spcarr();
		car.setSpbase(bas);
		car.setSpbhor(spbhor);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 18, 16, 15, 0).toDate()); // 33h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 17, 8, 15, 0).toDate()); // 1h
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getNbashh())).thenReturn(1200);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getNbasch())).thenReturn(2340);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(600)).thenReturn(10.485d);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1, p2), car);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent est en temps partiel, il ne peut pas avoir plus de 28,52 heures supplémentaires.", result
				.getErrors().get(0));
	}

	@Test
	public void checkHeureFinSaisieHSup_noErrorBecauseNoHSup() {

		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Spcarr car = new Spcarr();

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 17, 8, 15, 0).toDate()); // 1h
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		// When
		ReturnMessageDto result = service.checkHeureFinSaisieHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p2), car);

		assertEquals(0, result.getInfos().size());
		assertEquals(0, result.getErrors().size());
	}

	@Test
	public void checkHeureFinSaisieHSup_1Error_fonctionnaire() {

		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Spcarr car = new Spcarr();
		car.setCdcate(1);

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.setDateDebut(new DateTime(2013, 05, 17, 21, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 18, 4, 15, 0).toDate()); // 1h
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		// When
		ReturnMessageDto result = service.checkHeureFinSaisieHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p2), car);

		assertEquals(0, result.getInfos().size());
		assertEquals(1, result.getErrors().size());
		assertEquals(
				result.getErrors().get(0),
				String.format(
						"L'heure de fin pour les Heures Sup. saisie le %s ne peut pas dépasser 4h (limite des heures de nuit).",
						new DateTime(p2.getDateDebut()).toString("dd/MM/yyyy")));
	}

	@Test
	public void checkHeureFinSaisieHSup_Ok_fonctionnaire() {

		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Spcarr car = new Spcarr();
		car.setCdcate(6); // fonctionnaire

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.setDateDebut(new DateTime(2013, 05, 17, 21, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 18, 4, 0, 0).toDate()); // 1h
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		// When
		ReturnMessageDto result = service.checkHeureFinSaisieHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p2), car);

		assertEquals(0, result.getInfos().size());
		assertEquals(0, result.getErrors().size());
	}

	@Test
	public void checkHeureFinSaisieHSup_Ok_contractuel() {

		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Spcarr car = new Spcarr();
		car.setCdcate(4); // contractuel

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.setDateDebut(new DateTime(2013, 05, 17, 21, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 18, 5, 0, 0).toDate()); // 1h
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		// When
		ReturnMessageDto result = service.checkHeureFinSaisieHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p2), car);

		assertEquals(0, result.getInfos().size());
		assertEquals(0, result.getErrors().size());
	}

	@Test
	public void checkHeureFinSaisieHSup_1Error_contractuel() {

		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Spcarr car = new Spcarr();
		car.setCdcate(4); // contractuel

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.setDateDebut(new DateTime(2013, 05, 17, 21, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 18, 5, 15, 0).toDate()); // 1h
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		// When
		ReturnMessageDto result = service.checkHeureFinSaisieHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p2), car);

		assertEquals(0, result.getInfos().size());
		assertEquals(1, result.getErrors().size());
		assertEquals(
				result.getErrors().get(0),
				String.format(
						"L'heure de fin pour les Heures Sup. saisie le %s ne peut pas dépasser 5h (limite des heures de nuit).",
						new DateTime(p2.getDateDebut()).toString("dd/MM/yyyy")));
	}

	@Test
	public void checkHeureFinSaisieHSup_1Error_ConvColl() {

		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Spcarr car = new Spcarr();
		car.setCdcate(7); // convention collective

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.setDateDebut(new DateTime(2013, 05, 17, 21, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 18, 4, 15, 0).toDate()); // 1h
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		// When
		ReturnMessageDto result = service.checkHeureFinSaisieHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p2), car);

		assertEquals(0, result.getInfos().size());
		assertEquals(1, result.getErrors().size());
		assertEquals(
				result.getErrors().get(0),
				String.format(
						"L'heure de fin pour les Heures Sup. saisie le %s ne peut pas dépasser 4h (limite des heures de nuit).",
						new DateTime(p2.getDateDebut()).toString("dd/MM/yyyy")));
	}

	@Test
	public void checkHeureFinSaisieHSup_Ok_ConvColl() {

		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Spcarr car = new Spcarr();
		car.setCdcate(7); // convention collective

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.setDateDebut(new DateTime(2013, 05, 17, 21, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 18, 4, 0, 0).toDate()); // 1h
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		// When
		ReturnMessageDto result = service.checkHeureFinSaisieHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p2), car);

		assertEquals(0, result.getInfos().size());
		assertEquals(0, result.getErrors().size());
	}
}
