package nc.noumea.mairie.ptg.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.SpcarrId;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPointagePK;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
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
		Mockito.when(arRepo.getRefPrimes(Arrays.asList(7058, 7059), carr.getStatutCarriere())).thenReturn(Arrays.asList(rp1, rp2));

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(agent, d)).thenReturn(carr);
		Mockito.when(mairieRepo.getPrimePointagesByAgent(agent.getIdAgent(), d)).thenReturn(listePrimePointage);

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
	public void getFilledFichePointageForAgent_Agentwith3PointagesPrimes_1isRefusedefinitivement_1isrejetedef_Return1Prime() {

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

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgent(idAgent)).thenReturn(ag);

		IPointageRepository pRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(pRepo.getPointagesForAgentAndDateOrderByIdDesc(idAgent, dateLundi)).thenReturn(Arrays.asList(p2, p1, p3));

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p1.getDateDebut())).thenReturn(1);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p2.getDateDebut())).thenReturn(3);
		Mockito.when(helperMock.getWeekDayFromDateBase0(p3.getDateDebut())).thenReturn(4);

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
		
		HelperService hs = Mockito.mock(HelperService.class);
		Mockito.when(hs.getCurrentDate()).thenReturn(new DateTime(2013, 05, 17, 9, 25, 8).toDate());
		
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hs);
		
		// When
		Pointage result = service.getOrCreateNewPointage(idPointage, 9007867, dateLundi, 89);
		
		// Then
		assertEquals(9007867, (int) result.getIdAgent());
		assertEquals(rf89, result.getRefPrime());
		assertEquals(dateLundi, result.getDateLundi());
		assertEquals(1, result.getEtats().size());
		assertEquals(EtatPointageEnum.SAISI, result.getLatestEtatPointage().getEtat());
		assertEquals(new DateTime(2013, 05, 17, 9, 25, 8).toDate(), result.getLatestEtatPointage().getEtatPointagePk().getDateEtat());
	}
	
	@Test
	public void getOrCreateNewPointage_PointageExists_IsSAISI_returnPointage() {
		
		// Given
		Integer idPointage = 67;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		
		EtatPointage etatSaisi = new EtatPointage();
		etatSaisi.setEtatPointagePk(new EtatPointagePK());
		etatSaisi.setEtat(EtatPointageEnum.SAISI);
		
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
		
		HelperService hs = Mockito.mock(HelperService.class);
		Mockito.when(hs.getCurrentDate()).thenReturn(new DateTime(2013, 05, 17, 9, 25, 8).toDate());
		
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hs);
		
		// When
		Pointage result = service.getOrCreateNewPointage(idPointage, 9007867, dateLundi, 89);
		
		// Then
		assertEquals(67, (int) result.getIdPointage());
		assertEquals(9007867, (int) result.getIdAgent());
		assertEquals(rf89, result.getRefPrime());
		assertEquals(dateLundi, result.getDateLundi());
		assertEquals(1, result.getEtats().size());
		assertEquals(EtatPointageEnum.SAISI, result.getLatestEtatPointage().getEtat());
		assertEquals(new DateTime(2013, 05, 17, 9, 25, 8).toDate(), result.getLatestEtatPointage().getEtatPointagePk().getDateEtat());
	}
	
	@Test
	public void getOrCreateNewPointage_PointageExists_IsNotSAISI_returnNewChildPointage() {
		
		// Given
		Integer idPointage = 67;
		Date dateLundi = new DateTime(2013, 05, 13, 0, 0, 0).toDate();
		
		EtatPointage etatSaisi = new EtatPointage();
		etatSaisi.setEtatPointagePk(new EtatPointagePK());
		etatSaisi.setEtat(EtatPointageEnum.REFUSE);
		
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
		Mockito.when(pRepo.getEntity(EtatPointage.class, EtatPointageEnum.SAISI.getCodeEtat())).thenReturn(etatSaisi);
		Mockito.when(pRepo.getEntity(RefPrime.class, 89)).thenReturn(rf89);
		
		HelperService hs = Mockito.mock(HelperService.class);
		Mockito.when(hs.getCurrentDate()).thenReturn(new DateTime(2013, 05, 17, 9, 25, 8).toDate());
		
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", pRepo);
		ReflectionTestUtils.setField(service, "helperService", hs);
		
		// When
		Pointage result = service.getOrCreateNewPointage(idPointage, 9007867, dateLundi, 89);
		
		// Then
		assertNull(result.getIdPointage());
		assertEquals(p67, result.getPointageParent());
		assertEquals(9007867, (int) result.getIdAgent());
		assertEquals(rf89, result.getRefPrime());
		assertEquals(dateLundi, result.getDateLundi());
		assertEquals(1, result.getEtats().size());
		assertEquals(EtatPointageEnum.SAISI, result.getLatestEtatPointage().getEtat());
		assertEquals(new DateTime(2013, 05, 17, 9, 25, 8).toDate(), result.getLatestEtatPointage().getEtatPointagePk().getDateEtat());
	}
}
