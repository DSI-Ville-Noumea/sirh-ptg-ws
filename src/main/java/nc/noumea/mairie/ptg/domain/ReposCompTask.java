package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_RC_TASK")
public class ReposCompTask {

	@Id 
	@Column(name = "ID_RC_TASK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idRcTask;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@NotNull
	@Column(name = "ID_AGENT_CREATION")
	private Integer idAgentCreation;
	
	@NotNull
	@Column(name = "DATE_CREATION")
	private Date dateCreation;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "ID_VENTIL_DATE", referencedColumnName = "ID_VENTIL_DATE")
	private VentilDate ventilDate;
	
	@Column(name = "DATE_CALCUL")
	private Date dateCalcul;
	
	@Column(name = "TASK_STATUS")
	private String taskStatus;

	@Version
    @Column(name = "version")
	private Integer version;
	
	public Integer getIdRcTask() {
		return idRcTask;
	}

	public void setIdRcTask(Integer idRcTask) {
		this.idRcTask = idRcTask;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getIdAgentCreation() {
		return idAgentCreation;
	}

	public void setIdAgentCreation(Integer idAgentCreation) {
		this.idAgentCreation = idAgentCreation;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public VentilDate getVentilDate() {
		return ventilDate;
	}

	public void setVentilDate(VentilDate ventilDate) {
		this.ventilDate = ventilDate;
	}

	public Date getDateCalcul() {
		return dateCalcul;
	}

	public void setDateCalcul(Date dateCalcul) {
		this.dateCalcul = dateCalcul;
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
