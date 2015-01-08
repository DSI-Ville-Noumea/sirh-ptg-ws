package nc.noumea.mairie.ptg.dto.etatsPayeur;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

public class ListEtatsPayeurDtoTest {

	@Test
	public void ListEtatsPayeurDto() {

		Date date = new Date();

		ListEtatsPayeurDto dto = new ListEtatsPayeurDto(new Integer(1), "C", date, "test label", "test.pdf", 1234,
				date, "nom", "prenom");

		assertEquals(new Integer(1), dto.getIdEtatPayeur());
		assertEquals("C", dto.getStatut());
		assertEquals(date, dto.getDateEtatPayeur());
		assertEquals("test label", dto.getLabel());
		assertEquals("test.pdf", dto.getFichier());
		assertEquals(new Integer(1234), dto.getIdAgent());
		assertEquals(date, dto.getDateEdition());
		assertEquals("nom", dto.getDisplayNom());
		assertEquals("prenom", dto.getDisplayPrenom());
	}
}
