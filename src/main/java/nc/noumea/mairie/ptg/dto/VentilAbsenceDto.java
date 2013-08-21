package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class VentilAbsenceDto extends VentilDto{

    private int minutes_concertees;
    private int minutes_non_concertees;

    public VentilAbsenceDto() {
    }

    public int getId_ventil_absence() {
        return id_ventil;
    }

    public void setId_ventil_absence(int id_ventil_absence) {
        this.id_ventil = id_ventil_absence;
    }

    public String getDate_lundi() {
        return date;
    }

    public void setDate_lundi(String date_lundi) {
        this.date = date_lundi;
    }

    public int getMinutes_concertees() {
        return minutes_concertees;
    }

    public void setMinutes_concertees(int minutes_concertees) {
        this.minutes_concertees = minutes_concertees;
    }

    public int getMinutes_non_concertees() {
        return minutes_non_concertees;
    }

    public void setMinutes_non_concertees(int minutes_non_concertees) {
        this.minutes_non_concertees = minutes_non_concertees;
    }
    
    
}
