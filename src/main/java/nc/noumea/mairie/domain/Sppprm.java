package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "SPPPRM")
public class Sppprm {

	@EmbeddedId
	private SppprmId id;
	
	@Column(name = "NBPRIM", columnDefinition = "numeric")
	private double nbPrime;

	public SppprmId getId() {
		return id;
	}

	public void setId(SppprmId id) {
		this.id = id;
	}

	public double getNbPrime() {
		return nbPrime;
	}

	public void setNbPrime(double nbPrime) {
		this.nbPrime = nbPrime;
	}
	
	
}
