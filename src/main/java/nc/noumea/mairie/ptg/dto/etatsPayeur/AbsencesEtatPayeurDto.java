package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.util.List;

import nc.noumea.mairie.ptg.domain.VentilAbsence;

public class AbsencesEtatPayeurDto {

	private String quantiteInf1Heure;
	private String quantiteEntre1HeureEt4Heure;
	private String quantiteSup4Heure;

	public AbsencesEtatPayeurDto() {

	}

	public AbsencesEtatPayeurDto(VentilAbsence va) {

		this(va, null);
	}

	public AbsencesEtatPayeurDto(VentilAbsence vaNew, VentilAbsence vaOld) {

		// #14977
		Integer nombreAbsenceInferieur1 = vaNew.getNombreAbsenceInferieur1() - (vaOld != null ? vaOld.getNombreAbsenceInferieur1() : 0);
		Integer nombreAbsenceEntre1Et4 = vaNew.getNombreAbsenceEntre1Et4() - (vaOld != null ? vaOld.getNombreAbsenceEntre1Et4() : 0);
		Integer nombreAbsenceSuperieur1 = vaNew.getNombreAbsenceSuperieur1() - (vaOld != null ? vaOld.getNombreAbsenceSuperieur1() : 0);

		quantiteInf1Heure = null != nombreAbsenceInferieur1 && 0 != nombreAbsenceInferieur1 ? nombreAbsenceInferieur1.toString() : "";
		quantiteEntre1HeureEt4Heure = null != nombreAbsenceEntre1Et4 && 0 != nombreAbsenceEntre1Et4 ? nombreAbsenceEntre1Et4.toString() : "";
		quantiteSup4Heure = null != nombreAbsenceSuperieur1 && 0 != nombreAbsenceSuperieur1 ? nombreAbsenceSuperieur1.toString() : "";
	}

	public AbsencesEtatPayeurDto(List<AbsencesEtatPayeurDto> listAbsencesEtatPayeurDto) {

		Integer qteInf1Heure = 0;
		Integer qteEntre1HeureEt4Heure = 0;
		Integer qteSup4Heure = 0;
		
		if(null != listAbsencesEtatPayeurDto) {
			for(AbsencesEtatPayeurDto vo : listAbsencesEtatPayeurDto) {
				qteInf1Heure += "".equals(vo.getQuantiteInf1Heure()) ? 0 : new Integer(vo.getQuantiteInf1Heure());
				qteEntre1HeureEt4Heure += "".equals(vo.getQuantiteEntre1HeureEt4Heure()) ? 0 : new Integer(vo.getQuantiteEntre1HeureEt4Heure());
				qteSup4Heure += "".equals(vo.getQuantiteSup4Heure()) ? 0 : new Integer(vo.getQuantiteSup4Heure());
			}
		}
		
		quantiteInf1Heure = 0 == qteInf1Heure ? "" : qteInf1Heure.toString();
		quantiteEntre1HeureEt4Heure =  0 == qteEntre1HeureEt4Heure ? "" : qteEntre1HeureEt4Heure.toString();
		quantiteSup4Heure =  0 == qteSup4Heure ? "" : qteSup4Heure.toString();
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
