package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_DROITS_AGENT", identifierField = "idDroitsAgent", identifierType = Integer.class, table = "PTG_DROITS_AGENT", sequenceName = "PTG_S_DROITS_AGENT")
@NamedQueries({
		@NamedQuery(name = "getAgentAccessRights", query = "from DroitsAgent da where da.idAgent = :idAgent or da.idDelegataire = :idAgent"),
		@NamedQuery(name = "getAllDroitsAgentForService", query = "from DroitsAgent da where da.codeService = :codeService")
})
public class DroitsAgent {

	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "CODE_SERVICE")
	private String codeService;
	
	@Column(name = "DATE_MODIFICATION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateModification;
	
	@Column(name = "ID_DELEGATAIRE")
    private Integer idDelegataire;
	
	@Column(name = "IS_APPROBATEUR", nullable = false)
    @Type(type="boolean")
    private boolean approbateur;
	
	@Column(name = "IS_OPERATEUR", nullable = false)
    @Type(type="boolean")
    private boolean operateur;
}
