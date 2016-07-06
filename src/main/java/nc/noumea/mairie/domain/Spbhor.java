package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SPBHOR")
public class Spbhor {

	@Id
	@Column(name = "CDTHOR", columnDefinition = "decimal")
	private Integer cdthor;
	
	@Column(name = "CDTAUX", columnDefinition = "decimal")
	private Double taux;

	public Integer getCdthor() {
		return cdthor;
	}

	public void setCdthor(Integer cdthor) {
		this.cdthor = cdthor;
	}

	public Double getTaux() {
		return taux;
	}

	public void setTaux(Double taux) {
		this.taux = taux;
	}
	
	
}
