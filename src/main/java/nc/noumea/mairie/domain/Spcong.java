package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.NamedQuery;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPCONG", versionField = "")
@NamedQuery(
		name = "getSpcongForAgentAndPeriod", 
		query = "from Spcong sp where sp.id.nomatr = :nomatr and sp.cdvali = 'V' and (sp.id.datdeb <= :start and sp.datfin >= :start or sp.id.datdeb >= :start and sp.id.datdeb <= :end)")
public class Spcong {

	@EmbeddedId
	private SpcongId id;
	
	@Column(name = "CDVALI", columnDefinition = "char")
	private String cdvali;
	
	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer datfin;
	
	@Column(name = "CODEM1", columnDefinition = "numeric")
	private Integer codem1;
	
	@Column(name = "CODEM2", columnDefinition = "numeric")
	private Integer codem2;
}
