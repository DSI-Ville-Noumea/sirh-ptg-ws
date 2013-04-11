package nc.noumea.mairie.ptg.domain;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;


@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_POINTAGE", identifierField = "idPointage", identifierType = Integer.class, table = "PTG_POINTAGE", sequenceName = "PTG_S_POINTAGE")
public class Pointage {

	@OneToOne(optional = false)
	@JoinColumn(name = "ID_TYPE_POINTAGE")
	private TypePointage type;
	
	@OneToMany(mappedBy = "pointage", fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<EtatPointage> etats;
	
	@Column(name="QUANTITE")
	private Integer quantite;
}
