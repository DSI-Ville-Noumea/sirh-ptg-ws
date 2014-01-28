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
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.MairiePrimeTableEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
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
@ContextConfiguration(locations={"/META-INF/spring/applicationContext-test.xml"})
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
		
		//ok
		Pointage ptg = new Pointage();
			ptg.setIdAgent(9008761);
			ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
			ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
			ptg.setAbsenceConcertee(true);
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
		
		// ok
		Pointage ptg2 = new Pointage();
			ptg2.setIdAgent(9008761);
			ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
			ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
			ptg2.setAbsenceConcertee(false);
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
			ptg3.setAbsenceConcertee(false);
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
			ptg4.setAbsenceConcertee(false);
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
			ptg5.setAbsenceConcertee(false);
			ptg5.setType(rtp);
		ptgEntityManager.persist(ptg5);
		
		EtatPointage ep5 = new EtatPointage();
			ep5.setDateEtat(new LocalDate(2013, 7, 29).toDate());
			ep5.setDateMaj(new LocalDate(2013, 7, 22).toDate());
			ep5.setEtat(EtatPointageEnum.APPROUVE);
			ep5.setIdAgent(9008761);
			ep5.setPointage(ptg5);
		ptgEntityManager.persist(ep5);
		
		List<Date> result = repository.getDistinctDatesOfPointages(new Integer(9008761), new LocalDate(2013, 7, 24).toDate(), new LocalDate(2013, 7, 26).toDate());
		
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
		
		Pointage ptg = new Pointage();
			ptg.setIdAgent(9008761);
			ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
			ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
			ptg.setAbsenceConcertee(true);
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
		
		Pointage ptg2 = new Pointage();
			ptg2.setIdAgent(9008762);
			ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
			ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
			ptg2.setAbsenceConcertee(false);
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
			ptg3.setAbsenceConcertee(false);
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
			ptg4.setAbsenceConcertee(false);
			ptg4.setType(rtp);
		ptgEntityManager.persist(ptg4);
		
		EtatPointage ep4 = new EtatPointage();
			ep4.setDateEtat(new LocalDate(2013, 7, 29).toDate());
			ep4.setDateMaj(new LocalDate(2013, 7, 22).toDate());
			ep4.setEtat(EtatPointageEnum.APPROUVE);
			ep4.setIdAgent(9008764);
			ep4.setPointage(ptg4);
		ptgEntityManager.persist(ep4);
		
		List<Integer> result = repository.getListIdAgentsForVentilationByDateAndEtat(new LocalDate(2013, 7, 24).toDate(), new LocalDate(2013, 7, 26).toDate());
		
		assertEquals(2, result.size());
		assertEquals(9008761, result.get(0).intValue());
		assertEquals(9008762, result.get(1).intValue());
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
//	@Test
	@Transactional("ptgTransactionManager")
	public void getListIdAgentsForExportPaie() {

//		Pointage ptg = new Pointage();
//		ptg.setIdAgent(9008765);
//		ptg.setDateLundi(new LocalDate(2013, 7, 22).toDate());
//		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
//
//		Set<Pointage> pointages = new HashSet<Pointage>();
//		pointages.add(ptg);
//		
//		VentilDate vd = new VentilDate();
//		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
//		vd.setPaye(true);
//		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
//		vd.setPointages(pointages);
//		vd.persist();
//		
//		List<Integer> result = repository.getListIdAgentsForExportPaie(vd.getIdVentilDate());
//		
//		assertEquals(1, result.size());
//		assertEquals(9008765, result.get(0).intValue());
//		
//		ptgEntityManager.flush();
//		ptgEntityManager.clear();
		
		//TODO pas insert dans la table de relation PTG_POINTAGE_VENTIL_DATE avec VentilDate.persist
		// du coup pas de resultat
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesAbsenceAndHSupForVentilation() {

		RefTypePointage rtp = new RefTypePointage();
			rtp.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		ptgEntityManager.persist(rtp);
		
		Pointage ptg = new Pointage();
			ptg.setIdAgent(9008765);
			ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
			ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
			ptg.setAbsenceConcertee(true);
			ptg.setType(rtp);
		ptgEntityManager.persist(ptg);
		
		RefTypePointage rtp2 = new RefTypePointage();
			rtp2.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtp2);
		
		Pointage ptg2 = new Pointage();
			ptg2.setIdAgent(9008765);
			ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
			ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
			ptg2.setAbsenceConcertee(false);
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
			ptg3.setAbsenceConcertee(false);
			ptg3.setType(rtp);
		ptgEntityManager.persist(ptg3);
		
		EtatPointage ep3 = new EtatPointage();
			ep3.setDateEtat(new LocalDate(2013, 7, 29).toDate());
			ep3.setDateMaj(new LocalDate(2013, 7, 22).toDate());
			ep3.setEtat(EtatPointageEnum.APPROUVE);
			ep3.setIdAgent(9008765);
			ep3.setPointage(ptg3);
		ptgEntityManager.persist(ep3);
		
		List<Pointage> result = repository.getListPointagesAbsenceAndHSupForVentilation(9008765, new LocalDate(2013, 7, 24).toDate(), new LocalDate(2013, 7, 26).toDate(), new LocalDate(2013, 7, 20).toDate());

		assertEquals(2, result.size());

		assertEquals(new DateTime(2013, 7, 23, 8, 0, 0).toDate(), result.get(0).getDateDebut());
		assertFalse(result.get(0).getAbsenceConcertee());
		assertEquals(RefTypePointageEnum.ABSENCE, result.get(0).getTypePointageEnum());
		
		assertEquals(new DateTime(2013, 7, 22, 8, 0, 0).toDate(), result.get(1).getDateDebut());
		assertTrue(result.get(1).getAbsenceConcertee());
		assertEquals(RefTypePointageEnum.H_SUP, result.get(1).getTypePointageEnum());
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesPrimeForVentilation() {
		
		RefTypePointage rtp = new RefTypePointage();
			rtp.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		ptgEntityManager.persist(rtp);
		
		Pointage ptg = new Pointage();
			ptg.setIdAgent(9008765);
			ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
			ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
			ptg.setAbsenceConcertee(true);
			ptg.setType(rtp);
		ptgEntityManager.persist(ptg);
		
		RefTypePointage rtp2 = new RefTypePointage();
			rtp2.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		ptgEntityManager.persist(rtp2);
		
		Pointage ptg2 = new Pointage();
			ptg2.setIdAgent(9008765);
			ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
			ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
			ptg2.setAbsenceConcertee(false);
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
		
		List<Pointage> result = repository.getListPointagesPrimeForVentilation(9008765, new LocalDate(2013, 7, 24).toDate(), new LocalDate(2013, 7, 26).toDate(), new LocalDate(2013, 7, 20).toDate());

		assertEquals(1, result.size());
		assertEquals(new DateTime(2013, 7, 22, 8, 0, 0).toDate(), result.get(0).getDateDebut());
		assertTrue(result.get(0).getAbsenceConcertee());
		assertEquals(RefTypePointageEnum.PRIME, result.get(0).getTypePointageEnum());
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointagesForPrimesCalculees() {
		
		Pointage ptg = new Pointage();
			ptg.setIdAgent(9008765);
			ptg.setDateLundi(new LocalDate(2013, 7, 20).toDate());
			ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
			ptg.setAbsenceConcertee(true);
		ptgEntityManager.persist(ptg);
		
		Pointage ptg2 = new Pointage();
			ptg2.setIdAgent(9008765);
			ptg2.setDateLundi(new LocalDate(2013, 7, 20).toDate());
			ptg2.setDateDebut(new DateTime(2013, 7, 23, 8, 0, 0).toDate());
			ptg2.setAbsenceConcertee(false);
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
		
		List<Pointage> result = repository.getListPointagesForPrimesCalculees(9008765, new LocalDate(2013, 7, 24).toDate(), new LocalDate(2013, 7, 26).toDate(), new LocalDate(2013, 7, 20).toDate());
		
		assertEquals(1, result.size());
		assertEquals(new DateTime(2013, 7, 22, 8, 0, 0).toDate(), result.get(0).getDateDebut());
		assertTrue(result.get(0).getAbsenceConcertee());
		
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
		
		List<PointageCalcule> result = repository.getListPointagesCalculesPrimeForVentilation(new Integer(9005138), new LocalDate(2013, 7, 23).toDate());
		
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
	public void removeVentilationsForDateAgentAndType() {
		
		VentilDate vd = new VentilDate();
			vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
			vd.setPaye(true);
			vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		ptgEntityManager.persist(vd);
		
		VentilPrime vp = new VentilPrime();
			vp.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
			vp.setEtat(EtatPointageEnum.APPROUVE);
			vp.setIdAgent(9005138);
			vp.setQuantite(1);
			vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);
		
		VentilHsup vh = new VentilHsup();
			vh.setDateLundi(new LocalDate(2013, 7, 23).toDate());
			vh.setEtat(EtatPointageEnum.APPROUVE);
			vh.setIdAgent(9005139);
			vh.setMAbsences(0);
			vh.setMComplementaires(1);
			vh.setMComplementairesRecup(0);
			vh.setMComposees(1);
			vh.setMComposeesRecup(0);
			vh.setMHorsContrat(1);
			vh.setVentilDate(vd);
		ptgEntityManager.persist(vh);
		
		VentilAbsence va = new VentilAbsence();
			va.setDateLundi(new LocalDate(2013, 7, 23).toDate());
			va.setEtat(EtatPointageEnum.APPROUVE);
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
		List<VentilPrime> resultPrime = repository.getListOfVentilPrimeForDateAgent(vd.getIdVentilDate(), listAgents);
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
		resultPrime = repository.getListOfVentilPrimeForDateAgent(vd.getIdVentilDate(), listAgents);
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
			va.setEtat(EtatPointageEnum.APPROUVE);
			va.setIdAgent(9005139);
			va.setMinutesConcertee(10);
			va.setMinutesNonConcertee(10);
			va.setVentilDate(vd);
		ptgEntityManager.persist(va);
		
		VentilAbsence va2 = new VentilAbsence();
			va2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
			va2.setEtat(EtatPointageEnum.EN_ATTENTE);
			va2.setIdAgent(9005140);
			va2.setMinutesConcertee(10);
			va2.setMinutesNonConcertee(10);
			va2.setVentilDate(vd);
		ptgEntityManager.persist(va2);
		
		List<Integer> listAgents = new ArrayList<Integer>();
		listAgents.add(9005139);
		listAgents.add(9005140);
		
		List<VentilAbsence> result = repository.getListOfVentilAbsenceForDateAgent(vd.getIdVentilDate(), listAgents);
		
		assertEquals(2, result.size());
		assertEquals(new Integer(9005139), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.APPROUVE, result.get(0).getEtat());

		assertEquals(new Integer(9005140), result.get(1).getIdAgent());
		assertEquals(EtatPointageEnum.EN_ATTENTE, result.get(1).getEtat());
		
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
			vp.setEtat(EtatPointageEnum.APPROUVE);
			vp.setIdAgent(9005138);
			vp.setQuantite(3);
			vp.setVentilDate(vd);
		ptgEntityManager.persist(vp);
		
		VentilPrime vp2 = new VentilPrime();
			vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
			vp2.setEtat(EtatPointageEnum.SAISI);
			vp2.setIdAgent(9005139);
			vp2.setQuantite(1);
			vp2.setVentilDate(vd);
		ptgEntityManager.persist(vp2);
		
		List<Integer> listAgents = new ArrayList<Integer>();
		listAgents.add(9005138);
		listAgents.add(9005139);
		
		List<VentilPrime> result = repository.getListOfVentilPrimeForDateAgent(vd.getIdVentilDate(), listAgents);
		
		assertEquals(2, result.size());
		assertEquals(new Integer(9005138), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.APPROUVE, result.get(0).getEtat());
		assertEquals(new Integer(3), result.get(0).getQuantite());

		assertEquals(new Integer(9005139), result.get(1).getIdAgent());
		assertEquals(EtatPointageEnum.SAISI, result.get(1).getEtat());
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
			vh.setEtat(EtatPointageEnum.VALIDE);
			vh.setIdAgent(9005138);
			vh.setMAbsences(3);
			vh.setMComplementaires(1);
			vh.setMComplementairesRecup(0);
			vh.setMComposees(5);
			vh.setMComposeesRecup(1);
			vh.setMHorsContrat(1);
			vh.setVentilDate(vd);
		ptgEntityManager.persist(vh);
		
		VentilHsup vh2 = new VentilHsup();
			vh2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
			vh2.setEtat(EtatPointageEnum.APPROUVE);
			vh2.setIdAgent(9005139);
			vh2.setMAbsences(0);
			vh2.setMComplementaires(1);
			vh2.setMComplementairesRecup(0);
			vh2.setMComposees(1);
			vh2.setMComposeesRecup(0);
			vh2.setMHorsContrat(11);
			vh2.setVentilDate(vd);
		ptgEntityManager.persist(vh2);
		
		List<Integer> listAgents = new ArrayList<Integer>();
		listAgents.add(9005138);
		listAgents.add(9005139);
		
		List<VentilHsup> result = repository.getListOfVentilHSForDateAgent(vd.getIdVentilDate(), listAgents);
		
		assertEquals(2, result.size());
		assertEquals(new Integer(9005138), result.get(0).getIdAgent());
		assertEquals(EtatPointageEnum.VALIDE, result.get(0).getEtat()); 
		assertEquals(3, result.get(0).getMAbsences());
		assertEquals(5, result.get(0).getMComposees());
		assertEquals(1, result.get(0).getMHorsContrat());
		

		assertEquals(new Integer(9005139), result.get(1).getIdAgent());
		assertEquals(EtatPointageEnum.APPROUVE, result.get(1).getEtat());
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
			vh.setEtat(EtatPointageEnum.VALIDE);
			vh.setIdAgent(9005138);
			vh.setMAbsences(3);
			vh.setMComplementaires(1);
			vh.setMComplementairesRecup(0);
			vh.setMComposees(5);
			vh.setMComposeesRecup(1);
			vh.setMHorsContrat(1);
			vh.setVentilDate(vd);
		ptgEntityManager.persist(vh);
		
		VentilHsup vh2 = new VentilHsup();
			vh2.setDateLundi(new LocalDate(2013, 7, 23).toDate());
			vh2.setEtat(EtatPointageEnum.APPROUVE);
			vh2.setIdAgent(9005138);
			vh2.setMAbsences(0);
			vh2.setMComplementaires(1);
			vh2.setMComplementairesRecup(0);
			vh2.setMComposees(1);
			vh2.setMComposeesRecup(0);
			vh2.setMHorsContrat(11);
			vh2.setVentilDate(vd);
		ptgEntityManager.persist(vh2);
		
		VentilHsup vh3 = new VentilHsup();
			vh3.setDateLundi(new LocalDate(2013, 7, 23).toDate());
			vh3.setEtat(EtatPointageEnum.SAISI);
			vh3.setIdAgent(9005138);
			vh3.setMAbsences(10);
			vh3.setMComplementaires(1);
			vh3.setMComplementairesRecup(0);
			vh3.setMComposees(1);
			vh3.setMComposeesRecup(0);
			vh3.setMHorsContrat(11);
			vh3.setVentilDate(vd2);
		ptgEntityManager.persist(vh3);
		
		List<VentilHsup> result = repository.getListVentilHSupForAgentAndVentilDateOrderByDateAsc(new Integer(9005138), vd.getIdVentilDate());

		assertEquals(2, result.size());
		assertEquals(EtatPointageEnum.VALIDE, result.get(0).getEtat());
		assertEquals(3, result.get(0).getMAbsences());
		
		assertEquals(EtatPointageEnum.APPROUVE, result.get(1).getEtat());
		assertEquals(0, result.get(1).getMAbsences());
		
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
			vp.setEtat(EtatPointageEnum.APPROUVE);
			vp.setIdAgent(9005138);
			vp.setQuantite(3);
			vp.setVentilDate(vd);
			vp.setRefPrime(rp);
		ptgEntityManager.persist(vp);
		
		VentilPrime vp2 = new VentilPrime();
			vp2.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
			vp2.setEtat(EtatPointageEnum.SAISI);
			vp2.setIdAgent(9005138);
			vp2.setQuantite(1);
			vp2.setVentilDate(vd);
			vp2.setRefPrime(rp);
		ptgEntityManager.persist(vp2);
		
		VentilPrime vp3 = new VentilPrime();
			vp3.setDateDebutMois(new LocalDate(2013, 7, 23).toDate());
			vp3.setEtat(EtatPointageEnum.REFUSE_DEFINITIVEMENT);
			vp3.setIdAgent(9005138);
			vp3.setQuantite(10);
			vp3.setVentilDate(vd2);
			vp3.setRefPrime(rp);
		ptgEntityManager.persist(vp3);
		
		List<VentilPrime> result = repository.getListVentilPrimesMoisForAgentAndVentilDateOrderByDateAsc(new Integer(9005138), vd.getIdVentilDate());
		
		assertEquals(2, result.size());
		assertEquals(EtatPointageEnum.APPROUVE, result.get(0).getEtat());
		assertEquals(new Integer(3), result.get(0).getQuantite());
		
		assertEquals(EtatPointageEnum.SAISI, result.get(1).getEtat());
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
		
		VentilAbsence result = repository.getPriorVentilAbsenceForAgentAndDate(new Integer(9005139), new LocalDate(2013, 7, 23).toDate(), va2);

		assertEquals(new LocalDate(2013, 7, 23).toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.APPROUVE, result.getEtat());
		assertEquals(10, result.getMinutesConcertee());
		
		VentilAbsence noResult = repository.getPriorVentilAbsenceForAgentAndDate(new Integer(9005138), new LocalDate(2013, 7, 23).toDate(), va2);
		
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
			vh.setMComplementaires(1);
			vh.setMComplementairesRecup(0);
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
			vh2.setMComplementaires(1);
			vh2.setMComplementairesRecup(0);
			vh2.setMComposees(1);
			vh2.setMComposeesRecup(0);
			vh2.setMHorsContrat(11);
			vh2.setVentilDate(vd);
		ptgEntityManager.persist(vh2);
		
		VentilHsup result = repository.getPriorVentilHSupAgentAndDate(new Integer(9005138), new LocalDate(2013, 7, 23).toDate(), vh2);

		assertEquals(new LocalDate(2013, 7, 23).toDate(), result.getDateLundi());
		assertEquals(EtatPointageEnum.VALIDE, result.getEtat());
		assertEquals(5, result.getMComposees());
		
		VentilHsup noResult = repository.getPriorVentilHSupAgentAndDate(new Integer(9005138), new LocalDate(2013, 7, 25).toDate(), vh2);
		
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
		
		VentilPrime result = repository.getPriorVentilPrimeForAgentAndDate(new Integer(9005138), new LocalDate(2013, 7, 23).toDate(), vp2);
		
		assertEquals(new LocalDate(2013, 7, 23).toDate(), result.getDateDebutMois());
		assertEquals(EtatPointageEnum.APPROUVE, result.getEtat());
		assertEquals(new Integer(3), result.getQuantite());

		VentilPrime noResult = repository.getPriorVentilPrimeForAgentAndDate(new Integer(9005137), new LocalDate(2013, 7, 25).toDate(), vp2);
		
		assertNull(noResult);
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
}
