package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_REF_TYPE_POINTAGE", versionField = "")
public class RefTypePointage {

	@Id
	@Column(name = "ID_REF_TYPE_POINTAGE")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idRefTypePointage;

	@Column(name = "LABEL", columnDefinition = "NVARCHAR2")
	private String label;
}
