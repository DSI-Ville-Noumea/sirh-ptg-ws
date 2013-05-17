package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.Lob;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_COMMENT", identifierField = "idPtgComment", identifierType = Integer.class, table = "PTG_COMMENT", sequenceName = "PTG_S_COMMENT")
public class PtgComment {

	@Column(name = "TEXT")
	@Lob
	private String text;
}
