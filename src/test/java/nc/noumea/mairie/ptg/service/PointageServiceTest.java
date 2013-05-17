package nc.noumea.mairie.ptg.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import nc.noumea.mairie.ptg.domain.TypePointage;
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
		AgentDto ag = new AgentDto(agent);
		List<JourPointageDto> listeJour = new ArrayList<JourPointageDto>();
		JourPointageDto jpdto = new JourPointageDto();
		List<PrimeDto> pp = new ArrayList<PrimeDto>();
		PrimeDto p1 = new PrimeDto();
		p1.setNumRubrique(7125);
		pp.add(p1);
		PrimeDto p2 = new PrimeDto();
		p2.setNumRubrique(7126);
		pp.add(p2);
		jpdto.setPrimes(pp);
		listeJour.add(jpdto);

		List<PrimePointage> listePrimePointage = new ArrayList<PrimePointage>();
		PrimePointage po = new PrimePointage();
		po.setNumRubrique(7058);
		PrimePointage po2 = new PrimePointage();
		po2.setNumRubrique(7059);

		listePrimePointage.add(po);
		listePrimePointage.add(po2);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date d = sdf.parse("15/05/2013");

		ISirhWSConsumer wsMock = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(wsMock.getAgentDirection(idAgent)).thenReturn(siserv);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getPrimePointagesByAgent(agent.getIdAgent(), d)).thenReturn(listePrimePointage);

		IMairieRepository mairieRepo = Mockito.mock(IMairieRepository.class);
		Mockito.when(mairieRepo.getAgentCurrentCarriere(agent, d)).thenReturn(carr);

		HelperService helperMock = Mockito.mock(HelperService.class);
		Mockito.when(helperMock.getCurrentDate()).thenReturn(d);

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "sirhWSConsumer", wsMock);
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);
		ReflectionTestUtils.setField(service, "mairieRepository", mairieRepo);
		ReflectionTestUtils.setField(service, "helperService", helperMock);

		FichePointageDto dto = service.getFichePointageForAgent(agent, d);
		dto.setAgent(ag);
		dto.setSaisies(listeJour);
		// Then
		assertEquals(9007654, (int) dto.getAgent().getIdAgent());
		assertEquals(7126, (int) dto.getSaisies().get(0).getPrimes().get(1).getNumRubrique());
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
		TypePointage t1 = new TypePointage();
		t1.setIdTypePointage(1);
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
		TypePointage t2 = new TypePointage();
		t2.setIdTypePointage(2);
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
		TypePointage t3 = new TypePointage();
		t3.setIdTypePointage(3);
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
}
