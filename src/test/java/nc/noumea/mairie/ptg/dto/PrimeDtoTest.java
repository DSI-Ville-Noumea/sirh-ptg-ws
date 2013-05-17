package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;

import org.joda.time.DateTime;
import org.junit.Test;

public class PrimeDtoTest {

	@Test
	public void ctor_withpointage() {

		// Given
		RefPrime ref = new RefPrime();
		ref.setCalculee(false);
		ref.setDescription("description");
		ref.setLibelle("libelle");
		ref.setNoRubr(12);
		ref.setStatut(AgentStatutEnum.F);
		ref.setTypeSaisie(TypeSaisieEnum.CASE_A_COCHER);

		// When
		PrimeDto result = new PrimeDto(ref);

		// Then
		assertEquals(ref.getLibelle(), result.getTitre());
		assertEquals("CASE_A_COCHER", result.getTypeSaisie());
		assertEquals(ref.getIdRefPrime(), result.getIdRefPrime());
		assertEquals(ref.getNoRubr(), result.getNumRubrique());
	}
	
	@Test
	public void updateWithPointage_withPERIODE_HEURES() {
		
		// Given
		PrimeDto result = new PrimeDto();
		Pointage p = new Pointage();
		EtatPointage e = new EtatPointage();
		e.setEtat(EtatPointageEnum.APPROUVE);
		p.getEtats().add(e);
		p.setIdPointage(99);
		p.setQuantite(123);
		p.setDateDebut(new DateTime().toDate());
		p.setDateFin(new DateTime().toDate());
		p.setRefPrime(new RefPrime());
		p.getRefPrime().setTypeSaisie(TypeSaisieEnum.PERIODE_HEURES);
		
		// When
		result.updateWithPointage(p);
		
		// Then
		assertEquals(p.getIdPointage(), result.getIdPointage());
		assertNull(result.getQuantite());
		assertEquals(p.getLatestEtatPointage().getEtat().name(), result.getEtat());
		assertEquals(p.getDateDebut(), result.getHeureDebut());
		assertEquals(p.getDateFin(), result.getHeureFin());
	}
	
	@Test
	public void updateWithPointage_withCASE_A_COCHER() {
		
		// Given
		PrimeDto result = new PrimeDto();
		Pointage p = new Pointage();
		EtatPointage e = new EtatPointage();
		e.setEtat(EtatPointageEnum.APPROUVE);
		p.getEtats().add(e);
		p.setIdPointage(99);
		p.setQuantite(123);
		p.setDateDebut(new DateTime().toDate());
		p.setRefPrime(new RefPrime());
		p.getRefPrime().setTypeSaisie(TypeSaisieEnum.CASE_A_COCHER);
		
		// When
		result.updateWithPointage(p);
		
		// Then
		assertEquals(p.getIdPointage(), result.getIdPointage());
		assertEquals(p.getQuantite(), result.getQuantite());
		assertEquals(p.getLatestEtatPointage().getEtat().name(), result.getEtat());
		assertNull(result.getHeureDebut());
		assertNull(result.getHeureFin());
	}
	
	@Test
	public void updateWithPointage_withNB_HEURES() {
		
		// Given
		PrimeDto result = new PrimeDto();
		Pointage p = new Pointage();
		EtatPointage e = new EtatPointage();
		e.setEtat(EtatPointageEnum.APPROUVE);
		p.getEtats().add(e);
		p.setIdPointage(99);
		p.setQuantite(123);
		p.setDateDebut(new DateTime().toDate());
		p.setRefPrime(new RefPrime());
		p.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		
		// When
		result.updateWithPointage(p);
		
		// Then
		assertEquals(p.getIdPointage(), result.getIdPointage());
		assertEquals(p.getQuantite(), result.getQuantite());
		assertEquals(p.getLatestEtatPointage().getEtat().name(), result.getEtat());
		assertNull(result.getHeureDebut());
		assertNull(result.getHeureFin());
	}
	
	@Test
	public void updateWithPointage_withNB_INDEMNITES() {
		
		// Given
		PrimeDto result = new PrimeDto();
		Pointage p = new Pointage();
		EtatPointage e = new EtatPointage();
		e.setEtat(EtatPointageEnum.APPROUVE);
		p.getEtats().add(e);
		p.setIdPointage(99);
		p.setQuantite(123);
		p.setDateDebut(new DateTime().toDate());
		p.setRefPrime(new RefPrime());
		p.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_INDEMNITES);
		
		// When
		result.updateWithPointage(p);
		
		// Then
		assertEquals(p.getIdPointage(), result.getIdPointage());
		assertEquals(p.getQuantite(), result.getQuantite());
		assertEquals(p.getLatestEtatPointage().getEtat().name(), result.getEtat());
		assertNull(result.getHeureDebut());
		assertNull(result.getHeureFin());
	}
}
