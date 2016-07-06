package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.RefTypePointage;

@XmlRootElement
public class RefTypePointageDto {

	private Integer idRefTypePointage;
	private String libelle;

	public RefTypePointageDto() {
	}

	public RefTypePointageDto(RefTypePointage type) {
		super();
		this.idRefTypePointage = type.getIdRefTypePointage();
		this.libelle = type.getLabel();

	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Integer getIdRefTypePointage() {
		return idRefTypePointage;
	}

	public void setIdRefTypePointage(Integer idRefTypePointage) {
		this.idRefTypePointage = idRefTypePointage;
	}
}
