package nc.noumea.mairie.ptg.dto.etatsPayeur;

import nc.noumea.mairie.ptg.domain.RefTypeAbsenceEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.service.impl.HelperService;

public class AbsencesEtatPayeurDto extends AbstractItemEtatPayeurDto {

	private String type;
	private String quantite;

	public AbsencesEtatPayeurDto() {

	}

	public AbsencesEtatPayeurDto(VentilAbsence va, RefTypeAbsenceEnum typeAbs, HelperService hS) {

		this(va, null, typeAbs, hS);
	}
	
	public AbsencesEtatPayeurDto(VentilAbsence vaNew, VentilAbsence vaOld, RefTypeAbsenceEnum typeAbs, HelperService hS) {

		super(vaNew);

		switch(typeAbs) {
			case CONCERTEE:
				this.type = "Absence de service fait";
				quantite = hS.formatMinutesToString(vaNew.getMinutesConcertee() - (vaOld != null ? vaOld.getMinutesConcertee() : 0));
				break;
			case IMMEDIATE:
				this.type = "Absence immédiate";
				quantite = hS.formatMinutesToString(vaNew.getMinutesImmediat() - (vaOld != null ? vaOld.getMinutesImmediat() : 0));
				break;
			case NON_CONCERTEE:
				this.type = "Absence non concertée";
				quantite = hS.formatMinutesToString(vaNew.getMinutesNonConcertee() - (vaOld != null ? vaOld.getMinutesNonConcertee() : 0));
				break;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getQuantite() {
		return quantite;
	}

	public void setQuantite(String quantite) {
		this.quantite = quantite;
	}

}
