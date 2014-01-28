package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "SPPRIM")
public class Spprim {

	@EmbeddedId
	private SpprimId id;
	
	@Column(name = "DATFIN", columnDefinition = "numeric")
	private Integer dateFin;
	
	@Column(name = "MTPRI", columnDefinition = "decimal")
	private double montantPrime;
	
	@Column(name = "REFARR", columnDefinition = "numeric default 0")
	private Integer refArr = 0;
	
	@Column(name = "DATARR", columnDefinition = "numeric default 0")
	private Integer datArr = 0;

	public SpprimId getId() {
		return id;
	}

	public void setId(SpprimId id) {
		this.id = id;
	}

	public Integer getDateFin() {
		return dateFin;
	}

	public void setDateFin(Integer dateFin) {
		this.dateFin = dateFin;
	}

	public double getMontantPrime() {
		return montantPrime;
	}

	public void setMontantPrime(double montantPrime) {
		this.montantPrime = montantPrime;
	}

	public Integer getRefArr() {
		return refArr;
	}

	public void setRefArr(Integer refArr) {
		this.refArr = refArr;
	}

	public Integer getDatArr() {
		return datArr;
	}

	public void setDatArr(Integer datArr) {
		this.datArr = datArr;
	}
	
	
}
