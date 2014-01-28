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

import nc.noumea.mairie.domain.TypeChainePaieEnum;

@Entity
@Table(name = "PTG_EXPORT_ETATS_PAYEUR_TASK")
public class ExportEtatsPayeurTask {

	@Id 
	@Column(name = "ID_EXPORT_ETATS_PAYEUR_TASK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idExportEtatsPayeurTask;
	
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "DATE_CREATION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreation;
	
	@Column(name = "DATE_EXPORT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateExport;

	@Column(name = "TYPE_CHAINE_PAIE")
	@Enumerated(EnumType.STRING)
	private TypeChainePaieEnum typeChainePaie;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "ID_VENTIL_DATE", referencedColumnName = "ID_VENTIL_DATE")
    private VentilDate ventilDate;

	@Column(name = "TASK_STATUS")
	private String taskStatus;

	@Version
    @Column(name = "version")
	private Integer version;
	
	public Integer getIdExportEtatsPayeurTask() {
		return idExportEtatsPayeurTask;
	}

	public void setIdExportEtatsPayeurTask(Integer idExportEtatsPayeurTask) {
		this.idExportEtatsPayeurTask = idExportEtatsPayeurTask;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public Date getDateExport() {
		return dateExport;
	}

	public void setDateExport(Date dateExport) {
		this.dateExport = dateExport;
	}

	public TypeChainePaieEnum getTypeChainePaie() {
		return typeChainePaie;
	}

	public void setTypeChainePaie(TypeChainePaieEnum typeChainePaie) {
		this.typeChainePaie = typeChainePaie;
	}

	public VentilDate getVentilDate() {
		return ventilDate;
	}

	public void setVentilDate(VentilDate ventilDate) {
		this.ventilDate = ventilDate;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
