package nc.noumea.mairie.ptg.dto.etatsPayeur;

import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.service.impl.HelperService;

public class HeuresSupEtatPayeurDto {

	private String normales;
	private String sup25;
	private String sup50;
	private String nuit;
	private String djf;
	private String h1Mai;

	public HeuresSupEtatPayeurDto() {

	}

	public HeuresSupEtatPayeurDto(VentilHsup vh, HelperService hS) {

		this(vh, null, hS);
	}

	public HeuresSupEtatPayeurDto(VentilHsup vhNew, VentilHsup vhOld, HelperService hS) {

		sup25 = hS.formatMinutesToString(((int) vhNew.getMSup25() - (vhOld != null ? vhOld.getMSup25() : 0))
				+ (vhNew.getMSimple() - (vhOld != null ? vhOld.getMSimple() : 0)));
		sup50 = hS.formatMinutesToString((vhNew.getMSup50() - (vhOld != null ? vhOld.getMSup50() : 0))
				+ (vhNew.getMComposees() - (vhOld != null ? vhOld.getMComposees() : 0)));
		djf = hS.formatMinutesToString(vhNew.getMsdjf() - (vhOld != null ? vhOld.getMsdjf() : 0));
		h1Mai = hS.formatMinutesToString(vhNew.getMMai() - (vhOld != null ? vhOld.getMMai() : 0));
		nuit = hS.formatMinutesToString(vhNew.getMsNuit() - (vhOld != null ? vhOld.getMsNuit() : 0));
		normales = hS.formatMinutesToString((vhNew.getMNormales() - (vhOld != null ? vhOld.getMNormales() : 0)));
	}

	public String getSup25() {
		return sup25;
	}

	public void setSup25(String sup25) {
		this.sup25 = sup25;
	}

	public String getSup50() {
		return sup50;
	}

	public void setSup50(String sup50) {
		this.sup50 = sup50;
	}

	public String getDjf() {
		return djf;
	}

	public void setDjf(String djf) {
		this.djf = djf;
	}

	public String getH1Mai() {
		return h1Mai;
	}

	public void setH1Mai(String h1Mai) {
		this.h1Mai = h1Mai;
	}

	public String getNuit() {
		return nuit;
	}

	public void setNuit(String nuit) {
		this.nuit = nuit;
	}

	public String getNormales() {
		return normales;
	}

	public void setNormales(String normales) {
		this.normales = normales;
	}

}
