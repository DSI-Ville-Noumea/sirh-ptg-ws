package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import nc.noumea.mairie.domain.AgentStatutEnum;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_REF_PRIME", sequenceName = "PTG_S_REF_PRIME", identifierColumn = "ID_REF_PRIME", identifierField = "idRefPrime", identifierType = Integer.class, versionField = "")
@NamedQueries({
	@NamedQuery(name = "getRefPrimesNotCalculated", query = "from RefPrime rf where rf.noRubr in (:noRubrList) and rf.statut = :statut and rf.calculee = false"),
	@NamedQuery(name = "getListPrimesByIdDesc", query = "select ptg from RefPrime ptg where ptg.statut = :statut order by ptg.noRubr desc")
})

public class RefPrime {

	@NotNull
	@Column(name = "NORUBR")
	private Integer noRubr;
	
	@Column(name = "LIBELLE", columnDefinition = "nvarchar2")
	private String libelle;
	
	@Column(name = "DESCRIPTION", columnDefinition = "nvarchar2")
	private String description;
	
	@Column(name = "TYPE_SAISIE", nullable = true)
    @Enumerated(EnumType.ORDINAL)
	private TypeSaisieEnum typeSaisie;
	
	@Column(name = "IS_CALCULEE")
	private boolean calculee;
	
	@Column(name = "STATUT")
	@Enumerated(EnumType.STRING)
	private AgentStatutEnum statut;
	
}
