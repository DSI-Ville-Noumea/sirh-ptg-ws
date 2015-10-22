package nc.noumea.mairie.titreRepas.dto;

import java.io.Serializable;
import java.util.Date;

import nc.noumea.mairie.ptg.domain.TitreRepasEtatPayeur;

public class TitreRepasEtatPayeurDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer idTrEtatPayeur;
	private Date dateEtatPayeur;
	private Integer idAgent;
	private Date dateEdition;
	private String label;
	private String fichier;
	
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
}
