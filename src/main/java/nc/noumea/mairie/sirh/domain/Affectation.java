package nc.noumea.mairie.sirh.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", identifierType = Integer.class, identifierColumn = "ID_AFFECTATION", identifierField = "idAffectation", schema = "SIRH", table = "AFFECTATION", versionField = "")
@NamedQuery(name = "getCurrentAffectation", query = "select a.fichePoste from Affectation a where a.agent.idAgent = :idAgent and a.dateDebutAff <= :today and (a.dateFinAff = '01/01/0001' or a.dateFinAff is null or a.dateFinAff >= :today)")
public class Affectation {

	@NotNull
	@OneToOne
	@JoinColumn(name = "ID_AGENT", referencedColumnName = "ID_AGENT")
	private Agent agent;

	@NotNull
	@OneToOne
	@JoinColumn(name = "ID_FICHE_POSTE", referencedColumnName = "ID_FICHE_POSTE")
	private FichePoste fichePoste;

	@NotNull
	@Column(name = "DATE_DEBUT_AFF")
	@Temporal(TemporalType.DATE)
	private Date dateDebutAff;

	@Column(name = "DATE_FIN_AFF")
	@Temporal(TemporalType.DATE)
	private Date dateFinAff;

	@Column(name = "TEMPS_TRAVAIL")
	private String tempsTravail;

	@OneToOne(optional = true)
	@JoinColumn(name = "ID_FICHE_POSTE_SECONDAIRE", referencedColumnName = "ID_FICHE_POSTE")
	private FichePoste fichePosteSecondaire;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "SIRH", name = "PRIME_POINTAGE_AFF", joinColumns = { @javax.persistence.JoinColumn(name = "ID_AFFECTATION") }, inverseJoinColumns = @javax.persistence.JoinColumn(name = "ID_PRIME_POINTAGE"))
	private Set<PrimePointage> primePointages;
}
