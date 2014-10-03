package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import nc.noumea.mairie.domain.TypeChainePaieEnum;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "PTG_VENTIL_DATE")
public class VentilDate {

	@Id
	@Column(name = "ID_VENTIL_DATE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idVentilDate;

	@Column(name = "DATE_VENTIL")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateVentilation;

	@Column(name = "TYPE_CHAINE_PAIE")
	@Enumerated(EnumType.STRING)
	private TypeChainePaieEnum typeChainePaie;

	@Column(name = "IS_PAYE", nullable = false)
	@Type(type = "boolean")
	private boolean paye;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "PTG_POINTAGE_VENTIL_DATE", inverseJoinColumns = @JoinColumn(name = "ID_POINTAGE"), joinColumns = @JoinColumn(name = "ID_VENTIL_DATE"))
	private Set<Pointage> pointages = new HashSet<Pointage>();

	@OneToMany(mappedBy = "lastVentilDate", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<PointageCalcule> pointagesCalcules = new HashSet<PointageCalcule>();

	@Version
	@Column(name = "version")
	private Integer version;

	public Integer getIdVentilDate() {
		return idVentilDate;
	}

	public void setIdVentilDate(Integer idVentilDate) {
		this.idVentilDate = idVentilDate;
	}

	public Date getDateVentilation() {
		return dateVentilation;
	}

	public void setDateVentilation(Date dateVentilation) {
		this.dateVentilation = dateVentilation;
	}

	public TypeChainePaieEnum getTypeChainePaie() {
		return typeChainePaie;
	}

	public void setTypeChainePaie(TypeChainePaieEnum typeChainePaie) {
		this.typeChainePaie = typeChainePaie;
	}

	public boolean isPaye() {
		return paye;
	}

	public void setPaye(boolean paye) {
		this.paye = paye;
	}

	public Set<Pointage> getPointages() {
		return pointages;
	}

	public void setPointages(Set<Pointage> pointages) {
		this.pointages = pointages;
	}

	public Set<PointageCalcule> getPointagesCalcules() {
		return pointagesCalcules;
	}

	public void setPointagesCalcules(Set<PointageCalcule> pointagesCalcules) {
		this.pointagesCalcules = pointagesCalcules;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
