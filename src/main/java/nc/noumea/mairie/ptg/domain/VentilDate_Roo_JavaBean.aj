// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import nc.noumea.mairie.ptg.domain.TypeChainePaieEnum;
import nc.noumea.mairie.ptg.domain.VentilDate;

privileged aspect VentilDate_Roo_JavaBean {
    
    public Date VentilDate.getDateVentilation() {
        return this.dateVentilation;
    }
    
    public void VentilDate.setDateVentilation(Date dateVentilation) {
        this.dateVentilation = dateVentilation;
    }
    
    public TypeChainePaieEnum VentilDate.getTypeChainePaie() {
        return this.typeChainePaie;
    }
    
    public void VentilDate.setTypeChainePaie(TypeChainePaieEnum typeChainePaie) {
        this.typeChainePaie = typeChainePaie;
    }
    
    public boolean VentilDate.isPaye() {
        return this.paye;
    }
    
    public void VentilDate.setPaye(boolean paye) {
        this.paye = paye;
    }
    
}
