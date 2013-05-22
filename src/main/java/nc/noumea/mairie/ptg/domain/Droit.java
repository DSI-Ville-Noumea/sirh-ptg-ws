package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_DROIT", identifierField = "idDroit", identifierType = Integer.class, table = "PTG_DROIT", sequenceName = "PTG_S_DROIT")
@NamedQueries({
		@NamedQuery(name = "getAgentAccessRights", query = "from Droit d where d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent"),
		@NamedQuery(name = "getAllDroitsAgentForService", query = "from Droit d where d.codeService = :codeService")
})
public class Droit {

	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@Column(name = "CODE_SERVICE")
	private String codeService;
	
	@Column(name = "DATE_MODIFICATION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateModification;
	
	@Column(name = "IS_APPROBATEUR", nullable = false)
    @Type(type="boolean")
    private boolean approbateur;
	
	@Column(name = "IS_OPERATEUR", nullable = false)
    @Type(type="boolean")
    private boolean operateur;
	
	@Column(name = "ID_AGENT_DELEGATAIRE")
    private Integer idAgentDelegataire;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "ID_DROIT_APPROBATEUR", referencedColumnName = "ID_DROIT")
	private Droit droitApprobateur;
}
