package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;

public class SppermId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public SppermId() {}

	@Column(name = "NORUBR", columnDefinition = "numeric")
	private Integer noRubr;
	
	@Column(name = "PERDEB", columnDefinition = "numeric")
	private Integer perdiodeDebut;
	
	@Column(name = "PERFIN", columnDefinition = "numeric")
	private Integer periodeFin;

	public Integer getNoRubr() {
		return noRubr;
	}

	public void setNoRubr(Integer noRubr) {
		this.noRubr = noRubr;
	}

	public Integer getPerdiodeDebut() {
		return perdiodeDebut;
	}

	public void setPerdiodeDebut(Integer perdiodeDebut) {
		this.perdiodeDebut = perdiodeDebut;
	}

	public Integer getPeriodeFin() {
		return periodeFin;
	}

	public void setPeriodeFin(Integer periodeFin) {
		this.periodeFin = periodeFin;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
