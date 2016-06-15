package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.MairiePrimeTableEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.domain.VentilTask;

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
public class VentilationRepositoryTest {

	@Autowired
	VentilationRepository repository;

	@PersistenceContext(unitName = "ptgPersistenceUnit")
	EntityManager ptgEntityManager;

	@Test
	@Transactional("ptgTransactionManager")
	public void getDistinctDatesOfPointages() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		ptgEntityManager.persist(rtp);

		RefTypeAbsence refTypeAbs = new RefTypeAbsence();
		refTypeAbs.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(refTypeAbs);

		// ok
		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008761);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(refTypeAbs);
		ptg.setType(rtp);
		ptgEntityManager.persist(ptg);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ep.setIdAgent(9008761);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		RefTypePointage rtp2 = new RefTypePointage();
		rtp2.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtp2);

		RefTypeAbsence refTypeAbsNon = new RefTypeAbsence();
		refTypeAbsNon.setIdRefTypeAbsence(2);
		ptgEntityManager.persist(refTypeAbsNon);

		// ok
		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008761);
		ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg2.setRefTypeAbsence(refTypeAbsNon);
		ptg2.setType(rtp2);
		ptgEntityManager.persist(ptg2);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep2.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep2.setEtat(EtatPointageEnum.VENTILE);
		ep2.setIdAgent(9008761);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		// etat different
		Pointage ptg3 = new Pointage();
		ptg3.setIdAgent(9008761);
		ptg3.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg3.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg3.setRefTypeAbsence(refTypeAbsNon);
		ptg3.setType(rtp);
		ptgEntityManager.persist(ptg3);

		EtatPointage ep3 = new EtatPointage();
		ep3.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep3.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep3.setEtat(EtatPointageEnum.EN_ATTENTE);
		ep3.setIdAgent(9008761);
		ep3.setPointage(ptg3);
		ptgEntityManager.persist(ep3);

		// id agent différent
		Pointage ptg4 = new Pointage();
		ptg4.setIdAgent(9008762);
		ptg4.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg4.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg4.setRefTypeAbsence(refTypeAbsNon);
		ptg4.setType(rtp);
		ptgEntityManager.persist(ptg4);

		EtatPointage ep4 = new EtatPointage();
		ep4.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep4.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep4.setEtat(EtatPointageEnum.APPROUVE);
		ep4.setIdAgent(9008762);
		ep4.setPointage(ptg4);
		ptgEntityManager.persist(ep4);

		// hors date
		Pointage ptg5 = new Pointage();
		ptg5.setIdAgent(9008761);
		ptg5.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg5.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg5.setRefTypeAbsence(refTypeAbsNon);
		ptg5.setType(rtp);
		ptgEntityManager.persist(ptg5);

		EtatPointage ep5 = new EtatPointage();
		ep5.setDateEtat(new LocalDate(2013, 7, 29).toDate());
		ep5.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep5.setEtat(EtatPointageEnum.APPROUVE);
		ep5.setIdAgent(9008761);
		ep5.setPointage(ptg5);
		ptgEntityManager.persist(ep5);

		List<Date> result = repository.getDistinctDatesOfPointages(new Integer(9008761),
				new LocalDate(2013, 7, 24).toDate(), new LocalDate(2013, 7, 26).toDate());

		assertEquals(2, result.size());
		assertEquals(new DateTime(2013, 7, 22, 8, 0, 0).toDate(), result.get(0));
		assertEquals(new DateTime(2013, 7, 23, 8, 0, 0).toDate(), result.get(1));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListIdAgentsForVentilationByDateAndEtat() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		ptgEntityManager.persist(rtp);

		RefTypeAbsence refTypeAbs = new RefTypeAbsence();
		refTypeAbs.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(refTypeAbs);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008761);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(refTypeAbs);
		ptg.setType(rtp);
		ptgEntityManager.persist(ptg);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ep.setIdAgent(9008761);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		RefTypePointage rtp2 = new RefTypePointage();
		rtp2.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtp2);

		RefTypeAbsence refTypeAbsNon = new RefTypeAbsence();
		refTypeAbsNon.setIdRefTypeAbsence(2);
		ptgEntityManager.persist(refTypeAbsNon);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008762);
		ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg2.setRefTypeAbsence(refTypeAbsNon);
		ptg2.setType(rtp2);
		ptgEntityManager.persist(ptg2);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep2.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep2.setEtat(EtatPointageEnum.VENTILE);
		ep2.setIdAgent(9008762);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		Pointage ptg3 = new Pointage();
		ptg3.setIdAgent(9008763);
		ptg3.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg3.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg3.setRefTypeAbsence(refTypeAbsNon);
		ptg3.setType(rtp);
		ptgEntityManager.persist(ptg3);

		EtatPointage ep3 = new EtatPointage();
		ep3.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep3.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep3.setEtat(EtatPointageEnum.EN_ATTENTE);
		ep3.setIdAgent(9008763);
		ep3.setPointage(ptg3);
		ptgEntityManager.persist(ep3);

		Pointage ptg4 = new Pointage();
		ptg4.setIdAgent(9008764);
		ptg4.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg4.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg4.setRefTypeAbsence(refTypeAbsNon);
		ptg4.setType(rtp);
		ptgEntityManager.persist(ptg4);

		EtatPointage ep4 = new EtatPointage();
		ep4.setDateEtat(new LocalDate(2013, 7, 29).toDate());
		ep4.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep4.setEtat(EtatPointageEnum.APPROUVE);
		ep4.setIdAgent(9008764);
		ep4.setPointage(ptg4);
		ptgEntityManager.persist(ep4);

		List<Integer> result = repository.getListIdAgentsForVentilationByDateAndEtat(
				new LocalDate(2013, 7, 24).toDate(), new LocalDate(2013, 7, 26).toDate());

		assertEquals(2, result.size());
		assertEquals(9008761, result.get(0).intValue());
		assertEquals(9008762, result.get(1).intValue());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	// bug #15212 : probleme avec pointage parent 
	// cas concret : donnees reprises de la recette
	@Test
	@Transactional("ptgTransactionManager")
	public void getListIdAgentsForVentilationByDateAndEtat_WithPointageParent() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		ptgEntityManager.persist(rtp);

		RefTypeAbsence refTypeAbs = new RefTypeAbsence();
		refTypeAbs.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(refTypeAbs);

		Pointage pointageParent = new Pointage();
		pointageParent.setIdAgent(9004432);
		pointageParent.setDateLundi(new LocalDate(2015, 3, 2).toDate());
		pointageParent.setDateDebut(new DateTime(2015, 3, 4, 6, 0, 0).toDate());
		pointageParent.setDateFin(new DateTime(2015, 3, 4, 13, 0, 0).toDate());
		pointageParent.setRefTypeAbsence(refTypeAbs);
		pointageParent.setType(rtp);
		ptgEntityManager.persist(pointageParent);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2015, 3, 31).toDate());
		ep.setDateMaj(new LocalDate(2015, 3, 31).toDate());
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ep.setIdAgent(9005138);
		ep.setPointage(pointageParent);
		ptgEntityManager.persist(ep);

		// 1er test sans pointage parent
		List<Integer> result = repository.getListIdAgentsForVentilationByDateAndEtat(
				new LocalDate(2015, 3, 15).toDate(), new LocalDate(2015, 4, 12).toDate());

		assertEquals(1, result.size());

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9004432);
		ptg2.setDateLundi(new LocalDate(2015, 3, 2).toDate());
		ptg2.setDateDebut(new DateTime(2015, 3, 4, 7, 45, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 3, 4, 13, 0, 0).toDate());
		ptg2.setRefTypeAbsence(refTypeAbs);
		ptg2.setPointageParent(pointageParent);
		ptg2.setType(rtp);
		ptgEntityManager.persist(ptg2);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2015, 4, 16).toDate());
		ep2.setDateMaj(new LocalDate(2015, 4, 16).toDate());
		ep2.setEtat(EtatPointageEnum.VALIDE);
		ep2.setIdAgent(9005138);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		List<Integer> resultAvecPointageParent = repository.getListIdAgentsForVentilationByDateAndEtat(
				new LocalDate(2015, 3, 15).toDate(), new LocalDate(2015, 4, 12).toDate());

		assertEquals(0, resultAvecPointageParent.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	// @Test
	@Transactional("ptgTransactionManager")
	public void getListIdAgentsForExportPaie() {

		// Pointage ptg = new Pointage();
		// ptg.setIdAgent(9008765);
		// ptg.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		// ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		//
		// Set<Pointage> pointages = new HashSet<Pointage>();
		// pointages.add(ptg);
		//
		// VentilDate vd = new VentilDate();
		// vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		// vd.setPaye(true);
		// vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		// vd.setPointages(pointages);
		// vd.persist();
		//
		// List<Integer> result =
		// repository.getListIdAgentsForExportPaie(vd.getIdVentilDate());
		//
		// assertEquals(1, result.size());
		// assertEquals(9008765, result.get(0).intValue());
		//
		// ptgEntityManager.flush();
		// ptgEntityManager.clear();

		// TODO pas insert dans la table de relation PTG_POINTAGE_VENTIL_DATE
		// avec VentilDate.persist
		// du coup pas de resultat
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesAbsenceAndHSupForVentilation() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		ptgEntityManager.persist(rtp);

		RefTypeAbsence typeAbs = new RefTypeAbsence();
		typeAbs.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(typeAbs);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008765);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(typeAbs);
		ptg.setType(rtp);
		ptgEntityManager.persist(ptg);

		RefTypePointage rtp2 = new RefTypePointage();
		rtp2.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtp2);

		RefTypeAbsence typeAbsNon = new RefTypeAbsence();
		typeAbsNon.setIdRefTypeAbsence(2);
		ptgEntityManager.persist(typeAbsNon);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008765);
		ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg2.setRefTypeAbsence(typeAbsNon);
		ptg2.setType(rtp2);
		ptgEntityManager.persist(ptg2);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ep.setIdAgent(9008765);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep2.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep2.setEtat(EtatPointageEnum.APPROUVE);
		ep2.setIdAgent(9008765);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		Pointage ptg3 = new Pointage();
		ptg3.setIdAgent(9008765);
		ptg3.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg3.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg3.setRefTypeAbsence(typeAbsNon);
		ptg3.setType(rtp);
		ptgEntityManager.persist(ptg3);

		EtatPointage ep3 = new EtatPointage();
		ep3.setDateEtat(new LocalDate(2013, 7, 29).toDate());
		ep3.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep3.setEtat(EtatPointageEnum.APPROUVE);
		ep3.setIdAgent(9008765);
		ep3.setPointage(ptg3);
		ptgEntityManager.persist(ep3);

		List<Pointage> result = repository.getListPointagesAbsenceAndHSupForVentilation(9008765, new LocalDate(2013, 7,
				24).toDate(), new LocalDate(2013, 7, 26).toDate(), new LocalDate(2013, 7, 20).toDate());

		assertEquals(2, result.size());

		assertEquals(new DateTime(2013, 7, 23, 8, 0, 0).toDate(), result.get(0).getDateDebut());
		assertEquals(2, (int) result.get(0).getRefTypeAbsence().getIdRefTypeAbsence());
		assertEquals(RefTypePointageEnum.ABSENCE, result.get(0).getTypePointageEnum());

		assertEquals(new DateTime(2013, 7, 22, 8, 0, 0).toDate(), result.get(1).getDateDebut());
		assertEquals(1, (int) result.get(1).getRefTypeAbsence().getIdRefTypeAbsence());
		assertEquals(RefTypePointageEnum.H_SUP, result.get(1).getTypePointageEnum());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	// #18186
	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesAbsenceAndHSupForVentilation_bug18186() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		ptgEntityManager.persist(rtp);

		Pointage ptg1 = new Pointage();
		ptg1.setIdAgent(9003388);
		ptg1.setDateLundi(new LocalDate(2015, 7, 27).toDate());
		ptg1.setDateDebut(new DateTime(2015, 7, 31, 4, 0, 0).toDate());
		ptg1.setDateFin(new DateTime(2015, 7, 31, 6, 30, 0).toDate());
		ptg1.setType(rtp);

		EtatPointage ep1 = new EtatPointage();
		ep1.setDateEtat(new LocalDate(2015, 8, 30).toDate());
		ep1.setDateMaj(new LocalDate(2015, 8, 30).toDate());
		ep1.setEtat(EtatPointageEnum.APPROUVE);
		ep1.setIdAgent(9003069);
		ep1.setPointage(ptg1);
		ptgEntityManager.persist(ep1);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9003388);
		ptg2.setDateLundi(new LocalDate(2015, 7, 27).toDate());
		ptg2.setDateDebut(new DateTime(2015, 7, 31, 4, 0, 0).toDate());
		ptg2.setDateFin(new DateTime(2015, 7, 31, 6, 15, 0).toDate());
		ptg2.setType(rtp);
		ptg1.setPointageParent(ptg2);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2015, 8, 30).toDate());
		ep2.setDateMaj(new LocalDate(2015, 8, 30).toDate());
		ep2.setEtat(EtatPointageEnum.APPROUVE);
		ep2.setIdAgent(9003069);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		Pointage ptg3 = new Pointage();
		ptg3.setIdAgent(9003388);
		ptg3.setDateLundi(new LocalDate(2015, 7, 27).toDate());
		ptg3.setDateDebut(new DateTime(2015, 7, 31, 23, 0, 0).toDate());
		ptg3.setDateFin(new DateTime(2015, 8, 1, 4, 0, 0).toDate());
		ptg3.setType(rtp);

		EtatPointage ep3 = new EtatPointage();
		ep3.setDateEtat(new LocalDate(2015, 8, 30).toDate());
		ep3.setDateMaj(new LocalDate(2015, 8, 30).toDate());
		ep3.setEtat(EtatPointageEnum.APPROUVE);
		ep3.setIdAgent(9003069);
		ep3.setPointage(ptg3);
		ptgEntityManager.persist(ep3);

		Pointage ptg4 = new Pointage();
		ptg4.setIdAgent(9003388);
		ptg4.setDateLundi(new LocalDate(2015, 7, 27).toDate());
		ptg4.setDateDebut(new DateTime(2015, 8, 1, 4, 0, 0).toDate());
		ptg4.setDateFin(new DateTime(2015, 8, 1, 6, 15, 0).toDate());
		ptg4.setType(rtp);

		EtatPointage ep4 = new EtatPointage();
		ep4.setDateEtat(new LocalDate(2015, 8, 30).toDate());
		ep4.setDateMaj(new LocalDate(2015, 8, 30).toDate());
		ep4.setEtat(EtatPointageEnum.APPROUVE);
		ep4.setIdAgent(9003069);
		ep4.setPointage(ptg4);
		ptgEntityManager.persist(ep4);

		Pointage ptg5 = new Pointage();
		ptg5.setIdAgent(9003388);
		ptg5.setDateLundi(new LocalDate(2015, 7, 27).toDate());
		ptg5.setDateDebut(new DateTime(2015, 8, 1, 23, 0, 0).toDate());
		ptg5.setDateFin(new DateTime(2015, 8, 2, 4, 0, 0).toDate());
		ptg5.setType(rtp);

		EtatPointage ep5 = new EtatPointage();
		ep5.setDateEtat(new LocalDate(2015, 8, 30).toDate());
		ep5.setDateMaj(new LocalDate(2015, 8, 30).toDate());
		ep5.setEtat(EtatPointageEnum.APPROUVE);
		ep5.setIdAgent(9003069);
		ep5.setPointage(ptg5);
		ptgEntityManager.persist(ep5);

		Pointage ptg6 = new Pointage();
		ptg6.setIdAgent(9003388);
		ptg6.setDateLundi(new LocalDate(2015, 7, 27).toDate());
		ptg6.setDateDebut(new DateTime(2015, 7, 31, 23, 0, 0).toDate());
		ptg6.setDateFin(new DateTime(2015, 7, 31, 4, 0, 0).toDate());
		ptg6.setType(rtp);
		ptg3.setPointageParent(ptg6);

		EtatPointage ep6 = new EtatPointage();
		ep6.setDateEtat(new LocalDate(2015, 8, 30).toDate());
		ep6.setDateMaj(new LocalDate(2015, 8, 30).toDate());
		ep6.setEtat(EtatPointageEnum.APPROUVE);
		ep6.setIdAgent(9003069);
		ep6.setPointage(ptg6);
		ptgEntityManager.persist(ep6);

		Pointage ptg7 = new Pointage();
		ptg7.setIdAgent(9003388);
		ptg7.setDateLundi(new LocalDate(2015, 7, 27).toDate());
		ptg7.setDateDebut(new DateTime(2015, 7, 31, 4, 0, 0).toDate());
		ptg7.setDateFin(new DateTime(2015, 7, 31, 6, 30, 0).toDate());
		ptg7.setType(rtp);
		ptg2.setPointageParent(ptg7);

		EtatPointage ep7 = new EtatPointage();
		ep7.setDateEtat(new LocalDate(2015, 8, 30).toDate());
		ep7.setDateMaj(new LocalDate(2015, 8, 30).toDate());
		ep7.setEtat(EtatPointageEnum.APPROUVE);
		ep7.setIdAgent(9003069);
		ep7.setPointage(ptg7);
		ptgEntityManager.persist(ep7);
		
		ptgEntityManager.persist(ptg7);
		ptgEntityManager.persist(ptg6);
		ptgEntityManager.persist(ptg5);
		ptgEntityManager.persist(ptg4);
		ptgEntityManager.persist(ptg3);
		ptgEntityManager.persist(ptg2);
		ptgEntityManager.persist(ptg1);

		List<Pointage> result = repository.getListPointagesAbsenceAndHSupForVentilation(9003388, new LocalDate(2015, 8,
				2).toDate(), new LocalDate(2015, 8, 30).toDate(), new LocalDate(2015, 7, 27).toDate());

		assertEquals(4, result.size());

		assertEquals(new DateTime(2015, 7, 31, 4, 0, 0).toDate(), result.get(0).getDateDebut());
		assertEquals(RefTypePointageEnum.H_SUP, result.get(0).getTypePointageEnum());

		assertEquals(new DateTime(2015, 7, 31, 23, 0, 0).toDate(), result.get(1).getDateDebut());
		assertEquals(RefTypePointageEnum.H_SUP, result.get(1).getTypePointageEnum());

		assertEquals(new DateTime(2015, 8, 1, 4, 0, 0).toDate(), result.get(2).getDateDebut());
		assertEquals(RefTypePointageEnum.H_SUP, result.get(2).getTypePointageEnum());

		assertEquals(new DateTime(2015, 8, 1, 23, 0, 0).toDate(), result.get(3).getDateDebut());
		assertEquals(RefTypePointageEnum.H_SUP, result.get(3).getTypePointageEnum());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeByWeekForVentilation_0resultBecauseHSupAndAbs() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		ptgEntityManager.persist(rtp);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008765);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setType(rtp);
		ptgEntityManager.persist(ptg);

		RefTypePointage rtp2 = new RefTypePointage();
		rtp2.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtp2);

		RefTypeAbsence typeAbsNon = new RefTypeAbsence();
		typeAbsNon.setIdRefTypeAbsence(2);
		ptgEntityManager.persist(typeAbsNon);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008765);
		ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg2.setRefTypeAbsence(typeAbsNon);
		ptg2.setType(rtp2);
		ptgEntityManager.persist(ptg2);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ep.setIdAgent(9008765);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep2.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep2.setEtat(EtatPointageEnum.APPROUVE);
		ep2.setIdAgent(9008765);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		Pointage ptg3 = new Pointage();
		ptg3.setIdAgent(9008765);
		ptg3.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg3.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg3.setRefTypeAbsence(typeAbsNon);
		ptg3.setType(rtp);
		ptgEntityManager.persist(ptg3);

		EtatPointage ep3 = new EtatPointage();
		ep3.setDateEtat(new LocalDate(2013, 7, 26).toDate());
		ep3.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep3.setEtat(EtatPointageEnum.APPROUVE);
		ep3.setIdAgent(9008765);
		ep3.setPointage(ptg3);
		ptgEntityManager.persist(ep3);

		List<Pointage> result = repository.getListPointagesPrimeByWeekForVentilation(9008765, new LocalDate(2013, 7,
				24).toDate(), new LocalDate(2013, 7, 26).toDate(), new LocalDate(2013, 7, 20).toDate());

		assertEquals(0, result.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeByWeekForVentilation_0resultBecauseBadAgent() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(rtp);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008765);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setType(rtp);
		ptgEntityManager.persist(ptg);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008765);
		ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg2.setType(rtp);
		ptgEntityManager.persist(ptg2);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ep.setIdAgent(9008765);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep2.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep2.setEtat(EtatPointageEnum.APPROUVE);
		ep2.setIdAgent(9008765);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		Pointage ptg3 = new Pointage();
		ptg3.setIdAgent(9008765);
		ptg3.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg3.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg3.setType(rtp);
		ptgEntityManager.persist(ptg3);

		EtatPointage ep3 = new EtatPointage();
		ep3.setDateEtat(new LocalDate(2013, 7, 26).toDate());
		ep3.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep3.setEtat(EtatPointageEnum.APPROUVE);
		ep3.setIdAgent(9008765);
		ep3.setPointage(ptg3);
		ptgEntityManager.persist(ep3);

		List<Pointage> result = repository.getListPointagesPrimeByWeekForVentilation(9009999, new LocalDate(2013, 7,
				24).toDate(), new LocalDate(2013, 7, 26).toDate(), new LocalDate(2013, 7, 20).toDate());

		assertEquals(0, result.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeByWeekForVentilation_0resultBecauseBadEtat() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(rtp);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008765);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setType(rtp);
		ptgEntityManager.persist(ptg);

		for(int i=0; i<10; i++) {
			EtatPointageEnum etat = EtatPointageEnum.getEtatPointageEnum(i);
			
			EtatPointage ep = new EtatPointage();
			ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
			ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
			ep.setEtat(etat);
			ep.setIdAgent(9008765);
			ep.setPointage(ptg);
			ptgEntityManager.persist(ep);
			
			List<Pointage> result = repository.getListPointagesPrimeByWeekForVentilation(9008765, new LocalDate(2013, 7,
					24).toDate(), new LocalDate(2013, 7, 26).toDate(), new LocalDate(2013, 7, 20).toDate());

			if(etat.equals(EtatPointageEnum.APPROUVE) || etat.equals(EtatPointageEnum.VALIDE) || etat.equals(EtatPointageEnum.VENTILE)) {
				assertEquals(1, result.size());
			}else{
				assertEquals(0, result.size());
			}
		}

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeByWeekForVentilation_2resultsAnd1BadBecauseBadDateLundi() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(rtp);

		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);
		ptgEntityManager.persist(refPrime);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008765);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefPrime(refPrime);
		ptg.setType(rtp);
		ptg.setQuantite(10);
		ptgEntityManager.persist(ptg);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008765);
		ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg2.setRefPrime(refPrime);
		ptg2.setType(rtp);
		ptg2.setQuantite(20);
		ptgEntityManager.persist(ptg2);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ep.setIdAgent(9008765);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep2.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep2.setEtat(EtatPointageEnum.APPROUVE);
		ep2.setIdAgent(9008765);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		Pointage ptg3 = new Pointage();
		ptg3.setIdAgent(9008765);
		ptg3.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg3.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg3.setRefPrime(refPrime);
		ptg3.setType(rtp);
		ptg3.setQuantite(30);
		ptgEntityManager.persist(ptg3);

		EtatPointage ep3 = new EtatPointage();
		ep3.setDateEtat(new LocalDate(2013, 7, 29).toDate());
		ep3.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep3.setEtat(EtatPointageEnum.APPROUVE);
		ep3.setIdAgent(9008765);
		ep3.setPointage(ptg3);
		ptgEntityManager.persist(ep3);

		List<Pointage> result = repository.getListPointagesPrimeByWeekForVentilation(9008765, new LocalDate(2013, 7,
				24).toDate(), new LocalDate(2013, 7, 26).toDate(), new LocalDate(2013, 7, 20).toDate());

		assertEquals(2, result.size());
		
		assertEquals(new DateTime(2013, 7, 23, 8, 0, 0).toDate(), result.get(0).getDateDebut());
		assertEquals(20, (int) result.get(0).getQuantite());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());

		assertEquals(new DateTime(2013, 7, 22, 8, 0, 0).toDate(), result.get(1).getDateDebut());
		assertEquals(10, (int) result.get(1).getQuantite());
		assertEquals(RefTypePointageEnum.PRIME, result.get(1).getTypePointageEnum());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeForVentilation() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(rtp);

		RefTypeAbsence refTypeAbs = new RefTypeAbsence();
		refTypeAbs.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(refTypeAbs);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008765);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(refTypeAbs);
		ptg.setType(rtp);
		ptgEntityManager.persist(ptg);

		RefTypePointage rtp2 = new RefTypePointage();
		rtp2.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtp2);

		RefTypeAbsence refTypeAbsNon = new RefTypeAbsence();
		refTypeAbsNon.setIdRefTypeAbsence(2);
		ptgEntityManager.persist(refTypeAbsNon);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008765);
		ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg2.setRefTypeAbsence(refTypeAbsNon);
		ptg2.setType(rtp2);
		ptgEntityManager.persist(ptg2);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ep.setIdAgent(9008765);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep2.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep2.setEtat(EtatPointageEnum.APPROUVE);
		ep2.setIdAgent(9008765);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		EtatPointage ep3 = new EtatPointage();
		ep3.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep3.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep3.setEtat(EtatPointageEnum.APPROUVE);
		ep3.setIdAgent(9008765);
		ep3.setPointage(ptg);
		ptgEntityManager.persist(ep3);

		List<Pointage> result = repository.getListPointagesPrimeForVentilation(9008765,
				new LocalDate(2013, 7, 24).toDate(), new LocalDate(2013, 7, 26).toDate(),
				new LocalDate(2013, 7, 20).toDate());

		assertEquals(1, result.size());
		assertEquals(new DateTime(2013, 7, 22, 8, 0, 0).toDate(), result.get(0).getDateDebut());
		assertEquals(1, (int) result.get(0).getRefTypeAbsence().getIdRefTypeAbsence());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	// bug PROD #31382
	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeForVentilation_bug31382() {

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(rtp);

		RefTypeAbsence refTypeAbs = new RefTypeAbsence();
		refTypeAbs.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(refTypeAbs);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008765);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(refTypeAbs);
		ptg.setType(rtp);
		ptgEntityManager.persist(ptg);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ep.setIdAgent(9008765);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		EtatPointage ep3 = new EtatPointage();
		ep3.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep3.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep3.setEtat(EtatPointageEnum.APPROUVE);
		ep3.setIdAgent(9008765);
		ep3.setPointage(ptg);
		ptgEntityManager.persist(ep3);

		// pointage de juillet mais un an avant
		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008765);
		ptg2.setDateLundi(new LocalDate(2012, 7, 20).toDate());
		ptg2.setDateDebut(new DateTime(2012, 7, 22, 8, 0, 0).toDate());
		ptg2.setRefTypeAbsence(refTypeAbs);
		ptg2.setType(rtp);
		ptgEntityManager.persist(ptg2);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2012, 7, 25).toDate());
		ep2.setDateMaj(new LocalDate(2012, 7, 22).toDate());
		ep2.setEtat(EtatPointageEnum.JOURNALISE);
		ep2.setIdAgent(9008765);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		List<Pointage> result = repository.getListPointagesPrimeForVentilation(9008765,
				new LocalDate(2013, 7, 24).toDate(), new LocalDate(2013, 7, 26).toDate(),
				new LocalDate(2013, 7, 20).toDate());

		assertEquals(1, result.size());
		assertEquals(new DateTime(2013, 7, 22, 8, 0, 0).toDate(), result.get(0).getDateDebut());
		assertEquals(1, (int) result.get(0).getRefTypeAbsence().getIdRefTypeAbsence());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesForPrimesCalculees() {

		RefTypeAbsence refTypeAbs = new RefTypeAbsence();
		refTypeAbs.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(refTypeAbs);

		RefTypeAbsence refTypeAbsNon = new RefTypeAbsence();
		refTypeAbsNon.setIdRefTypeAbsence(2);
		ptgEntityManager.persist(refTypeAbsNon);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008765);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(refTypeAbs);
		ptgEntityManager.persist(ptg);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008765);
		ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg2.setRefTypeAbsence(refTypeAbsNon);
		ptgEntityManager.persist(ptg2);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 22).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ep.setIdAgent(9008765);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2013, 7, 23).toDate());
		ep2.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep2.setEtat(EtatPointageEnum.APPROUVE);
		ep2.setIdAgent(9008765);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		EtatPointage ep3 = new EtatPointage();
		ep3.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep3.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep3.setEtat(EtatPointageEnum.APPROUVE);
		ep3.setIdAgent(9008765);
		ep3.setPointage(ptg);
		ptgEntityManager.persist(ep3);

		List<Pointage> result = repository.getListPointagesForPrimesCalculees(9008765,
				new LocalDate(2013, 7, 24).toDate(), new LocalDate(2013, 7, 26).toDate(),
				new LocalDate(2013, 7, 20).toDate());

		assertEquals(1, result.size());
		assertEquals(new DateTime(2013, 7, 22, 8, 0, 0).toDate(), result.get(0).getDateDebut());
		assertEquals(1, (int) result.get(0).getRefTypeAbsence().getIdRefTypeAbsence());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesCalculesPrimeForVentilation() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtp);

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		PointageCalcule pc = new PointageCalcule();
		pc.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		pc.setDateFin(new LocalDate(2013, 7, 23).toDate());
		pc.setDateLundi(new LocalDate(2013, 7, 24).toDate());
		pc.setEtat(EtatPointageEnum.APPROUVE);
		pc.setIdAgent(9005138);
		pc.setLastVentilDate(vd);
		pc.setQuantite(1.0);
		pc.setRefPrime(rp);
		pc.setType(rtp);
		ptgEntityManager.persist(pc);

		PointageCalcule pc2 = new PointageCalcule();
		pc2.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		pc2.setDateFin(new LocalDate(2013, 7, 23).toDate());
		pc2.setDateLundi(new LocalDate(2013, 7, 24).toDate());
		pc2.setEtat(EtatPointageEnum.REJETE_DEFINITIVEMENT);
		pc2.setIdAgent(9005139);
		pc2.setLastVentilDate(vd);
		pc2.setQuantite(2.0);
		pc2.setRefPrime(rp);
		pc2.setType(rtp);
		ptgEntityManager.persist(pc2);

		PointageCalcule pc3 = new PointageCalcule();
		pc3.setDateDebut(new LocalDate(2013, 6, 22).toDate());
		pc3.setDateFin(new LocalDate(2013, 7, 23).toDate());
		pc3.setDateLundi(new LocalDate(2013, 7, 24).toDate());
		pc3.setEtat(EtatPointageEnum.EN_ATTENTE);
		pc3.setIdAgent(9005138);
		pc3.setLastVentilDate(vd);
		pc3.setQuantite(3.0);
		pc3.setRefPrime(rp);
		pc3.setType(rtp);
		ptgEntityManager.persist(pc3);

		PointageCalcule pc4 = new PointageCalcule();
		pc4.setDateDebut(new LocalDate(2013, 8, 22).toDate());
		pc4.setDateFin(new LocalDate(2013, 7, 23).toDate());
		pc4.setDateLundi(new LocalDate(2013, 7, 24).toDate());
		pc4.setEtat(EtatPointageEnum.REFUSE_DEFINITIVEMENT);
		pc4.setIdAgent(9005138);
		pc4.setLastVentilDate(vd);
		pc4.setQuantite(4.0);
		pc4.setRefPrime(rp);
		pc4.setType(rtp);
		ptgEntityManager.persist(pc4);

		List<PointageCalcule> result = repository.getListPointagesCalculesPrimeForVentilation(new Integer(9005138),
				new LocalDate(2013, 7, 23).toDate());

		assertEquals(1, result.size());
		assertEquals(EtatPointageEnum.APPROUVE, result.get(0).getEtat());
		assertEquals(new Double(1), result.get(0).getQuantite());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	// bug #17553 en cas de rappel sur un mois precedent
	// => s assurer que l on prend que le dernier resultat de pointage calcule
	// pour eviter de comptabiliser deux fois les primes
	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesCalculesPrimeForVentilation_withDoublon() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2015, 8, 4).toDate());
		vd.setPaye(false);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtp);

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(true);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		ptgEntityManager.persist(rp);

		RefPrime rpPanier = new RefPrime();
		rpPanier.setAide("Prime Panier");
		rpPanier.setCalculee(true);
		rpPanier.setDescription(null);
		rpPanier.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rpPanier.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		rpPanier.setNoRubr(7713);
		rpPanier.setStatut(AgentStatutEnum.F);
		rpPanier.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rpPanier);

		PointageCalcule pc = new PointageCalcule();
		pc.setDateDebut(new LocalDate(2015, 6, 1).toDate());
		pc.setDateFin(null);
		pc.setDateLundi(new LocalDate(2015, 6, 1).toDate());
		pc.setEtat(EtatPointageEnum.VALIDE);
		pc.setIdAgent(9004377);
		pc.setLastVentilDate(vd);
		pc.setQuantite(240.0);
		pc.setRefPrime(rp);
		pc.setType(rtp);
		ptgEntityManager.persist(pc);

		PointageCalcule pc2 = new PointageCalcule();
		pc2.setDateDebut(new LocalDate(2015, 6, 1).toDate());
		pc2.setDateFin(null);
		pc2.setDateLundi(new LocalDate(2015, 6, 1).toDate());
		pc2.setEtat(EtatPointageEnum.VALIDE);
		pc2.setIdAgent(9004377);
		pc2.setLastVentilDate(vd);
		pc2.setQuantite(240.0);
		pc2.setRefPrime(rp);
		pc2.setType(rtp);
		ptgEntityManager.persist(pc2);

		PointageCalcule pc3 = new PointageCalcule();
		pc3.setDateDebut(new LocalDate(2015, 6, 2).toDate());
		pc3.setDateFin(null);
		pc3.setDateLundi(new LocalDate(2015, 6, 1).toDate());
		pc3.setEtat(EtatPointageEnum.VALIDE);
		pc3.setIdAgent(9004377);
		pc3.setLastVentilDate(vd);
		pc3.setQuantite(240.0);
		pc3.setRefPrime(rp);
		pc3.setType(rtp);
		ptgEntityManager.persist(pc3);

		PointageCalcule pc4 = new PointageCalcule();
		pc4.setDateDebut(new LocalDate(2015, 6, 2).toDate());
		pc4.setDateFin(null);
		pc4.setDateLundi(new LocalDate(2015, 6, 1).toDate());
		pc4.setEtat(EtatPointageEnum.VALIDE);
		pc4.setIdAgent(9004377);
		pc4.setLastVentilDate(vd);
		pc4.setQuantite(240.0);
		pc4.setRefPrime(rp);
		pc4.setType(rtp);
		ptgEntityManager.persist(pc4);

		PointageCalcule pc5 = new PointageCalcule();
		pc5.setDateDebut(new LocalDate(2015, 6, 2).toDate());
		pc5.setDateFin(null);
		pc5.setDateLundi(new LocalDate(2015, 6, 1).toDate());
		pc5.setEtat(EtatPointageEnum.VALIDE);
		pc5.setIdAgent(9004377);
		pc5.setLastVentilDate(vd);
		pc5.setQuantite(1.0);
		pc5.setRefPrime(rpPanier);
		pc5.setType(rtp);
		ptgEntityManager.persist(pc5);

		PointageCalcule pc6 = new PointageCalcule();
		pc6.setDateDebut(new LocalDate(2015, 6, 2).toDate());
		pc6.setDateFin(null);
		pc6.setDateLundi(new LocalDate(2015, 6, 1).toDate());
		pc6.setEtat(EtatPointageEnum.VALIDE);
		pc6.setIdAgent(9004377);
		pc6.setLastVentilDate(vd);
		pc6.setQuantite(1.0);
		pc6.setRefPrime(rpPanier);
		pc6.setType(rtp);
		ptgEntityManager.persist(pc6);

		PointageCalcule pc7 = new PointageCalcule();
		pc7.setDateDebut(new LocalDate(2015, 6, 4).toDate());
		pc7.setDateFin(null);
		pc7.setDateLundi(new LocalDate(2015, 6, 1).toDate());
		pc7.setEtat(EtatPointageEnum.VALIDE);
		pc7.setIdAgent(9004377);
		pc7.setLastVentilDate(vd);
		pc7.setQuantite(1.0);
		pc7.setRefPrime(rpPanier);
		pc7.setType(rtp);
		ptgEntityManager.persist(pc7);

		PointageCalcule pc8 = new PointageCalcule();
		pc8.setDateDebut(new LocalDate(2015, 6, 4).toDate());
		pc8.setDateFin(null);
		pc8.setDateLundi(new LocalDate(2015, 6, 1).toDate());
		pc8.setEtat(EtatPointageEnum.VALIDE);
		pc8.setIdAgent(9004377);
		pc8.setLastVentilDate(vd);
		pc8.setQuantite(240.0);
		pc8.setRefPrime(rp);
		pc8.setType(rtp);
		ptgEntityManager.persist(pc8);

		List<PointageCalcule> result = repository.getListPointagesCalculesPrimeForVentilation(new Integer(9004377),
				new LocalDate(2015, 6, 2).toDate());

		assertEquals(5, result.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getLatestVentilDaten() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilDate result = repository.getLatestVentilDate(TypeChainePaieEnum.SCV, true);
		VentilDate noResult = repository.getLatestVentilDate(TypeChainePaieEnum.SCV, false);
		VentilDate noResultBis = repository.getLatestVentilDate(TypeChainePaieEnum.SHC, true);

		assertEquals(new LocalDate(2013, 7, 23).toDate(), result.getDateVentilation());
		assertEquals(TypeChainePaieEnum.SCV, result.getTypeChainePaie());
		assertTrue(result.isPaye());
		assertNull(noResult);
		assertNull(noResultBis);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void removeVentilationsForDateAgentAndType_etatVentile() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp.setEtat(EtatPointageEnum.VENTILE);
		vp.setIdAgent(9005138);
		vp.setQuantite(1.0);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh.setEtat(EtatPointageEnum.VENTILE);
		vh.setIdAgent(9005139);
		vh.setMAbsences(0);
		vh.setMComposees(1);
		vh.setMComposeesRecup(0);
		vh.setMHorsContrat(1);
		vh.setVentilDate(vd);
		ptgEntityManager.persist(vh);

		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va.setEtat(EtatPointageEnum.VENTILE);
		va.setIdAgent(9005140);
		va.setMinutesConcertee(10);
		va.setMinutesNonConcertee(10);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		List<Integer> listAgents = new ArrayList<Integer>();
		listAgents.add(9005138);
		listAgents.add(9005139);
		listAgents.add(9005140);

		List<VentilAbsence> resultAbs = repository.getListOfVentilAbsenceForDateAgent(vd.getIdVentilDate(), listAgents);
		assertEquals(1, resultAbs.size());
		List<VentilHsup> resultHS = repository.getListOfVentilHSForDateAgent(vd.getIdVentilDate(), listAgents);
		assertEquals(1, resultHS.size());
		List<VentilPrime> resultPrime = repository.getListOfVentilPrimeForDateAgent(vd.getIdVentilDate(), listAgents,
				true);
		assertEquals(1, resultPrime.size());

		repository.removeVentilationsForDateAgentAndType(vd, 9005138, RefTypePointageEnum.PRIME);
		repository.removeVentilationsForDateAgentAndType(vd, 9005139, RefTypePointageEnum.H_SUP);
		repository.removeVentilationsForDateAgentAndType(vd, 9005140, RefTypePointageEnum.ABSENCE);

		resultAbs = null;
		resultHS = null;
		resultPrime = null;

		resultAbs = repository.getListOfVentilAbsenceForDateAgent(vd.getIdVentilDate(), listAgents);
		assertEquals(0, resultAbs.size());
		resultHS = repository.getListOfVentilHSForDateAgent(vd.getIdVentilDate(), listAgents);
		assertEquals(0, resultHS.size());
		resultPrime = repository.getListOfVentilPrimeForDateAgent(vd.getIdVentilDate(), listAgents, true);
		assertEquals(0, resultPrime.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void removeVentilationsForDateAgentAndType_etatValide() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp.setEtat(EtatPointageEnum.VALIDE);
		vp.setIdAgent(9005138);
		vp.setQuantite(1.0);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh.setEtat(EtatPointageEnum.VALIDE);
		vh.setIdAgent(9005139);
		vh.setMAbsences(0);
		vh.setMComposees(1);
		vh.setMComposeesRecup(0);
		vh.setMHorsContrat(1);
		vh.setVentilDate(vd);
		ptgEntityManager.persist(vh);

		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va.setEtat(EtatPointageEnum.VALIDE);
		va.setIdAgent(9005140);
		va.setMinutesConcertee(10);
		va.setMinutesNonConcertee(10);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.VENTILE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(1.0);
		vp2.setVentilDate(vd);
		ptgEntityManager.persist(vp2);

		VentilHsup vh2 = new VentilHsup();
		vh2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh2.setEtat(EtatPointageEnum.VENTILE);
		vh2.setIdAgent(9005139);
		vh2.setMAbsences(0);
		vh2.setMComposees(1);
		vh2.setMComposeesRecup(0);
		vh2.setMHorsContrat(1);
		vh2.setVentilDate(vd);
		ptgEntityManager.persist(vh2);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9005140);
		va2.setMinutesConcertee(10);
		va2.setMinutesNonConcertee(10);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		List<Integer> listAgents = new ArrayList<Integer>();
		listAgents.add(9005138);
		listAgents.add(9005139);
		listAgents.add(9005140);

		List<VentilAbsence> resultAbs = repository.getListOfVentilAbsenceForDateAgent(vd.getIdVentilDate(), listAgents);
		assertEquals(1, resultAbs.size());
		List<VentilHsup> resultHS = repository.getListOfVentilHSForDateAgent(vd.getIdVentilDate(), listAgents);
		assertEquals(1, resultHS.size());
		List<VentilPrime> resultPrime = repository.getListOfVentilPrimeForDateAgent(vd.getIdVentilDate(), listAgents,
				true);
		assertEquals(1, resultPrime.size());

		repository.removeVentilationsForDateAgentAndType(vd, 9005138, RefTypePointageEnum.PRIME);
		repository.removeVentilationsForDateAgentAndType(vd, 9005139, RefTypePointageEnum.H_SUP);
		repository.removeVentilationsForDateAgentAndType(vd, 9005140, RefTypePointageEnum.ABSENCE);

		resultAbs = null;
		resultHS = null;
		resultPrime = null;

		resultAbs = repository.getListOfVentilAbsenceForDateAgent(vd.getIdVentilDate(), listAgents);
		assertEquals(0, resultAbs.size());
		resultHS = repository.getListOfVentilHSForDateAgent(vd.getIdVentilDate(), listAgents);
		assertEquals(0, resultHS.size());
		resultPrime = repository.getListOfVentilPrimeForDateAgent(vd.getIdVentilDate(), listAgents, true);
		assertEquals(0, resultPrime.size());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilAbsenceForDateAgent() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va.setEtat(EtatPointageEnum.VENTILE);
		va.setIdAgent(9005139);
		va.setMinutesConcertee(10);
		va.setMinutesNonConcertee(10);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9005140);
		va2.setMinutesConcertee(10);
		va2.setMinutesNonConcertee(10);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		VentilAbsence va3 = new VentilAbsence();
		va3.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va3.setEtat(EtatPointageEnum.VALIDE);
		va3.setIdAgent(9005140);
		va3.setMinutesConcertee(10);
		va3.setMinutesNonConcertee(10);
		va3.setVentilDate(vd);
		ptgEntityManager.persist(va3);

		List<Integer> listAgents = new ArrayList<Integer>();
		listAgents.add(9005139);
		listAgents.add(9005140);

		List<VentilAbsence> result = repository.getListOfVentilAbsenceForDateAgent(vd.getIdVentilDate(), listAgents);

		assertEquals(2, result.size());
		assertEquals(new Integer(9005139), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());

		assertEquals(new Integer(9005140), result.get(1).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(1).getEtat());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilPrimeForDateAgent() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp.setEtat(EtatPointageEnum.VENTILE);
		vp.setIdAgent(9005138);
		vp.setQuantite(3.0);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.VENTILE);
		vp2.setIdAgent(9005139);
		vp2.setQuantite(1.0);
		vp2.setVentilDate(vd);
		ptgEntityManager.persist(vp2);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp3.setEtat(EtatPointageEnum.VALIDE);
		vp3.setIdAgent(9005139);
		vp3.setQuantite(1.0);
		vp3.setVentilDate(vd);
		ptgEntityManager.persist(vp3);

		VentilPrime vp4 = new VentilPrime();
		vp4.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp4.setEtat(EtatPointageEnum.VENTILE);
		vp4.setIdAgent(9009999);
		vp4.setQuantite(1.0);
		vp4.setVentilDate(vd);
		ptgEntityManager.persist(vp4);

		VentilPrime vpBadQuantite = new VentilPrime();
		vpBadQuantite.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vpBadQuantite.setEtat(EtatPointageEnum.VENTILE);
		vpBadQuantite.setIdAgent(9005139);
		vpBadQuantite.setQuantite(0.0);
		vpBadQuantite.setVentilDate(vd);
		ptgEntityManager.persist(vpBadQuantite);

		List<Integer> listAgents = new ArrayList<Integer>();
		listAgents.add(9005138);
		listAgents.add(9005139);

		List<VentilPrime> result = repository.getListOfVentilPrimeForDateAgent(vd.getIdVentilDate(), listAgents, true);

		assertEquals(2, result.size());
		assertEquals(new Integer(9005138), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(new Double(3), result.get(0).getQuantite());

		assertEquals(new Integer(9005139), result.get(1).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(1).getEtat());
		assertEquals(new Double(1), result.get(1).getQuantite());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilHSForDateAgent() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh.setEtat(EtatPointageEnum.VENTILE);
		vh.setIdAgent(9005138);
		vh.setMAbsences(3);
		vh.setMComposees(5);
		vh.setMComposeesRecup(1);
		vh.setMHorsContrat(1);
		vh.setVentilDate(vd);
		ptgEntityManager.persist(vh);

		VentilHsup vh2 = new VentilHsup();
		vh2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh2.setEtat(EtatPointageEnum.VENTILE);
		vh2.setIdAgent(9005139);
		vh2.setMAbsences(0);
		vh2.setMComposees(1);
		vh2.setMComposeesRecup(0);
		vh2.setMHorsContrat(11);
		vh2.setVentilDate(vd);
		ptgEntityManager.persist(vh2);

		VentilHsup vh3 = new VentilHsup();
		vh3.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh3.setEtat(EtatPointageEnum.VALIDE);
		vh3.setIdAgent(9005139);
		vh3.setMAbsences(0);
		vh3.setMComposees(1);
		vh3.setMComposeesRecup(0);
		vh3.setMHorsContrat(11);
		vh3.setVentilDate(vd);
		ptgEntityManager.persist(vh3);

		VentilHsup vh4 = new VentilHsup();
		vh4.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh4.setEtat(EtatPointageEnum.VENTILE);
		vh4.setIdAgent(9009999);
		vh4.setMAbsences(0);
		vh4.setMComposees(1);
		vh4.setMComposeesRecup(0);
		vh4.setMHorsContrat(11);
		vh4.setVentilDate(vd);
		ptgEntityManager.persist(vh2);

		List<Integer> listAgents = new ArrayList<Integer>();
		listAgents.add(9005138);
		listAgents.add(9005139);

		List<VentilHsup> result = repository.getListOfVentilHSForDateAgent(vd.getIdVentilDate(), listAgents);

		assertEquals(2, result.size());
		assertEquals(new Integer(9005138), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(3, result.get(0).getMAbsences());
		assertEquals(5, result.get(0).getMComposees());
		assertEquals(1, result.get(0).getMHorsContrat());

		assertEquals(new Integer(9005139), result.get(1).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(1).getEtat());
		assertEquals(0, result.get(1).getMAbsences());
		assertEquals(1, result.get(1).getMComposees());
		assertEquals(11, result.get(1).getMHorsContrat());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListVentilHSupForAgentAndVentilDateOrderByDateAsc() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilDate vd2 = new VentilDate();
		vd2.setDateVentilation(new LocalDate(2013, 7, 24).toDate());
		vd2.setPaye(true);
		vd2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd2);

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh.setEtat(EtatPointageEnum.VENTILE);
		vh.setIdAgent(9005138);
		vh.setMAbsences(3);
		vh.setMComposees(5);
		vh.setMComposeesRecup(1);
		vh.setMHorsContrat(1);
		vh.setVentilDate(vd);
		ptgEntityManager.persist(vh);

		VentilHsup vh2 = new VentilHsup();
		vh2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh2.setEtat(EtatPointageEnum.VENTILE);
		vh2.setIdAgent(9005138);
		vh2.setMAbsences(0);
		vh2.setMComposees(1);
		vh2.setMComposeesRecup(0);
		vh2.setMHorsContrat(11);
		vh2.setVentilDate(vd);
		ptgEntityManager.persist(vh2);

		VentilHsup vh3 = new VentilHsup();
		vh3.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh3.setEtat(EtatPointageEnum.VENTILE);
		vh3.setIdAgent(9005138);
		vh3.setMAbsences(10);
		vh3.setMComposees(1);
		vh3.setMComposeesRecup(0);
		vh3.setMHorsContrat(11);
		vh3.setVentilDate(vd2);
		ptgEntityManager.persist(vh3);

		VentilHsup vh4 = new VentilHsup();
		vh4.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh4.setEtat(EtatPointageEnum.VALIDE);
		vh4.setIdAgent(9005138);
		vh4.setMAbsences(10);
		vh4.setMComposees(1);
		vh4.setMComposeesRecup(0);
		vh4.setMHorsContrat(11);
		vh4.setVentilDate(vd2);
		ptgEntityManager.persist(vh4);

		VentilHsup vh5 = new VentilHsup();
		vh5.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh5.setEtat(EtatPointageEnum.VALIDE);
		vh5.setIdAgent(9009999);
		vh5.setMAbsences(10);
		vh5.setMComposees(1);
		vh5.setMComposeesRecup(0);
		vh5.setMHorsContrat(11);
		vh5.setVentilDate(vd2);
		ptgEntityManager.persist(vh5);

		List<VentilHsup> result = repository.getListVentilHSupForAgentAndVentilDateOrderByDateAsc(new Integer(9005138),
				vd.getIdVentilDate());

		assertEquals(2, result.size());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(3, result.get(0).getMAbsences());

		assertEquals(EtatPointageEnum.VENTILE, result.get(1).getEtat());
		assertEquals(0, result.get(1).getMAbsences());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListVentilAbsencesForAgentAndVentilDate() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilDate vd2 = new VentilDate();
		vd2.setDateVentilation(new LocalDate(2013, 7, 24).toDate());
		vd2.setPaye(true);
		vd2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd2);

		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va.setEtat(EtatPointageEnum.VENTILE);
		va.setIdAgent(9005138);
		va.setMinutesConcertee(10);
		va.setMinutesImmediat(0);
		va.setMinutesNonConcertee(5);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9005138);
		va2.setMinutesConcertee(20);
		va2.setMinutesImmediat(0);
		va2.setMinutesNonConcertee(5);
		va2.setVentilDate(vd2);
		ptgEntityManager.persist(va2);

		VentilAbsence va3 = new VentilAbsence();
		va3.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va3.setEtat(EtatPointageEnum.VALIDE);
		va3.setIdAgent(9005138);
		va3.setMinutesConcertee(30);
		va3.setMinutesImmediat(0);
		va3.setMinutesNonConcertee(5);
		va3.setVentilDate(vd);
		ptgEntityManager.persist(va3);

		List<VentilAbsence> result = repository.getListVentilAbsencesForAgentAndVentilDate(new Integer(9005138),
				vd.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(10, result.get(0).getMinutesConcertee());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListVentilPrimesMoisForAgentAndVentilDateOrderByDateAsc() {

		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		ptgEntityManager.persist(rp);

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilDate vd2 = new VentilDate();
		vd2.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd2.setPaye(true);
		vd2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd2);

		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp.setEtat(EtatPointageEnum.VENTILE);
		vp.setIdAgent(9005138);
		vp.setQuantite(3.0);
		vp.setVentilDate(vd);
		vp.setRefPrime(rp);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.VENTILE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(1.0);
		vp2.setVentilDate(vd);
		vp2.setRefPrime(rp);
		ptgEntityManager.persist(vp2);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp3.setEtat(EtatPointageEnum.VENTILE);
		vp3.setIdAgent(9005138);
		vp3.setQuantite(10.0);
		vp3.setVentilDate(vd2);
		vp3.setRefPrime(rp);
		ptgEntityManager.persist(vp3);

		VentilPrime vp4 = new VentilPrime();
		vp4.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp4.setEtat(EtatPointageEnum.VALIDE);
		vp4.setIdAgent(9005138);
		vp4.setQuantite(1.0);
		vp4.setVentilDate(vd);
		vp4.setRefPrime(rp);
		ptgEntityManager.persist(vp4);

		VentilPrime vp5 = new VentilPrime();
		vp5.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp5.setEtat(EtatPointageEnum.VENTILE);
		vp5.setIdAgent(9009999);
		vp5.setQuantite(1.0);
		vp5.setVentilDate(vd);
		vp5.setRefPrime(rp);
		ptgEntityManager.persist(vp5);

		List<VentilPrime> result = repository.getListVentilPrimesMoisForAgentAndVentilDateOrderByDateAsc(new Integer(
				9005138), vd.getIdVentilDate());

		assertEquals(2, result.size());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(new Double(3), result.get(0).getQuantite());

		assertEquals(EtatPointageEnum.VENTILE, result.get(1).getEtat());
		assertEquals(new Double(1), result.get(1).getQuantite());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void canStartVentilation() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtp);

		VentilDate vdFrom = new VentilDate();
		vdFrom.setDateVentilation(new LocalDate(2013, 7, 20).toDate());
		vdFrom.setPaye(true);
		vdFrom.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vdFrom);

		VentilDate vdTo = new VentilDate();
		vdTo.setDateVentilation(new LocalDate(2013, 7, 20).toDate());
		vdTo.setPaye(true);
		vdTo.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vdTo);

		VentilTask vt = new VentilTask();
		vt.setDateCreation(new LocalDate(2013, 7, 23).toDate());
		vt.setDateVentilation(null);
		vt.setIdAgent(9005138);
		vt.setIdAgentCreation(9005138);
		vt.setTaskStatus(null);
		vt.setTypeChainePaie(TypeChainePaieEnum.SCV);
		vt.setVentilDateFrom(vdFrom);
		vt.setVentilDateTo(vdTo);
		ptgEntityManager.persist(vt);

		boolean resultTrue = repository.canStartVentilation(TypeChainePaieEnum.SHC);
		boolean resultFalse = repository.canStartVentilation(TypeChainePaieEnum.SCV);

		assertTrue(resultTrue);
		assertFalse(resultFalse);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getPriorVentilAbsenceForAgentAndDate_resultNull() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va.setEtat(EtatPointageEnum.VALIDE);
		va.setIdAgent(9005139);
		va.setMinutesConcertee(10);
		va.setMinutesNonConcertee(10);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2013, 7, 24).toDate());
		va2.setEtat(EtatPointageEnum.VALIDE);
		va2.setIdAgent(9005140);
		va2.setMinutesConcertee(10);
		va2.setMinutesNonConcertee(10);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		VentilAbsence result = repository.getPriorVentilAbsenceForAgentAndDate(new Integer(9005139), new LocalDate(
				2013, 7, 23).toDate(), va2);

		assertNull(result);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getPriorVentilAbsenceForAgentAndDate_difference() {

		// ventil retournee
		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va.setEtat(EtatPointageEnum.VALIDE);
		va.setIdAgent(9005139);
		va.setMinutesConcertee(10);
		va.setMinutesNonConcertee(10);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		// ventil a ne pas retourner
		VentilDate vd2 = new VentilDate();
		vd2.setDateVentilation(new LocalDate(2013, 7, 16).toDate());
		vd2.setPaye(true);
		vd2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd2);
		
		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va2.setEtat(EtatPointageEnum.VALIDE);
		va2.setIdAgent(9005140);
		va2.setMinutesConcertee(10);
		va2.setMinutesNonConcertee(10);
		va2.setVentilDate(vd2);
		ptgEntityManager.persist(va2);

		VentilAbsence result = repository.getPriorVentilAbsenceForAgentAndDate(new Integer(9005139), new LocalDate(
				2013, 7, 23).toDate(), va2);

		assertEquals(new LocalDate(2013, 7, 23).toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.VALIDE, result.getEtat());
		assertEquals(10, result.getMinutesConcertee());

		VentilAbsence noResult = repository.getPriorVentilAbsenceForAgentAndDate(new Integer(9005138), new LocalDate(
				2013, 7, 23).toDate(), va2);

		assertNull(noResult);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getPriorVentilHSupAgentAndDate_returnNull() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh.setEtat(EtatPointageEnum.VALIDE);
		vh.setIdAgent(9005138);
		vh.setMAbsences(3);
		vh.setMComposees(5);
		vh.setMComposeesRecup(1);
		vh.setMHorsContrat(1);
		vh.setVentilDate(vd);
		ptgEntityManager.persist(vh);

		VentilHsup vh2 = new VentilHsup();
		vh2.setDateLundi(new LocalDate(2013, 7, 24).toDate());
		vh2.setEtat(EtatPointageEnum.VALIDE);
		vh2.setIdAgent(9005139);
		vh2.setMAbsences(0);
		vh2.setMComposees(1);
		vh2.setMComposeesRecup(0);
		vh2.setMHorsContrat(11);
		vh2.setVentilDate(vd);
		ptgEntityManager.persist(vh2);

		VentilHsup result = repository.getPriorVentilHSupAgentAndDate(new Integer(9005138),
				new LocalDate(2013, 7, 23).toDate(), vh2);

		assertNull(result);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getPriorVentilHSupAgentAndDate_difference() {

		// a retourner
		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh.setEtat(EtatPointageEnum.VALIDE);
		vh.setIdAgent(9005138);
		vh.setMAbsences(3);
		vh.setMComposees(5);
		vh.setMComposeesRecup(1);
		vh.setMHorsContrat(1);
		vh.setVentilDate(vd);
		ptgEntityManager.persist(vh);

		// pas retournee
		VentilDate vd2 = new VentilDate();
		vd2.setDateVentilation(new LocalDate(2013, 7, 16).toDate());
		vd2.setPaye(true);
		vd2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd2);
		
		VentilHsup vh2 = new VentilHsup();
		vh2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		vh2.setEtat(EtatPointageEnum.VALIDE);
		vh2.setIdAgent(9005139);
		vh2.setMAbsences(0);
		vh2.setMComposees(1);
		vh2.setMComposeesRecup(0);
		vh2.setMHorsContrat(11);
		vh2.setVentilDate(vd2);
		ptgEntityManager.persist(vh2);

		VentilHsup result = repository.getPriorVentilHSupAgentAndDate(new Integer(9005138),
				new LocalDate(2013, 7, 23).toDate(), vh2);

		assertEquals(new LocalDate(2013, 7, 23).toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.VALIDE, result.getEtat());
		assertEquals(5, result.getMComposees());

		VentilHsup noResult = repository.getPriorVentilHSupAgentAndDate(new Integer(9005138),
				new LocalDate(2013, 7, 25).toDate(), vh2);

		assertNull(noResult);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getPriorVentilPrimeForAgentAndDate_returnNull() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);
		
		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(5);
		ptgEntityManager.persist(refPrime);

		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp.setEtat(EtatPointageEnum.APPROUVE);
		vp.setIdAgent(9005138);
		vp.setQuantite(3.0);
		vp.setVentilDate(vd);
		vp.setRefPrime(refPrime);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.EN_ATTENTE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(3.0);
		vp2.setVentilDate(vd);
		vp2.setRefPrime(refPrime);
		ptgEntityManager.persist(vp2);

		VentilPrime result = repository.getPriorVentilPrimeForAgentAndDate(new Integer(9005138), new LocalDate(2013, 7,
				23).toDate(), vp2);

		assertNull(result);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	// #15452 
	@Test
	@Transactional("ptgTransactionManager")
	public void getPriorVentilPrimeForAgentAndDate_difference() {

		// ancienne ventil a retourner
		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);
		
		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(5);
		ptgEntityManager.persist(refPrime);

		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2013, 7, 1).toDate());
		vp.setDatePrime(new LocalDate(2013, 7, 23).toDate());
		vp.setEtat(EtatPointageEnum.VALIDE);
		vp.setIdAgent(9005138);
		vp.setQuantite(3.0);
		vp.setVentilDate(vd);
		vp.setRefPrime(refPrime);
		ptgEntityManager.persist(vp);

		// nouvelle ventil a ne pas retourner
		VentilDate vd2 = new VentilDate();
		vd2.setDateVentilation(new LocalDate(2013, 7, 16).toDate());
		vd2.setPaye(true);
		vd2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd2);
		
		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 1).toDate());
		vp2.setDatePrime(new LocalDate(2013, 7, 16).toDate());
		vp2.setEtat(EtatPointageEnum.VALIDE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(3.0);
		vp2.setVentilDate(vd2);
		vp2.setRefPrime(refPrime);
		ptgEntityManager.persist(vp2);

		VentilPrime result = repository.getPriorVentilPrimeForAgentAndDate(new Integer(9005138), new LocalDate(2013, 7,
				1).toDate(), vp2);

		assertEquals(new LocalDate(2013, 7, 1).toDate(), result.getDateDebutMois());
		assertEquals(new LocalDate(2013, 7, 23).toDate(), result.getDatePrime());
		assertEquals(EtatPointageEnum.VALIDE, result.getEtat());
		assertEquals(new Double(3), result.getQuantite());

		VentilPrime noResult = repository.getPriorVentilPrimeForAgentAndDate(new Integer(9005137), new LocalDate(2013,
				7, 1).toDate(), vp2);

		assertNull(noResult);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilTaskErreur_SCV() {

		VentilDate ventilDateFrom = new VentilDate();
		ventilDateFrom.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		ventilDateFrom.setPaye(false);
		ventilDateFrom.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(ventilDateFrom);

		VentilDate ventilDateTo = new VentilDate();
		ventilDateTo.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		ventilDateTo.setPaye(false);
		ventilDateTo.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(ventilDateTo);

		VentilDate ventilDateTo2 = new VentilDate();
		ventilDateTo2.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		ventilDateTo2.setPaye(false);
		ventilDateTo2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(ventilDateTo2);

		VentilTask vt = new VentilTask();
		vt.setTypeChainePaie(TypeChainePaieEnum.SCV);
		vt.setTaskStatus("OK");
		vt.setVentilDateTo(ventilDateTo);
		vt.setIdAgent(1111);
		vt.setDateCreation(new Date());
		vt.setIdAgentCreation(9005138);
		vt.setVentilDateFrom(ventilDateFrom);
		ptgEntityManager.persist(vt);

		VentilTask vt2 = new VentilTask();
		vt2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		vt2.setTaskStatus("KO");
		vt2.setVentilDateTo(ventilDateTo);
		vt2.setIdAgent(2222);
		vt2.setDateCreation(new Date());
		vt2.setIdAgentCreation(9005138);
		vt2.setVentilDateFrom(ventilDateFrom);
		ptgEntityManager.persist(vt2);

		VentilTask vt3 = new VentilTask();
		vt3.setTypeChainePaie(TypeChainePaieEnum.SCV);
		vt3.setTaskStatus("ERREUR");
		vt3.setVentilDateTo(ventilDateTo);
		vt3.setIdAgent(3333);
		vt3.setDateCreation(new Date());
		vt3.setIdAgentCreation(9005138);
		vt3.setVentilDateFrom(ventilDateFrom);
		ptgEntityManager.persist(vt3);

		VentilTask vt4 = new VentilTask();
		vt4.setTypeChainePaie(TypeChainePaieEnum.SHC);
		vt4.setTaskStatus("ERREUR");
		vt4.setVentilDateTo(ventilDateTo);
		vt4.setIdAgent(4444);
		vt4.setDateCreation(new Date());
		vt4.setIdAgentCreation(9005138);
		vt4.setVentilDateFrom(ventilDateFrom);
		ptgEntityManager.persist(vt4);

		VentilTask vt5 = new VentilTask();
		vt5.setTypeChainePaie(TypeChainePaieEnum.SCV);
		vt5.setTaskStatus("KO");
		vt5.setVentilDateTo(ventilDateTo2);
		vt5.setIdAgent(5555);
		vt5.setDateCreation(new Date());
		vt5.setIdAgentCreation(9005138);
		vt5.setVentilDateFrom(ventilDateFrom);
		ptgEntityManager.persist(vt5);

		List<VentilTask> result = repository.getListOfVentilTaskErreur(TypeChainePaieEnum.SCV, ventilDateTo);

		assertEquals(2, result.size());
		assertEquals(2222, result.get(0).getIdAgent().intValue());
		assertEquals(3333, result.get(1).getIdAgent().intValue());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilTaskErreur_SHC() {

		VentilDate ventilDateFrom = new VentilDate();
		ventilDateFrom.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		ventilDateFrom.setPaye(false);
		ventilDateFrom.setTypeChainePaie(TypeChainePaieEnum.SHC);
		ptgEntityManager.persist(ventilDateFrom);

		VentilDate ventilDateTo = new VentilDate();
		ventilDateTo.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		ventilDateTo.setPaye(false);
		ventilDateTo.setTypeChainePaie(TypeChainePaieEnum.SHC);
		ptgEntityManager.persist(ventilDateTo);

		VentilDate ventilDateTo2 = new VentilDate();
		ventilDateTo2.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		ventilDateTo2.setPaye(false);
		ventilDateTo2.setTypeChainePaie(TypeChainePaieEnum.SHC);
		ptgEntityManager.persist(ventilDateTo2);

		VentilTask vt = new VentilTask();
		vt.setTypeChainePaie(TypeChainePaieEnum.SHC);
		vt.setTaskStatus("OK");
		vt.setVentilDateTo(ventilDateTo);
		vt.setIdAgent(1111);
		vt.setDateCreation(new Date());
		vt.setIdAgentCreation(9005138);
		vt.setVentilDateFrom(ventilDateFrom);
		ptgEntityManager.persist(vt);

		VentilTask vt2 = new VentilTask();
		vt2.setTypeChainePaie(TypeChainePaieEnum.SHC);
		vt2.setTaskStatus("KO");
		vt2.setVentilDateTo(ventilDateTo);
		vt2.setIdAgent(2222);
		vt2.setDateCreation(new Date());
		vt2.setIdAgentCreation(9005138);
		vt2.setVentilDateFrom(ventilDateFrom);
		ptgEntityManager.persist(vt2);

		VentilTask vt3 = new VentilTask();
		vt3.setTypeChainePaie(TypeChainePaieEnum.SHC);
		vt3.setTaskStatus("ERREUR");
		vt3.setVentilDateTo(ventilDateTo);
		vt3.setIdAgent(3333);
		vt3.setDateCreation(new Date());
		vt3.setIdAgentCreation(9005138);
		vt3.setVentilDateFrom(ventilDateFrom);
		ptgEntityManager.persist(vt3);

		VentilTask vt4 = new VentilTask();
		vt4.setTypeChainePaie(TypeChainePaieEnum.SCV);
		vt4.setTaskStatus("ERREUR");
		vt4.setVentilDateTo(ventilDateTo);
		vt4.setIdAgent(4444);
		vt4.setDateCreation(new Date());
		vt4.setIdAgentCreation(9005138);
		vt4.setVentilDateFrom(ventilDateFrom);
		ptgEntityManager.persist(vt4);

		VentilTask vt5 = new VentilTask();
		vt5.setTypeChainePaie(TypeChainePaieEnum.SHC);
		vt5.setTaskStatus("KO");
		vt5.setVentilDateTo(ventilDateTo2);
		vt5.setIdAgent(5555);
		vt5.setDateCreation(new Date());
		vt5.setIdAgentCreation(9005138);
		vt5.setVentilDateFrom(ventilDateFrom);
		ptgEntityManager.persist(vt5);

		List<VentilTask> result = repository.getListOfVentilTaskErreur(TypeChainePaieEnum.SHC, ventilDateTo);

		assertEquals(2, result.size());
		assertEquals(2222, result.get(0).getIdAgent().intValue());
		assertEquals(3333, result.get(1).getIdAgent().intValue());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilAbsenceForAgentBeetweenDate() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va.setEtat(EtatPointageEnum.VENTILE);
		va.setIdAgent(9005138);
		va.setMinutesConcertee(10);
		va.setMinutesNonConcertee(10);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9005138);
		va2.setMinutesConcertee(10);
		va2.setMinutesNonConcertee(10);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		VentilAbsence va3 = new VentilAbsence();
		va3.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va3.setEtat(EtatPointageEnum.VENTILE);
		va3.setIdAgent(9009999);
		va3.setMinutesConcertee(10);
		va3.setMinutesNonConcertee(10);
		va3.setVentilDate(vd);
		ptgEntityManager.persist(va3);

		VentilAbsence va4 = new VentilAbsence();
		va4.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va4.setEtat(EtatPointageEnum.VALIDE);
		va4.setIdAgent(9005138);
		va4.setMinutesConcertee(10);
		va4.setMinutesNonConcertee(10);
		va4.setVentilDate(vd);
		ptgEntityManager.persist(va4);

		List<VentilAbsence> result = repository.getListOfVentilAbsenceForAgentBeetweenDate(2, 2014, 9005138, vd.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(new Integer(9005138), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilHSForAgentBeetweenDate() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilHsup va = new VentilHsup();
		va.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va.setEtat(EtatPointageEnum.VENTILE);
		va.setIdAgent(9005138);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilHsup va2 = new VentilHsup();
		va2.setDateLundi(new LocalDate(2014, 2, 2).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9005138);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		VentilHsup va3 = new VentilHsup();
		va3.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va3.setEtat(EtatPointageEnum.VENTILE);
		va3.setIdAgent(9009999);
		va3.setVentilDate(vd);
		ptgEntityManager.persist(va3);

		VentilHsup va4 = new VentilHsup();
		va4.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va4.setEtat(EtatPointageEnum.VALIDE);
		va4.setIdAgent(9009999);
		va4.setVentilDate(vd);
		ptgEntityManager.persist(va4);

		List<VentilHsup> result = repository.getListOfVentilHSForAgentBeetweenDate(2, 2014, 9005138, vd.getIdVentilDate());

		assertEquals(2, result.size());
		assertEquals(new Integer(9005138), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(new Integer(9005138), result.get(1).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(1).getEtat());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListAgentsForShowVentilationPrimesForDate_All() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp.setEtat(EtatPointageEnum.VENTILE);
		vp.setIdAgent(9001111);
		vp.setQuantite(3.0);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.VENTILE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(3.0);
		vp2.setVentilDate(vd);
		ptgEntityManager.persist(vp2);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp3.setEtat(EtatPointageEnum.VALIDE);
		vp3.setIdAgent(9005138);
		vp3.setQuantite(3.0);
		vp3.setVentilDate(vd);
		ptgEntityManager.persist(vp3);

		List<Integer> result = repository.getListAgentsForShowVentilationPrimesForDate(vd.getIdVentilDate(), null,
				null, false);

		assertEquals(2, result.size());
		assertEquals(new Integer(9001111), result.get(1));
		assertEquals(new Integer(9005138), result.get(0));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListAgentsForShowVentilationPrimesForDate_idAgentMax() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp.setEtat(EtatPointageEnum.VENTILE);
		vp.setIdAgent(9001111);
		vp.setQuantite(3.0);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.VENTILE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(3.0);
		vp2.setVentilDate(vd);
		ptgEntityManager.persist(vp2);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp3.setEtat(EtatPointageEnum.VALIDE);
		vp3.setIdAgent(9005138);
		vp3.setQuantite(3.0);
		vp3.setVentilDate(vd);
		ptgEntityManager.persist(vp3);

		List<Integer> result = repository.getListAgentsForShowVentilationPrimesForDate(vd.getIdVentilDate(), 9005000,
				9009000, false);

		assertEquals(1, result.size());
		assertEquals(new Integer(9005138), result.get(0));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListAgentsForShowVentilationAbsencesForDate_All() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va.setEtat(EtatPointageEnum.VENTILE);
		va.setIdAgent(9004999);
		va.setMinutesConcertee(10);
		va.setMinutesNonConcertee(10);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9005138);
		va2.setMinutesConcertee(10);
		va2.setMinutesNonConcertee(10);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		VentilAbsence va3 = new VentilAbsence();
		va3.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va3.setEtat(EtatPointageEnum.VALIDE);
		va3.setIdAgent(9005138);
		va3.setMinutesConcertee(10);
		va3.setMinutesNonConcertee(10);
		va3.setVentilDate(vd);
		ptgEntityManager.persist(va3);

		List<Integer> result = repository.getListAgentsForShowVentilationAbsencesForDate(vd.getIdVentilDate(), null,
				null, false);

		assertEquals(2, result.size());
		assertEquals(new Integer(9005138), result.get(0));
		assertEquals(new Integer(9004999), result.get(1));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListAgentsForShowVentilationAbsencesForDate_idAgentMax() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va.setEtat(EtatPointageEnum.VENTILE);
		va.setIdAgent(9004999);
		va.setMinutesConcertee(10);
		va.setMinutesNonConcertee(10);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9005138);
		va2.setMinutesConcertee(10);
		va2.setMinutesNonConcertee(10);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		VentilAbsence va3 = new VentilAbsence();
		va3.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va3.setEtat(EtatPointageEnum.VALIDE);
		va3.setIdAgent(9005138);
		va3.setMinutesConcertee(10);
		va3.setMinutesNonConcertee(10);
		va3.setVentilDate(vd);
		ptgEntityManager.persist(va3);

		List<Integer> result = repository.getListAgentsForShowVentilationAbsencesForDate(vd.getIdVentilDate(), 9005000,
				9009000, false);

		assertEquals(1, result.size());
		assertEquals(new Integer(9005138), result.get(0));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListAgentsForShowVentilationHeuresSupForDate_All() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilHsup va = new VentilHsup();
		va.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va.setEtat(EtatPointageEnum.VENTILE);
		va.setIdAgent(9004999);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilHsup va2 = new VentilHsup();
		va2.setDateLundi(new LocalDate(2014, 2, 2).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9005138);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		List<Integer> result = repository.getListAgentsForShowVentilationHeuresSupForDate(vd.getIdVentilDate(), null,
				null, false);

		assertEquals(2, result.size());
		assertEquals(new Integer(9005138), result.get(0));
		assertEquals(new Integer(9004999), result.get(1));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListAgentsForShowVentilationHeuresSupForDate_idAgentMax() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilHsup va = new VentilHsup();
		va.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va.setEtat(EtatPointageEnum.VENTILE);
		va.setIdAgent(9004999);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilHsup va2 = new VentilHsup();
		va2.setDateLundi(new LocalDate(2014, 2, 2).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9005138);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		List<Integer> result = repository.getListAgentsForShowVentilationHeuresSupForDate(vd.getIdVentilDate(),
				9005000, 9009000, false);

		assertEquals(1, result.size());
		assertEquals(new Integer(9005138), result.get(0));

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfOldVentilHSForAgentAndDateLundi() {

		Date dateLundi = new LocalDate(2014, 2, 23).toDate();

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilDate badVd = new VentilDate();
		badVd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		badVd.setPaye(true);
		badVd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(badVd);

		VentilHsup vaOk = new VentilHsup();
		vaOk.setDateLundi(dateLundi);
		vaOk.setEtat(EtatPointageEnum.VALIDE);
		vaOk.setIdAgent(9005138);
		vaOk.setVentilDate(vd);
		ptgEntityManager.persist(vaOk);

		VentilHsup vaBadAgent = new VentilHsup();
		vaBadAgent.setDateLundi(dateLundi);
		vaBadAgent.setEtat(EtatPointageEnum.VALIDE);
		vaBadAgent.setIdAgent(9009999);
		vaBadAgent.setVentilDate(vd);
		ptgEntityManager.persist(vaBadAgent);

		VentilHsup vaBadLundi = new VentilHsup();
		vaBadLundi.setDateLundi(new LocalDate(2014, 2, 2).toDate());
		vaBadLundi.setEtat(EtatPointageEnum.VALIDE);
		vaBadLundi.setIdAgent(9005138);
		vaBadLundi.setVentilDate(vd);
		ptgEntityManager.persist(vaBadLundi);

		VentilHsup vaBadEtat = new VentilHsup();
		vaBadEtat.setDateLundi(dateLundi);
		vaBadEtat.setEtat(EtatPointageEnum.VENTILE);
		vaBadEtat.setIdAgent(9005138);
		vaBadEtat.setVentilDate(vd);
		ptgEntityManager.persist(vaBadEtat);

		VentilHsup vaBadVentiDate = new VentilHsup();
		vaBadVentiDate.setDateLundi(dateLundi);
		vaBadVentiDate.setEtat(EtatPointageEnum.VALIDE);
		vaBadVentiDate.setIdAgent(9005138);
		vaBadVentiDate.setVentilDate(badVd);
		ptgEntityManager.persist(vaBadVentiDate);

		List<VentilHsup> result = repository.getListOfOldVentilHSForAgentAndDateLundi(9005138, dateLundi,
				vd.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(dateLundi, result.get(0).getDateLundi());
		assertEquals(EtatPointageEnum.VALIDE, result.get(0).getEtat());
		assertEquals(9005138, result.get(0).getIdAgent().intValue());
		assertEquals(vd.getIdVentilDate(), result.get(0).getVentilDate().getIdVentilDate());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilAbsenceWithDateForEtatPayeur() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(false);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va.setEtat(EtatPointageEnum.VALIDE);
		va.setIdAgent(9004999);
		va.setMinutesConcertee(10);
		va.setMinutesNonConcertee(10);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va2.setEtat(EtatPointageEnum.VALIDE);
		va2.setIdAgent(9004999);
		va2.setMinutesConcertee(12);
		va2.setMinutesNonConcertee(10);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		VentilAbsence va3 = new VentilAbsence();
		va3.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va3.setEtat(EtatPointageEnum.VALIDE);
		va3.setIdAgent(9005138);
		va3.setMinutesConcertee(10);
		va3.setMinutesNonConcertee(10);
		va3.setVentilDate(vd);
		ptgEntityManager.persist(va3);

		List<VentilAbsence> result = repository.getListOfVentilAbsenceWithDateForEtatPayeur(vd.getIdVentilDate(), null);

		assertEquals(2, result.size());
		assertEquals(new LocalDate(2014, 2, 23).toDate(), result.get(1).getDateLundi());
		assertEquals(new LocalDate(2013, 7, 23).toDate(), result.get(0).getDateLundi());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilHeuresSupWithDateForEtatPayeur() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(false);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilHsup va = new VentilHsup();
		va.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va.setEtat(EtatPointageEnum.VALIDE);
		va.setIdAgent(9004999);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilHsup va2 = new VentilHsup();
		va2.setDateLundi(new LocalDate(2014, 2, 2).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9005138);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		List<VentilHsup> result = repository.getListOfVentilHeuresSupWithDateForEtatPayeur(vd.getIdVentilDate(), null);

		assertEquals(1, result.size());
		assertEquals(new LocalDate(2014, 2, 23).toDate(), result.get(0).getDateLundi());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListVentilHSupForAgentAndVentilDateOrderByDateAscForReposComp() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(false);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilDate vd2 = new VentilDate();
		vd2.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd2.setPaye(false);
		vd2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd2);

		VentilHsup va = new VentilHsup();
		va.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va.setEtat(EtatPointageEnum.VALIDE);
		va.setIdAgent(9004999);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilHsup va2 = new VentilHsup();
		va2.setDateLundi(new LocalDate(2014, 2, 2).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9005138);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		VentilHsup va3 = new VentilHsup();
		va3.setDateLundi(new LocalDate(2014, 2, 12).toDate());
		va3.setEtat(EtatPointageEnum.VALIDE);
		va3.setIdAgent(9005138);
		va3.setVentilDate(vd);
		ptgEntityManager.persist(va3);

		VentilHsup va4 = new VentilHsup();
		va4.setDateLundi(new LocalDate(2014, 2, 17).toDate());
		va4.setEtat(EtatPointageEnum.VALIDE);
		va4.setIdAgent(9005138);
		va4.setVentilDate(vd2);
		ptgEntityManager.persist(va4);

		List<VentilHsup> result = repository.getListVentilHSupForAgentAndVentilDateOrderByDateAscForReposComp(9005138,
				vd.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(new LocalDate(2014, 2, 12).toDate(), result.get(0).getDateLundi());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	// #14640 cas reel de recette utilisateur agent DPM avec plusieurs primes
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilPrimeWithDateForEtatPayeur_withManyPrimes() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(false);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);
		
		RefPrime refPrime = new RefPrime();
		refPrime.setIdRefPrime(1);
		ptgEntityManager.persist(refPrime);
		
		RefPrime refPrime6 = new RefPrime();
		refPrime6.setIdRefPrime(6);
		ptgEntityManager.persist(refPrime6);

		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2015, 2, 1).toDate());
		vp.setEtat(EtatPointageEnum.VALIDE);
		vp.setIdAgent(9001111);
		vp.setQuantite(3.0);
		vp.setVentilDate(vd);
		vp.setDatePrime(new LocalDate(2015, 2, 26).toDate());
		vp.setRefPrime(refPrime);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2015, 2, 1).toDate());
		vp2.setEtat(EtatPointageEnum.VALIDE);
		vp2.setIdAgent(9004378);
		vp2.setQuantite(240.0);
		vp2.setVentilDate(vd);
		vp2.setDatePrime(new LocalDate(2015, 2, 26).toDate());
		vp2.setRefPrime(refPrime);
		ptgEntityManager.persist(vp2);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2015, 2, 1).toDate());
		vp3.setEtat(EtatPointageEnum.VALIDE);
		vp3.setIdAgent(9004378);
		vp3.setQuantite(1.0);
		vp3.setVentilDate(vd);
		vp3.setDatePrime(new LocalDate(2015, 2, 26).toDate());
		vp3.setRefPrime(refPrime6);
		ptgEntityManager.persist(vp3);

		List<VentilPrime> result = repository.getListOfVentilPrimeWithDateForEtatPayeur(vd.getIdVentilDate(), 9004378);

		assertEquals(2, result.size());
		assertEquals(new LocalDate(2015, 2, 1).toDate(), result.get(0).getDateDebutMois());
		assertEquals(240, result.get(0).getQuantite().intValue());
		assertEquals(9004378, result.get(0).getIdAgent().intValue());
		assertEquals(new LocalDate(2015, 2, 1).toDate(), result.get(1).getDateDebutMois());
		assertEquals(1, result.get(1).getQuantite().intValue());
		assertEquals(9004378, result.get(1).getIdAgent().intValue());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilPrimeWithDateForEtatPayeur() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(false);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp.setEtat(EtatPointageEnum.VALIDE);
		vp.setIdAgent(9001111);
		vp.setQuantite(3.0);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.VENTILE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(3.0);
		vp2.setVentilDate(vd);
		ptgEntityManager.persist(vp2);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2013, 7, 18).toDate());
		vp3.setEtat(EtatPointageEnum.VALIDE);
		vp3.setIdAgent(9005138);
		vp3.setQuantite(3.0);
		vp3.setVentilDate(vd);
		ptgEntityManager.persist(vp3);

		List<VentilPrime> result = repository.getListOfVentilPrimeWithDateForEtatPayeur(vd.getIdVentilDate(), null);

		assertEquals(2, result.size());
		assertEquals(new LocalDate(2013, 7, 18).toDate(), result.get(0).getDateDebutMois());
		assertEquals(new LocalDate(2013, 7, 23).toDate(), result.get(1).getDateDebutMois());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfOldVentilPrimeForAgentAndDateDebutMois() {

		Date dateDebutMois = new LocalDate(2014, 2, 23).toDate();

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilDate badVd = new VentilDate();
		badVd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		badVd.setPaye(true);
		badVd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(badVd);

		VentilPrime vpOk = new VentilPrime();
		vpOk.setDateDebutMois(dateDebutMois);
		vpOk.setEtat(EtatPointageEnum.VALIDE);
		vpOk.setIdAgent(9005138);
		vpOk.setVentilDate(vd);
		ptgEntityManager.persist(vpOk);

		VentilPrime vpBadAgent = new VentilPrime();
		vpBadAgent.setDateDebutMois(dateDebutMois);
		vpBadAgent.setEtat(EtatPointageEnum.VALIDE);
		vpBadAgent.setIdAgent(9005999);
		vpBadAgent.setVentilDate(vd);
		ptgEntityManager.persist(vpBadAgent);

		VentilPrime vpBadEtat = new VentilPrime();
		vpBadEtat.setDateDebutMois(dateDebutMois);
		vpBadEtat.setEtat(EtatPointageEnum.VENTILE);
		vpBadEtat.setIdAgent(9005138);
		vpBadEtat.setVentilDate(vd);
		ptgEntityManager.persist(vpBadEtat);

		VentilPrime vpBadVentilDate = new VentilPrime();
		vpBadVentilDate.setDateDebutMois(dateDebutMois);
		vpBadVentilDate.setEtat(EtatPointageEnum.VALIDE);
		vpBadVentilDate.setIdAgent(9005138);
		vpBadVentilDate.setVentilDate(badVd);
		ptgEntityManager.persist(vpBadVentilDate);

		VentilPrime vpBadDateDebutMois = new VentilPrime();
		vpBadDateDebutMois.setDateDebutMois(new LocalDate(2014, 5, 23).toDate());
		vpBadDateDebutMois.setEtat(EtatPointageEnum.VALIDE);
		vpBadDateDebutMois.setIdAgent(9005138);
		vpBadDateDebutMois.setVentilDate(vd);
		ptgEntityManager.persist(vpBadDateDebutMois);

		List<VentilPrime> result = repository.getListOfOldVentilPrimeForAgentAndDateDebutMois(9005138, dateDebutMois,
				vd.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(dateDebutMois, result.get(0).getDateDebutMois());
		assertEquals(EtatPointageEnum.VALIDE, result.get(0).getEtat());
		assertEquals(9005138, result.get(0).getIdAgent().intValue());
		assertEquals(vd.getIdVentilDate(), result.get(0).getVentilDate().getIdVentilDate());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListIdAgentsWithPointagesValidatedAndRejetes() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new Date());
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		ptgEntityManager.persist(rtp);

		RefTypeAbsence refTypeAbs = new RefTypeAbsence();
		refTypeAbs.setIdRefTypeAbsence(1);
		ptgEntityManager.persist(refTypeAbs);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008761);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(refTypeAbs);
		ptg.setType(rtp);
		ptgEntityManager.persist(ptg);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.APPROUVE);
		ep.setIdAgent(9008761);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		RefTypePointage rtp2 = new RefTypePointage();
		rtp2.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtp2);

		RefTypeAbsence refTypeAbsNon = new RefTypeAbsence();
		refTypeAbsNon.setIdRefTypeAbsence(2);
		ptgEntityManager.persist(refTypeAbsNon);

		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008762);
		ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg2.setRefTypeAbsence(refTypeAbsNon);
		ptg2.setType(rtp2);
		ptgEntityManager.persist(ptg2);

		EtatPointage ep2 = new EtatPointage();
		ep2.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep2.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep2.setEtat(EtatPointageEnum.VENTILE);
		ep2.setIdAgent(9008762);
		ep2.setPointage(ptg2);
		ptgEntityManager.persist(ep2);

		Pointage ptg3 = new Pointage();
		ptg3.setIdAgent(9008763);
		ptg3.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg3.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg3.setRefTypeAbsence(refTypeAbsNon);
		ptg3.setType(rtp);
		ptgEntityManager.persist(ptg3);

		EtatPointage ep3 = new EtatPointage();
		ep3.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep3.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep3.setEtat(EtatPointageEnum.EN_ATTENTE);
		ep3.setIdAgent(9008763);
		ep3.setPointage(ptg3);
		ptgEntityManager.persist(ep3);

		Pointage ptg4 = new Pointage();
		ptg4.setIdAgent(9008764);
		ptg4.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg4.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg4.setRefTypeAbsence(refTypeAbsNon);
		ptg4.setType(rtp);
		ptgEntityManager.persist(ptg4);

		EtatPointage ep4 = new EtatPointage();
		ep4.setDateEtat(new LocalDate(2013, 7, 29).toDate());
		ep4.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep4.setEtat(EtatPointageEnum.APPROUVE);
		ep4.setIdAgent(9008764);
		ep4.setPointage(ptg4);
		ptgEntityManager.persist(ep4);

		Pointage ptg5 = new Pointage();
		ptg5.setIdAgent(9003215);
		ptg5.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg5.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
		ptg5.setRefTypeAbsence(refTypeAbsNon);
		ptg5.setType(rtp);
		ptgEntityManager.persist(ptg5);

		EtatPointage ep5 = new EtatPointage();
		ep5.setDateEtat(new LocalDate(2013, 7, 29).toDate());
		ep5.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep5.setEtat(EtatPointageEnum.REJETE);
		ep5.setIdAgent(9003215);
		ep5.setPointage(ptg5);
		ptgEntityManager.persist(ep5);

		vd.getPointages().add(ptg);
		vd.getPointages().add(ptg2);
		vd.getPointages().add(ptg3);
		vd.getPointages().add(ptg4);
		vd.getPointages().add(ptg5);

		List<Integer> result = repository.getListIdAgentsWithPointagesValidatedAndRejetes(vd.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(9003215, result.get(0).intValue());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeValideByMoisAndRefPrime_1result() {

		RefPrime refPrime = new RefPrime();
		refPrime.setNoRubr(32);
		ptgEntityManager.persist(refPrime);

		RefTypePointage rtpPrime = new RefTypePointage();
		rtpPrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(rtpPrime);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008761);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(null);
		ptg.setType(rtpPrime);
		ptg.setRefPrime(refPrime);
		ptgEntityManager.persist(ptg);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.VALIDE);
		ep.setIdAgent(9008761);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		List<Pointage> result = repository.getListPointagesPrimeValideByMoisAndRefPrime(9008761, new DateTime(2013, 7,
				1, 8, 0, 0).toDate(), refPrime.getIdRefPrime());

		assertEquals(1, result.size());
		assertEquals(9008761, result.get(0).getIdAgent().intValue());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeValideByMoisAndRefPrime_badEtat() {

		RefPrime refPrime = new RefPrime();
		refPrime.setNoRubr(32);
		ptgEntityManager.persist(refPrime);

		RefTypePointage rtpPrime = new RefTypePointage();
		rtpPrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(rtpPrime);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008761);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(null);
		ptg.setType(rtpPrime);
		ptg.setRefPrime(refPrime);
		ptgEntityManager.persist(ptg);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.VENTILE);
		ep.setIdAgent(9008761);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		List<Pointage> result = repository.getListPointagesPrimeValideByMoisAndRefPrime(9008761, new DateTime(2013, 7,
				1, 8, 0, 0).toDate(), refPrime.getIdRefPrime());

		assertEquals(0, result.size());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeValideByMoisAndRefPrime_badMonth() {

		RefPrime refPrime = new RefPrime();
		refPrime.setNoRubr(32);
		ptgEntityManager.persist(refPrime);

		RefTypePointage rtpPrime = new RefTypePointage();
		rtpPrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(rtpPrime);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008761);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(null);
		ptg.setType(rtpPrime);
		ptg.setRefPrime(refPrime);
		ptgEntityManager.persist(ptg);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.VALIDE);
		ep.setIdAgent(9008761);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		List<Pointage> result = repository.getListPointagesPrimeValideByMoisAndRefPrime(9008761, new DateTime(2013, 8,
				1, 8, 0, 0).toDate(), refPrime.getIdRefPrime());

		assertEquals(0, result.size());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeValideByMoisAndRefPrime_badRefPrime() {

		RefPrime refPrime = new RefPrime();
		refPrime.setNoRubr(32);
		ptgEntityManager.persist(refPrime);

		RefTypePointage rtpPrime = new RefTypePointage();
		rtpPrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(rtpPrime);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008761);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(null);
		ptg.setType(rtpPrime);
		ptg.setRefPrime(refPrime);
		ptgEntityManager.persist(ptg);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.VALIDE);
		ep.setIdAgent(9008761);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		List<Pointage> result = repository.getListPointagesPrimeValideByMoisAndRefPrime(9008761, new DateTime(2013, 7,
				1, 8, 0, 0).toDate(), 11);

		assertEquals(0, result.size());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeValideByMoisAndRefPrime_badAgent() {

		RefPrime refPrime = new RefPrime();
		refPrime.setNoRubr(32);
		ptgEntityManager.persist(refPrime);

		RefTypePointage rtpPrime = new RefTypePointage();
		rtpPrime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(rtpPrime);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008761);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(null);
		ptg.setType(rtpPrime);
		ptg.setRefPrime(refPrime);
		ptgEntityManager.persist(ptg);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.VALIDE);
		ep.setIdAgent(9008761);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		List<Pointage> result = repository.getListPointagesPrimeValideByMoisAndRefPrime(9009999, new DateTime(2013, 7,
				1, 8, 0, 0).toDate(), refPrime.getIdRefPrime());

		assertEquals(0, result.size());
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeValideByMoisAndRefPrime_badTypePointage() {

		RefPrime refPrime = new RefPrime();
		refPrime.setNoRubr(32);
		ptgEntityManager.persist(refPrime);

		RefTypePointage rtpPrime = new RefTypePointage();
		rtpPrime.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtpPrime);

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008761);
		ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.setRefTypeAbsence(null);
		ptg.setType(rtpPrime);
		ptg.setRefPrime(refPrime);
		ptgEntityManager.persist(ptg);

		EtatPointage ep = new EtatPointage();
		ep.setDateEtat(new LocalDate(2013, 7, 25).toDate());
		ep.setDateMaj(new LocalDate(2013, 7, 22).toDate());
		ep.setEtat(EtatPointageEnum.VALIDE);
		ep.setIdAgent(9008761);
		ep.setPointage(ptg);
		ptgEntityManager.persist(ep);

		List<Pointage> result = repository.getListPointagesPrimeValideByMoisAndRefPrime(9008761, new DateTime(2013, 7,
				1, 8, 0, 0).toDate(), refPrime.getIdRefPrime());

		assertEquals(0, result.size());
	}
	


	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfAgentWithDateForEtatPayeur() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(false);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);
		
		//////// primes //////////
		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp.setEtat(EtatPointageEnum.VALIDE);
		vp.setIdAgent(1);
		vp.setQuantite(3.0);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2013, 7, 18).toDate());
		vp3.setEtat(EtatPointageEnum.VALIDE);
		vp3.setIdAgent(8);
		vp3.setQuantite(3.0);
		vp3.setVentilDate(vd);
		ptgEntityManager.persist(vp3);
		
		//////// hsup //////////
		VentilHsup vhs = new VentilHsup();
		vhs.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		vhs.setEtat(EtatPointageEnum.VALIDE);
		vhs.setIdAgent(23);
		vhs.setVentilDate(vd);
		ptgEntityManager.persist(vhs);
		
		//////// absences //////////
		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va.setEtat(EtatPointageEnum.VALIDE);
		va.setIdAgent(17);
		va.setMinutesConcertee(10);
		va.setMinutesNonConcertee(10);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2014, 2, 23).toDate());
		va2.setEtat(EtatPointageEnum.VALIDE);
		va2.setIdAgent(40);
		va2.setMinutesConcertee(12);
		va2.setMinutesNonConcertee(10);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		VentilAbsence va3 = new VentilAbsence();
		va3.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va3.setEtat(EtatPointageEnum.VALIDE);
		va3.setIdAgent(9005138);
		va3.setMinutesConcertee(10);
		va3.setMinutesNonConcertee(10);
		va3.setVentilDate(vd);
		ptgEntityManager.persist(va3);

		List<Integer> result = repository.getListOfAgentWithDateForEtatPayeur(vd.getIdVentilDate());

		assertEquals(6, result.size());
		
		Integer idAgentPrcd = 0;
		for(Integer idAgent : result) {
			if(idAgent < idAgentPrcd) {
				fail("error de tri");
			}
			idAgentPrcd = idAgent;
		}

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	// #15286 : bug de QUALIF : nomatr 3544
	// resultat d une ancienne ventilation affichee dans une nouvelle
	// car meme date de lundi, mais deux ventilations bien distinctes
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilAbsenceForAgentBeetweenDate_nomatr3544() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2015, 4, 12).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);
		
		VentilDate vd2 = new VentilDate();
		vd2.setDateVentilation(new LocalDate(2015, 5, 3).toDate());
		vd2.setPaye(false);
		vd2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd2);

		// 1er ventilation
		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2015, 3, 16).toDate());
		va.setEtat(EtatPointageEnum.VENTILE);
		va.setIdAgent(9003544);
		va.setMinutesConcertee(900);
		va.setMinutesNonConcertee(0);
		va.setMinutesImmediat(0);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2015, 3, 30).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9003544);
		va2.setMinutesConcertee(420);
		va2.setMinutesNonConcertee(0);
		va2.setMinutesImmediat(0);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		// 2e ventilation
		VentilAbsence va3 = new VentilAbsence();
		va3.setDateLundi(new LocalDate(2015, 3, 30).toDate());
		va3.setEtat(EtatPointageEnum.VENTILE);
		va3.setIdAgent(9003544);
		va3.setMinutesConcertee(420);
		va3.setMinutesNonConcertee(0);
		va3.setMinutesImmediat(0);
		va3.setVentilDate(vd2);
		ptgEntityManager.persist(va3);

		List<VentilAbsence> result = repository.getListOfVentilAbsenceForAgentBeetweenDate(3, 2015, 9003544, vd2.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(new Integer(9003544), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(420, result.get(0).getMinutesConcertee());
		assertEquals(va3.getIdVentilAbsence(), result.get(0).getIdVentilAbsence());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	// #15286 : bug de QUALIF : nomatr 3544
	// resultat d une ancienne ventilation affichee dans une nouvelle
	// car meme date de lundi, mais deux ventilations bien distinctes
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilAbsenceForAgentBeetweenDateAllVentilation_nomatr3544() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2015, 4, 12).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);
		
		VentilDate vd2 = new VentilDate();
		vd2.setDateVentilation(new LocalDate(2015, 5, 3).toDate());
		vd2.setPaye(false);
		vd2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd2);

		// 1er ventilation
		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2015, 3, 16).toDate());
		va.setEtat(EtatPointageEnum.VALIDE);
		va.setIdAgent(9003544);
		va.setMinutesConcertee(900);
		va.setMinutesNonConcertee(0);
		va.setMinutesImmediat(0);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2015, 3, 30).toDate());
		va2.setEtat(EtatPointageEnum.VALIDE);
		va2.setIdAgent(9003544);
		va2.setMinutesConcertee(420);
		va2.setMinutesNonConcertee(0);
		va2.setMinutesImmediat(0);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		// 2e ventilation
		VentilAbsence va3 = new VentilAbsence();
		va3.setDateLundi(new LocalDate(2015, 3, 30).toDate());
		va3.setEtat(EtatPointageEnum.VALIDE);
		va3.setIdAgent(9003544);
		va3.setMinutesConcertee(420);
		va3.setMinutesNonConcertee(0);
		va3.setMinutesImmediat(0);
		va3.setVentilDate(vd2);
		ptgEntityManager.persist(va3);

		List<VentilAbsence> result = repository.getListOfVentilAbsenceForAgentBeetweenDateAllVentilation(3, 2015, 9003544, vd2.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(new Integer(9003544), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VALIDE, result.get(0).getEtat());
		assertEquals(420, result.get(0).getMinutesConcertee());
		assertEquals(va3.getIdVentilAbsence(), result.get(0).getIdVentilAbsence());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	// #15286 : bug de QUALIF : nomatr 3544
	// resultat d une ancienne ventilation affichee dans une nouvelle
	// car meme date de lundi, mais deux ventilations bien distinctes
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilHSForAgentBeetweenDate_nomatr3544() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2015, 4, 12).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);
		
		VentilDate vd2 = new VentilDate();
		vd2.setDateVentilation(new LocalDate(2015, 5, 3).toDate());
		vd2.setPaye(false);
		vd2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd2);

		// 1er ventilation
		VentilHsup va = new VentilHsup();
		va.setDateLundi(new LocalDate(2015, 3, 16).toDate());
		va.setEtat(EtatPointageEnum.VENTILE);
		va.setIdAgent(9003544);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilHsup va2 = new VentilHsup();
		va2.setDateLundi(new LocalDate(2015, 3, 30).toDate());
		va2.setEtat(EtatPointageEnum.VENTILE);
		va2.setIdAgent(9003544);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		// 2e ventilation
		VentilHsup va3 = new VentilHsup();
		va3.setDateLundi(new LocalDate(2015, 3, 30).toDate());
		va3.setEtat(EtatPointageEnum.VENTILE);
		va3.setIdAgent(9003544);
		va3.setVentilDate(vd2);
		ptgEntityManager.persist(va3);

		List<VentilHsup> result = repository.getListOfVentilHSForAgentBeetweenDate(3, 2015, 9003544, vd2.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(new Integer(9003544), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(va3.getIdVentilHSup(), result.get(0).getIdVentilHSup());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	// #15286 : bug de QUALIF : nomatr 3544
	// resultat d une ancienne ventilation affichee dans une nouvelle
	// car meme date de lundi, mais deux ventilations bien distinctes
	@Test
	@Transactional("ptgTransactionManager")
	public void getListOfVentilHSForAgentBeetweenDateAllVentilation_nomatr3544() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2015, 4, 12).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);
		
		VentilDate vd2 = new VentilDate();
		vd2.setDateVentilation(new LocalDate(2015, 5, 3).toDate());
		vd2.setPaye(false);
		vd2.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd2);

		// 1er ventilation
		VentilHsup va = new VentilHsup();
		va.setDateLundi(new LocalDate(2015, 3, 16).toDate());
		va.setEtat(EtatPointageEnum.VALIDE);
		va.setIdAgent(9003544);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilHsup va2 = new VentilHsup();
		va2.setDateLundi(new LocalDate(2015, 3, 30).toDate());
		va2.setEtat(EtatPointageEnum.VALIDE);
		va2.setIdAgent(9003544);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		// 2e ventilation
		VentilHsup va3 = new VentilHsup();
		va3.setDateLundi(new LocalDate(2015, 3, 30).toDate());
		va3.setEtat(EtatPointageEnum.VALIDE);
		va3.setIdAgent(9003544);
		va3.setVentilDate(vd2);
		ptgEntityManager.persist(va3);

		List<VentilHsup> result = repository.getListOfVentilHSForAgentBeetweenDateAllVentilation(3, 2015, 9003544, vd2.getIdVentilDate());

		assertEquals(1, result.size());
		assertEquals(new Integer(9003544), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VALIDE, result.get(0).getEtat());
		assertEquals(va3.getIdVentilHSup(), result.get(0).getIdVentilHSup());

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
}
