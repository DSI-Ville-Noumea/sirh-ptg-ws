// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.RefPrime;
import nc.noumea.mairie.ptg.domain.TypeSaisieEnum;

privileged aspect RefPrime_Roo_JavaBean {
    
    public Integer RefPrime.getNoRubr() {
        return this.noRubr;
    }
    
    public void RefPrime.setNoRubr(Integer noRubr) {
        this.noRubr = noRubr;
    }
    
    public String RefPrime.getLibelle() {
        return this.libelle;
    }
    
    public void RefPrime.setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public String RefPrime.getDescription() {
        return this.description;
    }
    
    public void RefPrime.setDescription(String description) {
        this.description = description;
    }
    
    public TypeSaisieEnum RefPrime.getTypeSaisie() {
        return this.typeSaisie;
    }
    
    public void RefPrime.setTypeSaisie(TypeSaisieEnum typeSaisie) {
        this.typeSaisie = typeSaisie;
    }
    
    public boolean RefPrime.isCalculee() {
        return this.calculee;
    }
    
    public void RefPrime.setCalculee(boolean calculee) {
        this.calculee = calculee;
    }
    
    public AgentStatutEnum RefPrime.getStatut() {
        return this.statut;
    }
    
    public void RefPrime.setStatut(AgentStatutEnum statut) {
        this.statut = statut;
    }
    
}