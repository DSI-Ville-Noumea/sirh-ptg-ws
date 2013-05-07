package nc.noumea.mairie.ptg.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FichePointageDto {

	private Date dateLundi;
	private AgentDto agent;
	private String semaine;
	List<JourPointageDto> saisies;

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

	public AgentDto getAgent() {
		return agent;
	}

	public void setAgent(AgentDto agent) {
		this.agent = agent;
	}

	public List<JourPointageDto> getSaisies() {
		return saisies;
	}

	public void setSaisies(List<JourPointageDto> saisies) {
		this.saisies = saisies;
	}
}
