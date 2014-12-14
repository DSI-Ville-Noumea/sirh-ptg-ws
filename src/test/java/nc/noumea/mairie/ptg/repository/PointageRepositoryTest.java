package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.Droit;
import nc.noumea.mairie.ptg.domain.DroitsAgent;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.MairiePrimeTableEnum;
import nc.noumea.mairie.ptg.domain.MotifHeureSup;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.ptg.domain.RefTypeAbsenceEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext-test.xml" })
public class PointageRepositoryTest {

	@Autowired
	PointageRepository repository;

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	EntityManager ptgEntityManager;

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointages_FilterByIdAgentDateAndTypePointage() {

		// Given
		// String seqSql =
		// "CREATE SEQUENCE PTG_S_POINTAGE START WITH 1 INCREMENT BY 1 CACHE 1";
		// javax.persistence.Query q =
		// ptgEntityManager.createNativeQuery(seqSql);
		// q.executeUpdate();
		//
		// String seqselectSql =
		// "select PTG_S_POINTAGE.nextval as nb from dual;";
		// q = ptgEntityManager.createNativeQuery(seqselectSql);
		// BigInteger id = (BigInteger) q.getSingleResult();
		// BigInteger id2 = (BigInteger) q.getSingleResult();
		// BigInteger id3 = (BigInteger) q.getSingleResult();

		RefTypePointage abs = new RefTypePointage();
		abs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(abs);
		RefTypePointage hSup = new RefTypePointage();
		hSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		ptgEntityManager.persist(hSup);
		RefTypePointage prime = new RefTypePointage();
		prime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(prime);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008765);
		ptg.setType(abs);
		ptg.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptgEntityManager.persist(ptg);

