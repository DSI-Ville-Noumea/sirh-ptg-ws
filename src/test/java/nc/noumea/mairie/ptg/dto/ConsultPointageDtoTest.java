package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.PtgComment;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;

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
		etat.setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		ptg.getEtats().add(etat);
		
		ptg.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 05, 14, 10, 45, 0).toDate());
		
		ptg.setCommentaire(new PtgComment());
		ptg.getCommentaire().setText("commentaire");
		
		ptg.setMotif(new PtgComment());
		ptg.getMotif().setText("motif");
		
		ptg.setQuantite(null);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(ptg.getDateDebut(), ptg.getDateFin())).thenReturn("2h45m");
		
		// When
		ConsultPointageDto dto = new ConsultPointageDto(ptg, hS);
		
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
		etat.setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		ptg.getEtats().add(etat);
		
		ptg.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		ptg.setDateFin(new DateTime(2013, 05, 14, 8, 45, 0).toDate());
		
		ptg.setCommentaire(new PtgComment());
		ptg.getCommentaire().setText("commentaire");
		
		ptg.setMotif(new PtgComment());
		ptg.getMotif().setText("motif");
		
		ptg.setQuantite(null);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(ptg.getDateDebut(), ptg.getDateFin())).thenReturn("45m");
		
		// When
		ConsultPointageDto dto = new ConsultPointageDto(ptg, hS);
		
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
		etat.setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		ptg.getEtats().add(etat);
		
		ptg.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		
		ptg.setCommentaire(new PtgComment());
		ptg.getCommentaire().setText("commentaire");
		
		ptg.setMotif(new PtgComment());
		ptg.getMotif().setText("motif");
		
		ptg.setQuantite(75);
		
		RefPrime ref = new RefPrime();
		ref.setLibelle("nono");;
		ptg.setRefPrime(ref);
		ptg.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(ptg.getQuantite())).thenReturn("1h15m");
		
		// When
		ConsultPointageDto dto = new ConsultPointageDto(ptg, hS);
		
		// Then
		assertEquals(123, (int) dto.getIdPointage());
		assertEquals("nono", dto.getTypePointage());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDate());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDebut());
		assertNull(dto.getFin());
		assertEquals("motif", dto.getMotif());
		assertEquals("commentaire", dto.getCommentaire());
		assertEquals("1h15m", dto.getQuantite());
	}
	
	@Test
	public void PointageConstructor_QuantiteIsNbHeuresWithoutMinutes() {
		
		// Given
		Pointage ptg = new Pointage();
		ptg.setIdPointage(123);
		RefTypePointage t = new RefTypePointage();
		t.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		t.setLabel("type ptg");
		ptg.setType(t);
		
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.APPROUVE);
		etat.setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		ptg.getEtats().add(etat);
		
		ptg.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		
		ptg.setCommentaire(new PtgComment());
		ptg.getCommentaire().setText("commentaire");
		
		ptg.setMotif(new PtgComment());
		ptg.getMotif().setText("motif");
		
		ptg.setQuantite(60);
		
		RefPrime ref = new RefPrime();
		ref.setLibelle("nono");;
		ptg.setRefPrime(ref);
		ptg.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		
		HelperService hS = Mockito.mock(HelperService.class);
        Mockito.when(hS.formatMinutesToString(ptg.getQuantite())).thenReturn("1h");
		
		// When
		ConsultPointageDto dto = new ConsultPointageDto(ptg, hS);
		
		// Then
		assertEquals(123, (int) dto.getIdPointage());
		assertEquals("nono", dto.getTypePointage());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDate());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDebut());
		assertNull(dto.getFin());
		assertEquals("motif", dto.getMotif());
		assertEquals("commentaire", dto.getCommentaire());
		assertEquals("1h", dto.getQuantite());
	}
	
	@Test
	public void PointageConstructor_QuantiteIsNbHeuresWithoutHours() {
		
		// Given
		Pointage ptg = new Pointage();
		ptg.setIdPointage(123);
		RefTypePointage t = new RefTypePointage();
		t.setIdRefTypePointage(RefTypePointageEnum.PRIME.getValue());
		t.setLabel("type ptg");
		ptg.setType(t);
		
		EtatPointage etat = new EtatPointage();
		etat.setEtat(EtatPointageEnum.APPROUVE);
		etat.setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		ptg.getEtats().add(etat);
		
		ptg.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		
		ptg.setCommentaire(new PtgComment());
		ptg.getCommentaire().setText("commentaire");
		
		ptg.setMotif(new PtgComment());
		ptg.getMotif().setText("motif");
		
		ptg.setQuantite(15);
		
		RefPrime ref = new RefPrime();
		ref.setLibelle("nono");;
		ptg.setRefPrime(ref);
		ptg.getRefPrime().setTypeSaisie(TypeSaisieEnum.NB_HEURES);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(ptg.getQuantite())).thenReturn("15m");
		
		// When
		ConsultPointageDto dto = new ConsultPointageDto(ptg, hS);
		
		// Then
		assertEquals(123, (int) dto.getIdPointage());
		assertEquals("nono", dto.getTypePointage());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDate());
		assertEquals(new DateTime(2013, 05, 14, 8, 0, 0).toDate(), dto.getDebut());
		assertNull(dto.getFin());
		assertEquals("motif", dto.getMotif());
		assertEquals("commentaire", dto.getCommentaire());
		assertEquals("15m", dto.getQuantite());
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
		etat.setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		ptg.getEtats().add(etat);
		
		ptg.setDateDebut(new DateTime(2013, 05, 14, 8, 0, 0).toDate());
		
		ptg.setCommentaire(new PtgComment());
		ptg.getCommentaire().setText("commentaire");
		
		ptg.setMotif(new PtgComment());
		ptg.getMotif().setText("motif");
		
		ptg.setQuantite(1);
		RefPrime ref = new RefPrime();
		ref.setLibelle("nono");
		ptg.setRefPrime(ref);
		ptg.getRefPrime().setTypeSaisie(TypeSaisieEnum.CASE_A_COCHER);
		
		HelperService hS = Mockito.mock(HelperService.class);
        
		// When
		ConsultPointageDto dto = new ConsultPointageDto(ptg, hS);
		
		// Then
		assertEquals(123, (int) dto.getIdPointage());
		assertEquals("nono", dto.getTypePointage());
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
		etat.setDateEtat(new DateTime(2013, 05, 24, 7, 56, 0).toDate());
		
		// When
		ConsultPointageDto dto = new ConsultPointageDto();
		dto.updateEtat(etat);
		
		// Then
		assertEquals(new DateTime(2013, 05, 24, 7, 56, 0).toDate(), dto.getDateSaisie());
		assertEquals(EtatPointageEnum.APPROUVE.getCodeEtat(), (int) dto.getIdRefEtat());
	}
}
