package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.util.Date;

public abstract class AbstractItemEtatPayeurDto {

	private String approbateurNom;
	private String approbateurPrenom;
	private Integer approbateurIdAgent;
	private String approbateurServiceLabel;

	private String nom;
	private String prenom;
	private Integer idAgent;
	
	private Date date;
	private String period;

	public String getApprobateurNom() {
		return approbateurNom;
	}

	public void setApprobateurNom(String approbateurNom) {
		this.approbateurNom = approbateurNom;
	}

	public String getApprobateurPrenom() {
		return approbateurPrenom;
	}

	public void setApprobateurPrenom(String approbateurPrenom) {
		this.approbateurPrenom = approbateurPrenom;
	}

	public Integer getApprobateurIdAgent() {
		return approbateurIdAgent;
	}

	public void setApprobateurIdAgent(Integer approbateurIdAgent) {
		this.approbateurIdAgent = approbateurIdAgent;
	}

	public String getApprobateurServiceLabel() {
		return approbateurServiceLabel;
	}

	public void setApprobateurServiceLabel(String approbateurServiceLabel) {
		this.approbateurServiceLabel = approbateurServiceLabel;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
}
