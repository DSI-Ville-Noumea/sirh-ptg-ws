package nc.noumea.mairie.titreRepas.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	private Integer idAgent;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateMonth;
	private Boolean commande;
	private String commentaire;
	private Integer idRefEtat;

	private AgentWithServiceDto agent;
	private AgentWithServiceDto operateur;

	private List<EtatTitreRepasDemandeDto> listEtats;

	public TitreRepasDemandeDto() {
		listEtats = new ArrayList<EtatTitreRepasDemandeDto>();
	}

	public TitreRepasDemandeDto(TitreRepasDemande titreRepasDemande, AgentWithServiceDto pAgent) {
		this();
		this.idTrDemande = titreRepasDemande.getIdTrDemande();
		this.idAgent = titreRepasDemande.getIdAgent();
		this.dateMonth = titreRepasDemande.getDateMonth();
		this.commande = titreRepasDemande.getCommande();
		this.commentaire = titreRepasDemande.getCommentaire();

		if (null != titreRepasDemande.getLatestEtatTitreRepasDemande() && null != titreRepasDemande.getLatestEtatTitreRepasDemande().getEtat()) {
			this.idRefEtat = titreRepasDemande.getLatestEtatTitreRepasDemande().getEtat().getCodeEtat();

			this.operateur = new AgentWithServiceDto();
			this.operateur.setIdAgent(titreRepasDemande.getLatestEtatTitreRepasDemande().getIdAgent());
		}

		this.agent = new AgentWithServiceDto();
		this.agent.setIdAgent(titreRepasDemande.getIdAgent());

		if (null != titreRepasDemande.getEtats() && !titreRepasDemande.getEtats().isEmpty()) {
			for (TitreRepasEtatDemande etat : titreRepasDemande.getEtats()) {
				EtatTitreRepasDemandeDto etatDto = new EtatTitreRepasDemandeDto(etat);
				this.listEtats.add(etatDto);
			}
		}
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

	public List<EtatTitreRepasDemandeDto> getListEtats() {
		return listEtats;
	}

	public void setListEtats(List<EtatTitreRepasDemandeDto> listEtats) {
		this.listEtats = listEtats;
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

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

}
