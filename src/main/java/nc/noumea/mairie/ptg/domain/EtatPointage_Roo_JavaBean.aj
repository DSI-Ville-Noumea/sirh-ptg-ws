// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import nc.noumea.mairie.ptg.domain.EtatPointage;
import nc.noumea.mairie.ptg.domain.EtatPointageEnum;
import nc.noumea.mairie.ptg.domain.EtatPointagePK;
import nc.noumea.mairie.ptg.domain.Pointage;

privileged aspect EtatPointage_Roo_JavaBean {
    
    public EtatPointagePK EtatPointage.getEtatPointagePk() {
        return this.etatPointagePk;
    }
    
    public void EtatPointage.setEtatPointagePk(EtatPointagePK etatPointagePk) {
        this.etatPointagePk = etatPointagePk;
    }
    
    public EtatPointageEnum EtatPointage.getEtat() {
        return this.etat;
    }
    
    public void EtatPointage.setEtat(EtatPointageEnum etat) {
        this.etat = etat;
    }
    
    public Pointage EtatPointage.getPointage() {
        return this.pointage;
    }
    
    public void EtatPointage.setPointage(Pointage pointage) {
        this.pointage = pointage;
    }
    
}
