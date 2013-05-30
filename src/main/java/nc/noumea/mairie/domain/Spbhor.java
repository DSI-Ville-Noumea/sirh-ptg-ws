package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", schema = "MAIRIE", table = "SPBHOR", versionField = "")
public class Spbhor {

	@Id
	@Column(name = "CDTHOR", columnDefinition = "decimal")
	private Integer cdthor;
	
	@Column(name = "CDTAUX", columnDefinition = "decimal")
	private Double taux;
}
