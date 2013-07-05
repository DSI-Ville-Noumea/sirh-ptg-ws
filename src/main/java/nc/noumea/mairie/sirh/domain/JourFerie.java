package nc.noumea.mairie.sirh.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
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
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", identifierType = Integer.class, identifierColumn = "ID_JOUR_FERIE", identifierField = "idJourFerie", table = "P_JOUR_FERIE", versionField = "")
@NamedQuery(name = "isJourHoliday", query = "select 1 from JourFerie where dateJour = :date")
public class JourFerie {

	@NotNull
	@Column(name = "DATE_JOUR")
	@Temporal(TemporalType.DATE)
	private Date dateJour;

	@Column(name = "DESCRIPTION")
	private String description;

	@NotNull
	@OneToOne
	@JoinColumn(name = "ID_TYPE_JOUR_FERIE", referencedColumnName = "ID_TYPE_JOUR_FERIE")
	private TypeJourFerie typeJour;
}
