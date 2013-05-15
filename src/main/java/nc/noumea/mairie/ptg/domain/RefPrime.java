package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_REF_PRIME")
@NamedQuery(name = "getRefPrimes", query = "from RefPrime rf where rf.noRubr in (:noRubrList)")
public class RefPrime {

	@Id
	@Column(name = "NORUBR")
	@GeneratedValue(strategy = GenerationType.IDENTITY) // hack to avoid using any ID generator this this entity
	private Integer noRubr;
	
	@Column(name = "LIBELLE")
	private String libelle;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "TYPE_SAISIE", nullable = true)
    @Enumerated(EnumType.ORDINAL)
	private TypeSaisieEnum typeSaisie;
	
	@Column(name = "IS_CALCULEE")
	private boolean calculee;
}
