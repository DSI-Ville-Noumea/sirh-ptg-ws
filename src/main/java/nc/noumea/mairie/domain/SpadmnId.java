package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@Embeddable
public class SpadmnId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6202523152549456648L;

	@Column(name = "NOMATR", columnDefinition = "numeric")
	private Integer nomatr;
	
	@Column(name = "DATDEB", columnDefinition = "numeric")
	private Integer datdeb;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
