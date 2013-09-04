package nc.noumea.mairie.ptg.dto;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;
import nc.noumea.mairie.ptg.domain.VentilPrime;

@XmlRootElement
public class VentilPrimeDto extends VentilDto {

	private int idRefPrime;
	private int quantite;

	public VentilPrimeDto() {
	}

	public VentilPrimeDto(VentilPrime hibObj) {
		idRefPrime = hibObj.getIdRefPrime();
		quantite = hibObj.getQuantite();
		idVentil = hibObj.getIdVentilPrime();
		date = hibObj.getDateDebutMois();
		idAgent = hibObj.getIdAgent();
		etat = hibObj.getEtat().getCodeEtat();
	}

	public Date getDateDebutMois() {
		return date;
	}

	public void setDateDebutMois(Date d) {
		date = d;
	}

	public int getIdRefPrime() {
		return idRefPrime;
	}

	public void setIdRefPrime(int idRefPrime) {
		this.idRefPrime = idRefPrime;
	}

	public int getQuantite() {
		return quantite;
	}

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	public int getIdVentilPrime() {
		return idVentil;
	}

}
