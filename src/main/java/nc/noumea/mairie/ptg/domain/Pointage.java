package nc.noumea.mairie.ptg.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_POINTAGE", identifierField = "idPointage", identifierType = Integer.class, table = "PTG_POINTAGE", sequenceName = "PTG_S_POINTAGE")
@NamedQueries({
	@NamedQuery(name = "getPointageForAgentAndDateLundiByIdDesc", query = "select ptg from Pointage ptg LEFT JOIN FETCH ptg.motif LEFT JOIN FETCH ptg.commentaire where ptg.idAgent = :idAgent and ptg.dateLundi = :dateLundi order by ptg.idPointage desc"),
	@NamedQuery(name = "getListPointageByAgentsAndDate", query = "select ptg from Pointage ptg LEFT JOIN FETCH ptg.motif LEFT JOIN FETCH ptg.commentaire LEFT JOIN FETCH ptg.refPrime JOIN FETCH ptg.type where ptg.idAgent in :idAgents and ptg.dateDebut >= :fromDate and ptg.dateDebut < :toDate order by ptg.idPointage desc"),
	@NamedQuery(name = "getListPointageByAgentsTypeAndDate", query = "select ptg from Pointage ptg LEFT JOIN FETCH ptg.motif LEFT JOIN FETCH ptg.commentaire LEFT JOIN FETCH ptg.refPrime JOIN FETCH ptg.type where ptg.idAgent in :idAgents and ptg.dateDebut >= :fromDate and ptg.dateDebut < :toDate and ptg.type.idRefTypePointage = :idRefTypePointage order by ptg.idPointage desc")
})
public class Pointage {

	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;

	@OneToOne(optional = false)
	@JoinColumn(name = "ID_TYPE_POINTAGE")
	private RefTypePointage type;

	@OneToMany(mappedBy = "pointage", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("idEtatPointage desc")
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
	private RefPrime refPrime;

	@Column(name = "IS_HSUP_RECUPEREE")
	@Type(type = "boolean")
	private Boolean heureSupRecuperee;

	@Column(name = "IS_ABS_CONCERTEE")
	@Type(type = "boolean")
	private Boolean absenceConcertee;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(name = "ID_COMMENT_MOTIF")
	private PtgComment motif;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
	@JoinColumn(name = "ID_COMMENT_COMMENTAIRE")
	private PtgComment commentaire;

	@ManyToMany
	@JoinTable(
			name = "PTG_POINTAGE_VENTIL_DATE",
			joinColumns = @JoinColumn(name = "ID_POINTAGE"), 
			inverseJoinColumns = @JoinColumn(name = "ID_VENTIL_DATE"))
	@OrderBy("idVentilDate desc")
	private List<VentilDate> ventilations = new ArrayList<VentilDate>();
	
	@Transient
	public RefTypePointageEnum getTypePointageEnum() {
		return RefTypePointageEnum.getRefTypePointageEnum(type.getIdRefTypePointage());
	}

	@Transient
	public EtatPointage getLatestEtatPointage() {
		return etats.iterator().next();
	}
	
	@Transient
	public VentilDate getLatestVentilDate() {
		if (ventilations.size() ==0)
			return null;
		return ventilations.iterator().next();
	}
}
