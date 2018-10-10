package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.util.List;

import nc.noumea.mairie.ptg.service.impl.HelperService;

public class HeuresSupEtatPayeurDto {

	private String normales;
	private String sup25;
	private String sup50;
	private String nuit;
	private String djf;
	private String h1Mai;
	private String simples;
	private String composees;

	public HeuresSupEtatPayeurDto() {

	}

	public HeuresSupEtatPayeurDto(List<HeuresSupEtatPayeurVo> listHSupEtatPayeur, HelperService hS) {

		int mSup25 = 0;
		int mSup50 = 0;
		int mDjf = 0;
		int mH1Mai = 0;
		int mNuit = 0;
		int mNormales = 0;
		int mSimples = 0;
		int mComposees = 0;
		
		if(null != listHSupEtatPayeur) {
			for(HeuresSupEtatPayeurVo vo : listHSupEtatPayeur) {
				mSup25 += vo.getSup25();
				mSup50 += vo.getSup50();
				mDjf += vo.getDjf();
				mH1Mai += vo.getH1Mai();
				mNuit += vo.getNuit();
				mNormales += vo.getNormales();
				mSimples += vo.getSimples();
				mComposees += vo.getComposees();
			}
		}
		
		sup25 = hS.formatMinutesToString(mSup25);
		sup50 = hS.formatMinutesToString(mSup50);
		djf = hS.formatMinutesToString(mDjf);
		h1Mai = hS.formatMinutesToString(mH1Mai);
		nuit = hS.formatMinutesToString(mNuit);
		normales = hS.formatMinutesToString(mNormales);
		simples = hS.formatMinutesToString(mSimples);
		composees = hS.formatMinutesToString(mComposees);
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
