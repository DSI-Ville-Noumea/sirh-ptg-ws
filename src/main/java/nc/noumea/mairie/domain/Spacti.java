package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPACTI", versionField = "")
public class Spacti {

	@Id
	@Column(name = "CDACT3", insertable = false, updatable = false, columnDefinition = "char")
	private Integer codeActvite;
	
	@Column(name = "LIACTI", columnDefinition = "char")
	private String libelleActivite;
}
