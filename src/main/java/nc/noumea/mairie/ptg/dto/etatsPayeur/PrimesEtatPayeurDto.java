package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.util.Date;

import nc.noumea.mairie.domain.MairiePrimeTableEnum;
import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.service.impl.HelperService;

public class PrimesEtatPayeurDto {

	private String type;
	private String quantite;
	private Date date;
	private Integer norubr;

	public PrimesEtatPayeurDto() {

	}

	public PrimesEtatPayeurDto(VentilPrime vp, HelperService hS) {

		this(vp, null, hS);

	}

	public PrimesEtatPayeurDto(VentilPrime vpNew, VentilPrime vpOld, HelperService hS) {

		this.type = vpNew.getRefPrime().getLibelle();
		this.norubr = vpNew.getRefPrime().getNoRubr();
		this.date = vpNew.getDatePrime();

		switch (vpNew.getRefPrime().getTypeSaisie()) {
			case NB_INDEMNITES:
			case CASE_A_COCHER:
				this.quantite = new Double(vpNew.getQuantite() - (vpOld != null ? vpOld.getQuantite() : 0)).toString();
				break;
			case NB_HEURES:
				// #15317 on arrondi à l'unité supérieure
				if (vpNew.getRefPrime().getMairiePrimeTableEnum() == MairiePrimeTableEnum.SPPRIM) {
					Double qte = vpNew.getQuantite() - (vpOld != null ? vpOld.getQuantite() : 0);
					double quantiteArrondie = hS.convertMinutesToMairieNbHeuresFormat(qte.intValue());
					this.quantite = hS.formatMinutesToStringForEVP(((int) Math.ceil(quantiteArrondie))*60);
				} else {
					this.quantite = hS.formatMinutesToStringForEVP(vpNew.getQuantite().intValue()
							- (vpOld != null ? vpOld.getQuantite().intValue() : 0));
				}
				break;
			case PERIODE_HEURES:
				this.quantite = hS.formatMinutesToStringForEVP(vpNew.getQuantite().intValue()
						- (vpOld != null ? vpOld.getQuantite().intValue() : 0));
				break;
		}
	}

	public String getQuantite() {
		return quantite;
	}

	public void setQuantite(String quantite) {
		this.quantite = quantite;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getNorubr() {
		return norubr;
	}

	public void setNorubr(Integer norubr) {
		this.norubr = norubr;
	}
}
