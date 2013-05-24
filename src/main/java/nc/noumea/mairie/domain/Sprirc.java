package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", schema = "MAIRIE", table = "SPRIRC", versionField = "")
public class Sprirc {

	@EmbeddedId
	private SprircId id;
	
	@Column(name = "CDVALI", columnDefinition = "char")
	private String cdvali;
	
	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer datFin;
	
	@Column(name = "CODEM2", columnDefinition = "numeric")
	private Integer codem2;
	
	@Column(name = "NBRCP", columnDefinition = "decimal")
	private Double nbRcp;
	
	@Column(name = "DATREP", columnDefinition = "numeric")
	private Integer datRep;
	
	@Column(name = "CODEMA", columnDefinition = "numeric")
	private Integer codema;
}
