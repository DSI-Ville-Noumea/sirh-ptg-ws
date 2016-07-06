package nc.noumea.mairie.ptg.dto.etatsPayeur;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.junit.Test;
import org.mockito.Mockito;

public class HeuresSupEtatPayeurDtoTest {

	@Test
	public void HeuresSupEtatPayeurDto_ctor() {

		// Given
		List<HeuresSupEtatPayeurVo> listHSupEtatPayeur = new ArrayList<HeuresSupEtatPayeurVo>();

		HelperService hS = Mockito.mock(HelperService.class);

		// When
		Mockito.when(hS.formatMinutesToString(0)).thenReturn("");
		HeuresSupEtatPayeurDto result = new HeuresSupEtatPayeurDto(listHSupEtatPayeur, hS);

		// Then
		assertEquals("", result.getH1Mai());
		assertEquals("", result.getNormales());
		assertEquals("", result.getDjf());
		assertEquals("", result.getNuit());
		assertEquals("", result.getSup25());
		assertEquals("", result.getSup50());
	}

	@Test
	public void HeuresSupEtatPayeurDto_NewOld_ctor() {

		// Given
		List<HeuresSupEtatPayeurVo> listHSupEtatPayeur = new ArrayList<HeuresSupEtatPayeurVo>();
		HeuresSupEtatPayeurVo vo1 = new HeuresSupEtatPayeurVo();
		vo1.setNormales(1);
		vo1.setSup25(2);
		vo1.setSup50(3);
		vo1.setNuit(4);
		vo1.setDjf(5);
		vo1.setH1Mai(6);
		
		HeuresSupEtatPayeurVo vo2 = new HeuresSupEtatPayeurVo();
		vo2.setNormales(10);
		vo2.setSup25(20);
		vo2.setSup50(30);
		vo2.setNuit(40);
		vo2.setDjf(50);
		vo2.setH1Mai(60);
		
		listHSupEtatPayeur.add(vo1);
		listHSupEtatPayeur.add(vo2);

		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(0)).thenReturn("");
		Mockito.when(hS.formatMinutesToString(11)).thenReturn("11m");
		Mockito.when(hS.formatMinutesToString(22)).thenReturn("22m");
		Mockito.when(hS.formatMinutesToString(33)).thenReturn("33m");
		Mockito.when(hS.formatMinutesToString(44)).thenReturn("44m");
		Mockito.when(hS.formatMinutesToString(55)).thenReturn("55m");
		Mockito.when(hS.formatMinutesToString(66)).thenReturn("1h6m");

		// When
		HeuresSupEtatPayeurDto result = new HeuresSupEtatPayeurDto(listHSupEtatPayeur, hS);

		// Then
		assertEquals("11m", result.getNormales());
		assertEquals("22m", result.getSup25());
		assertEquals("33m", result.getSup50());
		assertEquals("44m", result.getNuit());
		assertEquals("55m", result.getDjf());
		assertEquals("1h6m", result.getH1Mai());
	}
}
