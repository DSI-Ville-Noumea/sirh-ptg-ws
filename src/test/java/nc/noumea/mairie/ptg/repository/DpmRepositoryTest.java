package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
		dpmAnnee.setDateDebut(new DateTime().minusMonths(3).toDate());
		dpmAnnee.setDateFin(new DateTime().plusMonths(3).toDate());
		dpmRepository.persistEntity(dpmAnnee);
		
		DpmIndemChoixAgent choixAgent = new DpmIndemChoixAgent();
		choixAgent.setIdAgent(9005138);
		choixAgent.setIdAgentCreation(9009999);
		choixAgent.setDpmIndemAnnee(dpmAnnee);
		choixAgent.setDateMaj(new Date());
		choixAgent.setChoixRecuperation(false);
		choixAgent.setChoixIndemnite(true);
		dpmRepository.persistEntity(choixAgent);
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListDpmIndemChoixAgent() {
		
		// teste l annee
		assertEquals(0, dpmRepository.getListDpmIndemChoixAgent(Arrays.asList(9005138), new DateTime().getYear()-1, null, null).size());
		
		// teste l agent
		assertEquals(0, dpmRepository.getListDpmIndemChoixAgent(Arrays.asList(9005140), new DateTime().getYear(), null, null).size());

		// teste le boolean choixrecuperation
		assertEquals(1, dpmRepository.getListDpmIndemChoixAgent(Arrays.asList(9005138), new DateTime().getYear(), null, false).size());
		assertEquals(0, dpmRepository.getListDpmIndemChoixAgent(Arrays.asList(9005138), new DateTime().getYear(), null, true).size());

		// teste le boolean choixrecuperation
		assertEquals(1, dpmRepository.getListDpmIndemChoixAgent(Arrays.asList(9005138), new DateTime().getYear(), true, null).size());
		assertEquals(0, dpmRepository.getListDpmIndemChoixAgent(Arrays.asList(9005138), new DateTime().getYear(), false, null).size());
		
		// ok
		assertEquals(1, dpmRepository.getListDpmIndemChoixAgent(Arrays.asList(9005138), new DateTime().getYear(), null, null).size());

		// ok
		assertEquals(1, dpmRepository.getListDpmIndemChoixAgent(null, null, null, null).size());
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getDpmIndemChoixAgent() {
		
		// teste l annee
		assertNull(dpmRepository.getDpmIndemChoixAgent(9005138, new DateTime().getYear()-1));
		
		// teste l agent
		assertNull(dpmRepository.getDpmIndemChoixAgent(9005140, new DateTime().getYear()));
		
		// ok
		assertNotNull(dpmRepository.getDpmIndemChoixAgent(9005138, new DateTime().getYear()));
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListDpmIndemAnneeOuverte() {
		// ok
		List<DpmIndemAnnee> result = dpmRepository.getListDpmIndemAnneeOuverte();
		
		assertEquals(result.size(), 1);
		assertEquals(result.get(0).getAnnee().intValue(), new DateTime().getYear());
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
