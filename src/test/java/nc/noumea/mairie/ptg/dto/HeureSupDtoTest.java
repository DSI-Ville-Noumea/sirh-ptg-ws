package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.TypePointage;

import org.joda.time.DateTime;
import org.junit.Test;

public class HeureSupDtoTest {

	@Test
	public void ctor_withpointage() {

		// Given
		TypePointage tp = new TypePointage();
		tp.setIdTypePointage(1);
		tp.setLabel("LABEL DE TEST");
		Pointage p = new Pointage();
		p.setIdPointage(9);
		p.setIdAgent(905138);
		p.setDateDebut(new DateTime(2013, 4, 19, 0, 0, 0, 0).toDate());
		p.setDateFin(new DateTime(2013, 5, 19, 0, 0, 0, 0).toDate());
		p.setDateLundi(new DateTime(2013, 4, 19, 0, 0, 0, 0).toDate());
		p.setType(tp);
		p.setHeureSupPayee(true);
		EtatPointage ep1 = new EtatPointage(); 
		ep1.setEtat(EtatPointageEnum.SAISI);
		ep1.setPointage(p);
		p.getEtats().add(ep1);

		// When
		HeureSupDto result = new HeureSupDto(p);

		// Then
		assertEquals(new Integer(9), result.getIdPointage());
		assertEquals(true, result.getPayee());
		assertEquals("SAISI", result.getEtat());
		assertEquals(p.getDateDebut(), result.getHeureDebut());
		assertEquals(p.getDateFin(), result.getHeureFin());
	}
}
