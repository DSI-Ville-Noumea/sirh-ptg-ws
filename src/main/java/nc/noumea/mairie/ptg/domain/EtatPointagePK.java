package nc.noumea.mairie.ptg.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Embeddable
public class EtatPointagePK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6230713751220384509L;

	@Column(name = "ID_POINTAGE")
	private Integer idPointage;

    @Column(name = "DATE_ETAT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEtat;

	public Integer getIdPointage() {
		return idPointage;
	}

	public void setIdPointage(Integer idPointage) {
		this.idPointage = idPointage;
	}

	public Date getDateEtat() {
		return dateEtat;
	}

	public void setDateEtat(Date dateEtat) {
		this.dateEtat = dateEtat;
	}
}
