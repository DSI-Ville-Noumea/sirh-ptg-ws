package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ListEtatsPayeurDtoTest {
	
	@Test
	public void ListEtatsPayeurDto() {
		
		ListEtatsPayeurDto dto = new ListEtatsPayeurDto(new Integer(1), "C", new Integer(2), new Date(), "test label", "test.pdf");
		
		assertEquals(new Integer(1), dto.getIdEtatPayeur());
		assertEquals("C", dto.getStatut());
		assertEquals(new Integer(2), dto.getType());
		assertEquals(new Date(), dto.getDateEtatPayeur());
		assertEquals("test label", dto.getLabel());
		assertEquals("test.pdf", dto.getFichier());
	}
}
