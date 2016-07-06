package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "PTG_DROIT")
@NamedQueries({
		@NamedQuery(name = "getAgentAccessRights", query = "select d from Droit d where d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent"),
		@NamedQuery(name = "getAgentsApprobateurs", query = "select d from Droit d where d.approbateur = true"),
		@NamedQuery(name = "getAgentsOperateurs", query = "select d from Droit d where d.operateur = true"),
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

	@Version
    @Column(name = "version")
	private Integer version;
	
	public Integer getIdDroit() {
		return idDroit;
	}

	public void setIdDroit(Integer idDroit) {
		this.idDroit = idDroit;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public boolean isApprobateur() {
		return approbateur;
	}

	public void setApprobateur(boolean approbateur) {
		this.approbateur = approbateur;
	}

	public boolean isOperateur() {
		return operateur;
	}

	public void setOperateur(boolean operateur) {
		this.operateur = operateur;
	}

	public Integer getIdAgentDelegataire() {
		return idAgentDelegataire;
	}

	public void setIdAgentDelegataire(Integer idAgentDelegataire) {
		this.idAgentDelegataire = idAgentDelegataire;
	}

	public Droit getDroitApprobateur() {
		return droitApprobateur;
	}

	public void setDroitApprobateur(Droit droitApprobateur) {
		this.droitApprobateur = droitApprobateur;
	}

	public Set<Droit> getOperateurs() {
		return operateurs;
	}

	public void setOperateurs(Set<Droit> operateurs) {
		this.operateurs = operateurs;
	}

	public Set<DroitsAgent> getAgents() {
		return agents;
	}

	public void setAgents(Set<DroitsAgent> agents) {
		this.agents = agents;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
