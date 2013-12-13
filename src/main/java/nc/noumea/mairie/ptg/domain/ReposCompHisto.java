package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_RC_HISTO")
public class ReposCompHisto {

	@Id
	@Column(name = "ID_RC_HISTO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idRcHisto;
	
	@NotNull
	@Column(name = "ID_AGENT")
	private Integer idAgent;
	
	@NotNull
	@Column(name = "DATE_LUNDI")
	private Date dateLundi;
	
	@Column(name = "M_SUP")
	private Integer mSup;

	@Column(name = "M_BASE_HORAIRE")
	private Integer mBaseHoraire;
}
