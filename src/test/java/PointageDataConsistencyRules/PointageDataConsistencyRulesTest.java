package PointageDataConsistencyRules;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.SpabsenId;
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
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.service.HelperService;
import nc.noumea.mairie.ptg.service.PointageDataConsistencyRules;
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
		List<String> result = service.checkSprircRecuperation(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(0, result.size());
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
		List<String> result = service.checkSprircRecuperation(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(0, result.size());
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
		sp.setCodem2(1);
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListRecuperationBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		List<String> result = service.checkSprircRecuperation(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(1, result.size());
		assertEquals("21/05/2013 09:00 : L'agent est en récupération sur cette période.", result.get(0));
	}
	
	@Test
	public void checkSprircRecuperation_1Sprirc_1PointageThatDayBeforeRecup_ReturnNoError() {
		
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
		List<String> result = service.checkSprircRecuperation(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(0, result.size());
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
		List<String> result = service.checkSprircRecuperation(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(1, result.size());
		assertEquals("22/05/2013 11:00 : L'agent est en récupération sur cette période.", result.get(0));
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
		List<String> result = service.checkSpcongConge(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(0, result.size());
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
		List<String> result = service.checkSpcongConge(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(0, result.size());
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
		sp.setCodem2(1);
		
		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getListCongeBetween(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate())).thenReturn(Arrays.asList(sp));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getDateFromMairieInteger(20130521)).thenReturn(new DateTime(2013, 5, 21, 0, 0, 0).toDate());
		
		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		List<String> result = service.checkSpcongConge(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(1, result.size());
		assertEquals("21/05/2013 09:00 : L'agent est en congés payés sur cette période.", result.get(0));
	}
	
	@Test
	public void checkSpcongConge_1Spcong_1PointageThatDayBeforeRecup_ReturnNoError() {
		
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
		List<String> result = service.checkSpcongConge(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(0, result.size());
	}
	
	@Test
	public void checkSpcongConge_1Spcong_1PointageInsidePeriod_ReturnError() {
		
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
		List<String> result = service.checkSpcongConge(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(1, result.size());
		assertEquals("22/05/2013 11:00 : L'agent est en congés payés sur cette période.", result.get(0));
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
		List<String> result = service.checkSpabsenMaladie(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(0, result.size());
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
		List<String> result = service.checkSpabsenMaladie(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(0, result.size());
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
		List<String> result = service.checkSpabsenMaladie(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(1, result.size());
		assertEquals("21/05/2013 : L'agent est en maladie sur cette période.", result.get(0));
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
		List<String> result = service.checkSpabsenMaladie(new ArrayList<String>(), idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(1, result.size());
		assertEquals("22/05/2013 : L'agent est en maladie sur cette période.", result.get(0));
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
		List<String> result = service.checkMaxAbsenceHebdo(new ArrayList<String>(), idAgent, dateLundi, Arrays.asList(p1, p2));
		
		// Then
		assertEquals(0, result.size());
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
		List<String> result = service.checkMaxAbsenceHebdo(new ArrayList<String>(), idAgent, dateLundi, Arrays.asList(p1, p2));
		
		// Then
		assertEquals(1, result.size());
		assertEquals("L'agent dépasse sa base horaire", result.get(0));
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
		List<String> result = service.checkMaxAbsenceHebdo(new ArrayList<String>(), idAgent, dateLundi, Arrays.asList(p1, p2));
		
		// Then
		assertEquals(0, result.size());
	}
	
	@Test
	public void checkAgentINAAndHSup_INALessThan315_NoError() {
		
		// Given
		Agent ag = new Agent();
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		
		Spbarem barem = new Spbarem();
		barem.setIna(205);
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		
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
		List<String> result = service.checkAgentINAAndHSup(new ArrayList<String>(), idAgent, dateLundi, Arrays.asList(p1));
		
		// Then
		assertEquals(0, result.size());
	}
	
	@Test
	public void checkAgentINAAndHSup_INAEq315_NoError() {
		
		// Given
		Agent ag = new Agent();
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		
		Spbarem barem = new Spbarem();
		barem.setIna(315);
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		
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
		List<String> result = service.checkAgentINAAndHSup(new ArrayList<String>(), idAgent, dateLundi, Arrays.asList(p1));
		
		// Then
		assertEquals(0, result.size());
	}
	
	@Test
	public void checkAgentINAAndHSup_INASupTo315_NoError() {
		
		// Given
		Agent ag = new Agent();
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		
		Spbarem barem = new Spbarem();
		barem.setIna(316);
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		
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
		List<String> result = service.checkAgentINAAndHSup(new ArrayList<String>(), idAgent, dateLundi, Arrays.asList(p1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals("L'agent n'a pas droit aux HS sur la période (INA > 315)", result.get(0));
	}
}
