package nc.noumea.mairie.ptg.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.dto.AbsenceDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.HeureSupDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.SaisieReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class SaisieServiceTest {

	@Test
	public void saveFichePointage_dateIsNotAMonday_throwException() {
		// Given
		Date notAMonday = new DateTime(2013, 05, 14, 0, 0, 0).toDate();
		FichePointageDto fichePointageDto = new FichePointageDto();
		fichePointageDto.setDateLundi(notAMonday);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(notAMonday)).thenReturn(false);
		
		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "helperService", hS);

		
		// When
		try {
			service.saveFichePointage(fichePointageDto);
		} catch (NotAMondayException ex) {
			return;
		}
		
		fail("Should have thrown a NotAMondayException");
	}
	
	@Test
	public void saveFichePointage_noExistingPointage_saveNewAbsence() {
		
		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);
		
		FichePointageDto dto = new FichePointageDto();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		dto.setSaisies(Arrays.asList(new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto()));
		
		AbsenceDto abs3 = new AbsenceDto();
		abs3.setConcertee(true);
		abs3.setHeureDebut(new DateTime(2013, 05, 16, 15, 0, 0).toDate());
		abs3.setHeureFin(new DateTime(2013, 05, 16, 16, 0, 0).toDate());
		abs3.setMotif("le motif 3");
		abs3.setCommentaire("le commentaire 3");
		dto.getSaisies().get(3).getAbsences().add(abs3);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 05, 22, 9, 8, 00).toDate());
		
		RefTypePointage absRef = new RefTypePointage();
		absRef.setIdRefTypePointage(1);
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(new ArrayList<Pointage>());
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 1)).thenReturn(absRef);
		
		Pointage newAbsPointage = new Pointage();
		newAbsPointage.setIdAgent(agent.getIdAgent());
		newAbsPointage.setDateLundi(lundi);
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(null, agent.getIdAgent(), lundi)).thenReturn(newAbsPointage);
		
		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		
		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		
		// When
		service.saveFichePointage(dto);
		
		// Then
		ArgumentCaptor<Pointage> argument = ArgumentCaptor.forClass(Pointage.class);
		Mockito.verify(pRepo).savePointage(argument.capture());
		
		assertEquals(RefTypePointageEnum.ABSENCE, argument.getValue().getTypePointageEnum());
		assertTrue(argument.getValue().getAbsenceConcertee());
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
	public void saveFichePointage_noExistingPointage_saveNewHsup() {
		
		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);
		
		FichePointageDto dto = new FichePointageDto();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		dto.setSaisies(Arrays.asList(new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto()));
		
		HeureSupDto hs1 = new HeureSupDto();
		hs1.setRecuperee(true);
		hs1.setHeureDebut(new DateTime(2013, 05, 16, 15, 0, 0).toDate());
		hs1.setHeureFin(new DateTime(2013, 05, 16, 16, 0, 0).toDate());
		hs1.setMotif("le motif 3");
		hs1.setCommentaire("le commentaire 3");
		dto.getSaisies().get(3).getHeuresSup().add(hs1);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 05, 22, 9, 8, 00).toDate());
		
		RefTypePointage absRef = new RefTypePointage();
		absRef.setIdRefTypePointage(2);
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(new ArrayList<Pointage>());
		Mockito.when(pRepo.getEntity(RefTypePointage.class, 2)).thenReturn(absRef);

		Pointage newHsPointage = new Pointage();
		newHsPointage.setIdAgent(agent.getIdAgent());
		newHsPointage.setDateLundi(lundi);
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(null, agent.getIdAgent(), lundi)).thenReturn(newHsPointage);
		
		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		
		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		
		// When
		service.saveFichePointage(dto);
		
		// Then
		ArgumentCaptor<Pointage> argument = ArgumentCaptor.forClass(Pointage.class);
		Mockito.verify(pRepo).savePointage(argument.capture());
		
		assertEquals(RefTypePointageEnum.H_SUP, argument.getValue().getTypePointageEnum());
		assertTrue(argument.getValue().getHeureSupRecuperee());
		assertEquals(agent.getIdAgent(), argument.getValue().getIdAgent());
		assertEquals(new DateTime(2013, 05, 16, 15, 0, 0).toDate(), argument.getValue().getDateDebut());
		assertEquals(new DateTime(2013, 05, 16, 16, 0, 0).toDate(), argument.getValue().getDateFin());
		assertEquals("le commentaire 3", argument.getValue().getCommentaire().getText());
		assertEquals("le motif 3", argument.getValue().getMotif().getText());
		assertEquals(lundi, argument.getValue().getDateLundi());
		assertNull(argument.getValue().getIdPointage());
		assertNull(argument.getValue().getQuantite());
		assertNull(argument.getValue().getRefPrime());
		assertNull(argument.getValue().getAbsenceConcertee());
		assertNull(argument.getValue().getPointageParent());
	}
	
	@Test
	public void saveFichePointage_noExistingPointage_saveNewPrime() {
		
		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);
		
		FichePointageDto dto = new FichePointageDto();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		
		
		// prime
		PrimeDto p = new PrimeDto();
		p.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES.toString());
		p.setIdRefPrime(22);
		p.setNumRubrique(1111);
		p.setTitre("Le titre");
		JourPointageDto j1 = new JourPointageDto();
		j1.getPrimes().add(p);
		dto.setSaisies(Arrays.asList(j1, new JourPointageDto(j1), new JourPointageDto(j1), new JourPointageDto(j1), 
				new JourPointageDto(j1), new JourPointageDto(j1), new JourPointageDto(j1)));
		PrimeDto prime7thday = dto.getSaisies().get(6).getPrimes().get(0);
		prime7thday.setHeureDebut(new DateTime(2013, 05, 19, 8, 0, 0).toDate());
		prime7thday.setHeureFin(new DateTime(2013, 05, 19, 11, 0, 0).toDate());
		prime7thday.setQuantite(3);
		prime7thday.setMotif("mot");
		prime7thday.setCommentaire("com");
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 05, 22, 9, 8, 00).toDate());
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(new ArrayList<Pointage>());
		
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
		Mockito.when(pService.getOrCreateNewPointage(null, agent.getIdAgent(), lundi, 22)).thenReturn(newPrimePointage);
		
		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		
		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		
		// When
		service.saveFichePointage(dto);
		
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
		assertNull(argument.getValue().getAbsenceConcertee());
		assertNull(argument.getValue().getPointageParent());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void saveFichePointage_saveNewAbsence_ErrorInConsistency_saveNothingAndReturnMessage() {
		
		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);
		
		FichePointageDto dto = new FichePointageDto();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		
		// prime
		AbsenceDto abs = new AbsenceDto();
		abs.setConcertee(true);
		abs.setHeureDebut(new DateTime(2013, 05, 13, 12, 0, 0).toDate());
		abs.setHeureFin(new DateTime(2013, 05, 13, 18, 0, 0).toDate());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().get(0).getAbsences().add(abs);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 05, 22, 9, 8, 00).toDate());
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(new ArrayList<Pointage>());
		
		Pointage newAbsPointage = new Pointage();
		newAbsPointage.setIdAgent(agent.getIdAgent());
		newAbsPointage.setDateLundi(lundi);
		
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(null, agent.getIdAgent(), lundi)).thenReturn(newAbsPointage);
		
		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		Mockito.doAnswer(
				new Answer() {
					  public Object answer(InvocationOnMock invocation) {
					      Object[] args = invocation.getArguments();
					      SaisieReturnMessageDto result = (SaisieReturnMessageDto)args[0];
					      result.getErrors().add("message d'erreur");
					      return null;
					  }}
			).when(dcMock).processDataConsistency(
				Mockito.any(SaisieReturnMessageDto.class), Mockito.eq(agent.getIdAgent()), Mockito.eq(lundi), Mockito.anyList());
		
		SaisieService service = new SaisieService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		
		// When
		SaisieReturnMessageDto res = service.saveFichePointage(dto);
		
		// Then
		Mockito.verify(pRepo, Mockito.never()).savePointage(newAbsPointage);

		assertEquals(1, res.getErrors().size());
		assertEquals(0, res.getInfos().size());
		assertEquals("message d'erreur", res.getErrors().get(0));
	}
	
	@Test
	public void saveFichePointage_deletedPointageFromDto_delete1PointageKeep1BecauseOldVersion() {
		
		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentWithServiceDto agent = new AgentWithServiceDto();
		agent.setIdAgent(9007654);
		
		FichePointageDto dto = new FichePointageDto();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		dto.setSaisies(Arrays.asList(new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto()));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);

		Pointage p = Mockito.spy(new Pointage());
		EtatPointage e1 = new EtatPointage();
		e1.setEtat(EtatPointageEnum.SAISI);
		p.getEtats().add(e1);
		Mockito.doNothing().when(p).remove();
		Pointage p2 = Mockito.spy(new Pointage());
		EtatPointage e2 = new EtatPointage();
		e2.setEtat(EtatPointageEnum.REFUSE);
		p2.getEtats().add(e2);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(Arrays.asList(p, p2));

		IPointageDataConsistencyRules dcMock = Mockito.mock(IPointageDataConsistencyRules.class);
		
		SaisieService service = Mockito.spy(new SaisieService());
		Mockito.doNothing().when(service).deletePointages(Arrays.asList(p, p2));
		
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "ptgDataCosistencyRules", dcMock);
		
		// When
		service.saveFichePointage(dto);
		
		// Then
		Mockito.verify(service, Mockito.times(1)).deletePointages(Arrays.asList(p, p2));
	}
	
	@Test
	public void deletePointages_1PointageSaisi_RemoveIt() {
		
		// Given
		Pointage p1 = Mockito.spy(new Pointage());
		Mockito.doNothing().when(p1).remove();
		EtatPointage e1 = new EtatPointage();
		e1.setEtat(EtatPointageEnum.SAISI);
		p1.getEtats().add(e1);
		
		List<Pointage> ptgToDelete = Arrays.asList(p1);
		SaisieService service = new SaisieService();
		
		// When
		service.deletePointages(ptgToDelete);
		
		// Then
		Mockito.verify(p1, Mockito.times(1)).remove();
	}
	
	@Test
	public void deletePointages_1PointageNotSaisi_createANewOneToSave() {
		
		// Given
		Pointage p1 = Mockito.spy(new Pointage());
		Mockito.doNothing().when(p1).remove();
		EtatPointage e1 = new EtatPointage();
		e1.setEtat(EtatPointageEnum.VENTILE);
		p1.getEtats().add(e1);
		
		Pointage p1bis = new Pointage();
		p1bis.setPointageParent(p1);
		EtatPointage e2 = new EtatPointage();
		e2.setEtat(EtatPointageEnum.SAISI);
		p1bis.getEtats().add(e2);
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		
		IPointageService pService = Mockito.mock(IPointageService.class);
		Mockito.when(pService.getOrCreateNewPointage(p1)).thenReturn(p1bis);
		
		List<Pointage> ptgToDelete = Arrays.asList(p1);
		SaisieService service = new SaisieService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "pointageService", pService);
		
		// When
		service.deletePointages(ptgToDelete);
		
		// Then
		Mockito.verify(pRepo, Mockito.times(1)).savePointage(p1bis);
	}
	
}
