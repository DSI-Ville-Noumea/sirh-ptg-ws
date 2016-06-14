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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_POINTAGE_CALCULE")
public class PointageCalcule {

	@Id 
	@Column(name = "ID_POINTAGE_CALCULE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idPointageCalcule;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;

	@OneToOne(optional = true)
	@JoinColumn(name = "ID_REF_TYPE_POINTAGE")
	private RefTypePointage type;
	
	@Column(name = "ETAT")
	@Enumerated(EnumType.ORDINAL)
	private EtatPointageEnum etat;
	
	@Column(name = "DATE_LUNDI")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateLundi;

	@Column(name = "DATE_DEBUT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDebut;

	@Column(name = "DATE_FIN")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateFin;

	@Column(name = "QUANTITE", columnDefinition = "numeric")
	private Double quantite;
	
	@ManyToOne
	@JoinColumn(name = "ID_REF_PRIME", referencedColumnName = "ID_REF_PRIME")
	private RefPrime refPrime;
	
	@ManyToOne
	@JoinColumn(name = "ID_VENTIL_DATE", referencedColumnName = "ID_VENTIL_DATE")
	private VentilDate lastVentilDate;
	
	@Version
    @Column(name = "version")
	private Integer version;
	
	@Transient
	public RefTypePointageEnum getTypePointageEnum() {
		return RefTypePointageEnum.getRefTypePointageEnum(type.getIdRefTypePointage());
	}
	
	public void addQuantite(Double qte) {
		this.quantite = this.quantite == null ? 0.0 : this.quantite;
		this.quantite += qte;
	}

	public Integer getIdPointageCalcule() {
		return idPointageCalcule;
	}

	public void setIdPointageCalcule(Integer idPointageCalcule) {
		this.idPointageCalcule = idPointageCalcule;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public RefTypePointage getType() {
		return type;
	}

	public void setType(RefTypePointage type) {
		this.type = type;
	}

	public EtatPointageEnum getEtat() {
		return etat;
	}

	public void setEtat(EtatPointageEnum etat) {
		this.etat = etat;
	}

	public Date getDateLundi() {
		return dateLundi;
	}

	public void setDateLundi(Date dateLundi) {
		this.dateLundi = dateLundi;
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

	public Double getQuantite() {
		return quantite;
	}

	public void setQuantite(Double quantite) {
		this.quantite = quantite;
	}

	public RefPrime getRefPrime() {
		return refPrime;
	}

	public void setRefPrime(RefPrime refPrime) {
		this.refPrime = refPrime;
	}

	public VentilDate getLastVentilDate() {
		return lastVentilDate;
	}

	public void setLastVentilDate(VentilDate lastVentilDate) {
		this.lastVentilDate = lastVentilDate;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
}
