package nc.noumea.mairie.ptg.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

@Entity
@Table(name = "PTG_POINTAGE")
@Audited
@NamedQueries({
		@NamedQuery(name = "getPointageForAgentAndDateLundiByIdDesc", query = "select ptg from Pointage ptg LEFT JOIN FETCH ptg.motif LEFT JOIN FETCH ptg.commentaire where ptg.idAgent = :idAgent and ptg.dateLundi = :dateLundi order by ptg.idPointage desc"),
		@NamedQuery(name = "getListPointageByAgentsAndDate", query = "select ptg from Pointage ptg LEFT JOIN FETCH ptg.motif LEFT JOIN FETCH ptg.commentaire LEFT JOIN FETCH ptg.refPrime JOIN FETCH ptg.type where ptg.idAgent in :idAgents and ptg.dateDebut >= :fromDate and ptg.dateDebut < :toDate order by ptg.idPointage desc"),
		@NamedQuery(name = "getListPointageByAgentsTypeAndDate", query = "select ptg from Pointage ptg LEFT JOIN FETCH ptg.motif LEFT JOIN FETCH ptg.commentaire LEFT JOIN FETCH ptg.refPrime JOIN FETCH ptg.type where ptg.idAgent in :idAgents and ptg.dateDebut >= :fromDate and ptg.dateDebut < :toDate and ptg.type.idRefTypePointage = :idRefTypePointage order by ptg.idPointage desc") })
public class Pointage {

	@Id
	@Column(name = "ID_POINTAGE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idPointage;

	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;

	@OneToOne(optional = true)
	@JoinColumn(name = "ID_TYPE_POINTAGE")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private RefTypePointage type;

	@OneToMany(mappedBy = "pointage", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("idEtatPointage desc")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	private List<EtatPointage> etats = new ArrayList<EtatPointage>();

	@Column(name = "DATE_LUNDI")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateLundi;

	@Column(name = "DATE_DEBUT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDebut;

	@Column(name = "DATE_FIN")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateFin;

	@Column(name = "QUANTITE")
	private Integer quantite;

	@OneToOne(optional = true)
	@JoinColumn(name = "ID_POINTAGE_PARENT")
	private Pointage pointageParent;

	@ManyToOne
	@JoinColumn(name = "ID_REF_PRIME", referencedColumnName = "ID_REF_PRIME")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private RefPrime refPrime;

	@Column(name = "IS_HSUP_RECUPEREE")
	@Type(type = "boolean")
	private Boolean heureSupRecuperee;

	@Column(name = "IS_HSUP_RAPPEL_SERVICE")
	@Type(type = "boolean")
	private Boolean heureSupRappelService;

	@Column(name = "IS_PRIME_FORFAIT_DPM_RECUP")
	@Type(type = "boolean")
	private Boolean primeForfaitDpmRecup;

	@OneToOne(optional = true)
	@JoinColumn(name = "ID_REF_TYPE_ABSENCE")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private RefTypeAbsence refTypeAbsence;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(name = "ID_COMMENT_MOTIF")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private PtgComment motif;

	@OneToOne(fetch = FetchType.EAGER, optional = true, orphanRemoval = false)
	@JoinColumn(name = "ID_MOTIF_HSUP")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private MotifHeureSup motifHsup;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(name = "ID_COMMENT_COMMENTAIRE")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	private PtgComment commentaire;

	@ManyToMany
	@JoinTable(name = "PTG_POINTAGE_VENTIL_DATE", joinColumns = @JoinColumn(name = "ID_POINTAGE"), inverseJoinColumns = @JoinColumn(name = "ID_VENTIL_DATE"))
	@OrderBy("idVentilDate desc")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@NotAudited
	private List<VentilDate> ventilations = new ArrayList<VentilDate>();

	@Column(name = "ID_AGENT_MODIFICATION")
	private Integer idAgentModification;

	@Version
	@Column(name = "version")
	private Integer version;

	@Transient
	public RefTypePointageEnum getTypePointageEnum() {
		return RefTypePointageEnum.getRefTypePointageEnum(type.getIdRefTypePointage());
	}

	@Transient
	public EtatPointage getLatestEtatPointage() {
		return etats.iterator().next();
	}
	
	@Transient
	public EtatPointage getLatestEtatPointageWithPointageNotPersist() {
		// ici si le pointage n est pas encore persiste en base, 
		// l annotation @OrderBy ne fonctionne pas 
		// on trie donc nous meme
		Collections.sort(etats);
		return null != etats && !etats.isEmpty() ? etats.iterator().next() : null;
	}

	@Transient
	public VentilDate getLatestVentilDate() {
		if (ventilations.size() == 0)
			return null;
		return ventilations.iterator().next();
	}

	public Integer getIdPointage() {
		return idPointage;
	}

	public void setIdPointage(Integer idPointage) {
		this.idPointage = idPointage;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public RefTypePointage getType() {
		return type;
	}

	public void setType(RefTypePointage type) {
		this.type = type;
	}

	public List<EtatPointage> getEtats() {
		return etats;
	}

	public void setEtats(List<EtatPointage> etats) {
		this.etats = etats;
	}

	public Date getDateLundi() {
		return dateLundi;
	}

	public void setDateLundi(Date dateLundi) {
		this.dateLundi = dateLundi;
	}

	public Date getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Date getDateFin() {
		return dateFin;
	}

	public void setDateFin(Date dateFin) {
		this.dateFin = dateFin;
	}

	public Integer getQuantite() {
		return quantite;
	}

	public void setQuantite(Integer quantite) {
		this.quantite = quantite;
	}

	public Pointage getPointageParent() {
		return pointageParent;
	}

	public void setPointageParent(Pointage pointageParent) {
		this.pointageParent = pointageParent;
	}

	public RefPrime getRefPrime() {
		return refPrime;
	}

	public void setRefPrime(RefPrime refPrime) {
		this.refPrime = refPrime;
	}

	public Boolean getHeureSupRecuperee() {
		return heureSupRecuperee;
	}

	public void setHeureSupRecuperee(Boolean heureSupRecuperee) {
		this.heureSupRecuperee = heureSupRecuperee;
	}

	public PtgComment getMotif() {
		return motif;
	}

	public void setMotif(PtgComment motif) {
		this.motif = motif;
	}

	public PtgComment getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(PtgComment commentaire) {
		this.commentaire = commentaire;
	}

	public List<VentilDate> getVentilations() {
		return ventilations;
	}

	public void setVentilations(List<VentilDate> ventilations) {
		this.ventilations = ventilations;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public RefTypeAbsence getRefTypeAbsence() {
		return refTypeAbsence;
	}

	public void setRefTypeAbsence(RefTypeAbsence refTypeAbsence) {
		this.refTypeAbsence = refTypeAbsence;
	}

	public Boolean getHeureSupRappelService() {
		return heureSupRappelService;
	}

	public void setHeureSupRappelService(Boolean heureSupRappelService) {
		this.heureSupRappelService = heureSupRappelService;
	}

	public MotifHeureSup getMotifHsup() {
		return motifHsup;
	}

	public void setMotifHsup(MotifHeureSup motifHsup) {
		this.motifHsup = motifHsup;
	}

	public Boolean getPrimeForfaitDpmRecup() {
		return primeForfaitDpmRecup;
	}

	public void setPrimeForfaitDpmRecup(Boolean primeForfaitDpmRecup) {
		this.primeForfaitDpmRecup = primeForfaitDpmRecup;
	}

	public Integer getIdAgentModification() {
		return idAgentModification;
	}

	public void setIdAgentModification(Integer idAgentModification) {
		this.idAgentModification = idAgentModification;
	}

}
