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
@Table(name = "PTG_ETAT_POINTAGE")
public class EtatPointage {

	@Id 
	@Column(name = "ID_ETAT_POINTAGE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idEtatPointage;
	
	@ManyToOne()
	@JoinColumn(name = "ID_POINTAGE", referencedColumnName = "ID_POINTAGE")
	private Pointage pointage;
	
	@NotNull
    @Column(name = "DATE_ETAT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEtat;
	
    @NotNull
    @Column(name = "DATE_MAJ", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateMaj;
    
	@NotNull
	@Column(name = "ETAT")
	@Enumerated(EnumType.ORDINAL)
	private EtatPointageEnum etat;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;

	@Version
    @Column(name = "version")
	private Integer version;
	
	public Integer getIdEtatPointage() {
		return idEtatPointage;
	}

	public void setIdEtatPointage(Integer idEtatPointage) {
		this.idEtatPointage = idEtatPointage;
	}

	public Pointage getPointage() {
		return pointage;
	}

	public void setPointage(Pointage pointage) {
		this.pointage = pointage;
	}

	public Date getDateEtat() {
		return dateEtat;
	}

	public void setDateEtat(Date dateEtat) {
		this.dateEtat = dateEtat;
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

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
