package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ListEtatsPayeurDtoTest {
	
	@Test
	public void ListEtatsPayeurDto() {
		
		Date date = new Date();
		
		ListEtatsPayeurDto dto = new ListEtatsPayeurDto(new Integer(1), "C", new Integer(2), date, "test label", "test.pdf", 1234, date);
		
		assertEquals(new Integer(1), dto.getIdEtatPayeur());
		assertEquals("C", dto.getStatut());
		assertEquals(new Integer(2), dto.getType());
		assertEquals(date, dto.getDateEtatPayeur());
		assertEquals("test label", dto.getLabel());
		assertEquals("test.pdf", dto.getFichier());
		assertEquals(new Integer(1234),  dto.getIdAgent());
		assertEquals(date, dto.getDateEdition());
	}
}
