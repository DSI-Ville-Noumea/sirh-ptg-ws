package nc.noumea.mairie.titreRepas.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPrestataire;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext-test.xml" })
public class TitreRepasRepositoryTest {

	@Autowired
	TitreRepasRepository	repository;

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	private EntityManager	ptgEntityManager;

	@Test
	@Transactional("ptgTransactionManager")
	public void getListTitreRepasDemande() {

		Date dateMonth = new DateTime().withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0).toDate();

		TitreRepasDemande demande = new TitreRepasDemande();
		demande.setIdAgent(9005138);
		demande.setDateMonth(dateMonth);
		demande.setCommande(true);

		TitreRepasEtatDemande etat = new TitreRepasEtatDemande();
		etat.setCommande(true);
		etat.setIdAgent(9002990);
		etat.setDateMaj(new Date());
		etat.setTitreRepasDemande(demande);
		etat.setEtat(EtatPointageEnum.SAISI);
		etat.setCommentaire("commentaire");

		demande.getEtats().add(etat);

		repository.persist(demande);
		ptgEntityManager.persist(etat);

		List<TitreRepasDemande> result = repository.getListTitreRepasDemande(Arrays.asList(9005138), null, null, EtatPointageEnum.SAISI.getCodeEtat(),
				null, dateMonth);
		assertEquals(1, result.size());

		result = repository.getListTitreRepasDemande(null, new DateTime().minusMonths(1).toDate(), new DateTime().plusMonths(1).toDate(), null, null,
				null);
		assertEquals(1, result.size());
		result = repository.getListTitreRepasDemande(new ArrayList<Integer>(), new DateTime().minusMonths(1).toDate(),
				new DateTime().plusMonths(1).toDate(), null, null, null);
		assertEquals(1, result.size());

		result = repository.getListTitreRepasDemande(null, new DateTime().minusMonths(1).toDate(), new DateTime().plusMonths(1).toDate(), null, true,
				null);
		assertEquals(1, result.size());

		// bad commande
		result = repository.getListTitreRepasDemande(null, new DateTime().minusMonths(1).toDate(), new DateTime().plusMonths(1).toDate(), null, false,
				null);
		assertEquals(0, result.size());

		// bad agent
		result = repository.getListTitreRepasDemande(Arrays.asList(9002999), new DateTime().minusMonths(1).toDate(),
				new DateTime().plusMonths(1).toDate(), null, true, null);
		assertEquals(0, result.size());

		// bad date periode
		result = repository.getListTitreRepasDemande(null, new DateTime().minusMonths(3).toDate(), new DateTime().minusMonths(1).toDate(), null, true,
				null);
		assertEquals(0, result.size());

		// bad etat
		result = repository.getListTitreRepasDemande(Arrays.asList(9005138), null, null, EtatPointageEnum.VALIDE.getCodeEtat(), null, dateMonth);
		assertEquals(0, result.size());

		// bad dateMonth
		result = repository.getListTitreRepasDemande(Arrays.asList(9005138), null, null, EtatPointageEnum.SAISI.getCodeEtat(), null,
				new DateTime().plusMonths(1).toDate());
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

		repository.persist(demande2);
		ptgEntityManager.persist(etat2);

