package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_DROITS_AGENT")
@NamedQueries({
		@NamedQuery(name = "getListOfAgentsToInputOrApprove", query = "from DroitsAgent da INNER JOIN FETCH da.droits d where d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent"),
		@NamedQuery(name = "getListOfAgentsToInputOrApproveByService", query = "from DroitsAgent da INNER JOIN FETCH da.droits d where (d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent) and da.codeService = :codeService")
})
public class DroitsAgent {

	@Id 
	@Column(name = "ID_DROITS_AGENT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idDroitsAgent;
	
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
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(
			name = "PTG_DROIT_DROITS_AGENT", 
			inverseJoinColumns = @JoinColumn(name = "ID_DROIT"), 
			joinColumns = @JoinColumn(name = "ID_DROITS_AGENT"))
	private Set<Droit> droits = new HashSet<Droit>();
}
