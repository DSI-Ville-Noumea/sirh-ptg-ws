// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package nc.noumea.mairie.ptg.domain;

import java.util.Date;
import nc.noumea.mairie.domain.AgentStatutEnum;
import nc.noumea.mairie.ptg.domain.EtatPayeur;
import nc.noumea.mairie.ptg.domain.RefTypePointage;

privileged aspect EtatPayeur_Roo_JavaBean {
    
    public Integer EtatPayeur.getIdEtatPayeur() {
        return this.idEtatPayeur;
    }
    
    public void EtatPayeur.setIdEtatPayeur(Integer idEtatPayeur) {
        this.idEtatPayeur = idEtatPayeur;
    }
    
    public AgentStatutEnum EtatPayeur.getStatut() {
        return this.statut;
    }
    
    public void EtatPayeur.setStatut(AgentStatutEnum statut) {
        this.statut = statut;
    }
    
    public RefTypePointage EtatPayeur.getType() {
        return this.type;
    }
    
    public void EtatPayeur.setType(RefTypePointage type) {
        this.type = type;
    }
    
    public Date EtatPayeur.getDateEtatPayeur() {
        return this.dateEtatPayeur;
    }
    
    public void EtatPayeur.setDateEtatPayeur(Date dateEtatPayeur) {
        this.dateEtatPayeur = dateEtatPayeur;
    }
    
    public String EtatPayeur.getLabel() {
        return this.label;
    }
    
    public void EtatPayeur.setLabel(String label) {
        this.label = label;
    }
    
    public String EtatPayeur.getFichier() {
        return this.fichier;
    }
    
    public void EtatPayeur.setFichier(String fichier) {
        this.fichier = fichier;
    }
    
}
