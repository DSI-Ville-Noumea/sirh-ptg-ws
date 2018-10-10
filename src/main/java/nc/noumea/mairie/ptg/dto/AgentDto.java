package nc.noumea.mairie.ptg.dto;

import nc.noumea.mairie.sirh.dto.AgentGeneriqueDto;

public class AgentDto {

	private String nom;
	private String prenom;
	private Integer idAgent;
	private Integer nomatr;
	private String idTiarhe;

	public AgentDto() {

	}

	public AgentDto(AgentGeneriqueDto agent) {
		if(null != agent) {
			nom = agent.getDisplayNom();
			prenom = agent.getDisplayPrenom();
			idAgent = agent.getIdAgent();
			nomatr = agent.getNomatr();
			idTiarhe = agent.getIdTiarhe();
		}
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

	public Integer getNomatr() {
		return nomatr;
	}

	public void setNomatr(Integer nomatr) {
		this.nomatr = nomatr;
	}

	public String getIdTiarhe() {
		return idTiarhe;
	}

	public void setIdTiarhe(String idTiarhe) {
		this.idTiarhe = idTiarhe;
	}

	@Override
	public boolean equals(Object obj) {
		return idAgent.equals(((AgentDto) obj).getIdAgent());
	}
}
