package nc.noumea.mairie.titreRepas.dto;

import java.io.Serializable;
import java.util.Date;

import nc.noumea.mairie.ptg.domain.TitreRepasDemande;

public class TitreRepasDemandeDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer idTrDemande;
	private Integer idAgent;
    private Date dateMonth;
	private Boolean commande;
	private String commentaire;
	private Integer idRefEtat;
	
	public TitreRepasDemandeDto() {
	}
	
	public TitreRepasDemandeDto(TitreRepasDemande titreRepasDemande) {
		this();
		this.idTrDemande = titreRepasDemande.getIdTrDemande();
		this.idAgent = titreRepasDemande.getIdAgent();
		this.dateMonth = titreRepasDemande.getDateMonth();
		this.commande = titreRepasDemande.getCommande();
		this.commentaire = titreRepasDemande.getCommentaire();
		
		if(null != titreRepasDemande.getLatestEtatTitreRepasDemande()
				&& null != titreRepasDemande.getLatestEtatTitreRepasDemande().getEtat()) {
			this.idRefEtat = titreRepasDemande.getLatestEtatTitreRepasDemande().getEtat().getCodeEtat();
		}
	}
	
	public Integer getIdTrDemande() {
		return idTrDemande;
	}
	public void setIdTrDemande(Integer idTrDemande) {
		this.idTrDemande = idTrDemande;
	}
	public Integer getIdAgent() {
		return idAgent;
	}
	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}
	public Date getDateMonth() {
		return dateMonth;
	}
	public void setDateMonth(Date dateMonth) {
		this.dateMonth = dateMonth;
	}
	public Boolean getCommande() {
		return commande;
	}
	public void setCommande(Boolean commande) {
		this.commande = commande;
	}
	public String getCommentaire() {
		return commentaire;
	}
	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}
	public Integer getIdRefEtat() {
		return idRefEtat;
	}
	public void setIdRefEtat(Integer idRefEtat) {
		this.idRefEtat = idRefEtat;
	}
	
	
}
