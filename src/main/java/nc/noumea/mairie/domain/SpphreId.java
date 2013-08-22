package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@Embeddable
public class SpphreId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6532385527445432473L;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	public SpphreId() {
		
	}
	
	public SpphreId(Integer nomatr, Integer datJour) {
		this.setNomatr(nomatr);
		this.setDatJour(datJour);
	}
	
	@Column(name = "NOMATR", columnDefinition = "numeric")
	private Integer nomatr;

	@Column(name = "DATJOU", columnDefinition = "numeric")
	private Integer datJour;
	
}
