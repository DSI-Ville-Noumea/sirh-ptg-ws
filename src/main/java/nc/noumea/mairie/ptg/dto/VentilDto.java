package nc.noumea.mairie.ptg.dto;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VentilDto {

	protected int idVentil;
	protected Date date;
	protected int idAgent;
	protected int etat;

	public VentilDto() {
	}

	public int getIdVentil() {
		return idVentil;
	}

	public void setIdVentil(int idVentil) {
		this.idVentil = idVentil;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getIdAgent() {
		return idAgent;
	}

	public void setIdAgent(int idAgent) {
		this.idAgent = idAgent;
	}

	public int getEtat() {
		return etat;
	}

	public void setEtat(int etat) {
		this.etat = etat;
	}

}
