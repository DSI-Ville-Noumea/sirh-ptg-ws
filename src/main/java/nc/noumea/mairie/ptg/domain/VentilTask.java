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
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import nc.noumea.mairie.domain.TypeChainePaieEnum;

@Entity
@Table(name = "PTG_VENTIL_TASK")
public class VentilTask {

	@Id 
	@Column(name = "ID_VENTIL_TASK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idVentilTask;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@NotNull
	@Column(name = "ID_AGENT_CREATION")
	private Integer idAgentCreation;
	
	@NotNull
	@Column(name = "DATE_CREATION")
	private Date dateCreation;
	
	@NotNull
	@Column(name = "TYPE_CHAINE_PAIE")
	@Enumerated(EnumType.STRING)
	private TypeChainePaieEnum typeChainePaie;
	
	@OneToOne(optional = true)
	@JoinColumn(name = "ID_TYPE_POINTAGE")
	private RefTypePointage refTypePointage;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "ID_VENTIL_DATE_FROM", referencedColumnName = "ID_VENTIL_DATE")
    private VentilDate ventilDateFrom;
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "ID_VENTIL_DATE_TO", referencedColumnName = "ID_VENTIL_DATE")
	private VentilDate ventilDateTo;
	
	@Column(name = "DATE_VENTILATION")
	private Date dateVentilation;
	
	@Column(name = "TASK_STATUS")
	private String taskStatus;

	@Version
    @Column(name = "version")
	private Integer version;
	
	public Integer getIdVentilTask() {
		return idVentilTask;
	}

	public void setIdVentilTask(Integer idVentilTask) {
		this.idVentilTask = idVentilTask;
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

	public TypeChainePaieEnum getTypeChainePaie() {
		return typeChainePaie;
	}

	public void setTypeChainePaie(TypeChainePaieEnum typeChainePaie) {
		this.typeChainePaie = typeChainePaie;
	}

	public RefTypePointage getRefTypePointage() {
		return refTypePointage;
	}

	public void setRefTypePointage(RefTypePointage refTypePointage) {
		this.refTypePointage = refTypePointage;
	}

	public VentilDate getVentilDateFrom() {
		return ventilDateFrom;
	}

	public void setVentilDateFrom(VentilDate ventilDateFrom) {
		this.ventilDateFrom = ventilDateFrom;
	}

	public VentilDate getVentilDateTo() {
		return ventilDateTo;
	}

	public void setVentilDateTo(VentilDate ventilDateTo) {
		this.ventilDateTo = ventilDateTo;
	}

	public Date getDateVentilation() {
		return dateVentilation;
	}

	public void setDateVentilation(Date dateVentilation) {
		this.dateVentilation = dateVentilation;
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
