package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_VENTIL_HSUP", identifierField = "idVentilHSup", identifierType = Integer.class, table = "PTG_VENTIL_HSUP", sequenceName = "PTG_S_VENTIL_HSUP")
public class VentilHsup {

	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "DATE_LUNDI")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateLundi;
	
	@Column(name = "H_ABS", columnDefinition = "number")
	private double hAbsences;

	@Column(name = "H_HORS_CONTRAT", columnDefinition = "number")
	private double hHorsContrat;
	
	@Column(name = "H_SUP", columnDefinition = "number")
	private double hSup;

	@Column(name = "H_SUP_25", columnDefinition = "number")
	private double hSup25;

	@Column(name = "H_SUP_50", columnDefinition = "number")
	private double hSup50;

	@Column(name = "H_DJF", columnDefinition = "number")
	private double hsdjf;

	@Column(name = "H_DJF_25", columnDefinition = "number")
	private double hsdjf25;

	@Column(name = "H_DJF_50", columnDefinition = "number")
	private double hsdjf50;
	
	@Column(name = "H_1_MAI", columnDefinition = "number")
	private double hMai;
	
	@Column(name = "H_NUIT", columnDefinition = "number")
	private double hsNuit;
	
	@Column(name = "H_NORMALES", columnDefinition = "number")
	private double hNormales;

	@Column(name = "H_COMPLEMENTAIRES", columnDefinition = "number")
	private double hComplementaires;
	
	@Column(name = "H_SIMPLES", columnDefinition = "number")
	private double hSimple;

	@Column(name = "H_COMPOSEES", columnDefinition = "number")
	private double hComposees;
}
