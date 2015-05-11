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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

@Entity
@Table(name = "PTG_VENTIL_PRIME") 
@NamedQuery(name = "getPriorVentilPrimeForAgentAndDate", query = "select vp from VentilPrime vp where vp.idVentilPrime != :idLatestVentilPrime and vp.ventilDate.idVentilDate != :idVentilDate and vp.idAgent = :idAgent and vp.dateDebutMois = :dateDebutMois and vp.refPrime.idRefPrime=:idRefPrime order by vp.idVentilPrime desc")
public class VentilPrime {

	@Id 
	@Column(name = "ID_VENTIL_PRIME")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idVentilPrime;
	
    @Column(name = "ID_AGENT")
    private Integer idAgent;
    
    @Column(name = "DATE_DEBUT_MOIS")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDebutMois;
    
    @ManyToOne
    @JoinColumn(name = "ID_REF_PRIME", referencedColumnName = "ID_REF_PRIME")
    private RefPrime refPrime;
    
    @Column(name = "ETAT")
    @Enumerated(EnumType.ORDINAL)
    private EtatPointageEnum etat;
    
    @Column(name = "QUANTITE")
    private Integer quantite;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_VENTIL_DATE", referencedColumnName = "ID_VENTIL_DATE")
    private VentilDate ventilDate;
    
    @Column(name = "DATE_PRIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePrime;

    @Version
    @Column(name = "version")
	private Integer version;
    
    @Transient
    public void addQuantite(Integer quantite) {
        if (this.quantite == null) {
            this.quantite = 0;
        }
        this.quantite += quantite;
    }

    public Integer getIdRefPrime() {
        return refPrime.getIdRefPrime();
    }

	public Integer getIdVentilPrime() {
		return idVentilPrime;
	}

	public void setIdVentilPrime(Integer idVentilPrime) {
		this.idVentilPrime = idVentilPrime;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateDebutMois() {
		return dateDebutMois;
	}

	public void setDateDebutMois(Date dateDebutMois) {
		this.dateDebutMois = dateDebutMois;
	}

	public RefPrime getRefPrime() {
		return refPrime;
	}

	public void setRefPrime(RefPrime refPrime) {
		this.refPrime = refPrime;
	}

	public EtatPointageEnum getEtat() {
		return etat;
	}

	public void setEtat(EtatPointageEnum etat) {
		this.etat = etat;
	}

	public Integer getQuantite() {
		return quantite;
	}

	public void setQuantite(Integer quantite) {
		this.quantite = quantite;
	}

	public VentilDate getVentilDate() {
		return ventilDate;
	}

	public void setVentilDate(VentilDate ventilDate) {
		this.ventilDate = ventilDate;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getDatePrime() {
		return datePrime;
	}

	public void setDatePrime(Date datePrime) {
		this.datePrime = datePrime;
	}
    
    
}
