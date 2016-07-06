package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_TR_ETAT_DEMANDE")
public class TitreRepasEtatDemande {

	@Id 
	@Column(name = "ID_TR_ETAT_DEMANDE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idTrEtatDemande;
	
	@ManyToOne()
	@JoinColumn(name = "ID_TR_DEMANDE", referencedColumnName = "ID_TR_DEMANDE")
	private TitreRepasDemande titreRepasDemande;
	
    @NotNull
    @Column(name = "DATE_MAJ", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateMaj;
    
	@NotNull
	@Column(name = "ETAT")
	@Enumerated(EnumType.ORDINAL)
	private EtatPointageEnum etat;
	
	@Column(name = "COMMENTAIRE", columnDefinition="text")
	private String commentaire;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "COMMANDE")
	private Boolean commande;

	@Version
    @Column(name = "version")
	private Integer version;
	
	

	public Integer getIdTrEtatDemande() {
		return idTrEtatDemande;
	}

	public void setIdTrEtatDemande(Integer idTrEtatDemande) {
		this.idTrEtatDemande = idTrEtatDemande;
	}

	public TitreRepasDemande getTitreRepasDemande() {
		return titreRepasDemande;
	}

	public void setTitreRepasDemande(TitreRepasDemande titreRepasDemande) {
		this.titreRepasDemande = titreRepasDemande;
	}

	public Date getDateMaj() {
		return dateMaj;
	}

	public void setDateMaj(Date dateMaj) {
		this.dateMaj = dateMaj;
	}

	public EtatPointageEnum getEtat() {
		return etat;
	}

	public void setEtat(EtatPointageEnum etat) {
		this.etat = etat;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Boolean getCommande() {
		return commande;
	}

	public void setCommande(Boolean commande) {
		this.commande = commande;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}
	
}
