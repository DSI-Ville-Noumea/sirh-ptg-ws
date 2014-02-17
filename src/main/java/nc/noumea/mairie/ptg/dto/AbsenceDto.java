package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;

@XmlRootElement
public class AbsenceDto extends PointageDto {

	private Boolean concertee;

	private Integer idTypeAbsence;

	public AbsenceDto() {
	}

	public AbsenceDto(Pointage p) {
		super(p);
		this.concertee = p.getAbsenceConcertee();
		if (null != p.getTypeAbsence()) {
			this.idTypeAbsence = p.getTypeAbsence().getIdRefTypeAbsence();
		}
	}

	public Boolean getConcertee() {
		return concertee;
	}

	public void setConcertee(Boolean concertee) {
		this.concertee = concertee;
	}

	public Integer getIdTypeAbsence() {
		return idTypeAbsence;
	}

	public void setIdTypeAbsence(Integer idTypeAbsence) {
		this.idTypeAbsence = idTypeAbsence;
	}
}
