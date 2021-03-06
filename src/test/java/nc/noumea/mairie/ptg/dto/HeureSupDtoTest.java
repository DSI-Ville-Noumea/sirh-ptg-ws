package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.*;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.MotifHeureSup;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PtgComment;
import nc.noumea.mairie.ptg.domain.RefTypePointage;

import org.joda.time.DateTime;
import org.junit.Test;

public class HeureSupDtoTest {

	@Test
	public void ctor_withpointage() {

		// Given
		RefTypePointage tp = new RefTypePointage();
		tp.setIdRefTypePointage(1);
		tp.setLabel("LABEL DE TEST");
		Pointage p = new Pointage();
		p.setIdPointage(9);
		p.setIdAgent(905138);
		p.setDateDebut(new DateTime(2013, 4, 19, 0, 0, 0, 0).toDate());
		p.setDateFin(new DateTime(2013, 5, 19, 0, 0, 0, 0).toDate());
		p.setDateLundi(new DateTime(2013, 4, 19, 0, 0, 0, 0).toDate());
		p.setType(tp);
		p.setHeureSupRecuperee(true);
		p.setHeureSupRappelService(true);
		EtatPointage ep1 = new EtatPointage();
		ep1.setEtat(EtatPointageEnum.SAISI);
		ep1.setPointage(p);
		p.getEtats().add(ep1);
		PtgComment m = new PtgComment();
		m.setText("blabla");
		p.setMotif(m);
		PtgComment c = new PtgComment();
		c.setText("blibli");
		p.setCommentaire(c);

		// When
		HeureSupDto result = new HeureSupDto(p);

		// Then
		assertEquals(new Integer(9), result.getIdPointage());
		assertEquals(true, result.getRecuperee());
		assertEquals(true, result.getRappelService());
		assertEquals(0, (int) result.getIdRefEtat());
		assertEquals(p.getDateDebut(), result.getHeureDebut());
		assertEquals(p.getDateFin(), result.getHeureFin());
		assertEquals("blabla", result.getMotif());
		assertEquals("blibli", result.getCommentaire());
		assertNull(result.getIdMotifHsup());
	}

	@Test
	public void ctor_withpointage_MotifHsup() {

		// Given
		RefTypePointage tp = new RefTypePointage();
		tp.setIdRefTypePointage(1);
		tp.setLabel("LABEL DE TEST");

		MotifHeureSup motifHsup = new MotifHeureSup();
		motifHsup.setText("blabla");
		motifHsup.setIdMotifHsup(2);

		Pointage p = new Pointage();
		p.setIdPointage(9);
		p.setIdAgent(905138);
		p.setDateDebut(new DateTime(2013, 4, 19, 0, 0, 0, 0).toDate());
		p.setDateFin(new DateTime(2013, 5, 19, 0, 0, 0, 0).toDate());
		p.setDateLundi(new DateTime(2013, 4, 19, 0, 0, 0, 0).toDate());
		p.setType(tp);
		p.setHeureSupRecuperee(true);
		p.setHeureSupRappelService(true);
		EtatPointage ep1 = new EtatPointage();
		ep1.setEtat(EtatPointageEnum.SAISI);
		ep1.setPointage(p);
		p.getEtats().add(ep1);
		p.setMotifHsup(motifHsup);
		PtgComment c = new PtgComment();
		c.setText("blibli");
		p.setCommentaire(c);

		// When
		HeureSupDto result = new HeureSupDto(p);

		// Then
		assertEquals(new Integer(9), result.getIdPointage());
		assertEquals(true, result.getRecuperee());
		assertEquals(true, result.getRappelService());
		assertEquals(0, (int) result.getIdRefEtat());
		assertEquals(p.getDateDebut(), result.getHeureDebut());
		assertEquals(p.getDateFin(), result.getHeureFin());
		assertNull(result.getMotif());
		assertEquals("blibli", result.getCommentaire());
		assertEquals(2, (int) result.getIdMotifHsup());
	}
}
