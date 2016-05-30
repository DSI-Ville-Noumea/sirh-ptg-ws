package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ptg.domain.DpmIndemAnnee;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/META-INF/spring/applicationContext-test.xml"})
public class DpmRepositoryTest {

	@Autowired
	private DpmRepository dpmRepository;
	
	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;
	
	@Before
	public void before() {
		
		DpmIndemAnnee  dpmAnnee = new DpmIndemAnnee();
		dpmAnnee.setAnnee(new DateTime().getYear());
		dpmAnnee.setDateDebut(new DateTime(2016,4,1,0,0,0).toDate());
		dpmAnnee.setDateFin(new DateTime(2016,4,30,0,0,0).toDate());
		ptgEntityManager.persist(dpmAnnee);
		
		DpmIndemChoixAgent choixAgent = new DpmIndemChoixAgent();
		choixAgent.setIdAgent(9005138);
		choixAgent.setIdAgentCreation(9009999);
		choixAgent.setDpmIndemAnnee(dpmAnnee);
		choixAgent.setDateMaj(new Date());
		choixAgent.setChoixRecuperation(false);
		choixAgent.setChoixIndemnite(true);
		ptgEntityManager.persist(choixAgent);
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListDpmIndemChoixAgent() {
		
		// teste l annee
		assertEquals(0, dpmRepository.getListDpmIndemChoixAgent(Arrays.asList(9005138), new DateTime().getYear()-1).size());

		// teste l agent
		assertEquals(0, dpmRepository.getListDpmIndemChoixAgent(Arrays.asList(9005140), new DateTime().getYear()).size());
		
		// ok
		assertEquals(1, dpmRepository.getListDpmIndemChoixAgent(Arrays.asList(9005138), new DateTime().getYear()).size());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getDpmIndemAnneeCourant() {
		// ok
		DpmIndemAnnee result = dpmRepository.getDpmIndemAnneeCourant();
		
		assertEquals(result.getAnnee().intValue(), new DateTime().getYear());
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getDpmIndemAnneeByAnnee() {
		assertNull(dpmRepository.getDpmIndemAnneeByAnnee(new DateTime().getYear()-1));
		assertNotNull(dpmRepository.getDpmIndemAnneeByAnnee(new DateTime().getYear()));
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListDpmIndemAnnee() {
		assertEquals(1, dpmRepository.getListDpmIndemAnnee().size());
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getDpmIndemChoixAgentByAgentAndAnnee() {
		// ok
		assertNotNull(dpmRepository.getDpmIndemChoixAgentByAgentAndAnnee(9005138, new DateTime().getYear()));
		// teste id agent
		assertNull(dpmRepository.getDpmIndemChoixAgentByAgentAndAnnee(9005138+1, new DateTime().getYear()));
		// teste date
		assertNull(dpmRepository.getDpmIndemChoixAgentByAgentAndAnnee(9005138, new DateTime().getYear()+1));
	}
}
