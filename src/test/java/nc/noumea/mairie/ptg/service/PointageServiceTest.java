package nc.noumea.mairie.ptg.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.SpcarrId;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.dto.AbsenceDto;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.HeureSupDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;
import nc.noumea.mairie.ptg.repository.IMairieRepository;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.PrimePointage;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.staticmock.MockStaticEntityMethods;
import org.springframework.test.util.ReflectionTestUtils;

@MockStaticEntityMethods
public class PointageServiceTest {

	@Test
	public void getAgentFichePointage() throws ParseException {

		// Given
		Integer idAgent = 9007654;

		ServiceDto siserv = new ServiceDto();
		siserv.setService("SERVICE");
		siserv.setServiceLibelle("LIB SERVICE");

		SpcarrId carrId = new SpcarrId();
		carrId.setNomatr(7654);
		carrId.setDatdeb(20120506);
		Spcarr carr = new Spcarr();
		carr.setId(carrId);
		carr.setCdcate(1);

		Agent agent = new Agent();
		agent.setIdAgent(9007654);
		
		List<PrimePointage> listePrimePointage = new ArrayList<PrimePointage>();
		PrimePointage po = new PrimePointage();
		po.setNumRubrique(7058);
		PrimePointage po2 = new PrimePointage();
		po2.setNumRubrique(7059);

		listePrimePointage.add(po);
		listePrimePointage.add(po2);

		RefPrime rp1 = new RefPrime();
		rp1.setNoRubr(7058);
		rp1.setIdRefPrime(1111);
		rp1.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		RefPrime rp2 = new RefPrime();
		rp2.setNoRubr(7059);
		rp2.setIdRefPrime(2222);
		rp2.setTypeSaisie(TypeSaisieEnum.PERIODE_HEURES);
		
		Date d = new DateTime(2013, 05, 15, 0, 0, 0).toDate();

		ISirhWSConsumer wsMock = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(wsMock.getAgentDirection(idAgent)).thenReturn(siserv);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getPrimePointagesByAgent(agent.getIdAgent(), d)).thenReturn(listePrimePointage);
		Mockito.when(arRepo.getRefPrimes(Arrays.asList(7058, 7059), carr.getStatutCarriere())).thenReturn(Arrays.asList(rp1, rp2));

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(agent, d)).thenReturn(carr);

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getCurrentDate()).thenReturn(d);
		Mockito.when(helperMock.isDateAMonday(d)).thenReturn(true);
		Mockito.when(helperMock.getWeekStringFromDate(d)).thenReturn("week string");

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "sirhWSConsumer", wsMock);
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);

		FichePointageDto dto = service.getFichePointageForAgent(agent, d);

		// Then
		assertEquals(d, dto.getDateLundi());
		assertEquals("week string", dto.getSemaine());
		assertEquals(9007654, (int) dto.getAgent().getIdAgent());
		assertEquals("F", dto.getAgent().getStatut());
		assertEquals(7, dto.getSaisies().size());
		assertEquals(7058, (int) dto.getSaisies().get(0).getPrimes().get(0).getNumRubrique());
		assertEquals(7059, (int) dto.getSaisies().get(0).getPrimes().get(1).getNumRubrique());
		assertEquals(7058, (int) dto.getSaisies().get(1).getPrimes().get(0).getNumRubrique());
		assertEquals(7059, (int) dto.getSaisies().get(1).getPrimes().get(1).getNumRubrique());
		assertEquals(7058, (int) dto.getSaisies().get(2).getPrimes().get(0).getNumRubrique());
		assertEquals(7059, (int) dto.getSaisies().get(2).getPrimes().get(1).getNumRubrique());
		assertEquals(7058, (int) dto.getSaisies().get(3).getPrimes().get(0).getNumRubrique());
		assertEquals(7059, (int) dto.getSaisies().get(3).getPrimes().get(1).getNumRubrique());
		assertEquals(7058, (int) dto.getSaisies().get(4).getPrimes().get(0).getNumRubrique());
		assertEquals(7059, (int) dto.getSaisies().get(4).getPrimes().get(1).getNumRubrique());
		assertEquals(7058, (int) dto.getSaisies().get(5).getPrimes().get(0).getNumRubrique());
		assertEquals(7059, (int) dto.getSaisies().get(5).getPrimes().get(1).getNumRubrique());
		assertEquals(7058, (int) dto.getSaisies().get(6).getPrimes().get(0).getNumRubrique());
		assertEquals(7059, (int) dto.getSaisies().get(6).getPrimes().get(1).getNumRubrique());
	}

	@Test
	public void getFilledFichePointageForAgent_Agentwith3PointagesHSUPandABS_Return2latest() {

		// Given
		int idAgent = 9006543;
		Agent ag = new Agent();
		ag.setIdAgent(9006543);
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		FichePointageDto dto = new FichePointageDto();
		dto.setAgent(new AgentDto());
		dto.setDateLundi(dateLundi);
		dto.setSemaine("SEMAINE");
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());

		Pointage p1 = new Pointage();
		p1.setIdPointage(1);
		RefTypePointage t1 = new RefTypePointage();
		t1.setIdRefTypePointage(1);
		t1.setLabel("ABSENCE");
		p1.setType(t1);
		p1.setAbsenceConcertee(true);
		p1.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 14, 12, 0, 0).toDate());
		p1.setDateLundi(dateLundi);
		EtatPointage e1 = new EtatPointage();
		e1.setEtat(EtatPointageEnum.REFUSE);
		p1.getEtats().add(e1);

		Pointage p1new = new Pointage();
		p1new.setIdPointage(3);
		p1new.setPointageParent(p1);
		p1new.setType(t1);
		p1new.setAbsenceConcertee(true);
		p1new.setDateDebut(new DateTime(2013, 05, 14, 9, 0, 0).toDate());
		p1new.setDateFin(new DateTime(2013, 05, 14, 12, 0, 0).toDate());
		p1new.setDateLundi(dateLundi);
		EtatPointage e1new = new EtatPointage();
		e1new.setEtat(EtatPointageEnum.SAISI);
		p1new.getEtats().add(e1new);

		Pointage p2 = new Pointage();
		p2.setIdPointage(2);
		RefTypePointage t2 = new RefTypePointage();
		t2.setIdRefTypePointage(2);
		t2.setLabel("H_SUP");
		p2.setType(t2);
		p2.setHeureSupPayee(true);
		p2.setDateDebut(new DateTime(2013, 05, 16, 14, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 16, 16, 0, 0).toDate());
		p2.setDateLundi(dateLundi);
		EtatPointage e2 = new EtatPointage();
		e2.setEtat(EtatPointageEnum.APPROUVE);
		p2.getEtats().add(e2);

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgent(idAgent)).thenReturn(ag);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(idAgent, dateLundi)).thenReturn(Arrays.asList(p1new, p2, p1));

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p1new.getDateDebut())).thenReturn(1);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p2.getDateDebut())).thenReturn(3);

		PointageService service = Mockito.spy(new PointageService());
		Mockito.doReturn(dto).when(service).getFichePointageForAgent(ag, dateLundi);

		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);

		// When
		FichePointageDto result = service.getFilledFichePointageForAgent(idAgent, dateLundi);

		// Then
		assertEquals(1, result.getSaisies().get(1).getAbsences().size());
		assertEquals(true, result.getSaisies().get(1).getAbsences().get(0).getConcertee());
		assertEquals("SAISI", result.getSaisies().get(1).getAbsences().get(0).getEtat());
		assertEquals(new DateTime(2013, 05, 14, 9, 0, 0).toDate(), result.getSaisies().get(1).getAbsences().get(0).getHeureDebut());
		assertEquals(new DateTime(2013, 05, 14, 12, 0, 0).toDate(), result.getSaisies().get(1).getAbsences().get(0).getHeureFin());
		assertEquals(new Integer(3), result.getSaisies().get(1).getAbsences().get(0).getIdPointage());

		assertEquals(1, result.getSaisies().get(3).getHeuresSup().size());
		assertEquals(true, result.getSaisies().get(3).getHeuresSup().get(0).getPayee());
		assertEquals("APPROUVE", result.getSaisies().get(3).getHeuresSup().get(0).getEtat());
		assertEquals(new DateTime(2013, 05, 16, 14, 0, 0).toDate(), result.getSaisies().get(3).getHeuresSup().get(0).getHeureDebut());
		assertEquals(new DateTime(2013, 05, 16, 16, 0, 0).toDate(), result.getSaisies().get(3).getHeuresSup().get(0).getHeureFin());
		assertEquals(new Integer(2), result.getSaisies().get(3).getHeuresSup().get(0).getIdPointage());
	}

	@Test
	public void getFilledFichePointageForAgent_Agentwith2PointagesPrimes_Return2Primes() {

		// Given
		int idAgent = 9006543;
		Agent ag = new Agent();
		ag.setIdAgent(9006543);
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		FichePointageDto dto = new FichePointageDto();
		dto.setAgent(new AgentDto());
		dto.setDateLundi(dateLundi);
		dto.setSemaine("SEMAINE");
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());

		PrimeDto pr1 = new PrimeDto();
		pr1.setNumRubrique(1111);
		pr1.setIdRefPrime(11);
		dto.getSaisies().get(1).getPrimes().add(pr1);

		PrimeDto pr2 = new PrimeDto();
		pr2.setNumRubrique(2222);
		pr2.setIdRefPrime(22);
		dto.getSaisies().get(3).getPrimes().add(pr2);

		Pointage p1 = new Pointage();
		p1.setIdPointage(1);
		p1.setRefPrime(new RefPrime());
		p1.getRefPrime().setNoRubr(1111);
		p1.getRefPrime().setIdRefPrime(11);
		p1.getRefPrime().setTypeSaisie(TypeSaisieEnum.PERIODE_HEURES);
		RefTypePointage t3 = new RefTypePointage();
		t3.setIdRefTypePointage(3);
		t3.setLabel("PRIME");
		p1.setType(t3);
		p1.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 14, 12, 0, 0).toDate());
		p1.setDateLundi(dateLundi);
		EtatPointage e1 = new EtatPointage();
		e1.setEtat(EtatPointageEnum.REFUSE);
		p1.getEtats().add(e1);

		Pointage p2 = new Pointage();
		p2.setIdPointage(2);
		p2.setRefPrime(new RefPrime());
		p2.getRefPrime().setNoRubr(2222);
		p2.getRefPrime().setIdRefPrime(22);
		p2.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		p2.setType(t3);
		p2.setQuantite(1);
		p2.setDateDebut(new DateTime(2013, 05, 16, 0, 0, 0).toDate());
		p2.setDateLundi(dateLundi);
		EtatPointage e2 = new EtatPointage();
		e2.setEtat(EtatPointageEnum.APPROUVE);
		p2.getEtats().add(e2);

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgent(idAgent)).thenReturn(ag);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(idAgent, dateLundi)).thenReturn(Arrays.asList(p2, p1));

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p1.getDateDebut())).thenReturn(1);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p2.getDateDebut())).thenReturn(3);

		PointageService service = Mockito.spy(new PointageService());
		Mockito.doReturn(dto).when(service).getFichePointageForAgent(ag, dateLundi);

		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);

		// When
		FichePointageDto result = service.getFilledFichePointageForAgent(idAgent, dateLundi);

		// Then
		assertEquals(1, result.getSaisies().get(1).getPrimes().size());
		assertNull(result.getSaisies().get(1).getPrimes().get(0).getQuantite());
		assertEquals(new Integer(1111), result.getSaisies().get(1).getPrimes().get(0).getNumRubrique());
		assertEquals(new Integer(11), result.getSaisies().get(1).getPrimes().get(0).getIdRefPrime());
		assertEquals("REFUSE", result.getSaisies().get(1).getPrimes().get(0).getEtat());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), result.getSaisies().get(1).getPrimes().get(0).getHeureDebut());
		assertEquals(new DateTime(2013, 05, 14, 12, 0, 0).toDate(), result.getSaisies().get(1).getPrimes().get(0).getHeureFin());
		assertEquals(new Integer(1), result.getSaisies().get(1).getPrimes().get(0).getIdPointage());

		assertEquals(1, result.getSaisies().get(3).getPrimes().size());
		assertEquals(new Integer(1), result.getSaisies().get(3).getPrimes().get(0).getQuantite());
		assertEquals(new Integer(2222), result.getSaisies().get(3).getPrimes().get(0).getNumRubrique());
		assertEquals(new Integer(22), result.getSaisies().get(3).getPrimes().get(0).getIdRefPrime());
		assertEquals("APPROUVE", result.getSaisies().get(3).getPrimes().get(0).getEtat());
		assertNull(result.getSaisies().get(3).getPrimes().get(0).getHeureDebut());
		assertNull(result.getSaisies().get(3).getPrimes().get(0).getHeureFin());
		assertEquals(new Integer(2), result.getSaisies().get(3).getPrimes().get(0).getIdPointage());
	}

	@Test
	public void saveFichePointage_noExistingPointage_saveNewAbsence() {
		
		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentDto agent = new AgentDto();
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
		
		PointageService service = new PointageService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		
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
		assertEquals(EtatPointageEnum.SAISI, argument.getValue().getLatestEtatPointage().getEtat());
		assertEquals(argument.getValue(), argument.getValue().getLatestEtatPointage().getEtatPointagePk().getPointage());
		assertEquals(new DateTime(2013, 05, 22, 9, 8, 00).toDate(), argument.getValue().getLatestEtatPointage().getEtatPointagePk().getDateEtat());
		assertEquals(1, argument.getValue().getEtats().size());
		assertNull(argument.getValue().getIdPointage());
		assertNull(argument.getValue().getQuantite());
		assertNull(argument.getValue().getRefPrime());
		assertNull(argument.getValue().getHeureSupPayee());
		assertNull(argument.getValue().getPointageParent());
	}
	
	@Test
	public void saveFichePointage_noExistingPointage_saveNewHsup() {
		
		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentDto agent = new AgentDto();
		agent.setIdAgent(9007654);
		
		FichePointageDto dto = new FichePointageDto();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		dto.setSaisies(Arrays.asList(new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto()));
		
		HeureSupDto hs1 = new HeureSupDto();
		hs1.setPayee(true);
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
		
		PointageService service = new PointageService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		
		// When
		service.saveFichePointage(dto);
		
		// Then
		ArgumentCaptor<Pointage> argument = ArgumentCaptor.forClass(Pointage.class);
		Mockito.verify(pRepo).savePointage(argument.capture());
		
		assertEquals(RefTypePointageEnum.H_SUP, argument.getValue().getTypePointageEnum());
		assertTrue(argument.getValue().getHeureSupPayee());
		assertEquals(agent.getIdAgent(), argument.getValue().getIdAgent());
		assertEquals(new DateTime(2013, 05, 16, 15, 0, 0).toDate(), argument.getValue().getDateDebut());
		assertEquals(new DateTime(2013, 05, 16, 16, 0, 0).toDate(), argument.getValue().getDateFin());
		assertEquals("le commentaire 3", argument.getValue().getCommentaire().getText());
		assertEquals("le motif 3", argument.getValue().getMotif().getText());
		assertEquals(lundi, argument.getValue().getDateLundi());
		assertEquals(EtatPointageEnum.SAISI, argument.getValue().getLatestEtatPointage().getEtat());
		assertEquals(argument.getValue(), argument.getValue().getLatestEtatPointage().getEtatPointagePk().getPointage());
		assertEquals(new DateTime(2013, 05, 22, 9, 8, 00).toDate(), argument.getValue().getLatestEtatPointage().getEtatPointagePk().getDateEtat());
		assertEquals(1, argument.getValue().getEtats().size());
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
		AgentDto agent = new AgentDto();
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
		Mockito.when(pRepo.getEntity(RefPrime.class, 22)).thenReturn(refPrime);
		
		PointageService service = new PointageService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		
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
		assertEquals(EtatPointageEnum.SAISI, argument.getValue().getLatestEtatPointage().getEtat());
		assertEquals(argument.getValue(), argument.getValue().getLatestEtatPointage().getEtatPointagePk().getPointage());
		assertEquals(new DateTime(2013, 05, 22, 9, 8, 00).toDate(), argument.getValue().getLatestEtatPointage().getEtatPointagePk().getDateEtat());
		assertEquals(1, argument.getValue().getEtats().size());
		assertNull(argument.getValue().getIdPointage());
		assertNull(argument.getValue().getHeureSupPayee());
		assertNull(argument.getValue().getAbsenceConcertee());
		assertNull(argument.getValue().getPointageParent());
	}
	
	@Test
	public void saveFichePointage_deletedPointageFromDto_delete1PointageKeep1BecauseOldVersion() {
		
		// Given
		Date lundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		AgentDto agent = new AgentDto();
		agent.setIdAgent(9007654);
		
		FichePointageDto dto = new FichePointageDto();
		dto.setDateLundi(lundi);
		dto.setAgent(agent);
		dto.setSaisies(Arrays.asList(new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto(), new JourPointageDto()));
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.isDateAMonday(lundi)).thenReturn(true);

		Pointage p = Mockito.spy(new Pointage());
		Mockito.doNothing().when(p).remove();
		Pointage p2 = Mockito.spy(new Pointage());
		p2.setPointageParent(new Pointage());
		
		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(agent.getIdAgent(), lundi)).thenReturn(Arrays.asList(p, p2));
		
		PointageService service = new PointageService();

		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		
		// When
		service.saveFichePointage(dto);
		
		// Then
		Mockito.verify(p, Mockito.times(1)).remove();
		Mockito.verify(p2, Mockito.never()).remove();
	}
}
