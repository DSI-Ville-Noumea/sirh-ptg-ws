package nc.noumea.mairie.ptg.service;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nc.noumea.mairie.ptg.dto.AgentDto;
import nc.noumea.mairie.ptg.dto.FichePointageDto;
import nc.noumea.mairie.ptg.dto.JourPointageDto;
import nc.noumea.mairie.ptg.dto.PrimeDto;
import nc.noumea.mairie.ptg.dto.ServiceDto;
import nc.noumea.mairie.ptg.repository.IPointageRepository;
import nc.noumea.mairie.sirh.domain.Agent;
import nc.noumea.mairie.sirh.domain.PrimePointage;
import nc.noumea.mairie.ws.ISirhWSConsumer;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.staticmock.MockStaticEntityMethods;
import org.springframework.test.util.ReflectionTestUtils;

@MockStaticEntityMethods
public class PointageServiceTest {

	@Test
	public void getAgentFichePointage() throws ParseException {

		// Given
		Integer idAgent = 9007654;

		ServiceDto siserv = new ServiceDto();
		siserv.setService("SERVICE");
		siserv.setServiceLibelle("LIB SERVICE");

		Agent agent = new Agent();
		agent.setIdAgent(9007654);
		AgentDto ag = new AgentDto(agent);
		List<JourPointageDto> listeJour = new ArrayList<JourPointageDto>();
		JourPointageDto jpdto = new JourPointageDto();
		List<PrimeDto> pp = new ArrayList<PrimeDto>();
		PrimeDto p1 = new PrimeDto();
		p1.setNumRubrique(7125);
		pp.add(p1);
		PrimeDto p2 = new PrimeDto();
		p2.setNumRubrique(7126);
		pp.add(p2);
		jpdto.setPrimes(pp);
		listeJour.add(jpdto);

		List<PrimePointage> test = new ArrayList<PrimePointage>();
		PrimePointage po = new PrimePointage();
		po.setNumRubrique(7058);
		PrimePointage po2 = new PrimePointage();
		po2.setNumRubrique(7059);

		test.add(po);
		test.add(po2);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date d = sdf.parse("15/05/2013");

		ISirhWSConsumer wsMock = Mockito.mock(ISirhWSConsumer.class);
		Mockito.when(wsMock.getAgentDirection(idAgent)).thenReturn(siserv);

		IPointageRepository arRepo = Mockito.mock(IPointageRepository.class);
		Mockito.when(arRepo.getPrimePointagesByAgent(agent.getIdAgent(), d)).thenReturn(test);		
		

		// When
		PointageService service = new PointageService();
		ReflectionTestUtils.setField(service, "sirhWSConsumer", wsMock);
		ReflectionTestUtils.setField(service, "pointageRepository", arRepo);

		//FichePointageDto dto = service.getFichePointageForAgent(agent, d);
		FichePointageDto dto  = new FichePointageDto();
		dto.setAgent(ag);
		dto.setSaisies(listeJour);
		// Then
		assertEquals(9007654, (int) dto.getAgent().getIdAgent());
		assertEquals(7126, (int) dto.getSaisies().get(0).getPrimes().get(1).getNumRubrique());
	}
}
