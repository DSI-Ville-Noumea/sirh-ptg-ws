package nc.noumea.mairie.ptg.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FichePointageDto {

	private Date dateLundi;
	private AgentWithServiceDto agent;
	private String semaine;
	private List<JourPointageDto> saisies;
	private boolean isDPM;
	private boolean isINASuperieur315;

	public FichePointageDto() {
		saisies = new ArrayList<JourPointageDto>();
	}

	public Date getDateLundi() {
		return dateLundi;
	}

	public void setDateLundi(Date dateLundi) {
		this.dateLundi = dateLundi;
	}

	public String getSemaine() {
		return semaine;
	}

	public void setSemaine(String semaine) {
		this.semaine = semaine;
	}

	public AgentWithServiceDto getAgent() {
		return agent;
	}

	public void setAgent(AgentWithServiceDto agent) {
		this.agent = agent;
	}

	public List<JourPointageDto> getSaisies() {
		return saisies;
	}

	public void setSaisies(List<JourPointageDto> saisies) {
		this.saisies = saisies;
	}

	public boolean isDPM() {
		return isDPM;
	}

	public void setDPM(boolean isDPM) {
		this.isDPM = isDPM;
	}

	public boolean isINASuperieur315() {
		return isINASuperieur315;
	}

	public void setINASuperieur315(boolean isINASuperieur315) {
		this.isINASuperieur315 = isINASuperieur315;
	}
	
}
