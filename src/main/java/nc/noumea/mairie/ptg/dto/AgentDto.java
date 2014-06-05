package nc.noumea.mairie.ptg.dto;

import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;

public class AgentDto {

	private String nom;
	private String prenom;
	private Integer idAgent;
	
	public AgentDto() {

	}

	public AgentDto(AgentGeneriqueDto agent) {
		nom = agent.getDisplayNom();
		prenom = agent.getDisplayPrenom();
		idAgent = agent.getIdAgent();
	}
	
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
}
