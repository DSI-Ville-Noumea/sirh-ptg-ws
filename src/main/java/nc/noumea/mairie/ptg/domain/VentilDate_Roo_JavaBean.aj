// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import java.util.Set;
import nc.noumea.mairie.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.Pointage;
import nc.noumea.mairie.ptg.domain.VentilAbsence;
import nc.noumea.mairie.ptg.domain.VentilDate;
import nc.noumea.mairie.ptg.domain.VentilHsup;
import nc.noumea.mairie.ptg.domain.VentilPrime;

privileged aspect VentilDate_Roo_JavaBean {
    
    public Date VentilDate.getDateVentilation() {
        return this.dateVentilation;
    }
    
    public void VentilDate.setDateVentilation(Date dateVentilation) {
        this.dateVentilation = dateVentilation;
    }
    
    public void VentilDate.setTypeChainePaie(TypeChainePaieEnum typeChainePaie) {
        this.typeChainePaie = typeChainePaie;
    }
    
    public void VentilDate.setPaye(boolean paye) {
        this.paye = paye;
    }
    
    public Set<VentilAbsence> VentilDate.getVentilAbsences() {
        return this.ventilAbsences;
    }
    
    public void VentilDate.setVentilAbsences(Set<VentilAbsence> ventilAbsences) {
        this.ventilAbsences = ventilAbsences;
    }
    
    public Set<VentilHsup> VentilDate.getVentilHsups() {
        return this.ventilHsups;
    }
    
    public void VentilDate.setVentilHsups(Set<VentilHsup> ventilHsups) {
        this.ventilHsups = ventilHsups;
    }
    
    public Set<VentilPrime> VentilDate.getVentilPrimes() {
        return this.ventilPrimes;
    }
    
    public void VentilDate.setVentilPrimes(Set<VentilPrime> ventilPrimes) {
        this.ventilPrimes = ventilPrimes;
    }
    
    public Set<Pointage> VentilDate.getPointages() {
        return this.pointages;
    }
    
    public void VentilDate.setPointages(Set<Pointage> pointages) {
        this.pointages = pointages;
    }
    
}
