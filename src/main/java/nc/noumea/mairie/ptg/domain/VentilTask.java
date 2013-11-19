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
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import nc.noumea.mairie.domain.TypeChainePaieEnum;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_VENTIL_TASK")
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
}
