package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertTrue;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.dto.CanStartWorkflowPaieActionDto;
import nc.noumea.mairie.ptg.workflow.IPaieWorkflowService;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ExportEtatPayeurServiceTest {

	@Test
	public void canStartExportPaieAction_callWorkflowService() {
		// Given
		TypeChainePaieEnum chainePaie = TypeChainePaieEnum.SCV;
		
		IPaieWorkflowService pwfs = Mockito.mock(IPaieWorkflowService.class);
		Mockito.when(pwfs.canChangeStateToExportEtatPayeurStarted(chainePaie)).thenReturn(true);
		
		ExportEtatPayeurService service = new ExportEtatPayeurService();
		ReflectionTestUtils.setField(service, "paieWorkflowService", pwfs);
		
		// When
		CanStartWorkflowPaieActionDto result = service.canStartExportEtatPayeurAction(chainePaie);
		
		// Then
		assertTrue(result.isCanStartExportPaieAction());
	}
}
