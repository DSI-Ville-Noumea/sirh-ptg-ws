package nc.noumea.mairie.domain;

import javax.persistence.EmbeddedId;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", schema = "MAIRIE", table = "SPCONG", versionField = "")
public class Spcong {

	@EmbeddedId
	private SpcongId id;
	
}
