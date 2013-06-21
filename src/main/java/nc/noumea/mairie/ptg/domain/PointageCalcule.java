package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_POINTAGE_CALCULE", identifierField = "idPointageCalcule", identifierType = Integer.class, table = "PTG_POINTAGE_CALCULE", sequenceName = "PTG_S_POINTAGE_CALCULE")
public class PointageCalcule {

	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;

	@OneToOne(optional = false)
	@JoinColumn(name = "ID_REF_TYPE_POINTAGE")
	private RefTypePointage type;
	
	@Column(name = "ETAT")
	@Enumerated(EnumType.ORDINAL)
	private EtatPointageEnum etat;
	
	@Column(name = "DATE_LUNDI")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateLundi;

	@Column(name = "DATE_DEBUT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDebut;

	@Column(name = "DATE_FIN")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateFin;

	@Column(name = "QUANTITE")
	private Integer quantite;
	
	@ManyToOne
	@JoinColumn(name = "ID_REF_PRIME", referencedColumnName = "ID_REF_PRIME")
	private RefPrime refPrime;
	
	@Transient
	public RefTypePointageEnum getTypePointageEnum() {
		return RefTypePointageEnum.getRefTypePointageEnum(type.getIdRefTypePointage());
	}
}
