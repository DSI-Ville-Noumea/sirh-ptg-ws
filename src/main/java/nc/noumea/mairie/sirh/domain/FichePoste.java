package nc.noumea.mairie.sirh.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", identifierType = Integer.class, identifierColumn = "ID_FICHE_POSTE", identifierField = "idFichePoste", schema = "SIRH", table = "FICHE_POSTE", versionField = "")
public class FichePoste {

	@Column(name = "ID_SERVI", columnDefinition = "char")
	private String codeService;
	
	@OneToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_RESPONSABLE", referencedColumnName = "ID_FICHE_POSTE")
	private FichePoste responsable;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "SIRH", name = "PRIME_POINTAGE_FP", joinColumns = { @javax.persistence.JoinColumn(name = "ID_FICHE_POSTE") }, inverseJoinColumns = @javax.persistence.JoinColumn(name = "ID_PRIME_POINTAGE"))
	private Set<PrimePointage> primePointages;
}
