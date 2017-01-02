package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.RefTypeAbsenceEnum;

@XmlRootElement
public class AbsenceDtoKiosque extends PointageDtoKiosque {

	private Integer	idRefTypeAbsence;
	private boolean	absenceModifiable	= true;

	public AbsenceDtoKiosque() {
	}

	public AbsenceDtoKiosque(Pointage p) {
		super(p);
		if (null != p.getRefTypeAbsence()) {
			this.idRefTypeAbsence = p.getRefTypeAbsence().getIdRefTypeAbsence();
			if (p.getRefTypeAbsence() != null && p.getRefTypeAbsence().getIdRefTypeAbsence() == RefTypeAbsenceEnum.GREVE.getValue()) {
				this.absenceModifiable = false;
			}
		}
	}

	public Integer getIdRefTypeAbsence() {
		return idRefTypeAbsence;
	}

	public void setIdRefTypeAbsence(Integer idRefTypeAbsence) {
		this.idRefTypeAbsence = idRefTypeAbsence;
	}

	public boolean isAbsenceModifiable() {
		return absenceModifiable;
	}

	public void setAbsenceModifiable(boolean isAbsenceModifiable) {
		this.absenceModifiable = isAbsenceModifiable;
	}
}
