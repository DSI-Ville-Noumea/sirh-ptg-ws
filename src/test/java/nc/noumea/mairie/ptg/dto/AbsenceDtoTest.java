package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.EtatPointage;
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
		p.setDateDebut(new DateTime(2013, 4, 19, 0, 0, 0, 0).toDate());
		p.setDateFin(new DateTime(2013, 5, 19, 0, 0, 0, 0).toDate());
		p.setDateLundi(new DateTime(2013, 4, 19, 0, 0, 0, 0).toDate());
		p.setQuantite(2);
		p.setType(tp);
		EtatPointage ep1 = new EtatPointage();
		ep1.setEtat('E');
		ep1.setPointage(p);
		p.getEtats().add(ep1);

		// When
		AbsenceDto result = new AbsenceDto(p);

		// Then
		assertEquals(p.getQuantite(), result.getQuantite());
		assertEquals(p.getDateDebut(), result.getHeureDebut());
		assertEquals(p.getDateFin(), result.getHeureFin());
		assertEquals(tp.getLabel(), result.getTypePrime());
		assertEquals(ep1.getEtat(), result.getEtatsPointage().get(0).getEtat());
	}
}
