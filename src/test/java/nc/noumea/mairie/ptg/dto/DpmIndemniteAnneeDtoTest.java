package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.DpmIndemAnnee;

import org.junit.Test;

public class DpmIndemniteAnneeDtoTest {

	@Test
	public void DpmIndemniteAnneeDto_construct() {
		
		DpmIndemAnnee dpmAnnee = new DpmIndemAnnee();
		dpmAnnee.setIdDpmIndemAnnee(1);
		dpmAnnee.setAnnee(2016);
		dpmAnnee.setDateDebut(new Date());
		dpmAnnee.setDateFin(new Date());

		DpmIndemniteAnneeDto result = new DpmIndemniteAnneeDto(dpmAnnee, false);
		
		assertEquals(dpmAnnee.getIdDpmIndemAnnee(), result.getIdDpmIndemAnnee());
		assertEquals(dpmAnnee.getAnnee(), result.getAnnee());
		assertEquals(dpmAnnee.getDateDebut(), result.getDateDebut());
		assertEquals(dpmAnnee.getDateFin(), result.getDateFin());
	}
}
