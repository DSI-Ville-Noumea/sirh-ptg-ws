package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;

@RooJavaBean
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_VENTIL_DATE", identifierField = "idVentilDate", identifierType = Integer.class, table = "PTG_VENTIL_DATE", sequenceName = "PTG_S_VENTIL_DATE")
public class VentilDate {

	@Column(name = "DATE_VENTIL")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateVentilation;

	@Column(name = "TYPE_CHAINE_PAIE")
	@Enumerated(EnumType.STRING)
	private TypeChainePaieEnum typeChainePaie;

	@Column(name = "IS_PAYE", nullable = false)
	@Type(type = "boolean")
	private boolean paye;
}
