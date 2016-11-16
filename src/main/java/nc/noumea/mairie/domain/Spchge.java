package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "SPCHGE")
public class Spchge {

	@EmbeddedId
	private SpchgeId	id;

	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer		dateFin;

	@Column(name = "CDCREA", columnDefinition = "numeric default 0")
	private Integer		codeCreancier = 0;

	@Column(name = "NOMATE", columnDefinition = "char")
	private String		matriculeChargeEmploye ="";

	@Column(name = "CDCHAR", columnDefinition = "numeric default 0")
	private Integer		codeCharge= 0;

	@Column(name = "TXSAL", columnDefinition = "decimal")
	private Double		tauxPartSalariale= 0.0;

	@Column(name = "MTTREG", columnDefinition = "decimal")
	private Double		montantTotalReglement= 0.0;

	@Column(name = "NOORDR", columnDefinition = "numeric default 0")
	private Integer		numeroOrdre	= 0;

	public SpchgeId getId() {
		return id;
	}

	public void setId(SpchgeId id) {
		this.id = id;
	}

	public Integer getDateFin() {
		return dateFin;
	}

	public void setDateFin(Integer dateFin) {
		this.dateFin = dateFin;
	}

	public Integer getCodeCreancier() {
		return codeCreancier;
	}

	public void setCodeCreancier(Integer codeCreancier) {
		this.codeCreancier = codeCreancier;
	}

	public String getMatriculeChargeEmploye() {
		return matriculeChargeEmploye;
	}

	public void setMatriculeChargeEmploye(String matriculeChargeEmploye) {
		this.matriculeChargeEmploye = matriculeChargeEmploye;
	}

	public Integer getCodeCharge() {
		return codeCharge;
	}

	public void setCodeCharge(Integer codeCharge) {
		this.codeCharge = codeCharge;
	}

	public Double getTauxPartSalariale() {
		return tauxPartSalariale;
	}

	public void setTauxPartSalariale(Double tauxPartSalariale) {
		this.tauxPartSalariale = tauxPartSalariale;
	}

	public Double getMontantTotalReglement() {
		return montantTotalReglement;
	}

	public void setMontantTotalReglement(Double montantTotalReglement) {
		this.montantTotalReglement = montantTotalReglement;
	}

	public Integer getNumeroOrdre() {
		return numeroOrdre;
	}

	public void setNumeroOrdre(Integer numeroOrdre) {
		this.numeroOrdre = numeroOrdre;
	}

}
