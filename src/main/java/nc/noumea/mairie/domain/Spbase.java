package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "SPBASE")
public class Spbase {

	@Id
	@Column(name = "CDBASE", columnDefinition = "char", updatable = false, insertable = false)
	private String cdBase;
	
	@Column(name = "LIBASE", columnDefinition = "char")
	private String liBase;

	@Column(name = "NBASHH", columnDefinition = "numeric")
	private double nbashh;
	
	@Column(name = "CDCBAS", columnDefinition = "numeric")
	private double cdcbas;
	
	@Column(name = "NBAHSA", columnDefinition = "numeric")
	private double nbahsa;
	
	@Column(name = "NBAHDI", columnDefinition = "numeric")
	private double nbahdi;
	
	@Column(name = "NBAHLU", columnDefinition = "numeric")
	private double nbahlu;
	
	@Column(name = "NBAHMA", columnDefinition = "numeric")
	private double nbahma;
	
	@Column(name = "NBAHME", columnDefinition = "numeric")
	private double nbahme;
	
	@Column(name = "NBAHJE", columnDefinition = "numeric")
	private double nbahje;
	
	@Column(name = "NBAHVE", columnDefinition = "numeric")
	private double nbahve;
	
	@Column(name = "NBASCH", columnDefinition = "numeric")
	private double nbasch;
	
	@Transient
	public double getDayBase(int day) {
		
		switch(day) {
			case 0:
				return getNbahlu();
			case 1:
				return getNbahma();
			case 2:
				return getNbahme();
			case 3:
				return getNbahje();
			case 4:
				return getNbahve();
			case 5:
				return getNbahsa();
			case 6:
				return getNbahdi();
				default:
					return 0.0;
		}
	}

	public String getCdBase() {
		return cdBase;
	}

	public void setCdBase(String cdBase) {
		this.cdBase = cdBase;
	}

	public String getLiBase() {
		return liBase;
	}

	public void setLiBase(String liBase) {
		this.liBase = liBase;
	}

	public double getNbashh() {
		return nbashh;
	}

	public void setNbashh(double nbashh) {
		this.nbashh = nbashh;
	}

	public double getCdcbas() {
		return cdcbas;
	}

	public void setCdcbas(double cdcbas) {
		this.cdcbas = cdcbas;
	}

	public double getNbahsa() {
		return nbahsa;
	}

	public void setNbahsa(double nbahsa) {
		this.nbahsa = nbahsa;
	}

	public double getNbahdi() {
		return nbahdi;
	}

	public void setNbahdi(double nbahdi) {
		this.nbahdi = nbahdi;
	}

	public double getNbahlu() {
		return nbahlu;
	}

	public void setNbahlu(double nbahlu) {
		this.nbahlu = nbahlu;
	}

	public double getNbahma() {
		return nbahma;
	}

	public void setNbahma(double nbahma) {
		this.nbahma = nbahma;
	}

	public double getNbahme() {
		return nbahme;
	}

	public void setNbahme(double nbahme) {
		this.nbahme = nbahme;
	}

	public double getNbahje() {
		return nbahje;
	}

	public void setNbahje(double nbahje) {
		this.nbahje = nbahje;
	}

	public double getNbahve() {
		return nbahve;
	}

	public void setNbahve(double nbahve) {
		this.nbahve = nbahve;
	}

	public double getNbasch() {
		return nbasch;
	}

	public void setNbasch(double nbasch) {
		this.nbasch = nbasch;
	}
	
	
}
