package nc.noumea.mairie.ptg.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nc.noumea.mairie.ptg.domain.DpmIndemChoixAgent;

public class DpmIndemniteChoixAgentDto implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3264579691396835041L;
	
	private Integer idDpmIndemChoixAgent;
	private DpmIndemniteAnneeDto dpmIndemniteAnnee;
	private Integer idAgent;
	private Integer idAgentCreation;
	private AgentWithServiceDto agent;
	private AgentWithServiceDto agentOperateur;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateMaj;
	private boolean isChoixRecuperation;
	private boolean isChoixIndemnite;
	
	public DpmIndemniteChoixAgentDto() {
		
	}
	
	public DpmIndemniteChoixAgentDto(DpmIndemChoixAgent dpmChoix) {
		this();
		this.idDpmIndemChoixAgent = dpmChoix.getIdDpmIndemChoixAgent();
		this.isChoixIndemnite = dpmChoix.isChoixIndemnite();
		this.isChoixRecuperation = dpmChoix.isChoixRecuperation();
		this.dateMaj = dpmChoix.getDateMaj();
		this.idAgent = dpmChoix.getIdAgent();
		this.idAgentCreation = dpmChoix.getIdAgentCreation();
		if(null != dpmChoix.getDpmIndemAnnee()) {
			this.dpmIndemniteAnnee = new DpmIndemniteAnneeDto(dpmChoix.getDpmIndemAnnee(), false);
		}
	}
	
	public DpmIndemniteChoixAgentDto(DpmIndemChoixAgent dpmChoix, AgentWithServiceDto agentDto, AgentWithServiceDto operateurDto) {
		this(dpmChoix);
		this.agent = agentDto;
		this.agentOperateur = operateurDto;
	}
	
	public Integer getIdDpmIndemChoixAgent() {
		return idDpmIndemChoixAgent;
	}
	public void setIdDpmIndemChoixAgent(Integer idDpmIndemChoixAgent) {
		this.idDpmIndemChoixAgent = idDpmIndemChoixAgent;
	}
	
	public DpmIndemniteAnneeDto getDpmIndemniteAnnee() {
		return dpmIndemniteAnnee;
	}
	public void setDpmIndemniteAnnee(DpmIndemniteAnneeDto dpmIndemniteAnnee) {
		this.dpmIndemniteAnnee = dpmIndemniteAnnee;
	}

	public Integer getIdAgent() {
		return idAgent;
	}
	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
	
	public Integer getIdAgentCreation() {
		return idAgentCreation;
	}
	public void setIdAgentCreation(Integer idAgentCreation) {
		this.idAgentCreation = idAgentCreation;
	}
	
	public Date getDateMaj() {
		return dateMaj;
	}
	public void setDateMaj(Date dateMaj) {
		this.dateMaj = dateMaj;
	}
	
	public boolean isChoixRecuperation() {
		return isChoixRecuperation;
	}
	public void setChoixRecuperation(boolean isChoixRecuperation) {
		this.isChoixRecuperation = isChoixRecuperation;
	}
	
	public boolean isChoixIndemnite() {
		return isChoixIndemnite;
	}
	public void setChoixIndemnite(boolean isChoixIndemnite) {
		this.isChoixIndemnite = isChoixIndemnite;
	}

	public AgentWithServiceDto getAgent() {
		return agent;
	}

	public void setAgent(AgentWithServiceDto agent) {
		this.agent = agent;
	}

	public AgentWithServiceDto getAgentOperateur() {
		return agentOperateur;
	}

	public void setAgentOperateur(AgentWithServiceDto agentOperateur) {
		this.agentOperateur = agentOperateur;
	}

	@Override
	public String toString() {
		return "DpmIndemniteChoixAgentDto [idDpmIndemChoixAgent=" + idDpmIndemChoixAgent + ", dpmIndemniteAnnee=" + dpmIndemniteAnnee + ", idAgent="
				+ idAgent + ", idAgentCreation=" + idAgentCreation + ", dateMaj=" + dateMaj + ", isChoixRecuperation=" + isChoixRecuperation
				+ ", isChoixIndemnite=" + isChoixIndemnite + "]";
	}
	
}
