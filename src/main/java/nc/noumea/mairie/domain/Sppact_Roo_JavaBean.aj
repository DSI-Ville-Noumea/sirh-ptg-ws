// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.domain;

import nc.noumea.mairie.domain.Sppact;
import nc.noumea.mairie.domain.SppactId;

privileged aspect Sppact_Roo_JavaBean {
    
    public SppactId Sppact.getId() {
        return this.id;
    }
    
    public void Sppact.setId(SppactId id) {
        this.id = id;
    }
    
    public double Sppact.getNbHeures() {
        return this.nbHeures;
    }
    
    public void Sppact.setNbHeures(double nbHeures) {
        this.nbHeures = nbHeures;
    }
    
    public String Sppact.getService() {
        return this.service;
    }
    
    public void Sppact.setService(String service) {
        this.service = service;
    }
    
}