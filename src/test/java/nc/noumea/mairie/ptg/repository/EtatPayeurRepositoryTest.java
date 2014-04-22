package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.domain.RefTypePointage;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring/applicationContext-test.xml"})
public class EtatPayeurRepositoryTest {

	@Autowired
	EtatPayeurRepository repository;
	
	@PersistenceContext(unitName = "ptgPersistenceUnit")
	EntityManager ptgEntityManager;
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getEtatPayeurById_returnResult() {
		
		RefTypePointage type = new RefTypePointage(); 
			type.setIdRefTypePointage(1);
			type.setLabel("ref type 1");
		ptgEntityManager.persist(type);
		
		EtatPayeur etatPayeur = new EtatPayeur();
			etatPayeur.setDateEtatPayeur(new LocalDate(2013, 10, 17).toDate());
			etatPayeur.setFichier("testUnit.pdf");
			etatPayeur.setLabel("test unitaire");
			etatPayeur.setStatut(AgentStatutEnum.C);
			etatPayeur.setType(type);
			etatPayeur.setIdAgent(9005138);
		ptgEntityManager.persist(etatPayeur);
		
		EtatPayeur result = repository.getEtatPayeurById(new Integer(5));
		
		assertEquals("testUnit.pdf", result.getFichier());
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListEditionEtatPayeur_returnResult() {
		
		RefTypePointage type = new RefTypePointage(); 
			type.setIdRefTypePointage(1);
			type.setLabel("ref type 1");
		ptgEntityManager.persist(type);
		
		EtatPayeur et1 = new EtatPayeur();
			et1.setDateEtatPayeur(new LocalDate(2013, 10, 17).toDate());
			et1.setFichier("testUnit.pdf");
			et1.setLabel("test unitaire");
			et1.setStatut(AgentStatutEnum.C);
			et1.setType(type);
			et1.setIdAgent(9005138);
		ptgEntityManager.persist(et1);
		
		EtatPayeur et2 = new EtatPayeur();
			et2.setDateEtatPayeur(new LocalDate(2013, 10, 18).toDate());
			et2.setFichier("testUnit2.pdf");
			et2.setLabel("test unitaire 2");
			et2.setStatut(AgentStatutEnum.C);
			et2.setType(type);
			et2.setIdAgent(9005138);
		ptgEntityManager.persist(et2);
		
		List<EtatPayeur> result = repository.getListEditionEtatPayeur(AgentStatutEnum.C);
		
		assertEquals(2, result.size());
		assertEquals("testUnit2.pdf", result.get(0).getFichier());
		assertEquals("testUnit.pdf", result.get(1).getFichier());
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getEtatPayeurById_returnNoResult() {
		
		RefTypePointage type = new RefTypePointage(); 
			type.setIdRefTypePointage(1);
			type.setLabel("ref type 1");
		ptgEntityManager.persist(type);
		
		EtatPayeur etatPayeur = new EtatPayeur();
			etatPayeur.setDateEtatPayeur(new LocalDate(2013, 10, 17).toDate());
			etatPayeur.setFichier("testUnit.pdf");
			etatPayeur.setLabel("test unitaire");
			etatPayeur.setStatut(AgentStatutEnum.C);
			etatPayeur.setType(type);
			etatPayeur.setIdAgent(9005138);
		ptgEntityManager.persist(etatPayeur);
		
		Throwable e = null;
		try {
			EtatPayeur result = repository.getEtatPayeurById(new Integer(2));
	    } catch (Throwable ex) {
	        e = ex;
	    }

		assertTrue(e instanceof javax.persistence.NoResultException);
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	@Test
	@Transactional("ptgTransactionManager")
	public void getListEditionEtatPayeur_returnNoResult() {
		
		RefTypePointage type = new RefTypePointage(); 
			type.setIdRefTypePointage(1);
			type.setLabel("ref type 1");
		ptgEntityManager.persist(type);
		
		EtatPayeur et1 = new EtatPayeur();
			et1.setDateEtatPayeur(new LocalDate(2013, 10, 17).toDate());
			et1.setFichier("testUnit.pdf");
			et1.setLabel("test unitaire");
			et1.setStatut(AgentStatutEnum.C);
			et1.setType(type);
			et1.setIdAgent(9005138);
		ptgEntityManager.persist(et1);
		
		EtatPayeur et2 = new EtatPayeur();
			et2.setDateEtatPayeur(new LocalDate(2013, 10, 18).toDate());
			et2.setFichier("testUnit2.pdf");
			et2.setLabel("test unitaire 2");
			et2.setStatut(AgentStatutEnum.C);
			et2.setType(type);
			et2.setIdAgent(9005138);
		ptgEntityManager.persist(et2);
		
		List<EtatPayeur> result = repository.getListEditionEtatPayeur(AgentStatutEnum.CC);
		
		assertEquals(0, result.size());
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	
}
