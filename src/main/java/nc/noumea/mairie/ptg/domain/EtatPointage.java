package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_ETAT_POINTAGE", sequenceName = "")
public class EtatPointage {

	@EmbeddedId
	private EtatPointagePK etatPointagePk;
	
	@Column(name = "ETAT")
	private char Etat;
	
	@ManyToOne()
	@MapsId("idPointage")
	@JoinColumn(name = "ID_POINTAGE", referencedColumnName = "ID_POINTAGE")
	private Pointage pointage;
}
