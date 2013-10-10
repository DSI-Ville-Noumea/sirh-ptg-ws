package nc.noumea.mairie.ptg.dto.etatsPayeur;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;

public class PrimesEtatPayeurDtoTest {

	@Test
	public void PrimesEtatPayeurDto_CASE_A_COCHER_ctor() {
		
		// Given
		VentilPrime vp = new VentilPrime();
		vp.setIdAgent(9008767);
		vp.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vp.setQuantite(12);
		vp.setRefPrime(new RefPrime());
		vp.getRefPrime().setLibelle("prime");
		vp.getRefPrime().setTypeSaisie(TypeSaisieEnum.CASE_A_COCHER);

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vp, null);

		// Then
		assertEquals(9008767, (int) result.getIdAgent());
		assertEquals(vp.getDateDebutMois(), result.getDate());
		assertEquals("octobre 2013", result.getPeriode());

		assertEquals("prime", result.getType());
		assertEquals("12", result.getQuantite());
	}
	
	@Test
	public void PrimesEtatPayeurDto_NB_INDEMNITES_ctor() {
		
		// Given
		VentilPrime vp = new VentilPrime();
		vp.setIdAgent(9008767);
		vp.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vp.setQuantite(12);
		vp.setRefPrime(new RefPrime());
		vp.getRefPrime().setLibelle("prime");
		vp.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vp, null);

		// Then
		assertEquals(9008767, (int) result.getIdAgent());
		assertEquals(vp.getDateDebutMois(), result.getDate());
		assertEquals("octobre 2013", result.getPeriode());

		assertEquals("prime", result.getType());
		assertEquals("12", result.getQuantite());
	}
	
	@Test
	public void PrimesEtatPayeurDto_NB_HEURES_ctor() {
		
		// Given
		VentilPrime vp = new VentilPrime();
		vp.setIdAgent(9008767);
		vp.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vp.setQuantite(60);
		vp.setRefPrime(new RefPrime());
		vp.getRefPrime().setLibelle("prime");
		vp.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(60)).thenReturn("1h");

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vp, hS);

		// Then
		assertEquals(9008767, (int) result.getIdAgent());
		assertEquals(vp.getDateDebutMois(), result.getDate());
		assertEquals("octobre 2013", result.getPeriode());

		assertEquals("prime", result.getType());
		assertEquals("1h", result.getQuantite());
	}
	
	@Test
	public void PrimesEtatPayeurDto_PERIODE_HEURES_ctor() {
		
		// Given
		VentilPrime vp = new VentilPrime();
		vp.setIdAgent(9008767);
		vp.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vp.setQuantite(75);
		vp.setRefPrime(new RefPrime());
		vp.getRefPrime().setLibelle("prime");
		vp.getRefPrime().setTypeSaisie(TypeSaisieEnum.PERIODE_HEURES);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(75)).thenReturn("1h15");

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vp, hS);

		// Then
		assertEquals(9008767, (int) result.getIdAgent());
		assertEquals(vp.getDateDebutMois(), result.getDate());
		assertEquals("octobre 2013", result.getPeriode());

		assertEquals("prime", result.getType());
		assertEquals("1h15", result.getQuantite());
	}
	
	@Test
	public void PrimesEtatPayeurDto_NewOld_NB_INDEMNITES_ctor() {
		
		// Given
		VentilPrime vpNew = new VentilPrime();
		vpNew.setIdAgent(9008767);
		vpNew.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vpNew.setQuantite(12);
		vpNew.setRefPrime(new RefPrime());
		vpNew.getRefPrime().setLibelle("prime");
		vpNew.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		
		VentilPrime vpOld = new VentilPrime();
		vpOld.setQuantite(10);

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vpNew, vpOld, null);

		// Then
		assertEquals(9008767, (int) result.getIdAgent());
		assertEquals(vpNew.getDateDebutMois(), result.getDate());
		assertEquals("octobre 2013", result.getPeriode());

		assertEquals("prime", result.getType());
		assertEquals("2", result.getQuantite());
	}
	
	@Test
	public void PrimesEtatPayeurDto_NewOldNegative_NB_INDEMNITES_ctor() {
		
		// Given
		VentilPrime vpNew = new VentilPrime();
		vpNew.setIdAgent(9008767);
		vpNew.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vpNew.setQuantite(12);
		vpNew.setRefPrime(new RefPrime());
		vpNew.getRefPrime().setLibelle("prime");
		vpNew.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		
		VentilPrime vpOld = new VentilPrime();
		vpOld.setQuantite(14);

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vpNew, vpOld, null);

		// Then
		assertEquals(9008767, (int) result.getIdAgent());
		assertEquals(vpNew.getDateDebutMois(), result.getDate());
		assertEquals("octobre 2013", result.getPeriode());

		assertEquals("prime", result.getType());
		assertEquals("-2", result.getQuantite());
	}
	
	@Test
	public void PrimesEtatPayeurDto_NewOld_NB_HEURES_ctor() {
		
		// Given
		VentilPrime vpNew = new VentilPrime();
		vpNew.setIdAgent(9008767);
		vpNew.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vpNew.setQuantite(60);
		vpNew.setRefPrime(new RefPrime());
		vpNew.getRefPrime().setLibelle("prime");
		vpNew.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(15)).thenReturn("15m");

		VentilPrime vpOld = new VentilPrime();
		vpOld.setQuantite(45);
		
		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vpNew, vpOld, hS);

		// Then
		assertEquals(9008767, (int) result.getIdAgent());
		assertEquals(vpNew.getDateDebutMois(), result.getDate());
		assertEquals("octobre 2013", result.getPeriode());

		assertEquals("prime", result.getType());
		assertEquals("15m", result.getQuantite());
	}
	
	@Test
	public void PrimesEtatPayeurDto_NewOld_Negative_NB_HEURES_ctor() {
		
		// Given
		VentilPrime vpNew = new VentilPrime();
		vpNew.setIdAgent(9008767);
		vpNew.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vpNew.setQuantite(60);
		vpNew.setRefPrime(new RefPrime());
		vpNew.getRefPrime().setLibelle("prime");
		vpNew.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(-15)).thenReturn("- 15m");

		VentilPrime vpOld = new VentilPrime();
		vpOld.setQuantite(75);
		
		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vpNew, vpOld, hS);

		// Then
		assertEquals(9008767, (int) result.getIdAgent());
		assertEquals(vpNew.getDateDebutMois(), result.getDate());
		assertEquals("octobre 2013", result.getPeriode());

		assertEquals("prime", result.getType());
		assertEquals("- 15m", result.getQuantite());
	}
}
