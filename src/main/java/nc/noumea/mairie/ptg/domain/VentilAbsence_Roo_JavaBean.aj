// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.VentilAbsence;

privileged aspect VentilAbsence_Roo_JavaBean {
    
    public Integer VentilAbsence.getIdAgent() {
        return this.idAgent;
    }
    
    public void VentilAbsence.setIdAgent(Integer idAgent) {
        this.idAgent = idAgent;
    }
    
    public Date VentilAbsence.getDateDebutMois() {
        return this.dateDebutMois;
    }
    
    public void VentilAbsence.setDateDebutMois(Date dateDebutMois) {
        this.dateDebutMois = dateDebutMois;
    }
    
    public Integer VentilAbsence.getQuantiteConcertee() {
        return this.quantiteConcertee;
    }
    
    public void VentilAbsence.setQuantiteConcertee(Integer quantiteConcertee) {
        this.quantiteConcertee = quantiteConcertee;
    }
    
    public Integer VentilAbsence.getQuantiteNonConcertee() {
        return this.quantiteNonConcertee;
    }
    
    public void VentilAbsence.setQuantiteNonConcertee(Integer quantiteNonConcertee) {
        this.quantiteNonConcertee = quantiteNonConcertee;
    }
    
    public EtatPointageEnum VentilAbsence.getEtat() {
        return this.etat;
    }
    
    public void VentilAbsence.setEtat(EtatPointageEnum etat) {
        this.etat = etat;
    }
    
}