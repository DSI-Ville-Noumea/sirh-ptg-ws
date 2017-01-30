package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_TR_ETAT_PRESTATAIRE")
public class TitreRepasEtatPrestataire {

	@Id
	@Column(name = "ID_TR_ETAT_PRESTATAIRE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer	idTrEtatPrestataire;

	@Column(name = "DATE_ETAT_PRESTATAIRE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date	dateEtatPrestataire;

	@NotNull
	@Column(name = "ID_AGENT")
	private Integer	idAgent;

	@Column(name = "DATE_EDITION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date	dateEdition;

	@Column(name = "LABEL", columnDefinition = "NVARCHAR2")
	private String	label;

	@Column(name = "FICHIER", columnDefinition = "NVARCHAR2")
	private String	fichier;

	@Column(name = "NODE_REF_ALFRESCO")
	private String	nodeRefAlfresco;

	@Version
	@Column(name = "version")
	private Integer	version;

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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getNodeRefAlfresco() {
		return nodeRefAlfresco;
	}

	public void setNodeRefAlfresco(String nodeRefAlfresco) {
		this.nodeRefAlfresco = nodeRefAlfresco;
	}

	public Integer getIdTrEtatPrestataire() {
		return idTrEtatPrestataire;
	}

	public void setIdTrEtatPrestataire(Integer idTrEtatPrestataire) {
		this.idTrEtatPrestataire = idTrEtatPrestataire;
	}

	public Date getDateEtatPrestataire() {
		return dateEtatPrestataire;
	}

	public void setDateEtatPrestataire(Date dateEtatPrestataire) {
		this.dateEtatPrestataire = dateEtatPrestataire;
	}
}
