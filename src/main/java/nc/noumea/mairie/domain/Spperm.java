package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "SPPERM")
@NamedQuery(
		name = "getSppermForTREtatPayeurOnPeriod", 
		query = "select sp from Spperm sp where sp.id.noRubr = 6500 and (sp.id.perdiodeDebut <= :date and (sp.id.periodeFin >= :date or sp.id.periodeFin = 0))")
public class Spperm {

	@EmbeddedId
	private SppermId id;
	
	@Column(name = "TXSAL", columnDefinition = "decimal")
	private Float tauxSalarial;
	
	@Column(name = "TXPAT", columnDefinition = "decimal")
	private Float tauxPatronal;
	
	@Column(name = "MTFORF", columnDefinition = "decimal")
	private Float montantForfait;
	
	@Column(name = "MTPLAF", columnDefinition = "decimal")
	private Float montantPlafond;

	public Float getTauxSalarial() {
		return tauxSalarial;
	}

	public void setTauxSalarial(Float tauxSalarial) {
		this.tauxSalarial = tauxSalarial;
	}

	public Float getTauxPatronal() {
		return tauxPatronal;
	}

	public void setTauxPatronal(Float tauxPatronal) {
		this.tauxPatronal = tauxPatronal;
	}

	public Float getMontantForfait() {
		return montantForfait;
	}

	public void setMontantForfait(Float montantForfait) {
		this.montantForfait = montantForfait;
	}

	public Float getMontantPlafond() {
		return montantPlafond;
	}

	public void setMontantPlafond(Float montantPlafond) {
		this.montantPlafond = montantPlafond;
	}
}
