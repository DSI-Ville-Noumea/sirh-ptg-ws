package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPACTI", versionField = "")
public class Spacti {

	public static String CODE_ACTIVITE_ABS_NON_CONCERTEE = "A01";
	public static String CODE_ACTIVITE_ABS_CONCERTEE = "A02";

	@Id
	@Column(name = "CDACT3", insertable = false, updatable = false, columnDefinition = "char")
	private String codeActvite;
	
	@Column(name = "LIACTI", columnDefinition = "char")
	private String libelleActivite;
}
