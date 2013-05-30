package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", schema = "MAIRIE", table = "SPADMN", versionField = "")
@NamedQuery(
		name = "getAgentSpadmnAsOfDate",
		query = "from Spadmn sp where sp.id.nomatr = :nomatr and sp.id.datdeb <= :dateFormatMairie and (sp.datfin >= :dateFormatMairie or sp.datfin = 0)")
public class Spadmn {

	@Id
	private SpadmnId id;

	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer datfin;
	
	@Column(name = "CDPADM", columnDefinition = "char")
	private String cdpadm;
}
