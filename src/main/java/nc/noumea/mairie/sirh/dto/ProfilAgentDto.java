package nc.noumea.mairie.sirh.dto;

import java.util.Date;

public class ProfilAgentDto {

	private AgentGeneriqueDto	agent;
	private String				titre;
	private Date				dateNaissance;

	public Date getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(Date dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public AgentGeneriqueDto getAgent() {
		return agent;
	}

	public void setAgent(AgentGeneriqueDto agent) {
		this.agent = agent;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

}
