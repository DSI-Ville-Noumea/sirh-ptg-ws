package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.repository.ISirhRepository;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.staticmock.MockStaticEntityMethods;
import org.springframework.test.util.ReflectionTestUtils;

@MockStaticEntityMethods
public class HolidayServiceTest {

	@Test
	public void isJourHoliday_True() {
		// Given
		DateTime dayTime = new DateTime(2013, 4, 9, 12, 9, 34);

		ISirhRepository arRepo = Mockito.mock(ISirhRepository.class);
		Mockito.when(arRepo.isJourHoliday(dayTime.toDate())).thenReturn(true);

		HolidayService service = new HolidayService();
		ReflectionTestUtils.setField(service, "sirhRepository", arRepo);

		// Then
		assertEquals(true, service.isHoliday(dayTime));
	}

}
