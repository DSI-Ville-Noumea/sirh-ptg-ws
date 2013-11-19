package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring/applicationContext-test.xml"})
public class AccessRightsRepositoryTest {

	@Autowired
	AccessRightsRepository repository;
	
	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getAgentAccessRights_ReturnNull() {
		
		// When
		List<Droit> result = repository.getAgentAccessRights(9008767);
		
		// Then 
		assertEquals(0, result.size());
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getAgentAccessRights_ReturnResult() {
		
		DroitsAgent agent = new DroitsAgent();
		agent.setIdAgent(9008767);
		agent.setIdDroitsAgent(1);
		agent.setCodeService("DEAB");
		agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
		agent.setDateModification(new Date());
		ptgEntityManager.persist(agent);
		
		Droit droit = new Droit();
		droit.setApprobateur(true);
		droit.setDateModification(new Date());
		droit.setIdAgent(9008767);
		droit.setIdDroit(1);
		droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		// When
		List<Droit> result = repository.getAgentAccessRights(9008767);
		
		// Then 
		assertEquals(1, result.size());
		assertEquals("9008767", result.get(0).getIdAgent().toString());
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void isUserApprobator() {
		
		Droit droitApprobateurTrue = new Droit();
		droitApprobateurTrue.setApprobateur(true);
		droitApprobateurTrue.setDateModification(new Date());
		droitApprobateurTrue.setIdAgent(9008767);
		droitApprobateurTrue.setIdDroit(1);
		droitApprobateurTrue.setOperateur(false);
		ptgEntityManager.persist(droitApprobateurTrue);
		
		Droit droitApprobateurFalse = new Droit();
		droitApprobateurFalse.setApprobateur(false);
		droitApprobateurFalse.setDateModification(new Date());
		droitApprobateurFalse.setIdAgent(9008768);
		droitApprobateurFalse.setIdDroit(2);
		droitApprobateurFalse.setOperateur(true);
		ptgEntityManager.persist(droitApprobateurFalse);
		
		// When
		assertTrue(repository.isUserApprobator(9008767));
		assertFalse(repository.isUserApprobator(9008768));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void isUserApprobatorOrDelegataire(){
		
		Droit droitApprobateurTrue = new Droit();
		droitApprobateurTrue.setApprobateur(true);
		droitApprobateurTrue.setDateModification(new Date());
		droitApprobateurTrue.setIdAgent(9008767);
		droitApprobateurTrue.setIdDroit(1);
		droitApprobateurTrue.setOperateur(false);
		droitApprobateurTrue.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateurTrue);
		
		Droit droitApprobateurFalse = new Droit();
		droitApprobateurFalse.setApprobateur(false);
		droitApprobateurFalse.setDateModification(new Date());
		droitApprobateurFalse.setIdAgent(9008768);
		droitApprobateurFalse.setIdDroit(2);
		droitApprobateurFalse.setOperateur(true);
		droitApprobateurFalse.setIdAgentDelegataire(9008773);
		ptgEntityManager.persist(droitApprobateurFalse);
		
		boolean isApprobateur = repository.isUserApprobatorOrDelegataire(9008769);
		boolean isNoApprobateur = repository.isUserApprobatorOrDelegataire(9008773);
				
		// When
		assertTrue(isApprobateur);
		assertFalse(isNoApprobateur);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void isUserOperator(){
		
		Droit droitApprobateurTrue = new Droit();
		droitApprobateurTrue.setApprobateur(false);
		droitApprobateurTrue.setDateModification(new Date());
		droitApprobateurTrue.setIdAgent(9008767);
		droitApprobateurTrue.setIdDroit(1);
		droitApprobateurTrue.setOperateur(true);
		ptgEntityManager.persist(droitApprobateurTrue);
		
		Droit droitApprobateurFalse = new Droit();
		droitApprobateurFalse.setApprobateur(true);
		droitApprobateurFalse.setDateModification(new Date());
		droitApprobateurFalse.setIdAgent(9008768);
		droitApprobateurFalse.setIdDroit(2);
		droitApprobateurFalse.setOperateur(false);
		ptgEntityManager.persist(droitApprobateurFalse);
		
		// When
		assertTrue(repository.isUserOperator(9008767));
		assertFalse(repository.isUserOperator(9008768));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void isUserApprobatorOrOperatorOrDelegataire(){
		
		Droit droitApprobateurAgentTrue = new Droit();
		droitApprobateurAgentTrue.setApprobateur(true);
		droitApprobateurAgentTrue.setDateModification(new Date());
		droitApprobateurAgentTrue.setIdAgent(9008787);
		droitApprobateurAgentTrue.setIdDroit(1);
		droitApprobateurAgentTrue.setOperateur(false);
		droitApprobateurAgentTrue.setIdAgentDelegataire(9008786);
		ptgEntityManager.persist(droitApprobateurAgentTrue);
		
		Droit droitOperateurDelegataireTrue = new Droit();
		droitOperateurDelegataireTrue.setApprobateur(false);
		droitOperateurDelegataireTrue.setDateModification(new Date());
		droitOperateurDelegataireTrue.setIdAgent(9008788);
		droitOperateurDelegataireTrue.setIdDroit(1);
		droitOperateurDelegataireTrue.setOperateur(true);
		droitOperateurDelegataireTrue.setIdAgentDelegataire(9008789);
		ptgEntityManager.persist(droitOperateurDelegataireTrue);
		
		Droit droitFalse = new Droit();
		droitFalse.setApprobateur(false);
		droitFalse.setDateModification(new Date());
		droitFalse.setIdAgent(9008781);
		droitFalse.setIdDroit(2);
		droitFalse.setOperateur(false);
		droitFalse.setIdAgentDelegataire(9008780);
		ptgEntityManager.persist(droitFalse);
		
		// When
		assertTrue(repository.isUserApprobatorOrOperatorOrDelegataire(9008786));
		assertTrue(repository.isUserApprobatorOrOperatorOrDelegataire(9008789));
		assertFalse(repository.isUserApprobatorOrOperatorOrDelegataire(9008780));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getAgentsApprobateurs_result(){
		
		Droit droitApprobateur1 = new Droit();
		droitApprobateur1.setApprobateur(true);
		droitApprobateur1.setDateModification(new Date());
		droitApprobateur1.setIdAgent(9008767);
		droitApprobateur1.setIdDroit(1);
		droitApprobateur1.setOperateur(false);
		droitApprobateur1.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateur1);
		
		Droit droitApprobateur2 = new Droit();
		droitApprobateur2.setApprobateur(true);
		droitApprobateur2.setDateModification(new Date());
		droitApprobateur2.setIdAgent(9008767);
		droitApprobateur2.setIdDroit(1);
		droitApprobateur2.setOperateur(false);
		droitApprobateur2.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateur2);
		
		List<Droit> listDroits = repository.getAgentsApprobateurs();
		
		assertEquals(2, listDroits.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getAgentsApprobateurs_noResult(){
		
		Droit droitApprobateur1 = new Droit();
		droitApprobateur1.setApprobateur(false);
		droitApprobateur1.setDateModification(new Date());
		droitApprobateur1.setIdAgent(9008767);
		droitApprobateur1.setIdDroit(1);
		droitApprobateur1.setOperateur(false);
		droitApprobateur1.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateur1);
		
		Droit droitApprobateur2 = new Droit();
		droitApprobateur2.setApprobateur(false);
		droitApprobateur2.setDateModification(new Date());
		droitApprobateur2.setIdAgent(9008767);
		droitApprobateur2.setIdDroit(1);
		droitApprobateur2.setOperateur(false);
		droitApprobateur2.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateur2);
		
		List<Droit> result = null;
		try {
			result = repository.getAgentsApprobateurs();
		} catch (Throwable ex) {
	    }
	
		assertEquals(0, result.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getAgentsOperateurs_result(){
		
		Droit droitApprobateur1 = new Droit();
		droitApprobateur1.setApprobateur(false);
		droitApprobateur1.setDateModification(new Date());
		droitApprobateur1.setIdAgent(9008767);
		droitApprobateur1.setIdDroit(1);
		droitApprobateur1.setOperateur(true);
		droitApprobateur1.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateur1);
		
		Droit droitApprobateur2 = new Droit();
		droitApprobateur2.setApprobateur(false);
		droitApprobateur2.setDateModification(new Date());
		droitApprobateur2.setIdAgent(9008767);
		droitApprobateur2.setIdDroit(1);
		droitApprobateur2.setOperateur(true);
		droitApprobateur2.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateur2);
		
		List<Droit> listDroits = repository.getAgentsOperateurs();
		
		assertEquals(2, listDroits.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getAgentsOperateurs_noResult(){
		
		Droit droitApprobateur1 = new Droit();
		droitApprobateur1.setApprobateur(false);
		droitApprobateur1.setDateModification(new Date());
		droitApprobateur1.setIdAgent(9008767);
		droitApprobateur1.setIdDroit(1);
		droitApprobateur1.setOperateur(false);
		droitApprobateur1.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateur1);
		
		Droit droitApprobateur2 = new Droit();
		droitApprobateur2.setApprobateur(false);
		droitApprobateur2.setDateModification(new Date());
		droitApprobateur2.setIdAgent(9008767);
		droitApprobateur2.setIdDroit(1);
		droitApprobateur2.setOperateur(false);
		droitApprobateur2.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateur2);
		
		List<Droit> result = null;
		try {
			result = repository.getAgentsOperateurs();
		} catch (Throwable ex) {
	    }
	
		assertEquals(0, result.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentsToInputOrApprove(){

		Droit droit = new Droit();
		droit.setApprobateur(true);
		droit.setDateModification(new Date());
		droit.setIdAgent(9008777);
		droit.setIdDroit(1);
		droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		
		DroitsAgent agent = new DroitsAgent();
		agent.setIdAgent(9008777);
		agent.setIdDroitsAgent(1);
		agent.setCodeService("DEAB");
		agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
		agent.setDateModification(new Date());
		agent.setDroits(droits);
		ptgEntityManager.persist(agent);
		
		List<DroitsAgent> result = repository.getListOfAgentsToInputOrApprove(9008777);
		
		assertEquals(1, result.size());
		assertEquals("9008777", result.get(0).getIdAgent().toString());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentsToInputOrApproveByService(){

		Droit droit = new Droit();
		droit.setApprobateur(true);
		droit.setDateModification(new Date());
		droit.setIdAgent(9008767);
		droit.setIdDroit(1);
		droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		
		DroitsAgent agent = new DroitsAgent();
		agent.setIdAgent(9008767);
		agent.setIdDroitsAgent(1);
		agent.setCodeService("DEAB");
		agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
		agent.setDateModification(new Date());
		agent.setDroits(droits);
		ptgEntityManager.persist(agent);
		
		List<DroitsAgent> result = repository.getListOfAgentsToInputOrApprove(9008767, "DEAB");
		
		assertEquals(1, result.size());
		assertEquals("9008767", result.get(0).getIdAgent().toString());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
}
