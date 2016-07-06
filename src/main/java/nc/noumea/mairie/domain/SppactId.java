package nc.noumea.mairie.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class SppactId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2981861500000391579L;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	public SppactId() {
		
	}
	
	public SppactId(Integer nomatr, Integer dateJour, Spacti activite) {
		this.nomatr = nomatr;
		this.dateJour = dateJour;
		this.activite = activite;
	}
	
	@Column(name = "NOMATR", columnDefinition = "numeric")
	private Integer nomatr;
	
	@Column(name = "DATJOU", columnDefinition = "numeric")
	private Integer dateJour;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CDACT3", referencedColumnName = "CDACT3", columnDefinition = "char")
	private Spacti activite;

	public Integer getNomatr() {
		return nomatr;
	}

	public void setNomatr(Integer nomatr) {
		this.nomatr = nomatr;
	}

	public Integer getDateJour() {
		return dateJour;
	}

	public void setDateJour(Integer dateJour) {
		this.dateJour = dateJour;
	}

	public Spacti getActivite() {
		return activite;
	}

	public void setActivite(Spacti activite) {
		this.activite = activite;
	}
	
	
}