		ptgEntityManager.flush();
		ptgEntityManager.clear();

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008765);
		ptg2.setType(abs);
		ptg2.setPointageParent(ptg);
		ptg2.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		ptg2.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptgEntityManager.persist(ptg2);

		Pointage ptg3 = new Pointage();
		ptg3.setIdAgent(9008766);
		ptg3.setType(abs);
		ptg3.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		ptg3.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptgEntityManager.persist(ptg3);

		Pointage ptg4 = new Pointage();
		ptg4.setIdAgent(9008765);
		ptg4.setType(prime);
		ptg4.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		ptg4.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptgEntityManager.persist(ptg4);

		RefTypeAbsence typeAbs = new RefTypeAbsence();
		typeAbs.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(typeAbs);

		Pointage ptg5 = new Pointage();
		ptg5.setIdAgent(9008765);
		ptg5.setType(abs);
		ptg5.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		ptg5.setDateDebut(new DateTime(2013, 7, 22, 6, 0, 0).toDate());
		ptg5.setDateFin(new DateTime(2013, 7, 22, 7, 0, 0).toDate());
		ptg5.setRefTypeAbsence(typeAbs);
		ptgEntityManager.persist(ptg5);

		Pointage ptg6 = new Pointage();
		ptg6.setIdAgent(9008765);
		ptg6.setType(abs);
		ptg6.setDateLundi(new LocalDate(2013, 7, 29).toDate());
		ptg6.setDateDebut(new DateTime(2013, 7, 29, 0, 0, 0).toDate());
		ptg6.setDateFin(new DateTime(2013, 7, 29, 0, 30, 0).toDate());
		ptgEntityManager.persist(ptg6);

		ptgEntityManager.flush();
		ptgEntityManager.clear();

		List<Integer> agents = Arrays.asList(9008765);
		Date fromDate = new LocalDate(2013, 7, 22).toDate();
		Date toDate = new LocalDate(2013, 7, 29).toDate();
		Integer idRefType = 1;

		// When
		List<Pointage> result = repository.getListPointages(agents, fromDate, toDate, idRefType);

		// Then
		assertEquals(3, result.size());
		assertEquals(ptg5.getDateDebut(), result.get(0).getDateDebut());
		assertEquals(ptg5.getDateFin(), result.get(0).getDateFin());
		assertEquals(ptg5.getCommentaire(), result.get(0).getCommentaire());
		assertEquals(RefTypeAbsenceEnum.CONCERTEE.getValue(), result.get(0).getRefTypeAbsence().getIdRefTypeAbsence());
		assertEquals(ptg5.getCommentaire(), result.get(0).getCommentaire());
		assertEquals(ptg5.getDateLundi(), result.get(0).getDateLundi());
		assertEquals(ptg5.getHeureSupRecuperee(), result.get(0).getHeureSupRecuperee());
		assertEquals(ptg5.getIdAgent(), result.get(0).getIdAgent());
		assertEquals(ptg5.getIdPointage(), result.get(0).getIdPointage());
		assertEquals(ptg5.getType().getIdRefTypePointage(), result.get(0).getType().getIdRefTypePointage());
		assertEquals(ptg5.getTypePointageEnum().getValue(), result.get(0).getTypePointageEnum().getValue());

		// assertEquals(ptg2, result.get(1));
		// assertEquals(ptg, result.get(2));
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getRefPrimesWithResult_And_RefPrimesCalculeesWithNoResult() {

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		List<Integer> listRubr = new ArrayList<Integer>();
		listRubr.add(7711);

		List<RefPrime> result = repository.getRefPrimes(listRubr, AgentStatutEnum.F);
		List<RefPrime> noResult = repository.getRefPrimesCalculees(listRubr, AgentStatutEnum.F);

		assertEquals(0, noResult.size());

		assertEquals(1, result.size());
		assertEquals(false, result.get(0).isCalculee());
		assertEquals("Saisir l'heure de début et l'heure de fin du roulement", result.get(0).getAide());
		assertEquals("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM", result.get(0).getLibelle());
		assertEquals(TypeSaisieEnum.NB_INDEMNITES, result.get(0).getTypeSaisie());
		assertEquals(AgentStatutEnum.F, result.get(0).getStatut());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getRefPrimesCalculeesWithResult_And_RefPrimesWithNoResult() {

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(true);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		List<Integer> listRubr = new ArrayList<Integer>();
		listRubr.add(7711);

		List<RefPrime> result = repository.getRefPrimesCalculees(listRubr, AgentStatutEnum.F);
		List<RefPrime> noResult = repository.getRefPrimes(listRubr, AgentStatutEnum.F);

		assertEquals(0, noResult.size());

		assertEquals(1, result.size());
		assertEquals(true, result.get(0).isCalculee());
		assertEquals("Saisir l'heure de début et l'heure de fin du roulement", result.get(0).getAide());
		assertEquals("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM", result.get(0).getLibelle());
		assertEquals(TypeSaisieEnum.NB_INDEMNITES, result.get(0).getTypeSaisie());
		assertEquals(AgentStatutEnum.F, result.get(0).getStatut());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getRefPrimesListForAgent() {

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(true);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		RefPrime rp2 = new RefPrime();
		rp2.setAide("Saisir l'heure de fin");
		rp2.setCalculee(true);
		rp2.setDescription(null);
		rp2.setLibelle("INDEMNITE BIS");
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp2.setNoRubr(7712);
		rp2.setStatut(AgentStatutEnum.F);
		rp2.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		ptgEntityManager.persist(rp2);

		List<RefPrime> result = repository.getRefPrimesListForAgent(AgentStatutEnum.F);

		assertEquals(2, result.size());
		assertEquals(true, result.get(0).isCalculee());
		assertEquals("Saisir l'heure de début et l'heure de fin du roulement", result.get(0).getAide());
		assertEquals("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM", result.get(0).getLibelle());
		assertEquals(TypeSaisieEnum.NB_INDEMNITES, result.get(0).getTypeSaisie());
		assertEquals(AgentStatutEnum.F, result.get(0).getStatut());

		assertEquals(true, result.get(1).isCalculee());
		assertEquals("Saisir l'heure de fin", result.get(1).getAide());
		assertEquals("INDEMNITE BIS", result.get(1).getLibelle());
		assertEquals(TypeSaisieEnum.NB_HEURES, result.get(1).getTypeSaisie());
		assertEquals(AgentStatutEnum.F, result.get(1).getStatut());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getRefPrimesList() {

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(true);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		RefPrime rp2 = new RefPrime();
		rp2.setAide("Saisir l'heure de fin");
		rp2.setCalculee(true);
		rp2.setDescription(null);
		rp2.setLibelle("INDEMNITE BIS");
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp2.setNoRubr(7712);
		rp2.setStatut(AgentStatutEnum.C);
		rp2.setTypeSaisie(TypeSaisieEnum.PERIODE_HEURES);
		ptgEntityManager.persist(rp2);

		List<RefPrime> result = repository.getRefPrimesList();

		assertEquals(2, result.size());
		assertEquals(true, result.get(0).isCalculee());
		assertEquals("Saisir l'heure de début et l'heure de fin du roulement", result.get(0).getAide());
		assertEquals("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM", result.get(0).getLibelle());
		assertEquals(TypeSaisieEnum.NB_INDEMNITES, result.get(0).getTypeSaisie());
		assertEquals(AgentStatutEnum.F, result.get(0).getStatut());

		assertEquals(true, result.get(1).isCalculee());
		assertEquals("Saisir l'heure de fin", result.get(1).getAide());
		assertEquals("INDEMNITE BIS", result.get(1).getLibelle());
		assertEquals(TypeSaisieEnum.PERIODE_HEURES, result.get(1).getTypeSaisie());
		assertEquals(AgentStatutEnum.C, result.get(1).getStatut());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getPointagesForAgentAndDateOrderByIdDesc() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		RefTypeAbsence refTypeAbs = new RefTypeAbsence();
		refTypeAbs.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(refTypeAbs);

		RefTypeAbsence refTypeAbsNon = new RefTypeAbsence();
		refTypeAbsNon.setIdRefTypeAbsence(2);
		ptgEntityManager.persist(refTypeAbsNon);

		Pointage p = new Pointage();
		p.setRefTypeAbsence(refTypeAbsNon);
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p.setHeureSupRecuperee(true);
		p.setIdAgent(9005138);
		p.setType(rtp);
		ptgEntityManager.persist(p);

		Pointage p2 = new Pointage();
		p2.setRefTypeAbsence(refTypeAbs);
		p2.setDateDebut(new LocalDate(2013, 7, 21).toDate());
		p2.setDateFin(new LocalDate(2013, 7, 30).toDate());
		p2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setIdAgent(9005138);
		p2.setType(rtp);
		ptgEntityManager.persist(p2);

		List<Pointage> result = repository.getPointagesForAgentAndDateOrderByIdDesc(9005138,
				new LocalDate(2013, 7, 23).toDate());

		assertEquals(2, result.size());
		assertEquals(RefTypeAbsenceEnum.CONCERTEE.getValue(), result.get(0).getRefTypeAbsence().getIdRefTypeAbsence());
		assertEquals(false, result.get(0).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 21).toDate(), result.get(0).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 30).toDate(), result.get(0).getDateFin());

		assertEquals(RefTypeAbsenceEnum.NON_CONCERTEE.getValue(), result.get(1).getRefTypeAbsence()
				.getIdRefTypeAbsence());
		assertEquals(true, result.get(1).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 22).toDate(), result.get(1).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 29).toDate(), result.get(1).getDateFin());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	// @Test H2 ne prend pas en compte la requete recursive
	@Transactional("ptgTransactionManager")
	public void getPointageArchives() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		RefTypeAbsence refTypeAbsNon = new RefTypeAbsence();
		refTypeAbsNon.setIdRefTypeAbsence(2);
		ptgEntityManager.persist(refTypeAbsNon);

		RefTypeAbsence refTypeAbs = new RefTypeAbsence();
		refTypeAbs.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(refTypeAbs);

		Pointage p = new Pointage();
		p.setRefTypeAbsence(refTypeAbsNon);
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p.setHeureSupRecuperee(true);
		p.setIdAgent(9005138);
		p.setType(rtp);
		ptgEntityManager.persist(p);

		Pointage p2 = new Pointage();
		p2.setPointageParent(p);
		p2.setRefTypeAbsence(refTypeAbs);
		p2.setDateDebut(new LocalDate(2013, 7, 21).toDate());
		p2.setDateFin(new LocalDate(2013, 7, 30).toDate());
		p2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setIdAgent(9005138);
		p2.setType(rtp);
		ptgEntityManager.persist(p2);

		List<Pointage> result = repository.getPointageArchives(p2.getIdPointage());

		assertEquals(2, result.size());
		assertEquals(RefTypeAbsenceEnum.CONCERTEE.getValue(), result.get(0).getRefTypeAbsence().getIdRefTypeAbsence());
		assertEquals(false, result.get(0).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 21).toDate(), result.get(0).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 30).toDate(), result.get(0).getDateFin());

		assertEquals(RefTypeAbsenceEnum.NON_CONCERTEE.getValue(), result.get(1).getRefTypeAbsence()
				.getIdRefTypeAbsence());
		assertEquals(true, result.get(1).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 22).toDate(), result.get(1).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 29).toDate(), result.get(1).getDateFin());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void removePointageCalculesForDateAgent_ok() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		PointageCalcule p = new PointageCalcule();
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p.setIdAgent(9005138);
		p.setType(rtp);
		p.setEtat(EtatPointageEnum.VENTILE);
		ptgEntityManager.persist(p);

		assertEquals(1, repository.removePointageCalculesForDateAgent(9005138, new LocalDate(2013, 7, 23).toDate()));
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void removePointageCalculesForDateAgent_badAgent() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		PointageCalcule p = new PointageCalcule();
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p.setIdAgent(9005138);
		p.setType(rtp);
		p.setEtat(EtatPointageEnum.VENTILE);
		ptgEntityManager.persist(p);

		assertEquals(0, repository.removePointageCalculesForDateAgent(9009999, new LocalDate(2013, 7, 23).toDate()));
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void removePointageCalculesForDateAgent_badEtat() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		PointageCalcule p = new PointageCalcule();
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p.setIdAgent(9005138);
		p.setType(rtp);
		p.setEtat(EtatPointageEnum.VALIDE);
		ptgEntityManager.persist(p);

		assertEquals(0, repository.removePointageCalculesForDateAgent(9005138, new LocalDate(2013, 7, 23).toDate()));
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void removePointageCalculesForDateAgent_badDateLundi() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		PointageCalcule p = new PointageCalcule();
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p.setIdAgent(9005138);
		p.setType(rtp);
		p.setEtat(EtatPointageEnum.VENTILE);
		ptgEntityManager.persist(p);

		assertEquals(0, repository.removePointageCalculesForDateAgent(9005138, new LocalDate(2013, 8, 23).toDate()));
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointages() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		RefTypeAbsence absNonCon = new RefTypeAbsence();
		absNonCon.setIdRefTypeAbsence(2);
		ptgEntityManager.persist(absNonCon);

		RefTypeAbsence absCon = new RefTypeAbsence();
		absCon.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(absCon);

		Pointage p = new Pointage();
		p.setRefTypeAbsence(absNonCon);
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p.setHeureSupRecuperee(true);
		p.setIdAgent(9005138);
		p.setType(rtp);
		ptgEntityManager.persist(p);

		Pointage p2 = new Pointage();
		p2.setRefTypeAbsence(absCon);
		p2.setDateDebut(new LocalDate(2013, 7, 21).toDate());
		p2.setDateFin(new LocalDate(2013, 7, 30).toDate());
		p2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p2.setHeureSupRecuperee(false);
		p2.setIdAgent(9005138);
		p2.setType(rtp);
		ptgEntityManager.persist(p2);

		List<Integer> listAgents = new ArrayList<Integer>();
		listAgents.add(9005138);
		List<Pointage> result = repository.getListPointages(listAgents, new LocalDate(2013, 7, 21).toDate(),
				new LocalDate(2013, 7, 22).toDate(), new Integer(1));

		assertEquals(1, result.size());
		assertEquals(RefTypeAbsenceEnum.CONCERTEE.getValue(), result.get(0).getRefTypeAbsence().getIdRefTypeAbsence());
		assertEquals(false, result.get(0).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 21).toDate(), result.get(0).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 30).toDate(), result.get(0).getDateFin());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getPointagesVentilesForAgent() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		RefTypeAbsence refTypeAbsNon = new RefTypeAbsence();
		refTypeAbsNon.setIdRefTypeAbsence(2);
		ptgEntityManager.persist(refTypeAbsNon);

		Pointage p = new Pointage();
		p.setRefTypeAbsence(refTypeAbsNon);
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p.setHeureSupRecuperee(true);
		p.setIdAgent(9005138);
		p.setType(rtp);
		ptgEntityManager.persist(p);

		Set<Pointage> pointages = new HashSet<Pointage>();
		pointages.add(p);

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setPointages(pointages);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		List<Pointage> result = repository.getPointagesVentilesForAgent(new Integer(9005138), vd.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(RefTypeAbsenceEnum.NON_CONCERTEE.getValue(), result.get(0).getRefTypeAbsence()
				.getIdRefTypeAbsence());
		assertEquals(true, result.get(0).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 22).toDate(), result.get(0).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 29).toDate(), result.get(0).getDateFin());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getPointagesCalculesVentilesForAgent() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		PointageCalcule pc = new PointageCalcule();
		pc.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		pc.setDateFin(new LocalDate(2013, 7, 29).toDate());
		pc.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		pc.setEtat(EtatPointageEnum.VENTILE);
		pc.setIdAgent(9005138);
		pc.setLastVentilDate(vd);
		pc.setQuantite(1);
		pc.setRefPrime(rp);
		pc.setType(rtp);
		ptgEntityManager.persist(pc);

		List<PointageCalcule> result = repository.getPointagesCalculesVentilesForAgent(new Integer(9005138),
				vd.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(new Integer(1), result.get(0).getQuantite());
		assertEquals(new LocalDate(2013, 7, 22).toDate(), result.get(0).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 29).toDate(), result.get(0).getDateFin());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getPointagesCalculesVentilesForAgent_badEtat() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		PointageCalcule pc = new PointageCalcule();
		pc.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		pc.setDateFin(new LocalDate(2013, 7, 29).toDate());
		pc.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		pc.setEtat(EtatPointageEnum.VALIDE);
		pc.setIdAgent(9005138);
		pc.setLastVentilDate(vd);
		pc.setQuantite(1);
		pc.setRefPrime(rp);
		pc.setType(rtp);
		ptgEntityManager.persist(pc);

		List<PointageCalcule> result = repository.getPointagesCalculesVentilesForAgent(new Integer(9005138),
				vd.getIdVentilDate());

		assertEquals(0, result.size());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getRefPrimesListWithNoRubr() {

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(true);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		RefPrime rp2 = new RefPrime();
		rp2.setAide("Saisir l'heure de fin");
		rp2.setCalculee(true);
		rp2.setDescription(null);
		rp2.setLibelle("INDEMNITE BIS");
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp2.setNoRubr(7712);
		rp2.setStatut(AgentStatutEnum.C);
		rp2.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		ptgEntityManager.persist(rp2);

		List<RefPrime> result = repository.getRefPrimesListWithNoRubr(new Integer(7711));

		assertEquals(1, result.size());
		assertEquals(true, result.get(0).isCalculee());
		assertEquals("Saisir l'heure de début et l'heure de fin du roulement", result.get(0).getAide());
		assertEquals("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM", result.get(0).getLibelle());
		assertEquals(AgentStatutEnum.F, result.get(0).getStatut());
		assertEquals(TypeSaisieEnum.NB_INDEMNITES, result.get(0).getTypeSaisie());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getRefPrimesListWithNoRubr_NoResult() {

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(true);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		RefPrime rp2 = new RefPrime();
		rp2.setAide("Saisir l'heure de fin");
		rp2.setCalculee(true);
		rp2.setDescription(null);
		rp2.setLibelle("INDEMNITE BIS");
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp2.setNoRubr(7712);
		rp2.setStatut(AgentStatutEnum.C);
		rp2.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		ptgEntityManager.persist(rp2);

		List<RefPrime> result = repository.getRefPrimesListWithNoRubr(new Integer(7709));

		assertEquals(0, result.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void isPrimeSurPointageouPointageCalcule_PointageCalcule() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		PointageCalcule pc = new PointageCalcule();
		pc.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		pc.setDateFin(new LocalDate(2013, 7, 29).toDate());
		pc.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		pc.setEtat(EtatPointageEnum.APPROUVE);
		pc.setIdAgent(9005138);
		pc.setLastVentilDate(vd);
		pc.setQuantite(1);
		pc.setRefPrime(rp);
		pc.setType(rtp);
		ptgEntityManager.persist(pc);

		boolean result = repository.isPrimeSurPointageouPointageCalcule(new Integer(9005138), rp.getIdRefPrime());

		assertTrue(result);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void isPrimeSurPointageouPointageCalcule_Pointage() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 24).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		Pointage p = new Pointage();
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p.setIdAgent(9005139);
		p.setQuantite(1);
		p.setRefPrime(rp);
		p.setType(rtp);
		ptgEntityManager.persist(p);

		boolean result = repository.isPrimeSurPointageouPointageCalcule(new Integer(9005139), rp.getIdRefPrime());

		assertTrue(result);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void isPrimeSurPointageouPointageCalcule_NoResult() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		ptgEntityManager.persist(rtp);

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		Pointage p = new Pointage();
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		p.setIdAgent(9005138);
		p.setQuantite(1);
		p.setRefPrime(rp);
		p.setType(rtp);
		ptgEntityManager.persist(p);

		boolean result = repository.isPrimeSurPointageouPointageCalcule(new Integer(9005138), 100);

		assertFalse(result);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void listAllDistinctIdAgentPointage_ReturnEmptyList() {

		List<Integer> noResult = repository.listAllDistinctIdAgentPointage();

		assertEquals(0, noResult.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void listAllDistinctIdAgentPointage_ReturnList() {

		Pointage rp = new Pointage();
		rp.setIdAgent(9005138);
		ptgEntityManager.persist(rp);

		Pointage rp2 = new Pointage();
		rp2.setIdAgent(9005138);
		ptgEntityManager.persist(rp2);

		Pointage rp3 = new Pointage();
		rp3.setIdAgent(9005131);
		ptgEntityManager.persist(rp3);

		List<Integer> result = repository.listAllDistinctIdAgentPointage();

		assertEquals(2, result.size());
		assertEquals(new Integer(9005138), result.get(0));
		assertEquals(new Integer(9005131), result.get(1));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void findAllRefTypeAbsence() {

		RefTypeAbsence rta = new RefTypeAbsence();
		rta.setIdRefTypeAbsence(RefTypeAbsenceEnum.CONCERTEE.getValue());
		ptgEntityManager.persist(rta);

		RefTypeAbsence rta2 = new RefTypeAbsence();
		rta2.setIdRefTypeAbsence(RefTypeAbsenceEnum.NON_CONCERTEE.getValue());
		ptgEntityManager.persist(rta2);

		RefTypeAbsence rta3 = new RefTypeAbsence();
		rta3.setIdRefTypeAbsence(RefTypeAbsenceEnum.IMMEDIATE.getValue());
		ptgEntityManager.persist(rta3);

		List<RefTypeAbsence> result = repository.findAllRefTypeAbsence();

		assertEquals(3, result.size());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListApprobateursPointagesSaisiesJourDonne() {

		/* APPRO 2 avec pointage parent */

		DroitsAgent droitsAgent2 = new DroitsAgent();
		droitsAgent2.setIdAgent(9000012);
		ptgEntityManager.persist(droitsAgent2);

		Set<DroitsAgent> droitDroitsAgent2 = new HashSet<DroitsAgent>();
		droitDroitsAgent2.add(droitsAgent2);

		Droit droitApprobateur2 = new Droit();
		droitApprobateur2.setIdAgent(9000002);
		droitApprobateur2.setAgents(droitDroitsAgent2);
		droitApprobateur2.setApprobateur(true);
		ptgEntityManager.persist(droitApprobateur2);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9000012);
		ptgEntityManager.persist(ptg2);

		EtatPointage etatDemande2 = new EtatPointage();
		etatDemande2.setPointage(ptg2);
		etatDemande2.setDateEtat(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		etatDemande2.setDateMaj(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		etatDemande2.setIdAgent(9000003);
		etatDemande2.setEtat(EtatPointageEnum.APPROUVE);
		ptgEntityManager.persist(etatDemande2);

		Pointage ptg3 = new Pointage();
		ptg3.setIdAgent(9000012);
		ptg3.setPointageParent(ptg2);
		ptgEntityManager.persist(ptg3);

		EtatPointage etatDemande3 = new EtatPointage();
		etatDemande3.setPointage(ptg3);
		etatDemande3.setDateEtat(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		etatDemande3.setDateMaj(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		etatDemande3.setIdAgent(9000002);
		etatDemande3.setEtat(EtatPointageEnum.SAISI);
		ptgEntityManager.persist(etatDemande3);

		/* APPRO 1 avec 1 PTG */
		DroitsAgent droitsAgent = new DroitsAgent();
		droitsAgent.setIdAgent(9000011);
		ptgEntityManager.persist(droitsAgent);

		Set<DroitsAgent> droitDroitsAgent = new HashSet<DroitsAgent>();
		droitDroitsAgent.add(droitsAgent);

		Droit droitApprobateur = new Droit();
		droitApprobateur.setIdAgent(9000001);
		droitApprobateur.setAgents(droitDroitsAgent);
		droitApprobateur.setApprobateur(true);
		ptgEntityManager.persist(droitApprobateur);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9000011);
		ptgEntityManager.persist(ptg);

		EtatPointage etatDemande = new EtatPointage();
		etatDemande.setPointage(ptg);
		etatDemande.setDateEtat(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		etatDemande.setDateMaj(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		etatDemande.setIdAgent(9000001);
		etatDemande.setEtat(EtatPointageEnum.SAISI);
		ptgEntityManager.persist(etatDemande);

		// When
		List<Integer> result = repository.getListApprobateursPointagesSaisiesJourDonne();

		assertEquals(2, result.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void findAllMotifHeureSup() {

		MotifHeureSup rta = new MotifHeureSup();
		rta.setIdMotifHsup(1);
		ptgEntityManager.persist(rta);

		MotifHeureSup rta2 = new MotifHeureSup();
		rta2.setIdMotifHsup(2);
		ptgEntityManager.persist(rta2);

		MotifHeureSup rta3 = new MotifHeureSup();
		rta3.setIdMotifHsup(3);
		ptgEntityManager.persist(rta3);

		List<MotifHeureSup> result = repository.findAllMotifHeureSup();

		assertEquals(3, result.size());
	}

}
