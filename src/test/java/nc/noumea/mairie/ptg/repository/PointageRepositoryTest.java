package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.runner.RunWith;
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
	
//	@Test
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
		assertEquals(1, result.size());
		assertEquals(ptg5, result.get(0));
		assertEquals(ptg2, result.get(1));
		assertEquals(ptg, result.get(2));
	}
}
