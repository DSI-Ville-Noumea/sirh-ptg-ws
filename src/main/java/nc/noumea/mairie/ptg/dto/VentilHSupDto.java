package nc.noumea.mairie.ptg.dto;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import nc.noumea.mairie.ptg.domain.VentilHsup;

@XmlRootElement
public class VentilHSupDto extends VentilDto {

    private int mabs;
    private int m_hors_contrat;
    private int m_sup;
    private int m_sup_25;
    private int m_sup_50;
    private int m_djf;
    private int m_djf_25;
    private int m_djf_50;
    private int m_1_mai;
    private int m_nuit;
    private int m_normales;
    private int m_complementaires;
    private int m_simples;
    private int m_composees;
    private int m_recuperees;
    private int m_sup_25_r;
    private int m_sup_50_r;
    private int m_djf_r;
    private int m_djf_25_r;
    private int m_djf_50_r;
    private int m_1_mai_r;
    private int m_nuit_r;
    private int m_normales_r;
    private int m_complementaires_r;
    private int m_simples_r;
    private int m_composees_r;

    public VentilHSupDto() {
    }

    public VentilHSupDto(VentilHsup hibObj) {
        mabs = hibObj.getmAbsences();
        m_hors_contrat = hibObj.getmHorsContrat();
        m_sup = hibObj.getmSup();
        m_sup_25 = hibObj.getmSup25();
        m_sup_50 = hibObj.getmSup50();
        m_djf = hibObj.getMsdjf();
        m_djf_25 = hibObj.getMsdjf25();
        m_djf_50 = hibObj.getMsdjf50();
        m_1_mai = hibObj.getmMai();
        m_nuit = hibObj.getMsNuit();
        m_normales = hibObj.getmNormales();
        m_complementaires = hibObj.getmComplementaires();
        m_simples = hibObj.getmSimple();
        m_composees = hibObj.getmComposees();
        etat = hibObj.getEtat().getCodeEtat();
        m_recuperees = hibObj.getmRecuperees();
        m_sup_25_r = hibObj.getmSup25Recup();
        m_sup_50_r = hibObj.getmSup50Recup();
        m_djf_r = hibObj.getMsdjfRecup();
        m_djf_25_r = hibObj.getMsdjf25Recup();
        m_djf_50_r = hibObj.getMsdjf50Recup();
        m_1_mai_r = hibObj.getmMaiRecup();
        m_nuit_r = hibObj.getMsNuitRecup();
        m_normales_r = hibObj.getmNormalesRecup();
        m_complementaires_r = hibObj.getmComplementairesRecup();
        m_simples_r = hibObj.getmSimpleRecup();
        m_composees_r = hibObj.getmComposeesRecup();
        date = hibObj.getDateLundi();
        id_agent = hibObj.getIdAgent();
        id_ventil=hibObj.getIdVentilHSup();
    }

    public int getId_ventil_hsup() {
        return id_ventil;
    }

    public void setId_ventil_hsup(int id_ventil_hsup) {
        this.id_ventil = id_ventil_hsup;
    }

    public Date getDate_lundi() {
        return date;
    }

    public void setDate_lundi(Date date_lundi) {
        this.date = date_lundi;
    }

    public int getMabs() {
        return mabs;
    }

    public void setMabs(int mabs) {
        this.mabs = mabs;
    }

    public int getM_hors_contrat() {
        return m_hors_contrat;
    }

    public void setM_hors_contrat(int m_hors_contrat) {
        this.m_hors_contrat = m_hors_contrat;
    }

    public int getM_sup() {
        return m_sup;
    }

    public void setM_sup(int m_sup) {
        this.m_sup = m_sup;
    }

    public int getM_sup_25() {
        return m_sup_25;
    }

    public void setM_sup_25(int m_sup_25) {
        this.m_sup_25 = m_sup_25;
    }

    public int getM_sup_50() {
        return m_sup_50;
    }

    public void setM_sup_50(int m_sup_50) {
        this.m_sup_50 = m_sup_50;
    }

    public int getM_djf() {
        return m_djf;
    }

    public void setM_djf(int m_djf) {
        this.m_djf = m_djf;
    }

    public int getM_djf_25() {
        return m_djf_25;
    }

    public void setM_djf_25(int m_djf_25) {
        this.m_djf_25 = m_djf_25;
    }

    public int getM_djf_50() {
        return m_djf_50;
    }

    public void setM_djf_50(int m_djf_50) {
        this.m_djf_50 = m_djf_50;
    }

    public int getM_1_mai() {
        return m_1_mai;
    }

    public void setM_1_mai(int m_1_mai) {
        this.m_1_mai = m_1_mai;
    }

    public int getM_nuit() {
        return m_nuit;
    }

    public void setM_nuit(int m_nuit) {
        this.m_nuit = m_nuit;
    }

    public int getM_normales() {
        return m_normales;
    }

    public void setM_normales(int m_normales) {
        this.m_normales = m_normales;
    }

    public int getM_complementaires() {
        return m_complementaires;
    }

    public void setM_complementaires(int m_complementaires) {
        this.m_complementaires = m_complementaires;
    }

    public int getM_simples() {
        return m_simples;
    }

    public void setM_simples(int m_simples) {
        this.m_simples = m_simples;
    }

    public int getM_composees() {
        return m_composees;
    }

    public void setM_composees(int m_composees) {
        this.m_composees = m_composees;
    }

    public int getM_recuperees() {
        return m_recuperees;
    }

    public void setM_recuperees(int m_recuperees) {
        this.m_recuperees = m_recuperees;
    }

    public int getM_sup_25_r() {
        return m_sup_25_r;
    }

    public void setM_sup_25_r(int m_sup_25_r) {
        this.m_sup_25_r = m_sup_25_r;
    }

    public int getM_sup_50_r() {
        return m_sup_50_r;
    }

    public void setM_sup_50_r(int m_sup_50_r) {
        this.m_sup_50_r = m_sup_50_r;
    }

    public int getM_djf_r() {
        return m_djf_r;
    }

    public void setM_djf_r(int m_djf_r) {
        this.m_djf_r = m_djf_r;
    }

    public int getM_djf_25_r() {
        return m_djf_25_r;
    }

    public void setM_djf_25_r(int m_djf_25_r) {
        this.m_djf_25_r = m_djf_25_r;
    }

    public int getM_djf_50_r() {
        return m_djf_50_r;
    }

    public void setM_djf_50_r(int m_djf_50_r) {
        this.m_djf_50_r = m_djf_50_r;
    }

    public int getM_1_mai_r() {
        return m_1_mai_r;
    }

    public void setM_1_mai_r(int m_1_mai_r) {
        this.m_1_mai_r = m_1_mai_r;
    }

    public int getM_nuit_r() {
        return m_nuit_r;
    }

    public void setM_nuit_r(int m_nuit_r) {
        this.m_nuit_r = m_nuit_r;
    }

    public int getM_normales_r() {
        return m_normales_r;
    }

    public void setM_normales_r(int m_normales_r) {
        this.m_normales_r = m_normales_r;
    }

    public int getM_complementaires_r() {
        return m_complementaires_r;
    }

    public void setM_complementaires_r(int m_complementaires_r) {
        this.m_complementaires_r = m_complementaires_r;
    }

    public int getM_simples_r() {
        return m_simples_r;
    }

    public void setM_simples_r(int m_simples_r) {
        this.m_simples_r = m_simples_r;
    }

    public int getM_composees_r() {
        return m_composees_r;
    }

    public void setM_composees_r(int m_composees_r) {
        this.m_composees_r = m_composees_r;
    }
}
