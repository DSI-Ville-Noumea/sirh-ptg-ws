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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_DROIT")
@NamedQueries({
		@NamedQuery(name = "getAgentAccessRights", query = "from Droit d where d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent"),
		@NamedQuery(name = "getAgentsApprobateurs", query = "from Droit d where d.approbateur = true"),
		@NamedQuery(name = "getAgentsOperateurs", query = "from Droit d where d.operateur = true"),
		@NamedQuery(name = "getAgentsApprobateur", query = "select d.idAgent from Droit d inner join d.agents da where da.idAgent = :idAgent and d.approbateur = true") })
public class Droit {

	@Id 
	@Column(name = "ID_DROIT")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idDroit;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;

	@Column(name = "DATE_MODIFICATION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateModification;

	@Column(name = "IS_APPROBATEUR", nullable = false)
	@Type(type = "boolean")
	private boolean approbateur;

	@Column(name = "IS_OPERATEUR", nullable = false)
	@Type(type = "boolean")
	private boolean operateur;

	@Column(name = "ID_AGENT_DELEGATAIRE")
	private Integer idAgentDelegataire;

	@ManyToOne(optional = true)
	@JoinColumn(name = "ID_DROIT_APPROBATEUR", referencedColumnName = "ID_DROIT")
	private Droit droitApprobateur;

	@OneToMany(mappedBy = "droitApprobateur", orphanRemoval = true, cascade = CascadeType.ALL)
	private Set<Droit> operateurs = new HashSet<Droit>();

	@ManyToMany
	@JoinTable(name = "PTG_DROIT_DROITS_AGENT", joinColumns = @JoinColumn(name = "ID_DROIT"), inverseJoinColumns = @JoinColumn(name = "ID_DROITS_AGENT"))
	private Set<DroitsAgent> agents = new HashSet<DroitsAgent>();
}
