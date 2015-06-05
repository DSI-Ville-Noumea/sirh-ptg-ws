package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
			agent.setCodeService("DEAB");
			agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent.setDateModification(new Date());
		ptgEntityManager.persist(agent);
		
		Droit droit = new Droit();
			droit.setApprobateur(true);
			droit.setDateModification(new Date());
			droit.setIdAgent(9008767);
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
			droitApprobateurTrue.setOperateur(false);
		ptgEntityManager.persist(droitApprobateurTrue);
		
		Droit droitApprobateurFalse = new Droit();
			droitApprobateurFalse.setApprobateur(false);
			droitApprobateurFalse.setDateModification(new Date());
			droitApprobateurFalse.setIdAgent(9008768);
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
			droitApprobateurTrue.setOperateur(false);
			droitApprobateurTrue.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateurTrue);
		
		Droit droitApprobateurFalse = new Droit();
			droitApprobateurFalse.setApprobateur(false);
			droitApprobateurFalse.setDateModification(new Date());
			droitApprobateurFalse.setIdAgent(9008768);
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
			droitApprobateurTrue.setOperateur(true);
		ptgEntityManager.persist(droitApprobateurTrue);
		
		Droit droitApprobateurFalse = new Droit();
			droitApprobateurFalse.setApprobateur(true);
			droitApprobateurFalse.setDateModification(new Date());
			droitApprobateurFalse.setIdAgent(9008768);
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
			droitApprobateurAgentTrue.setOperateur(false);
			droitApprobateurAgentTrue.setIdAgentDelegataire(9008786);
		ptgEntityManager.persist(droitApprobateurAgentTrue);
		
		Droit droitOperateurDelegataireTrue = new Droit();
			droitOperateurDelegataireTrue.setApprobateur(false);
			droitOperateurDelegataireTrue.setDateModification(new Date());
			droitOperateurDelegataireTrue.setIdAgent(9008788);
			droitOperateurDelegataireTrue.setOperateur(true);
			droitOperateurDelegataireTrue.setIdAgentDelegataire(9008789);
		ptgEntityManager.persist(droitOperateurDelegataireTrue);
		
		Droit droitFalse = new Droit();
			droitFalse.setApprobateur(false);
			droitFalse.setDateModification(new Date());
			droitFalse.setIdAgent(9008781);
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
			droitApprobateur1.setOperateur(false);
			droitApprobateur1.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateur1);
		
		Droit droitApprobateur2 = new Droit();
			droitApprobateur2.setApprobateur(true);
			droitApprobateur2.setDateModification(new Date());
			droitApprobateur2.setIdAgent(9008767);
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
			droitApprobateur1.setOperateur(false);
			droitApprobateur1.setIdAgentDelegataire(9008769);
		ptgEntityManager.persist(droitApprobateur1);
		
		Droit droitApprobateur2 = new Droit();
			droitApprobateur2.setApprobateur(false);
			droitApprobateur2.setDateModification(new Date());
			droitApprobateur2.setIdAgent(9008767);
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
	public void getListOfAgentsToInputOrApprove(){

		Droit droit = new Droit();
			droit.setApprobateur(true);
			droit.setDateModification(new Date());
			droit.setIdAgent(9008777);
			droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008777);
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
			droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008767);
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
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentsToInputOrApprove_oneResultWith2SameAgent(){

		Date dateModif = new Date();
		
		Droit droit = new Droit();
			droit.setApprobateur(true);
			droit.setDateModification(new Date());
			droit.setIdAgent(9008777);
			droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Droit droit2 = new Droit();
			droit2.setApprobateur(true);
			droit2.setDateModification(new Date());
			droit2.setIdAgent(9008777);
			droit2.setOperateur(false);
		ptgEntityManager.persist(droit2);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		droits.add(droit2);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008777);
			agent.setCodeService("DEAB");
			agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent.setDateModification(dateModif);
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
	public void getListOfAgentsToInputOrApproveByService_oneResultWith2SameAgent(){

		Droit droit = new Droit();
			droit.setApprobateur(true);
			droit.setDateModification(new Date());
			droit.setIdAgent(9008767);
			droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008767);
			agent.setCodeService("DEAB");
			agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent.setDateModification(new Date());
			agent.setDroits(droits);
		ptgEntityManager.persist(agent);
		
		Droit droit2 = new Droit();
			droit2.setApprobateur(true);
			droit2.setDateModification(new Date());
			droit2.setIdAgent(9008767);
			droit2.setOperateur(false);
		ptgEntityManager.persist(droit2);
		
		Set<Droit> droits2 = new HashSet<Droit>();
		droits.add(droit2);
		
		DroitsAgent agent2 = new DroitsAgent();
			agent2.setIdAgent(9008767);
			agent2.setCodeService("DEAB");
			agent2.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent2.setDateModification(new Date());
			agent2.setDroits(droits2);
		ptgEntityManager.persist(agent2);
		
		List<DroitsAgent> result = repository.getListOfAgentsToInputOrApprove(9008767, "DEAB");
		
		assertEquals(1, result.size());
		assertEquals("9008767", result.get(0).getIdAgent().toString());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentsToApprove(){

		Droit droit = new Droit();
			droit.setApprobateur(true);
			droit.setDateModification(new Date());
			droit.setIdAgent(9008777);
			droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008777);
			agent.setCodeService("DEAB");
			agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent.setDateModification(new Date());
			agent.setDroits(droits);
		ptgEntityManager.persist(agent);
		
		List<DroitsAgent> result = repository.getListOfAgentsToApprove(9008777, null, true);
		
		assertEquals(1, result.size());
		assertEquals("9008777", result.get(0).getIdAgent().toString());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentsToApprove_withService(){

		Droit droit = new Droit();
			droit.setApprobateur(true);
			droit.setDateModification(new Date());
			droit.setIdAgent(9008767);
			droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008767);
			agent.setCodeService("DEAB");
			agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent.setDateModification(new Date());
			agent.setDroits(droits);
		ptgEntityManager.persist(agent);
		
		List<DroitsAgent> result = repository.getListOfAgentsToApprove(9008767, "DEAB", true);
		
		assertEquals(1, result.size());
		assertEquals("9008767", result.get(0).getIdAgent().toString());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentsToApprove_0resultBecauseNotApprobateur(){

		Droit droit = new Droit();
			droit.setApprobateur(false);
			droit.setDateModification(new Date());
			droit.setIdAgent(9008777);
			droit.setOperateur(true);
		ptgEntityManager.persist(droit);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008777);
			agent.setCodeService("DEAB");
			agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent.setDateModification(new Date());
			agent.setDroits(droits);
		ptgEntityManager.persist(agent);
		
		List<DroitsAgent> result = repository.getListOfAgentsToApprove(9008777, null, true);
		
		assertEquals(0, result.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentsToApprove_withService_0resultBecauseNotApprobateur(){

		Droit droit = new Droit();
			droit.setApprobateur(false);
			droit.setDateModification(new Date());
			droit.setIdAgent(9008767);
			droit.setOperateur(true);
		ptgEntityManager.persist(droit);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008767);
			agent.setCodeService("DEAB");
			agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent.setDateModification(new Date());
			agent.setDroits(droits);
		ptgEntityManager.persist(agent);
		
		List<DroitsAgent> result = repository.getListOfAgentsToApprove(9008767, "DEAB", true);
		
		assertEquals(0, result.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentsToApprove_oneResultWith2SameAgent(){

		Date dateModif = new Date();
		
		Droit droit = new Droit();
			droit.setApprobateur(true);
			droit.setDateModification(new Date());
			droit.setIdAgent(9008777);
			droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Droit droit2 = new Droit();
			droit2.setApprobateur(true);
			droit2.setDateModification(new Date());
			droit2.setIdAgent(9008777);
			droit2.setOperateur(false);
		ptgEntityManager.persist(droit2);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		droits.add(droit2);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008777);
			agent.setCodeService("DEAB");
			agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent.setDateModification(dateModif);
			agent.setDroits(droits);
		ptgEntityManager.persist(agent);
		
		List<DroitsAgent> result = repository.getListOfAgentsToApprove(9008777, null, true);
		
		assertEquals(1, result.size());
		assertEquals("9008777", result.get(0).getIdAgent().toString());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentsToApprove_withService_oneResultWith2SameAgent(){

		Droit droit = new Droit();
			droit.setApprobateur(true);
			droit.setDateModification(new Date());
			droit.setIdAgent(9008767);
			droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Droit droit2 = new Droit();
			droit2.setApprobateur(true);
			droit2.setDateModification(new Date());
			droit2.setIdAgent(9008767);
			droit2.setOperateur(false);
		ptgEntityManager.persist(droit2);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		droits.add(droit2);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008769);
			agent.setCodeService("DEAB");
			agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent.setDateModification(new Date());
			agent.setDroits(droits);
		ptgEntityManager.persist(agent);
		
		List<DroitsAgent> result = repository.getListOfAgentsToApprove(9008767, "DEAB", true);
		
		assertEquals(1, result.size());
		assertEquals("9008769", result.get(0).getIdAgent().toString());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentsToApprove_withService_2Agents_withDelegataire(){

		Droit droit = new Droit();
			droit.setApprobateur(true);
			droit.setDateModification(new Date());
			droit.setIdAgent(9005138);
			droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008767);
			agent.setCodeService("DEAB");
			agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent.setDateModification(new Date());
			agent.setDroits(droits);
		ptgEntityManager.persist(agent);
		
		Droit droit2 = new Droit();
			droit2.setApprobateur(true);
			droit2.setDateModification(new Date());
			droit2.setIdAgent(9002990);
			droit2.setOperateur(false);
			droit2.setIdAgentDelegataire(9005138);
		ptgEntityManager.persist(droit2);
		
		Set<Droit> droits2 = new HashSet<Droit>();
		droits2.add(droit2);
		
		DroitsAgent agent2 = new DroitsAgent();
			agent2.setIdAgent(9008789);
			agent2.setCodeService("DEAB");
			agent2.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent2.setDateModification(new Date());
			agent2.setDroits(droits2);
		ptgEntityManager.persist(agent2);
		
		List<DroitsAgent> result = repository.getListOfAgentsToApprove(9005138, "DEAB", true);
		
		assertEquals(2, result.size());
		assertEquals("9008767", result.get(0).getIdAgent().toString());
		assertEquals("9008789", result.get(1).getIdAgent().toString());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentsToApprove_withService_1Agent_withoutDelegataire(){

		Droit droit = new Droit();
			droit.setApprobateur(true);
			droit.setDateModification(new Date());
			droit.setIdAgent(9005138);
			droit.setOperateur(false);
		ptgEntityManager.persist(droit);
		
		Set<Droit> droits = new HashSet<Droit>();
		droits.add(droit);
		
		DroitsAgent agent = new DroitsAgent();
			agent.setIdAgent(9008767);
			agent.setCodeService("DEAB");
			agent.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent.setDateModification(new Date());
			agent.setDroits(droits);
		ptgEntityManager.persist(agent);
		
		Droit droit2 = new Droit();
			droit2.setApprobateur(true);
			droit2.setDateModification(new Date());
			droit2.setIdAgent(9002990);
			droit2.setOperateur(false);
			droit2.setIdAgentDelegataire(9005138);
		ptgEntityManager.persist(droit2);
		
		Set<Droit> droits2 = new HashSet<Droit>();
		droits2.add(droit2);
		
		DroitsAgent agent2 = new DroitsAgent();
			agent2.setIdAgent(9008789);
			agent2.setCodeService("DEAB");
			agent2.setLibelleService("DASP Pôle Administratif et Budgétaire");
			agent2.setDateModification(new Date());
			agent2.setDroits(droits2);
		ptgEntityManager.persist(agent2);
		
		List<DroitsAgent> result = repository.getListOfAgentsToApprove(9005138, "DEAB", false);
		
		assertEquals(1, result.size());
		assertEquals("9008767", result.get(0).getIdAgent().toString()); 

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	
}
