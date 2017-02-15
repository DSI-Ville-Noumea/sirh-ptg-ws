package nc.noumea.mairie.titreRepas.dto;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatDemande;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.JsonDateDeserializer;
import nc.noumea.mairie.ptg.dto.JsonDateSerializer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TitreRepasDemandeDto {

	private Integer idTrDemande;
	private AgentWithServiceDto agent;
	private Integer idRefEtat;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateSaisie;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateMonth;
	private boolean commande;
	private String commentaire;
	private AgentWithServiceDto operateur;

	public TitreRepasDemandeDto() {
		super();
	}

	public TitreRepasDemandeDto(TitreRepasDemande titreRepasDemande, AgentWithServiceDto agent) {
		this();
		this.idTrDemande = titreRepasDemande.getIdTrDemande();
		this.agent = agent;
		this.dateMonth = titreRepasDemande.getDateMonth();
		this.setCommande(titreRepasDemande.getCommande());
	}

	public void updateEtat(TitreRepasEtatDemande etat, AgentWithServiceDto ope) {
		idRefEtat = etat.getEtat().getCodeEtat();
		dateSaisie = etat.getDateMaj();
		operateur = ope;
		commentaire = etat.getCommentaire();
		this.setCommande(etat.getCommande());
	}

	public Integer getIdTrDemande() {
		return idTrDemande;
	}

	public void setIdTrDemande(Integer idTrDemande) {
		this.idTrDemande = idTrDemande;
	}

	public Date getDateMonth() {
		return dateMonth;
	}

	public void setDateMonth(Date dateMonth) {
		this.dateMonth = dateMonth;
	}

	public boolean getCommande() {
		return commande;
	}

	public void setCommande(boolean commande) {
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

	public AgentWithServiceDto getAgent() {
		return agent;
	}

	public void setAgent(AgentWithServiceDto agent) {
		this.agent = agent;
	}

	public AgentWithServiceDto getOperateur() {
		return operateur;
	}

	public void setOperateur(AgentWithServiceDto operateur) {
		this.operateur = operateur;
	}

	public Date getDateSaisie() {
		return dateSaisie;
	}

	public void setDateSaisie(Date dateSaisie) {
		this.dateSaisie = dateSaisie;
	}

}
