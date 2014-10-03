package nc.noumea.mairie.ptg.repository;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.domain.Spacti;
import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.domain.SppactId;
import nc.noumea.mairie.domain.Sppprm;
import nc.noumea.mairie.domain.SppprmId;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext-test.xml" })
public class ExportPaieRepositoryTest {

	@Autowired
	ExportPaieRepository repository;

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	EntityManager sirhEntityManager;

	@Test
	@Transactional("sirhTransactionManager")
	public void deleteSppactForDayAndAgent_OK() {
		
		Date dateDebut = new Date();
		Integer dateJour = 20140923;
		String codeActi = "codeActvite";
		
		Spacti activite = new Spacti();
			activite.setCodeActvite(codeActi);
		sirhEntityManager.persist(activite);
		
		SppactId id = new SppactId();
			id.setActivite(activite);
			id.setDateJour(dateJour);
			id.setNomatr(5138);
		Sppact spAct = new Sppact();
			spAct.setId(id);
		sirhEntityManager.persist(spAct);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9005138)).thenReturn(5138);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateDebut)).thenReturn(dateJour);
		
		ReflectionTestUtils.setField(repository, "helperService", helperService);
		
		assertEquals(1, repository.deleteSppactForDayAndAgent(9005138, dateDebut, codeActi));
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void deleteSppactForDayAndAgent_badCodeActi() {
		
		Date dateDebut = new Date();
		Integer dateJour = 20140923;
		String codeActi = "codeActvite";
		
		Spacti activite = new Spacti();
			activite.setCodeActvite(codeActi);
		sirhEntityManager.persist(activite);
		
		SppactId id = new SppactId();
			id.setActivite(activite);
			id.setDateJour(dateJour);
			id.setNomatr(5138);
		Sppact spAct = new Sppact();
			spAct.setId(id);
		sirhEntityManager.persist(spAct);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9005138)).thenReturn(5138);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateDebut)).thenReturn(dateJour);
		
		ReflectionTestUtils.setField(repository, "helperService", helperService);
		
		assertEquals(0, repository.deleteSppactForDayAndAgent(9005138, dateDebut, "codeActvite2"));
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void deleteSppactForDayAndAgent_badAgent() {
		
		Date dateDebut = new Date();
		Integer dateJour = 20140923;
		String codeActi = "codeActvite";
		
		Spacti activite = new Spacti();
			activite.setCodeActvite(codeActi);
		sirhEntityManager.persist(activite);
		
		SppactId id = new SppactId();
			id.setActivite(activite);
			id.setDateJour(dateJour);
			id.setNomatr(5199);
		Sppact spAct = new Sppact();
			spAct.setId(id);
		sirhEntityManager.persist(spAct);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9005138)).thenReturn(5138);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateDebut)).thenReturn(dateJour);
		
		ReflectionTestUtils.setField(repository, "helperService", helperService);
		
		assertEquals(0, repository.deleteSppactForDayAndAgent(9005138, dateDebut, codeActi));
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void deleteSppactForDayAndAgent_badDate() {
		
		Date dateDebut = new Date();
		Integer dateJour = 20140923;
		String codeActi = "codeActvite";
		
		Spacti activite = new Spacti();
			activite.setCodeActvite(codeActi);
		sirhEntityManager.persist(activite);
		
		SppactId id = new SppactId();
			id.setActivite(activite);
			id.setDateJour(dateJour);
			id.setNomatr(5138);
		Sppact spAct = new Sppact();
			spAct.setId(id);
		sirhEntityManager.persist(spAct);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9005138)).thenReturn(5138);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateDebut)).thenReturn(20140924);
		
		ReflectionTestUtils.setField(repository, "helperService", helperService);
		
		assertEquals(0, repository.deleteSppactForDayAndAgent(9005138, dateDebut, codeActi));
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void deleteSppprmForDayAndNorubr_OK() {
		
		Date dateDebut = new Date();
		Integer dateJour = 20140923;
		Integer noRubr = 123;
		
		SppprmId id = new SppprmId();
			id.setNoRubr(noRubr);
			id.setDatJour(dateJour);
			id.setNomatr(5138);
		Sppprm sppprm = new Sppprm();
			sppprm.setId(id);
		sirhEntityManager.persist(sppprm);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9005138)).thenReturn(5138);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateDebut)).thenReturn(dateJour);
		
		ReflectionTestUtils.setField(repository, "helperService", helperService);
		
		assertEquals(1, repository.deleteSppprmForDayAndNorubr(9005138, dateDebut, noRubr));
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void deleteSppprmForDayAndNorubr_badAgent() {
		
		Date dateDebut = new Date();
		Integer dateJour = 20140923;
		Integer noRubr = 123;
		
		SppprmId id = new SppprmId();
			id.setNoRubr(noRubr);
			id.setDatJour(dateJour);
			id.setNomatr(5138);
		Sppprm sppprm = new Sppprm();
			sppprm.setId(id);
		sirhEntityManager.persist(sppprm);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9005138)).thenReturn(5138);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateDebut)).thenReturn(dateJour);
		
		ReflectionTestUtils.setField(repository, "helperService", helperService);
		
		assertEquals(0, repository.deleteSppprmForDayAndNorubr(9005999, dateDebut, noRubr));
	}
	
	@Test
	@Transactional("sirhTransactionManager")
	public void deleteSppprmForDayAndNorubr_badDate() {
		
		Date dateDebut = new Date();
		Integer dateJour = 20140923;
		Integer noRubr = 123;
		
		SppprmId id = new SppprmId();
			id.setNoRubr(noRubr);
			id.setDatJour(dateJour);
			id.setNomatr(5138);
		Sppprm sppprm = new Sppprm();
			sppprm.setId(id);
		sirhEntityManager.persist(sppprm);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9005138)).thenReturn(5138);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateDebut)).thenReturn(dateJour+1);
		
		ReflectionTestUtils.setField(repository, "helperService", helperService);
		
		assertEquals(0, repository.deleteSppprmForDayAndNorubr(9005138, dateDebut, noRubr));
	}

	@Test
	@Transactional("sirhTransactionManager")
	public void deleteSppprmForDayAndNorubr_badnoRubr() {
		
		Date dateDebut = new Date();
		Integer dateJour = 20140923;
		Integer noRubr = 123;
		
		SppprmId id = new SppprmId();
			id.setNoRubr(noRubr);
			id.setDatJour(dateJour);
			id.setNomatr(5138);
		Sppprm sppprm = new Sppprm();
			sppprm.setId(id);
		sirhEntityManager.persist(sppprm);
		
		HelperService helperService = Mockito.mock(HelperService.class);
		Mockito.when(helperService.getMairieMatrFromIdAgent(9005138)).thenReturn(5138);
		Mockito.when(helperService.getIntegerDateMairieFromDate(dateDebut)).thenReturn(dateJour);
		
		ReflectionTestUtils.setField(repository, "helperService", helperService);
		
		assertEquals(0, repository.deleteSppprmForDayAndNorubr(9005138, dateDebut, 351));
	}
}
