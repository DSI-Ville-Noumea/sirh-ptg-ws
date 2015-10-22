package nc.noumea.mairie.repository;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.domain.Spadmn;
import nc.noumea.mairie.domain.SpadmnId;
import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.SpcarrId;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
	public void getAgentCurrentCarriere() {
		
		SpcarrId id = new SpcarrId();
		id.setDatdeb(20141201);
		id.setNomatr(5138);
		
		Spcarr carr = new Spcarr();
		carr.setCdcate(1);
		carr.setId(id);
		carr.setDateFin(20141231);
		sirhEntityManager.persist(carr);
		
		Spcarr result = repository.getAgentCurrentCarriere(5138, new DateTime(2014,12,15,0,0,0).toDate());
		
		assertNotNull(result);
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void getPAOfAgentBetween2Date_2PA() {
		
		SpadmnId id = new SpadmnId();
		id.setNomatr(5138);
		id.setDatdeb(20150901);
		
		Spadmn pa = new Spadmn();
		pa.setId(id);
		pa.setDatfin(20150915);
		sirhEntityManager.persist(pa);
		
		SpadmnId id2 = new SpadmnId();
		id2.setNomatr(5138);
		id2.setDatdeb(20150916);
		
		Spadmn pa2 = new Spadmn();
		pa2.setId(id2);
		pa2.setDatfin(20150930);
		sirhEntityManager.persist(pa2);
		
		List<Spadmn> result = repository.getListPAOfAgentBetween2Date(5138, new DateTime(2015,9,1,0,0,0).toDate(), new DateTime(2015,9,30,0,0,0).toDate());
		
		assertEquals(2, result.size());
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void getPAOfAgentBetween2Date_badAgent() {
		
		SpadmnId id = new SpadmnId();
		id.setNomatr(5138);
		id.setDatdeb(20150901);
		
		Spadmn pa = new Spadmn();
		pa.setId(id);
		pa.setDatfin(20150915);
		sirhEntityManager.persist(pa);
		
		SpadmnId id2 = new SpadmnId();
		id2.setNomatr(5138);
		id2.setDatdeb(20150916);
		
		Spadmn pa2 = new Spadmn();
		pa2.setId(id2);
		pa2.setDatfin(20150930);
		sirhEntityManager.persist(pa2);
		
		List<Spadmn> result = repository.getListPAOfAgentBetween2Date(5130, new DateTime(2015,9,1,0,0,0).toDate(), new DateTime(2015,9,30,0,0,0).toDate());
		
		assertEquals(0, result.size());
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void getPAOfAgentBetween2Date_DatePAPlusGrandeQueDatesRecherchees() {
		
		SpadmnId id = new SpadmnId();
		id.setNomatr(5138);
		id.setDatdeb(20140901);
		
		Spadmn pa = new Spadmn();
		pa.setId(id);
		pa.setDatfin(20160915);
		sirhEntityManager.persist(pa);
		
		List<Spadmn> result = repository.getListPAOfAgentBetween2Date(5138, new DateTime(2015,9,1,0,0,0).toDate(), new DateTime(2015,9,30,0,0,0).toDate());
		
		assertEquals(1, result.size());
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void getPAOfAgentBetween2Date_DatePAPlusPetiteQueDatesRecherchees() {
		
		SpadmnId id = new SpadmnId();
		id.setNomatr(5138);
		id.setDatdeb(20150910);
		
		Spadmn pa = new Spadmn();
		pa.setId(id);
		pa.setDatfin(20150915);
		sirhEntityManager.persist(pa);
		
		List<Spadmn> result = repository.getListPAOfAgentBetween2Date(5138, new DateTime(2015,9,1,0,0,0).toDate(), new DateTime(2015,9,30,0,0,0).toDate());
		
		assertEquals(1, result.size());
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void getPAOfAgentBetween2Date_DatePAAChevalDatesRecherchees() {
		
		SpadmnId id = new SpadmnId();
		id.setNomatr(5138);
		id.setDatdeb(20150110);
		
		Spadmn pa = new Spadmn();
		pa.setId(id);
		pa.setDatfin(20150915);
		sirhEntityManager.persist(pa);
		
		SpadmnId id2 = new SpadmnId();
		id2.setNomatr(5138);
		id2.setDatdeb(20150916);
		
		Spadmn pa2 = new Spadmn();
		pa2.setId(id2);
		pa2.setDatfin(20151030);
		sirhEntityManager.persist(pa2);
		
		List<Spadmn> result = repository.getListPAOfAgentBetween2Date(5138, new DateTime(2015,9,1,0,0,0).toDate(), new DateTime(2015,9,30,0,0,0).toDate());
		
		assertEquals(2, result.size());
	}
}
