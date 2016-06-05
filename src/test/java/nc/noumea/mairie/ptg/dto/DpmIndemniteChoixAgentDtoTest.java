package nc.noumea.mairie.ptg.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.DpmIndemAnnee;
import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;

import org.junit.Test;

public class DpmIndemniteChoixAgentDtoTest {

	@Test
	public void DpmIndemniteChoixAgentDto_construct() {
		
		DpmIndemAnnee dpmIndemAnnee = new DpmIndemAnnee();
		
		DpmIndemChoixAgent dpmChoix = new DpmIndemChoixAgent();
		dpmChoix.setIdDpmIndemChoixAgent(1);
		dpmChoix.setChoixIndemnite(true);
		dpmChoix.setChoixRecuperation(false);
		dpmChoix.setDateMaj(new Date());
		dpmChoix.setIdAgent(9005138);
		dpmChoix.setIdAgentCreation(9004847);
		dpmChoix.setDpmIndemAnnee(dpmIndemAnnee);
		
		AgentWithServiceDto agentDto = new AgentWithServiceDto();
		agentDto.setIdAgent(9005138);
		agentDto.setNom("Zobi");
		agentDto.setPrenom("zoba");
		
		AgentWithServiceDto operateurDto = new AgentWithServiceDto();
		operateurDto.setIdAgent(9004847);
		operateurDto.setNom("Zobi 2");
		operateurDto.setPrenom("zoba 2");
		
		DpmIndemniteChoixAgentDto result = new DpmIndemniteChoixAgentDto(dpmChoix, agentDto, operateurDto);
		
		assertEquals(dpmChoix.getIdDpmIndemChoixAgent(), result.getIdDpmIndemChoixAgent());
		assertEquals(dpmChoix.getIdAgent(), result.getIdAgent());
		assertEquals(dpmChoix.getIdAgentCreation(), result.getIdAgentCreation());
		assertEquals(dpmChoix.isChoixIndemnite(), result.isChoixIndemnite());
		assertEquals(dpmChoix.isChoixRecuperation(), result.isChoixRecuperation());
		assertEquals(dpmChoix.getDateMaj(), result.getDateMaj());
		assertEquals(agentDto.getIdAgent(), result.getAgent().getIdAgent());
		assertEquals(operateurDto.getIdAgent(), result.getAgentOperateur().getIdAgent());
		assertNotNull(dpmChoix.getDpmIndemAnnee());
	}
}
