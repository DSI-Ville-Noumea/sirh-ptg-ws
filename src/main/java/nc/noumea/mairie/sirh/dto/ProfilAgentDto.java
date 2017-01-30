package nc.noumea.mairie.sirh.dto;

import java.util.Date;

public class ProfilAgentDto {

	private AgentGeneriqueDto	agent;
	private Date				dateNaissance;
	private String				titre;

	public AgentGeneriqueDto getAgent() {
		return agent;
	}

	public void setAgent(AgentGeneriqueDto agent) {
		this.agent = agent;
	}

	public Date getDateNaissance() {
		return dateNaissance;
	}

	public void setDateNaissance(Date dateNaissance) {
		this.dateNaissance = dateNaissance;
	}

	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

}
