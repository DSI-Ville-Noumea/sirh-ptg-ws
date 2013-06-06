package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
	
	@ManyToMany
	@JoinTable(
			name = "PTG_DROIT_DROITS_AGENT", 
			inverseJoinColumns = @JoinColumn(name = "ID_DROIT"), 
			joinColumns = @JoinColumn(name = "ID_DROITS_AGENT"))
	private Set<Droit> droits = new HashSet<Droit>();
}
