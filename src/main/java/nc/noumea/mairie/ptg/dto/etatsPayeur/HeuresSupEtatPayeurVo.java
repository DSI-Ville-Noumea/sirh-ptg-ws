package nc.noumea.mairie.ptg.dto.etatsPayeur;

import java.util.Date;

import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.service.impl.HelperService;

public class HeuresSupEtatPayeurVo {

	private Integer normales;
	private Integer sup25;
	private Integer sup50;
	private Integer nuit;
	private Integer djf;
	private Integer h1Mai;	
	private Integer simples;
	private Integer composees;
	private Date dateVentilation;

	public HeuresSupEtatPayeurVo() {

	}

	public HeuresSupEtatPayeurVo(VentilHsup vh) {
		this(vh, null);
	}

	public HeuresSupEtatPayeurVo(VentilHsup vhNew, VentilHsup vhOld, Boolean isForEVP) {

		HelperService hs = new HelperService();
		
		// #14640 retirer les heures recuperees 
		int mSup25 = (vhNew.getMSup25() - vhNew.getMSup25Recup());
		int mSup25Old = (vhOld != null ? vhOld.getMSup25() - vhOld.getMSup25Recup() : 0);
		if (!isForEVP) {
			mSup25 += vhNew.getMSimple() - vhNew.getMSimpleRecup();
			mSup25Old += vhOld != null ? vhOld.getMSimple() - vhOld.getMSimpleRecup() : 0;
			simples = 0;
		}
		sup25 = mSup25 - mSup25Old;
		
		int mSup50 = (vhNew.getMSup50() - vhNew.getMSup50Recup());
		int mSup50Old = (vhOld != null ? vhOld.getMSup50() - vhOld.getMSup50Recup() : 0);
		if (!isForEVP) {
			mSup50 += vhNew.getMComposees() - vhNew.getMComposeesRecup();
			mSup25Old += vhOld != null ? vhOld.getMComposees() - vhOld.getMComposeesRecup() : 0;
			composees = 0;
		}
		sup50 = mSup50 - mSup50Old;
		
		int mDjf = vhNew.getMsdjf() - vhNew.getMsdjfRecup();
		int mDjfOld = vhOld != null ? vhOld.getMsdjf() - vhOld.getMsdjfRecup() : 0;
		djf = mDjf- mDjfOld;
		
		int mH1Mai = vhNew.getMMai() - vhNew.getMMaiRecup();
		int mH1MaiOld = vhOld != null ? vhOld.getMMai() - vhOld.getMMaiRecup() : 0;
		h1Mai = mH1Mai - mH1MaiOld;
		
		int mNuit = vhNew.getMsNuit() - vhNew.getMsNuitRecup();
		int mNuitOld = vhOld != null ? vhOld.getMsNuit() - vhOld.getMsNuitRecup() : 0;
		nuit = mNuit - mNuitOld;
		
		int mNormales = vhNew.getMNormales() - vhNew.getMNormalesRecup();
		int mNormalesOld = vhOld != null ? vhOld.getMNormales() - vhOld.getMNormalesRecup() : 0;
		normales = mNormales - mNormalesOld;
		
		if (isForEVP) {
			int mSimples = vhNew.getMSimple() - vhNew.getMSimpleRecup();
			int mSimpleOld = vhOld != null ? vhOld.getMSimple() - vhOld.getMSimpleRecup() : 0;
			simples = mSimples - mSimpleOld;
			
			int mComposees = vhNew.getMComposees() - vhNew.getMComposeesRecup();
			int mComposeesOld = vhOld != null ? vhOld.getMComposees() - vhOld.getMComposeesRecup() : 0;
			composees = mComposees - mComposeesOld;
		}
		
		dateVentilation = hs.getDatePremierJourOfMonth(vhOld == null ? vhNew.getDateLundi() : vhOld.getDateLundi());
	}

