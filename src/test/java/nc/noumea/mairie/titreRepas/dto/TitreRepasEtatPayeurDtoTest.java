package nc.noumea.mairie.titreRepas.dto;

import static org.junit.Assert.*;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;

import org.junit.Test;

public class TitreRepasEtatPayeurDtoTest {

	@Test
	public void TitreRepasEtatPayeur_ctr() {

		TitreRepasEtatPayeur titreRepasEtatPayeur = new TitreRepasEtatPayeur();
		titreRepasEtatPayeur.setIdTrEtatPayeur(1);
		titreRepasEtatPayeur.setDateEtatPayeur(new Date());
		titreRepasEtatPayeur.setIdAgent(9005154);
		titreRepasEtatPayeur.setDateEdition(new Date());
		titreRepasEtatPayeur.setLabel("label");
		titreRepasEtatPayeur.setFichier("fichier");

		TitreRepasEtatPayeurDto dto = new TitreRepasEtatPayeurDto(titreRepasEtatPayeur, null);

		assertEquals(titreRepasEtatPayeur.getIdTrEtatPayeur(), dto.getIdTrEtatPayeur());
		assertEquals(titreRepasEtatPayeur.getDateEtatPayeur(), dto.getDateEtatPayeur());
		assertEquals(titreRepasEtatPayeur.getIdAgent(), dto.getIdAgent());
		assertEquals(titreRepasEtatPayeur.getDateEdition(), dto.getDateEdition());
		assertEquals(titreRepasEtatPayeur.getLabel(), dto.getLabel());
		assertEquals(titreRepasEtatPayeur.getFichier(), dto.getFichier());
		assertEquals("", dto.getUrlAlfrescoPrestataire());
		assertEquals("", dto.getLabelPrestataire());
	}
}
