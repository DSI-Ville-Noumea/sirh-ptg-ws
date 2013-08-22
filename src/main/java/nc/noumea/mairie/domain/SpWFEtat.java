package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPWFETAT", versionField = "")
public class SpWFEtat {

	@Id
	@NotNull
	@Column(name = "CDETAT", columnDefinition = "numeric")
	private Integer codeEtat;

	@NotNull
	@Column(name = "LIBETAT", columnDefinition = "char")
	private String libelleEtat;
}
