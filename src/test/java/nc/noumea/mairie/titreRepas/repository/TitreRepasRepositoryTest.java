package nc.noumea.mairie.titreRepas.repository;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatDemande;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext-test.xml" })
public class TitreRepasRepositoryTest {

	@Autowired
	TitreRepasRepository repository;

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager ptgEntityManager;

	@Test
	@Transactional("ptgTransactionManager")
	public void getListTitreRepasDemande() {

		Date dateMonth = new DateTime().withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();

		TitreRepasDemande demande = new TitreRepasDemande();
		demande.setIdAgent(9005138);
		demande.setDateMonth(dateMonth);

		TitreRepasEtatDemande etat = new TitreRepasEtatDemande();
		etat.setCommande(true);
		etat.setIdAgent(9002990);
		etat.setDateMaj(new Date());
		etat.setTitreRepasDemande(demande);
		etat.setEtat(EtatPointageEnum.SAISI);
		etat.setCommentaire("commentaire");

		demande.getEtats().add(etat);

		ptgEntityManager.persist(demande);
		ptgEntityManager.persist(etat);

		List<TitreRepasDemande> result = repository.getListTitreRepasDemande(Arrays.asList(9005138), null, null, EtatPointageEnum.SAISI.getCodeEtat(), null, dateMonth);
		assertEquals(1, result.size());

		result = repository.getListTitreRepasDemande(null, new DateTime().minusMonths(1).toDate(), new DateTime().plusMonths(1).toDate(), null, null, null);
		assertEquals(1, result.size());

		result = repository.getListTitreRepasDemande(null, new DateTime().minusMonths(1).toDate(), new DateTime().plusMonths(1).toDate(), null, true, null);
		assertEquals(1, result.size());

		// bad commande
		result = repository.getListTitreRepasDemande(null, new DateTime().minusMonths(1).toDate(), new DateTime().plusMonths(1).toDate(), null, false, null);
		assertEquals(0, result.size());

		// bad agent
		result = repository.getListTitreRepasDemande(Arrays.asList(9002999), new DateTime().minusMonths(1).toDate(), new DateTime().plusMonths(1).toDate(), null, true, null);
		assertEquals(0, result.size());

		// bad date periode
		result = repository.getListTitreRepasDemande(null, new DateTime().minusMonths(3).toDate(), new DateTime().minusMonths(1).toDate(), null, true, null);
		assertEquals(0, result.size());

		// bad etat
		result = repository.getListTitreRepasDemande(Arrays.asList(9005138), null, null, EtatPointageEnum.VALIDE.getCodeEtat(), null, dateMonth);
		assertEquals(0, result.size());

		// bad dateMonth
		result = repository.getListTitreRepasDemande(Arrays.asList(9005138), null, null, EtatPointageEnum.SAISI.getCodeEtat(), null, new DateTime().plusMonths(1).toDate());
		assertEquals(0, result.size());

		TitreRepasDemande demande2 = new TitreRepasDemande();
		demande2.setIdAgent(9005131);
		demande2.setDateMonth(dateMonth);

		TitreRepasEtatDemande etat2 = new TitreRepasEtatDemande();
		etat2.setCommande(true);
		etat2.setIdAgent(9002990);
		etat2.setDateMaj(new Date());
		etat2.setTitreRepasDemande(demande2);
		etat2.setEtat(EtatPointageEnum.SAISI);
		etat2.setCommentaire("commentaire");

		demande2.getEtats().add(etat2);

		ptgEntityManager.persist(demande2);
		ptgEntityManager.persist(etat2);

		// 2 results
		result = repository.getListTitreRepasDemande(Arrays.asList(9005138, 9005131), null, null, EtatPointageEnum.SAISI.getCodeEtat(), null, dateMonth);
		assertEquals(2, result.size());
	}

}
