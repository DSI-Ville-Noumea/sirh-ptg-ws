package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPointagePK;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PtgComment;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;

import org.joda.time.DateTime;
import org.junit.Test;

public class ConsultPointageDtoTest {

	@Test
	public void PointageConstructor() {
		
		// Given
		Pointage ptg = new Pointage();
		ptg.setIdPointage(123);
		RefTypePointage t = new RefTypePointage();
		t.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		t.setLabel("type ptg");
		ptg.setType(t);
		
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.APPROUVE);
		etat.setEtatPointagePk(new EtatPointagePK());
		etat.getEtatPointagePk().setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		ptg.getEtats().add(etat);
		
		ptg.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 05, 14, 10, 45, 0).toDate());
		
		ptg.setCommentaire(new PtgComment());
		ptg.getCommentaire().setText("commentaire");
		
		ptg.setMotif(new PtgComment());
		ptg.getMotif().setText("motif");
		
		ptg.setQuantite(null);
		
		// When
		ConsultPointageDto dto = new ConsultPointageDto(ptg);
		
		// Then
		assertEquals(123, (int) dto.getIdPointage());
		assertEquals("type ptg", dto.getTypePointage());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDate());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDebut());
		assertEquals(new DateTime(2013, 05, 14, 10, 45, 0).toDate(), dto.getFin());
		assertEquals("motif", dto.getMotif());
		assertEquals("commentaire", dto.getCommentaire());
		assertEquals("2h45m", dto.getQuantite());
	}
	
	@Test
	public void PointageConstructor_QuantitewithoutHour() {
		
		// Given
		Pointage ptg = new Pointage();
		ptg.setIdPointage(123);
		RefTypePointage t = new RefTypePointage();
		t.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
		t.setLabel("type ptg");
		ptg.setType(t);
		
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.APPROUVE);
		etat.setEtatPointagePk(new EtatPointagePK());
		etat.getEtatPointagePk().setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		ptg.getEtats().add(etat);
		
		ptg.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 05, 14, 8, 45, 0).toDate());
		
		ptg.setCommentaire(new PtgComment());
		ptg.getCommentaire().setText("commentaire");
		
		ptg.setMotif(new PtgComment());
		ptg.getMotif().setText("motif");
		
		ptg.setQuantite(null);
		
		// When
		ConsultPointageDto dto = new ConsultPointageDto(ptg);
		
		// Then
		assertEquals(123, (int) dto.getIdPointage());
		assertEquals("type ptg", dto.getTypePointage());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDate());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDebut());
		assertEquals(new DateTime(2013, 05, 14, 8, 45, 0).toDate(), dto.getFin());
		assertEquals("motif", dto.getMotif());
		assertEquals("commentaire", dto.getCommentaire());
		assertEquals("45m", dto.getQuantite());
	}
	
	@Test
	public void PointageConstructor_QuantiteIsNbHeures() {
		
		// Given
		Pointage ptg = new Pointage();
		ptg.setIdPointage(123);
		RefTypePointage t = new RefTypePointage();
		t.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		t.setLabel("type ptg");
		ptg.setType(t);
		
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.APPROUVE);
		etat.setEtatPointagePk(new EtatPointagePK());
		etat.getEtatPointagePk().setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		ptg.getEtats().add(etat);
		
		ptg.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		
		ptg.setCommentaire(new PtgComment());
		ptg.getCommentaire().setText("commentaire");
		
		ptg.setMotif(new PtgComment());
		ptg.getMotif().setText("motif");
		
		ptg.setQuantite(2);
		
		ptg.setRefPrime(new RefPrime());
		ptg.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		
		// When
		ConsultPointageDto dto = new ConsultPointageDto(ptg);
		
		// Then
		assertEquals(123, (int) dto.getIdPointage());
		assertEquals("type ptg", dto.getTypePointage());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDate());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDebut());
		assertNull(dto.getFin());
		assertEquals("motif", dto.getMotif());
		assertEquals("commentaire", dto.getCommentaire());
		assertEquals("2h", dto.getQuantite());
	}
	
	@Test
	public void PointageConstructor_QuantiteIsInteger() {
		
		// Given
		Pointage ptg = new Pointage();
		ptg.setIdPointage(123);
		RefTypePointage t = new RefTypePointage();
		t.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		t.setLabel("type ptg");
		ptg.setType(t);
		
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.APPROUVE);
		etat.setEtatPointagePk(new EtatPointagePK());
		etat.getEtatPointagePk().setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		ptg.getEtats().add(etat);
		
		ptg.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		
		ptg.setCommentaire(new PtgComment());
		ptg.getCommentaire().setText("commentaire");
		
		ptg.setMotif(new PtgComment());
		ptg.getMotif().setText("motif");
		
		ptg.setQuantite(1);
		
		ptg.setRefPrime(new RefPrime());
		ptg.getRefPrime().setTypeSaisie(TypeSaisieEnum.CASE_A_COCHER);
		
		// When
		ConsultPointageDto dto = new ConsultPointageDto(ptg);
		
		// Then
		assertEquals(123, (int) dto.getIdPointage());
		assertEquals("type ptg", dto.getTypePointage());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDate());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDebut());
		assertNull(dto.getFin());
		assertEquals("motif", dto.getMotif());
		assertEquals("commentaire", dto.getCommentaire());
		assertEquals("1", dto.getQuantite());
	}
	
	@Test
	public void updateEtat() {

		// Given
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.APPROUVE);
		etat.setEtatPointagePk(new EtatPointagePK());
		etat.getEtatPointagePk().setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		
		// When
		ConsultPointageDto dto = new ConsultPointageDto();
		dto.updateEtat(etat);
		
		// Then
		assertEquals(new DateTime(2013, 05, 24, 7, 56, 0).toDate(), dto.getDateSaisie());
		assertEquals(EtatPointageEnum.APPROUVE.getCodeEtat(), (int) dto.getIdRefEtat());
	}
}
