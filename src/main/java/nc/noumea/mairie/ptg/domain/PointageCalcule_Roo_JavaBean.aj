// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.PointageCalcule;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.RefTypePointage;

privileged aspect PointageCalcule_Roo_JavaBean {
    
    public Integer PointageCalcule.getIdAgent() {
        return this.idAgent;
    }
    
    public void PointageCalcule.setIdAgent(Integer idAgent) {
        this.idAgent = idAgent;
    }
    
    public RefTypePointage PointageCalcule.getType() {
        return this.type;
    }
    
    public void PointageCalcule.setType(RefTypePointage type) {
        this.type = type;
    }
    
    public EtatPointageEnum PointageCalcule.getEtat() {
        return this.etat;
    }
    
    public void PointageCalcule.setEtat(EtatPointageEnum etat) {
        this.etat = etat;
    }
    
    public Date PointageCalcule.getDateLundi() {
        return this.dateLundi;
    }
    
    public void PointageCalcule.setDateLundi(Date dateLundi) {
        this.dateLundi = dateLundi;
    }
    
    public Date PointageCalcule.getDateDebut() {
        return this.dateDebut;
    }
    
    public void PointageCalcule.setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public Date PointageCalcule.getDateFin() {
        return this.dateFin;
    }
    
    public void PointageCalcule.setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }
    
    public Integer PointageCalcule.getQuantite() {
        return this.quantite;
    }
    
    public void PointageCalcule.setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
    
    public RefPrime PointageCalcule.getRefPrime() {
        return this.refPrime;
    }
    
    public void PointageCalcule.setRefPrime(RefPrime refPrime) {
        this.refPrime = refPrime;
    }
    
}