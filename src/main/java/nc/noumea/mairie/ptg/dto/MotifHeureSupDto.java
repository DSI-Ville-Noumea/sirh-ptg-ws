package nc.noumea.mairie.ptg.dto;

import nc.noumea.mairie.ptg.domain.MotifHeureSup;

public class MotifHeureSupDto {

	private Integer idMotifHsup;
	private String libelle;

	public MotifHeureSupDto() {
	}

	public MotifHeureSupDto(MotifHeureSup motif) {
		super();
		this.idMotifHsup = motif.getIdMotifHsup();
		this.libelle = motif.getText();

	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Integer getIdMotifHsup() {
		return idMotifHsup;
	}

	public void setIdMotifHsup(Integer idMotifHsup) {
		this.idMotifHsup = idMotifHsup;
	}
}
