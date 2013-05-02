package nc.noumea.mairie.ptg.dto;

import nc.noumea.mairie.sirh.domain.Agent;

public class AgentDto {

	private String nom;
	private String prenom;
	private Integer idAgent;
	private String service;
	private String codeService;
	
	public AgentDto() {
		
	}
	
	public AgentDto(Agent agent) {
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

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getCodeService() {
		return codeService;
	}

	public void setCodeService(String codeService) {
		this.codeService = codeService;
	}	
}
