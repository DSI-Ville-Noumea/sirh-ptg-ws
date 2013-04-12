package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_DROITS_AGENT", identifierField = "idDroitsAgent", identifierType = Integer.class, table = "PTG_DROITS_AGENT", sequenceName = "PTG_S_DROITS_AGENT")
@NamedQuery(name = "getAgentAccessRights", query = "from DroitsAgent da JOIN FETCH da.profil where da.idAgent = :idAgent")
public class DroitsAgent {

	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "ID_AGENT_APPROBATEUR")
	private Integer idAgentApprobateur;
	
	@Column(name = "CODE_SERVICE")
	private String codeService;
	
	@Column(name = "DATE_MODIFICATION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateModification;
	
	@OneToOne
	@JoinColumn(name = "ID_DROITS_PROFIL")
	private DroitsProfil profil;
}
