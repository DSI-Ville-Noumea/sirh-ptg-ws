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

	@Column(name = "MINUTES_IMMEDIAT")
	private int minutesImmediat;

	@Column(name = "ETAT")
	@Enumerated(EnumType.ORDINAL)
	private EtatPointageEnum etat;

	@ManyToOne(optional = false)
	@JoinColumn(name = "ID_VENTIL_DATE", referencedColumnName = "ID_VENTIL_DATE")
	private VentilDate ventilDate;

	@Version
	@Column(name = "version")
	private Integer version;
	
	@Column(name = "NOMBRE_ABSENCE_INFERIEUR_4")
	private int nombreAbsenceInferieur1;
	
	@Column(name = "NOMBRE_ABSENCE_ENTRE_1_ET_4")
	private int nombreAbsenceEntre1Et4;
	
	@Column(name = "NOMBRE_ABSENCE_SUPERIEUR_4")
	private int nombreAbsenceSuperieur1;

	public void addMinutesConcertee(Integer minutes) {
		minutesConcertee += minutes;
	}

	public void addMinutesNonConcertee(Integer minutes) {
		minutesNonConcertee += minutes;
	}

	public void addMinutesImmediate(Integer minutes) {
		minutesImmediat += minutes;
	}

	public void addNombreAbsenceInferieur1(Integer occurence) {
		nombreAbsenceInferieur1 += occurence;
	}

	public void addNombreAbsenceEntre1Et4(Integer occurence) {
		nombreAbsenceEntre1Et4 += occurence;
	}

	public void addNombreAbsenceSuperieur1(Integer occurence) {
		nombreAbsenceSuperieur1 += occurence;
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

	public int getMinutesImmediat() {
		return minutesImmediat;
	}

	public void setMinutesImmediat(int minutesImmediate) {
		this.minutesImmediat = minutesImmediate;
	}

	public Integer getNombreAbsenceInferieur1() {
		return nombreAbsenceInferieur1;
	}

	public void setNombreAbsenceInferieur1(Integer nombreAbsenceInferieur1) {
		this.nombreAbsenceInferieur1 = nombreAbsenceInferieur1;
	}

	public Integer getNombreAbsenceEntre1Et4() {
		return nombreAbsenceEntre1Et4;
	}

	public void setNombreAbsenceEntre1Et4(Integer nombreAbsenceEntre1Et4) {
		this.nombreAbsenceEntre1Et4 = nombreAbsenceEntre1Et4;
	}

	public Integer getNombreAbsenceSuperieur1() {
		return nombreAbsenceSuperieur1;
	}

	public void setNombreAbsenceSuperieur1(Integer nombreAbsenceSuperieur1) {
		this.nombreAbsenceSuperieur1 = nombreAbsenceSuperieur1;
	}
	
}
