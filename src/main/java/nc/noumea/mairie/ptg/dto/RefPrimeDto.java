package nc.noumea.mairie.ptg.dto;

import nc.noumea.mairie.ptg.domain.RefPrime;

public class RefPrimeDto {

	private Integer idRefPrime;
	private Integer numRubrique;
	private String libelle;
	private String description;
	private String typeSaisie;
	private boolean calculee;
	private String statut;

	public RefPrimeDto() {
	}

	public RefPrimeDto(RefPrime prime) {
		idRefPrime = prime.getIdRefPrime();
		numRubrique = prime.getNoRubr();
		libelle = prime.getLibelle();
		description = prime.getDescription();
		if (prime.getTypeSaisie() == null) {
			typeSaisie = "null";
		} else {
			typeSaisie = prime.getTypeSaisie().name();
		}
		calculee = prime.isCalculee();
		if (prime.getStatut() == null) {
			statut = "null";
		} else {
			statut = prime.getStatut().name();
		}
	}

	public RefPrimeDto(RefPrimeDto primeDto) {
		this.statut = primeDto.statut;
		this.calculee = primeDto.calculee;
		this.description = primeDto.description;
		this.typeSaisie = primeDto.typeSaisie;
		this.libelle = primeDto.libelle;
		this.numRubrique = primeDto.numRubrique;
		this.idRefPrime = primeDto.idRefPrime;
	}

	public Integer getIdRefPrime() {
		return idRefPrime;
	}

	public void setIdRefPrime(Integer idRefPrime) {
		this.idRefPrime = idRefPrime;
	}

	public Integer getNumRubrique() {
		return numRubrique;
	}

	public void setNumRubrique(Integer numRubrique) {
		this.numRubrique = numRubrique;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isCalculee() {
		return calculee;
	}

	public void setCalculee(boolean calculee) {
		this.calculee = calculee;
	}

	public String getStatut() {
		return statut;
	}

	public void setStatut(String statut) {
		this.statut = statut;
	}

	public String getTypeSaisie() {
		return typeSaisie;
	}

	public void setTypeSaisie(String typeSaisie) {
		this.typeSaisie = typeSaisie;
	}

}
