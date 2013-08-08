package nc.noumea.mairie.ptg.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class EtatPointagePK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6230713751220384509L;

	@ManyToOne()
	@JoinColumn(name = "ID_POINTAGE", referencedColumnName = "ID_POINTAGE")
	private Pointage pointage;
	
    @Column(name = "DATE_ETAT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEtat;

	public Date getDateEtat() {
		return dateEtat;
	}

	public Pointage getPointage() {
		return pointage;
	}

	public void setPointage(Pointage pointage) {
		this.pointage = pointage;
	}

	public void setDateEtat(Date dateEtat) {
		this.dateEtat = dateEtat;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
