package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_VENTIL_DATE", identifierField = "idVentilDate", identifierType = Integer.class, table = "PTG_VENTIL_DATE", sequenceName = "PTG_S_VENTIL_DATE")
public class VentilDate {

	@Column(name = "DATE_VENTIL")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateVentilation;

	@Column(name = "TYPE_CHAINE_PAIE")
	@Enumerated(EnumType.STRING)
	private TypeChainePaieEnum typeChainePaie;

	@Column(name = "IS_PAYE", nullable = false)
	@Type(type = "boolean")
	private boolean paye;
	
	@OneToMany(mappedBy = "ventilDate", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<VentilAbsence> ventilAsbences;
	
	@OneToMany(mappedBy = "ventilDate", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<VentilHsup> ventilHsups;
	
	@OneToMany(mappedBy = "ventilDate", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<VentilPrime> ventilPrimes;
}
