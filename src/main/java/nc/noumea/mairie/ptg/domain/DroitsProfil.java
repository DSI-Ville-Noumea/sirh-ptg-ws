package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;

import org.hibernate.annotations.Type;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", identifierColumn = "ID_DROITS_PROFIL", identifierField = "idDroitsProfil", identifierType = Integer.class, table = "PTG_DROITS_PROFIL", sequenceName = "PTG_S_DROITS_PROFIL")
public class DroitsProfil {

	@Column(name = "LABEL", nullable = false)
	private String label;
	
	@Column(name = "IS_EDITION", nullable = false)
    @Type(type="boolean")
    private boolean isEdition;
	
	@Column(name = "IS_SAISIE", nullable = false)
    @Type(type="boolean")
    private boolean isSaisie;
	
	@Column(name = "IS_VISUALISATION", nullable = false)
    @Type(type="boolean")
    private boolean isVisualisation;
	
	@Column(name = "IS_APPROBATION", nullable = false)
    @Type(type="boolean")
    private boolean isApprobation;
	
	@Column(name = "IS_GRANTOR", nullable = false)
    @Type(type="boolean")
    private boolean isGrantor;
}
