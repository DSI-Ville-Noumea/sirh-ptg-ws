package nc.noumea.mairie.ptg.dto;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import nc.noumea.mairie.ptg.domain.VentilHsup;

@XmlRootElement
public class VentilHSupDto extends VentilDto {

	private int mabs;
	private int mHorsContrat;
	private int mSup;
	private int mSup25;
	private int mSup50;
	private int mDjf;
	private int mDjf25;
	private int mDjf50;
	private int m1Mai;
	private int mNuit;
	private int mNormales;
	private int mComplementaires;
	private int mSimples;
	private int mComposees;
	private int mRecuperees;
	private int mSup25R;
	private int mSup50R;
	private int mDjfR;
	private int mDjf25R;
	private int mDjf_50R;
	private int m1maiR;
	private int mNuitR;
	private int mNormalesR;
	private int mComplementairesR;
	private int mSimplesR;
	private int mComposeesR;

	public VentilHSupDto() {
	}

	public VentilHSupDto(VentilHsup hibObj) {
		mabs = hibObj.getmAbsences();
		mHorsContrat = hibObj.getmHorsContrat();
		mSup = hibObj.getmSup();
		mSup25 = hibObj.getmSup25();
		mSup50 = hibObj.getmSup50();
		mDjf = hibObj.getMsdjf();
		mDjf25 = hibObj.getMsdjf25();
		mDjf50 = hibObj.getMsdjf50();
		m1Mai = hibObj.getmMai();
		mNuit = hibObj.getMsNuit();
		mNormales = hibObj.getmNormales();
		mComplementaires = hibObj.getmComplementaires();
		mSimples = hibObj.getmSimple();
		mComposees = hibObj.getmComposees();
		etat = hibObj.getEtat().getCodeEtat();
		mRecuperees = hibObj.getmRecuperees();
		mSup25R = hibObj.getmSup25Recup();
		mSup50R = hibObj.getmSup50Recup();
		mDjfR = hibObj.getMsdjfRecup();
		mDjf25R = hibObj.getMsdjf25Recup();
		mDjf_50R = hibObj.getMsdjf50Recup();
		m1maiR = hibObj.getmMaiRecup();
		mNuitR = hibObj.getMsNuitRecup();
		mNormalesR = hibObj.getmNormalesRecup();
		mComplementairesR = hibObj.getmComplementairesRecup();
		mSimplesR = hibObj.getmSimpleRecup();
		mComposeesR = hibObj.getmComposeesRecup();
		date = hibObj.getDateLundi();
		idAgent = hibObj.getIdAgent();
		idVentil = hibObj.getIdVentilHSup();
	}

	public int getIdVentilHsup() {
		return idVentil;
	}

	public Date getDateLundi() {
		return date;
	}

	public int getMabs() {
		return mabs;
	}

	public void setMabs(int mabs) {
		this.mabs = mabs;
	}

	public int getmHorsContrat() {
		return mHorsContrat;
	}

	public void setmHorsContrat(int mHorsContrat) {
		this.mHorsContrat = mHorsContrat;
	}

	public int getmSup() {
		return mSup;
	}

	public void setmSup(int mSup) {
		this.mSup = mSup;
	}

	public int getmSup25() {
		return mSup25;
	}

	public void setmSup25(int mSup25) {
		this.mSup25 = mSup25;
	}

	public int getmSup50() {
		return mSup50;
	}

	public void setmSup50(int mSup50) {
		this.mSup50 = mSup50;
	}

	public int getmDjf() {
		return mDjf;
	}

	public void setmDjf(int mDjf) {
		this.mDjf = mDjf;
	}

	public int getmDjf25() {
		return mDjf25;
	}

	public void setmDjf25(int mDjf25) {
		this.mDjf25 = mDjf25;
	}

	public int getmDjf50() {
		return mDjf50;
	}

	public void setmDjf50(int mDjf50) {
		this.mDjf50 = mDjf50;
	}

	public int getM1Mai() {
		return m1Mai;
	}

	public void setM1Mai(int m1Mai) {
		this.m1Mai = m1Mai;
	}

	public int getmNuit() {
		return mNuit;
	}

	public void setmNuit(int mNuit) {
		this.mNuit = mNuit;
	}

	public int getmNormales() {
		return mNormales;
	}

	public void setmNormales(int mNormales) {
		this.mNormales = mNormales;
	}

	public int getmComplementaires() {
		return mComplementaires;
	}

	public void setmComplementaires(int mComplementaires) {
		this.mComplementaires = mComplementaires;
	}

	public int getmSimples() {
		return mSimples;
	}

	public void setmSimples(int mSimples) {
		this.mSimples = mSimples;
	}

	public int getmComposees() {
		return mComposees;
	}

	public void setmComposees(int mComposees) {
		this.mComposees = mComposees;
	}

	public int getmRecuperees() {
		return mRecuperees;
	}

	public void setmRecuperees(int mRecuperees) {
		this.mRecuperees = mRecuperees;
	}

	public int getmSup25R() {
		return mSup25R;
	}

	public void setmSup25R(int mSup25R) {
		this.mSup25R = mSup25R;
	}

	public int getmSup50R() {
		return mSup50R;
	}

	public void setmSup50R(int mSup50R) {
		this.mSup50R = mSup50R;
	}

	public int getmDjfR() {
		return mDjfR;
	}

	public void setmDjfR(int mDjfR) {
		this.mDjfR = mDjfR;
	}

	public int getmDjf25R() {
		return mDjf25R;
	}

	public void setmDjf25R(int mDjf25R) {
		this.mDjf25R = mDjf25R;
	}

	public int getmDjf_50R() {
		return mDjf_50R;
	}

	public void setmDjf_50R(int mDjf_50R) {
		this.mDjf_50R = mDjf_50R;
	}

	public int getM1maiR() {
		return m1maiR;
	}

	public void setM1maiR(int m1maiR) {
		this.m1maiR = m1maiR;
	}

	public int getmNuitR() {
		return mNuitR;
	}

	public void setmNuitR(int mNuitR) {
		this.mNuitR = mNuitR;
	}

	public int getmNormalesR() {
		return mNormalesR;
	}

	public void setmNormalesR(int mNormalesR) {
		this.mNormalesR = mNormalesR;
	}

	public int getmComplementairesR() {
		return mComplementairesR;
	}

	public void setmComplementairesR(int mComplementairesR) {
		this.mComplementairesR = mComplementairesR;
	}

	public int getmSimplesR() {
		return mSimplesR;
	}

	public void setmSimplesR(int mSimplesR) {
		this.mSimplesR = mSimplesR;
	}

	public int getmComposeesR() {
		return mComposeesR;
	}

	public void setmComposeesR(int mComposeesR) {
		this.mComposeesR = mComposeesR;
	}

}
