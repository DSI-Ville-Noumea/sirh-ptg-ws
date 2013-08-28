package nc.noumea.mairie.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "sirhPersistenceUnit", table = "SPWFPAIE", versionField = "")
public class SpWFPaie {
	
	@Id
	@Column(name = "CDCHAINE", columnDefinition = "char")
	@Enumerated(EnumType.STRING)
	private TypeChainePaieEnum codeChaine;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "CDETAT")
	private SpWFEtat etat;

	@Column(name = "DATMAJ")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateMaj;
	
	@Column(name = "PERPAIE", columnDefinition = "numeric")
	private Integer periodePaie;
}
