package nc.noumea.mairie.ptg.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import nc.noumea.mairie.ptg.domain.VentilAbsence;

@XmlRootElement
public class VentilAbsenceDto extends VentilDto {

    private int minutesConcertees;
    private int minutesNonConcertees;
    private int minutesImmediates;

    public VentilAbsenceDto() {
    }

    public VentilAbsenceDto(VentilAbsence hibObj) {
    	minutesConcertees = hibObj.getMinutesConcertee();
    	minutesNonConcertees = hibObj.getMinutesNonConcertee();
    	minutesImmediates = hibObj.getMinutesImmediat();
        idVentil = hibObj.getIdVentilAbsence();
        date = hibObj.getDateLundi();
        idAgent = hibObj.getIdAgent();
        etat = hibObj.getEtat().getCodeEtat();
        
    }

	public int getMinutesConcertees() {
		return minutesConcertees;
	}

	public void setMinutesConcertees(int minutesConcertees) {
		this.minutesConcertees = minutesConcertees;
	}

	public int getMinutesNonConcertees() {
		return minutesNonConcertees;
	}

	public void setMinutesNonConcertees(int minutesNonConcertees) {
		this.minutesNonConcertees = minutesNonConcertees;
	}
	
	public Date getDateLundi(){
		return date;
	}

	public int getMinutesImmediates() {
		return minutesImmediates;
	}

	public void setMinutesImmediates(int minutesImmediates) {
		this.minutesImmediates = minutesImmediates;
	}

}
