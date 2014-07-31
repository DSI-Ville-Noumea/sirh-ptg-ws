package nc.noumea.mairie.ptg.dto.etatsPayeur;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.service.impl.HelperService;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.Mockito;

public class HeuresSupEtatPayeurDtoTest {

	@Test
	public void HeuresSupEtatPayeurDto_ctor() {
		
		// Given
		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vh.setIdAgent(9008767);
		vh.setMComplementaires(10);
		vh.setMComposees(20);
		vh.setMHorsContrat(30);
		vh.setMMai(40);
		vh.setMNormales(50);
		vh.setMsdjf(60);
		vh.setMSimple(90);
		vh.setMsNuit(100);
		vh.setMSup(110);
		vh.setMSup25(120);
		vh.setMSup50(-90);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(10)).thenReturn("10m");
		Mockito.when(hS.formatMinutesToString(20)).thenReturn("20m");
		Mockito.when(hS.formatMinutesToString(30)).thenReturn("30m");
		Mockito.when(hS.formatMinutesToString(40)).thenReturn("40m");
		Mockito.when(hS.formatMinutesToString(50)).thenReturn("50m");
		Mockito.when(hS.formatMinutesToString(60)).thenReturn("1h");
		Mockito.when(hS.formatMinutesToString(70)).thenReturn("1h10m");
		Mockito.when(hS.formatMinutesToString(80)).thenReturn("1h20m");
		Mockito.when(hS.formatMinutesToString(90)).thenReturn("1h30m");
		Mockito.when(hS.formatMinutesToString(100)).thenReturn("1h40m");
		Mockito.when(hS.formatMinutesToString(110)).thenReturn("1h50m");
		Mockito.when(hS.formatMinutesToString(120)).thenReturn("2h");
		Mockito.when(hS.formatMinutesToString(-90)).thenReturn("- 1h30m");
		
		// When
		HeuresSupEtatPayeurDto result = new HeuresSupEtatPayeurDto(vh, hS);
		
		// Then
		assertEquals(9008767, (int) result.getIdAgent());
		assertEquals(vh.getDateLundi(), result.getDate());
		assertEquals("septembre 2013", result.getPeriode());
		
		assertEquals("10m", result.getComplementaires());
		assertEquals("20m", result.getComposees());
		assertEquals("30m", result.getHorsContrat());
		assertEquals("40m", result.getH1Mai());
		assertEquals("50m", result.getNormales());
		assertEquals("1h", result.getDjf());
		assertEquals("1h30m", result.getSimples());
		assertEquals("1h40m", result.getNuit());
		assertEquals("1h50m", result.getSup());
		assertEquals("2h", result.getSup25());
		assertEquals("- 1h30m", result.getSup50());
	}
	
	@Test
	public void HeuresSupEtatPayeurDto_NewOld_ctor() {
		
		// Given
		VentilHsup vhNew = new VentilHsup();
		vhNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vhNew.setIdAgent(9008767);
		vhNew.setMComplementaires(10);
		vhNew.setMComposees(20);
		vhNew.setMHorsContrat(30);
		vhNew.setMMai(40);
		vhNew.setMNormales(50);
		vhNew.setMsdjf(60);
		vhNew.setMSimple(90);
		vhNew.setMsNuit(100);
		vhNew.setMSup(110);
		vhNew.setMSup25(150);
		vhNew.setMSup50(30);
		
		VentilHsup vhOld = new VentilHsup();
		vhOld.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vhOld.setIdAgent(9008767);
		vhOld.setMComplementaires(10);
		vhOld.setMComposees(20);
		vhOld.setMHorsContrat(30);
		vhOld.setMMai(40);
		vhOld.setMNormales(50);
		vhOld.setMsdjf(60);
		vhOld.setMSimple(90);
		vhOld.setMsNuit(100);
		vhOld.setMSup(110);
		vhOld.setMSup25(120);
		vhOld.setMSup50(90);
		
		HelperService hS = Mockito.mock(HelperService.class);
		Mockito.when(hS.formatMinutesToString(0)).thenReturn("");
		Mockito.when(hS.formatMinutesToString(30)).thenReturn("30m");
		Mockito.when(hS.formatMinutesToString(-60)).thenReturn("- 1h");
		
		// When
		HeuresSupEtatPayeurDto result = new HeuresSupEtatPayeurDto(vhNew, vhOld, hS);
		
		// Then
		assertEquals(9008767, (int) result.getIdAgent());
		assertEquals(vhNew.getDateLundi(), result.getDate());
		assertEquals("septembre 2013", result.getPeriode());
		
		assertEquals("", result.getComplementaires());
		assertEquals("", result.getComposees());
		assertEquals("", result.getHorsContrat());
		assertEquals("", result.getH1Mai());
		assertEquals("", result.getNormales());
		assertEquals("", result.getDjf());
		assertEquals("", result.getSimples());
		assertEquals("", result.getNuit());
		assertEquals("", result.getSup());
		assertEquals("30m", result.getSup25());
		assertEquals("- 1h", result.getSup50());
	}
}
