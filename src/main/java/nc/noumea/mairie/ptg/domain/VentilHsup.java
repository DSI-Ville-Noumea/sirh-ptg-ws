package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
	
	@Column(name = "M_ABS")
	private int mAbsences;

	@Column(name = "M_HORS_CONTRAT")
	private int mHorsContrat;
	
	@Column(name = "M_SUP")
	private int mSup;

	@Column(name = "M_SUP_25")
	private int mSup25;

	@Column(name = "M_SUP_50")
	private int mSup50;

	@Column(name = "M_DJF")
	private int msdjf;

	@Column(name = "M_DJF_25")
	private int msdjf25;

	@Column(name = "M_DJF_50")
	private int msdjf50;
	
	@Column(name = "M_1_MAI")
	private int mMai;
	
	@Column(name = "M_NUIT")
	private int msNuit;
	
	@Column(name = "M_NORMALES")
	private int mNormales;

	@Column(name = "M_COMPLEMENTAIRES")
	private int mComplementaires;
	
	@Column(name = "M_SIMPLES")
	private int mSimple;

	@Column(name = "M_COMPOSEES")
	private int mComposees;
	
	@Column(name = "ETAT")
	@Enumerated(EnumType.ORDINAL)
	private EtatPointageEnum etat;
}
