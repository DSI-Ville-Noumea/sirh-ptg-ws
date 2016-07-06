package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.RefEtat;

@XmlRootElement
public class RefEtatDto {

	private Integer idRefEtat;
	private String libelle;

	public RefEtatDto() {
	}

	public RefEtatDto(RefEtat etat) {
		super();
		this.idRefEtat = etat.getIdRefEtat();
		this.libelle = etat.getLabel();

	}

	public Integer getIdRefEtat() {
		return idRefEtat;
	}

	public void setIdRefEtat(Integer idRefEtat) {
		this.idRefEtat = idRefEtat;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}
}
