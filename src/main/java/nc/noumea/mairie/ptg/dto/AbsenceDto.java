package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;

@XmlRootElement
public class AbsenceDto extends PointageDto {

	private Boolean concertee;

	public AbsenceDto() {
	}

	public AbsenceDto(Pointage p) {
		super(p);
		this.concertee = p.getAbsenceConcertee();
	}

	public Boolean getConcertee() {
		return concertee;
	}

	public void setConcertee(Boolean concertee) {
		this.concertee = concertee;
	}
}
