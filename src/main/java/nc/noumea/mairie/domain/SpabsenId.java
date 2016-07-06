package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SpabsenId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8543037110547148118L;
	
	public SpabsenId() {
	}

	public SpabsenId(Integer nomatr, Integer datdeb, String type3) {
		this.nomatr = nomatr;
		this.datdeb = datdeb;
		this.type3 = type3;
	}

	@Column(name = "NOMATR", insertable = false, updatable = false, columnDefinition = "numeric")
	private Integer nomatr;
	
	@Column(name = "DATDEB", insertable = false, updatable = false, columnDefinition = "numeric")
	private Integer datdeb;
	
	@Column(name = "TYPE3", insertable = false, updatable = false, columnDefinition = "char")
	private String type3;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

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

	public String getType3() {
		return type3;
	}

	public void setType3(String type3) {
		this.type3 = type3;
	}
	
	
}
