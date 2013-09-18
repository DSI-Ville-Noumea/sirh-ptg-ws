package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_ETAT_POINTAGE", identifierColumn = "ID_ETAT_POINTAGE", identifierType = Integer.class, identifierField = "idEtatPointage", sequenceName = "PTG_S_ETAT_POINTAGE")
public class EtatPointage {

	@ManyToOne()
	@JoinColumn(name = "ID_POINTAGE", referencedColumnName = "ID_POINTAGE")
	private Pointage pointage;
	
	@NotNull
    @Column(name = "DATE_ETAT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEtat;
	
    @NotNull
    @Column(name = "DATE_MAJ", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateMaj;
    
	@NotNull
	@Column(name = "ETAT")
	@Enumerated(EnumType.ORDINAL)
	private EtatPointageEnum etat;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
}
