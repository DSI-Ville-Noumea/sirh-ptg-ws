package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPRUBR", versionField = "")
public class Sprubr {

	@Id
	@Column(name = "NORUBR", columnDefinition = "numeric")
	private Integer noRubr;

	@NotNull
	@Column(name = "LIRUBR", columnDefinition = "char")
	private String liRubr;
}
