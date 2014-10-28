package nc.noumea.mairie.sirh.dto;

import javax.persistence.Transient;

public class BaseHorairePointageDto {

	private Integer idBaseHorairePointage;
	private String codeBaseHorairePointage;
	private String libelleBaseHorairePointage;
	private String descriptionBaseHorairePointage;
	private Double heureLundi;
	private Double heureMardi;
	private Double heureMercredi;
	private Double heureJeudi;
	private Double heureVendredi;
	private Double heureSamedi;
	private Double heureDimanche;
	private Double baseLegale;
	private Double baseCalculee;

	public BaseHorairePointageDto() {
		super();
	}

	@Transient
	public double getDayBase(int day) {

		switch (day) {
			case 0:
				return getHeureLundi();
			case 1:
				return getHeureMardi();
			case 2:
				return getHeureMercredi();
			case 3:
				return getHeureJeudi();
			case 4:
				return getHeureVendredi();
			case 5:
				return getHeureSamedi();
			case 6:
				return getHeureDimanche();
			default:
				return 0.0;
		}
	}

	public Integer getIdBaseHorairePointage() {
		return idBaseHorairePointage;
	}

	public void setIdBaseHorairePointage(Integer idBaseHorairePointage) {
		this.idBaseHorairePointage = idBaseHorairePointage;
	}

	public String getCodeBaseHorairePointage() {
		return codeBaseHorairePointage;
	}

	public void setCodeBaseHorairePointage(String codeBaseHorairePointage) {
		this.codeBaseHorairePointage = codeBaseHorairePointage;
	}

	public String getLibelleBaseHorairePointage() {
		return libelleBaseHorairePointage;
	}

	public void setLibelleBaseHorairePointage(String libelleBaseHorairePointage) {
		this.libelleBaseHorairePointage = libelleBaseHorairePointage;
	}

	public String getDescriptionBaseHorairePointage() {
		return descriptionBaseHorairePointage;
	}

	public void setDescriptionBaseHorairePointage(String descriptionBaseHorairePointage) {
		this.descriptionBaseHorairePointage = descriptionBaseHorairePointage;
	}

	public Double getHeureLundi() {
		return heureLundi;
	}

	public void setHeureLundi(Double heureLundi) {
		this.heureLundi = heureLundi;
	}

	public Double getHeureMardi() {
		return heureMardi;
	}

	public void setHeureMardi(Double heureMardi) {
		this.heureMardi = heureMardi;
	}

	public Double getHeureMercredi() {
		return heureMercredi;
	}

	public void setHeureMercredi(Double heureMercredi) {
		this.heureMercredi = heureMercredi;
	}

	public Double getHeureJeudi() {
		return heureJeudi;
	}

	public void setHeureJeudi(Double heureJeudi) {
		this.heureJeudi = heureJeudi;
	}

	public Double getHeureVendredi() {
		return heureVendredi;
	}

	public void setHeureVendredi(Double heureVendredi) {
		this.heureVendredi = heureVendredi;
	}

	public Double getHeureSamedi() {
		return heureSamedi;
	}

	public void setHeureSamedi(Double heureSamedi) {
		this.heureSamedi = heureSamedi;
	}

	public Double getHeureDimanche() {
		return heureDimanche;
	}

	public void setHeureDimanche(Double heureDimanche) {
		this.heureDimanche = heureDimanche;
	}

	public Double getBaseLegale() {
		return baseLegale;
	}

	public void setBaseLegale(Double baseLegale) {
		this.baseLegale = baseLegale;
	}

	public Double getBaseCalculee() {
		return baseCalculee;
	}

	public void setBaseCalculee(Double baseCalculee) {
		this.baseCalculee = baseCalculee;
	}
}
