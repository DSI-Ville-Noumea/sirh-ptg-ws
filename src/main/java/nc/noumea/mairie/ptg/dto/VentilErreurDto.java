package nc.noumea.mairie.ptg.dto;

import java.util.Date;

public class VentilErreurDto {

	private Integer idAgent;
	
	private Date dateCreation;
	
	private String typeChainePaie;
	
	private String taskStatus;

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getTypeChainePaie() {
		return typeChainePaie;
	}

	public void setTypeChainePaie(String typeChainePaie) {
		this.typeChainePaie = typeChainePaie;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	
	
}
