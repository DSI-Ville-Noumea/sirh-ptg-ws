package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.noumea.mairie.domain.Spphre;
import nc.noumea.mairie.domain.SpphreId;
import nc.noumea.mairie.domain.SpphreRecupEnum;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.repository.IExportPaieRepository;

import org.joda.time.LocalDate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class ExportPaieHSupServiceTest {

	private static RefTypePointage hSup;
	private static RefTypePointage abs;
	
	@BeforeClass
	public static void Setup() {
		hSup = new RefTypePointage();
		hSup.setIdRefTypePointage(RefTypePointageEnum.H_SUP.getValue());
		abs= new RefTypePointage();
		abs.setIdRefTypePointage(RefTypePointageEnum.ABSENCE.getValue());
	}
	
	@Test
	public void exportHSupToPaie_NoPointages_ReturnEmptyList() {
		
		// Given
		List<VentilHsup> ventilatedHSup = new ArrayList<VentilHsup>();
		ExportPaieHSupService service = new ExportPaieHSupService();
		
		// Then
		assertEquals(0, service.exportHsupToPaie(ventilatedHSup).size());
	}
	
	@Test
	public void exportHSupToPaie_1VentilHSup_nothingInDataBase_ReturnNewSpphre() {
		
		// Given
		VentilHsup h1 = new VentilHsup();
		h1.setIdAgent(9008765);
		h1.setDateLundi(new LocalDate(2013, 8, 5).toDate());
		h1.setMSup25(60);
		h1.setMSup50(70);
		h1.setMsdjf(80);
		h1.setMMai(90);
		h1.setMsNuit(100);
		h1.setMSimple(110);
		h1.setMComposees(120);
		h1.setMComplementaires(125);
		h1.setMRecuperees(130);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
		Mockito.when(hS.getIntegerDateMairieFromDate(h1.getDateLundi())).thenReturn(20130805);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(60)).thenReturn(1d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(70)).thenReturn(1.1d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(80)).thenReturn(1.2d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(90)).thenReturn(1.3d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(100)).thenReturn(1.4d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(110)).thenReturn(1.5d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(120)).thenReturn(2d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(125)).thenReturn(2.05d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(130)).thenReturn(2.1d);
		
		IExportPaieRepository eR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(eR.getSpphreForDayAndAgent(9008765, h1.getDateLundi())).thenReturn(null);
		
		ExportPaieHSupService service = new ExportPaieHSupService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", eR);
		
		// When
		List<Spphre> result = service.exportHsupToPaie(Arrays.asList(h1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(20130805, (int) result.get(0).getId().getDatJour());
		assertEquals(8765, (int) result.get(0).getId().getNomatr());
		assertEquals(SpphreRecupEnum.P, result.get(0).getSpphreRecup());
		assertEquals(1d, result.get(0).getNbh25(), 0);
		assertEquals(1.1d, result.get(0).getNbh50(), 0);
		assertEquals(1.2d, result.get(0).getNbhdim(), 0);
		assertEquals(1.3d, result.get(0).getNbhmai(), 0);
		assertEquals(1.4d, result.get(0).getNbhnuit(), 0);
		assertEquals(1.5d, result.get(0).getNbhssimple(), 0);
		assertEquals(2d, result.get(0).getNbhscomposees(), 0);
		assertEquals(2.05d, result.get(0).getNbhcomplementaires(), 0);
		assertEquals(2.1d, result.get(0).getNbhrecuperees(), 0);
	}
	
	@Test
	public void exportHSupToPaie_1VentilHSup_nothingInDataBase_ReturnNewSpphre_WithHNormales() {
		
		// Given
		VentilHsup h1 = new VentilHsup();
		h1.setIdAgent(9008765);
		h1.setDateLundi(new LocalDate(2013, 8, 5).toDate());
		h1.setMSup25(60);
		h1.setMSup50(70);
		h1.setMsdjf(80);
		h1.setMMai(90);
		h1.setMsNuit(100);
		h1.setMSimple(110);
		h1.setMComposees(120);
		h1.setMNormales(125);
		h1.setMRecuperees(130);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
		Mockito.when(hS.getIntegerDateMairieFromDate(h1.getDateLundi())).thenReturn(20130805);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(60)).thenReturn(1d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(70)).thenReturn(1.1d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(80)).thenReturn(1.2d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(90)).thenReturn(1.3d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(100)).thenReturn(1.4d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(110)).thenReturn(1.5d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(120)).thenReturn(2d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(125)).thenReturn(2.05d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(130)).thenReturn(2.1d);
		
		IExportPaieRepository eR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(eR.getSpphreForDayAndAgent(9008765, h1.getDateLundi())).thenReturn(null);
		
		ExportPaieHSupService service = new ExportPaieHSupService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", eR);
		
		// When
		List<Spphre> result = service.exportHsupToPaie(Arrays.asList(h1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(20130805, (int) result.get(0).getId().getDatJour());
		assertEquals(8765, (int) result.get(0).getId().getNomatr());
		assertEquals(SpphreRecupEnum.P, result.get(0).getSpphreRecup());
		assertEquals(1d, result.get(0).getNbh25(), 0);
		assertEquals(1.1d, result.get(0).getNbh50(), 0);
		assertEquals(1.2d, result.get(0).getNbhdim(), 0);
		assertEquals(1.3d, result.get(0).getNbhmai(), 0);
		assertEquals(1.4d, result.get(0).getNbhnuit(), 0);
		assertEquals(1.5d, result.get(0).getNbhssimple(), 0);
		assertEquals(2d, result.get(0).getNbhscomposees(), 0);
		assertEquals(2.05d, result.get(0).getNbhcomplementaires(), 0);
		assertEquals(2.1d, result.get(0).getNbhrecuperees(), 0);
	}
	
	@Test
	public void exportHSupToPaie_1VentilHSup_existingValueInDataBase_ReturnUpdatedSpphre() {
		
		// Given
		VentilHsup h1 = new VentilHsup();
		h1.setIdAgent(9008765);
		h1.setDateLundi(new LocalDate(2013, 8, 5).toDate());
		h1.setMSup25(60);
		h1.setMSup50(70);
		h1.setMsdjf(80);
		h1.setMMai(90);
		h1.setMsNuit(100);
		h1.setMSimple(110);
		h1.setMComposees(120);
		h1.setMComplementaires(125);
		h1.setMRecuperees(130);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
		Mockito.when(hS.getIntegerDateMairieFromDate(h1.getDateLundi())).thenReturn(20130805);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(60)).thenReturn(1d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(70)).thenReturn(1.1d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(80)).thenReturn(1.2d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(90)).thenReturn(1.3d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(100)).thenReturn(1.4d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(110)).thenReturn(1.5d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(120)).thenReturn(2d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(125)).thenReturn(2.05d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(130)).thenReturn(2.1d);
		
		Spphre existingHre = new Spphre();
		existingHre.setId(new SpphreId(8765, 20130805));
		existingHre.setNbh25(1);
		existingHre.setNbh50(1);
		existingHre.setNbhdim(1);
		existingHre.setNbhmai(1);
		existingHre.setNbhnuit(1);
		existingHre.setNbhssimple(1);
		existingHre.setNbhscomposees(1);
		existingHre.setNbhcomplementaires(1);
		existingHre.setNbhrecuperees(1);
		IExportPaieRepository eR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(eR.getSpphreForDayAndAgent(9008765, h1.getDateLundi())).thenReturn(existingHre);
		
		ExportPaieHSupService service = new ExportPaieHSupService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", eR);
		
		// When
		List<Spphre> result = service.exportHsupToPaie(Arrays.asList(h1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(existingHre, result.get(0));
		assertEquals(20130805, (int) result.get(0).getId().getDatJour());
		assertEquals(8765, (int) result.get(0).getId().getNomatr());
		assertEquals(SpphreRecupEnum.P, result.get(0).getSpphreRecup());
		assertEquals(1d, result.get(0).getNbh25(), 0);
		assertEquals(1.1d, result.get(0).getNbh50(), 0);
		assertEquals(1.2d, result.get(0).getNbhdim(), 0);
		assertEquals(1.3d, result.get(0).getNbhmai(), 0);
		assertEquals(1.4d, result.get(0).getNbhnuit(), 0);
		assertEquals(1.5d, result.get(0).getNbhssimple(), 0);
		assertEquals(2d, result.get(0).getNbhscomposees(), 0);
		assertEquals(2.05d, result.get(0).getNbhcomplementaires(), 0);
		assertEquals(2.1d, result.get(0).getNbhrecuperees(), 0);
	}
	
	@Test
	public void exportHSupToPaie_1VentilHSup_NewValueIsO_DeleteExistingSpphre() {
		
		// Given
		VentilHsup h1 = new VentilHsup();
			h1.setIdAgent(9008765);
			h1.setDateLundi(new LocalDate(2013, 8, 5).toDate());
		
		HelperService hS = Mockito.mock(HelperService.class);
			Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
			Mockito.when(hS.getIntegerDateMairieFromDate(h1.getDateLundi())).thenReturn(20130805);
		
		Spphre existingHre = Mockito.spy(new Spphre());
			existingHre.setId(new SpphreId(8765, 20130805));
			existingHre.setNbh25(1);
			existingHre.setNbh50(1);
			existingHre.setNbhdim(1);
			existingHre.setNbhmai(1);
			existingHre.setNbhnuit(1);
			existingHre.setNbhssimple(1);
			existingHre.setNbhscomposees(1);
			existingHre.setNbhcomplementaires(1);
			existingHre.setNbhrecuperees(1);
			
		IExportPaieRepository eR = Mockito.mock(IExportPaieRepository.class);
			Mockito.when(eR.getSpphreForDayAndAgent(9008765, h1.getDateLundi())).thenReturn(existingHre);
			Mockito.doAnswer(new Answer<Object>() {
				public Object answer(InvocationOnMock invocation) {
					return true;
				}
			}).when(eR).removeEntity(Mockito.isA(Spphre.class));
		
		ExportPaieHSupService service = new ExportPaieHSupService();
			ReflectionTestUtils.setField(service, "helperService", hS);
			ReflectionTestUtils.setField(service, "exportPaieRepository", eR);
		
		// When
		List<Spphre> result = service.exportHsupToPaie(Arrays.asList(h1));
		
		// Then
		assertEquals(0, result.size());
		Mockito.verify(eR, Mockito.times(1)).removeEntity(Mockito.isA(Spphre.class));
	}
	
	@Test
	public void exportHSupToPaie_1VentilHSup_nothingInDataBase_ReturnNewSpphre_withSpphreRecup() {
		
		// Given
		VentilHsup h1 = new VentilHsup();
		h1.setIdAgent(9008765);
		h1.setDateLundi(new LocalDate(2013, 8, 5).toDate());
		h1.setMSup25(0);
		h1.setMSup50(0);
		h1.setMsdjf(0);
		h1.setMMai(0);
		h1.setMsNuit(0);
		h1.setMSimple(0);
		h1.setMComposees(0);
		h1.setMComplementaires(0);
		h1.setMRecuperees(130);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.getMairieMatrFromIdAgent(9008765)).thenReturn(8765);
		Mockito.when(hS.getIntegerDateMairieFromDate(h1.getDateLundi())).thenReturn(20130805);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(0)).thenReturn(0d);
		Mockito.when(hS.convertMinutesToMairieNbHeuresFormat(130)).thenReturn(2.1d);
		
		IExportPaieRepository eR = Mockito.mock(IExportPaieRepository.class);
		Mockito.when(eR.getSpphreForDayAndAgent(9008765, h1.getDateLundi())).thenReturn(null);
		
		ExportPaieHSupService service = new ExportPaieHSupService();
		ReflectionTestUtils.setField(service, "helperService", hS);
		ReflectionTestUtils.setField(service, "exportPaieRepository", eR);
		
		// When
		List<Spphre> result = service.exportHsupToPaie(Arrays.asList(h1));
		
		// Then
		assertEquals(1, result.size());
		assertEquals(20130805, (int) result.get(0).getId().getDatJour());
		assertEquals(8765, (int) result.get(0).getId().getNomatr());
		assertEquals(SpphreRecupEnum.R, result.get(0).getSpphreRecup());
		assertEquals(0d, result.get(0).getNbh25(), 0);
		assertEquals(0d, result.get(0).getNbh50(), 0);
		assertEquals(0d, result.get(0).getNbhdim(), 0);
		assertEquals(0d, result.get(0).getNbhmai(), 0);
		assertEquals(0d, result.get(0).getNbhnuit(), 0);
		assertEquals(0d, result.get(0).getNbhssimple(), 0);
		assertEquals(0d, result.get(0).getNbhscomposees(), 0);
		assertEquals(0d, result.get(0).getNbhcomplementaires(), 0);
		assertEquals(2.1d, result.get(0).getNbhrecuperees(), 0);
	}
}
