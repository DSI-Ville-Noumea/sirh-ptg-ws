package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SprircId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1664614049474696555L;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public SprircId() {
	}

	public SprircId(Integer nomatr, Integer datdeb, Integer codem1) {
		this.nomatr = nomatr;
		this.datdeb = datdeb;
		this.codem1 = codem1;
	}

	@Column(name = "NOMATR", insertable = false, updatable = false, columnDefinition = "numeric")
	private Integer nomatr;

	@Column(name = "DATDEB", insertable = false, updatable = false, columnDefinition = "numeric")
	private Integer datdeb;

	@Column(name = "CODEM1", insertable = false, updatable = false, columnDefinition = "numeric")
	private Integer codem1;

	public Integer getNomatr() {
		return nomatr;
	}

	public void setNomatr(Integer nomatr) {
		this.nomatr = nomatr;
	}

	public Integer getDatdeb() {
		return datdeb;
	}

	public void setDatdeb(Integer datdeb) {
		this.datdeb = datdeb;
	}

	public Integer getCodem1() {
		return codem1;
	}

	public void setCodem1(Integer codem1) {
		this.codem1 = codem1;
	}
	
	

}
