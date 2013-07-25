package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPPACT", versionField = "")
public class Sppact {

	@EmbeddedId
	private SppactId id;
	
	@Column(name = "NBHEUR", columnDefinition = "numeric")
	private double nbHeures;
	
	@Column(name = "SERVI", columnDefinition = "char default '    '")
	private String service;
}
