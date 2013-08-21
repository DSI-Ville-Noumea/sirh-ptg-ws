package nc.noumea.mairie.ptg.dto;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class VentilDto{

    private int id_ventil_date;
    protected int id_ventil;
    protected String date;
    private int id_agent;
    private int etat;
    
    public VentilDto() {
    }

    public int getId_ventil_date() {
        return id_ventil_date;
    }

    public void setId_ventil_date(int id_ventil_date) {
        this.id_ventil_date = id_ventil_date;
    }

    public int getId_agent() {
        return id_agent;
    }

    public void setId_agent(int id_agent) {
        this.id_agent = id_agent;
    }

    public int getEtat() {
        return etat;
    }

    public void setEtat(int etat) {
        this.etat = etat;
    }
    
}
