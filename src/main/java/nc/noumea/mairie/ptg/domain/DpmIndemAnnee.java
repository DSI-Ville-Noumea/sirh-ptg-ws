package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;


@Entity
@Table(name = "PTG_DPM_INDEM_ANNEE")
@NamedQueries({
	@NamedQuery(name = "getDpmIndemAnneeByAnnee", query = "select d from DpmIndemAnnee d where d.annee = :annee "),
	@NamedQuery(name = "getListDpmIndemAnneeOuverte", query = "select d from DpmIndemAnnee d where d.dateDebut <= :dateJour and d.dateFin >= :dateJour order by d.annee "),
	@NamedQuery(name = "getListDpmIndemAnneeOrderByAnneeDesc", query = "select d from DpmIndemAnnee d order by d.annee desc ")
})
public class DpmIndemAnnee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_DPM_INDEM_ANNEE")
	private Integer idDpmIndemAnnee;

	@NotNull
	@Column(name = "ANNEE")
	private Integer annee;

	@NotNull
	@Column(name = "DATE_DEBUT")
	@Temporal(TemporalType.DATE)
	private Date dateDebut;

	@NotNull
	@Column(name = "DATE_FIN")
	@Temporal(TemporalType.DATE)
	private Date dateFin;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "dpmIndemAnnee")
	private Set<DpmIndemChoixAgent> setDpmIndemChoixAgent = new HashSet<DpmIndemChoixAgent>();
	
	@Version
    @Column(name = "version")
	private Integer version;

	public Integer getIdDpmIndemAnnee() {
		return idDpmIndemAnnee;
	}

	public void setIdDpmIndemAnnee(Integer idDpmIndemAnnee) {
		this.idDpmIndemAnnee = idDpmIndemAnnee;
	}

	public Integer getAnnee() {
		return annee;
	}

	public void setAnnee(Integer annee) {
		this.annee = annee;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public Set<DpmIndemChoixAgent> getSetDpmIndemChoixAgent() {
		return setDpmIndemChoixAgent;
	}

	public void setSetDpmIndemChoixAgent(Set<DpmIndemChoixAgent> setDpmIndemChoixAgent) {
		this.setDpmIndemChoixAgent = setDpmIndemChoixAgent;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
