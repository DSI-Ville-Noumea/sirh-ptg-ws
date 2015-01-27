package nc.noumea.mairie.repository;

import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.SpcarrId;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/spring/applicationContext-test.xml" })
public class MairieRepositoryTest {

	@PersistenceContext(unitName = "sirhPersistenceUnit")
	EntityManager sirhEntityManager;
	
	@Autowired
	MairieRepository repository;
	
	@Test
	@Transactional("sirhTransactionManager")
	public void getAgentCurrentCarriere() {
		
		SpcarrId id = new SpcarrId();
		id.setDatdeb(20141201);
		id.setNomatr(5138);
		
		Spcarr carr = new Spcarr();
		carr.setCdcate(1);
		carr.setId(id);
		carr.setDateFin(20141231);
		sirhEntityManager.persist(carr);
		
		Spcarr result = repository.getAgentCurrentCarriere(5138, new DateTime(2014,12,15,0,0,0).toDate());
		
		assertNotNull(result);
		
		
		
		//select carr from Spcarr carr where carr.id.nomatr = :nomatr and carr.id.datdeb <= :todayFormatMairie and (carr.dateFin = 0 or carr.dateFin >= :todayFormatMairie)
	}
}
