// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.domain;

import nc.noumea.mairie.domain.Spcarr;
import nc.noumea.mairie.domain.SpcarrId;

privileged aspect Spcarr_Roo_JavaBean {
    
    public SpcarrId Spcarr.getId() {
        return this.id;
    }
    
    public void Spcarr.setId(SpcarrId id) {
        this.id = id;
    }
    
    public Integer Spcarr.getDateFin() {
        return this.dateFin;
    }
    
    public void Spcarr.setDateFin(Integer dateFin) {
        this.dateFin = dateFin;
    }
    
    public Integer Spcarr.getCdcate() {
        return this.cdcate;
    }
    
    public void Spcarr.setCdcate(Integer cdcate) {
        this.cdcate = cdcate;
    }
    
}
