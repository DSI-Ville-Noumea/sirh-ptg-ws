package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", schema = "MAIRIE", table = "SPBAREM", versionField = "")
public class Spbarem {

	@Id
	@Column(name = "IBAN", columnDefinition = "char")
	private String iban;
	
	@Column(name = "INA", columnDefinition = "numeric")
	private Integer ina;
}
