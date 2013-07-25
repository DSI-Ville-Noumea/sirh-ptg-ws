package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPPPRM", versionField = "")
public class Sppprm {

	@EmbeddedId
	private SppprmId id;
	
	@Column(name = "NBPRIM", columnDefinition = "numeric")
	private double nbPrime;
}
