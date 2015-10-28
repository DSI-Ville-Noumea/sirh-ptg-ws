package nc.noumea.mairie.titreRepas.dto;

import java.io.Serializable;
import java.util.Date;

import nc.noumea.mairie.ptg.domain.TitreRepasEtatDemande;
import nc.noumea.mairie.ptg.dto.AgentWithServiceDto;
import nc.noumea.mairie.ptg.dto.JsonDateDeserializer;
import nc.noumea.mairie.ptg.dto.JsonDateSerializer;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class EtatTitreRepasDemandeDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9026012609607050164L;

	private Integer idTrEtatDemande;
	@JsonSerialize(using = JsonDateSerializer.class)
	@JsonDeserialize(using = JsonDateDeserializer.class)
	private Date dateMaj;
	private Integer etat;
	private Boolean commande;
	private AgentWithServiceDto operateur;

	public EtatTitreRepasDemandeDto() {
	}

	public EtatTitreRepasDemandeDto(TitreRepasEtatDemande etat) {
		this();
		this.idTrEtatDemande = etat.getIdTrEtatDemande();
		this.dateMaj = etat.getDateMaj();
		this.etat = etat.getEtat().getCodeEtat();
		this.commande = etat.getCommande();
	}

	public EtatTitreRepasDemandeDto(TitreRepasEtatDemande etat, AgentWithServiceDto agent) {
		this(etat);

		if (null != agent) {
			this.operateur = agent;
		}
	}

	public Integer getIdTrEtatDemande() {
		return idTrEtatDemande;
	}

	public void setIdTrEtatDemande(Integer idTrEtatDemande) {
		this.idTrEtatDemande = idTrEtatDemande;
	}

	public Date getDateMaj() {
		return dateMaj;
	}

	public void setDateMaj(Date dateMaj) {
		this.dateMaj = dateMaj;
	}

	public Integer getEtat() {
		return etat;
	}

	public void setEtat(Integer etat) {
		this.etat = etat;
	}

	public Boolean getCommande() {
		return commande;
	}

	public void setCommande(Boolean commande) {
		this.commande = commande;
	}

	public AgentWithServiceDto getOperateur() {
		return operateur;
	}

	public void setOperateur(AgentWithServiceDto operateur) {
		this.operateur = operateur;
	}
}
