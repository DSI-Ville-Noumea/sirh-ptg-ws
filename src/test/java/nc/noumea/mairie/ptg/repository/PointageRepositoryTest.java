package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.MairiePrimeTableEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(locations={"/META-INF/spring/applicationContext-test.xml"})
public class PointageRepositoryTest {

	@Autowired
	PointageRepository repository;
	
	@PersistenceContext(unitName = "ptgPersistenceUnit")
	EntityManager ptgEntityManager;
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointages_FilterByIdAgentDateAndTypePointage() {
		
		// Given
//		String seqSql = "CREATE SEQUENCE PTG_S_POINTAGE START WITH 1 INCREMENT BY 1 CACHE 1";
//		javax.persistence.Query q = ptgEntityManager.createNativeQuery(seqSql);
//		q.executeUpdate();
//		
//		String seqselectSql = "select PTG_S_POINTAGE.nextval as nb from dual;";
//		q = ptgEntityManager.createNativeQuery(seqselectSql);
//		BigInteger id = (BigInteger) q.getSingleResult();
//		BigInteger id2 = (BigInteger) q.getSingleResult();
//		BigInteger id3 = (BigInteger) q.getSingleResult();
		
		RefTypePointage abs = new RefTypePointage();
		abs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		abs.persist();
		RefTypePointage hSup = new RefTypePointage();
		hSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		hSup.persist();
		RefTypePointage prime = new RefTypePointage();
		prime.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		prime.persist();

		Pointage ptg = new Pointage();
		ptg.setIdAgent(9008765);
		ptg.setType(abs);
		ptg.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		ptg.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg.persist();
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
		
		Pointage ptg2 = new Pointage();
		ptg2.setIdAgent(9008765);
		ptg2.setType(abs);
		ptg2.setPointageParent(Pointage.findPointage(1));
		ptg2.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		ptg2.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg2.persist();
		
		Pointage ptg3 = new Pointage();
		ptg3.setIdAgent(9008766);
		ptg3.setType(abs);
		ptg3.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		ptg3.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg3.persist();
		
		Pointage ptg4 = new Pointage();
		ptg4.setIdAgent(9008765);
		ptg4.setType(prime);
		ptg4.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		ptg4.setDateDebut(new DateTime(2013, 7, 22, 8, 0, 0).toDate());
		ptg4.persist();
		
		Pointage ptg5 = new Pointage();
		ptg5.setIdAgent(9008765);
		ptg5.setType(abs);
		ptg5.setDateLundi(new LocalDate(2013, 7, 22).toDate());
		ptg5.setDateDebut(new DateTime(2013, 7, 22, 6, 0, 0).toDate());
		ptg5.setDateFin(new DateTime(2013, 7, 22, 7, 0, 0).toDate());
		ptg5.persist();
		
		Pointage ptg6 = new Pointage();
		ptg6.setIdAgent(9008765);
		ptg6.setType(abs);
		ptg6.setDateLundi(new LocalDate(2013, 7, 29).toDate());
		ptg6.setDateDebut(new DateTime(2013, 7, 29, 0, 0, 0).toDate());
		ptg6.setDateFin(new DateTime(2013, 7, 29, 0, 30, 0).toDate());
		ptg6.persist();

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
		assertEquals(ptg5.getAbsenceConcertee(), result.get(0).getAbsenceConcertee());
		assertEquals(ptg5.getCommentaire(), result.get(0).getCommentaire());
		assertEquals(ptg5.getDateLundi(), result.get(0).getDateLundi());
		assertEquals(ptg5.getHeureSupRecuperee(), result.get(0).getHeureSupRecuperee());
		assertEquals(ptg5.getIdAgent(), result.get(0).getIdAgent());
		assertEquals(ptg5.getIdPointage(), result.get(0).getIdPointage());
		assertEquals(ptg5.getType().getIdRefTypePointage(), result.get(0).getType().getIdRefTypePointage());
		assertEquals(ptg5.getTypePointageEnum().getValue(), result.get(0).getTypePointageEnum().getValue());
		
		
//		assertEquals(ptg2, result.get(1));
//		assertEquals(ptg, result.get(2));
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
		rp.persist();
		
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
		rp.persist();
		
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
		rp.persist();
		
		RefPrime rp2 = new RefPrime();
		rp2.setAide("Saisir l'heure de fin");
		rp2.setCalculee(true);
		rp2.setDescription(null);
		rp2.setLibelle("INDEMNITE BIS");
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp2.setNoRubr(7712);
		rp2.setStatut(AgentStatutEnum.F);
		rp2.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		rp2.persist();
		
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
		rp.persist();
		
		RefPrime rp2 = new RefPrime();
		rp2.setAide("Saisir l'heure de fin");
		rp2.setCalculee(true);
		rp2.setDescription(null);
		rp2.setLibelle("INDEMNITE BIS");
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp2.setNoRubr(7712);
		rp2.setStatut(AgentStatutEnum.C);
		rp2.setTypeSaisie(TypeSaisieEnum.PERIODE_HEURES);
		rp2.persist();
		
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
		rtp.persist();
		
		Pointage p = new Pointage();
		p.setAbsenceConcertee(false); 
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate()); 
		p.setHeureSupRecuperee(true);
		p.setIdAgent(9005138);
		p.setType(rtp);
		p.persist();
		
