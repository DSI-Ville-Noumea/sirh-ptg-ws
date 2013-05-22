package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
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
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_DROITS_AGENT", identifierField = "idDroitsAgent", identifierType = Integer.class, table = "PTG_DROITS_AGENT", sequenceName = "PTG_S_DROITS_AGENT")
public class DroitsAgent {

	@ManyToOne(optional = true)
	@JoinColumn(name = "ID_DROIT", referencedColumnName = "ID_DROIT")
	private Droit droit;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "CODE_SERVICE")
	private String codeService;
	
	@Column(name = "LIBELLE_SERVICE")
	private String libelleService;
	
	@Column(name = "DATE_MODIFICATION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateModification;
}
