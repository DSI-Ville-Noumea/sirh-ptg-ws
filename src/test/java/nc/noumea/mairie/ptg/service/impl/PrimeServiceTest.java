package nc.noumea.mairie.ptg.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;
import nc.noumea.mairie.ptg.domain.RefTypePointageEnum;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;
import nc.noumea.mairie.ptg.dto.AbsenceDto;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.HeureSupDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.RefPrimeDto;
import nc.noumea.mairie.ptg.dto.SaisieReturnMessageDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.ptg.service.IPointageDataConsistencyRules;
import nc.noumea.mairie.ptg.service.IPointageService;
import nc.noumea.mairie.ptg.service.IPrimeService;
import nc.noumea.mairie.ptg.service.NotAMondayException;
import nc.noumea.mairie.ptg.service.impl.HelperService;
import nc.noumea.mairie.ptg.service.impl.SaisieService;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

public class PrimeServiceTest {

	
	@Test
	public void getPrimeListForAgent() {	
		// Given
		List<RefPrime> result = Mockito.mock(IPointageRepository.class).getRefPrimesListForAgent(AgentStatutEnum.F);
		List<RefPrimeDto> result2 = Mockito.mock(IPrimeService.class).getPrimeListForAgent(AgentStatutEnum.F);
				
		// Then
		testList(result,result2);	
	}
	
	@Test
	public void getPrimeList() {
		// Given
		List<RefPrime> result = Mockito.mock(IPointageRepository.class).getRefPrimesList();
		List<RefPrimeDto> result2 = Mockito.mock(IPrimeService.class).getPrimeList();
				
		// Then
		testList(result,result2);			
	}
	
	
	private void testList(	List<RefPrime> list1,List<RefPrimeDto> list2){
		assertEquals(list1.size(), list2.size());		
	}
	
}
