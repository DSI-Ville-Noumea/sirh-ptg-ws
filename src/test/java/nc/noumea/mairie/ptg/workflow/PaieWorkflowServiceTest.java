package nc.noumea.mairie.ptg.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import nc.noumea.mairie.domain.SpWFEtat;
import nc.noumea.mairie.domain.SpWFPaie;
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
		etat.setCodeEtat(0);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs1_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(1);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs2_true() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(2);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs3_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(3);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs4_true() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(4);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs5_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(5);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs6_true() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(6);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs7_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(7);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIs8_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(8);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportPaieStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportPaieStarted_stateIsValid_returnTrue() {
		
		// Given
		SpWFPaie shc = new SpWFPaie();
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(0);
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
		etat.setCodeEtat(1);
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
		etat.setCodeEtat(8);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertTrue(service.canChangeStateToExportEtatPayeurStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportEtatPayeurStarted_stateIsOtherThan6_false() {
		
		// Given
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(7);
		
		PaieWorkflowService service = new PaieWorkflowService();
		
		// Then
		assertFalse(service.canChangeStateToExportEtatPayeurStarted(etat));
	}
	
	@Test
	public void canChangeStateToExportEtatPayeurStarted_stateIsValid_returnTrue() {
		
		// Given
		SpWFPaie shc = new SpWFPaie();
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(8);
		shc.setEtat(etat);
		
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(TypeChainePaieEnum.SHC)).thenReturn(shc);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// Then
		assertTrue(service.canChangeStateToExportEtatPayeurStarted(TypeChainePaieEnum.SHC));
	}
	
	@Test
	public void canChangeStateToExportEtatPayeurStarted_stateIsNotValid_returnFalse() {
		
		// Given
		SpWFPaie shc = new SpWFPaie();
		SpWFEtat etat = new SpWFEtat();
		etat.setCodeEtat(1);
		shc.setEtat(etat);
		
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.readCurrentState(TypeChainePaieEnum.SHC)).thenReturn(shc);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// Then
		assertFalse(service.canChangeStateToExportEtatPayeurStarted(TypeChainePaieEnum.SHC));
	}
	
	@Test
	public void changeStateToExportPaieDone_StateIsNotValid_ThrowException() {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e0 = new SpWFEtat();
		e0.setCodeEtat(0);
		e0.setLibelleEtat("ZERO");
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e0);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.selectForUpdateState(chainePaie)).thenReturn(state);
		SpWFEtat e2 = new SpWFEtat();
		e2.setCodeEtat(2);
		e2.setLibelleEtat("DEUX");
		Mockito.when(wfR.getEtat(2)).thenReturn(e2);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// When
		try {
			service.changeStateToExportPaieDone(chainePaie);
		} catch (WorkflowInvalidStateException ex) {

			// Then
			assertEquals("Impossible de passer à l'état [2: DEUX] car l'état en cours est [0: ZERO]", ex.getMessage());
			return;
		}
		
		fail("Should have thrown a WorkflowInvalidStateException");
	}
	
	@Test
	public void changeStateToExportPaieDone_StateIsValid_UpdateState() throws WorkflowInvalidStateException {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e1 = new SpWFEtat();
		e1.setCodeEtat(1);
		e1.setLibelleEtat("UN");
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e1);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.selectForUpdateState(chainePaie)).thenReturn(state);
		SpWFEtat e2 = new SpWFEtat();
		e2.setCodeEtat(2);
		e2.setLibelleEtat("DEUX");
		Mockito.when(wfR.getEtat(2)).thenReturn(e2);
		
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
	public void changeStateToExportPaieStarted_StateIsNotValid_ThrowException() {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e3 = new SpWFEtat();
		e3.setCodeEtat(3);
		e3.setLibelleEtat("TROIS");
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e3);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.selectForUpdateState(chainePaie)).thenReturn(state);
		SpWFEtat e1 = new SpWFEtat();
		e1.setCodeEtat(1);
		e1.setLibelleEtat("UN");
		Mockito.when(wfR.getEtat(1)).thenReturn(e1);
		
		PaieWorkflowService service = new PaieWorkflowService();
		ReflectionTestUtils.setField(service, "paieWorkflowRepository", wfR);
		
		// When
		try {
			service.changeStateToExportPaieStarted(chainePaie);
		} catch (WorkflowInvalidStateException ex) {

			// Then
			assertEquals("Impossible de passer à l'état [1: UN] car l'état en cours est [3: TROIS]", ex.getMessage());
			return;
		}
		
		fail("Should have thrown a WorkflowInvalidStateException");
	}
	
	@Test
	public void changeStateToExportPaieStarted_StateIsValid_UpdateState() throws WorkflowInvalidStateException {
		
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SHC;
		
		SpWFEtat e2 = new SpWFEtat();
		e2.setCodeEtat(2);
		e2.setLibelleEtat("DEUX");
		SpWFPaie state = new SpWFPaie();
		state.setEtat(e2);
		IPaieWorkflowRepository wfR = Mockito.mock(IPaieWorkflowRepository.class);
		Mockito.when(wfR.selectForUpdateState(chainePaie)).thenReturn(state);
		SpWFEtat e1 = new SpWFEtat();
		e1.setCodeEtat(1);
		e1.setLibelleEtat("UN");
		Mockito.when(wfR.getEtat(1)).thenReturn(e1);
		
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
}
