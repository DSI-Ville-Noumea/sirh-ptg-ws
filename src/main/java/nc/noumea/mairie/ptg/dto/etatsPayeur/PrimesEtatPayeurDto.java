package nc.noumea.mairie.ptg.dto.etatsPayeur;

import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.service.impl.HelperService;

public class PrimesEtatPayeurDto extends AbstractItemEtatPayeurDto {

	private String type;
	private String quantite;

	public PrimesEtatPayeurDto() {

	}

	public PrimesEtatPayeurDto(VentilPrime vp, HelperService hS) {

		this(vp, null, hS);

	}

	public PrimesEtatPayeurDto(VentilPrime vpNew, VentilPrime vpOld, HelperService hS) {
		
		super(vpNew);

		this.type = vpNew.getRefPrime().getLibelle();

		switch (vpNew.getRefPrime().getTypeSaisie()) {
			case NB_INDEMNITES:
			case CASE_A_COCHER:
				this.quantite = new Integer(vpNew.getQuantite() - (vpOld != null ? vpOld.getQuantite() : 0)).toString();
				break;
			case NB_HEURES:
			case PERIODE_HEURES:
				this.quantite = hS.formatMinutesToString(vpNew.getQuantite() - (vpOld != null ? vpOld.getQuantite() : 0));
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
}