		Pointage p2 = new Pointage();
		p2.setAbsenceConcertee(true); 
		p2.setDateDebut(new LocalDate(2013, 7, 21).toDate());
		p2.setDateFin(new LocalDate(2013, 7, 30).toDate());
		p2.setDateLundi(new LocalDate(2013, 7, 23).toDate()); 
		p2.setHeureSupRecuperee(false);
		p2.setIdAgent(9005138);
		p2.setType(rtp);
		p2.persist();
		
		List<Pointage> result = repository.getPointagesForAgentAndDateOrderByIdDesc(9005138, new LocalDate(2013, 7, 23).toDate());
		
		assertEquals(2, result.size());
		assertEquals(true, result.get(0).getAbsenceConcertee());
		assertEquals(false, result.get(0).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 21).toDate(), result.get(0).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 30).toDate(), result.get(0).getDateFin());
		
		assertEquals(false, result.get(1).getAbsenceConcertee());
		assertEquals(true, result.get(1).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 22).toDate(), result.get(1).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 29).toDate(), result.get(1).getDateFin());
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
//	@Test H2 ne prend pas en compte la requete recursive
	@Transactional("ptgTransactionManager")
	public void getPointageArchives() {
		
		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		rtp.persist();
		
		Pointage p = new Pointage();
		p.setAbsenceConcertee(false); 
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate()); 
		p.setHeureSupRecuperee(true);
		p.setIdAgent(9005138);
		p.setType(rtp);
		p.persist();
		
		Pointage p2 = new Pointage();
		p.setPointageParent(p);
		p2.setAbsenceConcertee(true); 
		p2.setDateDebut(new LocalDate(2013, 7, 21).toDate());
		p2.setDateFin(new LocalDate(2013, 7, 30).toDate());
		p2.setDateLundi(new LocalDate(2013, 7, 23).toDate()); 
		p2.setHeureSupRecuperee(false);
		p2.setIdAgent(9005138);
		p2.setType(rtp);
		p2.persist();
		
		List<Pointage> result = repository.getPointageArchives(p.getIdPointage());
		
		assertEquals(2, result.size());
		assertEquals(true, result.get(0).getAbsenceConcertee());
		assertEquals(false, result.get(0).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 21).toDate(), result.get(0).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 30).toDate(), result.get(0).getDateFin());
		
		assertEquals(false, result.get(1).getAbsenceConcertee());
		assertEquals(true, result.get(1).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 22).toDate(), result.get(1).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 29).toDate(), result.get(1).getDateFin());
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void removePointageCalculesForDateAgent() {
		
		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		rtp.persist();
		
		Pointage p = new Pointage();
		p.setIdPointage(100);
		p.setAbsenceConcertee(false); 
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate()); 
		p.setHeureSupRecuperee(true);
		p.setIdAgent(9005138);
		p.setType(rtp);
		p.persist();
		
		repository.removePointageCalculesForDateAgent(9005138, new LocalDate(2013, 7, 23).toDate());
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getListPointages() {
		
		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		rtp.persist();
		
		Pointage p = new Pointage();
		p.setAbsenceConcertee(false); 
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate()); 
		p.setHeureSupRecuperee(true);
		p.setIdAgent(9005138);
		p.setType(rtp);
		p.persist();
		
		Pointage p2 = new Pointage();
		p2.setAbsenceConcertee(true); 
		p2.setDateDebut(new LocalDate(2013, 7, 21).toDate());
		p2.setDateFin(new LocalDate(2013, 7, 30).toDate());
		p2.setDateLundi(new LocalDate(2013, 7, 23).toDate()); 
		p2.setHeureSupRecuperee(false);
		p2.setIdAgent(9005138);
		p2.setType(rtp);
		p2.persist();
		
		
		List<Integer> listAgents = new ArrayList<Integer>();
		listAgents.add(9005138);
		List<Pointage> result = repository.getListPointages(listAgents, new LocalDate(2013, 7, 21).toDate(), new LocalDate(2013, 7, 22).toDate(), new Integer(1));
		
		assertEquals(1, result.size());
		assertEquals(true, result.get(0).getAbsenceConcertee());
		assertEquals(false, result.get(0).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 21).toDate(), result.get(0).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 30).toDate(), result.get(0).getDateFin());
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getPointagesVentilesForAgent() {
		
		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		rtp.persist();
		
		Pointage p = new Pointage();
		p.setAbsenceConcertee(false); 
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate()); 
		p.setHeureSupRecuperee(true);
		p.setIdAgent(9005138);
		p.setType(rtp);
		p.persist();
		
		Set<Pointage> pointages = new HashSet<Pointage>();
		pointages.add(p);
		
		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setPointages(pointages);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		vd.persist();
		
		List<Pointage> result = repository.getPointagesVentilesForAgent(new Integer(9005138), vd.getIdVentilDate());
		
		assertEquals(1, result.size());
		assertEquals(false, result.get(0).getAbsenceConcertee());
		assertEquals(true, result.get(0).getHeureSupRecuperee());
		assertEquals(new LocalDate(2013, 7, 22).toDate(), result.get(0).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 29).toDate(), result.get(0).getDateFin());
	}
	
	@Test
	@Transactional("ptgTransactionManager")
	public void getPointagesCalculesVentilesForAgent() {
		
		RefTypePointage rtp = new RefTypePointage();
		rtp.setIdRefTypePointage(1);
		rtp.persist();
		
		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		rp.persist();
		
		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		vd.persist();

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
		pc.persist();
		
		List<PointageCalcule> result = repository.getPointagesCalculesVentilesForAgent(new Integer(9005138), vd.getIdVentilDate());
		
		assertEquals(1, result.size()); 
		assertEquals(new Integer(1), result.get(0).getQuantite());
		assertEquals(new LocalDate(2013, 7, 22).toDate(), result.get(0).getDateDebut());
		assertEquals(new LocalDate(2013, 7, 29).toDate(), result.get(0).getDateFin());
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
		rp.persist();
		
		RefPrime rp2 = new RefPrime();
		rp2.setAide("Saisir l'heure de fin");
		rp2.setCalculee(true);
		rp2.setDescription(null);
		rp2.setLibelle("INDEMNITE BIS");
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp2.setNoRubr(7712);
		rp2.setStatut(AgentStatutEnum.C);
		rp2.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		rp2.persist();
		
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
		rp.persist();
		
		RefPrime rp2 = new RefPrime();
		rp2.setAide("Saisir l'heure de fin");
		rp2.setCalculee(true);
		rp2.setDescription(null);
		rp2.setLibelle("INDEMNITE BIS");
		rp2.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp2.setNoRubr(7712);
		rp2.setStatut(AgentStatutEnum.C);
		rp2.setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		rp2.persist();
		
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
		rtp.persist();
		
		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		rp.persist();
		
		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		vd.persist();

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
		pc.persist();
		
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
		rtp.persist();
		
		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		rp.persist();
		
		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 24).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		vd.persist();

		Pointage p = new Pointage();
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate()); 
		p.setIdAgent(9005139);
		p.setQuantite(1);
		p.setRefPrime(rp);
		p.setType(rtp);
		p.persist();
		
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
		rtp.persist();
		
		RefPrime rp = new RefPrime();
		rp.setAide("Saisir l'heure de début et l'heure de fin du roulement");
		rp.setCalculee(false);
		rp.setDescription(null);
		rp.setLibelle("INDEMNITE HORAIRE TRAVAIL DE NUIT DPM");
		rp.setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPPRM);
		rp.setNoRubr(7711);
		rp.setStatut(AgentStatutEnum.F);
		rp.setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		rp.persist();
		
		VentilDate vd = new VentilDate();
		vd.setDateVentilation(new LocalDate(2013, 7, 23).toDate());
		vd.setPaye(true);
		vd.setTypeChainePaie(TypeChainePaieEnum.SCV);
		vd.persist();

		Pointage p = new Pointage();
		p.setDateDebut(new LocalDate(2013, 7, 22).toDate());
		p.setDateFin(new LocalDate(2013, 7, 29).toDate());
		p.setDateLundi(new LocalDate(2013, 7, 23).toDate()); 
		p.setIdAgent(9005138);
		p.setQuantite(1);
		p.setRefPrime(rp);
		p.setType(rtp);
		p.persist();
		
		boolean result = repository.isPrimeSurPointageouPointageCalcule(new Integer(9005138), 100);
		
		assertFalse(result);
		
		ptgEntityManager.flush();
		ptgEntityManager.clear();
	}
	
	
}
