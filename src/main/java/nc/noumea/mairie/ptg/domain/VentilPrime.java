package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_VENTIL_PRIME")
@NamedQuery(name = "getPriorVentilPrimeForAgentAndDate", query = "select vp from VentilPrime vp where vp.idVentilPrime != :idLatestVentilPrime and vp.idAgent = :idAgent and vp.dateDebutMois = :dateDebutMois order by vp.idVentilPrime desc")
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
}
