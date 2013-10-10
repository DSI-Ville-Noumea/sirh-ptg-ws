package nc.noumea.mairie.ptg.dto.etatsPayeur;

import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.service.impl.HelperService;

public class AbsencesEtatPayeurDto extends AbstractItemEtatPayeurDto {

	private String type;
	private String quantite;

	public AbsencesEtatPayeurDto() {

	}

	public AbsencesEtatPayeurDto(VentilAbsence va, boolean isConcertee, HelperService hS) {

		this(va, null, isConcertee, hS);
	}
	
	public AbsencesEtatPayeurDto(VentilAbsence vaNew, VentilAbsence vaOld, boolean isConcertee, HelperService hS) {

		super(vaNew);

		if (!isConcertee) {
			this.type = "Absence non concertée";
			quantite = hS.formatMinutesToString(vaNew.getMinutesNonConcertee() - (vaOld != null ? vaOld.getMinutesNonConcertee() : 0));
		} else {
			this.type = "Absence de service fait";
			quantite = hS.formatMinutesToString(vaNew.getMinutesConcertee() - (vaOld != null ? vaOld.getMinutesConcertee() : 0));
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
