package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SpprimId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7131813811489261748L;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	public SpprimId() {
		
	}
	
	public SpprimId(Integer nomatr, Integer dateDebut, Integer norubr) {
		this.nomatr = nomatr;
		this.dateDebut = dateDebut;
		this.noRubr = norubr;
	}
	
	@Column(name = "NOMATR", columnDefinition = "numeric")
	private Integer nomatr;

	@Column(name = "DATDEB", columnDefinition = "numeric")
	private Integer dateDebut;
	
	@Column(name = "NORUBR", columnDefinition = "numeric")
	private Integer noRubr;

	public Integer getNomatr() {
		return nomatr;
	}

	public void setNomatr(Integer nomatr) {
		this.nomatr = nomatr;
	}

	public Integer getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Integer dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Integer getNoRubr() {
		return noRubr;
	}

	public void setNoRubr(Integer noRubr) {
		this.noRubr = noRubr;
	}
	
	
}
