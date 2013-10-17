package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.domain.RefTypePointage;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring/applicationContext-test.xml"})
public class EtatPayeurRepositoryTest {

	@Autowired
	EtatPayeurRepository repository;
	
	@PersistenceContext(unitName = "ptgPersistenceUnit")
	EntityManager ptgEntityManager;
	
//	@Test
	@Transactional("ptgTransactionManager")
	public void getEtatPayeurById() {
		
		EtatPayeur etatPayeur = new EtatPayeur();
		etatPayeur.setIdEtatPayeur(new Integer(1));
		etatPayeur.setDateEtatPayeur(new LocalDate(2013, 10, 17).toDate());
		etatPayeur.setFichier("testUnit.pdf");
		etatPayeur.setLabel("test unitaire");
		etatPayeur.setStatut(AgentStatutEnum.C);
		etatPayeur.setType(new RefTypePointage());
		etatPayeur.persist();
		
		EtatPayeur result = repository.getEtatPayeurById(new Integer(1));
		
		assertEquals("testUnit.pdf", result.getFichier());
	}
	
//	@Test
	@Transactional("ptgTransactionManager")
	public void getListEditionEtatPayeur() {
		
		EtatPayeur etatPayeur = new EtatPayeur();
		etatPayeur.setIdEtatPayeur(new Integer(1));
		etatPayeur.setDateEtatPayeur(new LocalDate(2013, 10, 17).toDate());
		etatPayeur.setFichier("testUnit.pdf");
		etatPayeur.setLabel("test unitaire");
		etatPayeur.setStatut(AgentStatutEnum.C);
		etatPayeur.setType(new RefTypePointage());
		etatPayeur.persist();
		
		EtatPayeur et2 = new EtatPayeur();
		et2.setIdEtatPayeur(new Integer(2));
		et2.setDateEtatPayeur(new LocalDate(2013, 10, 18).toDate());
		et2.setFichier("testUnit2.pdf");
		et2.setLabel("test unitaire2");
		et2.setStatut(AgentStatutEnum.C);
		et2.setType(new RefTypePointage());
		et2.persist();
		
		List<EtatPayeur> result = repository.getListEditionEtatPayeur(AgentStatutEnum.C);
		
		assertEquals(2, result.size());
	}
}
