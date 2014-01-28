package nc.noumea.mairie.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "SPPHRE")
public class Spphre {

	@EmbeddedId
	private SpphreId id;

	@Column(name = "CDRECU", columnDefinition = "char")
	@Enumerated(EnumType.STRING)
	private SpphreRecupEnum spphreRecup = SpphreRecupEnum.P;

	@Column(name = "NBH25", columnDefinition = "numeric")
	private double nbh25;

	@Column(name = "NBH50", columnDefinition = "numeric")
	private double nbh50;

	@Column(name = "NBHDIM", columnDefinition = "numeric")
	private double nbhdim;

	@Column(name = "NBHMAI", columnDefinition = "numeric")
	private double nbhmai;

	@Column(name = "NBHNUI", columnDefinition = "numeric")
	private double nbhnuit;

	@Column(name = "NBHSSI", columnDefinition = "numeric")
	private double nbhssimple;

	@Column(name = "NBHSCO", columnDefinition = "numeric")
	private double nbhscomposees;

	@Column(name = "NBHCOM", columnDefinition = "numeric")
	private double nbhcomplementaires;

	public SpphreId getId() {
		return id;
	}

	public void setId(SpphreId id) {
		this.id = id;
	}

	public SpphreRecupEnum getSpphreRecup() {
		return spphreRecup;
	}

	public void setSpphreRecup(SpphreRecupEnum spphreRecup) {
		this.spphreRecup = spphreRecup;
	}

	public double getNbh25() {
		return nbh25;
	}

	public void setNbh25(double nbh25) {
		this.nbh25 = nbh25;
	}

	public double getNbh50() {
		return nbh50;
	}

	public void setNbh50(double nbh50) {
		this.nbh50 = nbh50;
	}

	public double getNbhdim() {
		return nbhdim;
	}

	public void setNbhdim(double nbhdim) {
		this.nbhdim = nbhdim;
	}

	public double getNbhmai() {
		return nbhmai;
	}

	public void setNbhmai(double nbhmai) {
		this.nbhmai = nbhmai;
	}

	public double getNbhnuit() {
		return nbhnuit;
	}

	public void setNbhnuit(double nbhnuit) {
		this.nbhnuit = nbhnuit;
	}

	public double getNbhssimple() {
		return nbhssimple;
	}

	public void setNbhssimple(double nbhssimple) {
		this.nbhssimple = nbhssimple;
	}

	public double getNbhscomposees() {
		return nbhscomposees;
	}

	public void setNbhscomposees(double nbhscomposees) {
		this.nbhscomposees = nbhscomposees;
	}

	public double getNbhcomplementaires() {
		return nbhcomplementaires;
	}

	public void setNbhcomplementaires(double nbhcomplementaires) {
		this.nbhcomplementaires = nbhcomplementaires;
	}
	
	
}
