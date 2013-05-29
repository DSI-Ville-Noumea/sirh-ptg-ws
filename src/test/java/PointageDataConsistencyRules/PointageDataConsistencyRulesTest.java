package PointageDataConsistencyRules;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Sprirc;
import nc.noumea.mairie.domain.SprircId;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.service.HelperService;
import nc.noumea.mairie.ptg.service.PointageDataConsistencyRules;

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
		List<String> result = service.checkSprircRecuperation(idAgent, dateLundi, ptgs);
		
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
		List<String> result = service.checkSprircRecuperation(idAgent, dateLundi, ptgs);
		
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
		List<String> result = service.checkSprircRecuperation(idAgent, dateLundi, ptgs);
		
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
		List<String> result = service.checkSprircRecuperation(idAgent, dateLundi, ptgs);
		
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
		List<String> result = service.checkSprircRecuperation(idAgent, dateLundi, ptgs);
		
		// Then
		assertEquals(1, result.size());
		assertEquals("22/05/2013 11:00 : L'agent est en récupération sur cette période.", result.get(0));
	}
}
