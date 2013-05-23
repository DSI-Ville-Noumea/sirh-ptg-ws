package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPointagePK;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PtgComment;
import nc.noumea.mairie.ptg.domain.RefTypePointage;

import org.joda.time.DateTime;
import org.junit.Test;

public class AbsenceDtoTest {

	@Test
	public void ctor_withpointage() {

		// Given
		RefTypePointage tp = new RefTypePointage();
		tp.setIdRefTypePointage(1);
		tp.setLabel("LABEL DE TEST");
		Pointage p = new Pointage();
		p.setIdPointage(10);
		p.setDateDebut(new DateTime(2013, 4, 19, 0, 0, 0, 0).toDate());
		p.setDateFin(new DateTime(2013, 5, 19, 0, 0, 0, 0).toDate());
		p.setDateLundi(new DateTime(2013, 4, 19, 0, 0, 0, 0).toDate());
		p.setType(tp);
		p.setAbsenceConcertee(true);
		EtatPointage ep1 = new EtatPointage();
		ep1.setEtat(EtatPointageEnum.APPROUVE);
		EtatPointagePK pk = new EtatPointagePK();
		pk.setPointage(p);
		ep1.setEtatPointagePk(pk);
		p.getEtats().add(ep1);
		PtgComment m = new PtgComment();
		m.setText("blabla");
		p.setMotif(m);
		PtgComment c = new PtgComment();
		c.setText("blibli");
		p.setCommentaire(c);

		// When
		AbsenceDto result = new AbsenceDto(p);

		// Then
		assertEquals(new Integer(10), result.getIdPointage());
		assertEquals(true, result.getConcertee());
		assertEquals("APPROUVE", result.getEtat());
		assertEquals(p.getDateDebut(), result.getHeureDebut());
		assertEquals(p.getDateFin(), result.getHeureFin());
		assertEquals("blabla", result.getMotif());
		assertEquals("blibli", result.getCommentaire());
	}
}
