package nc.noumea.mairie.sirh.dto;

public class ProfilAgentDto {

	private AgentGeneriqueDto	agent;
	private String				titre;

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
