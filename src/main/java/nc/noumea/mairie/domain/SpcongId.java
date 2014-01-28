package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SpcongId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1026029305053411438L;

	public SpcongId() {
	}

	public SpcongId(Integer nomatr, Integer datdeb, Integer type2, Integer rang) {
		this.nomatr = nomatr;
		this.datdeb = datdeb;
		this.type2 = type2;
		this.rang = rang;
	}

	@Column(name = "NOMATR", insertable = false, updatable = false, columnDefinition = "numeric")
	private Integer nomatr;

	@Column(name = "DATDEB", insertable = false, updatable = false, columnDefinition = "numeric")
	private Integer datdeb;

	@Column(name = "TYPE2", insertable = false, updatable = false, columnDefinition = "numeric")
	private Integer type2;
	
	@Column(name = "RANG", insertable = false, updatable = false, columnDefinition = "numeric")
	private Integer rang;

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

	public Integer getType2() {
		return type2;
	}

	public void setType2(Integer type2) {
		this.type2 = type2;
	}

	public Integer getRang() {
		return rang;
	}

	public void setRang(Integer rang) {
		this.rang = rang;
	}
	
	
}
