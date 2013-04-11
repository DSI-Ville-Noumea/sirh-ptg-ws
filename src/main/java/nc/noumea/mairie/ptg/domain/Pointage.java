package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;


@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_POINTAGE", identifierField = "idPointage", identifierType = Integer.class, table = "PTG_POINTAGE", sequenceName = "PTG_S_POINTAGE")
public class Pointage {

	@OneToOne(optional = false)
	@JoinColumn(name = "ID_TYPE_POINTAGE")
	private TypePointage type;
	
	@OneToMany(mappedBy = "pointage", fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<EtatPointage> etats;
	
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
	
	@OneToOne(optional = true)
	@JoinColumn(name = "ID_POINTAGE_PARENT")
	private Pointage pointageParent;
	
}
