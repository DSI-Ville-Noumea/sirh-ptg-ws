package nc.noumea.mairie.ptg.dto;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import nc.noumea.mairie.ptg.domain.VentilHsup;

@XmlRootElement
public class VentilHSupDto extends VentilDto {

	private int mabs;
	private int mabsAs400;
	private int mHorsContrat;
	private int mSup;
	private int mSup25;
	private int mSup50;
	private int mDjf;
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
	private int m1maiR;
	private int mNuitR;
	private int mNormalesR;
	private int mComplementairesR;
	private int mSimplesR;
	private int mComposeesR;

	public VentilHSupDto() {
	}

	public VentilHSupDto(VentilHsup hibObj) {
		mabs = hibObj.getMAbsences();
		mabsAs400 = hibObj.getMAbsencesAS400();
		mHorsContrat = hibObj.getMHorsContrat();
		mSup = hibObj.getMSup();
		mSup25 = hibObj.getMSup25();
		mSup50 = hibObj.getMSup50();
		mDjf = hibObj.getMsdjf();
		m1Mai = hibObj.getMMai();
		mNuit = hibObj.getMsNuit();
		mNormales = hibObj.getMNormales();
		mComplementaires = hibObj.getMComplementaires();
		mSimples = hibObj.getMSimple();
		mComposees = hibObj.getMComposees();
		etat = hibObj.getEtat().getCodeEtat();
		mRecuperees = hibObj.getMRecuperees();
		mSup25R = hibObj.getMSup25Recup();
		mSup50R = hibObj.getMSup50Recup();
		mDjfR = hibObj.getMsdjfRecup();
		m1maiR = hibObj.getMMaiRecup();
		mNuitR = hibObj.getMsNuitRecup();
		mNormalesR = hibObj.getMNormalesRecup();
		mComplementairesR = hibObj.getMComplementairesRecup();
		mSimplesR = hibObj.getMSimpleRecup();
		mComposeesR = hibObj.getMComposeesRecup();
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

	public int getMabsAs400() {
		return mabsAs400;
	}

	public void setMabsAs400(int mabsAs400) {
		this.mabsAs400 = mabsAs400;
	}

}
