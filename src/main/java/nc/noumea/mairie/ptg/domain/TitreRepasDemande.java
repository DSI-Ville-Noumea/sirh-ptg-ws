package nc.noumea.mairie.ptg.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "PTG_TR_DEMANDE")
@NamedQueries({
		@NamedQuery(name = "getTitreRepasDemandeForAgentAndDateMonthByIdDesc", query = "select tr from TitreRepasDemande tr LEFT JOIN FETCH tr.etats where tr.idAgent = :idAgent and tr.dateMonth = :dateMonth order by tr.idTrDemande desc"),
		@NamedQuery(name = "getListTitreRepasDemandeByAgentsAndDate", query = "select tr from TitreRepasDemande tr LEFT JOIN FETCH tr.etats where tr.idAgent in :idAgents and tr.dateMonth >= :fromDate and tr.dateMonth < :toDate order by tr.idTrDemande desc") })
public class TitreRepasDemande {

	@Id
	@Column(name = "ID_TR_DEMANDE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idTrDemande;

	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;

	@NotNull
	@Column(name = "DATE_MONTH", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dateMonth;

	@OneToMany(mappedBy = "titreRepasDemande", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.PERSIST)
	@OrderBy("idTrEtatDemande desc")
	private List<TitreRepasEtatDemande> etats = new ArrayList<TitreRepasEtatDemande>();

	@Version
	@Column(name = "version")
	private Integer version;

	@Transient
	public TitreRepasEtatDemande getLatestEtatTitreRepasDemande() {
		return etats.iterator().next();
	}

	public Integer getIdTrDemande() {
		return idTrDemande;
	}

	public void setIdTrDemande(Integer idTrDemande) {
		this.idTrDemande = idTrDemande;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateMonth() {
		return dateMonth;
	}

	public void setDateMonth(Date dateMonth) {
		this.dateMonth = dateMonth;
	}

	public List<TitreRepasEtatDemande> getEtats() {
		return etats;
	}

	public void setEtats(List<TitreRepasEtatDemande> etats) {
		this.etats = etats;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
}
