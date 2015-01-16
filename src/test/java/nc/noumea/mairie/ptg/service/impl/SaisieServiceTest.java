package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.MotifHeureSup;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PtgComment;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.ptg.domain.RefTypeAbsenceEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.dto.AbsenceDto;
import nc.noumea.mairie.ptg.dto.AbsenceDtoKiosque;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.FichePointageDtoKiosque;
import nc.noumea.mairie.ptg.dto.HeureSupDto;
import nc.noumea.mairie.ptg.dto.HeureSupDtoKiosque;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.JourPointageDtoKiosque;
import nc.noumea.mairie.ptg.dto.PointageDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.PrimeDtoKiosque;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.dto.SirhWsServiceDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;
import nc.noumea.mairie.ptg.service.IPointageDataConsistencyRules;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.service.NotAMondayException;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class SaisieServiceTest {

	@Test
	public void saveFichePointageKiosque_dateIsNotAMonday_throwException() {
		// Given
		Date notAMonday = new DateTime(2013, 05, 14, 0, 0, 0).toDate();
		FichePointageDtoKiosque fichePointageDto = new FichePointageDtoKiosque();
		fichePointageDto.setDateLundi(notAMonday);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(notAMonday)).thenReturn(false);

		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		try {
			service.saveFichePointage(9001234, fichePointageDto);
		} catch (NotAMondayException ex) {
			return;
		}

		fail("Should have thrown a NotAMondayException");
	}

	@Test
	public void saveFichePointageKiosque_noExistingPointage_saveNewAbsence() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		dto.setSaisies(Arrays.asList(new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque()));

		AbsenceDtoKiosque abs3 = new AbsenceDtoKiosque();
		abs3.setHeureDebutDate(new DateTime(2013, 05, 16, 15, 0, 0).toDate());
		abs3.setHeureFinDate(new DateTime(2013, 05, 16, 16, 0, 0).toDate());
		abs3.setMotif("le motif 3");
		abs3.setCommentaire("le commentaire 3");
		abs3.setIdRefTypeAbsence(1);
		dto.getSaisies().get(3).getAbsences().add(abs3);

		Date currentDate = new DateTime(2013, 05, 22, 9, 8, 00).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);
		Mockito.when(hS.getMairieMatrFromIdAgent(9007654)).thenReturn(7654);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F)).thenReturn(chainePaie);

		RefTypePointage absRef = new RefTypePointage();
		absRef.setIdRefTypePointage(1);
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(
				new ArrayList<Pointage>());
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 1)).thenReturn(absRef);

		Pointage newAbsPointage = new Pointage();
		newAbsPointage.setIdAgent(agent.getIdAgent());
		newAbsPointage.setDateLundi(lundi);
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(9001234, null, agent.getIdAgent(), lundi, currentDate))
				.thenReturn(newAbsPointage);

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(new ReturnMessageDto());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(null);

		Spcarr carr = new Spcarr();
		carr.setCdcate(6);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(7654, lundi)).thenReturn(carr);

		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		service.saveFichePointage(9001234, dto);

		// Then
		ArgumentCaptor<Pointage> argument = ArgumentCaptor.forClass(Pointage.class);
		Mockito.verify(pRepo).savePointage(argument.capture());

		assertEquals(RefTypePointageEnum.ABSENCE, argument.getValue().getTypePointageEnum());
		assertEquals(agent.getIdAgent(), argument.getValue().getIdAgent());
		assertEquals(new DateTime(2013, 05, 16, 15, 0, 0).toDate(), argument.getValue().getDateDebut());
		assertEquals(new DateTime(2013, 05, 16, 16, 0, 0).toDate(), argument.getValue().getDateFin());
		assertEquals("le commentaire 3", argument.getValue().getCommentaire().getText());
		assertEquals("le motif 3", argument.getValue().getMotif().getText());
		assertEquals(lundi, argument.getValue().getDateLundi());
		assertNull(argument.getValue().getIdPointage());
		assertNull(argument.getValue().getQuantite());
		assertNull(argument.getValue().getRefPrime());
		assertNull(argument.getValue().getHeureSupRecuperee());
		assertNull(argument.getValue().getPointageParent());
	}

	@Test
	public void saveFichePointageKiosque_noExistingPointage_saveNewHsup() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		dto.setSaisies(Arrays.asList(new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque()));

		MotifHeureSup motifHSup = new MotifHeureSup();
		motifHSup.setIdMotifHsup(2);
		motifHSup.setText("le motif 3");

		HeureSupDtoKiosque hs1 = new HeureSupDtoKiosque();
		hs1.setRecuperee(false);
		hs1.setRappelService(false);
		hs1.setHeureDebutDate(new DateTime(2013, 05, 16, 15, 0, 0).toDate());
		hs1.setHeureFinDate(new DateTime(2013, 05, 16, 16, 0, 0).toDate());
		hs1.setIdMotifHsup(2);
		hs1.setCommentaire("le commentaire 3");
		dto.getSaisies().get(3).getHeuresSup().add(hs1);

		Date currentDate = new DateTime(2013, 05, 22, 9, 8, 00).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);
		Mockito.when(hS.getMairieMatrFromIdAgent(9007654)).thenReturn(7654);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F)).thenReturn(chainePaie);

		RefTypePointage absRef = new RefTypePointage();
		absRef.setIdRefTypePointage(2);
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(
				new ArrayList<Pointage>());
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 2)).thenReturn(absRef);
		Mockito.when(pRepo.getEntity(MotifHeureSup.class, 2)).thenReturn(motifHSup);

		Pointage newHsPointage = new Pointage();
		newHsPointage.setIdAgent(agent.getIdAgent());
		newHsPointage.setDateLundi(lundi);
		newHsPointage.setDateDebut(new Date());
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(9001234, null, agent.getIdAgent(), lundi, currentDate))
				.thenReturn(newHsPointage);

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(new ReturnMessageDto());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(null);

		Spcarr carr = new Spcarr();
		carr.setCdcate(6);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(7654, lundi)).thenReturn(carr);

		SirhWsServiceDto dtoService = new SirhWsServiceDto();
		dtoService.setSigle("DPM");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(agent.getIdAgent(), newHsPointage.getDateDebut())).thenReturn(
				dtoService);

		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		service.saveFichePointage(9001234, dto);

		// Then
		ArgumentCaptor<Pointage> argument = ArgumentCaptor.forClass(Pointage.class);
		Mockito.verify(pRepo).savePointage(argument.capture());

		assertEquals(RefTypePointageEnum.H_SUP, argument.getValue().getTypePointageEnum());
		assertTrue(argument.getValue().getHeureSupRecuperee());
		assertFalse(argument.getValue().getHeureSupRappelService());
		assertEquals(agent.getIdAgent(), argument.getValue().getIdAgent());
		assertEquals(new DateTime(2013, 05, 16, 15, 0, 0).toDate(), argument.getValue().getDateDebut());
		assertEquals(new DateTime(2013, 05, 16, 16, 0, 0).toDate(), argument.getValue().getDateFin());
		assertEquals("le commentaire 3", argument.getValue().getCommentaire().getText());
		assertEquals("le motif 3", argument.getValue().getMotifHsup().getText());
		assertEquals(lundi, argument.getValue().getDateLundi());
		assertNull(argument.getValue().getIdPointage());
		assertNull(argument.getValue().getQuantite());
		assertNull(argument.getValue().getRefPrime());
		assertNull(argument.getValue().getRefTypeAbsence());
		assertNull(argument.getValue().getPointageParent());
	}

	@Test
	public void saveFichePointageKiosque_noExistingPointage_saveNewPrime() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);

		// prime
		PrimeDtoKiosque p = new PrimeDtoKiosque();
		p.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES.toString());
		p.setIdRefPrime(22);
		p.setNumRubrique(1111);
		p.setTitre("Le titre");
		JourPointageDtoKiosque j1 = new JourPointageDtoKiosque();
		j1.getPrimes().add(p);
		dto.setSaisies(Arrays.asList(j1, new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1),
				new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1),
				new JourPointageDtoKiosque(j1)));
		PrimeDtoKiosque prime7thday = dto.getSaisies().get(6).getPrimes().get(0);
		prime7thday.setHeureDebutDate(new DateTime(2013, 05, 19, 8, 0, 0).toDate());
		prime7thday.setHeureFinDate(new DateTime(2013, 05, 19, 11, 0, 0).toDate());
		prime7thday.setQuantite(3);
		prime7thday.setMotif("mot");
		prime7thday.setCommentaire("com");

		Date currentDate = new DateTime(2013, 05, 22, 9, 8, 00).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(
				new ArrayList<Pointage>());

		RefTypePointage primTypeRef = new RefTypePointage();
		primTypeRef.setIdRefTypePointage(3);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 3)).thenReturn(primTypeRef);

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(22);
		refPrime.setNoRubr(1111);

		Pointage newPrimePointage = new Pointage();
		newPrimePointage.setIdAgent(agent.getIdAgent());
		newPrimePointage.setDateLundi(lundi);
		newPrimePointage.setRefPrime(refPrime);
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(9001234, null, agent.getIdAgent(), lundi, currentDate, 22))
				.thenReturn(newPrimePointage);

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(new ReturnMessageDto());

		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);

		// When
		service.saveFichePointage(9001234, dto);

		// Then
		ArgumentCaptor<Pointage> argument = ArgumentCaptor.forClass(Pointage.class);
		Mockito.verify(pRepo).savePointage(argument.capture());

		assertEquals(RefTypePointageEnum.PRIME, argument.getValue().getTypePointageEnum());
		assertEquals(22, (int) argument.getValue().getRefPrime().getIdRefPrime());
		assertEquals(1111, (int) argument.getValue().getRefPrime().getNoRubr());
		assertEquals(agent.getIdAgent(), argument.getValue().getIdAgent());
		assertEquals(3, (int) argument.getValue().getQuantite());
		assertEquals(new DateTime(2013, 05, 19, 8, 0, 0).toDate(), argument.getValue().getDateDebut());
		assertEquals(new DateTime(2013, 05, 19, 11, 0, 0).toDate(), argument.getValue().getDateFin());
		assertEquals("com", argument.getValue().getCommentaire().getText());
		assertEquals("mot", argument.getValue().getMotif().getText());
		assertEquals(lundi, argument.getValue().getDateLundi());
		assertNull(argument.getValue().getIdPointage());
		assertNull(argument.getValue().getHeureSupRecuperee());
		assertNull(argument.getValue().getRefTypeAbsence());
		assertNull(argument.getValue().getPointageParent());
	}

	@Test
	public void saveFichePointageKiosque_noExistingPointage_saveNewPrimeQuantite() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);

		// prime
		PrimeDtoKiosque p = new PrimeDtoKiosque();
		p.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES.toString());
		p.setIdRefPrime(22);
		p.setNumRubrique(1111);
		p.setTitre("Le titre");
		JourPointageDtoKiosque j1 = new JourPointageDtoKiosque();
		j1.getPrimes().add(p);
		dto.setSaisies(Arrays.asList(j1, new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1),
				new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1),
				new JourPointageDtoKiosque(j1)));
		dto.getSaisies().get(6).setDate(new DateTime(2013, 05, 19, 0, 0, 0).toDate());
		PrimeDtoKiosque prime7thday = dto.getSaisies().get(6).getPrimes().get(0);
		prime7thday.setHeureDebutDate(null);
		prime7thday.setHeureFinDate(null);
		prime7thday.setQuantite(3);
		prime7thday.setMotif("mot");
		prime7thday.setCommentaire("com");

		Date currentDate = new DateTime(2013, 05, 22, 9, 8, 00).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(
				new ArrayList<Pointage>());

		RefTypePointage primTypeRef = new RefTypePointage();
		primTypeRef.setIdRefTypePointage(3);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 3)).thenReturn(primTypeRef);

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(22);
		refPrime.setNoRubr(1111);

		Pointage newPrimePointage = new Pointage();
		newPrimePointage.setIdAgent(agent.getIdAgent());
		newPrimePointage.setDateLundi(lundi);
		newPrimePointage.setRefPrime(refPrime);
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(9001234, null, agent.getIdAgent(), lundi, currentDate, 22))
				.thenReturn(newPrimePointage);

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(new ReturnMessageDto());

		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);

		// When
		service.saveFichePointage(9001234, dto);

		// Then
		ArgumentCaptor<Pointage> argument = ArgumentCaptor.forClass(Pointage.class);
		Mockito.verify(pRepo).savePointage(argument.capture());

		assertEquals(RefTypePointageEnum.PRIME, argument.getValue().getTypePointageEnum());
		assertEquals(22, (int) argument.getValue().getRefPrime().getIdRefPrime());
		assertEquals(1111, (int) argument.getValue().getRefPrime().getNoRubr());
		assertEquals(agent.getIdAgent(), argument.getValue().getIdAgent());
		assertEquals(3, (int) argument.getValue().getQuantite());
		assertEquals(new DateTime(2013, 05, 19, 0, 0, 0).toDate(), argument.getValue().getDateDebut());
		assertNull(argument.getValue().getDateFin());
		assertEquals("com", argument.getValue().getCommentaire().getText());
		assertEquals("mot", argument.getValue().getMotif().getText());
		assertEquals(lundi, argument.getValue().getDateLundi());
		assertNull(argument.getValue().getIdPointage());
		assertNull(argument.getValue().getHeureSupRecuperee());
		assertNull(argument.getValue().getRefTypeAbsence());
		assertNull(argument.getValue().getPointageParent());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void saveFichePointageKiosque_saveNewAbsence_ErrorInConsistency_saveNothingAndReturnMessage() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);

		// prime
		AbsenceDtoKiosque abs = new AbsenceDtoKiosque();
		abs.setIdRefTypeAbsence(1);
		abs.setHeureDebutDate(new DateTime(2013, 05, 13, 12, 0, 0).toDate());
		abs.setHeureFinDate(new DateTime(2013, 05, 13, 18, 0, 0).toDate());
		dto.getSaisies().add(new JourPointageDtoKiosque());
		dto.getSaisies().get(0).getAbsences().add(abs);

		Date currentDate = new DateTime(2013, 05, 22, 9, 8, 00).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(
				new ArrayList<Pointage>());

		Pointage newAbsPointage = new Pointage();
		newAbsPointage.setIdAgent(agent.getIdAgent());
		newAbsPointage.setDateLundi(lundi);

		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(9001234, null, agent.getIdAgent(), lundi, currentDate))
				.thenReturn(newAbsPointage);

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.doAnswer(new Answer() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				ReturnMessageDto result = (ReturnMessageDto) args[0];
				result.getErrors().add("message d'erreur");
				return null;
			}
		})
				.when(dcMock)
				.processDataConsistency(Mockito.any(ReturnMessageDto.class), Mockito.eq(agent.getIdAgent()),
						Mockito.eq(lundi), Mockito.anyList());

		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(new ReturnMessageDto());

		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);

		// When
		ReturnMessageDto res = service.saveFichePointage(9001234, dto);

		// Then
		Mockito.verify(pRepo, Mockito.never()).savePointage(newAbsPointage);

		assertEquals(1, res.getErrors().size());
		assertEquals(0, res.getInfos().size());
		assertEquals("message d'erreur", res.getErrors().get(0));
	}

	@Test
	public void saveFichePointageKiosque_deletedPointageFromDto_delete1PointageKeep1BecauseOldVersion() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		dto.setSaisies(Arrays.asList(new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque()));

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);

		Pointage p = Mockito.spy(new Pointage());
		EtatPointage e1 = new EtatPointage();
		e1.setEtat(EtatPointageEnum.SAISI);
		p.getEtats().add(e1);
		Pointage p2 = Mockito.spy(new Pointage());
		EtatPointage e2 = new EtatPointage();
		e2.setEtat(EtatPointageEnum.REFUSE);
		p2.getEtats().add(e2);

		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getLatestPointagesForSaisieForAgentAndDateMonday(agent.getIdAgent(), lundi)).thenReturn(
				Arrays.asList(p, p2));

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(new ReturnMessageDto());

		SaisieService service = Mockito.spy(new SaisieService());
		Mockito.doNothing().when(service).deletePointages(9001234, Arrays.asList(p, p2));

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);

		// When
		service.saveFichePointage(9001234, dto);

		// Then
		Mockito.verify(service, Mockito.times(1)).deletePointages(9001234, Arrays.asList(p, p2));
	}

	@Test
	public void saveFichePointageKiosque_delete2PointagesFromDtoUsingASupprimer_delete1PointageKeep1BecauseOldVersio() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);

		// prime
		PrimeDtoKiosque templateP = new PrimeDtoKiosque();
		templateP.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES.toString());
		templateP.setIdRefPrime(22);
		templateP.setNumRubrique(1111);
		templateP.setTitre("Le titre");
		JourPointageDtoKiosque j1 = new JourPointageDtoKiosque();
		j1.getPrimes().add(templateP);
		dto.setSaisies(Arrays.asList(j1, new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1),
				new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1),
				new JourPointageDtoKiosque(j1)));
		PrimeDtoKiosque prime7thday = dto.getSaisies().get(6).getPrimes().get(0);
		prime7thday.setIdPointage(2000);
		prime7thday.setaSupprimer(true);
		prime7thday.setHeureDebutDate(new DateTime(2013, 05, 19, 8, 0, 0).toDate());
		prime7thday.setHeureFinDate(new DateTime(2013, 05, 19, 11, 0, 0).toDate());
		prime7thday.setQuantite(3);
		prime7thday.setMotif("mot");
		prime7thday.setCommentaire("com");
		prime7thday.setIdPointage(2000);
		PrimeDtoKiosque prime6thday = dto.getSaisies().get(5).getPrimes().get(0);
		prime6thday.setIdPointage(1999);
		prime6thday.setaSupprimer(true);
		prime6thday.setHeureDebutDate(new DateTime(2013, 05, 19, 8, 0, 0).toDate());
		prime6thday.setHeureFinDate(new DateTime(2013, 05, 19, 11, 0, 0).toDate());
		prime6thday.setQuantite(3);
		prime6thday.setMotif("mot");
		prime6thday.setCommentaire("com");

		Date currentDate = new DateTime(2013, 05, 22, 9, 8, 00).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		RefTypePointage primTypeRef = new RefTypePointage();
		primTypeRef.setIdRefTypePointage(3);
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 3)).thenReturn(primTypeRef);

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(22);
		refPrime.setNoRubr(1111);

		Pointage existingPointageApprouve = new Pointage();
		existingPointageApprouve.setIdAgent(agent.getIdAgent());
		existingPointageApprouve.setDateLundi(lundi);
		existingPointageApprouve.setRefPrime(refPrime);
		existingPointageApprouve.setIdPointage(1999);
		existingPointageApprouve.getEtats().add(new EtatPointage());
		existingPointageApprouve.getLatestEtatPointage().setEtat(EtatPointageEnum.APPROUVE);
		Pointage existingPointageSaisi = Mockito.spy(new Pointage());
		existingPointageSaisi.setIdAgent(agent.getIdAgent());
		existingPointageSaisi.setDateLundi(lundi);
		existingPointageSaisi.setRefPrime(refPrime);
		existingPointageSaisi.setIdPointage(2000);
		existingPointageSaisi.getEtats().add(new EtatPointage());
		existingPointageSaisi.getLatestEtatPointage().setEtat(EtatPointageEnum.SAISI);

		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getLatestPointagesForSaisieForAgentAndDateMonday(agent.getIdAgent(), lundi)).thenReturn(
				Arrays.asList(existingPointageApprouve, existingPointageSaisi));

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(new ReturnMessageDto());

		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);

		// When
		service.saveFichePointage(9001234, dto);

		// Then
		Mockito.verify(pRepo, Mockito.never()).savePointage(Mockito.any(Pointage.class));
		Mockito.verify(pRepo, Mockito.times(1)).removeEntity(Mockito.isA(Pointage.class));
	}

	@Test
	public void saveFichePointageKiosque_fromKiosque_dateLundiPlus3Mois() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		ReturnMessageDto srm = new ReturnMessageDto();
		srm.getErrors().add("Erreur PTG plus 3 mois");

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(srm);

		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		ReturnMessageDto result = service.saveFichePointage(9001234, dto);

		// Then
		assertEquals(1, result.getErrors().size());
	}

	@Test
	public void saveFichePointage_fromSIRH_dateLundiPlus3Mois() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		FichePointageDto dto = new FichePointageDto();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);

		ReturnMessageDto srm = new ReturnMessageDto();
		srm.getErrors().add("Erreur PTG plus 3 mois");

		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;

		// prime
		PrimeDto templateP = new PrimeDto();
		templateP.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES.toString());
		templateP.setIdRefPrime(22);
		templateP.setNumRubrique(1111);
		templateP.setTitre("Le titre");
		JourPointageDto j1 = new JourPointageDto();
		j1.getPrimes().add(templateP);
		dto.setSaisies(Arrays.asList(j1, new JourPointageDto(j1), new JourPointageDto(j1), new JourPointageDto(j1),
				new JourPointageDto(j1), new JourPointageDto(j1), new JourPointageDto(j1)));
		PrimeDto prime7thday = dto.getSaisies().get(6).getPrimes().get(0);
		prime7thday.setIdPointage(2000);
		prime7thday.setaSupprimer(true);
		prime7thday.setHeureDebut(new DateTime(2013, 05, 19, 8, 0, 0).toDate());
		prime7thday.setHeureFin(new DateTime(2013, 05, 19, 11, 0, 0).toDate());
		prime7thday.setQuantite(3);
		prime7thday.setMotif("mot");
		prime7thday.setCommentaire("com");
		prime7thday.setIdPointage(2000);
		PrimeDto prime6thday = dto.getSaisies().get(5).getPrimes().get(0);
		prime6thday.setIdPointage(1999);
		prime6thday.setaSupprimer(true);
		prime6thday.setHeureDebut(new DateTime(2013, 05, 19, 8, 0, 0).toDate());
		prime6thday.setHeureFin(new DateTime(2013, 05, 19, 11, 0, 0).toDate());
		prime6thday.setQuantite(3);
		prime6thday.setMotif("mot");
		prime6thday.setCommentaire("com");

		Date currentDate = new DateTime(2013, 05, 22, 9, 8, 00).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F)).thenReturn(chainePaie);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009999)).thenReturn(9999);

		RefTypePointage primTypeRef = new RefTypePointage();
		primTypeRef.setIdRefTypePointage(3);
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 3)).thenReturn(primTypeRef);

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(22);
		refPrime.setNoRubr(1111);

		Pointage existingPointageApprouve = new Pointage();
		existingPointageApprouve.setIdAgent(agent.getIdAgent());
		existingPointageApprouve.setDateLundi(lundi);
		existingPointageApprouve.setRefPrime(refPrime);
		existingPointageApprouve.setIdPointage(1999);
		existingPointageApprouve.getEtats().add(new EtatPointage());
		existingPointageApprouve.getLatestEtatPointage().setEtat(EtatPointageEnum.APPROUVE);
		Pointage existingPointageSaisi = Mockito.spy(new Pointage());
		existingPointageSaisi.setIdAgent(agent.getIdAgent());
		existingPointageSaisi.setDateLundi(lundi);
		existingPointageSaisi.setRefPrime(refPrime);
		existingPointageSaisi.setIdPointage(2000);
		existingPointageSaisi.getEtats().add(new EtatPointage());
		existingPointageSaisi.getLatestEtatPointage().setEtat(EtatPointageEnum.SAISI);

		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getLatestPointagesForSaisieForAgentAndDateMonday(agent.getIdAgent(), lundi)).thenReturn(
				Arrays.asList(existingPointageApprouve, existingPointageSaisi));

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(srm);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(null);

		Spcarr carr = new Spcarr();
		carr.setCdcate(6);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(Mockito.anyInt(), Mockito.isA(Date.class))).thenReturn(carr);

		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		service.saveFichePointage(9001234, dto, true);

		// Then
		Mockito.verify(pRepo, Mockito.never()).savePointage(Mockito.any(Pointage.class));
		Mockito.verify(pRepo, Mockito.times(1)).removeEntity(Mockito.isA(Pointage.class));
	}

	@Test
	public void saveFichePointageKiosque_ExistingPointage_saveNewHsup_WithOthersOldHSup() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		dto.setSaisies(Arrays.asList(new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque()));

		HeureSupDtoKiosque hs1 = new HeureSupDtoKiosque();
		hs1.setRecuperee(true);
		hs1.setRappelService(true);
		hs1.setHeureDebutDate(new DateTime(2013, 05, 16, 15, 0, 0).toDate());
		hs1.setHeureFinDate(new DateTime(2013, 05, 16, 16, 0, 0).toDate());
		hs1.setMotif("le motif 3");
		hs1.setCommentaire("le commentaire 3");

		HeureSupDtoKiosque hs2 = new HeureSupDtoKiosque();
		hs2.setIdPointage(1);
		hs2.setRecuperee(false);
		hs2.setRappelService(false);
		hs2.setHeureDebutDate(new DateTime(2013, 05, 16, 15, 0, 0).toDate());
		hs2.setHeureFinDate(new DateTime(2013, 05, 16, 16, 0, 0).toDate());
		hs2.setMotif("le motif 3");
		hs2.setCommentaire("le commentaire 3");

		dto.getSaisies().get(3).getHeuresSup().add(hs1);
		dto.getSaisies().get(3).getHeuresSup().add(hs2);

		Date currentDate = new DateTime(2013, 05, 22, 9, 8, 00).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);
		Mockito.when(hS.getMairieMatrFromIdAgent(9007654)).thenReturn(7654);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F)).thenReturn(chainePaie);

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(22);
		refPrime.setNoRubr(1111);

		Pointage existingPointageApprouve = new Pointage();
		existingPointageApprouve.setIdAgent(agent.getIdAgent());
		existingPointageApprouve.setDateLundi(lundi);
		existingPointageApprouve.setDateDebut(new Date());
		existingPointageApprouve.setRefPrime(refPrime);
		existingPointageApprouve.setIdPointage(1999);
		existingPointageApprouve.getEtats().add(new EtatPointage());
		existingPointageApprouve.getLatestEtatPointage().setEtat(EtatPointageEnum.APPROUVE);

		RefTypePointage absRef = new RefTypePointage();
		absRef.setIdRefTypePointage(2);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 2)).thenReturn(absRef);

		Pointage newHsPointage = new Pointage();
		newHsPointage.setIdAgent(agent.getIdAgent());
		newHsPointage.setDateLundi(lundi);
		newHsPointage.setDateDebut(new Date());

		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(9001234, null, agent.getIdAgent(), lundi, currentDate))
				.thenReturn(newHsPointage);
		Mockito.when(
				pService.getOrCreateNewPointage(9001234, hs2.getIdPointage(), existingPointageApprouve.getIdAgent(),
						lundi, currentDate)).thenReturn(existingPointageApprouve);
		Mockito.when(pService.getLatestPointagesForSaisieForAgentAndDateMonday(agent.getIdAgent(), lundi)).thenReturn(
				Arrays.asList(existingPointageApprouve));

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(new ReturnMessageDto());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(null);

		Spcarr carr = new Spcarr();
		carr.setCdcate(6);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(7654, lundi)).thenReturn(carr);

		SirhWsServiceDto dtoService = new SirhWsServiceDto();
		dtoService.setSigle("TITI");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(agent.getIdAgent(), newHsPointage.getDateDebut())).thenReturn(
				dtoService);
		Mockito.when(sirhRepo.getAgentDirection(agent.getIdAgent(), existingPointageApprouve.getDateDebut()))
				.thenReturn(dtoService);

		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		service.saveFichePointage(9001234, dto);

		// Then
		Mockito.verify(pRepo, Mockito.times(2)).savePointage(Mockito.isA(Pointage.class));
		Mockito.verify(pRepo, Mockito.never()).removeEntity(Mockito.isA(Pointage.class));
	}

	@Test
	public void deletePointages_1PointageSaisi_RemoveIt() {

		// Given
		Pointage p1 = Mockito.spy(new Pointage());
		EtatPointage e1 = new EtatPointage();
		e1.setEtat(EtatPointageEnum.SAISI);
		p1.getEtats().add(e1);

		List<Pointage> ptgToDelete = Arrays.asList(p1);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);

		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		// When
		service.deletePointages(9001234, ptgToDelete);

		// Then
		Mockito.verify(pRepo, Mockito.times(1)).removeEntity(Mockito.isA(Pointage.class));
	}

	@Test
	public void crudComments_motif_commentaire_newString() {

		// Given
		Pointage ptg = new Pointage();
		String motif = "motif";
		String commentaire = "commentaire";

		SaisieService service = new SaisieService();

		// When
		service.crudComments(ptg, motif, commentaire);

		// Then
		assertEquals(motif, ptg.getMotif().getText());
		assertEquals(commentaire, ptg.getCommentaire().getText());
	}

	@Test
	public void crudComments_motif_commentaire_modifyString() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setMotif(new PtgComment());
		ptg.getMotif().setText("value");
		ptg.setCommentaire(new PtgComment());
		ptg.getMotif().setText("value");
		String motif = "motif";
		String commentaire = "commentaire";

		SaisieService service = new SaisieService();

		// When
		service.crudComments(ptg, motif, commentaire);

		// Then
		assertEquals(motif, ptg.getMotif().getText());
		assertEquals(commentaire, ptg.getCommentaire().getText());
	}

	@Test
	public void crudComments_motif_commentaire_deleteComment() {

		// Given
		PtgComment existingMotif = Mockito.spy(new PtgComment());
		existingMotif.setText("value");
		PtgComment existingComment = Mockito.spy(new PtgComment());

		existingComment.setText("value2");
		Pointage ptg = new Pointage();
		ptg.setMotif(existingMotif);
		ptg.setCommentaire(existingComment);

		String motif = "";
		String commentaire = "";

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);

		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		// When
		service.crudComments(ptg, motif, commentaire);

		// Then
		assertNull(ptg.getMotif());
		assertNull(ptg.getCommentaire());

		Mockito.verify(pRepo, Mockito.times(2)).removeEntity(Mockito.isA(PtgComment.class));
	}

	@Test
	public void hasPointageChanged_PrimeQuantiteHasNotChanged_ReturnFalse() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setQuantite(1);
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(ep);

		PrimeDto prime = new PrimeDto();
		prime.setQuantite(1);

		SaisieService service = new SaisieService();

		// Then
		assertFalse(service.hasPointageChanged(ptg, prime));
	}

	@Test
	public void hasPointageChanged_PrimeMotifHasChangedEtatIsSaisi_ReturnTrue() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setQuantite(1);
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(ep);

		PrimeDto prime = new PrimeDto();
		prime.setQuantite(1);
		prime.setMotif("coucou");

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, prime));
	}

	@Test
	public void hasPointageChanged_PrimeCommentaireHasChangedEtatIsSaisi_ReturnTrue() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setQuantite(1);
		ptg.setCommentaire(new PtgComment("aaa"));
		ptg.setMotif(new PtgComment("aaa"));
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(ep);

		PrimeDto prime = new PrimeDto();
		prime.setQuantite(1);
		prime.setCommentaire("coucou");
		prime.setMotif("aaa");

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, prime));
	}

	@Test
	public void hasPointageChanged_PrimeMotifHasChangedEtatisNotSaisi_ReturnFalse() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setQuantite(1);
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(ep);

		PrimeDto prime = new PrimeDto();
		prime.setQuantite(1);
		prime.setMotif("coucou");

		SaisieService service = new SaisieService();

		// Then
		assertFalse(service.hasPointageChanged(ptg, prime));
	}

	@Test
	public void hasPointageChanged_PrimeCommentaireHasChangedEtatisNotSaisi_ReturnFalse() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setQuantite(1);
		ptg.setCommentaire(new PtgComment("aaa"));
		ptg.setMotif(new PtgComment("aaa"));
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(ep);

		PrimeDto prime = new PrimeDto();
		prime.setQuantite(1);
		prime.setCommentaire("coucou");
		prime.setMotif("aaa");

		SaisieService service = new SaisieService();

		// Then
		assertFalse(service.hasPointageChanged(ptg, prime));
	}

	@Test
	public void hasPointageChanged_PrimeHDebutAndHFinHasNotChanged_ReturnFalse() {

		// Given
		Pointage ptg = new Pointage();
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(ep);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		PrimeDto prime = new PrimeDto();
		prime.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		prime.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		SaisieService service = new SaisieService();

		// Then
		assertFalse(service.hasPointageChanged(ptg, prime));
	}

	@Test
	public void hasPointageChanged_PrimeQuantiteHasChanged_ReturnTrue() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setQuantite(1);

		PrimeDto prime = new PrimeDto();
		prime.setQuantite(2);

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, prime));
	}

	@Test
	public void hasPointageChanged_PrimeHDebutAndHFinHasChanged_ReturnTrue() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		PrimeDto prime = new PrimeDto();
		prime.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		prime.setHeureFin(new DateTime(2013, 8, 1, 13, 10, 0).toDate());

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, prime));
	}

	@Test
	public void hasPointageChanged_AbsenceHasNotChanged_ReturnFalse() {

		// Given
		RefTypeAbsence refTypeAbsence = new RefTypeAbsence();
		refTypeAbsence.setIdRefTypeAbsence(1);

		Pointage ptg = new Pointage();
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(ep);
		ptg.setRefTypeAbsence(refTypeAbsence);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		AbsenceDto abs = new AbsenceDto();
		abs.setIdRefTypeAbsence(1);
		abs.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		abs.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		SaisieService service = new SaisieService();

		// Then
		assertFalse(service.hasPointageChanged(ptg, abs));
	}

	@Test
	public void hasPointageChanged_AbsenceConcerteeHasChanged_ReturnTrue() {

		RefTypeAbsence typeAbsence = new RefTypeAbsence();
		typeAbsence.setIdRefTypeAbsence(RefTypeAbsenceEnum.CONCERTEE.getValue());

		// Given
		Pointage ptg = new Pointage();
		ptg.setRefTypeAbsence(typeAbsence);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());
		ptg.setRefTypeAbsence(typeAbsence);

		AbsenceDto abs = new AbsenceDto();
		abs.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		abs.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());
		abs.setIdRefTypeAbsence(RefTypeAbsenceEnum.NON_CONCERTEE.getValue());

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, abs));
	}

	@Test
	public void hasPointageChanged_AbsenceHoursHasChanged_ReturnTrue() {

		RefTypeAbsence typeAbsence = new RefTypeAbsence();
		typeAbsence.setIdRefTypeAbsence(RefTypeAbsenceEnum.CONCERTEE.getValue());

		// Given
		Pointage ptg = new Pointage();
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());
		ptg.setRefTypeAbsence(typeAbsence);

		AbsenceDto abs = new AbsenceDto();
		abs.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		abs.setHeureFin(new DateTime(2013, 8, 1, 13, 10, 0).toDate());
		abs.setIdRefTypeAbsence(RefTypeAbsenceEnum.CONCERTEE.getValue());

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, abs));
	}

	@Test
	public void hasPointageChanged_AbsenceMotifHasChangedEtatIsSaisi_ReturnTrue() {

		// Given
		RefTypeAbsence refTypeAbsence = new RefTypeAbsence();
		refTypeAbsence.setIdRefTypeAbsence(1);

		Pointage ptg = new Pointage();
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(ep);
		ptg.setRefTypeAbsence(refTypeAbsence);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		AbsenceDto abs = new AbsenceDto();
		abs.setIdRefTypeAbsence(1);
		abs.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		abs.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());
		abs.setMotif("coucou");

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, abs));
	}

	@Test
	public void hasPointageChanged_AbsenceCommentaireHasChangedEtatIsSaisi_ReturnTrue() {

		// Given
		RefTypeAbsence refTypeAbsence = new RefTypeAbsence();
		refTypeAbsence.setIdRefTypeAbsence(1);

		Pointage ptg = new Pointage();
		ptg.setCommentaire(new PtgComment("aaa"));
		ptg.setMotif(new PtgComment("aaa"));
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(ep);
		ptg.setRefTypeAbsence(refTypeAbsence);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		AbsenceDto abs = new AbsenceDto();
		abs.setIdRefTypeAbsence(1);
		abs.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		abs.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());
		abs.setCommentaire("coucou");
		abs.setMotif("aaa");

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, abs));
	}

	@Test
	public void hasPointageChanged_AbsenceMotifHasChangedEtatisNotSaisi_ReturnFalse() {

		// Given
		RefTypeAbsence refTypeAbsence = new RefTypeAbsence();
		refTypeAbsence.setIdRefTypeAbsence(1);

		Pointage ptg = new Pointage();
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(ep);
		ptg.setRefTypeAbsence(refTypeAbsence);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		AbsenceDto abs = new AbsenceDto();
		abs.setIdRefTypeAbsence(1);
		abs.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		abs.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());
		abs.setMotif("coucou");

		SaisieService service = new SaisieService();

		// Then
		assertFalse(service.hasPointageChanged(ptg, abs));
	}

	@Test
	public void hasPointageChanged_AbsenceCommentaireHasChangedEtatisNotSaisi_ReturnFalse() {

		// Given
		RefTypeAbsence refTypeAbsence = new RefTypeAbsence();
		refTypeAbsence.setIdRefTypeAbsence(1);

		Pointage ptg = new Pointage();
		ptg.setCommentaire(new PtgComment("aaa"));
		ptg.setMotif(new PtgComment("aaa"));
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(ep);
		ptg.setRefTypeAbsence(refTypeAbsence);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		AbsenceDto abs = new AbsenceDto();
		abs.setIdRefTypeAbsence(1);
		abs.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		abs.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());
		abs.setCommentaire("coucou");
		abs.setMotif("aaa");

		SaisieService service = new SaisieService();

		// Then
		assertFalse(service.hasPointageChanged(ptg, abs));
	}

	@Test
	public void hasPointageChanged_HSupHasNotChanged_ReturnFalse() {

		// Given
		Pointage ptg = new Pointage();
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(ep);
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(true);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		HeureSupDto hSup = new HeureSupDto();
		hSup.setRecuperee(true);
		hSup.setRappelService(true);
		hSup.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		hSup.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		SaisieService service = new SaisieService();

		// Then
		assertFalse(service.hasPointageChanged(ptg, hSup));
	}

	@Test
	public void hasPointageChanged_HSupRecupHasChanged_ReturnTrue() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setHeureSupRecuperee(true);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		HeureSupDto hSup = new HeureSupDto();
		hSup.setRecuperee(false);
		hSup.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		hSup.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, hSup));
	}

	@Test
	public void hasPointageChanged_HSupRecupHasChangedRappelService_ReturnTrue() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(true);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		HeureSupDto hSup = new HeureSupDto();
		hSup.setRecuperee(true);
		hSup.setRappelService(false);
		hSup.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		hSup.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, hSup));
	}

	@Test
	public void hasPointageChanged_HSupHoursHasChanged_ReturnTrue() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(true);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		HeureSupDto hSup = new HeureSupDto();
		hSup.setRecuperee(true);
		hSup.setRappelService(false);
		hSup.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		hSup.setHeureFin(new DateTime(2013, 8, 1, 13, 10, 0).toDate());

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, hSup));
	}

	@Test
	public void hasPointageChanged_HSupMotifHasChangedEtatIsSaisi_ReturnTrue() {

		// Given
		Pointage ptg = new Pointage();
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(ep);
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(true);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		HeureSupDto hSup = new HeureSupDto();
		hSup.setRecuperee(true);
		hSup.setRappelService(false);
		hSup.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		hSup.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());
		hSup.setMotif("coucou");

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, hSup));
	}

	@Test
	public void hasPointageChanged_HSupCommentaireHasChangedEtatIsSaisi_ReturnTrue() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setCommentaire(new PtgComment("aaa"));
		ptg.setMotif(new PtgComment("aaa"));
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.SAISI);
		ptg.getEtats().add(ep);
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(true);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		HeureSupDto hSup = new HeureSupDto();
		hSup.setRecuperee(true);
		hSup.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		hSup.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());
		hSup.setCommentaire("coucou");
		hSup.setMotif("aaa");

		SaisieService service = new SaisieService();

		// Then
		assertTrue(service.hasPointageChanged(ptg, hSup));
	}

	@Test
	public void hasPointageChanged_HSupMotifHasChangedEtatisNotSaisi_ReturnFalse() {

		// Given
		Pointage ptg = new Pointage();
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(ep);
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(true);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		HeureSupDto hSup = new HeureSupDto();
		hSup.setRecuperee(true);
		hSup.setRappelService(true);
		hSup.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		hSup.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());
		hSup.setMotif("coucou");

		SaisieService service = new SaisieService();

		// Then
		assertFalse(service.hasPointageChanged(ptg, hSup));
	}

	@Test
	public void hasPointageChanged_HSupCommentaireHasChangedEtatisNotSaisi_ReturnFalse() {

		// Given
		Pointage ptg = new Pointage();
		ptg.setCommentaire(new PtgComment("aaa"));
		ptg.setMotif(new PtgComment("aaa"));
		EtatPointage ep = new EtatPointage();
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ptg.getEtats().add(ep);
		ptg.setHeureSupRecuperee(true);
		ptg.setHeureSupRappelService(true);
		ptg.setDateDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());

		HeureSupDto hSup = new HeureSupDto();
		hSup.setRecuperee(true);
		hSup.setRappelService(true);
		hSup.setHeureDebut(new DateTime(2013, 8, 1, 12, 0, 0).toDate());
		hSup.setHeureFin(new DateTime(2013, 8, 1, 13, 0, 0).toDate());
		hSup.setCommentaire("coucou");
		hSup.setMotif("aaa");

		SaisieService service = new SaisieService();

		// Then
		assertFalse(service.hasPointageChanged(ptg, hSup));
	}

	@Test
	public void findPointageAndRemoveFromOriginals_CantFindPointage_ReturnNull() {

		// Given
		List<Pointage> pointages = new ArrayList<Pointage>();
		Pointage p1 = new Pointage();
		p1.setIdPointage(1);
		pointages.add(p1);

		PointageDto dto = new PrimeDto();
		dto.setIdPointage(9);

		SaisieService service = new SaisieService();

		// When
		Pointage ptg = service.findPointageAndRemoveFromOriginals(pointages, dto);

		// Then
		assertNull(ptg);
		assertEquals(1, pointages.size());
	}

	@Test
	public void findPointageAndRemoveFromOriginals_FindPointage_RemoveFromListAndReturnPointage() {

		// Given
		List<Pointage> pointages = new ArrayList<Pointage>();
		Pointage p1 = new Pointage();
		p1.setIdPointage(1);
		pointages.add(p1);

		PointageDto dto = new PrimeDto();
		dto.setIdPointage(1);

		SaisieService service = new SaisieService();

		// When
		Pointage ptg = service.findPointageAndRemoveFromOriginals(pointages, dto);

		// Then
		assertEquals(ptg, p1);
		assertEquals(0, pointages.size());
	}

	@Test
	public void markPointagesAsApproved_NoVentilDate_SetCurrentDateAsDateEtat() {

		// Given
		Date dateLundi = new LocalDate(2013, 7, 1).toDate();

		Pointage p1 = new Pointage();
		p1.getEtats().add(new EtatPointage());
		p1.getEtats().get(0).setEtat(EtatPointageEnum.SAISI);

		Integer idAgentOperator = 9007867;
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 4, 7, 9, 0, 0).toDate());
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F)).thenReturn(chainePaie);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009999)).thenReturn(9999);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(null);

		Spcarr carr = new Spcarr();
		carr.setCdcate(6);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(9999, dateLundi)).thenReturn(carr);

		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		service.markPointagesAsApproved(Arrays.asList(p1), dateLundi, 9009999, idAgentOperator);

		// Then
		assertEquals(2, p1.getEtats().size());
		assertEquals(idAgentOperator, p1.getEtats().get(1).getIdAgent());
		assertEquals(new DateTime(2013, 4, 7, 9, 0, 0).toDate(), p1.getEtats().get(1).getDateEtat());
		assertEquals(new DateTime(2013, 4, 7, 9, 0, 0).toDate(), p1.getEtats().get(1).getDateMaj());
		assertEquals(EtatPointageEnum.APPROUVE, p1.getEtats().get(1).getEtat());
		assertEquals(p1, p1.getEtats().get(1).getPointage());
	}

	@Test
	public void markPointagesAsApproved_1VentilDate_SetVentilDateAsDateEtat() {

		// Given
		Date dateLundi = new LocalDate(2013, 7, 1).toDate();

		Pointage p1 = new Pointage();
		p1.getEtats().add(new EtatPointage());
		p1.getEtats().get(0).setEtat(EtatPointageEnum.SAISI);

		Integer idAgentOperator = 9007867;
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 4, 7, 9, 0, 0).toDate());
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F)).thenReturn(chainePaie);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009999)).thenReturn(9999);

		VentilDate vD = new VentilDate();
		vD.setDateVentilation(new DateTime(2013, 4, 5, 5, 5, 5).toDate());
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(vD);

		Spcarr carr = new Spcarr();
		carr.setCdcate(6);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(9999, dateLundi)).thenReturn(carr);

		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		service.markPointagesAsApproved(Arrays.asList(p1), dateLundi, 9009999, idAgentOperator);

		// Then
		assertEquals(2, p1.getEtats().size());
		assertEquals(idAgentOperator, p1.getEtats().get(1).getIdAgent());
		assertEquals(vD.getDateVentilation(), p1.getEtats().get(1).getDateEtat());
		assertEquals(new DateTime(2013, 4, 7, 9, 0, 0).toDate(), p1.getEtats().get(1).getDateMaj());
		assertEquals(EtatPointageEnum.APPROUVE, p1.getEtats().get(1).getEtat());
		assertEquals(p1, p1.getEtats().get(1).getPointage());
	}

	@Test
	public void markPointagesAsApproved_PtgIsApproved_DoNothing() {

		// Given
		Date dateLundi = new LocalDate(2013, 7, 1).toDate();

		Pointage p1 = new Pointage();
		p1.getEtats().add(new EtatPointage());
		p1.getEtats().get(0).setEtat(EtatPointageEnum.APPROUVE);

		Integer idAgentOperator = 9007867;
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 4, 7, 9, 0, 0).toDate());
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F)).thenReturn(chainePaie);
		Mockito.when(hS.getMairieMatrFromIdAgent(9009999)).thenReturn(9999);

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(null);

		Spcarr carr = new Spcarr();
		carr.setCdcate(6);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(9999, dateLundi)).thenReturn(carr);

		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);

		// When
		service.markPointagesAsApproved(Arrays.asList(p1), dateLundi, 9009999, idAgentOperator);

		// Then
		assertEquals(1, p1.getEtats().size());
	}

	@Test
	public void saveFichePointage_noExistingPointage_saveNewAbsence_2Days() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		dto.setSaisies(Arrays.asList(new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque()));

		AbsenceDtoKiosque abs3 = new AbsenceDtoKiosque();
		abs3.setHeureDebutDate(new DateTime(2013, 05, 16, 15, 0, 0).toDate());
		abs3.setHeureFinDate(new DateTime(2013, 05, 17, 16, 0, 0).toDate());
		abs3.setMotif("le motif 3");
		abs3.setCommentaire("le commentaire 3");
		abs3.setIdRefTypeAbsence(1);
		dto.getSaisies().get(3).getAbsences().add(abs3);

		Date currentDate = new DateTime(2013, 05, 22, 9, 8, 00).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);
		Mockito.when(hS.getMairieMatrFromIdAgent(9007654)).thenReturn(7654);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F)).thenReturn(chainePaie);

		RefTypePointage absRef = new RefTypePointage();
		absRef.setIdRefTypePointage(1);
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(
				new ArrayList<Pointage>());
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 1)).thenReturn(absRef);

		Pointage newAbsPointage = new Pointage();
		newAbsPointage.setIdAgent(agent.getIdAgent());
		newAbsPointage.setDateLundi(lundi);
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(9001234, null, agent.getIdAgent(), lundi, currentDate))
				.thenReturn(newAbsPointage);

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(new ReturnMessageDto());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(null);

		Spcarr carr = new Spcarr();
		carr.setCdcate(6);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(7654, lundi)).thenReturn(carr);

		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);

		// When
		service.saveFichePointage(9001234, dto);

		// Then
		ArgumentCaptor<Pointage> argument = ArgumentCaptor.forClass(Pointage.class);
		Mockito.verify(pRepo).savePointage(argument.capture());

		assertEquals(RefTypePointageEnum.ABSENCE, argument.getValue().getTypePointageEnum());
		assertEquals(agent.getIdAgent(), argument.getValue().getIdAgent());
		assertEquals(new DateTime(2013, 05, 16, 15, 0, 0).toDate(), argument.getValue().getDateDebut());
		assertEquals(new DateTime(2013, 05, 17, 16, 0, 0).toDate(), argument.getValue().getDateFin());
		assertEquals("le commentaire 3", argument.getValue().getCommentaire().getText());
		assertEquals("le motif 3", argument.getValue().getMotif().getText());
		assertEquals(lundi, argument.getValue().getDateLundi());
		assertNull(argument.getValue().getIdPointage());
		assertNull(argument.getValue().getQuantite());
		assertNull(argument.getValue().getRefPrime());
		assertNull(argument.getValue().getHeureSupRecuperee());
		assertNull(argument.getValue().getPointageParent());
	}

	@Test
	public void saveFichePointage_noExistingPointage_saveNewHsup_2Days() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		dto.setSaisies(Arrays.asList(new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque(), new JourPointageDtoKiosque(),
				new JourPointageDtoKiosque(), new JourPointageDtoKiosque()));

		MotifHeureSup motifHsup = new MotifHeureSup();
		motifHsup.setIdMotifHsup(2);
		motifHsup.setText("le motif 3");

		HeureSupDtoKiosque hs1 = new HeureSupDtoKiosque();
		hs1.setRecuperee(true);
		hs1.setHeureDebutDate(new DateTime(2013, 05, 16, 15, 0, 0).toDate());
		hs1.setHeureFinDate(new DateTime(2013, 05, 17, 16, 0, 0).toDate());
		hs1.setCommentaire("le commentaire 3");
		hs1.setIdMotifHsup(2);
		dto.getSaisies().get(3).getHeuresSup().add(hs1);

		Date currentDate = new DateTime(2013, 05, 22, 9, 8, 00).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);
		Mockito.when(hS.getMairieMatrFromIdAgent(9007654)).thenReturn(7654);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.F)).thenReturn(chainePaie);

		RefTypePointage absRef = new RefTypePointage();
		absRef.setIdRefTypePointage(2);
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(
				new ArrayList<Pointage>());
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 2)).thenReturn(absRef);
		Mockito.when(pRepo.getEntity(MotifHeureSup.class, 2)).thenReturn(motifHsup);

		Pointage newHsPointage = new Pointage();
		newHsPointage.setIdAgent(agent.getIdAgent());
		newHsPointage.setDateLundi(lundi);
		newHsPointage.setDateDebut(new Date());
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(9001234, null, agent.getIdAgent(), lundi, currentDate))
				.thenReturn(newHsPointage);

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(new ReturnMessageDto());

		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(chainePaie, false)).thenReturn(null);

		Spcarr carr = new Spcarr();
		carr.setCdcate(6);
		IMairieRepository sR = Mockito.mock(IMairieRepository.class);
		Mockito.when(sR.getAgentCurrentCarriere(7654, lundi)).thenReturn(carr);

		SirhWsServiceDto dtoService = new SirhWsServiceDto();
		dtoService.setSigle("TITI");

		ISirhWSConsumer sirhRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sirhRepo.getAgentDirection(agent.getIdAgent(), newHsPointage.getDateDebut())).thenReturn(
				dtoService);

		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		ReflectionTestUtils.setField(service, "mairieRepository", sR);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sirhRepo);

		// When
		service.saveFichePointage(9001234, dto);

		// Then
		ArgumentCaptor<Pointage> argument = ArgumentCaptor.forClass(Pointage.class);
		Mockito.verify(pRepo).savePointage(argument.capture());

		assertEquals(RefTypePointageEnum.H_SUP, argument.getValue().getTypePointageEnum());
		assertTrue(argument.getValue().getHeureSupRecuperee());
		assertEquals(agent.getIdAgent(), argument.getValue().getIdAgent());
		assertEquals(new DateTime(2013, 05, 16, 15, 0, 0).toDate(), argument.getValue().getDateDebut());
		assertEquals(new DateTime(2013, 05, 17, 16, 0, 0).toDate(), argument.getValue().getDateFin());
		assertEquals("le commentaire 3", argument.getValue().getCommentaire().getText());
		assertEquals("le motif 3", argument.getValue().getMotifHsup().getText());
		assertEquals(lundi, argument.getValue().getDateLundi());
		assertNull(argument.getValue().getIdPointage());
		assertNull(argument.getValue().getQuantite());
		assertNull(argument.getValue().getRefPrime());
		assertNull(argument.getValue().getRefTypeAbsence());
		assertNull(argument.getValue().getPointageParent());
	}

	@Test
	public void saveFichePointage_noExistingPointage_saveNewPrime_2Days() {

		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);

		FichePointageDtoKiosque dto = new FichePointageDtoKiosque();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);

		// prime
		PrimeDtoKiosque p = new PrimeDtoKiosque();
		p.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES.toString());
		p.setIdRefPrime(22);
		p.setNumRubrique(1111);
		p.setTitre("Le titre");
		JourPointageDtoKiosque j1 = new JourPointageDtoKiosque();
		j1.getPrimes().add(p);
		dto.setSaisies(Arrays.asList(j1, new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1),
				new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1), new JourPointageDtoKiosque(j1),
				new JourPointageDtoKiosque(j1)));
		PrimeDtoKiosque prime7thday = dto.getSaisies().get(6).getPrimes().get(0);
		prime7thday.setHeureDebutDate(new DateTime(2013, 05, 19, 8, 0, 0).toDate());
		prime7thday.setHeureFinDate(new DateTime(2013, 05, 20, 11, 0, 0).toDate());
		prime7thday.setQuantite(3);
		prime7thday.setMotif("mot");
		prime7thday.setCommentaire("com");

		Date currentDate = new DateTime(2013, 05, 22, 9, 8, 00).toDate();
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(currentDate);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(
				new ArrayList<Pointage>());

		RefTypePointage primTypeRef = new RefTypePointage();
		primTypeRef.setIdRefTypePointage(3);
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 3)).thenReturn(primTypeRef);

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(22);
		refPrime.setNoRubr(1111);

		Pointage newPrimePointage = new Pointage();
		newPrimePointage.setIdAgent(agent.getIdAgent());
		newPrimePointage.setDateLundi(lundi);
		newPrimePointage.setRefPrime(refPrime);
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(9001234, null, agent.getIdAgent(), lundi, currentDate, 22))
				.thenReturn(newPrimePointage);

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.when(dcMock.checkDateLundiAnterieurA3Mois(Mockito.isA(ReturnMessageDto.class), Mockito.isA(Date.class)))
				.thenReturn(new ReturnMessageDto());

		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);

		// When
		service.saveFichePointage(9001234, dto);

		// Then
		ArgumentCaptor<Pointage> argument = ArgumentCaptor.forClass(Pointage.class);
		Mockito.verify(pRepo).savePointage(argument.capture());

		assertEquals(RefTypePointageEnum.PRIME, argument.getValue().getTypePointageEnum());
		assertEquals(22, (int) argument.getValue().getRefPrime().getIdRefPrime());
		assertEquals(1111, (int) argument.getValue().getRefPrime().getNoRubr());
		assertEquals(agent.getIdAgent(), argument.getValue().getIdAgent());
		assertEquals(3, (int) argument.getValue().getQuantite());
		assertEquals(new DateTime(2013, 05, 19, 8, 0, 0).toDate(), argument.getValue().getDateDebut());
		assertEquals(new DateTime(2013, 05, 20, 11, 0, 0).toDate(), argument.getValue().getDateFin());
		assertEquals("com", argument.getValue().getCommentaire().getText());
		assertEquals("mot", argument.getValue().getMotif().getText());
		assertEquals(lundi, argument.getValue().getDateLundi());
		assertNull(argument.getValue().getIdPointage());
		assertNull(argument.getValue().getHeureSupRecuperee());
		assertNull(argument.getValue().getRefTypeAbsence());
		assertNull(argument.getValue().getPointageParent());
	}

	@Test
	public void crudCommentsHeureSup_motif_commentaire_newString() {

		// Given
		Pointage ptg = new Pointage();
		MotifHeureSup motif = new MotifHeureSup();
		motif.setIdMotifHsup(1);
		motif.setText("essai");
		String commentaire = "commentaire";

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getEntity(MotifHeureSup.class, motif.getIdMotifHsup())).thenReturn(motif);

		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);

		// When
		service.crudCommentsHeureSup(ptg, motif.getIdMotifHsup(), commentaire);

		// Then
		assertEquals(motif.getText(), ptg.getMotifHsup().getText());
		assertEquals(commentaire, ptg.getCommentaire().getText());
	}

	@Test
	public void crudCommentsHeureSup_motif_commentaire_modifyString() {

		// Given
		Pointage ptg = new Pointage();
		MotifHeureSup motif = new MotifHeureSup();
		motif.setIdMotifHsup(1);
		motif.setText("value");
		ptg.setMotifHsup(motif);
		ptg.setCommentaire(new PtgComment());
		String commentaire = "commentaire";

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getEntity(MotifHeureSup.class, motif.getIdMotifHsup())).thenReturn(motif);

		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);

		// When
		service.crudCommentsHeureSup(ptg, motif.getIdMotifHsup(), commentaire);

		// Then
		assertEquals(motif.getText(), ptg.getMotifHsup().getText());
		assertEquals(commentaire, ptg.getCommentaire().getText());
	}

	@Test
	public void crudCommentsHeureSup_motif_commentaire_deleteComment() {

		// Given
		MotifHeureSup existingMotif = Mockito.spy(new MotifHeureSup());
		existingMotif.setText("value");

		PtgComment existingComment = Mockito.spy(new PtgComment());
		existingComment.setText("value2");

		Pointage ptg = new Pointage();
		ptg.setMotifHsup(existingMotif);
		ptg.setCommentaire(existingComment);

		String commentaire = "";

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(MotifHeureSup.class, existingMotif.getIdMotifHsup())).thenReturn(null);

		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		// When
		service.crudCommentsHeureSup(ptg, existingMotif.getIdMotifHsup(), commentaire);

		// Then
		assertNull(ptg.getMotifHsup());
		assertNull(ptg.getCommentaire());

		Mockito.verify(pRepo, Mockito.times(1)).removeEntity(Mockito.isA(PtgComment.class));
	}

}
