package nc.noumea.mairie.abs.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.RefTypeAbsence;
import nc.noumea.mairie.sirh.dto.RefTypeSaisiCongeAnnuelDto;

/**
 * ATTENTION 
 * il existe dans le meme projet un DTO avec le meme nom
 * provement de SIRH-ABS-WS
 * 
 * @author rebjo84
 */
@XmlRootElement
public class RefTypeAbsenceDto {

	private Integer idRefTypeAbsence;
	private String libelle;
	private RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDto;

	public RefTypeAbsenceDto() {
	}

	public RefTypeAbsenceDto(RefTypeAbsence type) {
		super();
		this.idRefTypeAbsence = type.getIdRefTypeAbsence();
		this.libelle = type.getLabel();

	}
	

	public RefTypeSaisiCongeAnnuelDto getTypeSaisiCongeAnnuelDto() {
		return typeSaisiCongeAnnuelDto;
	}

	public void setTypeSaisiCongeAnnuelDto(
			RefTypeSaisiCongeAnnuelDto typeSaisiCongeAnnuelDto) {
		this.typeSaisiCongeAnnuelDto = typeSaisiCongeAnnuelDto;
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
