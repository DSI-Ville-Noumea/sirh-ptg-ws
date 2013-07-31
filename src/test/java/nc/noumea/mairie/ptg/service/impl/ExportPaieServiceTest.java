package nc.noumea.mairie.ptg.service.impl;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.repository.IVentilationRepository;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class ExportPaieServiceTest {

	@Test
	public void exportToPaie_NoVentilDate_DoNothing() {
		
		// Given
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getTypeChainePaieFromStatut(AgentStatutEnum.CC)).thenReturn(TypeChainePaieEnum.SCV);
		
		IVentilationRepository vR = Mockito.mock(IVentilationRepository.class);
		Mockito.when(vR.getLatestVentilDate(TypeChainePaieEnum.SCV, false)).thenReturn(null);
		
		ExportPaieService service = new ExportPaieService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "ventilationRepository", vR);
		
		// When
		service.exportToPaie(1, AgentStatutEnum.CC);
		
		// Then
		Mockito.verify(vR, Mockito.times(1)).getLatestVentilDate(TypeChainePaieEnum.SCV, false);
	}
}
