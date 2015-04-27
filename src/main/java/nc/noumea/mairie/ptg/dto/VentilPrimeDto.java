package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.VentilPrime;
import nc.noumea.mairie.ptg.service.impl.HelperService;

@XmlRootElement
public class VentilPrimeDto extends VentilDto {

	private int idRefPrime;
	private String quantite;

	public VentilPrimeDto() {
	}

	public VentilPrimeDto(VentilPrime hibObj, HelperService helper) {
		idRefPrime = hibObj.getIdRefPrime();
		quantite = formatQuantite(hibObj, helper);
		idVentil = hibObj.getIdVentilPrime();
		date = hibObj.getDateDebutMois();
		idAgent = hibObj.getIdAgent();
		etat = hibObj.getEtat().getCodeEtat();
		
		if(null != hibObj.getVentilDate())
			idVentilDate = hibObj.getVentilDate().getIdVentilDate();
	}

	private String formatQuantite(VentilPrime hibObj, HelperService helper) {

		switch (hibObj.getRefPrime().getTypeSaisie()) {
			case CASE_A_COCHER:
			case NB_INDEMNITES:
				return String.format("%s", hibObj.getQuantite());
			case NB_HEURES:
			case PERIODE_HEURES:
				return helper.formatMinutesToString(hibObj.getQuantite());
		}
		
		return null;
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

	public String getQuantite() {
		return quantite;
	}

	public void setQuantite(String quantite) {
		this.quantite = quantite;
	}

	public int getIdVentilPrime() {
		return idVentil;
	}

}