		// 2 results
		result = repository.getListTitreRepasDemande(Arrays.asList(9005138, 9005131), null, null, EtatPointageEnum.SAISI.getCodeEtat(), null,
				dateMonth);
		assertEquals(2, result.size());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListeMoisTitreRepasSaisie_0Date() {

		List<Date> result = repository.getListeMoisTitreRepasSaisie();

		assertEquals(result.size(), 0);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListeMoisTitreRepasSaisie_OK() {

		DateTime dateMonth = new DateTime(2014, 12, 1, 0, 0, 0);
		DateTime dateMonth2 = new DateTime(2014, 11, 1, 0, 0, 0);

		TitreRepasDemande d3 = new TitreRepasDemande();
		d3.setDateMonth(dateMonth2.toDate());
		ptgEntityManager.persist(d3);

		TitreRepasDemande d2 = new TitreRepasDemande();
		d2.setDateMonth(dateMonth.toDate());
		ptgEntityManager.persist(d2);

		TitreRepasDemande d = new TitreRepasDemande();
		d.setDateMonth(dateMonth.toDate());
		ptgEntityManager.persist(d);

		List<Date> result = repository.getListeMoisTitreRepasSaisie();

		assertEquals(result.size(), 2);
		assertEquals(dateMonth.toDate(), result.get(0));
		assertEquals(dateMonth2.toDate(), result.get(1));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getTitreRepasDemandeById() {

		DateTime dateMonth = new DateTime(2014, 12, 1, 0, 0, 0);

		TitreRepasDemande d = new TitreRepasDemande();
		d.setDateMonth(dateMonth.toDate());
		repository.persist(d);

		TitreRepasDemande result = repository.getTitreRepasDemandeById(d.getIdTrDemande());

		assertEquals(result, d);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListTitreRepasEtatPayeur() {

		TitreRepasEtatPayeur titreRepasEtatPayeur = new TitreRepasEtatPayeur();
		titreRepasEtatPayeur.setIdTrEtatPayeur(1);
		titreRepasEtatPayeur.setDateEtatPayeur(new Date());
		titreRepasEtatPayeur.setIdAgent(9005154);
		titreRepasEtatPayeur.setDateEdition(new Date());
		titreRepasEtatPayeur.setLabel("label");
		titreRepasEtatPayeur.setFichier("fichier");
		ptgEntityManager.persist(titreRepasEtatPayeur);

		List<TitreRepasEtatPayeur> result = repository.getListTitreRepasEtatPayeur();

		assertEquals(1, result.size());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getTitreRepasEtatPayeurByMonth() {

		TitreRepasEtatPayeur titreRepasEtatPayeur = new TitreRepasEtatPayeur();
		titreRepasEtatPayeur.setIdTrEtatPayeur(1);
		titreRepasEtatPayeur.setDateEtatPayeur(new LocalDate(new Date()).withDayOfMonth(1).toDate());
		titreRepasEtatPayeur.setIdAgent(9005154);
		titreRepasEtatPayeur.setDateEdition(new Date());
		titreRepasEtatPayeur.setLabel("label");
		titreRepasEtatPayeur.setFichier("fichier");
		ptgEntityManager.persist(titreRepasEtatPayeur);

		TitreRepasEtatPayeur result = repository.getTitreRepasEtatPayeurByMonth(new LocalDate(new Date()).withDayOfMonth(1).toDate());

		assertEquals(titreRepasEtatPayeur, result);

		result = repository.getTitreRepasEtatPayeurByMonth(new LocalDate(new Date()).plusMonths(1).withDayOfMonth(1).toDate());

		assertNull(result);
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getEtatPrestataireByMonth_NoResult() {
		Date dateRecherche = new DateTime(2016, 1, 1, 0, 0, 0).toDate();

		TitreRepasEtatPrestataire titreRepasEtatPrestataire = new TitreRepasEtatPrestataire();
		titreRepasEtatPrestataire.setIdTrEtatPrestataire(1);
		titreRepasEtatPrestataire.setDateEtatPrestataire(new DateTime(2016, 2, 1, 0, 0, 0).toDate());
		titreRepasEtatPrestataire.setIdAgent(9005154);
		titreRepasEtatPrestataire.setDateEdition(new Date());
		titreRepasEtatPrestataire.setLabel("label");
		titreRepasEtatPrestataire.setFichier("fichier");
		ptgEntityManager.persist(titreRepasEtatPrestataire);

		TitreRepasEtatPrestataire result = repository.getEtatPrestataireByMonth(dateRecherche);
		assertNull(result);
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getEtatPrestataireByMonth_Result() {
		Date dateRecherche = new DateTime(2016, 1, 1, 0, 0, 0).toDate();

		TitreRepasEtatPrestataire titreRepasEtatPrestataire = new TitreRepasEtatPrestataire();
		titreRepasEtatPrestataire.setIdTrEtatPrestataire(1);
		titreRepasEtatPrestataire.setDateEtatPrestataire(dateRecherche);
		titreRepasEtatPrestataire.setIdAgent(9005154);
		titreRepasEtatPrestataire.setDateEdition(new Date());
		titreRepasEtatPrestataire.setLabel("label");
		titreRepasEtatPrestataire.setFichier("fichier");
		ptgEntityManager.persist(titreRepasEtatPrestataire);

		TitreRepasEtatPrestataire result = repository.getEtatPrestataireByMonth(dateRecherche);

		assertNotNull(result);
		assertEquals("label", result.getLabel());
	}

}
