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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_DROITS_AGENT")
@NamedQueries({
		@NamedQuery(name = "getDroitsAgent", query = "select da from DroitsAgent da where da.idAgent = :idAgent"),
		@NamedQuery(name = "getListOfAgentsToInputOrApprove", query = "select da from DroitsAgent da INNER JOIN FETCH da.droits d where d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent"),
		@NamedQuery(name = "getListOfAgentsToInputOrApproveByService", query = "select da from DroitsAgent da INNER JOIN FETCH da.droits d where (d.idAgent = :idAgent or d.idAgentDelegataire = :idAgent) and da.idServiceADS = :idServiceADS") })
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
	@JoinTable(name = "PTG_DROIT_DROITS_AGENT", inverseJoinColumns = @JoinColumn(name = "ID_DROIT"), joinColumns = @JoinColumn(name = "ID_DROITS_AGENT"))
	private Set<Droit> droits = new HashSet<Droit>();

	@Column(name = "ID_SERVICE_ADS")
	private Integer idServiceADS;

	@Version
	@Column(name = "version")
	private Integer version;

	public DroitsAgent(Integer idAgent, Integer idServiceADS, String libelleService) {
		super();
		this.idAgent = idAgent;
		this.libelleService = libelleService;
		this.idServiceADS = idServiceADS;
	}

	public Integer getIdDroitsAgent() {
		return idDroitsAgent;
	}

	public void setIdDroitsAgent(Integer idDroitsAgent) {
		this.idDroitsAgent = idDroitsAgent;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public String getLibelleService() {
		return libelleService;
	}

	public void setLibelleService(String libelleService) {
		this.libelleService = libelleService;
	}

	public Date getDateModification() {
		return dateModification;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}

	public Set<Droit> getDroits() {
		return droits;
	}

	public void setDroits(Set<Droit> droits) {
		this.droits = droits;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public DroitsAgent() {
	}

	public Integer getIdServiceADS() {
		return idServiceADS;
	}

	public void setIdServiceADS(Integer idServiceADS) {
		this.idServiceADS = idServiceADS;
	}

	public String getCodeService() {
		return codeService;
	}

	public void setCodeService(String codeService) {
		this.codeService = codeService;
	}

}
