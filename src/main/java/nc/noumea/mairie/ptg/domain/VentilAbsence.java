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
import javax.persistence.Version;

@Entity
@Table(name = "PTG_VENTIL_ABSENCE")
@NamedQuery(name = "getPriorVentilAbsenceForAgentAndDate", query = "select va from VentilAbsence va where va.idVentilAbsence != :idLatestVentilAbsence and va.idAgent = :idAgent and va.dateLundi = :dateLundi order by va.idVentilAbsence desc")
public class VentilAbsence {

	@Id 
	@Column(name = "ID_VENTIL_ABSENCE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idVentilAbsence;
	
    @Column(name = "ID_AGENT")
    private Integer idAgent;
    
    @Column(name = "DATE_LUNDI")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLundi;
    
    @Column(name = "MINUTES_CONCERTEE")
    private int minutesConcertee;
    
    @Column(name = "MINUTES_NON_CONCERTEE")
    private int minutesNonConcertee;
    
    @Column(name = "ETAT")
    @Enumerated(EnumType.ORDINAL)
    private EtatPointageEnum etat;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_VENTIL_DATE", referencedColumnName = "ID_VENTIL_DATE")
    private VentilDate ventilDate;

    @Version
    @Column(name = "version")
	private Integer version;
    
    public void addMinutesConcertee(Integer minutes) {
        minutesConcertee += minutes;
    }

    public void addMinutesNonConcertee(Integer minutes) {
        minutesNonConcertee += minutes;
    }

	public Integer getIdVentilAbsence() {
		return idVentilAbsence;
	}

	public void setIdVentilAbsence(Integer idVentilAbsence) {
		this.idVentilAbsence = idVentilAbsence;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateLundi() {
		return dateLundi;
	}

	public void setDateLundi(Date dateLundi) {
		this.dateLundi = dateLundi;
	}

	public int getMinutesConcertee() {
		return minutesConcertee;
	}

	public void setMinutesConcertee(int minutesConcertee) {
		this.minutesConcertee = minutesConcertee;
	}

	public int getMinutesNonConcertee() {
		return minutesNonConcertee;
	}

	public void setMinutesNonConcertee(int minutesNonConcertee) {
		this.minutesNonConcertee = minutesNonConcertee;
	}

	public EtatPointageEnum getEtat() {
		return etat;
	}

	public void setEtat(EtatPointageEnum etat) {
		this.etat = etat;
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
}
