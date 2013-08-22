package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
@Embeddable
public class SpWFPaieId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8784365023540410335L;
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	@OneToOne(optional = false)
	@JoinColumn(name = "CDETAT")
	private SpWFEtat etat;
	
	@Column(name = "CDCHAINE", columnDefinition = "char")
	@Enumerated(EnumType.STRING)
	private TypeChainePaieEnum codeChaine;
	
}
