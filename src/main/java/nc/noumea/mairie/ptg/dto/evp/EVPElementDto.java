package nc.noumea.mairie.ptg.dto.evp;

import java.util.Date;

public class EVPElementDto {
	
	private String rubrique;
	private final String format = "Q";
	private String quantite;
	private Date periodeEV;
	
	public String getRubrique() {
		return rubrique;
	}
	public void setRubrique(String rubrique) {
		this.rubrique = rubrique;
	}
	public String getQuantite() {
		return quantite;
	}
	public void setQuantite(String quantite) {
		this.quantite = quantite;
	}
	public Date getPeriodeEV() {
		return periodeEV;
	}
	public void setPeriodeEV(Date periodeEV) {
		this.periodeEV = periodeEV;
	}
	public String getFormat() {
		return format;
	}
	
	
}
