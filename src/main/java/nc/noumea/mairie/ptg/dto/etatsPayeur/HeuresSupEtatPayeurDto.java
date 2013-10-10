package nc.noumea.mairie.ptg.dto.etatsPayeur;

import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.service.impl.HelperService;

public class HeuresSupEtatPayeurDto extends AbstractItemEtatPayeurDto {

	private String horsContrat;
	private String sup;
	private String sup25;
	private String sup50;
	private String djf;
	private String djf25;
	private String djf50;
	private String h1Mai;
	private String nuit;
	private String normales;
	private String complementaires;
	private String simples;
	private String composees;

	public HeuresSupEtatPayeurDto() {

	}

	public HeuresSupEtatPayeurDto(VentilHsup vh, HelperService hS) {

		this(vh, null, hS);

	}

	public HeuresSupEtatPayeurDto(VentilHsup vhNew, VentilHsup vhOld, HelperService hS) {

		super(vhNew);

		horsContrat = hS.formatMinutesToString(vhNew.getMHorsContrat() - (vhOld != null ? vhOld.getMHorsContrat() : 0));
		sup = hS.formatMinutesToString(vhNew.getMSup() - (vhOld != null ? vhOld.getMSup() : 0));
		sup25 = hS.formatMinutesToString((int) vhNew.getMSup25() - (vhOld != null ? vhOld.getMSup25() : 0));
		sup50 = hS.formatMinutesToString(vhNew.getMSup50() - (vhOld != null ? vhOld.getMSup50() : 0));
		djf = hS.formatMinutesToString(vhNew.getMsdjf() - (vhOld != null ? vhOld.getMsdjf() : 0));
		djf25 = hS.formatMinutesToString(vhNew.getMsdjf25() - (vhOld != null ? vhOld.getMsdjf25() : 0));
		djf50 = hS.formatMinutesToString(vhNew.getMsdjf50() - (vhOld != null ? vhOld.getMsdjf50() : 0));
		h1Mai = hS.formatMinutesToString(vhNew.getMMai() - (vhOld != null ? vhOld.getMMai() : 0));
		nuit = hS.formatMinutesToString(vhNew.getMsNuit() - (vhOld != null ? vhOld.getMsNuit() : 0));
		normales = hS.formatMinutesToString(vhNew.getMNormales() - (vhOld != null ? vhOld.getMNormales() : 0));
		complementaires = hS.formatMinutesToString(vhNew.getMComplementaires()
				- (vhOld != null ? vhOld.getMComplementaires() : 0));
		simples = hS.formatMinutesToString(vhNew.getMSimple() - (vhOld != null ? vhOld.getMSimple() : 0));
		composees = hS.formatMinutesToString(vhNew.getMComposees() - (vhOld != null ? vhOld.getMComposees() : 0));

	}

	public String getHorsContrat() {
		return horsContrat;
	}

	public void setHorsContrat(String horsContrat) {
		this.horsContrat = horsContrat;
	}

	public String getSup() {
		return sup;
	}

	public void setSup(String sup) {
		this.sup = sup;
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

	public String getDjf25() {
		return djf25;
	}

	public void setDjf25(String djf25) {
		this.djf25 = djf25;
	}

	public String getDjf50() {
		return djf50;
	}

	public void setDjf50(String djf50) {
		this.djf50 = djf50;
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

	public String getComplementaires() {
		return complementaires;
	}

	public void setComplementaires(String complementaires) {
		this.complementaires = complementaires;
	}

	public String getSimples() {
		return simples;
	}

	public void setSimples(String simples) {
		this.simples = simples;
	}

	public String getComposees() {
		return composees;
	}

	public void setComposees(String composees) {
		this.composees = composees;
	}

}
