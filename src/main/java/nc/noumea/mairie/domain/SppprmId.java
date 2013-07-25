package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
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
	
	@Column(name = "NOMATR", columnDefinition = "numeric")
	private Integer nomatr;

	@Column(name = "DATJOU", columnDefinition = "numeric")
	private Integer datJour;
	
	@Column(name = "NORUBR", columnDefinition = "numeric")
	private Integer noRubr;
}
