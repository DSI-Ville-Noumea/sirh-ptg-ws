package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import nc.noumea.mairie.domain.TypeChainePaieEnum;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_EXPORT_ETATS_PAYEUR_TASK", identifierField = "idExportEtatsPayeurTask", identifierType = Integer.class, table = "PTG_EXPORT_ETATS_PAYEUR_TASK", sequenceName = "PTG_S_EXPORT_ETATS_PAYEUR_TASK")
public class ExportEtatsPayeurTask {

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
}
