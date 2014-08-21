package nc.noumea.mairie.repository;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.domain.Spcong;
import nc.noumea.mairie.domain.SpcongId;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext-test.xml" })
public class MairieRepositoryTest {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	EntityManager sirhEntityManager;
	
	@Autowired
	MairieRepository repository;
	
	@Test
	@Transactional("sirhTransactionManager")
	public void getListCongeWithoutCongesAnnuelsEtAnnulesBetween_returnNothingBecauseWrongType() throws ParseException {
		
		SimpleDateFormat mairieDateFormat = new SimpleDateFormat("yyyyMMdd");
		
		Integer idAgent = 9005138;
		Integer dateDebut = 20140818;
		Integer dateFin = 20140821;
		Date dateDebutReq = mairieDateFormat.parse("20140818");
		Date dateFinReq = mairieDateFormat.parse("20140820");
		
		// conges annuels
		SpcongId id1 = new SpcongId();
		id1.setDatdeb(dateDebut);
		id1.setNomatr(idAgent);
		id1.setRang(1);
		id1.setType2(1);
		
		Spcong cong1 = new Spcong();
		cong1.setCdvali("V");
		cong1.setCodem1(1);
		cong1.setCodem2(1);
		cong1.setDatfin(dateFin);
		cong1.setId(id1);
		sirhEntityManager.persist(cong1);
		
		// conges annules
		SpcongId id2 = new SpcongId();
		id2.setDatdeb(dateDebut);
		id2.setNomatr(idAgent);
		id2.setRang(1);
		id2.setType2(91);
		
		Spcong cong2 = new Spcong();
		cong2.setCdvali("V");
		cong2.setCodem1(1);
		cong2.setCodem2(1);
		cong2.setDatfin(dateFin);
		cong2.setId(id2);
		sirhEntityManager.persist(cong2);
		
		SpcongId id3 = new SpcongId();
		id3.setDatdeb(dateDebut);
		id3.setNomatr(idAgent);
		id3.setRang(1);
		id3.setType2(92);
		
		Spcong cong3 = new Spcong();
		cong3.setCdvali("V");
		cong3.setCodem1(1);
		cong3.setCodem2(1);
		cong3.setDatfin(dateFin);
		cong3.setId(id3);
		sirhEntityManager.persist(cong3);
		
		SpcongId id4 = new SpcongId();
		id4.setDatdeb(dateDebut);
		id4.setNomatr(idAgent);
		id4.setRang(1);
		id4.setType2(93);
		
		Spcong cong4 = new Spcong();
		cong4.setCdvali("V");
		cong4.setCodem1(1);
		cong4.setCodem2(1);
		cong4.setDatfin(dateFin);
		cong4.setId(id4);
		sirhEntityManager.persist(cong4);
		
		SpcongId id5 = new SpcongId();
		id5.setDatdeb(dateDebut);
		id5.setNomatr(idAgent);
		id5.setRang(1);
		id5.setType2(94);
		
		Spcong cong5 = new Spcong();
		cong5.setCdvali("V");
		cong5.setCodem1(1);
		cong5.setCodem2(1);
		cong5.setDatfin(dateFin);
		cong5.setId(id5);
		sirhEntityManager.persist(cong5);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(idAgent);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateDebutReq)).thenReturn(dateDebut);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateFinReq)).thenReturn(dateFin);
		
		ReflectionTestUtils.setField(repository, "helperService", helperService);
		
		List<Spcong> result = repository.getListCongeWithoutCongesAnnuelsEtAnnulesBetween(idAgent, dateDebutReq, dateFinReq);
		
		assertEquals(0, result.size());
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void getListCongeWithoutCongesAnnuelsEtAnnulesBetween_returnNothingBecauseWrongDate() throws ParseException {
		
		SimpleDateFormat mairieDateFormat = new SimpleDateFormat("yyyyMMdd");
		
		Integer idAgent = 9005138;
		Integer dateDebut = 20140821;
		Integer dateFin = 20140823;
		Integer dateDebutConges = 20140819;
		Integer dateFinConges = 20140820;
		Date dateDebutReq = mairieDateFormat.parse("20140821");
		Date dateFinReq = mairieDateFormat.parse("20140823");
		
		// conges annuels
		SpcongId id1 = new SpcongId();
		id1.setDatdeb(dateDebutConges);
		id1.setNomatr(idAgent);
		id1.setRang(1);
		id1.setType2(2);
		
		Spcong cong1 = new Spcong();
		cong1.setCdvali("V");
		cong1.setCodem1(1);
		cong1.setCodem2(1);
		cong1.setDatfin(dateFinConges);
		cong1.setId(id1);
		sirhEntityManager.persist(cong1);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(idAgent);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateDebutReq)).thenReturn(dateDebut);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateFinReq)).thenReturn(dateFin);
		
		ReflectionTestUtils.setField(repository, "helperService", helperService);
		
		List<Spcong> result = repository.getListCongeWithoutCongesAnnuelsEtAnnulesBetween(idAgent, dateDebutReq, dateFinReq);
		
		assertEquals(0, result.size());
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void getListCongeWithoutCongesAnnuelsEtAnnulesBetween_return1result() throws ParseException {
		
		SimpleDateFormat mairieDateFormat = new SimpleDateFormat("yyyyMMdd");
		
		Integer idAgent = 9005138;
		Integer dateDebut = 20140818;
		Integer dateFin = 20140821;
		Integer dateDebutConges = 20140819;
		Integer dateFinConges = 20140820;
		Date dateDebutReq = mairieDateFormat.parse("20140818");
		Date dateFinReq = mairieDateFormat.parse("20140820");
		
		// conges annuels
		SpcongId id1 = new SpcongId();
		id1.setDatdeb(dateDebutConges);
		id1.setNomatr(idAgent);
		id1.setRang(1);
		id1.setType2(2);
		
		Spcong cong1 = new Spcong();
		cong1.setCdvali("V");
		cong1.setCodem1(1);
		cong1.setCodem2(1);
		cong1.setDatfin(dateFinConges);
		cong1.setId(id1);
		sirhEntityManager.persist(cong1);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(idAgent)).thenReturn(idAgent);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateDebutReq)).thenReturn(dateDebut);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateFinReq)).thenReturn(dateFin);
		
		ReflectionTestUtils.setField(repository, "helperService", helperService);
		
		List<Spcong> result = repository.getListCongeWithoutCongesAnnuelsEtAnnulesBetween(idAgent, dateDebutReq, dateFinReq);
		
		assertEquals(1, result.size());
	}
}
