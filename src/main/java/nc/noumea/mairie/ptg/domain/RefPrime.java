package nc.noumea.mairie.ptg.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import nc.noumea.mairie.domain.AgentStatutEnum;

@Entity
@Table(name = "PTG_REF_PRIME")
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
	
	@Version
    @Column(name = "version")
	private Integer version;

	public Integer getIdRefPrime() {
		return idRefPrime;
	}

	public void setIdRefPrime(Integer idRefPrime) {
		this.idRefPrime = idRefPrime;
	}

	public Integer getNoRubr() {
		return noRubr;
	}

	public void setNoRubr(Integer noRubr) {
		this.noRubr = noRubr;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public TypeSaisieEnum getTypeSaisie() {
		return typeSaisie;
	}

	public void setTypeSaisie(TypeSaisieEnum typeSaisie) {
		this.typeSaisie = typeSaisie;
	}

	public boolean isCalculee() {
		return calculee;
	}

	public void setCalculee(boolean calculee) {
		this.calculee = calculee;
	}

	public AgentStatutEnum getStatut() {
		return statut;
	}

	public void setStatut(AgentStatutEnum statut) {
		this.statut = statut;
	}

	public MairiePrimeTableEnum getMairiePrimeTableEnum() {
		return mairiePrimeTableEnum;
	}

	public void setMairiePrimeTableEnum(MairiePrimeTableEnum mairiePrimeTableEnum) {
		this.mairiePrimeTableEnum = mairiePrimeTableEnum;
	}

	public String getAide() {
		return aide;
	}

	public void setAide(String aide) {
		this.aide = aide;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	
}
