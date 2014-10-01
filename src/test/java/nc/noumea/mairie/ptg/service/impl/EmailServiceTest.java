package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import nc.noumea.mairie.ptg.dto.EmailInfoDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class EmailServiceTest {

	@Test
	public void getListIdDestinatairesEmailInfo() {
		List<Integer> listApprobateurs = new ArrayList<Integer>();
		listApprobateurs.add(2);

		IPointageRepository pointageRepository = Mockito.mock(IPointageRepository.class);
		Mockito.when(pointageRepository.getListApprobateursPointagesSaisiesJourDonne()).thenReturn(listApprobateurs);

		EmailService service = new EmailService();
		ReflectionTestUtils.setField(service, "pointageRepository", pointageRepository);

		EmailInfoDto dto = service.getListIdDestinatairesEmailInfo();

		assertEquals(1, dto.getListApprobateurs().size());
		assertEquals(2, dto.getListApprobateurs().get(0).intValue());
	}
}
