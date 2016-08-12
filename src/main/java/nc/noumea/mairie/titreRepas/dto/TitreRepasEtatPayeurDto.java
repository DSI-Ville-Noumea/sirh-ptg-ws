package nc.noumea.mairie.titreRepas.dto;

import java.io.Serializable;
import java.util.Date;

import nc.noumea.mairie.alfresco.cmis.AlfrescoCMISService;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.JsonDateDeserializer;
import nc.noumea.mairie.ptg.dto.JsonDateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TitreRepasEtatPayeurDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer idTrEtatPayeur;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateEtatPayeur;
	private Integer idAgent;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateEdition;
	private String label;
	private String fichier;
	private AgentWithServiceDto agent;
	
	private String urlAlfresco;
	
	public TitreRepasEtatPayeurDto(){
	}
	
	public TitreRepasEtatPayeurDto(TitreRepasEtatPayeur titreRepasEtatPayeur) {
		this();
		this.idTrEtatPayeur = titreRepasEtatPayeur.getIdTrEtatPayeur();
		this.dateEtatPayeur = titreRepasEtatPayeur.getDateEtatPayeur();
		this.idAgent = titreRepasEtatPayeur.getIdAgent();
		this.dateEdition = titreRepasEtatPayeur.getDateEdition();
		this.label = titreRepasEtatPayeur.getLabel();
		this.fichier = titreRepasEtatPayeur.getFichier();
		
		this.urlAlfresco = AlfrescoCMISService.getUrlOfDocument(titreRepasEtatPayeur.getNodeRefAlfresco());
	}
	
	public Integer getIdTrEtatPayeur() {
		return idTrEtatPayeur;
	}
	public void setIdTrEtatPayeur(Integer idTrEtatPayeur) {
		this.idTrEtatPayeur = idTrEtatPayeur;
	}
	public Date getDateEtatPayeur() {
		return dateEtatPayeur;
	}
	public void setDateEtatPayeur(Date dateEtatPayeur) {
		this.dateEtatPayeur = dateEtatPayeur;
	}
	public Integer getIdAgent() {
		return idAgent;
	}
	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
	public Date getDateEdition() {
		return dateEdition;
	}
	public void setDateEdition(Date dateEdition) {
		this.dateEdition = dateEdition;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getFichier() {
		return fichier;
	}
	public void setFichier(String fichier) {
		this.fichier = fichier;
	}

	public AgentWithServiceDto getAgent() {
		return agent;
	}

	public void setAgent(AgentWithServiceDto agent) {
		this.agent = agent;
	}

	public String getUrlAlfresco() {
		return urlAlfresco;
	}

	public void setUrlAlfresco(String urlAlfresco) {
		this.urlAlfresco = urlAlfresco;
	}
	
}
