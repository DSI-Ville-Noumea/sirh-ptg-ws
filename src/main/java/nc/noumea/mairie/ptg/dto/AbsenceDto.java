package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;

@XmlRootElement
public class AbsenceDto extends PointageDto {

	private Integer idRefTypeAbsence;

	public AbsenceDto() {
	}

	public AbsenceDto(Pointage p) {
		super(p);
		if (null != p.getRefTypeAbsence()) {
			this.idRefTypeAbsence = p.getRefTypeAbsence().getIdRefTypeAbsence();
		}
	}

	public Integer getIdRefTypeAbsence() {
		return idRefTypeAbsence;
	}

	public void setIdRefTypeAbsence(Integer idRefTypeAbsence) {
		this.idRefTypeAbsence = idRefTypeAbsence;
	}
}
