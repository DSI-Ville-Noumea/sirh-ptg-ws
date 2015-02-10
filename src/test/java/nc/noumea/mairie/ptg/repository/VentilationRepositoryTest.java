package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
		pc.setQuantite(1);
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
		pc2.setQuantite(2);
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
		pc3.setQuantite(3);
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
		pc4.setQuantite(4);
		pc4.setRefPrime(rp);
		pc4.setType(rtp);
		ptgEntityManager.persist(pc4);

		List<PointageCalcule> result = repository.getListPointagesCalculesPrimeForVentilation(new Integer(9005138),
				new LocalDate(2013, 7, 23).toDate());

		assertEquals(1, result.size());
		assertEquals(EtatPointageEnum.APPROUVE, result.get(0).getEtat());
		assertEquals(new Integer(1), result.get(0).getQuantite());

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
		vp.setQuantite(1);
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
		vp.setQuantite(1);
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
		vp2.setQuantite(1);
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
		vp.setQuantite(3);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.VENTILE);
		vp2.setIdAgent(9005139);
		vp2.setQuantite(1);
		vp2.setVentilDate(vd);
		ptgEntityManager.persist(vp2);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp3.setEtat(EtatPointageEnum.VALIDE);
		vp3.setIdAgent(9005139);
		vp3.setQuantite(1);
		vp3.setVentilDate(vd);
		ptgEntityManager.persist(vp3);

		VentilPrime vp4 = new VentilPrime();
		vp4.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp4.setEtat(EtatPointageEnum.VENTILE);
		vp4.setIdAgent(9009999);
		vp4.setQuantite(1);
		vp4.setVentilDate(vd);
		ptgEntityManager.persist(vp4);

		VentilPrime vpBadQuantite = new VentilPrime();
		vpBadQuantite.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vpBadQuantite.setEtat(EtatPointageEnum.VENTILE);
		vpBadQuantite.setIdAgent(9005139);
		vpBadQuantite.setQuantite(0);
		vpBadQuantite.setVentilDate(vd);
		ptgEntityManager.persist(vpBadQuantite);

		List<Integer> listAgents = new ArrayList<Integer>();
		listAgents.add(9005138);
		listAgents.add(9005139);

		List<VentilPrime> result = repository.getListOfVentilPrimeForDateAgent(vd.getIdVentilDate(), listAgents, true);

		assertEquals(2, result.size());
		assertEquals(new Integer(9005138), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(new Integer(3), result.get(0).getQuantite());

		assertEquals(new Integer(9005139), result.get(1).getIdAgent());
		assertEquals(EtatPointageEnum.VENTILE, result.get(1).getEtat());
		assertEquals(new Integer(1), result.get(1).getQuantite());

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
		vp.setQuantite(3);
		vp.setVentilDate(vd);
		vp.setRefPrime(rp);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.VENTILE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(1);
		vp2.setVentilDate(vd);
		vp2.setRefPrime(rp);
		ptgEntityManager.persist(vp2);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp3.setEtat(EtatPointageEnum.VENTILE);
		vp3.setIdAgent(9005138);
		vp3.setQuantite(10);
		vp3.setVentilDate(vd2);
		vp3.setRefPrime(rp);
		ptgEntityManager.persist(vp3);

		VentilPrime vp4 = new VentilPrime();
		vp4.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp4.setEtat(EtatPointageEnum.VALIDE);
		vp4.setIdAgent(9005138);
		vp4.setQuantite(1);
		vp4.setVentilDate(vd);
		vp4.setRefPrime(rp);
		ptgEntityManager.persist(vp4);

		VentilPrime vp5 = new VentilPrime();
		vp5.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp5.setEtat(EtatPointageEnum.VENTILE);
		vp5.setIdAgent(9009999);
		vp5.setQuantite(1);
		vp5.setVentilDate(vd);
		vp5.setRefPrime(rp);
		ptgEntityManager.persist(vp5);

		List<VentilPrime> result = repository.getListVentilPrimesMoisForAgentAndVentilDateOrderByDateAsc(new Integer(
				9005138), vd.getIdVentilDate());

		assertEquals(2, result.size());
		assertEquals(EtatPointageEnum.VENTILE, result.get(0).getEtat());
		assertEquals(new Integer(3), result.get(0).getQuantite());

		assertEquals(EtatPointageEnum.VENTILE, result.get(1).getEtat());
		assertEquals(new Integer(1), result.get(1).getQuantite());

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
	public void getPriorVentilAbsenceForAgentAndDate() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilAbsence va = new VentilAbsence();
		va.setDateLundi(new LocalDate(2013, 7, 23).toDate());
		va.setEtat(EtatPointageEnum.APPROUVE);
		va.setIdAgent(9005139);
		va.setMinutesConcertee(10);
		va.setMinutesNonConcertee(10);
		va.setVentilDate(vd);
		ptgEntityManager.persist(va);

		VentilAbsence va2 = new VentilAbsence();
		va2.setDateLundi(new LocalDate(2013, 7, 24).toDate());
		va2.setEtat(EtatPointageEnum.EN_ATTENTE);
		va2.setIdAgent(9005140);
		va2.setMinutesConcertee(10);
		va2.setMinutesNonConcertee(10);
		va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);

		VentilAbsence result = repository.getPriorVentilAbsenceForAgentAndDate(new Integer(9005139), new LocalDate(
				2013, 7, 23).toDate(), va2);

		assertEquals(new LocalDate(2013, 7, 23).toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.APPROUVE, result.getEtat());
		assertEquals(10, result.getMinutesConcertee());

		VentilAbsence noResult = repository.getPriorVentilAbsenceForAgentAndDate(new Integer(9005138), new LocalDate(
				2013, 7, 23).toDate(), va2);

		assertNull(noResult);

		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}

	@Test
	@Transactional("ptgTransactionManager")
	public void getPriorVentilHSupAgentAndDate() {

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
		vh2.setEtat(EtatPointageEnum.APPROUVE);
		vh2.setIdAgent(9005139);
		vh2.setMAbsences(0);
		vh2.setMComposees(1);
		vh2.setMComposeesRecup(0);
		vh2.setMHorsContrat(11);
		vh2.setVentilDate(vd);
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
	public void getPriorVentilPrimeForAgentAndDate() {

		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);

		VentilPrime vp = new VentilPrime();
		vp.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp.setEtat(EtatPointageEnum.APPROUVE);
		vp.setIdAgent(9005138);
		vp.setQuantite(3);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.EN_ATTENTE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(3);
		vp2.setVentilDate(vd);
		ptgEntityManager.persist(vp2);

		VentilPrime result = repository.getPriorVentilPrimeForAgentAndDate(new Integer(9005138), new LocalDate(2013, 7,
				23).toDate(), vp2);

		assertEquals(new LocalDate(2013, 7, 23).toDate(), result.getDateDebutMois());
		assertEquals(EtatPointageEnum.APPROUVE, result.getEtat());
		assertEquals(new Integer(3), result.getQuantite());

		VentilPrime noResult = repository.getPriorVentilPrimeForAgentAndDate(new Integer(9005137), new LocalDate(2013,
				7, 25).toDate(), vp2);

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

		List<VentilAbsence> result = repository.getListOfVentilAbsenceForAgentBeetweenDate(2, 2014, 9005138);

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

		List<VentilHsup> result = repository.getListOfVentilHSForAgentBeetweenDate(2, 2014, 9005138);

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
		vp.setQuantite(3);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.VENTILE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(3);
		vp2.setVentilDate(vd);
		ptgEntityManager.persist(vp2);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp3.setEtat(EtatPointageEnum.VALIDE);
		vp3.setIdAgent(9005138);
		vp3.setQuantite(3);
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
		vp.setQuantite(3);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.VENTILE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(3);
		vp2.setVentilDate(vd);
		ptgEntityManager.persist(vp2);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp3.setEtat(EtatPointageEnum.VALIDE);
		vp3.setIdAgent(9005138);
		vp3.setQuantite(3);
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
		vp.setQuantite(3);
		vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);

		VentilPrime vp2 = new VentilPrime();
		vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
		vp2.setEtat(EtatPointageEnum.VENTILE);
		vp2.setIdAgent(9005138);
		vp2.setQuantite(3);
		vp2.setVentilDate(vd);
		ptgEntityManager.persist(vp2);

		VentilPrime vp3 = new VentilPrime();
		vp3.setDateDebutMois(new LocalDate(2013, 7, 18).toDate());
		vp3.setEtat(EtatPointageEnum.VALIDE);
		vp3.setIdAgent(9005138);
		vp3.setQuantite(3);
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
}
