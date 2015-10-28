package nc.noumea.mairie.titreRepas.dto;

import java.io.Serializable;
import java.util.Date;

import nc.noumea.mairie.ptg.domain.TitreRepasDemande;
import nc.noumea.mairie.ptg.domain.TitreRepasEtatDemande;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.JsonDateDeserializer;
import nc.noumea.mairie.ptg.dto.JsonDateSerializer;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class TitreRepasDemandeDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1727622068229921626L;

	private Integer idTrDemande;
	private AgentWithServiceDto agent;
	private Integer idRefEtat;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateSaisie;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateMonth;
	private Boolean commande;
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
		this.commande = titreRepasDemande.getCommande();
		this.commentaire = titreRepasDemande.getCommentaire();
	}

	public void updateEtat(TitreRepasEtatDemande etat, AgentWithServiceDto ope) {
		idRefEtat = etat.getEtat().getCodeEtat();
		dateSaisie = etat.getDateMaj();
		operateur = ope;
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

}
