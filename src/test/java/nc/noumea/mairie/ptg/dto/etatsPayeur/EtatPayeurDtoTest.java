package nc.noumea.mairie.ptg.dto.etatsPayeur;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;

import org.joda.time.LocalDate;
import org.junit.Test;

public class EtatPayeurDtoTest {

	@Test
	public void EtatPayeurDto_default_ctor() {
		
		// When
		EtatPayeurDto result = new EtatPayeurDto();
		
		// Then
		assertEquals(0, result.getAbsences().size());
		assertEquals(0, result.getHeuresSup().size());
		assertEquals(0, result.getPrimes().size());
		
	}
	
	@Test
	public void EtatPayeurDto_ctor() {
		
		// Given
		TypeChainePaieEnum cp = TypeChainePaieEnum.SCV;
		AgentStatutEnum statut = AgentStatutEnum.F;
		Date periode = new LocalDate(2013, 9, 1).toDate();
		
		// When
		EtatPayeurDto result = new EtatPayeurDto(cp, statut, periode);
		
		// Then
		assertEquals("SCV", result.getChainePaie());
		assertEquals("F", result.getStatut());
		assertEquals("septembre 2013", result.getPeriode());
		assertEquals(0, result.getAbsences().size());
		assertEquals(0, result.getHeuresSup().size());
		assertEquals(0, result.getPrimes().size());
	}
}
