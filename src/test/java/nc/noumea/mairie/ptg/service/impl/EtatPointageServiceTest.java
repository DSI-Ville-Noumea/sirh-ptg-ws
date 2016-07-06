package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.dto.ReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

public class EtatPointageServiceTest {
	
	@Test
	public void majEtatPointagesByListId_KO() {
		
		Integer idEtatPointage = 1;
		
		EtatPointage ep = null;
		
		IPointageRepository ptgRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(ptgRepo.getEntity(EtatPointage.class, idEtatPointage)).thenReturn(ep);
				
		EtatPointageService service = new EtatPointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", ptgRepo);
		
		ReturnMessageDto result = service.majEtatPointagesByListId(idEtatPointage, EtatPointageEnum.APPROUVE);
		
		assertEquals(1, result.getErrors().size());
	}
	
	@Test
	public void majEtatPointagesByListId_OK() {
		
		Integer idEtatPointage = 1;
		
		EtatPointage ep = new EtatPointage();
		
		IPointageRepository ptgRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(ptgRepo.getEntity(EtatPointage.class, idEtatPointage)).thenReturn(ep);
				
		EtatPointageService service = new EtatPointageService();
		ReflectionTestUtils.setField(service, "pointageRepository", ptgRepo);
		
		ReturnMessageDto result = service.majEtatPointagesByListId(idEtatPointage, EtatPointageEnum.APPROUVE);
		
		Mockito.verify(ptgRepo, Mockito.times(1)).persisEntity(Mockito.isA(EtatPointage.class));
		assertEquals(0, result.getErrors().size());
	}
}
