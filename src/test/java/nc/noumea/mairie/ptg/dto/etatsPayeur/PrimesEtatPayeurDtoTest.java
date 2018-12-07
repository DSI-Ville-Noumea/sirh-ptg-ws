package nc.noumea.mairie.ptg.dto.etatsPayeur;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.domain.MairiePrimeTableEnum;
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
		vp.setQuantite(12.0);
		vp.setRefPrime(new RefPrime());
		vp.getRefPrime().setLibelle("prime");
		vp.getRefPrime().setTypeSaisie(TypeSaisieEnum.CASE_A_COCHER);

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vp, null);

		// Then
		assertEquals("prime", result.getType());
		assertEquals("12.0", result.getQuantite());
	}

	@Test
	public void PrimesEtatPayeurDto_NB_INDEMNITES_ctor() {

		// Given
		VentilPrime vp = new VentilPrime();
		vp.setIdAgent(9008767);
		vp.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vp.setQuantite(12.0);
		vp.setRefPrime(new RefPrime());
		vp.getRefPrime().setLibelle("prime");
		vp.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vp, null);

		// Then
		assertEquals("prime", result.getType());
		assertEquals("12.0", result.getQuantite());
	}

	@Test
	public void PrimesEtatPayeurDto_NB_HEURES_ctor() {

		// Given
		VentilPrime vp = new VentilPrime();
		vp.setIdAgent(9008767);
		vp.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vp.setQuantite(60.0);
		vp.setRefPrime(new RefPrime());
		vp.getRefPrime().setLibelle("prime");
		vp.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToStringForEVP(60)).thenReturn("1h");

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vp, hS);

		// Then
		assertEquals("prime", result.getType());
		assertEquals("1h", result.getQuantite());
	}

	@Test
	public void PrimesEtatPayeurDto_PERIODE_HEURES_ctor() {

		// Given
		VentilPrime vp = new VentilPrime();
		vp.setIdAgent(9008767);
		vp.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vp.setQuantite(75.0);
		vp.setRefPrime(new RefPrime());
		vp.getRefPrime().setLibelle("prime");
		vp.getRefPrime().setNoRubr(4747);
		vp.getRefPrime().setTypeSaisie(TypeSaisieEnum.PERIODE_HEURES);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToStringForEVP(75)).thenReturn("1h15");

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vp, hS);

		// Then
		assertEquals("prime", result.getType());
		assertEquals("1h15", result.getQuantite());
	}

	@Test
	public void PrimesEtatPayeurDto_NewOld_NB_INDEMNITES_ctor() {

		// Given
		VentilPrime vpNew = new VentilPrime();
		vpNew.setIdAgent(9008767);
		vpNew.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vpNew.setQuantite(12.0);
		vpNew.setRefPrime(new RefPrime());
		vpNew.getRefPrime().setLibelle("prime");
		vpNew.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);

		VentilPrime vpOld = new VentilPrime();
		vpOld.setQuantite(10.0);

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vpNew, vpOld, null);

		// Then
		assertEquals("prime", result.getType());
		assertEquals("2.0", result.getQuantite());
	}

	@Test
	public void PrimesEtatPayeurDto_NewOldNegative_NB_INDEMNITES_ctor() {

		// Given
		VentilPrime vpNew = new VentilPrime();
		vpNew.setIdAgent(9008767);
		vpNew.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vpNew.setQuantite(12.0);
		vpNew.setRefPrime(new RefPrime());
		vpNew.getRefPrime().setLibelle("prime");
		vpNew.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);

		VentilPrime vpOld = new VentilPrime();
		vpOld.setQuantite(14.0);

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vpNew, vpOld, null);

		// Then
		assertEquals("prime", result.getType());
		assertEquals("-2.0", result.getQuantite());
	}

	@Test
	public void PrimesEtatPayeurDto_NewOld_NB_HEURES_ctor() {

		// Given
		VentilPrime vpNew = new VentilPrime();
		vpNew.setIdAgent(9008767);
		vpNew.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vpNew.setQuantite(60.0);
		vpNew.setRefPrime(new RefPrime());
		vpNew.getRefPrime().setLibelle("prime");
		vpNew.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToStringForEVP(15)).thenReturn("15m");

		VentilPrime vpOld = new VentilPrime();
		vpOld.setQuantite(45.0);

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vpNew, vpOld, hS);

		// Then
		assertEquals("prime", result.getType());
		assertEquals("15m", result.getQuantite());
	}

	//#15317
	@Test
	public void PrimesEtatPayeurDto_NewOld_NB_HEURES_SPPRIM_ctor() {

		// Given
		VentilPrime vpNew = new VentilPrime();
		vpNew.setIdAgent(9008767);
		vpNew.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vpNew.setQuantite(172.0);
		vpNew.setRefPrime(new RefPrime());
		vpNew.getRefPrime().setLibelle("prime");
		vpNew.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		vpNew.getRefPrime().setMairiePrimeTableEnum(MairiePrimeTableEnum.SPPRIM);
		HelperService hS = Mockito.mock(HelperService.class);		
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(172)).thenReturn(2.52);
		Mockito.when(hS.formatMinutesToStringForEVP(180)).thenReturn("3H");

		VentilPrime vpOld = new VentilPrime();
		vpOld.setQuantite(0.0);

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vpNew, vpOld, hS);

		// Then
		assertEquals("prime", result.getType());
		assertEquals("3H", result.getQuantite());
	}

	@Test
	public void PrimesEtatPayeurDto_NewOld_Negative_NB_HEURES_ctor() {

		// Given
		VentilPrime vpNew = new VentilPrime();
		vpNew.setIdAgent(9008767);
		vpNew.setDateDebutMois(new LocalDate(2013, 10, 1).toDate());
		vpNew.setQuantite(60.0);
		vpNew.setRefPrime(new RefPrime());
		vpNew.getRefPrime().setLibelle("prime");
		vpNew.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToStringForEVP(-15)).thenReturn("- 15m");

		VentilPrime vpOld = new VentilPrime();
		vpOld.setQuantite(75.0);

		// When
		PrimesEtatPayeurDto result = new PrimesEtatPayeurDto(vpNew, vpOld, hS);

		// Then
		assertEquals("prime", result.getType());
		assertEquals("- 15m", result.getQuantite());
	}
}
