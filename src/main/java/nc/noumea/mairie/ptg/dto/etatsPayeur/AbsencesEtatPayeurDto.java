package nc.noumea.mairie.ptg.dto.etatsPayeur;

import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.service.impl.HelperService;

public class AbsencesEtatPayeurDto {

	private String quantiteInf1Heure;
	private String quantiteEntre1HeureEt4Heure;
	private String quantiteSup4Heure;

	public AbsencesEtatPayeurDto() {

	}

	public AbsencesEtatPayeurDto(VentilAbsence va, HelperService hS) {

		this(va, null, hS);
	}

	public AbsencesEtatPayeurDto(VentilAbsence vaNew, VentilAbsence vaOld, HelperService hS) {

		Integer quantiteConcerte = vaNew.getMinutesConcertee() - (vaOld != null ? vaOld.getMinutesConcertee() : 0);
		Integer quantiteImmediate = vaNew.getMinutesImmediat() - (vaOld != null ? vaOld.getMinutesImmediat() : 0);
		Integer quantiteNonConcerte = vaNew.getMinutesNonConcertee()
				- (vaOld != null ? vaOld.getMinutesNonConcertee() : 0);

		Integer total = quantiteConcerte + quantiteImmediate + quantiteNonConcerte;
		if (total == 0) {
			quantiteInf1Heure = "";
			quantiteEntre1HeureEt4Heure = "";
			quantiteSup4Heure = "";
		} else if (total <= 60) {
			quantiteInf1Heure = hS.formatMinutesToString(total);
			quantiteEntre1HeureEt4Heure = "";
			quantiteSup4Heure = "";
		} else if (60 < total && total <= 240) {
			quantiteInf1Heure = "";
			quantiteEntre1HeureEt4Heure = hS.formatMinutesToString(total);
			quantiteSup4Heure = "";
		} else {
			quantiteInf1Heure = "";
			quantiteEntre1HeureEt4Heure = "";
			quantiteSup4Heure = hS.formatMinutesToString(total);
		}
	}

	public String getQuantiteInf1Heure() {
		return quantiteInf1Heure;
	}

	public void setQuantiteInf1Heure(String quantiteInf1Heure) {
		this.quantiteInf1Heure = quantiteInf1Heure;
	}

	public String getQuantiteEntre1HeureEt4Heure() {
		return quantiteEntre1HeureEt4Heure;
	}

	public void setQuantiteEntre1HeureEt4Heure(String quantiteEntre1HeureEt4Heure) {
		this.quantiteEntre1HeureEt4Heure = quantiteEntre1HeureEt4Heure;
	}

	public String getQuantiteSup4Heure() {
		return quantiteSup4Heure;
	}

	public void setQuantiteSup4Heure(String quantiteSup4Heure) {
		this.quantiteSup4Heure = quantiteSup4Heure;
	}

}
