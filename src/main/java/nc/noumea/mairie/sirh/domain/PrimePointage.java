package nc.noumea.mairie.sirh.domain;

import javax.persistence.Column;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", identifierType = Integer.class, identifierColumn = "ID_PRIME_POINTAGE", identifierField = "idPrimePointage", schema = "SIRH", table = "PRIME_POINTAGE", versionField = "")
public class PrimePointage {

	@Column(name = "NUM_RUBRIQUE", columnDefinition = "numeric")
	private Integer numRubrique;

}
