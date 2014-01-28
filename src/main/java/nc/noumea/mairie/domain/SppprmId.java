package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SppprmId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2496736808139629983L;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	public SppprmId() {
		
	}
	
	public SppprmId(Integer nomatr, Integer datJour, Integer noRubr) {
		this.nomatr = nomatr;
		this.datJour = datJour;
		this.noRubr = noRubr;
	}
	
	@Column(name = "NOMATR", columnDefinition = "numeric")
	private Integer nomatr;

	@Column(name = "DATJOU", columnDefinition = "numeric")
	private Integer datJour;
	
	@Column(name = "NORUBR", columnDefinition = "numeric")
	private Integer noRubr;

	public Integer getNomatr() {
		return nomatr;
	}

	public void setNomatr(Integer nomatr) {
		this.nomatr = nomatr;
	}

	public Integer getDatJour() {
		return datJour;
	}

	public void setDatJour(Integer datJour) {
		this.datJour = datJour;
	}

	public Integer getNoRubr() {
		return noRubr;
	}

	public void setNoRubr(Integer noRubr) {
		this.noRubr = noRubr;
	}
	
	
}
