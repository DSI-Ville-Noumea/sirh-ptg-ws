package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "SPPACT")
public class Sppact {

	@EmbeddedId
	private SppactId id;
	
	@Column(name = "NBHEUR", columnDefinition = "numeric")
	private double nbHeures;
	
	@Column(name = "SERVI", columnDefinition = "char default '    '")
	private String service = "    ";

	public SppactId getId() {
		return id;
	}

	public void setId(SppactId id) {
		this.id = id;
	}

	public double getNbHeures() {
		return nbHeures;
	}

	public void setNbHeures(double nbHeures) {
		this.nbHeures = nbHeures;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
	
	
}
