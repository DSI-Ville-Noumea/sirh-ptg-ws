package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPPRIM", versionField = "")
public class Spprim {

	@EmbeddedId
	private SpprimId id;
	
	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer dateFin;
	
	@Column(name = "MTPRI", columnDefinition = "decimal")
	private double montantPrime;
	
	@Column(name = "REFARR", columnDefinition = "numeric default 0")
	private Integer refArr = 0;
	
	@Column(name = "DATARR", columnDefinition = "numeric default 0")
	private Integer datArr = 0;
}
