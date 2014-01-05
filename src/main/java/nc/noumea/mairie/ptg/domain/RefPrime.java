package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

import nc.noumea.mairie.domain.AgentStatutEnum;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(persistenceUnit = "ptgPersistenceUnit", table = "PTG_REF_PRIME")
@NamedQueries({
		@NamedQuery(name = "getRefPrimesNotCalculated", query = "from RefPrime rf where rf.noRubr in (:noRubrList) and rf.statut = :statut and rf.calculee = false order by rf.noRubr"),
		@NamedQuery(name = "getRefPrimesCalculated", query = "from RefPrime rf where rf.noRubr in (:noRubrList) and rf.statut = :statut and rf.calculee = true order by rf.noRubr"),
		@NamedQuery(name = "getListPrimesWithStatusByIdDesc", query = "select ptg from RefPrime ptg where ptg.statut = :statut order by ptg.noRubr"),
		@NamedQuery(name = "getRefPrimesByNorubr", query = "select ptg from RefPrime ptg where ptg.noRubr=:noRubr"),
		@NamedQuery(name = "getListPrimesByIdDesc", query = "select ptg from RefPrime ptg order by ptg.noRubr")
})
public class RefPrime {

	@Id 
	@Column(name = "ID_REF_PRIME")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idRefPrime;
	
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
	
	@Column(name = "MAIRIE_PRIME")
	@Enumerated(EnumType.STRING)
	private MairiePrimeTableEnum mairiePrimeTableEnum;

	@Column(name = "AIDE", columnDefinition = "nvarchar2")
	private String aide;

}
