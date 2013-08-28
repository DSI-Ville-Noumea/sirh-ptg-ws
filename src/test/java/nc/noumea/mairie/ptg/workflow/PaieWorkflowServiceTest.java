package nc.noumea.mairie.ptg.workflow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import nc.noumea.mairie.domain.SpWFEtat;
import nc.noumea.mairie.domain.SpWFPaie;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.repository.IPaieWorkflowRepository;

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
}
