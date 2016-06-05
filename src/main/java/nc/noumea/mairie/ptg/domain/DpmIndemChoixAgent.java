package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_DPM_INDEM_CHOIX_AGENT")
@NamedQueries ({
	@NamedQuery(name = "getListDpmIndemChoixAgentByListIdsAgentAndAnnee", 
			query = "select d from DpmIndemChoixAgent d where d.dpmIndemAnnee.annee = :annee and d.idAgent in :listIdsAgent "),
	@NamedQuery(name = "getDpmIndemChoixAgentByAgentAndAnnee", 
			query = "select d from DpmIndemChoixAgent d where d.dpmIndemAnnee.annee = :annee and d.idAgent = :idAgent ")
})
public class DpmIndemChoixAgent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_DPM_INDEM_CHOIX_AGENT")
	private Integer idDpmIndemChoixAgent;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "ID_DPM_INDEM_ANNEE", referencedColumnName = "ID_DPM_INDEM_ANNEE")
	private DpmIndemAnnee dpmIndemAnnee;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@NotNull
	@Column(name = "ID_AGENT_CREATION")
	private Integer idAgentCreation;
	
	@NotNull
	@Column(name = "DATE_MAJ")
	private Date dateMaj;

	@NotNull
	@Column(name = "IS_CHOIX_RECUPERATION")
	private boolean isChoixRecuperation;

	@NotNull
	@Column(name = "IS_CHOIX_INDEMNITE")
	private boolean isChoixIndemnite;

	@Version
	@Column(name = "VERSION")
	private Integer version;

	public Integer getIdDpmIndemChoixAgent() {
		return idDpmIndemChoixAgent;
	}

	public void setIdDpmIndemChoixAgent(Integer idDpmIndemChoixAgent) {
		this.idDpmIndemChoixAgent = idDpmIndemChoixAgent;
	}

	public DpmIndemAnnee getDpmIndemAnnee() {
		return dpmIndemAnnee;
	}

	public void setDpmIndemAnnee(DpmIndemAnnee dpmIndemAnnee) {
		this.dpmIndemAnnee = dpmIndemAnnee;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Integer getIdAgentCreation() {
		return idAgentCreation;
	}

	public void setIdAgentCreation(Integer idAgentCreation) {
		this.idAgentCreation = idAgentCreation;
	}

	public Date getDateMaj() {
		return dateMaj;
	}

	public void setDateMaj(Date dateMaj) {
		this.dateMaj = dateMaj;
	}

	public boolean isChoixRecuperation() {
		return isChoixRecuperation;
	}

	public void setChoixRecuperation(boolean isChoixRecuperation) {
		this.isChoixRecuperation = isChoixRecuperation;
	}

	public boolean isChoixIndemnite() {
		return isChoixIndemnite;
	}

	public void setChoixIndemnite(boolean isChoixIndemnite) {
		this.isChoixIndemnite = isChoixIndemnite;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
}
