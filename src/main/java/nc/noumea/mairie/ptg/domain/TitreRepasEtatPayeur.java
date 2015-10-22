package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_TR_ETAT_PAYEUR")
@NamedQueries({
	@NamedQuery(name = "getListEditionsTitreRepasEtatPayeur", query = "select ep from TitreRepasEtatPayeur ep order by ep.dateEtatPayeur desc"),
	@NamedQuery(name = "getTitreRepasEtatPayeurById", query = "select ep from TitreRepasEtatPayeur ep where ep.idTrEtatPayeur = :idEtatPayeur") 
})
public class TitreRepasEtatPayeur {
	
	@Id
	@Column(name = "ID_TR_ETAT_PAYEUR")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idTrEtatPayeur;

	@Column(name = "DATE_ETAT_PAYEUR")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateEtatPayeur;

	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;

	@Column(name = "DATE_EDITION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateEdition;

	@Column(name = "LABEL", columnDefinition = "NVARCHAR2")
	private String label;

	@Column(name = "FICHIER", columnDefinition = "NVARCHAR2")
	private String fichier;

	@Version
	@Column(name = "version")
	private Integer version;
	

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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
