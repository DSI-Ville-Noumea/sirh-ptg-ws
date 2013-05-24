package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
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
}
