package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SPRUBR")
public class Sprubr {

	@Id
	@Column(name = "NORUBR", columnDefinition = "numeric")
	private Integer noRubr;

	@NotNull
	@Column(name = "LIRUBR", columnDefinition = "char")
	private String liRubr;

	public Integer getNoRubr() {
		return noRubr;
	}

	public void setNoRubr(Integer noRubr) {
		this.noRubr = noRubr;
	}

	public String getLiRubr() {
		return liRubr;
	}

	public void setLiRubr(String liRubr) {
		this.liRubr = liRubr;
	}
	
	
}
