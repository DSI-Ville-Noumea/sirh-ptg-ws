package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.domain.Spabsen;
import nc.noumea.mairie.domain.SpabsenId;
import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.Spbarem;
import nc.noumea.mairie.domain.Spbhor;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.sirh.dto.BaseHorairePointageDto;
import nc.noumea.mairie.ws.IAbsWsConsumer;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class PointageDataConsistencyRulesTest {

	@Test
	public void processDataConsistency_CheckDataMethodsAreCalled() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		ReturnMessageDto rmd = new ReturnMessageDto();
		Spcarr carr = new Spcarr();
		AgentGeneriqueDto agent = new AgentGeneriqueDto();

		BaseHorairePointageDto base = new BaseHorairePointageDto();

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgentCurrentCarriere(agent, dateLundi)).thenReturn(carr);

		ISirhWSConsumer sRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sRepo.getAgent(idAgent)).thenReturn(agent);
		Mockito.when(sRepo.getBaseHorairePointageAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(base);

		PointageDataConsistencyRules service = Mockito.spy(new PointageDataConsistencyRules());
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sRepo);

		Mockito.doReturn(rmd).when(service).checkRecuperation(rmd, idAgent, pointages);
		Mockito.doReturn(rmd).when(service).checkReposComp(rmd, idAgent, pointages);
		Mockito.doReturn(rmd).when(service).checkAbsencesSyndicales(rmd, idAgent, pointages);
		Mockito.doReturn(rmd).when(service).checkCongesExceptionnels(rmd, idAgent, pointages);
		Mockito.doReturn(rmd).when(service).checkCongeAnnuel(rmd, idAgent, pointages);
		Mockito.doReturn(rmd).when(service).checkSpabsenMaladie(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkMaxAbsenceHebdo(rmd, idAgent, dateLundi, pointages, carr, base);
		Mockito.doReturn(rmd).when(service).checkAgentINAAndHSup(rmd, idAgent, dateLundi, pointages, carr, base);
		Mockito.doReturn(rmd).when(service).checkAgentInactivity(rmd, idAgent, dateLundi, pointages, agent);
		Mockito.doReturn(rmd).when(service).checkPrime7650(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkPrime7651(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkPrime7652(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service)
				.checkAgentTempsPartielAndHSup(rmd, idAgent, dateLundi, pointages, carr, base, false);

		// When
		service.processDataConsistency(rmd, idAgent, dateLundi, pointages, false);

		// Then
		Mockito.verify(service, Mockito.times(1)).checkRecuperation(rmd, idAgent, pointages);
		Mockito.verify(service, Mockito.times(1)).checkReposComp(rmd, idAgent, pointages);
		Mockito.verify(service, Mockito.times(1)).checkAbsencesSyndicales(rmd, idAgent, pointages);
		Mockito.verify(service, Mockito.times(1)).checkCongesExceptionnels(rmd, idAgent, pointages);
		Mockito.verify(service, Mockito.times(1)).checkCongeAnnuel(rmd, idAgent, pointages);
		Mockito.verify(service, Mockito.times(1)).checkSpabsenMaladie(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkMaxAbsenceHebdo(rmd, idAgent, dateLundi, pointages, carr, base);
		Mockito.verify(service, Mockito.times(1)).checkAgentINAAndHSup(rmd, idAgent, dateLundi, pointages, carr, base);
		Mockito.verify(service, Mockito.times(1)).checkAgentInactivity(rmd, idAgent, dateLundi, pointages, agent);
		Mockito.verify(service, Mockito.times(1)).checkPrime7650(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkPrime7651(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkPrime7652(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkAgentTempsPartielAndHSup(rmd, idAgent, dateLundi, pointages,
				carr, base, false);
	}
	
	@Test
	public void processDataConsistency_CheckDataMethodsAreCalled_pasAffectation() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFinSemaine = new DateTime(dateLundi).plusDays(7).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		ReturnMessageDto rmd = new ReturnMessageDto();
		Spcarr carr = new Spcarr();
		AgentGeneriqueDto agent = new AgentGeneriqueDto();

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgentCurrentCarriere(agent, dateLundi)).thenReturn(carr);

		ISirhWSConsumer sRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sRepo.getAgent(idAgent)).thenReturn(agent);
		Mockito.when(sRepo.getBaseHorairePointageAgent(idAgent, dateLundi, dateFinSemaine)).thenReturn(null);

		PointageDataConsistencyRules service = Mockito.spy(new PointageDataConsistencyRules());
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sRepo);

		Mockito.doReturn(rmd).when(service).checkRecuperation(rmd, idAgent, pointages);
		Mockito.doReturn(rmd).when(service).checkReposComp(rmd, idAgent, pointages);
		Mockito.doReturn(rmd).when(service).checkAbsencesSyndicales(rmd, idAgent, pointages);
		Mockito.doReturn(rmd).when(service).checkCongesExceptionnels(rmd, idAgent, pointages);
		Mockito.doReturn(rmd).when(service).checkCongeAnnuel(rmd, idAgent, pointages);
		Mockito.doReturn(rmd).when(service).checkSpabsenMaladie(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkMaxAbsenceHebdo(rmd, idAgent, dateLundi, pointages, carr, null);
		Mockito.doReturn(rmd).when(service).checkAgentINAAndHSup(rmd, idAgent, dateLundi, pointages, carr, null);
		Mockito.doReturn(rmd).when(service).checkAgentInactivity(rmd, idAgent, dateLundi, pointages, agent);
		Mockito.doReturn(rmd).when(service).checkPrime7650(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkPrime7651(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service).checkPrime7652(rmd, idAgent, dateLundi, pointages);
		Mockito.doReturn(rmd).when(service)
				.checkAgentTempsPartielAndHSup(rmd, idAgent, dateLundi, pointages, carr, null, false);

		// When
		service.processDataConsistency(rmd, idAgent, dateLundi, pointages, false);

		// Then
		Mockito.verify(service, Mockito.times(1)).checkRecuperation(rmd, idAgent, pointages);
		Mockito.verify(service, Mockito.times(1)).checkReposComp(rmd, idAgent, pointages);
		Mockito.verify(service, Mockito.times(1)).checkAbsencesSyndicales(rmd, idAgent, pointages);
		Mockito.verify(service, Mockito.times(1)).checkCongesExceptionnels(rmd, idAgent, pointages);
		Mockito.verify(service, Mockito.times(1)).checkCongeAnnuel(rmd, idAgent, pointages);
		Mockito.verify(service, Mockito.times(1)).checkSpabsenMaladie(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkMaxAbsenceHebdo(rmd, idAgent, dateLundi, pointages, carr, null);
		Mockito.verify(service, Mockito.times(1)).checkAgentINAAndHSup(rmd, idAgent, dateLundi, pointages, carr, null);
		Mockito.verify(service, Mockito.times(1)).checkAgentInactivity(rmd, idAgent, dateLundi, pointages, agent);
		Mockito.verify(service, Mockito.times(1)).checkPrime7650(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkPrime7651(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkPrime7652(rmd, idAgent, dateLundi, pointages);
		Mockito.verify(service, Mockito.times(1)).checkAgentTempsPartielAndHSup(rmd, idAgent, dateLundi, pointages,
				carr, null, false);
		
		assertEquals(rmd.getErrors().get(0), "L'agent n'a pas d'affectation ou la base horaire de pointage n'y est pas renseignée."); 
	}

	@Test
	public void checkRecuperation_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkRecuperation(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(new ReturnMessageDto());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkRecuperation(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkRecuperation_ReturnErrors() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2013, 5, 20, 2, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		Pointage p = new Pointage();
		p.setDateDebut(dateDebut);
		p.setDateFin(dateFin);
		p.setType(type);
		pointages.add(p);

		ReturnMessageDto res = new ReturnMessageDto();
		res.getErrors().add("21/05/2013 : L'agent est en récupération sur cette période.");

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkRecuperation(idAgent, dateDebut, dateFin)).thenReturn(res);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkRecuperation(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("21/05/2013 : L'agent est en récupération sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkRecuperation_withPrime() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2013, 5, 20, 2, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		Pointage p = new Pointage();
		p.setDateDebut(dateDebut);
		p.setDateFin(dateFin);
		p.setType(type);
		pointages.add(p);

		ReturnMessageDto res = new ReturnMessageDto();
		res.getErrors().add("21/05/2013 : L'agent est en récupération sur cette période.");

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkRecuperation(idAgent, dateDebut, dateFin)).thenReturn(res);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkRecuperation(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkReposComp_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkReposComp(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(new ReturnMessageDto());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkReposComp(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkReposComp_ReturnErrors() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2013, 5, 20, 2, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		Pointage p = new Pointage();
		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		p.setDateDebut(dateDebut);
		p.setDateFin(dateFin);
		p.setType(type);
		pointages.add(p);

		ReturnMessageDto res = new ReturnMessageDto();
		res.getErrors().add("21/05/2013 : L'agent est en repos compensateur sur cette période.");

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkReposComp(idAgent, dateDebut, dateFin)).thenReturn(res);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkReposComp(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("21/05/2013 : L'agent est en repos compensateur sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkReposComp_withPrime() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2013, 5, 20, 2, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		Pointage p = new Pointage();
		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		p.setDateDebut(dateDebut);
		p.setDateFin(dateFin);
		p.setType(type);
		pointages.add(p);

		ReturnMessageDto res = new ReturnMessageDto();
		res.getErrors().add("21/05/2013 : L'agent est en repos compensateur sur cette période.");

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkReposComp(idAgent, dateDebut, dateFin)).thenReturn(res);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkReposComp(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAbsencesSyndicales_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(
				absWsConsumer.checkAbsencesSyndicales(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(new ReturnMessageDto());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkAbsencesSyndicales(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAbsencesSyndicales_ReturnErrors() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2013, 5, 20, 2, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		Pointage p = new Pointage();
		p.setDateDebut(dateDebut);
		p.setDateFin(dateFin);
		p.setType(type);
		pointages.add(p);

		ReturnMessageDto res = new ReturnMessageDto();
		res.getErrors().add("21/05/2013 : L'agent est en absence syndicale sur cette période.");

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkAbsencesSyndicales(idAgent, dateDebut, dateFin)).thenReturn(res);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkAbsencesSyndicales(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("21/05/2013 : L'agent est en absence syndicale sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkAbsencesSyndicales_withPrime() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2013, 5, 20, 2, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		Pointage p = new Pointage();
		p.setDateDebut(dateDebut);
		p.setDateFin(dateFin);
		p.setType(type);
		pointages.add(p);

		ReturnMessageDto res = new ReturnMessageDto();
		res.getErrors().add("21/05/2013 : L'agent est en absence syndicale sur cette période.");

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkAbsencesSyndicales(idAgent, dateDebut, dateFin)).thenReturn(res);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkAbsencesSyndicales(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkCongesExceptionnels_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(
				absWsConsumer
						.checkCongesExceptionnels(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(new ReturnMessageDto());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkCongesExceptionnels(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkCongesExceptionnels_ReturnErrors() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2013, 5, 20, 2, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		Pointage p = new Pointage();
		p.setDateDebut(dateDebut);
		p.setDateFin(dateFin);
		p.setType(type);
		pointages.add(p);

		ReturnMessageDto res = new ReturnMessageDto();
		res.getErrors().add("21/05/2013 : L'agent est en congé exceptionnel sur cette période.");

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkCongesExceptionnels(idAgent, dateDebut, dateFin)).thenReturn(res);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkCongesExceptionnels(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("21/05/2013 : L'agent est en congé exceptionnel sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkCongesExceptionnels_withPrime() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2013, 5, 20, 2, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		Pointage p = new Pointage();
		p.setDateDebut(dateDebut);
		p.setDateFin(dateFin);
		p.setType(type);
		pointages.add(p);

		ReturnMessageDto res = new ReturnMessageDto();
		res.getErrors().add("21/05/2013 : L'agent est en congé exceptionnel sur cette période.");

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkCongesExceptionnels(idAgent, dateDebut, dateFin)).thenReturn(res);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkCongesExceptionnels(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkCongeAnnuel_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkCongeAnnuel(idAgent, dateLundi, new DateTime(dateLundi).plusDays(7).toDate()))
				.thenReturn(new ReturnMessageDto());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkCongeAnnuel(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkCongeAnnuel_ReturnErrors() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2013, 5, 20, 2, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		Pointage p = new Pointage();
		p.setDateDebut(dateDebut);
		p.setDateFin(dateFin);
		p.setType(type);
		pointages.add(p);

		ReturnMessageDto res = new ReturnMessageDto();
		res.getErrors().add("21/05/2013 : L'agent est en congé annuel sur cette période.");

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkCongeAnnuel(idAgent, dateDebut, dateFin)).thenReturn(res);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkCongeAnnuel(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("21/05/2013 : L'agent est en congé annuel sur cette période.", result.getErrors().get(0));
	}

	@Test
	public void checkCongeAnnuel_withPrime() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2013, 5, 20, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2013, 5, 20, 2, 0, 0).toDate();
		List<Pointage> pointages = new ArrayList<Pointage>();
		RefTypePointage type = new RefTypePointage();
		type.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		Pointage p = new Pointage();
		p.setDateDebut(dateDebut);
		p.setDateFin(dateFin);
		p.setType(type);
		pointages.add(p);

		ReturnMessageDto res = new ReturnMessageDto();
		res.getErrors().add("21/05/2013 : L'agent est en congé annuel sur cette période.");

		IAbsWsConsumer absWsConsumer = Mockito.mock(IAbsWsConsumer.class);
		Mockito.when(absWsConsumer.checkCongeAnnuel(idAgent, dateDebut, dateFin)).thenReturn(res);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "absWsConsumer", absWsConsumer);

		// When
		ReturnMessageDto result = service.checkCongeAnnuel(new ReturnMessageDto(), idAgent, pointages);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
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
	public void checkSpabsenMaladie_1Spabsen_NoPointageHorsPrimeThatDay_NoError() {

		// Given
		Integer idAgent = 9005138;
		Date dateLundi = new DateTime(2013, 5, 20, 0, 0, 0).toDate();

		Pointage p1 = new Pointage();
		p1.setDateLundi(dateLundi);
		p1.setDateDebut(new DateTime(2013, 5, 21, 7, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 5, 21, 9, 0, 0).toDate());
		p1.setType(new RefTypePointage());
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
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
		BaseHorairePointageDto base = new BaseHorairePointageDto();

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		ReturnMessageDto result = service.checkMaxAbsenceHebdo(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1, p2), carr, base);

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
		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setBaseCalculee(32.0);
		Spcarr car = new Spcarr();
		car.setSpbhor(hor);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		ReturnMessageDto result = service.checkMaxAbsenceHebdo(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1, p2), car, bas);

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
		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setBaseCalculee(32.0);
		Spcarr car = new Spcarr();
		car.setSpbhor(hor);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		ReturnMessageDto result = service.checkMaxAbsenceHebdo(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1, p2), car, bas);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentINAAndHSup_INASupTo315_ButZ_returnError() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(316);
		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("00Z");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setCdcate(1);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EntiteDto dtoService = new EntiteDto();
		dtoService.setSigle("TITI");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(idAgent, dateLundi)).thenReturn(dtoService);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car, bas);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals("L'agent est en base horaire \"00Z\" sur la période", result.getErrors().get(0));
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentINAAndHSup_INASupTo315_DPM_returnNothing_ForceRecup() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(316);
		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("A");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setCdcate(1);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EntiteDto dtoService = new EntiteDto();
		dtoService.setSigle("DPM");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(idAgent, dateLundi)).thenReturn(dtoService);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car, bas);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals(p1.getHeureSupRecuperee(), true);
	}

	@Test
	public void checkAgentINAAndHSup_INASupTo315_returnNothing_ForceRecup() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(316);
		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("A");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setCdcate(4);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		// bug #20374
		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.setDateDebut(new DateTime(2013, 05, 17, 18, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 17, 19, 15, 0).toDate()); // 9h
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EntiteDto dtoService = new EntiteDto();
		dtoService.setSigle("TITI");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(idAgent, dateLundi)).thenReturn(dtoService);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1, p2), car, bas);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals(p1.getHeureSupRecuperee(), true);
		assertEquals(p2.getHeureSupRecuperee(), true);
	}

	@Test
	public void checkAgentINAAndHSup_INALessThan315_returnOK() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(315);
		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("00p");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setCdcate(1);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EntiteDto dtoService = new EntiteDto();
		dtoService.setSigle("TITI");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(idAgent, dateLundi)).thenReturn(dtoService);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car, bas);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentINAAndHSup_INALessThan315_ButZ_returnError() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(315);
		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("00Z");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setCdcate(1);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EntiteDto dtoService = new EntiteDto();
		dtoService.setSigle("TITI");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(idAgent, dateLundi)).thenReturn(dtoService);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car, bas);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent est en base horaire \"00Z\" sur la période", result.getErrors().get(0));
	}

	@Test
	public void checkAgentINAAndHSup_INALessThan315_ButDPM_ReturnNothing() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		Spbarem barem = new Spbarem();
		barem.setIna(315);
		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("00I");
		Spcarr car = new Spcarr();
		car.setSpbarem(barem);
		car.setCdcate(1);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		EntiteDto dtoService = new EntiteDto();
		dtoService.setSigle("DPM");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(idAgent, dateLundi)).thenReturn(dtoService);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		ReturnMessageDto result = service.checkAgentINAAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car, bas);

		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals(0, result.getErrors().size());
		assertEquals(p1.getHeureSupRecuperee(), true);
	}

	@Test
	public void checkAgentInactivity_AgentIsActive_ReturnNothing() {

		// Given
		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9007865);
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		Date dateDebut = new DateTime(2013, 05, 15, 0, 0, 0).toDate();

		Spadmn sp = new Spadmn();
		sp.setCdpadm("01");

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgentCurrentPosition(ag, dateLundi)).thenReturn(null);
		Mockito.when(mRepo.getAgentCurrentPosition(ag, dateDebut)).thenReturn(sp);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);

		List<Pointage> listPointages = new ArrayList<Pointage>();
		Pointage ptg = new Pointage();
		ptg.setDateDebut(dateDebut);
		listPointages.add(ptg);
		
		// When
		ReturnMessageDto result = service.checkAgentInactivity(new ReturnMessageDto(), 9007865, dateLundi, listPointages, ag);

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
		Date dateDebut = new DateTime(2013, 05, 15, 0, 0, 0).toDate();

		Spadmn sp = new Spadmn();
		sp.setCdpadm("99");

		IMairieRepository mRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mRepo.getAgentCurrentPosition(ag, dateLundi)).thenReturn(null);
		Mockito.when(mRepo.getAgentCurrentPosition(ag, dateDebut)).thenReturn(sp);

		List<Pointage> listPointages = new ArrayList<Pointage>();
		Pointage ptg = new Pointage();
		ptg.setDateDebut(dateDebut);
		listPointages.add(ptg);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "mairieRepository", mRepo);

		// When
		ReturnMessageDto result = service.checkAgentInactivity(new ReturnMessageDto(), 9007865, dateLundi, listPointages, ag);

		// Then
		assertEquals(0, result.getInfos().size());
		assertEquals(1, result.getErrors().size());
		assertEquals("L'agent n'est pas en activité le 15/05/2013.", result.getErrors().get(0));
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

		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("A");
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(1.0);
		Spcarr car = new Spcarr();
		car.setSpbhor(spbhor);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 16, 15, 0).toDate()); // 9h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		// When
		ReturnMessageDto result = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car, bas, false);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	@Test
	public void checkAgentTempsPartielAndHSup_TempsPartiel_OK() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("A");
		bas.setBaseCalculee(20.0);
		bas.setBaseLegale(39.0);
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(0.5);
		Spcarr car = new Spcarr();
		car.setSpbhor(spbhor);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 11, 15, 0).toDate()); // 4h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getBaseCalculee())).thenReturn(1200);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getBaseLegale())).thenReturn(2340);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car, bas, false);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	// #18728 : evol sur le calcul
	// ici la personne est a temps partiel à 20h par semaine
	// il a donc le droit à 20h x 20% = 4 HSup
	@Test
	public void checkAgentTempsPartielAndHSup_KO() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("A");
		bas.setBaseCalculee(20.0);
		bas.setBaseLegale(39.0);
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(0.5);
		Spcarr car = new Spcarr();
		car.setSpbhor(spbhor);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 18, 16, 15, 0).toDate()); // 33h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getBaseCalculee())).thenReturn(1200);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getBaseLegale())).thenReturn(2340);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				int nombreMinutesHSupAutorisees = (int) invocation.getArguments()[0];
				assertEquals(nombreMinutesHSupAutorisees, new Integer(4*60).intValue());
				return new Integer(nombreMinutesHSupAutorisees/60).toString() + "h";
			}
		}).when(hS).formatMinutesToString(Mockito.anyInt());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car, bas, false);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent est en temps partiel, il ne peut pas avoir plus de 4h d'heures supplémentaires.", result
				.getErrors().get(0));
	}

	@Test
	public void checkAgentTempsPartielAndHSup_TempsPartiel_WithAbsence_OK() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("A");
		bas.setBaseCalculee(20.0);
		bas.setBaseLegale(39.0);
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(0.5);
		Spcarr car = new Spcarr();
		car.setSpbhor(spbhor);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 17, 12, 15, 0).toDate()); // 5h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		Pointage p2 = new Pointage();
		p2.setType(new RefTypePointage());
		p2.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 17, 8, 15, 0).toDate()); // 1h
		p2.getType().setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getBaseCalculee())).thenReturn(1200);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getBaseLegale())).thenReturn(2340);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1, p2), car, bas, false);

		// Then
		assertEquals(0, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
	}

	// #18728 : evol sur le calcul
	// ici la personne est a temps partiel à 20h30 par semaine
	// il a donc le droit à 20h30 x 20% = 4 HSUP
	// + 1 heure d absence 
	// soit 5 HSup
	@Test
	public void checkAgentTempsPartielAndHSup_WithAbsence_KO() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("A");
		bas.setBaseCalculee(20.5); 
		bas.setBaseLegale(39.0);
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(0.5);
		Spcarr car = new Spcarr();
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
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getBaseCalculee())).thenReturn(1200);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getBaseLegale())).thenReturn(2340);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				int nombreMinutesHSupAutorisees = (int) invocation.getArguments()[0];
				assertEquals(nombreMinutesHSupAutorisees, new Integer(5*60).intValue());
				return new Integer(nombreMinutesHSupAutorisees/60).toString() + "h";
			}
		}).when(hS).formatMinutesToString(Mockito.anyInt());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1, p2), car, bas, false);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent est en temps partiel, il ne peut pas avoir plus de 5h d'heures supplémentaires.", result
				.getErrors().get(0));
	}

	// #18728 : evol sur le calcul
	// cas de Luc (repris du redmine)
	// ici la personne est a temps partiel à 35h par semaine
	// il a donc le droit à 35h x 20% = 7 HSUP
	// mais comme 35h + 7HSup > 39h legale
	// il ne peut faire faire que 4HSup
	@Test
	public void checkAgentTempsPartielAndHSup_KO_casTempsPartiel35h() {

		// Given
		Integer idAgent = 9008765;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		BaseHorairePointageDto bas = new BaseHorairePointageDto();
		bas.setCodeBaseHorairePointage("A");
		bas.setBaseCalculee(35.0); 
		bas.setBaseLegale(39.0);
		Spbhor spbhor = new Spbhor();
		spbhor.setTaux(0.5);
		Spcarr car = new Spcarr();
		car.setSpbhor(spbhor);

		Pointage p1 = new Pointage();
		p1.setType(new RefTypePointage());
		p1.setDateDebut(new DateTime(2013, 05, 17, 7, 15, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 18, 16, 15, 0).toDate()); // 33h
		p1.getType().setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getBaseCalculee())).thenReturn(1200);
		Mockito.when(hS.convertMairieNbHeuresFormatToMinutes(bas.getBaseLegale())).thenReturn(2340);

		Mockito.doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				int nombreMinutesHSupAutorisees = (int) invocation.getArguments()[0];
				assertEquals(nombreMinutesHSupAutorisees, new Integer(4*60).intValue());
				return new Integer(nombreMinutesHSupAutorisees/60).toString();
			}
		}).when(hS).formatMinutesToString(Mockito.anyInt());

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car, bas, false);

		// Then
		assertEquals(1, result.getErrors().size());
		assertEquals(0, result.getInfos().size());
		assertEquals("L'agent est en temps partiel, il ne peut pas avoir plus de 4 d'heures supplémentaires.", result
				.getErrors().get(0));
		

		// When
		ReturnMessageDto resultFromSIRH = service.checkAgentTempsPartielAndHSup(new ReturnMessageDto(), idAgent, dateLundi,
				Arrays.asList(p1), car, bas, true);

		// Then
		assertEquals(0, resultFromSIRH.getErrors().size());
		assertEquals(1, resultFromSIRH.getInfos().size());
		assertEquals("L'agent est en temps partiel, il ne peut pas avoir plus de 4 d'heures supplémentaires.", resultFromSIRH
				.getInfos().get(0));
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

	@Test
	public void checkDateLundiNotSuperieurDateJour_OK() {

		ReturnMessageDto srm = new ReturnMessageDto();

		Date dateLundi = new Date();

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		srm = service.checkDateLundiNotSuperieurDateJour(srm, dateLundi);

		assertEquals(0, srm.getErrors().size());
	}

	@Test
	public void checkDateLundiNotSuperieurDateJour_KO() {

		ReturnMessageDto srm = new ReturnMessageDto();

		GregorianCalendar calStr1 = new GregorianCalendar();
		calStr1.setTime(new Date());
		calStr1.add(GregorianCalendar.MONTH, 1);

		PointageDataConsistencyRules service = new PointageDataConsistencyRules();

		srm = service.checkDateLundiNotSuperieurDateJour(srm, calStr1.getTime());

		assertEquals(1, srm.getErrors().size());
	}
}
