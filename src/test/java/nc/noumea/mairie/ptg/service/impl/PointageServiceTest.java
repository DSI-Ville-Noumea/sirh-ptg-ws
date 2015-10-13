package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ads.dto.EntiteDto;
import nc.noumea.mairie.domain.Spbarem;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.SpcarrId;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.MotifHeureSup;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.ptg.domain.RefTypeAbsenceEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.FichePointageDtoKiosque;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.MotifHeureSupDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.RefTypeAbsenceDto;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.repository.IMairieRepository;
import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.staticmock.MockStaticEntityMethods;
import org.springframework.test.util.ReflectionTestUtils;

@MockStaticEntityMethods
public class PointageServiceTest {

	private static RefTypePointage hSup;
	private static RefTypePointage prime;
	private static RefTypePointage abs;

	@BeforeClass
	public static void Setup() {
		prime = new RefTypePointage();
		prime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		hSup = new RefTypePointage();
		hSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		abs = new RefTypePointage();
		abs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
	}

	@Test
	public void getAgentFichePointage() throws ParseException {

		// Given
		Integer idAgent = 9007654;

		EntiteDto entite = new EntiteDto();
		entite.setIdEntite(66);
		entite.setLabel("LIB SERVICE");
		entite.setSigle("SIGLE");

		Spbarem spbarem = new Spbarem();
		spbarem.setIna(315);
		
		SpcarrId carrId = new SpcarrId();
		carrId.setNomatr(7654);
		carrId.setDatdeb(20120506);
		Spcarr carr = new Spcarr();
		carr.setId(carrId);
		carr.setCdcate(1);
		carr.setSpbarem(spbarem);

		AgentGeneriqueDto agent = new AgentGeneriqueDto();
		agent.setIdAgent(9007654);

		List<Integer> listePrimePointage = new ArrayList<Integer>();

		listePrimePointage.add(7058);
		listePrimePointage.add(7059);

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
		Mockito.when(wsMock.getAgentDirection(idAgent, d)).thenReturn(entite);
		Mockito.when(wsMock.getPrimePointagesByAgent(agent.getIdAgent(), d)).thenReturn(listePrimePointage);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getRefPrimes(Arrays.asList(7058, 7059), carr.getStatutCarriere())).thenReturn(
				Arrays.asList(rp1, rp2));

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(agent, d)).thenReturn(carr);

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getCurrentDate()).thenReturn(d);
		Mockito.when(helperMock.isDateAMonday(d)).thenReturn(true);
		Mockito.when(helperMock.getWeekStringFromDate(d)).thenReturn("week string");

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", wsMock);
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
		assertFalse(dto.isINASuperieur315());
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
	public void getAgentFichePointage_INA316() throws ParseException {

		// Given
		Integer idAgent = 9007654;

		EntiteDto entite = new EntiteDto();
		entite.setIdEntite(66);
		entite.setLabel("LIB SERVICE");
		entite.setSigle("SIGLE");

		Spbarem spbarem = new Spbarem();
		spbarem.setIna(316);
		
		SpcarrId carrId = new SpcarrId();
		carrId.setNomatr(7654);
		carrId.setDatdeb(20120506);
		Spcarr carr = new Spcarr();
		carr.setId(carrId);
		carr.setCdcate(1);
		carr.setSpbarem(spbarem);

		AgentGeneriqueDto agent = new AgentGeneriqueDto();
		agent.setIdAgent(9007654);

		List<Integer> listePrimePointage = new ArrayList<Integer>();

		listePrimePointage.add(7058);
		listePrimePointage.add(7059);

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
		Mockito.when(wsMock.getAgentDirection(idAgent, d)).thenReturn(entite);
		Mockito.when(wsMock.getPrimePointagesByAgent(agent.getIdAgent(), d)).thenReturn(listePrimePointage);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getRefPrimes(Arrays.asList(7058, 7059), carr.getStatutCarriere())).thenReturn(
				Arrays.asList(rp1, rp2));

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(agent, d)).thenReturn(carr);

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getCurrentDate()).thenReturn(d);
		Mockito.when(helperMock.isDateAMonday(d)).thenReturn(true);
		Mockito.when(helperMock.getWeekStringFromDate(d)).thenReturn("week string");

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", wsMock);
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
		assertTrue(dto.isINASuperieur315());
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
		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9006543);
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		FichePointageDto dto = new FichePointageDto();
		dto.setAgent(new AgentWithServiceDto());
		dto.setDateLundi(dateLundi);
		dto.setSemaine("SEMAINE");
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());
		dto.getSaisies().add(new JourPointageDto());

		RefTypeAbsence typeAbsence = new RefTypeAbsence();
		typeAbsence.setIdRefTypeAbsence(RefTypeAbsenceEnum.CONCERTEE.getValue());

		Pointage p1 = new Pointage();
		p1.setIdPointage(1);
		RefTypePointage t1 = new RefTypePointage();
		t1.setIdRefTypePointage(1);
		t1.setLabel("ABSENCE");
		p1.setType(t1);
		p1.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		p1.setDateFin(new DateTime(2013, 05, 14, 12, 0, 0).toDate());
		p1.setDateLundi(dateLundi);
		EtatPointage e1 = new EtatPointage();
		e1.setEtat(EtatPointageEnum.REFUSE);
		p1.getEtats().add(e1);
		p1.setRefTypeAbsence(typeAbsence);

		Pointage p1new = new Pointage();
		p1new.setIdPointage(3);
		p1new.setPointageParent(p1);
		p1new.setType(t1);
		p1new.setDateDebut(new DateTime(2013, 05, 14, 9, 0, 0).toDate());
		p1new.setDateFin(new DateTime(2013, 05, 14, 12, 0, 0).toDate());
		p1new.setDateLundi(dateLundi);
		EtatPointage e1new = new EtatPointage();
		e1new.setEtat(EtatPointageEnum.SAISI);
		p1new.getEtats().add(e1new);
		p1new.setRefTypeAbsence(typeAbsence);

		Pointage p2 = new Pointage();
		p2.setIdPointage(2);
		RefTypePointage t2 = new RefTypePointage();
		t2.setIdRefTypePointage(2);
		t2.setLabel("H_SUP");
		p2.setType(t2);
		p2.setHeureSupRecuperee(true);
		p2.setHeureSupRappelService(true);
		p2.setDateDebut(new DateTime(2013, 05, 16, 14, 0, 0).toDate());
		p2.setDateFin(new DateTime(2013, 05, 16, 16, 0, 0).toDate());
		p2.setDateLundi(dateLundi);
		EtatPointage e2 = new EtatPointage();
		e2.setEtat(EtatPointageEnum.APPROUVE);
		p2.getEtats().add(e2);

		ISirhWSConsumer sRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sRepo.getAgent(idAgent)).thenReturn(ag);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(idAgent, dateLundi)).thenReturn(
				Arrays.asList(p1new, p2, p1));

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p1new.getDateDebut())).thenReturn(1);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p2.getDateDebut())).thenReturn(3);

		PointageService service = Mockito.spy(new PointageService());
		Mockito.doReturn(dto).when(service).getFichePointageForAgent(ag, dateLundi);

		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sRepo);

		// When
		FichePointageDto result = service.getFilledFichePointageForAgent(idAgent, dateLundi);

		// Then
		assertEquals(1, result.getSaisies().get(1).getAbsences().size());
		assertEquals(1, (int) result.getSaisies().get(1).getAbsences().get(0).getIdRefTypeAbsence());
		assertEquals(0, (int) result.getSaisies().get(1).getAbsences().get(0).getIdRefEtat());
		assertEquals(new DateTime(2013, 05, 14, 9, 0, 0).toDate(), result.getSaisies().get(1).getAbsences().get(0)
				.getHeureDebut());
		assertEquals(new DateTime(2013, 05, 14, 12, 0, 0).toDate(), result.getSaisies().get(1).getAbsences().get(0)
				.getHeureFin());
		assertEquals(new Integer(3), result.getSaisies().get(1).getAbsences().get(0).getIdPointage());

		assertEquals(1, result.getSaisies().get(3).getHeuresSup().size());
		assertEquals(true, result.getSaisies().get(3).getHeuresSup().get(0).getRecuperee());
		assertEquals(true, result.getSaisies().get(3).getHeuresSup().get(0).getRappelService());
		assertEquals(1, (int) result.getSaisies().get(3).getHeuresSup().get(0).getIdRefEtat());
		assertEquals(new DateTime(2013, 05, 16, 14, 0, 0).toDate(), result.getSaisies().get(3).getHeuresSup().get(0)
				.getHeureDebut());
		assertEquals(new DateTime(2013, 05, 16, 16, 0, 0).toDate(), result.getSaisies().get(3).getHeuresSup().get(0)
				.getHeureFin());
		assertEquals(new Integer(2), result.getSaisies().get(3).getHeuresSup().get(0).getIdPointage());
	}

	@Test
	public void getFilledFichePointageForAgent_Agentwith2PointagesPrimes_Return2Primes() {

		// Given
		int idAgent = 9006543;
		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9006543);
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		FichePointageDto dto = new FichePointageDto();
		dto.setAgent(new AgentWithServiceDto());
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

		ISirhWSConsumer sRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sRepo.getAgent(idAgent)).thenReturn(ag);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(idAgent, dateLundi)).thenReturn(
				Arrays.asList(p2, p1));

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p1.getDateDebut())).thenReturn(1);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p2.getDateDebut())).thenReturn(3);

		PointageService service = Mockito.spy(new PointageService());
		Mockito.doReturn(dto).when(service).getFichePointageForAgent(ag, dateLundi);

		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sRepo);

		// When
		FichePointageDto result = service.getFilledFichePointageForAgent(idAgent, dateLundi);

		// Then
		assertEquals(1, result.getSaisies().get(1).getPrimes().size());
		assertNull(result.getSaisies().get(1).getPrimes().get(0).getQuantite());
		assertEquals(new Integer(1111), result.getSaisies().get(1).getPrimes().get(0).getNumRubrique());
		assertEquals(new Integer(11), result.getSaisies().get(1).getPrimes().get(0).getIdRefPrime());
		assertEquals(2, (int) result.getSaisies().get(1).getPrimes().get(0).getIdRefEtat());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), result.getSaisies().get(1).getPrimes().get(0)
				.getHeureDebut());
		assertEquals(new DateTime(2013, 05, 14, 12, 0, 0).toDate(), result.getSaisies().get(1).getPrimes().get(0)
				.getHeureFin());
		assertEquals(new Integer(1), result.getSaisies().get(1).getPrimes().get(0).getIdPointage());

		assertEquals(1, result.getSaisies().get(3).getPrimes().size());
		assertEquals(new Integer(1), result.getSaisies().get(3).getPrimes().get(0).getQuantite());
		assertEquals(new Integer(2222), result.getSaisies().get(3).getPrimes().get(0).getNumRubrique());
		assertEquals(new Integer(22), result.getSaisies().get(3).getPrimes().get(0).getIdRefPrime());
		assertEquals(1, (int) result.getSaisies().get(3).getPrimes().get(0).getIdRefEtat());
		assertNull(result.getSaisies().get(3).getPrimes().get(0).getHeureDebut());
		assertNull(result.getSaisies().get(3).getPrimes().get(0).getHeureFin());
		assertEquals(new Integer(2), result.getSaisies().get(3).getPrimes().get(0).getIdPointage());
	}

	@Test
	public void getFilledFichePointageForAgent_Agentwith3PointagesPrimes_1isRefusedefinitivement_1isrejetedef_Return1Prime() {

		// Given
		int idAgent = 9006543;
		AgentGeneriqueDto ag = new AgentGeneriqueDto();
		ag.setIdAgent(9006543);
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		FichePointageDto dto = new FichePointageDto();
		dto.setAgent(new AgentWithServiceDto());
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

		PrimeDto pr3 = new PrimeDto();
		pr3.setNumRubrique(2222);
		pr3.setIdRefPrime(22);
		dto.getSaisies().get(4).getPrimes().add(pr3);

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
		e2.setEtat(EtatPointageEnum.REJETE_DEFINITIVEMENT);
		p2.getEtats().add(e2);

		Pointage p3 = new Pointage();
		p3.setIdPointage(3);
		p3.setRefPrime(new RefPrime());
		p3.getRefPrime().setNoRubr(2222);
		p3.getRefPrime().setIdRefPrime(22);
		p3.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		p3.setType(t3);
		p3.setQuantite(1);
		p3.setDateDebut(new DateTime(2013, 05, 17, 0, 0, 0).toDate());
		p3.setDateLundi(dateLundi);
		EtatPointage e3 = new EtatPointage();
		e3.setEtat(EtatPointageEnum.REFUSE_DEFINITIVEMENT);
		p3.getEtats().add(e3);

		ISirhWSConsumer sRepo = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(sRepo.getAgent(idAgent)).thenReturn(ag);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(idAgent, dateLundi)).thenReturn(
				Arrays.asList(p2, p1, p3));

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p1.getDateDebut())).thenReturn(1);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p2.getDateDebut())).thenReturn(3);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p3.getDateDebut())).thenReturn(4);

		PointageService service = Mockito.spy(new PointageService());
		Mockito.doReturn(dto).when(service).getFichePointageForAgent(ag, dateLundi);

		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);
		ReflectionTestUtils.setField(service, "sirhWsConsumer", sRepo);

		// When
		FichePointageDto result = service.getFilledFichePointageForAgent(idAgent, dateLundi);

		// Then
		assertEquals(1, result.getSaisies().get(1).getPrimes().size());
		assertNull(result.getSaisies().get(1).getPrimes().get(0).getQuantite());
		assertEquals(new Integer(1111), result.getSaisies().get(1).getPrimes().get(0).getNumRubrique());
		assertEquals(new Integer(11), result.getSaisies().get(1).getPrimes().get(0).getIdRefPrime());
		assertEquals(2, (int) result.getSaisies().get(1).getPrimes().get(0).getIdRefEtat());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), result.getSaisies().get(1).getPrimes().get(0)
				.getHeureDebut());
		assertEquals(new DateTime(2013, 05, 14, 12, 0, 0).toDate(), result.getSaisies().get(1).getPrimes().get(0)
				.getHeureFin());
		assertEquals(new Integer(1), result.getSaisies().get(1).getPrimes().get(0).getIdPointage());

		assertEquals(1, result.getSaisies().get(3).getPrimes().size());
		assertNull(result.getSaisies().get(3).getPrimes().get(0).getIdPointage());
		assertNull(result.getSaisies().get(3).getPrimes().get(0).getQuantite());
		assertNull(result.getSaisies().get(3).getPrimes().get(0).getHeureDebut());
		assertNull(result.getSaisies().get(3).getPrimes().get(0).getHeureFin());

		assertEquals(1, result.getSaisies().get(4).getPrimes().size());
		assertNull(result.getSaisies().get(4).getPrimes().get(0).getIdPointage());
		assertNull(result.getSaisies().get(4).getPrimes().get(0).getQuantite());
		assertNull(result.getSaisies().get(4).getPrimes().get(0).getHeureDebut());
		assertNull(result.getSaisies().get(4).getPrimes().get(0).getHeureFin());
	}

	@Test
	public void getOrCreateNewPointage_PointageDoesNotExist_CreateNewOne() {

		// Given
		Integer idPointage = null;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		EtatPointage etatSaisi = new EtatPointage();
		etatSaisi.setEtat(EtatPointageEnum.SAISI);

		RefPrime rf89 = new RefPrime();
		rf89.setIdRefPrime(89);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(EtatPointage.class, EtatPointageEnum.SAISI.getCodeEtat())).thenReturn(etatSaisi);
		Mockito.when(pRepo.getEntity(RefPrime.class, 89)).thenReturn(rf89);

		Date dateEtat = new DateTime(2013, 05, 17, 9, 25, 8).toDate();

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 5, 17, 9, 12, 0).toDate());

		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		Pointage result = service.getOrCreateNewPointage(9001234, idPointage, 9007867, dateLundi, dateEtat, 89);

		// Then
		assertEquals(9007867, (int) result.getIdAgent());
		assertEquals(rf89, result.getRefPrime());
		assertEquals(dateLundi, result.getDateLundi());
		assertEquals(1, result.getEtats().size());
		assertEquals(EtatPointageEnum.SAISI, result.getLatestEtatPointage().getEtat());
		assertEquals(new DateTime(2013, 05, 17, 9, 25, 8).toDate(), result.getLatestEtatPointage().getDateEtat());
		assertEquals(new DateTime(2013, 05, 17, 9, 12, 0).toDate(), result.getLatestEtatPointage().getDateMaj());
		assertEquals(9001234, (int) result.getLatestEtatPointage().getIdAgent());
	}

	@Test
	public void getOrCreateNewPointage_PointageExists_IsSAISI_returnPointage() {

		// Given
		Integer idPointage = 67;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		EtatPointage etatSaisi = new EtatPointage();
		etatSaisi.setEtat(EtatPointageEnum.SAISI);
		etatSaisi.setDateEtat(new LocalDate(2013, 5, 1).toDate());

		RefPrime rf89 = new RefPrime();
		rf89.setIdRefPrime(89);

		Pointage p67 = new Pointage();
		p67.setIdPointage(67);
		p67.setIdAgent(9007867);
		p67.setRefPrime(rf89);
		p67.setDateLundi(dateLundi);
		p67.getEtats().add(etatSaisi);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, idPointage)).thenReturn(p67);

		Date dateEtat = new DateTime(2013, 05, 17, 9, 25, 8).toDate();

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 5, 17, 9, 12, 0).toDate());

		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		Pointage result = service.getOrCreateNewPointage(9001234, idPointage, 9007867, dateLundi, dateEtat, 89);

		// Then
		assertEquals(67, (int) result.getIdPointage());
		assertEquals(9007867, (int) result.getIdAgent());
		assertEquals(rf89, result.getRefPrime());
		assertEquals(dateLundi, result.getDateLundi());
		assertEquals(1, result.getEtats().size());
		assertEquals(EtatPointageEnum.SAISI, result.getLatestEtatPointage().getEtat());
		assertEquals(new LocalDate(2013, 5, 1).toDate(), result.getLatestEtatPointage().getDateEtat());
		assertEquals(new DateTime(2013, 05, 17, 9, 12, 0).toDate(), result.getLatestEtatPointage().getDateMaj());
		assertEquals(9001234, (int) result.getLatestEtatPointage().getIdAgent());
	}

	@Test
	public void getOrCreateNewPointage_PointageExists_IsNotSAISI_returnNewChildPointage() {

		// Given
		Integer idPointage = 67;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();

		EtatPointage etatSaisi = new EtatPointage();
		etatSaisi.setEtat(EtatPointageEnum.REFUSE);

		RefPrime rf89 = new RefPrime();
		rf89.setIdRefPrime(89);

		RefTypeAbsence refTypeAbsence = new RefTypeAbsence();
		refTypeAbsence.setIdRefTypeAbsence(1);

		Pointage p67 = new Pointage();
		p67.setIdPointage(67);
		p67.setIdAgent(9007867);
		p67.setRefPrime(rf89);
		p67.setDateLundi(dateLundi);
		p67.getEtats().add(etatSaisi);
		p67.setDateDebut(new DateTime(2013, 5, 14, 8, 0, 0).toDate());
		p67.setDateFin(new DateTime(2013, 5, 14, 10, 30, 0).toDate());
		p67.setQuantite(2);
		p67.setRefTypeAbsence(refTypeAbsence);
		p67.setHeureSupRecuperee(true);
		p67.setType(hSup);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getEntity(Pointage.class, idPointage)).thenReturn(p67);
		Mockito.when(pRepo.getEntity(EtatPointage.class, EtatPointageEnum.SAISI.getCodeEtat())).thenReturn(etatSaisi);
		Mockito.when(pRepo.getEntity(RefPrime.class, 89)).thenReturn(rf89);

		Date dateEtat = new DateTime(2013, 05, 17, 9, 25, 8).toDate();

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new DateTime(2013, 5, 17, 9, 12, 0).toDate());

		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hS);

		// When
		Pointage result = service.getOrCreateNewPointage(9001234, idPointage, 9007867, dateLundi, dateEtat, 89);

		// Then
		assertNull(result.getIdPointage());
		assertEquals(p67, result.getPointageParent());
		assertEquals(9007867, (int) result.getIdAgent());
		assertEquals(rf89, result.getRefPrime());
		assertEquals(dateLundi, result.getDateLundi());
		assertEquals(1, result.getEtats().size());
		assertEquals(EtatPointageEnum.SAISI, result.getLatestEtatPointage().getEtat());
		assertEquals(new DateTime(2013, 05, 17, 9, 25, 8).toDate(), result.getLatestEtatPointage().getDateEtat());
		assertEquals(new DateTime(2013, 05, 17, 9, 12, 0).toDate(), result.getLatestEtatPointage().getDateMaj());
		assertEquals(9001234, (int) result.getLatestEtatPointage().getIdAgent());
		assertEquals(p67.getDateDebut(), result.getDateDebut());
		assertEquals(p67.getDateFin(), result.getDateFin());
		assertEquals(p67.getQuantite(), result.getQuantite());
		assertEquals(p67.getRefTypeAbsence(), result.getRefTypeAbsence());
		assertEquals(p67.getHeureSupRecuperee(), result.getHeureSupRecuperee());
		assertEquals(p67.getType(), result.getType());

	}

	@Test
	public void getLatestPointagesForAgentsAndDates_1Agent_2Dates_SkipOldPointages() {

		// Given
		List<Integer> agentIds = Arrays.asList(9008765);
		Date from = new LocalDate(2013, 7, 8).toDate();
		Date to = new LocalDate(2013, 7, 19).toDate();

		Pointage p1 = new Pointage();
		p1.setIdPointage(1);

		Pointage p2 = new Pointage();
		p2.setIdPointage(2);
		p2.setPointageParent(p1);

		Pointage p3 = new Pointage();
		p3.setIdPointage(3);

		List<Pointage> ptgs = Arrays.asList(p3, p2, p1);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getListPointages(agentIds, from, to, null)).thenReturn(ptgs);

		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		// When
		List<Pointage> result = service.getLatestPointagesForAgentsAndDates(agentIds, from, to, null, null, null);

		// Then
		assertEquals(2, result.size());
		assertEquals(p3, result.get(0));
		assertEquals(p2, result.get(1));
	}

	@Test
	public void getLatestPointagesForAgentsAndDates_3Agentz_RefTypeFilter_SkipOldPointages() {

		// Given
		List<Integer> agentIds = Arrays.asList(9008765);
		Date from = new LocalDate(2013, 7, 8).toDate();
		Date to = new LocalDate(2013, 7, 19).toDate();
		RefTypePointageEnum type = RefTypePointageEnum.H_SUP;

		Pointage p3 = new Pointage();
		p3.setType(hSup);
		p3.setIdPointage(3);

		Pointage p4 = new Pointage();
		p4.setType(hSup);
		p4.setIdPointage(4);
		p4.setPointageParent(p3);

		Pointage p5 = new Pointage();
		p5.setType(hSup);
		p5.setIdPointage(5);
		p5.setPointageParent(p4);

		List<Pointage> ptgs = Arrays.asList(p5, p4, p3);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getListPointages(agentIds, from, to, type.getValue())).thenReturn(ptgs);

		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		// When
		List<Pointage> result = service.getLatestPointagesForAgentsAndDates(agentIds, from, to, type, null, null);

		// Then
		assertEquals(1, result.size());
		assertEquals(p5, result.get(0));
	}

	@Test
	public void getLatestPointagesForAgentsAndDates_3Agentz_EtatFilter_SkipOldPointages() {

		// Given
		List<Integer> agentIds = Arrays.asList(9008765);
		Date from = new LocalDate(2013, 7, 8).toDate();
		Date to = new LocalDate(2013, 7, 19).toDate();
		EtatPointageEnum etat = EtatPointageEnum.APPROUVE;

		Pointage p1 = new Pointage();
		p1.setIdPointage(1);
		EtatPointage ep1 = new EtatPointage();
		ep1.setEtat(EtatPointageEnum.REFUSE);
		p1.getEtats().add(ep1);

		Pointage p2 = new Pointage();
		p2.setIdPointage(2);
		p2.setPointageParent(p1);
		EtatPointage ep2 = new EtatPointage();
		ep2.setEtat(EtatPointageEnum.APPROUVE);
		p2.getEtats().add(ep2);

		Pointage p3 = new Pointage();
		p3.setType(hSup);
		p3.setIdPointage(3);
		EtatPointage ep3 = new EtatPointage();
		ep3.setEtat(EtatPointageEnum.REFUSE);
		p3.getEtats().add(ep3);

		Pointage p4 = new Pointage();
		p4.setType(hSup);
		p4.setIdPointage(4);
		p4.setPointageParent(p3);
		EtatPointage ep4 = new EtatPointage();
		ep4.setEtat(EtatPointageEnum.APPROUVE);
		p4.getEtats().add(ep4);

		Pointage p5 = new Pointage();
		p5.setType(hSup);
		p5.setIdPointage(5);
		p5.setPointageParent(p4);
		EtatPointage ep5 = new EtatPointage();
		ep5.setEtat(EtatPointageEnum.SAISI);
		p5.getEtats().add(ep5);

		List<Pointage> ptgs = Arrays.asList(p5, p4, p2, p3, p1);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getListPointages(agentIds, from, to, null)).thenReturn(ptgs);

		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		// When
		List<Pointage> result = service.getLatestPointagesForAgentsAndDates(agentIds, from, to, null,
				Arrays.asList(etat), null);

		// Then
		assertEquals(1, result.size());
		assertEquals(p2, result.get(0));
	}

	@Test
	public void isPrimeUtiliseePointage() {

		// Given
		Integer idAgent = 9008765;
		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(12);
		;

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.isPrimeSurPointageouPointageCalcule(idAgent, refPrime.getIdRefPrime())).thenReturn(false);

		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		List<Integer> list = new ArrayList<>();
		list.add(refPrime.getIdRefPrime());
		// When
		boolean result = service.isPrimeUtiliseePointage(idAgent, list);

		// Then
		assertEquals(false, result);
	}

	@Test
	public void getRefTypeAbsence() {

		List<RefTypeAbsence> list = new ArrayList<RefTypeAbsence>();

		RefTypeAbsence rta = new RefTypeAbsence();
		rta.setIdRefTypeAbsence(RefTypeAbsenceEnum.CONCERTEE.getValue());
		RefTypeAbsence rta2 = new RefTypeAbsence();
		rta2.setIdRefTypeAbsence(RefTypeAbsenceEnum.NON_CONCERTEE.getValue());
		RefTypeAbsence rta3 = new RefTypeAbsence();
		rta3.setIdRefTypeAbsence(RefTypeAbsenceEnum.IMMEDIATE.getValue());

		list.addAll(Arrays.asList(rta, rta2, rta3));

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.findAllRefTypeAbsence()).thenReturn(list);

		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);

		List<RefTypeAbsenceDto> result = service.getRefTypeAbsence();

		assertEquals(3, result.size());
	}

	@Test
	public void getMotifHeureSup_Empty() {

		// Given
		List<MotifHeureSup> listeMotif = new ArrayList<MotifHeureSup>();

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.findAllMotifHeureSup()).thenReturn(listeMotif);

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);

		List<MotifHeureSupDto> dto = service.getMotifHeureSup();

		// Then
		assertEquals(0, dto.size());
	}

	@Test
	public void getMotifHeureSup() {

		// Given
		MotifHeureSup m = new MotifHeureSup();
		m.setIdMotifHsup(1);
		m.setText("teset");

		List<MotifHeureSup> listeMotif = new ArrayList<MotifHeureSup>();
		listeMotif.add(m);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.findAllMotifHeureSup()).thenReturn(listeMotif);

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);

		List<MotifHeureSupDto> dto = service.getMotifHeureSup();

		// Then
		assertEquals(1, dto.size());
		assertEquals(m.getIdMotifHsup(), dto.get(0).getIdMotifHsup());
		assertEquals(m.getText(), dto.get(0).getLibelle());
	}

	@Test
	public void setMotifHeureSup_MotifInexistant() {

		// Given
		MotifHeureSupDto motif = new MotifHeureSupDto();
		motif.setIdMotifHsup(1);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getEntity(MotifHeureSup.class, 1)).thenReturn(null);

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);

		ReturnMessageDto dto = service.setMotifHeureSup(motif);

		// Then
		assertEquals(1, dto.getErrors().size());
		assertEquals("Le motif à modifier n'existe pas.", dto.getErrors().get(0));
	}

	@Test
	public void setMotifHeureSup_LibelleVide() {

		// Given
		MotifHeureSupDto motif = new MotifHeureSupDto();
		motif.setIdMotifHsup(1);

		MotifHeureSup hsup = new MotifHeureSup();
		hsup.setIdMotifHsup(1);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getEntity(MotifHeureSup.class, 1)).thenReturn(hsup);

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);

		ReturnMessageDto dto = service.setMotifHeureSup(motif);

		// Then
		assertEquals(1, dto.getErrors().size());
		assertEquals("Le libellé du motif n'est pas saisi.", dto.getErrors().get(0));
	}

	@Test
	public void setMotifHeureSup_OK_create() {

		// Given
		MotifHeureSupDto motif = new MotifHeureSupDto();
		motif.setIdMotifHsup(1);
		motif.setLibelle("libelle");

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getEntity(MotifHeureSup.class, 1)).thenReturn(new MotifHeureSup());

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);

		ReturnMessageDto dto = service.setMotifHeureSup(motif);

		// Then
		assertEquals(0, dto.getErrors().size());
		Mockito.verify(arRepo, Mockito.times(1)).persisEntity(Mockito.isA(MotifHeureSup.class));
	}

	@Test
	public void checkPointage_OK() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2014, 1, 1, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2014, 1, 1, 0, 0, 0).toDate();

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(
				arRepo.getListPointagesVerification(idAgent, dateDebut, dateFin, RefTypePointageEnum.ABSENCE.getValue()))
				.thenReturn(new ArrayList<Pointage>());
		Mockito.when(
				arRepo.getListPointagesVerification(idAgent, dateDebut, dateFin, RefTypePointageEnum.H_SUP.getValue()))
				.thenReturn(new ArrayList<Pointage>());

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);

		ReturnMessageDto dto = service.checkPointage(idAgent, dateDebut, dateFin);

		// Then
		assertEquals(0, dto.getErrors().size());
	}

	@Test
	public void checkPointage_Errors() {

		// Given
		Integer idAgent = 9005138;
		Date dateDebut = new DateTime(2014, 1, 1, 0, 0, 0).toDate();
		Date dateFin = new DateTime(2014, 1, 1, 0, 0, 0).toDate();
		
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.APPROUVE);
		
		Pointage p = new Pointage();
		p.getEtats().add(etat);
		List<Pointage> listePointage = new ArrayList<Pointage>();
		listePointage.add(p);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(
				arRepo.getListPointagesVerification(idAgent, dateDebut, dateFin, RefTypePointageEnum.ABSENCE.getValue()))
				.thenReturn(listePointage);
		Mockito.when(
				arRepo.getListPointagesVerification(idAgent, dateDebut, dateFin, RefTypePointageEnum.H_SUP.getValue()))
				.thenReturn(new ArrayList<Pointage>());

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);

		ReturnMessageDto dto = service.checkPointage(idAgent, dateDebut, dateFin);

		// Then
		assertEquals(1, dto.getErrors().size());
		assertEquals("01/01/2014 00:00 : L'agent a déjà un pointage sur cette période.", dto.getErrors().get(0));
	}

	@Test
	public void getAgentFichePointageKiosque() throws ParseException {

		// Given
		Integer idAgent = 9007654;

		EntiteDto entite = new EntiteDto();
		entite.setIdEntite(66);
		entite.setLabel("LIB SERVICE");
		entite.setSigle("SIGLE");

		Spbarem spbarem = new Spbarem();
		spbarem.setIna(315);
		spbarem.setIban("12");
		
		SpcarrId carrId = new SpcarrId();
		carrId.setNomatr(7654);
		carrId.setDatdeb(20120506);
		Spcarr carr = new Spcarr();
		carr.setId(carrId);
		carr.setCdcate(1);
		carr.setSpbarem(spbarem);

		AgentGeneriqueDto agent = new AgentGeneriqueDto();
		agent.setIdAgent(9007654);

		List<Integer> listePrimePointage = new ArrayList<Integer>();

		listePrimePointage.add(7058);
		listePrimePointage.add(7059);

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
		Mockito.when(wsMock.getAgentDirection(idAgent, d)).thenReturn(entite);
		Mockito.when(wsMock.getPrimePointagesByAgent(agent.getIdAgent(), d)).thenReturn(listePrimePointage);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getRefPrimes(Arrays.asList(7058, 7059), carr.getStatutCarriere())).thenReturn(
				Arrays.asList(rp1, rp2));

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(agent, d)).thenReturn(carr);

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getCurrentDate()).thenReturn(d);
		Mockito.when(helperMock.isDateAMonday(d)).thenReturn(true);
		Mockito.when(helperMock.getWeekStringFromDate(d)).thenReturn("week string");

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", wsMock);
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);

		FichePointageDtoKiosque dto = service.getFichePointageForAgentKiosque(agent, d);

		// Then
		assertEquals(d, dto.getDateLundi());
		assertEquals("week string", dto.getSemaine());
		assertEquals(9007654, (int) dto.getAgent().getIdAgent());
		assertEquals("F", dto.getAgent().getStatut());
		assertEquals(7, dto.getSaisies().size());
		assertFalse(dto.isINASuperieur315());
		assertFalse(dto.isDPM());
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
	public void getAgentFichePointageKiosque_pasDeDirection() throws ParseException {

		// Given
		Integer idAgent = 9007654;

		Spbarem spbarem = new Spbarem();
		spbarem.setIna(315);
		spbarem.setIban("12");
		
		SpcarrId carrId = new SpcarrId();
		carrId.setNomatr(7654);
		carrId.setDatdeb(20120506);
		Spcarr carr = new Spcarr();
		carr.setId(carrId);
		carr.setCdcate(1);
		carr.setSpbarem(spbarem);

		AgentGeneriqueDto agent = new AgentGeneriqueDto();
		agent.setIdAgent(9007654);

		List<Integer> listePrimePointage = new ArrayList<Integer>();

		listePrimePointage.add(7058);
		listePrimePointage.add(7059);

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
		Mockito.when(wsMock.getAgentDirection(idAgent, d)).thenReturn(null);
		Mockito.when(wsMock.getPrimePointagesByAgent(agent.getIdAgent(), d)).thenReturn(listePrimePointage);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getRefPrimes(Arrays.asList(7058, 7059), carr.getStatutCarriere())).thenReturn(
				Arrays.asList(rp1, rp2));

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(agent, d)).thenReturn(carr);

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getCurrentDate()).thenReturn(d);
		Mockito.when(helperMock.isDateAMonday(d)).thenReturn(true);
		Mockito.when(helperMock.getWeekStringFromDate(d)).thenReturn("week string");

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", wsMock);
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);

		FichePointageDtoKiosque dto = service.getFichePointageForAgentKiosque(agent, d);

		// Then
		assertEquals(d, dto.getDateLundi());
		assertEquals("week string", dto.getSemaine());
		assertEquals(9007654, (int) dto.getAgent().getIdAgent());
		assertEquals("F", dto.getAgent().getStatut());
		assertEquals(7, dto.getSaisies().size());
		assertFalse(dto.isINASuperieur315());
		assertFalse(dto.isDPM());
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
	public void getAgentFichePointageKiosque_INA316() throws ParseException {

		// Given
		Integer idAgent = 9007654;

		EntiteDto entite = new EntiteDto();
		entite.setIdEntite(66);
		entite.setLabel("LIB SERVICE");
		entite.setSigle("SIGLE");

		Spbarem spbarem = new Spbarem();
		spbarem.setIna(316);
		spbarem.setIban("12");
		
		SpcarrId carrId = new SpcarrId();
		carrId.setNomatr(7654);
		carrId.setDatdeb(20120506);
		Spcarr carr = new Spcarr();
		carr.setId(carrId);
		carr.setCdcate(1);
		carr.setSpbarem(spbarem);

		AgentGeneriqueDto agent = new AgentGeneriqueDto();
		agent.setIdAgent(9007654);

		List<Integer> listePrimePointage = new ArrayList<Integer>();

		listePrimePointage.add(7058);
		listePrimePointage.add(7059);

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
		Mockito.when(wsMock.getAgentDirection(idAgent, d)).thenReturn(entite);
		Mockito.when(wsMock.getPrimePointagesByAgent(agent.getIdAgent(), d)).thenReturn(listePrimePointage);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getRefPrimes(Arrays.asList(7058, 7059), carr.getStatutCarriere())).thenReturn(
				Arrays.asList(rp1, rp2));

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(agent, d)).thenReturn(carr);

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getCurrentDate()).thenReturn(d);
		Mockito.when(helperMock.isDateAMonday(d)).thenReturn(true);
		Mockito.when(helperMock.getWeekStringFromDate(d)).thenReturn("week string");

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", wsMock);
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);

		FichePointageDtoKiosque dto = service.getFichePointageForAgentKiosque(agent, d);

		// Then
		assertEquals(d, dto.getDateLundi());
		assertEquals("week string", dto.getSemaine());
		assertEquals(9007654, (int) dto.getAgent().getIdAgent());
		assertEquals("F", dto.getAgent().getStatut());
		assertEquals(7, dto.getSaisies().size());
		assertTrue(dto.isINASuperieur315());
		assertFalse(dto.isDPM());
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
	public void getAgentFichePointageKiosque_DPM() throws ParseException {

		// Given
		Integer idAgent = 9007654;

		EntiteDto entite = new EntiteDto();
		entite.setIdEntite(66);
		entite.setLabel("LIB SERVICE");
		entite.setSigle("DPM");

		Spbarem spbarem = new Spbarem();
		spbarem.setIna(316);
		spbarem.setIban("12");
		
		SpcarrId carrId = new SpcarrId();
		carrId.setNomatr(7654);
		carrId.setDatdeb(20120506);
		Spcarr carr = new Spcarr();
		carr.setId(carrId);
		carr.setCdcate(1);
		carr.setSpbarem(spbarem);

		AgentGeneriqueDto agent = new AgentGeneriqueDto();
		agent.setIdAgent(9007654);

		List<Integer> listePrimePointage = new ArrayList<Integer>();

		listePrimePointage.add(7058);
		listePrimePointage.add(7059);

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
		Mockito.when(wsMock.getAgentDirection(idAgent, d)).thenReturn(entite);
		Mockito.when(wsMock.getPrimePointagesByAgent(agent.getIdAgent(), d)).thenReturn(listePrimePointage);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getRefPrimes(Arrays.asList(7058, 7059), carr.getStatutCarriere())).thenReturn(
				Arrays.asList(rp1, rp2));

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(agent, d)).thenReturn(carr);

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getCurrentDate()).thenReturn(d);
		Mockito.when(helperMock.isDateAMonday(d)).thenReturn(true);
		Mockito.when(helperMock.getWeekStringFromDate(d)).thenReturn("week string");

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", wsMock);
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);

		FichePointageDtoKiosque dto = service.getFichePointageForAgentKiosque(agent, d);

		// Then
		assertEquals(d, dto.getDateLundi());
		assertEquals("week string", dto.getSemaine());
		assertEquals(9007654, (int) dto.getAgent().getIdAgent());
		assertEquals("F", dto.getAgent().getStatut());
		assertEquals(7, dto.getSaisies().size());
		assertTrue(dto.isINASuperieur315());
		assertTrue(dto.isDPM());
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
	public void getAgentFichePointageKiosque_SpBaremAlpha() throws ParseException {

		// Given
		Integer idAgent = 9007654;

		EntiteDto entite = new EntiteDto();
		entite.setIdEntite(66);
		entite.setLabel("LIB SERVICE");
		entite.setSigle("SIGLE");

		Spbarem spbarem = new Spbarem();
		spbarem.setIna(0);
		spbarem.setIban("toto");
		
		SpcarrId carrId = new SpcarrId();
		carrId.setNomatr(7654);
		carrId.setDatdeb(20120506);
		Spcarr carr = new Spcarr();
		carr.setId(carrId);
		carr.setCdcate(1);
		carr.setSpbarem(spbarem);

		AgentGeneriqueDto agent = new AgentGeneriqueDto();
		agent.setIdAgent(9007654);

		List<Integer> listePrimePointage = new ArrayList<Integer>();

		listePrimePointage.add(7058);
		listePrimePointage.add(7059);

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
		Mockito.when(wsMock.getAgentDirection(idAgent, d)).thenReturn(entite);
		Mockito.when(wsMock.getPrimePointagesByAgent(agent.getIdAgent(), d)).thenReturn(listePrimePointage);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getRefPrimes(Arrays.asList(7058, 7059), carr.getStatutCarriere())).thenReturn(
				Arrays.asList(rp1, rp2));

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(agent, d)).thenReturn(carr);

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getCurrentDate()).thenReturn(d);
		Mockito.when(helperMock.isDateAMonday(d)).thenReturn(true);
		Mockito.when(helperMock.getWeekStringFromDate(d)).thenReturn("week string");

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "sirhWsConsumer", wsMock);
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);

		FichePointageDtoKiosque dto = service.getFichePointageForAgentKiosque(agent, d);

		// Then
		assertEquals(d, dto.getDateLundi());
		assertEquals("week string", dto.getSemaine());
		assertEquals(9007654, (int) dto.getAgent().getIdAgent());
		assertEquals("F", dto.getAgent().getStatut());
		assertEquals(7, dto.getSaisies().size());
		assertTrue(dto.isINASuperieur315());
		assertFalse(dto.isDPM());
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
	
}
