package nc.noumea.mairie.ptg.dto;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import nc.noumea.mairie.ptg.domain.VentilPrime;

@XmlRootElement
public class VentilPrimeDto extends VentilDto {

    private int id_ref_prime;
    private int quantite;

    public VentilPrimeDto() {
    }

    public VentilPrimeDto(VentilPrime hibObj) {
        id_ref_prime = hibObj.getIdRefPrime();
        quantite = hibObj.getQuantite();
        id_ventil = hibObj.getIdVentilPrime();
        date = hibObj.getDateDebutMois();
        id_agent = hibObj.getIdAgent();
        etat = hibObj.getEtat().getCodeEtat();
    }

    public int getId_ventil_prime() {
        return id_ventil;
    }

    public void setId_ventil_prime(int id_ventil_prime) {
        this.id_ventil = id_ventil_prime;
    }

    public Date getDate_debut_mois() {
        return date;
    }

    public void setDate_debut_mois(Date date_debut_mois) {
        this.date = date_debut_mois;
    }

    public int getId_ref_prime() {
        return id_ref_prime;
    }

    public void setId_ref_prime(int id_ref_prime) {
        this.id_ref_prime = id_ref_prime;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }
}
