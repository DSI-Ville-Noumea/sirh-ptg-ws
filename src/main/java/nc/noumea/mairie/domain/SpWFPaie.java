package nc.noumea.mairie.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "SPWFPAIE")
public class SpWFPaie {
	
	@Id
	@Column(name = "CDCHAINE", columnDefinition = "char")
	@Enumerated(EnumType.STRING)
	private TypeChainePaieEnum codeChaine;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "CDETAT")
	private SpWFEtat etat;

	@Column(name = "DATMAJ")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateMaj;
	
	@Column(name = "PERPAIE", columnDefinition = "numeric")
	private Integer periodePaie;

	public TypeChainePaieEnum getCodeChaine() {
		return codeChaine;
	}

	public void setCodeChaine(TypeChainePaieEnum codeChaine) {
		this.codeChaine = codeChaine;
	}

	public SpWFEtat getEtat() {
		return etat;
	}

	public void setEtat(SpWFEtat etat) {
		this.etat = etat;
	}

	public Date getDateMaj() {
		return dateMaj;
	}

	public void setDateMaj(Date dateMaj) {
		this.dateMaj = dateMaj;
	}

	public Integer getPeriodePaie() {
		return periodePaie;
	}

	public void setPeriodePaie(Integer periodePaie) {
		this.periodePaie = periodePaie;
	}
	
	
}
