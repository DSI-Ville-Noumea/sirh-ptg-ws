package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPMATR", versionField = "")
public class Spmatr {

	@Id
	@Column(name = "NOMATR", columnDefinition = "numeric")
	private Integer nomatr;

	@Column(name = "PERPRE", columnDefinition = "numeric default 0")
	private Integer perpre;
	
	@Column(name = "PERRAP", columnDefinition = "numeric")
	private Integer perrap;

	@Column(name = "CDVALI", columnDefinition = "char default ' '")
	private String cdvali;

	@Column(name = "CDCHAI", columnDefinition = "char")
	@Enumerated(EnumType.STRING)
	private TypeChainePaieEnum typeChainePaie;
}
