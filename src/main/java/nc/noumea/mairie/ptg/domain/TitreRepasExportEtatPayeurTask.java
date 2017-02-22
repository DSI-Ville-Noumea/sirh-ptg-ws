package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_TR_EXPORT_ETATS_PAYEUR_TASK")
public class TitreRepasExportEtatPayeurTask {

	@Id
	@Column(name = "ID_TR_EXPORT_ETATS_PAYEUR_TASK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer	idTitreRepasExportEtatsPayeurTask;

	@Column(name = "ID_AGENT")
	private Integer	idAgent;

	@NotNull
	@Column(name = "DATE_MONTH", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dateMonth;

	@Column(name = "DATE_CREATION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date	dateCreation;

	@Column(name = "DATE_EXPORT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date	dateExport;

	@Column(name = "TASK_STATUS")
	private String	taskStatus;

	@Version
	@Column(name = "version")
	private Integer	version;

	public Integer getIdTitreRepasExportEtatsPayeurTask() {
		return idTitreRepasExportEtatsPayeurTask;
	}

	public void setIdTitreRepasExportEtatsPayeurTask(Integer idTitreRepasExportEtatsPayeurTask) {
		this.idTitreRepasExportEtatsPayeurTask = idTitreRepasExportEtatsPayeurTask;
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

	public Date getDateMonth() {
		return dateMonth;
	}

	public void setDateMonth(Date dateMonth) {
		this.dateMonth = dateMonth;
	}
}
