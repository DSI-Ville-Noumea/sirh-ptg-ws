// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.sirh.domain;

import nc.noumea.mairie.sirh.domain.FichePoste;

privileged aspect FichePoste_Roo_JavaBean {
    
    public String FichePoste.getCodeService() {
        return this.codeService;
    }
    
    public void FichePoste.setCodeService(String codeService) {
        this.codeService = codeService;
    }
    
    public FichePoste FichePoste.getResponsable() {
        return this.responsable;
    }
    
    public void FichePoste.setResponsable(FichePoste responsable) {
        this.responsable = responsable;
    }
    
}
