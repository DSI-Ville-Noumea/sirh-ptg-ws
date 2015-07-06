package nc.noumea.mairie.ptg.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import nc.noumea.mairie.domain.SpWFEtat;
import nc.noumea.mairie.domain.SpWFPaie;
import nc.noumea.mairie.domain.SpWfEtatEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.repository.IPaieWorkflowRepository;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class PaieWorkflowServiceTest {

	@Test
	public void getCurrentState_SCV() {
		
		// Given
		SpWFPaie expected = new SpWFPaie();
		
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(TypeChainePaieEnum.SCV)).thenReturn(expected);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// Then
		assertEquals(expected, service.getCurrentState(TypeChainePaieEnum.SCV));
	}
	
	@Test
	public void getCurrentState_SHC() {
		
		// Given
		SpWFPaie expected = new SpWFPaie();
		
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(TypeChainePaieEnum.SHC)).thenReturn(expected);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// Then
		assertEquals(expected, service.getCurrentState(TypeChainePaieEnum.SHC));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs0_true() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.PRET);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs1_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs2_true() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_TERMINEE);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs3_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.CALCUL_SALAIRE_EN_COURS);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs4_true() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.CALCUL_SALAIRE_TERMINE);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs5_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.ETATS_PAYEUR_EN_COURS);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs6_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.ETATS_PAYEUR_TERMINES);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs7_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.JOURNAL_EN_COURS);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs8_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.JOURNAL_TERMINE);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs9_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.VALIDATION_PAIE_EN_COURS);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIsValid_returnTrue() {
		
		// Given
		SpWFPaie shc = new SpWFPaie();
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.PRET);
		shc.setEtat(etat);
		
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(TypeChainePaieEnum.SHC)).thenReturn(shc);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// Then
		assertTrue(service.canChangeStateToExportPaieStarted(TypeChainePaieEnum.SHC));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIsNotValid_returnFalse() {
		
		// Given
		SpWFPaie shc = new SpWFPaie();
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS);
		shc.setEtat(etat);
		
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(TypeChainePaieEnum.SHC)).thenReturn(shc);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(TypeChainePaieEnum.SHC));
	}
	
	@Test
	public void canChangeStateToExportEtatPayeurStarted_stateIs6_true() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.CALCUL_SALAIRE_TERMINE);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToExportEtatsPayeurStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportEtatPayeurStarted_stateIsOtherThan6_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.JOURNAL_TERMINE);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportEtatsPayeurStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportEtatPayeurStarted_stateIsValid_returnTrue() {
		
		// Given
		SpWFPaie shc = new SpWFPaie();
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.CALCUL_SALAIRE_TERMINE);
		shc.setEtat(etat);
		
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(TypeChainePaieEnum.SHC)).thenReturn(shc);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// Then
		assertTrue(service.canChangeStateToExportEtatsPayeurStarted(TypeChainePaieEnum.SHC));
	}
	
	@Test
	public void canChangeStateToExportEtatPayeurStarted_stateIsNotValid_returnFalse() {
		
		// Given
		SpWFPaie shc = new SpWFPaie();
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS);
		shc.setEtat(etat);
		
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(TypeChainePaieEnum.SHC)).thenReturn(shc);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// Then
		assertFalse(service.canChangeStateToExportEtatsPayeurStarted(TypeChainePaieEnum.SHC));
	}
	
	@Test
	public void changeStateToExportPaieDone_StateIsValid_UpdateState() throws WorkflowInvalidStateException {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e0 = new SpWFEtat();
		e0.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS);
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e0);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(chainePaie)).thenReturn(state);
		SpWFEtat e2 = new SpWFEtat();
		e2.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_TERMINEE);
		Mockito.when(wfR.getEtat(SpWfEtatEnum.ECRITURE_POINTAGES_TERMINEE)).thenReturn(e2);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new LocalDate(2013, 4, 5).toDate());
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		service.changeStateToExportPaieDone(chainePaie);
				
		assertEquals(e2, state.getEtat());
		assertEquals(new LocalDate(2013, 4, 5).toDate(), state.getDateMaj());
	}
	
	@Test
	public void changeStateToExportPaieDone_StateIsNotValid_ThrowException() {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e0 = new SpWFEtat();
		e0.setCodeEtat(SpWfEtatEnum.PRET);
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e0);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(chainePaie)).thenReturn(state);
		SpWFEtat e2 = new SpWFEtat();
		e2.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_TERMINEE);
		Mockito.when(wfR.getEtat(SpWfEtatEnum.ECRITURE_POINTAGES_TERMINEE)).thenReturn(e2);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// When
		try {
			service.changeStateToExportPaieDone(chainePaie);
		} catch (WorkflowInvalidStateException ex) {

			// Then
			assertEquals("Impossible de passer à l'état [2 : ECRITURE_POINTAGES_TERMINEE] car l'état en cours est [0 : PRET]", ex.getMessage());
			return;
		}
		
		fail("Should have thrown a WorkflowInvalidStateException");
	}
	
	@Test
	public void changeStateToExportPaieStarted_StateIsNotValid_ThrowException() {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e3 = new SpWFEtat();
		e3.setCodeEtat(SpWfEtatEnum.CALCUL_SALAIRE_EN_COURS);
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e3);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(chainePaie)).thenReturn(state);
		SpWFEtat e1 = new SpWFEtat();
		e1.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS);
		Mockito.when(wfR.getEtat(SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS)).thenReturn(e1);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// When
		try {
			service.changeStateToExportPaieStarted(chainePaie);
		} catch (WorkflowInvalidStateException ex) {

			// Then
			assertEquals("Impossible de passer à l'état [1 : ECRITURE_POINTAGES_EN_COURS] car l'état en cours est [3 : CALCUL_SALAIRE_EN_COURS]", ex.getMessage());
			return;
		}
		
		fail("Should have thrown a WorkflowInvalidStateException");
	}
	
	@Test
	public void changeStateToExportPaieStarted_StateIsValid_UpdateState() throws WorkflowInvalidStateException {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e2 = new SpWFEtat();
		e2.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_TERMINEE);
		e2.setLibelleEtat("DEUX");
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e2);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(chainePaie)).thenReturn(state);
		SpWFEtat e1 = new SpWFEtat();
		e1.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS);
		e1.setLibelleEtat("UN");
		Mockito.when(wfR.getEtat(SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS)).thenReturn(e1);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new LocalDate(2013, 4, 5).toDate());
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		service.changeStateToExportPaieStarted(chainePaie);
				
		assertEquals(e1, state.getEtat());
		assertEquals(new LocalDate(2013, 4, 5).toDate(), state.getDateMaj());
	}
	
	@Test
	public void changeStateToExportEtatsPayeurDone_StateIsNotValid_ThrowException() {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e0 = new SpWFEtat();
		e0.setCodeEtat(SpWfEtatEnum.PRET);
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e0);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(chainePaie)).thenReturn(state);
		SpWFEtat e9 = new SpWFEtat();
		e9.setCodeEtat(SpWfEtatEnum.ETATS_PAYEUR_TERMINES);
		Mockito.when(wfR.getEtat(SpWfEtatEnum.ETATS_PAYEUR_TERMINES)).thenReturn(e9);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// When
		try {
			service.changeStateToExportEtatsPayeurDone(chainePaie);
		} catch (WorkflowInvalidStateException ex) {

			// Then
			assertEquals("Impossible de passer à l'état [6 : ETATS_PAYEUR_TERMINES] car l'état en cours est [0 : PRET]", ex.getMessage());
			return;
		}
		
		fail("Should have thrown a WorkflowInvalidStateException");
	}
	
	@Test
	public void changeStateToExportEtatsPayeurDone_StateIsValid_UpdateState() throws WorkflowInvalidStateException {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e1 = new SpWFEtat();
		e1.setCodeEtat(SpWfEtatEnum.ETATS_PAYEUR_EN_COURS);
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e1);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(chainePaie)).thenReturn(state);
		SpWFEtat e9 = new SpWFEtat();
		e9.setCodeEtat(SpWfEtatEnum.ETATS_PAYEUR_TERMINES);
		Mockito.when(wfR.getEtat(SpWfEtatEnum.ETATS_PAYEUR_TERMINES)).thenReturn(e9);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new LocalDate(2013, 4, 5).toDate());
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		service.changeStateToExportEtatsPayeurDone(chainePaie);
				
		assertEquals(e9, state.getEtat());
		assertEquals(new LocalDate(2013, 4, 5).toDate(), state.getDateMaj());
	}
	
	@Test
	public void changeStateToExportEtatsPayeurStarted_StateIsNotValid_ThrowException() {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e3 = new SpWFEtat();
		e3.setCodeEtat(SpWfEtatEnum.CALCUL_SALAIRE_EN_COURS);
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e3);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(chainePaie)).thenReturn(state);
		SpWFEtat e8 = new SpWFEtat();
		e8.setCodeEtat(SpWfEtatEnum.ETATS_PAYEUR_EN_COURS);
		Mockito.when(wfR.getEtat(SpWfEtatEnum.ETATS_PAYEUR_EN_COURS)).thenReturn(e8);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// When
		try {
			service.changeStateToExportEtatsPayeurStarted(chainePaie);
		} catch (WorkflowInvalidStateException ex) {

			// Then
			assertEquals("Impossible de passer à l'état [5 : ETATS_PAYEUR_EN_COURS] car l'état en cours est [3 : CALCUL_SALAIRE_EN_COURS]", ex.getMessage());
			return;
		}
		
		fail("Should have thrown a WorkflowInvalidStateException");
	}
	
	@Test
	public void changeStateToExportEtatsPayeurStarted_StateIsValid_UpdateState() throws WorkflowInvalidStateException {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e2 = new SpWFEtat();
		e2.setCodeEtat(SpWfEtatEnum.CALCUL_SALAIRE_TERMINE);
		e2.setLibelleEtat("HUIT");
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e2);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(chainePaie)).thenReturn(state);
		SpWFEtat e8 = new SpWFEtat();
		e8.setCodeEtat(SpWfEtatEnum.ETATS_PAYEUR_EN_COURS);
		e8.setLibelleEtat("NEUF");
		Mockito.when(wfR.getEtat(SpWfEtatEnum.ETATS_PAYEUR_EN_COURS)).thenReturn(e8);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getCurrentDate()).thenReturn(new LocalDate(2013, 4, 5).toDate());
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		ReflectionTestUtils.setField(service, "helperService", hS);
		
		// When
		service.changeStateToExportEtatsPayeurStarted(chainePaie);
				
		assertEquals(e8, state.getEtat());
		assertEquals(new LocalDate(2013, 4, 5).toDate(), state.getDateMaj());
	}
	

	
	@Test
	public void canChangeStateToVentilationStarted_stateIs0_true() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.PRET);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToVentilationStarted(etat));
	}
	
	@Test
	public void canChangeStateToVentilationStarted_stateIs1_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_EN_COURS);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToVentilationStarted(etat));
	}
	
	@Test
	public void canChangeStateToVentilationStarted_stateIs2_true() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.ECRITURE_POINTAGES_TERMINEE);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToVentilationStarted(etat));
	}
	
	@Test
	public void canChangeStateToVentilationStarted_stateIs3_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.CALCUL_SALAIRE_EN_COURS);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToVentilationStarted(etat));
	}
	
	@Test
	public void canChangeStateToVentilationStarted_stateIs4_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.CALCUL_SALAIRE_TERMINE);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToVentilationStarted(etat));
	}
	
	@Test
	public void canChangeStateToVentilationStarted_stateIs5_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.ETATS_PAYEUR_EN_COURS);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToVentilationStarted(etat));
	}
	
	@Test
	public void canChangeStateToVentilationStarted_stateIs6_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.ETATS_PAYEUR_TERMINES);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToVentilationStarted(etat));
	}
	
	@Test
	public void canChangeStateToVentilationStarted_stateIs7_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.JOURNAL_EN_COURS);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToVentilationStarted(etat));
	}
	
	@Test
	public void canChangeStateToVentilationStarted_stateIs8_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.JOURNAL_TERMINE);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToVentilationStarted(etat));
	}
	
	@Test
	public void canChangeStateToVentilationStarted_stateIs9_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(SpWfEtatEnum.VALIDATION_PAIE_EN_COURS);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToVentilationStarted(etat));
	}
}
