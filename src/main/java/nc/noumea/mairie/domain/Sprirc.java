package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.NamedQuery;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", schema = "MAIRIE", table = "SPRIRC", versionField = "")
@NamedQuery(
		name = "getSprircForAgentAndPeriod", 
		query = "from Sprirc sp where sp.id.nomatr = :nomatr and sp.cdvali = 'V' and (sp.id.datdeb <= :start and sp.datfin >= :start or sp.id.datdeb >= :start and sp.id.datdeb <= :end)")
public class Sprirc {

	@EmbeddedId
	private SprircId id;
	
	@Column(name = "CDVALI", columnDefinition = "char")
	private String cdvali;
	
	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer datfin;
	
	@Column(name = "CODEM2", columnDefinition = "numeric")
	private Integer codem2;
	
	@Column(name = "NBRCP", columnDefinition = "decimal")
	private Double nbRcp;
	
	@Column(name = "DATREP", columnDefinition = "numeric")
	private Integer datRep;
	
	@Column(name = "CODEMA", columnDefinition = "numeric")
	private Integer codema;
}
