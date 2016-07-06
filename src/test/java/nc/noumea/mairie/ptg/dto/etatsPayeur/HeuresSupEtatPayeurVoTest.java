package nc.noumea.mairie.ptg.dto.etatsPayeur;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.VentilHsup;

import org.joda.time.LocalDate;
import org.junit.Test;

public class HeuresSupEtatPayeurVoTest {

	@Test
	public void HeuresSupEtatPayeurVo_ctor() {

		// Given
		VentilHsup vh = new VentilHsup();
		vh.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vh.setIdAgent(9008767);
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

		// When
		HeuresSupEtatPayeurVo result = new HeuresSupEtatPayeurVo(vh);

		// Then
		assertEquals(40, result.getH1Mai().intValue());
		assertEquals(50, result.getNormales().intValue());
		assertEquals(60, result.getDjf().intValue());
		assertEquals(100, result.getNuit().intValue());
		assertEquals(210, result.getSup25().intValue());
		assertEquals(-70, result.getSup50().intValue());
	}

	@Test
	public void HeuresSupEtatPayeurVo_NewOld_ctor() {

		// Given
		VentilHsup vhNew = new VentilHsup();
		vhNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vhNew.setIdAgent(9008767);
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

		// When
		HeuresSupEtatPayeurVo result = new HeuresSupEtatPayeurVo(vhNew, vhOld);

		// Then
		assertEquals(0, result.getH1Mai().intValue());
		assertEquals(0, result.getNormales().intValue());
		assertEquals(0, result.getDjf().intValue());
		assertEquals(0, result.getNuit().intValue());
		assertEquals(30, result.getSup25().intValue());
		assertEquals(-60, result.getSup50().intValue());
	}

	@Test
	public void HeuresSupEtatPayeurVo_NewOld_ctor_withRecup() {

		// Given
		VentilHsup vhNew = new VentilHsup();
		vhNew.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vhNew.setIdAgent(9008767);
		vhNew.setMComposees(20);
		vhNew.setMComposeesRecup(20);
		vhNew.setMHorsContrat(30);
		vhNew.setMMai(40);
		vhNew.setMMaiRecup(40);
		vhNew.setMNormales(50);
		vhNew.setMNormalesRecup(50);
		vhNew.setMsdjf(60);
		vhNew.setMsdjfRecup(60);
		vhNew.setMSimple(90);
		vhNew.setMSimpleRecup(90);
		vhNew.setMsNuit(100);
		vhNew.setMsNuitRecup(100);
		vhNew.setMSup(110);
		vhNew.setMSup25(150);
		vhNew.setMSup25Recup(150);
		vhNew.setMSup50(30);
		vhNew.setMSup50Recup(30);

		VentilHsup vhOld = new VentilHsup();
		vhOld.setDateLundi(new LocalDate(2013, 9, 30).toDate());
		vhOld.setIdAgent(9008767);
		vhOld.setMComposees(20);
		vhOld.setMComposeesRecup(10);
		vhOld.setMHorsContrat(30);
		vhOld.setMMai(40);
		vhOld.setMMaiRecup(30);
		vhOld.setMNormales(50);
		vhOld.setMNormalesRecup(40);
		vhOld.setMsdjf(60);
		vhOld.setMsdjfRecup(50);
		vhOld.setMSimple(90);
		vhOld.setMSimpleRecup(80);
		vhOld.setMsNuit(100);
		vhOld.setMsNuitRecup(90);
		vhOld.setMSup(110);
		vhOld.setMSup25(120);
		vhOld.setMSup25Recup(110);
		vhOld.setMSup50(90);
		vhOld.setMSup50Recup(80);

		// When
		HeuresSupEtatPayeurVo result = new HeuresSupEtatPayeurVo(vhNew, vhOld);

		// Then
		assertEquals(-10, result.getH1Mai().intValue());
		assertEquals(-10, result.getNormales().intValue());
		assertEquals(-10, result.getDjf().intValue());
		assertEquals(-10, result.getNuit().intValue());
		assertEquals(-20, result.getSup25().intValue());
		assertEquals(-20, result.getSup50().intValue());
	}
}
