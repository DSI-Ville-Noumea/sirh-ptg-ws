package nc.noumea.mairie.ptg.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
@Table(name = "PTG_VENTIL_HSUP")
@NamedQuery(name = "getPriorVentilHSupAgentAndDate", query = "select vh from VentilHsup vh where vh.idVentilHSup != :idLatestVentilHSup and vh.idAgent = :idAgent and vh.dateLundi = :dateLundi order by vh.idVentilHSup desc")
public class VentilHsup {

	@Id 
	@Column(name = "ID_VENTIL_HSUP")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idVentilHSup;
	
    @Column(name = "ID_AGENT")
    private Integer idAgent;
    
    @Column(name = "DATE_LUNDI")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateLundi;
    
    @Column(name = "M_ABS")
    private int mAbsences;
    
    @Column(name = "M_HORS_CONTRAT")
    private int mHorsContrat;
    
    @Column(name = "M_SUP")
    private int mSup;
    
    @Column(name = "M_SUP_25")
    private int mSup25;
    
    @Column(name = "M_SUP_25_R")
    private int mSup25Recup;
    
    @Column(name = "M_SUP_50")
    private int mSup50;
    
    @Column(name = "M_SUP_50_R")
    private int mSup50Recup;
    
    @Column(name = "M_DJF")
    private int msdjf;
    
    @Column(name = "M_DJF_R")
    private int msdjfRecup;
    
    @Column(name = "M_1_MAI")
    private int mMai;
    
    @Column(name = "M_1_MAI_R")
    private int mMaiRecup;
    
    @Column(name = "M_NUIT")
    private int msNuit;
    
    @Column(name = "M_NUIT_R")
    private int msNuitRecup;
    
    @Column(name = "M_NORMALES")
    private int mNormales;
    
    @Column(name = "M_NORMALES_R")
    private int mNormalesRecup;
    
    @Column(name = "M_COMPLEMENTAIRES")
    private int mComplementaires;
    
    @Column(name = "M_COMPLEMENTAIRES_R")
    private int mComplementairesRecup;
    
    @Column(name = "M_SIMPLES")
    private int mSimple;
    
    @Column(name = "M_SIMPLES_R")
    private int mSimpleRecup;
    
    @Column(name = "M_COMPOSEES")
    private int mComposees;
    
    @Column(name = "M_COMPOSEES_R")
    private int mComposeesRecup;
    
    @Column(name = "M_RECUPEREES")
    private int mRecuperees;
    
    @Column(name = "ETAT")
    @Enumerated(EnumType.ORDINAL)
    private EtatPointageEnum etat;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_VENTIL_DATE", referencedColumnName = "ID_VENTIL_DATE")
    private VentilDate ventilDate;

    @Version
    @Column(name = "version")
	private Integer version;
    
	public Integer getIdVentilHSup() {
		return idVentilHSup;
	}

	public void setIdVentilHSup(Integer idVentilHSup) {
		this.idVentilHSup = idVentilHSup;
	}

	public Integer getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(Integer idAgent) {
		this.idAgent = idAgent;
	}

	public Date getDateLundi() {
		return dateLundi;
	}

	public void setDateLundi(Date dateLundi) {
		this.dateLundi = dateLundi;
	}

	public int getMAbsences() {
		return mAbsences;
	}

	public void setMAbsences(int mAbsences) {
		this.mAbsences = mAbsences;
	}

	public int getMHorsContrat() {
		return mHorsContrat;
	}

	public void setMHorsContrat(int mHorsContrat) {
		this.mHorsContrat = mHorsContrat;
	}

	public int getMSup() {
		return mSup;
	}

	public void setMSup(int mSup) {
		this.mSup = mSup;
	}

	public int getMSup25() {
		return mSup25;
	}

	public void setMSup25(int mSup25) {
		this.mSup25 = mSup25;
	}

	public int getMSup25Recup() {
		return mSup25Recup;
	}

	public void setMSup25Recup(int mSup25Recup) {
		this.mSup25Recup = mSup25Recup;
	}

	public int getMSup50() {
		return mSup50;
	}

	public void setMSup50(int mSup50) {
		this.mSup50 = mSup50;
	}

	public int getMSup50Recup() {
		return mSup50Recup;
	}

	public void setMSup50Recup(int mSup50Recup) {
		this.mSup50Recup = mSup50Recup;
	}

	public int getMsdjf() {
		return msdjf;
	}

	public void setMsdjf(int msdjf) {
		this.msdjf = msdjf;
	}

	public int getMsdjfRecup() {
		return msdjfRecup;
	}

	public void setMsdjfRecup(int msdjfRecup) {
		this.msdjfRecup = msdjfRecup;
	}

	public int getMMai() {
		return mMai;
	}

	public void setMMai(int mMai) {
		this.mMai = mMai;
	}

	public int getMMaiRecup() {
		return mMaiRecup;
	}

	public void setMMaiRecup(int mMaiRecup) {
		this.mMaiRecup = mMaiRecup;
	}

	public int getMsNuit() {
		return msNuit;
	}

	public void setMsNuit(int msNuit) {
		this.msNuit = msNuit;
	}

	public int getMsNuitRecup() {
		return msNuitRecup;
	}

	public void setMsNuitRecup(int msNuitRecup) {
		this.msNuitRecup = msNuitRecup;
	}

	public int getMNormales() {
		return mNormales;
	}

	public void setMNormales(int mNormales) {
		this.mNormales = mNormales;
	}

	public int getMNormalesRecup() {
		return mNormalesRecup;
	}

	public void setMNormalesRecup(int mNormalesRecup) {
		this.mNormalesRecup = mNormalesRecup;
	}

	public int getMComplementaires() {
		return mComplementaires;
	}

	public void setMComplementaires(int mComplementaires) {
		this.mComplementaires = mComplementaires;
	}

	public int getMComplementairesRecup() {
		return mComplementairesRecup;
	}

	public void setMComplementairesRecup(int mComplementairesRecup) {
		this.mComplementairesRecup = mComplementairesRecup;
	}

	public int getMSimple() {
		return mSimple;
	}

	public void setMSimple(int mSimple) {
		this.mSimple = mSimple;
	}

	public int getMSimpleRecup() {
		return mSimpleRecup;
	}

	public void setMSimpleRecup(int mSimpleRecup) {
		this.mSimpleRecup = mSimpleRecup;
	}

	public int getMComposees() {
		return mComposees;
	}

	public void setMComposees(int mComposees) {
		this.mComposees = mComposees;
	}

	public int getMComposeesRecup() {
		return mComposeesRecup;
	}

	public void setMComposeesRecup(int mComposeesRecup) {
		this.mComposeesRecup = mComposeesRecup;
	}

	public int getMRecuperees() {
		return mRecuperees;
	}

	public void setMRecuperees(int mRecuperees) {
		this.mRecuperees = mRecuperees;
	}

	public EtatPointageEnum getEtat() {
		return etat;
	}

	public void setEtat(EtatPointageEnum etat) {
		this.etat = etat;
	}

	public VentilDate getVentilDate() {
		return ventilDate;
	}

	public void setVentilDate(VentilDate ventilDate) {
		this.ventilDate = ventilDate;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
    
}
