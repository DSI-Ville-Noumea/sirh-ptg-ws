package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.SaisieReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.ptg.service.impl.PointageDataConsistencyRules;
import nc.noumea.mairie.sirh.domain.Agent;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class PointageDataConsistencyRulesTest {

	@Test
	public void checkSprircRecuperation_NoSprirc_NoError() {
		
		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> ptgs = new ArrayList<Pointage>();
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(new ArrayList<Sprirc>());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkSprircRecuperation(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSprircRecuperation(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSprircRecuperation(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSprircRecuperation(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSprircRecuperation(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130515)).thenReturn(new DateTime(2013, 5, 15, 0, 0, 0).toDate());
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSprircRecuperation(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130515)).thenReturn(new DateTime(2013, 5, 15, 0, 0, 0).toDate());
		Mockito.when(hS.getDateFromMairieInteger(20130522)).thenReturn(new DateTime(2013, 5, 22, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSprircRecuperation(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListCongeBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(new ArrayList<Spcong>());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkSpcongConge(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListCongeBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSpcongConge(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListCongeBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSpcongConge(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListCongeBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSpcongConge(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListCongeBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130515)).thenReturn(new DateTime(2013, 5, 15, 0, 0, 0).toDate());
		Mockito.when(hS.getDateFromMairieInteger(20130522)).thenReturn(new DateTime(2013, 5, 22, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSpcongConge(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListMaladieBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(new ArrayList<Spabsen>());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkSpabsenMaladie(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListMaladieBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130519)).thenReturn(new DateTime(2013, 5, 19, 0, 0, 0).toDate());
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSpabsenMaladie(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListMaladieBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSpabsenMaladie(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
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
		Mockito.when(mRepo.getListMaladieBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130515)).thenReturn(new DateTime(2013, 5, 15, 0, 0, 0).toDate());
		Mockito.when(hS.getDateFromMairieInteger(20130522)).thenReturn(new DateTime(2013, 5, 27, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		SaisieReturnMessageDto result = service.checkSpabsenMaladie(new SaisieReturnMessageDto(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("22/05/2013 11:00 : L'agent est en maladie sur cette période.", result.getErrors().get(0));
	}
	
	@Test
	public void checkMaxAbsenceHebdo_Noabsences_ReturnNoError() {
		
		// Given
		Agent ag = new Agent();
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 17, 0, 0, 0).toDate();
		
		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(idAgent)).thenReturn(ag);
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkMaxAbsenceHebdo(new SaisieReturnMessageDto(), idAgent, dateLundi, Arrays.asList(p1, p2));
		
		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}
	
	@Test
	public void checkMaxAbsenceHebdo_2absences_NbHeureDepasse_returnError() {
		
		// Given
		Agent ag = new Agent();
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
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(idAgent)).thenReturn(ag);
		Mockito.when(mRepo.getAgentCurrentCarriere(ag, dateLundi)).thenReturn(car);
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkMaxAbsenceHebdo(new SaisieReturnMessageDto(), idAgent, dateLundi, Arrays.asList(p1, p2));
		
		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent dépasse sa base horaire", result.getErrors().get(0));
	}
	
	@Test
	public void checkMaxAbsenceHebdo_2absences_NbHeureNonDepasse_returnError() {
		
		// Given
		Agent ag = new Agent();
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
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(idAgent)).thenReturn(ag);
		Mockito.when(mRepo.getAgentCurrentCarriere(ag, dateLundi)).thenReturn(car);
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkMaxAbsenceHebdo(new SaisieReturnMessageDto(), idAgent, dateLundi, Arrays.asList(p1, p2));
		
		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}
	
	@Test
	public void checkAgentINAAndHSup_INALessThan315_NoError() {
		
		// Given
		Agent ag = new Agent();
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
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(idAgent)).thenReturn(ag);
		Mockito.when(mRepo.getAgentCurrentCarriere(ag, dateLundi)).thenReturn(car);
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkAgentINAAndHSup(new SaisieReturnMessageDto(), idAgent, dateLundi, Arrays.asList(p1));
		
		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}
	
	@Test
	public void checkAgentINAAndHSup_INAEq315_NoError() {
		
		// Given
		Agent ag = new Agent();
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
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(idAgent)).thenReturn(ag);
		Mockito.when(mRepo.getAgentCurrentCarriere(ag, dateLundi)).thenReturn(car);
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkAgentINAAndHSup(new SaisieReturnMessageDto(), idAgent, dateLundi, Arrays.asList(p1));
		
		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}
	
	@Test
	public void checkAgentINAAndHSup_INASupTo315_returnError() {
		
		// Given
		Agent ag = new Agent();
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
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(idAgent)).thenReturn(ag);
		Mockito.when(mRepo.getAgentCurrentCarriere(ag, dateLundi)).thenReturn(car);
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkAgentINAAndHSup(new SaisieReturnMessageDto(), idAgent, dateLundi, Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent n'a pas droit aux HS sur la période (INA > 315)", result.getErrors().get(0));
	}
	
	@Test
	public void checkAgentINAAndHSup_INASupTo315_Not_F_returnNothing() {
		
		// Given
		Agent ag = new Agent();
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
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(idAgent)).thenReturn(ag);
		Mockito.when(mRepo.getAgentCurrentCarriere(ag, dateLundi)).thenReturn(car);
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkAgentINAAndHSup(new SaisieReturnMessageDto(), idAgent, dateLundi, Arrays.asList(p1));
		
		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}
	
	@Test
	public void checkAgentINAAndHSup_INALessThan315ButZ_returnError() {
		
		// Given
		Agent ag = new Agent();
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
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(idAgent)).thenReturn(ag);
		Mockito.when(mRepo.getAgentCurrentCarriere(ag, dateLundi)).thenReturn(car);
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkAgentINAAndHSup(new SaisieReturnMessageDto(), idAgent, dateLundi, Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent est en base horaire \"Z\" sur la période", result.getErrors().get(0));
	}

	@Test
	public void checkAgentInactivity_AgentIsActive_ReturnNothing() {
		
		// Given
		Agent ag = new Agent();
		ag.setIdAgent(9007865);
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		
		Spadmn sp = new Spadmn();
		sp.setCdpadm("01");
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(9007865)).thenReturn(ag);
		Mockito.when(mRepo.getAgentCurrentPosition(ag, dateLundi)).thenReturn(sp);
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkAgentInactivity(new SaisieReturnMessageDto(), 9007865, dateLundi, null);
		
		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals(0, result.getErrors().size());
	}
	
	@Test
	public void checkAgentInactivity_AgentIsInActive_ReturnError() {
		
		// Given
		Agent ag = new Agent();
		ag.setIdAgent(9007865);
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		
		Spadmn sp = new Spadmn();
		sp.setCdpadm("99");
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgent(9007865)).thenReturn(ag);
		Mockito.when(mRepo.getAgentCurrentPosition(ag, dateLundi)).thenReturn(sp);
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		
		// When
		SaisieReturnMessageDto result = service.checkAgentInactivity(new SaisieReturnMessageDto(), 9007865, dateLundi, null);
		
		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals(1, result.getErrors().size());
		assertEquals("L'agent n'est pas en activité sur cette période.", result.getErrors().get(0));
	}
}