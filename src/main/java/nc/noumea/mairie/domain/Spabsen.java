package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.NamedQuery;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", schema = "MAIRIE", table = "SPABSEN", versionField = "")
@NamedQuery(
		name = "getSpabsenForAgentAndPeriod", 
		query = "from Spabsen sp where sp.id.nomatr = :nomatr and (sp.id.datdeb <= :start and sp.datfin >= :start or sp.id.datdeb <= :end and sp.datfin >= :end)")
public class Spabsen {

	@EmbeddedId
	private SpabsenId id;
	
	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer datfin;
}
