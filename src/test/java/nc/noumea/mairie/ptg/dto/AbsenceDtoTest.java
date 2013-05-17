package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.TypePointage;

import org.joda.time.DateTime;
import org.junit.Test;

public class AbsenceDtoTest {

	@Test
	public void ctor_withpointage() {

		// Given
		TypePointage tp = new TypePointage();
		tp.setIdTypePointage(1);
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
		ep1.setPointage(p);
		p.getEtats().add(ep1);

		// When
		AbsenceDto result = new AbsenceDto(p);

		// Then
		assertEquals(new Integer(10), result.getIdPointage());
		assertEquals(true, result.getConcertee());
		assertEquals("APPROUVE", result.getEtat());
		assertEquals(p.getDateDebut(), result.getHeureDebut());
		assertEquals(p.getDateFin(), result.getHeureFin());
	}
}
