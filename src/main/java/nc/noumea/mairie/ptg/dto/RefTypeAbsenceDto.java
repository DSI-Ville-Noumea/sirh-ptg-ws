package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.RefTypeAbsence;

@XmlRootElement
public class RefTypeAbsenceDto {

	private Integer idRefTypeAbsence;
	private String libelle;

	public RefTypeAbsenceDto() {
	}

	public RefTypeAbsenceDto(RefTypeAbsence type) {
		super();
		this.idRefTypeAbsence = type.getIdRefTypeAbsence();
		this.libelle = type.getLabel();

	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public Integer getIdRefTypeAbsence() {
		return idRefTypeAbsence;
	}

	public void setIdRefTypeAbsence(Integer idRefTypeAbsence) {
		this.idRefTypeAbsence = idRefTypeAbsence;
	}
}
