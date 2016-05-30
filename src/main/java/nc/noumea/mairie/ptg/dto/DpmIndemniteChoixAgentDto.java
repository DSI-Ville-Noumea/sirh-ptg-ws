package nc.noumea.mairie.ptg.dto;

import java.io.Serializable;
import java.util.Date;

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
	private Date dateMaj;
	private boolean isChoixRecuperation;
	private boolean isChoixIndemnite;
	
	public DpmIndemniteChoixAgentDto() {
		
	}
	
	public DpmIndemniteChoixAgentDto(DpmIndemChoixAgent dpmChoix) {
		this();
		this.idDpmIndemChoixAgent = dpmChoix.getIdDpmIndemChoixAgent();
		if(null != dpmChoix.getDpmIndemAnnee()) {
			this.dpmIndemniteAnnee = new DpmIndemniteAnneeDto(dpmChoix.getDpmIndemAnnee(), false);
		}
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

	@Override
	public String toString() {
		return "DpmIndemniteChoixAgentDto [idDpmIndemChoixAgent=" + idDpmIndemChoixAgent + ", dpmIndemniteAnnee=" + dpmIndemniteAnnee + ", idAgent="
				+ idAgent + ", idAgentCreation=" + idAgentCreation + ", dateMaj=" + dateMaj + ", isChoixRecuperation=" + isChoixRecuperation
				+ ", isChoixIndemnite=" + isChoixIndemnite + "]";
	}
	
}