	public HeuresSupEtatPayeurVo(VentilHsup vhNew, VentilHsup vhOld) {

		// #14640 retirer les heures recuperees 
		int mSup25 = (vhNew.getMSup25() - vhNew.getMSup25Recup()) + (vhNew.getMSimple() - vhNew.getMSimpleRecup());
		int mSup25Old = (vhOld != null ? vhOld.getMSup25() - vhOld.getMSup25Recup() : 0)
				+ (vhOld != null ? vhOld.getMSimple() - vhOld.getMSimpleRecup() : 0);
		sup25 = mSup25 - mSup25Old;
		
		int mSup50 = (vhNew.getMSup50() - vhNew.getMSup50Recup()) + (vhNew.getMComposees() - vhNew.getMComposeesRecup());
		int mSup50Old = (vhOld != null ? vhOld.getMSup50() - vhOld.getMSup50Recup() : 0)
				+ (vhOld != null ? vhOld.getMComposees() - vhOld.getMComposeesRecup() : 0);
		sup50 = mSup50 - mSup50Old;
		
		int mDjf = vhNew.getMsdjf() - vhNew.getMsdjfRecup();
		int mDjfOld = vhOld != null ? vhOld.getMsdjf() - vhOld.getMsdjfRecup() : 0;
		djf = mDjf- mDjfOld;
		
		int mH1Mai = vhNew.getMMai() - vhNew.getMMaiRecup();
		int mH1MaiOld = vhOld != null ? vhOld.getMMai() - vhOld.getMMaiRecup() : 0;
		h1Mai = mH1Mai - mH1MaiOld;
		
		int mNuit = vhNew.getMsNuit() - vhNew.getMsNuitRecup();
		int mNuitOld = vhOld != null ? vhOld.getMsNuit() - vhOld.getMsNuitRecup() : 0;
		nuit = mNuit - mNuitOld;
		
		int mNormales = vhNew.getMNormales() - vhNew.getMNormalesRecup();
		int mNormalesOld = vhOld != null ? vhOld.getMNormales() - vhOld.getMNormalesRecup() : 0;
		normales = mNormales - mNormalesOld;
		
		int mSimples = vhNew.getMSimple() - vhNew.getMSimpleRecup();
		int mSimpleOld = vhOld != null ? vhOld.getMSimple() - vhOld.getMSimpleRecup() : 0;
		simples = mSimples - mSimpleOld;
		
		int mComposees = vhNew.getMComposees() - vhNew.getMComposeesRecup();
		int mComposeesOld = vhOld != null ? vhOld.getMComposees() - vhOld.getMComposeesRecup() : 0;
		composees = mComposees - mComposeesOld;
	}

	public Integer getNormales() {
		return normales;
	}

	public void setNormales(Integer normales) {
		this.normales = normales;
	}

	public Integer getSup25() {
		return sup25;
	}

	public void setSup25(Integer sup25) {
		this.sup25 = sup25;
	}

	public Integer getSup50() {
		return sup50;
	}

	public void setSup50(Integer sup50) {
		this.sup50 = sup50;
	}

	public Integer getNuit() {
		return nuit;
	}

	public void setNuit(Integer nuit) {
		this.nuit = nuit;
	}

	public Integer getDjf() {
		return djf;
	}

	public void setDjf(Integer djf) {
		this.djf = djf;
	}

	public Integer getH1Mai() {
		return h1Mai;
	}

	public void setH1Mai(Integer h1Mai) {
		this.h1Mai = h1Mai;
	}

	public Integer getSimples() {
		return simples;
	}

	public void setSimples(Integer simples) {
		this.simples = simples;
	}

	public Integer getComposees() {
		return composees;
	}

	public void setComposees(Integer composees) {
		this.composees = composees;
	}

	public Date getDateVentilation() {
		return dateVentilation;
	}

	public void setDateVentilation(Date dateVentilation) {
		this.dateVentilation = dateVentilation;
	}
	
}
