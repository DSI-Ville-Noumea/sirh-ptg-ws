package nc.noumea.mairie.sirh.domain;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", identifierType = Integer.class, identifierColumn = "ID_TYPE_JOUR_FERIE", identifierField = "idTypeJourFerie", table = "R_TYPE_JOUR_FERIE", versionField = "")
public class TypeJourFerie {

	@NotNull
	@Column(name = "LIB_TYPE_JOUR_FERIE")
	private String libelle;
}
