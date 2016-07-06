package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ptg.domain.ReposCompHisto;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations={"/META-INF/spring/applicationContext-test.xml"})
public class ReposCompRepositoryTest {

	@Autowired
	ReposCompRepository repository;
	
	@PersistenceContext(unitName = "ptgPersistenceUnit")
	EntityManager ptgEntityManager;
	
	@Test
	@Transactional("ptgTransactionManager")
	public void countTotalHSupsSinceStartOfYear_CountOnlyCurrentYearHSups() {

		ReposCompHisto h1 = new ReposCompHisto();
		h1.setIdAgent(9005138);;
		h1.setDateLundi(new DateTime(2013, 01, 07, 0, 0, 0).toDate());
		h1.setMBaseHoraire(2325);
		h1.setMSup(60);
		ptgEntityManager.persist(h1);

		ReposCompHisto h2 = new ReposCompHisto();
		h2.setIdAgent(9005138);;
		h2.setDateLundi(new DateTime(2013, 01, 07, 0, 0, 0).toDate());
		h2.setMBaseHoraire(2325);
		h2.setMSup(180);
		ptgEntityManager.persist(h2);

		ReposCompHisto h3 = new ReposCompHisto();
		h3.setIdAgent(9005138);;
		h3.setDateLundi(new DateTime(2012, 12, 24, 0, 0, 0).toDate());
		h3.setMBaseHoraire(2325);
		h3.setMSup(240);
		ptgEntityManager.persist(h3);
		
		ReposCompHisto h4 = new ReposCompHisto();
		h4.setIdAgent(9005139);;
		h4.setDateLundi(new DateTime(2012, 6, 24, 0, 0, 0).toDate());
		h4.setMBaseHoraire(2325);
		h4.setMSup(240);
		ptgEntityManager.persist(h4);
		
		Integer result = repository.countTotalHSupsSinceStartOfYear(9005138, 2013);
		
		assertEquals(240, (int) result);
		
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void countTotalHSupsSinceStartOfYear_noValues_CountOnlyCurrentYearHSups() {

		Integer result = repository.countTotalHSupsSinceStartOfYear(9005138, 2013);
		
		assertEquals(0, (int) result);
		
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void findReposCompHistoForAgentAndDate_HistoDoesNotExists_ReturnNull() {
		
		// Given
		ReposCompHisto h1 = new ReposCompHisto();
		h1.setIdAgent(9005138);;
		h1.setDateLundi(new LocalDate(2013, 01, 07).toDate());
		h1.setMBaseHoraire(2325);
		h1.setMSup(60);
		ptgEntityManager.persist(h1);
		
		// When
		ReposCompHisto result = repository.findReposCompHistoForAgentAndDate(9005138, new LocalDate(2013, 12, 16).toDate());
		
		// Then
		assertNull(result);
		
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void findReposCompHistoForAgentAndDate_HistotExists_ReturnIt() {
		
		// Given
		ReposCompHisto h1 = new ReposCompHisto();
		h1.setIdAgent(9005138);;
		h1.setDateLundi(new LocalDate(2013, 01, 07).toDate());
		h1.setMBaseHoraire(2325);
		h1.setMSup(60);
		ptgEntityManager.persist(h1);
		
		// When
		ReposCompHisto result = repository.findReposCompHistoForAgentAndDate(9005138, h1.getDateLundi());
		
		// Then
		assertEquals(h1, result);
		
		ptgEntityManager.clear();
	}
}
